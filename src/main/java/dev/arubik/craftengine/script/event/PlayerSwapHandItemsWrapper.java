package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires when a player presses F to swap their main-hand and off-hand items. A script could use
 * {@link #setMainHandItem}/{@link #setOffHandItem} to veto swapping a two-handed custom weapon into
 * the off-hand slot, or cancel the swap outright while that weapon is on cooldown.
 */
public final class PlayerSwapHandItemsWrapper extends ScriptEvent {
    private final PlayerSwapHandItemsEvent raw;

    public PlayerSwapHandItemsWrapper(PlayerSwapHandItemsEvent raw) {
        super("PlayerSwapHandItemsEvent");
        this.raw = raw;
    }

    public PlayerSwapHandItemsEvent raw() { return raw; }

    public ScriptValue mainHandItem() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getMainHandItem()));
    }

    public void setMainHandItem(ScriptValue item) {
        if (item instanceof ScriptValue.Item i && i.stack() != null && !i.stack().isEmpty()) {
            raw.setMainHandItem(CraftItemStack.asBukkitCopy(i.stack()));
        } else {
            raw.setMainHandItem(null);
        }
    }

    public ScriptValue offHandItem() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getOffHandItem()));
    }

    public void setOffHandItem(ScriptValue item) {
        if (item instanceof ScriptValue.Item i && i.stack() != null && !i.stack().isEmpty()) {
            raw.setOffHandItem(CraftItemStack.asBukkitCopy(i.stack()));
        } else {
            raw.setOffHandItem(null);
        }
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
