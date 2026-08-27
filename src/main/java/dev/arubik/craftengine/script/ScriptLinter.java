package dev.arubik.craftengine.script;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A load-time lint pass over {@code .pf} source text: for every {@code Global.member} reference
 * whose receiver is one of the script engine's well-known global namespaces (Machine, Player,
 * World, Item, Block, ...), checks that {@code member} actually resolves as a registered property
 * or method on that {@link PolyType} (walking its parent chain, same resolution
 * {@link ScriptFormula} itself uses at runtime) — so a typo, a stale name left over from a Java-side
 * rename, or a call to a method that was moved/removed shows up as a WARNING in the console the
 * moment the script loads/reloads, instead of silently evaluating to {@code ScriptValue.NULL} deep
 * inside some tick handler with no indication anything went wrong (see e.g. the engine-wide
 * "Machine.get_item_in_slot"-style cleanup this exists because of — every one of those stale call
 * sites would have been caught here immediately, instead of requiring a manual grep audit).
 *
 * <p><b>Deliberately a heuristic, not a real type checker.</b> The engine's expression parser
 * ({@link ScriptFormula}) compiles directly into closures with no reified AST left to walk after
 * parsing (see its {@code Node} javadoc), so this works over the raw source text instead: strip
 * comments/string literals (so neither can produce a false hit), then regex-match {@code
 * Known.identifier} pairs. This means:
 * <ul>
 *   <li>Only the FIRST hop of a chain is checked — {@code Machine.container.push(...)} validates
 *       {@code Machine.container} (a real property) but never inspects {@code .push} on whatever
 *       that returns, since the returned type isn't known without real type inference.</li>
 *   <li>A global name shadowed by a same-named local variable would still be checked against the
 *       global's type — not a real risk in practice since every global here is capitalized
 *       specifically so scripts (snake_case locals by convention) never collide with one.</li>
 *   <li>A type registering a {@code defaultProperty}/{@code defaultMethod} fallback (a genuinely
 *       dynamic namespace, e.g. a Map) never produces a false positive: {@link PolyType#resolveProperty}/
 *       {@link PolyType#resolveMethod} already return a non-null handler for ANY name on such a type,
 *       exactly like they would at real evaluation time.</li>
 * </ul>
 * Never throws and never blocks loading — a lint finding is always a WARNING alongside the file's
 * own load, not a load failure; a wrong or overly-eager finding costs nothing but a log line.
 */
public final class ScriptLinter {

    private ScriptLinter() {}

    /** The engine's well-known global namespaces — one per distinct {@link PolyType} name any
     *  script-visible "Global.member" expression can start with. Kept as an explicit list (rather
     *  than e.g. every registered PolyType name) because plenty of registered types are NEVER a
     *  bare global receiver — they only ever appear as the return value of some property/method
     *  (Container, Belt, Recipe, ...) — checking those here would be pure noise since scripts don't
     *  reference them by a fixed name this pass could regex for. */
    private static final Set<String> GLOBAL_TYPES = Set.of(
        "Machine", "Player", "World", "Block", "Entity", "Item", "Menu",
        "EventManager", "TaskManager", "Dialog", "SQL", "Redis", "Uuid", "Plugins",
        "Server", "ChainManager", "TypedKey", "Glue", "Contraption", "ContraptionManager",
        "Registry", "Inventory", "FluidTanks", "GasTanks", "Images", "MultiBlock", "Network"
    );

    private static final Pattern GLOBAL_MEMBER = Pattern.compile(
        "\\b(" + String.join("|", GLOBAL_TYPES) + ")\\.([A-Za-z_][A-Za-z0-9_]*)"
    );

    private static final int MAX_SUGGESTION_DISTANCE = 3;

    /** Lints {@code src} (the raw text of {@code scriptName}), logging one WARNING per unresolved
     *  {@code Global.member} reference found. Safe to call on every load/reload — cheap (one regex
     *  pass plus a handful of map lookups) and side-effect-free beyond logging. */
    public static void lint(String scriptName, String src, Logger log) {
        if (src == null || src.isEmpty()) return;
        String cleaned = stripCommentsAndStrings(src);
        lintFinalReassignment(scriptName, cleaned, log);
        Matcher m = GLOBAL_MEMBER.matcher(cleaned);
        int found = 0;
        while (m.find()) {
            String globalName = m.group(1);
            String member = m.group(2);
            PolyType type = PolyTypeRegistry.get(globalName);
            if (type == null) continue; // shouldn't happen — every GLOBAL_TYPES entry is engine-registered
            if (type.resolveProperty(member) != null || type.resolveMethod(member) != null) continue;
            int line = 1 + countNewlinesBefore(cleaned, m.start());
            String suggestion = nearestName(member, type);
            log.log(Level.WARNING, "[CEPolyfills lint] " + scriptName + ":" + line
                + " — '" + globalName + "." + member + "' is not a known " + globalName
                + " property or method" + (suggestion != null ? " (did you mean '" + suggestion + "'?)" : "")
                + ".");
            found++;
        }
        if (found > 0) {
            log.warning("[CEPolyfills lint] " + scriptName + ": " + found
                + " unresolved global reference(s) — see above. This is a load-time HINT, not a"
                + " failure; the script still loaded normally.");
        }
    }

    /** {@code final NAME = ...} declares NAME (see {@code ScriptProgram.Statement.StaticDecl})
     *  write-once/shared — a later plain {@code NAME = ...} anywhere else in the file overwrites
     *  {@code ScriptProgram}'s shared store just like it would for a {@code static} var (see
     *  {@code runStatements}' {@code Assign} case), which is almost certainly not what "final" was
     *  meant to promise. Flags every such reassignment as a WARNING — same heuristic, text-based
     *  approach as the rest of this pass: line-scan {@code cleaned} (comments/strings already
     *  blanked) for {@code final NAME =}/{@code static NAME =} declarations first, then for a bare
     *  {@code NAME =} (not {@code ==}, {@code !=}, or a compound {@code +=}/{@code -=}/... — none
     *  of those have a bare {@code =} immediately after the name) on any OTHER line. */
    private static final Pattern DECL = Pattern.compile("^\\s*(final|static)\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*=");
    private static final Pattern BARE_ASSIGN = Pattern.compile("^\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*=(?!=)");

    private static void lintFinalReassignment(String scriptName, String cleaned, Logger log) {
        String[] lines = cleaned.split("\n", -1);
        Set<String> finalNames = new LinkedHashSet<>();
        int[] declLine = new int[lines.length]; // unused slots stay 0
        for (int i = 0; i < lines.length; i++) {
            Matcher d = DECL.matcher(lines[i]);
            if (d.find() && "final".equals(d.group(1))) {
                finalNames.add(d.group(2));
                declLine[i] = 1; // marks this line as the declaration itself — never a reassignment
            }
        }
        for (int i = 0; i < lines.length; i++) {
            if (declLine[i] == 1) continue;
            Matcher a = BARE_ASSIGN.matcher(lines[i]);
            if (!a.find()) continue;
            String name = a.group(1);
            if (!finalNames.contains(name)) continue;
            log.warning("[CEPolyfills lint] " + scriptName + ":" + (i + 1)
                + " — reassigns 'final " + name + "', declared elsewhere in this file as write-once.");
        }
    }

    /** Replaces every {@code #...} line comment and every {@code "..."}/{@code '...'} string
     *  literal's CONTENT with spaces — preserves overall length and every newline so character
     *  offsets (and therefore line numbers) computed against the result still line up with the
     *  original source, while guaranteeing neither a comment nor a string body can ever produce a
     *  false "Global.member" hit (e.g. a body/message string that happens to mention
     *  "Machine.foo" in prose). Matches the real {@code Tokenizer}'s own string-escape handling
     *  ({@code \} escapes the next char) closely enough for this heuristic purpose. */
    private static String stripCommentsAndStrings(String src) {
        StringBuilder out = new StringBuilder(src.length());
        int i = 0;
        int n = src.length();
        while (i < n) {
            char c = src.charAt(i);
            if (c == '#') {
                while (i < n && src.charAt(i) != '\n') { out.append(' '); i++; }
                continue;
            }
            if (c == '"' || c == '\'') {
                char quote = c;
                out.append(' ');
                i++;
                while (i < n && src.charAt(i) != quote) {
                    if (src.charAt(i) == '\\' && i + 1 < n) {
                        out.append(src.charAt(i) == '\n' ? '\n' : ' ');
                        i++;
                    }
                    out.append(src.charAt(i) == '\n' ? '\n' : ' ');
                    i++;
                }
                if (i < n) { out.append(' '); i++; } // closing quote
                continue;
            }
            out.append(c);
            i++;
        }
        return out.toString();
    }

    private static int countNewlinesBefore(String s, int offset) {
        int count = 0;
        for (int i = 0; i < offset && i < s.length(); i++) if (s.charAt(i) == '\n') count++;
        return count;
    }

    /** Nearest known property/method name on {@code type} to {@code member} by edit distance, or
     *  null if nothing is close enough to be a useful hint (distance > {@link
     *  #MAX_SUGGESTION_DISTANCE}) — an unrelated random name would be more confusing than no
     *  suggestion at all. */
    private static String nearestName(String member, PolyType type) {
        Set<String> candidates = new LinkedHashSet<>(type.allPropertyNames());
        candidates.addAll(type.allMethodNames());
        String best = null;
        int bestDist = Integer.MAX_VALUE;
        for (String candidate : candidates) {
            int d = levenshtein(member, candidate);
            if (d < bestDist) { bestDist = d; best = candidate; }
        }
        return (best != null && bestDist <= MAX_SUGGESTION_DISTANCE) ? best : null;
    }

    private static int levenshtein(String a, String b) {
        int[] prev = new int[b.length() + 1];
        int[] cur = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) prev[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            cur[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }
            int[] tmp = prev; prev = cur; cur = tmp;
        }
        return prev[b.length()];
    }
}
