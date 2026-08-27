package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;

import java.util.List;

/**
 * "RecipeOutputs" script type — the result of {@code Recipe.outputs()}: one firing's worth of
 * ACTUAL rolled results, split by kind so the caller decides what to do with each (a machine like
 * the saw with no inventory still needs custom placement logic for items — belt/depot/toss — but
 * fluid/gas/xp are just numbers to hand to whatever tank/xp mechanism the caller has, if any).
 */
public final class RecipeOutputsType {

    private RecipeOutputsType() {}

    /** One fluid/gas entry: {@code id} (e.g. "minecraft:water"), {@code amount} in mB. */
    public record ResourceAmount(String id, int amount) {}

    public record Result(List<ScriptValue> items, List<ResourceAmount> fluids,
                          List<ResourceAmount> gases, float experience) {}

    public static void register() {
        PolyTypeRegistry.define("RecipeOutputs")
            .property("items", obj -> new ScriptValue.Array(r(obj).items()))
            .property("fluids", obj -> new ScriptValue.Array(r(obj).fluids().stream()
                    .map(RecipeOutputsType::resourceMap).toList()))
            .property("gases", obj -> new ScriptValue.Array(r(obj).gases().stream()
                    .map(RecipeOutputsType::resourceMap).toList()))
            .property("experience", obj -> ScriptValue.of(r(obj).experience()));
    }

    private static ScriptValue resourceMap(ResourceAmount ra) {
        java.util.LinkedHashMap<String, ScriptValue> map = new java.util.LinkedHashMap<>();
        map.put("id", ScriptValue.of(ra.id()));
        map.put("amount", ScriptValue.of(ra.amount()));
        return dev.arubik.craftengine.script.types.primitive.MapType.wrap(map);
    }

    public static ScriptValue wrap(Result result) {
        return result == null ? ScriptValue.NULL : ScriptValue.ofObj("RecipeOutputs", result);
    }

    private static Result r(Object obj) { return (Result) obj; }
}
