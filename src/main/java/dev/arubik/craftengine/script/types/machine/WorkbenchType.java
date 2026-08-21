package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.crafting.WorkbenchDefinition;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.world.item.ItemStack;

public final class WorkbenchType {

    public record WorkbenchRef(WorkbenchDefinition def, ItemStack[] slots) {}

    private WorkbenchType() {}

    public static void register() {
        PolyTypeRegistry.define("Workbench")
            .property("width",         obj -> ScriptValue.of(ref(obj).def().layout().gridWidth()))
            .property("height",        obj -> ScriptValue.of(ref(obj).def().layout().gridHeight()))
            .property("total_inputs",  obj -> ScriptValue.of(ref(obj).def().layout().inputSlots().length))
            .property("total_outputs", obj -> ScriptValue.of(ref(obj).def().layout().outputSlots().size()))
            .method("input", (obj, args) -> {
                WorkbenchRef r = ref(obj);
                int idx = args.isEmpty() ? 0 : (int) args.get(0).asNum();
                int[] inputSlots = r.def().layout().inputSlots();
                if (idx < 0 || idx >= inputSlots.length) return ScriptValue.NULL;
                return getSlot(r.slots(), inputSlots[idx]);
            })
            .method("output", (obj, args) -> {
                WorkbenchRef r = ref(obj);
                int idx = args.isEmpty() ? 0 : (int) args.get(0).asNum();
                java.util.List<Integer> outputSlots = r.def().layout().outputSlots();
                if (idx < 0 || idx >= outputSlots.size()) return ScriptValue.NULL;
                return getSlot(r.slots(), outputSlots.get(idx));
            })
            .method("tool", (obj, args) -> {
                WorkbenchRef r = ref(obj);
                int idx = args.isEmpty() ? 0 : (int) args.get(0).asNum();
                java.util.List<Integer> toolSlots = r.def().toolSlots();
                if (idx < 0 || idx >= toolSlots.size()) return ScriptValue.NULL;
                return getSlot(r.slots(), toolSlots.get(idx));
            });
    }

    public static ScriptValue wrap(WorkbenchDefinition def, ItemStack[] slots) {
        if (def == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("Workbench", new WorkbenchRef(def, slots != null ? slots : new ItemStack[0]));
    }

    private static ScriptValue getSlot(ItemStack[] slots, int slot) {
        if (slots == null || slot < 0 || slot >= slots.length) return ScriptValue.NULL;
        ItemStack s = slots[slot];
        return (s == null || s.isEmpty()) ? ScriptValue.NULL : ScriptValue.ofItem(s);
    }

    private static WorkbenchRef ref(Object obj) { return (WorkbenchRef) obj; }
}
