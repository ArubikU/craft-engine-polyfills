package dev.arubik.craftengine.crafting;

/**
 * EXTENSION POINT (intentionally not implemented).
 *
 * <p>A craft-engine {@code net.momirealms.craftengine.core.item.recipe.Ingredient}
 * could back a {@link CraftingRecipeLike} so recipes can match by tag / custom
 * item / count instead of a single {@link net.momirealms.craftengine.core.util.Key}.
 * Verified via {@code javap}, {@code Ingredient} exposes:
 * <pre>
 *   boolean test(UniqueIdItem)        // membership test
 *   boolean acceptsItem(UniqueIdItem) // ignores count
 *   int count()                       // required count
 *   List&lt;UniqueKey&gt; items()          // resolved members
 * </pre>
 *
 * <p>It is NOT wired in here because matching an {@code Ingredient} requires a
 * {@code UniqueIdItem} per grid cell, and constructing one needs the live
 * craft-engine item registry (no pure/JVM constructor surfaced by {@code javap}).
 * Bridging would therefore pull Bukkit/NMS into the otherwise-pure matching core
 * and break the unit-test boundary.
 *
 * <p>To add it: implement {@link CraftingRecipeLike} in the Bukkit-facing layer
 * (next to {@link CraftItemAdapter}), resolve each {@link CraftCell} id to a
 * {@code UniqueIdItem} via the item registry, and delegate to
 * {@code Ingredient.test(...)}. Keep the pure matcher untouched; this adapter
 * lives outside the test boundary.
 */
public final class IngredientRecipeExtensionPoint {
    private IngredientRecipeExtensionPoint() {
    }
}
