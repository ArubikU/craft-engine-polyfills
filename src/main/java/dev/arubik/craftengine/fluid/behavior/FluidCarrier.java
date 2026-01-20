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
}
