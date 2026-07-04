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
        // Euler/robin_euler "extended-solid" bearings (dropped their load as real blocks) aren't live
        // contraptions, so they're ticked separately: a redstone pulse / dwell timer re-assembles +
        // retracts them (2026-07-03 goal). See EulerExtendedRegistry.
        try {
            EulerExtendedRegistry.tick();
        } catch (Throwable ignored) {
        }
        java.util.List<ContraptionEntity> disassembleAtEnd = null;
        for (ContraptionEntity entity : ContraptionManager.all()) {
            org.bukkit.World bukkitWorld = safeGetWorld(entity.state());
            ServerLevel level = bukkitWorld != null ? ((CraftWorld) bukkitWorld).getHandle() : null;

            stepKinematics(entity.state(), level);

            if (bukkitWorld != null) {
                try {
                    render(entity, bukkitWorld, level);
                } catch (Throwable ignored) {
                    // Keep other contraptions ticking even if one viewer/render call misbehaves.
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
        if (!stalled && !velocity.equals(Vec3.ZERO)) {
            state.setPosition(state.x() + velocity.x, state.y() + velocity.y, state.z() + velocity.z);
            state.setLastDelta(velocity.x, velocity.y, velocity.z);
        } else {
            state.setLastDelta(0, 0, 0);
        }
    }

    private static void render(ContraptionEntity entity, org.bukkit.World world, ServerLevel realLevel) {
        java.util.List<net.momirealms.craftengine.core.entity.player.Player> viewers =
                CePlayers.resolve(world.getPlayers());

        // Live-shape bug fix: without this, a piston (or anything else) that adds/removes a
        // block inside the contraption's ContraptionLevel after initial capture would never be
        // reflected in the external display/hitbox/element-mirror swarms — rebuildSwarm() was
        // previously only ever called once, from ContraptionEntity's own constructor. Cheap when
        // nothing changed: ContraptionLevel#refreshLocalPositions only scans the currently-tracked
        // cells + their immediate neighbors, and each swarm's rebuild() reuses existing entries
        // rather than respawning everything every tick — `viewers` is only actually used to
        // despawn a cell that truly stopped existing (see ContraptionEntity#rebuildSwarm javadoc).
        entity.rebuildSwarm(viewers);

        // Real-world ambient light source (2026-07-02 session — "la luz del ambiente no esta
        // afectando el contraption... el contraption brilla" at night): see
        // ContraptionDisplaySwarm#render's own javadoc for the full root-cause writeup. `realLevel`
        // is the SAME real ServerLevel `stepKinematics` above already resolved this tick — passed
        // through so the display swarm can read the REAL block/sky light at the bearing's current
        // real-world position instead of the flat hardcoded full-bright override it used to send.
        entity.render(viewers, realLevel);

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
        java.util.List<net.minecraft.server.level.ServerPlayer> candidates = new java.util.ArrayList<>();
        for (org.bukkit.entity.Player p : world.getPlayers()) {
            candidates.add(((org.bukkit.craftbukkit.entity.CraftPlayer) p).getHandle());
        }
        entity.carryRiders(candidates);
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
