package dev.arubik.craftengine.script.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Creates transient fake ServerPlayer instances for script interactions
 * (bone meal application, item use on entities, etc).
 *
 * These are NOT registered with the server — create, use, discard.
 * Cached weakly per (level, uuid) pair to avoid repeated allocation within one tick.
 */
public final class FakePlayerUtil {

    private FakePlayerUtil() {}

    private static final WeakHashMap<ServerLevel, ServerPlayer> cache = new WeakHashMap<>();

    /**
     * Get or create a fake player for the given level with a specific UUID/name.
     * The fake player is positioned at world spawn and is never tracked by the server.
     */
    public static ServerPlayer getOrCreate(ServerLevel level, UUID uuid, String name) {
        ServerPlayer cached = cache.get(level);
        if (cached != null && cached.getUUID().equals(uuid)) return cached;
        ServerPlayer fake = create(level, uuid, name);
        if (fake != null) cache.put(level, fake);
        return fake;
    }

    public static ServerPlayer getOrCreate(ServerLevel level, UUID uuid) {
        return getOrCreate(level, uuid, "FakePlayer");
    }

    /** Create a new fake player (not cached). Returns null on failure. */
    public static ServerPlayer create(ServerLevel level, UUID uuid, String name) {
        try {
            MinecraftServer server = level.getServer();
            if (server == null) return null;
            GameProfile profile = new GameProfile(uuid, name != null ? name : "FakePlayer");
            // Construct via reflection to avoid hard dependency on specific constructor signature
            var ctors = ServerPlayer.class.getDeclaredConstructors();
            for (var ctor : ctors) {
                var params = ctor.getParameterTypes();
                if (params.length >= 3
                        && params[0].isAssignableFrom(MinecraftServer.class)
                        && params[1].isAssignableFrom(ServerLevel.class)
                        && params[2].isAssignableFrom(GameProfile.class)) {
                    ctor.setAccessible(true);
                    Object[] args = new Object[params.length];
                    args[0] = server;
                    args[1] = level;
                    args[2] = profile;
                    // Fill remaining params with nulls — they're optional metadata
                    ServerPlayer fake = (ServerPlayer) ctor.newInstance(args);
                    fake.setPos(0, 64, 0);
                    return fake;
                }
            }
        } catch (Throwable ignored) {}
        return null;
    }

    public static ServerPlayer create(ServerLevel level, UUID uuid) {
        return create(level, uuid, "FakePlayer");
    }

    /** Create a fake player with a deterministic UUID based on a string key (e.g. machine UUID). */
    public static ServerPlayer createFor(ServerLevel level, String key) {
        UUID uuid = UUID.nameUUIDFromBytes(("fakeplayer:" + key).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return getOrCreate(level, uuid, "FP_" + key.substring(0, Math.min(key.length(), 8)));
    }
}
