package dev.arubik.craftengine.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.momirealms.craftengine.core.util.Key;

/**
 * A dedicated registry bucket for {@link StationRecipe}s, sized for the
 * workbench's 3-wide x 2-tall input grid so station recipes never collide with
 * the 3x3 table recipes in {@link CraftingRecipeRegistry}. Returns the concrete
 * {@link StationRecipe} type (not the erased SPI) so the menu can read the tool /
 * condition / executor without casting.
 *
 * <p>Pure / JVM-testable: matching delegates to {@link StationRecipe#matches}.
 */
public final class StationRecipeRegistry {

    private static final StationRecipeRegistry GLOBAL = new StationRecipeRegistry();

    public static StationRecipeRegistry global() {
        return GLOBAL;
    }

    private final List<StationRecipe> recipes = new ArrayList<>();
    private final Map<Key, StationRecipe> byId = new HashMap<>();

    public StationRecipeRegistry register(StationRecipe recipe) {
        recipes.add(recipe);
        byId.put(recipe.id(), recipe);
        return this;
    }

    public Optional<StationRecipe> byId(Key id) {
        return Optional.ofNullable(byId.get(id));
    }

    /** First registered station recipe that matches {@code grid}, if any. */
    public Optional<StationRecipe> match(CraftingGrid grid) {
        for (StationRecipe r : recipes) {
            if (r.matches(grid)) {
                return Optional.of(r);
            }
        }
        return Optional.empty();
    }

    /** Remove all registered station recipes (used before a reload). */
    public void clear() {
        recipes.clear();
        byId.clear();
    }

    /** All station recipes whose required tool is {@code toolId} (a blueprint may hold several). */
    public java.util.List<StationRecipe> allByTool(Key toolId) {
        java.util.List<StationRecipe> out = new java.util.ArrayList<>();
        if (toolId != null) {
            for (StationRecipe r : recipes) {
                if (toolId.equals(r.requiredTool())) {
                    out.add(r);
                }
            }
        }
        return out;
    }

    /** First station recipe whose required tool is {@code toolId}, if any. */
    /** Recipes belonging to a data-defined station. */
    public java.util.List<StationRecipe> byWorkbench(Key workbenchId) {
        java.util.List<StationRecipe> out = new java.util.ArrayList<>();
        for (StationRecipe r : recipes) {
            if (workbenchId != null && workbenchId.equals(r.workbench()))
                out.add(r);
        }
        return out;
    }

    public Optional<StationRecipe> byTool(Key toolId) {
        if (toolId != null) {
            for (StationRecipe r : recipes) {
                if (toolId.equals(r.requiredTool())) {
                    return Optional.of(r);
                }
            }
        }
        return Optional.empty();
    }

    /** True if {@code itemId} is the required tool of ANY registered station recipe. */
    public boolean isRequiredTool(Key itemId) {
        if (itemId == null) {
            return false;
        }
        for (StationRecipe r : recipes) {
            if (itemId.equals(r.requiredTool())) {
                return true;
            }
        }
        return false;
    }

    public void unregister(Key id) {
        StationRecipe removed = byId.remove(id);
        if (removed != null) {
            recipes.removeIf(r -> r.id().equals(id));
        }
    }
}
