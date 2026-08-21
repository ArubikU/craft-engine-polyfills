package dev.arubik.craftengine.script.types.resource;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
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
            .property("size",     obj -> ScriptValue.of(slots(obj).length))
            .property("contents", obj -> {
                List<ScriptValue> list = new ArrayList<>();
                for (ItemStack s : slots(obj)) if (!s.isEmpty()) list.add(ScriptValue.ofItem(s));
                return new ScriptValue.Array(list);
            })
            .method("slot", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                int n = (int) args.get(0).asNum();
                ItemStack[] s = slots(obj);
                return (n >= 0 && n < s.length) ? ScriptValue.ofItem(s[n]) : ScriptValue.NULL;
            })
            .method("slots", (obj, args) -> {
                ItemStack[] s = slots(obj);
                List<ScriptValue> result = new ArrayList<>();
                for (ScriptValue a : args) {
                    int n = (int) a.asNum();
                    if (n >= 0 && n < s.length) result.add(ScriptValue.ofItem(s[n]));
                }
                return new ScriptValue.Array(result);
            })
            .method("count_of", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(0);
                String id = args.get(0).asStr();
                int total = 0;
                for (ItemStack s : slots(obj)) {
                    if (!s.isEmpty() && matchesId(s, id)) total += s.getCount();
                }
                return ScriptValue.of(total);
            })
            .method("has", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                String id = args.get(0).asStr();
                for (ItemStack s : slots(obj))
                    if (!s.isEmpty() && matchesId(s, id)) return ScriptValue.of(true);
                return ScriptValue.of(false);
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
