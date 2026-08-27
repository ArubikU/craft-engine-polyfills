package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.network.NetworkPacket;
import dev.arubik.craftengine.network.NetworkRegistry;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
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
            // Not migrated — MIXED arity, which neither typed form covers (this reasoning applies to
            // every untyped method in this file; they all share the shape):
            //   * `channel` is REQUIRED: args.isEmpty() short-circuits to false having done nothing,
            //     so methodTypedOptN is wrong — it makes every argument optional and ALWAYS runs the
            //     handler, so a no-arg call would subscribe to whatever default channel we picked.
            //     That's a real side effect the original explicitly refuses to perform.
            //   * `type` is a default-if-missing trailing ARGUMENT (args.size() >= 2 ?
            //     args.get(1).asStr() : NetworkRegistry.TYPE_SIGNAL), so methodTyped2's
            //     onMissingArgs is wrong too — it would reject the valid 1-arg call form outright.
            // There is no "first N required, rest optional" typed overload, so this stays untyped.
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
            // Not migrated: multi-shape dynamic dispatch — args.isEmpty() takes an entirely
            // different code path (unsubscribe-all) than the channel+type path below it, plus the
            // same default-if-missing type argument as register() above. methodTypedOptN can't help:
            // it never tells the handler how many arguments were actually supplied, so the
            // unsubscribe-all branch is unreachable from a typed registration. Left untyped.
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
            // Not migrated: same mixed arity as register() above — channel+value are required (a
            // short call returns false and broadcasts nothing, which methodTypedOpt3 would turn into
            // a real broadcast), while the trailing type is default-if-missing.
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
            // Not migrated: same mixed arity as register() above — a no-arg call must return the
            // literal 0.0 without consulting the registry at all, whereas methodTypedOpt2 would run
            // a real lookup against whatever default channel we picked and return that channel's
            // payload instead. The trailing type is default-if-missing.
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
            // Not migrated: same mixed arity as register() above — a no-arg call must answer false
            // flatly, not report on some default channel, while the trailing type is
            // default-if-missing.
            .method("has_packet", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                int channel = (int) args.get(0).asNum();
                String type = args.size() >= 2 ? args.get(1).asStr() : NetworkRegistry.TYPE_SIGNAL;
                return ScriptValue.of(NetworkRegistry.hasPacket(type, channel, ref(obj).nodeId()));
            })
            // packet_type(channel) → string type of last received packet or "". Unlike its siblings
            // above, this one has no optional type argument — fixed 1-arg shape, migrated.
            .methodTyped1("packet_type", TypeCodecs.DOUBLE, TypeCodecs.STRING, "",
                (NetworkRef r, Double channelArg) -> {
                    int channel = (int) (double) channelArg;
                    // Check all types for a packet on this channel
                    for (String t : new String[]{NetworkRegistry.TYPE_SIGNAL, NetworkRegistry.TYPE_REDSTONE,
                            NetworkRegistry.TYPE_ITEM, NetworkRegistry.TYPE_FLUID, NetworkRegistry.TYPE_CUSTOM}) {
                        if (NetworkRegistry.hasPacket(t, channel, r.nodeId())) return t;
                    }
                    return "";
                })
            // query(channel) / query(channel, type) → Array of subscriber node UUIDs as strings
            // Not migrated: same mixed arity as register() above, plus genuine multi-shape dispatch
            // that reads args.size() DIRECTLY (args.size() < 2 branches to a completely different
            // legacy-block return shape than the "other types" branch) — no typed form hands the
            // handler an argument count at all, so that branch is inexpressible either way.
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
