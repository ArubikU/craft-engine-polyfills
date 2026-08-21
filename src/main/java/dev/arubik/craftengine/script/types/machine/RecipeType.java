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
 * "Recipe" script type — wraps AbstractProcessingRecipe for script inspection.
 */
public final class RecipeType {

    private RecipeType() {}

    public static void register() {
        PolyTypeRegistry.define("Recipe")
            .property("processing_time", obj -> ScriptValue.of(recipe(obj).getProcessTime()))
            .property("su_cost",         obj -> ScriptValue.of(recipe(obj).getSuCost()))
            .property("min_rpm",         obj -> ScriptValue.of(recipe(obj).getMinRpm()))
            .property("fuel_required",   obj -> ScriptValue.of(recipe(obj).isFuelRequired()))
            .property("inputs", obj -> {
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
                List<ScriptValue> result = new ArrayList<>();
                for (RecipeOutput out : recipe(obj).getOutputs()) {
                    if (out instanceof ItemOutput io) {
                        net.minecraft.world.item.ItemStack s = io.getItem();
                        if (s != null && !s.isEmpty()) result.add(ScriptValue.ofItem(s));
                    }
                }
                return new ScriptValue.Array(result);
            });
    }

    public static ScriptValue wrap(AbstractProcessingRecipe recipe) {
        return recipe == null ? ScriptValue.NULL : ScriptValue.ofObj("Recipe", recipe);
    }

    private static AbstractProcessingRecipe recipe(Object obj) {
        return (AbstractProcessingRecipe) obj;
    }
}
