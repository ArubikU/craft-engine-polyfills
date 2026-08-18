/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package dev.arubik.craftengine.contraption.physics;

import dev.arubik.craftengine.CraftEnginePolyfills;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

public final class BlockPropertyTable {
    private final String fileName;
    private final double builtInDefault;
    private final Map<Material, Double> overrides = new HashMap<Material, Double>();
    private double configuredDefault;

    public BlockPropertyTable(String fileName, double builtInDefault) {
        this.fileName = fileName;
        this.builtInDefault = builtInDefault;
        this.configuredDefault = builtInDefault;
    }

    public void load() {
        try {
            File file = new File(CraftEnginePolyfills.instance().getDataFolder(), this.fileName);
            if (!file.exists()) {
                CraftEnginePolyfills.instance().saveResource(this.fileName, false);
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration((File)file);
            HashMap<Material, Double> parsed = new HashMap<Material, Double>();
            if (yaml.isConfigurationSection("blocks")) {
                for (String key : yaml.getConfigurationSection("blocks").getKeys(false)) {
                    Material material = BlockPropertyTable.materialOf(key);
                    if (material == null) {
                        CraftEnginePolyfills.instance().getLogger().warning("[" + this.fileName + "] unknown block '" + key + "' \u2014 ignored");
                        continue;
                    }
                    parsed.put(material, yaml.getDouble("blocks." + key));
                }
            }
            this.overrides.clear();
            this.overrides.putAll(parsed);
            this.configuredDefault = yaml.getDouble("default", this.builtInDefault);
        }
        catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger().warning("[" + this.fileName + "] failed to load, keeping previous values: " + String.valueOf(t));
        }
    }

    public Double override(Material material) {
        return material == null ? null : this.overrides.get(material);
    }

    public double defaultValue() {
        return this.configuredDefault;
    }

    private static Material materialOf(String key) {
        String name = key.trim();
        int colon = name.indexOf(58);
        if (colon >= 0) {
            name = name.substring(colon + 1);
        }
        return Material.matchMaterial((String)name.toUpperCase(Locale.ROOT));
    }
}

