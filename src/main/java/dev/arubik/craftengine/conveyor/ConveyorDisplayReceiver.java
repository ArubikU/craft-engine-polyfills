package dev.arubik.craftengine.conveyor;

import net.momirealms.craftengine.core.util.Direction;

/**
 * A {@link ConveyorReceiver} that can ADOPT the sender's live item-display entity on hand-off,
 * instead of having the sender despawn its display and the receiver spawn a fresh one. Adopting the
 * same entity removes the 1-tick despawn/respawn gap, so the carried item never flickers across the
 * boundary (same seamless path conveyor belts already use between each other).
 */
public interface ConveyorDisplayReceiver extends ConveyorReceiver {

    /**
     * Take the item AND its existing display entity. Implementations should reuse {@code display}
     * (not spawn a new one) so the visual is continuous.
     *
     * @param spawned whether {@code display} has already been shown to clients
     * @return true if adopted (the sender must then drop its reference WITHOUT despawning)
     */
    boolean adoptConveyorItem(org.bukkit.inventory.ItemStack stack, float jitter,
            ConveyorItemDisplay display, boolean spawned, Direction sourceFacing);
}
