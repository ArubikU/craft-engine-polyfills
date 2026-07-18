package dev.arubik.craftengine.conveyor;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;

/**
 * Drops contents + clears renders for the conveyor-IO blocks (depot / merger /
 * splitter) on a Bukkit {@link BlockBreakEvent}. CraftEngine's
 * {@code affectNeighborsAfterRemoval} callback is unreliable for player breaks
 * (esp. creative), so teardown runs here while the block entity is still resolvable
 * (the block hasn't been removed yet at MONITOR/ignoreCancelled).
 */
public final class ConveyorIoBreakListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        CEWorld world;
        try {
            world = dev.arubik.craftengine.util.CeWorlds.of(block.getWorld()).storageWorld();
        } catch (Throwable t) {
            return;
        }
        if (world == null)
            return;
        BlockPos pos = new BlockPos(block.getX(), block.getY(), block.getZ());
        BlockEntity be = world.getBlockEntityAtIfLoaded(pos);
        if (be == null)
            return;
        if (be.controller instanceof DepotBlockEntity depot) {
            depot.dropAll();
        } else if (be.controller instanceof AbstractRouterBlockEntity router) {
            RouterDrops.dropBuffer(world, pos, router);
        }
    }
}
