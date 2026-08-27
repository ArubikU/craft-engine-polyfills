package dev.arubik.craftengine.script.types.resource;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Upgrades type. Instance = UpgradesRef holding type counts + optional inventory slots.
 */
public final class UpgradesType {

    public record UpgradesRef(Map<String, Integer> byType, ItemStack[] inventory) {
        public int total() { return byType.values().stream().mapToInt(i -> i != null ? i : 0).sum(); }
    }

    private UpgradesType() {}

    public static void register() {
        PolyTypeRegistry.define("Upgrades")
            .property("total", obj -> ScriptValue.of(ref(obj).total()))
            .property("inventory", obj -> {
                ItemStack[] inv = ref(obj).inventory();
                if (inv == null) return new ScriptValue.Array(List.of());
                List<ScriptValue> items = new ArrayList<>();
                for (ItemStack s : inv) if (s != null && !s.isEmpty()) items.add(ScriptValue.ofItem(s));
                return new ScriptValue.Array(items);
            })
            .methodTyped1("count", TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (UpgradesRef obj, String type) -> {
                    Integer c = obj.byType().get(type);
                    return (double) (c != null ? c : 0);
                })
            .methodTyped1("slot", TypeCodecs.DOUBLE, TypeCodecs.RAW, ScriptValue.NULL,
                (UpgradesRef obj, Double nArg) -> {
                    ItemStack[] inv = obj.inventory();
                    if (inv == null) return ScriptValue.NULL;
                    int n = nArg.intValue();
                    return (n >= 0 && n < inv.length) ? ScriptValue.ofItem(inv[n]) : ScriptValue.NULL;
                })
            .methodTyped1("has", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (UpgradesRef obj, String type) -> {
                    Integer c = obj.byType().get(type);
                    return c != null && c > 0;
                })
            // Upgrades.get("overclock") — explicit method
            .methodTyped1("get", TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (UpgradesRef obj, String type) -> {
                    Integer c = obj.byType().get(type);
                    return (double) (c != null ? c : 0);
                })
            // Upgrades.overclock / Upgrades.anyType — default property fallback
            .defaultProperty((obj, prop) -> {
                Integer c = ref(obj).byType().get(prop);
                return ScriptValue.of(c != null ? c : 0);
            });

    }

    public static ScriptValue wrap(Map<String, Integer> byType) {
        return wrap(byType, null);
    }

    public static ScriptValue wrap(Map<String, Integer> byType, ItemStack[] inventory) {
        if (byType == null || byType.isEmpty()) return ScriptValue.NULL;
        return ScriptValue.ofObj("Upgrades", new UpgradesRef(byType, inventory));
    }

    private static UpgradesRef ref(Object obj) { return (UpgradesRef) obj; }
}
