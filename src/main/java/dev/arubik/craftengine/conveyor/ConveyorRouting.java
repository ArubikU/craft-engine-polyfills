package dev.arubik.craftengine.conveyor;

import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;

/**
 * Shared helpers for the conveyor-network "router" blocks (merger / splitter /
 * depot). They locate a {@link ConveyorReceiver} at an output cell and push a
 * single stack into it, mirroring the belt's own hand-off contract.
 */
final class ConveyorRouting {

    private ConveyorRouting() {
    }

    /** Clockwise horizontal neighbour of {@code d} (NORTH→EAST→SOUTH→WEST). */
    static Direction cw(Direction d) {
        switch (d) {
            case NORTH:
                return Direction.EAST;
            case EAST:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.WEST;
            case WEST:
                return Direction.NORTH;
            default:
                return d;
        }
    }

    /** Counter-clockwise horizontal neighbour of {@code d}. */
    static Direction ccw(Direction d) {
        switch (d) {
            case NORTH:
                return Direction.WEST;
            case WEST:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.EAST;
            case EAST:
                return Direction.NORTH;
            default:
                return d;
        }
    }

    /**
     * The {@link ConveyorReceiver} controller at the cell {@code dir} away from
     * {@code pos}, checking the flat cell and the ±1 Y cells (so a sloped belt
     * entering/leaving is still found). Returns null if none.
     */
    static ConveyorReceiver receiverAt(CEWorld world, BlockPos pos, Direction dir) {
        BlockPos flat = pos.relative(dir);
        BlockPos[] cands = {
                flat,
                new BlockPos(flat.x(), flat.y() + 1, flat.z()),
                new BlockPos(flat.x(), flat.y() - 1, flat.z())
        };
        for (BlockPos c : cands) {
            BlockEntity be = world.getBlockEntityAtIfLoaded(c);
            if (be != null && be.controller instanceof ConveyorReceiver r)
                return r;
        }
        return null;
    }

    /**
     * Try to push one stack out of {@code pos} toward {@code dir}. Passes
     * {@code dir} as the source-facing so the receiving belt treats it as an
     * in-line (straight) entry when it faces the same way, or a corner otherwise.
     *
     * @return true if a receiver accepted the whole stack.
     */
    static boolean push(CEWorld world, BlockPos pos, Direction dir, org.bukkit.inventory.ItemStack stack) {
        if (stack == null || stack.getType().isAir())
            return false;
        ConveyorReceiver r = receiverAt(world, pos, dir);
        if (r == null || r.isFull())
            return false;
        return r.receiveConveyorItem(stack, dir);
    }
}
