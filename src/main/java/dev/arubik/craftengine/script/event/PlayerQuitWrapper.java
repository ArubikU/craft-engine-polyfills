package dev.arubik.craftengine.script.event;

import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Fires when a player disconnects (logout, kick, timeout, ...) — the mirror of
 * {@link PlayerJoinWrapper}. A script can use this to replace the default "Foo left the game"
 * broadcast, or silence it, the same way a join hook would.
 */
public final class PlayerQuitWrapper extends ScriptEvent {
    private final PlayerQuitEvent raw;

    public PlayerQuitWrapper(PlayerQuitEvent raw) {
        super("PlayerQuitEvent");
        this.raw = raw;
    }

    public PlayerQuitEvent raw() { return raw; }

    public String quitMessage() { return ScriptEventUtil.componentToString(raw.quitMessage()); }

    public void setQuitMessage(String text) {
        raw.quitMessage(text == null || text.isEmpty() ? null : ScriptEventUtil.parseComponent(text));
    }

    // Not Cancellable — same as PlayerJoinEvent.
}
