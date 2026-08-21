/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  org.bukkit.block.Block
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockPlaceEvent
 */
package dev.arubik.craftengine;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public final class MachineOwnerListener
implements Listener {
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent e) {
        try {
            BlockEntityController blockEntityController;
            Block block = e.getBlockPlaced();
            ServerLevel level = ((CraftWorld)block.getWorld()).getHandle();
            BlockPos pos = new BlockPos(block.getX(), block.getY(), block.getZ());
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded((Level)level, pos);
            if (be != null && (blockEntityController = be.controller) instanceof AbstractMachineBlockEntity) {
                AbstractMachineBlockEntity mbe = (AbstractMachineBlockEntity)blockEntityController;
                mbe.setOwnerUuid(e.getPlayer().getUniqueId());
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

