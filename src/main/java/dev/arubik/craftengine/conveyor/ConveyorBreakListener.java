package dev.arubik.craftengine.conveyor;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;

/**
 * Runs the conveyor break/teardown logic from a Bukkit {@link BlockBreakEvent}.
 *
 * <p>CraftEngine's {@code affectNeighborsAfterRemoval} behavior callback proved
 * unreliable for player breaks (especially in creative), so the belt teardown
 * (drop carried items, move/destroy segments) is driven here instead. The
 * behavior callback still runs for non-player removals (pistons); a small
 * handled-set prevents double processing when both paths fire for one break.</p>
 */
public final class ConveyorBreakListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        CEWorld world;
        try {
            world = new BukkitWorld(block.getWorld()).storageWorld();
        } catch (Throwable t) {
            return;
        }
        if (world == null)
            return;
        BlockPos pos = new BlockPos(block.getX(), block.getY(), block.getZ());
        // Funnel / router (splitter/merger) also hold an in-transit render display whose
        // affectNeighborsAfterRemoval is unreliable on creative player breaks -> despawn here.
        net.momirealms.craftengine.core.block.entity.BlockEntity be = world.getBlockEntityAtIfLoaded(pos);
        if (be != null) {
            if (be.controller instanceof FunnelBlockEntity f) {
                f.dropTransit();
                return;
            }
            if (be.controller instanceof AbstractRouterBlockEntity r) {
                r.dropAndDespawn();
                return;
            }
        }
        ConveyorBlockEntity seg = ConveyorBlockEntity.conveyorAt(world, pos);
        if (seg == null)
            return;
        // Tell the behavior callback we already handled this position (no double run).
        ConveyorBehavior.markListenerHandled(block.getX(), block.getY(), block.getZ());
        seg.onBroken(world, pos, seg.facing());
    }
}
