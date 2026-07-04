package dev.arubik.craftengine.contraption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

import dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Tracks piston bearings in the EULER / ROBIN_EULER "extended-solid" state (2026-07-03 goal — "al
 * llegar al final se convierte en bloques reales; si le metes redstone vuelve a ser contraption").
 * When such a piston reaches the end, {@code ContraptionEngine} disassembles its load into REAL
 * world blocks and records the drop here: the body's real position, the piston's facing/distance/
 * speed/su/mode, and the EXACT world positions the load landed on. Every tick {@link #tick} then
 * checks each recorded bearing — a redstone pulse at the body (EULER) or the dwell timer elapsing
 * (ROBIN_EULER) re-captures those blocks into a contraption that immediately retracts them home
 * (see {@code ContraptionAssembler#assembleExplicitRetracting}). Persisted to disk so the redstone
 * trigger still works after a restart.
 */
public final class EulerExtendedRegistry {

    private EulerExtendedRegistry() {
    }

    private static final class Entry {
        final UUID worldId;
        final BlockPos bodyPos;
        final Vec3 facing;
        final int distance;
        final double speed;
        final double su;
        final PistonBearingBehavior.Mode mode;
        final long delay;
        final Set<BlockPos> positions;
        /** Positions of the physical shaft (pipe/head) filler blocks placed when the load became real. */
        final Set<BlockPos> shaftPositions;
        long timer = 0;
        boolean prevRedstone;

        Entry(UUID worldId, BlockPos bodyPos, Vec3 facing, int distance, double speed, double su,
                PistonBearingBehavior.Mode mode, long delay, Set<BlockPos> positions, Set<BlockPos> shaftPositions,
                boolean initialRedstone) {
            this.worldId = worldId;
            this.bodyPos = bodyPos.immutable();
            this.facing = facing;
            this.distance = distance;
            this.speed = speed;
            this.su = su;
            this.mode = mode;
            this.delay = delay;
            this.positions = positions;
            this.shaftPositions = shaftPositions == null ? new HashSet<>() : shaftPositions;
            // Seed to whatever the signal ALREADY is at record time (2026-07-04 fix — "al llegar al
            // final regresa automaticamente"): defaulting this to false meant a lever/signal still
            // held HIGH the instant the piston finished extending looked like a brand-new rising edge
            // on the very next tick, immediately re-triggering a return with no new player action.
            this.prevRedstone = initialRedstone;
        }
    }

    private static final Map<UUID, Map<Long, Entry>> STORE = new ConcurrentHashMap<>();

    /** Records a euler/robin_euler bearing that just dropped its load as real blocks. */
    public static void record(UUID worldId, BlockPos bodyPos, Vec3 facing, int distance, double speed, double su,
            PistonBearingBehavior.Mode mode, long delay, Set<BlockPos> positions, Set<BlockPos> shaftPositions,
            boolean initialRedstone) {
        STORE.computeIfAbsent(worldId, k -> new ConcurrentHashMap<>())
                .put(bodyPos.asLong(), new Entry(worldId, bodyPos, facing, distance, speed, su, mode, delay,
                        new HashSet<>(positions), new HashSet<>(shaftPositions), initialRedstone));
    }

    /** Drops a recorded bearing (e.g. hammer-disassembled while extended-solid, or re-assembled). */
    public static void forget(UUID worldId, BlockPos bodyPos) {
        Map<Long, Entry> w = STORE.get(worldId);
        if (w != null) {
            w.remove(bodyPos.asLong());
        }
    }

    public static boolean isExtendedSolid(UUID worldId, BlockPos bodyPos) {
        Map<Long, Entry> w = STORE.get(worldId);
        return w != null && w.containsKey(bodyPos.asLong());
    }

    /** Per-tick check: redstone pulse (euler) or dwell timer (robin_euler) → re-assemble + retract. */
    public static void tick() {
        for (Map.Entry<UUID, Map<Long, Entry>> we : STORE.entrySet()) {
            World world = Bukkit.getWorld(we.getKey());
            if (world == null) {
                continue; // world not loaded — leave the record for when it loads
            }
            Level level = ((CraftWorld) world).getHandle();
            List<Entry> triggered = null;
            for (Entry e : we.getValue().values()) {
                boolean redstone;
                try {
                    redstone = level.hasNeighborSignal(e.bodyPos);
                } catch (Throwable t) {
                    continue; // chunk not loaded
                }
                boolean rising = redstone && !e.prevRedstone;
                e.prevRedstone = redstone;
                // EXTENDED-solid → re-grab + retract. LINEAR = redstone pulse; ROUND_ROBIN = powered
                // auto-cycle after the dwell, stops (stays extended-solid) when unpowered (2026-07-03
                // unify — solid at rest, move on trigger; euler is now the standard for both).
                boolean fire;
                if (e.mode == PistonBearingBehavior.Mode.ROUND_ROBIN) {
                    if (redstone) {
                        fire = (++e.timer >= Math.max(1, e.delay));
                    } else {
                        e.timer = 0;
                        fire = false;
                    }
                } else {
                    fire = rising;
                }
                if (fire) {
                    if (triggered == null) {
                        triggered = new ArrayList<>();
                    }
                    triggered.add(e);
                }
            }
            if (triggered != null) {
                for (Entry e : triggered) {
                    we.getValue().remove(e.bodyPos.asLong());
                    try {
                        // Remove the physical shaft filler (pipe/head blocks) — the piston is about to
                        // render it again as packet-only while it retracts.
                        for (BlockPos sp : e.shaftPositions) {
                            try {
                                level.setBlock(sp, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
                            } catch (Throwable ignored) {
                            }
                        }
                        ContraptionEntity entity = ContraptionAssembler.assembleExplicitRetracting(world, e.bodyPos,
                                e.positions, e.facing, e.distance, e.speed, e.su, e.mode, e.delay);
                        if (entity != null) {
                            BearingHammerListener.markAssembled(e.worldId, e.bodyPos, entity.state().id());
                        }
                    } catch (Throwable t) {
                        Bukkit.getLogger().warning("[Contraption] euler re-assemble failed: " + t);
                    }
                }
            }
        }
    }

    // ---- persistence (mirrors GlueRegistry) ----

    public static void saveAll(Path file) throws IOException {
        CompoundTag root = new CompoundTag();
        ListTag entries = new ListTag();
        for (Map<Long, Entry> w : STORE.values()) {
            for (Entry e : w.values()) {
                CompoundTag t = new CompoundTag();
                t.putString("world", e.worldId.toString());
                t.putLong("body", e.bodyPos.asLong());
                t.putDouble("fx", e.facing.x);
                t.putDouble("fy", e.facing.y);
                t.putDouble("fz", e.facing.z);
                t.putInt("distance", e.distance);
                t.putDouble("speed", e.speed);
                t.putDouble("su", e.su);
                t.putString("mode", e.mode.name());
                t.putLong("delay", e.delay);
                t.putLong("timer", e.timer);
                long[] pos = new long[e.positions.size()];
                int i = 0;
                for (BlockPos p : e.positions) {
                    pos[i++] = p.asLong();
                }
                t.putLongArray("positions", pos);
                long[] shaft = new long[e.shaftPositions.size()];
                int j = 0;
                for (BlockPos p : e.shaftPositions) {
                    shaft[j++] = p.asLong();
                }
                t.putLongArray("shaft", shaft);
                entries.add(t);
            }
        }
        root.put("entries", entries);
        Files.createDirectories(file.getParent());
        NbtIo.writeCompressed(root, file);
    }

    public static void loadAll(Path file) throws IOException {
        if (!Files.exists(file)) {
            return;
        }
        CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
        ListTag entries = root.getListOrEmpty("entries");
        for (int i = 0; i < entries.size(); i++) {
            CompoundTag t = entries.getCompoundOrEmpty(i);
            UUID worldId;
            try {
                worldId = UUID.fromString(t.getString("world").orElse(""));
            } catch (IllegalArgumentException bad) {
                continue;
            }
            BlockPos body = BlockPos.of(t.getLong("body").orElse(0L));
            Vec3 facing = new Vec3(t.getDouble("fx").orElse(0.0), t.getDouble("fy").orElse(1.0),
                    t.getDouble("fz").orElse(0.0));
            Set<BlockPos> positions = new HashSet<>();
            for (long l : t.getLongArray("positions").orElse(new long[0])) {
                positions.add(BlockPos.of(l));
            }
            Set<BlockPos> shaft = new HashSet<>();
            for (long l : t.getLongArray("shaft").orElse(new long[0])) {
                shaft.add(BlockPos.of(l));
            }
            Entry e = new Entry(worldId, body, facing, t.getInt("distance").orElse(1),
                    t.getDouble("speed").orElse(1.0), t.getDouble("su").orElse(2.0),
                    PistonBearingBehavior.Mode.fromString(t.getString("mode").orElse("euler")),
                    t.getLong("delay").orElse(100L), positions, shaft, false);
            e.timer = t.getLong("timer").orElse(0L);
            STORE.computeIfAbsent(worldId, k -> new ConcurrentHashMap<>()).put(body.asLong(), e);
        }
    }
}
