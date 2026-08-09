package dev.arubik.craftengine.rotation;

import java.util.List;
import java.util.Map;

import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.multiblock.RelativeDirection;
import net.momirealms.craftengine.core.util.Key;

/**
 * A source of rotational power: what it burns, what that yields, and which way
 * the shaft points.
 *
 * <p>
 * Machines that <em>consume</em> rpm and SU became data-driven; the things that
 * <em>produce</em> it stayed as one Java class per motor, with the fuel table in
 * the block config and the tank, upgrade grid and overclock headroom as constants.
 * A motor is now a file.
 *
 * <p>
 * A motor is a machine that happens to turn a shaft — it has the same menu,
 * slots and gauges — so it carries a {@link MachineDefinition} rather than
 * reinventing that surface. It does not name its own block: the block config
 * points at the motor, the same direction machines already point at theirs.
 *
 * @param id            registry id
 * @param machine       the menu surface: slots, tanks, bars, buttons
 * @param fuels         what it can burn and what each yields
 * @param outputFaces   which faces the shaft drives, relative to the block's
 *                      facing, so one definition works at every rotation
 * @param upgradeSlots  size of the upgrade grid
 * @param baseUnlocked  how many of those are usable before any EXTRA_SLOTS upgrade
 * @param baseOverclock overclock headroom over a fuel's base rpm before upgrades
 */
public record MotorDefinition(Key id, MachineDefinition machine, Map<Key, FuelOutput> fuels,
        List<RelativeDirection> outputFaces, int upgradeSlots, int baseUnlocked, float baseOverclock) {

    /** Every data-defined motor. Rebuilt on reload. */
    public static final Registry<MotorDefinition> REGISTRY = Registries.create("motor");

    /** What a fuel is: a gas, a liquid, or a burnable item. */
    public enum FuelKind {
        GAS,
        FLUID,
        ITEM
    }

    /**
     * What one fuel yields.
     *
     * @param kind    gas, fluid or item — an item motor burns from a slot rather
     *                than a tank, which is why a solid fuel needs a burn time
     * @param rpm     rotational speed delivered while burning it
     * @param su      stress units it can support at that speed
     * @param perTick mB consumed per tick; unused for an item fuel
     * @param burnTime ticks one item lasts; unused for a gas or fluid fuel
     */
    public record FuelOutput(FuelKind kind, float rpm, float su, int perTick, int burnTime) {
    }

    /** The output for a fuel, or {@code null} if this motor cannot burn it. */
    public FuelOutput fuel(Key fuelId) {
        return fuels.get(fuelId);
    }

    /** Fuels of one kind, e.g. every gas this motor accepts. */
    public Map<Key, FuelOutput> fuelsOfKind(FuelKind kind) {
        Map<Key, FuelOutput> out = new java.util.LinkedHashMap<>();
        for (Map.Entry<Key, FuelOutput> e : fuels.entrySet())
            if (e.getValue().kind() == kind)
                out.put(e.getKey(), e.getValue());
        return out;
    }

    public static MotorDefinition byName(String name) {
        if (name == null || name.isBlank())
            return null;
        String trimmed = name.trim();
        return REGISTRY.get(trimmed.indexOf(':') >= 0 ? Key.of(trimmed) : Key.of("polyfills", trimmed));
    }

    @Override
    public String toString() {
        return id + "(" + fuels.size() + " fuel(s))";
    }
}
