package dev.arubik.craftengine.contraption.physics;

import org.joml.Quaterniond;
import org.joml.Vector3d;

import net.minecraft.world.phys.AABB;

/**
 * One simulated entry: a {@link RigidBody}'s dynamic state, its {@link CollisionShape} geometry, and
 * the {@link WorldBlockCache} snapshot of the terrain around it.
 *
 * <p>Bundled so {@link XpbdSolver} takes a flat list and touches nothing else — no level, no Bukkit
 * handle, no contraption. The solver is therefore a pure function of these values, which is what lets
 * it be unit-tested without a server and, later, run off-thread without a data race.
 */
public final class PhysBody {

    public final RigidBody body = new RigidBody();

    /** Merged, COM-relative geometry. Rebuilt when the contraption's cells change. */
    public CollisionShape shape = CollisionShape.EMPTY;

    /** Terrain snapshot. Re-baked only when {@link #bakedRegion} no longer covers where the body can reach. */
    public WorldBlockCache world = WorldBlockCache.EMPTY;

    /**
     * The world region {@link #world} was baked from, or {@code null} if it must be re-baked.
     *
     * <p>Baking is the single most expensive main-thread thing physics does — a full block-state,
     * collision-shape and fluid-state read for every cell of a region that scales with the body's size
     * CUBED. Doing it every tick for every body is what a server actually feels.
     *
     * <p>But terrain does not change most ticks, and a body usually has not left the region it was
     * baked against. So the bake is kept until one of two things is true: the body can now reach
     * outside the region (checked here), or the world inside it changed (pushed in by
     * {@link PhysicsWorld#wakeNear}). A slow or resting body re-bakes almost never; a fast one still
     * re-bakes as often as it genuinely must.
     */
    public net.minecraft.world.phys.AABB bakedRegion;

    /** Drops the terrain snapshot, forcing a fresh bake on the next step. */
    public void invalidateBake() {
        bakedRegion = null;
    }

    /**
     * How much gravity this body keeps in a fluid — the mass-weighted mean of its cells' floatability
     * (see {@link FloatabilityModel}). Compared against the fluid's own buoyancy in
     * {@code XpbdSolver#applyBuoyancy}; it does NOT affect gravity in air.
     */
    public double floatability = FloatabilityTable.DEFAULT_FLOATABILITY;

    /** When true the solver skips this body entirely (held by the wand, stalled, or asleep). */
    public boolean kinematic;

    /**
     * Ticks of active-thruster self-levelling remaining (2026-07-19 — "aunque los 4 fans estén al mismo
     * overclock tarde o temprano se voltea la plataforma"). A fan thrust sets this; while it is positive the
     * solver gently levels the body toward world-up but ONLY within a cone (see {@code XpbdSolver
     * #SELF_RIGHT_CONE_COS}), so an all-same-overclock platform holds level against the imbalance torque an
     * off-centre payload (tanks on one side) would otherwise tip it with, yet a deliberate hard off-centre
     * thrust that pushes past the cone still rolls it clean over. Zero for a static/unpowered body, which
     * therefore keeps whatever orientation it was left in — a wand-flipped contraption stays flipped, no
     * spurious auto-upright.
     */
    public int selfRightTicks;

    /** Ticks this body has been below the rest thresholds — see {@link XpbdSolver#SLEEP_TICKS}. */
    int restTicks;

    /** Substep scratch: position and orientation at the start of the current substep. */
    final Vector3d previousPosition = new Vector3d();
    final Quaterniond previousOrientation = new Quaterniond();

    /**
     * Hardest closing speed seen at any contact during the current tick, in blocks per tick, and where
     * it happened. Recorded by {@link XpbdSolver} — which stays pure, it only writes a number here — and
     * consumed AFTER the step by whoever needs to react to a crash in the world (see
     * {@code ContraptionImpactDetonator}). Zeroed at the start of every {@link XpbdSolver#step}, so a
     * reader always sees this tick's answer and never a stale one.
     */
    private double maxImpactSpeed;
    private final Vector3d impactPoint = new Vector3d();
    private boolean impacted;

    /** Clears the impact record. Called once per tick by the solver before any contact is generated. */
    void resetImpact() {
        maxImpactSpeed = 0.0;
        impacted = false;
    }

    /** Keeps the hardest of the closing speeds offered this tick, and the point it was measured at. */
    void recordImpact(double closingSpeed, Vector3d worldPoint) {
        if (closingSpeed <= maxImpactSpeed) {
            return;
        }
        maxImpactSpeed = closingSpeed;
        impactPoint.set(worldPoint);
        impacted = true;
    }

    /** Hardest closing speed at any contact this tick, blocks per tick. Zero when nothing hit this body. */
    public double maxImpactSpeed() {
        return maxImpactSpeed;
    }

    /**
     * World point of {@link #maxImpactSpeed}, or {@code null} if no contact was recorded this tick.
     * Returned instance is this body's own field — copy it before the next step.
     */
    public Vector3d impactPoint() {
        return impacted ? impactPoint : null;
    }

    /**
     * World-space bounds of this body's geometry at its current transform, inflated by {@code margin}.
     * Uses the bounding sphere rather than a rotated box union — a sphere is orientation-invariant, so
     * this cannot under-cover as the body spins, and over-covering only costs a slightly larger bake.
     */
    public AABB worldBounds(double margin) {
        double r = shape.boundingRadius() * body.scale() + margin;
        return new AABB(
                body.position.x - r, body.position.y - r, body.position.z - r,
                body.position.x + r, body.position.y + r, body.position.z + r);
    }

    /** True when this body is asleep — at rest long enough that the solver may skip it. */
    public boolean isAsleep() {
        return restTicks >= XpbdSolver.SLEEP_TICKS;
    }

    /** Clears the rest counter. Call whenever anything disturbs the body (a push, a wand grab, a cell change). */
    public void wakeUp() {
        restTicks = 0;
    }
}
