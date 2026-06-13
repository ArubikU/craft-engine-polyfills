package dev.arubik.craftengine.conveyor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ConveyorMath#interpolate(double, double, double)}.
 *
 * Assumed cross-feature contract (conveyor agent OWNS this class):
 *   package dev.arubik.craftengine.conveyor;
 *   public final class ConveyorMath {
 *       // linear interpolation from start to end by progress in [0,1];
 *       // progress is CLAMPED to [0,1] before interpolating.
 *       public static double interpolate(double start, double end, double progress);
 *   }
 *
 * If the conveyor agent instead exposes a vector-valued interpolate (e.g. over a
 * 3D position), these tests need to be re-pointed at the per-axis scalar helper;
 * the numeric expectations below still hold per component.
 */
class ConveyorMathTest {

    private static final double EPS = 1e-9;

    @Test
    void progressZeroReturnsStart() {
        assertEquals(2.0, ConveyorMath.interpolate(2.0, 10.0, 0.0), EPS);
        assertEquals(-5.0, ConveyorMath.interpolate(-5.0, 5.0, 0.0), EPS);
    }

    @Test
    void progressOneReturnsEnd() {
        assertEquals(10.0, ConveyorMath.interpolate(2.0, 10.0, 1.0), EPS);
        assertEquals(5.0, ConveyorMath.interpolate(-5.0, 5.0, 1.0), EPS);
    }

    @Test
    void progressHalfReturnsMidpoint() {
        assertEquals(6.0, ConveyorMath.interpolate(2.0, 10.0, 0.5), EPS);
        assertEquals(0.0, ConveyorMath.interpolate(-5.0, 5.0, 0.5), EPS);
        assertEquals(0.5, ConveyorMath.interpolate(0.0, 1.0, 0.5), EPS);
    }

    @Test
    void arbitraryProgressInterpolatesLinearly() {
        // 0 + (100-0)*0.25 = 25
        assertEquals(25.0, ConveyorMath.interpolate(0.0, 100.0, 0.25), EPS);
        // 10 + (20-10)*0.75 = 17.5
        assertEquals(17.5, ConveyorMath.interpolate(10.0, 20.0, 0.75), EPS);
    }

    @Test
    void progressBelowZeroClampsToStart() {
        assertEquals(2.0, ConveyorMath.interpolate(2.0, 10.0, -0.5), EPS);
        assertEquals(2.0, ConveyorMath.interpolate(2.0, 10.0, -1000.0), EPS);
    }

    @Test
    void progressAboveOneClampsToEnd() {
        assertEquals(10.0, ConveyorMath.interpolate(2.0, 10.0, 1.5), EPS);
        assertEquals(10.0, ConveyorMath.interpolate(2.0, 10.0, 1000.0), EPS);
    }

    @Test
    void equalStartAndEndIsConstant() {
        assertEquals(7.0, ConveyorMath.interpolate(7.0, 7.0, 0.0), EPS);
        assertEquals(7.0, ConveyorMath.interpolate(7.0, 7.0, 0.5), EPS);
        assertEquals(7.0, ConveyorMath.interpolate(7.0, 7.0, 1.0), EPS);
    }
}
