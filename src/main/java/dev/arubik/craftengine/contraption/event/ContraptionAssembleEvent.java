/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 *  org.jetbrains.annotations.Nullable
 */
package dev.arubik.craftengine.contraption.event;

import java.util.Collections;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

public class ContraptionAssembleEvent
extends Event
implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final World world;
    private final BlockPos bearing;
    private final Key type;
    private final Set<BlockPos> capturedPositions;
    private final Player cause;
    private boolean cancelled;

    public ContraptionAssembleEvent(World world, BlockPos bearing, Key type, Set<BlockPos> capturedPositions, @Nullable Player cause) {
        this.world = world;
        this.bearing = bearing;
        this.type = type;
        this.capturedPositions = Collections.unmodifiableSet(capturedPositions);
        this.cause = cause;
    }

    public World getWorld() {
        return this.world;
    }

    public BlockPos getBearingBlockPos() {
        return this.bearing;
    }

    public Location getBearingLocation() {
        return new Location(this.world, (double)this.bearing.getX(), (double)this.bearing.getY(), (double)this.bearing.getZ());
    }

    public Key getType() {
        return this.type;
    }

    public Set<BlockPos> getCapturedPositions() {
        return this.capturedPositions;
    }

    @Nullable
    public Player getCause() {
        return this.cause;
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

