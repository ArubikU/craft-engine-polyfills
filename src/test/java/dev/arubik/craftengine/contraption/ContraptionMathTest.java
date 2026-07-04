package dev.arubik.craftengine.contraption;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/** Pure bearing-relative coordinate math for {@link ContraptionMath}. */
class ContraptionMathTest {

    @Test
    void toLocalIsWorldMinusBearing() {
        BlockPos world = new BlockPos(10, 5, -3);
        BlockPos bearing = new BlockPos(7, 5, -3);
        assertEquals(new BlockPos(3, 0, 0), ContraptionMath.toLocal(world, bearing));
    }

    @Test
    void toWorldIsInverseOfToLocal() {
        BlockPos bearing = new BlockPos(7, 5, -3);
        BlockPos original = new BlockPos(10, 8, 2);
        BlockPos local = ContraptionMath.toLocal(original, bearing);
        assertEquals(original, ContraptionMath.toWorld(local, bearing));
    }

    @Test
    void renderPositionAtZeroYawIsPlainTranslation() {
        BlockPos local = new BlockPos(2, 1, 3);
        Vec3 bearing = new Vec3(100, 64, 200);
        Vec3 result = ContraptionMath.renderPosition(local, bearing, 0.0);
        assertEquals(102.0, result.x, 1e-9);
        assertEquals(65.0, result.y, 1e-9);
        assertEquals(203.0, result.z, 1e-9);
    }

    @Test
    void renderPositionAtQuarterTurnRotatesAroundBearingCenterNotCorner() {
        // 90 degrees (PI/2), pivoting around the bearing block's CENTER (0.5,0.5) — not its
        // corner (0,0) — see ContraptionMath#PIVOT_XZ's javadoc (2026-07-02 pivot fix). Local
        // (1,0,0) is centered to (0.5,-0.5), rotated to (0.5,0.5), then un-centered back to
        // world (1.0,0,1.0) — verified by hand against rotateYaw's rx = x*cos - z*sin,
        // rz = x*sin + z*cos convention.
        BlockPos local = new BlockPos(1, 0, 0);
        Vec3 bearing = Vec3.ZERO;
        Vec3 result = ContraptionMath.renderPosition(local, bearing, Math.PI / 2.0);
        assertEquals(1.0, result.x, 1e-9);
        assertEquals(0.0, result.y, 1e-9);
        assertEquals(1.0, result.z, 1e-9);
    }

    @Test
    void renderPositionOfBearingsOwnCellOrbitsItsCenterNotItsCorner() {
        // The bearing's own cell (localOffset = 0,0,0, i.e. its corner) is NOT the rotation
        // pivot itself anymore — it's 0.5 blocks away from the true pivot (the block's center),
        // so at a 90-degree turn it visibly moves rather than staying frozen at the bearing's
        // raw world position. This is the direct fix for "gira sobre una esquina y no sobre el
        // medio del bloque": before this fix, local (0,0,0) rotated to itself trivially (a
        // rotation of the zero vector is always the zero vector), silently masking the fact
        // that the pivot itself was wrong — every other cell orbited that same wrong corner.
        BlockPos local = new BlockPos(0, 0, 0);
        Vec3 bearing = new Vec3(100, 64, 200);
        Vec3 result = ContraptionMath.renderPosition(local, bearing, Math.PI / 2.0);
        // centered (-0.5,-0.5) -> rotated (0.5,-0.5) -> world (100.5+0.5, 64, 200.5-0.5)
        assertEquals(101.0, result.x, 1e-9);
        assertEquals(64.0, result.y, 1e-9);
        assertEquals(200.0, result.z, 1e-9);
    }

    @Test
    void realToLocalIsInverseOfRenderPositionAtVariousYaws() {
        Vec3 bearing = new Vec3(12.5, 70, -8.25);
        double[] yaws = { 0.0, Math.PI / 2.0, Math.PI, 3 * Math.PI / 2.0, 0.37, -1.9 };
        BlockPos[] locals = { new BlockPos(0, 0, 0), new BlockPos(1, 0, 0), new BlockPos(2, 1, 3), new BlockPos(-1, 0, -2) };
        for (double yaw : yaws) {
            for (BlockPos local : locals) {
                Vec3 real = ContraptionMath.renderPosition(local, bearing, yaw);
                Vec3 backToLocal = ContraptionMath.realToLocal(real, bearing, yaw);
                assertEquals(local.getX(), backToLocal.x, 1e-9);
                assertEquals(local.getY(), backToLocal.y, 1e-9);
                assertEquals(local.getZ(), backToLocal.z, 1e-9);
            }
        }
    }

    @Test
    void gridSnapRoundsToNearestBlock() {
        assertEquals(new BlockPos(3, 64, -2), ContraptionMath.gridSnap(new Vec3(3.4, 64.1, -1.6)));
        assertEquals(new BlockPos(4, 64, -1), ContraptionMath.gridSnap(new Vec3(3.6, 64.4, -0.6)));
    }
}
