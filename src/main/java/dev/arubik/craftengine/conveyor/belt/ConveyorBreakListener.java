package dev.arubik.craftengine.conveyor.belt;

import dev.arubik.craftengine.conveyor.routing.AbstractRouterBlockEntity;
import dev.arubik.craftengine.conveyor.belt.ConveyorBehavior;
import dev.arubik.craftengine.conveyor.belt.ConveyorBlockEntity;
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

public final class ConveyorBreakListener
implements Listener {
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBreak(BlockBreakEvent event) {
        ConveyorBlockEntity seg;
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
        if (be != null) {
            BlockEntityController blockEntityController = be.controller;
            if (blockEntityController instanceof AbstractRouterBlockEntity) {
                AbstractRouterBlockEntity r = (AbstractRouterBlockEntity)blockEntityController;
                r.dropAndDespawn();
                return;
            }
        }
        if ((seg = ConveyorBlockEntity.conveyorAt(world, pos)) == null) {
            return;
        }
        ConveyorBehavior.markListenerHandled(block.getX(), block.getY(), block.getZ());
        seg.onBroken(world, pos, seg.facing());
    }
}

