/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  org.bukkit.block.Block
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 */
package dev.arubik.craftengine.rotation;

import dev.arubik.craftengine.rotation.DataMotorBlockEntity;
import dev.arubik.craftengine.util.CeWorlds;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public final class DataMotorBreakListener
implements Listener {
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBreak(BlockBreakEvent event) {
        BlockEntityController blockEntityController;
        CEWorld world;
        Block block = event.getBlock();
        try {
            world = CeWorlds.of(block.getWorld()).storageWorld();
        }
        catch (Throwable t) {
            return;
        }
        if (world == null) {
            return;
        }
        BlockPos pos = new BlockPos(block.getX(), block.getY(), block.getZ());
        BlockEntity be = world.getBlockEntityAtIfLoaded(pos);
        if (be != null && (blockEntityController = be.controller) instanceof DataMotorBlockEntity) {
            DataMotorBlockEntity motor = (DataMotorBlockEntity)blockEntityController;
            motor.dropUpgrades();
        }
    }
}

