package dev.arubik.craftengine.conveyor;

import net.momirealms.craftengine.core.util.Direction;

/**
 * Contract for any block-entity controller that can accept an item pushed off the
 * end of a conveyor belt. Implement this on machines, buffers, etc. so a belt can
 * deliver into them instead of dropping the item on the ground.
 *
 * <p>Backpressure: if {@link #isFull()} returns true the belt does NOT drop the
 * item — its front item stalls at the end, which makes the followers queue and
 * the whole 1×1 line stop until the receiver frees up.</p>
 *
 * <p>{@link ConveyorBlockEntity} itself is a receiver; conveyor→conveyor transfer
 * uses a richer path (display-entity hand-off + corner animation), so the belt
 * special-cases other belts and falls back to this generic contract for everything
 * else.</p>
 */
public interface ConveyorReceiver {

    /** True when this receiver cannot take another item right now (causes belt stall). */
    boolean isFull();

    /**
     * Take one item being pushed from a conveyor whose travel direction is
     * {@code sourceFacing} (so the item arrives from {@code sourceFacing.opposite()}).
     *
     * @return true if the item was accepted (the belt then clears its slot); false to
     *         leave it on the belt (stall).
     */
    boolean receiveConveyorItem(org.bukkit.inventory.ItemStack stack, Direction sourceFacing);
}
