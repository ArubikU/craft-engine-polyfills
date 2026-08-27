package dev.arubik.craftengine.script.types.entity;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Player extends Entity — inherits all Entity properties and methods,
 * adds player-specific ones.
 */
public final class PlayerType {

    private PlayerType() {}

    private static final String TYPED_PREFIX = "tkey_";

    public static void register() {
        PolyTypeRegistry.define("Player", "LivingEntity")
            // --- Generic TypedKey storage — OVERRIDES Entity's PDC-on-the-live-entity version.
            // A wrapped Player script value only exists while that player is online, and its
            // underlying NMS ServerPlayer (and that instance's PersistentDataContainer) is a
            // THROWAWAY object across a respawn/relog — unlike a block entity or a plain
            // non-player Entity, there's no single persistent Bukkit-native handle to key off of
            // for a player's whole lifetime. So Player keys its typed storage by UUID into
            // PlayerFlags (own file, same shape as ServerFlags) instead, which also means it works
            // offline (see PlayerFlags) even when no wrapped Player value exists at all.
            .method("get_typed", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.NULL;
                dev.arubik.craftengine.script.TypedKeyBridge.Codec codec = dev.arubik.craftengine.script.TypedKeyBridge.resolve(args.get(1).asStr());
                if (codec == null) return ScriptValue.NULL;
                return codec.fromStorage(dev.arubik.craftengine.util.PlayerFlags.getTyped(player(obj).getUUID(), TYPED_PREFIX + args.get(0).asStr()));
            })
            .method("set_typed", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.of(false);
                dev.arubik.craftengine.script.TypedKeyBridge.Codec codec = dev.arubik.craftengine.script.TypedKeyBridge.resolve(args.get(1).asStr());
                if (codec == null) return ScriptValue.of(false);
                try {
                    dev.arubik.craftengine.util.PlayerFlags.setTyped(player(obj).getUUID(), TYPED_PREFIX + args.get(0).asStr(), codec.toStorage(args.get(2)));
                    return ScriptValue.of(true);
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            .method("has_typed", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                return ScriptValue.of(dev.arubik.craftengine.util.PlayerFlags.hasTyped(player(obj).getUUID(), TYPED_PREFIX + args.get(0).asStr()));
            })
            .property("food_level", obj -> ScriptValue.of(player(obj).getFoodData().getFoodLevel()))
            .property("saturation", obj -> ScriptValue.of(player(obj).getFoodData().getSaturationLevel()))
            .property("xp_level", obj -> ScriptValue.of(player(obj).experienceLevel))
            .property("xp_progress", obj -> ScriptValue.of(player(obj).experienceProgress))
            // total_exp/give_exp/take_exp/has_exp — a ready-made "currency" for any feature that
            // wants a cost/reward without this addon inventing its own economy or depending on an
            // external Vault-style plugin (e.g. /warps' create/teleport/sponsor-slot costs).
            .property("total_exp", obj -> ScriptValue.of(((org.bukkit.entity.Player) player(obj).getBukkitEntity()).getTotalExperience()))
            .method("give_exp", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try {
                    ((org.bukkit.entity.Player) player(obj).getBukkitEntity()).giveExp((int) args.get(0).asNum());
                    return ScriptValue.of(true);
                } catch (Throwable t) { return ScriptValue.of(false); }
            })
            .method("has_exp", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                return ScriptValue.of(((org.bukkit.entity.Player) player(obj).getBukkitEntity()).getTotalExperience() >= (int) args.get(0).asNum());
            })
            // take_exp(amount) — fails (false, no deduction) if the player doesn't have enough,
            // so a caller can charge-then-act in one call: `if (Player.take_exp(cost)) { ... }`.
            .method("take_exp", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try {
                    org.bukkit.entity.Player bp = (org.bukkit.entity.Player) player(obj).getBukkitEntity();
                    int amount = (int) args.get(0).asNum();
                    if (amount < 0 || bp.getTotalExperience() < amount) return ScriptValue.of(false);
                    bp.giveExp(-amount);
                    return ScriptValue.of(true);
                } catch (Throwable t) { return ScriptValue.of(false); }
            })
            // close_inventory() — the generic "Quit"/close-menu button action this scripting API
            // was otherwise missing entirely (every menu system here relies on the player closing
            // the inventory themselves or navigating to another page).
            // Migrated to the typed-registration API (PolyType.methodTyped0, see
            // dev.arubik.craftengine.script.TypeCodecs) as a second prototype call site — 0 args,
            // and its real body genuinely calls out to a Bukkit method after unwrapping the NMS
            // Player, proving the typed API can represent that shape cleanly.
            .methodTyped0("close_inventory", dev.arubik.craftengine.script.TypeCodecs.BOOL,
                (Player p) -> {
                    try {
                        ((org.bukkit.entity.Player) p.getBukkitEntity()).closeInventory();
                        return true;
                    } catch (Throwable t) { return false; }
                })
            // has_permission(node) — the generic gate a data-driven feature (e.g. /warps' sponsor-
            // locked extra warp slots) needs to distinguish "free tier" from "unlocked" content
            // without any bespoke rank/economy concept of its own.
            .method("has_permission", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try {
                    return ScriptValue.of(((org.bukkit.entity.Player) player(obj).getBukkitEntity())
                            .hasPermission(args.get(0).asStr()));
                } catch (Throwable t) { return ScriptValue.of(false); }
            })
            .property("gamemode", obj -> {
                if (player(obj) instanceof ServerPlayer sp) {
                    return ScriptValue.of(sp.gameMode.getGameModeForPlayer().getName());
                }
                return ScriptValue.of("survival");
            })
            .property("is_flying", obj -> ScriptValue.of(player(obj).getAbilities().flying))
            .property("is_creative", obj -> ScriptValue.of(player(obj).getAbilities().instabuild))
            .property("allow_flight", obj -> ScriptValue.of(player(obj).getAbilities().mayfly))
            // Generic vanilla-flight-ability toggles — not jetpack-specific. A script gates when
            // to grant/revoke these (fuel checks, equip state, etc.); this class never hardcodes
            // what any particular item does with them.
            .method("set_allow_flight", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                boolean value = args.get(0).asBool();
                Player p = player(obj);
                p.getAbilities().mayfly = value;
                if (!value) p.getAbilities().flying = false;
                syncAbilities(p);
                return ScriptValue.of(true);
            })
            .method("set_flying", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                Player p = player(obj);
                p.getAbilities().flying = args.get(0).asBool() && p.getAbilities().mayfly;
                syncAbilities(p);
                return ScriptValue.of(true);
            })
            .property("main_hand", obj -> ScriptValue.ofItem(player(obj).getMainHandItem()))
            .property("off_hand", obj -> ScriptValue.ofItem(player(obj).getOffhandItem()))
            .method("send_message", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                String text = args.get(0).asStr();
                if (player(obj) instanceof ServerPlayer sp) {
                    try {
                        Component msg;
                        if (text.contains("<") && text.contains(">")) {
                            msg = MiniMessage.miniMessage().deserialize(text);
                        } else {
                            msg = LegacyComponentSerializer.legacyAmpersand().deserialize(text);
                        }
                        sp.getBukkitEntity().sendMessage(msg);
                        return ScriptValue.of(true);
                    } catch (Throwable ignored) {
                        try { sp.getBukkitEntity().sendMessage(text); return ScriptValue.of(true); } catch (Throwable e2) {}
                    }
                }
                return ScriptValue.of(false);
            })
            .method("give_item", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                ScriptValue itemArg = args.get(0);
                ItemStack toGive = null;
                if (itemArg instanceof ScriptValue.Item i) toGive = i.stack().copy();
                else if (itemArg instanceof ScriptValue.Obj o && o.instance() instanceof ItemStack is) toGive = is.copy();
                if (toGive == null || toGive.isEmpty()) return ScriptValue.of(false);
                Player p = player(obj);
                boolean added = p.getInventory().add(toGive);
                if (!added) p.drop(toGive, false);
                return ScriptValue.of(true);
            })
            .method("remove_item", (obj, args) -> {
                // remove_item("main_hand"|"off_hand"|slotN, count)
                if (args.isEmpty()) return ScriptValue.of(false);
                Player p = player(obj);
                String slot = args.get(0).asStr();
                int count = args.size() >= 2 ? (int) args.get(1).asNum() : 1;
                if ("main_hand".equals(slot)) {
                    ItemStack s = p.getMainHandItem();
                    s.shrink(count);
                    return ScriptValue.of(true);
                } else if ("off_hand".equals(slot)) {
                    p.getOffhandItem().shrink(count);
                    return ScriptValue.of(true);
                } else {
                    try {
                        int idx = Integer.parseInt(slot);
                        p.getInventory().getItem(idx).shrink(count);
                        return ScriptValue.of(true);
                    } catch (Throwable ignored) {}
                }
                return ScriptValue.of(false);
            })
            // has_item(id, count?) / count_item(id) / consume_item(id, count?) — search the WHOLE
            // inventory (not one named slot, unlike remove_item above) by item registry id, e.g.
            // "minecraft:nether_star" — the generic "does this player have N of X, take them if so"
            // primitive a cost like /teleporters' per-jump Nether Star charge needs, since that
            // command isn't tied to a machine's energy system at all.
            .method("has_item", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                int need = args.size() >= 2 ? (int) args.get(1).asNum() : 1;
                return ScriptValue.of(countItem(player(obj), args.get(0).asStr()) >= need);
            })
            .method("count_item", (obj, args) ->
                ScriptValue.of(args.isEmpty() ? 0 : countItem(player(obj), args.get(0).asStr())))
            .method("consume_item", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                int need = args.size() >= 2 ? (int) args.get(1).asNum() : 1;
                return ScriptValue.of(consumeItem(player(obj), args.get(0).asStr(), need));
            })
            .method("get_inventory_slot", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                int slot = (int) args.get(0).asNum();
                Player p = player(obj);
                if (slot < 0 || slot >= p.getInventory().getContainerSize()) return ScriptValue.NULL;
                ItemStack stack = p.getInventory().getItem(slot);
                return stack.isEmpty() ? ScriptValue.NULL : ScriptValue.ofItem(stack);
            })
            // General escape hatch letting a script trigger any other registered command exactly as if
            // this player typed it — e.g. a `/cmds`-defined command wanting to chain into another command.
            .method("exec_command", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                String cmd = args.get(0).asStr();
                if (cmd.startsWith("/")) cmd = cmd.substring(1);
                try {
                    org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) player(obj).getBukkitEntity();
                    boolean ok = org.bukkit.Bukkit.dispatchCommand(bukkitPlayer, cmd);
                    return ScriptValue.of(ok);
                } catch (Throwable t) {
                    return ScriptValue.of(false);
                }
            })
            // Lets a `.pf` script write Player.parse("#player_name#") (or native %player_name%) to resolve
            // a PlaceholderAPI placeholder for that player, falling back to the text unchanged when PAPI isn't installed.
            .method("parse", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of("");
                org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) player(obj).getBukkitEntity();
                String result = dev.arubik.craftengine.util.plugins.PlaceholderSupport.set(bukkitPlayer, args.get(0).asStr());
                return ScriptValue.of(result);
            })
            // send_title(title, subtitle?, fade_in_ticks?, stay_ticks?, fade_out_ticks?) — a full
            // title/subtitle pair in one call (Adventure sends both together as one packet-level
            // Title anyway; there's no separate "set subtitle only" server->client action to expose).
            .method("send_title", (obj, args) -> {
                if (args.isEmpty() || !(player(obj) instanceof ServerPlayer sp)) return ScriptValue.of(false);
                try {
                    Component title = parseComponent(args.get(0).asStr());
                    Component subtitle = args.size() > 1 ? parseComponent(args.get(1).asStr()) : Component.empty();
                    int fadeIn  = args.size() > 2 ? (int) args.get(2).asNum() : 10;
                    int stay    = args.size() > 3 ? (int) args.get(3).asNum() : 70;
                    int fadeOut = args.size() > 4 ? (int) args.get(4).asNum() : 20;
                    net.kyori.adventure.title.Title t = net.kyori.adventure.title.Title.title(title, subtitle,
                        net.kyori.adventure.title.Title.Times.times(
                            java.time.Duration.ofMillis(fadeIn * 50L),
                            java.time.Duration.ofMillis(stay * 50L),
                            java.time.Duration.ofMillis(fadeOut * 50L)));
                    sp.getBukkitEntity().showTitle(t);
                    return ScriptValue.of(true);
                } catch (Throwable t) { return ScriptValue.of(false); }
            })
            .method("send_actionbar", (obj, args) -> {
                if (args.isEmpty() || !(player(obj) instanceof ServerPlayer sp)) return ScriptValue.of(false);
                try {
                    sp.getBukkitEntity().sendActionBar(parseComponent(args.get(0).asStr()));
                    return ScriptValue.of(true);
                } catch (Throwable t) { return ScriptValue.of(false); }
            })
            // show_bossbar(text, progress?[0-1], color?, overlay?) — creates this player's tracked
            // boss bar on first call, or updates it in place on later calls (a fresh BossBar
            // instance per call would show a SECOND bar stacked on the first instead of replacing
            // it — Adventure identifies a shown bar by instance, not by player+any-other-key).
            .method("show_bossbar", (obj, args) -> {
                if (args.isEmpty() || !(player(obj) instanceof ServerPlayer sp)) return ScriptValue.of(false);
                try {
                    ensureQuitCleanup();
                    org.bukkit.entity.Player bp = sp.getBukkitEntity();
                    Component text = parseComponent(args.get(0).asStr());
                    float progress = args.size() > 1 ? (float) Math.max(0.0, Math.min(1.0, args.get(1).asNum())) : 1.0f;
                    net.kyori.adventure.bossbar.BossBar.Color color = args.size() > 2
                        ? parseBossBarColor(args.get(2).asStr()) : net.kyori.adventure.bossbar.BossBar.Color.WHITE;
                    net.kyori.adventure.bossbar.BossBar.Overlay overlay = args.size() > 3
                        ? parseBossBarOverlay(args.get(3).asStr()) : net.kyori.adventure.bossbar.BossBar.Overlay.PROGRESS;
                    net.kyori.adventure.bossbar.BossBar bar = BOSSBARS.get(bp.getUniqueId());
                    if (bar == null) {
                        bar = net.kyori.adventure.bossbar.BossBar.bossBar(text, progress, color, overlay);
                        BOSSBARS.put(bp.getUniqueId(), bar);
                        bp.showBossBar(bar);
                    } else {
                        bar.name(text);
                        bar.progress(progress);
                        bar.color(color);
                        bar.overlay(overlay);
                    }
                    return ScriptValue.of(true);
                } catch (Throwable t) { return ScriptValue.of(false); }
            })
            .method("hide_bossbar", (obj, args) -> {
                if (!(player(obj) instanceof ServerPlayer sp)) return ScriptValue.of(false);
                org.bukkit.entity.Player bp = sp.getBukkitEntity();
                net.kyori.adventure.bossbar.BossBar bar = BOSSBARS.remove(bp.getUniqueId());
                if (bar != null) { try { bp.hideBossBar(bar); } catch (Throwable ignored) {} }
                return ScriptValue.of(true);
            });
        ensureQuitCleanup();
    }

    /** Same MiniMessage-if-tagged / legacy-ampersand-otherwise heuristic {@code send_message}
     *  already uses, shared by title/actionbar/bossbar text so every player-facing text method on
     *  this type accepts either formatting convention identically. */
    private static Component parseComponent(String text) {
        if (text != null && text.contains("<") && text.contains(">")) {
            try { return MiniMessage.miniMessage().deserialize(text); } catch (Throwable ignored) {}
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text == null ? "" : text);
    }

    private static net.kyori.adventure.bossbar.BossBar.Color parseBossBarColor(String name) {
        try { return net.kyori.adventure.bossbar.BossBar.Color.valueOf(name.trim().toUpperCase(java.util.Locale.ROOT)); }
        catch (Throwable ignored) { return net.kyori.adventure.bossbar.BossBar.Color.WHITE; }
    }

    private static net.kyori.adventure.bossbar.BossBar.Overlay parseBossBarOverlay(String name) {
        try { return net.kyori.adventure.bossbar.BossBar.Overlay.valueOf(name.trim().toUpperCase(java.util.Locale.ROOT)); }
        catch (Throwable ignored) { return net.kyori.adventure.bossbar.BossBar.Overlay.PROGRESS; }
    }

    // Per-player tracked boss bar (see show_bossbar/hide_bossbar) — cleaned up on quit below so a
    // player who disconnects without calling hide_bossbar doesn't leak an entry forever.
    private static final java.util.Map<java.util.UUID, net.kyori.adventure.bossbar.BossBar> BOSSBARS =
        new java.util.concurrent.ConcurrentHashMap<>();
    private static boolean quitCleanupRegistered = false;

    private static synchronized void ensureQuitCleanup() {
        if (quitCleanupRegistered) return;
        quitCleanupRegistered = true;
        org.bukkit.Bukkit.getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onQuit(org.bukkit.event.player.PlayerQuitEvent e) {
                BOSSBARS.remove(e.getPlayer().getUniqueId());
            }
        }, dev.arubik.craftengine.CraftEnginePolyfills.instance());
    }

    public static ScriptValue wrap(Player player) {
        if (player == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("Player", player);
    }

    private static Player player(Object obj) {
        return (Player) obj;
    }

    private static void syncAbilities(Player p) {
        if (p instanceof ServerPlayer sp) sp.onUpdateAbilities();
    }

    private static net.minecraft.world.item.Item resolveItem(String id) {
        try {
            net.minecraft.resources.Identifier ident =
                    net.minecraft.resources.Identifier.parse(id.contains(":") ? id : "minecraft:" + id);
            return net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(ident);
        } catch (Throwable ignored) { return null; }
    }

    /** Matches a slot's stack against {@code id} — a CraftEngine custom item id (checked FIRST,
     *  since a custom item's registry-level {@code net.minecraft.world.item.Item} is usually just
     *  a plain vanilla base item and would otherwise false-match any vanilla stack of that same
     *  base) or, failing that, a plain vanilla item registry id. Lets {@code has_item}/{@code
     *  count_item}/{@code consume_item} work identically for either, e.g. a CE custom "coin" item
     *  as a command's cost just as well as a vanilla "minecraft:nether_star". */
    private static boolean stackMatches(ItemStack s, String id) {
        if (s.isEmpty()) return false;
        String customId = dev.arubik.craftengine.script.types.primitive.ItemType.customItemId(s);
        if (customId != null) return customId.equals(id);
        net.minecraft.world.item.Item item = resolveItem(id);
        return item != null && s.is(item);
    }

    private static int countItem(Player p, String id) {
        var inv = p.getInventory();
        int total = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (stackMatches(s, id)) total += s.getCount();
        }
        return total;
    }

    private static boolean consumeItem(Player p, String id, int need) {
        if (need <= 0 || countItem(p, id) < need) return false;
        var inv = p.getInventory();
        int remaining = need;
        for (int i = 0; i < inv.getContainerSize() && remaining > 0; i++) {
            ItemStack s = inv.getItem(i);
            if (!stackMatches(s, id)) continue;
            int take = Math.min(remaining, s.getCount());
            s.shrink(take);
            remaining -= take;
        }
        return true;
    }
}
