/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix3d
 *  org.joml.Matrix3dc
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.arubik.craftengine.contraption.physics;

import java.util.function.ToDoubleFunction;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public final class RigidBody {
    public final Vector3d position = new Vector3d();
    public final Quaterniond orientation = new Quaterniond();
    public final Vector3d linearVelocity = new Vector3d();
    public final Vector3d angularVelocity = new Vector3d();
    private double baseInverseMass;
    private final Matrix3d baseInverseInertiaLocal = new Matrix3d().zero();
    private double scale = 1.0;
    private double friction = 0.7;
    private double restitution = 0.0;
    private ToDoubleFunction<Vector3d> restitutionField = null;
    private final Matrix3d rotationScratch = new Matrix3d();
    private final Matrix3d inverseInertiaScratch = new Matrix3d();

    public void setMassProperties(double inverseMass, Matrix3d inverseInertiaLocal) {
        this.baseInverseMass = inverseMass;
        this.baseInverseInertiaLocal.set((Matrix3dc)inverseInertiaLocal);
    }

    public void setScale(double scale) {
        this.scale = scale <= 0.0 ? 1.0 : scale;
    }

    public double scale() {
        return this.scale;
    }

    public void setFriction(double friction) {
        this.friction = friction < 0.0 ? 0.0 : friction;
    }

    public double friction() {
        return this.friction;
    }

    public void setRestitution(double restitution) {
        this.restitution = restitution < 0.0 ? 0.0 : (restitution > 1.0 ? 1.0 : restitution);
    }

    public double restitution() {
        return this.restitution;
    }

    public void setRestitutionField(ToDoubleFunction<Vector3d> restitutionField) {
        this.restitutionField = restitutionField;
    }

    public double restitutionAt(Vector3d worldPoint) {
        return this.restitutionField != null ? this.restitutionField.applyAsDouble(worldPoint) : this.restitution;
    }

    public double inverseMass() {
        return this.baseInverseMass / (this.scale * this.scale * this.scale);
    }

    public boolean isStatic() {
        return this.baseInverseMass <= 0.0;
    }

    public Matrix3d inverseInertiaWorld() {
        this.orientation.get(this.rotationScratch);
        this.inverseInertiaScratch.set((Matrix3dc)this.baseInverseInertiaLocal);
        this.inverseInertiaScratch.mulLocal((Matrix3dc)this.rotationScratch);
        this.inverseInertiaScratch.mul((Matrix3dc)this.rotationScratch.transpose(new Matrix3d()));
        double s5 = this.scale * this.scale * this.scale * this.scale * this.scale;
        this.inverseInertiaScratch.scale(1.0 / s5);
        return this.inverseInertiaScratch;
    }

    public Vector3d toWorld(Vector3d local, Vector3d dest) {
        this.orientation.transform((Vector3dc)local, dest);
        dest.mul(this.scale);
        dest.add((Vector3dc)this.position);
        return dest;
    }

    public Vector3d toLocal(Vector3d world, Vector3d dest) {
        world.sub((Vector3dc)this.position, dest);
        dest.div(this.scale);
        this.orientation.transformInverse(dest);
        return dest;
    }

    public Vector3d velocityAt(Vector3d r, Vector3d dest) {
        this.angularVelocity.cross((Vector3dc)r, dest);
        dest.add((Vector3dc)this.linearVelocity);
        return dest;
    }

    public void integrateOrientation(double h) {
        Quaterniond delta = new Quaterniond(this.angularVelocity.x * h * 0.5, this.angularVelocity.y * h * 0.5, this.angularVelocity.z * h * 0.5, 0.0).mul((Quaterniondc)this.orientation);
        this.orientation.x += delta.x;
        this.orientation.y += delta.y;
        this.orientation.z += delta.z;
        this.orientation.w += delta.w;
        this.orientation.normalize();
    }

    public void recoverAngularVelocity(Quaterniond previous, double h) {
        Quaterniond delta = new Quaterniond((Quaterniondc)this.orientation).mul((Quaterniondc)previous.invert(new Quaterniond()));
        double sign = delta.w >= 0.0 ? 1.0 : -1.0;
        this.angularVelocity.set(delta.x, delta.y, delta.z).mul(2.0 * sign / h);
    }
}

