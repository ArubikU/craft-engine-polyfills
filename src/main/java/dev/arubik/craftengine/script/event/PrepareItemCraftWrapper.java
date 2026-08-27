package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires every time a crafting grid's contents change enough to re-evaluate what recipe (if any)
 * matches — well before a player actually clicks the result slot. A script could use
 * {@link #setResult} to preview a custom result item the moment the matching ingredients are laid
 * out, without waiting for {@link CraftItemWrapper} to fire.
 *
 * <p>Unlike most inventory events, {@code PrepareItemCraftEvent} does NOT implement
 * {@code Cancellable} in this API version (there's no "recipe" yet to cancel — clearing the result
 * via {@link #setResult} with a null/empty item is the equivalent of vetoing it) — so
 * {@link ScriptEvent#cancel()} here only flips the inert base-class flag.
 */
public final class PrepareItemCraftWrapper extends ScriptEvent {
    private final PrepareItemCraftEvent raw;

    public PrepareItemCraftWrapper(PrepareItemCraftEvent raw) {
        super("PrepareItemCraftEvent");
        this.raw = raw;
    }

    public PrepareItemCraftEvent raw() { return raw; }

    public ScriptValue result() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getInventory().getResult()));
    }

    public void setResult(ScriptValue item) {
        if (item instanceof ScriptValue.Item i && i.stack() != null && !i.stack().isEmpty()) {
            raw.getInventory().setResult(CraftItemStack.asBukkitCopy(i.stack()));
        } else {
            raw.getInventory().setResult(null);
        }
    }
}
