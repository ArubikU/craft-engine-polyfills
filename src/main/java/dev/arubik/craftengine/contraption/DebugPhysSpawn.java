package dev.arubik.craftengine.contraption;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

import dev.arubik.craftengine.contraption.level.ContraptionLevel;
import dev.arubik.craftengine.contraption.level.BukkitContraptionLevel;
import dev.arubik.craftengine.contraption.physics.PhysicsWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

/**
 * Builds a PhysContraption from cells supplied by a caller rather than from a glued structure
 * standing in the world — the driver behind {@code /cep contraption spawn} (debug).
 *
 * <h2>Why not {@link ContraptionAssembler#assemblePhysics}</h2>
 * That entry point starts from {@link GlueRegistry#structureAt}: it requires the blocks to already
 * exist, glued, at a real anchor, and it CAPTURES them (removing them from the world). This tool's
 * whole point is to test phys/LOD/collision without hand-building anything, so there is no real
 * structure to scan and nothing to remove. What is shared is everything downstream of capture —
 * {@link ContraptionAssembler#attachDefaultBehavior} is called with {@link BearingType#PHYS} exactly
 * as the capture path does, so the resulting contraption is indistinguishable to
 * {@link PhysicsWorld} from one a hammer assembled: same {@code PhysicsBehavior}, same recorded
 * bearing type.
 *
 * <p>Session-only, like a {@code ContraptionSplitter} fragment and for the same reason: with no
 * anchor block there is no key for {@code BlockAnchoredContraptionStore} to persist against. It
 * survives until it is disassembled or the server stops.
 */
public final class DebugPhysSpawn {

    private DebugPhysSpawn() {
    }

    /** How far in front of the player's eyes, horizontally, a spawned body is anchored. */
    private static final double SPAWN_DISTANCE = 3.0;
    /** How far above eye level a spawned body is anchored, so it has room to fall. */
    private static final double SPAWN_LIFT = 1.0;

    /**
     * Where to anchor a body spawned by a player at {@code eye} looking along {@code look}.
     *
     * <p>Only the HORIZONTAL component of {@code look} is used: aiming at the floor must still put
     * the body in front of the player with air under it, not inside the ground where it can never
     * fall. Pure — takes and returns plain vectors so the placement rule is testable without a
     * server. {@code look} pointing straight up or down falls back to due south (+Z).
     */
    public static Vec3 spawnAnchor(Vec3 eye, Vec3 look) {
        Vec3 flat = new Vec3(look.x, 0, look.z);
        flat = flat.lengthSqr() < 1.0e-6 ? new Vec3(0, 0, 1) : flat.normalize();
        return eye.add(flat.scale(SPAWN_DISTANCE)).add(0, SPAWN_LIFT, 0);
    }

    /**
     * The local-space shift that centers a structure of {@code size} horizontally on the anchor.
     *
     * <p>A structure template's block positions all start at its own corner, so replaying them raw
     * would hang the whole body off one side of the pivot. Y is deliberately NOT centered — the
     * anchor is meant to be the body's footing, and a structure should drop from its base, not from
     * its middle. Pure.
     */
    public static BlockPos centerOffset(Vec3i size) {
        return new BlockPos(-(size.getX() - 1) / 2, 0, -(size.getZ() - 1) / 2);
    }

    /**
     * Parses a blockstate in vanilla command syntax ({@code minecraft:oak_stairs[facing=north]}).
     *
     * @throws IllegalArgumentException on an unknown block or a malformed property list
     */
    public static BlockState parseBlockState(String input) {
        return ((CraftBlockData) org.bukkit.Bukkit.createBlockData(input)).getState();
    }

    /**
     * The cells of the vanilla structure template {@code id} ({@code minecraft:igloo/top}), in local
     * coordinates centered on the anchor.
     *
     * <p>Air and {@code structure_void} are dropped: a template records them as real palette entries,
     * and carrying them in would give the body mass and collision where it is meant to be empty.
     *
     * @return empty if {@code id} is malformed or resolves to no template
     */
    public static Optional<Map<BlockPos, StructureTemplate.StructureBlockInfo>> structureCells(ServerLevel level,
            String id) {
        Identifier key = Identifier.tryParse(id);
        if (key == null) {
            return Optional.empty();
        }
        Optional<StructureTemplate> template = level.getStructureManager().get(key);
        if (template.isEmpty() || template.get().palettes.isEmpty()) {
            return Optional.empty();
        }
        BlockPos offset = centerOffset(template.get().getSize());
        Map<BlockPos, StructureTemplate.StructureBlockInfo> cells = new HashMap<>();
        for (StructureTemplate.StructureBlockInfo info : template.get().palettes.get(0).blocks()) {
            if (info.state().isAir() || info.state().is(Blocks.STRUCTURE_VOID)) {
                continue;
            }
            cells.put(info.pos().offset(offset), info);
        }
        return Optional.of(cells);
    }

    /**
     * Builds, registers and primes a PhysContraption holding {@code cells} at {@code anchor}.
     *
     * <p>{@link PhysicsWorld#ensureBody} is called before returning so the body's mass, shape and
     * transform exist on the tick it spawns rather than a tick later — the same reason
     * {@code ContraptionSplitter} primes a fragment it just built.
     *
     * @param cells local position to blockstate; air entries are skipped
     * @param blockEntities local position to block-entity NBT, for the subset of cells that have one
     * @return the registered contraption, or {@code null} if {@code cells} holds nothing solid
     */
    public static ContraptionEntity spawn(World bukkitWorld, Vec3 anchor, Map<BlockPos, BlockState> cells,
            Map<BlockPos, CompoundTag> blockEntities) {
        return spawn(bukkitWorld, anchor, cells, blockEntities, BearingType.PHYS);
    }

    /** As {@link #spawn(World, Vec3, Map, Map)} but assembles as {@code type} (PHYS or VEHICLE) — the vehicle
     *  variant attaches a {@code VehicleControlBehavior} so a driver can steer it. */
    public static ContraptionEntity spawn(World bukkitWorld, Vec3 anchor, Map<BlockPos, BlockState> cells,
            Map<BlockPos, CompoundTag> blockEntities, BearingType type) {
        boolean __prof = dev.arubik.craftengine.contraption.ContraptionPerf.enabled();
        long __t0 = __prof ? System.nanoTime() : 0;
        Level realLevel = ((CraftWorld) bukkitWorld).getHandle();
        ContraptionLevel level = ContraptionLevel.create(realLevel, anchor.x, anchor.y, anchor.z, 0.0);
        long __t1 = __prof ? System.nanoTime() : 0;
        level.putBlocks(cells, true);
        long __t2 = __prof ? System.nanoTime() : 0;
        if (__prof) {
            org.bukkit.Bukkit.getLogger().info(String.format("[SpawnProfile] create=%.2fms putBlocks=%.2fms",
                    (__t1 - __t0) / 1e6, (__t2 - __t1) / 1e6));
        }
        if (blockEntities != null) {
            for (Map.Entry<BlockPos, CompoundTag> be : blockEntities.entrySet()) {
                if (!cells.getOrDefault(be.getKey(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                        .isAir()) {
                    level.putBlockEntity(be.getKey(), be.getValue());
                }
            }
        }
        if (level.blockCount() == 0) {
            level.dispose();
            return null;
        }
        ContraptionState state = new ContraptionState(UUID.randomUUID(), bukkitWorld.getUID(), level, anchor.x,
                anchor.y, anchor.z);
        // Through the normal path, not a hand-rolled PhysicsBehavior: this is what records
        // BearingType.PHYS on the state, which is what makes the body persist/rehydrate and read as a
        // phys contraption everywhere else. rpm/su are inert for PHYS (no motor) — see the PHYS case.
        ContraptionAssembler.attachDefaultBehavior(realLevel, BlockPos.containing(anchor), state, type,
                ContraptionAssembler.DEFAULT_ROTATIONAL_RPM,
                dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.DEFAULT_SU_PER_BLOCK);
        ContraptionEntity entity = ContraptionManager.register(new ContraptionEntity(state));
        PhysicsWorld.ensureBody(state);
        // Register the persistence anchor NOW, not on the first solver step: every save path keys off the
        // assembled-anchor map, so a body that is spawned and then never steps (spawned during a stop, or
        // asleep from birth) would otherwise never be written to disk at all. Recording BearingType.PHYS on
        // the state is necessary but NOT sufficient for persistence — this is the part that was missing, and
        // why phys contraptions vanished on restart. PhysicsWorld#writeBack keeps this key on the live chunk
        // from here on.
        dev.arubik.craftengine.contraption.BearingHammerListener.markAssembled(bukkitWorld.getUID(),
                BlockPos.containing(anchor), state.id());
        return entity;
    }
}
