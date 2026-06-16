package dev.arubik.craftengine.conveyor;

import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;

/**
 * Conveyor MERGER: 3 inputs (back / left / right), 1 output (front = {@code facing}).
 * Accepts items from any side except the front, buffers them, and feeds them onto a
 * single downstream belt/receiver in front, first-buffered-first-out.
 */
public class MergerBlockEntity extends AbstractRouterBlockEntity {

    public static final int DEFAULT_SLOTS = 6;

    public MergerBlockEntity(BlockEntity blockEntity, Direction defaultFacing, int slots) {
        super(blockEntity, defaultFacing, slots);
    }

    @Override
    protected boolean acceptsFrom(Direction sourceFacing) {
        // Reject a belt sitting on the front (output) side feeding backwards into us.
        return sourceFacing == null || sourceFacing != facing().opposite();
    }

    @Override
    protected Direction[] inputSides() {
        Direction f = facing();
        return new Direction[] { f.opposite(), ConveyorRouting.cw(f), ConveyorRouting.ccw(f) }; // back/left/right
    }

    @Override
    protected Direction[] outputSides() {
        return new Direction[] { facing() }; // front: the single output belt
    }

    @Override
    protected Direction chooseExit(CEWorld world, BlockPos pos) {
        Direction out = facing();
        ConveyorReceiver r = ConveyorRouting.receiverAt(world, pos, out);
        return (r != null && !r.isFull()) ? out : null; // single output: front
    }
}
