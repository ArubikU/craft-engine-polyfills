package dev.arubik.craftengine.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.momirealms.craftengine.core.util.Key;

/**
 * Registers and looks up {@link CraftingRecipe}s, bucketed by the grid
 * dimensions they are intended for, so a 3x3 table only ever scans 3x3 recipes.
 *
 * <p>Pure/JVM-testable: matching delegates to {@link CraftingRecipe#matches}.
 * A single global instance is exposed via {@link #global()}, but additional
 * isolated registries may be created (handy for tests and for differently sized
 * tables sharing nothing).
 */
public final class CraftingRecipeRegistry {

    private static final CraftingRecipeRegistry GLOBAL = new CraftingRecipeRegistry();

    public static CraftingRecipeRegistry global() {
        return GLOBAL;
    }

    private record Dims(int width, int height, int depth) {
    }

    private final Map<Dims, List<CraftingRecipe>> byDims = new HashMap<>();
    private final Map<Key, CraftingRecipe> byId = new HashMap<>();

    /**
     * Registers {@code recipe} for the table size {@code width x height}
     * (depth 1). Recipes are matched against grids of exactly this size.
     */
    public CraftingRecipeRegistry register(int width, int height, CraftingRecipe recipe) {
        return register(width, height, 1, recipe);
    }

    public CraftingRecipeRegistry register(int width, int height, int depth, CraftingRecipe recipe) {
        Dims dims = new Dims(width, height, depth);
        byDims.computeIfAbsent(dims, k -> new ArrayList<>()).add(recipe);
        byId.put(recipe.id(), recipe);
        return this;
    }

    public Optional<CraftingRecipe> byId(Key id) {
        return Optional.ofNullable(byId.get(id));
    }

    /** First recipe (registered for the grid's dimensions) that matches, if any. */
    public Optional<CraftingRecipe> match(CraftingGrid grid) {
        Dims dims = new Dims(grid.width(), grid.height(), grid.depth());
        List<CraftingRecipe> candidates = byDims.get(dims);
        if (candidates == null) {
            return Optional.empty();
        }
        for (CraftingRecipe recipe : candidates) {
            if (recipe.matches(grid)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    /** Removes a recipe by id from all buckets. */
    public void unregister(Key id) {
        CraftingRecipe removed = byId.remove(id);
        if (removed == null) {
            return;
        }
        for (List<CraftingRecipe> list : byDims.values()) {
            list.removeIf(r -> r.id().equals(id));
        }
    }
}
