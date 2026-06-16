package dev.arubik.craftengine.crafting;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * Drives ALL {@link AbstractCraftingMenu} interaction (the default crafting
 * table and any custom subclass): keeps the output preview in sync as inputs
 * change, handles taking the result (normal and shift-click bulk craft), defers
 * CUSTOM-slot changes to the menu hook, and returns inputs on close.
 *
 * <p>Kept as the same class name with a no-arg constructor so existing
 * registration wiring continues to work unchanged.
 */
public final class CraftingTableListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof AbstractCraftingMenu menu)) {
            return;
        }
        int raw = event.getRawSlot();
        boolean topInventory = raw >= 0 && raw < event.getInventory().getSize();

        if (topInventory) {
            if (menu.isBackgroundSlot(raw)) {
                event.setCancelled(true);
                return;
            }
            if (menu.isOutputSlot(raw)) {
                event.setCancelled(true);
                if (event.getWhoClicked() instanceof Player player) {
                    if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                        menu.shiftTakeResult(player);
                    } else {
                        menu.takeResult(player);
                    }
                }
                return;
            }
            if (menu.isCustomSlot(raw)) {
                // Custom-slot click hook (e.g. right-click blueprint -> auto-fill grid).
                org.bukkit.inventory.ItemStack cursor = event.getCursor();
                boolean cursorEmpty = cursor == null || cursor.getType() == org.bukkit.Material.AIR;
                if (event.getWhoClicked() instanceof Player clicker
                        && menu.onCustomSlotClickExternal(raw, event.getClick(), cursorEmpty, clicker)) {
                    event.setCancelled(true);
                    return;
                }
                // Enforce the CUSTOM-slot whitelist for the item being placed.
                org.bukkit.inventory.ItemStack placing = event.getCursor();
                if (event.getClick() == ClickType.NUMBER_KEY && event.getWhoClicked() instanceof Player p
                        && event.getHotbarButton() >= 0) {
                    placing = p.getInventory().getItem(event.getHotbarButton());
                }
                if (placing != null && placing.getType() != org.bukkit.Material.AIR
                        && !menu.canPlaceCustomExternal(raw, placing)) {
                    event.setCancelled(true);
                    return;
                }
                // Let the change apply, then notify the subclass next tick.
                scheduleCustom(menu, raw);
                return;
            }
            // INPUT slot: allow the change, then recompute next tick.
            scheduleRecompute(menu);
            return;
        }

        // Shift-click from the player inventory: route by role + whitelist ourselves
        // (vanilla would dump into the first empty slot, ignoring the tool whitelist).
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            org.bukkit.inventory.ItemStack moving = event.getCurrentItem();
            if (moving == null || moving.getType() == org.bukkit.Material.AIR) {
                return;
            }
            event.setCancelled(true);
            org.bukkit.inventory.ItemStack leftover = menu.shiftInsert(moving.clone());
            event.setCurrentItem(leftover);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof AbstractCraftingMenu menu)) {
            return;
        }
        for (int raw : event.getRawSlots()) {
            if (raw < event.getInventory().getSize()
                    && (menu.isOutputSlot(raw) || menu.isBackgroundSlot(raw))) {
                event.setCancelled(true);
                return;
            }
        }
        scheduleRecompute(menu);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof AbstractCraftingMenu menu)) {
            return;
        }
        // Shared inventory: only finalize (return inputs + persist) when the LAST viewer closes.
        // During this event the closing player still counts as a viewer, so size 1 = last.
        if (event.getInventory().getViewers().size() > 1) {
            return;
        }
        if (event.getPlayer() instanceof Player player) {
            menu.returnInputs(player);
        }
    }

    private void scheduleRecompute(AbstractCraftingMenu menu) {
        org.bukkit.Bukkit.getScheduler().runTask(
                dev.arubik.craftengine.CraftEnginePolyfills.instance(), menu::recompute);
    }

    private void scheduleCustom(AbstractCraftingMenu menu, int slot) {
        org.bukkit.Bukkit.getScheduler().runTask(
                dev.arubik.craftengine.CraftEnginePolyfills.instance(), () -> menu.onCustomSlotChangedExternal(slot));
    }
}
