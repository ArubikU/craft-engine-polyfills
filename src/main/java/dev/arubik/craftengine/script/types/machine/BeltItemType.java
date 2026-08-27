package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.conveyor.belt.ConveyorBlockEntity;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

/**
 * "BeltItem" script type — one specific SLOT on a conveyor segment (see {@link BeltType}'s
 * {@code get_belt_items}/{@code get_belt_item}), carrying its own index, item, and travel
 * progress. Lets a caller enumerate/target any in-transit item on a segment directly, instead of
 * only ever seeing whichever one is nearest the exit (Belt's own front-slot has_item/peek/take).
 */
public final class BeltItemType {

    private BeltItemType() {}

    /** {@code belt}: the segment this slot belongs to. {@code index}: which slot. */
    public record BeltItemRef(BeltType.BeltRef belt, int index) {}

    public static void register() {
        PolyTypeRegistry.define("BeltItem")
            .property("index", obj -> ScriptValue.of(ref(obj).index()))
            // item — the ItemStack in this slot, or an empty Item if it was taken/cleared since
            // this BeltItem was obtained (the underlying slot may have moved on).
            .property("item", obj -> {
                ConveyorBlockEntity belt = conveyor(obj);
                if (belt == null) return ScriptValue.ofItem(net.minecraft.world.item.ItemStack.EMPTY);
                org.bukkit.inventory.ItemStack s = belt.getSlotItem(ref(obj).index());
                return ScriptValue.ofItem(s == null ? net.minecraft.world.item.ItemStack.EMPTY : CraftItemStack.asNMSCopy(s));
            })
            // progress — 0..1 how far along its own travel this specific item is (1.0 = fully
            // arrived at the segment's exit) — -1 if the slot is empty/out of range.
            .property("progress", obj -> {
                ConveyorBlockEntity belt = conveyor(obj);
                return ScriptValue.of(belt == null ? -1.0 : belt.slotProgress(ref(obj).index()));
            })
            // jitter — the random per-item Y-rotation (radians) this slot's display was given on
            // entry, purely cosmetic (visual variety). 0 if empty/out of range.
            .property("jitter", obj -> {
                ConveyorBlockEntity belt = conveyor(obj);
                return ScriptValue.of(belt == null ? 0.0 : belt.slotJitter(ref(obj).index()));
            })
            // entry_dir — the world direction ("north"/"south"/"east"/"west"/"up"/"down") this
            // item entered FROM, i.e. the side of the PREVIOUS segment it came off of — null if
            // empty/out of range/not recorded.
            .property("entry_dir", obj -> {
                ConveyorBlockEntity belt = conveyor(obj);
                if (belt == null) return ScriptValue.NULL;
                var dir = belt.slotEntryDir(ref(obj).index());
                return dir == null ? ScriptValue.NULL : ScriptValue.of(dir.name().toLowerCase(java.util.Locale.ROOT));
            })
            // take() -> Item. Removes and returns whatever is in THIS slot specifically (unlike
            // Belt.take(), which always targets the front/furthest-advanced slot regardless of
            // which BeltItem the caller actually looked at).
            .methodTyped0("take", TypeCodecs.RAW,
                (BeltItemRef obj) -> {
                    ConveyorBlockEntity belt = conveyor(obj);
                    if (belt == null) return ScriptValue.ofItem(net.minecraft.world.item.ItemStack.EMPTY);
                    org.bukkit.inventory.ItemStack taken = belt.takeSlotAt(obj.index());
                    if (taken == null) return ScriptValue.ofItem(net.minecraft.world.item.ItemStack.EMPTY);
                    return ScriptValue.ofItem(CraftItemStack.asNMSCopy(taken));
                });
    }

    public static ScriptValue wrap(BeltItemRef ref) {
        return ref == null ? ScriptValue.NULL : ScriptValue.ofObj("BeltItem", ref);
    }

    private static BeltItemRef ref(Object obj) { return (BeltItemRef) obj; }

    private static ConveyorBlockEntity conveyor(Object obj) {
        BeltItemRef r = ref(obj);
        try {
            var pbe = dev.arubik.craftengine.block.entity.PersistentBlockEntity.getIfLoaded(r.belt().level(), r.belt().pos());
            if (pbe instanceof ConveyorBlockEntity belt) return belt;
        } catch (Throwable ignored) {}
        return null;
    }
}
