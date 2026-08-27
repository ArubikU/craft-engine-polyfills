package dev.arubik.craftengine.script.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Fires the instant a player starts or stops sneaking — the usual trigger for a custom "sneak to
 * open a hidden panel" or "sneak-right-click to access a secondary menu" interaction that vanilla
 * has no hook for on its own.
 */
public final class PlayerToggleSneakWrapper extends ScriptEvent {
    private final PlayerToggleSneakEvent raw;

    public PlayerToggleSneakWrapper(PlayerToggleSneakEvent raw) {
        super("PlayerToggleSneakEvent");
        this.raw = raw;
    }

    public PlayerToggleSneakEvent raw() { return raw; }

    /** {@code true} if the player just started sneaking, {@code false} if they just stopped. */
    public boolean isSneaking() { return raw.isSneaking(); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
