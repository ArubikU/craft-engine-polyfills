package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryClickEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires on every single inventory-slot click — the single highest-traffic inventory event in the
 * whole API, since shift-clicks, hotbar-number swaps, double-clicks, and drops-via-click all funnel
 * through here too (distinguished by {@link #clickType()}/{@link #action()}) rather than each having
 * their own event. A script could use this to build a custom shop GUI: cancel the click whenever
 * {@link #rawSlot()} lands on a "buy" button slot, then charge the player and hand them
 * {@link #currentItem()}'s slot manually instead of letting the real click go through.
 *
 * <p>Non-final so {@link CraftItemWrapper} (fired instead of this one for a crafting-result click,
 * see its own javadoc) can extend it and inherit everything below for free, the same "IS-A" pattern
 * {@link BlockPlaceWrapper}/{@link BlockMultiPlaceWrapper} already use.
 */
public class InventoryClickWrapper extends ScriptEvent {
    private final InventoryClickEvent raw;

    public InventoryClickWrapper(InventoryClickEvent raw) {
        this("InventoryClickEvent", raw);
    }

    protected InventoryClickWrapper(String type, InventoryClickEvent raw) {
        super(type);
        this.raw = raw;
    }

    public InventoryClickEvent raw() { return raw; }

    public int slot() { return raw.getSlot(); }

    public int rawSlot() { return raw.getRawSlot(); }

    /** The item sitting in the clicked slot before this click resolves ({@code NULL} if empty). */
    public ScriptValue currentItem() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getCurrentItem()));
    }

    public void setCurrentItem(ScriptValue item) {
        if (item instanceof ScriptValue.Item i && i.stack() != null && !i.stack().isEmpty()) {
            raw.setCurrentItem(CraftItemStack.asBukkitCopy(i.stack()));
        } else {
            raw.setCurrentItem(null);
        }
    }

    /** What's on the clicker's cursor ({@code NULL} if nothing). */
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

    /** Lowercase {@code ClickType} name — {@code "left"}, {@code "shift_right"}, {@code "double_click"}, ... */
    public String clickType() { return raw.getClick().name().toLowerCase(Locale.ROOT); }

    /** Lowercase {@code InventoryAction} name — {@code "pickup_all"}, {@code "move_to_other_inventory"}, ... */
    public String action() { return raw.getAction().name().toLowerCase(Locale.ROOT); }

    /** {@code getWhoClicked()} is typed as {@code HumanEntity} in the raw Bukkit API (almost always a
     *  real player, but the API leaves room for it not to be), so this wraps as the richer
     *  {@code Player} PolyType when it is one and falls back to plain {@code Entity} otherwise — see
     *  {@link ScriptEventUtil#wrapHumanEntity}. */
    public ScriptValue whoClicked() {
        HumanEntity human = raw.getWhoClicked();
        return ScriptEventUtil.wrapHumanEntity(human);
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
