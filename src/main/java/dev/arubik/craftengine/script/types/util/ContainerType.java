package dev.arubik.craftengine.script.types.util;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * Generic "Container" script type — wraps ANY vanilla {@link Container} (a machine's own
 * inventory, a chest, {@link dev.arubik.craftengine.contraption.ContraptionContainerView}, ...) so
 * scripts have one uniform read/write/push surface regardless of what's actually backing it. Every
 * more specific container-flavored PolyType (e.g. {@code ContraptionContainer}) is registered as a
 * subtype of this one (see {@code PolyTypeRegistry.define(name, "Container")}) purely to layer on
 * extra properties/methods — the base get/set/push/has_room behavior here is shared and final.
 *
 * Deliberately container-implementation-agnostic: nothing here assumes STORAGE/OUTPUT slot roles,
 * a machine, or a contraption — it only calls the plain {@link Container} contract. That's what
 * makes it safe to reuse for "dark", not-yet-imagined uses (a script wrapping a raw shulker box's
 * contents, a future capability's storage, whatever) rather than only where it exists today.
 */
public final class ContainerType {

    private ContainerType() {}

    public static void register() {
        PolyTypeRegistry.define("Container")
            .propertyTyped("size", TypeCodecs.DOUBLE, (Container c) -> (double) c.getContainerSize())
            .propertyTyped("is_empty", TypeCodecs.BOOL, (Container c) -> c.isEmpty())
            .methodTyped1("get_item", TypeCodecs.DOUBLE, TypeCodecs.RAW, ScriptValue.NULL,
                (Container container, Double iArg) -> {
                    int i = iArg.intValue();
                    if (i < 0 || i >= container.getContainerSize()) return ScriptValue.NULL;
                    return ScriptValue.ofItem(container.getItem(i));
                })
            // Item argument decoded with TypeCodecs.RAW (identity passthrough) since stackArg()
            // accepts either shape a script might pass (an Item value or a raw ItemStack Obj) —
            // dynamic dispatch on the argument's own runtime type, so it's decoded with RAW and
            // resolved via stackArg() inside the body exactly as before.
            .methodTyped2("set_item", TypeCodecs.DOUBLE, TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Container container, Double iArg, ScriptValue itemArg) -> {
                    int i = iArg.intValue();
                    if (i < 0 || i >= container.getContainerSize()) return false;
                    ItemStack stack = stackArg(itemArg);
                    container.setItem(i, stack == null ? ItemStack.EMPTY : stack.copy());
                    container.setChanged();
                    return true;
                })
            .methodTyped2("remove_item", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.RAW, ScriptValue.NULL,
                (Container container, Double iArg, Double countArg) -> {
                    int i = iArg.intValue();
                    if (i < 0 || i >= container.getContainerSize()) return ScriptValue.NULL;
                    ItemStack removed = container.removeItem(i, countArg.intValue());
                    container.setChanged();
                    return ScriptValue.ofItem(removed);
                })
            // has_room(item) -> bool, without mutating anything. Item argument via RAW — see set_item's
            // note above on stackArg()'s dynamic dispatch.
            .methodTyped1("has_room", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Container container, ScriptValue itemArg) -> {
                    ItemStack stack = stackArg(itemArg);
                    if (stack == null || stack.isEmpty()) return false;
                    return hasRoom(container, stack);
                })
            // push(item) -> leftover Item that didn't fit (empty Item if it all went in). Merges
            // into existing partial stacks first, then fills empty slots — same strategy as
            // Machine.push_item_to_inventory, generalized to any Container. Item argument via RAW —
            // see set_item's note above on stackArg()'s dynamic dispatch.
            .methodTyped1("push", TypeCodecs.RAW, TypeCodecs.RAW, ScriptValue.NULL,
                (Container container, ScriptValue itemArg) -> {
                    ItemStack stack = stackArg(itemArg);
                    if (stack == null) return ScriptValue.NULL;
                    return ScriptValue.ofItem(push(container, stack.copy()));
                })
            .methodTyped0("clear", TypeCodecs.BOOL,
                (Container container) -> {
                    container.clearContent();
                    return true;
                })
            // pull(count) -> takes up to `count` (default a full stack) from the FIRST non-empty
            // slot found, whatever item it is — the untargeted "just give me something" pull a
            // hopper-alike would do. Returns an empty Item if the container had nothing at all.
            // `count` is optional-with-default (64 when omitted) and the body still runs either
            // way, which is exactly methodTypedOpt1's shape.
            .methodTypedOpt1("pull", TypeCodecs.DOUBLE, 64.0, TypeCodecs.RAW,
                (Container container, Double countArg) ->
                    ScriptValue.ofItem(pullAny(container, countArg.intValue())))
            // pull_item(spec, count) -> takes up to `count` matching `spec`, merging across however
            // many slots hold it — the targeted counterpart of pull(). `spec` accepts:
            //   - an Item/ItemStack value  -> exact id + data-component match
            //   - a plain id string        -> "oak_log" / "minecraft:oak_log" / "polyfills:foo"
            //                                 (vanilla OR CraftEngine custom id, either namespace form)
            //   - a "#"-prefixed tag       -> "#minecraft:logs" / "#logs" (vanilla tag) or a
            //                                 CraftEngine custom item tag declared via that item's
            //                                 own "tags" config
            // Useful e.g. for a Portable Storage Interface only pulling logs, or a sawmill only
            // pulling planks back out of contraption storage.
            // Typed with a NULL-SENTINEL default on the required `spec` slot: RAW is identity over
            // a (never-null) args element, so `spec == null` can only mean "no argument was passed"
            // — exactly the old `args.isEmpty()` early return, taken before the container is
            // touched. `count` keeps its optional 64 default.
            .methodTypedOpt2("pull_item", TypeCodecs.RAW, null, TypeCodecs.DOUBLE, 64.0, TypeCodecs.RAW,
                (Container container, ScriptValue spec, Double countArg) -> {
                    if (spec == null) return ScriptValue.NULL;
                    java.util.function.Predicate<ItemStack> filter = ItemMatch.predicateFor(spec);
                    if (filter == null) return ScriptValue.NULL;
                    return ScriptValue.ofItem(pullMatching(container, filter, countArg.intValue()));
                });
    }

    /** Removes up to {@code count} items from the first non-empty slot, regardless of what it is. */
    public static ItemStack pullAny(Container container, int count) {
        int size = container.getContainerSize();
        for (int i = 0; i < size; i++) {
            if (container.getItem(i).isEmpty()) continue;
            ItemStack removed = container.removeItem(i, count);
            if (!removed.isEmpty()) {
                container.setChanged();
                return removed;
            }
        }
        return ItemStack.EMPTY;
    }

    /** Removes up to {@code count} total items matching {@code filter}, merging across every slot
     *  that holds a match into one returned stack (only valid to merge because every match is, by
     *  construction, the same item+components as the first one found). */
    public static ItemStack pullMatching(Container container, java.util.function.Predicate<ItemStack> filter, int count) {
        ItemStack result = ItemStack.EMPTY;
        int size = container.getContainerSize();
        for (int i = 0; i < size && (result.isEmpty() || result.getCount() < count); i++) {
            ItemStack current = container.getItem(i);
            if (current.isEmpty() || !filter.test(current)) continue;
            if (!result.isEmpty() && !ItemStack.isSameItemSameComponents(current, result)) continue;
            int want = count - (result.isEmpty() ? 0 : result.getCount());
            ItemStack removed = container.removeItem(i, want);
            if (removed.isEmpty()) continue;
            if (result.isEmpty()) result = removed;
            else result.grow(removed.getCount());
        }
        if (!result.isEmpty()) container.setChanged();
        return result;
    }

    /** Merges as much of {@code stack} as fits into {@code container} (partials first, then empty
     *  slots), mutating {@code stack} in place and returning it (possibly emptied). */
    public static ItemStack push(Container container, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return stack;
        int size = container.getContainerSize();
        for (int i = 0; i < size && !stack.isEmpty(); i++) {
            ItemStack current = container.getItem(i);
            if (current.isEmpty() || !ItemStack.isSameItemSameComponents(current, stack)) continue;
            int room = Math.min(current.getMaxStackSize(), container.getMaxStackSize()) - current.getCount();
            if (room <= 0) continue;
            int moved = Math.min(room, stack.getCount());
            current.grow(moved);
            container.setItem(i, current);
            stack.shrink(moved);
        }
        for (int i = 0; i < size && !stack.isEmpty(); i++) {
            if (!container.getItem(i).isEmpty()) continue;
            int cap = Math.min(stack.getMaxStackSize(), container.getMaxStackSize());
            int moved = Math.min(cap, stack.getCount());
            container.setItem(i, stack.copyWithCount(moved));
            stack.shrink(moved);
        }
        container.setChanged();
        return stack;
    }

    public static boolean hasRoom(Container container, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        int size = container.getContainerSize();
        for (int i = 0; i < size; i++) {
            ItemStack current = container.getItem(i);
            if (current.isEmpty()) return true;
            if (ItemStack.isSameItemSameComponents(current, stack)
                    && current.getCount() < current.getMaxStackSize()
                    && current.getCount() < container.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    /** Null-safe: NULL if not backed by a {@link Container}. */
    public static ScriptValue wrap(Container container) {
        return container == null ? ScriptValue.NULL : ScriptValue.ofObj("Container", container);
    }

    /** Accepts the same shapes as {@code Machine.push_item_to_inventory}: a script Item value, or a
     *  raw ItemStack Obj. Returns null (not EMPTY) when the arg isn't an item at all. */
    private static ItemStack stackArg(ScriptValue arg) {
        if (arg instanceof ScriptValue.Item i) return i.stack();
        if (arg instanceof ScriptValue.Obj o && o.instance() instanceof ItemStack is) return is;
        return null;
    }
}
