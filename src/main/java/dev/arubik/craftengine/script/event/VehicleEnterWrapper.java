package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when an entity (a player, or a mob wandering in) gets into a vehicle (a minecart, boat,
 * or entity fitted with a saddle/chair). A script could use {@link #entered()} to check for a
 * permission before letting a player ride a claimed minecart, and {@link ScriptEvent#cancel} to
 * eject them right back out.
 */
public final class VehicleEnterWrapper extends ScriptEvent {
    private final VehicleEnterEvent raw;

    public VehicleEnterWrapper(VehicleEnterEvent raw) {
        super("VehicleEnterEvent");
        this.raw = raw;
    }

    public VehicleEnterEvent raw() { return raw; }

    public ScriptValue vehicle() {
        return EntityType.wrap(((CraftEntity) raw.getVehicle()).getHandle());
    }

    public ScriptValue entered() {
        return EntityType.wrap(((CraftEntity) raw.getEntered()).getHandle());
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
