/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix3d
 *  org.joml.Matrix3dc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.contraption.behavior.MassModel;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.physics.PhysBody;
import dev.arubik.craftengine.contraption.physics.RigidBody;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class PhysicsBehavior
implements MovementBehavior {
    private PhysBody physBody;
    private MassModel massModel = MassModel.EMPTY;
    private Vec3 pendingDelta = Vec3.ZERO;
    private double pendingYaw;
    private double pendingPitch;
    private double pendingRoll;
    private boolean hasPendingMotion;

    public void attach(PhysBody physBody, MassModel massModel) {
        this.physBody = physBody;
        this.massModel = massModel;
    }

    public void setPendingMotion(Vec3 delta, double yawRadians, double pitchRadians, double rollRadians) {
        this.pendingDelta = delta;
        this.pendingYaw = yawRadians;
        this.pendingPitch = pitchRadians;
        this.pendingRoll = rollRadians;
        this.hasPendingMotion = true;
    }

    public void clearPendingMotion() {
        this.pendingDelta = Vec3.ZERO;
        this.hasPendingMotion = false;
    }

    @Override
    public void tick(MovementContext ctx) {
        if (!this.hasPendingMotion) {
            return;
        }
        ContraptionState state = ctx.state();
        state.setYawRadians(this.pendingYaw);
        state.setPitchRadians(this.pendingPitch);
        state.setRollRadians(this.pendingRoll);
    }

    @Override
    public Vec3 velocityThisTick() {
        return this.pendingDelta;
    }

    @Override
    public boolean isStalled() {
        return false;
    }

    public MassModel massModel() {
        return this.massModel;
    }

    public PhysBody physBody() {
        return this.physBody;
    }

    public Vec3 linearVelocity() {
        RigidBody body = this.body();
        return body == null ? Vec3.ZERO : new Vec3(body.linearVelocity.x, body.linearVelocity.y, body.linearVelocity.z);
    }

    public Vec3 angularVelocity() {
        RigidBody body = this.body();
        return body == null ? Vec3.ZERO : new Vec3(body.angularVelocity.x, body.angularVelocity.y, body.angularVelocity.z);
    }

    public boolean isResting() {
        return this.physBody != null && this.physBody.isAsleep();
    }

    public void applyImpulseAt(Vec3 worldPoint, Vec3 impulse) {
        RigidBody body = this.body();
        if (body == null) {
            return;
        }
        Vector3d p = new Vector3d(impulse.x, impulse.y, impulse.z);
        Vector3d r = new Vector3d(worldPoint.x, worldPoint.y, worldPoint.z).sub((Vector3dc)body.position);
        body.linearVelocity.fma(body.inverseMass(), (Vector3dc)p);
        Vector3d torque = new Vector3d((Vector3dc)r).cross((Vector3dc)p);
        body.angularVelocity.add((Vector3dc)new Matrix3d((Matrix3dc)body.inverseInertiaWorld()).transform(torque));
        this.physBody.wakeUp();
    }

    public void applyImpulse(Vec3 impulse) {
        RigidBody body = this.body();
        if (body == null) {
            return;
        }
        body.linearVelocity.fma(body.inverseMass(), (Vector3dc)new Vector3d(impulse.x, impulse.y, impulse.z));
        this.physBody.wakeUp();
    }

    private RigidBody body() {
        return this.physBody == null ? null : this.physBody.body;
    }
}

