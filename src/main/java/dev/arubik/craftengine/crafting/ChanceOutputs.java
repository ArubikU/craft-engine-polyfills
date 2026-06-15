package dev.arubik.craftengine.crafting;

/**
 * Optional mix-in for a recipe whose outputs may be probabilistic (a guaranteed
 * primary + one or more chance-based secondary byproducts). The output index
 * aligns with {@link CraftingRecipeLike#outputs}. A chance of {@code >= 100} (the
 * default) means the output is always produced.
 */
public interface ChanceOutputs {

    /** Drop chance (0..100) for the output at {@code index}; 100 = guaranteed. */
    int outputChance(int index);
}
