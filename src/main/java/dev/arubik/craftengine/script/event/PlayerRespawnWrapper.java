package dev.arubik.craftengine.script.event;

import org.bukkit.event.player.PlayerRespawnEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires when a dead player is about to respawn — after the death screen, before they're actually
 * placed back in the world. A script can use this to implement a custom spawn point system (a
 * player-set "home", a per-world lobby, ...) by overwriting {@link #setRespawnLocation}.
 */
public final class PlayerRespawnWrapper extends ScriptEvent {
    private final PlayerRespawnEvent raw;

    public PlayerRespawnWrapper(PlayerRespawnEvent raw) {
        super("PlayerRespawnEvent");
        this.raw = raw;
    }

    public PlayerRespawnEvent raw() { return raw; }

    public ScriptValue respawnLocation() { return ScriptEventUtil.wrapLocation(raw.getRespawnLocation()); }

    public void setRespawnLocation(ScriptValue location) {
        org.bukkit.Location loc = ScriptEventUtil.toBukkitLocation(location, raw.getRespawnLocation());
        if (loc != null) raw.setRespawnLocation(loc);
    }

    // Not Cancellable — a respawn always happens; only where it happens can be changed.
}
