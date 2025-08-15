package dev.arubik.craftengine;

import org.bukkit.plugin.java.JavaPlugin;

import dev.arubik.craftengine.block.BlockBehaviors;
import net.momirealms.craftengine.core.plugin.CraftEngine;

public final class CraftEnginePolyfills extends JavaPlugin {
    private static CraftEnginePolyfills instance;

    @Override
    public void onLoad() {
        instance = this;
        initPlugin();
        getLogger().info("CraftEngine Polyfills Loaded");
    }

    @Override
    public void onDisable() {
        getLogger().info("CraftEngine Polyfills Disabled");
    }

    private void initPlugin() {
        BlockBehaviors.register();
    }

    public static CraftEnginePolyfills instance() {
        return instance;
    }

    public static void log(String message) {
        CraftEngine.instance().logger().info(message);
    }
}
