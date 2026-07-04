package dev.arubik.craftengine.contraption;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.plugin.Plugin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Phase 2/3 throwaway test harness (CONTRAPTIONS.md §5): captures the glued structure at a
 * looked-at block, removes it from the real world, and spawns a {@link ContraptionEntity}
 * hologram in its exact place — "any bug is obvious against nothing having moved."
 * Rendering is driven by the global {@link ContraptionEngine} loop (registered once in
 * {@code onEnable}), not a per-run timer — this class only owns spawn/despawn bookkeeping.
 * One active hologram per world (simplest possible MVP); repeat command replaces the
 * previous one.
 */
public final class HologramTest {

    private HologramTest() {
    }

    private static final Map<UUID, Run> ACTIVE = new HashMap<>();

    public static void start(Plugin plugin, org.bukkit.World bukkitWorld, BlockPos bearing) {
        stop(bukkitWorld);

        Set<BlockPos> structure = GlueRegistry.structureAt(bukkitWorld.getUID(), bearing);
        Level level = ((CraftWorld) bukkitWorld).getHandle();
        // Same multiblock auto-expansion as ContraptionAssembler#assemble (CONTRAPTIONS.md —
        // gluing even one cell of a multiblock, e.g. one fluid_block_tank corner, must still
        // capture the whole multiblock) — this test harness must match production behavior or
        // this exact bug looks "fixed" via ContraptionAssembler but still reproduces via
        // /cep contraption spawn-holo.
        structure = ContraptionAssembler.expandMultiblockMembers(level, structure);
        ContraptionCapture.Result captured = ContraptionCapture.capture(level, structure, bearing);
        // Persist glue topology onto the captured level — parity with ContraptionAssembler#assemble.
        ContraptionCapture.captureGlueEdges(bukkitWorld.getUID(), captured.level(), bearing);
        // Furniture scan BEFORE the blocks are removed — mirrors ContraptionAssembler#assemble
        // exactly (bug fix, this session — "el furniture original no desaparece y tampoco
        // aparece la representacion"): this test harness used to skip furniture capture
        // entirely, so /cep contraption spawn-holo left the real furniture untouched and never
        // built any ContraptionFurniture cells for the swarm to render — the real bearing-driven
        // assemble() path (BearingHammerListener -> ContraptionAssembler#assemble) always called
        // this; only this debug harness didn't, which is exactly why nothing happened AND no
        // capture-failure warnings ever fired (the method was simply never invoked).
        ContraptionFurnitureCapture.Result furnitureResult = ContraptionFurnitureCapture.captureNear(level, structure, bearing, captured.level());
        ContraptionCapture.removeFromWorld(level, structure);

        ContraptionState state = new ContraptionState(UUID.randomUUID(), bukkitWorld.getUID(), captured.level(),
                bearing.getX(), bearing.getY(), bearing.getZ());
        state.setFurniture(furnitureResult.furniture());
        for (Map.Entry<UUID, Vec3> e : furnitureResult.seatedRiders().entrySet()) {
            state.addSeatedRider(e.getKey(), e.getValue());
            UUID mountId = furnitureResult.seatedRiderMounts().get(e.getKey());
            if (mountId != null) {
                state.setSeatedRiderMount(e.getKey(), mountId);
            }
        }
        for (MovementBehavior autoBehavior : captured.autoBehaviors()) {
            state.addBehavior(autoBehavior);
        }
        ContraptionEntity entity = ContraptionManager.register(new ContraptionEntity(state));

        Run run = new Run();
        run.bukkitWorld = bukkitWorld;
        run.bearing = bearing;
        run.entity = entity;
        ACTIVE.put(bukkitWorld.getUID(), run);
    }

    /** Attaches a constant-velocity {@link dev.arubik.craftengine.contraption.behavior.LinearActuatorBehavior}
     * to the world's active hologram, in blocks/second, so {@link ContraptionEngine} moves it. */
    public static boolean setLinearVelocity(org.bukkit.World bukkitWorld, double dxPerSec, double dyPerSec, double dzPerSec) {
        Run run = ACTIVE.get(bukkitWorld.getUID());
        if (run == null) {
            return false;
        }
        run.entity.state().behaviors().clear();
        run.entity.state().addBehavior(
                dev.arubik.craftengine.contraption.behavior.LinearActuatorBehavior.blocksPerSecond(dxPerSec, dyPerSec, dzPerSec));
        return true;
    }

    /**
     * Debug tool: instantly shifts the active hologram's CONTINUOUS position by a one-shot
     * delta (unlike {@link #setLinearVelocity}, which attaches a constant blocks/sec
     * behavior) — for manually testing movement/rendering without a real bearing behavior
     * running. Pushes straight through {@link ContraptionState#setPosition}, which already
     * keeps {@link dev.arubik.craftengine.contraption.level.ContraptionLevel#setTransform} in
     * sync for live rendering.
     */
    public static boolean nudgePosition(org.bukkit.World bukkitWorld, double dx, double dy, double dz) {
        Run run = ACTIVE.get(bukkitWorld.getUID());
        if (run == null) {
            return false;
        }
        ContraptionState state = run.entity.state();
        state.setPosition(state.x() + dx, state.y() + dy, state.z() + dz);
        return true;
    }

    /**
     * Debug tool: adds {@code degrees} to the active hologram's current yaw — for manually
     * testing rotation (and the axis-snap-on-disassemble behavior, see
     * {@link ContraptionMath#snapYawToCardinal}) without a real ROTATIONAL bearing spinning
     * it. Pushes straight through {@link ContraptionState#setYawRadians}.
     */
    public static boolean rotateYawDegrees(org.bukkit.World bukkitWorld, double degrees) {
        Run run = ACTIVE.get(bukkitWorld.getUID());
        if (run == null) {
            return false;
        }
        ContraptionState state = run.entity.state();
        state.setYawRadians(state.yawRadians() + Math.toRadians(degrees));
        return true;
    }

    /**
     * Attaches a {@link dev.arubik.craftengine.contraption.behavior.MinerBehavior} targeting a
     * bearing-relative offset, at a manually-injected rpm (gearRatio 1.0, {@code setInputRpm}
     * called once immediately — this test command has no live {@code RpmProvider} to drive it,
     * unlike a real captured miner block fed by a {@code RotationalBearingBehavior}).
     */
    public static boolean addMiner(org.bukkit.World bukkitWorld, BlockPos targetOffset, double rpm) {
        Run run = ACTIVE.get(bukkitWorld.getUID());
        if (run == null) {
            return false;
        }
        dev.arubik.craftengine.contraption.behavior.MinerBehavior miner =
                new dev.arubik.craftengine.contraption.behavior.MinerBehavior(targetOffset, 1.0);
        miner.setInputRpm((float) rpm);
        run.entity.state().addBehavior(miner);
        return true;
    }

    public static void stop(org.bukkit.World bukkitWorld) {
        Run run = ACTIVE.remove(bukkitWorld.getUID());
        if (run == null) {
            return;
        }
        // Snapshot BEFORE any despawn touches the hitbox swarm's own bookkeeping — see
        // ContraptionEntity#currentRiderIds's javadoc.
        java.util.Set<java.util.UUID> riderIds = run.entity.currentRiderIds();
        List<net.momirealms.craftengine.core.entity.player.Player> viewers = CePlayers.resolve(run.bukkitWorld.getPlayers());
        // Fall-through-the-floor fix (CONTRAPTIONS.md — "al despawnear un contraption/holo el
        // jugador parado sobre el contraption cae hacia abajo/atraviesa"): despawn everything
        // EXCEPT the hitbox/shulker-collider swarm now (as before); the hitbox swarm itself is
        // despawned further down, only once the real blocks are actually back in the world —
        // see ContraptionEntity#despawnRest/#despawnHitboxesOnly's javadocs for why.
        run.entity.despawnRest(viewers);
        ContraptionManager.remove(run.entity.state().id());

        Level level = ((CraftWorld) run.bukkitWorld).getHandle();
        // Grid-snap disassembly (CONTRAPTIONS.md §1 "Assembly/disassembly", spike #7): restore
        // at wherever the contraption's CURRENT continuous position rounds to (not necessarily
        // where it started — Phase 3 may have translated it), collision-checking each target
        // cell so a player who built something in the flight path gets it ejected as a drop
        // instead of silently overwritten.
        ContraptionState state = run.entity.state();
        BlockPos snappedBearing = ContraptionMath.gridSnap(new Vec3(state.x(), state.y(), state.z()));
        // Axis-snap rotation — see ContraptionAssembler#disassemble's matching comment. Captures
        // always start at yaw 0, so quarterTurns is just the current yaw's drift from that
        // origin, snapped to the nearest cardinal direction.
        int quarterTurns = ContraptionMath.quarterTurnsBetween(0, state.yawRadians());
        ContraptionCapture.restoreGlue(run.bukkitWorld.getUID(), state.level(), state.originBearingBlockPos(),
                snappedBearing, quarterTurns);
        ContraptionCapture.restoreRotated(level, state.level(), snappedBearing, quarterTurns);
        // Reverse of ContraptionFurnitureCapture#captureNear — see
        // ContraptionAssembler#disassemble's matching comment and
        // ContraptionFurnitureCapture#restoreFurniture's own javadoc.
        ContraptionFurnitureCapture.restoreFurniture(run.bukkitWorld, state.furniture(), snappedBearing, quarterTurns);
        // Real blocks are now genuinely present in the world — safe to nudge any rider up a hair
        // and THEN despawn the hitbox swarm; see ContraptionAssembler#disassemble's matching
        // comment and ContraptionAssembler#nudgeRidersUp's javadoc for why.
        ContraptionAssembler.nudgeRidersUp(riderIds);
        run.entity.despawnHitboxesOnly(viewers);

        if (state.level() != null) {
            // Same leak ContraptionAssembler#disassemble fixes (see its javadoc): a
            // fluid_block_tank controller living inside the mini-dimension owns packet-only
            // fluid displays keyed by ITS OWN position (FluidTankRender.RENDERS), normally
            // cleared via the controller's own onRemove() — but this despawn-holo path (like
            // disassemble()) just discards the whole ContraptionLevel wholesale, so no
            // state-change event ever fires and the displays would otherwise orphan forever at
            // the hologram's last position instead of despawning/following the now-restored
            // real block. FluidTankRender.remove is a cheap no-op for any position that isn't a
            // tank controller, so it's safe to call blindly for every captured cell.
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

    private static final class Run {
        org.bukkit.World bukkitWorld;
        BlockPos bearing;
        ContraptionEntity entity;
    }
}
