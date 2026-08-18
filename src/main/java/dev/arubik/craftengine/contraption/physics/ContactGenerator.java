/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.AABB
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.arubik.craftengine.contraption.physics;

import dev.arubik.craftengine.contraption.physics.CollisionShape;
import dev.arubik.craftengine.contraption.physics.Contact;
import dev.arubik.craftengine.contraption.physics.RigidBody;
import dev.arubik.craftengine.contraption.physics.WorldBlockCache;
import java.util.List;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public final class ContactGenerator {
    private static final double MIN_DEPTH = 1.0E-6;
    private static final double BOUNDARY_EPSILON = 1.0E-4;
    private static final double PUSH_THROUGH_PENALTY = 1.25;
    private static final double PROBE = 1.0E-4;

    private ContactGenerator() {
    }

    public static void worldContacts(RigidBody body, CollisionShape shape, WorldBlockCache world, List<Contact> out) {
        if (shape.isEmpty() || world.isEmpty()) {
            return;
        }
        Vector3d worldPoint = new Vector3d();
        for (Vector3d local : shape.samplePoints()) {
            body.toWorld(local, worldPoint);
            int bx = (int)Math.floor(worldPoint.x);
            int by = (int)Math.floor(worldPoint.y);
            int bz = (int)Math.floor(worldPoint.z);
            int loX = ContactGenerator.onBoundary(worldPoint.x) ? -1 : 0;
            int loY = ContactGenerator.onBoundary(worldPoint.y) ? -1 : 0;
            int loZ = ContactGenerator.onBoundary(worldPoint.z) ? -1 : 0;
            for (int dx = loX; dx <= 0; ++dx) {
                for (int dy = loY; dy <= 0; ++dy) {
                    for (int dz = loZ; dz <= 0; ++dz) {
                        for (AABB box : world.at(bx + dx, by + dy, bz + dz)) {
                            Vector3d normal;
                            double planeD;
                            if (!ContactGenerator.contains(box, worldPoint) || Double.isNaN(planeD = ContactGenerator.shallowestExposedExit(box, worldPoint, normal = new Vector3d(), world)) || planeD - normal.dot((Vector3dc)worldPoint) <= 1.0E-6) continue;
                            out.add(Contact.againstWorld(body, new Vector3d((Vector3dc)local), normal, planeD));
                        }
                    }
                }
            }
        }
    }

    private static boolean onBoundary(double coordinate) {
        double frac = coordinate - Math.floor(coordinate);
        return frac < 1.0E-4;
    }

    public static void bodyContacts(RigidBody a, CollisionShape shapeA, RigidBody b, CollisionShape shapeB, List<Contact> out) {
        if (shapeA.isEmpty() || shapeB.isEmpty()) {
            return;
        }
        if (!ContactGenerator.broadphaseOverlap(a, shapeA, b, shapeB)) {
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
                if (!ContactGenerator.contains(box, localInB)) continue;
                worldPoint.sub((Vector3dc)a.position, rA);
                worldPoint.sub((Vector3dc)b.position, rB);
                a.velocityAt(rA, velA);
                b.velocityAt(rB, velB);
                velA.sub((Vector3dc)velB, relVelLocal);
                b.orientation.transformInverse(relVelLocal);
                Vector3d normal = new Vector3d();
                double planeD = ContactGenerator.exitFace(box, localInB, relVelLocal, normal);
                if (planeD - normal.dot((Vector3dc)localInB) <= 1.0E-6) continue;
                out.add(new Contact(a, b, new Vector3d((Vector3dc)local), normal, planeD));
            }
        }
    }

    private static double exitFace(AABB box, Vector3d p, Vector3d relVel, Vector3d normalOut) {
        double[][] faces;
        double vlen = relVel.length();
        if (vlen < 1.0E-4) {
            return ContactGenerator.shallowestExit(box, p, normalOut);
        }
        double vx = relVel.x / vlen;
        double vy = relVel.y / vlen;
        double vz = relVel.z / vlen;
        double negX = p.x - box.minX;
        double posX = box.maxX - p.x;
        double negY = p.y - box.minY;
        double posY = box.maxY - p.y;
        double negZ = p.z - box.minZ;
        double posZ = box.maxZ - p.z;
        double bestScore = Double.POSITIVE_INFINITY;
        double bestPlaneD = -box.minX;
        double bnx = -1.0;
        double bny = 0.0;
        double bnz = 0.0;
        for (double[] f : faces = new double[][]{{negX, -1.0, 0.0, 0.0, -box.minX}, {posX, 1.0, 0.0, 0.0, box.maxX}, {negY, 0.0, -1.0, 0.0, -box.minY}, {posY, 0.0, 1.0, 0.0, box.maxY}, {negZ, 0.0, 0.0, -1.0, -box.minZ}, {posZ, 0.0, 0.0, 1.0, box.maxZ}}) {
            double dist = f[0];
            double nx = f[1];
            double ny = f[2];
            double nz = f[3];
            double along = nx * vx + ny * vy + nz * vz;
            double score = dist + 1.25 * Math.max(0.0, along);
            if (!(score < bestScore)) continue;
            bestScore = score;
            bnx = nx;
            bny = ny;
            bnz = nz;
            bestPlaneD = f[4];
        }
        normalOut.set(bnx, bny, bnz);
        return bestPlaneD;
    }

    public static boolean broadphaseOverlap(RigidBody a, CollisionShape shapeA, RigidBody b, CollisionShape shapeB) {
        double reach = shapeA.boundingRadius() * a.scale() + shapeB.boundingRadius() * b.scale() + 0.05 + a.linearVelocity.length() + b.linearVelocity.length();
        return a.position.distanceSquared((Vector3dc)b.position) <= reach * reach;
    }

    private static boolean contains(AABB box, Vector3d p) {
        return p.x > box.minX && p.x < box.maxX && p.y > box.minY && p.y < box.maxY && p.z > box.minZ && p.z < box.maxZ;
    }

    private static double shallowestExposedExit(AABB box, Vector3d p, Vector3d normalOut, WorldBlockCache world) {
        double posZ;
        double negZ;
        double posY;
        double negY;
        double posX;
        double best = Double.POSITIVE_INFINITY;
        double planeD = Double.NaN;
        double negX = p.x - box.minX;
        if (negX < best && !ContactGenerator.solidAt(world, box.minX - 1.0E-4, p.y, p.z)) {
            best = negX;
            normalOut.set(-1.0, 0.0, 0.0);
            planeD = -box.minX;
        }
        if ((posX = box.maxX - p.x) < best && !ContactGenerator.solidAt(world, box.maxX + 1.0E-4, p.y, p.z)) {
            best = posX;
            normalOut.set(1.0, 0.0, 0.0);
            planeD = box.maxX;
        }
        if ((negY = p.y - box.minY) < best && !ContactGenerator.solidAt(world, p.x, box.minY - 1.0E-4, p.z)) {
            best = negY;
            normalOut.set(0.0, -1.0, 0.0);
            planeD = -box.minY;
        }
        if ((posY = box.maxY - p.y) < best && !ContactGenerator.solidAt(world, p.x, box.maxY + 1.0E-4, p.z)) {
            best = posY;
            normalOut.set(0.0, 1.0, 0.0);
            planeD = box.maxY;
        }
        if ((negZ = p.z - box.minZ) < best && !ContactGenerator.solidAt(world, p.x, p.y, box.minZ - 1.0E-4)) {
            best = negZ;
            normalOut.set(0.0, 0.0, -1.0);
            planeD = -box.minZ;
        }
        if ((posZ = box.maxZ - p.z) < best && !ContactGenerator.solidAt(world, p.x, p.y, box.maxZ + 1.0E-4)) {
            normalOut.set(0.0, 0.0, 1.0);
            planeD = box.maxZ;
        }
        return planeD;
    }

    private static boolean solidAt(WorldBlockCache world, double x, double y, double z) {
        int cx = (int)Math.floor(x);
        int cy = (int)Math.floor(y);
        int cz = (int)Math.floor(z);
        for (AABB box : world.at(cx, cy, cz)) {
            if (!(x >= box.minX) || !(x <= box.maxX) || !(y >= box.minY) || !(y <= box.maxY) || !(z >= box.minZ) || !(z <= box.maxZ)) continue;
            return true;
        }
        return false;
    }

    private static double shallowestExit(AABB box, Vector3d p, Vector3d normalOut) {
        double negX = p.x - box.minX;
        double posX = box.maxX - p.x;
        double negY = p.y - box.minY;
        double posY = box.maxY - p.y;
        double negZ = p.z - box.minZ;
        double posZ = box.maxZ - p.z;
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

