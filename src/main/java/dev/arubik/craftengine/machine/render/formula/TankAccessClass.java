package dev.arubik.craftengine.machine.render.formula;

import java.util.List;

/**
 * {@link PolyClass} wrapping one tank's level and capacity for property access.
 *
 * <h3>Properties / Methods (identical)</h3>
 * <ul>
 *   <li>{@code level}    — stored amount</li>
 *   <li>{@code capacity} — maximum capacity</li>
 *   <li>{@code fraction} — fill fraction (0–1)</li>
 *   <li>{@code percent}  — fill percentage (0–100)</li>
 *   <li>{@code is_empty} — {@link PolyValue.Bool} true when level is 0</li>
 *   <li>{@code is_full}  — {@link PolyValue.Bool} true when level equals capacity</li>
 * </ul>
 *
 * <p>Instances are obtained via {@link FluidTanksClass#forTank(String)} and
 * {@link GasTanksClass#forTank(String)}.  They are used internally — e.g. when
 * evaluating a {@link dev.arubik.craftengine.machine.render.variable.VariableSpec.TankVar}
 * — rather than being returned as a {@link PolyValue} to the expression engine.</p>
 */
public final class TankAccessClass implements PolyClass {

    private final double level;
    private final double capacity;

    public TankAccessClass(double[] data) {
        this.level    = data.length > 0 ? data[0] : 0;
        this.capacity = data.length > 1 ? data[1] : 0;
    }

    @Override
    public PolyValue get(String property) {
        return switch (property) {
            case "level"    -> PolyValue.of(level);
            case "capacity" -> PolyValue.of(capacity);
            case "fraction" -> PolyValue.of(capacity > 0 ? level / capacity : 0);
            case "percent"  -> PolyValue.of(capacity > 0 ? (level / capacity) * 100 : 0);
            case "is_empty" -> PolyValue.of(level <= 0);
            case "is_full"  -> PolyValue.of(capacity > 0 && level >= capacity);
            default -> PolyValue.NULL;
        };
    }

    /** Treat method calls as property reads (e.g. {@code tank.level()} == {@code tank.level}). */
    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        return get(method);
    }
}
