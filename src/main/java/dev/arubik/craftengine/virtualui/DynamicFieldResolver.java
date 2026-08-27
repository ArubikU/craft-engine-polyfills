package dev.arubik.craftengine.virtualui;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.script.DynamicText;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.virtualui.model.WidgetRenderContext;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Resolves ANY widget text/icon field that might be dynamic — reuses the SAME two conventions
 * every other data-driven text field in this codebase already supports via {@link DynamicText}
 * (extracted from machine menu buttons, see its own javadoc), instead of VirtualUI inventing a
 * third:
 * <ul>
 *   <li>A bare {@code "file.pf:func[:args]"} ref — the WHOLE field is that function's return
 *       value (a string for text/an id, or — item fields only, see {@link #resolveValue} — an
 *       already-built {@code ScriptValue.Item}).
 *   <li>{@code ${expr}} inline substitutions anywhere inside a larger literal string, e.g.
 *       {@code "<gray>HP: ${Player.get_typed('hp','int')}"} — always coerced to a string fragment,
 *       same as a machine button's name/lore template.
 * </ul>
 * Both are evaluated against that widget's own live render state, bound under its kind's own
 * context type name (e.g. {@code "VUIButton"}, {@code "VUIScrollbar"} — see
 * {@code dev.arubik.craftengine.script.types.util.VirtualUIWidgetContextTypes}) on top of the
 * same baseline namespaces every other VirtualUI script context gets. Evaluated fresh every
 * render tick a widget with a dynamic field is visible — cheap for the common case (neither
 * {@code ".pf:"} nor {@code "${"} present, skipped entirely) and consistent with how a machine
 * renderer's own {@code "run"} script re-evaluates every tick.
 */
public final class DynamicFieldResolver {

    private DynamicFieldResolver() {}

    /** True if {@code raw} has ANY dynamic content ({@code .pf:} ref or {@code ${...}}) —
     *  callers use this as their fast-path skip before doing any other work. */
    public static boolean isDynamic(String raw) {
        return raw != null && (raw.contains(".pf:") || raw.contains("${"));
    }

    /** Resolves a field expected to end up as a plain string (text, an id) — {@code .pf:} whole-
     *  field refs AND {@code ${expr}} inline substitutions both work, exactly like a machine
     *  button's name/lore. */
    public static String resolveText(String raw, Player player, WidgetRenderContext ctx, String contextTypeName) {
        if (!isDynamic(raw)) return raw;
        try {
            return DynamicText.evaluateNameRaw(raw, buildContext(player, ctx, contextTypeName));
        } catch (Throwable t) {
            logFailure(raw, t);
            return raw;
        }
    }

    /** Resolves an ITEM field that may come back as either a plain id ({@code ScriptValue.Str})
     *  or an already-built item ({@code ScriptValue.Item}) — only a whole-field {@code .pf:} ref
     *  can return an {@code Item} (an {@code ${expr}}-only string always resolves through
     *  {@link #resolveText} to a plain id instead, same as any other {@code ${...}} fragment).
     *  Returns {@code null} if {@code raw} isn't a {@code .pf:} ref at all. */
    public static ScriptValue resolveValue(String raw, Player player, WidgetRenderContext ctx, String contextTypeName) {
        if (raw == null || !raw.contains(".pf:")) return null;
        try {
            ScriptCall call = ScriptCall.parse(raw);
            if (call == null) return null;
            ScriptValue result = call.evaluate(buildContext(player, ctx, contextTypeName));
            return result instanceof ScriptValue.Null ? null : result;
        } catch (Throwable t) {
            logFailure(raw, t);
            return null;
        }
    }

    private static ScriptContext buildContext(Player player, WidgetRenderContext ctx, String contextTypeName) {
        net.minecraft.server.level.ServerPlayer sp = ((CraftPlayer) player).getHandle();
        ScriptContext.Builder b = ScriptContext.builder();
        b.typed("Server", dev.arubik.craftengine.script.types.world.ServerType.INSTANCE);
        b.typed("Item", dev.arubik.craftengine.script.types.primitive.ItemType.NAMESPACE);
        b.typed("VirtualUI", dev.arubik.craftengine.script.types.util.VirtualUIManagerType.INSTANCE);
        b.typed(contextTypeName, ctx);
        b.player(sp);
        return b.build();
    }

    private static void logFailure(String raw, Throwable t) {
        CraftEnginePolyfills.instance().getLogger()
                .log(java.util.logging.Level.WARNING, "[VirtualUI] dynamic field '" + raw + "' failed", t);
    }
}
