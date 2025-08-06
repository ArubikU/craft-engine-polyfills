package dev.arubik.craftengine;

import org.bukkit.plugin.java.JavaPlugin;

import dev.arubik.craftengine.block.BlockBehaviors;

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
}
