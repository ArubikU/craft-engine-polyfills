/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.Level
 */
package dev.arubik.craftengine.fluid;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.fluid.FluidKeys;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.util.TypedKey;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class FluidCarrierImpl {
    public static FluidStack getStored(Level level, BlockPos pos) {
        PersistentBlockEntity be = PersistentBlockEntity.getIfLoaded(level, pos);
        return be != null ? be.getOrDefault(FluidKeys.FLUID, FluidStack.EMPTY) : FluidStack.EMPTY;
    }

    public static int insertFluid(Level level, BlockPos pos, FluidStack stack, int capacity, int pressureBoost) {
        return FluidCarrierImpl.insertFluid(level, pos, stack, capacity, pressureBoost, FluidKeys.FLUID);
    }

    public static int insertFluid(Level level, BlockPos pos, FluidStack stack, int capacity, int pressureBoost, Direction direction) {
        return FluidCarrierImpl.insertFluid(level, pos, stack, capacity, pressureBoost, FluidKeys.FLUID);
    }

    public static int insertFluid(Level level, BlockPos pos, FluidStack stack, int capacity, int pressureBoost, TypedKey<FluidStack> key) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        FluidStack incoming = stack;
        if (pressureBoost > 0) {
            incoming = new FluidStack(stack.getType(), stack.getAmount(), Math.max(stack.getPressure(), pressureBoost));
        }
        int[] accepted = new int[]{0};
        FluidStack finalIncoming = incoming;
        PersistentBlockEntity.executeAt(level, pos, p -> {
            int space;
            FluidStack stored = p.getOrDefault(key, FluidStack.EMPTY);
            if (stored.isEmpty()) {
                int move = Math.min(capacity, finalIncoming.getAmount());
                p.set(key, new FluidStack(finalIncoming.getType(), move, finalIncoming.getPressure()));
                accepted[0] = move;
            } else if (stored.getType() == finalIncoming.getType() && (space = capacity - stored.getAmount()) > 0) {
                int move;
                int oldAmt = stored.getAmount();
                int blended = oldAmt + (move = Math.min(space, finalIncoming.getAmount())) > 0 ? Math.round((float)(oldAmt * stored.getPressure() + move * finalIncoming.getPressure()) / (float)(oldAmt + move)) : finalIncoming.getPressure();
                stored.addAmount(move);
                p.set(key, new FluidStack(stored.getType(), stored.getAmount(), blended));
                accepted[0] = move;
            }
        });
        return accepted[0];
    }

    public static int extractFluid(Level level, BlockPos pos, int max, Consumer<FluidStack> drained) {
        return FluidCarrierImpl.extractFluid(level, pos, max, drained, FluidKeys.FLUID);
    }

    public static int extractFluid(Level level, BlockPos pos, int max, Consumer<FluidStack> drained, Direction direction) {
        return FluidCarrierImpl.extractFluid(level, pos, max, drained, FluidKeys.FLUID);
    }

    public static int extractFluid(Level level, BlockPos pos, int max, Consumer<FluidStack> drained, TypedKey<FluidStack> key) {
        int[] moved = new int[]{0};
        PersistentBlockEntity.executeAt(level, pos, p -> {
            FluidStack stored = p.getOrDefault(key, FluidStack.EMPTY);
            if (stored.isEmpty()) {
                return;
            }
            int toMove = Math.min(max, stored.getAmount());
            FluidStack out = new FluidStack(stored.getType(), toMove, stored.getPressure());
            stored.removeAmount(toMove);
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
}

