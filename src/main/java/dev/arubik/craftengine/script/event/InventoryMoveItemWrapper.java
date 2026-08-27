package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires whenever a hopper (or dropper acting as one) moves a single item stack from one inventory
 * into another. A script could use {@link #source()}/{@link #destination()} to block hoppers from
 * ever pulling items out of a specific custom "vault" chest type.
 *
 * <p>{@code getSource()}/{@code getDestination()}/{@code getInitiator()} return raw Bukkit
 * {@code Inventory} objects, which this codebase has no dedicated script wrapper type for — rather
 * than invent one just for this event, each is exposed as a simple lowercase inventory-type
 * description (e.g. {@code "hopper"}, {@code "chest"}, {@code "furnace"}) via {@link #source()} etc.
 */
public final class InventoryMoveItemWrapper extends ScriptEvent {
    private final InventoryMoveItemEvent raw;

    public InventoryMoveItemWrapper(InventoryMoveItemEvent raw) {
        super("InventoryMoveItemEvent");
        this.raw = raw;
    }

    public InventoryMoveItemEvent raw() { return raw; }

    public ScriptValue item() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getItem()));
    }

    public void setItem(ScriptValue item) {
        if (item instanceof ScriptValue.Item i && i.stack() != null && !i.stack().isEmpty()) {
            raw.setItem(CraftItemStack.asBukkitCopy(i.stack()));
        }
    }

    /** Where the item is being pulled from — see the class javadoc for why this is a plain
     *  description rather than a full inventory object. */
    public String source() { return describeInventory(raw.getSource()); }

    /** Where the item is headed. */
    public String destination() { return describeInventory(raw.getDestination()); }

    /** The inventory that actually initiated the move (usually the same as {@link #source()},
     *  but not always — e.g. a dropper pushing INTO a hopper is initiated by the dropper). */
    public String initiator() { return describeInventory(raw.getInitiator()); }

    private static String describeInventory(Inventory inv) {
        if (inv == null) return "unknown";
        return inv.getType().name().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
