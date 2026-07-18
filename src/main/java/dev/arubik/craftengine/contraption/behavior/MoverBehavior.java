package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.ContraptionMath;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.rotation.RpmConsumer;
import net.minecraft.world.phys.Vec3;

/**
 * Self-propelled propulsion block (ROADMAP-world-boundary.md §2, Interpretation A — the
 * "mover" / self-moving chassis / thruster). A {@code polyfills:mover_block} captured inside
 * a contraption contributes a per-tick world-space push along its own {@code facing}, exactly
 * the way {@link MinerBehavior} made <em>mining</em> a captured-block property: it is
 * auto-attached at assembly by {@code ContraptionCapture#resolveAutoBehaviors} via
 * {@link MovementBehaviorRegistry} (see {@link MoverBlockBehavior#buildMovementBehavior}), and
 * every mover's {@link #velocityThisTick()} is summed into the bearing's position by
 * {@code ContraptionEngine.stepKinematics} (which already adds every behavior's velocity —
 * multiple movers therefore stack naturally).
 *
 * <p><b>Scope / "one bearing per contraption" (the design's §2 caveat, resolved for MVP).</b>
 * A mover is deliberately <em>only an augmenting</em> captured behavior — it never assembles a
 * contraption by itself and adds NO new {@code BearingType}/assembly trigger. A normal bearing
 * (LINEAR/ROTATIONAL/MINECART) still assembles the glued structure via the existing
 * {@code BearingHammerListener} path; any mover blocks glued inside it then add propulsion on
 * top. Because the bearing remains the sole assembler, CONTRAPTIONS.md §7's "exactly one of
 * LINEAR/ROTATIONAL/MINECART kinematics per contraption" rule is untouched here — a mover is a
 * plain velocity contributor, not a fourth kinematics <em>source</em> that needs its own anchor.
 *
 * <p><b>Direction &amp; rotation.</b> {@link #localDir} is the block's facing captured in the
 * contraption's own local (yaw-0) basis. Every tick it is rotated into the contraption's CURRENT
 * real-world orientation with {@link ContraptionMath#rotateYaw} before being scaled — so a mover
 * riding a spinning ROTATIONAL bearing keeps pushing along its true world heading as the
 * structure turns, and on a non-rotating (LINEAR/MINECART) contraption the yaw stays 0 and the
 * rotation is a no-op.
 *
 * <p><b>Power model (mirrors {@link MinerBehavior} exactly).</b> This implements
 * {@link RpmConsumer} and reads a fresh input rpm each tick, preferring its OWN locally-fed rpm
 * ({@link #getInputRpm()}, set via {@link #setInputRpm} — e.g. a real motor captured adjacent to
 * it, or the driving bearing's retransmit loop in {@code PistonBearingBehavior}/
 * {@code RotationalBearingBehavior}) over the bearing's {@code globalRpm()} feed, exactly the
 * local-over-global priority the miner documents.
 * <ul>
 *   <li>When {@link #requiresPower} is {@code true} the mover is rpm-gated like the miner:
 *   effective rpm ({@code inputRpm * gearRatio}) must reach {@link #minRpm} or the mover is
 *   INERT (contributes zero velocity) — and, like the miner, an inert mover reports
 *   {@link #isStalled()} {@code false} so a powerless thruster can never permanently deadlock
 *   the contraption's movement (a LINEAR bearing produces no in-structure rpm, so a
 *   {@code requiresPower} mover riding one is inert-by-design until something feeds it).</li>
 *   <li>When {@link #requiresPower} is {@code false} (the MVP default) the mover is a
 *   <em>constant push</em>: it always contributes {@code speed} blocks/sec along its facing
 *   whenever the contraption is not otherwise stalled — a genuinely self-propelled chassis that
 *   works the moment it is captured, with no motor required. This is the deliberate "constant
 *   push for MVP" choice the roadmap allows; flip {@code requiresPower: true} in the block YAML
 *   for the fully rpm/SU-gated behavior.</li>
 * </ul>
 * Whenever it actually pushes, its {@link #suCost} is added to the contraption's per-tick SU
 * demand via {@code ContraptionState#addSuDemand} — the same finite-resource pool the bearing
 * (ticked last) reports to its real external motor, so a heavier thruster load raises the load
 * reported upstream exactly like a captured miner's does.
 *
 * <p><b>Stall vote.</b> A mover never stalls the contraption ({@link #isStalled()} is always
 * {@code false} — same as {@link PistonBearingBehavior}); when SOME OTHER behavior stalls (e.g. a
 * captured miner hitting bedrock), the engine already zeroes the summed velocity for that tick,
 * so the mover's contribution is withheld automatically without the mover having to vote.
 */
public final class MoverBehavior implements MovementBehavior, RpmConsumer {

    /** Unit push direction in the contraption's LOCAL (yaw-0) basis — rotated into world space each tick. */
    private final Vec3 localDir;
    /** Push speed in blocks/sec (world-space translation this behavior wants to contribute). */
    private final double speedBlocksPerSec;
    /** Multiplier applied to whatever rpm is delivered via {@link #setInputRpm} (mirrors {@code MinerBehavior#gearRatio}). */
    private final double gearRatio;
    /** Effective rpm must reach this before a {@link #requiresPower} mover pushes at all (0 disables the floor). */
    private final double minRpm;
    /** Stress units this mover adds to the contraption's per-tick SU demand while pushing. */
    private final double suCost;
    /** When true the mover is rpm-gated (miner-style); when false it is a constant self-propelled push. */
    private final boolean requiresPower;

    private float inputRpm;
    private Vec3 velocityPerTick = Vec3.ZERO;
    /**
     * On/off switch (2026-07-04 user request — "haz que el mover se pueda apagar o desactivar").
     * Captured from the block's {@code enabled} state at assembly (see
     * {@link MoverBlockBehavior#buildMovementBehavior}); a disabled mover is completely INERT
     * (contributes no velocity, draws no SU), same no-op as a mover on a pinned bearing. Live-
     * togglable via {@link #setEnabled} so a running contraption can switch a mover off/on.
     */
    private boolean enabled;

    public MoverBehavior(Vec3 localDirectionUnit, double speedBlocksPerSec, double gearRatio, double minRpm,
            double suCost, boolean requiresPower, boolean enabled) {
        this.localDir = localDirectionUnit.normalize();
        this.speedBlocksPerSec = speedBlocksPerSec;
        this.gearRatio = gearRatio;
        this.minRpm = minRpm;
        this.suCost = suCost;
        this.requiresPower = requiresPower;
        this.enabled = enabled;
    }

    /** Convenience for callers that don't care about the power gate (tests, tools): constant push, no minRpm/suCost, enabled. */
    public MoverBehavior(Vec3 localDirectionUnit, double speedBlocksPerSec) {
        this(localDirectionUnit, speedBlocksPerSec, 1.0, 0, 0, false, true);
    }

    /** Whether this mover is currently switched on (see {@link #enabled}). */
    public boolean isEnabled() {
        return enabled;
    }

    /** Live on/off toggle for a running contraption's mover — a disabled mover is fully inert. */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void tick(MovementContext ctx) {
        // DEMO / minecart-only (2026-07-04 user feedback — "el mover block ... las contraption
        // exceptuando la de minecart estaran estaticas en un punto ... por el linear/rotation
        // bearing"): a mover only makes sense on a FREE-TRANSLATING contraption, i.e. one driven by a
        // MINECART bearing. LINEAR (extend-along-axis) and ROTATIONAL (spin-in-place) bearings PIN the
        // structure to a fixed anchor, so a translational push there is not just useless — its summed
        // velocity would DRIFT the whole contraption off its bearing. So the mover is INERT unless a
        // MinecartFollowBehavior is present on this contraption; on a pinned bearing it contributes
        // nothing (a demo/no-op), which is the intended scope until a truly free-moving non-minecart
        // contraption type exists.
        boolean freeMoving = false;
        for (MovementBehavior b : ctx.state().behaviors()) {
            if (b instanceof MinecartFollowBehavior) {
                freeMoving = true;
                break;
            }
        }
        if (!freeMoving) {
            velocityPerTick = Vec3.ZERO;
            return;
        }

        // Switched off (2026-07-04 — "que se pueda apagar o desactivar"): fully inert, no push, no SU.
        if (!enabled) {
            velocityPerTick = Vec3.ZERO;
            return;
        }

        // Local-over-global rpm priority, identical to MinerBehavior#tick: prefer this mover's OWN
        // locally-fed rpm over the bearing's globally-fed rpm, falling back to the global feed only
        // when nothing local is driving it.
        float rpmToUse = inputRpm > 0 ? inputRpm : ctx.state().globalRpm();
        double effectiveRpm = rpmToUse * gearRatio;

        if (requiresPower && (effectiveRpm <= 0 || effectiveRpm < minRpm)) {
            // Underpowered — INERT, not stalled (see class javadoc): a powerless thruster must never
            // permanently deadlock the contraption's movement, exactly as a powerless miner doesn't.
            velocityPerTick = Vec3.ZERO;
            return;
        }

        // Pushing this tick: add our SU demand to the shared per-tick pool the bearing reports to its
        // real external motor (same finite-resource loop MinerBehavior participates in).
        ctx.state().addSuDemand((float) suCost);

        double perTick = Math.max(0.0, speedBlocksPerSec) / 20.0;
        // Carry the local facing into the contraption's current real-world orientation (no-op when the
        // contraption isn't rotating) before scaling — see class javadoc "Direction & rotation".
        Vec3 worldDir = ContraptionMath.rotateYaw(localDir, ctx.state().yawRadians());
        velocityPerTick = worldDir.scale(perTick);
    }

    @Override
    public boolean isStalled() {
        return false;
    }

    @Override
    public Vec3 velocityThisTick() {
        return velocityPerTick;
    }

    /** Unit push direction in the contraption's local (yaw-0) basis — exposed for tests/render. */
    public Vec3 localDirection() {
        return localDir;
    }

    /** Configured push speed in blocks/sec. */
    public double speedBlocksPerSec() {
        return speedBlocksPerSec;
    }

    // ---------------- RpmConsumer ----------------

    @Override
    public void setInputRpm(float rpm) {
        this.inputRpm = rpm;
    }

    @Override
    public float getInputRpm() {
        return inputRpm;
    }
}
