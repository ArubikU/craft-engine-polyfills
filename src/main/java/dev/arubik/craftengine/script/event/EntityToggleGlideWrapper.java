package dev.arubik.craftengine.script.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityToggleGlideEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires when an entity starts or stops elytra-gliding. A script could use {@link #isGliding()} to
 * grant a speed boost the instant a player starts gliding, and remove it again the moment this
 * fires with gliding false.
 */
public final class EntityToggleGlideWrapper extends ScriptEvent {
    private final EntityToggleGlideEvent raw;

    public EntityToggleGlideWrapper(EntityToggleGlideEvent raw) {
        super("EntityToggleGlideEvent");
        this.raw = raw;
    }

    public EntityToggleGlideEvent raw() { return raw; }

    /** {@code true} if the entity is starting to glide, {@code false} if it just stopped. */
    public boolean isGliding() { return raw.isGliding(); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
