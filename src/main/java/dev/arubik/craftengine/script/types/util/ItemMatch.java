package dev.arubik.craftengine.script.types.util;

import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

/**
 * The one shared "does this stack match that spec" check for the whole script engine — a plain id
 * ("oak_log" / "minecraft:oak_log" / a CraftEngine custom id like "polyfills:foo"), a
 * {@code #}-prefixed tag ("#minecraft:logs", vanilla OR a CraftEngine custom item's own declared
 * tags), or an exact Item/ItemStack script value (full id + data-component match).
 *
 * Every place in the script engine that used to compare a script Item against only a raw vanilla
 * registry id (missing CraftEngine custom items entirely, and with no tag support at all) should
 * go through here instead — see {@code Item.matches}/{@code ItemType}, the {@code matches}/
 * {@code has_item} builtins ({@code ScriptBuiltins}), and {@code Container.pull_item}
 * ({@code ContainerType}).
 */
public final class ItemMatch {

    private ItemMatch() {}

    /** {@code spec} against a raw NMS stack — a plain id or {@code #}-tag string. */
    public static boolean matches(ItemStack stack, String spec) {
        if (stack == null || stack.isEmpty() || spec == null || spec.isBlank()) return false;
        if (spec.startsWith("#")) return matchesTag(stack, spec.substring(1));
        return normalizeId(spec).equalsIgnoreCase(idOf(stack));
    }

    /** {@code spec} against a script Item value (the common script-engine shape). */
    public static boolean matches(ScriptValue itemValue, String spec) {
        if (!(itemValue instanceof ScriptValue.Item i)) return false;
        return matches(i.stack(), spec);
    }

    public static boolean matchesTag(ItemStack stack, String tagSpec) {
        if (stack == null || stack.isEmpty()) return false;
        net.momirealms.craftengine.core.util.Key tagKey = parseKey(tagSpec);
        try {
            org.bukkit.inventory.ItemStack bukkit = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(stack);
            var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byItemStack(bukkit);
            if (def != null) return def.is(tagKey);
        } catch (Throwable ignored) {}
        TagKey<Item> vanillaTag = TagKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(tagKey.namespace(), tagKey.value()));
        return stack.is(vanillaTag);
    }

    /** The CraftEngine custom id if {@code stack} is a custom item, else its vanilla registry id —
     *  same resolution {@code StorageFilters}/recipe matching already use. */
    public static String idOf(ItemStack stack) {
        try {
            org.bukkit.inventory.ItemStack bukkit = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(stack);
            net.momirealms.craftengine.core.util.Key ce =
                    net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(bukkit);
            if (ce != null) return ce.toString();
        } catch (Throwable ignored) {}
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    /**
     * Full predicate builder for a script arg that may be an exact Item/ItemStack value, a plain id
     * string, or a {@code #}-tag string — what {@code Container.pull_item} accepts. Null if
     * {@code spec} is neither an item nor a usable string.
     */
    public static Predicate<ItemStack> predicateFor(ScriptValue spec) {
        ItemStack exact = stackArg(spec);
        if (exact != null) {
            if (exact.isEmpty()) return null;
            return s -> !s.isEmpty() && ItemStack.isSameItemSameComponents(s, exact);
        }
        String text;
        try { text = spec.asStr(); } catch (Throwable ignored) { return null; }
        if (text == null || text.isBlank()) return null;
        if (text.startsWith("#")) {
            String tagSpec = text.substring(1);
            return s -> matchesTag(s, tagSpec);
        }
        return s -> matches(s, text);
    }

    private static String normalizeId(String spec) {
        return spec.contains(":") ? spec : "minecraft:" + spec;
    }

    private static net.momirealms.craftengine.core.util.Key parseKey(String spec) {
        return spec.contains(":") ? net.momirealms.craftengine.core.util.Key.of(spec)
                : net.momirealms.craftengine.core.util.Key.of("minecraft", spec);
    }

    /** Accepts the same shapes as {@code Machine.push_item_to_inventory}: a script Item value, or a
     *  raw ItemStack Obj. Returns null (not EMPTY) when the arg isn't an item at all. */
    private static ItemStack stackArg(ScriptValue arg) {
        if (arg instanceof ScriptValue.Item i) return i.stack();
        if (arg instanceof ScriptValue.Obj o && o.instance() instanceof ItemStack is) return is;
        return null;
    }
}
