package dev.arubik.craftengine.contraption.physics;

import org.joml.Matrix3d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

/**
 * One PhysContraption's rigid body: the complete dynamic state the solver integrates.
 *
 * <h2>The origin IS the center of mass</h2>
 * {@link #position} is the world position of the body's CENTER OF MASS, not of its bearing corner.
 * This is the single most important design decision here, lifted from Valkyrien Skies
 * ({@code VSGamePipelineStage#getShipVoxelOffset} returns {@code -centerOfMassInShip} and feeds it to
 * {@code collisionShapeOffset}). Two consequences follow, and both are load-bearing:
 *
 * <ol>
 * <li><b>Every torque is automatically about the COM.</b> {@code ω += I⁻¹·(r × p)} is only valid when
 *     {@code r} is measured from the COM. Making the origin the COM means no code can forget to
 *     re-base it.</li>
 * <li><b>Gravity produces exactly zero torque</b>, which is <i>physically correct</i>:
 *     {@code τ = Σ rᵢ × (mᵢ·g) = (Σ mᵢ·rᵢ) × g = 0}, because {@code Σ mᵢ·rᵢ = 0} about the COM by
 *     definition. A body does not tip because gravity twists it. It tips because <b>contact forces
 *     act at points away from the COM</b> — the ground pushes up on one corner, and that off-center
 *     push is the torque. Tipping is therefore emergent from contact resolution and must never be
 *     special-cased or faked.</li>
 * </ol>
 *
 * <p>Shape geometry is stored COM-relative to match (see {@link CollisionShape}), so
 * {@link #toWorld} is a plain {@code position + R·(p·scale)}.
 *
 * <h2>Scale</h2>
 * A body scaled by {@code s} obeys the rigid-body scaling laws — mass grows as {@code s³} (volume)
 * and inertia as {@code s⁵} ({@code m·r²} ⇒ {@code s³·s²}). {@link #inverseMass()} and
 * {@link #inverseInertiaWorld} apply these, so a scaled contraption responds to contacts correctly
 * rather than behaving like a same-mass body wearing a bigger coat.
 */
public final class RigidBody {

    /** World position of the CENTER OF MASS. See the class javadoc — this is not the bearing corner. */
    public final Vector3d position = new Vector3d();

    /** Orientation, body frame → world frame. The authoritative rotation; render Euler angles derive from it. */
    public final Quaterniond orientation = new Quaterniond();

    /** Linear velocity of the COM, in blocks per tick. */
    public final Vector3d linearVelocity = new Vector3d();

    /** Angular velocity, world frame, in radians per tick. */
    public final Vector3d angularVelocity = new Vector3d();

    private double baseInverseMass;
    private final Matrix3d baseInverseInertiaLocal = new Matrix3d().zero();
    private double scale = 1.0;

    /**
     * This body's own surface friction — the mass-weighted mean of its cells (see {@code FrictionModel}).
     * The solver combines it with the surface the body contacts, so it is only ONE side of a contact's
     * grip. Default is the ordinary coefficient the solver used to hardcode.
     */
    private double friction = 0.7;

    /** Scratch, reused to keep the per-substep solve allocation-free. */
    private final Matrix3d rotationScratch = new Matrix3d();
    private final Matrix3d inverseInertiaScratch = new Matrix3d();

    /**
     * Sets the mass properties from the aggregated model. {@code inverseInertiaLocal} must be the
     * inverse inertia tensor about the COM in the body's local (unrotated) frame; a zero matrix means
     * "infinite inertia" — an unrotatable body, the correct degenerate answer for an empty model.
     */
    public void setMassProperties(double inverseMass, Matrix3d inverseInertiaLocal) {
        this.baseInverseMass = inverseMass;
        this.baseInverseInertiaLocal.set(inverseInertiaLocal);
    }

    public void setScale(double scale) {
        this.scale = scale <= 0.0 ? 1.0 : scale;
    }

    public double scale() {
        return scale;
    }

    public void setFriction(double friction) {
        this.friction = friction < 0.0 ? 0.0 : friction;
    }

    public double friction() {
        return friction;
    }

    /** {@code 1/(m·s³)} — the scaling law for mass under a uniform scale {@code s}. */
    public double inverseMass() {
        return baseInverseMass / (scale * scale * scale);
    }

    /** True when this body cannot translate (infinite mass) — e.g. an empty or pinned contraption. */
    public boolean isStatic() {
        return baseInverseMass <= 0.0;
    }

    /**
     * The inverse inertia tensor in WORLD space: {@code R · I⁻¹ · Rᵀ · s⁻⁵}.
     *
     * <p>The similarity transform is what makes the tensor orientation-aware — a long beam lying flat
     * resists roll differently than the same beam stood on end, and that difference lives entirely in
     * this {@code R · … · Rᵀ}. Returned matrix is a shared scratch instance: read it before the next
     * call.
     */
    public Matrix3d inverseInertiaWorld() {
        orientation.get(rotationScratch);
        inverseInertiaScratch.set(baseInverseInertiaLocal);
        // I⁻¹_world = R · I⁻¹_local · Rᵀ
        inverseInertiaScratch.mulLocal(rotationScratch);
        inverseInertiaScratch.mul(rotationScratch.transpose(new Matrix3d()));
        double s5 = scale * scale * scale * scale * scale;
        inverseInertiaScratch.scale(1.0 / s5);
        return inverseInertiaScratch;
    }

    /** Transforms a COM-relative body-local point into world space. */
    public Vector3d toWorld(Vector3d local, Vector3d dest) {
        orientation.transform(local, dest);
        dest.mul(scale);
        dest.add(position);
        return dest;
    }

    /** Transforms a world point into COM-relative body-local space — the inverse of {@link #toWorld}. */
    public Vector3d toLocal(Vector3d world, Vector3d dest) {
        world.sub(position, dest);
        dest.div(scale);
        orientation.transformInverse(dest);
        return dest;
    }

    /**
     * Velocity of the material point at world-space offset {@code r} from the COM:
     * {@code v + ω × r}. This is what makes a spinning body's rim move faster than its hub, and what
     * feeds friction and restitution at a contact.
     */
    public Vector3d velocityAt(Vector3d r, Vector3d dest) {
        angularVelocity.cross(r, dest);
        dest.add(linearVelocity);
        return dest;
    }

    /**
     * Integrates the orientation by the angular velocity over {@code h}, using the quaternion
     * derivative {@code q̇ = ½·ω·q}, then renormalizes.
     *
     * <p>Renormalizing every substep is not optional: the first-order update leaves the quaternion
     * slightly non-unit, and the error compounds multiplicatively. An un-normalized quaternion
     * silently becomes a rotation-plus-scale, which shears the body's geometry.
     */
    public void integrateOrientation(double h) {
        Quaterniond delta = new Quaterniond(
                angularVelocity.x * h * 0.5,
                angularVelocity.y * h * 0.5,
                angularVelocity.z * h * 0.5,
                0.0).mul(orientation);
        orientation.x += delta.x;
        orientation.y += delta.y;
        orientation.z += delta.z;
        orientation.w += delta.w;
        orientation.normalize();
    }

    /**
     * Recovers angular velocity from the orientation change over a substep:
     * {@code Δq = q · q_prev⁻¹}, then {@code ω = 2·Δq.xyz / h}.
     *
     * <p>This is XPBD's defining move — positions are solved first, and velocities are then read back
     * <i>from</i> the resulting motion, rather than velocities being solved and integrated into
     * positions. The {@code Δq.w < 0} sign flip picks the shorter of the two equivalent rotations
     * ({@code q} and {@code −q} name the same orientation); without it a body would occasionally
     * report a near-2π spin and explode.
     */
    public void recoverAngularVelocity(Quaterniond previous, double h) {
        Quaterniond delta = new Quaterniond(orientation).mul(previous.invert(new Quaterniond()));
        double sign = delta.w >= 0.0 ? 1.0 : -1.0;
        angularVelocity.set(delta.x, delta.y, delta.z).mul(2.0 * sign / h);
    }
}
