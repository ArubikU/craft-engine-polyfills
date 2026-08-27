package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.ItemInput;
import dev.arubik.craftengine.machine.recipe.ItemOutput;
import dev.arubik.craftengine.machine.recipe.RecipeInput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;

import java.util.ArrayList;
import java.util.List;

/**
 * "Recipe" script type — wraps AbstractProcessingRecipe (this addon's own machine-processing
 * recipes) for script inspection, e.g. via {@code Machine.recipes}.
 *
 * <p>Also doubles as the wrapper for a VANILLA recipe adapted through {@link VanillaRecipeRef} —
 * see {@code Registry.recipes.stonecutter}/{@code .furnace}/etc (RecipeRegistryType/
 * RecipeCollectionType) — so a script that only cares about {@code .inputs}/{@code .outputs} can
 * treat either kind of recipe identically. The addon-specific fields with no vanilla equivalent
 * (su_cost, min_rpm, fuel_required, energy_cost) read as 0/false for a vanilla-backed instance
 * rather than throwing — a vanilla furnace/stonecutter recipe simply doesn't have those concepts.
 */
public final class RecipeType {

    private RecipeType() {}

    public static void register() {
        PolyTypeRegistry.define("Recipe")
            .property("processing_time", obj -> obj instanceof VanillaRecipeRef v
                    ? ScriptValue.of(v.processingTimeTicks())
                    : ScriptValue.of(recipe(obj).getProcessTime()))
            .property("su_cost",         obj -> obj instanceof VanillaRecipeRef ? ScriptValue.of(0) : ScriptValue.of(recipe(obj).getSuCost()))
            .property("min_rpm",         obj -> obj instanceof VanillaRecipeRef ? ScriptValue.of(0) : ScriptValue.of(recipe(obj).getMinRpm()))
            .property("fuel_required",   obj -> obj instanceof VanillaRecipeRef ? ScriptValue.of(false) : ScriptValue.of(recipe(obj).isFuelRequired()))
            // CraftEnergy (Forge-Energy-alike) drawn from the machine's own buffer every tick this
            // recipe processes — 0 for a recipe with no energy requirement at all, same optionality
            // as fuel_required.
            .property("energy_cost",     obj -> obj instanceof VanillaRecipeRef ? ScriptValue.of(0) : ScriptValue.of(recipe(obj).getEnergyCost()))
            // Vanilla recipe id ("minecraft:stone_bricks_from_stonecutting") — NULL for this
            // addon's own AbstractProcessingRecipe, which has no equivalent registry id concept.
            .property("id", obj -> obj instanceof VanillaRecipeRef v ? ScriptValue.of(v.id()) : ScriptValue.NULL)
            .property("inputs", obj -> {
                if (obj instanceof VanillaRecipeRef v) {
                    return new ScriptValue.Array(v.input().isEmpty()
                            ? List.of() : List.of(ScriptValue.ofItem(v.input())));
                }
                List<ScriptValue> result = new ArrayList<>();
                for (RecipeInput in : recipe(obj).getInputs()) {
                    if (in instanceof ItemInput ii) {
                        net.minecraft.world.item.ItemStack s = ii.getStack();
                        if (s != null && !s.isEmpty()) result.add(ScriptValue.ofItem(s));
                    }
                }
                return new ScriptValue.Array(result);
            })
            .property("outputs", obj -> {
                if (obj instanceof VanillaRecipeRef v) {
                    return new ScriptValue.Array(v.output().isEmpty()
                            ? List.of() : List.of(ScriptValue.ofItem(v.output())));
                }
                List<ScriptValue> result = new ArrayList<>();
                for (RecipeOutput out : recipe(obj).getOutputs()) {
                    if (out instanceof ItemOutput io) {
                        net.minecraft.world.item.ItemStack s = io.getItem();
                        if (s != null && !s.isEmpty()) result.add(ScriptValue.ofItem(s));
                    }
                }
                return new ScriptValue.Array(result);
            })
            // Parallel array to `outputs` (same order, same length) — each item output's drop
            // chance as a 0..1 fraction (1.0 = always drops). Not on ItemType itself since chance
            // is a property of the RECIPE's output slot, not of the ItemStack it produces. Always
            // [1.0] for a vanilla-backed recipe (stonecutting/cooking always fully produce their
            // single result).
            .property("output_chances", obj -> {
                if (obj instanceof VanillaRecipeRef v) {
                    return new ScriptValue.Array(v.output().isEmpty() ? List.of() : List.of(ScriptValue.of(1.0)));
                }
                List<ScriptValue> result = new ArrayList<>();
                for (RecipeOutput out : recipe(obj).getOutputs()) {
                    if (out instanceof ItemOutput io) {
                        net.minecraft.world.item.ItemStack s = io.getItem();
                        if (s != null && !s.isEmpty()) {
                            float chance = io.getChance() <= 0.0f ? 1.0f : io.getChance();
                            result.add(ScriptValue.of(chance));
                        }
                    }
                }
                return new ScriptValue.Array(result);
            })
            // outputs() -> RecipeOutputs — ONE firing's worth of REAL results, split by kind
            // (.items/.fluids/.gases/.experience — see RecipeOutputsType): each output's own chance
            // is rolled independently, so e.g. .items can legitimately come back shorter than the
            // `outputs` PROPERTY's raw declared list (a chance<1.0 bonus item isn't always
            // included). Doesn't touch any machine/tank/xp-buffer itself — the caller decides what
            // to do with each category (a machine like the saw with no inventory needs custom item
            // placement — belt/depot/toss — but fluid/gas/xp are just numbers to hand to whatever
            // it has, if anything). Always just the single item output, no fluid/gas/xp, for a
            // vanilla-backed recipe (stonecutting/cooking never have those or a <1.0 chance).
            .method("outputs", (obj, args) -> {
                if (obj instanceof VanillaRecipeRef v) {
                    List<ScriptValue> items = v.output().isEmpty()
                            ? List.of() : List.of(ScriptValue.ofItem(v.output()));
                    return RecipeOutputsType.wrap(new RecipeOutputsType.Result(items, List.of(), List.of(), 0f));
                }
                List<ScriptValue> items = new ArrayList<>();
                List<RecipeOutputsType.ResourceAmount> fluids = new ArrayList<>();
                List<RecipeOutputsType.ResourceAmount> gases = new ArrayList<>();
                float[] xp = {0f};
                for (RecipeOutput out : recipe(obj).getOutputs()) {
                    float chance = out.getChance() <= 0.0f ? 1.0f : out.getChance();
                    boolean hit = chance >= 1.0f || Math.random() < chance;
                    if (!hit) continue;
                    if (out instanceof ItemOutput io) {
                        net.minecraft.world.item.ItemStack s = io.getItem();
                        if (s != null && !s.isEmpty()) items.add(ScriptValue.ofItem(s.copy()));
                    } else if (out instanceof dev.arubik.craftengine.machine.recipe.FluidOutput fo) {
                        var stack = (dev.arubik.craftengine.fluid.FluidStack) fo.getOutput();
                        if (stack != null && !stack.isEmpty()) {
                            fluids.add(new RecipeOutputsType.ResourceAmount(
                                    stack.getType().id().toString(), stack.getAmount()));
                        }
                    } else if (out instanceof dev.arubik.craftengine.machine.recipe.GasOutput go) {
                        var stack = (dev.arubik.craftengine.gas.GasStack) go.getOutput();
                        if (stack != null && !stack.isEmpty()) {
                            gases.add(new RecipeOutputsType.ResourceAmount(
                                    stack.getType().id().toString(), stack.getAmount()));
                        }
                    } else if (out.getOutput() instanceof Float f) {
                        xp[0] += f;
                    }
                }
                return RecipeOutputsType.wrap(new RecipeOutputsType.Result(items, fluids, gases, xp[0]));
            });
    }

    public static ScriptValue wrap(AbstractProcessingRecipe recipe) {
        return recipe == null ? ScriptValue.NULL : ScriptValue.ofObj("Recipe", recipe);
    }

    /** Wraps a VANILLA recipe (stonecutting/smelting/blasting/smoking/campfire — always exactly
     *  one input, one output) under the SAME "Recipe" script type as this addon's own processing
     *  recipes — see {@code RecipeCollectionType#forInput}. */
    public static ScriptValue wrapVanilla(VanillaRecipeRef ref) {
        return ref == null ? ScriptValue.NULL : ScriptValue.ofObj("Recipe", ref);
    }

    /** {@code id}: vanilla recipe id string. {@code input}/{@code output}: single ItemStack each
     *  (every vanilla recipe kind this addon adapts has exactly one of each). {@code
     *  processingTimeTicks}: vanilla cooking time for a furnace-family recipe, 0 for stonecutting
     *  (instant) — see RecipeCollectionType, which is the only place that constructs this. */
    public record VanillaRecipeRef(String id, net.minecraft.world.item.ItemStack input,
                                    net.minecraft.world.item.ItemStack output, int processingTimeTicks) {}

    private static AbstractProcessingRecipe recipe(Object obj) {
        return (AbstractProcessingRecipe) obj;
    }
}
