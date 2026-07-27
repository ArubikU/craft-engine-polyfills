package dev.arubik.craftengine.contraption.behavior;

import org.joml.Vector3d;

import net.minecraft.world.phys.Vec3;

import dev.arubik.craftengine.contraption.ContraptionState;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.contraption.physics.PhysBody;
import dev.arubik.craftengine.contraption.physics.PhysicsWorld;
import dev.arubik.craftengine.contraption.physics.RigidBody;

/**
 * Marks a contraption as a PhysContraption — a free rigid body under gravity — and exposes its
 * dynamic state to the rest of the plugin.
 *
 * <h2>This behavior does not integrate anything</h2>
 * It used to: it was a hand-rolled gravity integrator that resolved collisions as a per-axis
 * positional clamp and tracked three independent Euler angular velocities. That design could not
 * tip, for a structural reason worth recording so it is not rebuilt. A whole-body axis clamp carries
 * no information about <i>where</i> the body was touched, and without a contact point there is no
 * lever arm, and without a lever arm there is no torque — only translation. The center of mass was
 * dutifully computed and then never read by anything, so making a block heavier changed nothing.
 *
 * <p>Simulation now lives in {@code contraption.physics}: {@link PhysicsWorld} steps every body in a
 * world together through {@code XpbdSolver} once per tick. It must be one call per world rather than
 * one per behavior, because two contraptions colliding must be solved simultaneously — resolving
 * them independently would have each push off a stale copy of the other and inject energy.
 *
 * <p>So {@link #tick} is deliberately empty and {@link #velocityThisTick} stays at the interface
 * default of {@link Vec3#ZERO}: {@code PhysicsWorld} writes the resolved transform straight onto
 * {@code ContraptionState}, and returning a velocity here as well would apply this tick's motion
 * twice.
 */
public class PhysicsBehavior implements MovementBehavior {

    /**
     * The simulated body, attached by {@link PhysicsWorld} on the first tick after assembly.
     * {@code null} until then, and for a contraption in an unloaded world.
     */
    private PhysBody physBody;

    private MassModel massModel = MassModel.EMPTY;

    /** This tick's solved translation of the bearing origin, handed over by {@link PhysicsWorld}. */
    private Vec3 pendingDelta = Vec3.ZERO;
    private double pendingYaw, pendingPitch, pendingRoll;
    private boolean hasPendingMotion;

    /** Wires this behavior to its solver state. Called by {@link PhysicsWorld} only. */
    public void attach(PhysBody physBody, MassModel massModel) {
        this.physBody = physBody;
        this.massModel = massModel;
    }

    /**
     * Hands this tick's solved transform over from {@link PhysicsWorld}. Called by it only, before
     * {@link #tick} runs.
     */
    public void setPendingMotion(Vec3 delta, double yawRadians, double pitchRadians, double rollRadians) {
        this.pendingDelta = delta;
        this.pendingYaw = yawRadians;
        this.pendingPitch = pitchRadians;
        this.pendingRoll = rollRadians;
        this.hasPendingMotion = true;
    }

    /**
     * Drops any unapplied motion, so this behavior reports no movement until the solver produces a
     * fresh result.
     *
     * <p>Required whenever {@link PhysicsWorld} skips a body (an unloaded world, a missing level):
     * {@link #velocityThisTick} is polled every tick regardless, so a retained delta would be
     * re-applied forever and walk the contraption across the world at its last-known speed.
     */
    public void clearPendingMotion() {
        this.pendingDelta = Vec3.ZERO;
        this.hasPendingMotion = false;
    }

    /**
     * Applies the solved ROTATION, matching how {@code RotationalBearingBehavior} works: a behavior
     * mutates the state's angles inside its own tick, and {@code ContraptionEngine#stepKinematics} —
     * which sampled the yaw BEFORE ticking behaviors — records the difference as
     * {@code lastYawDelta}. That delta is what carries a standing rider around with a spinning body,
     * so rotating the state anywhere other than inside this call would silently strand riders.
     *
     * <p>The translation is NOT applied here; it is reported via {@link #velocityThisTick} so
     * {@code stepKinematics} both moves the body and derives {@code lastDelta} from it.
     */
    @Override
    public void tick(MovementContext ctx) {
        if (!hasPendingMotion) {
            return;
        }
        ContraptionState state = ctx.state();
        state.setYawRadians(pendingYaw);
        state.setPitchRadians(pendingPitch);
        state.setRollRadians(pendingRoll);
    }

    /**
     * This tick's solved translation. Reporting it (instead of writing the position directly) is what
     * makes {@code stepKinematics} record {@code lastDelta}, which every rider-carry and
     * entity-push path reads.
     */
    @Override
    public Vec3 velocityThisTick() {
        return pendingDelta;
    }

    @Override
    public boolean isStalled() {
        return false;
    }

    /** The aggregate mass model: total mass, center of mass, and the inertia tensor about it. */
    public MassModel massModel() {
        return massModel;
    }

    /** The underlying body, or {@code null} before {@link PhysicsWorld} has attached one. */
    public PhysBody physBody() {
        return physBody;
    }

    /** Linear velocity of the center of mass, blocks per tick. */
    public Vec3 linearVelocity() {
        RigidBody body = body();
        return body == null ? Vec3.ZERO
                : new Vec3(body.linearVelocity.x, body.linearVelocity.y, body.linearVelocity.z);
    }

    /** Angular velocity, world frame, radians per tick. */
    public Vec3 angularVelocity() {
        RigidBody body = body();
        return body == null ? Vec3.ZERO
                : new Vec3(body.angularVelocity.x, body.angularVelocity.y, body.angularVelocity.z);
    }

    /** True when the body has settled and the solver has put it to sleep. */
    public boolean isResting() {
        return physBody != null && physBody.isAsleep();
    }

    /**
     * Applies an impulse at a world-space point — the general force API, and the only one needed.
     *
     * <p>There are no longer separate yaw/pitch/roll torque entry points. They existed because the
     * old engine tracked three unrelated scalar spins; a real body has one angular velocity vector,
     * and an impulse at a point produces exactly the right mix of translation and rotation on its
     * own via {@code r × p}. An impulse through the center of mass purely translates; the further out
     * it lands, the more it spins — which is the physics, not a special case.
     */
    public void applyImpulseAt(Vec3 worldPoint, Vec3 impulse) {
        RigidBody body = body();
        if (body == null) {
            return;
        }
        Vector3d p = new Vector3d(impulse.x, impulse.y, impulse.z);
        Vector3d r = new Vector3d(worldPoint.x, worldPoint.y, worldPoint.z).sub(body.position);
        body.linearVelocity.fma(body.inverseMass(), p);
        Vector3d torque = new Vector3d(r).cross(p);
        body.angularVelocity.add(new org.joml.Matrix3d(body.inverseInertiaWorld()).transform(torque));
        physBody.wakeUp();
    }

    /** Applies an impulse through the center of mass — pure translation, no spin. */
    public void applyImpulse(Vec3 impulse) {
        RigidBody body = body();
        if (body == null) {
            return;
        }
        body.linearVelocity.fma(body.inverseMass(), new Vector3d(impulse.x, impulse.y, impulse.z));
        physBody.wakeUp();
    }

    private RigidBody body() {
        return physBody == null ? null : physBody.body;
    }
}
