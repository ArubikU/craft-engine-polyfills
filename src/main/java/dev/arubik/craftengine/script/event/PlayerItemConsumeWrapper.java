package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires right before a player finishes eating/drinking an item (food, potion, milk bucket, ...). A
 * script could use {@link #setItem} to substitute what's actually consumed — e.g. a custom "golden
 * apple" that consumes as a plain apple for hunger purposes but still applies its own effect
 * separately — or veto the consume outright while a debuff is active.
 */
public final class PlayerItemConsumeWrapper extends ScriptEvent {
    private final PlayerItemConsumeEvent raw;

    public PlayerItemConsumeWrapper(PlayerItemConsumeEvent raw) {
        super("PlayerItemConsumeEvent");
        this.raw = raw;
    }

    public PlayerItemConsumeEvent raw() { return raw; }

    public ScriptValue item() { return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getItem())); }

    public void setItem(ScriptValue item) {
        if (item instanceof ScriptValue.Item i && i.stack() != null) {
            raw.setItem(CraftItemStack.asBukkitCopy(i.stack()));
        }
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
