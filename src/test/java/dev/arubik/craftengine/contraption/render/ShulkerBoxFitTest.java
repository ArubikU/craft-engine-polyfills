package dev.arubik.craftengine.contraption.render;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.world.phys.AABB;

/**
 * The LOD cube fit ({@link ShulkerBoxFit}) and the tier hysteresis
 * ({@link ContraptionShulkerColliderSwarm#tierFor}) — both pure, so both testable without the
 * packet/entity machinery around them.
 */
class ShulkerBoxFitTest {

    /**
     * {@link ContraptionShulkerColliderSwarm}'s static initializer reflects {@code Entity.ENTITY_COUNTER},
     * which cannot resolve until the NMS registries are up. Without this the class fails to initialize and
     * every tier test errors — and it only appeared to pass before because some other test class in the
     * shared JVM happened to bootstrap first.
     */
    @BeforeAll
    static void bootstrapRegistries() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    private static final AABB FULL_BLOCK = new AABB(0, 0, 0, 1, 1, 1);
    private static final AABB BOTTOM_SLAB = new AABB(0, 0, 0, 1, 0.5, 1);
    /** The back half of a north-facing stair, on top of {@link #BOTTOM_SLAB}. */
    private static final AABB STAIR_STEP = new AABB(0, 0.5, 0.5, 1, 1, 1);
    /** A closed door's panel: full height and width, ~3/16 thick. */
    private static final AABB DOOR = new AABB(0, 0, 0, 1, 1, 0.1875);

    /** Every emitted cube must lie within the tolerance of the region it claims to cover. */
    private static void assertCovers(List<ShulkerBoxFit.Cube> cubes, AABB box, double tolerance) {
        for (ShulkerBoxFit.Cube c : cubes) {
            assertTrue(c.x0() >= box.minX - tolerance && c.x0() + c.size() <= box.maxX + tolerance
                    && c.y0() >= box.minY - tolerance && c.y0() + c.size() <= box.maxY + tolerance
                    && c.z0() >= box.minZ - tolerance && c.z0() + c.size() <= box.maxZ + tolerance,
                    "cube " + c + " spills outside " + box + " by more than " + tolerance);
        }
    }

    /** The union of the cubes must reach every extreme of the region — no uncovered corner. */
    private static void assertSpans(List<ShulkerBoxFit.Cube> cubes, AABB box) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
        for (ShulkerBoxFit.Cube c : cubes) {
            minX = Math.min(minX, c.x0());
            minY = Math.min(minY, c.y0());
            minZ = Math.min(minZ, c.z0());
            maxX = Math.max(maxX, c.x0() + c.size());
            maxY = Math.max(maxY, c.y0() + c.size());
            maxZ = Math.max(maxZ, c.z0() + c.size());
        }
        assertEquals(box.minX, minX, 1.0E-6);
        assertEquals(box.minY, minY, 1.0E-6);
        assertEquals(box.minZ, minZ, 1.0E-6);
        assertEquals(box.maxX, maxX, 1.0E-6);
        assertEquals(box.maxY, maxY, 1.0E-6);
        assertEquals(box.maxZ, maxZ, 1.0E-6);
    }

    @Test
    void farTier_isOneCubeSizedByHeight_matchingThePreLodBehaviour() {
        List<ShulkerBoxFit.Cube> cubes = ShulkerBoxFit.fit(List.of(BOTTOM_SLAB), 1);
        assertEquals(1, cubes.size());
        ShulkerBoxFit.Cube c = cubes.get(0);
        assertEquals(0.5, c.size(), 1.0E-9);
        assertEquals(0.0, c.y0(), 1.0E-9);
        assertEquals(0.5, c.centerX(), 1.0E-9);
        assertEquals(0.5, c.centerZ(), 1.0E-9);
    }

    @Test
    void fullBlock_isOneCubeAtEveryTier_becauseFourCannotImproveOnIt() {
        for (int budget : new int[] { 1, 4, 16 }) {
            List<ShulkerBoxFit.Cube> cubes = ShulkerBoxFit.fit(List.of(FULL_BLOCK), budget);
            assertEquals(1, cubes.size(), "budget " + budget);
            assertEquals(1.0, cubes.get(0).size(), 1.0E-9);
        }
    }

    @Test
    void slab_atFourCubes_tilesItsRealRegionExactly() {
        List<ShulkerBoxFit.Cube> cubes = ShulkerBoxFit.fit(List.of(BOTTOM_SLAB), 4);
        assertEquals(4, cubes.size());
        for (ShulkerBoxFit.Cube c : cubes) {
            assertEquals(0.5, c.size(), 1.0E-9);
        }
        assertCovers(cubes, BOTTOM_SLAB, 1.0E-9);
        assertSpans(cubes, BOTTOM_SLAB);
    }

    @Test
    void slab_atSixteen_staysAtFour_becauseFourIsAlreadyExact() {
        assertEquals(4, ShulkerBoxFit.fit(List.of(BOTTOM_SLAB), 16).size());
    }

    @Test
    void stair_atSixteen_coversBothRegionsWithoutFillingTheNotch() {
        List<ShulkerBoxFit.Cube> cubes = ShulkerBoxFit.fit(List.of(BOTTOM_SLAB, STAIR_STEP), 16);
        assertTrue(cubes.size() <= 16, "over budget: " + cubes.size());
        // The notch (the open quadrant above the tread, in front of the riser) is what a single
        // bounding-box collider wrongly fills — no cube may reach into it.
        AABB notch = new AABB(0, 0.5, 0, 1, 1, 0.5);
        for (ShulkerBoxFit.Cube c : cubes) {
            boolean intersects = c.x0() < notch.maxX && c.x0() + c.size() > notch.minX
                    && c.y0() < notch.maxY - 1.0E-9 && c.y0() + c.size() > notch.minY + 1.0E-9
                    && c.z0() < notch.maxZ - 1.0E-9 && c.z0() + c.size() > notch.minZ + 1.0E-9;
            assertTrue(!intersects, "cube " + c + " fills the stair's open notch");
        }
        assertSpans(cubes, new AABB(0, 0, 0, 1, 1, 1));
    }

    @Test
    void door_atSixteen_staysThin_ratherThanInflatingToAFullCube() {
        List<ShulkerBoxFit.Cube> cubes = ShulkerBoxFit.fit(List.of(DOOR), 16);
        assertTrue(cubes.size() <= 16, "over budget: " + cubes.size());
        for (ShulkerBoxFit.Cube c : cubes) {
            assertTrue(c.size() <= 0.25 + 1.0E-9, "cube of side " + c.size() + " is thicker than the door tier allows");
        }
        // The panel's FACE is reproduced exactly — that is the surface a player walks into.
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
        for (ShulkerBoxFit.Cube c : cubes) {
            minX = Math.min(minX, c.x0());
            maxX = Math.max(maxX, c.x0() + c.size());
            minY = Math.min(minY, c.y0());
            maxY = Math.max(maxY, c.y0() + c.size());
            minZ = Math.min(minZ, c.z0());
            maxZ = Math.max(maxZ, c.z0() + c.size());
        }
        assertEquals(0.0, minX, 1.0E-6);
        assertEquals(1.0, maxX, 1.0E-6);
        assertEquals(0.0, minY, 1.0E-6);
        assertEquals(1.0, maxY, 1.0E-6);
        // Thickness is the one axis that lands on a single cube, so it is the one axis that can
        // spill — and it spills SYMMETRICALLY (the cube is centred), never lopsidedly onto one face.
        double spillNear = DOOR.minZ - minZ;
        double spillFar = maxZ - DOOR.maxZ;
        assertEquals(spillNear, spillFar, 1.0E-6);
        assertTrue(spillNear >= 0.0 && spillNear < 0.05, "thickness spill of " + spillNear + " is too large");
    }

    @Test
    void everyTierRespectsItsBudget_forAShapeWithMoreRegionsThanCubes() {
        List<AABB> fence = List.of(
                new AABB(0.375, 0, 0.375, 0.625, 1.5, 0.625),
                new AABB(0.4375, 0.75, 0, 0.5625, 0.9375, 0.375),
                new AABB(0.4375, 0.75, 0.625, 0.5625, 0.9375, 1),
                new AABB(0.4375, 0.375, 0, 0.5625, 0.5625, 0.375),
                new AABB(0.4375, 0.375, 0.625, 0.5625, 0.5625, 1));
        assertEquals(1, ShulkerBoxFit.fit(fence, 1).size());
        assertTrue(ShulkerBoxFit.fit(fence, 4).size() <= 4);
        assertTrue(ShulkerBoxFit.fit(fence, 16).size() <= 16);
    }

    @Test
    void emptyGeometry_fitsNothing() {
        assertTrue(ShulkerBoxFit.fit(List.of(), 16).isEmpty());
    }

    @Test
    void tierFor_upgradesOnEnterThresholds_fromFar() {
        int far = ContraptionShulkerColliderSwarm.BUDGET_FAR;
        assertEquals(ContraptionShulkerColliderSwarm.BUDGET_NEAR,
                ContraptionShulkerColliderSwarm.tierFor(far, 0.9, 1.0));
        assertEquals(ContraptionShulkerColliderSwarm.BUDGET_MID,
                ContraptionShulkerColliderSwarm.tierFor(far, 2.0, 1.0));
        assertEquals(far, ContraptionShulkerColliderSwarm.tierFor(far, 3.2, 1.0));
    }

    @Test
    void tierFor_thresholdsScaleWithTheContraption() {
        int far = ContraptionShulkerColliderSwarm.BUDGET_FAR;
        // At scale 1 a viewer 1.8 blocks out is only MID; the cell's face is ~0.5 blocks from its
        // centre, so they are not close to it yet.
        assertEquals(ContraptionShulkerColliderSwarm.BUDGET_MID,
                ContraptionShulkerColliderSwarm.tierFor(far, 1.8, 1.0));
        // The SAME 1.8 blocks against a scale-2 cell is NEAR: that cell is two blocks across, so its
        // face is already a full block from its centre and the viewer is effectively touching it.
        assertEquals(ContraptionShulkerColliderSwarm.BUDGET_NEAR,
                ContraptionShulkerColliderSwarm.tierFor(far, 1.8, 2.0));
        // Scale 2 doubles every threshold, so MID reaches twice as far too.
        assertEquals(ContraptionShulkerColliderSwarm.BUDGET_MID,
                ContraptionShulkerColliderSwarm.tierFor(far, 5.0, 2.0));
        assertEquals(far, ContraptionShulkerColliderSwarm.tierFor(far, 5.0, 1.0));
        // A shrunk contraption tightens them the same way.
        assertEquals(far, ContraptionShulkerColliderSwarm.tierFor(far, 1.8, 0.5));
    }

    @Test
    void tierFor_holdsTheCurrentTierInsideTheHysteresisBand() {
        // 2.0 is past NEAR_ENTER but short of NEAR_EXIT: a cell already NEAR stays NEAR, while one
        // that is only MID does not yet upgrade. That gap is what stops boundary-standing thrash.
        assertEquals(ContraptionShulkerColliderSwarm.BUDGET_NEAR,
                ContraptionShulkerColliderSwarm.tierFor(ContraptionShulkerColliderSwarm.BUDGET_NEAR, 2.0, 1.0));
        assertEquals(ContraptionShulkerColliderSwarm.BUDGET_MID,
                ContraptionShulkerColliderSwarm.tierFor(ContraptionShulkerColliderSwarm.BUDGET_MID, 2.0, 1.0));
        // Same story at the MID/FAR boundary.
        assertEquals(ContraptionShulkerColliderSwarm.BUDGET_MID,
                ContraptionShulkerColliderSwarm.tierFor(ContraptionShulkerColliderSwarm.BUDGET_MID, 3.2, 1.0));
        assertEquals(ContraptionShulkerColliderSwarm.BUDGET_FAR,
                ContraptionShulkerColliderSwarm.tierFor(ContraptionShulkerColliderSwarm.BUDGET_FAR, 3.2, 1.0));
    }

    @Test
    void tierFor_dropsAllTheWayToNoColliderWhenFarPastEveryExit() {
        assertEquals(ContraptionShulkerColliderSwarm.BUDGET_NONE,
                ContraptionShulkerColliderSwarm.tierFor(ContraptionShulkerColliderSwarm.BUDGET_NEAR, 10.0, 1.0));
    }

    @Test
    void tierFor_stopsRenderingAColliderPastFarExit() {
        int none = ContraptionShulkerColliderSwarm.BUDGET_NONE;
        // Inside FAR_ENTER a player can still walk into the cell within a tick or two, so something
        // solid has to be there.
        assertEquals(ContraptionShulkerColliderSwarm.BUDGET_FAR,
                ContraptionShulkerColliderSwarm.tierFor(none, 4.0, 1.0));
        // Past it there is nothing to collide with, so the collider is despawned outright rather than
        // kept as an entity nobody can reach.
        assertEquals(none, ContraptionShulkerColliderSwarm.tierFor(none, 5.5, 1.0));
        // ...and a cell already showing FAR holds until FAR_EXIT, not FAR_ENTER — the same band.
        assertEquals(ContraptionShulkerColliderSwarm.BUDGET_FAR,
                ContraptionShulkerColliderSwarm.tierFor(ContraptionShulkerColliderSwarm.BUDGET_FAR, 4.6, 1.0));
        // Scale carries the cutoff with it: a scale-2 contraption is still worth a collider at 8.
        assertEquals(ContraptionShulkerColliderSwarm.BUDGET_FAR,
                ContraptionShulkerColliderSwarm.tierFor(none, 8.0, 2.0));
    }

    @Test
    void tierFor_highestTierIsInPlaceBeforeThePlayerArrives() {
        int none = ContraptionShulkerColliderSwarm.BUDGET_NONE;
        // The point of NEAR_ENTER being 1.5 rather than 1.0: a player ~0.6 wide with reach is already
        // interacting at 1.4, so the top tier must be up BEFORE they get there — upgrading at exactly
        // 1.0 flipped the collider's shape underfoot at the moment of contact.
        assertEquals(ContraptionShulkerColliderSwarm.BUDGET_NEAR,
                ContraptionShulkerColliderSwarm.tierFor(none, 1.4, 1.0));
    }

    @Test
    void fullBlock_axisAligned_staysOneCube() {
        // One cube IS the block when the body is on the grid — spending 16 buys nothing and costs 16
        // fake entities per viewer.
        assertEquals(1, ShulkerBoxFit.fit(List.of(FULL_BLOCK), 16, false).size());
        assertEquals(1, ShulkerBoxFit.fit(List.of(FULL_BLOCK), 4, false).size());
    }

    @Test
    void fullBlock_offAxis_spendsTheBudget() {
        // Turned off the grid, one axis-aligned cube cannot describe a rotated cube: it bulges on the
        // diagonals and gaps on the faces. Smaller cubes trace it closer, so the budget is worth paying.
        List<ShulkerBoxFit.Cube> near = ShulkerBoxFit.fit(List.of(FULL_BLOCK), 16, true);
        assertTrue(near.size() > 1, "an off-axis full block must subdivide, got " + near.size());
        assertTrue(near.size() <= 16, "must not exceed the budget, got " + near.size());
        // 2x2x2 is the finest grid that fits 16 (3x3x3 = 27 would not), so each cube is half a block.
        assertEquals(8, near.size());

        // MID cannot, and that is arithmetic rather than a shortcoming: equal cubes tile a cube only as
        // n^3 — 1, 8, 27 — so the next step up from one cube is EIGHT, and a budget of 4 cannot buy it.
        // It correctly falls back to the single cube rather than leaving part of the block uncovered.
        assertEquals(1, ShulkerBoxFit.fit(List.of(FULL_BLOCK), 4, true).size());
    }

    @Test
    void offAxisFit_stillCoversTheBlock() {
        // Subdividing must not leave a hole — a gap is a player falling through the deck.
        assertCovers(ShulkerBoxFit.fit(List.of(FULL_BLOCK), 16, true), FULL_BLOCK, 1.0E-6);
    }
}
