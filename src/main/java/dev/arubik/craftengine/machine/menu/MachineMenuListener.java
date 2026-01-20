package dev.arubik.craftengine.machine.menu;

import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryAction;

public class MachineMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof MachineMenu menu) {
            MachineLayout layout = menu.getLayout();
            int slot = event.getRawSlot();

            // Check if click is in the Machine Inventory (top), not Player Inventory
            // (bottom)
            if (slot < event.getInventory().getSize() && slot >= 0) {
                MenuSlotType type = layout.getSlotType(slot);

                switch (type) {
                    case DYNAMIC:
                    case BACKGROUND:
                        event.setCancelled(true);
                        break;
                    case BUTTON:
                        event.setCancelled(true);
                        var action = layout.getButtonAction(slot);
                        if (action != null && event.getWhoClicked() instanceof org.bukkit.entity.Player player) {
                            action.accept(menu.getMachine(), player);
                        }
                        break;
                    case OUTPUT:
                        // Prevent placing items INTO output
                        // allow taking (PICKUP_ALL, etc)
                        // But block PLACE_ALL, PLACE_ONE, SWAP_WITH_CURSOR
                        if (isPlaceAction(event.getAction())) {
                            event.setCancelled(true);
                        }
                        break;
                    case INPUT:
                    case BURNING:
                        // Allow everything, or add specific filters here
                        // e.g. menu.getMachine().isItemValidForSlot(slot, item);
                        break;
                }
            } else {
                // Click in player inventory.
                // Check Shift-Click (MOVE_TO_OTHER_INVENTORY)
                if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    // We need to know destination slot logic.
                    // This is complex to implement perfectly without NMS container logic.
                    // For MVP: Allow it, but if it lands in DYNAMIC/OUTPUT it might be weird.
                    // Ideally we cancel shift-click or strictly handle it.
                    // event.setCancelled(true); // Easiest safety
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof MachineMenu menu) {
            MachineLayout layout = menu.getLayout();
            for (int slot : event.getRawSlots()) {
                if (slot < event.getInventory().getSize()) {
                    MenuSlotType type = layout.getSlotType(slot);
                    if (type == MenuSlotType.DYNAMIC || type == MenuSlotType.BACKGROUND
                            || type == MenuSlotType.OUTPUT) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    private boolean isPlaceAction(InventoryAction action) {
        return action == InventoryAction.PLACE_ALL ||
                action == InventoryAction.PLACE_ONE ||
                action == InventoryAction.PLACE_SOME ||
                action == InventoryAction.SWAP_WITH_CURSOR;
    }
}
