package dev.arubik.craftengine.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.momirealms.craftengine.core.util.Key;

/**
 * An immutable N x M (x K) grid of {@link CraftCell}s used as the input to
 * recipe matching.
 *
 * <p>The grid is stored as a flat cell list plus explicit {@code width},
 * {@code height} and {@code depth} dimensions. The matcher operates over this
 * abstract {@code (cells, dims)} representation rather than a fixed 2D array, so
 * a future 3D crafting volume (depth &gt; 1) can be matched by the same code
 * without rewriting it. The current shaped/shapeless matchers in
 * {@link CraftingRecipe} reduce a 3D grid layer-agnostically for shapeless and
 * support 2D layouts (depth == 1) for shaped.
 *
 * <p>Index layout is row-major within a layer, layers stacked last:
 * {@code index = (z * height + y) * width + x}.
 *
 * <p>Pure/JVM-testable: no Bukkit or NMS types appear here.
 */
public final class CraftingGrid {

    private final int width;
    private final int height;
    private final int depth;
    private final List<CraftCell> cells;

    private CraftingGrid(int width, int height, int depth, List<CraftCell> cells) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.cells = cells;
    }

    /** Builds a 2D grid (depth = 1). {@code cells} must have width*height entries. */
    public static CraftingGrid of(int width, int height, List<CraftCell> cells) {
        return of(width, height, 1, cells);
    }

    /** Builds an N x M x K grid. {@code cells} must have width*height*depth entries. */
    public static CraftingGrid of(int width, int height, int depth, List<CraftCell> cells) {
        if (width <= 0 || height <= 0 || depth <= 0) {
            throw new IllegalArgumentException("dimensions must be positive");
        }
        int expected = width * height * depth;
        if (cells.size() != expected) {
            throw new IllegalArgumentException(
                    "cell count " + cells.size() + " != " + expected + " for " + width + "x" + height + "x" + depth);
        }
        List<CraftCell> copy = new ArrayList<>(cells.size());
        for (CraftCell c : cells) {
            copy.add(c == null ? CraftCell.EMPTY : c);
        }
        return new CraftingGrid(width, height, depth, Collections.unmodifiableList(copy));
    }

    /** Builds an empty grid of the given dimensions. */
    public static CraftingGrid empty(int width, int height) {
        return empty(width, height, 1);
    }

    public static CraftingGrid empty(int width, int height, int depth) {
        List<CraftCell> cells = new ArrayList<>(width * height * depth);
        for (int i = 0; i < width * height * depth; i++) {
            cells.add(CraftCell.EMPTY);
        }
        return new CraftingGrid(width, height, depth, Collections.unmodifiableList(cells));
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int depth() {
        return depth;
    }

    public List<CraftCell> cells() {
        return cells;
    }

    public CraftCell cell(int x, int y) {
        return cell(x, y, 0);
    }

    public CraftCell cell(int x, int y, int z) {
        return cells.get((z * height + y) * width + x);
    }

    public boolean isFlat() {
        return depth == 1;
    }

    public boolean isEmpty() {
        for (CraftCell c : cells) {
            if (!c.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /** Count of non-empty cells. */
    public int occupied() {
        int n = 0;
        for (CraftCell c : cells) {
            if (!c.isEmpty()) {
                n++;
            }
        }
        return n;
    }

    /**
     * Returns a 2D sub-grid with empty border rows/columns removed (the
     * "pattern" view used for shaped matching). Only valid for flat (depth == 1)
     * grids. An all-empty grid trims to a 0-area result represented as a 1x1
     * empty grid.
     */
    public CraftingGrid trimmed() {
        if (!isFlat()) {
            throw new IllegalStateException("trimmed() only supported for flat (2D) grids");
        }
        int minX = width;
        int minY = height;
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!cell(x, y).isEmpty()) {
                    minX = Math.min(minX, x);
                    maxX = Math.max(maxX, x);
                    minY = Math.min(minY, y);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        if (maxX < 0) {
            return empty(1, 1);
        }
        int newW = maxX - minX + 1;
        int newH = maxY - minY + 1;
        List<CraftCell> out = new ArrayList<>(newW * newH);
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                out.add(cell(x, y));
            }
        }
        return new CraftingGrid(newW, newH, 1, Collections.unmodifiableList(out));
    }

    /** Horizontally mirrors a flat grid (used for optional shaped mirroring). */
    public CraftingGrid mirroredHorizontally() {
        if (!isFlat()) {
            throw new IllegalStateException("mirroredHorizontally() only supported for flat (2D) grids");
        }
        List<CraftCell> out = new ArrayList<>(cells.size());
        for (int y = 0; y < height; y++) {
            for (int x = width - 1; x >= 0; x--) {
                out.add(cell(x, y));
            }
        }
        return new CraftingGrid(width, height, 1, Collections.unmodifiableList(out));
    }

    // --- Builder convenience for tests and sample recipes ---

    /** Mutable builder keyed by chars, ala vanilla shaped recipe definitions. */
    public static Builder builder(int width, int height) {
        return new Builder(width, height);
    }

    public static final class Builder {
        private final int width;
        private final int height;
        private final CraftCell[] cells;

        private Builder(int width, int height) {
            this.width = width;
            this.height = height;
            this.cells = new CraftCell[width * height];
            for (int i = 0; i < cells.length; i++) {
                cells[i] = CraftCell.EMPTY;
            }
        }

        public Builder set(int x, int y, Key id, int count) {
            cells[y * width + x] = CraftCell.of(id, count);
            return this;
        }

        public Builder set(int x, int y, Key id) {
            return set(x, y, id, 1);
        }

        public CraftingGrid build() {
            return CraftingGrid.of(width, height, List.of(cells));
        }
    }
}
