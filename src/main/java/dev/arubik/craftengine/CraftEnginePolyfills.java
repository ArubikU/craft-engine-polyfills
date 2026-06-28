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
        // Boot-time solver self-test (logs pass/fail for the 50+ edge-case suite).
        try {
            dev.arubik.craftengine.fluid.graph.FluidSolverTests.Out o = dev.arubik.craftengine.fluid.graph.FluidSolverTests
                    .run();
            getLogger().info("[FluidSolver] tests: " + o.passed + " passed, " + o.failed + " failed"
                    + (o.failures.isEmpty() ? "" : " -> " + o.failures));
        } catch (Throwable t) {
            getLogger().warning("[FluidSolver] test run failed: " + t);
        }
        // Hydraulic engine driver (Phase 6): steps every registered fluid network each tick. No-op while
        // FluidEngine.ENABLED is false (default), so the live per-block transport runs until toggled.
        getServer().getScheduler().runTaskTimer(this, () -> {
            if (!dev.arubik.craftengine.fluid.graph.FluidEngine.ENABLED)
                return;
            for (org.bukkit.World w : getServer().getWorlds()) {
                try {
                    dev.arubik.craftengine.fluid.graph.FluidEngine
                            .tickAll(((org.bukkit.craftbukkit.CraftWorld) w).getHandle());
                } catch (Throwable ignored) {
                }
            }
        }, 1L, 1L);
        dev.arubik.craftengine.block.behavior.CrafterSlotStateListener.register();
        CustomBlockData.registerListener(this);
        BlockContainer.ensureListenerRegistered(this);
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.machine.menu.MachineMenuListener(),
                this);
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.crafting.CraftingTableListener(),
                this);
        dev.arubik.craftengine.conveyor.ConveyorWandListener conveyorWand =
                new dev.arubik.craftengine.conveyor.ConveyorWandListener();
        getServer().getPluginManager().registerEvents(conveyorWand, this);
        conveyorWand.start(this); // live aim-tracking preview task
        dev.arubik.craftengine.pipe.PipeWandListener pipeWand =
                new dev.arubik.craftengine.pipe.PipeWandListener();
        getServer().getPluginManager().registerEvents(pipeWand, this);
        pipeWand.start(this); // live aim-tracking preview task (MAGIC mode)
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.conveyor.FunnelPlaceListener(),
                this);
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.multiblock.HammerAssembleListener(),
                this);
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.conveyor.ConveyorBreakListener(),
                this);
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.conveyor.ConveyorIoBreakListener(),
                this);
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.rotation.GasMotorBreakListener(),
                this);
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.machine.block.MachineBreakListener(),
                this);
        // upgrade_scrapped/upgrade_copper are loot-only: inject them into vanilla chest loot.
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.loot.UpgradeLootListener(),
                this);
        // cepolyfill command
        // sub command data get <block_pos>
        CepCommand cepCommand = new CepCommand();
        getCommand("cepolyfill").setExecutor(cepCommand);
        getCommand("cepolyfill").setTabCompleter(cepCommand);
        // Initial load of the central machine-menu title-image config (also refreshed on each
        // CraftEngineReloadEvent below). Images resolve lazily per-open, so loading the id map here is fine.
        dev.arubik.craftengine.machine.menu.GuiTitles.reload();
        getLogger().info("CraftEngine Polyfills Enabled");

        // NOTE: don't load recipes here — onEnable runs BEFORE CraftEngine registers its
        // custom items, so custom-item outputs (e.g. cml:aluminum_scraping) resolve null.
        // The CraftEngineReloadEvent listener below loads them once (incl. the first load),
        // after CE's items are ready.

        // Reload machine + workbench recipes whenever CraftEngine reloads
        // (so `/craftengine reload all` also refreshes the JSON-defined recipes).
        getServer().getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onReload(net.momirealms.craftengine.bukkit.api.event.CraftEngineReloadEvent event) {
                // Reload AFTER CraftEngine has (re)loaded its items — including the first
                // load — so custom-item recipe outputs resolve (onEnable runs too early).
                dev.arubik.craftengine.machine.recipe.loader.RecipeManager.loadRecipes();
                dev.arubik.craftengine.crafting.StationRecipeLoader.load();
                // Reload the central machine-menu title-image config (so /craftengine reload all
                // re-reads polyfills_gui.yml). The CraftEngineReloadEvent also fires the initial load.
                dev.arubik.craftengine.machine.menu.GuiTitles.reload();
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
