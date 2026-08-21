package dev.arubik.craftengine.network;

import dev.arubik.craftengine.script.ScriptValue;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Global registry of typed, filterable network channels.
 * Channels are keyed by (type, channelId). Default type is "signal".
 *
 * Supported types out-of-the-box: "signal", "redstone", "item", "fluid", "custom"
 * Scripts can use any string as type for custom packet types.
 */
public final class NetworkRegistry {

    public static final String TYPE_SIGNAL   = "signal";
    public static final String TYPE_REDSTONE = "redstone";
    public static final String TYPE_ITEM     = "item";
    public static final String TYPE_FLUID    = "fluid";
    public static final String TYPE_CUSTOM   = "custom";

    // type → channelId → channel
    private static final ConcurrentHashMap<String, ConcurrentHashMap<Integer, NetworkChannel>> CHANNELS
        = new ConcurrentHashMap<>();

    private NetworkRegistry() {}

    // ---- Channel access -------------------------------------------------------

    public static NetworkChannel getOrCreate(String type, int channelId) {
        return CHANNELS.computeIfAbsent(type, k -> new ConcurrentHashMap<>())
                       .computeIfAbsent(channelId, id -> new NetworkChannel(id, type));
    }

    public static NetworkChannel get(String type, int channelId) {
        var byType = CHANNELS.get(type);
        return byType != null ? byType.get(channelId) : null;
    }

    // ---- Subscribe / unsubscribe ----------------------------------------------

    public static void subscribe(String type, int channelId, UUID nodeId) {
        getOrCreate(type, channelId).subscribe(nodeId);
    }

    public static void subscribe(String type, int channelId, UUID nodeId, Predicate<NetworkPacket> filter) {
        getOrCreate(type, channelId).subscribe(nodeId, filter);
    }

    public static void unsubscribe(String type, int channelId, UUID nodeId) {
        NetworkChannel ch = get(type, channelId);
        if (ch != null) ch.unsubscribe(nodeId);
    }

    public static void unsubscribeAll(UUID nodeId) {
        for (var byType : CHANNELS.values())
            for (NetworkChannel ch : byType.values())
                ch.unsubscribe(nodeId);
    }

    // ---- Broadcast / receive --------------------------------------------------

    public static void broadcast(String type, int channelId, UUID sender, ScriptValue payload) {
        getOrCreate(type, channelId).broadcast(NetworkPacket.of(type, channelId, sender, payload));
    }

    public static NetworkPacket receive(String type, int channelId, UUID nodeId) {
        NetworkChannel ch = get(type, channelId);
        return ch != null ? ch.receive(nodeId) : null;
    }

    public static ScriptValue receiveValue(String type, int channelId, UUID nodeId) {
        NetworkPacket pkt = receive(type, channelId, nodeId);
        return pkt != null ? pkt.payload() : ScriptValue.NULL;
    }

    public static boolean hasPacket(String type, int channelId, UUID nodeId) {
        NetworkChannel ch = get(type, channelId);
        return ch != null && ch.hasPacket(nodeId);
    }

    // ---- Query ----------------------------------------------------------------

    public static Set<UUID> subscribers(String type, int channelId) {
        NetworkChannel ch = get(type, channelId);
        return ch != null ? ch.subscribers() : Set.of();
    }

    // ---- Lifecycle ------------------------------------------------------------

    public static void clear() { CHANNELS.clear(); }
}
