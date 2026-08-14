package dev.arubik.craftengine.machine.render.formula;

import java.util.List;
import java.util.Map;

/**
 * {@link PolyClass} for fluid tanks.
 *
 * <h3>Properties</h3>
 * <ul>
 *   <li>{@code FluidTanks.total_level} — sum of all tank levels</li>
 *   <li>{@code FluidTanks.<name>}      — level of the named tank (shorthand)</li>
 * </ul>
 *
 * <h3>Methods</h3>
 * <ul>
 *   <li>{@code FluidTanks.level("water")}    — stored amount</li>
 *   <li>{@code FluidTanks.capacity("water")} — maximum capacity</li>
 *   <li>{@code FluidTanks.fraction("water")} — fill fraction (0–1)</li>
 *   <li>{@code FluidTanks.percent("water")}  — fill percentage (0–100)</li>
 *   <li>{@code FluidTanks.is_empty("water")} — true when level is 0</li>
 *   <li>{@code FluidTanks.is_full("water")}  — true when level equals capacity</li>
 * </ul>
 *
 * <p>Registered on {@link PolyContext} as {@code "FluidTanks"} when the machine has
 * at least one fluid tank. Flat convenience variables {@code fluid_level},
 * {@code fluid_capacity}, and {@code fluid_fraction} are also injected for the
 * first (or only) tank.</p>
 *
 * <p>Use the built-in {@code FluidTank(name, property)} function in expressions for
 * single-value access, e.g. {@code FluidTank("water", "fraction") > 0.5}.</p>
 */
public final class FluidTanksClass implements PolyClass {

    /** Map of tank name → {@code [level, capacity]}. */
    private final Map<String, double[]> tanks;

    public FluidTanksClass(Map<String, double[]> tanks) {
        this.tanks = tanks;
    }

    @Override
    public PolyValue get(String property) {
        if ("total_level".equals(property)) {
            return PolyValue.of(tanks.values().stream().mapToDouble(t -> t[0]).sum());
        }
        // Property matches a tank name → return its level as a shorthand
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
     * Used internally by {@code MachineRenderContext} and {@code PolyFormula}
     * builtins to read a specific property of a specific tank.
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
