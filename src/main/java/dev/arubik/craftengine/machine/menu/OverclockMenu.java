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

    /** Overload without items map — uses defaults. */
    public static MachineLayout build(String machineId, NamedTextColor fallbackTitleColor,
            DoubleSupplier overclockSupplier, DoubleSupplier limitSupplier,
            BiConsumer<Boolean, ClickType> bump, Consumer<Player> back) {
        return build(machineId, null, fallbackTitleColor, overclockSupplier, limitSupplier, bump, back, null);
    }

    /** Overload with title but no items map. */
    public static MachineLayout build(String machineId, Component customTitle, NamedTextColor fallbackTitleColor,
            DoubleSupplier overclockSupplier, DoubleSupplier limitSupplier,
            BiConsumer<Boolean, ClickType> bump, Consumer<Player> back) {
        return build(machineId, customTitle, fallbackTitleColor, overclockSupplier, limitSupplier, bump, back, null);
    }

    /** Full build — accepts JSON-defined item overrides from specialItems map. */
    public static MachineLayout build(String machineId, Component customTitle, NamedTextColor fallbackTitleColor,
            DoubleSupplier overclockSupplier, DoubleSupplier limitSupplier,
            BiConsumer<Boolean, ClickType> bump, Consumer<Player> back,
            java.util.Map<String, dev.arubik.craftengine.machine.MachineDefinition.ItemSpec> items) {
        MachineLayout l = new MachineLayout(InventoryType.CHEST, 27, "Overclock");
        l.setTitleComponent(customTitle != null ? customTitle
            : MenuText.noI(MenuText.tr("polyfill.ui.overclock", fallbackTitleColor)));

        // Decrease (−) — slot 10
        dev.arubik.craftengine.machine.MachineDefinition.ItemSpec decSpec = items != null ? items.get("decrease") : null;
        l.addClickButton(10, (m, t) -> decSpec != null ? decSpec.build(Material.RED_STAINED_GLASS_PANE)
            : MenuText.iconItem(Key.of("cml", "minus_icon"), Material.RED_STAINED_GLASS_PANE,
                MenuText.tr("polyfill.ui.oc_minus", NamedTextColor.RED), MenuText.tr("polyfill.ui.oc_hint_minus", NamedTextColor.GRAY)),
            (m, p, c) -> bump.accept(Boolean.FALSE, c));

        // Increase (+) — slot 16
        dev.arubik.craftengine.machine.MachineDefinition.ItemSpec incSpec = items != null ? items.get("increase") : null;
        l.addClickButton(16, (m, t) -> incSpec != null ? incSpec.build(Material.GREEN_STAINED_GLASS_PANE)
            : MenuText.iconItem(Key.of("cml", "plus_icon"), Material.GREEN_STAINED_GLASS_PANE,
                MenuText.tr("polyfill.ui.oc_plus", NamedTextColor.GREEN), MenuText.tr("polyfill.ui.oc_hint", NamedTextColor.GRAY)),
            (m, p, c) -> bump.accept(Boolean.TRUE, c));

        // Meter — slot 13. Icon can reference ${state} for the 73-state needle animation.
        dev.arubik.craftengine.machine.MachineDefinition.ItemSpec meterSpec = items != null ? items.get("meter") : null;
        l.setDynamicProvider(13, (m, t) -> {
            double oc = overclockSupplier.getAsDouble();
            if (meterSpec != null) {
                String rawIcon = meterSpec.icon() != null ? meterSpec.icon() : "cml:clock_${state}";
                String meterIcon = rawIcon.replace("${state}", String.valueOf(meterState(oc)));
                Component nameC = (meterSpec.name() != null && !meterSpec.name().isBlank())
                    ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(meterSpec.name())
                        .decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false)
                    : MenuText.tr("polyfill.ui.overclock", NamedTextColor.RED);
                Component[] loreC = (meterSpec.lore() == null || meterSpec.lore().isEmpty())
                    ? meterLore(oc, limitSupplier.getAsDouble())
                    : meterSpec.lore().stream()
                        .map(s -> (Component) net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(s)
                            .decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false))
                        .toArray(Component[]::new);
                org.bukkit.inventory.ItemStack meterItem = MenuText.iconItem(Key.of(meterIcon), Material.CLOCK, nameC, loreC);
                // Apply any data components from the spec (e.g. tooltip_display to hide tooltip)
                if (meterSpec.components() != null && !meterSpec.components().isEmpty() && meterItem != null) {
                    try {
                        net.minecraft.world.item.ItemStack nms = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(meterItem);
                        nms = dev.arubik.craftengine.script.types.primitive.DataComponentTypes.applyJsonComponents(nms, meterSpec.components());
                        meterItem = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nms);
                    } catch (Throwable ignored2) {}
                }
                return meterItem;
            }
            return MenuText.iconItem(Key.of("cml", "clock_" + meterState(oc)), Material.CLOCK,
                MenuText.tr("polyfill.ui.overclock", NamedTextColor.RED), meterLore(oc, limitSupplier.getAsDouble()));
        });

        // Mirror-lore carrier slots (3,4,5,12,14) — cml:gui_empty with meter tooltip
        dev.arubik.craftengine.machine.MachineDefinition.ItemSpec mirrorSpec = items != null ? items.get("mirror") : null;
        for (int slot : new int[]{3, 4, 5, 12, 14}) {
            l.setDynamicProvider(slot, (m, t) -> {
                double oc = overclockSupplier.getAsDouble();
                if (mirrorSpec != null) return mirrorSpec.build(Material.PAPER);
                return MenuText.iconItem(Key.of("cml", "gui_empty"), Material.PAPER,
                    Component.empty(), meterLore(oc, limitSupplier.getAsDouble()));
            });
        }

        l.addButton(22, (m, t) -> MenuText.backIcon(), (m, p) -> back.accept((Player)p));

        dev.arubik.craftengine.machine.MachineDefinition.ItemSpec fillerSpec = items != null ? items.get("filler") : null;
        org.bukkit.inventory.ItemStack filler = fillerSpec != null ? fillerSpec.build() : MenuText.emptyFiller();
        if (filler != null) l.fillBackground(filler);
        return l;
    }

    private static Component[] meterLore(double overclock, double limit) {
        return new Component[]{MenuText.kv("polyfill.ui.overclock", NamedTextColor.RED, String.format(Locale.ROOT, "%+.0f%%", overclock * 100.0), NamedTextColor.WHITE), MenuText.kv("polyfill.ui.max", NamedTextColor.GRAY, String.format(Locale.ROOT, "+%.0f%%", limit * 100.0), NamedTextColor.WHITE), MenuText.kv("polyfill.ui.speed", NamedTextColor.GRAY, String.format(Locale.ROOT, "x%.2f", 1.0 + overclock), NamedTextColor.WHITE)};
    }
}

