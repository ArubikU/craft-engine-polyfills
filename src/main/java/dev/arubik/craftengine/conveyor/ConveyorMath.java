package dev.arubik.craftengine.conveyor;

import org.joml.Vector3f;

/**
 * Pure, Bukkit-free interpolation math for conveyor item movement so it can be
 * unit-tested in isolation.
 */
public final class ConveyorMath {

    private ConveyorMath() {
    }

    /**
     * Linearly interpolate between {@code start} and {@code end} by {@code progress}.
     *
     * @param start    block-relative start point
     * @param end      block-relative end point
     * @param progress clamped to [0,1]
     * @return a new vector at the interpolated position
     */
    public static Vector3f interpolate(Vector3f start, Vector3f end, float progress) {
        float t = clamp01(progress);
        return new Vector3f(
                start.x + (end.x - start.x) * t,
                start.y + (end.y - start.y) * t,
                start.z + (end.z - start.z) * t);
    }

    /** Scalar (per-axis) linear interpolation; {@code progress} is clamped to [0,1]. */
    public static double interpolate(double start, double end, double progress) {
        double t = progress < 0 ? 0 : (progress > 1 ? 1 : progress);
        return start + (end - start) * t;
    }

    /** Block-relative Y at which the carried item rides the belt top (flat). */
    public static final float BELT_TOP_Y = 0.28f;

    /**
     * Build the block-relative START point (entry) of a segment from its facing
     * step vector and slope. The entry is the face OPPOSITE travel; for an UP
     * slope the entry sits low and for a DOWN slope it sits high, so the item Y
     * ramps by +/-1 across the segment.
     *
     * <p>Pure: no Bukkit/NMS. {@code stepX}/{@code stepZ} are the facing's unit
     * step (one of them is +/-1, the other 0). {@code slopeStepY} is -1, 0 or +1.</p>
     */
    public static Vector3f startPoint(int stepX, int stepZ, int slopeStepY) {
        // entry face = -facing, centred, at belt top; UP starts one lower, DOWN one higher
        float yOff = slopeStepY > 0 ? -0.5f : (slopeStepY < 0 ? 0.5f : 0f);
        return new Vector3f(0.5f - stepX * 0.5f, BELT_TOP_Y + yOff + slopeLift(slopeStepY), 0.5f - stepZ * 0.5f);
    }

    /** Extra +8px (0.5) lift so the item rides ON the ramp surface, not sunk into it. */
    private static float slopeLift(int slopeStepY) {
        return slopeStepY != 0 ? 0.5f : 0f;
    }

    /**
     * Build the block-relative END point (exit) of a segment from its facing
     * step vector and slope. The exit is the facing face; for UP it sits high,
     * for DOWN it sits low.
     *
     * <p>Pure: no Bukkit/NMS.</p>
     */
    public static Vector3f endPoint(int stepX, int stepZ, int slopeStepY) {
        float yOff = slopeStepY > 0 ? 0.5f : (slopeStepY < 0 ? -0.5f : 0f);
        return new Vector3f(0.5f + stepX * 0.5f, BELT_TOP_Y + yOff + slopeLift(slopeStepY), 0.5f + stepZ * 0.5f);
    }

    /**
     * Orientation for the carried item display: yaw aligns the item with the belt
     * travel direction (from the facing step vector) and pitch tilts it +/-45 on
     * up/down ramps. Pure (joml only) so it is unit-testable.
     *
     * @param facingStepX one of -1/0/1 (the facing's unit X step)
     * @param facingStepZ one of -1/0/1 (the facing's unit Z step)
     * @param slopeStepY  -1 (down), 0 (flat) or +1 (up)
     */
    public static org.joml.Quaternionf itemRotation(int facingStepX, int facingStepZ, int slopeStepY) {
        float yaw = (float) Math.atan2(-facingStepX, facingStepZ);
        float pitch = slopeStepY > 0 ? (float) Math.toRadians(45)
                : slopeStepY < 0 ? (float) Math.toRadians(-45) : 0f;
        // Negative-axis facings (NORTH stepZ=-1, WEST stepX=-1) carry a yaw that flips the
        // item end-for-end, which mirrors the ramp pitch. Invert pitch there so the item
        // tilts along the slope (not against it).
        if (facingStepX < 0 || facingStepZ < 0)
            pitch = -pitch;
        // Lay the item flat on the belt (default item_display stands upright) via a
        // -90° tilt about X, then add the ramp pitch so it follows up/down slopes.
        return new org.joml.Quaternionf().rotateY(yaw).rotateX((float) Math.toRadians(-90) + pitch);
    }

    /** Clamp a float into the [0,1] range. */
    public static float clamp01(float v) {
        if (v < 0f)
            return 0f;
        if (v > 1f)
            return 1f;
        return v;
    }

    /**
     * Derive the per-tick progress increment from input RPM.
     *
     * <p>One full belt segment is one revolution-equivalent of travel. We map RPM
     * to ticks-per-item linearly and invert: at {@code rpm <= 0} the belt is
     * stalled (0 increment). {@code beltLengthTicksAtBaseRpm} is how many ticks a
     * single item takes to cross one segment at {@code baseRpm}.</p>
     *
     * @param rpm                      current input rpm (0 => stalled)
     * @param baseRpm                  the reference rpm at which the belt runs at base speed
     * @param beltLengthTicksAtBaseRpm ticks to cross one segment at baseRpm (must be > 0)
     * @return progress increment per tick in [0,1]; 0 if stalled
     */
    public static float progressPerTick(float rpm, float baseRpm, int beltLengthTicksAtBaseRpm) {
        if (rpm <= 0f || baseRpm <= 0f || beltLengthTicksAtBaseRpm <= 0)
            return 0f;
        float ticksToCross = beltLengthTicksAtBaseRpm * (baseRpm / rpm);
        if (ticksToCross < 1f)
            ticksToCross = 1f;
        return 1f / ticksToCross;
    }
}
