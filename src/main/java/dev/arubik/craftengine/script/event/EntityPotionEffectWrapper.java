package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityPotionEffectEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires whenever a potion effect is added, removed, changed, or cleared on an entity (a splash
 * potion, a beacon, milk, a status effect running out, ...). A script could use {@link #cause()}
 * and {@link #action()} together to implement a "beacon effects don't apply to hostile mobs" rule
 * by cancelling whenever both match and {@link #entity()} isn't a player.
 */
public final class EntityPotionEffectWrapper extends ScriptEvent {
    private final EntityPotionEffectEvent raw;

    public EntityPotionEffectWrapper(EntityPotionEffectEvent raw) {
        super("EntityPotionEffectEvent");
        this.raw = raw;
    }

    public EntityPotionEffectEvent raw() { return raw; }

    public ScriptValue entity() {
        return EntityType.wrap(((CraftEntity) raw.getEntity()).getHandle());
    }

    /** e.g. {@code "potion_drink"}, {@code "milk"}, {@code "beacon"}, {@code "plugin"}. */
    public String cause() { return raw.getCause().name().toLowerCase(Locale.ROOT); }

    /** e.g. {@code "added"}, {@code "removed"}, {@code "changed"}, {@code "cleared"}. */
    public String action() { return raw.getAction().name().toLowerCase(Locale.ROOT); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
