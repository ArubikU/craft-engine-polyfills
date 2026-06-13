package dev.arubik.craftengine.conveyor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests for the pure two-point belt planner {@link ConveyorPath}. */
class ConveyorPathTest {

    @Test
    void straightFlatLineAlongX_isValid() {
        ConveyorPath.Result r = ConveyorPath.plan(0, 64, 0, 3, 64, 0);
        assertTrue(r.isValid(), r.error());
        List<ConveyorPath.Step> steps = r.steps();
        assertEquals(4, steps.size());
        assertEquals(ConveyorPart.START, steps.get(0).part);
        assertEquals(ConveyorPart.MIDDLE, steps.get(1).part);
        assertEquals(ConveyorPart.MIDDLE, steps.get(2).part);
        assertEquals(ConveyorPart.END, steps.get(3).part);
        // facing = +X, flat
        for (ConveyorPath.Step s : steps) {
            assertEquals(1, s.stepX);
            assertEquals(0, s.stepZ);
            assertEquals(ConveyorSlope.FLAT, s.slope);
        }
        // positions advance along X only
        assertEquals(2, steps.get(2).x);
        assertEquals(64, steps.get(2).y);
        assertEquals(0, steps.get(2).z);
    }

    @Test
    void straightFlatLineAlongNegativeZ_isValid() {
        ConveyorPath.Result r = ConveyorPath.plan(5, 70, 9, 5, 70, 6);
        assertTrue(r.isValid(), r.error());
        assertEquals(4, r.steps().size());
        assertEquals(-1, r.steps().get(0).stepZ);
        assertEquals(0, r.steps().get(0).stepX);
        assertEquals(ConveyorSlope.FLAT, r.steps().get(0).slope);
    }

    @Test
    void diagonal45Up_isValid() {
        // 3 blocks along +X, +3 Y => consistent 45 up slope
        ConveyorPath.Result r = ConveyorPath.plan(0, 64, 0, 3, 67, 0);
        assertTrue(r.isValid(), r.error());
        List<ConveyorPath.Step> steps = r.steps();
        assertEquals(4, steps.size());
        for (ConveyorPath.Step s : steps) {
            assertEquals(ConveyorSlope.UP, s.slope);
        }
        // y ramps +1 per step
        assertEquals(64, steps.get(0).y);
        assertEquals(65, steps.get(1).y);
        assertEquals(66, steps.get(2).y);
        assertEquals(67, steps.get(3).y);
    }

    @Test
    void diagonal45Down_isValid() {
        ConveyorPath.Result r = ConveyorPath.plan(0, 64, 0, 0, 62, 2);
        assertTrue(r.isValid(), r.error());
        for (ConveyorPath.Step s : r.steps()) {
            assertEquals(ConveyorSlope.DOWN, s.slope);
        }
        assertEquals(62, r.steps().get(2).y);
    }

    @Test
    void nonStraightDiagonalHorizontal_isInvalid() {
        // both X and Z change => not a single axis
        ConveyorPath.Result r = ConveyorPath.plan(0, 64, 0, 3, 64, 3);
        assertFalse(r.isValid());
    }

    @Test
    void mixedSlopeNot45_isInvalid() {
        // 3 along X but only +1 Y => not a consistent 45-degree slope
        ConveyorPath.Result r = ConveyorPath.plan(0, 64, 0, 3, 65, 0);
        assertFalse(r.isValid());
    }

    @Test
    void samePosition_isInvalid() {
        ConveyorPath.Result r = ConveyorPath.plan(1, 64, 1, 1, 64, 1);
        assertFalse(r.isValid());
    }
}
