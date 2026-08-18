/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.util.Direction
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 */
package dev.arubik.craftengine.conveyor;

import dev.arubik.craftengine.conveyor.AbstractRouterBlockEntity;
import dev.arubik.craftengine.conveyor.ConveyorReceiver;
import dev.arubik.craftengine.conveyor.ConveyorRouting;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;

public class SplitterBlockEntity
extends AbstractRouterBlockEntity {
    public static final int DEFAULT_SLOTS = 6;
    private final long[] dispatched = new long[3];

    public SplitterBlockEntity(BlockEntity blockEntity, Direction defaultFacing, int slots) {
        super(blockEntity, defaultFacing, slots);
    }

    @Override
    protected boolean acceptsFrom(Direction sourceFacing) {
        return sourceFacing == null || sourceFacing == this.facing();
    }

    @Override
    protected Direction[] inputSides() {
        return new Direction[]{this.facing().opposite()};
    }

    @Override
    protected Direction[] outputSides() {
        Direction f = this.facing();
        return new Direction[]{f, ConveyorRouting.cw(f), ConveyorRouting.ccw(f)};
    }

    @Override
    protected Direction chooseExit(CEWorld world, BlockPos pos) {
        Direction front = this.facing();
        Direction[] outs = new Direction[]{front, ConveyorRouting.cw(front), ConveyorRouting.ccw(front)};
        int best = -1;
        long bestCount = Long.MAX_VALUE;
        for (int k = 0; k < outs.length; ++k) {
            ConveyorReceiver r = ConveyorRouting.receiverAt(world, pos, outs[k]);
            if (r == null || r.isFull() || this.dispatched[k] >= bestCount) continue;
            bestCount = this.dispatched[k];
            best = k;
        }
        return best < 0 ? null : outs[best];
    }

    @Override
    protected void onDispatched(Direction dir) {
        Direction front = this.facing();
        if (dir == front) {
            this.dispatched[0] = this.dispatched[0] + 1L;
        } else if (dir == ConveyorRouting.cw(front)) {
            this.dispatched[1] = this.dispatched[1] + 1L;
        } else if (dir == ConveyorRouting.ccw(front)) {
            this.dispatched[2] = this.dispatched[2] + 1L;
        }
    }
}

