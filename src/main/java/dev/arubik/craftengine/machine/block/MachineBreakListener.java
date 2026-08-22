/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  org.bukkit.block.Block
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 */
package dev.arubik.craftengine.machine.block;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity;
import dev.arubik.craftengine.util.CeWorlds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public final class MachineBreakListener
implements Listener {
    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        BlockEntityController blockEntityController;
        CEWorld world;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        try {
            world = CeWorlds.of(block.getWorld()).storageWorld();
        }
        catch (Throwable t) {
            return;
        }
        if (world == null) {
            return;
        }
        BlockPos cePos = new BlockPos(block.getX(), block.getY(), block.getZ());
        BlockEntity be = world.getBlockEntityAtIfLoaded(cePos);
        if (be == null || !((blockEntityController = be.controller) instanceof DataMachineBlockEntity)) {
            return;
        }
        DataMachineBlockEntity dm = (DataMachineBlockEntity)blockEntityController;
        if (dm.definition() == null || dm.definition().attackScript() == null) {
            return;
        }
        event.setCancelled(true);
        ServerPlayer nmsPlayer = ((CraftPlayer)event.getPlayer()).getHandle();
        dm.runInteractScript(dm.definition().attackScript(), nmsPlayer, "on_left_click");
    }

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
        BlockPos cePos = new BlockPos(block.getX(), block.getY(), block.getZ());
        BlockEntity be = world.getBlockEntityAtIfLoaded(cePos);
        if (be == null || !((blockEntityController = be.controller) instanceof AbstractMachineBlockEntity)) {
            return;
        }
        AbstractMachineBlockEntity m = (AbstractMachineBlockEntity)blockEntityController;
        // Declaring on_break hands the machine's container to the script's own judgment (e.g. a
        // placeable-container item like the backpack dropping itself back as one reconstructed
        // item via Machine.to_item/Machine.drop_item instead of spilling its contents loose) —
        // skip the default drop-everything-on-the-ground behavior entirely; on_break itself fires
        // from DataMachineBlockEntity#onRemove (below, after this event resolves), which has no
        // player of its own but doesn't need one — dropping a replacement item just needs a
        // position, which the script gets from Machine.pos.
        if (m instanceof DataMachineBlockEntity dm && dm.definition() != null && dm.definition().onBreakScript() != null) {
            return;
        }
        try {
            Level level = (Level)world.world().minecraftWorld();
            if (level == null) {
                return;
            }
            m.dropAllContents(level, new net.minecraft.core.BlockPos(block.getX(), block.getY(), block.getZ()));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

