package dev.arubik.craftengine;

import java.util.Properties;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.retrooper.packetevents.PacketEvents;

import dev.arubik.craftengine.block.BlockBehaviors;
import dev.arubik.craftengine.item.ItemBehaviors;
import dev.arubik.craftengine.item.ItemListener;
import dev.arubik.craftengine.util.BlockContainer;
import dev.arubik.craftengine.util.CustomBlockData;
import dev.arubik.craftengine.util.DataHolders;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.momirealms.craftengine.core.plugin.CraftEngine;

public final class CraftEnginePolyfills extends JavaPlugin {
    private static CraftEnginePolyfills instance;

    @Override
    public void onLoad() {
        instance = this;
        dev.arubik.craftengine.property.Properties.register();
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
        initPlugin();
        getLogger().info("CraftEngine Polyfills Loaded");
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        ItemListener.register(this);
        CustomBlockData.registerListener(this);
        BlockContainer.ensureListenerRegistered(this);
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.machine.menu.MachineMenuListener(),
                this);
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.crafting.CraftingTableListener(),
                this);
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.conveyor.ConveyorWandListener(),
                this);
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.conveyor.ConveyorBreakListener(),
                this);
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.conveyor.ConveyorIoBreakListener(),
                this);
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.rotation.AdvancedMotorBreakListener(),
                this);
        // cepolyfill command
        // sub command data get <block_pos>
        CepCommand cepCommand = new CepCommand();
        getCommand("cepolyfill").setExecutor(cepCommand);
        getCommand("cepolyfill").setTabCompleter(cepCommand);
        getLogger().info("CraftEngine Polyfills Enabled");

        // Load Recipes
        dev.arubik.craftengine.machine.recipe.loader.RecipeManager.loadRecipes();

        // Reload machine + workbench recipes whenever CraftEngine reloads
        // (so `/craftengine reload all` also refreshes the JSON-defined recipes).
        getServer().getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onReload(net.momirealms.craftengine.bukkit.api.event.CraftEngineReloadEvent event) {
                if (event.isFirstReload()) {
                    return; // initial load already done above
                }
                dev.arubik.craftengine.machine.recipe.loader.RecipeManager.loadRecipes();
                dev.arubik.craftengine.crafting.StationRecipeLoader.load();
            }
        }, this);
    }

    /** Copy a bundled resource (jar) to the data folder if absent. */
    public void saveDefaultResource(String path) {
        try {
            saveResource(path, false);
        } catch (IllegalArgumentException ignored) {
            // resource not present in the jar
        }
    }

    /** List bundled resource paths under {@code dir/} (e.g. "workbench_recipes") ending in {@code suffix}. */
    public java.util.List<String> listBundledResources(String dir, String suffix) {
        java.util.List<String> out = new java.util.ArrayList<>();
        try (java.util.zip.ZipFile zip = new java.util.zip.ZipFile(getFile())) {
            String prefix = dir.endsWith("/") ? dir : dir + "/";
            var entries = zip.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(prefix) && name.endsWith(suffix) && !name.endsWith("/")) {
                    out.add(name);
                }
            }
        } catch (Exception e) {
            getLogger().warning("Could not list bundled resources in " + dir + ": " + e.getMessage());
        }
        return out;
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        DataHolders.INSTANCE.removeHolders();
        getLogger().info("CraftEngine Polyfills Disabled");
    }

    private void initPlugin() {
        BlockBehaviors.register();
        ItemBehaviors.register();
    }

    public static CraftEnginePolyfills instance() {
        return instance;
    }

    public static void log(String message) {
        CraftEngine.instance().logger().info(message);
    }
}
