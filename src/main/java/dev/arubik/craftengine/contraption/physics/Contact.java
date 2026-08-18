/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.arubik.craftengine.contraption.physics;

import dev.arubik.craftengine.contraption.physics.RigidBody;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public record Contact(RigidBody bodyA, RigidBody bodyB, Vector3d localA, Vector3d normal, double planeD) {
    public static Contact againstWorld(RigidBody body, Vector3d localA, Vector3d normal, double planeD) {
        return new Contact(body, null, localA, normal, planeD);
    }

    public boolean isWorldContact() {
        return this.bodyB == null;
    }

    public Evaluation evaluate() {
        double d;
        Vector3d worldNormal;
        Vector3d point = this.bodyA.toWorld(new Vector3d((Vector3dc)this.localA), new Vector3d());
        if (this.bodyB == null) {
            worldNormal = new Vector3d((Vector3dc)this.normal);
            d = this.planeD;
        } else {
            worldNormal = this.bodyB.orientation.transform(new Vector3d((Vector3dc)this.normal));
            Vector3d planePoint = this.bodyB.toWorld(new Vector3d((Vector3dc)this.normal).mul(this.planeD), new Vector3d());
            d = worldNormal.dot((Vector3dc)planePoint);
        }
        double depth = d - worldNormal.dot((Vector3dc)point);
        return new Evaluation(point, worldNormal, depth);
    }

    public static Vector3d rA(RigidBody body, Vector3d worldPoint) {
        return new Vector3d((Vector3dc)worldPoint).sub((Vector3dc)body.position);
    }

    public record Evaluation(Vector3d point, Vector3d worldNormal, double depth) {
    }
}

