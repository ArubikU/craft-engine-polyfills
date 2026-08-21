package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.network.NetworkPacket;
import dev.arubik.craftengine.network.NetworkRegistry;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Network type — abstract packet-oriented channel system.
 *
 * Scripts access channels by (channelId) or (channelId, type).
 * Default type: "signal". Supported types: "signal", "redstone", "item", "fluid", "custom".
 *
 * Usage:
 *   Network.register(5)                 → subscribe to signal ch 5
 *   Network.register(5, "item")         → subscribe to item ch 5
 *   Network.broadcast(5, value)         → send ScriptValue on signal ch 5
 *   Network.broadcast(5, value, "item") → typed broadcast
 *   Network.listen(5)                   → receive last value on signal ch 5
 *   Network.listen(5, "item")           → typed receive
 *   Network.has_packet(5)               → bool: new packet arrived?
 *   Network.packet_type(5)              → string type of last packet
 *   Network.query(5)                    → Array of BlockType for all subscribers
 *   Network.unregister(5)               → leave signal ch 5
 */
public final class NetworkType {

    public record NetworkRef(ServerLevel level, UUID worldId, int x, int y, int z) {
        public UUID nodeId() {
            return UUID.nameUUIDFromBytes((worldId.toString() + ":" + x + ":" + y + ":" + z).getBytes());
        }
    }

    private NetworkType() {}

    public static void register() {
        PolyTypeRegistry.define("Network")
            // register(channel) / register(channel, type)
            .method("register", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                int channel = (int) args.get(0).asNum();
                String type = args.size() >= 2 ? args.get(1).asStr() : NetworkRegistry.TYPE_SIGNAL;
                NetworkRef r = ref(obj);
                NetworkRegistry.subscribe(type, channel, r.nodeId());
                // Legacy compat: also register in GlobalBlockNetwork for "signal"
                if (NetworkRegistry.TYPE_SIGNAL.equals(type)) {
                    dev.arubik.craftengine.network.GlobalBlockNetwork.instance()
                        .register(channel, r.worldId(), r.x(), r.y(), r.z());
                }
                return ScriptValue.of(true);
            })
            // unregister(channel) / unregister(channel, type)
            .method("unregister", (obj, args) -> {
                NetworkRef r = ref(obj);
                if (args.isEmpty()) {
                    NetworkRegistry.unsubscribeAll(r.nodeId());
                    dev.arubik.craftengine.network.GlobalBlockNetwork.instance()
                        .unregisterAll(r.worldId(), r.x(), r.y(), r.z());
                } else {
                    int channel = (int) args.get(0).asNum();
                    String type = args.size() >= 2 ? args.get(1).asStr() : NetworkRegistry.TYPE_SIGNAL;
                    NetworkRegistry.unsubscribe(type, channel, r.nodeId());
                    if (NetworkRegistry.TYPE_SIGNAL.equals(type)) {
                        dev.arubik.craftengine.network.GlobalBlockNetwork.instance()
                            .unregister(channel, r.worldId(), r.x(), r.y(), r.z());
                    }
                }
                return ScriptValue.of(true);
            })
            // broadcast(channel, value) / broadcast(channel, value, type)
            .method("broadcast", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                int channel = (int) args.get(0).asNum();
                ScriptValue payload = args.get(1);
                String type = args.size() >= 3 ? args.get(2).asStr() : NetworkRegistry.TYPE_SIGNAL;
                NetworkRef r = ref(obj);
                NetworkRegistry.broadcast(type, channel, r.nodeId(), payload);
                // Legacy compat for signal type
                if (NetworkRegistry.TYPE_SIGNAL.equals(type)) {
                    dev.arubik.craftengine.network.GlobalBlockNetwork.instance()
                        .broadcast(channel, payload.asNum());
                }
                return ScriptValue.of(true);
            })
            // listen(channel) / listen(channel, type) → ScriptValue payload or NULL
            .method("listen", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(0.0);
                int channel = (int) args.get(0).asNum();
                String type = args.size() >= 2 ? args.get(1).asStr() : NetworkRegistry.TYPE_SIGNAL;
                NetworkRef r = ref(obj);
                ScriptValue val = NetworkRegistry.receiveValue(type, channel, r.nodeId());
                if (val != ScriptValue.NULL) return val;
                // Fallback to legacy for signal
                if (NetworkRegistry.TYPE_SIGNAL.equals(type)) {
                    return ScriptValue.of(dev.arubik.craftengine.network.GlobalBlockNetwork.instance().listen(channel));
                }
                return ScriptValue.NULL;
            })
            // has_packet(channel) / has_packet(channel, type) → bool
            .method("has_packet", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                int channel = (int) args.get(0).asNum();
                String type = args.size() >= 2 ? args.get(1).asStr() : NetworkRegistry.TYPE_SIGNAL;
                return ScriptValue.of(NetworkRegistry.hasPacket(type, channel, ref(obj).nodeId()));
            })
            // packet_type(channel) → string type of last received packet or ""
            .method("packet_type", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of("");
                int channel = (int) args.get(0).asNum();
                NetworkRef r = ref(obj);
                // Check all types for a packet on this channel
                for (String t : new String[]{NetworkRegistry.TYPE_SIGNAL, NetworkRegistry.TYPE_REDSTONE,
                        NetworkRegistry.TYPE_ITEM, NetworkRegistry.TYPE_FLUID, NetworkRegistry.TYPE_CUSTOM}) {
                    if (NetworkRegistry.hasPacket(t, channel, r.nodeId())) return ScriptValue.of(t);
                }
                return ScriptValue.of("");
            })
            // query(channel) / query(channel, type) → Array of subscriber node UUIDs as strings
            .method("query", (obj, args) -> {
                if (args.isEmpty()) return new ScriptValue.Array(List.of());
                int channel = (int) args.get(0).asNum();
                String type = args.size() >= 2 ? args.get(1).asStr() : NetworkRegistry.TYPE_SIGNAL;
                // For "signal", also return legacy block entries as BlockType
                if (NetworkRegistry.TYPE_SIGNAL.equals(type) || args.size() < 2) {
                    NetworkRef r = ref(obj);
                    if (r.level() == null) return new ScriptValue.Array(List.of());
                    List<dev.arubik.craftengine.network.GlobalBlockNetwork.Entry> entries =
                        dev.arubik.craftengine.network.GlobalBlockNetwork.instance().query(channel);
                    List<ScriptValue> result = new ArrayList<>();
                    for (var entry : entries) {
                        if (entry.worldId().equals(r.worldId())) {
                            result.add(BlockType.wrap(r.level(), new BlockPos(entry.x(), entry.y(), entry.z())));
                        }
                    }
                    return new ScriptValue.Array(result);
                }
                // For other types, return UUID strings of subscribers
                List<ScriptValue> uuids = new ArrayList<>();
                for (UUID id : NetworkRegistry.subscribers(type, channel))
                    uuids.add(ScriptValue.of(id.toString()));
                return new ScriptValue.Array(uuids);
            });
    }

    public static ScriptValue wrap(ServerLevel level, int x, int y, int z) {
        if (level == null) return ScriptValue.NULL;
        UUID worldId = level.getWorld().getUID();
        return ScriptValue.ofObj("Network", new NetworkRef(level, worldId, x, y, z));
    }

    private static NetworkRef ref(Object obj) { return (NetworkRef) obj; }
}
