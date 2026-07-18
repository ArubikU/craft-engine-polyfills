package dev.arubik.craftengine.contraption.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.minecraft.world.phys.AABB;

/**
 * Covers a cell's REAL collision geometry with a bounded number of axis-aligned CUBES, for
 * {@link ContraptionShulkerColliderSwarm}'s distance-LOD colliders.
 *
 * <h2>Why cubes, not boxes</h2>
 * A packet-spawned {@code SHULKER}'s client-side box is not freely dimensioned: vanilla's
 * {@code Shulker#getProgressDeltaAabb} builds {@code [-scale/2, 0, -scale/2]}..{@code [scale/2,
 * scale, scale/2]} — a single CUBE of side {@code scale}, bottom-anchored on the entity and
 * centred on its X/Z (see {@link ContraptionShulkerColliderSwarm#addSlot}'s decompile writeup).
 * There is no per-axis size. So approximating an arbitrary block shape means COVERING it with N
 * cubes, and the whole quality/cost tradeoff is how large N is allowed to be.
 *
 * <h2>Cover the real boxes, not an octree of the unit cube</h2>
 * The input is the cell's {@code VoxelShape} already merged and flattened via
 * {@code optimize().toAabbs()} — a slab is ONE flat box, a stair TWO, a door ONE thin box. Tiling
 * each of those real regions is what makes a close-up collider actually stair-shaped; blindly
 * splitting the enclosing unit cube into 8 or 64 octants would spend the whole budget describing
 * air. Consequently the emitted cube count is frequently BELOW the budget: a bottom slab needs
 * exactly 4 cubes of side 0.5 and asking for 16 still returns 4, because 4 already reproduces the
 * region exactly.
 *
 * <h2>Tiling rule</h2>
 * For one box of extents {@code (sx,sy,sz)} and budget {@code b}: pick the SMALLEST cube side
 * {@code s} whose grid {@code ceil(sx/s)*ceil(sy/s)*ceil(sz/s)} still fits in {@code b}, then
 * shrink {@code s} to {@code max(sx/nx, sy/ny, sz/nz)} — the smallest side that still spans every
 * axis at those counts, which is what keeps over-coverage minimal. Cubes are then spread so the
 * first sits at the box minimum and the last ENDS at the box maximum, overlapping in between
 * rather than overshooting: exact coverage with no spill, since overlapping colliders are free.
 * An axis whose count lands at 1 is the only place spill can happen (the cube is centred on that
 * axis and may stick out either side) — unavoidable for e.g. a 0.1875-thick door under a budget
 * that can't afford 1/16-scale cubes.
 */
public final class ShulkerBoxFit {

    private ShulkerBoxFit() {
    }

    /**
     * Smallest cube side ever emitted, in blocks. 1/16 is the granularity vanilla's own block
     * shapes are authored at, so nothing finer can add real precision — and it floors the entity
     * count that a pathological shape could otherwise demand.
     */
    public static final double MIN_CUBE_SIZE = 0.0625;

    /**
     * One fitted collider cube: its MINIMUM corner plus a uniform side length, in whatever frame
     * the input boxes were in. A shulker slot placed for this cube sits at
     * {@code (x0 + size/2, y0, z0 + size/2)} — X/Z centred, Y bottom-anchored — matching the
     * entity's own box construction.
     */
    public record Cube(double x0, double y0, double z0, double size) {

        public double centerX() {
            return x0 + size / 2.0;
        }

        public double centerZ() {
            return z0 + size / 2.0;
        }
    }

    /**
     * Fits at most {@code budget} cubes to {@code boxes}.
     *
     * @param boxes the cell's real collision boxes (already merged/optimized), in any single frame
     * @param budget the maximum number of cubes; the result may be smaller when fewer already
     *        reproduce the geometry exactly
     */
    public static List<Cube> fit(List<AABB> boxes, int budget) {
        return fit(boxes, budget, false);
    }

    /**
     * As {@link #fit(List, int)}, but {@code fillBudget} spends the WHOLE budget even on a shape a
     * single cube already covers exactly.
     *
     * <h2>Why an exact fit is not always the best fit</h2>
     * A full block is one 1x1x1 box, and one cube covers it perfectly — so the fitter stops at one, and
     * that is right whenever the contraption is axis-aligned: the cube IS the block.
     *
     * <p>It stops being right the moment the body is rotated off-axis. A shulker's box is always
     * axis-aligned in the world (its scale is a single number; it cannot be turned), so a rotated block
     * drawn as one big axis-aligned cube bulges past its own render on the diagonals and leaves gaps on
     * the faces. Many SMALL cubes, each placed at its own rotated position, trace the rotated block far
     * more closely — the approximation error shrinks with the cube size, not with the count. That is why
     * the near tiers are worth spending on a plain cube, but only when the body is actually turned.
     */
    public static List<Cube> fit(List<AABB> boxes, int budget, boolean fillBudget) {
        if (boxes == null || boxes.isEmpty() || budget < 1) {
            return List.of();
        }
        if (budget == 1) {
            return List.of(coarseCube(overallBounds(boxes)));
        }
        // A shape with more regions than the budget can't give each region even one cube, so the
        // budget buys more by tiling the enclosing bounds than by dropping regions outright
        // (dropping one leaves a hole the player falls through; over-covering is merely blunt).
        List<AABB> targets = boxes.size() <= budget ? boxes : List.of(overallBounds(boxes));
        int[] perBox = allocate(targets, budget);
        List<Cube> out = new ArrayList<>(budget);
        for (int i = 0; i < targets.size(); i++) {
            out.addAll(fitBox(targets.get(i), perBox[i], fillBudget));
        }
        return out;
    }

    /**
     * The single-cube (farthest-LOD) approximation: side = the shape's Y extent, X/Z centred on the
     * shape, bottom at its {@code minY}. This is deliberately NOT {@link #fitBox} with a budget of
     * one (which would have to use the LARGEST extent as the side, inflating a slab to a full cube):
     * it reproduces the long-standing one-shulker-per-cell behaviour byte-for-byte, so a distant
     * contraption collides exactly as it did before LOD existed.
     */
    private static Cube coarseCube(AABB b) {
        double height = b.maxY - b.minY;
        // A shape taller than one cell is degenerate for a cube approximation — fall back to the
        // full-cell cube rather than trusting it, matching the previous FULL_CUBE fallback.
        double side = (height <= 0.0 || height > 1.0) ? 1.0 : Math.max(height, MIN_CUBE_SIZE);
        double cx = (b.minX + b.maxX) / 2.0;
        double cz = (b.minZ + b.maxZ) / 2.0;
        return new Cube(cx - side / 2.0, b.minY, cz - side / 2.0, side);
    }

    private static AABB overallBounds(List<AABB> boxes) {
        AABB out = boxes.get(0);
        for (int i = 1; i < boxes.size(); i++) {
            out = out.minmax(boxes.get(i));
        }
        return out;
    }

    /**
     * Splits {@code budget} across {@code boxes} by volume, largest-remainder, with a floor of one
     * cube each — a fence post's thin arms must still get a collider even though the post dwarfs
     * them in volume. Caller guarantees {@code boxes.size() <= budget}, so the floor always fits.
     */
    private static int[] allocate(List<AABB> boxes, int budget) {
        int k = boxes.size();
        int[] out = new int[k];
        Arrays.fill(out, 1);
        int remaining = budget - k;
        if (remaining <= 0) {
            return out;
        }
        double[] vol = new double[k];
        double total = 0.0;
        for (int i = 0; i < k; i++) {
            vol[i] = volumeOf(boxes.get(i));
            total += vol[i];
        }
        if (total <= 0.0) {
            return out;
        }
        double[] remainder = new double[k];
        int used = 0;
        for (int i = 0; i < k; i++) {
            double want = remaining * vol[i] / total;
            int whole = (int) Math.floor(want);
            remainder[i] = want - whole;
            out[i] += whole;
            used += whole;
        }
        Integer[] order = new Integer[k];
        for (int i = 0; i < k; i++) {
            order[i] = i;
        }
        Arrays.sort(order, Comparator.comparingDouble((Integer i) -> remainder[i]).reversed());
        for (int i = 0; i < remaining - used; i++) {
            out[order[i % k]]++;
        }
        return out;
    }

    private static double volumeOf(AABB b) {
        return Math.max(b.maxX - b.minX, MIN_CUBE_SIZE)
                * Math.max(b.maxY - b.minY, MIN_CUBE_SIZE)
                * Math.max(b.maxZ - b.minZ, MIN_CUBE_SIZE);
    }

    /** Tiles one box with at most {@code budget} cubes — see the class javadoc's "Tiling rule". */
    private static List<Cube> fitBox(AABB b, int budget, boolean fillBudget) {
        double sx = Math.max(b.maxX - b.minX, MIN_CUBE_SIZE);
        double sy = Math.max(b.maxY - b.minY, MIN_CUBE_SIZE);
        double sz = Math.max(b.maxZ - b.minZ, MIN_CUBE_SIZE);
        // fillBudget drops the floor to the smallest cube allowed, so the bisection below is forced to
        // search for the smallest side the budget affords instead of stopping at the shape's own
        // shortest extent — which for a cube is the whole cube, i.e. one cube, i.e. no subdivision.
        double lo = fillBudget ? MIN_CUBE_SIZE
                : Math.max(Math.min(sx, Math.min(sy, sz)), MIN_CUBE_SIZE);
        double hi = Math.max(sx, Math.max(sy, sz));
        double s;
        if (gridCount(sx, sy, sz, lo) <= budget) {
            s = lo;
        } else {
            // gridCount is monotonically non-increasing in s, and hi (side = the largest extent)
            // always yields a 1x1x1 grid, so the smallest budget-fitting side is bisectable. 40
            // iterations resolves it far below MIN_CUBE_SIZE for any block-scale input.
            for (int i = 0; i < 40; i++) {
                double mid = (lo + hi) / 2.0;
                if (gridCount(sx, sy, sz, mid) <= budget) {
                    hi = mid;
                } else {
                    lo = mid;
                }
            }
            s = hi;
        }
        int nx = axisCount(sx, s);
        int ny = axisCount(sy, s);
        int nz = axisCount(sz, s);
        double side = Math.max(MIN_CUBE_SIZE, Math.max(sx / nx, Math.max(sy / ny, sz / nz)));
        double[] xs = axisPositions(b.minX, sx, nx, side);
        double[] ys = axisPositions(b.minY, sy, ny, side);
        double[] zs = axisPositions(b.minZ, sz, nz, side);
        List<Cube> out = new ArrayList<>(nx * ny * nz);
        for (double x : xs) {
            for (double y : ys) {
                for (double z : zs) {
                    out.add(new Cube(x, y, z, side));
                }
            }
        }
        return out;
    }

    private static long gridCount(double sx, double sy, double sz, double s) {
        return (long) axisCount(sx, s) * axisCount(sy, s) * axisCount(sz, s);
    }

    private static int axisCount(double size, double s) {
        return Math.max(1, (int) Math.ceil(size / s - 1.0E-9));
    }

    /**
     * Cube MIN corners along one axis: the first flush with {@code min}, the last ending exactly at
     * {@code min + size}, evenly spaced (so they overlap when {@code n * side > size}). A single
     * cube is centred instead, which is the only case that can spill outside the box.
     */
    private static double[] axisPositions(double min, double size, int n, double side) {
        double[] out = new double[n];
        if (n == 1) {
            out[0] = min + (size - side) / 2.0;
            return out;
        }
        double step = (size - side) / (n - 1);
        for (int i = 0; i < n; i++) {
            out[i] = min + i * step;
        }
        return out;
    }
}
