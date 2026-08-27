package dev.arubik.craftengine.machine.render;

import dev.arubik.craftengine.machine.render.variable.MachineRenderContext;

/**
 * A renderer entry's {@code when} — whether it's currently active at all. Parsed ONCE, at machine
 * DEFINITION load time ({@link dev.arubik.craftengine.machine.MachineDefinitionLoader} et al.),
 * from the raw JSON/composed boolexpr string — never re-parsed per block-entity instance or per
 * tick. Same idea as {@link UpdateWhen}, scoped to what a boolean gate actually needs.
 *
 * <p>Supported forms (see {@link #parse}):
 * <ul>
 *   <li>{@code "always"}/{@code "true"} (default, unset) / {@code "never"}/{@code "false"} — the
 *       obvious extremes, skip the script engine entirely.</li>
 *   <li>A bare fast property name ({@code "processing"}/{@code "powered"}/{@code "overclocked"}/
 *       {@code "has_fuel"}) — reads the field directly off {@link MachineRenderContext}, no script
 *       engine involved.</li>
 *   <li>Anything else — a boolexpr (optionally combining {@code &&}/{@code ||}/{@code !} per the
 *       JSON {@code not}/{@code and}/{@code or} composition {@code MachineDefinitionLoader
 *       #parseWhen} already builds) OR a {@code "file.pf:func"} script-call reference, evaluated
 *       through the normal script engine.</li>
 * </ul>
 */
public sealed interface WhenCondition {
    WhenCondition ALWAYS = new Always();
    WhenCondition NEVER = new Never();

    record Always() implements WhenCondition {}
    record Never() implements WhenCondition {}
    record FastProperty(String name) implements WhenCondition {}
    record ScriptGate(String expr) implements WhenCondition {}

    static WhenCondition parse(String raw) {
        if (raw == null || raw.isBlank() || raw.equals("always") || raw.equals("true")) return ALWAYS;
        if (raw.equals("never") || raw.equals("false")) return NEVER;
        switch (raw) {
            case "processing", "powered", "overclocked", "has_fuel" -> { return new FastProperty(raw); }
        }
        return new ScriptGate(raw);
    }

    /** The original source text — {@code "always"}/{@code "never"} for the two extremes, the bare
     *  name for {@code FastProperty}, the literal expr for {@code ScriptGate}. Some callers need to
     *  string-inspect the RAW text itself (e.g. detecting a per-player condition by checking for
     *  {@code "player_facing"}/{@code "player_in_range"} substrings), not just evaluate it — this
     *  keeps that working without re-introducing a parallel raw-string field. */
    default String raw() {
        return switch (this) {
            case Always ignored -> "always";
            case Never ignored -> "never";
            case FastProperty fp -> fp.name();
            case ScriptGate sg -> sg.expr();
        };
    }

    /** Evaluates this condition against {@code ctx} — {@code Always}/{@code Never}/{@code
     *  FastProperty} never touch the script engine at all. */
    default boolean evaluate(MachineRenderContext ctx) {
        return switch (this) {
            case Always ignored -> true;
            case Never ignored -> false;
            case FastProperty fp -> switch (fp.name()) {
                case "processing" -> ctx.processing();
                case "powered" -> ctx.powered();
                case "overclocked" -> ctx.overclocked();
                case "has_fuel" -> ctx.hasFuel();
                default -> true;
            };
            case ScriptGate sg -> ctx.evalBool(sg.expr(), null);
        };
    }
}
