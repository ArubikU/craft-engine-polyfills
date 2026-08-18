/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.conveyor;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class ConveyorMath {
    public static final float BELT_TOP_Y = 0.28f;

    private ConveyorMath() {
    }

    public static Vector3f interpolate(Vector3f start, Vector3f end, float progress) {
        float t = ConveyorMath.clamp01(progress);
        return new Vector3f(start.x + (end.x - start.x) * t, start.y + (end.y - start.y) * t, start.z + (end.z - start.z) * t);
    }

    public static double interpolate(double start, double end, double progress) {
        double t = progress < 0.0 ? 0.0 : (progress > 1.0 ? 1.0 : progress);
        return start + (end - start) * t;
    }

    public static Vector3f startPoint(int stepX, int stepZ, int slopeStepY) {
        float yOff = slopeStepY > 0 ? -0.5f : (slopeStepY < 0 ? 0.5f : 0.0f);
        return new Vector3f(0.5f - (float)stepX * 0.5f, 0.28f + yOff + ConveyorMath.slopeLift(slopeStepY), 0.5f - (float)stepZ * 0.5f);
    }

    private static float slopeLift(int slopeStepY) {
        return slopeStepY != 0 ? 0.5f : 0.0f;
    }

    public static Vector3f endPoint(int stepX, int stepZ, int slopeStepY) {
        float yOff = slopeStepY > 0 ? 0.5f : (slopeStepY < 0 ? -0.5f : 0.0f);
        return new Vector3f(0.5f + (float)stepX * 0.5f, 0.28f + yOff + ConveyorMath.slopeLift(slopeStepY), 0.5f + (float)stepZ * 0.5f);
    }

    public static Quaternionf itemRotation(int facingStepX, int facingStepZ, int slopeStepY) {
        float pitch;
        float yaw = (float)Math.atan2(-facingStepX, facingStepZ);
        float f = slopeStepY > 0 ? (float)Math.toRadians(45.0) : (pitch = slopeStepY < 0 ? (float)Math.toRadians(-45.0) : 0.0f);
        if (facingStepX < 0 || facingStepZ < 0) {
            pitch = -pitch;
        }
        return new Quaternionf().rotateY(yaw).rotateX((float)Math.toRadians(-90.0) + pitch);
    }

    public static float clamp01(float v) {
        if (v < 0.0f) {
            return 0.0f;
        }
        if (v > 1.0f) {
            return 1.0f;
        }
        return v;
    }

    public static float progressPerTick(float rpm, float baseRpm, int beltLengthTicksAtBaseRpm) {
        if (rpm == 0.0f || baseRpm <= 0.0f || beltLengthTicksAtBaseRpm <= 0) {
            return 0.0f;
        }
        float ticksToCross = (float)beltLengthTicksAtBaseRpm * (baseRpm / Math.abs(rpm));
        if (ticksToCross < 1.0f) {
            ticksToCross = 1.0f;
        }
        return 1.0f / ticksToCross;
    }
}

