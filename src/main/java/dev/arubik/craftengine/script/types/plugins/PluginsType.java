package dev.arubik.craftengine.script.types.plugins;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
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
            .method("balance", (obj, args) -> args.isEmpty() ? ScriptValue.of(0.0)
                : ScriptValue.of(VaultEconomy.balance(offlinePlayer(args.get(0)))))
            .method("has", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(VaultEconomy.has(offlinePlayer(args.get(0)), args.get(1).asNum())))
            .method("deposit", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(VaultEconomy.deposit(offlinePlayer(args.get(0)), args.get(1).asNum())))
            .method("withdraw", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(VaultEconomy.withdraw(offlinePlayer(args.get(0)), args.get(1).asNum())))
            .method("format", (obj, args) -> ScriptValue.of(VaultEconomy.format(args.isEmpty() ? 0.0 : args.get(0).asNum())))
            .method("currency_name_plural", (obj, args) -> ScriptValue.of(VaultEconomy.currencyNamePlural()))
            .method("currency_name_singular", (obj, args) -> ScriptValue.of(VaultEconomy.currencyNameSingular()))
            // ---- Vault's Permission/Chat services — the permission-plugin-agnostic counterpart
            // of LuckPermsBridge (works with WHATEVER Vault is hooked into, not just LuckPerms).
            .property("permission_available", obj -> ScriptValue.of(VaultPermission.isAvailable()))
            .method("has_permission", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(VaultPermission.has(bukkitPlayer(args.get(0)), args.get(1).asStr())))
            .method("add_permission", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(VaultPermission.add(bukkitPlayer(args.get(0)), args.get(1).asStr())))
            .method("remove_permission", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(VaultPermission.remove(bukkitPlayer(args.get(0)), args.get(1).asStr())))
            .method("in_group", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(VaultPermission.inGroup(bukkitPlayer(args.get(0)), args.get(1).asStr())))
            .method("add_group", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(VaultPermission.addGroup(bukkitPlayer(args.get(0)), args.get(1).asStr())))
            .method("remove_group", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(VaultPermission.removeGroup(bukkitPlayer(args.get(0)), args.get(1).asStr())))
            .method("primary_group", (obj, args) -> args.isEmpty() ? ScriptValue.of("")
                : ScriptValue.of(VaultPermission.primaryGroup(bukkitPlayer(args.get(0)))))
            .method("groups", (obj, args) -> {
                if (args.isEmpty()) return new ScriptValue.Array(java.util.List.of());
                String[] groups = VaultPermission.groups(bukkitPlayer(args.get(0)));
                java.util.List<ScriptValue> out = new java.util.ArrayList<>(groups.length);
                for (String g : groups) out.add(ScriptValue.of(g));
                return new ScriptValue.Array(out);
            })
            .property("chat_available", obj -> ScriptValue.of(VaultChat.isAvailable()))
            .method("prefix", (obj, args) -> args.isEmpty() ? ScriptValue.of("")
                : ScriptValue.of(VaultChat.prefix(bukkitPlayer(args.get(0)))))
            .method("set_prefix", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(VaultChat.setPrefix(bukkitPlayer(args.get(0)), args.get(1).asStr())))
            .method("suffix", (obj, args) -> args.isEmpty() ? ScriptValue.of("")
                : ScriptValue.of(VaultChat.suffix(bukkitPlayer(args.get(0)))))
            .method("set_suffix", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(VaultChat.setSuffix(bukkitPlayer(args.get(0)), args.get(1).asStr())));

        PolyTypeRegistry.define("LuckPermsBridge")
            .property("is_available", obj -> ScriptValue.of(LuckPermsSupport.isAvailable()))
            .method("primary_group", (obj, args) -> args.isEmpty() ? ScriptValue.of("")
                : ScriptValue.of(LuckPermsSupport.primaryGroup(uuidOf(args.get(0)))))
            .method("set_primary_group", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(LuckPermsSupport.setPrimaryGroup(uuidOf(args.get(0)), args.get(1).asStr())))
            .method("groups", (obj, args) -> {
                if (args.isEmpty()) return new ScriptValue.Array(java.util.List.of());
                return stringListToArray(LuckPermsSupport.groups(uuidOf(args.get(0))));
            })
            .method("has_group", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(LuckPermsSupport.hasGroup(uuidOf(args.get(0)), args.get(1).asStr())))
            .method("add_group", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(LuckPermsSupport.addGroup(uuidOf(args.get(0)), args.get(1).asStr())))
            .method("remove_group", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(LuckPermsSupport.removeGroup(uuidOf(args.get(0)), args.get(1).asStr())))
            .method("meta", (obj, args) -> args.size() < 2 ? ScriptValue.of("")
                : ScriptValue.of(LuckPermsSupport.metaValue(uuidOf(args.get(0)), args.get(1).asStr())))
            .method("has_permission", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(LuckPermsSupport.hasPermission(uuidOf(args.get(0)), args.get(1).asStr())))
            .method("add_permission", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(LuckPermsSupport.addPermission(uuidOf(args.get(0)), args.get(1).asStr(),
                    args.size() < 3 || args.get(2).asBool())))
            .method("remove_permission", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(LuckPermsSupport.removePermission(uuidOf(args.get(0)), args.get(1).asStr())))
            // ---- Group-level operations — always safe regardless of who's online (see
            // LuckPermsSupport's own javadoc on why users vs. groups differ here).
            .method("group_exists", (obj, args) -> args.isEmpty() ? ScriptValue.of(false)
                : ScriptValue.of(LuckPermsSupport.groupExists(args.get(0).asStr())))
            .method("group_has_permission", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(LuckPermsSupport.groupHasPermission(args.get(0).asStr(), args.get(1).asStr())))
            .method("set_group_permission", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(LuckPermsSupport.setGroupPermission(args.get(0).asStr(), args.get(1).asStr(),
                    args.size() < 3 || args.get(2).asBool())))
            .method("remove_group_permission", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(LuckPermsSupport.removeGroupPermission(args.get(0).asStr(), args.get(1).asStr())))
            .method("group_parents", (obj, args) -> args.isEmpty() ? new ScriptValue.Array(java.util.List.of())
                : stringListToArray(LuckPermsSupport.groupParents(args.get(0).asStr())));

        PolyTypeRegistry.define("WorldGuardBridge")
            .property("is_available", obj -> ScriptValue.of(WorldGuardSupport.isAvailable()))
            // register_flag(name, type) — call once from a script's __init__(); see
            // WorldGuardSupport#registerFlag's own javadoc for the load-order caveat.
            .method("register_flag", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(WorldGuardSupport.registerFlag(args.get(0).asStr(), args.get(1).asStr())))
            .method("regions_at", (obj, args) -> {
                if (args.size() < 4) return new ScriptValue.Array(java.util.List.of());
                java.util.List<String> ids = WorldGuardSupport.regionsAt(
                        bukkitWorld(args.get(0)), args.get(1).asNum(), args.get(2).asNum(), args.get(3).asNum());
                java.util.List<ScriptValue> out = new java.util.ArrayList<>(ids.size());
                for (String id : ids) out.add(ScriptValue.of(id));
                return new ScriptValue.Array(out);
            })
            .method("is_protected", (obj, args) -> args.size() < 4 ? ScriptValue.of(false)
                : ScriptValue.of(WorldGuardSupport.isRegionProtected(
                    bukkitWorld(args.get(0)), args.get(1).asNum(), args.get(2).asNum(), args.get(3).asNum())))
            .method("get_region_flag", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                Object value = WorldGuardSupport.getRegionFlag(
                        bukkitWorld(args.get(0)), args.get(1).asStr(), args.get(2).asStr());
                return toScriptValue(value);
            })
            .method("set_region_flag", (obj, args) -> args.size() < 4 ? ScriptValue.of(false)
                : ScriptValue.of(WorldGuardSupport.setRegionFlagFromString(
                    bukkitWorld(args.get(0)), args.get(1).asStr(), args.get(2).asStr(), args.get(3).asStr())))
            .method("query_flag_state", (obj, args) -> {
                if (args.size() < 5) return ScriptValue.NULL;
                Boolean state = WorldGuardSupport.queryFlagState(
                        bukkitWorld(args.get(0)), args.get(1).asNum(), args.get(2).asNum(), args.get(3).asNum(), args.get(4).asStr());
                return state == null ? ScriptValue.NULL : ScriptValue.of(state);
            })
            .method("can_build", (obj, args) -> args.size() < 4 ? ScriptValue.of(true)
                : ScriptValue.of(WorldGuardSupport.canBuild(
                    bukkitPlayer(args.get(0)), args.get(1).asNum(), args.get(2).asNum(), args.get(3).asNum())));

        PolyTypeRegistry.define("WorldEditBridge")
            .property("is_available", obj -> ScriptValue.of(WorldEditSupport.isAvailable()))
            .property("is_fawe", obj -> ScriptValue.of(WorldEditSupport.isFawe()))
            .method("get_selection", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                Map<String, Object> selection = WorldEditSupport.getSelection(bukkitPlayer(args.get(0)));
                return selection == null ? ScriptValue.NULL : MapType.wrap(toScriptMap(selection));
            })
            .method("fill_selection", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(WorldEditSupport.fillSelection(bukkitPlayer(args.get(0)), args.get(1).asStr())))
            .method("paste_clipboard", (obj, args) -> args.size() < 4 ? ScriptValue.of(false)
                : ScriptValue.of(WorldEditSupport.pasteClipboard(bukkitPlayer(args.get(0)),
                    args.get(1).asNum(), args.get(2).asNum(), args.get(3).asNum(),
                    args.size() < 5 || args.get(4).asBool())));

        PolyTypeRegistry.define("DynmapBridge")
            .property("is_available", obj -> ScriptValue.of(DynmapSupport.isAvailable()))
            .method("add_marker", (obj, args) -> args.size() < 8 ? ScriptValue.of(false)
                : ScriptValue.of(DynmapSupport.addMarker(args.get(0).asStr(), args.get(1).asStr(),
                    args.get(2).asStr(), args.get(3).asStr(), args.get(4).asStr(),
                    args.get(5).asNum(), args.get(6).asNum(), args.get(7).asNum(),
                    args.size() > 8 ? args.get(8).asStr() : null)))
            .method("remove_marker", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(DynmapSupport.removeMarker(args.get(0).asStr(), args.get(1).asStr())));

        PolyTypeRegistry.define("BlueMapBridge")
            .property("is_available", obj -> ScriptValue.of(BlueMapSupport.isAvailable()))
            .method("add_marker", (obj, args) -> args.size() < 8 ? ScriptValue.of(false)
                : ScriptValue.of(BlueMapSupport.addMarker(args.get(0).asStr(), args.get(1).asStr(),
                    args.get(2).asStr(), args.get(3).asStr(), args.get(4).asStr(),
                    args.get(5).asNum(), args.get(6).asNum(), args.get(7).asNum())))
            .method("remove_marker", (obj, args) -> args.size() < 3 ? ScriptValue.of(false)
                : ScriptValue.of(BlueMapSupport.removeMarker(args.get(0).asStr(), args.get(1).asStr(), args.get(2).asStr())));

        PolyTypeRegistry.define("MythicMobsBridge")
            .property("is_available", obj -> ScriptValue.of(MythicMobsSupport.isAvailable()))
            // spawn_mob(mobType, world, x, y, z, level?)
            .method("spawn_mob", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                org.bukkit.Location loc = locationArg(args, 1);
                if (loc == null) return ScriptValue.NULL;
                org.bukkit.entity.Entity entity = args.size() >= 6
                        ? MythicMobsSupport.spawnMob(args.get(0).asStr(), loc, (int) args.get(5).asNum())
                        : MythicMobsSupport.spawnMob(args.get(0).asStr(), loc);
                return entity == null ? ScriptValue.NULL : EntityType.wrap(((org.bukkit.craftbukkit.entity.CraftEntity) entity).getHandle());
            })
            .method("is_mythic_mob", (obj, args) -> args.isEmpty() ? ScriptValue.of(false)
                : ScriptValue.of(MythicMobsSupport.isMythicMob(bukkitEntity(args.get(0)))))
            .method("mob_type", (obj, args) -> args.isEmpty() ? ScriptValue.of("")
                : ScriptValue.of(MythicMobsSupport.mobType(bukkitEntity(args.get(0)))))
            .method("display_name", (obj, args) -> args.isEmpty() ? ScriptValue.of("")
                : ScriptValue.of(MythicMobsSupport.displayName(bukkitEntity(args.get(0)))))
            .method("mob_level", (obj, args) -> args.isEmpty() ? ScriptValue.of(0.0)
                : ScriptValue.of(MythicMobsSupport.mobLevel(bukkitEntity(args.get(0)))))
            .method("faction", (obj, args) -> args.isEmpty() ? ScriptValue.of("")
                : ScriptValue.of(MythicMobsSupport.faction(bukkitEntity(args.get(0)))))
            .method("set_faction", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(MythicMobsSupport.setFaction(bukkitEntity(args.get(0)), args.get(1).asStr())))
            .method("despawn", (obj, args) -> args.isEmpty() ? ScriptValue.of(false)
                : ScriptValue.of(MythicMobsSupport.despawn(bukkitEntity(args.get(0)))))
            .method("mob_type_names", (obj, args) -> stringListToArray(MythicMobsSupport.mobTypeNames()))
            .method("cast_skill", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(MythicMobsSupport.castSkill(bukkitEntity(args.get(0)), args.get(1).asStr())))
            // cast_skill_at(caster, skillName, world, x, y, z)
            .method("cast_skill_at", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                org.bukkit.Location loc = locationArg(args, 2);
                return ScriptValue.of(loc != null
                        && MythicMobsSupport.castSkillAt(bukkitEntity(args.get(0)), args.get(1).asStr(), loc));
            });

        PolyTypeRegistry.define("CitizensBridge")
            .property("is_available", obj -> ScriptValue.of(CitizensSupport.isAvailable()))
            .method("is_npc", (obj, args) -> args.isEmpty() ? ScriptValue.of(false)
                : ScriptValue.of(CitizensSupport.isNpc(bukkitEntity(args.get(0)))))
            .method("npc_id", (obj, args) -> args.isEmpty() ? ScriptValue.of(-1)
                : ScriptValue.of(CitizensSupport.npcId(bukkitEntity(args.get(0)))))
            .method("npc_name", (obj, args) -> args.isEmpty() ? ScriptValue.of("")
                : ScriptValue.of(CitizensSupport.npcName(bukkitEntity(args.get(0)))))
            // create_npc(entityType, name, world, x, y, z)
            .method("create_npc", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.NULL;
                org.bukkit.Location loc = locationArg(args, 2);
                if (loc == null) return ScriptValue.NULL;
                org.bukkit.entity.Entity entity = CitizensSupport.createNpc(args.get(0).asStr(), args.get(1).asStr(), loc);
                return entity == null ? ScriptValue.NULL : EntityType.wrap(((org.bukkit.craftbukkit.entity.CraftEntity) entity).getHandle());
            })
            .method("remove_npc", (obj, args) -> args.isEmpty() ? ScriptValue.of(false)
                : ScriptValue.of(CitizensSupport.removeNpc(bukkitEntity(args.get(0)))));

        PolyTypeRegistry.define("GriefPreventionBridge")
            .property("is_available", obj -> ScriptValue.of(GriefPreventionSupport.isAvailable()))
            .method("is_claimed", (obj, args) -> {
                org.bukkit.Location loc = locationArg(args, 0);
                return ScriptValue.of(loc != null && GriefPreventionSupport.isClaimed(loc));
            })
            .method("claim_owner", (obj, args) -> {
                org.bukkit.Location loc = locationArg(args, 0);
                if (loc == null) return ScriptValue.NULL;
                java.util.UUID owner = GriefPreventionSupport.claimOwner(loc);
                return owner == null ? ScriptValue.NULL : ScriptValue.of(owner.toString());
            })
            .method("can_build", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(true);
                org.bukkit.Location loc = locationArg(args, 1);
                return ScriptValue.of(loc == null || GriefPreventionSupport.canBuild(bukkitPlayer(args.get(0)), loc));
            });

        PolyTypeRegistry.define("DecentHologramsBridge")
            .property("is_available", obj -> ScriptValue.of(DecentHologramsSupport.isAvailable()))
            .method("create", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                org.bukkit.Location loc = locationArg(args, 1);
                return ScriptValue.of(loc != null
                        && DecentHologramsSupport.createHologram(args.get(0).asStr(), loc, stringArgList(args, 2)));
            })
            .method("set_lines", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(DecentHologramsSupport.setLines(args.get(0).asStr(), stringArgList(args, 1))))
            .method("remove", (obj, args) -> args.isEmpty() ? ScriptValue.of(false)
                : ScriptValue.of(DecentHologramsSupport.removeHologram(args.get(0).asStr())))
            .method("exists", (obj, args) -> args.isEmpty() ? ScriptValue.of(false)
                : ScriptValue.of(DecentHologramsSupport.exists(args.get(0).asStr())));

        PolyTypeRegistry.define("DiscordSrvBridge")
            .property("is_available", obj -> ScriptValue.of(DiscordSrvSupport.isAvailable()))
            .method("send_message", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(DiscordSrvSupport.sendMessage(args.get(0).asStr(), args.get(1).asStr())))
            .method("send_to_main_channel", (obj, args) -> args.isEmpty() ? ScriptValue.of(false)
                : ScriptValue.of(DiscordSrvSupport.sendToMainChannel(args.get(0).asStr())))
            .method("discord_id", (obj, args) -> args.isEmpty() ? ScriptValue.of("")
                : ScriptValue.of(DiscordSrvSupport.discordId(uuidOf(args.get(0)))))
            .method("minecraft_uuid", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                java.util.UUID uuid = DiscordSrvSupport.minecraftUuid(args.get(0).asStr());
                return uuid == null ? ScriptValue.NULL : ScriptValue.of(uuid.toString());
            });

        PolyTypeRegistry.define("PlaceholderApiBridge")
            .property("is_available", obj -> ScriptValue.of(PlaceholderSupport.isAvailable()))
            .method("set", (obj, args) -> args.size() < 2 ? ScriptValue.of("")
                : ScriptValue.of(PlaceholderSupport.set(offlinePlayer(args.get(0)), args.get(1).asStr())))
            .method("set_global", (obj, args) -> args.isEmpty() ? ScriptValue.of("")
                : ScriptValue.of(PlaceholderSupport.setGlobal(args.get(0).asStr())))
            // register_placeholder(identifier, "script.pf:func") — call from a script's __init__();
            // see PlaceholderSupport#registerPlaceholder's own javadoc for the full example.
            .method("register_placeholder", (obj, args) -> args.size() < 2 ? ScriptValue.of(false)
                : ScriptValue.of(PlaceholderSupport.registerPlaceholder(args.get(0).asStr(), args.get(1).asStr())))
            .method("unregister_placeholder", (obj, args) -> args.isEmpty() ? ScriptValue.of(false)
                : ScriptValue.of(PlaceholderSupport.unregisterPlaceholder(args.get(0).asStr())));
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
