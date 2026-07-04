package dev.arubik.craftengine.contraption.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Pure damage/stall math for {@link MiningMath}. */
class MiningMathTest {

    @Test
    void unbreakableBlockNeverTakesDamage() {
        assertEquals(0.0, MiningMath.damagePerTick(100, -1));
        assertFalse(MiningMath.isBroken(1000, -1));
    }

    @Test
    void zeroHardnessBreaksInstantly() {
        assertEquals(Double.MAX_VALUE, MiningMath.damagePerTick(1, 0));
        assertTrue(MiningMath.isBroken(0.001, 0));
    }

    @Test
    void takesExactlyHardnessOverRpmTicksToBreak() {
        double rpm = 2.0, hardness = 10.0;
        double perTick = MiningMath.damagePerTick(rpm, hardness);
        assertEquals(0.2, perTick, 1e-9);

        double accumulated = 0;
        int ticks = 0;
        while (!MiningMath.isBroken(accumulated, hardness)) {
            accumulated += perTick;
            ticks++;
        }
        // Floating-point accumulation of 0.2 fifty times may land a hair under 10.0, needing
        // one extra tick — assert the theoretical count within a tick of slack, not exact equality.
        assertTrue(ticks == 50 || ticks == 51, "expected ~50 ticks, got " + ticks);
    }

    @Test
    void notBrokenBeforeAccumulatedReachesHardness() {
        assertFalse(MiningMath.isBroken(9.99, 10.0));
        assertTrue(MiningMath.isBroken(10.0, 10.0));
        assertTrue(MiningMath.isBroken(10.01, 10.0));
    }

    @Test
    void crackStageRampsFromZeroToNine() {
        double hardness = 10.0;
        assertEquals(0, MiningMath.crackStage(0.0, hardness));
        assertEquals(0, MiningMath.crackStage(0.99, hardness));
        assertEquals(1, MiningMath.crackStage(1.0, hardness));
        assertEquals(5, MiningMath.crackStage(5.0, hardness));
        assertEquals(9, MiningMath.crackStage(9.99, hardness));
        assertEquals(9, MiningMath.crackStage(10.0, hardness), "clamped at 9 even once fully broken");
    }

    @Test
    void crackStageOfZeroHardnessIsZero() {
        assertEquals(0, MiningMath.crackStage(0.0, 0.0));
    }
}
