/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package dev.arubik.craftengine.machine.menu;

import dev.arubik.craftengine.machine.menu.MenuText;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public final class GuiTitles {
    private static final String CONFIG_PATH = "plugins/CraftEngine/resources/modern/configuration/polyfills_gui.yml";
    private static volatile Map<String, Map<String, String>> images = new HashMap<String, Map<String, String>>();
    private static volatile int shiftPx = -8;

    private GuiTitles() {
    }

    public static void reload() {
        HashMap<String, Map<String, String>> next = new HashMap<String, Map<String, String>>();
        int nextShift = -8;
        try {
            YamlConfiguration cfg;
            ConfigurationSection root;
            File f = new File(CONFIG_PATH);
            if (f.isFile() && (root = (cfg = YamlConfiguration.loadConfiguration((File)f)).getConfigurationSection("gui_titles")) != null) {
                if (root.isInt("shift")) {
                    nextShift = root.getInt("shift");
                }
                for (String machine : root.getKeys(false)) {
                    ConfigurationSection ms;
                    if (machine.equals("shift") || (ms = root.getConfigurationSection(machine)) == null) continue;
                    HashMap<String, String> menus = new HashMap<String, String>();
                    for (String menu : ms.getKeys(false)) {
                        String id = ms.getString(menu);
                        if (id == null || id.isBlank()) continue;
                        menus.put(menu, id);
                    }
                    if (menus.isEmpty()) continue;
                    next.put(machine, menus);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        images = next;
        shiftPx = nextShift;
    }

    public static Component title(String machineId, String menu) {
        if (machineId == null || menu == null) {
            return null;
        }
        Map<String, String> menus = images.get(machineId);
        if (menus == null) {
            return null;
        }
        String imageId = menus.get(menu);
        if (imageId == null) {
            return null;
        }
        return MenuText.imageTitle(imageId, shiftPx);
    }
}

