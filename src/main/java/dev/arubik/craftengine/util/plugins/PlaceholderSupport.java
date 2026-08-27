package dev.arubik.craftengine.util.plugins;

import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bridge to PlaceholderAPI — resolving %placeholder% text ({@link #set}/{@link #setGlobal}, pure
 * reflection, no compile dependency needed for a single static method call) AND registering brand
 * new script-defined placeholders ({@link #registerPlaceholder}, which needs {@code
 * me.clip:placeholderapi} as a real {@code compileOnly} dependency — see build.gradle.kts — since
 * that direction requires actually EXTENDING PlaceholderAPI's own {@code PlaceholderExpansion}
 * class, not just calling one of its static methods).
 *
 * <pre>
 *   # in a script's __init__():
 *   Plugins.placeholderapi.register_placeholder("myaddon", "myscript.pf:on_placeholder")
 *   # myscript.pf:
 *   def on_placeholder(params):
 *       if params == "balance":
 *           return "" + Plugins.vault.balance(Player)
 *       end
 *       return ""
 *   end
 *   # now %myaddon_balance% resolves via that script, for any player it's shown to.
 * </pre>
 */
public final class PlaceholderSupport {

    private static volatile Boolean available = null;

    // Registered script-backed expansions, keyed by identifier — re-registering the same
    // identifier (e.g. a script's __init__() re-running on /cep reload) unregisters the old one
    // first so PlaceholderAPI never ends up holding a stale PlaceholderExpansion instance.
    private static final Map<String, PlaceholderExpansion> REGISTERED = new ConcurrentHashMap<>();

    private PlaceholderSupport() {}

    public static boolean isAvailable() {
        Boolean result = available;
        if (result == null) {
            result = org.bukkit.Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
            available = result;
        }
        return result;
    }

    public static String set(OfflinePlayer player, String text) {
        if (text == null) return "";
        if (!isAvailable()) return text;

        // This scripting engine's `.pf` string literals are plain double-quoted strings and `%` is
        // already meaningful inside `.pf` expressions as the modulo operator (confirmed in
        // ScriptFormula.java), so `#name#` is offered as a friendlier authoring convenience that
        // avoids that clash. Both spellings work: this only rewrites `#...#` patterns and leaves
        // any literal `%...%` the caller already wrote untouched.
        String converted = text.replaceAll("#([a-zA-Z0-9_]+)#", "%$1%");

        try {
            Object result = Class.forName("me.clip.placeholderapi.PlaceholderAPI")
                .getMethod("setPlaceholders", OfflinePlayer.class, String.class)
                .invoke(null, player, converted);
            return (String) result;
        } catch (Throwable t) {
            // If PAPI itself failed, present the original text (with `#foo#` as the script author
            // wrote it) rather than the converted `%foo%` left unresolved.
            return text;
        }
    }

    /** Same as {@link #set(OfflinePlayer, String)} but for a placeholder that doesn't need a
     *  player context (e.g. {@code %server_online%}, %vault_...% totals, ...) — PlaceholderAPI's
     *  own {@code setPlaceholders} accepts a null player for exactly this case, so this is just
     *  that call spelled out for a caller with no player on hand. */
    public static String setGlobal(String text) {
        return set(null, text);
    }

    /** Whether {@code text} contains anything that looks like a placeholder in either accepted
     *  spelling ({@code %name%} or {@code #name#}) — lets a caller skip the PAPI round trip
     *  entirely for plain text that has nothing to resolve. */
    public static boolean containsPlaceholders(String text) {
        return text != null && (text.matches("(?s).*%[a-zA-Z0-9_]+%.*") || text.matches("(?s).*#[a-zA-Z0-9_]+#.*"));
    }

    /**
     * Registers {@code %identifier_<params>%} as a brand-new PlaceholderAPI placeholder resolved
     * by calling {@code scriptRef} (a {@code "file.pf:func"} reference) with the text AFTER the
     * identifier bound as that function's first argument (PAPI's own "params" convention — see
     * the class javadoc example). Intended to be called once from a script's {@code __init__()},
     * the same lifecycle convention {@code WorldGuardSupport#registerFlag} uses for WorldGuard's
     * own registration-time requirement. Calling this again with the same {@code identifier}
     * replaces the previous registration (safe across a {@code /cep reload}).
     *
     * <p>The script function runs with {@code Player} bound (the OfflinePlayer PAPI resolved this
     * placeholder for, if online — unset otherwise) — same binding convention every other
     * script-firing entry point in this codebase uses.
     */
    public static boolean registerPlaceholder(String identifier, String scriptRef) {
        if (!isAvailable() || identifier == null || identifier.isBlank() || scriptRef == null) return false;
        try {
            ScriptCall call = ScriptCall.parse(scriptRef);
            if (call == null) return false;
            PlaceholderExpansion expansion = new ScriptPlaceholderExpansion(identifier, call);
            PlaceholderExpansion previous = REGISTERED.put(identifier, expansion);
            if (previous != null) previous.unregister();
            return expansion.register();
        } catch (Throwable ignored) { return false; }
    }

    public static boolean unregisterPlaceholder(String identifier) {
        if (identifier == null) return false;
        PlaceholderExpansion expansion = REGISTERED.remove(identifier);
        if (expansion == null) return false;
        try { return expansion.unregister(); } catch (Throwable ignored) { return false; }
    }

    /** Thin {@code PlaceholderExpansion} that forwards every {@code %identifier_params%} request
     *  straight to a script function — see {@link #registerPlaceholder}. */
    private static final class ScriptPlaceholderExpansion extends PlaceholderExpansion {
        private final String identifier;
        private final ScriptCall call;

        ScriptPlaceholderExpansion(String identifier, ScriptCall call) {
            this.identifier = identifier;
            this.call = call;
        }

        @Override public String getIdentifier() { return identifier; }
        @Override public String getAuthor() { return "CraftEnginePolyfills"; }
        @Override public String getVersion() { return "1.0"; }
        // Script-backed expansions are cheap to re-create on demand and the ref they wrap can
        // change across a /cep reload — persisting a stale one across a PlaceholderAPI restart
        // would silently keep calling old logic, so this stays non-persistent (re-registered by
        // the owning script's own __init__() every time scripts load instead).
        @Override public boolean persist() { return false; }

        @Override
        public String onRequest(OfflinePlayer player, String params) {
            try {
                ScriptContext.Builder b = ScriptContext.builder();
                if (player instanceof Player online) {
                    b.player(((org.bukkit.craftbukkit.entity.CraftPlayer) online).getHandle());
                }
                ScriptContext ctx = b.build();
                ScriptValue result = call.evaluateWithExtraArgs(ctx, java.util.List.of(ScriptValue.of(params == null ? "" : params)));
                return result == null || result == ScriptValue.NULL ? "" : result.asStr();
            } catch (Throwable ignored) {
                return "";
            }
        }
    }
}
