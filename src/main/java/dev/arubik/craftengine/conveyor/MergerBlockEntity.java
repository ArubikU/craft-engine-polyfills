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
    protected void route(CEWorld world, BlockPos pos) {
        Direction out = facing();
        // Push as many buffered stacks forward as the downstream receiver accepts this
        // tick (a belt only takes one when its entry point is clear, so this self-paces).
        for (int i = 0; i < slots; i++) {
            if (slotEmpty(i))
                continue;
            org.bukkit.inventory.ItemStack stack = bukkitSlot(i);
            if (ConveyorRouting.push(world, pos, out, stack))
                clearSlot(i);
            else
                break; // downstream full -> hold everything (backpressure)
        }
    }
}
