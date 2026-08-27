package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.vehicle.VehicleExitEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when a living entity dismounts a vehicle (jumping out of a boat, being thrown from a
 * minecart, ...) — {@link #exited()} is only ever a {@code LivingEntity} (not the broader
 * {@code Entity} {@link VehicleEnterWrapper#entered()} allows), since only living entities can ride
 * in the first place. A script could use this to teleport the rider back onto dry land if the
 * vehicle exited over open water.
 */
public final class VehicleExitWrapper extends ScriptEvent {
    private final VehicleExitEvent raw;

    public VehicleExitWrapper(VehicleExitEvent raw) {
        super("VehicleExitEvent");
        this.raw = raw;
    }

    public VehicleExitEvent raw() { return raw; }

    public ScriptValue vehicle() {
        return EntityType.wrap(((CraftEntity) raw.getVehicle()).getHandle());
    }

    public ScriptValue exited() {
        return EntityType.wrap(((CraftEntity) raw.getExited()).getHandle());
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
