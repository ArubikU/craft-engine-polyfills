package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityCombustEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when an entity catches fire (lava, fire block, sunlight on an undead mob, dragon breath,
 * ...). A script could use this to implement a "fire resistance" trinket: check the wearer via
 * {@link #entity()} and {@link #setDuration} to {@code 0} (or {@link #setCancelled} outright) when
 * they're holding the right item.
 */
public final class EntityCombustWrapper extends ScriptEvent {
    private final EntityCombustEvent raw;

    public EntityCombustWrapper(EntityCombustEvent raw) {
        super("EntityCombustEvent");
        this.raw = raw;
    }

    public EntityCombustEvent raw() { return raw; }

    public ScriptValue entity() {
        return EntityType.wrap(((CraftEntity) raw.getEntity()).getHandle());
    }

    /** How many seconds the entity will burn for. */
    public double duration() { return raw.getDuration(); }
    public void setDuration(double seconds) { raw.setDuration((float) seconds); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
