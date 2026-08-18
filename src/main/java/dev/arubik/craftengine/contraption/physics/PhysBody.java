/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.AABB
 *  org.joml.Quaterniond
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.arubik.craftengine.contraption.physics;

import dev.arubik.craftengine.contraption.physics.CollisionShape;
import dev.arubik.craftengine.contraption.physics.RigidBody;
import dev.arubik.craftengine.contraption.physics.WorldBlockCache;
import net.minecraft.world.phys.AABB;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public final class PhysBody {
    public final RigidBody body = new RigidBody();
    public CollisionShape shape = CollisionShape.EMPTY;
    public WorldBlockCache world = WorldBlockCache.EMPTY;
    public AABB bakedRegion;
    public double floatability = 1.0;
    public boolean kinematic;
    public int selfRightTicks;
    int restTicks;
    final Vector3d previousPosition = new Vector3d();
    final Quaterniond previousOrientation = new Quaterniond();
    private double maxImpactSpeed;
    private final Vector3d impactPoint = new Vector3d();
    private boolean impacted;

    public void invalidateBake() {
        this.bakedRegion = null;
    }

    void resetImpact() {
        this.maxImpactSpeed = 0.0;
        this.impacted = false;
    }

    void recordImpact(double closingSpeed, Vector3d worldPoint) {
        if (closingSpeed <= this.maxImpactSpeed) {
            return;
        }
        this.maxImpactSpeed = closingSpeed;
        this.impactPoint.set((Vector3dc)worldPoint);
        this.impacted = true;
    }

    public double maxImpactSpeed() {
        return this.maxImpactSpeed;
    }

    public Vector3d impactPoint() {
        return this.impacted ? this.impactPoint : null;
    }

    public AABB worldBounds(double margin) {
        double r = this.shape.boundingRadius() * this.body.scale() + margin;
        return new AABB(this.body.position.x - r, this.body.position.y - r, this.body.position.z - r, this.body.position.x + r, this.body.position.y + r, this.body.position.z + r);
    }

    public boolean isAsleep() {
        return this.restTicks >= 20;
    }

    public void wakeUp() {
        this.restTicks = 0;
    }
}

