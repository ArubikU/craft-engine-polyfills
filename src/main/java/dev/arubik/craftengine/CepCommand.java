package dev.arubik.craftengine;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.util.ArgumentList;
import dev.arubik.craftengine.util.ArgumentList.XAxisCoordinate;
import dev.arubik.craftengine.util.ArgumentList.YAxisCoordinate;
import dev.arubik.craftengine.util.ArgumentList.ZAxisCoordinate;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.BiFunction;

public class CepCommand implements CommandExecutor, TabCompleter {

    private final Map<ArgumentList, BiFunction<CommandSender, Object[], Boolean>> cases = new HashMap<>();

    public CepCommand() {
        // Ejemplo: /cepolyfill data get <string> <int>
        cases.put(new ArgumentList("data^","get^", XAxisCoordinate.class,YAxisCoordinate.class,ZAxisCoordinate.class), (sender, parsed) -> {

            int x = (Integer) parsed[2];
            int y = (Integer) parsed[3];
            int z = (Integer) parsed[4];
            if (sender instanceof Player player){
                Level level = ((CraftWorld) player.getWorld()).getHandle();
                BlockPos pos = new BlockPos(x, y, z);
                PersistentBlockEntity be = PersistentBlockEntity.getIfLoaded(level, pos);
                JsonObject json = new JsonObject();
                if (be != null) {
                    for (var entry : be.debugDump().entrySet()) {
                        json.addProperty(entry.getKey(), entry.getValue());
                    }
                }
                sender.sendMessage(MiniMessage.miniMessage().deserialize(JsonFormatter.toMiniMessage(json)));
            }
            return true;
        });

        // /cep fluid graph — Phase 1 debug: dump the fluid network graph for the block you're looking at.
        cases.put(new ArgumentList("fluid^", "graph^"), (sender, parsed) -> {
            if (sender instanceof Player player) {
                Block tb = player.getTargetBlockExact(8);
                if (tb == null) {
                    sender.sendMessage("Look at a fluid block (pipe/tank/pump).");
                    return true;
                }
                net.minecraft.world.level.Level level = ((org.bukkit.craftbukkit.CraftWorld) tb.getWorld()).getHandle();
                net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos(tb.getX(), tb.getY(), tb.getZ());
                dev.arubik.craftengine.fluid.graph.FluidGraph g = dev.arubik.craftengine.fluid.graph.FluidGraphBuilder
                        .build(level, pos);
                sender.sendMessage("§bFluidGraph§7: §f" + g.nodes.size() + "§7 nodes, §f" + g.edges.size()
                        + "§7 edges");
                int shown = 0;
                for (dev.arubik.craftengine.fluid.graph.FluidNode n : g.nodes) {
                    if (shown++ >= 16) {
                        sender.sendMessage("§7 ...(" + (g.nodes.size() - 16) + " more nodes)");
                        break;
                    }
                    sender.sendMessage("§7 • §f" + n);
                }
                shown = 0;
                for (dev.arubik.craftengine.fluid.graph.FluidEdge e : g.edges) {
                    if (shown++ >= 16) {
                        sender.sendMessage("§7 ...(" + (g.edges.size() - 16) + " more edges)");
                        break;
                    }
                    sender.sendMessage("§8 ─ §7" + e);
                }
            }
            return true;
        });

        // /cep fluid solvetest — Phase 2 self-test of the hydraulic solver (no world side effects).
        cases.put(new ArgumentList("fluid^", "solvetest^"), (sender, parsed) -> {
            StringBuilder sb = new StringBuilder("§bFluidNetworkSolver self-test:\n");
            // 1) Two tanks (head 10 / 0) should equalize over steps; mass conserved each step.
            {
                var nodes = new dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.NodeSpec[] {
                        new dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.NodeSpec(1000, 10),
                        new dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.NodeSpec(1000, 0) };
                var br = new dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.BranchSpec[] {
                        new dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.BranchSpec(0, 1, 100, 0, 0) };
                var r = dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.solve(nodes, br, 1.0);
                boolean conserved = Math.abs(r.netInflow[0] + r.netInflow[1]) < 1e-6;
                boolean toward = r.heads[0] < 10.0 - 1e-9 && r.heads[1] > 1e-9 && r.flows[0] > 0;
                sb.append(conserved && toward ? "§a" : "§c").append(" [equalize] conserved=").append(conserved)
                        .append(" toward=").append(toward).append(" h=[").append(fmt(r.heads[0])).append(",")
                        .append(fmt(r.heads[1])).append("] q=").append(fmt(r.flows[0])).append("\n");
            }
            // 2) Check valve (a→b only): with head pushing b→a, flow must be blocked (branch deactivated).
            {
                var nodes = new dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.NodeSpec[] {
                        new dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.NodeSpec(1000, 0),
                        new dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.NodeSpec(1000, 10) };
                var br = new dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.BranchSpec[] {
                        new dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.BranchSpec(0, 1, 100, 0, +1) };
                var r = dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.solve(nodes, br, 1.0);
                boolean blocked = !r.active[0] && Math.abs(r.flows[0]) < 1e-6;
                sb.append(blocked ? "§a" : "§c").append(" [check-valve] blocked backflow=").append(blocked)
                        .append("\n");
            }
            // 3) Pump emf lifts b above a (a→b with positive emf raises b's head over steps).
            {
                var nodes = new dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.NodeSpec[] {
                        new dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.NodeSpec(1000, 0),
                        new dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.NodeSpec(1000, 0) };
                var br = new dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.BranchSpec[] {
                        new dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.BranchSpec(0, 1, 100, 5, 0) };
                var r = dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.solve(nodes, br, 1.0);
                boolean lifts = r.flows[0] > 0 && r.heads[1] > r.heads[0];
                sb.append(lifts ? "§a" : "§c").append(" [pump] lifts a→b=").append(lifts).append(" h=[")
                        .append(fmt(r.heads[0])).append(",").append(fmt(r.heads[1])).append("]\n");
            }
            sender.sendMessage(sb.toString());
            return true;
        });

        // /cep fluid step — Phase 3 apply demo: build the graph for the looked-at network, run ONE solver
        // step, and write the resulting per-node volume change back into the real stores. Manual + targeted
        // (the always-on per-tick engine + dirty-tracking is the remaining wiring).
        cases.put(new ArgumentList("fluid^", "step^"), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            Block tb = player.getTargetBlockExact(8);
            if (tb == null) {
                sender.sendMessage("Look at a fluid block.");
                return true;
            }
            net.minecraft.world.level.Level level = ((org.bukkit.craftbukkit.CraftWorld) tb.getWorld()).getHandle();
            net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos(tb.getX(), tb.getY(), tb.getZ());
            int moved = dev.arubik.craftengine.fluid.graph.FluidEngine.step(level,
                    dev.arubik.craftengine.fluid.graph.FluidGraphBuilder.build(level, pos));
            sender.sendMessage("§bfluid step§7: applied, §f" + moved + "§7 mB moved across the network");
            return true;
        });

        // /cep gas step — Phase 5: gas network solved with no gravity (head = fill only).
        cases.put(new ArgumentList("gas^", "step^"), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            Block tb = player.getTargetBlockExact(8);
            if (tb == null) {
                sender.sendMessage("Look at a gas block.");
                return true;
            }
            net.minecraft.world.level.Level level = ((org.bukkit.craftbukkit.CraftWorld) tb.getWorld()).getHandle();
            net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos(tb.getX(), tb.getY(), tb.getZ());
            int moved = dev.arubik.craftengine.fluid.graph.GasEngine.step(level, pos);
            sender.sendMessage("§bgas step§7: applied, §f" + moved + "§7 units moved (no gravity)");
            return true;
        });

        // /cep fluid engine on — turn the always-on hydraulic engine ON and register the looked-at network.
        cases.put(new ArgumentList("fluid^", "engine^", "on^"), (sender, parsed) -> {
            if (sender instanceof Player player) {
                Block tb = player.getTargetBlockExact(8);
                if (tb != null) {
                    net.minecraft.world.level.Level lvl = ((org.bukkit.craftbukkit.CraftWorld) tb.getWorld())
                            .getHandle();
                    net.minecraft.core.BlockPos p = new net.minecraft.core.BlockPos(tb.getX(), tb.getY(), tb.getZ());
                    dev.arubik.craftengine.fluid.graph.FluidGraph g = dev.arubik.craftengine.fluid.graph.FluidGraphBuilder
                            .build(lvl, p);
                    for (dev.arubik.craftengine.fluid.graph.FluidNode node : g.nodes)
                        dev.arubik.craftengine.fluid.graph.FluidEngine.registerSeed(node.pos);
                    sender.sendMessage("§aregistered §f" + g.nodes.size() + "§a-node network");
                }
            }
            dev.arubik.craftengine.fluid.graph.FluidEngine.ENABLED = true;
            sender.sendMessage("§aFluidEngine ON§7 (old per-block transport suspended). seeds="
                    + dev.arubik.craftengine.fluid.graph.FluidEngine.seedCount());
            return true;
        });

        // /cep fluid test — run the 50+ solver edge-case suite.
        cases.put(new ArgumentList("fluid^", "test^"), (sender, parsed) -> {
            dev.arubik.craftengine.fluid.graph.FluidSolverTests.Out o = dev.arubik.craftengine.fluid.graph.FluidSolverTests
                    .run();
            sender.sendMessage("§bSolver tests§7: §a" + o.passed + " passed§7, "
                    + (o.failed == 0 ? "§a0 failed" : "§c" + o.failed + " failed"));
            for (String f : o.failures)
                sender.sendMessage("§c ✗ " + f);
            return true;
        });

        // /cep fluid engine off — back to the live per-block transport.
        cases.put(new ArgumentList("fluid^", "engine^", "off^"), (sender, parsed) -> {
            dev.arubik.craftengine.fluid.graph.FluidEngine.ENABLED = false;
            sender.sendMessage("§cFluidEngine OFF§7 (per-block transport restored).");
            return true;
        });

        // /cep contraption spike-swarm <n> <item|shulker> — Phase 0 spike #1: packet-swarm
        // entity budget + per-tick reposition cost at 20Hz (see CONTRAPTIONS.md §4).
        cases.put(new ArgumentList("contraption^", "spike-swarm^", Integer.class, String.class), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            int n = (Integer) parsed[2];
            String kind = (String) parsed[3];
            dev.arubik.craftengine.contraption.SwarmSpike.start(CraftEnginePolyfills.instance(), player, n, kind);
            return true;
        });

        // /cep show placement — logs what each in-contraption placement resolved to (contraption yaw,
        // player yaw, the yaw it was placed with, and the resulting blockstate), for diagnosing an
        // orientation report that cannot be reproduced without a client.
        cases.put(new ArgumentList("show^", "placement^"), (sender, parsed) -> {
            boolean on = !dev.arubik.craftengine.contraption.ContraptionInteractionListener.PLACEMENT_DEBUG;
            dev.arubik.craftengine.contraption.ContraptionInteractionListener.PLACEMENT_DEBUG = on;
            sender.sendMessage(MiniMessage.miniMessage().deserialize(on
                    ? "<green>Placement logging ON — place a block in a contraption, then check the console."
                    : "<gray>Placement logging off."));
            return true;
        });

        // /cep show shulkers — drop the collider shulkers' invisible flag so the distance LOD tiers
        // can be seen directly. Already-spawned colliders are re-sent, so it applies live.
        cases.put(new ArgumentList("show^", "shulkers^"), (sender, parsed) -> {
            boolean shown = dev.arubik.craftengine.contraption.render.ContraptionShulkerColliderSwarm
                    .toggleShowColliders();
            sender.sendMessage(MiniMessage.miniMessage().deserialize(shown
                    ? "<green>Collider shulkers are now VISIBLE — walk toward a contraption to watch the LOD tiers (1 -> 4 -> 16 cubes per cell)."
                    : "<gray>Collider shulkers are hidden again."));
            return true;
        });

        // /cep contraption spawn <blockstate> — DEBUG: spawn a one-cell phys contraption of the given
        // block in front of the player, so phys/LOD/collision can be exercised without hand-building
        // and gluing a structure first. Accepts vanilla blockstate syntax
        // (minecraft:oak_stairs[facing=north,half=bottom]).
        cases.put(new ArgumentList("contraption^", "spawn^", String.class), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            String input = (String) parsed[2];
            net.minecraft.world.level.block.state.BlockState state;
            try {
                state = dev.arubik.craftengine.contraption.DebugPhysSpawn.parseBlockState(input);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cNot a blockstate: §f" + input + "§c — try minecraft:iron_block or"
                        + " minecraft:oak_stairs[facing=north,half=bottom].");
                return true;
            }
            spawnDebugPhys(player, Map.of(BlockPos.ZERO, state), null, input);
            return true;
        });

        // /cep contraption spawn structure <namespacedKey> — DEBUG: same, but built from a vanilla
        // STRUCTURE TEMPLATE (a structure-block .nbt, e.g. minecraft:igloo/top) so a many-cell body can
        // be dropped in one command.
        cases.put(new ArgumentList("contraption^", "spawn^", "structure^", String.class), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            String id = (String) parsed[3];
            net.minecraft.server.level.ServerLevel level = ((CraftWorld) player.getWorld()).getHandle();
            var cells = dev.arubik.craftengine.contraption.DebugPhysSpawn.structureCells(level, id);
            if (cells.isEmpty() || cells.get().isEmpty()) {
                sender.sendMessage("§cNo structure template §f" + id + "§c (or it holds no solid blocks)."
                        + " Try minecraft:igloo/top.");
                return true;
            }
            Map<BlockPos, net.minecraft.world.level.block.state.BlockState> states = new HashMap<>();
            Map<BlockPos, net.minecraft.nbt.CompoundTag> blockEntities = new HashMap<>();
            cells.get().forEach((local, info) -> {
                states.put(local, info.state());
                if (info.nbt() != null) {
                    blockEntities.put(local, info.nbt());
                }
            });
            spawnDebugPhys(player, states, blockEntities, id);
            return true;
        });

        // /cep contraption killall — destroy every live contraption on the server, in every world.
        // DISCARDS them: the captured blocks are NOT restored to the world (that's what disassemble is
        // for) and the persisted records are deleted so nothing comes back on reload — see ContraptionKill
        // for the full contract. Admin panic button for "contraptions are eating the tick budget".
        cases.put(new ArgumentList("contraption^", "killall^"), (sender, parsed) -> {
            if (!sender.hasPermission("cep.contraption.debug")) {
                sender.sendMessage("§cYou don't have permission to kill contraptions.");
                return true;
            }
            int live = dev.arubik.craftengine.contraption.ContraptionManager.count();
            if (live == 0) {
                sender.sendMessage("§7No live contraptions to kill.");
                return true;
            }
            int killed = dev.arubik.craftengine.contraption.ContraptionKill.killAll();
            sender.sendMessage("§cKilled §f" + killed + "§c contraption(s).§7 Their captured blocks were"
                    + " DISCARDED (not restored to the world) and their saved records deleted — nothing"
                    + " will come back on chunk reload or restart.");
            CraftEnginePolyfills.instance().getLogger()
                    .info("[contraption] killall by " + sender.getName() + " — " + killed + " destroyed");
            return true;
        });

        // /cep contraption perf — per-phase timings + counts for the master tick loop. Instrumentation is
        // off until this is first run (it costs a nanoTime pair per phase per tick), so the first call
        // arms it and reports once the rolling window has samples.
        cases.put(new ArgumentList("contraption^", "perf^"), (sender, parsed) -> {
            if (!dev.arubik.craftengine.contraption.ContraptionPerf.enabled()) {
                dev.arubik.craftengine.contraption.ContraptionPerf.setEnabled(true);
                sender.sendMessage("§bContraption profiling ARMED§7 — run §f/cep contraption perf§7 again in"
                        + " a few seconds to read the rolling average. §8/cep contraption perf off to disarm.");
                return true;
            }
            sender.sendMessage(perfReport());
            return true;
        });

        // TEMP spawn benchmark — console-runnable, no player needed.
        cases.put(new ArgumentList("contraption^", "spawnbench^", Integer.class), (sender, parsed) -> {
            int n = (Integer) parsed[2];
            org.bukkit.World w = org.bukkit.Bukkit.getWorlds().get(0);
            var state = dev.arubik.craftengine.contraption.DebugPhysSpawn.parseBlockState("minecraft:iron_block");
            org.bukkit.Location s = w.getSpawnLocation();
            long t0 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                var ent = dev.arubik.craftengine.contraption.DebugPhysSpawn.spawn(w,
                        new net.minecraft.world.phys.Vec3(s.getX() + i * 3, s.getY() + 80, s.getZ()),
                        Map.of(BlockPos.ZERO, state), null);
                // Dispose immediately — otherwise the bench leaks a level per spawn and later spawns
                // measure a server bogged down by hundreds of empty dimensions, not the spawn cost itself.
                if (ent != null) {
                    dev.arubik.craftengine.contraption.ContraptionManager.remove(ent.state().id());
                    if (ent.state().level() != null) {
                        ent.state().level().dispose();
                    }
                }
            }
            double ms = (System.nanoTime() - t0) / 1e6;
            sender.sendMessage(String.format("§bspawnbench: %d spawns in %.1fms = %.2fms each", n, ms, ms / n));
            CraftEnginePolyfills.instance().getLogger()
                    .info(String.format("[SpawnProfile] BENCH %d spawns total=%.1fms avg=%.2fms", n, ms, ms / n));
            return true;
        });

        // /cep contraption perf off — disarm profiling so it stops costing anything.
        cases.put(new ArgumentList("contraption^", "perf^", "off^"), (sender, parsed) -> {
            dev.arubik.craftengine.contraption.ContraptionPerf.setEnabled(false);
            sender.sendMessage("§7Contraption profiling disarmed.");
            return true;
        });

        // /cep contraption spike-stop — cancel + despawn the running swarm spike early.
        cases.put(new ArgumentList("contraption^", "spike-stop^"), (sender, parsed) -> {
            if (sender instanceof Player player)
                dev.arubik.craftengine.contraption.SwarmSpike.stop(player);
            return true;
        });

        // Glue is now an item-based wand tool (cml:slime_glue, right-click twice to glue two
        // adjacent blocks) — see contraption.GlueWandListener. The old /cep contraption glue
        // and glue-clear commands were removed once that replaced them.

        // /cep contraption structure — report the size of the glued structure you're looking at.
        cases.put(new ArgumentList("contraption^", "structure^"), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            Block tb = player.getTargetBlockExact(8);
            if (tb == null) {
                sender.sendMessage("§cLook at a block.");
                return true;
            }
            BlockPos pos = new BlockPos(tb.getX(), tb.getY(), tb.getZ());
            Set<BlockPos> structure = dev.arubik.craftengine.contraption.GlueRegistry
                    .structureAt(tb.getWorld().getUID(), pos);
            sender.sendMessage("§bStructure§7: §f" + structure.size() + "§7 block(s) glued together.");
            return true;
        });

        // /cep contraption capture-test — Phase 1 round-trip proof (CONTRAPTIONS.md §5
        // Phase 1): capture the glued structure at the looked-at block, blank it to air,
        // wait 2s, then restore it exactly from the captured NBT blob.
        cases.put(new ArgumentList("contraption^", "capture-test^"), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            Block tb = player.getTargetBlockExact(8);
            if (tb == null) {
                sender.sendMessage("§cLook at a block.");
                return true;
            }
            BlockPos bearing = new BlockPos(tb.getX(), tb.getY(), tb.getZ());
            Set<BlockPos> structure = dev.arubik.craftengine.contraption.GlueRegistry
                    .structureAt(tb.getWorld().getUID(), bearing);
            net.minecraft.world.level.Level level = ((CraftWorld) tb.getWorld()).getHandle();

            dev.arubik.craftengine.contraption.ContraptionCapture.Result captured = dev.arubik.craftengine.contraption.ContraptionCapture
                    .capture(level, structure, bearing);
            sender.sendMessage("§bCaptured §f" + captured.level().blockCount() + "§b block(s). Blanking for 2s, then restoring...");
            dev.arubik.craftengine.contraption.ContraptionCapture.removeFromWorld(level, structure);

            org.bukkit.Bukkit.getScheduler().runTaskLater(CraftEnginePolyfills.instance(), () -> {
                dev.arubik.craftengine.contraption.ContraptionCapture.restore(level, captured.level(), bearing);
                sender.sendMessage("§aRestored §f" + captured.level().blockCount() + "§a block(s) from the captured NBT blob.");
            }, 40L);
            return true;
        });

        // /cep contraption spawn-holo — Phase 2 (CONTRAPTIONS.md §5): capture the glued
        // structure at the looked-at block, remove it from the world, and spawn a
        // ContraptionEntity hologram in its exact place via the BLOCK_DISPLAY swarm.
        cases.put(new ArgumentList("contraption^", "spawn-holo^"), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            Block tb = player.getTargetBlockExact(8);
            if (tb == null) {
                sender.sendMessage("§cLook at a block.");
                return true;
            }
            BlockPos bearing = new BlockPos(tb.getX(), tb.getY(), tb.getZ());
            dev.arubik.craftengine.contraption.HologramTest.start(CraftEnginePolyfills.instance(), tb.getWorld(),
                    bearing);
            sender.sendMessage("§bHologram spawned. §7/cep contraption despawn-holo to restore the real blocks.");
            return true;
        });

        // /cep contraption despawn-holo — despawn the swarm and restore the real blocks.
        cases.put(new ArgumentList("contraption^", "despawn-holo^"), (sender, parsed) -> {
            if (sender instanceof Player player) {
                dev.arubik.craftengine.contraption.HologramTest.stop(player.getWorld());
                sender.sendMessage("§7Hologram despawned, real blocks restored.");
            }
            return true;
        });

        // /cep contraption move <dx> <dy> <dz> — Phase 3 (CONTRAPTIONS.md §5): attach a
        // constant-velocity LinearActuatorBehavior (blocks/second) to the active hologram
        // so ContraptionEngine's master clock drives it every tick.
        cases.put(new ArgumentList("contraption^", "move^", Double.class, Double.class, Double.class),
                (sender, parsed) -> {
                    if (!(sender instanceof Player player))
                        return true;
                    double dx = (Double) parsed[2];
                    double dy = (Double) parsed[3];
                    double dz = (Double) parsed[4];
                    boolean ok = dev.arubik.craftengine.contraption.HologramTest.setLinearVelocity(player.getWorld(),
                            dx, dy, dz);
                    sender.sendMessage(ok
                            ? "§bMoving at §f" + dx + ", " + dy + ", " + dz + "§b blocks/sec."
                            : "§cNo active hologram in this world — /cep contraption spawn-holo first.");
                    return true;
                });

        // /cep contraption nudge <dx> <dy> <dz> — debug tool: instantly shift the active
        // hologram's CONTINUOUS position by a one-shot delta (as opposed to "move", which
        // attaches a constant blocks/sec velocity behavior) — for manually testing
        // movement/rendering without a real bearing behavior driving it. Resolves "the
        // currently active contraption" the same way "move"/"miner" already do: HologramTest's
        // per-world ACTIVE run.
        cases.put(new ArgumentList("contraption^", "nudge^", Double.class, Double.class, Double.class),
                (sender, parsed) -> {
                    if (!(sender instanceof Player player))
                        return true;
                    double dx = (Double) parsed[2];
                    double dy = (Double) parsed[3];
                    double dz = (Double) parsed[4];
                    boolean ok = dev.arubik.craftengine.contraption.HologramTest.nudgePosition(player.getWorld(),
                            dx, dy, dz);
                    sender.sendMessage(ok
                            ? "§bNudged by §f" + dx + ", " + dy + ", " + dz + "§b."
                            : "§cNo active hologram in this world — /cep contraption spawn-holo first.");
                    return true;
                });

        // /cep contraption rotate <90|180|270|-90> — debug tool: add the given number of
        // degrees to the active hologram's current yaw, for manually testing rotation (and the
        // axis-snap-on-disassemble behavior) without a real ROTATIONAL bearing spinning it.
        // This project's contraptions only ever track a single yaw around the vertical Y axis
        // (no pitch/roll — see ContraptionMath/ContraptionState), so "choosing an axis" here
        // means picking one of the 4 cardinal-facing steps rather than a genuine 3D axis.
        cases.put(new ArgumentList("contraption^", "rotate^", Integer.class), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            int degrees = (Integer) parsed[2];
            boolean ok = dev.arubik.craftengine.contraption.HologramTest.rotateYawDegrees(player.getWorld(), degrees);
            sender.sendMessage(ok
                    ? "§bRotated by §f" + degrees + "§b degrees."
                    : "§cNo active hologram in this world — /cep contraption spawn-holo first.");
            return true;
        });

        // /cep contraption miner <dx> <dy> <dz> <rpm> — Phase 5 (CONTRAPTIONS.md §5): attach
        // a MinerBehavior targeting the given bearing-relative offset to the active hologram.
        cases.put(new ArgumentList("contraption^", "miner^", Integer.class, Integer.class, Integer.class,
                Double.class), (sender, parsed) -> {
                    if (!(sender instanceof Player player))
                        return true;
                    int dx = (Integer) parsed[2];
                    int dy = (Integer) parsed[3];
                    int dz = (Integer) parsed[4];
                    double rpm = (Double) parsed[5];
                    boolean ok = dev.arubik.craftengine.contraption.HologramTest.addMiner(player.getWorld(),
                            new net.minecraft.core.BlockPos(dx, dy, dz), rpm);
                    sender.sendMessage(ok
                            ? "§bMiner attached, targeting offset §f" + dx + "," + dy + "," + dz + "§b at §f" + rpm + "§b rpm."
                            : "§cNo active hologram in this world — /cep contraption spawn-holo first.");
                    return true;
                });
        // /cep contraption join — DEBUG: teleport the executing player DIRECTLY INTO the
        // active hologram's hidden ContraptionLevel (a real cross-world Bukkit teleport, not a
        // packet illusion), positioned just above the bearing's local origin (BlockPos.ZERO —
        // ContraptionCapture always stores the bearing itself at local (0,0,0), see
        // ContraptionMath.toLocal). Lets a developer walk around INSIDE the mini-dimension to
        // inspect the raw captured blocks directly, rather than trusting the packet-mirror
        // swarms to be rendering them faithfully. Debug-only noclip power — gated on a
        // permission (falls back to op, like any unregistered Bukkit permission node).
        cases.put(new ArgumentList("contraption^", "join^"), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            if (!player.hasPermission("cep.contraption.debug")) {
                sender.sendMessage("§cYou don't have permission to noclip into a contraption's internal level.");
                return true;
            }
            // Join the contraption the player is LOOKING AT (2026-07-02 — "join debe meterte al
            // contraption que estas mirando"): raycast against every live contraption's cells via
            // the same real-world raycast ContraptionInteractionListener uses for right-click
            // routing, instead of blindly grabbing the first contraption in this world.
            net.minecraft.server.level.ServerPlayer nmsPlayer = ((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle();
            dev.arubik.craftengine.contraption.ContraptionInteractionListener.Hit hit =
                    dev.arubik.craftengine.contraption.ContraptionInteractionListener.raycast(nmsPlayer);
            dev.arubik.craftengine.contraption.level.ContraptionLevel level = hit != null ? hit.state().level() : null;
            if (level == null) {
                sender.sendMessage("§cYou're not looking at a contraption — aim at one and try again.");
                return true;
            }
            dev.arubik.craftengine.contraption.ContraptionJoinManager.recordPreJoinLocation(player);
            org.bukkit.Location dest = new org.bukkit.Location(level.getWorld(), 0.5, 1.0, 0.5);
            player.teleport(dest);
            sender.sendMessage("§bTeleported into the contraption's internal level§7 (local origin ~0,1,0)."
                    + " §cNote: this level's blocks/entities only ever MOVE via render-position math"
                    + " (ContraptionLevel#setTransform) — your real Bukkit position here is static and won't"
                    + " follow the contraption if it's moving/rotating. §7/cep contraption leave to return.");
            CraftEnginePolyfills.instance().getLogger()
                    .info("[contraption debug] " + player.getName() + " joined ContraptionLevel " + level.getWorld().getName());
            return true;
        });

        // /cep contraption leave — DEBUG: teleport the player back to wherever they were in the
        // real world before /cep contraption join. Fails gracefully (no NPE) if they never joined.
        cases.put(new ArgumentList("contraption^", "leave^"), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            if (!player.hasPermission("cep.contraption.debug")) {
                sender.sendMessage("§cYou don't have permission to use this command.");
                return true;
            }
            org.bukkit.Location back = dev.arubik.craftengine.contraption.ContraptionJoinManager.consumePreJoinLocation(player);
            if (back == null) {
                sender.sendMessage("§cYou haven't /cep contraption join'ed anything — nowhere to return to.");
                return true;
            }
            player.teleport(back);
            sender.sendMessage("§bTeleported back to the real world.");
            CraftEnginePolyfills.instance().getLogger()
                    .info("[contraption debug] " + player.getName() + " left the contraption level.");
            return true;
        });
    }

    private static String fmt(double d) {
        return String.format("%.3f", d);
    }

    /**
     * Renders {@link dev.arubik.craftengine.contraption.ContraptionPerf}'s rolling averages. Times are the
     * mean MILLISECONDS per tick spent in each phase — a tick's whole budget is 50ms, so the total here is
     * directly comparable against it (and against the ~2.5ms that separates 20.0 TPS from 19.5).
     */
    private static String perfReport() {
        int samples = dev.arubik.craftengine.contraption.ContraptionPerf.sampleCount();
        if (samples == 0) {
            return "§7Profiling armed but no ticks sampled yet — try again in a second.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("§bContraption perf §7(mean over last §f").append(samples).append("§7 ticks, tick budget"
                + " §f50ms§7)\n");
        sb.append("§7 total in tickAll: §f")
                .append(fmt(dev.arubik.craftengine.contraption.ContraptionPerf.totalMillis())).append(" ms\n");
        for (var phase : dev.arubik.craftengine.contraption.ContraptionPerf.Phase.values()) {
            sb.append("§8  • §7").append(phase.name().toLowerCase(java.util.Locale.ROOT)).append(": §f")
                    .append(fmt(dev.arubik.craftengine.contraption.ContraptionPerf.millis(phase))).append(" ms\n");
        }
        sb.append("§7 contraptions: §f").append(dev.arubik.craftengine.contraption.ContraptionManager.count())
                .append("§7 live — §a").append(fmt(dev.arubik.craftengine.contraption.ContraptionPerf.meanRendered()))
                .append("§7 rendered, §8").append(fmt(dev.arubik.craftengine.contraption.ContraptionPerf.meanSkippedUnloaded()))
                .append("§7 skipped (chunk unloaded), §8")
                .append(fmt(dev.arubik.craftengine.contraption.ContraptionPerf.meanSkippedNoViewers()))
                .append("§7 skipped (no viewers)\n");
        sb.append("§7 cells rendered: §f")
                .append(fmt(dev.arubik.craftengine.contraption.ContraptionPerf.meanCells()));
        return sb.toString();
    }

    /** Shared tail of both {@code /cep contraption spawn} forms: place the body and report it. */
    private static void spawnDebugPhys(Player player,
            Map<BlockPos, net.minecraft.world.level.block.state.BlockState> states,
            Map<BlockPos, net.minecraft.nbt.CompoundTag> blockEntities, String label) {
        org.bukkit.Location eye = player.getEyeLocation();
        net.minecraft.world.phys.Vec3 anchor = dev.arubik.craftengine.contraption.DebugPhysSpawn.spawnAnchor(
                new net.minecraft.world.phys.Vec3(eye.getX(), eye.getY(), eye.getZ()),
                new net.minecraft.world.phys.Vec3(eye.getDirection().getX(), eye.getDirection().getY(),
                        eye.getDirection().getZ()));
        dev.arubik.craftengine.contraption.ContraptionEntity entity = dev.arubik.craftengine.contraption.DebugPhysSpawn
                .spawn(player.getWorld(), anchor, states, blockEntities);
        if (entity == null) {
            player.sendMessage("§cNothing solid to spawn from §f" + label + "§c.");
            return;
        }
        player.sendMessage("§bPhysContraption §f" + entity.state().id() + "§b — §f" + entity.state().level().blockCount()
                + "§b cell(s) from §f" + label + "§b, dropped at §f" + fmt(anchor.x) + ", " + fmt(anchor.y) + ", "
                + fmt(anchor.z) + "§b.");
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        for (Map.Entry<ArgumentList, BiFunction<CommandSender, Object[], Boolean>> e : cases.entrySet()) {
            if (e.getKey().matches(args,sender)) {
                Object[] parsed = e.getKey().parse(args,sender);
                return e.getValue().apply(sender, parsed);
            }
        }
        sender.sendMessage("No matching argument signature for: " + Arrays.toString(args));
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        int index = args.length - 1;
        String current = args[args.length - 1];

        for (var entry : cases.keySet()) {
            if (index < entry.types.length) {
                suggestions.addAll(entry.suggest(index, current, sender));
            }
        }
        return suggestions;
    }
}
