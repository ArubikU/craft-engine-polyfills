package dev.arubik.craftengine.contraption.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class ContraptionConfig {

    private static ContraptionConfig INSTANCE = new ContraptionConfig();

    private String itemFrameModel = "";

    private ContraptionConfig() {}

    public static ContraptionConfig get() {
        return INSTANCE;
    }

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
        INSTANCE.itemFrameModel = cfg.getString("item_frame.model", "").trim();
    }

    /** CE item ID to use as the item frame model, or empty string for vanilla ITEM_FRAME entity. */
    public String itemFrameModel() {
        return itemFrameModel;
    }

    public boolean useCustomItemFrame() {
        return !itemFrameModel.isEmpty();
    }
}
