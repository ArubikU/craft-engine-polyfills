package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.vehicle.VehicleDamageEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when a vehicle takes damage (a player punching a boat, a mob attacking a minecart, ...),
 * before it either breaks or absorbs the hit. A script could use {@link #setDamage} to make
 * vehicles in a protected zone effectively indestructible without fully cancelling the event (which
 * would also suppress knockback/particle feedback the attacker expects).
 */
public final class VehicleDamageWrapper extends ScriptEvent {
    private final VehicleDamageEvent raw;

    public VehicleDamageWrapper(VehicleDamageEvent raw) {
        super("VehicleDamageEvent");
        this.raw = raw;
    }

    public VehicleDamageEvent raw() { return raw; }

    public ScriptValue vehicle() {
        return EntityType.wrap(((CraftEntity) raw.getVehicle()).getHandle());
    }

    public ScriptValue attacker() {
        return EntityType.wrap(((CraftEntity) raw.getAttacker()).getHandle());
    }

    public double damage() { return raw.getDamage(); }
    public void setDamage(double damage) { raw.setDamage(damage); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
