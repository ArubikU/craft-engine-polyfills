package dev.arubik.craftengine.energy;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/** Static store helpers shared by plain-conduit energy carriers (cables), mirroring
 * {@code GasCarrierImpl}/{@code FluidCarrierImpl} but for a plain int buffer. */
public final class EnergyCarrierImpl {

    private EnergyCarrierImpl() {
    }

    public static int insertEnergy(Level level, BlockPos pos, int amount, int capacity) {
        if (amount <= 0)
            return 0;
        int[] accepted = { 0 };
        PersistentBlockEntity.executeAt(level, pos, be -> {
            int stored = be.getOrDefault(EnergyKeys.ENERGY, 0);
            int space = Math.max(0, capacity - stored);
            int move = Math.min(space, amount);
            if (move > 0) {
                be.set(EnergyKeys.ENERGY, stored + move);
                accepted[0] = move;
            }
        });
        return accepted[0];
    }

    public static int extractEnergy(Level level, BlockPos pos, int max) {
        int[] moved = { 0 };
        PersistentBlockEntity.executeAt(level, pos, be -> {
            int stored = be.getOrDefault(EnergyKeys.ENERGY, 0);
            if (stored <= 0)
                return;
            int take = Math.min(max, stored);
            int remaining = stored - take;
            if (remaining <= 0)
                be.remove(EnergyKeys.ENERGY);
            else
                be.set(EnergyKeys.ENERGY, remaining);
            moved[0] = take;
        });
        return moved[0];
    }

    public static int getStoredEnergy(Level level, BlockPos pos) {
        PersistentBlockEntity be = PersistentBlockEntity.getIfLoaded(level, pos);
        return be != null ? be.getOrDefault(EnergyKeys.ENERGY, 0) : 0;
    }
}
