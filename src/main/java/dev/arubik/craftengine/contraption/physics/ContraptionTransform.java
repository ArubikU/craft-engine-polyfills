package dev.arubik.craftengine.contraption.physics;

import org.joml.Quaterniond;
import org.joml.Vector3d;

import net.minecraft.world.phys.Vec3;

/**
 * The seam between the solver's COM-origin quaternion world and the render pipeline's
 * bearing-origin Euler world.
 *
 * <h2>The two frames, and why they differ</h2>
 * The solver must rotate about the CENTER OF MASS — {@code ω += I⁻¹·(r × p)} is only valid for
 * {@code r} measured from the COM, and gravity only cancels to zero torque there (see
 * {@link RigidBody}). The render pipeline, however, rotates every cell about the bearing's
 * XZ-centred base line, {@link #PIVOT}:
 *
 * <pre>
 *   world(cell) = bearingOrigin + P + scale·R·(cell − P)          [render]
 *   world(cell) = comWorld      + scale·R·(cell − com)            [physics]
 * </pre>
 *
 * <h2>Reconciling them exactly</h2>
 * Setting the two equal and cancelling the {@code scale·R·cell} term that appears on both sides
 * leaves a closed form with no residual dependence on {@code cell} — meaning the frames agree for
 * <i>every</i> cell simultaneously, not approximately:
 *
 * <pre>
 *   bearingOrigin = comWorld − scale·R·(com − P) − P
 * </pre>
 *
 * This is Valkyrien Skies' {@code deltaVoxelOffset} counter-translation generalized: VS keeps the
 * body origin at the COM and shifts the collision shape by {@code −COM}, then counter-translates the
 * body when the COM moves so the ship does not teleport. Same idea, solved for our pivot.
 *
 * <p>The practical payoff is that render, interaction hitboxes, and colliders all derive from ONE
 * transform. Previously each maintained its own notion of the body's placement, which is why they
 * disagreed by fractions of a block and why a landing body looked half-buried.
 */
public final class ContraptionTransform {

    private ContraptionTransform() {
    }

    /**
     * The render pipeline's rotation pivot in LOCAL cell space — the bearing's XZ centre at its base.
     * X and Z are centred (a cell spans {@code [0,1]}, so its centre is {@code 0.5}); Y is not,
     * because the body pivots about its base line, not its mid-height.
     */
    public static final Vector3d PIVOT = new Vector3d(0.5, 0.0, 0.5);

    /**
     * Converts the solver's COM world position into the bearing origin the render pipeline expects.
     *
     * @param comWorld world position of the center of mass
     * @param orientation body → world rotation
     * @param comLocal the COM in the contraption's LOCAL yaw-0 frame
     * @param scale the contraption's uniform scale
     */
    public static Vec3 bearingOrigin(Vector3d comWorld, Quaterniond orientation, Vector3d comLocal, double scale) {
        Vector3d offset = new Vector3d(comLocal).sub(PIVOT);
        orientation.transform(offset).mul(scale);
        return new Vec3(
                comWorld.x - offset.x - PIVOT.x,
                comWorld.y - offset.y - PIVOT.y,
                comWorld.z - offset.z - PIVOT.z);
    }

    /** The exact inverse of {@link #bearingOrigin} — recovers the COM's world position. */
    public static Vector3d comWorld(Vec3 bearingOrigin, Quaterniond orientation, Vector3d comLocal, double scale) {
        Vector3d offset = new Vector3d(comLocal).sub(PIVOT);
        orientation.transform(offset).mul(scale);
        return new Vector3d(
                bearingOrigin.x + PIVOT.x + offset.x,
                bearingOrigin.y + PIVOT.y + offset.y,
                bearingOrigin.z + PIVOT.z + offset.z);
    }

    /**
     * Decomposes an orientation into the render pipeline's {@code (yaw, pitch, roll)}.
     *
     * <p>{@code ContraptionMath#rotateYawPitchRoll} composes roll, then pitch, then yaw — i.e.
     * {@code R = Ry(yaw)·Rx(pitch)·Rz(roll)} — which is exactly JOML's YXZ sequence, so this is an
     * exact inverse rather than an approximation.
     *
     * @return {@code {yaw, pitch, roll}} in radians
     */
    public static double[] eulerYXZ(Quaterniond orientation) {
        Vector3d euler = new Vector3d();
        orientation.getEulerAnglesYXZ(euler);
        return new double[] { euler.y, euler.x, euler.z };
    }

    /** Builds an orientation from the render pipeline's Euler triple — the inverse of {@link #eulerYXZ}. */
    public static Quaterniond fromEuler(double yawRadians, double pitchRadians, double rollRadians) {
        return new Quaterniond().rotationYXZ(yawRadians, pitchRadians, rollRadians);
    }
}
