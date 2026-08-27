package dev.arubik.craftengine.script.types.util;

import java.util.List;
import java.util.logging.Level;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import dev.arubik.craftengine.script.TypedKeyBridge;
import dev.arubik.craftengine.script.types.event.EventManagerType;
import dev.arubik.craftengine.script.types.primitive.ItemType;
import dev.arubik.craftengine.script.types.world.ServerType;
import dev.arubik.craftengine.sql.RedisDriver;

/**
 * {@code Redis} — thin scripting front for {@link RedisDriver}. Disabled unless {@code
 * database.yml}'s {@code redis.enabled} is true; every method silently no-ops (returns the
 * "absent" value for its type) when disabled/disconnected rather than throwing, since a script
 * using Redis as an optional cache/pubsub layer shouldn't have to guard every call with {@code
 * Redis.is_ready()} first — same "reads as harmless empty state" convention {@code get_typed}
 * already uses for a never-set key.
 */
public final class RedisDriverType {

    public static final Object INSTANCE = new Object();

    private RedisDriverType() {}

    public static void register() {
        PolyTypeRegistry.define("Redis")
            .methodTyped0("is_ready", TypeCodecs.BOOL, (Object obj) -> RedisDriver.isReady())
            .methodTyped1("get", TypeCodecs.STRING, TypeCodecs.STRING, "",
                (Object obj, String key) -> {
                    if (!RedisDriver.isReady()) return "";
                    try {
                        String v = RedisDriver.get(key);
                        return v == null ? "" : v;
                    } catch (Throwable t) { return ""; }
                })
            .methodTyped2("set", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String key, String value) -> {
                    if (!RedisDriver.isReady()) return false;
                    try { RedisDriver.set(key, value); return true; }
                    catch (Throwable t) { return false; }
                })
            // Redis.setex(key, value, seconds) — set with a TTL, e.g. a short-lived cache entry.
            .methodTyped3("setex", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (Object obj, String key, String value, Double seconds) -> {
                    if (!RedisDriver.isReady()) return false;
                    try { RedisDriver.setex(key, seconds.longValue(), value); return true; }
                    catch (Throwable t) { return false; }
                })
            .methodTyped1("del", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String key) -> {
                    if (!RedisDriver.isReady()) return false;
                    try { return RedisDriver.del(key); }
                    catch (Throwable t) { return false; }
                })
            .methodTyped1("exists", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String key) -> {
                    if (!RedisDriver.isReady()) return false;
                    try { return RedisDriver.exists(key); }
                    catch (Throwable t) { return false; }
                })
            .methodTyped2("expire", TypeCodecs.STRING, TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (Object obj, String key, Double seconds) -> {
                    if (!RedisDriver.isReady()) return false;
                    try { return RedisDriver.expire(key, seconds.longValue()); }
                    catch (Throwable t) { return false; }
                })
            .methodTyped1("incr", TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (Object obj, String key) -> {
                    if (!RedisDriver.isReady()) return 0.0;
                    try { return (double) RedisDriver.incr(key); }
                    catch (Throwable t) { return 0.0; }
                })
            .methodTyped2("incr_by", TypeCodecs.STRING, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, 0.0,
                (Object obj, String key, Double amount) -> {
                    if (!RedisDriver.isReady()) return 0.0;
                    try { return (double) RedisDriver.incrBy(key, amount.longValue()); }
                    catch (Throwable t) { return 0.0; }
                })
            .methodTyped2("publish", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (Object obj, String channel, String message) -> {
                    if (!RedisDriver.isReady()) return 0.0;
                    try { return (double) RedisDriver.publish(channel, message); }
                    catch (Throwable t) { return 0.0; }
                })

            // Redis.set_typed(key, type, value) / get_typed(key, type) — same TypedKeyBridge codec
            // convention as Server/Machine/SQL, so an item/vector/map round-trips identically here
            // (encoded as text, since Redis strings are the natural fit).
            .methodTyped3("set_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Object obj, String key, String type, ScriptValue value) -> {
                    if (!RedisDriver.isReady()) return false;
                    TypedKeyBridge.Codec codec = TypedKeyBridge.resolve(type);
                    if (codec == null) return false;
                    try {
                        Object raw = codec.toStorage(value);
                        RedisDriver.set(key, rawToText(raw));
                        return true;
                    } catch (Throwable t) {
                        CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[Redis] set_typed threw", t);
                        return false;
                    }
                })
            .methodTyped2("get_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, String key, String type) -> {
                    TypedKeyBridge.Codec codec = TypedKeyBridge.resolve(type);
                    if (codec == null) return ScriptValue.NULL;
                    if (!RedisDriver.isReady()) return codec.fromStorage(null);
                    try {
                        String stored = RedisDriver.get(key);
                        return codec.fromStorage(stored == null || stored.isEmpty() ? null
                                : SQLDriverType.textToRaw(codec, stored));
                    } catch (Throwable t) {
                        return codec.fromStorage(null);
                    }
                })

            // Redis.query_async is deliberately NOT offered — Redis ops are already sub-millisecond
            // and don't benefit from the extra callback-plumbing complexity get/set_async would add;
            // use SQL.query_async for anything that's actually worth taking off the main thread.
            ;
    }

    private static String rawToText(Object raw) {
        return switch (raw) {
            case null -> "";
            case String s -> s;
            case byte[] b -> java.util.Base64.getEncoder().encodeToString(b);
            default -> String.valueOf(raw);
        };
    }
}
