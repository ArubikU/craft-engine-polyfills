package dev.arubik.craftengine.script.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerToggleSprintEvent;

/**
 * Fires the instant a player starts or stops sprinting. A script could use this to drain a
 * "stamina" resource bar while {@link #isSprinting()} is {@code true}, or veto sprinting entirely
 * (via {@link #setCancelled}) while some debuff is active.
 */
public final class PlayerToggleSprintWrapper extends ScriptEvent {
    private final PlayerToggleSprintEvent raw;

    public PlayerToggleSprintWrapper(PlayerToggleSprintEvent raw) {
        super("PlayerToggleSprintEvent");
        this.raw = raw;
    }

    public PlayerToggleSprintEvent raw() { return raw; }

    public boolean isSprinting() { return raw.isSprinting(); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
