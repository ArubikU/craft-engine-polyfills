/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.minecraft.world.item.ItemStack
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.persistence.PersistentDataType
 */
package dev.arubik.craftengine.machine.menu;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.layout.DynamicItemProvider;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.util.BridgeUtils;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class MachineMenu
implements InventoryHolder {
    private final AbstractMachineBlockEntity machine;
    private final MachineLayout layout;
    private final Inventory inventory;
    private int tickCount = 0;
    private volatile boolean suppressInputPull = false;
    private static final NamespacedKey GHOST_KEY = new NamespacedKey("cml", "slot_ghost");

    public MachineMenu(AbstractMachineBlockEntity machine, MachineLayout layout) {
        boolean chest;
        String dynamic;
        this.machine = machine;
        this.layout = layout;
        String title = layout.getTitlePattern();
        if (layout.getTitleProvider() != null && (dynamic = layout.getTitleProvider().provide(machine)) != null) {
            title = dynamic;
        }
        Component titleComp = layout.getTitleComponent();
        boolean bl = chest = layout.getInventoryType() == InventoryType.CHEST;
        this.inventory = titleComp != null ? (chest ? Bukkit.createInventory((InventoryHolder)this, (int)layout.getSize(), (Component)titleComp) : Bukkit.createInventory((InventoryHolder)this, (InventoryType)layout.getInventoryType(), (Component)titleComp)) : (chest ? Bukkit.createInventory((InventoryHolder)this, (int)layout.getSize(), (String)title) : Bukkit.createInventory((InventoryHolder)this, (InventoryType)layout.getInventoryType(), (String)title));
        this.updateDynamicSlots();
    }

    public void tick() {
        String dynamic;
        if (this.inventory.getViewers().isEmpty()) {
            return;
        }
        ++this.tickCount;
        if (this.tickCount % 2 == 0) {
            this.updateDynamicSlots();
            this.syncFromMachine();
        }
        if (this.tickCount % 5 == 0 && this.layout.getTitleProvider() != null && (dynamic = this.layout.getTitleProvider().provide(this.machine)) != null && !dynamic.isEmpty()) {
            for (HumanEntity viewer : this.inventory.getViewers()) {
                try {
                    viewer.getOpenInventory().setTitle(dynamic);
                }
                catch (Throwable throwable) {}
            }
        }
    }

    public void markInputDirty() {
        this.suppressInputPull = true;
    }

    private static ItemStack fuelGhost() {
        ItemStack s = new ItemStack(Material.COAL);
        ItemMeta m = s.getItemMeta();
        if (m != null) {
            m.displayName(MenuText.noI(MenuText.tr("polyfill.ui.fuel", NamedTextColor.GRAY)));
            m.getPersistentDataContainer().set(GHOST_KEY, PersistentDataType.BYTE, (byte) 1);
            s.setItemMeta(m);
        }
        return s;
    }

    public static boolean isGhost(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return false;
        }
        ItemMeta m = stack.getItemMeta();
        return m != null && m.getPersistentDataContainer().has(GHOST_KEY, PersistentDataType.BYTE);
    }

    public void syncFromMachine() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            boolean io;
            MenuSlotType type = this.layout.getSlotType(i);
            boolean bl = io = type == MenuSlotType.INPUT || type == MenuSlotType.OUTPUT || type == MenuSlotType.FUEL || type == MenuSlotType.UPGRADE;
            if (!io || this.suppressInputPull && (type == MenuSlotType.INPUT || type == MenuSlotType.FUEL)) continue;
            net.minecraft.world.item.ItemStack nms = this.machine.getItem(this.layout.getMachineSlot(i));
            this.inventory.setItem(i, BridgeUtils.toBukkit(nms));
        }
    }

    public void syncToMachine(int slot) {
        if (slot < 0 || slot >= this.inventory.getSize()) {
            return;
        }
        MenuSlotType type = this.layout.getSlotType(slot);
        if (type == MenuSlotType.INPUT || type == MenuSlotType.OUTPUT || type == MenuSlotType.FUEL || type == MenuSlotType.UPGRADE) {
            ItemStack bukkit = this.inventory.getItem(slot);
            if (bukkit == null || bukkit.getType() == Material.AIR || MachineMenu.isGhost(bukkit)) {
                this.machine.setItem(this.layout.getMachineSlot(slot), net.minecraft.world.item.ItemStack.EMPTY);
            } else if (bukkit instanceof CraftItemStack) {
                this.machine.setItem(this.layout.getMachineSlot(slot), ((CraftItemStack)bukkit).handle);
            } else {
                this.machine.setItem(this.layout.getMachineSlot(slot), CraftItemStack.asNMSCopy((ItemStack)bukkit));
            }
        }
    }

    public void syncToMachine() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            this.syncToMachine(i);
        }
        this.suppressInputPull = false;
    }

    private void updateDynamicSlots() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            DynamicItemProvider provider;
            MenuSlotType type = this.layout.getSlotType(i);
            if (type != MenuSlotType.DYNAMIC && type != MenuSlotType.BUTTON || (provider = this.layout.getProvider(i)) == null) continue;
            ItemStack next = provider.provide(this.machine, this.tickCount);
            if (Objects.equals(this.inventory.getItem(i), next)) continue;
            this.inventory.setItem(i, next);
        }
    }

    public void open(Player player) {
        player.openInventory(this.inventory);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public AbstractMachineBlockEntity getMachine() {
        return this.machine;
    }

    public MachineLayout getLayout() {
        return this.layout;
    }
}

