/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.Level
 */
package dev.arubik.craftengine.gas;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.gas.GasKeys;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.util.TransferAccessMode;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface GasCarrier {
    public GasStack getStoredGas(Level var1, BlockPos var2);

    public int insertGas(Level var1, BlockPos var2, GasStack var3, Direction var4);

    public int extractGas(Level var1, BlockPos var2, int var3, Consumer<GasStack> var4, Direction var5);

    default public int insertGas(Level level, BlockPos pos, GasStack stack, Direction side, int slot) {
        return this.insertGas(level, pos, stack, side);
    }

    default public int extractGas(Level level, BlockPos pos, int max, Consumer<GasStack> drained, Direction side, int slot) {
        return this.extractGas(level, pos, max, drained, side);
    }

    public TransferAccessMode getAccessMode();

    default public long getGasCapacity(Level level, BlockPos pos) {
        return 1000L;
    }

    default public void setStoredGasRaw(Level level, BlockPos pos, GasStack stack) {
        PersistentBlockEntity.executeAt(level, pos, be -> {
            if (stack == null || stack.isEmpty()) {
                be.remove(GasKeys.GAS);
            } else {
                be.set(GasKeys.GAS, stack);
            }
        });
    }
}

