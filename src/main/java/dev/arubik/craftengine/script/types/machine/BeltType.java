package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.conveyor.belt.ConveyorBlockEntity;
import dev.arubik.craftengine.script.types.world.BlockType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

public final class BeltType {

    public record BeltRef(ServerLevel level, BlockPos pos) {}

    private BeltType() {}

    public static void register() {
        PolyTypeRegistry.define("Belt")
            .propertyTyped("x", TypeCodecs.DOUBLE, (BeltRef r) -> (double) r.pos().getX())
            .propertyTyped("y", TypeCodecs.DOUBLE, (BeltRef r) -> (double) r.pos().getY())
            .propertyTyped("z", TypeCodecs.DOUBLE, (BeltRef r) -> (double) r.pos().getZ())
            .propertyTyped("pos", TypeCodecs.polyType("Vector", org.joml.Vector3d.class),
                (BeltRef r) -> new org.joml.Vector3d(r.pos().getX(), r.pos().getY(), r.pos().getZ()))
            // BlockType.wrap's null guard is preserved by returning null (which the codec encodes
            // back to NULL) rather than a BlockRef with a null half.
            .propertyTyped("block", TypeCodecs.polyType("Block", BlockType.BlockRef.class),
                (BeltRef r) -> r.level() == null || r.pos() == null ? null : new BlockType.BlockRef(r.level(), r.pos()))
            .methodTyped0("get_block", TypeCodecs.RAW, (BeltRef obj) -> BlockType.wrap(obj.level(), obj.pos()))
            // exists — whether a REAL conveyor is actually at this position. Machine.belt_at(...)
            // (unlike container_at) always returns a non-null Belt wrapper for any loaded position,
            // even open air — every OTHER property here (is_full, speed, ...) silently falls back to
            // a "safe" default when there's no real conveyor (is_full -> true, speed -> 0), which is
            // exactly right for a caller only reading THOSE, but indistinguishable from "belt full"
            // if a caller needs to know "is there even a belt here at all" (e.g. a funnel deciding
            // between holding at a full belt vs dropping into open air) without this.
            // conveyor(obj) is a live block-entity LOOKUP, not a cast, so the instance stays Object
            // on every property below that calls it — same rule the methods here already follow.
            .propertyTyped("exists", TypeCodecs.BOOL, (Object obj) -> conveyor(obj) != null)
            // is_full — whether this belt segment has no room to accept a new item at all.
            .propertyTyped("is_full", TypeCodecs.BOOL, (Object obj) -> {
                ConveyorBlockEntity belt = conveyor(obj);
                return belt == null || belt.isFull();
            })
            // has_item — whether this segment is currently carrying an item at its front (exit) slot
            // — the one a machine sitting over/beside this segment would actually interact with.
            // TRUE as soon as an item enters this segment at all, even mid-transit — see `progress`
            // for how far along that item actually is.
            .propertyTyped("has_item", TypeCodecs.BOOL, (Object obj) -> {
                ConveyorBlockEntity belt = conveyor(obj);
                return belt != null && belt.peekCarried() != null && !belt.peekCarried().getType().isAir();
            })
            // progress — how far along its own travel the front carried item is, 0..1 (1.0 = fully
            // arrived at this segment's exit, stalled because the next tile won't accept it yet);
            // -1 if nothing is carried. A machine pulling from a side-adjacent feeding belt (not one
            // it sits directly on) should gate on this (e.g. progress >= 0.99), not just has_item,
            // or it'll snatch an item still mid-transit toward some OTHER destination.
            .propertyTyped("progress", TypeCodecs.DOUBLE, (Object obj) -> {
                ConveyorBlockEntity belt = conveyor(obj);
                return belt == null ? -1.0 : (double) belt.frontProgress();
            })
            // rpm — this segment's own current effective RPM (0 if stalled/no belt there).
            .propertyTyped("rpm", TypeCodecs.DOUBLE, (Object obj) -> {
                ConveyorBlockEntity belt = conveyor(obj);
                return belt == null ? 0.0 : (double) belt.effectiveRpm();
            })
            // speed — the actual per-tick progress increment (0..1) this segment is moving items
            // at right now, respecting its own belt_types speed formula/base_travel_ticks (see
            // BeltRuntime#progressPerTick) — not just a function of rpm, since a belt_types entry
            // can override the rpm->speed relationship entirely. What a neighbor (e.g. a funnel)
            // should read instead of hardcoding its own travel cadence.
            .propertyTyped("speed", TypeCodecs.DOUBLE, (Object obj) -> {
                ConveyorBlockEntity belt = conveyor(obj);
                return belt == null ? 0.0 : (double) belt.currentProgressPerTick();
            })
            // height — this segment's own carry height (0..1, block-local).
            .propertyTyped("height", TypeCodecs.DOUBLE, (Object obj) -> {
                ConveyorBlockEntity belt = conveyor(obj);
                return belt == null
                        ? (double) dev.arubik.craftengine.conveyor.belt.ConveyorMath.BELT_TOP_Y
                        : (double) belt.carryHeight();
            })
            // item_scale — this segment's own item display scale.
            .propertyTyped("item_scale", TypeCodecs.DOUBLE, (Object obj) -> {
                ConveyorBlockEntity belt = conveyor(obj);
                return belt == null
                        ? (double) dev.arubik.craftengine.conveyor.belt.BeltType.BeltProperties.DEFAULT_ITEM_SCALE
                        : (double) belt.itemScale();
            })
            // peek() — the item currently at this segment's front slot, WITHOUT removing it (an
            // empty Item if none). Use with replace()/take() to intercept it.
            .methodTyped0("peek", TypeCodecs.RAW, (Object obj) -> {
                ConveyorBlockEntity belt = conveyor(obj);
                if (belt == null) return ScriptValue.NULL;
                org.bukkit.inventory.ItemStack carried = belt.peekCarried();
                if (carried == null) return ScriptValue.ofItem(net.minecraft.world.item.ItemStack.EMPTY);
                return ScriptValue.ofItem(CraftItemStack.asNMSCopy(carried));
            })
            // replace(item) -> bool. Swaps whatever this segment is currently carrying for `item`,
            // in place — the item keeps riding the SAME belt through the same position, just
            // transformed. This is the exact mechanic Create's saw uses to turn logs into planks as
            // they pass underneath without ever leaving the belt. Pass an empty/NULL item to just
            // remove whatever was there (same as take()). False if this segment isn't carrying
            // anything to replace.
            // A missing arg maps to `bukkit = null` and the handler STILL RUNS
            // belt.replaceCarried(null) — an in-body default, not an onMissingArgs short-circuit —
            // so this is methodTypedOpt1's shape. The item is decoded with TypeCodecs.RAW (see
            // put() below on bukkitStack's dynamic coercion) defaulting to ScriptValue.NULL, which
            // bukkitStack maps to null exactly as the old args.isEmpty() branch did. `conveyor(obj)`
            // is a live block-entity lookup, not a cast, so the instance stays Object here.
            .methodTypedOpt1("replace", TypeCodecs.RAW, ScriptValue.NULL, TypeCodecs.BOOL,
                (Object obj, ScriptValue itemArg) -> {
                    ConveyorBlockEntity belt = conveyor(obj);
                    if (belt == null) return false;
                    return belt.replaceCarried(bukkitStack(itemArg));
                })
            // take() -> Item. Removes and returns whatever this segment is carrying (an empty Item
            // if nothing was there) — for a machine that wants to pull the item off the belt
            // entirely (e.g. into Contraption.container) rather than transform it in place.
            .methodTyped0("take", TypeCodecs.RAW, (Object obj) -> {
                ConveyorBlockEntity belt = conveyor(obj);
                if (belt == null) return ScriptValue.ofItem(net.minecraft.world.item.ItemStack.EMPTY);
                org.bukkit.inventory.ItemStack taken = belt.takeSlot();
                if (taken == null) return ScriptValue.ofItem(net.minecraft.world.item.ItemStack.EMPTY);
                return ScriptValue.ofItem(CraftItemStack.asNMSCopy(taken));
            })
            // put(item) -> leftover Item that didn't fit (empty if it all went on). Places `item`
            // onto this belt segment as a new carried item — for a machine ejecting a result onto a
            // belt in front of it. Argument is decoded with TypeCodecs.RAW (identity passthrough) —
            // bukkitStack(ScriptValue) itself does the real coercion (Item value or raw ItemStack
            // Obj), which isn't expressible as a single native TypeCodec.
            .methodTyped1("put", TypeCodecs.RAW, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, ScriptValue itemArg) -> {
                    ConveyorBlockEntity belt = conveyor(obj);
                    org.bukkit.inventory.ItemStack bukkit = bukkitStack(itemArg);
                    if (bukkit == null) return ScriptValue.NULL;
                    if (belt == null) return ScriptValue.ofItem(CraftItemStack.asNMSCopy(bukkit));
                    org.bukkit.inventory.ItemStack leftover = belt.putSlot(bukkit);
                    return ScriptValue.ofItem(leftover == null ? net.minecraft.world.item.ItemStack.EMPTY : CraftItemStack.asNMSCopy(leftover));
                })
            // slot_count — how many items this segment can carry in transit at once.
            .propertyTyped("slot_count", TypeCodecs.DOUBLE, (Object obj) -> {
                ConveyorBlockEntity belt = conveyor(obj);
                return (double) (belt == null ? 0 : belt.slotCount());
            })
            // get_belt_items() -> Array of BeltItem, one per OCCUPIED slot (any position along the
            // segment, not just the front) — for a caller that needs to see everything in transit,
            // not just whatever's nearest the exit. Empty array if nothing's carried or the block
            // isn't (or is no longer) a conveyor.
            // Return codec declares the element type: every element is a BeltItem, so the handler
            // returns the real List<BeltItemRef> and the codec does the ofObj("BeltItem", ...)
            // wrapping BeltItemType.wrap used to do by hand (the refs are freshly constructed and
            // never null, so the encoding is identical).
            .methodTyped0("get_belt_items", TypeCodecs.listOf("BeltItem", BeltItemType.BeltItemRef.class),
                (BeltRef r) -> {
                    ConveyorBlockEntity belt = conveyor(r);
                    if (belt == null) return java.util.List.of();
                    java.util.List<BeltItemType.BeltItemRef> result = new java.util.ArrayList<>();
                    for (int i = 0; i < belt.slotCount(); i++) {
                        if (!belt.isSlotEmpty(i)) result.add(new BeltItemType.BeltItemRef(r, i));
                    }
                    return result;
                })
            // get_belt_item(index) -> BeltItem at that specific slot, NULL if out of range or empty.
            .methodTyped1("get_belt_item", TypeCodecs.DOUBLE, TypeCodecs.RAW, ScriptValue.NULL,
                (BeltRef r, Double idxArg) -> {
                    ConveyorBlockEntity belt = conveyor(r);
                    int idx = idxArg.intValue();
                    if (belt == null || belt.isSlotEmpty(idx)) return ScriptValue.NULL;
                    return BeltItemType.wrap(new BeltItemType.BeltItemRef(r, idx));
                });
    }

    public static ScriptValue wrap(ServerLevel level, BlockPos pos) {
        if (level == null || pos == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("Belt", new BeltRef(level, pos));
    }

    private static BeltRef ref(Object obj) { return (BeltRef) obj; }

    /** The live {@link ConveyorBlockEntity} at this Belt's position, or null if the block there
     *  isn't (or is no longer) a conveyor — e.g. it was broken since this Belt value was obtained. */
    private static ConveyorBlockEntity conveyor(Object obj) {
        BeltRef r = ref(obj);
        try {
            var pbe = dev.arubik.craftengine.block.entity.PersistentBlockEntity.getIfLoaded(r.level(), r.pos());
            if (pbe instanceof ConveyorBlockEntity belt) return belt;
        } catch (Throwable ignored) {}
        return null;
    }

    /** Accepts the same script-item shapes used across the engine (Item value or a raw ItemStack
     *  Obj) and converts to Bukkit, which is what ConveyorBlockEntity's own API speaks. */
    private static org.bukkit.inventory.ItemStack bukkitStack(ScriptValue arg) {
        net.minecraft.world.item.ItemStack nms;
        if (arg instanceof ScriptValue.Item i) nms = i.stack();
        else if (arg instanceof ScriptValue.Obj o && o.instance() instanceof net.minecraft.world.item.ItemStack is) nms = is;
        else return null;
        if (nms == null || nms.isEmpty()) return null;
        return CraftItemStack.asBukkitCopy(nms);
    }
}
