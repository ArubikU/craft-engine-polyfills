package dev.arubik.craftengine.conveyor.routing;

import dev.arubik.craftengine.conveyor.belt.ConveyorPart;
import dev.arubik.craftengine.conveyor.routing.ConveyorPath;
import dev.arubik.craftengine.conveyor.belt.ConveyorSlope;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ConveyorRoute {
    private ConveyorRoute() {
    }

    public static Result plan(List<Waypoint> waypoints, CellValidator valid) {
        if (waypoints == null || waypoints.size() < 2) {
            return Result.fail("Need at least 2 points.", "polyfill.wand.err_need_points", new int[0]);
        }
        ArrayList<Cell> cells = new ArrayList<Cell>();
        HashSet<Long> visited = new HashSet<Long>();
        Object cur = waypoints.get(0);
        for (int w = 1; w < waypoints.size(); ++w) {
            Waypoint next = waypoints.get(w);
            LegError err = ConveyorRoute.planLeg((Waypoint)cur, next, cells, valid, w, visited);
            if (err != null) {
                return Result.fail(err.message, err.key, err.args);
            }
            cur = next;
        }
        if (cells.isEmpty()) {
            return Result.fail("Route is empty.", "polyfill.wand.err_empty", new int[0]);
        }
        ArrayList<Cell> dedup = new ArrayList<Cell>(cells.size());
        for (Cell c : cells) {
            if (!dedup.isEmpty()) {
                Cell last = (Cell)dedup.get(dedup.size() - 1);
                if (last.x == c.x && last.y == c.y && last.z == c.z) {
                    last.stepX = c.stepX;
                    last.stepZ = c.stepZ;
                    last.slope = c.slope;
                    continue;
                }
            }
            dedup.add(c);
        }
        for (int i = 1; i < dedup.size(); ++i) {
            Cell c;
            c = (Cell)dedup.get(i);
            if (c.stepX != 0 || c.stepZ != 0) continue;
            Cell prev = (Cell)dedup.get(i - 1);
            c.stepX = prev.stepX;
            c.stepZ = prev.stepZ;
        }
        ArrayList<ConveyorPath.Step> steps = new ArrayList<ConveyorPath.Step>(dedup.size());
        for (int i = 0; i < dedup.size(); ++i) {
            boolean isTail;
            Cell c = (Cell)dedup.get(i);
            boolean entryStraight = i > 0 && ConveyorRoute.straightContinuation((Cell)dedup.get(i - 1), c);
            boolean exitStraight = i < dedup.size() - 1 && ConveyorRoute.straightContinuation(c, (Cell)dedup.get(i + 1));
            boolean isHead = i == 0;
            boolean bl = isTail = i == dedup.size() - 1;
            ConveyorPart part = isTail && !exitStraight ? ConveyorPart.END : (!entryStraight ? ConveyorPart.START : (!exitStraight ? ConveyorPart.END : ConveyorPart.MIDDLE));
            if (isHead) {
                part = ConveyorPart.START;
            }
            steps.add(new ConveyorPath.Step(c.x, c.y, c.z, c.stepX, c.stepZ, c.slope, part));
        }
        return Result.ok(steps);
    }

    private static LegError planLeg(Waypoint a, Waypoint b, List<Cell> out, CellValidator valid, int legIndex, Set<Long> visited) {
        int dx = b.x - a.x;
        int dz = b.z - a.z;
        int dy = b.y - a.y;
        if (dx == 0 && dz == 0) {
            if (dy == 0) {
                return new LegError("Waypoint " + legIndex + " repeats the previous point.", "polyfill.wand.err_repeat", legIndex);
            }
            return new LegError("Waypoint " + legIndex + " only changes height \u2014 move it horizontally too.", "polyfill.wand.err_vertical_only", legIndex);
        }
        int horiz = Math.abs(dx) + Math.abs(dz);
        if (Math.abs(dy) > horiz) {
            return new LegError("Waypoint " + legIndex + " is too steep: needs " + Math.abs(dy) + " blocks of run for the slope but only has " + horiz + ".", "polyfill.wand.err_steep", legIndex, Math.abs(dy), horiz);
        }
        int sx = Integer.signum(dx);
        int sz = Integer.signum(dz);
        boolean axisAligned = dx == 0 || dz == 0;
        ArrayList<int[][]> orderings = new ArrayList<int[][]>(2);
        boolean xFirstPreferred = Math.abs(dx) >= Math.abs(dz);
        orderings.add(ConveyorRoute.buildMoves(dx, dz, sx, sz, xFirstPreferred));
        if (!axisAligned) {
            orderings.add(ConveyorRoute.buildMoves(dx, dz, sx, sz, !xFirstPreferred));
        }
        LegError firstErr = null;
        for (int[][] moves : orderings) {
            ArrayList<Cell> legCells;
            LegError err = ConveyorRoute.walkLeg(a, b, moves, valid, legIndex, out, legCells = new ArrayList<Cell>(horiz + 1), visited);
            if (err == null) {
                for (Cell c : legCells) {
                    visited.add(ConveyorRoute.pack(c.x, c.y, c.z));
                }
                out.addAll(legCells);
                return null;
            }
            if (firstErr != null) continue;
            firstErr = err;
        }
        return firstErr;
    }

    private static long pack(int x, int y, int z) {
        return (long)(x & 0x3FFFFFF) << 38 | (long)(y & 0xFFF) << 26 | (long)(z & 0x3FFFFFF);
    }

    private static int[][] buildMoves(int dx, int dz, int sx, int sz, boolean xFirst) {
        int horiz = Math.abs(dx) + Math.abs(dz);
        int[][] moves = new int[horiz][2];
        int idx = 0;
        if (xFirst) {
            int i;
            for (i = 0; i < Math.abs(dx); ++i) {
                moves[idx++] = new int[]{sx, 0};
            }
            for (i = 0; i < Math.abs(dz); ++i) {
                moves[idx++] = new int[]{0, sz};
            }
        } else {
            int i;
            for (i = 0; i < Math.abs(dz); ++i) {
                moves[idx++] = new int[]{0, sz};
            }
            for (i = 0; i < Math.abs(dx); ++i) {
                moves[idx++] = new int[]{sx, 0};
            }
        }
        return moves;
    }

    private static LegError walkLeg(Waypoint a, Waypoint b, int[][] moves, CellValidator valid, int legIndex, List<Cell> existing, List<Cell> out, Set<Long> visited) {
        int horiz = moves.length;
        int cx = a.x;
        int surf = a.y;
        int cz = a.z;
        boolean forceApex = false;
        boolean forceDown = false;
        for (int i = 0; i < horiz; ++i) {
            int nextSurf;
            ConveyorSlope slope;
            int blockY;
            int mx = moves[i][0];
            int mz = moves[i][1];
            int nx = cx + mx;
            int nz = cz + mz;
            if (forceApex) {
                blockY = surf;
                slope = ConveyorSlope.FLAT;
                nextSurf = surf;
                forceApex = false;
                forceDown = true;
            } else if (forceDown) {
                blockY = surf - 1;
                if (!ConveyorRoute.cellFree(valid, existing, out, visited, nx, blockY, nz)) {
                    return ConveyorRoute.blocked(legIndex, nx, blockY, nz);
                }
                slope = ConveyorSlope.DOWN;
                nextSurf = surf - 1;
                forceDown = false;
            } else if (surf < b.y) {
                blockY = surf;
                if (!ConveyorRoute.cellFree(valid, existing, out, visited, cx, surf + 1, cz)) {
                    return ConveyorRoute.blocked(legIndex, cx, surf + 1, cz);
                }
                if (!ConveyorRoute.cellFree(valid, existing, out, visited, nx, surf + 1, nz)) {
                    return ConveyorRoute.blocked(legIndex, nx, surf + 1, nz);
                }
                slope = ConveyorSlope.UP;
                nextSurf = surf + 1;
            } else if (surf > b.y) {
                int movesRemaining = horiz - i;
                boolean mustDrop = surf - b.y >= movesRemaining;
                boolean flatFree = ConveyorRoute.cellFree(valid, existing, out, visited, nx, surf, nz);
                if (!mustDrop && flatFree) {
                    blockY = surf;
                    slope = ConveyorSlope.FLAT;
                    nextSurf = surf;
                } else {
                    blockY = surf - 1;
                    if (!ConveyorRoute.cellFree(valid, existing, out, visited, cx, blockY, cz)) {
                        return ConveyorRoute.revisitOrBlocked(legIndex, cx, blockY, cz, visited);
                    }
                    if (!ConveyorRoute.cellFree(valid, existing, out, visited, nx, blockY, nz)) {
                        return ConveyorRoute.blocked(legIndex, nx, blockY, nz);
                    }
                    slope = ConveyorSlope.DOWN;
                    nextSurf = surf - 1;
                }
            } else {
                blockY = surf;
                if (ConveyorRoute.cellFree(valid, existing, out, visited, nx, surf, nz)) {
                    slope = ConveyorSlope.FLAT;
                    nextSurf = surf;
                } else {
                    if (ConveyorRoute.isCrossing(existing, out, visited, nx, surf, nz)) {
                        return ConveyorRoute.overlap(legIndex);
                    }
                    if (ConveyorRoute.canAutoStep(valid, existing, out, visited, cx, surf, cz, nx, nz, i, horiz)) {
                        slope = ConveyorSlope.UP;
                        nextSurf = surf + 1;
                        forceApex = true;
                    } else {
                        return ConveyorRoute.blocked(legIndex, nx, surf, nz);
                    }
                }
            }
            if (!(ConveyorRoute.isAnchor(existing, out, cx, blockY, cz) || valid.isFree(cx, blockY, cz) && !visited.contains(ConveyorRoute.pack(cx, blockY, cz)))) {
                return ConveyorRoute.revisitOrBlocked(legIndex, cx, blockY, cz, visited);
            }
            Cell cell = new Cell(cx, blockY, cz);
            cell.stepX = mx;
            cell.stepZ = mz;
            cell.slope = slope;
            out.add(cell);
            cx = nx;
            cz = nz;
            surf = nextSurf;
        }
        if (forceApex || forceDown) {
            return new LegError("Auto-step over terrain could not descend before waypoint " + legIndex + ".", "polyfill.wand.err_terrain", legIndex);
        }
        if (cx != b.x || cz != b.z) {
            return new LegError("Could not reach waypoint " + legIndex + " (internal route error).", "polyfill.wand.err_unreachable", legIndex);
        }
        if (surf != b.y) {
            return new LegError("Waypoint " + legIndex + " could not settle to its height after stepping over terrain.", "polyfill.wand.err_terrain", legIndex);
        }
        if (visited.contains(ConveyorRoute.pack(b.x, b.y, b.z))) {
            return ConveyorRoute.overlap(legIndex);
        }
        Cell dest = new Cell(b.x, b.y, b.z);
        dest.stepX = 0;
        dest.stepZ = 0;
        dest.slope = ConveyorSlope.FLAT;
        out.add(dest);
        return null;
    }

    private static LegError blocked(int legIndex, int x, int y, int z) {
        return new LegError("Blocked at " + x + "," + y + "," + z + " (waypoint " + legIndex + ").", "polyfill.wand.err_blocked", x, y, z, legIndex);
    }

    private static LegError overlap(int legIndex) {
        return new LegError("Route would cross itself near waypoint " + legIndex + ".", "polyfill.wand.err_overlap", new int[0]);
    }

    private static LegError revisitOrBlocked(int legIndex, int x, int y, int z, Set<Long> visited) {
        if (visited.contains(ConveyorRoute.pack(x, y, z))) {
            return ConveyorRoute.overlap(legIndex);
        }
        return ConveyorRoute.blocked(legIndex, x, y, z);
    }

    private static boolean canAutoStep(CellValidator valid, List<Cell> existing, List<Cell> out, Set<Long> visited, int cx, int cy, int cz, int nx, int nz, int moveIndex, int horiz) {
        if (!ConveyorRoute.cellFree(valid, existing, out, visited, cx, cy + 1, cz)) {
            return false;
        }
        if (!ConveyorRoute.cellFree(valid, existing, out, visited, nx, cy + 1, nz)) {
            return false;
        }
        return moveIndex + 2 < horiz;
    }

    private static boolean cellFree(CellValidator valid, List<Cell> existing, List<Cell> out, Set<Long> visited, int x, int y, int z) {
        if (ConveyorRoute.isAnchor(existing, out, x, y, z)) {
            return true;
        }
        if (visited.contains(ConveyorRoute.pack(x, y, z))) {
            return false;
        }
        if (ConveyorRoute.containsCell(out, x, y, z)) {
            return false;
        }
        return valid.isFree(x, y, z);
    }

    private static boolean isCrossing(List<Cell> existing, List<Cell> out, Set<Long> visited, int x, int y, int z) {
        if (ConveyorRoute.isAnchor(existing, out, x, y, z)) {
            return false;
        }
        return visited.contains(ConveyorRoute.pack(x, y, z)) || ConveyorRoute.containsCell(out, x, y, z);
    }

    private static boolean containsCell(List<Cell> out, int x, int y, int z) {
        for (Cell c : out) {
            if (c.x != x || c.y != y || c.z != z) continue;
            return true;
        }
        return false;
    }

    private static boolean isAnchor(List<Cell> existing, List<Cell> out, int x, int y, int z) {
        if (!out.isEmpty()) {
            Cell last = out.get(out.size() - 1);
            if (last.x == x && last.y == y && last.z == z) {
                return true;
            }
        } else if (!existing.isEmpty()) {
            Cell last = existing.get(existing.size() - 1);
            return last.x == x && last.y == y && last.z == z;
        }
        return false;
    }

    private static boolean straightContinuation(Cell a, Cell b) {
        if (a.stepX != b.stepX || a.stepZ != b.stepZ) {
            return false;
        }
        if (a.slope != b.slope) {
            return false;
        }
        int exitY = a.y + a.slope.stepY();
        return b.x == a.x + a.stepX && b.z == a.z + a.stepZ && b.y == exitY;
    }

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

        public static Result fail(String error, String errorKey, int ... errorArgs) {
            return new Result(null, error, errorKey, errorArgs);
        }

        public boolean isValid() {
            return this.error == null;
        }

        public List<ConveyorPath.Step> steps() {
            return this.steps;
        }

        public String error() {
            return this.error;
        }

        public String errorKey() {
            return this.errorKey;
        }

        public int[] errorArgs() {
            return this.errorArgs;
        }
    }

    public static final class Waypoint {
        public final int x;
        public final int y;
        public final int z;

        public Waypoint(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    @FunctionalInterface
    public static interface CellValidator {
        public boolean isFree(int var1, int var2, int var3);
    }

    private static final class LegError {
        final String message;
        final String key;
        final int[] args;

        LegError(String message, String key, int ... args) {
            this.message = message;
            this.key = key;
            this.args = args;
        }
    }

    private static final class Cell {
        final int x;
        final int y;
        final int z;
        int stepX;
        int stepZ;
        ConveyorSlope slope;

        Cell(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}

