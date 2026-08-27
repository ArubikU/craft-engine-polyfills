package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires right before a vehicle is destroyed (its health finally ran out, or a passenger/attacker
 * dealt a lethal hit). A script could use {@link #attacker()} to award the destroyer a "boat
 * wrecker" statistic before {@code cancel()}ing to keep the vehicle alive as a reward-only sink.
 */
public final class VehicleDestroyWrapper extends ScriptEvent {
    private final VehicleDestroyEvent raw;

    public VehicleDestroyWrapper(VehicleDestroyEvent raw) {
        super("VehicleDestroyEvent");
        this.raw = raw;
    }

    public VehicleDestroyEvent raw() { return raw; }

    public ScriptValue vehicle() {
        return EntityType.wrap(((CraftEntity) raw.getVehicle()).getHandle());
    }

    /** The entity that destroyed the vehicle, or {@code NULL} if nothing did (e.g. it just fell
     *  into the void or expired). */
    public ScriptValue attacker() {
        var attacker = raw.getAttacker();
        if (attacker == null) return ScriptValue.NULL;
        return EntityType.wrap(((CraftEntity) attacker).getHandle());
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
