package dev.arubik.craftengine.contraption.type;

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

import dev.arubik.craftengine.contraption.listener.BearingHammerListener;
import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.api.ContraptionType;
import dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Linear (piston-style) contraption type.
 *
 * <h2>Design pattern: block-anchored bearing</h2>
 * Bearing block stays fixed. Structure extends/retracts along facing direction. Powered by
 * adjacent motor, controlled by redstone.
 *
 * <h2>Reference for external plugins</h2>
 * Study this for:
 * - Extendable structures (bridges, drawbridges, sliding doors)
 * - Elevators (vertical piston)
 * - Block-anchored movement (bearing never moves)
 *
 * <h2>Key elements</h2>
 * 1. {@link dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior}: extends/retracts, reads motor RPM
 * 2. Bearing block remains in world (ContraptionCapture excludes it)
 * 3. Modes: extend-only, retract-only, round-robin
 * 4. Upgrades: speed multiplier, fuel multiplier (SU efficiency)
 *
 * <h2>Behavior attachment</h2>
 * See {@link dev.arubik.craftengine.contraption.ContraptionAssembler#attachDefaultBehavior} LINEAR case.
 * Reads config from {@link dev.arubik.craftengine.contraption.behavior.PistonBearingBlockEntity}.
 *
 * <h2>Extended-solid state tracking</h2>
 * Tracks piston bearings in the EULER / ROBIN_EULER "extended-solid" state (2026-07-03 goal — "al
 * llegar al final se convierte en bloques reales; si le metes redstone vuelve a ser contraption").
 * When such a piston reaches the end, {@code ContraptionEngine} disassembles its load into REAL
 * world blocks and records the drop here: the body's real position, the piston's facing/distance/
 * speed/su/mode, and the EXACT world positions the load landed on. Every tick {@link #tickExtendedSolids} then
 * checks each recorded bearing — a redstone pulse at the body (EULER) or the dwell timer elapsing
 * (ROBIN_EULER) re-captures those blocks into a contraption that immediately retracts them home
 * (see {@code ContraptionAssembler#assembleExplicitRetracting}). Persisted to disk so the redstone
 * trigger still works after a restart.
 */
public class LinearContraptionType implements ContraptionType {

    public static final LinearContraptionType INSTANCE = new LinearContraptionType();

    private LinearContraptionType() {
    }

    // ---- Extended-solid state tracking ----

    /**
     * Tracks a single extended-solid bearing state (was Entry in EulerExtendedRegistry).
     */
    private static final class ExtendedSolidState {
        final ResourceKey<Level> worldId;
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

        ExtendedSolidState(ResourceKey<Level> worldId, BlockPos bodyPos, Vec3 facing, int distance, double speed, double su,
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

    private static final Map<ResourceKey<Level>, Map<Long, ExtendedSolidState>> EXTENDED_SOLIDS = new ConcurrentHashMap<>();

    /** Records a euler/robin_euler bearing that just dropped its load as real blocks. */
    public static void recordExtendedSolid(ResourceKey<Level> worldId, BlockPos bodyPos, Vec3 facing, int distance, double speed, double su,
            PistonBearingBehavior.Mode mode, long delay, Set<BlockPos> positions, Set<BlockPos> shaftPositions,
            boolean initialRedstone) {
        EXTENDED_SOLIDS.computeIfAbsent(worldId, k -> new ConcurrentHashMap<>())
                .put(bodyPos.asLong(), new ExtendedSolidState(worldId, bodyPos, facing, distance, speed, su, mode, delay,
                        new HashSet<>(positions), new HashSet<>(shaftPositions), initialRedstone));
    }

    /** Drops a recorded bearing (e.g. hammer-disassembled while extended-solid, or re-assembled). */
    public static void forgetExtendedSolid(ResourceKey<Level> worldId, BlockPos bodyPos) {
        Map<Long, ExtendedSolidState> w = EXTENDED_SOLIDS.get(worldId);
        if (w != null) {
            w.remove(bodyPos.asLong());
        }
    }

    public static boolean isExtendedSolid(ResourceKey<Level> worldId, BlockPos bodyPos) {
        Map<Long, ExtendedSolidState> w = EXTENDED_SOLIDS.get(worldId);
        return w != null && w.containsKey(bodyPos.asLong());
    }

    /** Per-tick check: redstone pulse (euler) or dwell timer (robin_euler) → re-assemble + retract. */
    public static void tickExtendedSolids() {
        for (Map.Entry<ResourceKey<Level>, Map<Long, ExtendedSolidState>> we : EXTENDED_SOLIDS.entrySet()) {
            MinecraftServer server = ((org.bukkit.craftbukkit.CraftServer) Bukkit.getServer()).getServer();
            ServerLevel level = server.getLevel(we.getKey());
            if (level == null) {
                continue; // world not loaded — leave the record for when it loads
            }
            World world = level.getWorld();
            List<ExtendedSolidState> triggered = null;
            for (ExtendedSolidState e : we.getValue().values()) {
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
                for (ExtendedSolidState e : triggered) {
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

    // ---- Persistence (mirrors GlueRegistry) ----

    public static void saveExtendedSolids(Path file) throws IOException {
        CompoundTag root = new CompoundTag();
        ListTag entries = new ListTag();
        for (Map<Long, ExtendedSolidState> w : EXTENDED_SOLIDS.values()) {
            for (ExtendedSolidState e : w.values()) {
                CompoundTag t = new CompoundTag();
                t.putString("world", e.worldId.identifier().getNamespace() + ":" + e.worldId.identifier().getPath());
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

    public static void loadExtendedSolids(Path file) throws IOException {
        if (!Files.exists(file)) {
            return;
        }
        CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
        ListTag entries = root.getListOrEmpty("entries");
        for (int i = 0; i < entries.size(); i++) {
            CompoundTag t = entries.getCompoundOrEmpty(i);
            String worldStr = t.getString("world").orElse("");
            ResourceKey<Level> worldKey;

            // Migration: try parsing as Identifier first, fall back to UUID (old format)
            if (worldStr.contains(":")) {
                // New format: "minecraft:overworld"
                try {
                    Identifier loc = Identifier.parse(worldStr);
                    worldKey = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, loc);
                } catch (Exception e) {
                    continue;
                }
            } else {
                // Old format: UUID string - convert via Bukkit World lookup
                try {
                    UUID worldUuid = UUID.fromString(worldStr);
                    org.bukkit.World bukkitWorld = org.bukkit.Bukkit.getWorld(worldUuid);
                    if (bukkitWorld == null) {
                        continue; // world not found, skip this entry
                    }
                    worldKey = ((CraftWorld) bukkitWorld).getHandle().dimension();
                } catch (IllegalArgumentException bad) {
                    continue;
                }
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
            ExtendedSolidState e = new ExtendedSolidState(worldKey, body, facing, t.getInt("distance").orElse(1),
                    t.getDouble("speed").orElse(1.0), t.getDouble("su").orElse(2.0),
                    PistonBearingBehavior.Mode.fromString(t.getString("mode").orElse("euler")),
                    t.getLong("delay").orElse(100L), positions, shaft, false);
            e.timer = t.getLong("timer").orElse(0L);
            EXTENDED_SOLIDS.computeIfAbsent(worldKey, k -> new ConcurrentHashMap<>()).put(body.asLong(), e);
        }
    }

    // ---- ContraptionType interface ----

    @Override
    public ContraptionEntity createEntity(Level level, ContraptionState state) {
        return new ContraptionEntity(state);
    }

    @Override
    public void attachBehaviors(ContraptionState state, Level level, BlockPos anchor) {
        // PistonBearingBehavior attached by ContraptionAssembler#attachDefaultBehavior.
        // Config read from bearing block entity (distance, speed, mode, upgrades).
    }

    @Override
    public boolean isRotational() {
        return false;
    }

    @Override
    public boolean isVehicle() {
        return false;
    }
}
