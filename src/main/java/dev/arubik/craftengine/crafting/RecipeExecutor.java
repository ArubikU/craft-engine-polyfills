package dev.arubik.craftengine.crafting;

/**
 * OPTIONAL extension a {@link CraftingRecipeLike} MAY implement to run custom
 * side-effects after a successful craft (award xp, spawn particles, attach NBT,
 * etc.). The base {@link CraftingRecipeLike} SPI is untouched; the menu invokes
 * this only when the recipe {@code instanceof RecipeExecutor}.
 *
 * <p>Invoked in {@link WorkbenchMenu#onCraft} AFTER the normal outputs are given
 * and tool durability is consumed, so the executor sees the post-craft world.
 */
@FunctionalInterface
public interface RecipeExecutor {

    /** Run side-effects for a completed craft. {@code ctx.crafts()} = how many. */
    void run(CraftContext ctx);
}
