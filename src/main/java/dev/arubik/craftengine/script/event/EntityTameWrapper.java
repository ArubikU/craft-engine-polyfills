package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityTameEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when a wolf/cat/horse/etc gets tamed. In practice {@link org.bukkit.entity.AnimalTamer} is
 * always a real {@link org.bukkit.entity.Player} in the current API (no NPC plugin ships a
 * non-entity tamer that would reach this event), so {@link #owner()} just checks it's an
 * {@link Entity} and goes through {@link EntityType#wrap} the same as everywhere else — a script
 * could use it to grant a "first tame" achievement/reward the moment {@link #owner()} resolves.
 */
public final class EntityTameWrapper extends ScriptEvent {
    private final EntityTameEvent raw;

    public EntityTameWrapper(EntityTameEvent raw) {
        super("EntityTameEvent");
        this.raw = raw;
    }

    public EntityTameEvent raw() { return raw; }

    /** The new owner, or {@code NULL} if the tamer isn't a real entity (shouldn't happen in
     *  practice, but {@code AnimalTamer} doesn't guarantee it). */
    public ScriptValue owner() {
        AnimalTamer tamer = raw.getOwner();
        if (tamer instanceof Entity e) return EntityType.wrap(((CraftEntity) e).getHandle());
        return ScriptValue.NULL;
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
