package dev.arubik.craftengine.script.types.entity;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
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
            .methodTyped2("get_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (Player p, String key, String type) -> {
                    dev.arubik.craftengine.script.TypedKeyBridge.Codec codec = dev.arubik.craftengine.script.TypedKeyBridge.resolve(type);
                    if (codec == null) return ScriptValue.NULL;
                    return codec.fromStorage(dev.arubik.craftengine.util.PlayerFlags.getTyped(p.getUUID(), TYPED_PREFIX + key));
                })
            .methodTyped3("set_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Player p, String key, String type, ScriptValue value) -> {
                    dev.arubik.craftengine.script.TypedKeyBridge.Codec codec = dev.arubik.craftengine.script.TypedKeyBridge.resolve(type);
                    if (codec == null) return false;
                    try {
                        dev.arubik.craftengine.util.PlayerFlags.setTyped(p.getUUID(), TYPED_PREFIX + key, codec.toStorage(value));
                        return true;
                    } catch (Throwable ignored) { return false; }
                })
            // has_typed's second argument (the type name) is accepted but unused — the presence
            // check doesn't need a codec. Kept in the signature so callers pass the same (key, type)
            // pair they pass to get_typed/set_typed.
            .methodTyped2("has_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Player p, String key, String type) ->
                    dev.arubik.craftengine.util.PlayerFlags.hasTyped(p.getUUID(), TYPED_PREFIX + key))
            .property("food_level", obj -> ScriptValue.of(player(obj).getFoodData().getFoodLevel()))
            .property("saturation", obj -> ScriptValue.of(player(obj).getFoodData().getSaturationLevel()))
            .property("xp_level", obj -> ScriptValue.of(player(obj).experienceLevel))
            .property("xp_progress", obj -> ScriptValue.of(player(obj).experienceProgress))
            // total_exp/give_exp/take_exp/has_exp — a ready-made "currency" for any feature that
            // wants a cost/reward without this addon inventing its own economy or depending on an
            // external Vault-style plugin (e.g. /warps' create/teleport/sponsor-slot costs).
            .property("total_exp", obj -> ScriptValue.of(((org.bukkit.entity.Player) player(obj).getBukkitEntity()).getTotalExperience()))
            .methodTyped1("give_exp", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (Player p, Double amount) -> {
                    try {
                        ((org.bukkit.entity.Player) p.getBukkitEntity()).giveExp(amount.intValue());
                        return true;
                    } catch (Throwable t) { return false; }
                })
            .methodTyped1("has_exp", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (Player p, Double amount) ->
                    ((org.bukkit.entity.Player) p.getBukkitEntity()).getTotalExperience() >= amount.intValue())
            // take_exp(amount) — fails (false, no deduction) if the player doesn't have enough,
            // so a caller can charge-then-act in one call: `if (Player.take_exp(cost)) { ... }`.
            .methodTyped1("take_exp", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (Player p, Double requested) -> {
                    try {
                        org.bukkit.entity.Player bp = (org.bukkit.entity.Player) p.getBukkitEntity();
                        int amount = requested.intValue();
                        if (amount < 0 || bp.getTotalExperience() < amount) return false;
                        bp.giveExp(-amount);
                        return true;
                    } catch (Throwable t) { return false; }
                })
            // close_inventory() — the generic "Quit"/close-menu button action this scripting API
            // was otherwise missing entirely (every menu system here relies on the player closing
            // the inventory themselves or navigating to another page).
            // Migrated to the typed-registration API (PolyType.methodTyped0, see
            // dev.arubik.craftengine.script.TypeCodecs) as a second prototype call site — 0 args,
            // and its real body genuinely calls out to a Bukkit method after unwrapping the NMS
            // Player, proving the typed API can represent that shape cleanly.
            .methodTyped0("close_inventory", TypeCodecs.BOOL,
                (Player p) -> {
                    try {
                        ((org.bukkit.entity.Player) p.getBukkitEntity()).closeInventory();
                        return true;
                    } catch (Throwable t) { return false; }
                })
            // has_permission(node) — the generic gate a data-driven feature (e.g. /warps' sponsor-
            // locked extra warp slots) needs to distinguish "free tier" from "unlocked" content
            // without any bespoke rank/economy concept of its own.
            .methodTyped1("has_permission", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Player p, String node) -> {
                    try {
                        return ((org.bukkit.entity.Player) p.getBukkitEntity()).hasPermission(node);
                    } catch (Throwable t) { return false; }
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
            .methodTyped1("set_allow_flight", TypeCodecs.BOOL, TypeCodecs.BOOL, false,
                (Player p, Boolean value) -> {
                    p.getAbilities().mayfly = value;
                    if (!value) p.getAbilities().flying = false;
                    syncAbilities(p);
                    return true;
                })
            .methodTyped1("set_flying", TypeCodecs.BOOL, TypeCodecs.BOOL, false,
                (Player p, Boolean value) -> {
                    p.getAbilities().flying = value && p.getAbilities().mayfly;
                    syncAbilities(p);
                    return true;
                })
            .property("main_hand", obj -> ScriptValue.ofItem(player(obj).getMainHandItem()))
            .property("off_hand", obj -> ScriptValue.ofItem(player(obj).getOffhandItem()))
            .methodTyped1("send_message", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Player player, String text) -> {
                    if (player instanceof ServerPlayer sp) {
                        try {
                            Component msg;
                            if (text.contains("<") && text.contains(">")) {
                                msg = MiniMessage.miniMessage().deserialize(text);
                            } else {
                                msg = LegacyComponentSerializer.legacyAmpersand().deserialize(text);
                            }
                            sp.getBukkitEntity().sendMessage(msg);
                            return true;
                        } catch (Throwable ignored) {
                            try { sp.getBukkitEntity().sendMessage(text); return true; } catch (Throwable e2) {}
                        }
                    }
                    return false;
                })
            // give_item's argument is genuinely dynamic (an Item value or an Obj wrapping a raw
            // ItemStack), so its slot stays TypeCodecs.RAW — the instance/return are still native.
            .methodTyped1("give_item", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Player p, ScriptValue itemArg) -> {
                    ItemStack toGive = null;
                    if (itemArg instanceof ScriptValue.Item i) toGive = i.stack().copy();
                    else if (itemArg instanceof ScriptValue.Obj o && o.instance() instanceof ItemStack is) toGive = is.copy();
                    if (toGive == null || toGive.isEmpty()) return false;
                    boolean added = p.getInventory().add(toGive);
                    if (!added) p.drop(toGive, false);
                    return true;
                })
            // remove_item("main_hand"|"off_hand"|slotN, count) — both slots optional: with no args
            // the default slot "" matches neither hand and fails Integer.parseInt, returning false
            // without touching the inventory, exactly as the old args.isEmpty() guard did.
            .methodTypedOpt2("remove_item", TypeCodecs.STRING, "", TypeCodecs.DOUBLE, 1.0, TypeCodecs.BOOL,
                (Player p, String slot, Double countArg) -> {
                int count = countArg.intValue();
                if ("main_hand".equals(slot)) {
                    ItemStack s = p.getMainHandItem();
                    s.shrink(count);
                    return true;
                } else if ("off_hand".equals(slot)) {
                    p.getOffhandItem().shrink(count);
                    return true;
                } else {
                    try {
                        int idx = Integer.parseInt(slot);
                        p.getInventory().getItem(idx).shrink(count);
                        return true;
                    } catch (Throwable ignored) {}
                }
                return false;
            })
            // has_item(id, count?) / count_item(id) / consume_item(id, count?) — search the WHOLE
            // inventory (not one named slot, unlike remove_item above) by item registry id, e.g.
            // "minecraft:nether_star" — the generic "does this player have N of X, take them if so"
            // primitive a cost like /teleporters' per-jump Nether Star charge needs, since that
            // command isn't tied to a machine's energy system at all.
            // Both take the id optionally: the default "" matches no stack (neither a CraftEngine
            // custom id nor a parseable vanilla registry id), so a no-arg call still yields
            // false/0 and consumes nothing, exactly like the old args.isEmpty() guard.
            .methodTypedOpt2("has_item", TypeCodecs.STRING, "", TypeCodecs.DOUBLE, 1.0, TypeCodecs.BOOL,
                (Player p, String id, Double need) -> countItem(p, id) >= need.intValue())
            .methodTypedOpt1("count_item", TypeCodecs.STRING, "", TypeCodecs.DOUBLE,
                (Player p, String id) -> (double) countItem(p, id))
            .methodTypedOpt2("consume_item", TypeCodecs.STRING, "", TypeCodecs.DOUBLE, 1.0, TypeCodecs.BOOL,
                (Player p, String id, Double need) -> consumeItem(p, id, need.intValue()))
            .methodTyped1("get_inventory_slot", TypeCodecs.DOUBLE, TypeCodecs.RAW, ScriptValue.NULL,
                (Player p, Double slotArg) -> {
                    int slot = slotArg.intValue();
                    if (slot < 0 || slot >= p.getInventory().getContainerSize()) return ScriptValue.NULL;
                    ItemStack stack = p.getInventory().getItem(slot);
                    return stack.isEmpty() ? ScriptValue.NULL : ScriptValue.ofItem(stack);
                })
            // General escape hatch letting a script trigger any other registered command exactly as if
            // this player typed it — e.g. a `/cmds`-defined command wanting to chain into another command.
            .methodTyped1("exec_command", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Player p, String command) -> {
                    String cmd = command;
                    if (cmd.startsWith("/")) cmd = cmd.substring(1);
                    try {
                        org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) p.getBukkitEntity();
                        return org.bukkit.Bukkit.dispatchCommand(bukkitPlayer, cmd);
                    } catch (Throwable t) {
                        return false;
                    }
                })
            // Lets a `.pf` script write Player.parse("#player_name#") (or native %player_name%) to resolve
            // a PlaceholderAPI placeholder for that player, falling back to the text unchanged when PAPI isn't installed.
            .methodTyped1("parse", TypeCodecs.STRING, TypeCodecs.STRING, "",
                (Player p, String text) -> {
                    org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) p.getBukkitEntity();
                    return dev.arubik.craftengine.util.plugins.PlaceholderSupport.set(bukkitPlayer, text);
                })
            // send_title(title, subtitle?, fade_in_ticks?, stay_ticks?, fade_out_ticks?) — a full
            // title/subtitle pair in one call (Adventure sends both together as one packet-level
            // Title anyway; there's no separate "set subtitle only" server->client action to expose).
            // Typed with a null sentinel on the required first argument: no codec decodes a PRESENT
            // argument to Java null (asStr() is total), so `titleText == null` is exactly the old
            // `args.isEmpty()` early return, checked before anything is sent. The subtitle keeps a
            // null default too so an absent one stays Component.empty() rather than parsed "".
            .methodTypedOpt5("send_title", TypeCodecs.STRING, null, TypeCodecs.STRING, null,
                TypeCodecs.DOUBLE, 10.0, TypeCodecs.DOUBLE, 70.0, TypeCodecs.DOUBLE, 20.0,
                TypeCodecs.BOOL, (Player p, String titleText, String subText,
                                  Double fadeInArg, Double stayArg, Double fadeOutArg) -> {
                if (titleText == null || !(p instanceof ServerPlayer sp)) return false;
                try {
                    Component title = parseComponent(titleText);
                    Component subtitle = subText != null ? parseComponent(subText) : Component.empty();
                    int fadeIn  = (int) (double) fadeInArg;
                    int stay    = (int) (double) stayArg;
                    int fadeOut = (int) (double) fadeOutArg;
                    net.kyori.adventure.title.Title t = net.kyori.adventure.title.Title.title(title, subtitle,
                        net.kyori.adventure.title.Title.Times.times(
                            java.time.Duration.ofMillis(fadeIn * 50L),
                            java.time.Duration.ofMillis(stay * 50L),
                            java.time.Duration.ofMillis(fadeOut * 50L)));
                    sp.getBukkitEntity().showTitle(t);
                    return true;
                } catch (Throwable t) { return false; }
            })
            .methodTyped1("send_actionbar", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Player player, String text) -> {
                    if (!(player instanceof ServerPlayer sp)) return false;
                    try {
                        sp.getBukkitEntity().sendActionBar(parseComponent(text));
                        return true;
                    } catch (Throwable t) { return false; }
                })
            // show_bossbar(text, progress?[0-1], color?, overlay?) — creates this player's tracked
            // boss bar on first call, or updates it in place on later calls (a fresh BossBar
            // instance per call would show a SECOND bar stacked on the first instead of replacing
            // it — Adventure identifies a shown bar by instance, not by player+any-other-key).
            // Typed with the same null sentinel as send_title — `textArg == null` is exactly the old
            // `args.isEmpty()` early return, checked before any bar is created. color/overlay keep
            // null defaults so an absent one uses the WHITE/PROGRESS constant, not a parsed "".
            .methodTypedOpt4("show_bossbar", TypeCodecs.STRING, null, TypeCodecs.DOUBLE, 1.0,
                TypeCodecs.STRING, null, TypeCodecs.STRING, null, TypeCodecs.BOOL,
                (Player p, String textArg, Double progressArg, String colorArg, String overlayArg) -> {
                if (textArg == null || !(p instanceof ServerPlayer sp)) return false;
                try {
                    ensureQuitCleanup();
                    org.bukkit.entity.Player bp = sp.getBukkitEntity();
                    Component text = parseComponent(textArg);
                    float progress = (float) Math.max(0.0, Math.min(1.0, progressArg));
                    net.kyori.adventure.bossbar.BossBar.Color color = colorArg != null
                        ? parseBossBarColor(colorArg) : net.kyori.adventure.bossbar.BossBar.Color.WHITE;
                    net.kyori.adventure.bossbar.BossBar.Overlay overlay = overlayArg != null
                        ? parseBossBarOverlay(overlayArg) : net.kyori.adventure.bossbar.BossBar.Overlay.PROGRESS;
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
                    return true;
                } catch (Throwable t) { return false; }
            })
            .methodTyped0("hide_bossbar", TypeCodecs.BOOL,
                (Player player) -> {
                    if (!(player instanceof ServerPlayer sp)) return false;
                    org.bukkit.entity.Player bp = sp.getBukkitEntity();
                    net.kyori.adventure.bossbar.BossBar bar = BOSSBARS.remove(bp.getUniqueId());
                    if (bar != null) { try { bp.hideBossBar(bar); } catch (Throwable ignored) {} }
                    return true;
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
