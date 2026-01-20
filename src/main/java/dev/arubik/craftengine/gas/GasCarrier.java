package dev.arubik.craftengine.gas;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;

public interface GasCarrier {
    GasStack getStoredGas(Level level, BlockPos pos);

    int insertGas(Level level, BlockPos pos, GasStack stack, Direction side);

    int extractGas(Level level, BlockPos pos, int max, java.util.function.Consumer<GasStack> drained, Direction side);

    /**
     * Insert gas into a specific slot/tank.
     * Default implementation ignores slot and calls generic method.
     */
    default int insertGas(Level level, BlockPos pos, GasStack stack, Direction side, int slot) {
        return insertGas(level, pos, stack, side);
    }

    /**
     * Extract gas from a specific slot/tank.
     * Default implementation ignores slot and calls generic method.
     */
    default int extractGas(Level level, BlockPos pos, int max, java.util.function.Consumer<GasStack> drained,
            Direction side, int slot) {
        return extractGas(level, pos, max, drained, side);
    }

    /**
     * Controla quién puede extraer de este carrier.
     */
    dev.arubik.craftengine.util.TransferAccessMode getAccessMode();
}
