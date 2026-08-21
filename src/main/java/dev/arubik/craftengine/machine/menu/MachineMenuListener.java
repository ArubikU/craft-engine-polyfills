/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryAction
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package dev.arubik.craftengine.machine.menu;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import java.util.Iterator;
import java.util.function.BiConsumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class MachineMenuListener
implements Listener {
    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (!(inventoryHolder instanceof MachineMenu)) return;
        MachineMenu menu = (MachineMenu)inventoryHolder;
        MachineLayout layout = menu.getLayout();
        int slot = event.getRawSlot();
        if (slot < event.getInventory().getSize() && slot >= 0) {
            // A locked slot is immovable regardless of its type — that is the whole point of
            // locking, since the slot usually IS a real input/output the machine still tracks.
            if (layout.isLocked(slot)) {
                event.setCancelled(true);
                return;
            }
            MenuSlotType type = layout.getSlotType(slot);
            boolean isHotbarSwap = event.getAction() == InventoryAction.HOTBAR_SWAP;
            switch (type) {
                case DYNAMIC: 
                case BACKGROUND: {
                    event.setCancelled(true);
                    return;
                }
                case BUTTON: {
                    event.setCancelled(true);
                    HumanEntity humanEntity = event.getWhoClicked();
                    if (!(humanEntity instanceof Player)) return;
                    Player player = (Player)humanEntity;
                    MachineLayout.ClickButtonAction clickAction = layout.getClickButtonAction(slot);
                    if (clickAction != null) {
                        clickAction.accept(menu.getMachine(), player, event.getClick());
                        return;
                    }
                    BiConsumer<AbstractMachineBlockEntity, Player> action = layout.getButtonAction(slot);
                    if (action == null) return;
                    action.accept(menu.getMachine(), player);
                    return;
                }
                case OUTPUT: {
                    if (this.isPlaceAction(event.getAction())) {
                        event.setCancelled(true);
                        return;
                    }
                    this.scheduleSync(menu);
                    return;
                }
                case UPGRADE: 
                case INPUT: 
                case FUEL: {
                    if (!menu.getMachine().canTakeFromSlot(layout.getMachineSlot(slot))) {
                        event.setCancelled(true);
                        return;
                    }
                    boolean isFuelSlot = type == MenuSlotType.FUEL;
                    ItemStack cursor = event.getCursor();
                    boolean placing = this.isPlaceAction(event.getAction());
                    if (isFuelSlot && placing && cursor != null && !cursor.getType().isAir() && !menu.getMachine().isFuelItem(cursor)) {
                        event.setCancelled(true);
                        return;
                    }
                    if (MachineMenu.isGhost(event.getInventory().getItem(slot))) {
                        HumanEntity humanEntity;
                        event.setCancelled(true);
                        if (cursor == null || cursor.getType().isAir() || !menu.getMachine().isFuelItem(cursor) || !((humanEntity = event.getWhoClicked()) instanceof Player)) return;
                        Player p = (Player)humanEntity;
                        event.getInventory().setItem(slot, cursor.clone());
                        p.setItemOnCursor(null);
                        this.scheduleSync(menu);
                        return;
                    }
                    this.scheduleSync(menu);
                }
            }
            return;
        } else {
            if (event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) return;
            event.setCancelled(true);
            ItemStack moving = event.getCurrentItem();
            if (moving == null || moving.getType().isAir()) return;
            AbstractMachineBlockEntity m = menu.getMachine();
            int[] targets = layout.getSlotsOfType(m.isFuelItem(moving) ? MenuSlotType.FUEL : MenuSlotType.INPUT);
            // Shift-clicking must not sneak items into a slot the player cannot click directly.
            targets = java.util.Arrays.stream(targets).filter(t -> !layout.isLocked(t)).toArray();
            ItemStack leftover = MachineMenuListener.mergeInto(event.getInventory(), targets, moving.clone());
            event.setCurrentItem(leftover);
            this.scheduleSync(menu);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (inventoryHolder instanceof MachineMenu) {
            MachineMenu menu = (MachineMenu)inventoryHolder;
            MachineLayout layout = menu.getLayout();
            boolean affectsMachine = false;
            Iterator iterator = event.getRawSlots().iterator();
            while (iterator.hasNext()) {
                int slot = (Integer)iterator.next();
                if (slot >= event.getInventory().getSize()) continue;
                MenuSlotType type = layout.getSlotType(slot);
                if (layout.isLocked(slot) || type == MenuSlotType.DYNAMIC || type == MenuSlotType.BACKGROUND || type == MenuSlotType.OUTPUT || type == MenuSlotType.BUTTON) {
                    event.setCancelled(true);
                    return;
                }
                affectsMachine = true;
            }
            if (affectsMachine) {
                this.scheduleSync(menu);
            }
        }
    }

    private void scheduleSync(MachineMenu menu) {
        menu.markInputDirty();
        Bukkit.getScheduler().runTask((Plugin)CraftEnginePolyfills.instance(), () -> menu.syncToMachine());
    }

    private boolean isPlaceAction(InventoryAction action) {
        return action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE || action == InventoryAction.PLACE_SOME || action == InventoryAction.SWAP_WITH_CURSOR || action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.HOTBAR_MOVE_AND_READD;
    }

    private static ItemStack mergeInto(Inventory inv, int[] slots, ItemStack stack) {
        ItemStack cur;
        if (slots == null) {
            return stack.getAmount() > 0 ? stack : null;
        }
        for (int s : slots) {
            int space;
            if (stack.getAmount() <= 0) {
                return null;
            }
            cur = inv.getItem(s);
            if (cur == null || cur.getType().isAir() || MachineMenu.isGhost(cur) || !cur.isSimilar(stack) || (space = cur.getMaxStackSize() - cur.getAmount()) <= 0) continue;
            int move = Math.min(space, stack.getAmount());
            cur.setAmount(cur.getAmount() + move);
            inv.setItem(s, cur);
            stack.setAmount(stack.getAmount() - move);
        }
        for (int s : slots) {
            if (stack.getAmount() <= 0) {
                return null;
            }
            cur = inv.getItem(s);
            if (cur != null && !cur.getType().isAir() && !MachineMenu.isGhost(cur)) continue;
            inv.setItem(s, stack.clone());
            return null;
        }
        return stack.getAmount() > 0 ? stack : null;
    }
}

