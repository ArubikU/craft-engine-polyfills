package dev.arubik.craftengine.machine.render.formula;

import java.util.List;
import java.util.Map;

/**
 * {@link PolyClass} for gas tanks.
 *
 * <p>Mirrors {@link FluidTanksClass} exactly but is registered separately as
 * {@code "GasTanks"} so formulas can address gas and fluid independently.</p>
 *
 * <h3>Properties</h3>
 * <ul>
 *   <li>{@code GasTanks.total_level} — sum of all tank levels</li>
 *   <li>{@code GasTanks.<name>}      — level of the named tank (shorthand)</li>
 * </ul>
 *
 * <h3>Methods</h3>
 * <ul>
 *   <li>{@code GasTanks.level("steam")}    — stored amount</li>
 *   <li>{@code GasTanks.capacity("steam")} — maximum capacity</li>
 *   <li>{@code GasTanks.fraction("steam")} — fill fraction (0–1)</li>
 *   <li>{@code GasTanks.percent("steam")}  — fill percentage (0–100)</li>
 *   <li>{@code GasTanks.is_empty("steam")} — true when level is 0</li>
 *   <li>{@code GasTanks.is_full("steam")}  — true when level equals capacity</li>
 * </ul>
 *
 * @see FluidTanksClass for fuller documentation.
 */
public final class GasTanksClass implements PolyClass {

    /** Map of tank name → {@code [level, capacity]}. */
    private final Map<String, double[]> tanks;

    public GasTanksClass(Map<String, double[]> tanks) {
        this.tanks = tanks;
    }

    @Override
    public PolyValue get(String property) {
        if ("total_level".equals(property)) {
            return PolyValue.of(tanks.values().stream().mapToDouble(t -> t[0]).sum());
        }
        double[] data = resolve(property);
        if (data == null) return PolyValue.NULL;
        return PolyValue.of(data[0]);
    }

    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        String tankName = args.isEmpty() ? "" : args.get(0).asStr();
        double[] data = resolve(tankName);
        if (data == null) return PolyValue.NULL;
        return switch (method) {
            case "level"    -> PolyValue.of(data[0]);
            case "capacity" -> PolyValue.of(data[1]);
            case "fraction" -> PolyValue.of(data[1] > 0 ? data[0] / data[1] : 0);
            case "percent"  -> PolyValue.of(data[1] > 0 ? (data[0] / data[1]) * 100 : 0);
            case "is_empty" -> PolyValue.of(data[0] <= 0);
            case "is_full"  -> PolyValue.of(data[1] > 0 && data[0] >= data[1]);
            default -> PolyValue.NULL;
        };
    }

    /**
     * Returns a {@link TankAccessClass} for the named tank.
     * Falls back to the first tank when the name is not found or empty.
     */
    public TankAccessClass forTank(String name) {
        double[] data = resolve(name);
        return new TankAccessClass(data != null ? data : new double[]{0, 0});
    }

    // ---- internal ---------------------------------------------------------

    private double[] resolve(String name) {
        double[] data = name != null ? tanks.get(name) : null;
        if (data == null && !tanks.isEmpty()) data = tanks.values().iterator().next();
        return data;
    }
}
