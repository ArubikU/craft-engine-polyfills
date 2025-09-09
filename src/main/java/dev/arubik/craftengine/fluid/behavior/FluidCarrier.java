package dev.arubik.craftengine.fluid.behavior;

import dev.arubik.craftengine.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Interfaz simple para nodos que almacenan y transfieren fluidos.
 * Permite detectar compatibilidad entre Pipe/Pump/Valve sin instanceof concretos.
 */
public interface FluidCarrier {

    enum FluidAccessMode {
        ANYONE_CAN_TAKE,
        PUMP_VALVE_CAN_TAKE
    }

    FluidStack getStored(Level level, BlockPos pos);
    int insertFluid(Level level, BlockPos pos, FluidStack stack);
    int extractFluid(Level level, BlockPos pos, int max, java.util.function.Consumer<FluidStack> drained);

    /**
     * Controla quién puede extraer de este carrier.
     */
    FluidAccessMode getAccessMode();
}
