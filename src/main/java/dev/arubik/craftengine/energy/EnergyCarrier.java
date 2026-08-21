package dev.arubik.craftengine.energy;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.util.TransferAccessMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

/**
 * Capability interface for a CraftEnergy node (cable/cell/machine) — the FE-alike counterpart of
 * {@link dev.arubik.craftengine.fluid.behavior.FluidCarrier} / {@link dev.arubik.craftengine.gas.GasCarrier}.
 * Energy has no "type" the way fluid/gas do, so this is simpler: just a stored/capacity int pair.
 */
public interface EnergyCarrier {

    /** Energy currently stored at {@code pos}, in CraftEnergy units (CE, 1:1 with Forge Energy). */
    int getStoredEnergy(Level level, BlockPos pos);

    /** Insert energy from {@code side}; returns the amount actually accepted. */
    int insertEnergy(Level level, BlockPos pos, int amount, Direction side);

    /** Extract up to {@code max} energy toward {@code side}; returns the amount actually removed. */
    int extractEnergy(Level level, BlockPos pos, int max, Direction side);

    TransferAccessMode getAccessMode();

    /** May energy LEAVE this block through {@code side} (world direction pointing away from this block)? */
    default boolean canEnergyOutput(Level level, BlockPos pos, Direction side) {
        return true;
    }

    /** May energy ENTER this block through {@code side} (same convention as {@link #insertEnergy})? */
    default boolean canEnergyInput(Level level, BlockPos pos, Direction side) {
        return true;
    }

    default long getEnergyCapacity(Level level, BlockPos pos) {
        return 10000L;
    }

    /** Engine apply path — bypasses IO-side gating. Default writes the shared {@link EnergyKeys#ENERGY} store
     * (cables); machines override to write their own buffer. */
    default void setStoredEnergyRaw(Level level, BlockPos pos, int amount) {
        PersistentBlockEntity.executeAt(level, pos, be -> {
            if (amount <= 0)
                be.remove(EnergyKeys.ENERGY);
            else
                be.set(EnergyKeys.ENERGY, amount);
        });
    }
}
