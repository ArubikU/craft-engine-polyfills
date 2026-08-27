package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityBreedEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when two animals successfully breed and produce offspring. A script could use
 * {@link #breeder()} to reward the player who fed the animals (a currency drop, XP, an
 * achievement) the moment {@link #child()} is born.
 */
public final class EntityBreedWrapper extends ScriptEvent {
    private final EntityBreedEvent raw;

    public EntityBreedWrapper(EntityBreedEvent raw) {
        super("EntityBreedEvent");
        this.raw = raw;
    }

    public EntityBreedEvent raw() { return raw; }

    public ScriptValue mother() {
        return EntityType.wrap(((CraftEntity) raw.getMother()).getHandle());
    }

    public ScriptValue father() {
        return EntityType.wrap(((CraftEntity) raw.getFather()).getHandle());
    }

    /** The player/entity that fed the parents to trigger breeding, or {@code NULL} if the API
     *  reports none (e.g. breeding triggered some other way). */
    public ScriptValue breeder() {
        Entity breeder = raw.getBreeder();
        return breeder == null ? ScriptValue.NULL : EntityType.wrap(((CraftEntity) breeder).getHandle());
    }

    public ScriptValue child() {
        return EntityType.wrap(((CraftEntity) raw.getEntity()).getHandle());
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
