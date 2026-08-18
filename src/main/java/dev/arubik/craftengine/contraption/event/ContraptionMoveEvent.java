/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package dev.arubik.craftengine.contraption.event;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ContraptionMoveEvent
extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final ContraptionEntity entity;
    private final World world;
    private final Vec3 from;
    private final Vec3 to;
    private final double yawDelta;

    public ContraptionMoveEvent(ContraptionEntity entity, World world, Vec3 from, Vec3 to, double yawDelta) {
        this.entity = entity;
        this.world = world;
        this.from = from;
        this.to = to;
        this.yawDelta = yawDelta;
    }

    public ContraptionEntity getEntity() {
        return this.entity;
    }

    public World getWorld() {
        return this.world;
    }

    public Vec3 getFrom() {
        return this.from;
    }

    public Vec3 getTo() {
        return this.to;
    }

    public Location getFromLocation() {
        return new Location(this.world, this.from.x, this.from.y, this.from.z);
    }

    public Location getToLocation() {
        return new Location(this.world, this.to.x, this.to.y, this.to.z);
    }

    public double getYawDelta() {
        return this.yawDelta;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

