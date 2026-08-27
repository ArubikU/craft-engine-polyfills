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
            // MIXED arity — a REQUIRED leading `channel` plus a default-if-missing trailing `type`
            // — expressed with methodTypedOpt2 and the NULL-SENTINEL idiom (this reasoning applies
            // to every typed method in this file; they all share the shape):
            //   * `channel`'s default is null, which can only ever mean "argument absent": a PRESENT
            //     argument decodes through TypeCodecs.DOUBLE, whose asNum() is primitive-backed and
            //     so never yields Java null. So `channel == null` is EXACTLY the original's
            //     `args.isEmpty()`, and re-checking it at the very top of the body — before any
            //     registry call — reproduces the original's "short-circuit having done nothing"
            //     exactly, side effect included (i.e. none).
            //   * `type` keeps its real default (NetworkRegistry.TYPE_SIGNAL) so the 1-arg call form
            //     behaves as before. (methodTyped2's onMissingArgs could not express this: it would
            //     reject the valid 1-arg form outright.)
            .methodTypedOpt2("register", TypeCodecs.DOUBLE, (Double) null,
                TypeCodecs.STRING, NetworkRegistry.TYPE_SIGNAL, TypeCodecs.BOOL,
                (NetworkRef r, Double channelArg, String type) -> {
                if (channelArg == null) return false;
                int channel = (int) (double) channelArg;
                NetworkRegistry.subscribe(type, channel, r.nodeId());
                // Legacy compat: also register in GlobalBlockNetwork for "signal"
                if (NetworkRegistry.TYPE_SIGNAL.equals(type)) {
                    dev.arubik.craftengine.network.GlobalBlockNetwork.instance()
                        .register(channel, r.worldId(), r.x(), r.y(), r.z());
                }
                return true;
                })
            // unregister(channel) / unregister(channel, type) — no-arg form unsubscribes from
            // EVERYTHING. That second shape survives the migration because the branch it hangs off,
            // `args.isEmpty()`, is precisely the null sentinel on the required `channel` slot (see
            // register above) — the handler doesn't need an argument COUNT to distinguish them.
            .methodTypedOpt2("unregister", TypeCodecs.DOUBLE, (Double) null,
                TypeCodecs.STRING, NetworkRegistry.TYPE_SIGNAL, TypeCodecs.BOOL,
                (NetworkRef r, Double channelArg, String type) -> {
                if (channelArg == null) {
                    NetworkRegistry.unsubscribeAll(r.nodeId());
                    dev.arubik.craftengine.network.GlobalBlockNetwork.instance()
                        .unregisterAll(r.worldId(), r.x(), r.y(), r.z());
                } else {
                    int channel = (int) (double) channelArg;
                    NetworkRegistry.unsubscribe(type, channel, r.nodeId());
                    if (NetworkRegistry.TYPE_SIGNAL.equals(type)) {
                        dev.arubik.craftengine.network.GlobalBlockNetwork.instance()
                            .unregister(channel, r.worldId(), r.x(), r.y(), r.z());
                    }
                }
                return true;
                })
            // broadcast(channel, value) / broadcast(channel, value, type)
            // Two required slots this time, so the null sentinel goes on the LAST of them
            // (`payload`): arguments are positional, so `payload == null` is exactly
            // `args.size() < 2`. Its RAW codec is identity over an args element, which is never
            // null, so a present argument can't be mistaken for an absent one either.
            .methodTypedOpt3("broadcast", TypeCodecs.DOUBLE, (Double) null, TypeCodecs.RAW, (ScriptValue) null,
                TypeCodecs.STRING, NetworkRegistry.TYPE_SIGNAL, TypeCodecs.BOOL,
                (NetworkRef r, Double channelArg, ScriptValue payload, String type) -> {
                if (payload == null) return false;
                int channel = (int) (double) channelArg;
                NetworkRegistry.broadcast(type, channel, r.nodeId(), payload);
                // Legacy compat for signal type
                if (NetworkRegistry.TYPE_SIGNAL.equals(type)) {
                    dev.arubik.craftengine.network.GlobalBlockNetwork.instance()
                        .broadcast(channel, payload.asNum());
                }
                return true;
                })
            // listen(channel) / listen(channel, type) → ScriptValue payload or NULL
            // RAW return — the payload is a dynamic ScriptValue. The null sentinel returns the
            // literal 0.0 before any registry lookup happens, exactly as the original did.
            .methodTypedOpt2("listen", TypeCodecs.DOUBLE, (Double) null,
                TypeCodecs.STRING, NetworkRegistry.TYPE_SIGNAL, TypeCodecs.RAW,
                (NetworkRef r, Double channelArg, String type) -> {
                if (channelArg == null) return ScriptValue.of(0.0);
                int channel = (int) (double) channelArg;
                ScriptValue val = NetworkRegistry.receiveValue(type, channel, r.nodeId());
                if (val != ScriptValue.NULL) return val;
                // Fallback to legacy for signal
                if (NetworkRegistry.TYPE_SIGNAL.equals(type)) {
                    return ScriptValue.of(dev.arubik.craftengine.network.GlobalBlockNetwork.instance().listen(channel));
                }
                return ScriptValue.NULL;
                })
            // has_packet(channel) / has_packet(channel, type) → bool
            .methodTypedOpt2("has_packet", TypeCodecs.DOUBLE, (Double) null,
                TypeCodecs.STRING, NetworkRegistry.TYPE_SIGNAL, TypeCodecs.BOOL,
                (NetworkRef r, Double channelArg, String type) -> {
                if (channelArg == null) return false;
                return NetworkRegistry.hasPacket(type, (int) (double) channelArg, r.nodeId());
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
            // The original's legacy-block branch read `TYPE_SIGNAL.equals(type) || args.size() < 2`,
            // but that second disjunct was REDUNDANT: args.size() < 2 is exactly the case where
            // `type` took its TYPE_SIGNAL default, so the first disjunct already covered it. Dropping
            // it changes nothing and leaves a condition that needs no argument count — so the null
            // sentinel on `channel` (see register above) is all this needs. RAW return: an Array.
            // The return CANNOT be a TypeCodecs.listOf — its element type is branch-dependent:
            // "signal" yields Obj-wrapped "Block" values, every other type yields raw UUID strings.
            .methodTypedOpt2("query", TypeCodecs.DOUBLE, (Double) null,
                TypeCodecs.STRING, NetworkRegistry.TYPE_SIGNAL, TypeCodecs.RAW,
                (NetworkRef r, Double channelArg, String type) -> {
                if (channelArg == null) return new ScriptValue.Array(List.of());
                int channel = (int) (double) channelArg;
                // For "signal", also return legacy block entries as BlockType
                if (NetworkRegistry.TYPE_SIGNAL.equals(type)) {
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
}
