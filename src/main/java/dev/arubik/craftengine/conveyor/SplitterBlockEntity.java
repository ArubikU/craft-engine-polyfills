package dev.arubik.craftengine.conveyor;

import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;

/**
 * Conveyor SPLITTER: 1 input (back), 3 outputs (front / right / left). Distributes
 * buffered items as evenly as possible across the three output sides, independent of
 * how fast each output drains or how the items stack: each item is sent to the output
 * (with room) that has received the FEWEST items so far. Outputs that are full/blocked
 * are skipped, so a stalled branch simply lets the others carry more — and when it
 * frees up its low count makes the splitter favour it again until it re-balances.
 */
public class SplitterBlockEntity extends AbstractRouterBlockEntity {

    public static final int DEFAULT_SLOTS = 6;

    /** Running per-output dispatch counts: [0]=front, [1]=right, [2]=left. */
    private final long[] dispatched = new long[3];

    public SplitterBlockEntity(BlockEntity blockEntity, Direction defaultFacing, int slots) {
        super(blockEntity, defaultFacing, slots);
    }

    @Override
    protected boolean acceptsFrom(Direction sourceFacing) {
        // Only the back input feeds the splitter: a belt travelling along our facing
        // (it sits behind us and moves toward us).
        return sourceFacing == null || sourceFacing == facing();
    }

    @Override
    protected void route(CEWorld world, BlockPos pos) {
        Direction front = facing();
        Direction right = ConveyorRouting.cw(front);
        Direction left = ConveyorRouting.ccw(front);
        Direction[] outs = { front, right, left };

        for (int i = 0; i < slots; i++) {
            if (slotEmpty(i))
                continue;
            org.bukkit.inventory.ItemStack stack = bukkitSlot(i);
            if (!dispatchBalanced(world, pos, outs, stack))
                break; // every output full/blocked -> hold (backpressure)
            clearSlot(i);
        }
    }

    /**
     * Send one stack to the least-loaded output that currently has room. Returns
     * false if no output could take it (all full/blocked).
     */
    private boolean dispatchBalanced(CEWorld world, BlockPos pos, Direction[] outs, org.bukkit.inventory.ItemStack stack) {
        int best = -1;
        long bestCount = Long.MAX_VALUE;
        for (int k = 0; k < outs.length; k++) {
            ConveyorReceiver r = ConveyorRouting.receiverAt(world, pos, outs[k]);
            if (r == null || r.isFull())
                continue;
            if (dispatched[k] < bestCount) {
                bestCount = dispatched[k];
                best = k;
            }
        }
        if (best < 0)
            return false;
        if (ConveyorRouting.push(world, pos, outs[best], stack)) {
            dispatched[best]++;
            return true;
        }
        return false;
    }
}
