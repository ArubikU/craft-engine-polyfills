package dev.arubik.craftengine.pipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests for the pure 3D pipe route planner {@link PipeRoute}. */
class PipeRouteTest {

    private static final PipeRoute.CellValidator ALL_FREE = (x, y, z) -> true;

    private static PipeRoute.Waypoint wp(int x, int y, int z) {
        return new PipeRoute.Waypoint(x, y, z);
    }

    @Test
    void straightLine_alongX() {
        PipeRoute.Result r = PipeRoute.straight(wp(0, 64, 0), wp(3, 64, 0), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        List<PipeRoute.Cell> c = r.cells();
        assertEquals(4, c.size());
        assertEquals(new PipeRoute.Cell(0, 64, 0), c.get(0));
        assertEquals(new PipeRoute.Cell(3, 64, 0), c.get(3));
    }

    @Test
    void straightLine_alongY() {
        PipeRoute.Result r = PipeRoute.straight(wp(0, 64, 0), wp(0, 67, 0), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        assertEquals(4, r.cells().size());
        assertEquals(67, r.cells().get(3).y);
    }

    @Test
    void straight_rejectsDiagonal() {
        PipeRoute.Result r = PipeRoute.straight(wp(0, 64, 0), wp(3, 64, 2), ALL_FREE);
        assertFalse(r.isValid());
        assertEquals("polyfill.pipewand.err_not_straight", r.errorKey());
    }

    @Test
    void straight_rejectsSamePos() {
        PipeRoute.Result r = PipeRoute.straight(wp(0, 64, 0), wp(0, 64, 0), ALL_FREE);
        assertFalse(r.isValid());
        assertEquals("polyfill.pipewand.err_same_pos", r.errorKey());
    }

    @Test
    void straight_blockedIntermediateFails() {
        PipeRoute.CellValidator v = (x, y, z) -> !(x == 1 && y == 64 && z == 0);
        PipeRoute.Result r = PipeRoute.straight(wp(0, 64, 0), wp(3, 64, 0), v);
        assertFalse(r.isValid());
        assertEquals("polyfill.pipewand.err_blocked", r.errorKey());
        assertTrue(r.error().contains("1,64,0"), r.error());
    }

    @Test
    void magic_straightShortestPath() {
        PipeRoute.Result r = PipeRoute.magic(wp(0, 64, 0), wp(3, 64, 0), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        // Shortest path along a clear axis is the straight line (4 cells).
        assertEquals(4, r.cells().size());
        assertEquals(new PipeRoute.Cell(0, 64, 0), r.cells().get(0));
        assertEquals(new PipeRoute.Cell(3, 64, 0), r.cells().get(r.cells().size() - 1));
    }

    @Test
    void magic_lBend_routesAround() {
        // A→B diagonal; shortest Manhattan path has length |dx|+|dz| = 5 -> 6 cells.
        PipeRoute.Result r = PipeRoute.magic(wp(0, 64, 0), wp(3, 64, 2), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        assertEquals(6, r.cells().size());
        assertEquals(new PipeRoute.Cell(0, 64, 0), r.cells().get(0));
        assertEquals(new PipeRoute.Cell(3, 64, 2), r.cells().get(5));
        assertNoDuplicates(r.cells());
        assertContiguous(r.cells());
    }

    @Test
    void magic_detoursAroundWall() {
        // A wall blocks the direct +X path at x==1 for z==0 over a tall column; the path must
        // detour through z==1 and still reach B.
        PipeRoute.CellValidator v = (x, y, z) -> !(x == 1 && z == 0 && y >= 63 && y <= 66);
        PipeRoute.Result r = PipeRoute.magic(wp(0, 64, 0), wp(2, 64, 0), v);
        assertTrue(r.isValid(), r.error());
        for (PipeRoute.Cell c : r.cells())
            assertFalse(c.x == 1 && c.z == 0 && c.y == 64, "route entered blocked cell: " + c);
        assertContiguous(r.cells());
    }

    @Test
    void magic_noRouteWhenFullyBlocked() {
        // B is fully walled off within the search box.
        PipeRoute.CellValidator v = (x, y, z) -> !((Math.abs(x - 5) <= 0) && true) && true
                ? !(x == 4) // wall at x==4 isolates x>=5
                : false;
        PipeRoute.Result r = PipeRoute.magic(wp(0, 64, 0), wp(8, 64, 0),
                (x, y, z) -> x != 4); // impenetrable plane at x==4
        assertFalse(r.isValid());
        assertEquals("polyfill.pipewand.err_no_route", r.errorKey());
    }

    @Test
    void pointed_chainsLegsAndDedupsJoin() {
        // (0,64,0) -> (3,64,0) straight, then -> (3,64,3) straight. Shared join (3,64,0) not duped.
        PipeRoute.Result r = PipeRoute.pointed(
                List.of(wp(0, 64, 0), wp(3, 64, 0), wp(3, 64, 3)), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        // 4 + 4 cells - 1 shared = 7.
        assertEquals(7, r.cells().size());
        assertNoDuplicates(r.cells());
        assertContiguous(r.cells());
    }

    @Test
    void pointed_diagonalLegUsesMagic() {
        PipeRoute.Result r = PipeRoute.pointed(List.of(wp(0, 64, 0), wp(2, 64, 2)), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        assertContiguous(r.cells());
        assertEquals(new PipeRoute.Cell(2, 64, 2), r.cells().get(r.cells().size() - 1));
    }

    @Test
    void pointed_overlappingLegsFailWithOverlap() {
        // Leg1 (0,64,0)->(3,64,0); leg2 (3,64,0)->(1,64,0) runs straight back over leg1 cells.
        PipeRoute.Result r = PipeRoute.pointed(
                List.of(wp(0, 64, 0), wp(3, 64, 0), wp(1, 64, 0)), ALL_FREE);
        assertFalse(r.isValid(), "a route doubling back must not succeed");
        assertEquals("polyfill.pipewand.err_overlap", r.errorKey());
    }

    @Test
    void pointed_needsTwoPoints() {
        PipeRoute.Result r = PipeRoute.pointed(List.of(wp(0, 64, 0)), ALL_FREE);
        assertFalse(r.isValid());
        assertEquals("polyfill.pipewand.err_need_points", r.errorKey());
    }

    /** No two cells share an (x,y,z). */
    private static void assertNoDuplicates(List<PipeRoute.Cell> cells) {
        java.util.Set<PipeRoute.Cell> seen = new java.util.HashSet<>();
        for (PipeRoute.Cell c : cells)
            assertTrue(seen.add(c), "duplicate cell: " + c);
    }

    /** Every consecutive pair differs by exactly one in a single axis (6-neighbour adjacency). */
    private static void assertContiguous(List<PipeRoute.Cell> cells) {
        for (int i = 0; i + 1 < cells.size(); i++) {
            PipeRoute.Cell a = cells.get(i), b = cells.get(i + 1);
            int d = Math.abs(a.x - b.x) + Math.abs(a.y - b.y) + Math.abs(a.z - b.z);
            assertEquals(1, d, "non-adjacent step: " + a + " -> " + b);
        }
    }
}
