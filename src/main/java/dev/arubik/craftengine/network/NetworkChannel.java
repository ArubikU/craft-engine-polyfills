package dev.arubik.craftengine.network;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * A typed, filterable network channel.
 * Supports multi-subscriber, per-node filtering, and typed packet delivery.
 */
public final class NetworkChannel {
    private final int id;
    private final String type;
    private final ConcurrentHashMap<UUID, NetworkPacket> lastReceived = new ConcurrentHashMap<>();
    private volatile NetworkPacket lastBroadcast = null;
    private final ConcurrentHashMap<UUID, Predicate<NetworkPacket>> filters = new ConcurrentHashMap<>();

    public NetworkChannel(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public int id() { return id; }
    public String type() { return type; }
    public NetworkPacket lastBroadcast() { return lastBroadcast; }
    public int subscriberCount() { return filters.size(); }
    public boolean hasSubscriber(UUID nodeId) { return filters.containsKey(nodeId); }
    public Set<UUID> subscribers() { return Collections.unmodifiableSet(filters.keySet()); }

    public void broadcast(NetworkPacket packet) {
        this.lastBroadcast = packet;
        for (Map.Entry<UUID, Predicate<NetworkPacket>> e : filters.entrySet()) {
            if (e.getValue() == null || e.getValue().test(packet)) {
                lastReceived.put(e.getKey(), packet);
            }
        }
    }

    public void subscribe(UUID nodeId) { filters.putIfAbsent(nodeId, null); }
    public void subscribe(UUID nodeId, Predicate<NetworkPacket> filter) { filters.put(nodeId, filter); }
    public void unsubscribe(UUID nodeId) { filters.remove(nodeId); lastReceived.remove(nodeId); }

    public NetworkPacket receive(UUID nodeId) { return lastReceived.get(nodeId); }

    public boolean hasPacket(UUID nodeId) { return lastReceived.containsKey(nodeId); }
}
