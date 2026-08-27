package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntitySpawnEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires whenever any entity is added to the world for any reason (natural spawn, plugin spawn,
 * vehicle, ...) — a broader net than {@link CreatureSpawnWrapper}, which only covers vanilla mob
 * spawns. A script could use this to enforce a hard entity cap per chunk: count nearby entities via
 * {@link #location()} and {@link #setCancelled} once a threshold is hit.
 */
public final class EntitySpawnWrapper extends ScriptEvent {
    private final EntitySpawnEvent raw;

    public EntitySpawnWrapper(EntitySpawnEvent raw) {
        super("EntitySpawnEvent");
        this.raw = raw;
    }

    public EntitySpawnEvent raw() { return raw; }

    public ScriptValue entity() {
        return EntityType.wrap(((CraftEntity) raw.getEntity()).getHandle());
    }

    public ScriptValue location() { return ScriptEventUtil.wrapLocation(raw.getLocation()); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
