package dev.arubik.craftengine.contraption;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorld;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

/**
 * Master tick loop for every registered contraption (CONTRAPTIONS.md §1 "Kinematics —
 * single master clock", §5 Phase 3), shaped after {@code FluidEngine.tickAll}: ONE
 * authoritative driver registered once in {@code onEnable}, not a ticker per component.
 *
 * <p>Per contraption per tick: every {@link MovementBehavior#tick} runs first (so internal
 * state always advances), THEN the stall vote decides whether the summed
 * {@link MovementBehavior#velocityThisTick} is actually applied to the bearing's position.
 * Rendering (the {@link ContraptionEntity}'s swarms) happens every tick right after, so
 * movement is never visually ahead of what actually got applied.
 */
public final class ContraptionEngine {

    private ContraptionEngine() {
    }

    public static volatile boolean ENABLED = true;

    /** Advance every registered contraption by one tick: behaviors, stall gate, render. */
    public static void tickAll() {
        if (!ENABLED) {
            return;
        }
        boolean perf = ContraptionPerf.enabled();
        if (perf) {
            ContraptionPerf.tickStart();
        }
        // Per-WORLD, per-tick viewer caches, shared by every contraption in that world (see #viewersOf).
        java.util.Map<java.util.UUID, java.util.List<net.momirealms.craftengine.core.entity.player.Player>> viewerCache =
                new java.util.HashMap<>();
        java.util.Map<java.util.UUID, java.util.List<net.minecraft.server.level.ServerPlayer>> carryCache =
                new java.util.HashMap<>();
        // Euler/robin_euler "extended-solid" bearings (dropped their load as real blocks) aren't live
        // contraptions, so they're ticked separately: a redstone pulse / dwell timer re-assembles +
        // retracts them (2026-07-03 goal). See EulerExtendedRegistry.
        try {
            EulerExtendedRegistry.tick();
        } catch (Throwable ignored) {
        }
        // Every PhysContraption in a world is solved in ONE call, before behaviors run, so
        // contraption-vs-contraption contacts are resolved simultaneously rather than each body
        // pushing off a stale copy of the other. The solve only COMPUTES here: the result is handed to
        // each PhysicsBehavior as pending motion and applied through the ordinary behavior path below,
        // so stepKinematics still derives lastDelta/lastYawDelta from it — which is what carries
        // standing riders. See PhysicsWorld#writeBack.
        long physStart = perf ? ContraptionPerf.begin() : 0L;
        try {
            dev.arubik.craftengine.contraption.physics.PhysicsWorld.stepAll();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (perf) {
            ContraptionPerf.end(ContraptionPerf.Phase.PHYSICS, physStart);
        }
        java.util.List<ContraptionEntity> disassembleAtEnd = null;
        // Minecart contraptions whose real anchor cart was DESTROYED (end portal / kill) — collected
        // during iteration, torn down after it (a MovementBehavior can't dismantle its own contraption
        // mid-tick without mutating ContraptionManager's set). See MinecartFollowBehavior's "End-portal
        // safety" and MinecartBearing#disassembleInPlace (roadmap item #1).
        java.util.List<ContraptionEntity> minecartDeadAtEnd = null;
        // Ghast contraptions whose cell set changed and may therefore have fractured. Deferred for the
        // same reason as the lists above: splitting REGISTERS new contraptions, and ContraptionManager's
        // set cannot be mutated while this loop is iterating it. See GhastFollowBehavior#consumeFractureCheck.
        java.util.List<ContraptionEntity> ghastFractureAtEnd = null;
        for (ContraptionEntity entity : ContraptionManager.all()) {
            org.bukkit.World bukkitWorld = safeGetWorld(entity.state());
            ServerLevel level = bukkitWorld != null ? ((CraftWorld) bukkitWorld).getHandle() : null;

            ContraptionState state = entity.state();
            Vec3 posBefore = new Vec3(state.x(), state.y(), state.z());
            double yawBefore = state.yawRadians();

            long behaviorStart = perf ? ContraptionPerf.begin() : 0L;
            stepKinematics(state, level);
            if (perf) {
                ContraptionPerf.end(ContraptionPerf.Phase.BEHAVIORS, behaviorStart);
            }

            // A behavior may have RE-ANCHORED the contraption into a different world during
            // stepKinematics (roadmap item #1 — minecart portal crossing calls
            // ContraptionEntity#teleport, which despawns the old world's satellites and rewrites
            // state.worldId()). Re-resolve the world/level so this tick's render + carry below target
            // the CURRENT (destination) world — never the pre-teleport one, into which they would
            // otherwise wrongly re-spawn the swarms we just despawned. `bukkitWorld`/`level` were
            // resolved at the top of the loop, before the behaviors ran.
            boolean crossedWorld = false;
            {
                org.bukkit.World worldNow = safeGetWorld(state);
                if (worldNow != bukkitWorld) {
                    crossedWorld = true;
                    bukkitWorld = worldNow;
                    level = bukkitWorld != null ? ((CraftWorld) bukkitWorld).getHandle() : null;
                }
            }

            // Fire ContraptionMoveEvent (public API) only when the transform actually changed this
            // tick — never for an idle/stalled contraption whose position and yaw are unchanged, and
            // never across a world re-anchor (a cross-world jump is a teleport, not a move; its
            // discontinuous from/to would be meaningless to a ContraptionMoveEvent listener).
            if (bukkitWorld != null && !crossedWorld && (state.x() != posBefore.x || state.y() != posBefore.y
                    || state.z() != posBefore.z || state.yawRadians() != yawBefore)) {
                fireMove(entity, bukkitWorld, posBefore, new Vec3(state.x(), state.y(), state.z()),
                        state.yawRadians() - yawBefore);
            }

            if (bukkitWorld != null) {
                // Everything below this point only has meaning where the contraption actually IS in the
                // real world — its projection, the entities it carries/pushes, and the real containers it
                // trades items with all live in the chunk under its current position. When that chunk is
                // not loaded there is nothing to project onto, nothing to carry, and no container to
                // reach; worse, several of these paths read real block/entity state, which on an unloaded
                // chunk forces a synchronous chunk load — so this gate is a correctness guard as much as a
                // cost one. The BEHAVIORS above are deliberately OUTSIDE it: a contraption's internal
                // state (bearing angle, piston extension, SU demand, its dead-anchor watchdogs below) must
                // keep advancing regardless of who is looking, or it would silently freeze mid-travel and
                // resume from a stale pose — and they touch only the hidden mini-dimension, which is
                // permanently chunk-ticketed and always loaded (see ContraptionLevel#ensureChunkTicking).
                boolean chunkLoaded = isAtLoadedChunk(bukkitWorld, state);
                try {
                    render(entity, bukkitWorld, level, viewerCache, carryCache, chunkLoaded, perf);
                } catch (Throwable ignored) {
                    // Keep other contraptions ticking even if one viewer/render call misbehaves.
                }
                // Hopper/inventory I/O bridge (roadmap item #6): move one item per cooldown between a
                // captured vanilla hopper and the real chest adjacent to the contraption's current
                // real-world footprint — but only while the contraption is docked (grid-aligned). A
                // captured hopper's native tick can only reach the void mini-dimension, so this is the
                // sole path items cross the contraption/real-world boundary. Wrapped so one bad transfer
                // never breaks the master tick loop; the bridge is a no-op when nothing is adjacent.
                if (chunkLoaded) {
                    try {
                        dev.arubik.craftengine.contraption.behavior.ContraptionHopperBridge.tick(entity);
                    } catch (Throwable ignored) {
                    }
                }
                // EULER / ROBIN_EULER piston reached the end → turn the whole contraption into real
                // blocks (a disassemble the engine performs, since a MovementBehavior can't dismantle
                // itself). Collected and run AFTER the loop so we don't mutate ContraptionManager's
                // set mid-iteration. See PistonBearingBehavior#wantsDisassembleAtEnd.
                if (pistonWantsDisassemble(entity)) {
                    if (disassembleAtEnd == null) {
                        disassembleAtEnd = new java.util.ArrayList<>();
                    }
                    disassembleAtEnd.add(entity);
                }
                // Dead entity anchor (an end portal destroyed the cart / the harnessed ghast is gone) →
                // safe disassemble-in-place, deferred to after the loop for the same set-mutation reason
                // as the piston path above.
                if (ghastWantsFractureCheck(entity)) {
                    if (ghastFractureAtEnd == null) {
                        ghastFractureAtEnd = new java.util.ArrayList<>();
                    }
                    ghastFractureAtEnd.add(entity);
                }
                if (minecartAnchorDead(entity) || ghastAnchorDead(entity)) {
                    if (minecartDeadAtEnd == null) {
                        minecartDeadAtEnd = new java.util.ArrayList<>();
                    }
                    minecartDeadAtEnd.add(entity);
                }
            }
        }
        if (ghastFractureAtEnd != null) {
            for (ContraptionEntity entity : ghastFractureAtEnd) {
                try {
                    // Any island that is no longer adjacent to the rest falls off as its own PHYS body —
                    // the splitter decides, and no-ops when the structure turns out to still be whole.
                    dev.arubik.craftengine.contraption.physics.ContraptionSplitter.splitIfDisconnected(entity);
                    // Re-dump the structure onto the ghast, because it just changed.
                    //
                    // The PDC was otherwise only ever written at assemble time, so it described the
                    // structure as it was BUILT, not as it is. Every later edit — a block placed, a block
                    // broken, a piston shoving one off — was invisible to it, and a restart rehydrated the
                    // original: cells the player had removed came back, and cells that had fractured away
                    // into their own (session-only) phys body returned on the ghast while their blocks had
                    // also been restored into the world. That is the duplicate.
                    resaveGhastStructure(entity);
                } catch (Throwable t) {
                    org.bukkit.Bukkit.getLogger().warning("[Contraption] ghast fracture split failed: " + t);
                }
            }
        }
        if (minecartDeadAtEnd != null) {
            for (ContraptionEntity entity : minecartDeadAtEnd) {
                org.bukkit.World w = safeGetWorld(entity.state());
                if (w == null) {
                    continue;
                }
                try {
                    // The anchor entity is already gone in both cases, so the ghast path is passed null —
                    // there is nothing left to untag. Which teardown to run is decided by the behavior the
                    // contraption actually carries, not by its recorded BearingType (a pre-GHAST state can
                    // have none — see ContraptionState#bearingType).
                    if (ghastAnchorDead(entity)) {
                        GhastHarnessBearing.disassembleInPlace(w, null, entity);
                    } else {
                        MinecartBearing.disassembleInPlace(w, entity);
                    }
                    dev.arubik.craftengine.contraption.BearingHammerListener.forgetAssembled(entity.state().id());
                } catch (Throwable t) {
                    org.bukkit.Bukkit.getLogger().warning("[Contraption] dead-anchor disassemble failed: " + t);
                }
            }
        }
        if (disassembleAtEnd != null) {
            for (ContraptionEntity entity : disassembleAtEnd) {
                org.bukkit.World w = safeGetWorld(entity.state());
                if (w == null) {
                    continue;
                }
                try {
                    // Record the euler-extended state BEFORE disassemble disposes the level: the exact
                    // world positions the load is about to land on (same grid-snap + rotation the
                    // disassemble uses), so a later redstone pulse / dwell timer can re-capture and
                    // retract them (EulerExtendedRegistry). Only for a piston in a "become real
                    // blocks" mode.
                    recordEulerDrop(entity);
                    ContraptionAssembler.disassemble(w, entity);
                    dev.arubik.craftengine.contraption.BearingHammerListener.forgetAssembled(entity.state().id());
                } catch (Throwable t) {
                    org.bukkit.Bukkit.getLogger().warning("[Contraption] piston disassemble-at-end failed: " + t);
                }
            }
        }
        if (perf) {
            ContraptionPerf.tickEnd();
        }
    }

    /**
     * Whether the real-world chunk under a contraption's CURRENT position is loaded. Uses the live
     * continuous position rather than the bearing's fixed anchor: a travelling contraption (minecart,
     * phys body) projects where it IS, not where it was assembled, and it is the destination chunk that
     * has to be loaded for any of the render/carry work to mean anything.
     *
     * <p>Note this is a genuinely different question from the anchor-chunk unload that
     * {@code ContraptionChunkLifecycleListener} already tears contraptions down on: that fires for the
     * ANCHOR's chunk, so a contraption anchored in a loaded chunk can still have travelled its body into
     * unloaded terrain and stay fully live here.
     */
    private static boolean isAtLoadedChunk(org.bukkit.World world, ContraptionState state) {
        try {
            return world.isChunkLoaded(net.minecraft.util.Mth.floor(state.x()) >> 4,
                    net.minecraft.util.Mth.floor(state.z()) >> 4);
        } catch (Throwable ignored) {
            return true; // pure-JVM unit-test path (no live server) — behave exactly as before this gate existed
        }
    }

    /** Snapshots a euler/robin_euler piston's dropped-load positions into {@link EulerExtendedRegistry}. */
    private static void recordEulerDrop(ContraptionEntity entity) {
        ContraptionState state = entity.state();
        dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior piston = null;
        for (dev.arubik.craftengine.contraption.MovementBehavior b : state.behaviors()) {
            if (b instanceof dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior p) {
                piston = p;
                break;
            }
        }
        if (piston == null || state.level() == null) {
            return;
        }
        net.minecraft.core.BlockPos snapped = ContraptionMath.gridSnap(
                new net.minecraft.world.phys.Vec3(state.x(), state.y(), state.z()));
        int quarterTurns = ContraptionMath.quarterTurnsBetween(0, state.yawRadians());
        java.util.Set<net.minecraft.core.BlockPos> restored = new java.util.HashSet<>();
        for (net.minecraft.core.BlockPos local : state.level().localPositions()) {
            restored.add(ContraptionMath.toWorld(ContraptionCapture.rotateLocal(local, quarterTurns), snapped));
        }
        if (restored.isEmpty()) {
            return;
        }
        // Place the physical SHAFT (2026-07-03 — "al volverse fisico tambien los shaft deben volverse
        // fisicos"): real cml:linear_bearing blocks (part=3 pipe run, part=2 head at the front) along
        // the pole, so the euler-dropped structure reads as physically connected to the body instead
        // of "popping" on re-grab. These part!=0 blocks create no block entity (see
        // BearingBlockBehavior#createBlockEntityController). Removed again on re-grab (EulerExtendedRegistry).
        java.util.Set<net.minecraft.core.BlockPos> shaftPositions = new java.util.HashSet<>();
        try {
            org.bukkit.World w = safeGetWorld(state);
            if (w != null) {
                ServerLevel level = ((CraftWorld) w).getHandle();
                net.minecraft.core.BlockPos body = state.originBearingBlockPos();
                net.minecraft.world.phys.Vec3 f = piston.direction();
                int dx = (int) Math.round(f.x), dy = (int) Math.round(f.y), dz = (int) Math.round(f.z);
                int dist = piston.maxDistance();
                net.minecraft.world.level.block.state.BlockState pipe = bearingPartState(level, body, 3);
                net.minecraft.world.level.block.state.BlockState head = bearingPartState(level, body, 2);
                for (int i = 1; i <= dist; i++) {
                    net.minecraft.core.BlockPos p = body.offset(dx * i, dy * i, dz * i);
                    net.minecraft.world.level.block.state.BlockState st = (i == dist) ? head : pipe;
                    if (st != null && level.getBlockState(p).isAir()) {
                        level.setBlock(p, st, 3);
                        shaftPositions.add(p.immutable());
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        boolean initialRedstone = false;
        try {
            org.bukkit.World w = safeGetWorld(state);
            if (w != null) {
                initialRedstone = ((CraftWorld) w).getHandle().hasNeighborSignal(state.originBearingBlockPos());
            }
        } catch (Throwable ignored) {
        }
        EulerExtendedRegistry.record(state.worldId(), state.originBearingBlockPos(), piston.direction(),
                piston.maxDistance(), piston.baseSpeedBlocksPerSec(), piston.suPerBlock(), piston.mode(),
                piston.roundRobinDelayTicks(), restored, shaftPositions, initialRedstone);
    }

    /** Resolves the bearing block's own (facing, part) placeholder BlockState for the physical shaft render. */
    private static net.minecraft.world.level.block.state.BlockState bearingPartState(ServerLevel level,
            net.minecraft.core.BlockPos bodyPos, int part) {
        try {
            net.minecraft.world.level.block.state.BlockState real = level.getBlockState(bodyPos);
            net.momirealms.craftengine.core.block.ImmutableBlockState ce =
                    net.momirealms.craftengine.bukkit.util.BlockStateUtils.getOptionalCustomBlockState(real).orElse(null);
            if (ce == null || ce.isEmpty()) {
                return null;
            }
            @SuppressWarnings("rawtypes")
            net.momirealms.craftengine.core.block.property.Property partProp = ce.getProperty("part");
            if (partProp == null) {
                return null;
            }
            return (net.minecraft.world.level.block.state.BlockState) net.momirealms.craftengine.core.block.ImmutableBlockState
                    .with(ce, partProp, (Comparable) Integer.valueOf(part)).customBlockState().minecraftState();
        } catch (Throwable t) {
            return null;
        }
    }

    /** Whether a contraption's harnessed-ghast anchor is gone (see {@code GhastFollowBehavior#wantsDisassembleInPlace}). */
    /**
     * Whether this contraption is ghast-anchored and its cells changed since the last poll, so it may
     * have fractured. Consuming, so one change is only ever acted on once.
     */
    /** Writes this ghast contraption's CURRENT structure back onto its anchor ghast's PDC. */
    private static void resaveGhastStructure(ContraptionEntity entity) {
        for (MovementBehavior behavior : entity.state().behaviors()) {
            if (!(behavior instanceof dev.arubik.craftengine.contraption.behavior.GhastFollowBehavior follow)) {
                continue;
            }
            org.bukkit.entity.Entity ghast = Bukkit.getEntity(follow.entityId());
            if (ghast != null) {
                GhastHarnessBearing.saveStructure(ghast, entity.state());
            }
            return;
        }
    }

    private static boolean ghastWantsFractureCheck(ContraptionEntity entity) {
        for (MovementBehavior behavior : entity.state().behaviors()) {
            if (behavior instanceof dev.arubik.craftengine.contraption.behavior.GhastFollowBehavior follow) {
                return follow.consumeFractureCheck();
            }
        }
        return false;
    }

    private static boolean ghastAnchorDead(ContraptionEntity entity) {
        for (dev.arubik.craftengine.contraption.MovementBehavior b : entity.state().behaviors()) {
            if (b instanceof dev.arubik.craftengine.contraption.behavior.GhastFollowBehavior follow
                    && follow.wantsDisassembleInPlace()) {
                return true;
            }
        }
        return false;
    }

    /** Whether a contraption's minecart anchor was destroyed (see {@code MinecartFollowBehavior#wantsDisassembleInPlace}). */
    private static boolean minecartAnchorDead(ContraptionEntity entity) {
        for (dev.arubik.craftengine.contraption.MovementBehavior b : entity.state().behaviors()) {
            if (b instanceof dev.arubik.craftengine.contraption.behavior.MinecartFollowBehavior follow
                    && follow.wantsDisassembleInPlace()) {
                return true;
            }
        }
        return false;
    }

    /** Whether a contraption's piston bearing has reached its extended end in a "become real blocks" mode. */
    private static boolean pistonWantsDisassemble(ContraptionEntity entity) {
        for (dev.arubik.craftengine.contraption.MovementBehavior b : entity.state().behaviors()) {
            if (b instanceof dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior piston
                    && piston.wantsDisassembleAtEnd()) {
                return true;
            }
        }
        return false;
    }

    /** Bukkit.getWorld throws under a pure-JVM unit test (no live server) — treat that the same as "not loaded." */
    private static org.bukkit.World safeGetWorld(ContraptionState state) {
        try {
            return Bukkit.getWorld(state.worldId());
        } catch (Throwable ignored) {
            return null;
        }
    }

    /**
     * Fires {@link dev.arubik.craftengine.contraption.event.ContraptionMoveEvent}. Wrapped so a
     * misbehaving listener can never break the master tick loop, and fail-open under the pure-JVM
     * unit-test path where {@code Bukkit.getPluginManager()} throws (no live server).
     */
    private static void fireMove(ContraptionEntity entity, org.bukkit.World world, Vec3 from, Vec3 to,
            double yawDelta) {
        try {
            Bukkit.getPluginManager().callEvent(
                    new dev.arubik.craftengine.contraption.event.ContraptionMoveEvent(entity, world, from, to, yawDelta));
        } catch (Throwable ignored) {
        }
    }

    private static void stepKinematics(ContraptionState state, ServerLevel level) {
        MovementContext ctx = new MovementContext(state, level);
        // Task 4/5 follow-up (CONTRAPTIONS.md 2026-07-01 session): reset the per-tick SU demand
        // accumulator BEFORE any behavior ticks, so every consumer's tick() (earlier in the
        // list) can add to it and the bearing (always ticked last, see ContraptionState#suDemand
        // field javadoc) sees the full total this same pass.
        state.resetSuDemand();
        // Capture yaw BEFORE behaviors run so we can record this tick's rotation delta afterward
        // (a ROTATIONAL bearing mutates state.yawRadians() inside its own tick, not via the
        // translational velocity path below) — fed to the standing-carry + seat rotation (2026-07-03).
        double yawBefore = state.yawRadians();
        // Capture PITCH before behaviors run too (roadmap item #9 phase 5 — TIPPING) — a phys body that tips
        // mutates state.pitchRadians() inside its own tick (via PhysicsBehavior#applyPitch), exactly as a
        // rotational spin mutates yaw — so we record this tick's pitch delta afterward for the standing/seat
        // carry. Zero for any contraption that never tips (pitchRadians stays 0), so lastPitchDelta stays 0.
        double pitchBefore = state.pitchRadians();
        // Capture ROLL before behaviors run too (roadmap item #9 phase 6 — ROLL) — a phys body that leans about
        // the horizontal Z axis mutates state.rollRadians() inside its own tick (via PhysicsBehavior#applyRoll),
        // exactly as a tip mutates pitch — so we record this tick's roll delta afterward for the standing/seat
        // carry. Zero for any contraption that never rolls (rollRadians stays 0), so lastRollDelta stays 0.
        double rollBefore = state.rollRadians();
        boolean stalled = false;
        Vec3 velocity = Vec3.ZERO;
        for (MovementBehavior behavior : state.behaviors()) {
            behavior.tick(ctx);
            if (behavior.isStalled()) {
                stalled = true;
            }
            velocity = velocity.add(behavior.velocityThisTick());
        }
        state.setStalled(stalled);
        state.setLastYawDelta(state.yawRadians() - yawBefore);
        state.setLastPitchDelta(state.pitchRadians() - pitchBefore);
        state.setLastRollDelta(state.rollRadians() - rollBefore);
        if (!stalled && !velocity.equals(Vec3.ZERO)) {
            state.setPosition(state.x() + velocity.x, state.y() + velocity.y, state.z() + velocity.z);
            state.setLastDelta(velocity.x, velocity.y, velocity.z);
        } else {
            state.setLastDelta(0, 0, 0);
        }
    }

    /**
     * The world's CraftEngine viewers, resolved at most ONCE per world per tick and shared by every
     * contraption in it. {@code CePlayers.resolve} costs O(players + online users) and allocates a list;
     * calling it per contraption made the whole render pass scale with (contraptions x players) for a
     * value that is identical across every contraption in the same world on the same tick.
     */
    private static java.util.List<net.momirealms.craftengine.core.entity.player.Player> viewersOf(
            org.bukkit.World world,
            java.util.Map<java.util.UUID, java.util.List<net.momirealms.craftengine.core.entity.player.Player>> cache) {
        return cache.computeIfAbsent(world.getUID(), k -> CePlayers.resolve(world.getPlayers()));
    }

    /**
     * The world's real players as NMS handles, resolved at most ONCE per world per tick — the carry
     * candidate list, shared for the same reason as {@link #viewersOf}. {@link ContraptionEntity#carryRiders}
     * filters seated riders out of this per contraption and never mutates it.
     */
    private static java.util.List<net.minecraft.server.level.ServerPlayer> carryCandidatesOf(
            org.bukkit.World world,
            java.util.Map<java.util.UUID, java.util.List<net.minecraft.server.level.ServerPlayer>> cache) {
        return cache.computeIfAbsent(world.getUID(), k -> {
            java.util.List<net.minecraft.server.level.ServerPlayer> candidates = new java.util.ArrayList<>();
            for (org.bukkit.entity.Player p : world.getPlayers()) {
                candidates.add(((org.bukkit.craftbukkit.entity.CraftPlayer) p).getHandle());
            }
            return candidates;
        });
    }

    private static void render(ContraptionEntity entity, org.bukkit.World world, ServerLevel realLevel,
            java.util.Map<java.util.UUID, java.util.List<net.momirealms.craftengine.core.entity.player.Player>> viewerCache,
            java.util.Map<java.util.UUID, java.util.List<net.minecraft.server.level.ServerPlayer>> carryCache,
            boolean chunkLoaded, boolean perf) {
        java.util.List<net.momirealms.craftengine.core.entity.player.Player> viewers = viewersOf(world, viewerCache);

        if (perf) {
            ContraptionPerf.countContraption(entity.cellCount());
        }

        // Nobody can see this contraption and nothing real is around it to interact with: its own chunk is
        // gone, or its world is empty. Hand the clients their entities back ONCE (see
        // ContraptionEntity#suspendRender — skipping without despawning would leave every swarm believing
        // the projection is still up) and then cost nothing per tick until that changes. `viewers` is the
        // world's real player list in both cases, which is what suspendRender requires; in the empty-world
        // case it is legitimately empty, and there is by definition nobody to strand.
        //
        // The carry/pushback passes go quiet with it. For the unloaded-chunk case that is forced anyway
        // (there are no real entities in an unloaded chunk to carry). For the empty-world case it means a
        // mob riding a contraption in a world with zero players stops being carried — accepted: nothing can
        // observe it, and carry is stateless per tick, so it simply resumes the moment someone arrives.
        if (!chunkLoaded || viewers.isEmpty()) {
            if (!entity.renderSuspended()) {
                entity.suspendRender(viewers);
            }
            if (perf) {
                if (!chunkLoaded) {
                    ContraptionPerf.countSkippedUnloaded();
                } else {
                    ContraptionPerf.countSkippedNoViewers();
                }
            }
            return;
        }
        if (entity.renderSuspended()) {
            entity.resumeRender();
        }
        if (perf) {
            ContraptionPerf.countRendered();
        }

        long rebuildStart = perf ? ContraptionPerf.begin() : 0L;

        // Live-shape bug fix: without this, a piston (or anything else) that adds/removes a
        // block inside the contraption's ContraptionLevel after initial capture would never be
        // reflected in the external display/hitbox/element-mirror swarms — rebuildSwarm() was
        // previously only ever called once, from ContraptionEntity's own constructor. Cheap when
        // nothing changed: ContraptionLevel#refreshLocalPositions early-outs on a dirty flag unless a
        // block in the level actually changed, and each swarm's rebuild() reuses existing entries
        // rather than respawning everything every tick — `viewers` is only actually used to
        // despawn a cell that truly stopped existing (see ContraptionEntity#rebuildSwarm javadoc).
        entity.rebuildSwarm(viewers);
        if (perf) {
            ContraptionPerf.end(ContraptionPerf.Phase.REBUILD, rebuildStart);
        }
        long renderStart = perf ? ContraptionPerf.begin() : 0L;

        // Real-world ambient light source (2026-07-02 session — "la luz del ambiente no esta
        // afectando el contraption... el contraption brilla" at night): see
        // ContraptionDisplaySwarm#render's own javadoc for the full root-cause writeup. `realLevel`
        // is the SAME real ServerLevel `stepKinematics` above already resolved this tick — passed
        // through so the display swarm can read the REAL block/sky light at the bearing's current
        // real-world position instead of the flat hardcoded full-bright override it used to send.
        entity.render(viewers, realLevel);
        if (perf) {
            ContraptionPerf.end(ContraptionPerf.Phase.RENDER, renderStart);
        }
        long carryStart = perf ? ContraptionPerf.begin() : 0L;

        // Player-carry RE-ENABLED (2026-07-02 session, later same day): briefly disabled under
        // the mistaken assumption that "carry" meant vehicle/passenger mounting (which really
        // does break jumping/free look/inventory — a legitimate concern). It doesn't: PlayerCarry
        // (see its own class javadoc) never calls startRiding/mount at all — it sends RELATIVE
        // (Relative.X/Y/Z only, never rotation) position-correction packets, gated on the
        // client's teleport-ack so corrections never stack and jitter. The player's own WASD/
        // jump/look input keeps working completely normally on top of the platform nudge — this
        // is exactly the same "add a delta, don't set an absolute position" approach later
        // requested again in this session, just implemented via relative-teleport-with-ack-gating
        // instead of raw Entity#setDeltaMovement (deliberately avoided per PlayerCarry's own
        // javadoc: a raw velocity add gets partially consumed/overridden by the client's own
        // movement prediction and vanilla ground friction before the next tick, which is worse
        // for a precise moving-platform ride than an acked, queued, exact-delta correction).
        entity.carryRiders(carryCandidatesOf(world, carryCache));
        entity.carrySeatedRiders();

        // Non-player carry (2026-07-02 session — "otras entidades que no sean jugador no se
        // mantienen sobre la contraption y la atraviesan"): mobs, dropped items, boats, etc.
        // standing on top of a moving contraption need carrying too, same as real players just
        // above — see ContraptionEntity#carryEntities / ContraptionHitboxSwarm#carryNearbyEntities
        // javadocs for the full design (server-authoritative direct reposition, no PlayerCarry
        // client-prediction dance needed; real players and ContraptionItemPickupSwarm's own
        // mirror ItemEntitys are excluded there to avoid double-handling).
        entity.carryEntities(((org.bukkit.craftbukkit.CraftWorld) world).getHandle());

        // Bystander wall-pushback (2026-07-02 session — see ContraptionEntity#pushBackBystanders /
        // ContraptionHitboxSwarm#pushBackNearbyBystanders javadocs): a player merely standing in an
        // approaching contraption's path (never "carried") used to clip straight through it.
        entity.pushBackBystanders(((org.bukkit.craftbukkit.CraftWorld) world).getHandle());
        // Non-player solid collision (2026-07-03 — mobs/items were phasing through the structure).
        entity.pushBackEntities(((org.bukkit.craftbukkit.CraftWorld) world).getHandle());
        if (perf) {
            ContraptionPerf.end(ContraptionPerf.Phase.CARRY, carryStart);
        }

        emitAmbientParticles(entity);
    }

    private static int ambientTickCounter = 0;

    /**
     * Manually emits the smoke/flame particles a real lit furnace/blast-furnace/smoker/campfire
     * normally shows (2026-07-02 session — "el horno encendido no muestra sus particulas
     * tampoco"). Vanilla's own ambient block particles ({@code BlockState#animateTick}) are
     * PURELY CLIENT-SIDE — driven by the client's own belief that a real block sits at that
     * position, which is never true for a packet-only mirrored cell inside a contraption; no
     * server redirect can "forward" something the server never sends in the first place. This
     * approximates the same visual by explicitly calling {@code level.addParticle(...)} for any
     * currently-{@code lit} captured block, which — unlike animateTick — genuinely is a real
     * server-broadcast API, and {@link dev.arubik.craftengine.contraption.level.ContraptionLevel
     * #addParticle} already correctly redirects it to the bearing's live real-world position (see
     * that override). Throttled to every 4 ticks (~5/sec, matches vanilla's own sparse rate) and
     * only scans the small {@code localPositions()} set, so the cost is proportional to
     * contraption size, not world size.
     */
    private static void emitAmbientParticles(ContraptionEntity entity) {
        if ((++ambientTickCounter & 3) != 0) {
            return; // once every 4 ticks
        }
        dev.arubik.craftengine.contraption.level.ContraptionLevel level = entity.state().level();
        if (level == null) {
            return;
        }
        for (BlockPos local : level.localPositions()) {
            net.minecraft.world.level.block.state.BlockState state = level.getBlockState(local);
            if (!state.hasProperty(BlockStateProperties.LIT) || !state.getValue(BlockStateProperties.LIT)) {
                continue;
            }
            double x = local.getX() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.6;
            double y = local.getY() + 0.9;
            double z = local.getZ() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.6;
            level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
            if (RANDOM.nextInt(3) == 0) {
                level.addParticle(net.minecraft.core.particles.ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
            }
        }
    }

    private static final java.util.Random RANDOM = new java.util.Random();
}
