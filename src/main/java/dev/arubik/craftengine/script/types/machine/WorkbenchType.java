package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.crafting.WorkbenchDefinition;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
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
            // Migrated to methodTypedOpt1: idx is a single OPTIONAL argument defaulting to 0 when
            // omitted (the old `args.isEmpty() ? 0 : ...`) with the body still running — exactly
            // what methodTypedOptN models (methodTyped1's onMissingArgs would instead skip the body
            // entirely). Instance parameter is WorkbenchRef because ref(obj) is a plain cast.
            .methodTypedOpt1("input", TypeCodecs.DOUBLE, 0.0, TypeCodecs.RAW,
                (WorkbenchRef r, Double idxArg) -> {
                    int idx = idxArg.intValue();
                    int[] inputSlots = r.def().layout().inputSlots();
                    if (idx < 0 || idx >= inputSlots.length) return ScriptValue.NULL;
                    return getSlot(r.slots(), inputSlots[idx]);
                })
            // Same default-if-missing idx shape as input() above.
            .methodTypedOpt1("output", TypeCodecs.DOUBLE, 0.0, TypeCodecs.RAW,
                (WorkbenchRef r, Double idxArg) -> {
                    int idx = idxArg.intValue();
                    java.util.List<Integer> outputSlots = r.def().layout().outputSlots();
                    if (idx < 0 || idx >= outputSlots.size()) return ScriptValue.NULL;
                    return getSlot(r.slots(), outputSlots.get(idx));
                })
            // Same default-if-missing idx shape as input() above.
            .methodTypedOpt1("tool", TypeCodecs.DOUBLE, 0.0, TypeCodecs.RAW,
                (WorkbenchRef r, Double idxArg) -> {
                    int idx = idxArg.intValue();
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
