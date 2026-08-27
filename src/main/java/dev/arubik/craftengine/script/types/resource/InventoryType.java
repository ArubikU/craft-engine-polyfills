package dev.arubik.craftengine.script.types.resource;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Inventory type. Instance = NMS ItemStack[] (slot array, may contain empty stacks).
 */
public final class InventoryType {

    private InventoryType() {}

    public static void register() {
        PolyTypeRegistry.define("Inventory")
            .propertyTyped("size", TypeCodecs.DOUBLE, (ItemStack[] s) -> (double) s.length)
            // Stays untyped: TypeCodecs.listOf only survives Obj-wrapped PolyType instances, and
            // these elements are ScriptValue.Item values (ofItem) — a distinct ScriptValue variant.
            .property("contents", obj -> {
                List<ScriptValue> list = new ArrayList<>();
                for (ItemStack s : slots(obj)) if (!s.isEmpty()) list.add(ScriptValue.ofItem(s));
                return new ScriptValue.Array(list);
            })
            .methodTyped1("slot", TypeCodecs.DOUBLE, TypeCodecs.RAW, ScriptValue.NULL,
                (ItemStack[] obj, Double nArg) -> {
                    int n = nArg.intValue();
                    return (n >= 0 && n < obj.length) ? ScriptValue.ofItem(obj[n]) : ScriptValue.NULL;
                })
            // slots(...) — variadic (accepts any number of index args), doesn't fit the fixed-arity
            // methodTypedN shape. Its elements are ScriptValue.Item values (ofItem) rather than
            // Obj-wrapped PolyType instances, so TypeCodecs.listOf("Item", ...) would not encode
            // the same value anyway.
            .method("slots", (obj, args) -> {
                ItemStack[] s = slots(obj);
                List<ScriptValue> result = new ArrayList<>();
                for (ScriptValue a : args) {
                    int n = (int) a.asNum();
                    if (n >= 0 && n < s.length) result.add(ScriptValue.ofItem(s[n]));
                }
                return new ScriptValue.Array(result);
            })
            .methodTyped1("count_of", TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (ItemStack[] obj, String id) -> {
                    int total = 0;
                    for (ItemStack s : obj) {
                        if (!s.isEmpty() && matchesId(s, id)) total += s.getCount();
                    }
                    return (double) total;
                })
            .methodTyped1("has", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (ItemStack[] obj, String id) -> {
                    for (ItemStack s : obj)
                        if (!s.isEmpty() && matchesId(s, id)) return true;
                    return false;
                });
    }

    public static ScriptValue wrap(ItemStack[] slots) {
        return slots == null ? ScriptValue.NULL : ScriptValue.ofObj("Inventory", slots);
    }

    private static ItemStack[] slots(Object obj) { return (ItemStack[]) obj; }

    private static boolean matchesId(ItemStack s, String id) {
        String key = BuiltInRegistries.ITEM.getKey(s.getItem()).toString();
        return key.equals(id) || key.equals("minecraft:" + id);
    }
}
