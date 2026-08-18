/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 */
package dev.arubik.craftengine.gas;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.util.TypedKey;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class GasCarrierImpl {
    public static int insertGas(Level level, BlockPos pos, GasStack stack, int capacity, TypedKey<GasStack> key) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        int[] accepted = new int[]{0};
        PersistentBlockEntity.executeAt(level, pos, p -> {
            int space;
            GasStack stored = p.getOrDefault(key, GasStack.EMPTY);
            if (stored.isEmpty()) {
                int move = Math.min(capacity, stack.getAmount());
                p.set(key, new GasStack(stack.getType(), move));
                accepted[0] = move;
            } else if (stored.isGasEqual(stack) && (space = capacity - stored.getAmount()) > 0) {
                int move = Math.min(space, stack.getAmount());
                stored.grow(move);
                p.set(key, new GasStack(stored.getType(), stored.getAmount()));
                accepted[0] = move;
            }
        });
        return accepted[0];
    }

    public static int extractGas(Level level, BlockPos pos, int max, Consumer<GasStack> drained, TypedKey<GasStack> key) {
        int[] moved = new int[]{0};
        PersistentBlockEntity.executeAt(level, pos, p -> {
            GasStack stored = p.getOrDefault(key, GasStack.EMPTY);
            if (stored.isEmpty()) {
                return;
            }
            int toMove = Math.min(max, stored.getAmount());
            GasStack out = new GasStack(stored.getType(), toMove);
            stored.shrink(toMove);
            if (stored.isEmpty()) {
                p.remove(key);
            } else {
                p.set(key, stored);
            }
            moved[0] = toMove;
            if (drained != null) {
                drained.accept(out);
            }
        });
        return moved[0];
    }

    public static GasStack getStoredGas(Level level, BlockPos pos, TypedKey<GasStack> key) {
        PersistentBlockEntity be = PersistentBlockEntity.getIfLoaded(level, pos);
        return be != null ? be.getOrDefault(key, GasStack.EMPTY) : GasStack.EMPTY;
    }
}

