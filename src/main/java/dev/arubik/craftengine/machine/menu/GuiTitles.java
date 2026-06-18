package dev.arubik.craftengine.machine.menu;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import net.kyori.adventure.text.Component;

/**
 * Central, config-driven registry for machine-menu title images.
 *
 * <p>Every machine menu (main / upgrade / overclock / ...) can render a CraftEngine GUI image as its
 * inventory title. The image id for each {@code (machineId, menu)} pair is declared in a SINGLE yml,
 * {@code plugins/CraftEngine/resources/modern/configuration/polyfills_gui.yml}, so adding a machine
 * or wiring a new menu is a config change with no Java edit. Resolution is delegated to
 * {@link MenuText#imageTitle(String, int)} (a global left-nudge {@code shift} px is applied to all).</p>
 *
 * <p>Loaded on plugin enable AND on every {@code CraftEngineReloadEvent} (see
 * {@code CraftEnginePolyfills}), mirroring how the recipe loaders are refreshed.</p>
 *
 * <pre>
 * gui_titles:
 *   vapor_furnace_mk1:
 *     main:      cml:copper_furnace_gui
 *     upgrade:   cml:upgrades_gui
 *     overclock: cml:overclock_gui   # image may not exist yet -> title() returns null -> text fallback
 *   crusher:
 *     upgrade:   cml:upgrades_gui
 *   shift: -8                         # global left-nudge px applied to every gui-image title
 * </pre>
 */
public final class GuiTitles {

    private GuiTitles() {
    }

    /** Path of the yml relative to the running CraftEngine plugin folder. */
    private static final String CONFIG_PATH =
            "plugins/CraftEngine/resources/modern/configuration/polyfills_gui.yml";

    /** {@code machineId -> (menu -> imageId)}. Rebuilt on every {@link #reload()}. */
    private static volatile Map<String, Map<String, String>> images = new HashMap<>();
    /** Global left-nudge px applied to every gui-image title (negative = left). */
    private static volatile int shiftPx = -8;

    /** (Re)load the yml. Safe to call repeatedly (enable + each CraftEngine reload). */
    public static void reload() {
        Map<String, Map<String, String>> next = new HashMap<>();
        int nextShift = -8;
        try {
            File f = new File(CONFIG_PATH);
            if (f.isFile()) {
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                ConfigurationSection root = cfg.getConfigurationSection("gui_titles");
                if (root != null) {
                    if (root.isInt("shift"))
                        nextShift = root.getInt("shift");
                    for (String machine : root.getKeys(false)) {
                        if (machine.equals("shift"))
                            continue;
                        ConfigurationSection ms = root.getConfigurationSection(machine);
                        if (ms == null)
                            continue;
                        Map<String, String> menus = new HashMap<>();
                        for (String menu : ms.getKeys(false)) {
                            String id = ms.getString(menu);
                            if (id != null && !id.isBlank())
                                menus.put(menu, id);
                        }
                        if (!menus.isEmpty())
                            next.put(machine, menus);
                    }
                }
            }
        } catch (Throwable ignored) {
            // bad/missing config -> keep an empty map so every title() falls back to text.
        }
        images = next;
        shiftPx = nextShift;
    }

    /**
     * Resolve the inventory-title {@link Component} for {@code (machineId, menu)}.
     *
     * @return the rendered GUI-image title, or {@code null} when no image is configured for this
     *         pair or its resolution fails (the caller then uses its existing text title).
     */
    public static Component title(String machineId, String menu) {
        if (machineId == null || menu == null)
            return null;
        Map<String, String> menus = images.get(machineId);
        if (menus == null)
            return null;
        String imageId = menus.get(menu);
        if (imageId == null)
            return null;
        return MenuText.imageTitle(imageId, shiftPx);
    }
}
