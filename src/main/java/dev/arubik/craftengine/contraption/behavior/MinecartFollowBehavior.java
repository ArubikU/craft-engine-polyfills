package dev.arubik.craftengine.contraption.behavior;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import net.minecraft.world.phys.Vec3;

/**
 * MINECART bearing kinematics (CONTRAPTIONS.md Phase 6 "Minecart bearing"): rather than
 * contributing its own velocity like {@link LinearActuatorBehavior}, this behavior just
 * reads the position/yaw of a REAL, already-existing vanilla minecart entity every tick and
 * feeds the contraption's {@code ContraptionState} the DELTA since last tick — the minecart's
 * own unmodified vanilla rail physics is what actually drives movement (no custom
 * {@code EntityType}, no reimplemented rail-following math). {@link #isStalled()} reports true
 * (freezing geographic movement for that tick, exactly like any other stalled behavior) only
 * when the anchor entity can't currently be resolved (e.g. its chunk isn't loaded) — internal
 * state elsewhere on the contraption still advances per the master-clock contract.
 *
 * <p><b>Delta, not absolute snap (2026-07-02 live-test fix — "el bearing del minecart no agarra
 * el sistema de carry", "al girar rota los bloques").</b> An earlier version of this class called
 * {@code ctx.state().setPosition(...)}/{@code setYawRadians(...)} directly inside {@link #tick},
 * bypassing {@code ContraptionEngine#stepKinematics}'s own position-application path entirely.
 * Since {@link #velocityThisTick()} always returned {@link Vec3#ZERO}, {@code stepKinematics}
 * always saw a zero summed velocity and took its "stalled/no movement" branch — which
 * unconditionally zeroes {@code ContraptionState#lastDeltaX/Y/Z} every tick, regardless of how far
 * the minecart actually moved. Every carry consumer ({@code ContraptionHitboxSwarm}/
 * {@code PlayerCarry}) reads exactly that value to know how far to push a rider, so with it
 * permanently zero nothing could ever be carried — matching the reported symptom exactly. The
 * directly-set yaw had a similar problem: snapping the absolute (jittery, vanilla-rail-driven) yaw
 * onto the state every tick with no smoothing, on top of a position that was never actually being
 * moved through the engine's own normal per-tick delta path, produced a visibly janky "the
 * structure snaps/rotates in place" look on curves rather than the smooth continuous-yaw rotation
 * {@code /cep contraption rotate} (a plain {@code state.setYawRadians(state.yawRadians() +
 * delta)} — see {@code HologramTest#rotateYawDegrees}) already demonstrates working correctly.
 *
 * <p>Fix: this behavior now only RECORDS the minecart's position/yaw each tick (in
 * {@code lastX}/{@code lastY}/{@code lastZ}/{@code lastYawRadians}) and returns the REAL per-tick
 * delta from {@link #velocityThisTick()} — letting {@code stepKinematics}'s own
 * {@code state.setPosition(state.x() + velocity.x, ...)} path do the actual position update
 * (correctly populating {@code lastDelta} in the process, exactly like every other behavior).
 * Yaw is advanced the same
 * "continuous += delta" way {@code HologramTest#rotateYawDegrees}/{@code /cep contraption rotate}
 * already use, computed as the shortest angular difference between this tick's and last tick's
 * minecart yaw (so a wrap-around near +-180 degrees doesn't cause a full-circle jump). The first
 * tick after (re)acquiring the entity has no previous sample yet, so it records the initial
 * position/yaw and contributes zero velocity/rotation that one tick only.
 *
 * <p><b>Y offset (bug — "el bearing esta a la altura del minecart, debe estar 1 bloque
 * encima").</b> {@code MinecartBearing#assemble} captures the glued structure with the BEARING
 * BLOCK's own position as local origin — that block always sat one block ABOVE the rail
 * (enforced by the {@code below.getBlock() instanceof BaseRailBlock} check), and the minecart is
 * spawned at that same Y. Once the minecart settles onto the rail under its own vanilla physics,
 * its resting Y ends up close to the RAIL's Y (one block lower than where it was originally
 * spawned) — so directly copying {@code loc.getY()} into the state would render the whole
 * captured structure one block too low, matching the reported symptom exactly. Adding a fixed
 * {@link #Y_OFFSET} of {@code +1.0} block when sampling the minecart's position restores the
 * original bearing-block-to-rail relationship the capture assumed.
 *
 * <p>Yaw mapping (Bukkit yaw, degrees, clockwise from south) to this project's
 * {@code yawRadians} convention (see {@code ContraptionMath}) is a direct negated
 * conversion — approximate, not yet visually verified against a live client (one of the
 * still-open Phase-0 spikes needs a human in a live client anyway).
 *
 * <p><b>Perpendicular-axis mirror correction (2026-07-02 live-test report — "cuando un
 * minecart contraption construido en el eje west-east gira a el eje south-north ... termina
 * mirando al otro lado ... si giro a la izquierda el modelo gire a la derecha, y viceversa;
 * esto no debe afectar cuando vuelve al mismo sentido").</b> A vanilla minecart's rendered yaw
 * ({@code getYRot()}) is NOT a continuous 360-degree heading — see the decompiled
 * {@code OldMinecartBehavior}: each tick it recomputes {@code yaw = atan2(zo - z, xo - x)}
 * (note: PREVIOUS minus CURRENT position, i.e. the negated travel direction) and then applies a
 * FLIP clamp: {@code if wrapDegrees(yaw - yRotO) is within 10 degrees of a full 180-degree
 * reversal, add 180 and toggle a 'flipped' flag}. That flip keeps the cart's yaw from ever
 * swinging a near-180 reversal (so a cart that reverses on a STRAIGHT rail — e.g. east then west
 * on the same west-east track — reports the SAME yaw both ways instead of a 180 flip), which is
 * exactly why the capture ("parallel") axis always looks fine: the raw yaw this behavior reads
 * stays put at ~0 on that axis regardless of travel direction. But on the PERPENDICULAR axis the
 * geometry works out (verified by decompiling the yaw math and simulating all four cardinal cases)
 * so that the raw yaw this behavior accumulates is the exact NEGATION of the physically-correct
 * 90-degree turn: a cart captured going east that curves north makes the contraption's raw yaw go
 * +90 when it should be -90 (and every other perpendicular case is likewise mirrored). That is the
 * "mira al otro lado / gira al lado contrario" the user sees. The parallel case never accumulates
 * that error (raw yaw stays ~0), and a full round trip out to the perpendicular axis and back to
 * the capture axis self-cancels to ~0 — matching "cuando vuelve a una ruta west-east se alinea
 * bien" exactly.
 *
 * <p>Fix (the user's own "usa mates"): each tick we keep accumulating the raw minecart-derived
 * contraption yaw ({@link #rawAccumYaw}) relative to the capture reference ({@link #refAccumYaw},
 * recorded on the first tick), classify whether the cart is currently on the capture axis
 * (accumulated turn within +-45 of 0/180) or the perpendicular axis (within +-45 of +-90), and on
 * the perpendicular axis feed the MIRRORED yaw ({@code ref - rel} instead of {@code ref + rel},
 * i.e. reflect the accumulated turn across the capture axis) so the contraption turns the correct
 * way. The parallel axis is fed the raw yaw unchanged, so the already-correct west-east look is
 * untouched. The corrected value is an absolute TARGET the existing per-tick smoothing
 * ({@link #MAX_YAW_STEP_RADIANS}) now drives {@code ContraptionState}'s yaw toward, so even the
 * one-tick target jump at the exact classification boundary is spread into a continuous ramp
 * rather than a visible 180-degree pop.
 */
public final class MinecartFollowBehavior implements MovementBehavior {

    /** See class javadoc "Y offset". */
    private static final double Y_OFFSET = 1.0;

    /**
     * A vanilla minecart entity rests ~1/16 block ABOVE the rail it sits on (its own model/position
     * offset), so {@code loc.getY()} is {@code railY + 0.0625}, not {@code railY} (2026-07-03 — "el
     * minecart sube el display unos centimetros encima de lo que es un bloque normal ... +1/16 de la
     * altura de un bloque"). Subtracting it when sampling snaps the whole contraption down onto the
     * clean block grid instead of floating 1/16 high above it — the display top then lines up exactly
     * with a normal block's top face (so the shulker-collider floor a rider stands on does too).
     */
    private static final double MINECART_RAIL_OFFSET = 0.0625;

    /**
     * Yaw smoothing over rail curves (follow-up to a live-test question about the INTERACTION/
     * shulker/render swarms looking "desordenadas" mid-curve). Every render swarm reads the same
     * {@code state.yawRadians()} snapshot in one synchronous render() call each tick, so they can
     * never disagree with EACH OTHER about the current transform -- there's no per-swarm caching
     * or staggering that could desync them. The actual disorder came from the delta bug fixed
     * above: while lastDelta was always zero, a rider standing on the structure never moved with
     * it, so the structure (reading live state directly) would snap to a new pose every tick while
     * the rider stayed put, visibly falling out of sync. That's fixed by the delta change above.
     * Separately, a real vanilla minecart's reported yaw does not change in the tiny, smooth,
     * every-tick increments a scripted RotationalBearingBehavior uses -- traversing a curved rail
     * piece advances it in visibly chunkier steps. Even with the delta bug fixed, snapping the
     * whole contraption's yaw instantly to match would still look like a jarring snap-rotation
     * rather than a smooth turn. MAX_YAW_STEP_RADIANS caps how much yaw this behavior feeds into
     * ContraptionState per tick; the corrected absolute target (see the class javadoc's
     * "Perpendicular-axis mirror correction" section) is approached at most this fast per tick, so
     * the contraption's own yaw always advances in small continuous steps regardless of how abruptly
     * the real minecart's yaw (or the parallel/perpendicular classification) changes underneath it --
     * matching the smooth look /cep contraption rotate already has.
     */
    private static final double MAX_YAW_STEP_RADIANS = Math.toRadians(9.0);

    /** One cardinal quarter-turn (PI/2), the boundary spacing used to classify parallel vs perpendicular axis — see class javadoc. */
    private static final double QUARTER_TURN = Math.PI / 2.0;

    private final UUID entityId;
    private boolean missing;
    private boolean hasLastSample;
    private double lastX, lastY, lastZ;
    private double lastYawRadians;
    private Vec3 pendingVelocity = Vec3.ZERO;
    /**
     * Raw contraption yaw (radians) accumulated from the minecart's per-tick yaw deltas in the same
     * continuous "+= shortestDelta" space, BEFORE the perpendicular-axis mirror correction — kept as
     * the classification/mirror basis (see class javadoc). {@link #refAccumYaw} is its value at the
     * moment the entity was (re)acquired, i.e. the capture-axis reference the mirror reflects across.
     */
    private double rawAccumYaw;
    private double refAccumYaw;

    public MinecartFollowBehavior(UUID entityId) {
        this.entityId = entityId;
    }

    @Override
    public void tick(MovementContext ctx) {
        Entity entity = Bukkit.getEntity(entityId);
        if (entity == null || !entity.isValid()) {
            missing = true;
            pendingVelocity = Vec3.ZERO;
            return;
        }
        missing = false;
        Location loc = entity.getLocation();
        double x = loc.getX();
        double y = loc.getY() + Y_OFFSET - MINECART_RAIL_OFFSET;
        double z = loc.getZ();
        double yawRadians = Math.toRadians(-loc.getYaw());

        if (!hasLastSample) {
            // First tick after (re)acquiring the entity — no previous sample to diff against yet.
            // Record it and contribute no movement/rotation THIS tick only (see class javadoc).
            // The raw-yaw accumulator anchors to the state's current yaw (captures always start at
            // yaw 0, per MinecartBearing#assemble), and that same value is the capture-axis reference
            // the perpendicular-axis mirror reflects across (see class javadoc).
            hasLastSample = true;
            lastX = x;
            lastY = y;
            lastZ = z;
            lastYawRadians = yawRadians;
            rawAccumYaw = ctx.state().yawRadians();
            refAccumYaw = rawAccumYaw;
            // Anchor the state's ABSOLUTE position to the minecart on the very first tick, so BOTH
            // the fresh-assemble path AND the chunk-load rehydrate path converge to the exact same
            // minecart-centered anchor regardless of what ContraptionState was initialised to
            // (2026-07-02 "el minecart bearing sigue undiendose" + 2026-07-03 "al cargar el minecart
            // bearing se desaliena y no queda alineado al centro del minecart como cuando se arma").
            //
            // The per-tick position is DELTA-based, so any CONSTANT offset baked into every sample
            // cancels in the `now - last` deltas AND was previously excluded from this first tick
            // (zero contribution) — which is why (a) Y_OFFSET did nothing and the contraption sank as
            // the freshly-spawned cart settled onto the rail below, and (b) rehydrate kept whatever
            // absolute X/Z the state was constructed with (MinecartBearing#rehydrate seeds it from the
            // minecart's own float CENTRE location, not the block corner assemble uses), leaving the
            // structure ~0.5 off-centre from the cart after a reload.
            //
            // Fix: emit the FULL absolute correction as this first tick's delta so the state snaps to
            // {@code (loc.x - 0.5, loc.y + Y_OFFSET, loc.z - 0.5)} — the bearing cell's own render
            // (renderPosition of local (0,0,0)'s centre, +PIVOT_XZ) then lands exactly on the
            // minecart's centre (state.x + 0.5 == loc.x) and one block above it. `x`/`y`/`z` already
            // fold in Y_OFFSET; the -0.5 on X/Z is the block-corner-vs-entity-centre centring offset.
            // Every later delta tracks raw cart movement from that correct, consistent anchor.
            pendingVelocity = new Vec3((x - 0.5) - ctx.state().x(), y - ctx.state().y(), (z - 0.5) - ctx.state().z());
            return;
        }

        pendingVelocity = new Vec3(x - lastX, y - lastY, z - lastZ);
        rawAccumYaw += shortestAngleDelta(lastYawRadians, yawRadians);
        driveYawTowards(ctx, correctedYaw());

        lastX = x;
        lastY = y;
        lastZ = z;
        lastYawRadians = yawRadians;
    }

    /**
     * The absolute yaw the contraption SHOULD be at this tick given the raw accumulated minecart yaw
     * (see class javadoc's "Perpendicular-axis mirror correction"): on the capture axis it's the raw
     * yaw unchanged (so west-east stays exactly as it looked before this fix); on the perpendicular
     * axis it's the raw turn MIRRORED across the capture-axis reference ({@code ref - rel} instead of
     * {@code ref + rel}), undoing vanilla's negated-quarter-turn quirk.
     */
    private double correctedYaw() {
        double rel = rawAccumYaw - refAccumYaw;
        // Distance of |rel| from the nearest quarter-turn multiple: perpendicular when |rel mod PI|
        // lands within PI/4 of PI/2 (i.e. closer to a +-90 turn than to a 0/180 turn).
        double modHalf = Math.abs(rel) % Math.PI;                 // in [0, PI)
        boolean perpendicular = Math.abs(modHalf - QUARTER_TURN) < (QUARTER_TURN / 2.0);
        return perpendicular ? (refAccumYaw - rel) : rawAccumYaw;
    }

    /** See {@link #MAX_YAW_STEP_RADIANS} javadoc — advances {@code ContraptionState}'s yaw toward {@code targetYaw} by at most one capped step this tick, along the shortest arc. */
    private void driveYawTowards(MovementContext ctx, double targetYaw) {
        double need = shortestAngleDelta(ctx.state().yawRadians(), targetYaw);
        double step = Math.max(-MAX_YAW_STEP_RADIANS, Math.min(MAX_YAW_STEP_RADIANS, need));
        if (step == 0) {
            return;
        }
        ctx.state().setYawRadians(ctx.state().yawRadians() + step);
    }

    /** Shortest signed angular difference from {@code from} to {@code to}, both radians, result in {@code (-PI, PI]}. */
    private static double shortestAngleDelta(double from, double to) {
        double delta = (to - from) % (Math.PI * 2);
        if (delta > Math.PI) {
            delta -= Math.PI * 2;
        } else if (delta < -Math.PI) {
            delta += Math.PI * 2;
        }
        return delta;
    }

    @Override
    public boolean isStalled() {
        return missing;
    }

    @Override
    public Vec3 velocityThisTick() {
        return pendingVelocity;
    }
}
