package dev.arubik.craftengine;

import java.util.Properties;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.retrooper.packetevents.PacketEvents;

import dev.arubik.craftengine.block.BlockBehaviors;
import dev.arubik.craftengine.item.ItemBehaviors;
import dev.arubik.craftengine.item.ItemListener;
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
        // The vanilla block tables the physics reads: how much a block IS (mass) and how a fluid pushes
        // it (floatability). Both are owner-editable overrides layered over a built-in family table —
        // see BlockPropertyTable for the lookup order. Loaded before anything can capture a contraption.
        dev.arubik.craftengine.contraption.behavior.WeightBlockBehavior.loadTable();
        dev.arubik.craftengine.contraption.physics.FloatabilityTable.load();
        dev.arubik.craftengine.contraption.physics.FrictionTable.load();
        dev.arubik.craftengine.contraption.physics.RestitutionTable.load();
        // Restore the loose world glue graph persisted at last shutdown (2026-07-03 — "has que
        // las glue persista al apagar o reiniciar el sv"). Assembled contraptions carry their own
        // glue in their structure NBT; this is the unassembled real-world glue.
        try {
            dev.arubik.craftengine.contraption.GlueRegistry.loadAll(getDataFolder().toPath().resolve("glue.dat"));
        } catch (Throwable t) {
            getLogger().warning("[Contraption] failed to load persisted glue graph: " + t);
        }
        // Boot-scan persisted block-anchored (LINEAR/ROTATIONAL) contraptions into an in-memory
        // index (2026-07-03 — restart persistence, the block-anchored analog of the minecart's
        // entity-PDC). NOT rehydrated immediately: the target world/chunk may not be loaded yet —
        // ContraptionChunkLifecycleListener#onChunkLoad rehydrates each as its bearing's chunk
        // loads, exactly how the minecart rehydrates via natural entity chunk-load.
        // Wipe any leftover contraption-level scaffolding folders in temp (a crash skips their per-dispose
        // cleanup). Live contraptions rehydrate from NBT below, never from these — see ContraptionLevel.
        dev.arubik.craftengine.contraption.level.BukkitContraptionLevel.wipeStorageRoot();
        try {
            dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore.loadIndex();
        } catch (Throwable t) {
            getLogger().warning("[Contraption] failed to index persisted block-anchored contraptions: " + t);
        }
        // Euler/robin_euler extended-solid piston bearings (dropped their load, awaiting a redstone
        // pulse / dwell timer to re-grab it) — restore so the trigger survives restart.
        try {
            dev.arubik.craftengine.contraption.EulerExtendedRegistry.loadAll(getDataFolder().toPath().resolve("euler.dat"));
        } catch (Throwable t) {
            getLogger().warning("[Contraption] failed to load euler-extended bearings: " + t);
        }
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
                net.minecraft.world.level.Level lvl = ((org.bukkit.craftbukkit.CraftWorld) w).getHandle();
                try {
                    dev.arubik.craftengine.fluid.graph.FluidEngine.tickAll(lvl);
                } catch (Throwable ignored) {
                }
                try {
                    dev.arubik.craftengine.fluid.graph.GasEngine.tickAll(lvl);
                } catch (Throwable ignored) {
                }
            }
        }, 1L, 3L); // every 3 ticks — fluid/gas equalize fine at ~7Hz, and the per-tick BFS rebuild is costly
        // Contraption master clock (CONTRAPTIONS.md §5 Phase 3): every registered contraption's
        // behaviors + stall gate + render, once per tick — mirrors the fluid driver above.
        getServer().getScheduler().runTaskTimer(this, dev.arubik.craftengine.contraption.ContraptionEngine::tickAll, 1L,
                1L);
        // Contraption chunk lifecycle (CONTRAPTIONS.md Phase 6): anchor-keyed (not
        // current-position-keyed) load/unload wiring — see ContraptionChunkLifecycleListener's
        // javadoc for why this replaced the old ContraptionPersistence/ContraptionChunkListener.
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.contraption.ContraptionChunkLifecycleListener(),
                this);
        // Bearing hammer-trigger assemble/disassemble (CONTRAPTIONS.md §5 Phase 6).
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.contraption.BearingHammerListener(),
                this);
        // Minecart-bearing data-loss guard (belt-and-suspenders on top of setInvulnerable —
        // see MinecartBearing.DamageGuard's own javadoc): cancels VehicleDamageEvent/
        // VehicleDestroyEvent for any bearing minecart so the only way to remove one is the
        // explicit hammer-driven MinecartBearing#disassemble flow.
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.contraption.MinecartBearing.DamageGuard(),
                this);
        // Happy-ghast harness contraption: equipping a harness on an adult ghast assembles a contraption
        // pre-filled with a hollow 4x4x4 shell of the harness's wool colour; removing it with shears hands
        // the whole structure back inside the harness item (like the minecart's save-to-item). See
        // GhastHarnessListener's javadoc for why EntityEquipmentChangedEvent is the detection hook and why
        // the item still has to be stamped from the interact event.
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.contraption.GhastHarnessListener(),
                this);
        // Glue wand item tool (CONTRAPTIONS.md §1): WorldEdit-style two-corner AREA glue with
        // cml:slime_glue — right-click pos1, right-click pos2 elsewhere to instantly glue the
        // whole axis-aligned box between them into one structure (sneak = cancel pending pos1);
        // 192-use durability, breaks when spent; passive particle indicator shows already-glued
        // faces near the crosshair whenever the wand is held. Replaces the old /cep contraption
        // glue command.
        dev.arubik.craftengine.contraption.GlueWandListener glueWand =
                new dev.arubik.craftengine.contraption.GlueWandListener();
        getServer().getPluginManager().registerEvents(glueWand, this);
        glueWand.start(this);
        // Task 2 (CONTRAPTIONS.md 2026-07-01 session): right-click raycast routing into a
        // contraption's real ContraptionLevel blocks; left-click is an explicit no-op.
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.contraption.ContraptionInteractionListener(),
                this);
        // Creative Phys Wand (roadmap item #9 — cml:creative_phys_wand): creative-only tool to GRAB a
        // contraption and drag it by the crosshair (reusing ContraptionEntity#teleport) and live-resize
        // it (ContraptionEntity#setScale). Owns a 1-tick drag task started via #start below (self-cancels
        // per-grab when a grabber logs off / leaves creative / puts the wand away / the contraption dies).
        dev.arubik.craftengine.contraption.CreativePhysWandListener physWand =
                new dev.arubik.craftengine.contraption.CreativePhysWandListener();
        getServer().getPluginManager().registerEvents(physWand, this);
        physWand.start(this);
        // A TNT cell lit inside ANY contraption is ejected as a real PrimedTnt into the real world at
        // that cell's live position, carrying the contraption's velocity there — otherwise it would
        // prime, tick and detonate inside a hidden dimension nobody can see. Catches every ignition
        // path at once by hooking the one addFreshEntity every TntBlock#prime funnels into; see the
        // listener's javadoc.
        getServer().getPluginManager().registerEvents(
                new dev.arubik.craftengine.contraption.explosive.ContraptionTntEjectListener(), this);
        // Wakes sleeping phys bodies when the world under them changes (a sleeping body is skipped by
        // the solver, so without this it hangs in mid-air after its support is mined out), and lets
        // explosions throw phys contraptions — vanilla's Explosion cannot see them, since they are not
        // real entities. See the listener's javadoc.
        getServer().getPluginManager().registerEvents(
                new dev.arubik.craftengine.contraption.physics.PhysicsWorldListener(), this);
        // Furniture-seat completion: sit down (right-click a free seat slot, runs BEFORE the
        // block-cell listener above) / stand up (sneak) after a contraption has been assembled.
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.contraption.ContraptionSeatListener(),
                this);
        // Dropped-item bridge (2026-07-01 session): a real player picking up one of
        // ContraptionItemPickupSwarm's real-world mirror ItemEntitys also discards the matching
        // internal item still sitting inside the owning ContraptionLevel — see that swarm's javadoc.
        getServer().getPluginManager().registerEvents(new dev.arubik.craftengine.contraption.render.ContraptionItemPickupListener(),
                this);
        dev.arubik.craftengine.block.behavior.CrafterSlotStateListener.register();
        // TEMPORARY diagnostic (2026-07-01 debugging session) — see class javadoc.
        dev.arubik.craftengine.contraption.ContraptionInteractPacketDebug.register();
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
        // Safety net (2026-07-01 session — "al cerrar el sv con el contraption esas entidades
        // se guarden tmb"): a ContraptionLevel's real entities (any mob that wandered in) are
        // NOT persisted by anything — the mini-dimension itself isn't saved/reloaded across a
        // restart (a separate, larger, explicitly-deferred persistence project). Without this,
        // every entity still living inside an active contraption at shutdown would simply cease
        // to exist. Only flushes entities into the real world at their current mirrored
        // position — vanilla's own save-on-shutdown then covers them like any other real entity.
        try {
            for (dev.arubik.craftengine.contraption.ContraptionEntity entity : dev.arubik.craftengine.contraption.ContraptionManager
                    .all()) {
                dev.arubik.craftengine.contraption.level.ContraptionLevel level = entity.state().level();
                if (level != null) {
                    level.transferRemainingEntitiesToRealWorld();
                }
            }
        } catch (Throwable t) {
            getLogger().warning("Failed to flush contraption entities on shutdown: " + t);
        }
        // Re-dump every currently-live BLOCK-ANCHORED contraption's CURRENT structure to disk
        // (2026-07-03 — restart persistence). A contraption is block-anchored iff its id is in the
        // assembled-anchor map (the minecart type isn't — it persists via its entity PDC, saved by
        // vanilla). This mirrors the minecart's structure being re-saved on unload; onDisable is
        // the shutdown equivalent since chunks aren't individually unloaded on a clean stop.
        try {
            var anchors = dev.arubik.craftengine.contraption.BearingHammerListener.assembledAnchorsSnapshot();
            for (dev.arubik.craftengine.contraption.ContraptionEntity entity : dev.arubik.craftengine.contraption.ContraptionManager
                    .all()) {
                var anchor = anchors.get(entity.state().id());
                if (anchor == null) {
                    continue; // not block-anchored (e.g. minecart) — handled elsewhere
                }
                org.bukkit.World world = getServer().getWorld(anchor.worldId());
                if (world == null) {
                    continue;
                }
                net.minecraft.world.level.Level realLevel = ((org.bukkit.craftbukkit.CraftWorld) world).getHandle();
                dev.arubik.craftengine.contraption.BearingType type =
                        dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore
                                .typeToPersist(entity.state(), realLevel, anchor.pos());
                double rpm = dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.rpmAt(realLevel,
                        anchor.pos());
                double su = dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.suPerBlockAt(realLevel,
                        anchor.pos());
                dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore.save(entity.state(),
                        anchor.pos(), type, rpm, su);
            }
        } catch (Throwable t) {
            getLogger().warning("[Contraption] failed to save block-anchored contraptions on shutdown: " + t);
        }
        // Persist the loose world glue graph so glued-but-unassembled structures keep their glue
        // across a restart (2026-07-03). Assembled contraptions persist their own glue separately.
        try {
            dev.arubik.craftengine.contraption.GlueRegistry.saveAll(getDataFolder().toPath().resolve("glue.dat"));
        } catch (Throwable t) {
            getLogger().warning("[Contraption] failed to save glue graph on shutdown: " + t);
        }
        // Euler/robin_euler extended-solid bearings — persist so their redstone/timer trigger survives.
        try {
            dev.arubik.craftengine.contraption.EulerExtendedRegistry.saveAll(getDataFolder().toPath().resolve("euler.dat"));
        } catch (Throwable t) {
            getLogger().warning("[Contraption] failed to save euler-extended bearings: " + t);
        }
        // Stop the island-solve workers. Daemon threads would not hold the JVM open anyway, but a
        // reload leaves the old plugin's pool running against classes that are about to be replaced.
        try {
            dev.arubik.craftengine.contraption.physics.PhysicsWorld.shutdown();
        } catch (Throwable t) {
            getLogger().warning("[Contraption] failed to stop physics workers: " + t);
        }
        // Cached world wrappers hold a reference to worlds that a reload replaces.
        dev.arubik.craftengine.util.CeWorlds.clear();
        PacketEvents.getAPI().terminate();
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
