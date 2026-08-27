package dev.arubik.craftengine.script.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryOpenEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires whenever any inventory GUI opens for a player (chests, crafting tables, this plugin's own
 * machine menus included). A script could use {@link #player()} plus {@link ScriptEvent#cancel()}
 * to lock a player out of opening any container while they're in combat.
 */
public final class InventoryOpenWrapper extends ScriptEvent {
    private final InventoryOpenEvent raw;

    public InventoryOpenWrapper(InventoryOpenEvent raw) {
        super("InventoryOpenEvent");
        this.raw = raw;
    }

    public InventoryOpenEvent raw() { return raw; }

    /** {@code getPlayer()} is typed as {@code HumanEntity}, see {@link ScriptEventUtil#wrapHumanEntity}. */
    public ScriptValue player() {
        return ScriptEventUtil.wrapHumanEntity(raw.getPlayer());
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
