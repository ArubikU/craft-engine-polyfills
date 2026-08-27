package dev.arubik.craftengine.script.types.plugins;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import dev.arubik.craftengine.script.types.entity.EntityType;
import dev.arubik.craftengine.script.types.primitive.MapType;
import dev.arubik.craftengine.util.plugins.BlueMapSupport;
import dev.arubik.craftengine.util.plugins.CitizensSupport;
import dev.arubik.craftengine.util.plugins.DecentHologramsSupport;
import dev.arubik.craftengine.util.plugins.DiscordSrvSupport;
import dev.arubik.craftengine.util.plugins.DynmapSupport;
import dev.arubik.craftengine.util.plugins.GriefPreventionSupport;
import dev.arubik.craftengine.util.plugins.LuckPermsSupport;
import dev.arubik.craftengine.util.plugins.MythicMobsSupport;
import dev.arubik.craftengine.util.plugins.PlaceholderSupport;
import dev.arubik.craftengine.util.plugins.VaultChat;
import dev.arubik.craftengine.util.plugins.VaultEconomy;
import dev.arubik.craftengine.util.plugins.VaultPermission;
import dev.arubik.craftengine.util.plugins.WorldEditSupport;
import dev.arubik.craftengine.util.plugins.WorldGuardSupport;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * {@code Plugins} — ONE global namespace fronting every optional third-party plugin integration
 * this addon offers (Vault, LuckPerms, WorldGuard, WorldEdit/FAWE, Dynmap, BlueMap, MythicMobs,
 * PlaceholderAPI, Citizens, GriefPrevention, DecentHolograms, DiscordSRV), each reachable as
 * {@code Plugins.<name>} rather than its own separate global.
 * Adding a brand-new bridge later only means one more {@code .property(...)} line here — every
 * OTHER call site that binds a script's globals (there are many — see each site's own {@code
 * bindCommonNamespaces}) never needs to change, since they all already bind {@code Plugins} once.
 *
 * <p>Every bridge is a thin script-facing wrapper over its {@code
 * dev.arubik.craftengine.util.plugins} Java counterpart — see each one's own javadoc for exactly
 * which real plugin API it targets and how it behaves when that plugin isn't installed (always:
 * {@code is_available} reads false, and every other method silently no-ops to a safe absent
 * value — NULL/0/""/false — never throwing).
 *
 * <pre>
 *   if Plugins.vault.is_available:
 *       Plugins.vault.withdraw(Player, 10)
 *   end
 *   Plugins.worldguard.register_flag("my_custom_flag", "bool")   // call from a script's __init__()
 * </pre>
 */
public final class PluginsType {

    public static final Object INSTANCE = new Object();

    private PluginsType() {}

    public static void register() {
        PolyTypeRegistry.define("Plugins")
            .property("vault", obj -> ScriptValue.ofObj("VaultBridge", VaultEconomy.class))
            .property("luckperms", obj -> ScriptValue.ofObj("LuckPermsBridge", LuckPermsSupport.class))
            .property("worldguard", obj -> ScriptValue.ofObj("WorldGuardBridge", WorldGuardSupport.class))
            .property("worldedit", obj -> ScriptValue.ofObj("WorldEditBridge", WorldEditSupport.class))
            .property("dynmap", obj -> ScriptValue.ofObj("DynmapBridge", DynmapSupport.class))
            .property("bluemap", obj -> ScriptValue.ofObj("BlueMapBridge", BlueMapSupport.class))
            .property("mythicmobs", obj -> ScriptValue.ofObj("MythicMobsBridge", MythicMobsSupport.class))
            .property("placeholderapi", obj -> ScriptValue.ofObj("PlaceholderApiBridge", PlaceholderSupport.class))
            .property("citizens", obj -> ScriptValue.ofObj("CitizensBridge", CitizensSupport.class))
            .property("griefprevention", obj -> ScriptValue.ofObj("GriefPreventionBridge", GriefPreventionSupport.class))
            .property("decentholograms", obj -> ScriptValue.ofObj("DecentHologramsBridge", DecentHologramsSupport.class))
            .property("discordsrv", obj -> ScriptValue.ofObj("DiscordSrvBridge", DiscordSrvSupport.class));

        PolyTypeRegistry.define("VaultBridge")
            .property("is_available", obj -> ScriptValue.of(VaultEconomy.isAvailable()))
            .methodTyped1("balance", TypeCodecs.RAW, TypeCodecs.DOUBLE, 0.0,
                (Object obj, ScriptValue p) -> VaultEconomy.balance(offlinePlayer(p)))
            .methodTyped2("has", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, Double amount) -> VaultEconomy.has(offlinePlayer(p), amount))
            .methodTyped2("deposit", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, Double amount) -> VaultEconomy.deposit(offlinePlayer(p), amount))
            .methodTyped2("withdraw", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, Double amount) -> VaultEconomy.withdraw(offlinePlayer(p), amount))
            // format(amount = 0.0 when missing) — original substitutes a default arg rather than
            // short-circuiting, so onMissingArgs is that same call pre-evaluated (pure formatting,
            // deterministic for a constant input — identical to the original's runtime result).
            .methodTyped1("format", TypeCodecs.DOUBLE, TypeCodecs.STRING, VaultEconomy.format(0.0),
                (Object obj, Double amount) -> VaultEconomy.format(amount))
            .methodTyped0("currency_name_plural", TypeCodecs.STRING, (Object obj) -> VaultEconomy.currencyNamePlural())
            .methodTyped0("currency_name_singular", TypeCodecs.STRING, (Object obj) -> VaultEconomy.currencyNameSingular())
            // ---- Vault's Permission/Chat services — the permission-plugin-agnostic counterpart
            // of LuckPermsBridge (works with WHATEVER Vault is hooked into, not just LuckPerms).
            .property("permission_available", obj -> ScriptValue.of(VaultPermission.isAvailable()))
            .methodTyped2("has_permission", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, String node) -> VaultPermission.has(bukkitPlayer(p), node))
            .methodTyped2("add_permission", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, String node) -> VaultPermission.add(bukkitPlayer(p), node))
            .methodTyped2("remove_permission", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, String node) -> VaultPermission.remove(bukkitPlayer(p), node))
            .methodTyped2("in_group", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, String group) -> VaultPermission.inGroup(bukkitPlayer(p), group))
            .methodTyped2("add_group", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, String group) -> VaultPermission.addGroup(bukkitPlayer(p), group))
            .methodTyped2("remove_group", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, String group) -> VaultPermission.removeGroup(bukkitPlayer(p), group))
            .methodTyped1("primary_group", TypeCodecs.RAW, TypeCodecs.STRING, "",
                (Object obj, ScriptValue p) -> VaultPermission.primaryGroup(bukkitPlayer(p)))
            .methodTyped1("groups", TypeCodecs.RAW, TypeCodecs.RAW, new ScriptValue.Array(java.util.List.of()),
                (Object obj, ScriptValue p) -> {
                    String[] groups = VaultPermission.groups(bukkitPlayer(p));
                    java.util.List<ScriptValue> out = new java.util.ArrayList<>(groups.length);
                    for (String g : groups) out.add(ScriptValue.of(g));
                    return new ScriptValue.Array(out);
                })
            .property("chat_available", obj -> ScriptValue.of(VaultChat.isAvailable()))
            .methodTyped1("prefix", TypeCodecs.RAW, TypeCodecs.STRING, "",
                (Object obj, ScriptValue p) -> VaultChat.prefix(bukkitPlayer(p)))
            .methodTyped2("set_prefix", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, String prefix) -> VaultChat.setPrefix(bukkitPlayer(p), prefix))
            .methodTyped1("suffix", TypeCodecs.RAW, TypeCodecs.STRING, "",
                (Object obj, ScriptValue p) -> VaultChat.suffix(bukkitPlayer(p)))
            .methodTyped2("set_suffix", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, String suffix) -> VaultChat.setSuffix(bukkitPlayer(p), suffix));

        PolyTypeRegistry.define("LuckPermsBridge")
            .property("is_available", obj -> ScriptValue.of(LuckPermsSupport.isAvailable()))
            .methodTyped1("primary_group", TypeCodecs.RAW, TypeCodecs.STRING, "",
                (Object obj, ScriptValue p) -> LuckPermsSupport.primaryGroup(uuidOf(p)))
            .methodTyped2("set_primary_group", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, String group) -> LuckPermsSupport.setPrimaryGroup(uuidOf(p), group))
            .methodTyped1("groups", TypeCodecs.RAW, TypeCodecs.RAW, new ScriptValue.Array(java.util.List.of()),
                (Object obj, ScriptValue p) -> stringListToArray(LuckPermsSupport.groups(uuidOf(p))))
            .methodTyped2("has_group", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, String group) -> LuckPermsSupport.hasGroup(uuidOf(p), group))
            .methodTyped2("add_group", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, String group) -> LuckPermsSupport.addGroup(uuidOf(p), group))
            .methodTyped2("remove_group", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, String group) -> LuckPermsSupport.removeGroup(uuidOf(p), group))
            .methodTyped2("meta", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.STRING, "",
                (Object obj, ScriptValue p, String key) -> LuckPermsSupport.metaValue(uuidOf(p), key))
            .methodTyped2("has_permission", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, String node) -> LuckPermsSupport.hasPermission(uuidOf(p), node))
            // add_permission(player, node, value? = true) — the 3rd arg is genuinely optional
            // (defaults to true), and the `node` slot's null String default is the "fewer than 2
            // args" sentinel reproducing the original's `false` short-circuit exactly (a present arg
            // always decodes to a non-null String — ScriptValue.asStr never returns null).
            .methodTypedOpt3("add_permission", TypeCodecs.RAW, (ScriptValue) null, TypeCodecs.STRING, (String) null,
                TypeCodecs.BOOL, true, TypeCodecs.BOOL,
                (Object obj, ScriptValue p, String node, Boolean value) -> {
                    if (node == null) return false;
                    return LuckPermsSupport.addPermission(uuidOf(p), node, value);
                })
            .methodTyped2("remove_permission", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, String node) -> LuckPermsSupport.removePermission(uuidOf(p), node))
            // ---- Group-level operations — always safe regardless of who's online (see
            // LuckPermsSupport's own javadoc on why users vs. groups differ here).
            .methodTyped1("group_exists", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String group) -> LuckPermsSupport.groupExists(group))
            .methodTyped2("group_has_permission", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String group, String node) -> LuckPermsSupport.groupHasPermission(group, node))
            // set_group_permission(group, node, value? = true) — same optional-3rd-arg shape as
            // add_permission above, with the same null-`node` "fewer than 2 args" sentinel.
            .methodTypedOpt3("set_group_permission", TypeCodecs.STRING, (String) null, TypeCodecs.STRING, (String) null,
                TypeCodecs.BOOL, true, TypeCodecs.BOOL,
                (Object obj, String group, String node, Boolean value) -> {
                    if (node == null) return false;
                    return LuckPermsSupport.setGroupPermission(group, node, value);
                })
            .methodTyped2("remove_group_permission", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String group, String node) -> LuckPermsSupport.removeGroupPermission(group, node))
            .methodTyped1("group_parents", TypeCodecs.STRING, TypeCodecs.RAW, new ScriptValue.Array(java.util.List.of()),
                (Object obj, String group) -> stringListToArray(LuckPermsSupport.groupParents(group)));

        PolyTypeRegistry.define("WorldGuardBridge")
            .property("is_available", obj -> ScriptValue.of(WorldGuardSupport.isAvailable()))
            // register_flag(name, type) — call once from a script's __init__(); see
            // WorldGuardSupport#registerFlag's own javadoc for the load-order caveat.
            .methodTyped2("register_flag", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String name, String type) -> WorldGuardSupport.registerFlag(name, type))
            .methodTyped4("regions_at", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE,
                TypeCodecs.RAW, new ScriptValue.Array(java.util.List.of()),
                (Object obj, ScriptValue world, Double x, Double y, Double z) -> {
                    java.util.List<String> ids = WorldGuardSupport.regionsAt(bukkitWorld(world), x, y, z);
                    java.util.List<ScriptValue> out = new java.util.ArrayList<>(ids.size());
                    for (String id : ids) out.add(ScriptValue.of(id));
                    return new ScriptValue.Array(out);
                })
            .methodTyped4("is_protected", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE,
                TypeCodecs.BOOL, false,
                (Object obj, ScriptValue world, Double x, Double y, Double z) -> WorldGuardSupport.isRegionProtected(bukkitWorld(world), x, y, z))
            .methodTyped3("get_region_flag", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, ScriptValue world, String region, String flag) ->
                    toScriptValue(WorldGuardSupport.getRegionFlag(bukkitWorld(world), region, flag)))
            .methodTyped4("set_region_flag", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.STRING,
                TypeCodecs.BOOL, false,
                (Object obj, ScriptValue world, String region, String flag, String value) ->
                    WorldGuardSupport.setRegionFlagFromString(bukkitWorld(world), region, flag, value))
            .methodTyped5("query_flag_state", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.STRING,
                TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, ScriptValue world, Double x, Double y, Double z, String flag) -> {
                    Boolean state = WorldGuardSupport.queryFlagState(bukkitWorld(world), x, y, z, flag);
                    return state == null ? ScriptValue.NULL : ScriptValue.of(state);
                })
            .methodTyped4("can_build", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE,
                TypeCodecs.BOOL, true,
                (Object obj, ScriptValue player, Double x, Double y, Double z) -> WorldGuardSupport.canBuild(bukkitPlayer(player), x, y, z));

        PolyTypeRegistry.define("WorldEditBridge")
            .property("is_available", obj -> ScriptValue.of(WorldEditSupport.isAvailable()))
            .property("is_fawe", obj -> ScriptValue.of(WorldEditSupport.isFawe()))
            .methodTyped1("get_selection", TypeCodecs.RAW, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, ScriptValue p) -> {
                    Map<String, Object> selection = WorldEditSupport.getSelection(bukkitPlayer(p));
                    return selection == null ? ScriptValue.NULL : MapType.wrap(toScriptMap(selection));
                })
            .methodTyped2("fill_selection", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue p, String pattern) -> WorldEditSupport.fillSelection(bukkitPlayer(p), pattern))
            // paste_clipboard(player, x, y, z, includeAir? = true) — 4 required args plus an
            // optional 5th; methodTypedOpt5 covers it, with the `z` slot's null Double default as
            // the "fewer than 4 args" sentinel (a present arg always decodes to a non-null Double).
            .methodTypedOpt5("paste_clipboard", TypeCodecs.RAW, (ScriptValue) null,
                TypeCodecs.DOUBLE, 0.0, TypeCodecs.DOUBLE, 0.0, TypeCodecs.DOUBLE, (Double) null,
                TypeCodecs.BOOL, true, TypeCodecs.BOOL,
                (Object obj, ScriptValue p, Double x, Double y, Double z, Boolean includeAir) -> {
                    if (z == null) return false;
                    return WorldEditSupport.pasteClipboard(bukkitPlayer(p), x, y, z, includeAir);
                });

        PolyTypeRegistry.define("DynmapBridge")
            .property("is_available", obj -> ScriptValue.of(DynmapSupport.isAvailable()))
            // add_marker(setId, setLabel, markerId, label, world, x, y, z, icon?) — 8 required args
            // plus an optional 9th, so methodTypedOpt9. The `z` slot's null Double default is the
            // "fewer than 8 args" sentinel (arguments are positional, so it is the LAST required
            // slot, and a present arg always decodes to a non-null Double — asNum() never returns
            // null). The 9th slot's null String default is literally the same `null` the original
            // passed through for an absent icon.
            .methodTypedOpt9("add_marker", TypeCodecs.STRING, "", TypeCodecs.STRING, "",
                TypeCodecs.STRING, "", TypeCodecs.STRING, "", TypeCodecs.STRING, "",
                TypeCodecs.DOUBLE, 0.0, TypeCodecs.DOUBLE, 0.0, TypeCodecs.DOUBLE, (Double) null,
                TypeCodecs.STRING, (String) null, TypeCodecs.BOOL,
                (Object obj, String setId, String setLabel, String markerId, String label, String world,
                 Double x, Double y, Double z, String icon) -> {
                    if (z == null) return false;
                    return DynmapSupport.addMarker(setId, setLabel, markerId, label, world, x, y, z, icon);
                })
            .methodTyped2("remove_marker", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String setId, String markerId) -> DynmapSupport.removeMarker(setId, markerId));

        PolyTypeRegistry.define("BlueMapBridge")
            .property("is_available", obj -> ScriptValue.of(BlueMapSupport.isAvailable()))
            // add_marker(mapId, setId, setLabel, markerId, label, x, y, z) — 8 required args, past
            // methodTyped7's 7-arg max, so methodTypedOpt8 with the `z` slot's null Double default
            // as the "fewer than 8 args" sentinel (arguments are positional, so it is the LAST
            // required slot; a present arg always decodes to a non-null Double).
            .methodTypedOpt8("add_marker", TypeCodecs.STRING, "", TypeCodecs.STRING, "",
                TypeCodecs.STRING, "", TypeCodecs.STRING, "", TypeCodecs.STRING, "",
                TypeCodecs.DOUBLE, 0.0, TypeCodecs.DOUBLE, 0.0, TypeCodecs.DOUBLE, (Double) null,
                TypeCodecs.BOOL,
                (Object obj, String mapId, String setId, String setLabel, String markerId, String label,
                 Double x, Double y, Double z) -> {
                    if (z == null) return false;
                    return BlueMapSupport.addMarker(mapId, setId, setLabel, markerId, label, x, y, z);
                })
            .methodTyped3("remove_marker", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String mapId, String setId, String markerId) -> BlueMapSupport.removeMarker(mapId, setId, markerId));

        PolyTypeRegistry.define("MythicMobsBridge")
            .property("is_available", obj -> ScriptValue.of(MythicMobsSupport.isAvailable()))
            // spawn_mob(mobType, world, x, y, z, level?) — 5 required args plus an optional 6th, so
            // methodTypedOpt6. The `z` slot's null Double default replaces locationArg(args, 1)'s
            // own "args.size() < 5" test exactly (arguments are positional, so z is the LAST slot
            // locationArg needed, and a present arg always decodes to a non-null Double); the
            // world-doesn't-resolve half of locationArg's null return is kept as the explicit
            // `world == null` check below. `level == null` is exactly the original's
            // `args.size() >= 6` being false, picking the 2-arg spawnMob overload as before.
            .methodTypedOpt6("spawn_mob", TypeCodecs.STRING, "", TypeCodecs.RAW, ScriptValue.NULL,
                TypeCodecs.DOUBLE, 0.0, TypeCodecs.DOUBLE, 0.0, TypeCodecs.DOUBLE, (Double) null,
                TypeCodecs.DOUBLE, (Double) null, TypeCodecs.RAW,
                (Object obj, String mobType, ScriptValue worldVal, Double x, Double y, Double z, Double level) -> {
                    if (z == null) return ScriptValue.NULL;
                    org.bukkit.World world = bukkitWorld(worldVal);
                    if (world == null) return ScriptValue.NULL;
                    org.bukkit.Location loc = new org.bukkit.Location(world, x, y, z);
                    org.bukkit.entity.Entity entity = level != null
                            ? MythicMobsSupport.spawnMob(mobType, loc, level.intValue())
                            : MythicMobsSupport.spawnMob(mobType, loc);
                    return entity == null ? ScriptValue.NULL : EntityType.wrap(((org.bukkit.craftbukkit.entity.CraftEntity) entity).getHandle());
                })
            .methodTyped1("is_mythic_mob", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue e) -> MythicMobsSupport.isMythicMob(bukkitEntity(e)))
            .methodTyped1("mob_type", TypeCodecs.RAW, TypeCodecs.STRING, "",
                (Object obj, ScriptValue e) -> MythicMobsSupport.mobType(bukkitEntity(e)))
            .methodTyped1("display_name", TypeCodecs.RAW, TypeCodecs.STRING, "",
                (Object obj, ScriptValue e) -> MythicMobsSupport.displayName(bukkitEntity(e)))
            .methodTyped1("mob_level", TypeCodecs.RAW, TypeCodecs.DOUBLE, 0.0,
                (Object obj, ScriptValue e) -> MythicMobsSupport.mobLevel(bukkitEntity(e)))
            .methodTyped1("faction", TypeCodecs.RAW, TypeCodecs.STRING, "",
                (Object obj, ScriptValue e) -> MythicMobsSupport.faction(bukkitEntity(e)))
            .methodTyped2("set_faction", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue e, String faction) -> MythicMobsSupport.setFaction(bukkitEntity(e), faction))
            .methodTyped1("despawn", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue e) -> MythicMobsSupport.despawn(bukkitEntity(e)))
            .methodTyped0("mob_type_names", TypeCodecs.RAW, (Object obj) -> stringListToArray(MythicMobsSupport.mobTypeNames()))
            .methodTyped2("cast_skill", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue e, String skill) -> MythicMobsSupport.castSkill(bukkitEntity(e), skill))
            // cast_skill_at(caster, skillName, world, x, y, z) — for any args.size() < 6 the
            // original always resolves to false (either the explicit size<2 guard, or
            // locationArg(args,2) returning null for sizes 2..5), so this is exactly a 6-arg
            // methodTyped6 shape with onMissingArgs = false.
            .methodTyped6("cast_skill_at", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE,
                TypeCodecs.BOOL, false,
                (Object obj, ScriptValue caster, String skillName, ScriptValue worldVal, Double x, Double y, Double z) -> {
                    org.bukkit.World world = bukkitWorld(worldVal);
                    if (world == null) return false;
                    org.bukkit.Location loc = new org.bukkit.Location(world, x, y, z);
                    return MythicMobsSupport.castSkillAt(bukkitEntity(caster), skillName, loc);
                });

        PolyTypeRegistry.define("CitizensBridge")
            .property("is_available", obj -> ScriptValue.of(CitizensSupport.isAvailable()))
            .methodTyped1("is_npc", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue e) -> CitizensSupport.isNpc(bukkitEntity(e)))
            .methodTyped1("npc_id", TypeCodecs.RAW, TypeCodecs.DOUBLE, -1.0,
                (Object obj, ScriptValue e) -> (double) CitizensSupport.npcId(bukkitEntity(e)))
            .methodTyped1("npc_name", TypeCodecs.RAW, TypeCodecs.STRING, "",
                (Object obj, ScriptValue e) -> CitizensSupport.npcName(bukkitEntity(e)))
            // create_npc(entityType, name, world, x, y, z) — for any args.size() < 6 the original
            // always resolves to NULL (either the size<2 guard, or locationArg(args,2) returning
            // null for sizes 2..5), so this is exactly a 6-arg methodTyped6 shape.
            .methodTyped6("create_npc", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE,
                TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, String entityType, String name, ScriptValue worldVal, Double x, Double y, Double z) -> {
                    org.bukkit.World world = bukkitWorld(worldVal);
                    if (world == null) return ScriptValue.NULL;
                    org.bukkit.Location loc = new org.bukkit.Location(world, x, y, z);
                    org.bukkit.entity.Entity entity = CitizensSupport.createNpc(entityType, name, loc);
                    return entity == null ? ScriptValue.NULL : EntityType.wrap(((org.bukkit.craftbukkit.entity.CraftEntity) entity).getHandle());
                })
            .methodTyped1("remove_npc", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue e) -> CitizensSupport.removeNpc(bukkitEntity(e)));

        PolyTypeRegistry.define("GriefPreventionBridge")
            .property("is_available", obj -> ScriptValue.of(GriefPreventionSupport.isAvailable()))
            // is_claimed(world, x, y, z) — locationArg(args,0) returns null (=> false, since it's
            // ORed with nothing) whenever args.size() < 4, matching methodTyped4's onMissingArgs.
            .methodTyped4("is_claimed", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE,
                TypeCodecs.BOOL, false,
                (Object obj, ScriptValue worldVal, Double x, Double y, Double z) -> {
                    org.bukkit.World world = bukkitWorld(worldVal);
                    if (world == null) return false;
                    return GriefPreventionSupport.isClaimed(new org.bukkit.Location(world, x, y, z));
                })
            // claim_owner(world, x, y, z) — same "< 4 args -> null location -> NULL" equivalence.
            .methodTyped4("claim_owner", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE,
                TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, ScriptValue worldVal, Double x, Double y, Double z) -> {
                    org.bukkit.World world = bukkitWorld(worldVal);
                    if (world == null) return ScriptValue.NULL;
                    java.util.UUID owner = GriefPreventionSupport.claimOwner(new org.bukkit.Location(world, x, y, z));
                    return owner == null ? ScriptValue.NULL : ScriptValue.of(owner.toString());
                })
            // can_build(player, world, x, y, z) — for any args.size() < 5 the original always
            // resolves to true (the explicit isEmpty() guard, or locationArg(args,1) returning null
            // for sizes 1..4, both hit the `loc == null || ...` short-circuit), matching
            // methodTyped5's onMissingArgs = true.
            .methodTyped5("can_build", TypeCodecs.RAW, TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE,
                TypeCodecs.BOOL, true,
                (Object obj, ScriptValue playerVal, ScriptValue worldVal, Double x, Double y, Double z) -> {
                    org.bukkit.World world = bukkitWorld(worldVal);
                    if (world == null) return true;
                    return GriefPreventionSupport.canBuild(bukkitPlayer(playerVal), new org.bukkit.Location(world, x, y, z));
                });

        PolyTypeRegistry.define("DecentHologramsBridge")
            .property("is_available", obj -> ScriptValue.of(DecentHologramsSupport.isAvailable()))
            // create(id, world, x, y, z, ...lines) — trailing lore lines are collected via
            // stringArgList's unbounded "rest of the args, arrays flattened" scan. Arity is NOT the
            // blocker (methodTypedOpt now reaches 10 slots): no FIXED slot count, however large, can
            // represent an unbounded variadic tail, and stringArgList also flattens an Array
            // argument in any of those positions. Left untyped.
            .method("create", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                org.bukkit.Location loc = locationArg(args, 1);
                return ScriptValue.of(loc != null
                        && DecentHologramsSupport.createHologram(args.get(0).asStr(), loc, stringArgList(args, 2)));
            })
            // set_lines(id, ...lines) — same unbounded variadic tail via stringArgList, unrepresentable
            // at any fixed arity; left untyped.
            .method("set_lines", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(DecentHologramsSupport.setLines(args.get(0).asStr(), stringArgList(args, 1))))
            .methodTyped1("remove", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String id) -> DecentHologramsSupport.removeHologram(id))
            .methodTyped1("exists", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String id) -> DecentHologramsSupport.exists(id));

        PolyTypeRegistry.define("DiscordSrvBridge")
            .property("is_available", obj -> ScriptValue.of(DiscordSrvSupport.isAvailable()))
            .methodTyped2("send_message", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String channel, String message) -> DiscordSrvSupport.sendMessage(channel, message))
            .methodTyped1("send_to_main_channel", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String message) -> DiscordSrvSupport.sendToMainChannel(message))
            .methodTyped1("discord_id", TypeCodecs.RAW, TypeCodecs.STRING, "",
                (Object obj, ScriptValue p) -> DiscordSrvSupport.discordId(uuidOf(p)))
            .methodTyped1("minecraft_uuid", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, String discordId) -> {
                    java.util.UUID uuid = DiscordSrvSupport.minecraftUuid(discordId);
                    return uuid == null ? ScriptValue.NULL : ScriptValue.of(uuid.toString());
                });

        PolyTypeRegistry.define("PlaceholderApiBridge")
            .property("is_available", obj -> ScriptValue.of(PlaceholderSupport.isAvailable()))
            .methodTyped2("set", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.STRING, "",
                (Object obj, ScriptValue p, String text) -> PlaceholderSupport.set(offlinePlayer(p), text))
            .methodTyped1("set_global", TypeCodecs.STRING, TypeCodecs.STRING, "",
                (Object obj, String text) -> PlaceholderSupport.setGlobal(text))
            // register_placeholder(identifier, "script.pf:func") — call from a script's __init__();
            // see PlaceholderSupport#registerPlaceholder's own javadoc for the full example.
            .methodTyped2("register_placeholder", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String identifier, String target) -> PlaceholderSupport.registerPlaceholder(identifier, target))
            .methodTyped1("unregister_placeholder", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String identifier) -> PlaceholderSupport.unregisterPlaceholder(identifier));
    }

    // ---- ScriptValue <-> Bukkit extraction helpers ---------------------------------------------

    /** Pulls a real Bukkit {@link org.bukkit.entity.Entity} out of a wrapped NMS Entity value —
     *  the same "wrapped instance -> live Bukkit entity" idiom used throughout this codebase. */
    private static org.bukkit.entity.Entity bukkitEntity(ScriptValue v) {
        if (v instanceof ScriptValue.Obj o && o.instance() instanceof net.minecraft.world.entity.Entity nms) {
            return nms.getBukkitEntity();
        }
        return null;
    }

    private static org.bukkit.entity.Player bukkitPlayer(ScriptValue v) {
        return bukkitEntity(v) instanceof org.bukkit.entity.Player p ? p : null;
    }

    /** Accepts either a wrapped online Player value or a plain UUID/name string (resolved via
     *  Bukkit's local player-data cache — never a blocking Mojang lookup, same rule this addon's
     *  other player-name resolvers already follow). */
    private static org.bukkit.OfflinePlayer offlinePlayer(ScriptValue v) {
        org.bukkit.entity.Player online = bukkitPlayer(v);
        if (online != null) return online;
        String raw = v.asStr();
        if (raw == null || raw.isBlank()) return null;
        try { return org.bukkit.Bukkit.getOfflinePlayer(UUID.fromString(raw)); }
        catch (IllegalArgumentException notAUuid) { return org.bukkit.Bukkit.getOfflinePlayerIfCached(raw); }
        catch (Throwable ignored) { return null; }
    }

    private static UUID uuidOf(ScriptValue v) {
        org.bukkit.entity.Player p = bukkitPlayer(v);
        if (p != null) return p.getUniqueId();
        try { return UUID.fromString(v.asStr()); } catch (Throwable ignored) { return null; }
    }

    /** Accepts either a wrapped World value or a plain world-name string. */
    private static org.bukkit.World bukkitWorld(ScriptValue v) {
        if (v instanceof ScriptValue.Obj o && o.instance() instanceof net.minecraft.server.level.ServerLevel sl) {
            return sl.getWorld();
        }
        String name = v.asStr();
        return name != null ? org.bukkit.Bukkit.getWorld(name) : null;
    }

    /** Builds a {@code Location} from 4 consecutive args starting at {@code idx} (world, x, y, z —
     *  the same shorthand {@code World.spawn_entity}/{@code Machine.block_at} etc. already use
     *  throughout this codebase instead of a dedicated Location argument). Null if there aren't
     *  enough args or the world name/value doesn't resolve. */
    private static org.bukkit.Location locationArg(java.util.List<ScriptValue> args, int idx) {
        if (args.size() < idx + 4) return null;
        org.bukkit.World world = bukkitWorld(args.get(idx));
        if (world == null) return null;
        return new org.bukkit.Location(world, args.get(idx + 1).asNum(), args.get(idx + 2).asNum(), args.get(idx + 3).asNum());
    }

    /** Collects every remaining arg from {@code fromIdx} as a flat list of strings — accepts
     *  either N separate string args OR one Array argument (same "both call shapes work"
     *  convenience {@code Item.with_lore} already offers). */
    private static java.util.List<String> stringArgList(java.util.List<ScriptValue> args, int fromIdx) {
        java.util.List<String> out = new java.util.ArrayList<>();
        for (int i = fromIdx; i < args.size(); i++) {
            ScriptValue v = args.get(i);
            if (v instanceof ScriptValue.Array arr) {
                for (ScriptValue e : arr.elements()) out.add(e.asStr());
            } else {
                out.add(v.asStr());
            }
        }
        return out;
    }

    private static ScriptValue stringListToArray(java.util.List<String> list) {
        java.util.List<ScriptValue> out = new java.util.ArrayList<>(list.size());
        for (String s : list) out.add(ScriptValue.of(s));
        return new ScriptValue.Array(out);
    }

    private static ScriptValue toScriptValue(Object value) {
        return switch (value) {
            case null -> ScriptValue.NULL;
            case Boolean b -> ScriptValue.of(b);
            case Number n -> ScriptValue.of(n.doubleValue());
            default -> ScriptValue.of(value.toString());
        };
    }

    @SuppressWarnings("unchecked")
    private static Map<String, ScriptValue> toScriptMap(Map<String, Object> map) {
        Map<String, ScriptValue> out = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            Object v = e.getValue();
            out.put(e.getKey(), v instanceof Map ? MapType.wrap(toScriptMap((Map<String, Object>) v)) : toScriptValue(v));
        }
        return out;
    }

    public static ScriptValue wrap() {
        return ScriptValue.ofObj("Plugins", INSTANCE);
    }
}
