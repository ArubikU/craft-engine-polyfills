package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.vehicle.VehicleCreateEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when a vehicle entity is created in the world (a boat/minecart placed, or spawned by a
 * dispenser, plugin, ...). A script could use {@link ScriptEvent#cancel} to enforce a "no vehicles"
 * rule inside a minigame arena.
 */
public final class VehicleCreateWrapper extends ScriptEvent {
    private final VehicleCreateEvent raw;

    public VehicleCreateWrapper(VehicleCreateEvent raw) {
        super("VehicleCreateEvent");
        this.raw = raw;
    }

    public VehicleCreateEvent raw() { return raw; }

    public ScriptValue vehicle() {
        return EntityType.wrap(((CraftEntity) raw.getVehicle()).getHandle());
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
