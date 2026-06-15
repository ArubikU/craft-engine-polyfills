package dev.arubik.craftengine.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.momirealms.craftengine.core.util.Key;

/**
 * A pure, unit-testable crafting recipe supporting SHAPED and SHAPELESS
 * matching against a {@link CraftingGrid}, with one or more outputs.
 *
 * <p>The matcher operates over the grid's abstract {@code (cells, dims)} view:
 * <ul>
 *   <li><b>SHAPELESS</b> reduces the grid (any dimensionality) to a multiset of
 *       non-empty cells and checks the recipe's ingredient multiset can be
 *       satisfied. This is automatically 3D-ready.</li>
 *   <li><b>SHAPED</b> trims the empty border of the (flat) grid and compares the
 *       resulting pattern cell-by-cell against the recipe pattern, optionally
 *       also trying the horizontal mirror.</li>
 * </ul>
 *
 * <p>Outputs are stored as {@link CraftCell}s (id + count) so this class stays
 * free of Bukkit/NMS; the menu layer converts them to real item stacks.
 */
public final class CraftingRecipe implements CraftingRecipeLike {

    public enum Type {
        SHAPED,
        SHAPELESS
    }

    private final Key id;
    private final Type type;

    // For SHAPED: a trimmed pattern (its own width/height) of ingredients.
    private final int patternWidth;
    private final int patternHeight;
    private final List<CraftIngredient> pattern; // row-major, size patternWidth*patternHeight
    private final boolean allowMirror;

    // For SHAPELESS: a flat multiset of required ingredients.
    private final List<CraftIngredient> ingredients;

    private final List<CraftCell> outputs;

    private CraftingRecipe(Key id, Type type, int patternWidth, int patternHeight, List<CraftIngredient> pattern,
            boolean allowMirror, List<CraftIngredient> ingredients, List<CraftCell> outputs) {
        this.id = id;
        this.type = type;
        this.patternWidth = patternWidth;
        this.patternHeight = patternHeight;
        this.pattern = pattern;
        this.allowMirror = allowMirror;
        this.ingredients = ingredients;
        this.outputs = outputs;
    }

    @Override
    public Key id() {
        return id;
    }

    /** {@inheritDoc} The built-in recipe ignores the grid; outputs are fixed. */
    @Override
    public List<CraftCell> outputs(CraftingGrid grid) {
        return outputs;
    }

    public Type type() {
        return type;
    }

    /** Unmodifiable list of outputs (id + count). */
    public List<CraftCell> outputs() {
        return outputs;
    }

    public int patternWidth() {
        return patternWidth;
    }

    public int patternHeight() {
        return patternHeight;
    }

    /** SHAPED ingredient at trimmed-pattern cell (x,y), or EMPTY (also for shapeless/out of range). */
    public CraftIngredient ingredientAt(int x, int y) {
        if (type != Type.SHAPED || x < 0 || y < 0 || x >= patternWidth || y >= patternHeight) {
            return CraftIngredient.EMPTY;
        }
        return pattern.get(y * patternWidth + x);
    }

    /**
     * True if {@code grid} produces this recipe. Pure; safe to call from tests.
     */
    @Override
    public boolean matches(CraftingGrid grid) {
        return switch (type) {
            case SHAPELESS -> matchesShapeless(grid);
            case SHAPED -> matchesShaped(grid);
        };
    }

    private boolean matchesShapeless(CraftingGrid grid) {
        // Collect non-empty cells.
        List<CraftCell> remaining = new ArrayList<>();
        for (CraftCell c : grid.cells()) {
            if (!c.isEmpty()) {
                remaining.add(c);
            }
        }
        if (remaining.size() != ingredients.size()) {
            return false;
        }
        // Greedy multiset match: each ingredient consumes one distinct cell.
        boolean[] used = new boolean[remaining.size()];
        for (CraftIngredient ing : ingredients) {
            int found = -1;
            for (int i = 0; i < remaining.size(); i++) {
                if (!used[i] && ing.matches(remaining.get(i))) {
                    found = i;
                    break;
                }
            }
            if (found < 0) {
                return false;
            }
            used[found] = true;
        }
        return true;
    }

    private boolean matchesShaped(CraftingGrid grid) {
        if (!grid.isFlat()) {
            return false; // shaped only defined for 2D layouts currently
        }
        CraftingGrid trimmed = grid.trimmed();
        if (patternMatchesTrimmed(trimmed)) {
            return true;
        }
        if (allowMirror) {
            return patternMatchesTrimmed(trimmed.mirroredHorizontally());
        }
        return false;
    }

    private boolean patternMatchesTrimmed(CraftingGrid trimmed) {
        if (trimmed.width() != patternWidth || trimmed.height() != patternHeight) {
            return false;
        }
        for (int y = 0; y < patternHeight; y++) {
            for (int x = 0; x < patternWidth; x++) {
                CraftIngredient ing = pattern.get(y * patternWidth + x);
                if (!ing.matches(trimmed.cell(x, y))) {
                    return false;
                }
            }
        }
        return true;
    }

    // ---------------- Builders ----------------

    /**
     * Shaped recipe builder. Rows are given as parallel strings of single-char
     * keys plus a key->ingredient map, ala vanilla. The pattern is trimmed of
     * empty borders on build so it matches regardless of placement in the grid.
     */
    public static ShapedBuilder shaped(Key id) {
        return new ShapedBuilder(id);
    }

    public static ShapelessBuilder shapeless(Key id) {
        return new ShapelessBuilder(id);
    }

    public static final class ShapedBuilder {
        private final Key id;
        private final List<String> rows = new ArrayList<>();
        private final java.util.Map<Character, CraftIngredient> keyMap = new java.util.HashMap<>();
        private final List<CraftCell> outputs = new ArrayList<>();
        private boolean allowMirror = false;

        private ShapedBuilder(Key id) {
            this.id = id;
        }

        /** Adds a pattern row. ' ' (space) means an empty cell. Rows may differ in length. */
        public ShapedBuilder row(String row) {
            rows.add(row);
            return this;
        }

        public ShapedBuilder define(char c, Key ingredient, int count) {
            keyMap.put(c, CraftIngredient.of(ingredient, count));
            return this;
        }

        public ShapedBuilder define(char c, Key ingredient) {
            return define(c, ingredient, 1);
        }

        public ShapedBuilder allowMirror(boolean v) {
            this.allowMirror = v;
            return this;
        }

        public ShapedBuilder output(Key item, int count) {
            outputs.add(CraftCell.of(item, count));
            return this;
        }

        public CraftingRecipe build() {
            int rawWidth = 0;
            for (String r : rows) {
                rawWidth = Math.max(rawWidth, r.length());
            }
            int rawHeight = rows.size();
            // Build a raw ingredient grid, then trim empty borders.
            List<CraftIngredient> raw = new ArrayList<>(rawWidth * rawHeight);
            for (int y = 0; y < rawHeight; y++) {
                String r = rows.get(y);
                for (int x = 0; x < rawWidth; x++) {
                    char c = x < r.length() ? r.charAt(x) : ' ';
                    if (c == ' ') {
                        raw.add(CraftIngredient.EMPTY);
                    } else {
                        CraftIngredient ing = keyMap.get(c);
                        if (ing == null) {
                            throw new IllegalArgumentException("Undefined pattern key '" + c + "'");
                        }
                        raw.add(ing);
                    }
                }
            }
            // Trim empty rows/columns.
            int minX = rawWidth;
            int minY = rawHeight;
            int maxX = -1;
            int maxY = -1;
            for (int y = 0; y < rawHeight; y++) {
                for (int x = 0; x < rawWidth; x++) {
                    if (!raw.get(y * rawWidth + x).isEmpty()) {
                        minX = Math.min(minX, x);
                        maxX = Math.max(maxX, x);
                        minY = Math.min(minY, y);
                        maxY = Math.max(maxY, y);
                    }
                }
            }
            if (maxX < 0) {
                throw new IllegalArgumentException("Shaped recipe pattern is empty");
            }
            int w = maxX - minX + 1;
            int h = maxY - minY + 1;
            List<CraftIngredient> pattern = new ArrayList<>(w * h);
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    pattern.add(raw.get(y * rawWidth + x));
                }
            }
            if (outputs.isEmpty()) {
                throw new IllegalArgumentException("Recipe must have at least one output");
            }
            return new CraftingRecipe(id, Type.SHAPED, w, h, Collections.unmodifiableList(pattern), allowMirror,
                    Collections.emptyList(), Collections.unmodifiableList(new ArrayList<>(outputs)));
        }
    }

    public static final class ShapelessBuilder {
        private final Key id;
        private final List<CraftIngredient> ingredients = new ArrayList<>();
        private final List<CraftCell> outputs = new ArrayList<>();

        private ShapelessBuilder(Key id) {
            this.id = id;
        }

        public ShapelessBuilder ingredient(Key item, int count) {
            ingredients.add(CraftIngredient.of(item, count));
            return this;
        }

        public ShapelessBuilder ingredient(Key item) {
            return ingredient(item, 1);
        }

        public ShapelessBuilder output(Key item, int count) {
            outputs.add(CraftCell.of(item, count));
            return this;
        }

        public CraftingRecipe build() {
            if (ingredients.isEmpty()) {
                throw new IllegalArgumentException("Shapeless recipe must have at least one ingredient");
            }
            if (outputs.isEmpty()) {
                throw new IllegalArgumentException("Recipe must have at least one output");
            }
            return new CraftingRecipe(id, Type.SHAPELESS, 0, 0, Collections.emptyList(), false,
                    Collections.unmodifiableList(new ArrayList<>(ingredients)),
                    Collections.unmodifiableList(new ArrayList<>(outputs)));
        }
    }
}
