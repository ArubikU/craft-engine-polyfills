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
                        if (isPlaceAction(event.getAction())) {
                            event.setCancelled(true);
                        } else {
                            // Schedule sync for removal
                            scheduleSync(menu);
                        }
                        break;
                    case INPUT:
                    case BURNING:
                        // Schedule sync for any change
                        scheduleSync(menu);
                        break;
                }
            } else {
                // Click in player inventory.
                // Handling Shift-Clicking items into the machine
                if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    scheduleSync(menu);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof MachineMenu menu) {
            MachineLayout layout = menu.getLayout();
            boolean affectsMachine = false;
            for (int slot : event.getRawSlots()) {
                if (slot < event.getInventory().getSize()) {
                    MenuSlotType type = layout.getSlotType(slot);
                    if (type == MenuSlotType.DYNAMIC || type == MenuSlotType.BACKGROUND
                            || type == MenuSlotType.OUTPUT || type == MenuSlotType.BUTTON) {
                        event.setCancelled(true);
                        return;
                    }
                    affectsMachine = true;
                }
            }
            if (affectsMachine) {
                scheduleSync(menu);
            }
        }
    }

    private void scheduleSync(MachineMenu menu) {
        org.bukkit.Bukkit.getScheduler().runTask(dev.arubik.craftengine.CraftEnginePolyfills.instance(), () -> {
            menu.syncToMachine();
        });
    }

    private boolean isPlaceAction(InventoryAction action) {
        return action == InventoryAction.PLACE_ALL ||
                action == InventoryAction.PLACE_ONE ||
                action == InventoryAction.PLACE_SOME ||
                action == InventoryAction.SWAP_WITH_CURSOR;
    }
}
