package dev.arubik.craftengine.contraption.event;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.arubik.craftengine.contraption.ContraptionEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Fired on the main thread from the master tick loop ({@code ContraptionEngine#tickAll}) when a
 * contraption's transform actually changed this tick — i.e. its bearing position translated
 * and/or its yaw rotated. It is deliberately NOT fired on idle/stalled ticks where the transform
 * is unchanged, so listeners aren't spammed 20×/second for a parked contraption.
 *
 * <p>Not cancellable — the movement has already been applied by the time this fires (the engine
 * decides movement via the stall-gate vote, not via events). This is a pure observation hook;
 * keep listeners cheap since it can fire every tick per moving contraption.
 */
public class ContraptionMoveEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ContraptionEntity entity;
    private final World world;
    private final Vec3 from;
    private final Vec3 to;
    private final double yawDelta;

    /**
     * @param entity   the contraption that moved
     * @param world    the world it lives in
     * @param from     its bearing position before this tick's movement
     * @param to       its bearing position after this tick's movement
     * @param yawDelta the yaw rotation (radians) applied this tick (0 for a pure translation)
     */
    public ContraptionMoveEvent(ContraptionEntity entity, World world, Vec3 from, Vec3 to, double yawDelta) {
        this.entity = entity;
        this.world = world;
        this.from = from;
        this.to = to;
        this.yawDelta = yawDelta;
    }

    /** The contraption that moved. */
    public ContraptionEntity getEntity() {
        return entity;
    }

    /** The world the contraption lives in. */
    public World getWorld() {
        return world;
    }

    /** The bearing's continuous world position before this tick's movement (NMS {@link Vec3}). */
    public Vec3 getFrom() {
        return from;
    }

    /** The bearing's continuous world position after this tick's movement (NMS {@link Vec3}). */
    public Vec3 getTo() {
        return to;
    }

    /** Bukkit-friendly view of {@link #getFrom()} in {@link #getWorld()}. */
    public Location getFromLocation() {
        return new Location(world, from.x, from.y, from.z);
    }

    /** Bukkit-friendly view of {@link #getTo()} in {@link #getWorld()}. */
    public Location getToLocation() {
        return new Location(world, to.x, to.y, to.z);
    }

    /** The yaw rotation (radians) applied this tick — 0 for a pure translation. */
    public double getYawDelta() {
        return yawDelta;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
