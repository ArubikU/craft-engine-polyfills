/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.AABB
 *  org.joml.Matrix3d
 *  org.joml.Matrix3dc
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.arubik.craftengine.contraption.physics;

import dev.arubik.craftengine.contraption.physics.Contact;
import dev.arubik.craftengine.contraption.physics.ContactGenerator;
import dev.arubik.craftengine.contraption.physics.PhysBody;
import dev.arubik.craftengine.contraption.physics.RigidBody;
import dev.arubik.craftengine.contraption.physics.WorldBlockCache;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public final class XpbdSolver {
    public static final int SUB_STEPS = 8;
    public static final int POSITION_ITERATIONS = 2;
    public static final double GRAVITY = -0.04;
    public static final double SPECULATIVE_DISTANCE = 0.05;
    public static final double MAX_DEPENETRATION_PER_SUBSTEP = 0.05;
    public static final double RESTITUTION = 0.0;
    public static final double RESTITUTION_MIN_SPEED = 0.1;
    public static final double FRICTION = 0.7;
    public static final double LINEAR_DAMPING = 0.02;
    public static final double ANGULAR_DAMPING = 0.05;
    public static final double MAX_ANGULAR_VELOCITY = 0.15;
    public static final double ANGULAR_NOISE_EPS = 0.0025;
    public static final double SELF_RIGHT_CONE_COS = 0.7;
    public static final double SELF_RIGHT_GAIN = 0.15;
    public static final double SELF_RIGHT_MAX = 0.05;
    public static final double BUOYANCY_GAIN = 2.0;
    public static final double WATER_DRAG = 0.8;
    public static final double LAVA_DRAG = 3.0;
    public static final double REST_LINEAR_EPSILON = 0.001;
    public static final double REST_ANGULAR_EPSILON = 0.001;
    public static final int SLEEP_TICKS = 20;
    public static final double MAX_DEPEN_LINEAR = 1.5;
    public static final double MAX_DEPEN_ANGULAR = 1.5;

    private XpbdSolver() {
    }

    public static void step(List<PhysBody> bodies, double dt) {
        ArrayList<PhysBody> active = new ArrayList<PhysBody>(bodies.size());
        for (PhysBody physBody : bodies) {
            physBody.resetImpact();
            if (physBody.kinematic || physBody.body.isStatic() || physBody.shape.isEmpty()) continue;
            active.add(physBody);
        }
        if (active.isEmpty()) {
            return;
        }
        IdentityHashMap<RigidBody, PhysBody> owners = new IdentityHashMap<RigidBody, PhysBody>(active.size() * 2);
        for (PhysBody b : active) {
            owners.put(b.body, b);
        }
        double d = dt / 8.0;
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        for (int s = 0; s < 8; ++s) {
            int i;
            for (PhysBody b : active) {
                if (b.isAsleep()) continue;
                XpbdSolver.integrate(b, d);
            }
            contacts.clear();
            XpbdSolver.generateContacts(active, contacts);
            XpbdSolver.recordImpacts(contacts, owners);
            double[] normalImpulse = new double[contacts.size()];
            double[] approachSpeed = new double[contacts.size()];
            for (i = 0; i < contacts.size(); ++i) {
                approachSpeed[i] = XpbdSolver.approachNormalSpeed((Contact)contacts.get(i));
            }
            for (i = 0; i < 2; ++i) {
                XpbdSolver.solvePositions(contacts, d, normalImpulse);
            }
            for (PhysBody b : active) {
                if (b.isAsleep()) continue;
                XpbdSolver.recoverVelocities(b, d);
            }
            XpbdSolver.solveVelocities(contacts, normalImpulse, approachSpeed);
        }
        for (PhysBody b : active) {
            XpbdSolver.finishTick(b, dt);
        }
    }

    private static void integrate(PhysBody b, double h) {
        RigidBody body = b.body;
        body.linearVelocity.y += -0.04 * h;
        XpbdSolver.applyBuoyancy(b, h);
        b.previousPosition.set((Vector3dc)body.position);
        body.position.fma(h, (Vector3dc)body.linearVelocity);
        b.previousOrientation.set((Quaterniondc)body.orientation);
        body.integrateOrientation(h);
    }

    private static void applyBuoyancy(PhysBody b, double h) {
        if (b.world.hasNoFluid() || b.shape.isEmpty()) {
            return;
        }
        RigidBody body = b.body;
        double scale = body.scale();
        double scale3 = scale * scale * scale;
        double bodyMass = body.inverseMass() <= 0.0 ? 0.0 : 1.0 / body.inverseMass();
        double totalVolume = 0.0;
        for (AABB box : b.shape.boxes()) {
            totalVolume += (box.maxX - box.minX) * (box.maxY - box.minY) * (box.maxZ - box.minZ) * scale3;
        }
        for (AABB box : b.shape.boxes()) {
            double height;
            double submerged;
            Vector3d localCentre = new Vector3d((box.minX + box.maxX) * 0.5, (box.minY + box.maxY) * 0.5, (box.minZ + box.maxZ) * 0.5);
            Vector3d at = body.toWorld(localCentre, new Vector3d());
            WorldBlockCache.Fluid fluid = b.world.fluidAt((int)Math.floor(at.x), (int)Math.floor(at.y), (int)Math.floor(at.z));
            if (fluid == null || (submerged = XpbdSolver.submergedFraction(at.y, height = (box.maxY - box.minY) * scale, fluid.topY())) <= 0.0) continue;
            double volume = (box.maxX - box.minX) * (box.maxY - box.minY) * (box.maxZ - box.minZ) * scale3;
            double weightShare = totalVolume <= 0.0 ? 0.0 : bodyMass * (volume / totalVolume);
            double lift = 2.0 * (fluid.buoyancy() - b.floatability) * weightShare * submerged * 0.04;
            Vector3d impulse = new Vector3d(0.0, lift * h, 0.0);
            Vector3d r = new Vector3d((Vector3dc)at).sub((Vector3dc)body.position);
            body.linearVelocity.fma(body.inverseMass(), (Vector3dc)impulse);
            Vector3d torque = new Vector3d((Vector3dc)r).cross((Vector3dc)impulse);
            body.angularVelocity.add((Vector3dc)new Matrix3d((Matrix3dc)body.inverseInertiaWorld()).transform(torque));
            Vector3d pointVelocity = body.velocityAt(r, new Vector3d());
            Vector3d relative = pointVelocity.sub(fluid.flow().x, fluid.flow().y, fluid.flow().z);
            double drag = (fluid.lava() ? 3.0 : 0.8) * submerged * h;
            Vector3d resist = new Vector3d((Vector3dc)relative).mul(-drag * volume);
            body.linearVelocity.fma(body.inverseMass(), (Vector3dc)resist);
            body.angularVelocity.add((Vector3dc)new Matrix3d((Matrix3dc)body.inverseInertiaWorld()).transform(new Vector3d((Vector3dc)r).cross((Vector3dc)resist)));
        }
    }

    private static double submergedFraction(double centreY, double height, double surfaceY) {
        if (height <= 1.0E-9) {
            return centreY <= surfaceY ? 1.0 : 0.0;
        }
        double bottom = centreY - height * 0.5;
        double depth = surfaceY - bottom;
        return Math.max(0.0, Math.min(1.0, depth / height));
    }

    private static void generateContacts(List<PhysBody> active, List<Contact> out) {
        for (PhysBody b : active) {
            if (b.isAsleep()) continue;
            ContactGenerator.worldContacts(b.body, b.shape, b.world, out);
        }
        for (int i = 0; i < active.size(); ++i) {
            for (int j = i + 1; j < active.size(); ++j) {
                PhysBody a = active.get(i);
                PhysBody c = active.get(j);
                if (a.isAsleep() && c.isAsleep()) continue;
                ContactGenerator.bodyContacts(a.body, a.shape, c.body, c.shape, out);
                ContactGenerator.bodyContacts(c.body, c.shape, a.body, a.shape, out);
            }
        }
    }

    private static void recordImpacts(List<Contact> contacts, Map<RigidBody, PhysBody> owners) {
        for (Contact c : contacts) {
            PhysBody ownerB;
            Vector3d rB;
            Vector3d rA;
            RigidBody b;
            Contact.Evaluation eval = c.evaluate();
            if (eval.depth() < -0.05) continue;
            RigidBody a = c.bodyA();
            double vn = XpbdSolver.relativeVelocity(a, b = c.bodyB(), rA = Contact.rA(a, eval.point()), rB = b == null ? null : new Vector3d((Vector3dc)eval.point()).sub((Vector3dc)b.position)).dot((Vector3dc)eval.worldNormal());
            if (vn >= 0.0) continue;
            double closing = -vn;
            PhysBody ownerA = owners.get(a);
            if (ownerA != null) {
                ownerA.recordImpact(closing, eval.point());
            }
            if ((ownerB = b == null ? null : owners.get(b)) == null) continue;
            ownerB.recordImpact(closing, eval.point());
        }
    }

    private static void solvePositions(List<Contact> contacts, double h, double[] normalImpulse) {
        for (int idx = 0; idx < contacts.size(); ++idx) {
            double wB;
            Contact c = contacts.get(idx);
            Contact.Evaluation eval = c.evaluate();
            double depth = Math.min(eval.depth(), 0.05);
            if (depth <= 0.0) continue;
            RigidBody a = c.bodyA();
            RigidBody b = c.bodyB();
            Vector3d n = eval.worldNormal();
            Vector3d rA = Contact.rA(a, eval.point());
            Vector3d rB = b == null ? null : new Vector3d((Vector3dc)eval.point()).sub((Vector3dc)b.position);
            double wA = XpbdSolver.generalizedInverseMass(a, rA, n);
            double wSum = wA + (wB = b == null ? 0.0 : XpbdSolver.generalizedInverseMass(b, rB, n));
            if (wSum <= 1.0E-12) continue;
            double lambda = depth / wSum;
            int n2 = idx;
            normalImpulse[n2] = normalImpulse[n2] + lambda;
            Vector3d p = new Vector3d((Vector3dc)n).mul(lambda);
            XpbdSolver.applyPositionalImpulse(a, rA, p, 1.0);
            if (b == null) continue;
            XpbdSolver.applyPositionalImpulse(b, rB, p, -1.0);
        }
    }

    private static double generalizedInverseMass(RigidBody body, Vector3d r, Vector3d n) {
        Vector3d rn = new Vector3d((Vector3dc)r).cross((Vector3dc)n);
        Matrix3d invI = new Matrix3d((Matrix3dc)body.inverseInertiaWorld());
        Vector3d transformed = invI.transform(new Vector3d((Vector3dc)rn));
        return body.inverseMass() + rn.dot((Vector3dc)transformed);
    }

    private static void applyPositionalImpulse(RigidBody body, Vector3d r, Vector3d p, double sign) {
        double invMass = body.inverseMass();
        body.position.x += p.x * invMass * sign;
        body.position.y += p.y * invMass * sign;
        body.position.z += p.z * invMass * sign;
        Vector3d torque = new Vector3d((Vector3dc)r).cross((Vector3dc)new Vector3d((Vector3dc)p).mul(sign));
        Matrix3d invI = new Matrix3d((Matrix3dc)body.inverseInertiaWorld());
        Vector3d dOmega = invI.transform(torque);
        Quaterniond dq = new Quaterniond(dOmega.x * 0.5, dOmega.y * 0.5, dOmega.z * 0.5, 0.0).mul((Quaterniondc)body.orientation);
        body.orientation.x += dq.x;
        body.orientation.y += dq.y;
        body.orientation.z += dq.z;
        body.orientation.w += dq.w;
        body.orientation.normalize();
    }

    private static void recoverVelocities(PhysBody b, double h) {
        RigidBody body = b.body;
        Vector3d preLinear = new Vector3d((Vector3dc)body.linearVelocity);
        Vector3d recovered = new Vector3d((Vector3dc)body.position).sub((Vector3dc)b.previousPosition).div(h);
        Vector3d depenLinear = recovered.sub((Vector3dc)preLinear);
        double dl = depenLinear.length();
        if (dl > 1.5) {
            depenLinear.mul(1.5 / dl);
        }
        body.linearVelocity.set((Vector3dc)preLinear).add((Vector3dc)depenLinear);
        Vector3d preAngular = new Vector3d((Vector3dc)body.angularVelocity);
        body.recoverAngularVelocity(new Quaterniond((Quaterniondc)b.previousOrientation), h);
        Vector3d depenAngular = new Vector3d((Vector3dc)body.angularVelocity).sub((Vector3dc)preAngular);
        double da = depenAngular.length();
        if (da > 1.5) {
            depenAngular.mul(1.5 / da);
        }
        body.angularVelocity.set((Vector3dc)preAngular).add((Vector3dc)depenAngular);
    }

    private static double approachNormalSpeed(Contact c) {
        Contact.Evaluation eval = c.evaluate();
        if (eval.depth() < -0.05) {
            return 0.0;
        }
        RigidBody a = c.bodyA();
        RigidBody b = c.bodyB();
        Vector3d rA = Contact.rA(a, eval.point());
        Vector3d rB = b == null ? null : new Vector3d((Vector3dc)eval.point()).sub((Vector3dc)b.position);
        return XpbdSolver.relativeVelocity(a, b, rA, rB).dot((Vector3dc)eval.worldNormal());
    }

    private static void solveVelocities(List<Contact> contacts, double[] normalImpulse, double[] approachSpeed) {
        for (int idx = 0; idx < contacts.size(); ++idx) {
            Vector3d post;
            Vector3d tangent;
            double tangentSpeed;
            double wB;
            Contact c = contacts.get(idx);
            RigidBody a = c.bodyA();
            RigidBody b = c.bodyB();
            Contact.Evaluation eval = c.evaluate();
            if (eval.depth() < -0.05) continue;
            Vector3d n = eval.worldNormal();
            Vector3d rA = Contact.rA(a, eval.point());
            Vector3d rB = b == null ? null : new Vector3d((Vector3dc)eval.point()).sub((Vector3dc)b.position);
            Vector3d relative = XpbdSolver.relativeVelocity(a, b, rA, rB);
            double vn = relative.dot((Vector3dc)n);
            double wA = XpbdSolver.generalizedInverseMass(a, rA, n);
            double wSum = wA + (wB = b == null ? 0.0 : XpbdSolver.generalizedInverseMass(b, rB, n));
            if (wSum <= 1.0E-12) continue;
            double e = a.restitutionAt(eval.point());
            if (b != null) {
                e = Math.max(e, b.restitutionAt(eval.point()));
            }
            double vnPrev = approachSpeed[idx];
            double target = e > 0.0 && vnPrev < -0.1 ? -e * vnPrev : 0.0;
            double jn = 0.0;
            if (vn < target) {
                jn = (target - vn) / wSum;
                XpbdSolver.applyVelocityImpulse(a, rA, new Vector3d((Vector3dc)n).mul(jn), 1.0);
                if (b != null) {
                    XpbdSolver.applyVelocityImpulse(b, rB, new Vector3d((Vector3dc)n).mul(jn), -1.0);
                }
            }
            if ((tangentSpeed = (tangent = new Vector3d((Vector3dc)(post = XpbdSolver.relativeVelocity(a, b, rA, rB))).sub((Vector3dc)new Vector3d((Vector3dc)n).mul(post.dot((Vector3dc)n)))).length()) <= 1.0E-9) continue;
            tangent.div(tangentSpeed);
            double wtA = XpbdSolver.generalizedInverseMass(a, rA, tangent);
            double wtB = b == null ? 0.0 : XpbdSolver.generalizedInverseMass(b, rB, tangent);
            double wtSum = wtA + wtB;
            if (wtSum <= 1.0E-12) continue;
            double muA = a.friction();
            double muB = b == null ? muA : b.friction();
            double mu = Math.sqrt(Math.max(0.0, muA) * Math.max(0.0, muB));
            double normalForce = normalImpulse[idx] + Math.abs(jn);
            double jt = Math.max(-tangentSpeed / wtSum, -mu * normalForce);
            XpbdSolver.applyVelocityImpulse(a, rA, new Vector3d((Vector3dc)tangent).mul(jt), 1.0);
            if (b == null) continue;
            XpbdSolver.applyVelocityImpulse(b, rB, new Vector3d((Vector3dc)tangent).mul(jt), -1.0);
        }
    }

    private static Vector3d relativeVelocity(RigidBody a, RigidBody b, Vector3d rA, Vector3d rB) {
        Vector3d va = a.velocityAt(rA, new Vector3d());
        if (b == null) {
            return va;
        }
        return va.sub((Vector3dc)b.velocityAt(rB, new Vector3d()));
    }

    private static void applyVelocityImpulse(RigidBody body, Vector3d r, Vector3d impulse, double sign) {
        double invMass = body.inverseMass();
        body.linearVelocity.x += impulse.x * invMass * sign;
        body.linearVelocity.y += impulse.y * invMass * sign;
        body.linearVelocity.z += impulse.z * invMass * sign;
        Vector3d torque = new Vector3d((Vector3dc)r).cross((Vector3dc)new Vector3d((Vector3dc)impulse).mul(sign));
        Matrix3d invI = new Matrix3d((Matrix3dc)body.inverseInertiaWorld());
        body.angularVelocity.add((Vector3dc)invI.transform(torque));
    }

    private static void finishTick(PhysBody b, double dt) {
        boolean resting;
        double spin;
        RigidBody body = b.body;
        body.linearVelocity.mul(Math.max(0.0, 1.0 - 0.02 * dt));
        body.angularVelocity.mul(Math.max(0.0, 1.0 - 0.05 * dt));
        if (b.selfRightTicks > 0) {
            Vector3d axis;
            double sinTilt;
            --b.selfRightTicks;
            Vector3d up = body.orientation.transform((Vector3dc)new Vector3d(0.0, 1.0, 0.0), new Vector3d());
            if (up.y > 0.7 && (sinTilt = (axis = up.cross(0.0, 1.0, 0.0, new Vector3d())).length()) > 1.0E-6) {
                double restore = Math.min(0.15 * sinTilt, 0.05);
                body.angularVelocity.fma(restore / sinTilt, (Vector3dc)axis);
            }
        }
        if ((spin = body.angularVelocity.length()) < 0.0025) {
            body.angularVelocity.zero();
        } else if (spin > 0.15) {
            body.angularVelocity.mul(0.15 / spin);
        }
        boolean bl = resting = body.linearVelocity.length() < 0.001 && body.angularVelocity.length() < 0.001;
        if (resting) {
            ++b.restTicks;
            if (b.isAsleep()) {
                body.linearVelocity.zero();
                body.angularVelocity.zero();
            }
        } else {
            b.restTicks = 0;
        }
    }
}

