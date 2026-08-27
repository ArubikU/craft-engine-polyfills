package dev.arubik.craftengine.script.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Fires before a player-typed command is dispatched to the server's command map — the standard
 * hook for a custom command alias system or a "block this command outside this region" filter. A
 * script could rewrite {@link #setMessage} to redirect {@code /home} into {@code /warp home}
 * without registering a real command at all.
 */
public final class PlayerCommandPreprocessWrapper extends ScriptEvent {
    private final PlayerCommandPreprocessEvent raw;

    public PlayerCommandPreprocessWrapper(PlayerCommandPreprocessEvent raw) {
        super("PlayerCommandPreprocessEvent");
        this.raw = raw;
    }

    public PlayerCommandPreprocessEvent raw() { return raw; }

    /** The full command line as typed, leading {@code /} included. */
    public String message() { return raw.getMessage(); }

    public void setMessage(String message) { raw.setMessage(message); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
