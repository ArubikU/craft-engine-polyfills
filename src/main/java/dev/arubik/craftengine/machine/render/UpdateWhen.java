package dev.arubik.craftengine.machine.render;

/**
 * A renderer entry's {@code update_when} — how often it re-evaluates its own {@code when}/value
 * formulas and re-renders, as opposed to reusing whatever it last showed. Parsed ONCE, at machine
 * DEFINITION load time ({@link dev.arubik.craftengine.machine.MachineDefinitionLoader}), from the
 * raw JSON string — never re-parsed per block-entity instance or per tick. Consumed by {@link
 * RendererManager#shouldUpdateThisTick}.
 *
 * <p>Supported forms (see {@link #parse}):
 * <ul>
 *   <li>{@code "always"} (default, unset) — every tick, today's behavior.</li>
 *   <li>{@code "never"} — only the very first tick (otherwise the display would never appear).</li>
 *   <li>{@code "on_change"} — only when the underlying block's state actually changed since the
 *       last tick this entry updated on — a cheap {@code BlockState} equality check, no script
 *       engine involved.</li>
 *   <li>A bare fast property name ({@code "processing"}/{@code "powered"}/{@code "overclocked"}/
 *       {@code "has_fuel"}) — reads the field directly off {@link
 *       dev.arubik.craftengine.machine.render.variable.MachineRenderContext}. Any OTHER bare name
 *       is tried as a real block-state property (CE custom or vanilla, via {@link
 *       dev.arubik.craftengine.script.types.world.BlockType#readProperty}) and treated as "truthy"
 *       when the property has a non-null, non-"false" value.</li>
 *   <li>A plain integer — re-evaluate only once every N ticks (same throttling idea {@code
 *       particle}'s own {@code interval} field already uses elsewhere).</li>
 *   <li>Anything else — an inline boolexpr OR a {@code "file.pf:func"} script-call reference,
 *       evaluated through the normal script engine ({@code MachineRenderContext#evalBool}, which
 *       already understands both forms via {@code ScriptFormula}) — the general fallback.</li>
 * </ul>
 */
public sealed interface UpdateWhen {
    UpdateWhen ALWAYS = new Always();
    UpdateWhen NEVER = new Never();
    UpdateWhen ON_CHANGE = new OnBlockChange();

    record Always() implements UpdateWhen {}
    record Never() implements UpdateWhen {}
    record OnBlockChange() implements UpdateWhen {}
    record FastProperty(String name) implements UpdateWhen {}
    record Interval(int ticks) implements UpdateWhen {}
    record ScriptGate(String expr) implements UpdateWhen {}

    static UpdateWhen parse(String raw) {
        if (raw == null || raw.isBlank() || raw.equals("always")) return ALWAYS;
        if (raw.equals("never")) return NEVER;
        if (raw.equals("on_change")) return ON_CHANGE;
        switch (raw) {
            case "processing", "powered", "overclocked", "has_fuel" -> { return new FastProperty(raw); }
        }
        try {
            int n = Integer.parseInt(raw.trim());
            return new Interval(Math.max(1, n));
        } catch (NumberFormatException ignored) {}
        // A bare identifier (no operators/dots/parens) that isn't one of the known machine fast
        // properties is tried as a block-state property name instead of a full script expression —
        // still "fast" (no ScriptFormula compile/evaluate), just resolved per-tick against whatever
        // block is actually here rather than a fixed MachineRenderContext field.
        if (raw.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '_')) {
            return new FastProperty(raw);
        }
        return new ScriptGate(raw);
    }
}
