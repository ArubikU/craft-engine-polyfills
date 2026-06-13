package dev.arubik.craftengine.crafting;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * Drives {@link CraftingTableMenu} interaction: keeps the result preview in
 * sync as inputs change, handles taking the result (consume-one-each, instant
 * output), and returns inputs on close.
 *
 * <p>Mirrors the scheduling approach of {@code MachineMenuListener}: changes to
 * input slots are applied by Bukkit first, then we recompute on the next tick.
 */
public final class CraftingTableListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof CraftingTableMenu menu)) {
            return;
        }
        int raw = event.getRawSlot();
        boolean topInventory = raw >= 0 && raw < event.getInventory().getSize();

        if (topInventory) {
            if (menu.isFillerSlot(raw)) {
                event.setCancelled(true);
                return;
            }
            if (menu.isResultSlot(raw)) {
                // Result slot: no placing into it; clicking it crafts.
                event.setCancelled(true);
                if (event.getWhoClicked() instanceof Player player) {
                    menu.takeResult(player);
                }
                return;
            }
            // Input slot: allow the change, then recompute next tick.
            scheduleRecompute(menu);
            return;
        }

        // Click in the player inventory: shift-click into the table may alter inputs.
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            scheduleRecompute(menu);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof CraftingTableMenu menu)) {
            return;
        }
        for (int raw : event.getRawSlots()) {
            if (raw < event.getInventory().getSize()) {
                if (menu.isResultSlot(raw) || menu.isFillerSlot(raw)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        scheduleRecompute(menu);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof CraftingTableMenu menu)) {
            return;
        }
        if (event.getPlayer() instanceof Player player) {
            menu.returnInputs(player);
        }
    }

    private void scheduleRecompute(CraftingTableMenu menu) {
        org.bukkit.Bukkit.getScheduler().runTask(
                dev.arubik.craftengine.CraftEnginePolyfills.instance(), menu::recompute);
    }
}
