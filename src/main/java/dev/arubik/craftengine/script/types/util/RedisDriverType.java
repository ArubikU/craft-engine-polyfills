package dev.arubik.craftengine.script.types.util;

import java.util.List;
import java.util.logging.Level;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
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
            .method("is_ready", (obj, args) -> ScriptValue.of(RedisDriver.isReady()))
            .method("get", (obj, args) -> {
                if (args.isEmpty() || !RedisDriver.isReady()) return ScriptValue.of("");
                try {
                    String v = RedisDriver.get(args.get(0).asStr());
                    return ScriptValue.of(v == null ? "" : v);
                } catch (Throwable t) { return ScriptValue.of(""); }
            })
            .method("set", (obj, args) -> {
                if (args.size() < 2 || !RedisDriver.isReady()) return ScriptValue.of(false);
                try { RedisDriver.set(args.get(0).asStr(), args.get(1).asStr()); return ScriptValue.of(true); }
                catch (Throwable t) { return ScriptValue.of(false); }
            })
            // Redis.setex(key, value, seconds) — set with a TTL, e.g. a short-lived cache entry.
            .method("setex", (obj, args) -> {
                if (args.size() < 3 || !RedisDriver.isReady()) return ScriptValue.of(false);
                try { RedisDriver.setex(args.get(0).asStr(), (long) args.get(2).asNum(), args.get(1).asStr()); return ScriptValue.of(true); }
                catch (Throwable t) { return ScriptValue.of(false); }
            })
            .method("del", (obj, args) -> {
                if (args.isEmpty() || !RedisDriver.isReady()) return ScriptValue.of(false);
                try { return ScriptValue.of(RedisDriver.del(args.get(0).asStr())); }
                catch (Throwable t) { return ScriptValue.of(false); }
            })
            .method("exists", (obj, args) -> {
                if (args.isEmpty() || !RedisDriver.isReady()) return ScriptValue.of(false);
                try { return ScriptValue.of(RedisDriver.exists(args.get(0).asStr())); }
                catch (Throwable t) { return ScriptValue.of(false); }
            })
            .method("expire", (obj, args) -> {
                if (args.size() < 2 || !RedisDriver.isReady()) return ScriptValue.of(false);
                try { return ScriptValue.of(RedisDriver.expire(args.get(0).asStr(), (long) args.get(1).asNum())); }
                catch (Throwable t) { return ScriptValue.of(false); }
            })
            .method("incr", (obj, args) -> {
                if (args.isEmpty() || !RedisDriver.isReady()) return ScriptValue.of(0);
                try { return ScriptValue.of(RedisDriver.incr(args.get(0).asStr())); }
                catch (Throwable t) { return ScriptValue.of(0); }
            })
            .method("incr_by", (obj, args) -> {
                if (args.size() < 2 || !RedisDriver.isReady()) return ScriptValue.of(0);
                try { return ScriptValue.of(RedisDriver.incrBy(args.get(0).asStr(), (long) args.get(1).asNum())); }
                catch (Throwable t) { return ScriptValue.of(0); }
            })
            .method("publish", (obj, args) -> {
                if (args.size() < 2 || !RedisDriver.isReady()) return ScriptValue.of(0);
                try { return ScriptValue.of(RedisDriver.publish(args.get(0).asStr(), args.get(1).asStr())); }
                catch (Throwable t) { return ScriptValue.of(0); }
            })

            // Redis.set_typed(key, type, value) / get_typed(key, type) — same TypedKeyBridge codec
            // convention as Server/Machine/SQL, so an item/vector/map round-trips identically here
            // (encoded as text, since Redis strings are the natural fit).
            .method("set_typed", (obj, args) -> {
                if (args.size() < 3 || !RedisDriver.isReady()) return ScriptValue.of(false);
                TypedKeyBridge.Codec codec = TypedKeyBridge.resolve(args.get(1).asStr());
                if (codec == null) return ScriptValue.of(false);
                try {
                    Object raw = codec.toStorage(args.get(2));
                    RedisDriver.set(args.get(0).asStr(), rawToText(raw));
                    return ScriptValue.of(true);
                } catch (Throwable t) {
                    CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[Redis] set_typed threw", t);
                    return ScriptValue.of(false);
                }
            })
            .method("get_typed", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.NULL;
                TypedKeyBridge.Codec codec = TypedKeyBridge.resolve(args.get(1).asStr());
                if (codec == null) return ScriptValue.NULL;
                if (!RedisDriver.isReady()) return codec.fromStorage(null);
                try {
                    String stored = RedisDriver.get(args.get(0).asStr());
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
