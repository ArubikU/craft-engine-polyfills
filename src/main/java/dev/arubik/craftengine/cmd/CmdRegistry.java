package dev.arubik.craftengine.cmd;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;
import dev.arubik.craftengine.script.types.entity.PlayerType;
import dev.arubik.craftengine.script.types.world.ServerType;
import dev.arubik.craftengine.script.types.world.WorldType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Builds a Brigadier command tree from every loaded {@link CmdDefinition} and registers it via
 * Paper's {@code LifecycleEvents.COMMANDS} — the whole {@code /cmds} feature's Java side. See
 * {@link CmdDefinition}'s javadoc for the JSON shape this reads, and {@link CmdArgDef} for the
 * argument-type/suggestion vocabulary.
 *
 * <p>Supported {@code "type"} values: {@code string}, {@code greedy_string}/{@code greedy},
 * {@code integer}/{@code int}, {@code double}/{@code number}, {@code bool}/{@code boolean},
 * {@code uuid}, {@code player} (single, resolved to a real online player), {@code players}
 * (multiple), {@code entity} (single, any entity), {@code entities} (multiple), {@code world},
 * {@code location}/{@code position} (fine, double-precision, relative to the sender), {@code
 * block_pos} (integer block coordinates) — everything beyond the arithmetic primitives is bound
 * as the matching script type ({@code Player}/{@code Entity}/{@code World}/{@code Location} —
 * see {@code extractArg}) rather than a raw value, since a script calling e.g.
 * {@code Cmd.arg("target").send_message(...)} is the entire point of typing an argument as
 * {@code "player"} instead of {@code "string"} in the first place.
 *
 * <p><b>Reload caveat:</b> {@code LifecycleEvents.COMMANDS} fires exactly once per plugin enable
 * and snapshots {@link #DEFINITIONS} at that moment — unlike this addon's other *.json registries,
 * a NEW command file dropped in {@code cmds/} needs a server restart before Brigadier picks it up.
 * Editing an EXISTING command's {@code execute} script, on the other hand, takes effect
 * immediately (the tree only captures which script ref to call, not its contents).
 */
public final class CmdRegistry {

    private static volatile List<CmdDefinition> DEFINITIONS = List.of();
    private static boolean registeredHandler = false;

    private CmdRegistry() {}

    public static void setDefinitions(List<CmdDefinition> defs) {
        DEFINITIONS = List.copyOf(defs);
    }

    /** Swaps in ONE freshly-reparsed {@code CmdDefinition} (matched by top-level name), leaving
     *  every other loaded command's object identity untouched — {@link CmdDefinitionLoader#loadOne}'s
     *  targeted counterpart to {@link #setDefinitions} replacing the whole list. A name with no
     *  existing match is appended (still restart-bound before Brigadier picks it up as a NEW
     *  command — see this class's own reload-caveat javadoc). */
    public static synchronized void replaceOne(CmdDefinition def) {
        List<CmdDefinition> current = new java.util.ArrayList<>(DEFINITIONS);
        for (int i = 0; i < current.size(); i++) {
            if (current.get(i).name().equals(def.name())) {
                current.set(i, def);
                DEFINITIONS = List.copyOf(current);
                return;
            }
        }
        current.add(def);
        DEFINITIONS = List.copyOf(current);
    }

    public static List<CmdDefinition> all() {
        return DEFINITIONS;
    }

    public static synchronized void registerLifecycleHandler(CraftEnginePolyfills plugin) {
        if (registeredHandler) return;
        registeredHandler = true;
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            for (CmdDefinition def : DEFINITIONS) {
                try {
                    LiteralCommandNode<CommandSourceStack> node = buildRoot(def);
                    event.registrar().register(node, def.description() == null ? "" : def.description(), def.aliases());
                } catch (Throwable t) {
                    plugin.getLogger().log(Level.SEVERE, "[Cmd] failed to register /" + def.name(), t);
                }
            }
        });
    }

    private static LiteralCommandNode<CommandSourceStack> buildRoot(CmdDefinition def) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(def.name());
        applyPermission(root, def);
        attach(root, def, List.of(def.name()));
        return root.build();
    }

    private static void applyPermission(ArgumentBuilder<CommandSourceStack, ?> node, CmdDefinition def) {
        if (def.permission() == null || def.permission().isBlank()) return;
        String perm = def.permission();
        node.requires(source -> source.getSender().hasPermission(perm));
    }

    /** Attaches {@code def}'s own arg chain (if any) directly onto {@code node}, then attaches
     *  every subcommand as a SIBLING literal branch off {@code node} (not nested after the arg
     *  chain) — so {@code /tpa <player>} and {@code /tpa accept} are alternative first branches,
     *  not "accept" only reachable after typing a player name. */
    private static void attach(ArgumentBuilder<CommandSourceStack, ?> node, CmdDefinition def, List<String> path) {
        if (!def.args().isEmpty()) {
            chainArgs(node, def, def.args(), 0, path);
        } else if (def.execute() != null || def.gui() != null || !def.pages().isEmpty()) {
            node.executes(ctx -> runExecute(path, def, def.args(), ctx));
        }
        for (CmdDefinition sub : def.subcommands()) {
            LiteralArgumentBuilder<CommandSourceStack> subNode = Commands.literal(sub.name());
            applyPermission(subNode, sub);
            List<String> subPath = new ArrayList<>(path);
            subPath.add(sub.name());
            attach(subNode, sub, List.copyOf(subPath));
            node.then(subNode);
        }
    }

    private static void chainArgs(ArgumentBuilder<CommandSourceStack, ?> node, CmdDefinition def,
                                   List<CmdArgDef> args, int idx, List<String> path) {
        if (idx >= args.size()) {
            if (def.execute() != null || def.gui() != null || !def.pages().isEmpty()) node.executes(ctx -> runExecute(path, def, args, ctx));
            return;
        }
        CmdArgDef a = args.get(idx);
        if (a.isLiteral()) {
            LiteralArgumentBuilder<CommandSourceStack> lit = Commands.literal(a.literal());
            chainArgs(lit, def, args, idx + 1, path);
            node.then(lit);
        } else {
            RequiredArgumentBuilder<CommandSourceStack, ?> req = Commands.argument(a.name(), argumentType(a.type()));
            SuggestionProvider<CommandSourceStack> suggester = suggesterFor(def, args, a);
            if (suggester != null) req.suggests(suggester);
            chainArgs(req, def, args, idx + 1, path);
            node.then(req);
        }
    }

    private static ArgumentType<?> argumentType(String type) {
        return switch (type) {
            case "integer", "int" -> IntegerArgumentType.integer();
            case "double", "number" -> DoubleArgumentType.doubleArg();
            case "bool", "boolean" -> BoolArgumentType.bool();
            case "greedy_string", "greedy" -> StringArgumentType.greedyString();
            case "uuid" -> ArgumentTypes.uuid();
            case "player" -> ArgumentTypes.player();
            case "players" -> ArgumentTypes.players();
            case "entity" -> ArgumentTypes.entity();
            case "entities" -> ArgumentTypes.entities();
            case "world" -> ArgumentTypes.world();
            case "location", "position" -> ArgumentTypes.finePosition();
            case "block_pos" -> ArgumentTypes.blockPosition();
            default -> StringArgumentType.string();
        };
    }

    /** {@code choices} (a fixed list) or {@code suggest} (a {@code "file.pf:function"} ref
     *  returning a fresh list every keystroke, with every EARLIER argument on this same command
     *  already resolved and readable via {@code Cmd.arg(...)}) — either way this only shapes
     *  tab-complete, never what's actually accepted (that's still {@code argumentType}). Returns
     *  {@code null} (no override) when the arg declares neither. */
    private static SuggestionProvider<CommandSourceStack> suggesterFor(CmdDefinition def, List<CmdArgDef> args, CmdArgDef current) {
        if (current.choices() == null && current.suggest() == null) return null;
        return (ctx, builder) -> {
            try {
                List<String> options = current.choices() != null ? current.choices() : dynamicChoices(def, args, current, ctx);
                String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
                for (String opt : options) {
                    if (opt != null && opt.toLowerCase(Locale.ROOT).startsWith(remaining)) builder.suggest(opt);
                }
            } catch (Throwable ignored) {
                // A broken suggest script must never break tab-complete for the WHOLE command —
                // worst case the player just sees no suggestions for this one argument.
            }
            return builder.buildFuture();
        };
    }

    /** Calls {@code current.suggest()} with every arg BEFORE {@code current} already resolved
     *  (best-effort — one not yet typed, or belonging to a sibling branch, is simply absent from
     *  {@code Cmd.arg(...)} for that call) and reads its return value as an array of strings. */
    private static List<String> dynamicChoices(CmdDefinition def, List<CmdArgDef> args, CmdArgDef current,
                                                CommandContext<CommandSourceStack> ctx) {
        Map<String, ScriptValue> partial = new LinkedHashMap<>();
        for (CmdArgDef a : args) {
            if (a == current) break;
            if (a.isLiteral()) continue;
            try {
                partial.put(a.name(), extractArg(ctx, a));
            } catch (Throwable ignored) {
                // Not resolvable yet (still being typed, or on a sibling branch) — leave it unset.
            }
        }
        CommandSender sender = ctx.getSource().getSender();
        ScriptContext.Builder b = ScriptContext.builder();
        b.typedAll(dev.arubik.craftengine.script.ScriptBootstrap.globalSingletons());
        boolean isPlayer = sender instanceof Player;
        if (isPlayer) b.player(((CraftPlayer) sender).getHandle());
        b.typed("Cmd", new CmdInvocation(def.name(), sender.getName(), isPlayer, partial, isPlayer ? (Player) sender : null));
        ScriptCall call = ScriptCall.parse(current.suggest());
        if (call == null) return List.of();
        ScriptValue result = call.evaluate(b.build());
        List<String> out = new ArrayList<>();
        if (result instanceof ScriptValue.Array arr) {
            for (ScriptValue v : arr.elements()) out.add(v.asStr());
        }
        return out;
    }

    private static int runExecute(List<String> path, CmdDefinition def, List<CmdArgDef> argDefs, CommandContext<CommandSourceStack> ctx) {
        // Brigadier's executor lambda (see #registerLifecycleHandler) closes over the CmdDefinition
        // instance that existed at plugin-enable time — `/cep reload cmds` swaps out #DEFINITIONS
        // wholesale, but that stale closure keeps pointing at the OLD object, so its "pages"/
        // "execute"/"gui" content never picked up the reload even though the tree structure (name/
        // args/subcommands) legitimately can't change without a restart. Re-resolve against the
        // CURRENT DEFINITIONS here so content edits (pages, execute/gui script refs) take effect
        // immediately, same as editing a .pf file already does.
        //
        // MUST walk the exact root->leaf PATH, not just match `def.name()` globally — two
        // different top-level commands can each declare a subcommand with the same leaf name
        // (e.g. both "warps" and "tpa" have a "list" subcommand); a bare name search would
        // silently resolve to whichever one happens to appear earlier in DEFINITIONS, running the
        // WRONG command's content (observed live: "/warps list" opened tpa's request-list GUI).
        CmdDefinition fresh = resolveByPath(path);
        if (fresh != null) def = fresh;
        CommandSender sender = ctx.getSource().getSender();
        Map<String, ScriptValue> argMap = new LinkedHashMap<>();
        for (CmdArgDef a : argDefs) {
            if (a.isLiteral()) continue;
            try {
                argMap.put(a.name(), extractArg(ctx, a));
            } catch (IllegalArgumentException | CommandSyntaxException ignored) {
                // Not present on THIS matched path (a sibling literal/branch was taken instead), or
                // a selector-based arg (player/entity) failed to resolve — Cmd.arg(...) returns
                // NULL for it either way, which is the correct behavior.
            }
        }
        boolean isPlayer = sender instanceof Player;
        ScriptContext.Builder b = ScriptContext.builder();
        b.typedAll(dev.arubik.craftengine.script.ScriptBootstrap.globalSingletons());
        if (isPlayer) b.player(((CraftPlayer) sender).getHandle());
        b.typed("Cmd", new CmdInvocation(def.name(), sender.getName(), isPlayer, argMap, isPlayer ? (Player) sender : null));
        ScriptContext builtCtx = b.build();
        if (def.execute() != null) {
            ScriptCall call = ScriptCall.parse(def.execute());
            if (call != null) {
                try {
                    call.execute(builtCtx);
                } catch (Throwable t) {
                    CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[Cmd] /" + def.name() + " script threw", t);
                }
            }
        }
        if (def.gui() != null) {
            ScriptCall guiCall = ScriptCall.parse(def.gui());
            if (guiCall != null) {
                try {
                    guiCall.execute(builtCtx);
                } catch (Throwable t) {
                    CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[Cmd] /" + def.name() + " gui script threw", t);
                }
            }
        }
        if (!def.pages().isEmpty()) {
            // A "pages" GUI is inherently player-only — console and command-block senders have no
            // inventory to show it in. Unlike "gui" (a .pf script that can decide for itself how
            // to handle a non-player sender), Java controls this path directly, so it must check.
            if (!(sender instanceof Player p)) {
                sender.sendMessage("This command can only be used by a player.");
                return Command.SINGLE_SUCCESS;
            }
            try {
                openDeclarativePage(def, def.pages().get(0), p);
            } catch (Throwable t) {
                CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[Cmd] /" + def.name() + " page open threw", t);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    // ---- Declarative "pages" (see CmdPageDef's javadoc) -------------------------------------

    /** Builds a fresh {@code ScriptMenu} straight from {@code page}'s wrapped {@code
     *  MachineDefinition.PageDef} — same static/generator content a machine page supports — and
     *  shows it to {@code bukkitPlayer}. Called both for the command's own initial page (from
     *  {@link #runExecute}) and for {@code "page:<name>"} in-menu navigation (see {@link
     *  #installClickable}), which is why this rebuilds a fresh context every time rather than
     *  reusing whatever context happened to be live at the original command invocation — a page
     *  reached by clicking through several others should see current state, not a stale snapshot. */
    private static void openDeclarativePage(CmdDefinition def, CmdPageDef page, Player bukkitPlayer) {
        dev.arubik.craftengine.machine.MachineDefinition.PageDef p = page.page();
        ScriptContext ctx = buildPageContext(def, bukkitPlayer);
        int size = p.isChestType() ? p.resolvedSize() : 27;
        // Same {Images.from('cml:xyz')}/MiniMessage title template every machine page's "title"
        // field already supports (see DataMachineBlockEntity#buildTitleComponent) — a page can use
        // a real ported GUI background image exactly like a machine menu does, not just plain text.
        org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, size,
                dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity.buildTitleComponent(
                        resolveText(p.title(), ctx), null, -8, ctx));
        dev.arubik.craftengine.menu.ScriptMenu menu = new dev.arubik.craftengine.menu.ScriptMenu(inv);

        List<dev.arubik.craftengine.machine.MachineDefinition.PageDef.StaticSlot> layout =
                new ArrayList<>(p.layout());
        if (p.layoutGenerator() != null) {
            layout.addAll(dev.arubik.craftengine.machine.menu.GeneratedPageContent.layout(p.layoutGenerator(), ctx));
        }
        for (var slot : layout) {
            org.bukkit.inventory.ItemStack item = slot.customIcon() != null
                    ? dev.arubik.craftengine.machine.menu.MenuText.iconItem(
                        slot.customIcon(), parseText(resolveText(slot.name(), ctx)), loreComponents(slot.lore(), ctx))
                    : dev.arubik.craftengine.machine.menu.MenuText.iconItem(
                        parseKey(slot.item()), vanillaFallback(slot.item()),
                        parseText(resolveText(slot.name(), ctx)), loreComponents(slot.lore(), ctx));
            installClickable(menu, def, bukkitPlayer, slot.slot(), item, slot.action());
        }

        List<dev.arubik.craftengine.machine.MachineDefinition.ButtonSpec> buttons =
                new ArrayList<>(p.buttons());
        if (p.buttonsGenerator() != null) {
            buttons.addAll(dev.arubik.craftengine.machine.menu.GeneratedPageContent.buttons(p.buttonsGenerator(), ctx));
        }
        for (var btn : buttons) {
            org.bukkit.inventory.ItemStack item = btn.customIcon() != null
                    ? dev.arubik.craftengine.machine.menu.MenuText.iconItem(
                        btn.customIcon(), parseText(resolveText(btn.name(), ctx)), loreComponents(btn.lore(), ctx))
                    : dev.arubik.craftengine.machine.menu.MenuText.iconItem(
                        parseKey(btn.icon()), vanillaFallback(btn.icon()),
                        parseText(resolveText(btn.name(), ctx)), loreComponents(btn.lore(), ctx));
            installClickable(menu, def, bukkitPlayer, btn.slot(), item, btn.action());
        }

        menu.bindExtra("Cmd", new CmdInvocation(def.name(), bukkitPlayer.getName(), true, Map.of(), bukkitPlayer));
        bukkitPlayer.openInventory(inv);
        dev.arubik.craftengine.menu.ScriptMenuRegistry.track(inv, menu);
    }

    /** {@code action == null} → purely decorative. {@code "page:<name>"} → Java-side in-menu
     *  navigation to another of {@code def}'s pages, no script involved at all. Anything else →
     *  a normal {@code "file.pf:function"} click script, dispatched later by {@code
     *  ScriptMenuListener} with the usual {@code MenuClick}/{@code Player} context (NOT this
     *  method's page-build context — a click happens well after the page was built). */
    private static void installClickable(dev.arubik.craftengine.menu.ScriptMenu menu, CmdDefinition def,
                                          Player bukkitPlayer, int slot, org.bukkit.inventory.ItemStack item, String action) {
        if (action != null && action.startsWith("page:")) {
            String target = action.substring("page:".length());
            menu.setItem(slot, item, null, null);
            menu.setJavaAction(slot, () -> openPageByName(def, target, bukkitPlayer));
        } else {
            menu.setItem(slot, item, action, null);
        }
    }

    private static void openPageByName(CmdDefinition def, String name, Player bukkitPlayer) {
        for (CmdPageDef p : def.pages()) {
            if (p.name().equals(name)) {
                openDeclarativePage(def, p, bukkitPlayer);
                return;
            }
        }
    }

    /** {@code Cmd.open_page(name)}'s Java side (see {@link CmdInvocation#openPage}) — looks up a
     *  TOP-LEVEL command by name (declarative pages are only supported on a root command or one of
     *  its subcommands invoked directly, matching how {@link #runExecute} finds {@code def} in the
     *  first place) and reopens one of its declared pages for {@code bukkitPlayer}. */
    public static boolean openPageByCommandName(String cmdName, String pageName, Player bukkitPlayer) {
        CmdDefinition def = findByName(DEFINITIONS, cmdName);
        if (def == null || def.pages().isEmpty()) return false;
        openPageByName(def, pageName, bukkitPlayer);
        return true;
    }

    private static CmdDefinition findByName(List<CmdDefinition> defs, String name) {
        for (CmdDefinition d : defs) {
            if (d.name().equals(name)) return d;
            CmdDefinition nested = findByName(d.subcommands(), name);
            if (nested != null) return nested;
        }
        return null;
    }

    /** Walks {@code path} (root name, then each subcommand name in order) down the CURRENT
     *  {@link #DEFINITIONS} tree, matching a DIRECT CHILD at each level — unlike {@link
     *  #findByName}'s global "first match anywhere" search, this can never cross into a sibling
     *  top-level command's subtree just because it happens to share a leaf name. Null if any
     *  segment no longer exists (e.g. a reload removed that subcommand). */
    private static CmdDefinition resolveByPath(List<String> path) {
        List<CmdDefinition> level = DEFINITIONS;
        CmdDefinition current = null;
        for (String segment : path) {
            current = null;
            for (CmdDefinition d : level) {
                if (d.name().equals(segment)) { current = d; break; }
            }
            if (current == null) return null;
            level = current.subcommands();
        }
        return current;
    }

    private static ScriptContext buildPageContext(CmdDefinition def, Player bukkitPlayer) {
        ScriptContext.Builder b = ScriptContext.builder();
        b.typedAll(dev.arubik.craftengine.script.ScriptBootstrap.globalSingletons());
        b.player(((CraftPlayer) bukkitPlayer).getHandle());
        b.typed("Cmd", new CmdInvocation(def.name(), bukkitPlayer.getName(), true, Map.of(), bukkitPlayer));
        return b.build();
    }

    /** {@code "cml:xyz"} → that namespace; a bare id with no {@code ":"} defaults to {@code cml}
     *  (CraftEngine's own convention, matching {@code DataMachineBlockEntity}'s private
     *  equivalent) — vanilla ids still need their {@code minecraft:} prefix written explicitly. */
    private static net.momirealms.craftengine.core.util.Key parseKey(String spec) {
        if (spec == null) return net.momirealms.craftengine.core.util.Key.of("cml", "gui_empty");
        int i = spec.indexOf(':');
        return i < 0 ? net.momirealms.craftengine.core.util.Key.of("cml", spec)
                : net.momirealms.craftengine.core.util.Key.of(spec.substring(0, i), spec.substring(i + 1));
    }

    /** {@code MenuText.iconItem(Key, Material fallback, ...)} only ever falls back to its
     *  {@code fallback} param when {@code id} ISN'T a real CraftEngine custom item — which is
     *  ALWAYS true for a plain vanilla icon like {@code "minecraft:barrier"} (CraftEngine has no
     *  custom item registered under the "minecraft" namespace). Passing a hardcoded PAPER fallback
     *  there meant every vanilla-material icon in a /cmds page silently rendered as paper — this
     *  resolves the SAME id's path segment as a real {@link org.bukkit.Material} instead, so a
     *  vanilla id actually shows as itself when it isn't a CraftEngine custom item, and a genuine
     *  CraftEngine custom item (resolved first, inside iconItem itself) is completely unaffected. */
    private static org.bukkit.Material vanillaFallback(String spec) {
        if (spec == null) return org.bukkit.Material.PAPER;
        String path = spec.contains(":") ? spec.substring(spec.indexOf(':') + 1) : spec;
        org.bukkit.Material m = org.bukkit.Material.matchMaterial(path);
        return m != null ? m : org.bukkit.Material.PAPER;
    }

    /** A name/lore value containing {@code ".pf:"} is a script ref, evaluated ONCE at page-build
     *  time (declarative pages are one-shot, unlike a machine's periodically-refreshed menu — see
     *  {@code CmdPageDef}'s javadoc) via {@code ScriptCall#evaluate}; anything else is literal
     *  text, exactly like a machine page's own {@code "name"}/{@code "lore"} fields. */
    private static String resolveText(String raw, ScriptContext ctx) {
        if (raw == null || !raw.contains(".pf:")) return raw;
        ScriptCall call = ScriptCall.parse(raw);
        if (call == null) return raw;
        ScriptValue v = call.evaluate(ctx);
        return v == ScriptValue.NULL ? raw : v.asStr();
    }

    /** Same MiniMessage-if-tagged / legacy-ampersand-otherwise heuristic every other text method
     *  this session added already uses (see {@code PlayerType#send_message}). */
    private static net.kyori.adventure.text.Component parseText(String raw) {
        if (raw == null) return net.kyori.adventure.text.Component.empty();
        if (raw.contains("<") && raw.contains(">")) {
            try { return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(raw); }
            catch (Throwable ignored) {}
        }
        return net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(raw);
    }

    private static net.kyori.adventure.text.Component[] loreComponents(List<String> lore, ScriptContext ctx) {
        if (lore == null || lore.isEmpty()) return new net.kyori.adventure.text.Component[0];
        net.kyori.adventure.text.Component[] out = new net.kyori.adventure.text.Component[lore.size()];
        for (int i = 0; i < lore.size(); i++) out[i] = parseText(resolveText(lore.get(i), ctx));
        return out;
    }

    private static ScriptValue extractArg(CommandContext<CommandSourceStack> ctx, CmdArgDef a) throws CommandSyntaxException {
        return switch (a.type()) {
            case "integer", "int" -> ScriptValue.of(IntegerArgumentType.getInteger(ctx, a.name()));
            case "double", "number" -> ScriptValue.of(DoubleArgumentType.getDouble(ctx, a.name()));
            case "bool", "boolean" -> ScriptValue.of(BoolArgumentType.getBool(ctx, a.name()));
            case "uuid" -> ScriptValue.of(ctx.getArgument(a.name(), java.util.UUID.class).toString());
            case "player" -> extractPlayer(ctx, a.name());
            case "players" -> extractPlayers(ctx, a.name());
            case "entity" -> extractEntity(ctx, a.name());
            case "entities" -> extractEntities(ctx, a.name());
            case "world" -> extractWorld(ctx, a.name());
            case "location", "position" -> extractPosition(ctx, a.name());
            case "block_pos" -> extractBlockPos(ctx, a.name());
            default -> ScriptValue.of(StringArgumentType.getString(ctx, a.name()));
        };
    }

    private static ScriptValue extractPlayer(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        PlayerSelectorArgumentResolver resolver = ctx.getArgument(name, PlayerSelectorArgumentResolver.class);
        List<Player> resolved = resolver.resolve(ctx.getSource());
        if (resolved.isEmpty()) return ScriptValue.NULL;
        return PlayerType.wrap(((CraftPlayer) resolved.get(0)).getHandle());
    }

    private static ScriptValue extractPlayers(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        PlayerSelectorArgumentResolver resolver = ctx.getArgument(name, PlayerSelectorArgumentResolver.class);
        List<Player> resolved = resolver.resolve(ctx.getSource());
        List<ScriptValue> out = new ArrayList<>(resolved.size());
        for (Player p : resolved) out.add(PlayerType.wrap(((CraftPlayer) p).getHandle()));
        return new ScriptValue.Array(out);
    }

    private static ScriptValue extractEntity(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        EntitySelectorArgumentResolver resolver = ctx.getArgument(name, EntitySelectorArgumentResolver.class);
        List<Entity> resolved = resolver.resolve(ctx.getSource());
        if (resolved.isEmpty()) return ScriptValue.NULL;
        return EntityType.wrap(((CraftEntity) resolved.get(0)).getHandle());
    }

    private static ScriptValue extractEntities(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        EntitySelectorArgumentResolver resolver = ctx.getArgument(name, EntitySelectorArgumentResolver.class);
        List<Entity> resolved = resolver.resolve(ctx.getSource());
        List<ScriptValue> out = new ArrayList<>(resolved.size());
        for (Entity e : resolved) out.add(EntityType.wrap(((CraftEntity) e).getHandle()));
        return new ScriptValue.Array(out);
    }

    private static ScriptValue extractWorld(CommandContext<CommandSourceStack> ctx, String name) {
        World world = ctx.getArgument(name, World.class);
        return WorldType.wrap(((CraftWorld) world).getHandle());
    }

    private static ScriptValue extractPosition(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver resolver =
                ctx.getArgument(name, io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver.class);
        io.papermc.paper.math.FinePosition pos = resolver.resolve(ctx.getSource());
        World world = ctx.getSource().getLocation().getWorld();
        return dev.arubik.craftengine.script.types.world.LocationType.wrap(
                ((CraftWorld) world).getHandle(), pos.x(), pos.y(), pos.z());
    }

    private static ScriptValue extractBlockPos(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver resolver =
                ctx.getArgument(name, io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver.class);
        io.papermc.paper.math.BlockPosition pos = resolver.resolve(ctx.getSource());
        World world = ctx.getSource().getLocation().getWorld();
        return dev.arubik.craftengine.script.types.world.BlockType.wrap(
                ((CraftWorld) world).getHandle(),
                new net.minecraft.core.BlockPos(pos.blockX(), pos.blockY(), pos.blockZ()));
    }
}
