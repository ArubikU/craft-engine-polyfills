package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerItemDamageEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires every time a durability-tracking item takes a point of damage (a tool used, armor hit,
 * ...) — well before it actually breaks (see {@link PlayerItemBreakWrapper} for that). A script
 * could use {@link #setDamage} to implement an "unbreakable until enchanted" custom tool by zeroing
 * out the damage whenever a specific enchant tag is missing.
 */
public final class PlayerItemDamageWrapper extends ScriptEvent {
    private final PlayerItemDamageEvent raw;

    public PlayerItemDamageWrapper(PlayerItemDamageEvent raw) {
        super("PlayerItemDamageEvent");
        this.raw = raw;
    }

    public PlayerItemDamageEvent raw() { return raw; }

    public ScriptValue item() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getItem()));
    }

    public int damage() { return raw.getDamage(); }

    public void setDamage(int damage) { raw.setDamage(damage); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
