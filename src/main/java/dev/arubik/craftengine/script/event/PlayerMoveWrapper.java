package dev.arubik.craftengine.script.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerMoveEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires every time a player's position changes by a meaningful amount — the highest-traffic event
 * in this list, so a script bound to it should stay cheap. A practical use: a region-guard script
 * that checks {@link #to()} against a claimed area and either {@link #setCancelled} the move or
 * redirect it elsewhere via {@link #setTo} (e.g. bouncing the player back to {@link #from()}).
 */
public final class PlayerMoveWrapper extends ScriptEvent {
    private final PlayerMoveEvent raw;

    public PlayerMoveWrapper(PlayerMoveEvent raw) {
        super("PlayerMoveEvent");
        this.raw = raw;
    }

    public PlayerMoveEvent raw() { return raw; }

    /** Where the player moved from — immutable, this already happened. */
    public ScriptValue from() { return ScriptEventUtil.wrapLocation(raw.getFrom()); }

    /** Where the player is about to end up — a script can redirect the move by setting this. */
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
