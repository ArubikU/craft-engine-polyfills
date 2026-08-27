package dev.arubik.craftengine.script.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerKickEvent;

/**
 * Fires right before a player is disconnected for a kick (including a ban-triggered kick). A
 * script could rewrite {@link #setReason} to show a friendlier, branded message instead of the
 * plugin/vanilla-authored default, or veto the kick outright for a protected player via
 * {@link #setCancelled}.
 */
public final class PlayerKickWrapper extends ScriptEvent {
    private final PlayerKickEvent raw;

    public PlayerKickWrapper(PlayerKickEvent raw) {
        super("PlayerKickEvent");
        this.raw = raw;
    }

    public PlayerKickEvent raw() { return raw; }

    public String reason() { return ScriptEventUtil.componentToString(raw.reason()); }

    public void setReason(String text) {
        raw.reason(ScriptEventUtil.parseComponent(text == null ? "" : text));
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
