package dev.arubik.craftengine.conveyor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests for the pure multi-waypoint belt planner {@link ConveyorRoute}. */
class ConveyorRouteTest {

    /** All cells free. */
    private static final ConveyorRoute.CellValidator ALL_FREE = (x, y, z) -> true;

    private static ConveyorRoute.Waypoint wp(int x, int y, int z) {
        return new ConveyorRoute.Waypoint(x, y, z);
    }

    @Test
    void straightFlatRoute_matchesPathPlanner() {
        ConveyorRoute.Result r = ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(3, 64, 0)), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        List<ConveyorPath.Step> s = r.steps();
        assertEquals(4, s.size());
        assertEquals(ConveyorPart.START, s.get(0).part);
        assertEquals(ConveyorPart.END, s.get(3).part);
        for (ConveyorPath.Step st : s) {
            assertEquals(1, st.stepX);
            assertEquals(0, st.stepZ);
            assertEquals(ConveyorSlope.FLAT, st.slope);
        }
    }

    @Test
    void lShapedCorner_turnsAtTheBend() {
        // A→corner along +X (3) then +Z (2): expect a corner where the facing changes.
        ConveyorRoute.Result r = ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(3, 64, 2)), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        List<ConveyorPath.Step> s = r.steps();
        // 3 + 2 = 5 moves -> 6 cells.
        assertEquals(6, s.size());
        // First run heads +X.
        assertEquals(1, s.get(0).stepX);
        assertEquals(0, s.get(0).stepZ);
        // Last run heads +Z.
        assertEquals(0, s.get(s.size() - 1).stepX);
        assertEquals(1, s.get(s.size() - 1).stepZ);
        // The corner cell is at (3,64,0): the last +X cell sits there, then it turns +Z.
        ConveyorPath.Step corner = s.get(3);
        assertEquals(3, corner.x);
        assertEquals(0, corner.z);
        assertEquals(64, corner.y);
        // Endpoint reached.
        ConveyorPath.Step end = s.get(s.size() - 1);
        assertEquals(3, end.x);
        assertEquals(2, end.z);
    }

    @Test
    void climbingRoute_usesSlopeThenFlat() {
        // Climb from y=64 to y=66 over a run of 4 along +X: CS,CS,CF,CF.
        // Grid example: ramp early, flat at the new height.
        ConveyorRoute.Result r = ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(4, 66, 0)), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        List<ConveyorPath.Step> s = r.steps();
        assertEquals(5, s.size());
        // First two ramp UP, raising Y each cell; the rest are flat at the top.
        assertEquals(ConveyorSlope.UP, s.get(0).slope);
        assertEquals(64, s.get(0).y);
        assertEquals(ConveyorSlope.UP, s.get(1).slope);
        assertEquals(65, s.get(1).y);
        assertEquals(ConveyorSlope.FLAT, s.get(2).slope);
        assertEquals(66, s.get(2).y);
        assertEquals(ConveyorSlope.FLAT, s.get(4).slope);
        assertEquals(66, s.get(4).y);
        assertEquals(4, s.get(4).x);
    }

    @Test
    void descendingRoute_usesDownSlope() {
        // Descend 64 -> 62 over a run of 3 (slack = 1). Under the LOWER-block convention the
        // top/anchor stays FLAT at 64, then the DOWN blocks sit at the lower levels they exit
        // onto: FLAT@64, DOWN@63, DOWN@62, FLAT@62.
        ConveyorRoute.Result r = ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(0, 62, 3)), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        List<ConveyorPath.Step> s = r.steps();
        assertEquals(4, s.size());
        assertEquals(ConveyorSlope.FLAT, s.get(0).slope);
        assertEquals(64, s.get(0).y);
        assertEquals(ConveyorSlope.DOWN, s.get(1).slope);
        assertEquals(63, s.get(1).y);
        assertEquals(ConveyorSlope.DOWN, s.get(2).slope);
        assertEquals(62, s.get(2).y);
        assertEquals(62, s.get(3).y);
    }

    @Test
    void multiWaypoint_chainsLegsWithCornerBetween() {
        // 0,64,0 -> 3,64,0 (east) -> 3,64,3 (south): a single corner at the middle waypoint.
        ConveyorRoute.Result r = ConveyorRoute.plan(
                List.of(wp(0, 64, 0), wp(3, 64, 0), wp(3, 64, 3)), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        List<ConveyorPath.Step> s = r.steps();
        // 3 + 3 = 6 moves -> 7 cells (the shared middle waypoint cell is not duplicated).
        assertEquals(7, s.size());
        assertEquals(ConveyorPart.START, s.get(0).part);
        assertEquals(ConveyorPart.END, s.get(6).part);
        assertEquals(3, s.get(6).x);
        assertEquals(3, s.get(6).z);
    }

    /**
     * Slope-Y math: every climbing CS slope cell must sit exactly one block BELOW the
     * cell it exits into (its exit reaches +1 Y), so the FLAT that follows a slope-up is
     * one block higher and their surfaces meet with no half-block step. Mirrors the
     * grid rule: a CS at the lower level then the upper-flat at +1.
     */
    @Test
    void climbingRoute_slopeIsOneBelowFollowingCell() {
        // Climb 64 -> 67 over a +X run of 5: CS@64, CS@65, CS@66, CF@67, CF@67.
        ConveyorRoute.Result r = ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(3, 67, 0)), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        List<ConveyorPath.Step> s = r.steps();
        for (int i = 0; i + 1 < s.size(); i++) {
            ConveyorPath.Step cur = s.get(i);
            ConveyorPath.Step nxt = s.get(i + 1);
            if (cur.slope == ConveyorSlope.UP) {
                // The slope's exit (and therefore the next cell) is exactly one block up.
                assertEquals(cur.y + 1, nxt.y,
                        "slope-up at " + cur + " must be ONE below its following cell " + nxt);
            }
        }
        // The first flat after the last slope is at the top (+3), never level with a slope.
        ConveyorPath.Step lastSlope = null;
        for (ConveyorPath.Step st : s)
            if (st.slope == ConveyorSlope.UP)
                lastSlope = st;
        assertEquals(66, lastSlope.y);
        // The cell right after that slope is the upper flat, one block higher.
        assertEquals(67, s.get(3).y);
        assertEquals(ConveyorSlope.FLAT, s.get(3).slope);
    }

    /**
     * Descending convention: a DOWN block sits at the LOWER of the two levels it bridges. Its
     * exit/front (the FOLLOWING cell) is at the SAME y; its entry/back (the PRECEDING cell) is
     * one ABOVE. So each DOWN is entered from {@code y+1} and exits at {@code y}.
     */
    @Test
    void descendingRoute_downSlopeSitsAtLowerLevel() {
        // 64 -> 61 over a run of 3 (no slack): DOWN@63, DOWN@62, DOWN@61, dest@61.
        ConveyorRoute.Result r = ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(0, 61, 3)), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        List<ConveyorPath.Step> s = r.steps();
        for (int i = 0; i < s.size(); i++) {
            ConveyorPath.Step cur = s.get(i);
            if (cur.slope != ConveyorSlope.DOWN)
                continue;
            // Exit/front: this DOWN's exit SURFACE is its own y; the following cell's ENTRY
            // surface meets it (a following FLAT sits at that y, a following DOWN one below).
            if (i + 1 < s.size())
                assertEquals(exitSurface(cur), entrySurface(s.get(i + 1)),
                        "slope-down at " + cur + " exit must meet the following cell " + s.get(i + 1));
            // Entry/back: this DOWN is entered from one ABOVE — its entry surface (y+1) meets
            // the preceding cell's exit surface.
            if (i > 0)
                assertEquals(exitSurface(s.get(i - 1)), entrySurface(cur),
                        "slope-down at " + cur + " must be entered from one above: " + s.get(i - 1));
            // The block itself sits one BELOW its entry surface.
            assertEquals(cur.y + 1, entrySurface(cur), "DOWN block must sit one below its entry surface: " + cur);
        }
    }

    /** The belt-surface Y a cell's FRONT/exit hands off at (UP rises one; FLAT/DOWN stay). */
    private static int exitSurface(ConveyorPath.Step s) {
        return s.y + (s.slope == ConveyorSlope.UP ? 1 : 0);
    }

    /** The belt-surface Y a cell's BACK/entry is met at (a DOWN block sits one below it). */
    private static int entrySurface(ConveyorPath.Step s) {
        return s.y + (s.slope == ConveyorSlope.DOWN ? 1 : 0);
    }

    /**
     * A single +1 hill — flat, up one, top flat(s), down one, flat — places its UP-slope block
     * and its DOWN-slope block at the SAME y (never vertically stacked), with the top flat
     * between them at that y+1. This is the core anti-stacking rule.
     */
    @Test
    void hill_upAndDownSlopesShareSameY() {
        // Climb 64->65 then descend 65->64 over a +X run: 0..2 climbs, 2..5 stays then descends.
        // (0,64,0) -> (2,65,0) -> (5,64,0): a hump that comes back down to the start height.
        ConveyorRoute.Result r = ConveyorRoute.plan(
                List.of(wp(0, 64, 0), wp(2, 65, 0), wp(5, 64, 0)), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        List<ConveyorPath.Step> s = r.steps();

        ConveyorPath.Step up = null, down = null;
        for (ConveyorPath.Step st : s) {
            if (st.slope == ConveyorSlope.UP)
                up = st; // last UP of the climb
            if (st.slope == ConveyorSlope.DOWN && down == null)
                down = st; // first DOWN of the descent
        }
        assertTrue(up != null, "expected an UP slope on the hill");
        assertTrue(down != null, "expected a DOWN slope on the hill");
        // The UP block and the DOWN block sit at the SAME y — never stacked one above the other.
        assertEquals(up.y, down.y, "hill UP " + up + " and DOWN " + down + " must share the same y");
        // The top flat between them is one above that shared slope y.
        boolean topFlat = false;
        for (ConveyorPath.Step st : s)
            if (st.slope == ConveyorSlope.FLAT && st.y == up.y + 1)
                topFlat = true;
        assertTrue(topFlat, "expected a top flat at the hill crest (slope y + 1)");

        // Surface continuity end to end (slopes connect flush, no float/gap).
        for (int i = 0; i + 1 < s.size(); i++)
            assertEquals(exitSurface(s.get(i)), entrySurface(s.get(i + 1)),
                    "hill surface break: " + s.get(i) + " -> " + s.get(i + 1));
    }

    /**
     * A slope's same-level end aligns in y with the adjacent FLAT it meets, and its other end
     * is one block higher. For an UP slope the same-level end is the ENTRY/back; for a DOWN
     * slope it is the EXIT/front. Verified for both a climb and a descent.
     */
    @Test
    void slopeLowEnd_matchesAdjacentFlatY() {
        // Climb: (0,64,0) -> (4,66,0) -> (4,66,3) so the climb's TOP is a FLAT. The UP slope's
        // same-level end is its ENTRY/back; the bottom flat (or the leg anchor surface) it meets
        // is at the UP's own y, and its exit is one higher.
        List<ConveyorPath.Step> climb =
                ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(4, 66, 0)), ALL_FREE).steps();
        for (int i = 0; i < climb.size(); i++) {
            ConveyorPath.Step cur = climb.get(i);
            if (cur.slope != ConveyorSlope.UP)
                continue;
            // Same-level (entry) end sits at the UP's own y; high (exit) end is one above.
            assertEquals(cur.y, entrySurface(cur), "UP entry surface must equal its own y: " + cur);
            assertEquals(cur.y + 1, exitSurface(cur), "UP exit must be one above its own y: " + cur);
            // Where an UP meets a FLAT behind it, that flat shares the UP's y (same-level end).
            if (i > 0 && climb.get(i - 1).slope == ConveyorSlope.FLAT)
                assertEquals(cur.y, climb.get(i - 1).y,
                        "FLAT->UP junction: the flat behind must share the UP's y: " + cur);
        }

        // Descent: (0,64,0) -> (4,62,0). Each DOWN's same-level end is its EXIT/front; the flat
        // it meets ahead shares the DOWN's y, and its high (entry) end is one above.
        List<ConveyorPath.Step> drop =
                ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(4, 62, 0)), ALL_FREE).steps();
        for (int i = 0; i < drop.size(); i++) {
            ConveyorPath.Step cur = drop.get(i);
            if (cur.slope != ConveyorSlope.DOWN)
                continue;
            assertEquals(cur.y, exitSurface(cur), "DOWN exit surface must equal its own y: " + cur);
            assertEquals(cur.y + 1, entrySurface(cur), "DOWN entry must be one above its own y: " + cur);
            // Where a DOWN meets a FLAT ahead, that flat shares the DOWN's y (same-level end).
            if (i + 1 < drop.size() && drop.get(i + 1).slope == ConveyorSlope.FLAT)
                assertEquals(cur.y, drop.get(i + 1).y,
                        "DOWN->FLAT junction: the flat ahead must share the DOWN's y: " + cur);
        }
    }

    /**
     * Corner capping: at an L-turn there must be no exposed MIDDLE transparent face. The
     * run's last cell before the turn is END (front face capped) and the turn cell is
     * START (back face capped); only cells with a straight neighbour on BOTH sides are
     * MIDDLE.
     */
    @Test
    void corner_isCappedWithEndThenStart() {
        // (0,64,0)->(4,64,0) east, then a turn south to (4,64,2): corner at (4,64,0).
        ConveyorRoute.Result r = ConveyorRoute.plan(
                List.of(wp(0, 64, 0), wp(4, 64, 0), wp(4, 64, 2)), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        List<ConveyorPath.Step> s = r.steps();

        // No MIDDLE cell may have a neighbour that turns or changes slope (i.e. every
        // MIDDLE must have a straight same-facing same-slope neighbour on BOTH sides).
        for (int i = 0; i < s.size(); i++) {
            if (s.get(i).part != ConveyorPart.MIDDLE)
                continue;
            assertTrue(i > 0 && i < s.size() - 1, "MIDDLE at route boundary: " + s.get(i));
            ConveyorPath.Step prev = s.get(i - 1), cur = s.get(i), nxt = s.get(i + 1);
            assertTrue(prev.stepX == cur.stepX && prev.stepZ == cur.stepZ && prev.slope == cur.slope,
                    "MIDDLE " + cur + " has a non-straight entry neighbour " + prev);
            assertTrue(nxt.stepX == cur.stepX && nxt.stepZ == cur.stepZ && nxt.slope == cur.slope,
                    "MIDDLE " + cur + " has a non-straight exit neighbour " + nxt);
        }

        // Locate the corner cell (4,64,0) and the run cell just before it (3,64,0).
        ConveyorPath.Step corner = null, beforeCorner = null;
        for (ConveyorPath.Step st : s) {
            if (st.x == 4 && st.y == 64 && st.z == 0)
                corner = st;
            if (st.x == 3 && st.y == 64 && st.z == 0)
                beforeCorner = st;
        }
        assertTrue(corner != null && beforeCorner != null);
        // The cell before the turn caps its exposed FRONT face: END. The turn cell, whose
        // BACK face is exposed (facing changed), caps it: START.
        assertEquals(ConveyorPart.END, beforeCorner.part, "pre-corner cell should be END");
        assertEquals(ConveyorPart.START, corner.part, "corner cell should be START");
    }

    @Test
    void tooSteep_isRejected() {
        // Climb 3 over a horizontal run of only 2 -> too steep.
        ConveyorRoute.Result r = ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(2, 67, 0)), ALL_FREE);
        assertFalse(r.isValid());
        assertTrue(r.error().toLowerCase().contains("steep"), r.error());
    }

    @Test
    void blockedCell_failsWithCoordinate() {
        // A 2-tall obstacle at x==1 (the cell AND the cell above it are solid) cannot be
        // auto-stepped, so the straight route fails at the blocked landing column. (A
        // single-block obstacle with clear headroom is now ramped over — see
        // flatRoute_autoStepsUpOverSingleBlock.)
        ConveyorRoute.CellValidator validator =
                (x, y, z) -> !(x == 1 && (y == 64 || y == 65) && z == 0);
        ConveyorRoute.Result r = ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(3, 64, 0)), validator);
        assertFalse(r.isValid());
        assertTrue(r.error().contains("1,64"), r.error());
    }

    @Test
    void singlePoint_isRejected() {
        ConveyorRoute.Result r = ConveyorRoute.plan(List.of(wp(0, 64, 0)), ALL_FREE);
        assertFalse(r.isValid());
    }

    // -------------------------------------------------- terrain auto-step (±1)

    /**
     * A flat leg at the SAME Y whose straight path is interrupted by a SINGLE solid block
     * (its top clear) must auto-ramp UP over it and back DOWN — "subir y bajar". The
     * route stays valid + chain-continuous and returns to the original height.
     */
    @Test
    void flatRoute_autoStepsUpOverSingleBlock() {
        // Straight +X run 0..5 at y=64. The cell (2,64,0) is solid; (2,65,0) is clear.
        // Expect: UP at x=1 onto the block top, FLAT on top at x=2, DOWN at x=3, FLAT after.
        ConveyorRoute.CellValidator validator = (x, y, z) -> !(x == 2 && y == 64 && z == 0);
        ConveyorRoute.Result r = ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(5, 64, 0)), validator);
        assertTrue(r.isValid(), r.error());
        List<ConveyorPath.Step> s = r.steps();

        // Never occupies the solid cell at y=64; it rides one block up across the obstacle.
        for (ConveyorPath.Step st : s)
            assertFalse(st.x == 2 && st.y == 64 && st.z == 0, "route entered the solid cell: " + st);

        // There is an UP ramp before the block and a DOWN ramp after it.
        boolean up = false, down = false, onTop = false;
        for (ConveyorPath.Step st : s) {
            if (st.slope == ConveyorSlope.UP)
                up = true;
            if (st.slope == ConveyorSlope.DOWN)
                down = true;
            if (st.x == 2 && st.y == 65 && st.z == 0)
                onTop = true;
        }
        assertTrue(up, "expected an UP ramp onto the block");
        assertTrue(down, "expected a DOWN ramp off the block");
        assertTrue(onTop, "expected a flat cell on top of the block at (2,65,0)");

        // Returns to the start height and reaches the destination.
        ConveyorPath.Step end = s.get(s.size() - 1);
        assertEquals(5, end.x);
        assertEquals(64, end.y);

        // Chain continuity holds across the whole over-and-back ramp.
        for (int i = 0; i + 1 < s.size(); i++)
            assertTrue(inExitCandidates(s.get(i), s.get(i + 1)),
                    "chain breaks at step " + i + ": " + s.get(i) + " -> " + s.get(i + 1));
    }

    /**
     * Headroom guard: a planned slope-UP whose cell-above is blocked cannot clip into it,
     * so the leg fails (here a tall wall at the climb column blocks both the +1 landing
     * and the headroom).
     */
    @Test
    void climb_failsWhenHeadroomBlocked() {
        // Climb 64->65 over a +X run of 2. Block the entire x==1 column at y 64..66 so the
        // ramp has nowhere legal to rise.
        ConveyorRoute.CellValidator validator =
                (x, y, z) -> !(x == 1 && (y == 64 || y == 65 || y == 66) && z == 0);
        ConveyorRoute.Result r = ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(2, 65, 0)), validator);
        assertFalse(r.isValid());
        assertTrue(r.errorKey().equals("polyfill.wand.err_blocked"), r.error());
    }

    // -------------------------------------------------- L orientation + straight

    /**
     * An axis-aligned leg ({@code dx==0} XOR {@code dz==0}) is a SINGLE straight run with
     * no corner: every cell shares the one heading and there is exactly one START/END pair.
     */
    @Test
    void axisAlignedLeg_isSingleStraightRun() {
        ConveyorRoute.Result r = ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(0, 64, 4)), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        List<ConveyorPath.Step> s = r.steps();
        assertEquals(5, s.size());
        // One heading throughout (no turn), START head, END tail, MIDDLE between.
        for (ConveyorPath.Step st : s) {
            assertEquals(0, st.stepX);
            assertEquals(1, st.stepZ);
        }
        assertEquals(ConveyorPart.START, s.get(0).part);
        assertEquals(ConveyorPart.END, s.get(s.size() - 1).part);
        for (int i = 1; i < s.size() - 1; i++)
            assertEquals(ConveyorPart.MIDDLE, s.get(i).part, "interior of a straight run must be MIDDLE: " + s.get(i));
    }

    /**
     * Two-orientation L selection: when the dominant-axis-first L is blocked at its corner
     * column but the other orientation is fully clear, the planner uses the clear one
     * instead of failing.
     */
    @Test
    void lBend_picksClearOrientationWhenDefaultBlocked() {
        // dx=3 (dominant), dz=2. Default x-first L runs +X to (3,64,0) then +Z to (3,64,2).
        // Block x==3 at z 0..1 (the x-first run's far column AND its corner) so x-first is
        // impossible; the z-first L (+Z first to (0,64,2), then +X to (3,64,2)) is clear and
        // must be chosen. To stop the auto-step from ramping OVER the wall, make it 2 tall.
        ConveyorRoute.CellValidator validator =
                (x, y, z) -> !(x == 3 && (z == 0 || z == 1) && (y == 64 || y == 65));
        ConveyorRoute.Result r = ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(3, 64, 2)), validator);
        assertTrue(r.isValid(), r.error());
        List<ConveyorPath.Step> s = r.steps();
        // z-first: the first move heads +Z, not +X.
        assertEquals(0, s.get(0).stepX);
        assertEquals(1, s.get(0).stepZ);
        // Never enters the blocked x==3 lower-z column.
        for (ConveyorPath.Step st : s)
            assertFalse(st.x == 3 && (st.z == 0 || st.z == 1),
                    "route used the blocked x-first corner column: " + st);
        ConveyorPath.Step end = s.get(s.size() - 1);
        assertEquals(3, end.x);
        assertEquals(2, end.z);
    }

    // ------------------------------------------------------------------ RPM chain

    /**
     * Mirror of {@link ConveyorBlockEntity#exitCandidates}: a belt at {@code pos} facing
     * {@code (fx,fz)} exits to the {@code facing} neighbour at the SAME Y, Y+1 or Y-1.
     * This is the exact geometry {@code downstreamConveyor} uses to chain the RPM/stress
     * tree, expressed without any Bukkit/NMS types so it is unit-testable.
     */
    private static boolean inExitCandidates(ConveyorPath.Step prev, ConveyorPath.Step next) {
        int fx = Integer.signum(prev.stepX);
        int fz = Integer.signum(prev.stepZ);
        int nx = prev.x + fx;
        int nz = prev.z + fz;
        if (next.x != nx || next.z != nz)
            return false;
        int dy = next.y - prev.y;
        return dy == 0 || dy == 1 || dy == -1;
    }

    /**
     * The RPM/stress chain invariant: every consecutive pair of placed cells must satisfy
     * {@code next ∈ exitCandidates(prev, prev.facing)} so {@code downstreamConveyor} walks
     * the whole route as ONE consumption chain (the driving motor at the START therefore
     * counts every segment). Asserted here for a mixed route: a climbing first leg, a
     * flat corner turn, then a descending leg — flat + slope + corner end to end.
     */
    @Test
    void mixedRoute_isRpmChainContinuous() {
        // Leg 1: (0,64,0)->(4,66,0) climb +X (CS,CS,CF,CF). Leg 2: (4,66,0)->(4,64,3)
        // turn +Z and descend (CS,CS,CF). One corner at (4,66,0), slopes on both legs.
        ConveyorRoute.Result r = ConveyorRoute.plan(
                List.of(wp(0, 64, 0), wp(4, 66, 0), wp(4, 64, 3)), ALL_FREE);
        assertTrue(r.isValid(), r.error());
        List<ConveyorPath.Step> s = r.steps();

        // Parts: exactly one START at the head, one END at the tail.
        assertEquals(ConveyorPart.START, s.get(0).part);
        assertEquals(ConveyorPart.END, s.get(s.size() - 1).part);

        // Chain continuity across EVERY consecutive pair (the core invariant).
        for (int i = 0; i + 1 < s.size(); i++) {
            ConveyorPath.Step prev = s.get(i);
            ConveyorPath.Step next = s.get(i + 1);
            assertTrue(inExitCandidates(prev, next),
                    "chain breaks between step " + i + " " + prev + " and " + next
                            + ": next is outside exitCandidates(prev.facing)");
        }

        // The corner cell (4,66,0) actually turns: incoming heads +X, the cell itself heads +Z.
        ConveyorPath.Step corner = null;
        for (ConveyorPath.Step st : s)
            if (st.x == 4 && st.y == 66 && st.z == 0)
                corner = st;
        assertTrue(corner != null, "expected a corner cell at 4,66,0");
        assertEquals(0, corner.stepX);
        assertEquals(1, corner.stepZ);
    }

    /**
     * The single source-of-truth surface invariant for the WHOLE route (every consecutive
     * pair, across leg joins and auto-steps): a cell's FRONT/exit surface meets the next
     * cell's BACK/entry surface, and {@code next.x,next.z == prev.x,prev.z + sign(prev.step)}.
     * Under the LOWER-block convention {@code exitSurface = y + (UP?1:0)} and
     * {@code entrySurface = y + (DOWN?1:0)}; their equality means the slopes always connect
     * with no floating/half-block gap. Asserted over a battery of routes including a climb, a
     * descent, multi-leg joins and an auto-step over a single block.
     */
    @Test
    void wholeRoute_satisfiesYInvariant() {
        // (route, validator) pairs. ALL_FREE except the auto-step case.
        assertYInvariant(ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(5, 64, 0)), ALL_FREE));
        assertYInvariant(ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(4, 66, 0)), ALL_FREE));
        assertYInvariant(ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(0, 61, 4)), ALL_FREE));
        assertYInvariant(ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(4, 66, 0), wp(4, 64, 3)), ALL_FREE));
        assertYInvariant(ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(3, 64, 0), wp(3, 64, 3)), ALL_FREE));
        // Auto-step over a single block at (2,64,0): the invariant must still hold end to end.
        ConveyorRoute.CellValidator step = (x, y, z) -> !(x == 2 && y == 64 && z == 0);
        assertYInvariant(ConveyorRoute.plan(List.of(wp(0, 64, 0), wp(5, 64, 0)), step));
    }

    /** Assert the route is valid and every consecutive pair obeys the exit/entry surface invariant. */
    private static void assertYInvariant(ConveyorRoute.Result r) {
        assertTrue(r.isValid(), r.error());
        List<ConveyorPath.Step> s = r.steps();
        for (int i = 0; i + 1 < s.size(); i++) {
            ConveyorPath.Step prev = s.get(i), next = s.get(i + 1);
            int fx = Integer.signum(prev.stepX), fz = Integer.signum(prev.stepZ);
            assertEquals(prev.x + fx, next.x, "x break: " + prev + " -> " + next);
            assertEquals(prev.z + fz, next.z, "z break: " + prev + " -> " + next);
            assertEquals(exitSurface(prev), entrySurface(next),
                    "surface invariant break: " + prev + " -> " + next
                            + " (prev exit surface must equal next entry surface)");
        }
    }

    // -------------------------------------------------- no-revisit / anti-crossing

    /**
     * A route whose naive default L-shape would cross an EARLIER leg must either re-route
     * to the other orientation or fail with {@code err_overlap}; never emit two belts in the
     * same cell. Here a spiral of waypoints forces the second/third legs back toward already
     * placed cells. Whatever the planner does, the final step list must have no duplicate
     * (x,y,z) and no two belts sharing an (x,z) column at the same y with conflicting facings.
     */
    @Test
    void route_neverRevisitsACell() {
        // A U-turn that, laid naively, would run a later leg straight back over an earlier one.
        ConveyorRoute.Result r = ConveyorRoute.plan(
                List.of(wp(0, 64, 0), wp(4, 64, 0), wp(4, 64, 2), wp(0, 64, 2), wp(0, 64, 0)), ALL_FREE);
        // It may legitimately fail with err_overlap (the last leg returns to the very first
        // cell). If it succeeds, there must be no duplicate cells.
        if (r.isValid()) {
            assertNoDuplicateCells(r.steps());
        } else {
            assertEquals("polyfill.wand.err_overlap", r.errorKey(), r.error());
        }
    }

    /**
     * Directly force a crossing: a 2-leg route where the second leg's only naive path runs
     * back through the first leg's cells. The planner must re-route or fail with err_overlap,
     * and either way must never duplicate a cell.
     */
    @Test
    void crossingRoute_reroutesOrFailsWithOverlap() {
        // Leg1: (0,64,0)->(3,64,0) east. Leg2: (3,64,0)->(1,64,0) heads back west THROUGH
        // (2,64,0) and (1,64,0) which leg1 already placed. There is no other orientation
        // (axis-aligned), so this must fail with err_overlap, not silently double-place.
        ConveyorRoute.Result r = ConveyorRoute.plan(
                List.of(wp(0, 64, 0), wp(3, 64, 0), wp(1, 64, 0)), ALL_FREE);
        assertFalse(r.isValid(), "a route doubling back over itself must not succeed");
        assertEquals("polyfill.wand.err_overlap", r.errorKey(), r.error());
    }

    /** No two steps share an (x,y,z); and no (x,z) at the same y carries conflicting belts. */
    private static void assertNoDuplicateCells(List<ConveyorPath.Step> s) {
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (ConveyorPath.Step st : s) {
            String key = st.x + "," + st.y + "," + st.z;
            assertTrue(seen.add(key), "duplicate cell in route: " + st);
        }
    }

    /** Pure straight + L-corner + climb permutations all stay chain-continuous. */
    @Test
    void variousRoutes_areRpmChainContinuous() {
        List<List<ConveyorRoute.Waypoint>> routes = List.of(
                List.of(wp(0, 64, 0), wp(5, 64, 0)),                       // straight flat
                List.of(wp(0, 64, 0), wp(3, 64, 4)),                       // L corner flat
                List.of(wp(0, 64, 0), wp(6, 67, 0)),                       // long climb
                List.of(wp(0, 64, 0), wp(0, 61, 4)),                       // descend
                List.of(wp(0, 64, 0), wp(3, 64, 0), wp(3, 64, 3)),         // two flat legs, corner
                List.of(wp(0, 64, 0), wp(2, 66, 0), wp(2, 66, 4)),         // climb then flat turn
                List.of(wp(0, 64, 0), wp(4, 64, 0), wp(4, 62, 3)));        // flat then descending turn
        for (List<ConveyorRoute.Waypoint> route : routes) {
            ConveyorRoute.Result r = ConveyorRoute.plan(route, ALL_FREE);
            assertTrue(r.isValid(), r.error());
            List<ConveyorPath.Step> s = r.steps();
            for (int i = 0; i + 1 < s.size(); i++)
                assertTrue(inExitCandidates(s.get(i), s.get(i + 1)),
                        "chain breaks at step " + i + " in route " + route.get(route.size() - 1).x
                                + ": " + s.get(i) + " -> " + s.get(i + 1));
        }
    }
}
