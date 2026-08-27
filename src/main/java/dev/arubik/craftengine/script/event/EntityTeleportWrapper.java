package dev.arubik.craftengine.script.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityTeleportEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires when a non-player entity teleports (an enderman blinking away, a shulker bullet warping a
 * target, a leash pulling a mob through a portal, ...) — the entity counterpart of {@link
 * PlayerTeleportWrapper}. A script could use {@link #setTo} to keep a leashed pet from following its
 * owner through a portal by redirecting the destination back to {@link #from()}.
 */
public final class EntityTeleportWrapper extends ScriptEvent {
    private final EntityTeleportEvent raw;

    public EntityTeleportWrapper(EntityTeleportEvent raw) {
        super("EntityTeleportEvent");
        this.raw = raw;
    }

    public EntityTeleportEvent raw() { return raw; }

    public ScriptValue from() { return ScriptEventUtil.wrapLocation(raw.getFrom()); }

    public ScriptValue to() { return ScriptEventUtil.wrapLocation(raw.getTo()); }

    public void setTo(ScriptValue location) {
        org.bukkit.Location loc = ScriptEventUtil.toBukkitLocation(location, raw.getTo());
        if (loc != null) raw.setTo(loc);
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
