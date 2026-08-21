package dev.arubik.craftengine.energy;

import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.util.TypedKey;

/**
 * NBT keys for CraftEnergy (a Forge-Energy-alike: one homogeneous resource, no
 * {@code EnergyType} registry needed — unlike {@code FluidKeys}/{@code GasKeys} there is no
 * complex stack to (de)serialize, just a plain int.
 */
public final class EnergyKeys {

    public static final TypedKey<Integer> ENERGY = TypedKey.of("craftengine", "energy", NbtType.INTEGER);
    public static final TypedKey<String> TRANSFER_HISTORY = TypedKey.of("craftengine", "energy_history",
            NbtType.STRING);

    private EnergyKeys() {
    }
}
