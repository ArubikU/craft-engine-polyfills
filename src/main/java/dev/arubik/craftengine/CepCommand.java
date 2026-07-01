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
    }

    private static String fmt(double d) {
        return String.format("%.3f", d);
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
