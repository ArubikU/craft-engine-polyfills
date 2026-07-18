package dev.arubik.craftengine.contraption.physics;

import java.util.List;

import org.joml.Vector3d;

import net.minecraft.world.phys.AABB;

/**
 * Builds the contact manifold: which of a body's surface points are inside solid geometry, and which
 * plane each must be pushed back out through.
 *
 * <h2>Point-sample narrowphase, not box SAT</h2>
 * Each body carries a precomputed cloud of surface points ({@link CollisionShape#samplePoints()});
 * a contact exists wherever a point lies inside solid geometry. This mirrors Valkyrien Skies, whose
 * blockstates carry {@code collisionPoints} (position + radius) tested against the other body's voxel
 * field.
 *
 * <p>Create's {@code ContinuousOBBCollider} was rejected deliberately. It is excellent at its actual
 * job — pushing one entity box out of a contraption — but it yields a single whole-body minimum
 * translation vector, which is exactly the shape of information the old engine already had and could
 * not tip with. Create never needs more, because Create contraptions are purely kinematic: no mass,
 * no center of mass, and collision never writes back to them. We need many simultaneous contacts with
 * distinct lever arms, because that <i>is</i> the tipping mechanism — a resting body is held up by an
 * impulse at every supported corner, and when the center of mass drifts past those corners the
 * impulses stop balancing and the body rotates.
 *
 * <h2>Normal choice</h2>
 * A point inside a box is pushed out through its <b>least-penetrating face</b>. This is the standard
 * minimum-translation choice, and it is what makes a body slide along a floor rather than get fired
 * out of the nearest wall.
 */
public final class ContactGenerator {

    private ContactGenerator() {
    }

    /**
     * Ignore contacts shallower than this. Solving a sub-micron overlap wastes an iteration and feeds
     * numerical noise into {@code r × n}, which surfaces as a resting body jittering in place.
     */
    private static final double MIN_DEPTH = 1.0E-6;

    /**
     * Generates every contact between {@code body}'s sample cloud and the baked world.
     *
     * <p>A sample can land inside more than one box — block seams share faces — and each produces its
     * own constraint. That is correct rather than redundant: a manifold is many constraints, and
     * because each one re-evaluates its own error during the solve ({@link Contact#evaluate}), the
     * ones already satisfied contribute nothing.
     */
    public static void worldContacts(RigidBody body, CollisionShape shape, WorldBlockCache world,
            List<Contact> out) {
        if (shape.isEmpty() || world.isEmpty()) {
            return;
        }
        Vector3d worldPoint = new Vector3d();
        for (Vector3d local : shape.samplePoints()) {
            body.toWorld(local, worldPoint);
            int bx = (int) Math.floor(worldPoint.x);
            int by = (int) Math.floor(worldPoint.y);
            int bz = (int) Math.floor(worldPoint.z);
            // A point exactly on a cell boundary belongs to two cells, and floor() may pick the air
            // side — so the cell BEFORE it on that axis has to be checked too. Only on that axis, and
            // only when the point is actually on a boundary: sweeping all eight neighbours
            // unconditionally costs 8x the map lookups on every sample of every substep to cover a case
            // that is rare and axis-local. This is the hottest loop in the solver.
            int loX = onBoundary(worldPoint.x) ? -1 : 0;
            int loY = onBoundary(worldPoint.y) ? -1 : 0;
            int loZ = onBoundary(worldPoint.z) ? -1 : 0;
            for (int dx = loX; dx <= 0; dx++) {
                for (int dy = loY; dy <= 0; dy++) {
                    for (int dz = loZ; dz <= 0; dz++) {
                        for (AABB box : world.at(bx + dx, by + dy, bz + dz)) {
                            if (!contains(box, worldPoint)) {
                                continue;
                            }
                            Vector3d normal = new Vector3d();
                            double planeD = shallowestExposedExit(box, worldPoint, normal, world);
                            if (Double.isNaN(planeD)) {
                                continue; // every face of this box is buried inside other solid — no surface to push out on
                            }
                            if (planeD - normal.dot(worldPoint) <= MIN_DEPTH) {
                                continue;
                            }
                            out.add(Contact.againstWorld(body, new Vector3d(local), normal, planeD));
                        }
                    }
                }
            }
        }
    }

    /**
     * Whether a world coordinate sits close enough to a block boundary that the cell on the low side
     * must be checked as well.
     *
     * <p>The window matches {@link #MIN_DEPTH}'s scale: a point further inside its own cell than this
     * cannot possibly be inside the previous cell's box, because that box ends at the boundary.
     */
    private static boolean onBoundary(double coordinate) {
        double frac = coordinate - Math.floor(coordinate);
        return frac < BOUNDARY_EPSILON;
    }

    private static final double BOUNDARY_EPSILON = 1.0E-4;

    /**
     * Generates contacts between two dynamic bodies by testing {@code a}'s sample cloud against
     * {@code b}'s merged boxes.
     *
     * <p>{@code a}'s world points are transformed into {@code b}'s local frame, where {@code b}'s
     * geometry is axis-aligned and static — Create's transform-into-contraption-space trick, which
     * collapses moving-body-vs-moving-body into point-vs-AABB. The resulting plane is stored in
     * {@code b}'s local frame so it keeps following {@code b} as it moves and spins during the solve.
     *
     * <p>Called once per ordered pair, so run it both ways ({@code a,b} then {@code b,a}): sampling
     * one direction only lets a sparse cloud on {@code a} miss a thin protrusion on {@code b}.
     */
    public static void bodyContacts(RigidBody a, CollisionShape shapeA, RigidBody b, CollisionShape shapeB,
            List<Contact> out) {
        if (shapeA.isEmpty() || shapeB.isEmpty()) {
            return;
        }
        if (!broadphaseOverlap(a, shapeA, b, shapeB)) {
            return;
        }
        Vector3d worldPoint = new Vector3d();
        Vector3d localInB = new Vector3d();
        Vector3d relVelLocal = new Vector3d();
        Vector3d rA = new Vector3d();
        Vector3d rB = new Vector3d();
        Vector3d velA = new Vector3d();
        Vector3d velB = new Vector3d();
        for (Vector3d local : shapeA.samplePoints()) {
            a.toWorld(local, worldPoint);
            b.toLocal(worldPoint, localInB);
            for (AABB box : shapeB.boxes()) {
                if (!contains(box, localInB)) {
                    continue;
                }
                // Velocity of A's material point RELATIVE to B, in B's local frame. This is the approach
                // direction the exit face is chosen against (see #exitFace) — the whole fix for two
                // contraptions that interpenetrate and then cannot separate (2026-07-17 — "una vez ya
                // empieza a atravesar dificil se salen").
                worldPoint.sub(a.position, rA);
                worldPoint.sub(b.position, rB);
                a.velocityAt(rA, velA);
                b.velocityAt(rB, velB);
                velA.sub(velB, relVelLocal);
                b.orientation.transformInverse(relVelLocal); // world direction -> B's local frame
                Vector3d normal = new Vector3d();
                double planeD = exitFace(box, localInB, relVelLocal, normal);
                if (planeD - normal.dot(localInB) <= MIN_DEPTH) {
                    continue;
                }
                out.add(new Contact(a, b, new Vector3d(local), normal, planeD));
            }
        }
    }

    /**
     * How strongly a face pointing ALONG the approach velocity is penalised, in blocks. Big enough that
     * once a point is past a box's midplane the entry face wins over the (geometrically nearer) far face,
     * small enough that it never overrides a clear, shallow resting contact.
     */
    private static final double PUSH_THROUGH_PENALTY = 1.25;

    /**
     * Picks the face to depenetrate a deep point out of, biased AGAINST the approach velocity.
     *
     * <p>Pure {@link #shallowestExit} takes the geometrically nearest face. Once A's point is past B's
     * midplane that nearest face is the FAR side — the side A is heading toward — so depenetration shoves A
     * the rest of the way THROUGH B instead of back out the way it came. That is exactly why two
     * contraptions that clip into each other stay stuck: every substep pushes them further in.
     *
     * <p>Each face is scored {@code exitDistance + PENALTY·max(0, n·v̂)}: a face whose outward normal points
     * along the approach velocity {@code v̂} is penalised (pushing that way is pushing through), so the
     * OPPOSING face — the one A entered through — wins even though it is geometrically farther. When the
     * relative velocity is ~0 (a resting or sliding contact) the penalty vanishes and this reduces exactly
     * to {@link #shallowestExit}, so stable contacts are unchanged.
     */
    private static double exitFace(AABB box, Vector3d p, Vector3d relVel, Vector3d normalOut) {
        double vlen = relVel.length();
        if (vlen < 1.0E-4) {
            return shallowestExit(box, p, normalOut);
        }
        double vx = relVel.x / vlen, vy = relVel.y / vlen, vz = relVel.z / vlen;
        double negX = p.x - box.minX, posX = box.maxX - p.x;
        double negY = p.y - box.minY, posY = box.maxY - p.y;
        double negZ = p.z - box.minZ, posZ = box.maxZ - p.z;

        double bestScore = Double.POSITIVE_INFINITY;
        double bestPlaneD = -box.minX;
        double bnx = -1, bny = 0, bnz = 0;
        // Each candidate: (exitDistance, normal, planeD). Score adds a penalty when the normal points
        // along +v (n·v̂ > 0), i.e. pushing that way drives the point further along its approach.
        double[][] faces = {
                { negX, -1, 0, 0, -box.minX },
                { posX, 1, 0, 0, box.maxX },
                { negY, 0, -1, 0, -box.minY },
                { posY, 0, 1, 0, box.maxY },
                { negZ, 0, 0, -1, -box.minZ },
                { posZ, 0, 0, 1, box.maxZ },
        };
        for (double[] f : faces) {
            double dist = f[0];
            double nx = f[1], ny = f[2], nz = f[3];
            double along = nx * vx + ny * vy + nz * vz; // n·v̂
            double score = dist + PUSH_THROUGH_PENALTY * Math.max(0.0, along);
            if (score < bestScore) {
                bestScore = score;
                bnx = nx;
                bny = ny;
                bnz = nz;
                bestPlaneD = f[4];
            }
        }
        normalOut.set(bnx, bny, bnz);
        return bestPlaneD;
    }

    /**
     * Cheap sphere-vs-sphere rejection before the O(samples × boxes) narrowphase, and the test that
     * decides which bodies must be solved TOGETHER.
     *
     * <h2>Why a tick of motion is added to the reach</h2>
     * This answers "could these two touch during this tick", not "are these two touching right now",
     * and the difference is the whole correctness of body-vs-body collision.
     *
     * <p>{@code PhysicsWorld} partitions bodies into islands with this test ONCE per tick and may solve
     * separate islands on separate threads. Two contraptions closing on each other are still apart at
     * that moment — so measuring only their current positions put them in different islands, where
     * neither could ever see the other, and they spent the whole tick's substeps moving straight
     * through. By the next tick they were already interpenetrated, and a contact generated from deep
     * inside picks whichever face is nearest, which shoves them further through rather than apart. That
     * is what two contraptions ending up inside each other looks like.
     *
     * <p>Including each body's per-tick displacement makes the reach cover everywhere they can get to
     * before this test is asked again. Velocity is already in blocks per tick, so its magnitude IS the
     * distance covered. Angular velocity needs no term: the bounding sphere is rotation-invariant, so
     * spinning cannot move a body outside it.
     *
     * <p>Radii are scaled because {@link CollisionShape#boundingRadius()} is in unscaled body-local
     * units.
     */
    public static boolean broadphaseOverlap(RigidBody a, CollisionShape shapeA, RigidBody b, CollisionShape shapeB) {
        double reach = shapeA.boundingRadius() * a.scale() + shapeB.boundingRadius() * b.scale()
                + XpbdSolver.SPECULATIVE_DISTANCE
                + a.linearVelocity.length() + b.linearVelocity.length();
        return a.position.distanceSquared(b.position) <= reach * reach;
    }

    private static boolean contains(AABB box, Vector3d p) {
        return p.x > box.minX && p.x < box.maxX
                && p.y > box.minY && p.y < box.maxY
                && p.z > box.minZ && p.z < box.maxZ;
    }

    /**
     * For a point known to be inside {@code box}, picks the nearest face, writes its outward unit
     * normal into {@code normalOut}, and returns the plane offset {@code normal · pointOnPlane}.
     *
     * <p>Returning the plane rather than the depth is what lets the constraint be re-evaluated later:
     * {@code depth = planeD − normal · point} holds for any subsequent position of the point.
     */
    /**
     * As {@link #shallowestExit} but never pushes a point out through a face that is buried against
     * another solid block — the seam between two floor blocks, an interior wall joint, any face with a
     * solid neighbour flush against it.
     *
     * <h2>Why this exists</h2>
     * The world is voxels, and a body resting on a flat multi-block floor sinks a hair below the surface
     * each substep before the solve lifts it back. A sample point that has slid just past a block boundary
     * then sits barely inside the NEXT floor block, equidistant from that block's top face and its
     * <i>internal</i> side face (the one flush against the block it just came from). {@link #shallowestExit}
     * would pick whichever is a whisker shallower — and half the time that is the side face, whose normal
     * points horizontally. A horizontal normal on a floor contact reads a slide as a head-on wall
     * collision: the velocity solve kills the entire sliding velocity. The body catches on every seam
     * between floor tiles and stops dead — which is exactly the friction test's "ice stops after one
     * block" (2026-07-17).
     *
     * <p>The fix is the standard voxel face-cull: a face flush against a solid neighbour is not a real
     * surface, so it is skipped. Only faces exposed to air can push a body out, so a flat floor presents
     * only its top face and a sliding body glides across the seams. Returns {@link Double#NaN} if every
     * face is occluded (a point fully entombed in solid), which the caller drops.
     */
    private static double shallowestExposedExit(AABB box, Vector3d p, Vector3d normalOut, WorldBlockCache world) {
        double best = Double.POSITIVE_INFINITY;
        double planeD = Double.NaN;
        // -X, +X, -Y, +Y, -Z, +Z: (exit distance, outward normal, plane offset, probe point just outside)
        double negX = p.x - box.minX;
        if (negX < best && !solidAt(world, box.minX - PROBE, p.y, p.z)) {
            best = negX; normalOut.set(-1.0, 0.0, 0.0); planeD = -box.minX;
        }
        double posX = box.maxX - p.x;
        if (posX < best && !solidAt(world, box.maxX + PROBE, p.y, p.z)) {
            best = posX; normalOut.set(1.0, 0.0, 0.0); planeD = box.maxX;
        }
        double negY = p.y - box.minY;
        if (negY < best && !solidAt(world, p.x, box.minY - PROBE, p.z)) {
            best = negY; normalOut.set(0.0, -1.0, 0.0); planeD = -box.minY;
        }
        double posY = box.maxY - p.y;
        if (posY < best && !solidAt(world, p.x, box.maxY + PROBE, p.z)) {
            best = posY; normalOut.set(0.0, 1.0, 0.0); planeD = box.maxY;
        }
        double negZ = p.z - box.minZ;
        if (negZ < best && !solidAt(world, p.x, p.y, box.minZ - PROBE)) {
            best = negZ; normalOut.set(0.0, 0.0, -1.0); planeD = -box.minZ;
        }
        double posZ = box.maxZ - p.z;
        if (posZ < best && !solidAt(world, p.x, p.y, box.maxZ + PROBE)) {
            normalOut.set(0.0, 0.0, 1.0); planeD = box.maxZ;
        }
        return planeD;
    }

    /**
     * How far past a face to sample when deciding whether it is buried against a neighbour. Small enough
     * to land inside a flush neighbour block (which abuts exactly at the face plane) and large enough to
     * clear floating-point slop at the boundary.
     */
    private static final double PROBE = 1.0E-4;

    /** Whether a world point lies inside any solid box of the cell that contains it. */
    private static boolean solidAt(WorldBlockCache world, double x, double y, double z) {
        int cx = (int) Math.floor(x), cy = (int) Math.floor(y), cz = (int) Math.floor(z);
        for (AABB box : world.at(cx, cy, cz)) {
            if (x >= box.minX && x <= box.maxX && y >= box.minY && y <= box.maxY && z >= box.minZ && z <= box.maxZ) {
                return true;
            }
        }
        return false;
    }

    private static double shallowestExit(AABB box, Vector3d p, Vector3d normalOut) {
        double negX = p.x - box.minX, posX = box.maxX - p.x;
        double negY = p.y - box.minY, posY = box.maxY - p.y;
        double negZ = p.z - box.minZ, posZ = box.maxZ - p.z;
        double best = negX;
        normalOut.set(-1.0, 0.0, 0.0);
        double planeD = -box.minX;
        if (posX < best) {
            best = posX;
            normalOut.set(1.0, 0.0, 0.0);
            planeD = box.maxX;
        }
        if (negY < best) {
            best = negY;
            normalOut.set(0.0, -1.0, 0.0);
            planeD = -box.minY;
        }
        if (posY < best) {
            best = posY;
            normalOut.set(0.0, 1.0, 0.0);
            planeD = box.maxY;
        }
        if (negZ < best) {
            best = negZ;
            normalOut.set(0.0, 0.0, -1.0);
            planeD = -box.minZ;
        }
        if (posZ < best) {
            normalOut.set(0.0, 0.0, 1.0);
            planeD = box.maxZ;
        }
        return planeD;
    }
}
