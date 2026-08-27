package dev.arubik.craftengine.menu;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.inventory.Inventory;

/**
 * Tracks currently-open {@link ScriptMenu} instances keyed by Bukkit {@link Inventory} identity —
 * the same inventory object a player's {@code InventoryClickEvent}/{@code InventoryCloseEvent}
 * reports, so {@code ScriptMenuListener} can find the menu backing whatever the player just
 * clicked/closed without any per-player bookkeeping (a player can only ever be looking at one
 * inventory at a time, so tracking by the inventory itself is simplest and needs no cleanup beyond
 * the close event this system already listens for).
 */
public final class ScriptMenuRegistry {

    private static final Map<Inventory, ScriptMenu> OPEN = new ConcurrentHashMap<>();

    private ScriptMenuRegistry() {}

    public static void track(Inventory inv, ScriptMenu menu) {
        if (inv == null || menu == null) return;
        OPEN.put(inv, menu);
    }

    public static ScriptMenu get(Inventory inv) {
        if (inv == null) return null;
        return OPEN.get(inv);
    }

    public static void untrack(Inventory inv) {
        if (inv == null) return;
        OPEN.remove(inv);
    }
}
