package dev.arubik.craftengine.script.event;

import org.bukkit.event.player.PlayerLevelChangeEvent;

/**
 * Fires whenever a player's XP level changes (up OR down). A script could use the {@link
 * #oldLevel()}/{@link #newLevel()} pair to grant a one-time reward every time a player crosses a
 * milestone level (10, 20, 30, ...).
 */
public final class PlayerLevelChangeWrapper extends ScriptEvent {
    private final PlayerLevelChangeEvent raw;

    public PlayerLevelChangeWrapper(PlayerLevelChangeEvent raw) {
        super("PlayerLevelChangeEvent");
        this.raw = raw;
    }

    public PlayerLevelChangeEvent raw() { return raw; }

    public int oldLevel() { return raw.getOldLevel(); }
    public int newLevel() { return raw.getNewLevel(); }

    // Not Cancellable.
}
