package dev.arubik.craftengine.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Global controller for managing fake BlockEntity data.
 */
public class BlockEntityBehaviorController {

    // Level-unique runtime data (per-world)
    private static final Map<UUID, BlockData> BLOCKS = new ConcurrentHashMap<>();

    // Pending move operations to be processed safely during ticks
    private static final ConcurrentLinkedQueue<MoveRequest> MOVE_QUEUE = new ConcurrentLinkedQueue<>();

    // --- API methods ---

    public static void register(Level level, BlockPos pos) {
        processPendingMoves(level);
        UUID id = UUID.randomUUID();
        BlockData data = new BlockData(id, pos);
        BLOCKS.put(id, data);
        save(level);
    }

    public static void unregister(Level level, BlockPos pos) {
        processPendingMoves(level);
        BLOCKS.values().removeIf(d -> d.pos.equals(pos));
        save(level);
    }

    public static void tick(Level level, BlockPos pos) {
        processPendingMoves(level);
        get(level, pos).ifPresent(data -> {
            int count = getInt0(data.customData, "ticks", 0);
            data.customData.putInt("ticks", count + 1);
        });
        save(level);
    }

    public static void neighborChanged(Level level, BlockPos pos) {
        processPendingMoves(level);
        get(level, pos).ifPresent(data -> {
            data.customData.putBoolean("neighborChanged", true);
        });
        save(level);
    }

    public static Optional<BlockData> get(Level level, BlockPos pos) {
        return BLOCKS.values().stream().filter(d -> d.pos.equals(pos)).findFirst();
    }

    public static Optional<BlockData> getById(UUID id) {
        return Optional.ofNullable(BLOCKS.get(id));
    }

    public static void update(Level level, BlockPos pos, CompoundTag newTag) {
        processPendingMoves(level);
        get(level, pos).ifPresent(d -> d.customData = newTag.copy());
        save(level);
    }

    /**
     * Enqueue a move operation (e.g., piston). It will be processed on the next tick.
     */
    public static void move(Level level, BlockPos oldPos, BlockPos newPos) {
        if (level instanceof ServerLevel server) {
            MOVE_QUEUE.add(new MoveRequest(server, oldPos.immutable(), newPos.immutable()));
            save(level);
        } else {
            // Fallback: apply directly in client/non-server contexts
            get(level, oldPos).ifPresent(data -> data.pos = newPos.immutable());
        }
    }

    // --- Queue processing ---

    private static void processPendingMoves(Level level) {
        if (!(level instanceof ServerLevel server)) return;
        MoveRequest req;
        boolean dirty = false;
        // Drain only requests for this level
        final List<MoveRequest> deferred = new ArrayList<>();
        while ((req = MOVE_QUEUE.poll()) != null) {
            if (req.level == server) {
                // Update the matching BlockData by position
                for (BlockData d : BLOCKS.values()) {
                    if (d.pos.equals(req.from)) {
                        d.pos = req.to;
                        dirty = true;
                    }
                }
            } else {
                // Keep requests for other levels
                deferred.add(req);
            }
        }
        // Re-enqueue deferred requests for other levels
        if (!deferred.isEmpty()) MOVE_QUEUE.addAll(deferred);
        if (dirty) save(server);
    }

    private record MoveRequest(ServerLevel level, BlockPos from, BlockPos to) { }

    // --- Persistence ---

    private static void save(Level level) {
        if (level instanceof ServerLevel server) {
            BlockEntitySavedData.get(server).setDirty();
        }
    }

    /**
     * Replace in-memory data with saved snapshot.
     */
    public static void hydrateFromSaved(Collection<BlockData> saved) {
        BLOCKS.clear();
        for (BlockData d : saved) {
            BLOCKS.put(d.id, new BlockData(d.id, d.pos));
            // Deep copy custom data
            getById(d.id).ifPresent(nd -> nd.customData = d.customData.copy());
        }
    }

    /**
     * Snapshot of current runtime data.
     */
    public static Collection<BlockData> getAll() {
        return new ArrayList<>(BLOCKS.values());
    }

    // --- Data object ---

    public static class BlockData {
        public final UUID id;
        public BlockPos pos;
        public CompoundTag customData = new CompoundTag();

        public BlockData(UUID id, BlockPos pos) {
            this.id = id;
            this.pos = pos.immutable();
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            // UUID compatibility for older MC versions
            tag.putLong("idMost", id.getMostSignificantBits());
            tag.putLong("idLeast", id.getLeastSignificantBits());
            // Store position without relying on NbtUtils for maximum compatibility
            tag.putInt("px", pos.getX());
            tag.putInt("py", pos.getY());
            tag.putInt("pz", pos.getZ());
            tag.put("data", customData.copy());
            return tag;
        }

        public static BlockData load(CompoundTag tag) {
            long msb = getLong0(tag, "idMost", 0L);
            long lsb = getLong0(tag, "idLeast", 0L);
            UUID id = new UUID(msb, lsb);

            int x; int y; int z;
            // Prefer our px/py/pz format
            Integer ox = getInt0OrNull(tag, "px");
            Integer oy = getInt0OrNull(tag, "py");
            Integer oz = getInt0OrNull(tag, "pz");
            if (ox != null && oy != null && oz != null) {
                x = ox; y = oy; z = oz;
            } else {
                // Fallback to legacy compound under key "pos" with X/Y/Z or x/y/z
                CompoundTag posTag = getCompound0(tag, "pos");
                x = firstPresentInt(posTag, new String[]{"X", "x"}, 0);
                y = firstPresentInt(posTag, new String[]{"Y", "y"}, 0);
                z = firstPresentInt(posTag, new String[]{"Z", "z"}, 0);
            }
            BlockPos pos = new BlockPos(x, y, z);
            BlockData data = new BlockData(id, pos);
            data.customData = getCompound0(tag, "data");
            return data;
        }
    }

    // --- NBT helper methods (compatible across different APIs) ---
    private static Integer getInt0OrNull(CompoundTag t, String key) {
        try {
            var m = t.getClass().getMethod("getInt", String.class);
            Object val = m.invoke(t, key);
            if (val instanceof Integer i) return i;
            if (val instanceof java.util.Optional<?> opt) {
                Object o = opt.orElse(null);
                return (o instanceof Integer) ? (Integer) o : null;
            }
        } catch (Throwable ignored) {}
        return null;
    }

    private static int getInt0(CompoundTag t, String key, int def) {
        Integer v = getInt0OrNull(t, key);
        return v != null ? v : def;
    }

    private static long getLong0(CompoundTag t, String key, long def) {
        try {
            var m = t.getClass().getMethod("getLong", String.class);
            Object val = m.invoke(t, key);
            if (val instanceof Long l) return l;
            if (val instanceof java.util.Optional<?> opt) {
                Object o = opt.orElse(null);
                return (o instanceof Long) ? (Long) o : def;
            }
        } catch (Throwable ignored) {}
        return def;
    }

    private static CompoundTag getCompound0(CompoundTag t, String key) {
        try {
            var m = t.getClass().getMethod("getCompound", String.class);
            Object val = m.invoke(t, key);
            if (val instanceof CompoundTag ct) return ct;
            if (val instanceof java.util.Optional<?> opt) {
                Object o = opt.orElse(null);
                return (o instanceof CompoundTag) ? (CompoundTag) o : new CompoundTag();
            }
        } catch (Throwable ignored) {}
        return new CompoundTag();
    }

    private static int firstPresentInt(CompoundTag t, String[] keys, int def) {
        for (String k : keys) {
            Integer v = getInt0OrNull(t, k);
            if (v != null) return v;
        }
        return def;
    }
}
