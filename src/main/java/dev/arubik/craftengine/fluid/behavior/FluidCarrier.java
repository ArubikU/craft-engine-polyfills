package dev.arubik.craftengine.fluid.behavior;

import dev.arubik.craftengine.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Interfaz simple para nodos que almacenan y transfieren fluidos.
 * Permite detectar compatibilidad entre Pipe/Pump/Valve sin instanceof
 * concretos.
 */
public interface FluidCarrier {

    /**
     * Get fluid stored in this carrier.
     */
    FluidStack getStored(Level level, BlockPos pos);

    /**
     * Insert fluid into this carrier.
     * 
     * @return Amount actually inserted.
     */
    int insertFluid(Level level, BlockPos pos, FluidStack stack, net.minecraft.core.Direction side);

    /**
     * Extract fluid from this carrier.
     * 
     * @return Amount actually extracted.
     */
    int extractFluid(Level level, BlockPos pos, int max, java.util.function.Consumer<FluidStack> drained,
            net.minecraft.core.Direction side);

    /**
     * Insert fluid into a specific slot/tank.
     * Default implementation ignores slot and calls generic method.
     */
    default int insertFluid(Level level, BlockPos pos, FluidStack stack, net.minecraft.core.Direction side, int slot) {
        return insertFluid(level, pos, stack, side);
    }

    /**
     * Extract fluid from a specific slot/tank.
     * Default implementation ignores slot and calls generic method.
     */
    default int extractFluid(Level level, BlockPos pos, int max, java.util.function.Consumer<FluidStack> drained,
            net.minecraft.core.Direction side, int slot) {
        return extractFluid(level, pos, max, drained, side);
    }

    /**
     * Controla quién puede extraer de este carrier.
     */
    dev.arubik.craftengine.util.TransferAccessMode getAccessMode();

    // ---- hydraulic engine hooks (use each block's REAL store, not a placeholder) ----

    /** This carrier's fluid capacity in mB at {@code pos}. Override per block (tank/pump/pipe). */
    default long getCapacity(Level level, BlockPos pos) {
        return 1000L;
    }

    /**
     * Set this carrier's stored fluid DIRECTLY (engine apply path — bypasses IO-side gating). Default
     * writes the shared {@link dev.arubik.craftengine.fluid.FluidKeys#FLUID} store (pipes/tanks). Machines
     * override to write their own fluid tank.
     */
    default void setStoredRaw(Level level, BlockPos pos, FluidStack stack) {
        dev.arubik.craftengine.block.entity.PersistentBlockEntity.executeAt(level, pos, be -> {
            if (stack == null || stack.isEmpty())
                be.remove(dev.arubik.craftengine.fluid.FluidKeys.FLUID);
            else
                be.set(dev.arubik.craftengine.fluid.FluidKeys.FLUID, stack);
        });
    }

    /** Called after the engine changes this carrier's store, so it can refresh its blockstate/model
     * (e.g. a tank's fluidtype/level). Default no-op. */
    default void onStoreChanged(Level level, BlockPos pos) {
    }
}
