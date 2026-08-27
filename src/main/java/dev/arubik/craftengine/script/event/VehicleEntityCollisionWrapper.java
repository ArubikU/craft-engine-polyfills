package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when a vehicle's bounding box collides with a live entity (a boat bumping into a villager,
 * a minecart running into a mob, ...) — unlike its sibling {@link VehicleBlockCollisionWrapper},
 * this one really IS {@link org.bukkit.event.Cancellable}. A script could use {@link #entity()} to
 * check if the collided entity is a tamed pet and {@link ScriptEvent#cancel} the collision so the
 * vehicle passes through it instead of shoving it aside.
 */
public final class VehicleEntityCollisionWrapper extends ScriptEvent {
    private final VehicleEntityCollisionEvent raw;

    public VehicleEntityCollisionWrapper(VehicleEntityCollisionEvent raw) {
        super("VehicleEntityCollisionEvent");
        this.raw = raw;
    }

    public VehicleEntityCollisionEvent raw() { return raw; }

    public ScriptValue vehicle() {
        return EntityType.wrap(((CraftEntity) raw.getVehicle()).getHandle());
    }

    public ScriptValue entity() {
        return EntityType.wrap(((CraftEntity) raw.getEntity()).getHandle());
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
