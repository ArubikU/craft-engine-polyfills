package dev.arubik.craftengine.crafting;

import java.util.logging.Logger;

import net.momirealms.craftengine.core.util.Key;

/**
 * Sample {@link StationRecipe} registrations for the engineer's workbench,
 * demonstrating the full developer-facing API: a base shaped/shapeless pattern
 * over the 3x2 grid, a required TOOL (consuming durability per craft), up to 2
 * outputs, plus optional {@link RecipeCondition} and {@link RecipeExecutor}.
 *
 * <p><b>Defining a station recipe.</b>
 * <pre>{@code
 * StationRecipe r = StationRecipe.builder(Key.of("ns", "id"))
 *     .requiredTool(Key.of("minecraft", "iron_pickaxe")) // custom id or vanilla key
 *     .toolUsesPerCraft(5)                                // durability per craft
 *     .condition(ctx -> ctx.crafts() >= 2)               // optional gate (whole UI state)
 *     .executor(ctx -> ctx.player().giveExp(10))         // optional side-effect
 *     .shaped()                                           // OR .shapeless()
 *         .row("III").row(" S ")
 *         .define('I', Key.of("minecraft","iron_ingot"))
 *         .define('S', Key.of("minecraft","stick"))
 *         .output(Key.of("minecraft","iron_block"), 1)
 *         .output(Key.of("minecraft","iron_nugget"), 4) // up to 2 outputs
 *     // (builder is the SAME instance; call .build() on the StationRecipe builder)
 *     ;
 * StationRecipeRegistry.global().register(r);
 * }</pre>
 *
 * <p>Self-registers via {@link WorkbenchBehavior}'s static initializer (mirrors
 * {@code CraftingSamples} / {@code UpgradeableFurnace}). Idempotent.
 */
public final class WorkbenchSamples {

    private static final Logger LOG = Logger.getLogger("CraftEnginePolyfills");
    private static boolean registered;

    private WorkbenchSamples() {
    }

    public static synchronized void registerDefaults() {
        if (registered) {
            return;
        }
        registered = true;
        registerInto(StationRecipeRegistry.global());
    }

    public static void registerInto(StationRecipeRegistry registry) {
        Key ironIngot = Key.of("minecraft", "iron_ingot");
        Key ironBlock = Key.of("minecraft", "iron_block");
        Key ironNugget = Key.of("minecraft", "iron_nugget");
        Key stick = Key.of("minecraft", "stick");
        Key copperIngot = Key.of("minecraft", "copper_ingot");
        Key copperBlock = Key.of("minecraft", "copper_block");
        Key ironPick = Key.of("minecraft", "iron_pickaxe");
        Key ironAxe = Key.of("minecraft", "iron_axe");

        // 1) SHAPED, requires an iron pickaxe (5 durability/craft), 2 outputs.
        //    Pattern uses the 3x2 grid: a row of iron over a centered stick.
        StationRecipe.Builder b1 = StationRecipe.builder(Key.of("polyfills", "station_iron_assembly"))
                .requiredTool(ironPick)
                .toolUsesPerCraft(5);
        b1.shaped()
                .row("III")
                .row(" S ")
                .define('I', ironIngot)
                .define('S', stick)
                .output(ironBlock, 1)
                .output(ironNugget, 4);
        registry.register(b1.build());

        // 2) SHAPELESS, requires an iron axe (2 durability/craft), with a sample
        //    CONDITION (only crafts in bulk: crafts >= 2) and a sample EXECUTOR
        //    (award a little xp + log) to prove both hooks fire end to end.
        StationRecipe.Builder b2 = StationRecipe.builder(Key.of("polyfills", "station_copper_press"))
                .requiredTool(ironAxe)
                .toolUsesPerCraft(2)
                .condition(ctx -> ctx.crafts() >= 2)
                .executor(ctx -> {
                    if (ctx.player() != null) {
                        ctx.player().giveExp(2 * ctx.crafts());
                    }
                    LOG.info("[workbench] copper_press executed x" + ctx.crafts()
                            + " for " + (ctx.player() != null ? ctx.player().getName() : "?"));
                });
        b2.shapeless()
                .ingredient(copperIngot, 9)
                .output(copperBlock, 1);
        registry.register(b2.build());
    }
}
