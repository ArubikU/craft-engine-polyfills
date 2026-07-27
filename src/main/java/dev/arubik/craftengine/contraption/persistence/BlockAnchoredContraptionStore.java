package dev.arubik.craftengine.contraption.persistence;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.BearingType;
import dev.arubik.craftengine.contraption.ContraptionAssembler;
import dev.arubik.craftengine.contraption.ContraptionCapture;
import dev.arubik.craftengine.contraption.ContraptionEntity;
import dev.arubik.craftengine.contraption.ContraptionFurnitureCapture;
import dev.arubik.craftengine.contraption.ContraptionManager;
import dev.arubik.craftengine.contraption.ContraptionState;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.level.ContraptionLevel;
import dev.arubik.craftengine.contraption.level.BukkitContraptionLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.Level;

/**
 * Disk-backed restart/chunk-unload persistence for the two BLOCK-ANCHORED contraption bearing
 * types (LINEAR/ROTATIONAL) — the block-anchored analog of what {@code MinecartBearing}'s
 * entity-PDC does for the MINECART type. A block-anchored bearing has no anchor ENTITY (thus
 * no PersistentDataContainer) and {@code BearingBlockBehavior} has no PersistentBlockEntity to
 * ride along in a chunk save, so the contraption is persisted to DISK files under the plugin
 * data folder instead, reusing {@link ContraptionStructureNbt} for the heavy structure dump.
 *
 * <p><b>Format</b>: one combined gzip'd {@link CompoundTag} per contraption at
 * {@code <dataFolder>/contraptions/<uuid>.dat} — a single atomic read/write per contraption.
 * The root tag carries the manifest fields (id, world, live x/y/z/yaw, stalled, the FIXED
 * bearing anchor block pos, the {@link BearingType}, rpm and suPerBlock) AND the full
 * {@link ContraptionStructureNbt#dump} (blocks + glue + furniture) under a {@code "structure"}
 * sub-tag. Block-anchored needs MORE than the minecart's structure-only dump because there is
 * no live block/entity to re-derive type/rpm from at boot except the bearing block itself,
 * which may not even be loaded when we rehydrate.
 *
 * <p><b>Boot flow</b> (mirrors how the minecart rehydrates via natural entity chunk-load):
 * {@link #loadIndex} scans every {@code .dat} at {@code onEnable} into an in-memory index
 * keyed by (worldId, bearing chunk); the target world/chunk may not be loaded yet, so nothing
 * is rehydrated immediately. {@link #rehydrateChunk} (called from the chunk-load listener)
 * then rehydrates each record whose bearing anchor is in the loading chunk.
 */
public final class BlockAnchoredContraptionStore {

    private BlockAnchoredContraptionStore() {
    }

    /** A persisted block-anchored contraption's manifest fields (structure loaded lazily on rehydrate). */
    /**
     * @param pitchRadians tilt, and {@code rollRadians} lean. Meaningless for LINEAR/ROTATIONAL — a
     *        bearing holds its structure upright, which is why only yaw was ever stored — but a PHYS body
     *        is free to topple, and a toppled one that came back upright would be a visible lie about
     *        where it was left. Absent from files written before this existed; those read as 0, i.e.
     *        exactly the upright pose they were restored with anyway.
     */
    public record Record(UUID id, UUID worldId, BlockPos bearingPos, double x, double y, double z,
            double yawRadians, double pitchRadians, double rollRadians, boolean stalled, BearingType type,
            double rpm, double suPerBlock, double explosionProof) {
    }

    /** In-memory boot index: contraption id -> its manifest record. Populated by {@link #loadIndex}. */
    private static final Map<UUID, Record> INDEX = new HashMap<>();

    /**
     * Single background thread that does the gzip compression + disk write for {@link #save} — the
     * profiled hot path was doing both synchronously on the server thread on every chunk unload
     * (spark: {@code onChunkUnload} -> {@code save} -> {@code NbtIo.writeCompressed} -> gzip
     * finish/flush ~= 21% of main-thread time). A SINGLE thread means all writes are globally
     * ordered, so two writes to the same {@code <uuid>.dat} can never race or interleave. Daemon,
     * mirroring {@code BukkitContraptionLevel}'s {@code cep-level-cleanup} executor. Volatile +
     * recreated by {@link #flush} so a plugin re-enable (reload) has a live writer again.
     */
    private static volatile ExecutorService writer = newWriter();

    private static ExecutorService newWriter() {
        return Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "cep-contraption-writer");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Snapshots handed to {@link #writer} whose async gzip+write hasn't landed on disk yet, keyed
     * by contraption id. {@link #rehydrate} consults this BEFORE reading the {@code .dat} so a
     * chunk that unloads and immediately reloads (write still in flight) still restores from the
     * fresh in-memory tag rather than a stale/absent file — the reload-before-write-completes race.
     */
    private static final Map<UUID, CompoundTag> PENDING = new ConcurrentHashMap<>();

    /**
     * Last root tag we queued to write per id — a content-equality dirty check. Chunk unloads
     * re-dump byte-identical structure constantly (a parked, unchanged contraption's chunk loads
     * and unloads as players wander past); when the freshly built tag {@code equals} this one the
     * data is already persisted/queued and the whole gzip+write is skipped. Cleared on write
     * FAILURE so a later identical save retries instead of being wrongly deduped away.
     */
    private static final Map<UUID, CompoundTag> LAST_WRITTEN = new ConcurrentHashMap<>();

    private static Path dir() {
        return CraftEnginePolyfills.instance().getDataFolder().toPath().resolve("contraptions");
    }

    private static Path fileFor(UUID id) {
        return dir().resolve(id.toString() + ".dat");
    }

    /**
     * Dumps the CURRENT live structure + transform for one block-anchored contraption to disk.
     * Callable both at assemble time and at chunk-unload (re-dumps the live structure, exactly
     * like {@code MinecartBearing#saveStructure}). Best-effort: a failure is logged, never thrown.
     */
    /**
     * The {@link BearingType} to persist for {@code state}: its OWN recorded type, falling back to the
     * anchor block only when it has none, and to {@link BearingType#ROTATIONAL} when neither answers.
     *
     * <h2>Why the state wins over the block</h2>
     * Re-reading the anchor block is sound for LINEAR/ROTATIONAL, whose bearing stays put in the world
     * and pins the structure to it. It is wrong for {@link BearingType#PHYS}, and silently so: a phys
     * contraption has no pinning bearing, its anchor block is captured INTO the structure, and the body
     * then FALLS AWAY from the anchor coordinates. By save time the world there is ordinary air, the
     * lookup returns null, and the ROTATIONAL fallback was taken — so every phys contraption was written
     * to disk as a spinning bearing and came back as one after a restart.
     *
     * @param level the real world to consult for the fallback; may be {@code null}
     */
    public static BearingType typeToPersist(ContraptionState state, net.minecraft.world.level.Level level,
            BlockPos anchor) {
        BearingType recorded = state == null ? null : state.bearingType();
        if (recorded != null) {
            return recorded;
        }
        BearingType fromBlock = level == null ? null
                : dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.typeAt(level, anchor);
        return fromBlock != null ? fromBlock : BearingType.ROTATIONAL;
    }

    public static void save(ContraptionState state, BlockPos bearingPos, BearingType type, double rpm,
            double suPerBlock) {
        if (state == null || state.level() == null) {
            return;
        }
        UUID id = state.id();
        // Build the tag on the MAIN thread: the structure dump reads live block/block-entity state
        // out of the hidden level and must see a consistent, non-mutating snapshot. Once built the
        // CompoundTag is immutable and is safe to hand to another thread. Compression + disk I/O —
        // the actual profiled cost — are NOT done here; they go to the background writer below.
        CompoundTag root;
        try {
            root = new CompoundTag();
            root.putString("id", id.toString());
            root.putString("world", state.worldId().toString());
            root.putInt("bx", bearingPos.getX());
            root.putInt("by", bearingPos.getY());
            root.putInt("bz", bearingPos.getZ());
            root.putDouble("x", state.x());
            root.putDouble("y", state.y());
            root.putDouble("z", state.z());
            root.putDouble("yaw", state.yawRadians());
            root.putDouble("pitch", state.pitchRadians());
            root.putDouble("roll", state.rollRadians());
            root.putBoolean("stalled", state.isStalled());
            root.putString("type", type.name());
            root.putDouble("rpm", rpm);
            root.putDouble("su", suPerBlock);
            root.putDouble("explosionProof", state.explosionProof());
            root.put("structure", ContraptionStructureNbt.dump(state.level()));
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to serialize block-anchored contraption " + id + ": " + t);
            return;
        }
        // Keep the boot index in sync so an unload-then-reload within the same session
        // (never restarted) still finds an up-to-date record. INDEX is main-thread-only.
        INDEX.put(id, new Record(id, state.worldId(), bearingPos, state.x(), state.y(),
                state.z(), state.yawRadians(), state.pitchRadians(), state.rollRadians(), state.isStalled(),
                type, rpm, suPerBlock, state.explosionProof()));
        // Dirty check: identical content to what we last queued for this id means it is already
        // (being) persisted — skip the gzip+write entirely. This is where most chunk-unload saves
        // are eliminated, since a parked contraption re-dumps byte-for-byte the same tag each time.
        if (root.equals(LAST_WRITTEN.get(id))) {
            return;
        }
        LAST_WRITTEN.put(id, root);
        // Hand the immutable snapshot to the background writer (gzip + temp-file + atomic move).
        // PENDING makes it visible to a racing rehydrate until the write lands on disk.
        Path file = fileFor(id);
        PENDING.put(id, root);
        writer.execute(() -> writeNow(id, file, root));
    }

    /**
     * Background-thread half of {@link #save}: gzip-compress {@code root} into a sibling
     * {@code .tmp} file, then atomically move it over the real {@code .dat} so a crash mid-write
     * can never leave a half-written (unreadable) file — a reader sees either the old complete
     * file or the new complete file, never a torn one. Runs only on the single {@link #writer}
     * thread, so writes to the same path are serialized by construction.
     */
    private static void writeNow(UUID id, Path file, CompoundTag root) {
        try {
            Files.createDirectories(file.getParent());
            Path tmp = file.resolveSibling(file.getFileName().toString() + ".tmp");
            NbtIo.writeCompressed(root, tmp);
            try {
                Files.move(tmp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException noAtomic) {
                // Filesystem/platform without atomic replace — still far safer than writing the
                // real file in place (the window for a torn file shrinks to the move itself).
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to write block-anchored contraption " + id + ": " + t);
            // The dedup hash assumed this content reached disk; it didn't. Drop it so the next
            // identical save is NOT skipped and gets a real retry.
            LAST_WRITTEN.remove(id, root);
        } finally {
            // Only clear if we're still the latest snapshot — a newer save may have replaced us
            // while this compression was running.
            PENDING.remove(id, root);
        }
    }

    /**
     * Drains and stops the background {@link #writer}, blocking up to a bounded timeout so every
     * queued gzip+disk write completes before the plugin/JVM goes away — the clean-shutdown
     * guarantee against data loss. Called from {@code onDisable} after the final shutdown save
     * loop has enqueued its writes. Recreates the writer afterward so a same-classloader re-enable
     * (reload) still has a live executor.
     */
    public static void flush() {
        ExecutorService w = writer;
        w.shutdown();
        try {
            if (!w.awaitTermination(30, TimeUnit.SECONDS)) {
                CraftEnginePolyfills.instance().getLogger().warning(
                        "[Contraption] block-anchored writer did not drain within 30s; forcing stop "
                                + "(" + PENDING.size() + " write(s) may be incomplete).");
                w.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            w.shutdownNow();
        }
        writer = newWriter();
    }

    /** Removes the on-disk file (and the index entry) for a contraption — called on disassemble. */
    public static void delete(UUID id) {
        INDEX.remove(id);
        LAST_WRITTEN.remove(id);
        PENDING.remove(id);
        Path file = fileFor(id);
        // Route the delete through the writer too, so it is ORDERED after any still-queued write
        // for this id (write-then-delete ends deleted, never the reverse leaving a resurrected
        // file). deleteIfExists also mops up a stray sibling .tmp from an interrupted write.
        writer.execute(() -> {
            try {
                Files.deleteIfExists(file);
                Files.deleteIfExists(file.resolveSibling(file.getFileName().toString() + ".tmp"));
            } catch (Throwable t) {
                CraftEnginePolyfills.instance().getLogger()
                        .warning("[Contraption] failed to delete block-anchored contraption file " + id + ": " + t);
            }
        });
    }

    /**
     * Scans every {@code .dat} at boot into {@link #INDEX} WITHOUT rehydrating (the target
     * world/chunk may not be loaded yet — the chunk-load hook rehydrates each as its chunk
     * loads). Best-effort per file: a single bad file is skipped, never aborts the whole scan.
     */
    public static void loadIndex() {
        INDEX.clear();
        Path dir = dir();
        if (!Files.isDirectory(dir)) {
            return;
        }
        try (var stream = Files.newDirectoryStream(dir, "*.dat")) {
            for (Path file : stream) {
                try {
                    CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
                    Record rec = readManifest(root);
                    if (rec != null) {
                        INDEX.put(rec.id(), rec);
                    }
                } catch (Throwable t) {
                    CraftEnginePolyfills.instance().getLogger()
                            .warning("[Contraption] skipping unreadable block-anchored file " + file + ": " + t);
                }
            }
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to scan block-anchored contraption dir: " + t);
        }
        if (!INDEX.isEmpty()) {
            CraftEnginePolyfills.instance().getLogger()
                    .info("[Contraption] indexed " + INDEX.size() + " persisted block-anchored contraption(s).");
        }
    }

    private static Record readManifest(CompoundTag root) {
        UUID id = UUID.fromString(root.getString("id").orElseThrow());
        UUID world = UUID.fromString(root.getString("world").orElseThrow());
        BlockPos bearingPos = new BlockPos(root.getInt("bx").orElse(0), root.getInt("by").orElse(0),
                root.getInt("bz").orElse(0));
        BearingType type;
        try {
            type = BearingType.valueOf(root.getString("type").orElse("ROTATIONAL"));
        } catch (IllegalArgumentException e) {
            type = BearingType.ROTATIONAL;
        }
        return new Record(id, world, bearingPos, root.getDouble("x").orElse(0.0), root.getDouble("y").orElse(0.0),
                root.getDouble("z").orElse(0.0), root.getDouble("yaw").orElse(0.0),
                // Absent from pre-existing files — 0 is both the safe default and the exact pose those
                // contraptions were being rehydrated with before pitch/roll were stored at all.
                root.getDouble("pitch").orElse(0.0), root.getDouble("roll").orElse(0.0),
                root.getBoolean("stalled").orElse(false), type, root.getDouble("rpm").orElse(0.0),
                root.getDouble("su").orElse(0.0), root.getDouble("explosionProof").orElse(0.0));
    }

    /**
     * Rehydrates every persisted block-anchored contraption whose bearing anchor block sits in
     * the given (already-loaded) chunk and isn't already live. Called from the chunk-load
     * listener — the block-anchored analog of {@code MinecartBearing#rehydrate} being invoked
     * per bearing entity found in a loading chunk.
     */
    public static void rehydrateChunk(World world, int chunkX, int chunkZ) {
        UUID worldId = world.getUID();
        List<Record> hits = new ArrayList<>();
        for (Record rec : INDEX.values()) {
            if (!rec.worldId().equals(worldId)) {
                continue;
            }
            if ((rec.bearingPos().getX() >> 4) != chunkX || (rec.bearingPos().getZ() >> 4) != chunkZ) {
                continue;
            }
            hits.add(rec);
        }
        for (Record rec : hits) {
            rehydrate(world, rec);
        }
    }

    /**
     * Rebuilds a live {@link ContraptionEntity} for one persisted record and re-registers its
     * assembled-anchor bookkeeping. No-ops if the contraption is already live (same guard as
     * {@code MinecartBearing#rehydrate}). A LINEAR actuator that had DRIFTED away from its
     * bearing anchor restores at its saved live x/y/z/yaw — it is NOT snapped back to the
     * anchor (the saved transform is the live one, mirroring the minecart restoring at the
     * cart's current location).
     */
    public static void rehydrate(World world, Record rec) {
        if (ContraptionManager.get(rec.id()) != null) {
            return; // already live (e.g. just assembled this session, or double chunk-load)
        }
        Level realLevel = ((CraftWorld) world).getHandle();
        CompoundTag structure;
        // Reload-before-write-completes race: if this contraption's last save is still queued in
        // the background writer, its .dat may be absent or stale on disk. Prefer the in-memory
        // snapshot so a rapid unload->reload restores the freshest structure, not an old file.
        CompoundTag pending = PENDING.get(rec.id());
        if (pending != null) {
            structure = pending.getCompoundOrEmpty("structure");
        } else {
            try {
                CompoundTag root = NbtIo.readCompressed(fileFor(rec.id()), NbtAccounter.unlimitedHeap());
                structure = root.getCompoundOrEmpty("structure");
            } catch (Throwable t) {
                CraftEnginePolyfills.instance().getLogger()
                        .warning("[Contraption] failed to read block-anchored structure for " + rec.id() + ": " + t);
                return;
            }
        }
        try {
            // Capture always creates the level at yaw 0 (see ContraptionCapture#capture); the live
            // transform is then applied via the state ctor + setYawRadians below.
            ContraptionLevel level = ContraptionLevel.create(realLevel, rec.x(), rec.y(), rec.z(), 0);
            ContraptionStructureNbt.load(level, structure);
            ContraptionState state = new ContraptionState(rec.id(), rec.worldId(), level, rec.x(), rec.y(), rec.z());
            state.setYawRadians(rec.yawRadians());
            // A PHYS body persists the pose it was actually left in, tilt included (see Record). Zero for
            // a bearing, which is what it always effectively was.
            state.setPitchRadians(rec.pitchRadians());
            state.setRollRadians(rec.rollRadians());
            // Restore the persisted uniform SCALE (roadmap item #9) — carried in the embedded structure blob
            // (ContraptionStructureNbt) and put onto the level by its #load above; reaffirm onto the state so
            // a scaled block-anchored contraption rehydrates at its saved size. 1.0 for a pre-scale blob.
            state.setScale(level.realScaleFactor());
            state.setStalled(rec.stalled());
            // Re-place captured CraftEngine furniture into the freshly-loaded hidden level (rode
            // along in the structure NBT) — same call MinecartBearing#rehydrate uses.
            state.setFurniture(ContraptionFurnitureCapture.restoreIntoFakeLevel(level, level.furnitureRecords()));
            for (MovementBehavior autoBehavior : ContraptionCapture.resolveAutoBehaviors(level)) {
                state.addBehavior(autoBehavior);
            }
            // Re-attach the MOVEMENT behavior for the saved bearing type using the saved rpm/su and
            // a fresh scan for a real adjacent motor at the bearing block (same helper assembly uses).
            ContraptionAssembler.attachDefaultBehavior(realLevel, rec.bearingPos(), state, rec.type(), rec.rpm(),
                    rec.suPerBlock());
            // attachDefaultBehavior re-reads the flag from the bearing block, which is gone for a PHYS/VEHICLE
            // body — so restore the PERSISTED value, authoritative across restart.
            state.setExplosionProof(rec.explosionProof());
            ContraptionManager.register(new ContraptionEntity(state));
            // Re-register the assembled-anchor bookkeeping so a later disassemble/unload finds it.
            dev.arubik.craftengine.contraption.BearingHammerListener.markAssembled(rec.worldId(), rec.bearingPos(),
                    rec.id());
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to rehydrate block-anchored contraption " + rec.id() + ": " + t);
        }
    }
}
