package dev.arubik.craftengine.conveyor.routing;

import dev.arubik.craftengine.conveyor.routing.AbstractRouterBlockEntity;
import dev.arubik.craftengine.conveyor.routing.ConveyorReceiver;
import dev.arubik.craftengine.conveyor.routing.ConveyorRouting;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;

public class MergerBlockEntity
extends AbstractRouterBlockEntity {
    public static final int DEFAULT_SLOTS = 6;

    public MergerBlockEntity(BlockEntity blockEntity, Direction defaultFacing, int slots) {
        super(blockEntity, defaultFacing, slots);
    }

    @Override
    protected boolean acceptsFrom(Direction sourceFacing) {
        return sourceFacing == null || sourceFacing != this.facing().opposite();
    }

    @Override
    protected Direction[] inputSides() {
        Direction f = this.facing();
        return new Direction[]{f.opposite(), ConveyorRouting.cw(f), ConveyorRouting.ccw(f)};
    }

    @Override
    protected Direction[] outputSides() {
        return new Direction[]{this.facing()};
    }

    @Override
    protected Direction chooseExit(CEWorld world, BlockPos pos) {
        Direction out = this.facing();
        ConveyorReceiver r = ConveyorRouting.receiverAt(world, pos, out);
        return r != null && !r.isFull() ? out : null;
    }
}

