package dev.arubik.craftengine.machine.menu;

import java.util.function.BiConsumer;
import java.util.function.DoubleSupplier;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.momirealms.craftengine.core.util.Key;

/**
 * Shared OVERCLOCK submenu (page 2), identical for every machine that exposes a player-tunable
 * overclock fraction (Vapor Furnace Mk1, Crusher, ...). Both machines' {@code buildOverclockLayout}
 * delegate here so the layout lives in ONE place.
 *
 * <h2>Slot map (27-slot CHEST)</h2>
 * <pre>
 *   3,4,5  — invisible (cml:gui_empty) but carry the SAME meter lore (wider hover area)
 *   10     — remove-overclock button (cml:minus_icon) -> bumpOverclock(false, click)
 *   12,14  — invisible mirror-lore (flank the meter)
 *   13     — overclock METER: cml:clock_&lt;state&gt; + lore (current %, max %, speed x)
 *   16     — add-overclock button (cml:plus_icon) -> bumpOverclock(true, click)
 *   22     — back button (cml:return_icon) -> page 0
 * </pre>
 *
 * <h2>Step change</h2>
 * left/normal click = 1% (0.01), right = 25% (0.25), drop or Q (DROP/CONTROL_DROP) = 50% (0.50).
 *
 * <h2>Meter scaling</h2>
 * 73 states across -100%..+400%: state 0 = -100% (or below), state 72 = +400% (or above).
 * {@code state = clamp(round((overclock+1.0)/5.0 * 72), 0, 72)}.
 */
public final class OverclockMenu {

    private OverclockMenu() {
    }

    /** Tuning step (fraction) for a given click type: 1% / 25% / 50%. */
    public static float step(ClickType c) {
        if (c == ClickType.DROP || c == ClickType.CONTROL_DROP)
            return 0.50f;
        if (c == ClickType.RIGHT || c == ClickType.SHIFT_RIGHT)
            return 0.25f;
        return 0.01f; // left / normal click
    }

    /** Map the current overclock fraction to a clock meter state index in [0, 72]. */
    public static int meterState(double overclock) {
        // Range spans -100% .. +400% (underclock included). state 0 = -100%, state 72 = +400%.
        int state = (int) Math.round((overclock + 1.0) / 5.0 * 72.0);
        return Math.max(0, Math.min(72, state));
    }

    /**
     * Build the shared overclock layout.
     *
     * @param machineId          the machine id (for the GuiTitles overclock image)
     * @param fallbackTitleColor color for the plain-text title fallback
     * @param overclockSupplier  current overclock fraction (0.5 = +50%)
     * @param limitSupplier      current overclock cap (from upgrades)
     * @param bump               (up, clickType) -&gt; tune the overclock
     * @param back               open page 0
     */
    public static MachineLayout build(String machineId, NamedTextColor fallbackTitleColor,
            DoubleSupplier overclockSupplier, DoubleSupplier limitSupplier,
            BiConsumer<Boolean, ClickType> bump,
            java.util.function.Consumer<org.bukkit.entity.Player> back) {

        MachineLayout l = new MachineLayout(InventoryType.CHEST, 27, "Overclock");
        Component title = GuiTitles.title(machineId, "overclock");
        l.setTitleComponent(title != null ? title
                : MenuText.noI(MenuText.tr("polyfill.ui.overclock", fallbackTitleColor)));

        // Remove (slot 10) — minus icon.
        l.addClickButton(10, (m, t) -> MenuText.iconItem(Key.of("cml", "minus_icon"), Material.RED_STAINED_GLASS_PANE,
                MenuText.tr("polyfill.ui.oc_minus", NamedTextColor.RED),
                MenuText.tr("polyfill.ui.oc_hint_minus", NamedTextColor.GRAY)),
                (m, p, c) -> bump.accept(Boolean.FALSE, c));

        // Add (slot 16) — plus icon.
        l.addClickButton(16, (m, t) -> MenuText.iconItem(Key.of("cml", "plus_icon"), Material.GREEN_STAINED_GLASS_PANE,
                MenuText.tr("polyfill.ui.oc_plus", NamedTextColor.GREEN),
                MenuText.tr("polyfill.ui.oc_hint", NamedTextColor.GRAY)),
                (m, p, c) -> bump.accept(Boolean.TRUE, c));

        // Meter (slot 13) — dynamic clock item + lore.
        l.setDynamicProvider(13, (m, t) -> {
            double oc = overclockSupplier.getAsDouble();
            return MenuText.iconItem(Key.of("cml", "clock_" + meterState(oc)), Material.CLOCK,
                    MenuText.tr("polyfill.ui.overclock", NamedTextColor.RED),
                    meterLore(oc, limitSupplier.getAsDouble()));
        });

        // Mirror-lore slots (3,4,5,12,14): invisible cml:gui_empty but carry the same meter lore so the
        // wider area shows the same info on hover. The clock NAME is dropped (empty) to keep them blank.
        for (int slot : new int[] { 3, 4, 5, 12, 14 }) {
            l.setDynamicProvider(slot, (m, t) -> {
                double oc = overclockSupplier.getAsDouble();
                return MenuText.iconItem(Key.of("cml", "gui_empty"), Material.PAPER,
                        Component.empty(), meterLore(oc, limitSupplier.getAsDouble()));
            });
        }

        // Back (slot 22).
        l.addButton(22, (m, t) -> MenuText.backIcon(), (m, p) -> back.accept(p));

        // Background filler is applied by the caller's fillRest().
        return l;
    }

    /** The shared meter tooltip: current overclock %, max %, and the speed multiplier x(1+overclock). */
    private static Component[] meterLore(double overclock, double limit) {
        return new Component[] {
                MenuText.kv("polyfill.ui.overclock", NamedTextColor.RED,
                        String.format(java.util.Locale.ROOT, "%+.0f%%", overclock * 100), NamedTextColor.WHITE),
                MenuText.kv("polyfill.ui.max", NamedTextColor.GRAY,
                        String.format(java.util.Locale.ROOT, "+%.0f%%", limit * 100), NamedTextColor.WHITE),
                MenuText.kv("polyfill.ui.speed", NamedTextColor.GRAY,
                        String.format(java.util.Locale.ROOT, "x%.2f", 1.0 + overclock), NamedTextColor.WHITE),
        };
    }
}
