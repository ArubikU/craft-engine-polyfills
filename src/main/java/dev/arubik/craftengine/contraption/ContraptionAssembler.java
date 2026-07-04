package dev.arubik.craftengine.contraption;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

import dev.arubik.craftengine.contraption.behavior.LinearActuatorBehavior;
import dev.arubik.craftengine.contraption.behavior.MultiblockMembershipRegistry;
import dev.arubik.craftengine.contraption.behavior.RealMotorLink;
import dev.arubik.craftengine.contraption.behavior.RotationalBearingBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * The production 4-state assemble/disassemble machine (CONTRAPTIONS.md §1, §3 —
 * {@code ContraptionAssembler} was in the original package layout but never created; this
 * promotes {@link HologramTest}'s inline start/stop logic to a real, shared class). The
 * glue-scan/capture/remove/spawn half (Static -&gt; Dynamic) and grid-snap/collision-restore
 * half (Dynamic -&gt; Static) are identical for every block-anchored bearing type — only the
 * default {@link MovementBehavior} attached at assembly time differs; see
 * {@link MinecartBearing} for the MINECART type's own (entity-anchored) assemble/disassemble,
 * which reuses {@link ContraptionCapture}/{@link ContraptionMath} the same way but can't
 * reuse this class directly since its anchor is a real entity, not a fixed block position.
 */
public final class ContraptionAssembler {

    private ContraptionAssembler() {
    }

    /** Default constant velocity attached to a freshly-assembled LINEAR bearing (blocks/sec, along local +X). */
    public static final double DEFAULT_LINEAR_SPEED = 1.0;
    /** Default rotation rate attached to a freshly-assembled ROTATIONAL bearing. */
    public static final double DEFAULT_ROTATIONAL_RPM = 5.0;

    /**
     * Static -&gt; Dynamic for a block-anchored bearing (LINEAR/ROTATIONAL), using the default
     * ROTATIONAL rpm. Prefer {@link #assemble(World, BlockPos, BearingType, double)} in
     * production (reads the real bearing block's own {@code rpm:} config via
     * {@link dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior#rpmAt}) — this
     * overload exists for callers that don't have/need a per-block rpm (tests, tools).
     */
    public static ContraptionEntity assemble(World bukkitWorld, BlockPos bearing, BearingType type) {
        return assemble(bukkitWorld, bearing, type, DEFAULT_ROTATIONAL_RPM,
                dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.DEFAULT_SU_PER_BLOCK);
    }

    /** @deprecated prefer the 5-arg overload that also takes {@code suPerBlock} — kept for older call sites/tests. */
    @Deprecated
    public static ContraptionEntity assemble(World bukkitWorld, BlockPos bearing, BearingType type,
            double rotationalRpm) {
        return assemble(bukkitWorld, bearing, type, rotationalRpm,
                dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.DEFAULT_SU_PER_BLOCK);
    }

    /**
     * Static -&gt; Dynamic for a block-anchored bearing (LINEAR/ROTATIONAL): glue-scan at
     * {@code bearing}, capture + remove the real blocks, spawn a {@code ContraptionLevel}/
     * {@link ContraptionEntity}, attach the bearing-type-appropriate default behavior, and
     * register with {@link ContraptionManager}. {@code rotationalRpm} is only used for
     * {@link BearingType#ROTATIONAL} (see {@link #attachDefaultBehavior}); {@code suPerBlock}
     * is used for LINEAR and ROTATIONAL both (Task 4's "small detail" follow-up — SU demand
     * scales with structure size, see {@code BearingBlockBehavior#suPerBlock()}).
     */
    public static ContraptionEntity assemble(World bukkitWorld, BlockPos bearing, BearingType type,
            double rotationalRpm, double suPerBlock) {
        Set<BlockPos> structure = GlueRegistry.structureAt(bukkitWorld.getUID(), bearing);
        if (structure.isEmpty()) {
            return null;
        }
        Level level = ((CraftWorld) bukkitWorld).getHandle();
        structure = expandMultiblockMembers(level, structure);
        // PISTON body stays REAL (2026-07-03 — "el bearing body debe... detectar la redstone del
        // mundo externo" + "el menú debería poderse hasta cuando está assembled"). Unlike a
        // ROTATIONAL bearing (which rotates the whole structure including its own block around the
        // pivot), a LINEAR piston's BODY block must remain in the world at its anchor while extended:
        // it's what reads external redstone/motor at its real position, backs the right-click config
        // block entity so the menu opens even mid-extension, and is the fixed base the shaft renders
        // from. So exclude the bearing cell from the captured/removed set — only the structure IN
        // FRONT (glued to it) is pushed.
        if (type == BearingType.LINEAR) {
            structure = new HashSet<>(structure);
            structure.remove(bearing);
        }
        ContraptionCapture.Result captured = ContraptionCapture.capture(level, structure, bearing);
        // Persist the structure's glue topology onto the captured level (2026-07-03) — read from
        // the world registry NOW (still populated), stored local so it survives restart in the
        // saved NBT and re-glues everything on disassemble. See ContraptionCapture#captureGlueEdges.
        ContraptionCapture.captureGlueEdges(bukkitWorld.getUID(), captured.level(), bearing);
        // Furniture scan BEFORE the blocks are removed — it only reads real entities near the
        // footprint, so ordering relative to removeFromWorld doesn't matter, but keeping it
        // here (right after capture, right before removal) reads as "capture everything, then
        // clear the world" rather than interleaved.
        ContraptionFurnitureCapture.Result furnitureResult = ContraptionFurnitureCapture.captureNear(level, structure, bearing, captured.level());
        ContraptionCapture.removeFromWorld(level, structure);

        ContraptionState state = new ContraptionState(UUID.randomUUID(), bukkitWorld.getUID(), captured.level(),
                bearing.getX(), bearing.getY(), bearing.getZ());
        state.setFurniture(furnitureResult.furniture());
        // Task 1 (CONTRAPTIONS.md 2026-07-01 session): re-register any real player who was
        // sitting in a captured seat at the exact moment of assembly — see
        // ContraptionFurnitureCapture#captureNear's javadoc for the full design.
        for (Map.Entry<UUID, net.minecraft.world.phys.Vec3> e : furnitureResult.seatedRiders().entrySet()) {
            state.addSeatedRider(e.getKey(), e.getValue());
            UUID mountId = furnitureResult.seatedRiderMounts().get(e.getKey());
            if (mountId != null) {
                state.setSeatedRiderMount(e.getKey(), mountId);
            }
        }
        for (MovementBehavior autoBehavior : captured.autoBehaviors()) {
            state.addBehavior(autoBehavior);
        }
        attachDefaultBehavior(level, bearing, state, type, rotationalRpm, suPerBlock);

        ContraptionEntity entity = ContraptionManager.register(new ContraptionEntity(state));
        // Persist to disk immediately (matches MinecartBearing calling saveStructure in assemble) so
        // a block-anchored contraption survives a chunk unload / restart — see
        // BlockAnchoredContraptionStore. MINECART goes through MinecartBearing, never this method.
        dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore.save(state, bearing, type,
                rotationalRpm, suPerBlock);
        return entity;
    }

    /**
     * No-hammer piston assembly (2026-07-03 — "el bearing debe agarrar el bloque que apunta y todos
     * los que estan pegados a ese con glue y armarlo solo"). Called from the bearing block entity's
     * redstone driver (NOT the hammer). Captures the block the bearing points at (bearing + facing) +
     * its whole glue component (and anything glued to the bearing), anchored at the bearing body
     * (which stays real), and attaches the configured piston. Returns null if there's nothing solid
     * in front to push.
     */
    public static ContraptionEntity assemblePiston(World bukkitWorld, BlockPos bearing) {
        Level level = ((CraftWorld) bukkitWorld).getHandle();
        UUID worldId = bukkitWorld.getUID();
        Vec3 facing = dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.facingVecAt(level, bearing);
        BlockPos front = bearing.offset((int) Math.round(facing.x), (int) Math.round(facing.y),
                (int) Math.round(facing.z));
        Set<BlockPos> structure = new HashSet<>(GlueRegistry.structureAt(worldId, front));
        structure.addAll(GlueRegistry.structureAt(worldId, bearing));
        structure = new HashSet<>(expandMultiblockMembers(level, structure));
        structure.remove(bearing); // the body block stays REAL at the anchor
        structure.removeIf(p -> level.getBlockState(p).isAir()); // nothing to push through air
        if (structure.isEmpty()) {
            return null;
        }
        ContraptionCapture.Result captured = ContraptionCapture.capture(level, structure, bearing);
        ContraptionCapture.captureGlueEdges(worldId, captured.level(), bearing);
        ContraptionFurnitureCapture.Result furnitureResult =
                ContraptionFurnitureCapture.captureNear(level, structure, bearing, captured.level());
        ContraptionCapture.removeFromWorld(level, structure);

        ContraptionState state = new ContraptionState(UUID.randomUUID(), worldId, captured.level(),
                bearing.getX(), bearing.getY(), bearing.getZ());
        state.setFurniture(furnitureResult.furniture());
        for (MovementBehavior autoBehavior : captured.autoBehaviors()) {
            state.addBehavior(autoBehavior);
        }
        double suPerBlock = dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.suPerBlockAt(level, bearing);
        attachDefaultBehavior(level, bearing, state, BearingType.LINEAR, DEFAULT_ROTATIONAL_RPM, suPerBlock);
        ContraptionEntity entity = ContraptionManager.register(new ContraptionEntity(state));
        dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore.save(state, bearing,
                BearingType.LINEAR, DEFAULT_ROTATIONAL_RPM, suPerBlock);
        return entity;
    }

    /**
     * Euler re-assemble (2026-07-03 goal — "si le metes redstone vuelve a ser contraption"): after a
     * EULER/ROBIN_EULER piston dropped its load as real blocks at the extended end, re-captures those
     * EXACT world blocks into a fresh contraption anchored at the bearing body and attaches a piston
     * that immediately RETRACTS them home. Unlike {@link #assemble} this does NOT glue-scan — the
     * caller passes the precise set of dropped positions (recorded when the load was dropped). The
     * captured local offsets are {@code worldPos - bodyPos} (so they already encode the extended
     * position), which is exactly what {@link PistonBearingBehavior#startRetracting} expects.
     */
    public static ContraptionEntity assembleExplicitRetracting(World bukkitWorld, BlockPos bodyPos,
            Set<BlockPos> worldPositions, Vec3 facing, int distance, double speed, double suPerBlock,
            dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior.Mode mode, long delay) {
        if (worldPositions == null || worldPositions.isEmpty()) {
            return null;
        }
        Level level = ((CraftWorld) bukkitWorld).getHandle();
        ContraptionCapture.Result captured = ContraptionCapture.capture(level, worldPositions, bodyPos);
        ContraptionCapture.captureGlueEdges(bukkitWorld.getUID(), captured.level(), bodyPos);
        ContraptionFurnitureCapture.Result furnitureResult =
                ContraptionFurnitureCapture.captureNear(level, worldPositions, bodyPos, captured.level());
        ContraptionCapture.removeFromWorld(level, worldPositions);

        ContraptionState state = new ContraptionState(UUID.randomUUID(), bukkitWorld.getUID(), captured.level(),
                bodyPos.getX(), bodyPos.getY(), bodyPos.getZ());
        state.setFurniture(furnitureResult.furniture());
        for (MovementBehavior autoBehavior : captured.autoBehaviors()) {
            state.addBehavior(autoBehavior);
        }
        BlockPos motorPos = RealMotorLink.findAdjacentMotor(level, bodyPos);
        state.addBehavior(dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior.startRetracting(
                facing, bodyPos, motorPos, distance, speed, suPerBlock, mode, delay));
        ContraptionEntity entity = ContraptionManager.register(new ContraptionEntity(state));
        dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore.save(state, bodyPos,
                BearingType.LINEAR, speed, suPerBlock);
        return entity;
    }

    /**
     * Expands a raw glue-component to a fixed point over {@link MultiblockMembershipRegistry}
     * (CONTRAPTIONS.md — "todos los multiblock ... deben pegarse completos solos"): gluing even
     * ONE cell of a multiblock (e.g. one corner of a {@code fluid_block_tank} prism) must still
     * capture the WHOLE multiblock, not just the glued cell(s). Every newly-pulled-in member is
     * itself re-checked against every provider (BFS), since a member could belong to a second
     * multiblock family in principle. {@code structure} itself is never mutated.
     */
    static Set<BlockPos> expandMultiblockMembers(Level level, Set<BlockPos> structure) {
        Set<BlockPos> expanded = new HashSet<>(structure);
        Deque<BlockPos> queue = new ArrayDeque<>(structure);
        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            Set<BlockPos> members = MultiblockMembershipRegistry.membersOf(level, pos);
            for (BlockPos member : members) {
                if (expanded.add(member)) {
                    queue.add(member);
                }
            }
        }
        return expanded;
    }

    /**
     * Task 4 (CONTRAPTIONS.md 2026-07-01 session): the bearing no longer produces its own
     * fixed rpm/speed — it pulls from a REAL adjacent motor. {@code bearing} is still a valid
     * real-world {@link BlockPos} in {@code level} at this point (glue-structure removal
     * happened just before this is called, in {@link #assemble}, but real-world coordinates
     * stay meaningful regardless of what block, if any, currently occupies them — the 6
     * neighbor cells around it are what's actually scanned, and a motor block placed next to
     * the bearing is never itself part of the glued/captured structure). See
     * {@link RealMotorLink} and each behavior's own javadoc for the scan-once/re-resolve-every-tick
     * mechanism and the {@code rotationalRpm}/{@code DEFAULT_LINEAR_SPEED} fallback-only role.
     */
    public static void attachDefaultBehavior(Level level, BlockPos bearing, ContraptionState state, BearingType type,
            double rotationalRpm, double suPerBlock) {
        BlockPos motorPos = RealMotorLink.findAdjacentMotor(level, bearing);
        switch (type) {
            case LINEAR -> {
                // 6-directional extending piston (2026-07-03 rework — replaces the old one-way
                // LinearActuatorBehavior). Direction from the bearing block's own `facing`. Distance/
                // speed/mode/dwell now come PER-INSTANCE from the bearing's PistonBearingBlockEntity
                // (edited via the right-click config menu), falling back to the block YAML `*At` defaults
                // when there's no block entity (an unconfigured bearing). The installed UPGRADE items on
                // that block entity scale the piston: finalSpeed = baseSpeed * speedMultiplier and
                // finalSuPerBlock = suPerBlock * fuelMultiplier (efficiency upgrades cut su). The body
                // stays at `bearing` so PistonBearingBehavior can keep reading the real adjacent motor
                // AND external redstone even while the structure is captured.
                dev.arubik.craftengine.contraption.behavior.PistonBearingBlockEntity be =
                        dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.controllerAt(level, bearing);
                dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior.Mode mode;
                int distance;
                double baseSpeed;
                long rrDelay;
                double finalSuPerBlock;
                if (be != null) {
                    dev.arubik.craftengine.machine.upgrade.UpgradeModifiers mods = be.upgradeModifiers();
                    mode = be.mode();
                    distance = be.distance();
                    baseSpeed = be.speedBlocksPerSec() * mods.speedMultiplier();
                    rrDelay = be.roundRobinDelayTicks();
                    finalSuPerBlock = suPerBlock * mods.fuelMultiplier();
                } else {
                    mode = dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.pistonModeAt(level, bearing);
                    distance = dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.distanceAt(level, bearing);
                    baseSpeed = dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.speedAt(level, bearing);
                    rrDelay = dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.roundRobinDelayAt(level, bearing);
                    finalSuPerBlock = suPerBlock;
                }
                Vec3 facing = dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.facingVecAt(level, bearing);
                state.addBehavior(new dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior(
                        facing, bearing, motorPos, distance, baseSpeed, finalSuPerBlock, mode, rrDelay));
            }
            case ROTATIONAL -> state.addBehavior(new RotationalBearingBehavior(motorPos, rotationalRpm, suPerBlock));
            case MINECART -> {
                // Never reached: MINECART bearings go through MinecartBearing.assemble, not
                // this method (see BearingHammerListener) — MinecartFollowBehavior is
                // attached there instead, once the anchor entity exists.
            }
        }
    }

    /**
     * Dynamic -&gt; Static for a block-anchored bearing: grid-snap the contraption's CURRENT
     * continuous position, collision-check + restore/eject each cell (CONTRAPTIONS.md §4
     * spike #7), despawn the render swarms, dispose the mini-dimension, and drop it from
     * {@link ContraptionManager}.
     */
    public static void disassemble(World bukkitWorld, ContraptionEntity entity) {
        ContraptionState state = entity.state();
        // Remove the on-disk persistence file first thing (all disassemble callers are covered here,
        // so a later chunk-load can never resurrect a disassembled contraption) — see
        // BlockAnchoredContraptionStore#delete. No-op / harmless for a contraption that was never
        // block-anchored-persisted.
        dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore.delete(state.id());
        // Snapshot BEFORE any despawn touches the hitbox swarm's own bookkeeping — see
        // ContraptionEntity#currentRiderIds's javadoc.
        java.util.Set<UUID> riderIds = entity.currentRiderIds();
        List<net.momirealms.craftengine.core.entity.player.Player> viewers = CePlayers.resolve(bukkitWorld.getPlayers());
        // Fall-through-the-floor fix (CONTRAPTIONS.md — "al despawnear un contraption/holo el
        // jugador parado sobre el contraption cae hacia abajo/atraviesa"): despawn everything
        // EXCEPT the hitbox/shulker-collider swarm now (as before); the hitbox swarm itself is
        // despawned further down, only once the real blocks are actually back in the world —
        // see ContraptionEntity#despawnRest/#despawnHitboxesOnly's javadocs for why.
        entity.despawnRest(viewers);
        ContraptionManager.remove(state.id());

        Level level = ((CraftWorld) bukkitWorld).getHandle();
        BlockPos snappedBearing = ContraptionMath.gridSnap(new Vec3(state.x(), state.y(), state.z()));
        // Axis-snap rotation (CONTRAPTIONS.md "al desarmar intentara acomodar en uno de los 4
        // ejes nunca en diagonal. y rotara los bloques de la estructura"): a contraption can be
        // mid-spin (ROTATIONAL bearing) or at an arbitrary heading (minecart) when disassembled
        // — captures always start at yaw 0 (see ContraptionCapture#capture's ContraptionLevel
        // .create(..., 0) call), so quarterTurns is simply how far the CURRENT yaw has drifted
        // from that origin, snapped to the nearest cardinal direction.
        int quarterTurns = ContraptionMath.quarterTurnsBetween(0, state.yawRadians());
        // Re-establish glue for every block as it lands (2026-07-03 — "al deconstruir cualquier
        // cosa siempre todos sus bloques se peguen ... si hay glue usa la guardada"). Rebuilds the
        // world glue graph from the level's PERSISTED local edges (so it works after a restart,
        // unlike the old registry re-key which needed the volatile in-memory graph to still hold
        // the capture-time nodes), rotated + translated to the exact positions restoreRotated
        // writes. Must run before dispose() (needs localPositions()/glueEdgesLocal()).
        ContraptionCapture.restoreGlue(bukkitWorld.getUID(), state.level(), state.originBearingBlockPos(),
                snappedBearing, quarterTurns);
        ContraptionCapture.restoreRotated(level, state.level(), snappedBearing, quarterTurns);
        // Reverse of ContraptionFurnitureCapture#captureNear (bug fix, this session — "al
        // disassemble debe... volver a spawnear el mueble real normal"): re-place a REAL
        // CraftEngine furniture entity for every captured piece, snapped to the SAME
        // block-aligned bearing/rotation the blocks just landed on above (not the raw continuous
        // transform) — see ContraptionFurnitureCapture#restoreFurniture's own javadoc.
        ContraptionFurnitureCapture.restoreFurniture(bukkitWorld, state.furniture(), snappedBearing, quarterTurns);
        // Real blocks are now genuinely present in the world (client-side too, once the
        // block-change packets flush) — safe to nudge any rider up a hair and THEN despawn the
        // hitbox swarm; doing this any earlier reintroduces the fall-through window.
        nudgeRidersUp(riderIds);
        entity.despawnHitboxesOnly(viewers);
        if (state.level() != null) {
            // Any fluid_block_tank controller living inside the mini-dimension owns packet-only
            // fluid displays keyed by ITS OWN position (dev.arubik.craftengine.fluid.behavior
            // .FluidTankRender.RENDERS) — normally cleared by the controller's own onRemove() when
            // a block is properly removed, but dispose() below just discards the whole dimension
            // wholesale (no state-change event ever fires), so those displays would otherwise
            // orphan forever at the hologram's last position instead of despawning/following the
            // now-restored real block. FluidTankRender.remove is a cheap no-op for any position
            // that isn't a tank controller, so it's safe to call blindly for every captured cell.
            for (net.minecraft.core.BlockPos local : state.level().localPositions()) {
                try {
                    dev.arubik.craftengine.fluid.behavior.FluidTankRender.remove(state.level(), local);
                } catch (Throwable ignored) {
                }
                try {
                    dev.arubik.craftengine.block.entity.PersistentBlockEntity pbe = dev.arubik.craftengine.block.entity.PersistentBlockEntity
                            .getIfLoaded(state.level(), local);
                    if (pbe instanceof dev.arubik.craftengine.conveyor.ConveyorBlockEntity conveyor) {
                        conveyor.despawnRender();
                    }
                } catch (Throwable ignored) {
                }
            }
            state.level().transferRemainingEntitiesToRealWorld();
            state.level().dispose();
        }
    }

    /**
     * Tiny safety-net Y correction (CONTRAPTIONS.md — "mueve el player 1px hacia arriba para
     * que no atraviese"): applied right after the real blocks are restored, for whichever real
     * players the hitbox swarm had tracked as standing on the contraption at the moment of
     * teardown. Uses the same one-shot relative {@code Player#teleport} convention {@link
     * ContraptionSeatListener} already relies on for its own "don't leave the player stuck in a
     * boundary" nudge (see that class's stand-up handling) rather than reaching for a raw NMS
     * position-sync packet — a SINGLE small relative teleport doesn't reintroduce the
     * repeated-correction jitter the class javadoc there warns about; it's only a problem when a
     * correction fires every tick. Bumping the player up by a hair guarantees their feet resolve
     * against the now-solid real block underneath instead of whatever razor-thin overlap they
     * happened to be sitting at the instant the fake hitbox vanished. Shared by
     * {@link #disassemble} and {@link HologramTest#stop} (both call this identically, right
     * after their own {@code restoreRotated} + right before their own
     * {@code despawnHitboxesOnly}).
     */
    static void nudgeRidersUp(java.util.Set<UUID> riderIds) {
        if (riderIds.isEmpty()) {
            return;
        }
        // ~1px in block units — a hair's-width safety epsilon, not a real vertical displacement.
        final double NUDGE = 0.05;
        for (UUID id : riderIds) {
            org.bukkit.entity.Player bukkit = org.bukkit.Bukkit.getPlayer(id);
            if (bukkit == null) {
                continue; // logged off mid-teardown — nothing to nudge
            }
            bukkit.teleport(bukkit.getLocation().add(0, NUDGE, 0));
        }
    }

    /**
     * See {@link GlueGraph#translateAll}. Shared by {@link #disassemble} and
     * {@link HologramTest#stop} (both restore a contraption's blocks back into the real world,
     * possibly at a different position than it was captured from). {@code quarterTurns} (0-3)
     * must be the SAME value passed to {@link ContraptionCapture#restoreRotated} for this
     * disassembly — glue nodes need to land at the exact same rotated world positions the
     * blocks themselves are being restored to, or "termina glue x todo el mundo" all over
     * again just for the rotated case.
     */
    static void translateGlueToRestorePosition(UUID worldId, ContraptionState state, BlockPos snappedBearing,
            int quarterTurns) {
        if (state.level() == null) {
            return;
        }
        BlockPos origin = state.originBearingBlockPos();
        if (origin.equals(snappedBearing) && quarterTurns == 0) {
            return; // never moved or rotated — nothing to translate
        }
        Map<BlockPos, BlockPos> move = new java.util.HashMap<>();
        for (BlockPos local : state.level().localPositions()) {
            BlockPos rotatedLocal = ContraptionCapture.rotateLocal(local, quarterTurns);
            move.put(ContraptionMath.toWorld(local, origin), ContraptionMath.toWorld(rotatedLocal, snappedBearing));
        }
        GlueRegistry.graphFor(worldId).translateAll(move);
    }
}
