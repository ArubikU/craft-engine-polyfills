package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires every tick a vehicle actually moves (a boat drifting, a minecart rolling along rails, ...)
 * — read-only (the raw Bukkit event isn't {@link org.bukkit.event.Cancellable} and exposes no
 * setters for {@code from}/{@code to}). A script could use {@link #to()} to detect a minecart
 * crossing into a new region and trigger a scripted event there.
 */
public final class VehicleMoveWrapper extends ScriptEvent {
    private final VehicleMoveEvent raw;

    public VehicleMoveWrapper(VehicleMoveEvent raw) {
        super("VehicleMoveEvent");
        this.raw = raw;
    }

    public VehicleMoveEvent raw() { return raw; }

    public ScriptValue vehicle() {
        return EntityType.wrap(((CraftEntity) raw.getVehicle()).getHandle());
    }

    public ScriptValue from() { return ScriptEventUtil.wrapLocation(raw.getFrom()); }

    public ScriptValue to() { return ScriptEventUtil.wrapLocation(raw.getTo()); }

    // VehicleMoveEvent isn't Cancellable — no proxy, uses ScriptEvent's own flag.
}
