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
