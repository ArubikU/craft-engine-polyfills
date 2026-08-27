package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires whenever any entity takes damage from any source (fall, fire, drowning, another entity, ...
 * see {@link EntityDamageByEntityWrapper} for the entity-on-entity specialization). A script could
 * use this to implement a flat damage-reduction buff: read {@link #damage()}, scale it down, and
 * {@link #setDamage} the result.
 */
public class EntityDamageWrapper extends ScriptEvent {
    private final EntityDamageEvent raw;

    public EntityDamageWrapper(EntityDamageEvent raw) {
        this("EntityDamageEvent", raw);
    }

    protected EntityDamageWrapper(String type, EntityDamageEvent raw) {
        super(type);
        this.raw = raw;
    }

    public EntityDamageEvent raw() { return raw; }

    public ScriptValue entity() {
        return EntityType.wrap(((CraftEntity) raw.getEntity()).getHandle());
    }

    public double damage() { return raw.getDamage(); }
    public void setDamage(double damage) { raw.setDamage(damage); }

    /** e.g. {@code "fall"}, {@code "fire"}, {@code "entity_attack"}, {@code "drowning"}. */
    public String cause() { return raw.getCause().name().toLowerCase(Locale.ROOT); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
