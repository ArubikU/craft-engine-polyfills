package dev.arubik.craftengine.contraption.physics;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import dev.arubik.craftengine.CraftEnginePolyfills;

/**
 * A per-vanilla-block numeric property, loaded from a YAML file the server owner can edit.
 *
 * <h2>Where a value comes from</h2>
 * Four layers, first match wins:
 * <ol>
 * <li>a CraftEngine custom block's own behavior ({@code weight:} / {@code floatability:}) — the block
 *     itself declares it, so nothing else may override it,</li>
 * <li>this file's {@code blocks:} map, keyed by Bukkit {@link Material} name — the server owner's
 *     override for a specific vanilla block,</li>
 * <li>the built-in family table in code (a stone hull is heavy, a wool sail is light),</li>
 * <li>this file's {@code default:} — the answer for a block nothing else classified.</li>
 * </ol>
 *
 * <p>The layering is what makes the built-in table a default rather than a decision: it exists so a
 * fresh server behaves sensibly with no configuration at all, and every entry in it can be overridden
 * without touching code.
 *
 * <h2>Format</h2>
 * <pre>
 * default: 10.0
 * blocks:
 *   IRON_BLOCK: 30.0
 *   OAK_PLANKS: 6.0
 * </pre>
 * Keys are Bukkit {@link Material} names, case-insensitive; a {@code minecraft:} prefix is accepted and
 * stripped, since that is how the same block is spelled everywhere else in this plugin's configs.
 */
public final class BlockPropertyTable {

    private final String fileName;
    private final double builtInDefault;
    private final Map<Material, Double> overrides = new HashMap<>();
    private double configuredDefault;

    public BlockPropertyTable(String fileName, double builtInDefault) {
        this.fileName = fileName;
        this.builtInDefault = builtInDefault;
        this.configuredDefault = builtInDefault;
    }

    /**
     * (Re)loads the file, writing a commented starter copy if it does not exist yet.
     *
     * <p>Best-effort by design: a malformed file logs and leaves the previous values in place rather
     * than throwing, because a typo in an optional tuning file must not stop the plugin from enabling.
     */
    public void load() {
        try {
            File file = new File(CraftEnginePolyfills.instance().getDataFolder(), fileName);
            if (!file.exists()) {
                CraftEnginePolyfills.instance().saveResource(fileName, false);
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            Map<Material, Double> parsed = new HashMap<>();
            if (yaml.isConfigurationSection("blocks")) {
                for (String key : yaml.getConfigurationSection("blocks").getKeys(false)) {
                    Material material = materialOf(key);
                    if (material == null) {
                        CraftEnginePolyfills.instance().getLogger()
                                .warning("[" + fileName + "] unknown block '" + key + "' — ignored");
                        continue;
                    }
                    parsed.put(material, yaml.getDouble("blocks." + key));
                }
            }
            overrides.clear();
            overrides.putAll(parsed);
            configuredDefault = yaml.getDouble("default", builtInDefault);
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[" + fileName + "] failed to load, keeping previous values: " + t);
        }
    }

    /** The owner's override for {@code material}, or {@code null} if they did not set one. */
    public Double override(Material material) {
        return material == null ? null : overrides.get(material);
    }

    /** The value for a block that neither a behavior, this file, nor the built-in table classified. */
    public double defaultValue() {
        return configuredDefault;
    }

    private static Material materialOf(String key) {
        String name = key.trim();
        int colon = name.indexOf(':');
        if (colon >= 0) {
            name = name.substring(colon + 1); // accept the minecraft: spelling used by every other config
        }
        return Material.matchMaterial(name.toUpperCase(java.util.Locale.ROOT));
    }
}
