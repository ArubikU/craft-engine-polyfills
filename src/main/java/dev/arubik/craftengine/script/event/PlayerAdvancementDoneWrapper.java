package dev.arubik.craftengine.script.event;

import org.bukkit.event.player.PlayerAdvancementDoneEvent;

/**
 * Fires when a player completes an advancement (vanilla or a datapack-added one). A script could
 * use {@link #advancementKey()} to grant a custom reward (an item, a currency amount) the moment a
 * specific advancement completes, without needing a separate advancement-reward datapack. Not
 * {@code Cancellable} — the advancement has already been granted by the time this fires.
 */
public final class PlayerAdvancementDoneWrapper extends ScriptEvent {
    private final PlayerAdvancementDoneEvent raw;

    public PlayerAdvancementDoneWrapper(PlayerAdvancementDoneEvent raw) {
        super("PlayerAdvancementDoneEvent");
        this.raw = raw;
    }

    public PlayerAdvancementDoneEvent raw() { return raw; }

    public String advancementKey() { return raw.getAdvancement().getKey().toString(); }
}
