package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

/**
 * Fires right before a player's game mode actually changes. A script could veto (via
 * {@link #setCancelled}) switching to creative/spectator while the player is inside a PvP arena,
 * something vanilla permissions alone can't express.
 */
public final class PlayerGameModeChangeWrapper extends ScriptEvent {
    private final PlayerGameModeChangeEvent raw;

    public PlayerGameModeChangeWrapper(PlayerGameModeChangeEvent raw) {
        super("PlayerGameModeChangeEvent");
        this.raw = raw;
    }

    public PlayerGameModeChangeEvent raw() { return raw; }

    /** e.g. {@code "survival"}, {@code "creative"}, {@code "adventure"}, {@code "spectator"}. */
    public String newGameMode() { return raw.getNewGameMode().name().toLowerCase(Locale.ROOT); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
