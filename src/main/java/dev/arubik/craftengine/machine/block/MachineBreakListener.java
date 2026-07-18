package dev.arubik.craftengine.machine.block;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.world.CEWorld;

/**
 * Drops a machine's stored items (input / output / fuel / upgrades + any in-flight funnel
 * transit) when the block is broken. Driven from a Bukkit {@link BlockBreakEvent} because
 * the behavior's {@code affectNeighborsAfterRemoval} callback fires AFTER the block (and its
 * block entity) is already gone — so it sees a null BE and drops nothing. At MONITOR (the break
 * will proceed) the block is still present, so the BE is still loaded and its contents readable.
 */
public final class MachineBreakListener implements Listener {

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
        net.momirealms.craftengine.core.world.BlockPos cePos =
                new net.momirealms.craftengine.core.world.BlockPos(block.getX(), block.getY(), block.getZ());
        BlockEntity be = world.getBlockEntityAtIfLoaded(cePos);
        if (be == null || !(be.controller instanceof AbstractMachineBlockEntity m))
            return;
        try {
            Level level = (Level) world.world().minecraftWorld();
            if (level == null)
                return;
            m.dropAllContents(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
        } catch (Throwable ignored) {
        }
    }
}
