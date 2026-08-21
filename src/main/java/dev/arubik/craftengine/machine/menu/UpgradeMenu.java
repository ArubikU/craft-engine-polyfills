/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package dev.arubik.craftengine.machine.menu;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.layout.DynamicItemProvider;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import java.util.function.BiConsumer;
import java.util.function.IntSupplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class UpgradeMenu {
    private UpgradeMenu() {
    }

    public static MachineLayout build(String machineId, int count, IntSupplier unlockedFn,
            BiConsumer<AbstractMachineBlockEntity, Player> back, DynamicItemProvider infoProvider,
            java.util.Map<String, dev.arubik.craftengine.machine.MachineDefinition.ItemSpec> items) {
        int rows = count <= 9 ? 2 : count <= 18 ? 3 : 4;
        MachineLayout l = new MachineLayout(InventoryType.CHEST, rows * 9, "Upgrades");

        // Pre-build locked item (may change at runtime if ItemSpec references dynamic data)
        dev.arubik.craftengine.machine.MachineDefinition.ItemSpec lockedSpec =
            items != null ? items.get("locked") : null;

        int unlocked = unlockedFn.getAsInt();
        for (int i = 0; i < count; ++i) {
            if (i < unlocked) {
                l.addSlot(i, MenuSlotType.UPGRADE);
                continue;
            }
            int si = i;
            l.setDynamicProvider(si, (m, t) -> {
                if (si < unlockedFn.getAsInt()) return null;
                if (lockedSpec != null) return lockedSpec.build(Material.BARRIER);
                return MenuText.lockedIcon(
                    MenuText.tr("polyfill.ui.locked", NamedTextColor.RED),
                    MenuText.tr("polyfill.ui.locked_desc", NamedTextColor.GRAY));
            });
        }
        int totalSlots = rows * 9;
        int backSlot = totalSlots - 1;
        int infoSlot = totalSlots - 5;
        if (infoProvider != null) l.setDynamicProvider(infoSlot, infoProvider);
        l.addButton(backSlot, (m, t) -> MenuText.backIcon(), (m, p) -> back.accept((AbstractMachineBlockEntity)m, (Player)p));

        dev.arubik.craftengine.machine.MachineDefinition.ItemSpec fillerSpec =
            items != null ? items.get("filler") : null;
        ItemStack filler = fillerSpec != null ? fillerSpec.build() : MenuText.emptyFiller();
        if (filler != null) l.fillBackground(filler);
        return l;
    }

    /** Backwards-compat overload without items map. */
    public static MachineLayout build(String machineId, int count, IntSupplier unlockedFn,
            BiConsumer<AbstractMachineBlockEntity, Player> back, DynamicItemProvider infoProvider) {
        return build(machineId, count, unlockedFn, back, infoProvider, null);
    }
}

