package dev.arubik.craftengine.util;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class BlockEntityBehaviorListener implements Listener{
    

    @EventHandler
    public void onPistonExtend(org.bukkit.event.block.BlockPistonExtendEvent event) {
        event.getBlocks().forEach(b -> {
            Level level = ((CraftWorld) b.getLocation().getWorld()).getHandle();
            BlockPos pos = new BlockPos(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ());
            Block newBlock = b.getRelative(event.getDirection());
            BlockPos newPos = new BlockPos(newBlock.getX(), newBlock.getY(), newBlock.getZ());
            SyncedGuiHolder.get(level, pos).ifPresent(holder -> {
                holder.closeViewers();
                BlockEntityBehaviorController.move(level, pos, newPos);
            });
        });
    }
}
