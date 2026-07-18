package dev.arubik.craftengine.fluid.behavior;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

/**
 * Covers {@link FluidTankRender#scaledCellBox} — the pure half of the 2026-07-16 fix for "the liquid render
 * on fluid block tank and outside of it when in a contraption dont inherit the scale". The packet/viewer half
 * of that path needs a live server and is not unit-testable here; this pins the arithmetic contract the fix
 * rests on.
 */
class FluidTankRenderScaleTest {

    /** The cell box a 1-wide tank actually stores: hull-inset on x/z, a full block on y. */
    private static final float HULL = 1f / 16f + 1f / 128f;

    private static float[] singleCellBox() {
        return new float[] { 1f - 2f * HULL, 1f, 1f - 2f * HULL };
    }

    @Test
    void scaleOfOneLeavesTheBoxUntouched() {
        float[] base = singleCellBox();
        assertArrayEquals(base, FluidTankRender.scaledCellBox(base, 1.0), 0f,
                "a never-scaled contraption (and every free-standing tank) must emit the pre-fix box exactly");
    }

    @Test
    void growingScalesEveryAxisByTheSameFactor() {
        float[] base = singleCellBox();
        float[] got = FluidTankRender.scaledCellBox(base, 3.0);
        assertArrayEquals(new float[] { base[0] * 3f, 3f, base[2] * 3f }, got, 1e-6f);
    }

    @Test
    void shrinkingScalesEveryAxisByTheSameFactor() {
        float[] base = singleCellBox();
        float[] got = FluidTankRender.scaledCellBox(base, 0.25);
        assertArrayEquals(new float[] { base[0] * 0.25f, 0.25f, base[2] * 0.25f }, got, 1e-6f);
    }

    /**
     * The hull inset must stay PROPORTIONAL to the box, not survive as a fixed real-world margin: the tank
     * walls it hides inside are themselves scaled, so a slab scaled on x/z by anything other than the same
     * factor as y would either poke through the (scaled) frame or float away from it.
     */
    @Test
    void hullInsetStaysProportionalAtAnyScale() {
        float[] base = singleCellBox();
        for (double s : new double[] { 0.1, 0.5, 2.0, 10.0 }) {
            float[] got = FluidTankRender.scaledCellBox(base, s);
            org.junit.jupiter.api.Assertions.assertEquals(base[0] / base[1], got[0] / got[1], 1e-5f,
                    "x:y aspect must be scale-invariant at s=" + s);
            org.junit.jupiter.api.Assertions.assertEquals(base[2] / base[1], got[2] / got[1], 1e-5f,
                    "z:y aspect must be scale-invariant at s=" + s);
        }
    }

    /**
     * The stored base is the LIVE source re-read every tick (see {@code GroupBoxes#localScales}); scaling it
     * in place would compound the factor once per tick and inflate the fluid without bound while a wand-set
     * scale simply sits still.
     */
    @Test
    void doesNotMutateTheStoredBase() {
        float[] base = singleCellBox();
        float[] snapshot = base.clone();
        float[] got = FluidTankRender.scaledCellBox(base, 4.0);
        assertNotSame(base, got);
        assertArrayEquals(snapshot, base, 0f, "the caller's stored base must survive the call unchanged");
    }

    /** Repeated resolution from the same base is idempotent — the tick loop calls this every tick forever. */
    @Test
    void repeatedResolutionFromTheSameBaseIsStable() {
        float[] base = singleCellBox();
        float[] first = FluidTankRender.scaledCellBox(base, 2.5);
        for (int i = 0; i < 100; i++) {
            assertArrayEquals(first, FluidTankRender.scaledCellBox(base, 2.5), 0f);
        }
    }
}
