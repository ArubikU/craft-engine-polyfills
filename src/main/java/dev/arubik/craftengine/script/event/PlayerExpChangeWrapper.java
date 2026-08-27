package dev.arubik.craftengine.script.event;

import org.bukkit.event.player.PlayerExpChangeEvent;

/**
 * Fires when a player is about to gain XP orbs (mining, killing mobs, smelting, ...). A script
 * could use {@link #setAmount} to implement an XP multiplier perk without touching every source of
 * XP individually.
 */
public final class PlayerExpChangeWrapper extends ScriptEvent {
    private final PlayerExpChangeEvent raw;

    public PlayerExpChangeWrapper(PlayerExpChangeEvent raw) {
        super("PlayerExpChangeEvent");
        this.raw = raw;
    }

    public PlayerExpChangeEvent raw() { return raw; }

    public int amount() { return raw.getAmount(); }
    public void setAmount(int amount) { raw.setAmount(amount); }

    // Not Cancellable — set amount to 0 to suppress the XP gain instead.
}
