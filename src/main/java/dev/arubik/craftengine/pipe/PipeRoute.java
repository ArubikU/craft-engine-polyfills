package dev.arubik.craftengine.pipe;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Pure, Bukkit-free 3D route planner for the PIPE wand.
 *
 * <p>Pipes auto-connect via {@code ConnectedBlockBehavior} in all 6 directions, so a route
 * is simply an ordered set of integer cells; there is NO facing/slope/part. The connection
 * variant of each placed block is resolved by the engine (and previewed by the listener via a
 * mask). This planner only decides WHICH cells the route occupies.</p>
 *
 * <p>Three planning modes:</p>
 * <ul>
 *   <li>{@link #straight} — an axis-aligned line A→B (must align on exactly one of X/Y/Z).</li>
 *   <li>{@link #magic} — a shortest 3D path A→B over passable cells (BFS, bounded box, cap ~256).</li>
 *   <li>{@link #pointed} — route each consecutive waypoint pair (straight by default, falling back
 *       to magic) then concatenate + dedup.</li>
 * </ul>
 *
 * <p>The plan NEVER revisits a cell (a plan-wide visited set); a crossing fails with
 * {@code err_overlap}. A cell may be occupied when the validator says it is free OR it already
 * holds a same-type pipe (passable, it just connects) — both expressed through {@link CellValidator}.
 * No Bukkit/NMS types are referenced so the planner is unit-testable.</p>
 */
public final class PipeRoute {

    /** Max cells a single planned route may contain (BFS / total budget cap). */
    public static final int MAX_CELLS = 256;

    private PipeRoute() {
    }

    /** Tells the planner whether a pipe cell may be occupied / traversed. */
    @FunctionalInterface
    public interface CellValidator {
        /**
         * @return true when a pipe segment may be placed at / routed through this block position.
         *         This must be true both for air/replaceable cells AND for cells already holding a
         *         same-type pipe (which are passable — the route just connects to them). A waypoint
         *         cell chosen by the player is always allowed by the planner regardless.
         */
        boolean isPassable(int x, int y, int z);
    }

    /** A single integer cell. */
    public static final class Cell {
        public final int x, y, z;

        public Cell(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Cell c))
                return false;
            return x == c.x && y == c.y && z == c.z;
        }

        @Override
        public int hashCode() {
            int h = x;
            h = 31 * h + y;
            h = 31 * h + z;
            return h;
        }

        @Override
        public String toString() {
            return "Cell[" + x + "," + y + "," + z + "]";
        }
    }

    /** Alias for a waypoint (same shape as a cell). */
    public static final class Waypoint {
        public final int x, y, z;

        public Waypoint(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    /**
     * Result: an ordered cell list, or a failure carrying an English {@code error()} (tests/logs)
     * and a client-translatable {@code errorKey()} + integer {@code errorArgs()} (rendered by the
     * wand as an Adventure {@code Component.translatable}). Keys live under {@code polyfill.pipewand.*}.
     */
    public static final class Result {
        private final List<Cell> cells;
        private final String error;
        private final String errorKey;
        private final int[] errorArgs;

        private Result(List<Cell> cells, String error, String errorKey, int[] errorArgs) {
            this.cells = cells;
            this.error = error;
            this.errorKey = errorKey;
            this.errorArgs = errorArgs;
        }

        public static Result ok(List<Cell> cells) {
            return new Result(cells, null, null, null);
        }

        public static Result fail(String error, String errorKey, int... errorArgs) {
            return new Result(null, error, errorKey, errorArgs);
        }

        public boolean isValid() {
            return error == null;
        }

        public List<Cell> cells() {
            return cells;
        }

        public String error() {
            return error;
        }

        public String errorKey() {
            return errorKey;
        }

        public int[] errorArgs() {
            return errorArgs;
        }
    }

    // ----------------------------------------------------------------- straight

    /**
     * Plan an axis-aligned straight line from A to B. A and B must differ and align on exactly one
     * axis (the other two deltas must be zero). Every intermediate cell (excluding the endpoints,
     * which are the player's anchors) must be passable.
     */
    public static Result straight(Waypoint a, Waypoint b, CellValidator valid) {
        return straight(a, b, valid, java.util.Collections.emptySet());
    }

    /** Straight line, with {@code blocked} cells (already used by other legs) treated as impassable. */
    public static Result straight(Waypoint a, Waypoint b, CellValidator valid, Set<Long> blocked) {
        int dx = b.x - a.x, dy = b.y - a.y, dz = b.z - a.z;
        int axes = (dx != 0 ? 1 : 0) + (dy != 0 ? 1 : 0) + (dz != 0 ? 1 : 0);
        if (axes == 0)
            return Result.fail("Start and end are the same cell.", "polyfill.pipewand.err_same_pos");
        if (axes != 1)
            return Result.fail("A straight pipe must align on exactly one axis (X, Y or Z).",
                    "polyfill.pipewand.err_not_straight");

        int sx = Integer.signum(dx), sy = Integer.signum(dy), sz = Integer.signum(dz);
        int len = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
        if (len + 1 > MAX_CELLS)
            return Result.fail("Route is too long.", "polyfill.pipewand.err_too_long");

        List<Cell> out = new ArrayList<>(len + 1);
        Set<Long> visited = new HashSet<>();
        for (int i = 0; i <= len; i++) {
            int x = a.x + sx * i, y = a.y + sy * i, z = a.z + sz * i;
            // Endpoints are anchors (always allowed); intermediate cells must be passable + not blocked.
            if (i != 0 && i != len && (!valid.isPassable(x, y, z) || blocked.contains(pack(x, y, z))))
                return Result.fail("Blocked at " + x + "," + y + "," + z + ".",
                        "polyfill.pipewand.err_blocked", x, y, z);
            if (!visited.add(pack(x, y, z)))
                return Result.fail("Route would cross itself.", "polyfill.pipewand.err_overlap");
            out.add(new Cell(x, y, z));
        }
        return Result.ok(out);
    }

    // -------------------------------------------------------------------- magic

    /**
     * Plan the shortest 3D path from A to B via breadth-first search over passable cells, bounded to
     * a padded box around A/B and capped at {@link #MAX_CELLS}. The endpoints are always traversable;
     * intermediate cells must be passable. Returns {@code err_no_route} when no path exists.
     */
    public static Result magic(Waypoint a, Waypoint b, CellValidator valid) {
        return magic(a, b, valid, java.util.Collections.emptySet());
    }

    /** 3D BFS path, with {@code blocked} cells (already used by other legs) treated as impassable so
     *  the search routes AROUND them instead of crossing — there is almost always a clear 3D detour. */
    public static Result magic(Waypoint a, Waypoint b, CellValidator valid, Set<Long> blocked) {
        if (a.x == b.x && a.y == b.y && a.z == b.z)
            return Result.fail("Start and end are the same cell.", "polyfill.pipewand.err_same_pos");

        // Bounded search box: the A/B bounding box padded by a margin so the path can detour around
        // obstacles, but never explodes the search space.
        final int pad = 8;
        int minX = Math.min(a.x, b.x) - pad, maxX = Math.max(a.x, b.x) + pad;
        int minY = Math.min(a.y, b.y) - pad, maxY = Math.max(a.y, b.y) + pad;
        int minZ = Math.min(a.z, b.z) - pad, maxZ = Math.max(a.z, b.z) + pad;

        long start = pack(a.x, a.y, a.z);
        long goal = pack(b.x, b.y, b.z);
        Map<Long, Long> cameFrom = new HashMap<>();
        ArrayDeque<long[]> queue = new ArrayDeque<>(); // {packed, x, y, z}
        Set<Long> seen = new HashSet<>();
        queue.add(new long[] { start, a.x, a.y, a.z });
        seen.add(start);

        int[][] dirs = { { 1, 0, 0 }, { -1, 0, 0 }, { 0, 1, 0 }, { 0, -1, 0 }, { 0, 0, 1 }, { 0, 0, -1 } };
        int expanded = 0;
        boolean found = false;
        while (!queue.isEmpty()) {
            long[] cur = queue.poll();
            if (cur[0] == goal) {
                found = true;
                break;
            }
            if (++expanded > 200_000)
                break; // hard safety bound
            int cx = (int) cur[1], cy = (int) cur[2], cz = (int) cur[3];
            for (int[] d : dirs) {
                int nx = cx + d[0], ny = cy + d[1], nz = cz + d[2];
                if (nx < minX || nx > maxX || ny < minY || ny > maxY || nz < minZ || nz > maxZ)
                    continue;
                long nk = pack(nx, ny, nz);
                if (seen.contains(nk))
                    continue;
                boolean isGoal = nk == goal;
                // Goal/endpoint cells are anchors; intermediate cells must be passable + not blocked.
                if (!isGoal && (!valid.isPassable(nx, ny, nz) || blocked.contains(nk)))
                    continue;
                seen.add(nk);
                cameFrom.put(nk, cur[0]);
                queue.add(new long[] { nk, nx, ny, nz });
            }
        }
        if (!found)
            return Result.fail("No clear 3D path between the two points.", "polyfill.pipewand.err_no_route");

        // Reconstruct A→B.
        ArrayDeque<Long> rev = new ArrayDeque<>();
        long c = goal;
        rev.push(c);
        while (c != start) {
            Long prev = cameFrom.get(c);
            if (prev == null)
                return Result.fail("No clear 3D path between the two points.", "polyfill.pipewand.err_no_route");
            c = prev;
            rev.push(c);
        }
        if (rev.size() > MAX_CELLS)
            return Result.fail("Route is too long.", "polyfill.pipewand.err_too_long");
        List<Cell> out = new ArrayList<>(rev.size());
        for (Long packed : rev)
            out.add(unpack(packed));
        return Result.ok(out);
    }

    // ------------------------------------------------------------------ pointed

    /**
     * Plan a route through {@code waypoints} in order. Each consecutive pair is routed straight when
     * axis-aligned, otherwise via {@link #magic}. The legs are concatenated and consecutive duplicate
     * join cells are dropped. The whole plan shares one visited set so it never revisits a cell — a
     * crossing fails with {@code err_overlap}.
     */
    public static Result pointed(List<Waypoint> waypoints, CellValidator valid) {
        if (waypoints == null || waypoints.size() < 2)
            return Result.fail("Need at least 2 points.", "polyfill.pipewand.err_need_points");

        List<Cell> all = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        for (int i = 1; i < waypoints.size(); i++) {
            Waypoint a = waypoints.get(i - 1);
            Waypoint b = waypoints.get(i);
            // Route this leg AROUND the cells already used by previous legs (pass them as blocked) so
            // legs don't collide — a 3D detour almost always exists, instead of failing with err_overlap.
            Result leg = planPair(a, b, valid, visited);
            if (!leg.isValid())
                return leg;
            List<Cell> cells = leg.cells();
            for (int j = 0; j < cells.size(); j++) {
                Cell cell = cells.get(j);
                // The shared join cell (this leg's first == previous leg's last) is dropped here.
                if (!all.isEmpty()) {
                    Cell last = all.get(all.size() - 1);
                    if (last.equals(cell))
                        continue;
                }
                if (!visited.add(pack(cell.x, cell.y, cell.z)))
                    return Result.fail("Route would cross itself.", "polyfill.pipewand.err_overlap");
                all.add(cell);
            }
        }
        if (all.isEmpty())
            return Result.fail("Route is empty.", "polyfill.pipewand.err_empty");
        if (all.size() > MAX_CELLS)
            return Result.fail("Route is too long.", "polyfill.pipewand.err_too_long");
        return Result.ok(all);
    }

    /** Route one waypoint pair, avoiding {@code blocked} cells: straight when axis-aligned and clear,
     *  otherwise magic (3D BFS detour). Falls back to magic if the straight line is blocked. */
    private static Result planPair(Waypoint a, Waypoint b, CellValidator valid, Set<Long> blocked) {
        int dx = b.x - a.x, dy = b.y - a.y, dz = b.z - a.z;
        int axes = (dx != 0 ? 1 : 0) + (dy != 0 ? 1 : 0) + (dz != 0 ? 1 : 0);
        if (axes <= 1) {
            Result s = straight(a, b, valid, blocked);
            if (s.isValid())
                return s;
            // straight line blocked -> try a 3D detour instead of giving up.
            return magic(a, b, valid, blocked);
        }
        return magic(a, b, valid, blocked);
    }

    // ------------------------------------------------------------------ packing

    /** Pack an integer cell coordinate into a single long key. */
    private static long pack(int x, int y, int z) {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (y & 0xFFF) << 26) | (z & 0x3FFFFFF);
    }

    private static Cell unpack(long k) {
        int x = (int) (k >> 38) & 0x3FFFFFF;
        int y = (int) (k >> 26) & 0xFFF;
        int z = (int) k & 0x3FFFFFF;
        x = signExtend(x, 26);
        y = signExtend(y, 12);
        z = signExtend(z, 26);
        return new Cell(x, y, z);
    }

    private static int signExtend(int value, int bits) {
        int shift = 32 - bits;
        return (value << shift) >> shift;
    }
}
