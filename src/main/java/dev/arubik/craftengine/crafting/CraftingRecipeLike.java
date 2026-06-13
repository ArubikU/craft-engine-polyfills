package dev.arubik.craftengine.crafting;

import java.util.List;

import net.momirealms.craftengine.core.util.Key;

/**
 * The recipe SPI for the reusable crafting framework. Any recipe type that can
 * be matched against a {@link CraftingGrid} and produce a list of output cells
 * can plug in, so different recipe sources (the built-in shaped/shapeless
 * {@link CraftingRecipe}, a future craft-engine {@code Ingredient}-backed recipe,
 * a scripted recipe, etc.) all share the same matching/UI plumbing.
 *
 * <p>Implementations MUST be pure (no Bukkit/NMS) so the matching layer stays
 * unit-testable on the plain JVM. Bukkit conversion happens only in the menu
 * layer via {@link CraftItemAdapter}.
 */
public interface CraftingRecipeLike {

    /** A stable identity for this recipe (used for de-dup / unregister). */
    Key id();

    /** True if {@code grid} satisfies this recipe. Pure. */
    boolean matches(CraftingGrid grid);

    /**
     * The outputs produced by ONE craft (primary first, then secondary). Never
     * empty for a matchable recipe. Pure.
     */
    List<CraftCell> outputs(CraftingGrid grid);

    /**
     * Optional remainder/return cells left in the input area after a craft
     * (vanilla "container item" semantics, e.g. an empty bucket replacing a
     * water bucket). The default is "no remainders". Indices correspond to the
     * occupied input cells of {@code grid} in row-major order; a null/EMPTY entry
     * means that input is simply consumed.
     *
     * <p>This is an OPTIONAL extension point: the default impl returns an empty
     * list (no remainders), which is the common case.
     */
    default List<CraftCell> remainders(CraftingGrid grid) {
        return List.of();
    }
}
