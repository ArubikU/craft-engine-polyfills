/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaterniond
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.arubik.craftengine.contraption.physics;

import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public final class ContraptionTransform {
    public static final Vector3d PIVOT = new Vector3d(0.5, 0.0, 0.5);

    private ContraptionTransform() {
    }

    public static Vec3 bearingOrigin(Vector3d comWorld, Quaterniond orientation, Vector3d comLocal, double scale) {
        Vector3d offset = new Vector3d((Vector3dc)comLocal).sub((Vector3dc)PIVOT);
        orientation.transform(offset).mul(scale);
        return new Vec3(comWorld.x - offset.x - ContraptionTransform.PIVOT.x, comWorld.y - offset.y - ContraptionTransform.PIVOT.y, comWorld.z - offset.z - ContraptionTransform.PIVOT.z);
    }

    public static Vector3d comWorld(Vec3 bearingOrigin, Quaterniond orientation, Vector3d comLocal, double scale) {
        Vector3d offset = new Vector3d((Vector3dc)comLocal).sub((Vector3dc)PIVOT);
        orientation.transform(offset).mul(scale);
        return new Vector3d(bearingOrigin.x + ContraptionTransform.PIVOT.x + offset.x, bearingOrigin.y + ContraptionTransform.PIVOT.y + offset.y, bearingOrigin.z + ContraptionTransform.PIVOT.z + offset.z);
    }

    public static double[] eulerYXZ(Quaterniond orientation) {
        Vector3d euler = new Vector3d();
        orientation.getEulerAnglesYXZ(euler);
        return new double[]{euler.y, euler.x, euler.z};
    }

    public static Quaterniond fromEuler(double yawRadians, double pitchRadians, double rollRadians) {
        return new Quaterniond().rotationYXZ(yawRadians, pitchRadians, rollRadians);
    }
}

