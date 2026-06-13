package dev.arubik.craftengine.crafting;

import net.momirealms.craftengine.core.util.Key;

/**
 * Sample recipe registrations for a 3x3 crafting table, demonstrating the
 * recipe-definition API. Call {@link #registerDefaults()} once during plugin
 * enable (after items are loaded) to populate {@link CraftingRecipeRegistry}.
 *
 * <p><b>Recipe-definition API.</b>
 * <ul>
 *   <li><b>Shaped:</b> {@code CraftingRecipe.shaped(id).row("...").define('X', key).output(key, n).build()} —
 *       rows are strings of single-char keys (' ' = empty); the pattern is
 *       trimmed of empty borders on build so placement anywhere in the grid
 *       matches. {@code allowMirror(true)} also accepts the horizontal mirror.</li>
 *   <li><b>Shapeless:</b> {@code CraftingRecipe.shapeless(id).ingredient(key[, n])...output(key, n).build()} —
 *       order-independent multiset match.</li>
 *   <li><b>Multiple outputs:</b> chain {@code .output(...)} more than once.</li>
 *   <li>Register with {@code registry.register(width, height, recipe)}; lookup
 *       via {@code registry.match(grid)}.</li>
 * </ul>
 */
public final class CraftingSamples {

    private CraftingSamples() {
    }

    public static void registerDefaults() {
        registerInto(CraftingRecipeRegistry.global());
    }

    public static void registerInto(CraftingRecipeRegistry registry) {
        // Vanilla item ids are minecraft:<material lowercase>; custom items use their craft-engine key.
        Key stick = Key.of("minecraft", "stick");
        Key plank = Key.of("minecraft", "oak_planks");
        Key diamond = Key.of("minecraft", "diamond");
        Key coal = Key.of("minecraft", "coal");
        Key torch = Key.of("minecraft", "torch");
        Key charcoal = Key.of("minecraft", "charcoal");
        Key pickaxe = Key.of("minecraft", "diamond_pickaxe");

        // 1) SHAPED, single output: diamond pickaxe (vanilla layout) on a 3x3 table.
        registry.register(3, 3, CraftingRecipe.shaped(Key.of("polyfills", "sample_pickaxe"))
                .row("DDD")
                .row(" S ")
                .row(" S ")
                .define('D', diamond)
                .define('S', stick)
                .output(pickaxe, 1)
                .build());

        // 2) SHAPELESS, single output: 1 coal + 1 stick -> 4 torches (any arrangement).
        registry.register(3, 3, CraftingRecipe.shapeless(Key.of("polyfills", "sample_torch"))
                .ingredient(coal)
                .ingredient(stick)
                .output(torch, 4)
                .build());

        // 3) MULTIPLE outputs: a 2x2 block of planks -> 1 stick AND 1 charcoal (demo of >1 output).
        registry.register(3, 3, CraftingRecipe.shaped(Key.of("polyfills", "sample_multi_output"))
                .row("PP")
                .row("PP")
                .define('P', plank)
                .output(stick, 1)
                .output(charcoal, 1)
                .build());

        // 4) FORGE (2x2 grid + fuel slot) sample: 4 coal in a 2x2 -> 1 diamond.
        //    Registered in the 2x2 bucket; ForgeMenu additionally gates on fuel.
        registry.register(2, 2, CraftingRecipe.shaped(Key.of("polyfills", "sample_forge_diamond"))
                .row("CC")
                .row("CC")
                .define('C', coal)
                .output(diamond, 1)
                .build());
    }

    /*
     * --- How a 5x5 table would be defined ---
     *
     * Register the block behavior with grid-width: 5 / grid-height: 5 in config
     * (the Factory reads those), then register 5x5 recipes:
     *
     *   CraftingRecipeRegistry.global().register(5, 5,
     *       CraftingRecipe.shaped(Key.of("polyfills", "big_star"))
     *           .row("  X  ")
     *           .row(" XXX ")
     *           .row("XXXXX")
     *           .row(" XXX ")
     *           .row("  X  ")
     *           .define('X', Key.of("minecraft", "iron_ingot"))
     *           .output(Key.of("minecraft", "iron_block"), 1)
     *           .build());
     *
     * Matching is bucketed by dimensions, so 3x3 and 5x5 recipes never collide.
     * The same CraftingGrid/CraftingRecipe matcher handles both because it works
     * over an abstract (cells, width, height[, depth]) view rather than a fixed
     * 3x3 array. A future 3D table would call register(w, h, k, recipe) and feed
     * a depth>1 grid; SHAPELESS matching already supports it.
     */
}
