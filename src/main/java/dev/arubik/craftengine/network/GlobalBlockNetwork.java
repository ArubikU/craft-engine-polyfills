package dev.arubik.craftengine.network;

import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class GlobalBlockNetwork {

    private static final GlobalBlockNetwork INSTANCE = new GlobalBlockNetwork();

    private final ConcurrentHashMap<Integer, Set<Entry>> channels = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Double> broadcastValues = new ConcurrentHashMap<>();

    private static final NamespacedKey KEY_CHANNELS = new NamespacedKey("polyfills", "network_channels");
    private static final NamespacedKey KEY_BROADCASTS = new NamespacedKey("polyfills", "network_broadcasts");

    private GlobalBlockNetwork() {}

    public static GlobalBlockNetwork instance() {
        return INSTANCE;
    }

    public void register(int channel, UUID worldId, int x, int y, int z) {
        channels.computeIfAbsent(channel, k -> ConcurrentHashMap.newKeySet())
                .add(new Entry(worldId, x, y, z));
    }

    public void unregister(int channel, UUID worldId, int x, int y, int z) {
        Set<Entry> set = channels.get(channel);
        if (set != null) {
            set.remove(new Entry(worldId, x, y, z));
        }
    }

    public void unregisterAll(UUID worldId, int x, int y, int z) {
        Entry entry = new Entry(worldId, x, y, z);
        for (Set<Entry> set : channels.values()) {
            set.remove(entry);
        }
    }

    public List<Entry> query(int channel) {
        Set<Entry> set = channels.get(channel);
        return set != null ? new ArrayList<>(set) : Collections.emptyList();
    }

    public void broadcast(int channel, double value) {
        broadcastValues.put(channel, value);
        // Also push into the new NetworkRegistry under "signal" type
        NetworkRegistry.getOrCreate(NetworkRegistry.TYPE_SIGNAL, channel)
            .broadcast(NetworkPacket.signal(channel, LEGACY_UUID, value));
    }

    public double listen(int channel) {
        return broadcastValues.getOrDefault(channel, 0.0);
    }

    private static final java.util.UUID LEGACY_UUID = new java.util.UUID(0L, 0L);

    public void clear() {
        channels.clear();
        broadcastValues.clear();
    }

    public void save(World world) {
        try {
            PersistentDataContainer pdc = world.getPersistentDataContainer();
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Integer, Set<Entry>> e : channels.entrySet()) {
                for (Entry entry : e.getValue()) {
                    if (sb.length() > 0) sb.append(';');
                    sb.append(e.getKey()).append(':')
                      .append(entry.worldId()).append(':')
                      .append(entry.x()).append(':')
                      .append(entry.y()).append(':')
                      .append(entry.z());
                }
            }
            pdc.set(KEY_CHANNELS, PersistentDataType.STRING, sb.toString());

            StringBuilder sb2 = new StringBuilder();
            for (Map.Entry<Integer, Double> e : broadcastValues.entrySet()) {
                if (sb2.length() > 0) sb2.append(';');
                sb2.append(e.getKey()).append('=').append(e.getValue());
            }
            pdc.set(KEY_BROADCASTS, PersistentDataType.STRING, sb2.toString());
        } catch (Throwable ignored) {}
    }

    public void load(World world) {
        try {
            PersistentDataContainer pdc = world.getPersistentDataContainer();
            String channelsStr = pdc.get(KEY_CHANNELS, PersistentDataType.STRING);
            if (channelsStr != null && !channelsStr.isBlank()) {
                for (String part : channelsStr.split(";")) {
                    String[] parts = part.split(":");
                    if (parts.length < 5) continue;
                    int ch = Integer.parseInt(parts[0]);
                    UUID uid = UUID.fromString(parts[1]);
                    int px = Integer.parseInt(parts[2]);
                    int py = Integer.parseInt(parts[3]);
                    int pz = Integer.parseInt(parts[4]);
                    register(ch, uid, px, py, pz);
                }
            }

            String broadcastStr = pdc.get(KEY_BROADCASTS, PersistentDataType.STRING);
            if (broadcastStr != null && !broadcastStr.isBlank()) {
                for (String part : broadcastStr.split(";")) {
                    String[] kv = part.split("=");
                    if (kv.length < 2) continue;
                    broadcastValues.put(Integer.parseInt(kv[0]), Double.parseDouble(kv[1]));
                }
            }
        } catch (Throwable ignored) {}
    }

    public record Entry(UUID worldId, int x, int y, int z) {}
}
