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
import dev.arubik.craftengine.contraption.assembly.ContraptionCapture;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.util.ArgumentList;
import dev.arubik.craftengine.util.ArgumentList.XAxisCoordinate;
import dev.arubik.craftengine.util.ArgumentList.YAxisCoordinate;
import dev.arubik.craftengine.util.ArgumentList.ZAxisCoordinate;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.util.Key;

import java.util.*;
import java.util.function.BiFunction;

public class CepCommand implements CommandExecutor, TabCompleter {

    private final Map<ArgumentList, BiFunction<CommandSender, Object[], Boolean>> cases = new HashMap<>();

    public CepCommand() {
        // ---- /cep reload <what> -------------------------------------------------
        //
        // Targeted, live reloads. Each one re-reads the relevant files from disk AND pushes the
        // result onto every object already in the world, so a machine placed before the reload
        // picks up the change too - reloading only the registry would leave existing blocks on
        // their old definition, which is the trap the renderer cache used to fall into.

        // /cep reload scripts - re-read every .pf and re-register the script types.
        cases.put(new ArgumentList("reload^", "scripts^"), (sender, parsed) -> {
            try {
                CraftEnginePolyfills plugin = CraftEnginePolyfills.instance();
                dev.arubik.craftengine.script.ScriptBootstrap.reload();
                dev.arubik.craftengine.script.ScriptRegistry.loadAll(plugin.getDataFolder());
                int n = dev.arubik.craftengine.script.ScriptRegistry.size();
                // Scripts are resolved by name at call time, so live machines pick the new body
                // up on their next action tick with nothing further to do.
                reloadMsg(sender, "scripts", n + " script(s) reloaded");
            } catch (Throwable t) {
                reloadFail(sender, "scripts", t);
            }
            return true;
        });

        // /cep reload scripts <name> - targeted reload of exactly one <name>.pf, skipping every
        // OTHER script's __init__/__unload__ churn (see ScriptRegistry#reloadOne's own javadoc for
        // why that matters, e.g. warps.pf's CREATE TABLE calls). NOT run through ScriptBootstrap
        // .reload() - that re-registers every PolyType from scratch, which is a whole-engine
        // operation with nothing "one script" about it; this only ever needs to matter if a NEW
        // builtin type was added, which a targeted single-file reload was never going to pick up
        // anyway (that needs the jar rebuilt regardless).
        cases.put(new ArgumentList("reload^", "scripts^", String.class), (sender, parsed) -> {
            String name = (String) parsed[2];
            try {
                boolean ok = dev.arubik.craftengine.script.ScriptRegistry.reloadOne(
                        CraftEnginePolyfills.instance().getDataFolder(), name);
                if (ok) reloadMsg(sender, "scripts", "'" + name + ".pf' reloaded");
                else reloadFail(sender, "scripts", new java.io.FileNotFoundException(name + ".pf"));
            } catch (Throwable t) {
                reloadFail(sender, "scripts", t);
            }
            return true;
        });

        // /cep reload machines - re-read machines/*.json and re-point every placed machine.
        cases.put(new ArgumentList("reload^", "machines^"), (sender, parsed) -> {
            try {
                int loaders = dev.arubik.craftengine.data.Registries.reloadLoaders(
                        "machines", "bars", "upgrades", "multiblocks");
                int machines = dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity
                        .refreshDefinitions();
                reloadMsg(sender, "machines",
                        dev.arubik.craftengine.machine.MachineDefinition.REGISTRY.size()
                                + " definition(s) from " + loaders + " loader(s), "
                                + machines + " placed machine(s) re-pointed");
            } catch (Throwable t) {
                reloadFail(sender, "machines", t);
            }
            return true;
        });

        // /cep reload items - re-read items/*.json. DataItemBehavior resolves its ItemDefinition
        // by id fresh on every dispatch rather than caching it, so there is no "re-point every
        // live instance" step to do here the way machines need - the new definition is already
        // live for every item everywhere (inventories, machines, dropped) the moment the registry
        // is repopulated.
        cases.put(new ArgumentList("reload^", "items^"), (sender, parsed) -> {
            try {
                int loaders = dev.arubik.craftengine.data.Registries.reloadLoaders("items");
                reloadMsg(sender, "items",
                        dev.arubik.craftengine.item.ItemDefinition.REGISTRY.size()
                                + " definition(s) from " + loaders + " loader(s)");
            } catch (Throwable t) {
                reloadFail(sender, "items", t);
            }
            return true;
        });

        // /cep reload render - re-read the renderer specs and rebuild every live renderer.
        cases.put(new ArgumentList("reload^", "render^"), (sender, parsed) -> {
            try {
                // Renderer specs are declared inside the machine JSONs, so the definitions have
                // to be re-read before the managers can be rebuilt from them.
                dev.arubik.craftengine.data.Registries.reloadLoaders("machines");
                int refreshed = dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity
                        .refreshDefinitions();
                reloadMsg(sender, "render", refreshed + " machine(s) re-pointed; every live "
                        + "renderer closed and rebuilt from the new specs on the next tick");
            } catch (Throwable t) {
                reloadFail(sender, "render", t);
            }
            return true;
        });

        // /cep reload cmds - re-read cmds/*.json. Brigadier's command TREE (name/aliases/args/
        // subcommand structure) is baked once at plugin enable and genuinely can't be rebuilt
        // without a restart (see CmdRegistry's own javadoc) - this only refreshes the CONTENT an
        // already-registered command reads live: execute/gui script refs, and every declarative
        // "pages" layout (CmdPageDef reads straight from CmdRegistry.all() on each open, so an
        // edited warps.json slot/button layout shows up on the NEXT menu open with no restart).
        cases.put(new ArgumentList("reload^", "cmds^"), (sender, parsed) -> {
            try {
                dev.arubik.craftengine.cmd.CmdDefinitionLoader.load();
                reloadMsg(sender, "cmds", dev.arubik.craftengine.cmd.CmdRegistry.all().size()
                        + " command(s) re-read - NEW command files still need a restart, "
                        + "existing ones' scripts/pages are live now");
            } catch (Throwable t) {
                reloadFail(sender, "cmds", t);
            }
            return true;
        });

        // /cep reload cmds <name> - targeted reload of exactly one cmds/<name>.json, leaving every
        // other loaded command's definition object untouched (see CmdDefinitionLoader#loadOne).
        // Same restart caveat as the bulk reload above for a BRAND NEW command file; an existing
        // one's content (execute/gui refs, pages) is live immediately either way.
        cases.put(new ArgumentList("reload^", "cmds^", String.class), (sender, parsed) -> {
            String name = (String) parsed[2];
            try {
                boolean ok = dev.arubik.craftengine.cmd.CmdDefinitionLoader.loadOne(name);
                if (ok) reloadMsg(sender, "cmds", "'" + name + ".json' re-read - a NEW command still "
                        + "needs a restart, an existing one's scripts/pages are live now");
                else reloadFail(sender, "cmds", new java.io.FileNotFoundException(name + ".json"));
            } catch (Throwable t) {
                reloadFail(sender, "cmds", t);
            }
            return true;
        });

        // /cep reload crons - re-read crons/*.json. CronScheduler's own ticking task reads
        // CronRegistry live every firing, so this is a genuinely full reload - no restart caveat.
        cases.put(new ArgumentList("reload^", "crons^"), (sender, parsed) -> {
            try {
                dev.arubik.craftengine.cron.CronDefinitionLoader.load();
                reloadMsg(sender, "crons", dev.arubik.craftengine.cron.CronRegistry.all().size() + " job(s) re-read");
            } catch (Throwable t) {
                reloadFail(sender, "crons", t);
            }
            return true;
        });

        // /cep reload events - re-read events/*.json AND fully re-register every listener (see
        // GenericEventBridge#reload's javadoc for why that's safe to do live, unlike /cmds) - a
        // brand new events/*.json file dropped in after startup DOES take effect here, no restart
        // needed at all (unlike /cmds' Brigadier-tree limitation).
        cases.put(new ArgumentList("reload^", "events^"), (sender, parsed) -> {
            try {
                int n = dev.arubik.craftengine.events.GenericEventBridge.reload(CraftEnginePolyfills.instance());
                reloadMsg(sender, "events", n + " definition(s) re-read and every listener re-registered");
            } catch (Throwable t) {
                reloadFail(sender, "events", t);
            }
            return true;
        });

        // /cep reload database - re-reads database.yml and reconnects SQLDriver/RedisDriver.
        // Doesn't touch any script or its __init__ tables - reconnecting doesn't imply re-running
        // setup, only /cep reload scripts (or a restart) does that.
        cases.put(new ArgumentList("reload^", "database^"), (sender, parsed) -> {
            try {
                dev.arubik.craftengine.sql.SQLDriver.init(CraftEnginePolyfills.instance().getDataFolder(), CraftEnginePolyfills.instance().getLogger());
                dev.arubik.craftengine.sql.RedisDriver.init(CraftEnginePolyfills.instance().getDataFolder(), CraftEnginePolyfills.instance().getLogger());
                String status = "SQL " + (dev.arubik.craftengine.sql.SQLDriver.isReady() ? "ready (" + dev.arubik.craftengine.sql.SQLDriver.backend() + ")" : "FAILED: " + dev.arubik.craftengine.sql.SQLDriver.lastError())
                        + ", Redis " + (!dev.arubik.craftengine.sql.RedisDriver.isEnabled() ? "disabled" : dev.arubik.craftengine.sql.RedisDriver.isReady() ? "ready" : "FAILED: " + dev.arubik.craftengine.sql.RedisDriver.lastError());
                reloadMsg(sender, "database", status);
            } catch (Throwable t) {
                reloadFail(sender, "database", t);
            }
            return true;
        });

        // /cep debug item <id> - diagnoses the Item.create(id)/with_profile(...) pipeline used by
        // warps.pf's player_head_icon() for "default:gui_head_size_1" GUI-scaled heads, since the
        // pipeline can silently fall back to an empty/vanilla stack on any of several failure paths
        // (CraftEngineItems.byId returning null, the defaulted vanilla ITEM registry returning AIR
        // instead of null, or the built stack's meta not actually being SkullMeta) with no visible
        // error anywhere - this reports every stage so the failure point doesn't have to be guessed.
        cases.put(new ArgumentList("debug^", "item^", String.class), (sender, parsed) -> {
            String id = (String) parsed[2];
            try {
                net.momirealms.craftengine.core.util.Key key = net.momirealms.craftengine.core.util.Key.of(id);
                var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(key);
                sender.sendMessage("CraftEngineItems.byId(" + id + ") = " + (def == null ? "NULL" : def.getClass().getName()));
                if (def == null) {
                    var loaded = net.momirealms.craftengine.bukkit.api.CraftEngineItems.loadedItems();
                    sender.sendMessage("loadedItems() size=" + loaded.size());
                    int shown = 0;
                    for (var k : loaded.keySet()) {
                        if (k.toString().contains(id.substring(id.indexOf(':') + 1).split("_")[0]) || k.toString().startsWith("default:")) {
                            sender.sendMessage("  candidate key: [" + k + "] equalsTarget=" + k.equals(key));
                            if (++shown >= 15) break;
                        }
                    }
                }
                if (def != null) {
                    org.bukkit.inventory.ItemStack bukkit = def.buildBukkitItem();
                    sender.sendMessage("buildBukkitItem() = " + bukkit.getType() + " x" + bukkit.getAmount()
                            + " meta=" + (bukkit.hasItemMeta() ? bukkit.getItemMeta().getClass().getSimpleName() : "none")
                            + " isSkullMeta=" + (bukkit.getItemMeta() instanceof org.bukkit.inventory.meta.SkullMeta));
                    org.bukkit.OfflinePlayer target = sender instanceof org.bukkit.entity.Player p ? p : null;
                    if (target != null && bukkit.getItemMeta() instanceof org.bukkit.inventory.meta.SkullMeta meta) {
                        meta.setOwningPlayer(target);
                        bukkit.setItemMeta(meta);
                        var ceId = net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(bukkit);
                        sender.sendMessage("after setOwningPlayer -> CraftEngineItems.getCustomItemId = " + ceId);
                    }
                }
            } catch (Throwable t) {
                sender.sendMessage("threw: " + t);
                CraftEnginePolyfills.instance().getLogger().log(java.util.logging.Level.WARNING, "[Cep] debug item threw", t);
            }
            return true;
        });

        // /cep debug pf <ref> <arg> - evaluates a "file.pf:func" ScriptCall with ONE string arg
        // exactly like the real page-generator pipeline does (see GeneratedPageContent#entries),
        // and reports the resulting ScriptValue's actual runtime type - the earlier "debug item"
        // case simulates the Item.create/with_profile Java calls directly, which can succeed while
        // the real script path still fails (chained method dispatch, arg binding, etc. all add
        // their own failure surface on top of what "debug item" alone can catch).
        cases.put(new ArgumentList("debug^", "pf^", String.class, String.class), (sender, parsed) -> {
            String ref = (String) parsed[2];
            String arg = (String) parsed[3];
            // Append the arg onto the ref itself ("file.pf:func:arg") - ScriptCall.evaluate() binds
            // args from its OWN parsed `args` list, not from executeWithExtraArgs (that method calls
            // fn.call(...) but discards its return value, always yielding NULL here regardless of
            // what the function actually returns - evaluate() is what GeneratedPageContent's own
            // generator-ref pipeline uses and is the only one of the three that reports a result).
            if (!arg.isBlank()) ref = ref + ":" + arg;
            try {
                dev.arubik.craftengine.script.ScriptCall call = dev.arubik.craftengine.script.ScriptCall.parse(ref);
                if (call == null) {
                    sender.sendMessage("ScriptCall.parse(" + ref + ") = NULL");
                    return true;
                }
                dev.arubik.craftengine.script.ScriptContext.Builder b = dev.arubik.craftengine.script.ScriptContext.builder();
                b.typed("Server", dev.arubik.craftengine.script.types.world.ServerType.INSTANCE);
                b.typed("SQL", dev.arubik.craftengine.script.types.util.SQLDriverType.INSTANCE);
                b.typed("Redis", dev.arubik.craftengine.script.types.util.RedisDriverType.INSTANCE);
                b.typed("Uuid", dev.arubik.craftengine.script.types.primitive.UuidType.NAMESPACE);
                b.typed("Plugins", dev.arubik.craftengine.script.types.plugins.PluginsType.INSTANCE);
                b.typed("Item", dev.arubik.craftengine.script.types.primitive.ItemType.NAMESPACE);
                if (sender instanceof org.bukkit.entity.Player p) {
                    b.player(((org.bukkit.craftbukkit.entity.CraftPlayer) p).getHandle());
                }
                dev.arubik.craftengine.script.ScriptValue result = call.evaluate(b.build());
                sender.sendMessage("result class = " + result.getClass().getName());
                if (result instanceof dev.arubik.craftengine.script.ScriptValue.Item item) {
                    var stack = item.stack();
                    sender.sendMessage("Item.stack() = " + (stack == null ? "NULL" : (stack.isEmpty() ? "EMPTY" : stack.getItem() + " x" + stack.getCount())));
                    if (stack != null && !stack.isEmpty()) {
                        var bukkit = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(stack);
                        sender.sendMessage("as bukkit: " + bukkit.getType() + " meta=" + (bukkit.hasItemMeta() ? bukkit.getItemMeta().getClass().getSimpleName() : "none"));
                    }
                } else {
                    sender.sendMessage("asStr() = " + result.asStr());
                }
            } catch (Throwable t) {
                sender.sendMessage("threw: " + t);
                CraftEnginePolyfills.instance().getLogger().log(java.util.logging.Level.WARNING, "[Cep] debug pf threw", t);
            }
            return true;
        });

        // /cep reload - usage, and the list of loaders a targeted reload can name.
        cases.put(new ArgumentList("reload^"), (sender, parsed) -> {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<gray>Usage: <white>/cep reload <aqua>render<gray>|<aqua>machines<gray>|<aqua>items<gray>|<aqua>scripts<gray>|<aqua>cmds<gray>|<aqua>crons<gray>|<aqua>events<gray>|<aqua>database"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<dark_gray>loaders: <gray>"
                            + String.join(", ", dev.arubik.craftengine.data.Registries.loaderNames())));
            return true;
        });

        // /cep data registries - what the data-driven load phase produced, and whether
        // it is sealed. Registration is only legal while these read "open".
        cases.put(new ArgumentList("data^", "registries^"), (sender, parsed) -> {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<gray>Data registries <white>(" + (dev.arubik.craftengine.data.Registries.isFrozen()
                            ? "<red>frozen" : "<green>open") + "<white>)"));
            for (var registry : dev.arubik.craftengine.data.Registries.all()) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        "  <aqua>" + registry.name() + "<gray>: <white>" + registry.size()
                                + "<gray> entries, " + (registry.isFrozen() ? "<red>frozen" : "<green>open")
                                + "<gray>, " + (registry.clearsOnReload() ? "rebuilt" : "identity-stable")
                                + " on reload"));
            }
            for (var loader : dev.arubik.craftengine.data.Registries.loaders()) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        "  <dark_gray>loader <gray>" + loader.name() + " <dark_gray>@ phase " + loader.phase()));
            }
            return true;
        });

        // /cep data fluids - the open liquid registry with each entry's tunables.
        cases.put(new ArgumentList("data^", "fluids^"), (sender, parsed) -> {
            for (var fluid : dev.arubik.craftengine.fluid.FluidType.REGISTRY) {
                var p = fluid.value().properties();
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<aqua>" + fluid.id() + " <gray>unit=<white>" + p.unitMb()
                                + " <gray>block=<white>" + p.mbPerFullBlock()
                                + " <gray>density=<white>" + p.density()
                                + " <gray>viscosity=<white>" + p.viscosity()
                                + " <gray>render=<white>" + p.renderFamily()));
            }
            return true;
        });

        // /cep data gases - same, for gases.
        cases.put(new ArgumentList("data^", "gases^"), (sender, parsed) -> {
            for (var gas : dev.arubik.craftengine.gas.GasType.REGISTRY) {
                var p = gas.value().properties();
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<aqua>" + gas.id() + " <gray>density=<white>" + p.density()
                                + " <gray>vent=<white>" + p.ventParticle()
                                + " <gray>per-particle=<white>" + p.ventPerParticle()));
            }
            return true;
        });

        // /cep data pipes - the open pipe-tier registry.
        cases.put(new ArgumentList("data^", "pipes^"), (sender, parsed) -> {
            for (var pipe : dev.arubik.craftengine.pipe.PipeType.REGISTRY) {
                var p = pipe.value().properties();
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<aqua>" + pipe.id() + " <gray>block=<white>" + p.blockId()
                                + " <gray>carries=<white>" + p.resource()
                                + " <gray>cap=<white>" + p.capacity()
                                + " <gray>rate=<white>" + p.transferPerTick()
                                + " <gray>conductance=<white>" + p.conductance()
                                + " <gray>tier=<white>" + p.tier()));
            }
            return true;
        });

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

        // /cep fluid graph - Phase 1 debug: dump the fluid network graph for the block you're looking at.
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

        // /cep fluid solvetest - Phase 2 self-test of the hydraulic solver (no world side effects).
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

        // /cep fluid step - Phase 3 apply demo: build the graph for the looked-at network, run ONE solver
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

        // /cep gas step - Phase 5: gas network solved with no gravity (head = fill only).
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

        // /cep fluid engine on - turn the always-on hydraulic engine ON and register the looked-at network.
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

        // /cep fluid test - run the 50+ solver edge-case suite.
        cases.put(new ArgumentList("fluid^", "test^"), (sender, parsed) -> {
            dev.arubik.craftengine.fluid.graph.FluidSolverTests.Out o = dev.arubik.craftengine.fluid.graph.FluidSolverTests
                    .run();
            sender.sendMessage("§bSolver tests§7: §a" + o.passed + " passed§7, "
                    + (o.failed == 0 ? "§a0 failed" : "§c" + o.failed + " failed"));
            for (String f : o.failures)
                sender.sendMessage("§c ✗ " + f);
            return true;
        });

        // /cep fluid engine off - back to the live per-block transport.
        cases.put(new ArgumentList("fluid^", "engine^", "off^"), (sender, parsed) -> {
            dev.arubik.craftengine.fluid.graph.FluidEngine.ENABLED = false;
            sender.sendMessage("§cFluidEngine OFF§7 (per-block transport restored).");
            return true;
        });

        // /cep contraption spike-swarm <n> <item|shulker> - Phase 0 spike #1: packet-swarm
        // entity budget + per-tick reposition cost at 20Hz (see CONTRAPTIONS.md §4).
        cases.put(new ArgumentList("contraption^", "spike-swarm^", Integer.class, String.class), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            int n = (Integer) parsed[2];
            String kind = (String) parsed[3];
            dev.arubik.craftengine.contraption.SwarmSpike.start(CraftEnginePolyfills.instance(), player, n, kind);
            return true;
        });

        // /cep show placement - logs what each in-contraption placement resolved to (contraption yaw,
        // player yaw, the yaw it was placed with, and the resulting blockstate), for diagnosing an
        // orientation report that cannot be reproduced without a client.
        // ---- /cep debug ... ----------------------------------------------------
        //
        // Two commands whose whole purpose is to let the plugin be profiled and exercised WITHOUT a
        // human present. Optimisation work needs a profile; reading one previously meant somebody
        // standing in-game running spark and pasting the output by hand.

        // /cep debug profile <seconds> - sample the server thread and write a report to
        // plugins/CraftEnginePolyfill/debug/. spark cannot do this: its public API exposes metrics
        // only, with no way to request a profiler dump programmatically. See ThreadSampler.
        cases.put(new ArgumentList("debug^", "profile^", Integer.class), (sender, parsed) -> {
            int seconds = Math.max(1, Math.min(120, (Integer) parsed[2]));
            if (dev.arubik.craftengine.debug.ThreadSampler.isRunning()) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<red>A profile is already running - two samplers would each pause the "
                        + "server thread and report a distorted picture of the other."));
                return true;
            }
            Thread serverThread = Thread.currentThread(); // a command runs ON the server thread
            java.nio.file.Path out = CraftEnginePolyfills.instance().getDataFolder().toPath()
                    .resolve("debug").resolve("profile-" + System.currentTimeMillis() + ".txt");
            boolean started = dev.arubik.craftengine.debug.ThreadSampler.start(
                    serverThread, seconds * 1000L, 5L, out,
                    written -> java.util.logging.Logger.getLogger("CraftEnginePolyfills").info(
                            written != null
                                    ? "[Cep] profile written: " + written.toAbsolutePath()
                                    : "[Cep] profile FAILED to write"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize(started
                    ? "<green>Profiling the server thread for <white>" + seconds + "s<green>. "
                      + "Report goes to <white>" + out.getFileName() + "<green> - keep the server busy."
                    : "<red>Could not start the sampler."));
            return true;
        });

        // /cep debug load <radius> - force-load chunks around you so machines keep ticking (and so
        // their action scripts and renderers keep running) with nobody standing there. See
        // DebugLoader for why this is forced chunks and not a fake player.
        cases.put(new ArgumentList("debug^", "load^", Integer.class), (sender, parsed) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<red>Run this as a player - it forces chunks around you."));
                return true;
            }
            int radius = Math.max(0, Math.min(8, (Integer) parsed[2]));
            int added = dev.arubik.craftengine.debug.DebugLoader.force(player.getWorld(),
                    player.getLocation().getBlockX(), player.getLocation().getBlockZ(), radius);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>Forced <white>" + added + "<green> new chunk(s) (radius " + radius
                    + "); <white>" + dev.arubik.craftengine.debug.DebugLoader.forcedCount()
                    + "<green> held in total. Machines there tick with nobody present. "
                    + "<gray>Display entities still need a real viewer.</gray> "
                    + "Release with <white>/cep debug unload"));
            return true;
        });

        // /cep debug loadat <world> <x> <z> <radius> - the console/RCON form of "load". The player
        // form below cannot be used by anything that is not a player, which defeats the point of
        // these commands: being able to exercise machines with nobody there.
        cases.put(new ArgumentList("debug^", "loadat^", String.class, Integer.class, Integer.class,
                Integer.class), (sender, parsed) -> {
            org.bukkit.World world = org.bukkit.Bukkit.getWorld((String) parsed[2]);
            if (world == null) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<red>No world named <white>" + parsed[2] + "<red>. Worlds: <white>"
                        + org.bukkit.Bukkit.getWorlds().stream().map(org.bukkit.World::getName)
                                .collect(java.util.stream.Collectors.joining(", "))));
                return true;
            }
            int radius = Math.max(0, Math.min(8, (Integer) parsed[5]));
            int added = dev.arubik.craftengine.debug.DebugLoader.force(
                    world, (Integer) parsed[3], (Integer) parsed[4], radius);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>Forced <white>" + added + "<green> new chunk(s) in <white>"
                    + world.getName() + "<green>; <white>"
                    + dev.arubik.craftengine.debug.DebugLoader.forcedCount()
                    + "<green> held in total."));
            return true;
        });

        // /cep debug fakeplayer <name> - a player the server actually knows about: it holds chunk
        // tickets, ticks, and is a real VIEWER, so display entities get built for it. Forced chunks
        // cover the loading half of this; only a registered player covers the viewer half.
        cases.put(new ArgumentList("debug^", "fakeplayer^", String.class), (sender, parsed) -> {
            String name = (String) parsed[2];
            net.minecraft.server.MinecraftServer server =
                    ((org.bukkit.craftbukkit.CraftServer) org.bukkit.Bukkit.getServer()).getServer();
            org.bukkit.Location at = sender instanceof Player p ? p.getLocation()
                    : org.bukkit.Bukkit.getWorlds().get(0).getSpawnLocation();
            net.minecraft.server.level.ServerLevel level = ((CraftWorld) at.getWorld()).getHandle();
            String error = dev.arubik.craftengine.debug.FakePlayer.spawn(
                    server, level, name, at.getX(), at.getY(), at.getZ());
            if (error != null) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<red>Could not spawn: <white>" + error));
                return true;
            }
            // Everything the server writes to a fake player is BUILT - which is the work being
            // exercised - and then has to go somewhere. Draining keeps the channel's queue from
            // growing for as long as the player exists.
            org.bukkit.Bukkit.getScheduler().runTaskTimer(CraftEnginePolyfills.instance(),
                    dev.arubik.craftengine.debug.FakePlayer::drainOutbound, 20L, 20L);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>Spawned fake player <white>" + name + "<green>. It loads chunks, ticks "
                    + "machines AND receives display entities. Remove with "
                    + "<white>/cepolyfill debug fakeplayer-remove"));
            return true;
        });

        // /cep debug fakeplayer-at <name> <world> <x> <y> <z> - the console form. The bare command
        // uses the sender's position, which from RCON is world spawn, and machines are rarely there.
        cases.put(new ArgumentList("debug^", "fakeplayer-at^", String.class, String.class,
                Integer.class, Integer.class, Integer.class), (sender, parsed) -> {
            org.bukkit.World world = org.bukkit.Bukkit.getWorld((String) parsed[3]);
            if (world == null) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<red>No world named <white>" + parsed[3]));
                return true;
            }
            net.minecraft.server.MinecraftServer server =
                    ((org.bukkit.craftbukkit.CraftServer) org.bukkit.Bukkit.getServer()).getServer();
            String error = dev.arubik.craftengine.debug.FakePlayer.spawn(
                    server, ((CraftWorld) world).getHandle(), (String) parsed[2],
                    (Integer) parsed[4], (Integer) parsed[5], (Integer) parsed[6]);
            if (error != null) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<red>Could not spawn: <white>" + error));
                return true;
            }
            org.bukkit.Bukkit.getScheduler().runTaskTimer(CraftEnginePolyfills.instance(),
                    dev.arubik.craftengine.debug.FakePlayer::drainOutbound, 20L, 20L);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>Spawned fake player <white>" + parsed[2] + "<green> in <white>"
                    + world.getName() + "<green> at " + parsed[4] + " " + parsed[5] + " " + parsed[6]));
            return true;
        });

        // /cep debug fakeplayer-remove - disconnect every fake player. Leaving one behind would
        // leave a player on the list that no human can log out.
        cases.put(new ArgumentList("debug^", "fakeplayer-remove^"), (sender, parsed) -> {
            net.minecraft.server.MinecraftServer server =
                    ((org.bukkit.craftbukkit.CraftServer) org.bukkit.Bukkit.getServer()).getServer();
            int n = dev.arubik.craftengine.debug.FakePlayer.removeAll(server);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>Removed <white>" + n + "<green> fake player(s)."));
            return true;
        });

        // /cep debug heap - a .hprof for a heap analyser AND a class histogram in plain text, which
        // is the half that can be read without one. Pauses the server while it walks the heap.
        cases.put(new ArgumentList("debug^", "heap^"), (sender, parsed) -> {
            long stamp = System.currentTimeMillis();
            java.nio.file.Path dir = CraftEnginePolyfills.instance().getDataFolder().toPath()
                    .resolve("debug");
            java.nio.file.Path hprof = dir.resolve("heap-" + stamp + ".hprof");
            java.nio.file.Path hist = dir.resolve("heap-" + stamp + ".txt");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<gray>Dumping the heap - the server will pause until it finishes."));
            String error = dev.arubik.craftengine.debug.HeapDump.dump(hprof, hist);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(error == null
                    ? "<green>Wrote <white>" + hprof.getFileName() + "<green> and <white>"
                      + hist.getFileName() + "<green> (the .txt is the readable one)."
                    : "<red>Heap dump failed: <white>" + error));
            return true;
        });

        // /cep debug unload - release ONLY what /cep debug load forced.
        cases.put(new ArgumentList("debug^", "unload^"), (sender, parsed) -> {
            int released = dev.arubik.craftengine.debug.DebugLoader.releaseAll();
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>Released <white>" + released + "<green> forced chunk(s)."));
            return true;
        });

        cases.put(new ArgumentList("show^", "placement^"), (sender, parsed) -> {
            boolean on = !dev.arubik.craftengine.contraption.ContraptionInteractionListener.PLACEMENT_DEBUG;
            dev.arubik.craftengine.contraption.ContraptionInteractionListener.PLACEMENT_DEBUG = on;
            sender.sendMessage(MiniMessage.miniMessage().deserialize(on
                    ? "<green>Placement logging ON - place a block in a contraption, then check the console."
                    : "<gray>Placement logging off."));
            return true;
        });

        // /cep show shulkers - drop the collider shulkers' invisible flag so the distance LOD tiers
        // can be seen directly. Already-spawned colliders are re-sent, so it applies live.
        cases.put(new ArgumentList("show^", "shulkers^"), (sender, parsed) -> {
            boolean shown = dev.arubik.craftengine.contraption.render.ContraptionShulkerColliderSwarm
                    .toggleShowColliders();
            sender.sendMessage(MiniMessage.miniMessage().deserialize(shown
                    ? "<green>Collider shulkers are now VISIBLE - walk toward a contraption to watch the LOD tiers (1 -> 4 -> 16 cubes per cell)."
                    : "<gray>Collider shulkers are hidden again."));
            return true;
        });

        // /cep contraption spawn <blockstate> - DEBUG: spawn a one-cell phys contraption of the given
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
                sender.sendMessage("§cNot a blockstate: §f" + input + "§c - try minecraft:iron_block or"
                        + " minecraft:oak_stairs[facing=north,half=bottom].");
                return true;
            }
            spawnDebugPhys(player, Map.of(BlockPos.ZERO, state), null, input);
            return true;
        });

        // /cep vehicle spawn <blockstate> - spawn a one-cell VEHICLE contraption in front of you and make
        // you its driver, so the steer-vehicle path (WASD thrust, A/D yaw, jump/sprint up/down) can be tested
        // without building a hull. Stand on it (the collider floor carries you) and drive.
        cases.put(new ArgumentList("vehicle^", "spawn^", String.class), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            String input = (String) parsed[2];
            net.minecraft.world.level.block.state.BlockState state;
            try {
                state = dev.arubik.craftengine.contraption.DebugPhysSpawn.parseBlockState(input);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cNot a blockstate: §f" + input + "§c - try minecraft:iron_block.");
                return true;
            }
            org.bukkit.Location eye = player.getEyeLocation();
            net.minecraft.world.phys.Vec3 anchor = dev.arubik.craftengine.contraption.DebugPhysSpawn.spawnAnchor(
                    new net.minecraft.world.phys.Vec3(eye.getX(), eye.getY(), eye.getZ()),
                    new net.minecraft.world.phys.Vec3(eye.getDirection().getX(), eye.getDirection().getY(),
                            eye.getDirection().getZ()));
            ContraptionEntity entity =
                    dev.arubik.craftengine.contraption.DebugPhysSpawn.spawn(player.getWorld(), anchor,
                            Map.of(BlockPos.ZERO, state), null,
                            Key.of("polyfills", "vehicle"));
            if (entity == null) {
                sender.sendMessage("§cNothing solid to spawn from §f" + input + "§c.");
                return true;
            }
            dev.arubik.craftengine.contraption.VehicleDriverRegistry.setDriver(entity.state().id(),
                    player.getUniqueId());
            sender.sendMessage("§bVehicle §f" + entity.state().id() + "§b spawned - you are the driver."
                    + " §7WASD to move, A/D turn, jump up, sprint down. §f/cep vehicle stop§7 to release.");
            return true;
        });

        // /cep vehicle drive - become the driver of the nearest VEHICLE contraption within 16 blocks.
        cases.put(new ArgumentList("vehicle^", "drive^"), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            ContraptionEntity best = null;
            double bestSq = 16.0 * 16.0;
            for (ContraptionEntity e :
                    ContraptionManager.all()) {
                var st = e.state();
                if (!Key.of("polyfills", "vehicle").equals(st.bearingType())
                        || !st.worldId().equals(((org.bukkit.craftbukkit.CraftWorld) player.getWorld()).getHandle().dimension())) {
                    continue;
                }
                double dx = st.x() - player.getX(), dy = st.y() - player.getY(), dz = st.z() - player.getZ();
                double d2 = dx * dx + dy * dy + dz * dz;
                if (d2 < bestSq) {
                    bestSq = d2;
                    best = e;
                }
            }
            if (best == null) {
                sender.sendMessage("§cNo vehicle contraption within 16 blocks.");
                return true;
            }
            dev.arubik.craftengine.contraption.VehicleDriverRegistry.setDriver(best.state().id(),
                    player.getUniqueId());
            sender.sendMessage("§bDriving vehicle §f" + best.state().id() + "§b. §7WASD/turn/jump/sprint.");
            return true;
        });

        // /cep vehicle stop - stop driving whatever you were.
        cases.put(new ArgumentList("vehicle^", "stop^"), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            java.util.UUID freed = dev.arubik.craftengine.contraption.VehicleDriverRegistry
                    .clearDriver(player.getUniqueId());
            sender.sendMessage(freed == null ? "§7You weren't driving anything." : "§bReleased the vehicle.");
            return true;
        });

        // /cep contraption spawn structure <namespacedKey> - DEBUG: same, but built from a vanilla
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

        // /cep contraption killall - destroy every live contraption on the server, in every world.
        // DISCARDS them: the captured blocks are NOT restored to the world (that's what disassemble is
        // for) and the persisted records are deleted so nothing comes back on reload - see ContraptionKill
        // for the full contract. Admin panic button for "contraptions are eating the tick budget".
        cases.put(new ArgumentList("contraption^", "killall^"), (sender, parsed) -> {
            if (!sender.hasPermission("cep.contraption.debug")) {
                sender.sendMessage("§cYou don't have permission to kill contraptions.");
                return true;
            }
            int live = ContraptionManager.count();
            if (live == 0) {
                sender.sendMessage("§7No live contraptions to kill.");
                return true;
            }
            int killed = dev.arubik.craftengine.contraption.ContraptionKill.killAll();
            sender.sendMessage("§cKilled §f" + killed + "§c contraption(s).§7 Their captured blocks were"
                    + " DISCARDED (not restored to the world) and their saved records deleted - nothing"
                    + " will come back on chunk reload or restart.");
            CraftEnginePolyfills.instance().getLogger()
                    .info("[contraption] killall by " + sender.getName() + " - " + killed + " destroyed");
            return true;
        });

        // /cep chainery clear - remove EVERY chain (render, hitboxes, anchors, registry). Admin cleanup, e.g.
        // for chains left orphaned/bugged. No item drops.
        cases.put(new ArgumentList("chainery^", "clear^"), (sender, parsed) -> {
            if (!sender.hasPermission("cep.contraption.debug")) {
                sender.sendMessage("§cNo tienes permiso.");
                return true;
            }
            int removed = dev.arubik.craftengine.chainery.ChainEngine.clearAll();
            try {
                dev.arubik.craftengine.chainery.ChainRegistry.saveAll(
                        CraftEnginePolyfills.instance().getDataFolder().toPath().resolve("chains.dat"));
            } catch (Throwable ignored) {
            }
            sender.sendMessage("§cRemovidas §f" + removed + "§c cadena(s).");
            return true;
        });

        // /cep contraption perf - per-phase timings + counts for the master tick loop. Instrumentation is
        // off until this is first run (it costs a nanoTime pair per phase per tick), so the first call
        // arms it and reports once the rolling window has samples.
        cases.put(new ArgumentList("contraption^", "perf^"), (sender, parsed) -> {
            if (!dev.arubik.craftengine.contraption.ContraptionPerf.enabled()) {
                dev.arubik.craftengine.contraption.ContraptionPerf.setEnabled(true);
                sender.sendMessage("§bContraption profiling ARMED§7 - run §f/cep contraption perf§7 again in"
                        + " a few seconds to read the rolling average. §8/cep contraption perf off to disarm.");
                return true;
            }
            sender.sendMessage(perfReport());
            return true;
        });

        // TEMP spawn benchmark - console-runnable, no player needed.
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
                // Dispose immediately - otherwise the bench leaks a level per spawn and later spawns
                // measure a server bogged down by hundreds of empty dimensions, not the spawn cost itself.
                if (ent != null) {
                    ContraptionManager.remove(ent.state().id());
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

        // /cep contraption perf off - disarm profiling so it stops costing anything.
        cases.put(new ArgumentList("contraption^", "perf^", "off^"), (sender, parsed) -> {
            dev.arubik.craftengine.contraption.ContraptionPerf.setEnabled(false);
            sender.sendMessage("§7Contraption profiling disarmed.");
            return true;
        });

        // /cep contraption spike-stop - cancel + despawn the running swarm spike early.
        cases.put(new ArgumentList("contraption^", "spike-stop^"), (sender, parsed) -> {
            if (sender instanceof Player player)
                dev.arubik.craftengine.contraption.SwarmSpike.stop(player);
            return true;
        });

        // Glue is now an item-based wand tool (cml:slime_glue, right-click twice to glue two
        // adjacent blocks) - see contraption.GlueWandListener. The old /cep contraption glue
        // and glue-clear commands were removed once that replaced them.

        // /cep contraption structure - report the size of the glued structure you're looking at.
        cases.put(new ArgumentList("contraption^", "structure^"), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            Block tb = player.getTargetBlockExact(8);
            if (tb == null) {
                sender.sendMessage("§cLook at a block.");
                return true;
            }
            BlockPos pos = new BlockPos(tb.getX(), tb.getY(), tb.getZ());
            Set<BlockPos> structure = dev.arubik.craftengine.contraption.glue.GlueRegistry
                    .structureAt(((org.bukkit.craftbukkit.CraftWorld) tb.getWorld()).getHandle().dimension(), pos);
            sender.sendMessage("§bStructure§7: §f" + structure.size() + "§7 block(s) glued together.");
            return true;
        });

        // /cep contraption capture-test - Phase 1 round-trip proof (CONTRAPTIONS.md §5
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
            net.minecraft.world.level.Level level = ((CraftWorld) tb.getWorld()).getHandle();
            Set<BlockPos> structure = dev.arubik.craftengine.contraption.glue.GlueRegistry
                    .structureAt(level.dimension(), bearing);

            ContraptionCapture.Result captured = ContraptionCapture
                    .capture(level, structure, bearing);
            sender.sendMessage("§bCaptured §f" + captured.level().blockCount() + "§b block(s). Blanking for 2s, then restoring...");
            ContraptionCapture.removeFromWorld(level, structure);

            org.bukkit.Bukkit.getScheduler().runTaskLater(CraftEnginePolyfills.instance(), () -> {
                ContraptionCapture.restore(level, captured.level(), bearing);
                sender.sendMessage("§aRestored §f" + captured.level().blockCount() + "§a block(s) from the captured NBT blob.");
            }, 40L);
            return true;
        });

        // /cep contraption spawn-holo - Phase 2 (CONTRAPTIONS.md §5): capture the glued
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

        // /cep contraption despawn-holo - despawn the swarm and restore the real blocks.
        cases.put(new ArgumentList("contraption^", "despawn-holo^"), (sender, parsed) -> {
            if (sender instanceof Player player) {
                dev.arubik.craftengine.contraption.HologramTest.stop(player.getWorld());
                sender.sendMessage("§7Hologram despawned, real blocks restored.");
            }
            return true;
        });

        // /cep contraption move <dx> <dy> <dz> - Phase 3 (CONTRAPTIONS.md §5): attach a
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
                            : "§cNo active hologram in this world - /cep contraption spawn-holo first.");
                    return true;
                });

        // /cep contraption nudge <dx> <dy> <dz> - debug tool: instantly shift the active
        // hologram's CONTINUOUS position by a one-shot delta (as opposed to "move", which
        // attaches a constant blocks/sec velocity behavior) - for manually testing
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
                            : "§cNo active hologram in this world - /cep contraption spawn-holo first.");
                    return true;
                });

        // /cep contraption rotate <90|180|270|-90> - debug tool: add the given number of
        // degrees to the active hologram's current yaw, for manually testing rotation (and the
        // axis-snap-on-disassemble behavior) without a real ROTATIONAL bearing spinning it.
        // This project's contraptions only ever track a single yaw around the vertical Y axis
        // (no pitch/roll - see ContraptionMath/ContraptionState), so "choosing an axis" here
        // means picking one of the 4 cardinal-facing steps rather than a genuine 3D axis.
        cases.put(new ArgumentList("contraption^", "rotate^", Integer.class), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            int degrees = (Integer) parsed[2];
            boolean ok = dev.arubik.craftengine.contraption.HologramTest.rotateYawDegrees(player.getWorld(), degrees);
            sender.sendMessage(ok
                    ? "§bRotated by §f" + degrees + "§b degrees."
                    : "§cNo active hologram in this world - /cep contraption spawn-holo first.");
            return true;
        });

        // /cep contraption miner <dx> <dy> <dz> <rpm> - Phase 5 (CONTRAPTIONS.md §5): attach
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
                            : "§cNo active hologram in this world - /cep contraption spawn-holo first.");
                    return true;
                });
        // /cep contraption join - DEBUG: teleport the executing player DIRECTLY INTO the
        // active hologram's hidden ContraptionLevel (a real cross-world Bukkit teleport, not a
        // packet illusion), positioned just above the bearing's local origin (BlockPos.ZERO -
        // ContraptionCapture always stores the bearing itself at local (0,0,0), see
        // ContraptionMath.toLocal). Lets a developer walk around INSIDE the mini-dimension to
        // inspect the raw captured blocks directly, rather than trusting the packet-mirror
        // swarms to be rendering them faithfully. Debug-only noclip power - gated on a
        // permission (falls back to op, like any unregistered Bukkit permission node).
        cases.put(new ArgumentList("contraption^", "join^"), (sender, parsed) -> {
            if (!(sender instanceof Player player))
                return true;
            if (!player.hasPermission("cep.contraption.debug")) {
                sender.sendMessage("§cYou don't have permission to noclip into a contraption's internal level.");
                return true;
            }
            // Join the contraption the player is LOOKING AT (2026-07-02 - "join debe meterte al
            // contraption que estas mirando"): raycast against every live contraption's cells via
            // the same real-world raycast ContraptionInteractionListener uses for right-click
            // routing, instead of blindly grabbing the first contraption in this world.
            net.minecraft.server.level.ServerPlayer nmsPlayer = ((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle();
            dev.arubik.craftengine.contraption.ContraptionInteractionListener.Hit hit =
                    dev.arubik.craftengine.contraption.ContraptionInteractionListener.raycast(nmsPlayer);
            ContraptionLevel level = hit != null ? hit.state().level() : null;
            if (level == null) {
                sender.sendMessage("§cYou're not looking at a contraption - aim at one and try again.");
                return true;
            }
            dev.arubik.craftengine.contraption.ContraptionJoinManager.recordPreJoinLocation(player);
            org.bukkit.Location dest = new org.bukkit.Location(level.getWorld(), 0.5, 1.0, 0.5);
            player.teleport(dest);
            sender.sendMessage("§bTeleported into the contraption's internal level§7 (local origin ~0,1,0)."
                    + " §cNote: this level's blocks/entities only ever MOVE via render-position math"
                    + " (ContraptionLevel#setTransform) - your real Bukkit position here is static and won't"
                    + " follow the contraption if it's moving/rotating. §7/cep contraption leave to return.");
            CraftEnginePolyfills.instance().getLogger()
                    .info("[contraption debug] " + player.getName() + " joined ContraptionLevel " + level.getWorld().getName());
            return true;
        });

        // /cep contraption leave - DEBUG: teleport the player back to wherever they were in the
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
                sender.sendMessage("§cYou haven't /cep contraption join'ed anything - nowhere to return to.");
                return true;
            }
            player.teleport(back);
            sender.sendMessage("§bTeleported back to the real world.");
            CraftEnginePolyfills.instance().getLogger()
                    .info("[contraption debug] " + player.getName() + " left the contraption level.");
            return true;
        });

        // /cep virtualui close - debug-only escape hatch (force-closes the sender's own open
        // VirtualUI without going through any script) - the actual sample UI is the data-driven
        // /virtualui command (cmds/virtualui_sample.json -> scripts/examples/virtualui_sample.pf),
        // NOT built here in Java: /cmds is the idiomatic way a player-facing feature like this
        // gets exposed in this codebase, same as /tpa or /warp.
        cases.put(new ArgumentList("virtualui^", "close^"), (sender, parsed) -> {
            if (!(sender instanceof Player player)) return true;
            if (!dev.arubik.craftengine.virtualui.VirtualUICameraSystem.isOpen(player)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>You don't have a VirtualUI open."));
                return true;
            }
            dev.arubik.craftengine.virtualui.VirtualUICameraSystem.hide(player);
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>VirtualUI closed (debug force-close)."));
            return true;
        });

        // /cep virtualui show - toggles per-tick particle outlines of every open widget's
        // interaction rectangle (yellow - the EXACT bounds VirtualUIClickSystem#resolveHit
        // hit-tests against) plus the click-detection marker's own box (red), so "why didn't that
        // click register" is visible instead of guessed at. Client-only, no effect on other players.
        cases.put(new ArgumentList("virtualui^", "show^"), (sender, parsed) -> {
            if (!(sender instanceof Player player)) return true;
            var session = dev.arubik.craftengine.virtualui.VirtualUICameraSystem.session(player);
            if (session == null) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>You don't have a VirtualUI open."));
                return true;
            }
            boolean next = !session.showHitboxes();
            session.setShowHitboxes(next);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(next
                    ? "<yellow>Hitbox outlines: <green>ON</green> - <gray>yellow = widget bounds, red = click marker."
                    : "<yellow>Hitbox outlines: <red>OFF</red>."));
            return true;
        });

        // /cep virtualui reload - re-reads virtualui.yml from disk (sensitivity, max_offset,
        // max_yaw_degrees/max_pitch_degrees, cursor.front_margin, cursor.click_marker_size, prediction, cursor
        // states, everything) with NO server restart needed. Every read of VirtualUIConfig.get()
        // happens fresh per-tick/per-packet (nothing caches a stale copy at session-open time), so
        // an already-open VirtualUI picks up the new values immediately too - safe to iterate on
        // tuning live while testing.
        cases.put(new ArgumentList("virtualui^", "reload^"), (sender, parsed) -> {
            try {
                dev.arubik.craftengine.virtualui.VirtualUIConfig.load(
                        CraftEnginePolyfills.instance().getDataFolder(), CraftEnginePolyfills.instance().getClass().getClassLoader());
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>virtualui.yml reloaded."));
            } catch (Throwable t) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Reload failed: " + t.getMessage()));
                CraftEnginePolyfills.instance().getLogger().log(java.util.logging.Level.WARNING, "[VirtualUI] reload failed", t);
            }
            return true;
        });
    }

    private static String fmt(double d) {
        return String.format("%.3f", d);
    }

    /**
     * Renders {@link dev.arubik.craftengine.contraption.ContraptionPerf}'s rolling averages. Times are the
     * mean MILLISECONDS per tick spent in each phase - a tick's whole budget is 50ms, so the total here is
     * directly comparable against it (and against the ~2.5ms that separates 20.0 TPS from 19.5).
     */
    private static String perfReport() {
        int samples = dev.arubik.craftengine.contraption.ContraptionPerf.sampleCount();
        if (samples == 0) {
            return "§7Profiling armed but no ticks sampled yet - try again in a second.";
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
        sb.append("§7 contraptions: §f").append(ContraptionManager.count())
                .append("§7 live - §a").append(fmt(dev.arubik.craftengine.contraption.ContraptionPerf.meanRendered()))
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
        ContraptionEntity entity = dev.arubik.craftengine.contraption.DebugPhysSpawn
                .spawn(player.getWorld(), anchor, states, blockEntities);
        if (entity == null) {
            player.sendMessage("§cNothing solid to spawn from §f" + label + "§c.");
            return;
        }
        player.sendMessage("§bPhysContraption §f" + entity.state().id() + "§b - §f" + entity.state().level().blockCount()
                + "§b cell(s) from §f" + label + "§b, dropped at §f" + fmt(anchor.x) + ", " + fmt(anchor.y) + ", "
                + fmt(anchor.z) + "§b.");
    }


    private static void reloadMsg(CommandSender sender, String what, String detail) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>\u2714 <white>reloaded <aqua>" + what + "<gray>: " + detail));
        CraftEnginePolyfills.instance().getLogger()
                .info("[reload] " + what + ": " + detail + " (by " + sender.getName() + ")");
    }

    private static void reloadFail(CommandSender sender, String what, Throwable t) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                "<red>\u2718 reload " + what + " failed: <white>" + t));
        CraftEnginePolyfills.instance().getLogger()
                .log(java.util.logging.Level.SEVERE, "[reload] " + what + " failed", t);
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
