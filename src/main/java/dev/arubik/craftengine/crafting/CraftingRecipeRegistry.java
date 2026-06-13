package dev.arubik.craftengine.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.momirealms.craftengine.core.util.Key;

/**
 * Registers and looks up {@link CraftingRecipeLike} recipes, bucketed by the
 * grid dimensions they are intended for, so a 3x3 table only ever scans 3x3
 * recipes.
 *
 * <p>Pure/JVM-testable: matching delegates to {@link CraftingRecipeLike#matches}.
 * A single global instance is exposed via {@link #global()}, but additional
 * isolated registries may be created (handy for tests and for differently sized
 * tables sharing nothing). The registry stores the SPI type, so any plug-in
 * recipe implementation can be registered alongside the built-in
 * {@link CraftingRecipe}.
 */
public final class CraftingRecipeRegistry {

    private static final CraftingRecipeRegistry GLOBAL = new CraftingRecipeRegistry();

    public static CraftingRecipeRegistry global() {
        return GLOBAL;
    }

    private record Dims(int width, int height, int depth) {
    }

    private final Map<Dims, List<CraftingRecipeLike>> byDims = new HashMap<>();
    private final Map<Key, CraftingRecipeLike> byId = new HashMap<>();

    /**
     * Registers {@code recipe} for the table size {@code width x height}
     * (depth 1). Recipes are matched against grids of exactly this size.
     */
    public CraftingRecipeRegistry register(int width, int height, CraftingRecipeLike recipe) {
        return register(width, height, 1, recipe);
    }

    public CraftingRecipeRegistry register(int width, int height, int depth, CraftingRecipeLike recipe) {
        Dims dims = new Dims(width, height, depth);
        byDims.computeIfAbsent(dims, k -> new ArrayList<>()).add(recipe);
        byId.put(recipe.id(), recipe);
        return this;
    }

    public Optional<CraftingRecipeLike> byId(Key id) {
        return Optional.ofNullable(byId.get(id));
    }

    /** First recipe (registered for the grid's dimensions) that matches, if any. */
    public Optional<CraftingRecipeLike> match(CraftingGrid grid) {
        Dims dims = new Dims(grid.width(), grid.height(), grid.depth());
        List<CraftingRecipeLike> candidates = byDims.get(dims);
        if (candidates == null) {
            return Optional.empty();
        }
        for (CraftingRecipeLike recipe : candidates) {
            if (recipe.matches(grid)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    /** Removes a recipe by id from all buckets. */
    public void unregister(Key id) {
        CraftingRecipeLike removed = byId.remove(id);
        if (removed == null) {
            return;
        }
        for (List<CraftingRecipeLike> list : byDims.values()) {
            list.removeIf(r -> r.id().equals(id));
        }
    }
}
