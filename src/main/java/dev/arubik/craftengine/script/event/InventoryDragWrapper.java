package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryDragEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires when a player drags an item across multiple slots while holding the mouse button down
 * (spreading one stack evenly, or one-at-a-time). A script could use {@link #oldCursor()} vs
 * {@link #cursor()} to detect and veto a drag that would spread a restricted item across a
 * player's own crafting grid.
 */
public final class InventoryDragWrapper extends ScriptEvent {
    private final InventoryDragEvent raw;

    public InventoryDragWrapper(InventoryDragEvent raw) {
        super("InventoryDragEvent");
        this.raw = raw;
    }

    public InventoryDragEvent raw() { return raw; }

    /** What's left on the cursor once the drag finishes (0/{@code NULL} if it was spread evenly). */
    public ScriptValue cursor() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getCursor()));
    }

    public void setCursor(ScriptValue item) {
        if (item instanceof ScriptValue.Item i && i.stack() != null && !i.stack().isEmpty()) {
            raw.setCursor(CraftItemStack.asBukkitCopy(i.stack()));
        } else {
            raw.setCursor(null);
        }
    }

    /** What was on the cursor before the drag started (the full stack being spread). */
    public ScriptValue oldCursor() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getOldCursor()));
    }

    public ScriptValue whoClicked() {
        return ScriptEventUtil.wrapHumanEntity(raw.getWhoClicked());
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
