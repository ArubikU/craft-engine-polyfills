package dev.arubik.craftengine.multiblock;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Pure geometry for the "right-click ANY block of the structure to assemble it" detection
 * ({@link MultiBlockGeometry}), without needing a server / NMS.
 */
class MultiBlockGeometryTest {

    // ---- rotate ----

    @Test
    void rotateNorthIsIdentity() {
        assertArrayEquals(new int[] { 2, 3, -1 }, MultiBlockGeometry.rotate(2, 3, -1, 0));
    }

    @Test
    void rotateMatchesLegacySwitch() {
        // NORTH (0,0,-1) maps: EAST->(+1,0,0), SOUTH->(0,0,1), WEST->(-1,0,0)
        assertArrayEquals(new int[] { 1, 0, 0 }, MultiBlockGeometry.rotate(0, 0, -1, 1)); // EAST
        assertArrayEquals(new int[] { 0, 0, 1 }, MultiBlockGeometry.rotate(0, 0, -1, 2)); // SOUTH
        assertArrayEquals(new int[] { -1, 0, 0 }, MultiBlockGeometry.rotate(0, 0, -1, 3)); // WEST
    }

    @Test
    void rotateKeepsYAxis() {
        for (int f = 0; f < 4; f++)
            org.junit.jupiter.api.Assertions.assertEquals(7, MultiBlockGeometry.rotate(2, 7, -3, f)[1],
                    "y must be preserved for facing " + f);
    }

    // ---- candidate cores: the core complaint — click any cell, find the core ----

    private static List<int[]> cube(int w, int h, int d) {
        List<int[]> cells = new ArrayList<>();
        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                for (int z = 0; z < d; z++)
                    cells.add(new int[] { x, y, z });
        return cells;
    }

    private static boolean contains(List<int[]> list, int[] want) {
        for (int[] c : list)
            if (c[0] == want[0] && c[1] == want[1] && c[2] == want[2])
                return true;
        return false;
    }

    @Test
    void clickingAnyCellOfMultipageChestFindsCenterCore() {
        // Multipage chest: 3x3x3, core at center (1,1,1). Core placed at world (10,64,10), NORTH.
        int[] coreOffset = { 1, 1, 1 };
        int[] worldCore = { 10, 64, 10 };
        List<int[]> cells = cube(3, 3, 3);
        // For EVERY cell, the block that occupies it sits at worldCore + (cell - coreOffset).
        for (int[] cell : cells) {
            int[] clicked = { worldCore[0] + (cell[0] - coreOffset[0]),
                    worldCore[1] + (cell[1] - coreOffset[1]),
                    worldCore[2] + (cell[2] - coreOffset[2]) };
            List<int[]> cand = MultiBlockGeometry.candidateCores(cells, coreOffset, clicked, 0);
            assertTrue(contains(cand, worldCore),
                    "clicking cell " + cell[0] + "," + cell[1] + "," + cell[2] + " must yield the center core");
        }
    }

    @Test
    void clickingTopBlockOfPressurizerTowerFindsBottomCenterCore() {
        // Pressurizer: 3x3x6, core at bottom-centre (1,0,1). Core at world (0,70,0).
        int[] coreOffset = { 1, 0, 1 };
        int[] worldCore = { 0, 70, 0 };
        List<int[]> cells = cube(3, 6, 3);
        // Click the top-centre block (cell 1,5,1) -> 5 above the core.
        int[] clickedTop = { 0, 75, 0 };
        List<int[]> cand = MultiBlockGeometry.candidateCores(cells, coreOffset, clickedTop, 0);
        assertTrue(contains(cand, worldCore), "clicking the top of the tower must still find the bottom core");
        // Click a top corner (cell 0,5,0).
        int[] clickedCorner = { -1, 75, -1 };
        assertTrue(contains(MultiBlockGeometry.candidateCores(cells, coreOffset, clickedCorner, 0), worldCore),
                "clicking a top corner must find the bottom core");
    }

    @Test
    void candidateCoresHonorRotation() {
        // Vertical 1x2 (refinery-like): core at origin, one part above (0,1,0). Click the part.
        List<int[]> cells = List.of(new int[] { 0, 0, 0 }, new int[] { 0, 1, 0 });
        int[] coreOffset = { 0, 0, 0 };
        int[] worldCore = { 5, 64, 5 };
        // The part is directly above regardless of horizontal facing (y axis unaffected).
        int[] clickedPart = { 5, 65, 5 };
        for (int f = 0; f < 4; f++)
            assertTrue(contains(MultiBlockGeometry.candidateCores(cells, coreOffset, clickedPart, f), worldCore),
                    "facing " + f + ": clicking the upper part must find the core below");
    }

    @Test
    void candidateCoresDedupe() {
        // A single-cell schema yields exactly one candidate (no duplicate from coreOffset + parts).
        List<int[]> cells = List.of(new int[] { 0, 0, 0 });
        List<int[]> cand = MultiBlockGeometry.candidateCores(cells, new int[] { 0, 0, 0 }, new int[] { 3, 3, 3 }, 0);
        org.junit.jupiter.api.Assertions.assertEquals(1, cand.size());
        assertArrayEquals(new int[] { 3, 3, 3 }, cand.get(0));
    }
}
