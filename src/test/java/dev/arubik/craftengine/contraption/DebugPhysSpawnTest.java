package dev.arubik.craftengine.contraption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

/**
 * The placement rules behind {@code /cep contraption spawn} — where a debug-spawned body lands and
 * how a structure template is seated on that anchor.
 *
 * <p>Pure: both helpers take plain vectors, so the rules are provable without a server or a level.
 */
class DebugPhysSpawnTest {

    private static final double EPS = 1.0e-9;

    @Test
    @DisplayName("anchor sits in front of the player along their horizontal heading")
    void anchorIsInFront() {
        Vec3 eye = new Vec3(10, 70, 20);
        Vec3 anchor = DebugPhysSpawn.spawnAnchor(eye, new Vec3(1, 0, 0));
        assertEquals(13.0, anchor.x, EPS);
        assertEquals(20.0, anchor.z, EPS);
        assertTrue(anchor.y > eye.y, "body must be lifted above the eye so it has room to fall");
    }

    @Test
    @DisplayName("looking at the floor still places the body in front, never underground")
    void steepLookIsFlattened() {
        Vec3 eye = new Vec3(0, 70, 0);
        // Aiming mostly down, slightly north: the pitch must be discarded entirely.
        Vec3 anchor = DebugPhysSpawn.spawnAnchor(eye, new Vec3(0, -0.99, -0.14).normalize());
        assertEquals(0.0, anchor.x, EPS);
        assertEquals(-3.0, anchor.z, EPS);
        assertTrue(anchor.y > eye.y, "a downward look must not bury the body");
    }

    @Test
    @DisplayName("look with no horizontal component falls back to a fixed heading instead of NaN")
    void straightUpDoesNotDivideByZero() {
        Vec3 anchor = DebugPhysSpawn.spawnAnchor(new Vec3(0, 70, 0), new Vec3(0, 1, 0));
        assertTrue(Double.isFinite(anchor.x) && Double.isFinite(anchor.z));
        assertEquals(3.0, Math.hypot(anchor.x, anchor.z), EPS);
    }

    @Test
    @DisplayName("a structure is centered horizontally on the anchor but stands on it vertically")
    void centerOffsetCentersXZOnly() {
        assertEquals(new BlockPos(-3, 0, -3), DebugPhysSpawn.centerOffset(new Vec3i(7, 5, 7)));
        assertEquals(new BlockPos(0, 0, 0), DebugPhysSpawn.centerOffset(new Vec3i(1, 1, 1)));
        // Even spans cannot be centered exactly; the shift must still stay within the footprint.
        assertEquals(new BlockPos(-1, 0, -2), DebugPhysSpawn.centerOffset(new Vec3i(4, 2, 6)));
    }
}
