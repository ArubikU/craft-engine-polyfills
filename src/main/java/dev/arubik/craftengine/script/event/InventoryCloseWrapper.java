package dev.arubik.craftengine.script.event;

import org.bukkit.event.inventory.InventoryCloseEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires whenever a player's inventory GUI closes (they hit Escape, the plugin closed it
 * programmatically, they died, ...). A script could use {@link #player()} to persist/save whatever
 * state a custom menu was tracking for that player once they walk away from it.
 *
 * <p>Unlike most inventory events, {@code InventoryCloseEvent} is NOT {@code Cancellable} in this
 * API version (a close, once decided, cannot be vetoed) — {@link ScriptEvent#cancel()} here only
 * flips the inert base-class flag, same as any other non-cancellable wrapper (e.g.
 * {@link PlayerItemBreakWrapper}).
 */
public final class InventoryCloseWrapper extends ScriptEvent {
    private final InventoryCloseEvent raw;

    public InventoryCloseWrapper(InventoryCloseEvent raw) {
        super("InventoryCloseEvent");
        this.raw = raw;
    }

    public InventoryCloseEvent raw() { return raw; }

    /** {@code getPlayer()} is typed as {@code HumanEntity}, see {@link ScriptEventUtil#wrapHumanEntity}. */
    public ScriptValue player() {
        return ScriptEventUtil.wrapHumanEntity(raw.getPlayer());
    }
}
