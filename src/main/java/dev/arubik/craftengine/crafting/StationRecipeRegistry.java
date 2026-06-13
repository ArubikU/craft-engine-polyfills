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

    public void unregister(Key id) {
        StationRecipe removed = byId.remove(id);
        if (removed != null) {
            recipes.removeIf(r -> r.id().equals(id));
        }
    }
}
