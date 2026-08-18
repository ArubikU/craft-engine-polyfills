/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockExplodeEvent
 *  org.bukkit.event.block.BlockFromToEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.entity.EntityExplodeEvent
 */
package dev.arubik.craftengine.contraption.physics;

import dev.arubik.craftengine.contraption.physics.PhysicsWorld;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public final class PhysicsWorldListener
implements Listener {
    private static final double ASSUMED_EXPLOSION_POWER = 4.0;

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        PhysicsWorldListener.wake(event.getBlock());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        PhysicsWorldListener.wake(event.getBlock());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockFromTo(BlockFromToEvent event) {
        PhysicsWorldListener.wake(event.getToBlock());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityExplode(EntityExplodeEvent event) {
        PhysicsWorldListener.blast(event.getLocation(), event.getYield(), event.getEntity().getWorld());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockExplode(BlockExplodeEvent event) {
        PhysicsWorldListener.blast(event.getBlock().getLocation(), event.getYield(), event.getBlock().getWorld());
    }

    private static void wake(Block block) {
        World bukkitWorld = block.getWorld();
        ServerLevel level = ((CraftWorld)bukkitWorld).getHandle();
        PhysicsWorld.wakeNear((ResourceKey<Level>)level.dimension(), (double)block.getX() + 0.5, (double)block.getY() + 0.5, (double)block.getZ() + 0.5);
    }

    private static void blast(Location at, float yield, World world) {
        if (at == null || world == null) {
            return;
        }
        ServerLevel level = ((CraftWorld)world).getHandle();
        PhysicsWorld.applyExplosion((ResourceKey<Level>)level.dimension(), at.getX(), at.getY(), at.getZ(), 4.0);
        PhysicsWorld.wakeNear((ResourceKey<Level>)level.dimension(), at.getX(), at.getY(), at.getZ());
    }
}

