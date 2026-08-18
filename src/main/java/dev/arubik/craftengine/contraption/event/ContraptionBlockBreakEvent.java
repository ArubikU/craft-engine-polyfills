/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.state.BlockState
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package dev.arubik.craftengine.contraption.event;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ContraptionBlockBreakEvent
extends Event
implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final ContraptionEntity entity;
    private final World world;
    private final BlockPos worldPos;
    private final BlockState state;
    private boolean cancelled;

    public ContraptionBlockBreakEvent(ContraptionEntity entity, World world, BlockPos worldPos, BlockState state) {
        this.entity = entity;
        this.world = world;
        this.worldPos = worldPos;
        this.state = state;
    }

    public ContraptionEntity getEntity() {
        return this.entity;
    }

    public World getWorld() {
        return this.world;
    }

    public BlockPos getWorldPos() {
        return this.worldPos;
    }

    public Location getLocation() {
        return new Location(this.world, (double)this.worldPos.getX(), (double)this.worldPos.getY(), (double)this.worldPos.getZ());
    }

    public BlockState getState() {
        return this.state;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

