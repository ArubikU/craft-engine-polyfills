package dev.arubik.craftengine.rotation;

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
 * Drops the advanced vapor motor's installed upgrade items when the block is broken.
 * (CraftEngine's removal callback is unreliable for player/creative breaks, so this
 * runs from a Bukkit {@link BlockBreakEvent} while the block entity is still resolvable.)
 */
public final class GasMotorBreakListener implements Listener {

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
        if (be != null && be.controller instanceof GasMotorMk1BlockEntity motor)
            motor.dropUpgrades();
    }
}
