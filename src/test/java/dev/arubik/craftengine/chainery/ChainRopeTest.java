package dev.arubik.craftengine.chainery;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Vector3d;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The verlet chain/rope (CHAINERY phase 3). Proves the physical behaviours the feature promises: a slack rope
 * sags under gravity, a rope over a floor rests ON it instead of falling through, and a rope pulled taut
 * between far-apart ends straightens out.
 */
class ChainRopeTest {

    private static final double GRAVITY = -0.04;
    private static final double DAMPING = 0.98;
    private static final int ITERS = 32;

    private static ChainRope settle(ChainRope rope, Vector3d a, Vector3d b, int segments, ChainRope.Terrain t,
            int ticks) {
        for (int i = 0; i < ticks; i++) {
            rope.step(a, b, segments, ITERS, GRAVITY, DAMPING, t);
        }
        return rope;
    }

    @Test
    @DisplayName("a slack rope sags below its endpoints under gravity")
    void slackRopeSags() {
        // Ends level at y=10, 4 apart, but 10 links of rope between them -> lots of slack.
        Vector3d a = new Vector3d(0, 10, 0);
        Vector3d b = new Vector3d(4, 10, 0);
        ChainRope rope = new ChainRope();
        settle(rope, a, b, 10, ChainRope.Terrain.EMPTY, 300);

        double lowest = Double.POSITIVE_INFINITY;
        for (int i = 0; i < rope.particleCount(); i++) {
            lowest = Math.min(lowest, rope.particle(i).y);
        }
        assertTrue(lowest < 9.0, "a slack rope must sag well below its endpoints (y=10); lowest was " + lowest);
    }

    @Test
    @DisplayName("a slack rope over a floor rests on it, not through it")
    void ropeRestsOnFloor() {
        // Floor fills everything at y < 5 (blocks with by <= 4 are solid). Ends anchored at y=8.
        ChainRope.Terrain floor = (x, y, z) -> y <= 4;
        Vector3d a = new Vector3d(0, 8, 0);
        Vector3d b = new Vector3d(3, 8, 0);
        ChainRope rope = new ChainRope();
        settle(rope, a, b, 20, floor, 400); // very slack -> wants to pile on the floor

        double lowest = Double.POSITIVE_INFINITY;
        for (int i = 0; i < rope.particleCount(); i++) {
            lowest = Math.min(lowest, rope.particle(i).y);
        }
        // The floor's top face is y=5; the rope must not sink below it.
        assertTrue(lowest >= 5.0 - 1.0e-6, "the rope must rest on the floor (y>=5), but a particle reached " + lowest);
        assertTrue(lowest < 6.0, "a very slack rope should actually reach the floor; lowest was " + lowest);
    }

    @Test
    @DisplayName("a rope pulled taut between far ends straightens toward a straight line")
    void tautRopeStraightens() {
        // 5 links but ends 5 apart on a line -> essentially taut, should be nearly straight (little sag).
        Vector3d a = new Vector3d(0, 10, 0);
        Vector3d b = new Vector3d(5, 10, 0);
        ChainRope rope = new ChainRope();
        settle(rope, a, b, 5, ChainRope.Terrain.EMPTY, 300);

        double maxSag = 0.0;
        for (int i = 0; i < rope.particleCount(); i++) {
            maxSag = Math.max(maxSag, 10.0 - rope.particle(i).y);
        }
        assertTrue(maxSag < 0.6, "a taut rope should stay nearly straight (little sag); max sag was " + maxSag);
    }
}
