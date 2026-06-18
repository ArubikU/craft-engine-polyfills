package dev.arubik.craftengine.conveyor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pure, Bukkit-free multi-waypoint route planner for the "pointed" conveyor wand.
 *
 * <p>Given an ordered list of waypoints it produces a single belt route that visits
 * every waypoint in order, auto-inserting CORNERS where the horizontal direction
 * changes and SLOPES where the Y changes. The result is the ordered list of
 * {@link ConveyorPath.Step}s (position + facing step + slope + part) for the whole
 * chain, with START at the very first cell, END at the very last and corners as the
 * turn cells. {@code prevPos} linking is done by the caller (the listener) from the
 * step order.</p>
 *
 * <p>No Bukkit/NMS types are referenced: cell validity is provided through the
 * {@link CellValidator} functional interface so the planner is unit-testable.</p>
 *
 * <p><b>Slope convention</b> — a slope block ALWAYS occupies the LOWER of the two
 * flat levels it bridges:
 * <ul>
 *   <li>FLAT at Y: entry and exit both at Y.</li>
 *   <li>UP at Y: entry/back meets FLAT at {@code (back, Y)} [same y]; exit/front
 *       meets FLAT at {@code (front, Y+1)}.</li>
 *   <li>DOWN at Y: entry/back meets FLAT at {@code (back, Y+1)} [one above-behind];
 *       exit/front meets FLAT at {@code (front, Y)} [same y].</li>
 * </ul>
 * Consequence: a single +1 hill is {@code flat(Y) -> UP@Y -> flat(Y+1) -> DOWN@Y ->
 * flat(Y)} with BOTH slope blocks at the SAME y — never vertically stacked. A slope's
 * same-level end always aligns in position, y and facing with the flat it meets.</p>
 *
 * <p>Runtime chaining ({@link ConveyorBlockEntity#exitCandidates}) probes the
 * {@code facing} neighbour at Y, Y+1 and Y-1, so it bridges any of these junctions
 * regardless of which level the slope block sits on.</p>
 */
public final class ConveyorRoute {

    private ConveyorRoute() {
    }

    /** Tells the planner whether a belt cell may be occupied (air / top-slab / replaceable). */
    @FunctionalInterface
    public interface CellValidator {
        /**
         * @return true when a belt segment may be placed at this block position.
         *         A waypoint cell itself is always allowed by the planner (it is the
         *         player's chosen anchor); only intermediate/derived cells are tested.
         */
        boolean isFree(int x, int y, int z);
    }

    /** A single integer waypoint chosen by the player. */
    public static final class Waypoint {
        public final int x, y, z;

        public Waypoint(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    /**
     * Result: an ordered step list, or a failure. A failure carries both a
     * human-readable English {@code error()} (used by unit tests / logs) and a
     * client-translatable {@code errorKey()} plus integer {@code errorArgs()} (the
     * wand turns these into an Adventure {@code Component.translatable}, so the player
     * sees the reason in their own language). Keys live under {@code polyfill.wand.*}
     * in {@code configuration/lang/polyfills_lang.yml}.
     */
    public static final class Result {
        private final List<ConveyorPath.Step> steps;
        private final String error;
        private final String errorKey;
        private final int[] errorArgs;

        private Result(List<ConveyorPath.Step> steps, String error, String errorKey, int[] errorArgs) {
            this.steps = steps;
            this.error = error;
            this.errorKey = errorKey;
            this.errorArgs = errorArgs;
        }

        public static Result ok(List<ConveyorPath.Step> steps) {
            return new Result(steps, null, null, null);
        }

        public static Result fail(String error, String errorKey, int... errorArgs) {
            return new Result(null, error, errorKey, errorArgs);
        }

        public boolean isValid() {
            return error == null;
        }

        public List<ConveyorPath.Step> steps() {
            return steps;
        }

        public String error() {
            return error;
        }

        /** The {@code polyfill.wand.*} lang key for this failure (null when valid). */
        public String errorKey() {
            return errorKey;
        }

        /** Numeric placeholders (coords/indices) substituted into {@link #errorKey()}. */
        public int[] errorArgs() {
            return errorArgs;
        }
    }

    /** A raw planned cell before START/MIDDLE/END parts are assigned. */
    private static final class Cell {
        final int x, y, z;
        int stepX, stepZ; // travel direction at this cell
        ConveyorSlope slope;

        Cell(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    /**
     * Plan a belt route through {@code waypoints} (in order).
     *
     * <p>Between each consecutive pair the planner lays an L-shaped (one-corner) or
     * straight horizontal route, then ramps the Y with slope segments. Each leg's
     * horizontal length must be at least its absolute Y change so the slope fits at
     * 45° (one Y per cell). The validator gates every derived cell; a blocked cell
     * fails the plan with the offending coordinate.</p>
     *
     * @param waypoints at least 2 points
     * @param valid     cell-occupancy test (air/top-slab/replaceable)
     * @return a valid {@link Result} or a fail with the reason.
     */
    public static Result plan(List<Waypoint> waypoints, CellValidator valid) {
        if (waypoints == null || waypoints.size() < 2)
            return Result.fail("Need at least 2 points.", "polyfill.wand.err_need_points");

        List<Cell> cells = new ArrayList<>();
        // Cells already committed by the WHOLE plan (every leg), so no later leg/auto-step/
        // L-orientation can place a belt where one already exists — the route never crosses
        // or overlaps itself. Keyed by packed (x,y,z).
        Set<Long> visited = new HashSet<>();
        Waypoint cur = waypoints.get(0);
        // The running Y the belt surface is currently at (flat level) as we leave a leg.
        for (int w = 1; w < waypoints.size(); w++) {
            Waypoint next = waypoints.get(w);
            LegError err = planLeg(cur, next, cells, valid, w, visited);
            if (err != null)
                return Result.fail(err.message, err.key, err.args);
            cur = next;
        }
        if (cells.isEmpty())
            return Result.fail("Route is empty.", "polyfill.wand.err_empty");

        // De-dup consecutive identical cells produced at leg joins (the corner cell of
        // one leg equals the start cell of the next): keep the first, drop the repeat.
        List<Cell> dedup = new ArrayList<>(cells.size());
        for (Cell c : cells) {
            if (!dedup.isEmpty()) {
                Cell last = dedup.get(dedup.size() - 1);
                if (last.x == c.x && last.y == c.y && last.z == c.z) {
                    // Same physical cell: prefer the OUTGOING direction/slope (c) so the turn
                    // flows toward the next leg.
                    last.stepX = c.stepX;
                    last.stepZ = c.stepZ;
                    last.slope = c.slope;
                    continue;
                }
            }
            dedup.add(c);
        }

        // A trailing dest cell (final END) has no outgoing direction — inherit the
        // previous cell's heading so its facing/rotation is sensible.
        for (int i = 1; i < dedup.size(); i++) {
            Cell c = dedup.get(i);
            if (c.stepX == 0 && c.stepZ == 0) {
                Cell prev = dedup.get(i - 1);
                c.stepX = prev.stepX;
                c.stepZ = prev.stepZ;
            }
        }

        // Assign parts by RUN BOUNDARY, not just by list index.
        //
        // A MIDDLE belt model has two transparent faces (entry + exit along travel),
        // which can only be hidden when an identical straight neighbour sits flush
        // against each of them. So a cell is MIDDLE only when BOTH its entry side and
        // its exit side are a straight continuation (same facing AND same slope) of the
        // adjacent cell. Otherwise the exposed face must be capped:
        //   - not continued from behind  -> START (caps the back/entry face)
        //   - not continued ahead        -> END   (caps the front/exit face)
        // This caps corners (facing changes) and slope-junction boundaries (slope
        // changes) automatically: the run's last cell before a turn becomes END and the
        // turn cell becomes START, so no see-through MIDDLE face is ever exposed.
        //
        // IMPORTANT: this only sets the visual `part` property. Chain continuity (the
        // RPM tree) is driven entirely by prevPos + exitCandidates(±1 Y), which the
        // caller links in step order and which this method does not touch — so START/END
        // in the middle of a route never breaks the consumption chain.
        List<ConveyorPath.Step> steps = new ArrayList<>(dedup.size());
        for (int i = 0; i < dedup.size(); i++) {
            Cell c = dedup.get(i);
            boolean entryStraight = i > 0 && straightContinuation(dedup.get(i - 1), c);
            boolean exitStraight = i < dedup.size() - 1 && straightContinuation(c, dedup.get(i + 1));
            boolean isHead = i == 0;
            boolean isTail = i == dedup.size() - 1;
            ConveyorPart part;
            if (isTail && !exitStraight)
                // Route tail: cap the exposed front/exit face = END, even when the entry side is
                // a turn/slope change (otherwise an isolated tail would mislabel as START).
                part = ConveyorPart.END;
            else if (!entryStraight)
                part = ConveyorPart.START; // route head, or the cell just after a turn/slope change
            else if (!exitStraight)
                part = ConveyorPart.END; // route tail, or the cell just before a turn/slope change
            else
                part = ConveyorPart.MIDDLE; // straight neighbour flush on BOTH faces
            // The very first cell is always the head (START): its back/entry face is exposed.
            if (isHead)
                part = ConveyorPart.START;
            steps.add(new ConveyorPath.Step(c.x, c.y, c.z, c.stepX, c.stepZ, c.slope, part));
        }
        return Result.ok(steps);
    }

    /**
     * Lay one leg from {@code a} to {@code b}. The horizontal route is L-shaped: it
     * runs along one axis, turns at the corner, then runs along the other axis. Both
     * L orientations (x-first and z-first) are tried and the FIRST fully-valid one is
     * kept, so a cramped/blocked default L is replaced by the clear alternative
     * instead of failing. An axis-aligned leg ({@code dx==0} XOR {@code dz==0}) has a
     * single ordering — one straight run, never a corner. The Y ramps along the path
     * using slope cells (planned waypoint climb/descent) AND auto-steps over/under a
     * single-block terrain bump or dip (see {@link #walkLeg}).
     */
    private static LegError planLeg(Waypoint a, Waypoint b, List<Cell> out, CellValidator valid, int legIndex,
            Set<Long> visited) {
        int dx = b.x - a.x;
        int dz = b.z - a.z;
        int dy = b.y - a.y;

        if (dx == 0 && dz == 0) {
            if (dy == 0)
                return new LegError("Waypoint " + legIndex + " repeats the previous point.",
                        "polyfill.wand.err_repeat", legIndex);
            return new LegError("Waypoint " + legIndex + " only changes height — move it horizontally too.",
                    "polyfill.wand.err_vertical_only", legIndex);
        }

        int horiz = Math.abs(dx) + Math.abs(dz); // Manhattan length (L-route cell count)
        if (Math.abs(dy) > horiz)
            return new LegError("Waypoint " + legIndex + " is too steep: needs " + Math.abs(dy)
                    + " blocks of run for the slope but only has " + horiz + ".",
                    "polyfill.wand.err_steep", legIndex, Math.abs(dy), horiz);

        int sx = Integer.signum(dx);
        int sz = Integer.signum(dz);
        boolean axisAligned = (dx == 0) || (dz == 0);

        // Candidate L orientations. For an axis-aligned leg there is exactly ONE
        // ordering (a single straight run, no corner). Otherwise prefer the
        // dominant-axis-first L (corner where the player expects it) but fall back to
        // the other orientation when the first one is blocked anywhere along it.
        List<int[][]> orderings = new ArrayList<>(2);
        boolean xFirstPreferred = Math.abs(dx) >= Math.abs(dz);
        orderings.add(buildMoves(dx, dz, sx, sz, xFirstPreferred));
        if (!axisAligned)
            orderings.add(buildMoves(dx, dz, sx, sz, !xFirstPreferred));

        // Try each orientation; keep the first that walks end-to-end with every derived
        // cell valid (terrain-aware slopes included). Remember the first failure so a
        // truly impossible leg still reports a precise, localized reason.
        LegError firstErr = null;
        for (int[][] moves : orderings) {
            List<Cell> legCells = new ArrayList<>(horiz + 1);
            LegError err = walkLeg(a, b, moves, valid, legIndex, out, legCells, visited);
            if (err == null) {
                // Commit this leg's cells to the global visited set. The shared join cell
                // (this leg's anchor == previous leg's last cell) is already present; adding
                // it again is a no-op, so the cross-leg dedup in plan() still collapses it.
                for (Cell c : legCells)
                    visited.add(pack(c.x, c.y, c.z));
                out.addAll(legCells);
                return null;
            }
            if (firstErr == null)
                firstErr = err;
        }
        return firstErr;
    }

    /** Pack an integer cell coordinate into a single long key for the visited set. */
    private static long pack(int x, int y, int z) {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (y & 0xFFF) << 26) | (z & 0x3FFFFFF);
    }

    /**
     * Build the ordered unit-move list for one L orientation. {@code xFirst} runs the
     * whole X delta first then the Z delta (or vice-versa). For an axis-aligned leg one
     * of the deltas is zero, so the result is a single straight run regardless of
     * {@code xFirst}.
     */
    private static int[][] buildMoves(int dx, int dz, int sx, int sz, boolean xFirst) {
        int horiz = Math.abs(dx) + Math.abs(dz);
        int[][] moves = new int[horiz][2];
        int idx = 0;
        if (xFirst) {
            for (int i = 0; i < Math.abs(dx); i++)
                moves[idx++] = new int[] { sx, 0 };
            for (int i = 0; i < Math.abs(dz); i++)
                moves[idx++] = new int[] { 0, sz };
        } else {
            for (int i = 0; i < Math.abs(dz); i++)
                moves[idx++] = new int[] { 0, sz };
            for (int i = 0; i < Math.abs(dx); i++)
                moves[idx++] = new int[] { sx, 0 };
        }
        return moves;
    }

    /**
     * Walk one orientation's move list, deciding each cell's block position + slope under
     * the LOWER-block convention. {@code surf} is the belt SURFACE Y entering the current
     * cell (= the exit Y of the previous cell). Per cell:
     * <ul>
     *   <li><b>FLAT/UP block</b> sits AT {@code surf}. UP exits one higher
     *       ({@code nextSurf = surf+1}); FLAT exits at {@code surf}.</li>
     *   <li><b>DOWN block</b> sits one BELOW the incoming surface ({@code surf-1}) — it
     *       occupies the LOWER of the two flat levels it bridges: entered from {@code surf}
     *       (its back/high edge meets the flat behind at {@code surf}) and exiting at
     *       {@code surf-1} (its front/low edge meets the flat ahead at {@code surf-1}).</li>
     * </ul>
     * Slope selection per cell:
     * <ul>
     *   <li><b>Planned climb</b> ({@code surf < b.y}): ramp UP — exit + headroom free, next
     *       cell lands one higher. The climb's first cell keeps the leg anchor at its y.</li>
     *   <li><b>Planned descent</b> ({@code surf > b.y}): ramp LATE — stay FLAT while there is
     *       slack ({@code surf - b.y < movesRemaining}) so the anchor/top stays at its y,
     *       then drop one level per remaining move with DOWN blocks at the lower level. A
     *       blocked drop FAILS (never a floating slope); a blocked flat with slack drops
     *       early instead.</li>
     *   <li><b>Auto-step over one block</b> (at the planned height, exit column blocked but
     *       its top clear): an atomic UP→top-FLAT→DOWN maneuver. The UP (block@surf) makes the
     *       next cell land on the obstacle top (surf+1, FLAT); the following DOWN sits back at
     *       surf (one below that top) so the UP and the DOWN share the SAME y — a +1 hill that
     *       is never vertically stacked. Committed only when the whole ramp is free — else FAIL.</li>
     * </ul>
     * Each placed cell (UP also its {@code surf+1} headroom) must pass the validator and be
     * unvisited; the shared join cell from the previous leg is exempt (it's a waypoint
     * anchor). A blocked cell with no legal ramp fails with {@code err_blocked}.
     */
    private static LegError walkLeg(Waypoint a, Waypoint b, int[][] moves, CellValidator valid, int legIndex,
            List<Cell> existing, List<Cell> out, Set<Long> visited) {
        int horiz = moves.length;
        int cx = a.x, surf = a.y, cz = a.z;
        // Auto-step state. After the UP onto a single obstacle the belt rides one block above
        // the planned floor: the NEXT cell is the apex FLAT on top of the obstacle
        // ({@code forceApex}), and the cell AFTER that ramps straight back DOWN onto the floor
        // ({@code forceDown}). Splitting it into apex-then-down keeps the DOWN block at the
        // LOWER level (same y as the UP), so the over-and-back hump is never vertically stacked.
        boolean forceApex = false;
        boolean forceDown = false;
        for (int i = 0; i < horiz; i++) {
            int mx = moves[i][0];
            int mz = moves[i][1];
            int nx = cx + mx, nz = cz + mz; // horizontal landing column

            ConveyorSlope slope;
            int blockY;     // y of THIS cell's block
            int nextSurf;   // surface y entering the NEXT cell (this cell's exit y)
            if (forceApex) {
                // Apex of an auto-step: a FLAT riding on TOP of the obstacle (verified free when
                // the UP was committed). It keeps the raised surface, then forces the descent.
                blockY = surf;
                slope = ConveyorSlope.FLAT;
                nextSurf = surf;
                forceApex = false;
                forceDown = true;
            } else if (forceDown) {
                // Far side of an auto-step: a DOWN block one below the raised surface, exiting
                // back onto the planned floor (surf-1). It sits at the SAME y as the auto-step UP.
                blockY = surf - 1;
                if (!cellFree(valid, existing, out, visited, nx, blockY, nz))
                    return blocked(legIndex, nx, blockY, nz);
                slope = ConveyorSlope.DOWN;
                nextSurf = surf - 1;
                forceDown = false;
            } else if (surf < b.y) {
                // Owe a climb: ramp UP if headroom + the +1 exit are free, else fail. The UP
                // block sits AT the surface; its exit (and the next cell) is one higher.
                blockY = surf;
                if (!cellFree(valid, existing, out, visited, cx, surf + 1, cz))
                    return blocked(legIndex, cx, surf + 1, cz);
                if (!cellFree(valid, existing, out, visited, nx, surf + 1, nz))
                    return blocked(legIndex, nx, surf + 1, nz);
                slope = ConveyorSlope.UP;
                nextSurf = surf + 1;
            } else if (surf > b.y) {
                // Owe a descent. Ramp LATE: stay FLAT (at the surface) while there is slack so
                // the top/anchor keeps its y, then drop. We MUST drop now when the remaining
                // height to lose equals the remaining moves; we MAY drop early if the flat exit
                // is blocked. A DOWN block sits one below the surface and exits at that level.
                int movesRemaining = horiz - i; // includes this move
                boolean mustDrop = (surf - b.y) >= movesRemaining;
                boolean flatFree = cellFree(valid, existing, out, visited, nx, surf, nz);
                if (!mustDrop && flatFree) {
                    blockY = surf;
                    slope = ConveyorSlope.FLAT;
                    nextSurf = surf;
                } else {
                    blockY = surf - 1;
                    if (!cellFree(valid, existing, out, visited, cx, blockY, cz))
                        return revisitOrBlocked(legIndex, cx, blockY, cz, visited);
                    if (!cellFree(valid, existing, out, visited, nx, blockY, nz))
                        return blocked(legIndex, nx, blockY, nz);
                    slope = ConveyorSlope.DOWN;
                    nextSurf = surf - 1;
                }
            } else {
                // At the planned height: go FLAT, or auto-step ±1 over a single TERRAIN block.
                blockY = surf;
                if (cellFree(valid, existing, out, visited, nx, surf, nz)) {
                    slope = ConveyorSlope.FLAT;
                    nextSurf = surf;
                } else if (isCrossing(existing, out, visited, nx, surf, nz)) {
                    // The exit is occupied by a belt WE already placed (this leg or earlier):
                    // ramping over our own belt would cross the route. Fail, never auto-step.
                    return overlap(legIndex);
                } else if (canAutoStep(valid, existing, out, visited, cx, surf, cz, nx, nz, i, horiz)) {
                    // A single solid TERRAIN block ahead with a clear top: ramp UP onto it now.
                    // forceApex rides the obstacle top next move, then forceDown returns to the
                    // floor — the UP and the DOWN landing at the SAME y (never stacked).
                    slope = ConveyorSlope.UP;
                    nextSurf = surf + 1;
                    forceApex = true;
                } else {
                    return blocked(legIndex, nx, surf, nz);
                }
            }

            // Validate this cell itself (skip the shared join cell of the previous leg).
            if (!isAnchor(existing, out, cx, blockY, cz)) {
                if (!valid.isFree(cx, blockY, cz) || visited.contains(pack(cx, blockY, cz)))
                    return revisitOrBlocked(legIndex, cx, blockY, cz, visited);
            }

            Cell cell = new Cell(cx, blockY, cz);
            cell.stepX = mx;
            cell.stepZ = mz;
            cell.slope = slope;
            out.add(cell);

            // Advance to the next cell's surface.
            cx = nx;
            cz = nz;
            surf = nextSurf;
        }

        // An auto-step that never got its apex/matching descent would leave the belt floating —
        // forbidden. (canAutoStep only commits when both fit, so this is a safety net.)
        if (forceApex || forceDown)
            return new LegError("Auto-step over terrain could not descend before waypoint " + legIndex + ".",
                    "polyfill.wand.err_terrain", legIndex);

        // The leg must arrive exactly at b. Terrain auto-steps can leave the belt off the
        // target height if it couldn't come back down in time — report it clearly.
        if (cx != b.x || cz != b.z)
            return new LegError("Could not reach waypoint " + legIndex + " (internal route error).",
                    "polyfill.wand.err_unreachable", legIndex);
        if (surf != b.y)
            return new LegError("Waypoint " + legIndex + " could not settle to its height after stepping over terrain.",
                    "polyfill.wand.err_terrain", legIndex);

        // The destination cell must not collide with a belt an EARLIER leg already placed
        // (a genuine crossing). It legitimately coincides with the next leg's start anchor,
        // but that leg hasn't run yet, so visited can't contain it here except via crossing.
        if (visited.contains(pack(b.x, b.y, b.z)))
            return overlap(legIndex);

        // Append the destination cell as a flat anchor so the next leg (or END) starts here.
        Cell dest = new Cell(b.x, b.y, b.z);
        dest.stepX = 0;
        dest.stepZ = 0;
        dest.slope = ConveyorSlope.FLAT;
        out.add(dest);
        return null;
    }

    private static LegError blocked(int legIndex, int x, int y, int z) {
        return new LegError("Blocked at " + x + "," + y + "," + z + " (waypoint " + legIndex + ").",
                "polyfill.wand.err_blocked", x, y, z, legIndex);
    }

    /** The route would cross/overlap a belt it already placed. */
    private static LegError overlap(int legIndex) {
        return new LegError("Route would cross itself near waypoint " + legIndex + ".",
                "polyfill.wand.err_overlap");
    }

    /**
     * Distinguish a self-crossing (the cell is already in the global visited set, i.e. a
     * belt we placed earlier) from terrain being blocked, so the player gets the precise
     * localized reason.
     */
    private static LegError revisitOrBlocked(int legIndex, int x, int y, int z, Set<Long> visited) {
        if (visited.contains(pack(x, y, z)))
            return overlap(legIndex);
        return blocked(legIndex, x, y, z);
    }

    /**
     * True when an atomic +1 auto-step UP over a SINGLE solid block at {@code (nx,cy,nz)} is
     * fully legal. The maneuver spans THREE cells: UP at this cell ({@code cy}), an apex FLAT
     * riding the obstacle top ({@code nx,cy+1,nz}), then a DOWN back onto the floor ({@code cy})
     * one move later. So we need: this cell's headroom ({@code cx,cy+1,cz}) and the obstacle top
     * ({@code nx,cy+1,nz}) clear, AND at least TWO more moves (apex + descent) before the leg
     * ends so the belt returns to the floor (no floating high stretch). The far DOWN landing is
     * validated again at forceDown time against the actual next move.
     */
    private static boolean canAutoStep(CellValidator valid, List<Cell> existing, List<Cell> out, Set<Long> visited,
            int cx, int cy, int cz, int nx, int nz, int moveIndex, int horiz) {
        // Headroom above the ramp cell and the obstacle top must be clear + unvisited.
        if (!cellFree(valid, existing, out, visited, cx, cy + 1, cz))
            return false;
        if (!cellFree(valid, existing, out, visited, nx, cy + 1, nz))
            return false;
        // Need an apex move (i+1) AND a descent move (i+2) within this leg so the belt comes
        // back down to the floor before the leg ends.
        if (moveIndex + 2 >= horiz)
            return false;
        return true;
    }

    /**
     * True when a derived cell at {@code (x,y,z)} may be occupied: either it is the
     * shared anchor cell carried over from a previous leg / already placed this leg
     * (always allowed — it's the player's waypoint or our own ramp), or the validator
     * says it is free AND it is not already used by an earlier-placed belt ({@code visited})
     * nor by a cell this leg already emitted (self-crossing guard). The shared join cell of
     * the previous/this leg is always occupiable.
     */
    private static boolean cellFree(CellValidator valid, List<Cell> existing, List<Cell> out, Set<Long> visited,
            int x, int y, int z) {
        if (isAnchor(existing, out, x, y, z))
            return true;
        if (visited.contains(pack(x, y, z)))
            return false;
        if (containsCell(out, x, y, z))
            return false;
        return valid.isFree(x, y, z);
    }

    /**
     * True when {@code (x,y,z)} is occupied by a belt the plan already placed — either an
     * earlier leg ({@code visited}) or this leg ({@code out}) — and is NOT the shared join
     * anchor. Used to tell a self-crossing apart from terrain so we never ramp over our own
     * belt.
     */
    private static boolean isCrossing(List<Cell> existing, List<Cell> out, Set<Long> visited, int x, int y, int z) {
        if (isAnchor(existing, out, x, y, z))
            return false;
        return visited.contains(pack(x, y, z)) || containsCell(out, x, y, z);
    }

    /** True when this leg has already emitted a belt at {@code (x,y,z)} (self-overlap guard). */
    private static boolean containsCell(List<Cell> out, int x, int y, int z) {
        for (Cell c : out)
            if (c.x == x && c.y == y && c.z == z)
                return true;
        return false;
    }

    /** True when {@code (x,y,z)} is the last cell appended by the previous leg or this leg. */
    private static boolean isAnchor(List<Cell> existing, List<Cell> out, int x, int y, int z) {
        if (!out.isEmpty()) {
            Cell last = out.get(out.size() - 1);
            if (last.x == x && last.y == y && last.z == z)
                return true;
        } else if (!existing.isEmpty()) {
            Cell last = existing.get(existing.size() - 1);
            return last.x == x && last.y == y && last.z == z;
        }
        return false;
    }

    /** A per-leg failure: English message + translatable key + integer args. */
    private static final class LegError {
        final String message;
        final String key;
        final int[] args;

        LegError(String message, String key, int... args) {
            this.message = message;
            this.key = key;
            this.args = args;
        }
    }

    /**
     * True when {@code b} is a flush straight continuation of {@code a}: same travel
     * facing AND same slope, and {@code b} sits exactly at {@code a}'s exit cell
     * (a's horizontal step + a's slope Y). Only then can {@code a}'s exit face and
     * {@code b}'s entry face hide each other, allowing both to be MIDDLE. A turn
     * (facing change) or a slope change (FLAT<->UP/DOWN) is NOT a straight
     * continuation, so the boundary cells get capped with START/END.
     */
    private static boolean straightContinuation(Cell a, Cell b) {
        if (a.stepX != b.stepX || a.stepZ != b.stepZ)
            return false; // a turn: facing changed
        if (a.slope != b.slope)
            return false; // a slope junction (FLAT<->UP/DOWN): expose + cap the face
        int exitY = a.y + a.slope.stepY();
        return b.x == a.x + a.stepX && b.z == a.z + a.stepZ && b.y == exitY;
    }
}
