package dev.arubik.craftengine.chainery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The rope constraint that ties two phys endpoints (CHAINERY phase 2). These prove the three behaviours the
 * feature promises: a slack chain does nothing, a stretched chain pulls its ends back to its span, and a
 * chain yanked harder than its {@code maxTension} snaps.
 */
class ChainPhysicsTest {

    private static final double DT = 1.0;

    @Test
    @DisplayName("a slack chain applies no force — endpoints closer than its span move freely")
    void slackChainDoesNothing() {
        // span 5, ends 3 apart: slack. No pull, no break.
        ChainPhysics.RopeResult r = ChainPhysics.resolve(3.0, 5.0, 0.0, 0.0, 2.0, 1.0, 60.0, DT);
        assertEquals(0.0, r.impulse(), 1.0e-9, "a slack chain must not pull");
        assertFalse(r.broke(), "a slack chain must not break");
    }

    @Test
    @DisplayName("a rigid chain holds two ends that are being driven apart at its span")
    void stretchedChainHoldsAgainstAPull() {
        // Two unit-mass point bodies driven steadily apart (a contraption trying to fly off), tethered by a
        // rigid (stretch 0) span of 5, started 9 apart. Without the chain the constant push would separate
        // them without bound; the chain must keep them taut around its span. This is the tether case.
        double maxLen = 5.0;
        double invMass = 1.0;
        double posA = 0.0, posB = 9.0, velA = 0.0, velB = 0.0;
        final double drive = 0.1; // constant outward push per tick on each end

        for (int tick = 0; tick < 600; tick++) {
            velA -= drive; // A pushed toward -x
            velB += drive; // B pushed toward +x (apart)
            double dist = posB - posA;
            double separating = velB - velA; // +axis A->B, so (vB - vA) is the separation rate
            ChainPhysics.RopeResult r = ChainPhysics.resolve(dist, maxLen, 0.0, separating,
                    invMass + invMass, 1.0, 0.0 /* unbreakable */, DT);
            velA += r.impulse() * invMass; // A pulled toward B
            velB -= r.impulse() * invMass; // B pulled toward A
            velA *= 0.9; // stand-in for the solver's damping — settle instead of ring forever
            velB *= 0.9;
            posA += velA * DT;
            posB += velB * DT;
        }

        double finalDist = posB - posA;
        assertTrue(finalDist > maxLen - 0.5 && finalDist < maxLen + 1.0,
                "a rigid chain must hold a driven-apart pair taut near its span (5); they settled at " + finalDist);
    }

    @Test
    @DisplayName("a chain yanked harder than its max tension snaps")
    void overTensionBreaksTheChain() {
        // Ends already past the span AND ripping apart fast: the impulse needed to hold them exceeds maxTension.
        double dist = 8.0;
        double maxLen = 5.0;
        double separating = 20.0; // violent pull-apart
        ChainPhysics.RopeResult r = ChainPhysics.resolve(dist, maxLen, 0.0, separating, 2.0, 1.0, 5.0, DT);
        assertTrue(r.broke(), "a chain pulled past its max tension must break; impulse=" + r.impulse());

        // The SAME stretch under a gentle pull (and a higher tension rating) must NOT break.
        ChainPhysics.RopeResult gentle = ChainPhysics.resolve(dist, maxLen, 0.0, 0.1, 2.0, 0.2, 60.0, DT);
        assertFalse(gentle.broke(), "a gently-loaded chain must not break; impulse=" + gentle.impulse());
    }

    @Test
    @DisplayName("stretch lets a rope overshoot its span before it resists; iron does not")
    void stretchAllowsOvershoot() {
        // Distance 6, span 5. Iron (stretch 0) is already taut -> pulls. A rope with 0.3 stretch tolerates up
        // to 6.5 -> still slack here.
        ChainPhysics.RopeResult iron = ChainPhysics.resolve(6.0, 5.0, 0.0, 0.0, 2.0, 1.0, 60.0, DT);
        ChainPhysics.RopeResult rope = ChainPhysics.resolve(6.0, 5.0, 0.3, 0.0, 2.0, 1.0, 60.0, DT);
        assertTrue(iron.impulse() > 0.0, "rigid iron at 6 > span 5 must already pull");
        assertEquals(0.0, rope.impulse(), 1.0e-9, "a 0.3-stretch rope tolerates 6 <= 6.5 and stays slack");
    }
}
