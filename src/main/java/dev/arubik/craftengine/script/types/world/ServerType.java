package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import dev.arubik.craftengine.util.ServerFlags;

/**
 * "Server" singleton — the script-facing surface over {@link ServerFlags}: plain global
 * key/value state, scoped to neither a block ({@code Machine.get_typed}), an entity ({@code
 * Entity.get_typed}/{@code Player.get_typed}), nor a single world ({@code World.get_typed}).
 *
 * <p>A cross-block feature that needs to track "every X currently registered under a name" — a
 * frequency, a team, a global toggle — stores it here under its own name; this stays a flat store
 * on purpose so it keeps being reusable instead of growing a bespoke shape per feature.
 *
 * <pre>
 *   Server.set_typed("boss_defeated", "bool", true)
 *   Server.set_typed("teleporter_freq_home", "string", "minecraft:overworld,120,64,-30;minecraft:the_nether,15,80,4")
 *   for entry in split(Server.get_typed("teleporter_freq_home", "string"), ";") { ... }
 * </pre>
 */
public final class ServerType {

    public static final Object INSTANCE = new Object();

    private ServerType() {
    }

    public static void register() {
        PolyTypeRegistry.define("Server")
            // Generic TypedKey storage — one flat store that can hold ANY registered type by name
            // ("int"/"bool"/"item"/"vector"/"compound"/"uuid"/a custom-registered one/...). Same
            // encoding SQL.get_typed/set_typed and Redis.get_typed/set_typed use, so a value moved
            // between this in-memory store and a real database round-trips identically.
            // key/type are always strings (TypeCodecs.STRING); the stored VALUE stays
            // TypeCodecs.RAW (a ScriptValue passthrough) since its real coercion is dynamic,
            // decided at call time by whichever TypedKeyBridge.Codec `type` names — same reasoning
            // as MachineType#get_typed/set_typed's typed migration. `obj` is unused (Server is a
            // process-wide singleton backed by ServerFlags, not per-instance state).
            .methodTyped2("get_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, String key, String type) -> {
                    dev.arubik.craftengine.script.TypedKeyBridge.Codec codec =
                            dev.arubik.craftengine.script.TypedKeyBridge.resolve(type);
                    if (codec == null) return ScriptValue.NULL;
                    return codec.fromStorage(ServerFlags.getTyped(key));
                })
            .methodTyped3("set_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Object obj, String key, String type, ScriptValue value) -> {
                    dev.arubik.craftengine.script.TypedKeyBridge.Codec codec =
                            dev.arubik.craftengine.script.TypedKeyBridge.resolve(type);
                    if (codec == null) return false;
                    try {
                        ServerFlags.setTyped(key, codec.toStorage(value));
                        return true;
                    } catch (Throwable ignored) { return false; }
                })
            .methodTyped1("has_typed", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String key) -> ServerFlags.hasTyped(key))
            // time — current wall-clock epoch SECONDS (not millis — deliberately, so it round-trips
            // cleanly through the (int) storage get_typed/set_typed already use everywhere, instead
            // of needing a separate string-timestamp convention just for this). For a script that
            // needs to stamp "when did this happen" and later check "has too long passed" itself
            // (e.g. a request expiring after N minutes) rather than relying solely on a
            // TaskManager cleanup task firing — the two are meant to be used TOGETHER: a stamped
            // timestamp makes expiry checkable at any time (including right when someone tries to
            // act on the stale state), while a TaskManager.schedule(...) cleanup actually reclaims
            // the entry once nobody's looking.
            .property("time", obj -> ScriptValue.of((double) (System.currentTimeMillis() / 1000L)))
            // General escape hatch letting a script trigger any other registered command as console —
            // e.g. a `/cmds`-defined command wanting to chain into another command without a player context.
            .methodTyped1("exec_command", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String cmdArg) -> {
                    String cmd = cmdArg;
                    if (cmd.startsWith("/")) cmd = cmd.substring(1);
                    try {
                        return org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(), cmd);
                    } catch (Throwable t) {
                        return false;
                    }
                })
            // get_player(name) — exact-match online-player lookup by name (not the fuzzy
            // prefix-match Bukkit#getPlayer does, since a script branching on "did I find the
            // right player" wants a deterministic yes/no, not a guess). NULL if offline/unknown —
            // e.g. a `/cmds` auction command resolving a seller's name typed by a buyer.
            .methodTyped1("get_player", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, String name) -> {
                    org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayerExact(name);
                    if (p == null) return ScriptValue.NULL;
                    return dev.arubik.craftengine.script.types.entity.PlayerType.wrap(((org.bukkit.craftbukkit.entity.CraftPlayer) p).getHandle());
                })
            // get_player_by_uuid(uuid) — same as get_player but by UUID string (survives a name
            // change, e.g. re-resolving a UUID persisted in Server.get_typed/Player.get_typed storage).
            .methodTyped1("get_player_by_uuid", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, String uuidStr) -> {
                    try {
                        org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(java.util.UUID.fromString(uuidStr));
                        if (p == null) return ScriptValue.NULL;
                        return dev.arubik.craftengine.script.types.entity.PlayerType.wrap(((org.bukkit.craftbukkit.entity.CraftPlayer) p).getHandle());
                    } catch (IllegalArgumentException badUuid) {
                        return ScriptValue.NULL;
                    }
                })
            // get_entity_by_uuid(uuid) — any loaded entity (player or not) server-wide by UUID.
            .methodTyped1("get_entity_by_uuid", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, String uuidStr) -> {
                    try {
                        org.bukkit.entity.Entity e = org.bukkit.Bukkit.getEntity(java.util.UUID.fromString(uuidStr));
                        if (e == null) return ScriptValue.NULL;
                        return dev.arubik.craftengine.script.types.entity.EntityType.wrap(((org.bukkit.craftbukkit.entity.CraftEntity) e).getHandle());
                    } catch (IllegalArgumentException badUuid) {
                        return ScriptValue.NULL;
                    }
                })
            // get_offline_name(uuid) — a player's name whether they're online or not, resolved
            // purely from Bukkit's local player-data cache (Bukkit.getOfflinePlayer(UUID) never
            // makes a network call — unlike its String-name overload, which can silently BLOCK
            // the main thread with a Mojang API request; that overload is deliberately never used
            // anywhere in this addon's scripting API). "" if this UUID has never played here — a
            // script that owns a NAME already (e.g. stashed at creation time, see warps.pf's
            // warp_owner_name_key) should keep using that instead of round-tripping through here.
            .methodTyped1("get_offline_name", TypeCodecs.STRING, TypeCodecs.STRING, "",
                (Object obj, String uuidStr) -> {
                    try {
                        org.bukkit.OfflinePlayer p = org.bukkit.Bukkit.getOfflinePlayer(java.util.UUID.fromString(uuidStr));
                        String name = p.getName();
                        return name != null ? name : "";
                    } catch (IllegalArgumentException badUuid) {
                        return "";
                    }
                });
    }

    public static ScriptValue wrap() {
        return ScriptValue.ofObj("Server", INSTANCE);
    }
}
