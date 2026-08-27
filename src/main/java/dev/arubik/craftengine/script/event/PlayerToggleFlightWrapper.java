package dev.arubik.craftengine.script.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/**
 * Fires the instant a player starts or stops flying (creative/spectator flight, or a plugin-granted
 * fly ability toggled via the double-jump). A script could veto {@link #isFlying()} turning on
 * (via {@link #setCancelled}) unless some jetpack item is equipped, without needing to poll
 * {@code Player.is_flying} every tick.
 */
public final class PlayerToggleFlightWrapper extends ScriptEvent {
    private final PlayerToggleFlightEvent raw;

    public PlayerToggleFlightWrapper(PlayerToggleFlightEvent raw) {
        super("PlayerToggleFlightEvent");
        this.raw = raw;
    }

    public PlayerToggleFlightEvent raw() { return raw; }

    public boolean isFlying() { return raw.isFlying(); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
