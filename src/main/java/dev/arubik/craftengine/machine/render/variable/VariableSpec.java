package dev.arubik.craftengine.machine.render.variable;

/**
 * Describes one named variable in a machine's "variables" JSON block.
 *
 * <p>Variables are declared once in JSON and resolved each tick by
 * {@link MachineRenderContext}. The sealed hierarchy lets renderer specs
 * reference {@code "$varname"} tokens that the context resolves to a
 * boolean, number, or item at evaluation time.
 */
public sealed interface VariableSpec {

    /**
     * A boolean flag from a named built-in source.
     * Valid source names: {@code processing}, {@code powered}, {@code overclocked},
     * {@code has_fuel}, {@code always}, {@code never}.
     */
    record BoolSource(String source) implements VariableSpec {}

    /**
     * A boolean that is computed from an inline expression, e.g. {@code "rpm > 100"}.
     * The expression is passed to {@link dev.arubik.craftengine.machine.render.SpeedFormula}.
     */
    record BoolExpr(String expr) implements VariableSpec {}

    /**
     * A numeric value computed from an inline expression,
     * e.g. {@code "rpm * (1 + overclock * 0.5)"}.
     */
    record NumExpr(String expr) implements VariableSpec {}

    /**
     * The live item stack sitting at container slot {@code slot}.
     * Evaluates to a non-null ItemStack (boolean true) when occupied,
     * null (boolean false) when empty.
     */
    record ItemSlot(int slot) implements VariableSpec {}

    /**
     * A value read from a fluid or gas tank property.
     *
     * <p>JSON declaration example:
     * <pre>{@code
     * "water_level": { "type": "tank", "tank": "water", "gas": false, "property": "fraction" }
     * "steam_full":  { "type": "tank", "tank": "steam", "gas": true,  "property": "is_full"  }
     * }</pre>
     *
     * <p>Valid {@code property} values: {@code level}, {@code capacity},
     * {@code fraction}, {@code percent}, {@code is_empty}, {@code is_full}.
     *
     * @param tankName name of the tank as declared in the machine definition
     * @param isGas    {@code true} for a gas tank, {@code false} for a fluid tank
     * @param property one of the property names above
     */
    record TankVar(String tankName, boolean isGas, String property) implements VariableSpec {}

    /**
     * A named PolyFormula expression — the simplest and most flexible variable type.
     *
     * <p>Declared in JSON as a plain string value:
     * <pre>{@code
     * "variables": {
     *   "running": "processing && rpm > 0",
     *   "speed":   "rpm * (1 + overclock * 0.5)"
     * }
     * }</pre>
     *
     * <p>Can be evaluated as boolean, number, or item depending on the call site.
     *
     * @param expr the PolyFormula expression string
     */
    record Formula(String expr) implements VariableSpec {}
}
