/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryType
 */
package dev.arubik.craftengine.machine.menu;

import dev.arubik.craftengine.machine.menu.GuiTitles;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;

public final class OverclockMenu {
    private OverclockMenu() {
    }

    public static float step(ClickType c) {
        if (c == ClickType.DROP || c == ClickType.CONTROL_DROP) {
            return 0.5f;
        }
        if (c == ClickType.RIGHT || c == ClickType.SHIFT_RIGHT) {
            return 0.25f;
        }
        return 0.01f;
    }

    public static int meterState(double overclock) {
        int state = (int)Math.round((overclock + 1.0) / 5.0 * 72.0);
        return Math.max(0, Math.min(72, state));
    }

    public static MachineLayout build(String machineId, NamedTextColor fallbackTitleColor, DoubleSupplier overclockSupplier, DoubleSupplier limitSupplier, BiConsumer<Boolean, ClickType> bump, Consumer<Player> back) {
        MachineLayout l = new MachineLayout(InventoryType.CHEST, 27, "Overclock");
        Component title = GuiTitles.title(machineId, "overclock");
        l.setTitleComponent(title != null ? title : MenuText.noI(MenuText.tr("polyfill.ui.overclock", fallbackTitleColor)));
        l.addClickButton(10, (m, t) -> MenuText.iconItem(Key.of((String)"cml", (String)"minus_icon"), Material.RED_STAINED_GLASS_PANE, MenuText.tr("polyfill.ui.oc_minus", NamedTextColor.RED), MenuText.tr("polyfill.ui.oc_hint_minus", NamedTextColor.GRAY)), (m, p, c) -> bump.accept(Boolean.FALSE, c));
        l.addClickButton(16, (m, t) -> MenuText.iconItem(Key.of((String)"cml", (String)"plus_icon"), Material.GREEN_STAINED_GLASS_PANE, MenuText.tr("polyfill.ui.oc_plus", NamedTextColor.GREEN), MenuText.tr("polyfill.ui.oc_hint", NamedTextColor.GRAY)), (m, p, c) -> bump.accept(Boolean.TRUE, c));
        l.setDynamicProvider(13, (m, t) -> {
            double oc = overclockSupplier.getAsDouble();
            return MenuText.iconItem(Key.of((String)"cml", (String)("clock_" + OverclockMenu.meterState(oc))), Material.CLOCK, MenuText.tr("polyfill.ui.overclock", NamedTextColor.RED), OverclockMenu.meterLore(oc, limitSupplier.getAsDouble()));
        });
        for (int slot : new int[]{3, 4, 5, 12, 14}) {
            l.setDynamicProvider(slot, (m, t) -> {
                double oc = overclockSupplier.getAsDouble();
                return MenuText.iconItem(Key.of((String)"cml", (String)"gui_empty"), Material.PAPER, (Component)Component.empty(), OverclockMenu.meterLore(oc, limitSupplier.getAsDouble()));
            });
        }
        l.addButton(22, (m, t) -> MenuText.backIcon(), (m, p) -> back.accept((Player)p));
        return l;
    }

    private static Component[] meterLore(double overclock, double limit) {
        return new Component[]{MenuText.kv("polyfill.ui.overclock", NamedTextColor.RED, String.format(Locale.ROOT, "%+.0f%%", overclock * 100.0), NamedTextColor.WHITE), MenuText.kv("polyfill.ui.max", NamedTextColor.GRAY, String.format(Locale.ROOT, "+%.0f%%", limit * 100.0), NamedTextColor.WHITE), MenuText.kv("polyfill.ui.speed", NamedTextColor.GRAY, String.format(Locale.ROOT, "x%.2f", 1.0 + overclock), NamedTextColor.WHITE)};
    }
}

