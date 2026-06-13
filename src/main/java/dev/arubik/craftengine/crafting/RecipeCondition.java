package dev.arubik.craftengine.crafting;

/**
 * OPTIONAL extension a {@link CraftingRecipeLike} MAY implement to gate crafting
 * on arbitrary UI state. The base {@link CraftingRecipeLike} SPI stays unchanged;
 * the menu checks {@code instanceof RecipeCondition} so recipe types that do not
 * implement it are unaffected.
 *
 * <p>Evaluated in {@link WorkbenchMenu#isCustomCraftAllowed} alongside the tool
 * check. Returning {@code false} blocks the craft (and, because it is part of the
 * gate, also suppresses the output preview path that depends on it).
 */
@FunctionalInterface
public interface RecipeCondition {

    /** True if a craft may proceed given the whole {@link CraftContext}. Pure-ish. */
    boolean test(CraftContext ctx);
}
