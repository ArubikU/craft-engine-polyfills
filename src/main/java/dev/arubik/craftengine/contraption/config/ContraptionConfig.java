package dev.arubik.craftengine.contraption.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class ContraptionConfig {

    private static ContraptionConfig INSTANCE = new ContraptionConfig();

    private String itemFrameModel = "";
    private String waterFluidType = "";
    private String lavaFluidType = "";
    private double lavaDamage = 4.0;

    private ContraptionConfig() {}

    public static ContraptionConfig get() { return INSTANCE; }

    public static void load(File dataFolder, ClassLoader classLoader) {
        INSTANCE = new ContraptionConfig();
        File file = new File(dataFolder, "contraptions.yml");
        YamlConfiguration cfg;
        if (file.exists()) {
            cfg = YamlConfiguration.loadConfiguration(file);
        } else {
            try (InputStream is = classLoader.getResourceAsStream("contraptions.yml")) {
                cfg = is != null
                        ? YamlConfiguration.loadConfiguration(new InputStreamReader(is, StandardCharsets.UTF_8))
                        : new YamlConfiguration();
            } catch (Throwable t) {
                cfg = new YamlConfiguration();
            }
        }
        INSTANCE.itemFrameModel   = cfg.getString("item_frame.model", "").trim();
        INSTANCE.waterFluidType   = cfg.getString("fluid_render.water_fluid_type", "").trim();
        INSTANCE.lavaFluidType    = cfg.getString("fluid_render.lava_fluid_type", "").trim();
        INSTANCE.lavaDamage       = cfg.getDouble("fluid_render.lava_damage", 4.0);
    }

    public String itemFrameModel() { return itemFrameModel; }
    public boolean useCustomItemFrame() { return !itemFrameModel.isEmpty(); }

    /** CE fluid type ID for vanilla water rendering, or empty = disabled. */
    public String waterFluidType() { return waterFluidType; }
    public boolean renderWater() { return !waterFluidType.isEmpty(); }

    /** CE fluid type ID for vanilla lava rendering, or empty = disabled. */
    public String lavaFluidType() { return lavaFluidType; }
    public boolean renderLava() { return !lavaFluidType.isEmpty(); }

    /** Lava damage per second to entities inside a contraption's lava cells. */
    public double lavaDamage() { return lavaDamage; }
}
