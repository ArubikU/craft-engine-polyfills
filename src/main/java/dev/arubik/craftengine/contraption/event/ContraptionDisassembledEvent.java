/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package dev.arubik.craftengine.contraption.event;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ContraptionDisassembledEvent
extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final UUID contraptionId;
    private final World world;
    private final BlockPos snappedBearing;
    private final Set<BlockPos> restingPositions;
    private final int quarterTurns;

    public ContraptionDisassembledEvent(UUID contraptionId, World world, BlockPos snappedBearing, Set<BlockPos> restingPositions, int quarterTurns) {
        this.contraptionId = contraptionId;
        this.world = world;
        this.snappedBearing = snappedBearing;
        this.restingPositions = Collections.unmodifiableSet(restingPositions);
        this.quarterTurns = quarterTurns;
    }

    public UUID getContraptionId() {
        return this.contraptionId;
    }

    public World getWorld() {
        return this.world;
    }

    public BlockPos getSnappedBearing() {
        return this.snappedBearing;
    }

    public Location getSnappedBearingLocation() {
        return new Location(this.world, (double)this.snappedBearing.getX(), (double)this.snappedBearing.getY(), (double)this.snappedBearing.getZ());
    }

    public Set<BlockPos> getRestingPositions() {
        return this.restingPositions;
    }

    public int getQuarterTurns() {
        return this.quarterTurns;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

