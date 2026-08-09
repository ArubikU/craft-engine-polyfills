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
                        if (event.getWhoClicked() instanceof org.bukkit.entity.Player player) {
                            var clickAction = layout.getClickButtonAction(slot);
                            if (clickAction != null) {
                                clickAction.accept(menu.getMachine(), player, event.getClick());
                            } else {
                                var action = layout.getButtonAction(slot);
                                if (action != null) {
                                    action.accept(menu.getMachine(), player);
                                }
                            }
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
                    case FUEL:
                        // Frozen slots (e.g. an upgrade holding others unlocked) reject changes.
                        if (!menu.getMachine().canTakeFromSlot(layout.getMachineSlot(slot))) {
                            event.setCancelled(true);
                            break;
                        }
                        boolean isFuelSlot = type == MenuSlotType.FUEL;
                        org.bukkit.inventory.ItemStack cursor = event.getCursor();
                        boolean placing = isPlaceAction(event.getAction());
                        // FUEL filter: only valid fuel for this machine may go into the fuel slot.
                        if (isFuelSlot && placing && cursor != null && !cursor.getType().isAir()
                                && !menu.getMachine().isFuelItem(cursor)) {
                            event.setCancelled(true);
                            break;
                        }
                        // Empty fuel slot shows a display-only ghost: it can't be taken; placing
                        // fuel onto it replaces it.
                        if (MachineMenu.isGhost(event.getInventory().getItem(slot))) {
                            event.setCancelled(true);
                            if (cursor != null && !cursor.getType().isAir()
                                    && menu.getMachine().isFuelItem(cursor)
                                    && event.getWhoClicked() instanceof org.bukkit.entity.Player p) {
                                event.getInventory().setItem(slot, cursor.clone());
                                p.setItemOnCursor(null);
                                scheduleSync(menu);
                            }
                            break;
                        }
                        // Schedule sync for any change
                        scheduleSync(menu);
                        break;
                }
            } else {
                // Click in player inventory: route shift-click by item type — FUEL items go to the
                // fuel slot(s) first, everything else to the input slot(s).
                if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    event.setCancelled(true);
                    org.bukkit.inventory.ItemStack moving = event.getCurrentItem();
                    if (moving != null && !moving.getType().isAir()) {
                        var m = menu.getMachine();
                        int[] targets = layout.getSlotsOfType(m.isFuelItem(moving) ? MenuSlotType.FUEL : MenuSlotType.INPUT);
                        org.bukkit.inventory.ItemStack leftover = mergeInto(event.getInventory(), targets,
                                moving.clone());
                        event.setCurrentItem(leftover);
                        scheduleSync(menu);
                    }
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
        // Freeze machine->menu input pulls until we capture the player's placement next tick,
        // otherwise the machine's per-tick setChanged() can wipe the freshly-placed item.
        menu.markInputDirty();
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

    /** Merge {@code stack} into {@code slots} in order (empty/ghost = fillable). Returns leftover or null. */
    private static org.bukkit.inventory.ItemStack mergeInto(org.bukkit.inventory.Inventory inv, int[] slots,
            org.bukkit.inventory.ItemStack stack) {
        if (slots == null)
            return stack.getAmount() > 0 ? stack : null;
        // First top up matching stacks, then drop into the first empty/ghost slot.
        for (int s : slots) {
            if (stack.getAmount() <= 0)
                return null;
            org.bukkit.inventory.ItemStack cur = inv.getItem(s);
            if (cur != null && !cur.getType().isAir() && !MachineMenu.isGhost(cur) && cur.isSimilar(stack)) {
                int space = cur.getMaxStackSize() - cur.getAmount();
                if (space > 0) {
                    int move = Math.min(space, stack.getAmount());
                    cur.setAmount(cur.getAmount() + move);
                    inv.setItem(s, cur);
                    stack.setAmount(stack.getAmount() - move);
                }
            }
        }
        for (int s : slots) {
            if (stack.getAmount() <= 0)
                return null;
            org.bukkit.inventory.ItemStack cur = inv.getItem(s);
            if (cur == null || cur.getType().isAir() || MachineMenu.isGhost(cur)) {
                inv.setItem(s, stack.clone());
                return null;
            }
        }
        return stack.getAmount() > 0 ? stack : null;
    }
}
