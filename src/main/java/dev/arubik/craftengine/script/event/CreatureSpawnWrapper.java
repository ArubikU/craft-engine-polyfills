package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.CreatureSpawnEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires for every vanilla mob spawn, tagged with WHY it spawned — natural, spawner, egg, breeding,
 * and so on. A script could use {@link #reason()} to implement a "no natural mob spawns in this
 * region" rule while still allowing spawner-placed or player-summoned mobs through.
 */
public final class CreatureSpawnWrapper extends ScriptEvent {
    private final CreatureSpawnEvent raw;

    public CreatureSpawnWrapper(CreatureSpawnEvent raw) {
        super("CreatureSpawnEvent");
        this.raw = raw;
    }

    public CreatureSpawnEvent raw() { return raw; }

    public ScriptValue entity() {
        return EntityType.wrap(((CraftEntity) raw.getEntity()).getHandle());
    }

    /** e.g. {@code "natural"}, {@code "spawner"}, {@code "egg"}, {@code "breeding"}. */
    public String reason() { return raw.getSpawnReason().name().toLowerCase(Locale.ROOT); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
