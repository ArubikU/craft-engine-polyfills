package dev.arubik.craftengine.contraption.physics;

import org.joml.Vector3d;

/**
 * One contact constraint: a point on {@link #bodyA} that must not pass through a plane belonging to
 * {@link #bodyB} (or to the static world).
 *
 * <h2>Why a plane, and not a cached depth</h2>
 * A contact is stored as a <b>recomputable constraint</b>, not as a frozen penetration measurement.
 * The constraint error {@code C} is evaluated from the bodies' CURRENT transforms every solver
 * iteration ({@link #evaluate}), which is what XPBD requires and what makes a manifold of many
 * contacts converge instead of explode.
 *
 * <p>The alternative — caching {@code depth} at generation time — fails in a way worth recording. A
 * body resting on flat ground produces dozens of simultaneous contacts. If each one applies a
 * correction sized from its own stale depth, and none of them observes that an earlier contact
 * already lifted the body clear, the body is pushed out dozens of times over and launched. Solving
 * against a live {@code C} means the first contact resolves the overlap and the rest evaluate to
 * {@code C <= 0} and correctly do nothing.
 *
 * <h2>Why this record has no torque, tilt, or tip field</h2>
 * It only knows a point, a plane, and which bodies. Rotation is not stored or requested anywhere —
 * it falls out of {@link #rA} being a lever arm in {@code r × p}.
 *
 * @param bodyA the body whose surface point is constrained
 * @param bodyB the body owning the plane, or {@code null} when the plane belongs to the immovable world
 * @param localA the contact point on {@code bodyA}, COM-relative in {@code bodyA}'s local frame
 * @param normal the plane's outward unit normal — in {@code bodyB}'s LOCAL frame, or world space when
 *        {@code bodyB} is {@code null}
 * @param planeD the plane offset, {@code normal · pointOnPlane}, in the same frame as {@code normal}
 */
public record Contact(RigidBody bodyA, RigidBody bodyB, Vector3d localA, Vector3d normal, double planeD) {

    /** A contact against a world-space plane. {@code normal}/{@code planeD} are already world-space. */
    public static Contact againstWorld(RigidBody body, Vector3d localA, Vector3d normal, double planeD) {
        return new Contact(body, null, localA, normal, planeD);
    }

    /** True when the plane belongs to the static world rather than a second dynamic body. */
    public boolean isWorldContact() {
        return bodyB == null;
    }

    /**
     * The live state of this constraint at the bodies' current transforms.
     *
     * @param point world position of the contact point on {@code bodyA}
     * @param worldNormal the plane's outward normal in world space
     * @param depth penetration: positive when the point is behind the plane and must be pushed out
     */
    public record Evaluation(Vector3d point, Vector3d worldNormal, double depth) {
    }

    /**
     * Recomputes the constraint from the bodies' current transforms. Called every solver iteration —
     * see the class javadoc for why this must not be cached.
     */
    public Evaluation evaluate() {
        Vector3d point = bodyA.toWorld(new Vector3d(localA), new Vector3d());
        Vector3d worldNormal;
        double d;
        if (bodyB == null) {
            worldNormal = new Vector3d(normal);
            d = planeD;
        } else {
            worldNormal = bodyB.orientation.transform(new Vector3d(normal));
            // A point on the plane, carried into world space by B's live transform, re-derives the
            // offset — so the plane follows B as it moves and spins.
            Vector3d planePoint = bodyB.toWorld(new Vector3d(normal).mul(planeD), new Vector3d());
            d = worldNormal.dot(planePoint);
        }
        double depth = d - worldNormal.dot(point);
        return new Evaluation(point, worldNormal, depth);
    }

    /** The lever arm: the contact point's offset from {@code bodyA}'s center of mass. */
    public static Vector3d rA(RigidBody body, Vector3d worldPoint) {
        return new Vector3d(worldPoint).sub(body.position);
    }
}
