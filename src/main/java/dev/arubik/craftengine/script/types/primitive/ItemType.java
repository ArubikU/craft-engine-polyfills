package dev.arubik.craftengine.script.types.primitive;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;

public final class ItemType {

    private ItemType() {}

    public static void register() {
        PolyTypeRegistry.define("Item")
            // A CraftEngine custom item reports its CE id ("cml:crate_acacia"); only a plain
            // vanilla item falls back to the registry key. Without this, every CE item sharing a
            // base material (usually paper) had the same id and no filter could tell them apart.
            .property("id",       obj -> {
                String ce = customItemId(stack(obj));
                return ScriptValue.of(ce != null ? ce : BuiltInRegistries.ITEM.getKey(stack(obj).getItem()).toString());
            })
            /** The vanilla material backing this item, ignoring any CE identity. */
            .property("vanilla_id", obj -> ScriptValue.of(BuiltInRegistries.ITEM.getKey(stack(obj).getItem()).toString()))
            .property("is_custom", obj -> ScriptValue.of(customItemId(stack(obj)) != null))
            /**
             * The block this item places, for an item carrying a CE block_item behavior.
             * Falls back to {@code id} so a comparison against Block.id still works for the
             * common case where the item and the block share an id.
             */
            .property("block_id", obj -> {
                String b = placedBlockId(stack(obj));
                if (b != null) return ScriptValue.of(b);
                String ce = customItemId(stack(obj));
                return ScriptValue.of(ce != null ? ce : BuiltInRegistries.ITEM.getKey(stack(obj).getItem()).toString());
            })
            .property("count",    obj -> ScriptValue.of(stack(obj).getCount()))
            .property("max_count",obj -> ScriptValue.of(stack(obj).getMaxStackSize()))
            .property("is_empty", obj -> ScriptValue.of(stack(obj).isEmpty()))
            .property("damage",   obj -> ScriptValue.of(stack(obj).getDamageValue()))
            .property("max_damage",obj->ScriptValue.of(stack(obj).getMaxDamage()))
            .property("name",     obj -> ScriptValue.of(stack(obj).getHoverName().getString()))
            .property("is_stackable", obj -> ScriptValue.of(stack(obj).isStackable()))
            .property("rarity",   obj -> {
                try { return ScriptValue.of(stack(obj).getRarity().name().toLowerCase()); }
                catch (Throwable ignored) { return ScriptValue.of("common"); }
            })
            .property("food_value", obj -> {
                try {
                    var food = stack(obj).get(DataComponents.FOOD);
                    return food != null ? ScriptValue.of(food.nutrition()) : ScriptValue.of(0);
                } catch (Throwable ignored) { return ScriptValue.of(0); }
            })
            .property("custom_model_data", obj -> {
                try {
                    CustomModelData cmd = stack(obj).get(DataComponents.CUSTOM_MODEL_DATA);
                    if (cmd == null) return ScriptValue.of(0);
                    return ScriptValue.of(!cmd.floats().isEmpty() ? cmd.floats().get(0) : 0.0);
                } catch (Throwable ignored) { return ScriptValue.of(0); }
            })
            .property("has_nbt", obj -> {
                try {
                    var cd = stack(obj).get(DataComponents.CUSTOM_DATA);
                    return ScriptValue.of(cd != null && !cd.isEmpty());
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            .property("nbt", obj -> {
                try {
                    var cd = stack(obj).get(DataComponents.CUSTOM_DATA);
                    if (cd == null) return ScriptValue.NULL;
                    CompoundTag tag = cd.copyTag();
                    return NbtDataType.wrap(tag);
                } catch (Throwable ignored) { return ScriptValue.NULL; }
            })
            .property("enchantments", obj -> {
                try {
                    ItemEnchantments enc = stack(obj).get(DataComponents.ENCHANTMENTS);
                    if (enc == null || enc.isEmpty()) return new ScriptValue.Array(List.of());
                    List<ScriptValue> list = new ArrayList<>();
                    enc.entrySet().forEach(entry -> {
                        try {
                            String name = entry.getKey().unwrapKey()
                                .map(Object::toString).orElse("unknown");
                            list.add(ScriptValue.of(name + ":" + entry.getIntValue()));
                        } catch (Throwable ignored2) {}
                    });
                    return new ScriptValue.Array(list);
                } catch (Throwable ignored) { return new ScriptValue.Array(List.of()); }
            })
            // aliases
            .property("type",   obj -> ScriptValue.of(BuiltInRegistries.ITEM.getKey(stack(obj).getItem()).toString()))
            .property("amount", obj -> ScriptValue.of(stack(obj).getCount()))
            // item category helpers — use item tags
            .property("is_food",   obj -> ScriptValue.of(stack(obj).get(DataComponents.FOOD) != null))
            .property("is_weapon", obj -> ScriptValue.of(
                stack(obj).is(net.minecraft.tags.ItemTags.SWORDS)
                || stack(obj).is(net.minecraft.tags.ItemTags.AXES)))
            .property("is_tool", obj -> ScriptValue.of(
                stack(obj).is(net.minecraft.tags.ItemTags.PICKAXES)
                || stack(obj).is(net.minecraft.tags.ItemTags.SHOVELS)
                || stack(obj).is(net.minecraft.tags.ItemTags.HOES)
                || stack(obj).is(net.minecraft.tags.ItemTags.AXES)))
            .property("is_armor", obj -> ScriptValue.of(
                stack(obj).is(net.minecraft.tags.ItemTags.HEAD_ARMOR)
                || stack(obj).is(net.minecraft.tags.ItemTags.CHEST_ARMOR)
                || stack(obj).is(net.minecraft.tags.ItemTags.LEG_ARMOR)
                || stack(obj).is(net.minecraft.tags.ItemTags.FOOT_ARMOR)))
            // attack / tool attributes via DataComponents
            .property("attack_damage", obj -> {
                try {
                    var attrMods = stack(obj).get(DataComponents.ATTRIBUTE_MODIFIERS);
                    if (attrMods == null) return ScriptValue.of(0.0);
                    for (var entry : attrMods.modifiers()) {
                        if (entry.attribute().value() == net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE.value())
                            return ScriptValue.of(entry.modifier().amount());
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.of(0.0);
            })
            .property("attack_speed", obj -> {
                try {
                    var attrMods = stack(obj).get(DataComponents.ATTRIBUTE_MODIFIERS);
                    if (attrMods == null) return ScriptValue.of(0.0);
                    for (var entry : attrMods.modifiers()) {
                        if (entry.attribute().value() == net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED.value())
                            return ScriptValue.of(entry.modifier().amount());
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.of(0.0);
            })
            .property("armor_value", obj -> {
                try {
                    var attrMods = stack(obj).get(DataComponents.ATTRIBUTE_MODIFIERS);
                    if (attrMods == null) return ScriptValue.of(0.0);
                    for (var entry : attrMods.modifiers()) {
                        if (entry.attribute().value() == net.minecraft.world.entity.ai.attributes.Attributes.ARMOR.value())
                            return ScriptValue.of(entry.modifier().amount());
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.of(0.0);
            })
            .property("lore", obj -> {
                try {
                    var lore = stack(obj).get(DataComponents.LORE);
                    if (lore == null || lore.lines().isEmpty()) return new ScriptValue.Array(List.of());
                    List<ScriptValue> lines = new ArrayList<>();
                    lore.lines().forEach(comp -> lines.add(ScriptValue.of(comp.getString())));
                    return new ScriptValue.Array(lines);
                } catch (Throwable ignored) { return new ScriptValue.Array(List.of()); }
            })
            .method("matches", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                String id = args.get(0).asStr();
                String myId = BuiltInRegistries.ITEM.getKey(stack(obj).getItem()).toString();
                return ScriptValue.of(myId.equals(id) || myId.equals("minecraft:" + id));
            })
            .method("with_count", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.ofItem(stack(obj));
                ItemStack copy = stack(obj).copy();
                copy.setCount((int) args.get(0).asNum());
                return ScriptValue.ofItem(copy);
            })
            .method("can_break", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try {
                    String blockId = args.get(0).asStr();
                    net.minecraft.world.level.block.Block block = (net.minecraft.world.level.block.Block)
                        BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.parse(
                            blockId.contains(":") ? blockId : "minecraft:" + blockId));
                    if (block == null) return ScriptValue.of(false);
                    net.minecraft.world.level.block.state.BlockState bs = block.defaultBlockState();
                    return ScriptValue.of(stack(obj).getDestroySpeed(bs) > 1.0f);
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })

            // ---- Data component API (pure NMS) ----

            // item.component("name") → {Name}Component obj or NULL
            .method("component", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                return DataComponentTypes.getComponent(stack(obj), args.get(0).asStr());
            })

            // item.has_component("name") → bool
            .method("has_component", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                return ScriptValue.of(DataComponentTypes.hasComponent(stack(obj), args.get(0).asStr()));
            })

            // item.with_component(comp) → item copy with component set
            // Also accepts: item.with_component("name", value_or_map_of_primitives)
            .method("with_component", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.ofItem(stack(obj));
                if (args.size() == 1) {
                    // Pass typed component object
                    return ScriptValue.ofItem(DataComponentTypes.setComponent(stack(obj), args.get(0)));
                }
                // Two-arg form: with_component("name", value) — build a simple component
                String name = args.get(0).asStr();
                ScriptValue val = args.get(1);
                java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                map.put(name, toPrimitive(val));
                return ScriptValue.ofItem(DataComponentTypes.applyJsonComponents(stack(obj), map));
            })

            // item.remove_component("name") → item copy without that component
            .method("remove_component", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.ofItem(stack(obj));
                return ScriptValue.ofItem(DataComponentTypes.removeComponent(stack(obj), args.get(0).asStr()));
            })

            // item.glow(bool?) → enchantment glint override copy
            .method("glow", (obj, args) -> {
                boolean g = args.isEmpty() || args.get(0).asBool();
                ItemStack copy = stack(obj).copy();
                try { copy.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, g); }
                catch (Throwable ignored) {}
                return ScriptValue.ofItem(copy);
            })

            // item.hide_tooltip(bool?) → TooltipDisplay copy
            .method("hide_tooltip", (obj, args) -> {
                boolean hide = args.isEmpty() || args.get(0).asBool();
                ItemStack copy = stack(obj).copy();
                try { copy.set(DataComponents.TOOLTIP_DISPLAY,
                    new net.minecraft.world.item.component.TooltipDisplay(hide, new java.util.LinkedHashSet<>())); }
                catch (Throwable ignored) {}
                return ScriptValue.ofItem(copy);
            });
    }

    /** Convert a ScriptValue to a JSON-compatible primitive for two-arg with_component. */
    private static Object toPrimitive(ScriptValue v) {
        if (v instanceof ScriptValue.Num n)  return n.value();
        if (v instanceof ScriptValue.Bool b) return b.value();
        if (v instanceof ScriptValue.Str s)  return s.value();
        return v.asStr();
    }

    public static ScriptValue wrap(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return ScriptValue.NULL;
        return ScriptValue.ofObj("Item", stack);
    }

    private static ItemStack stack(Object obj) { return (ItemStack) obj; }

    /** CE custom item id, or null for a plain vanilla item. */
    public static String customItemId(ItemStack nms) {
        try {
            if (nms == null || nms.isEmpty()) return null;
            org.bukkit.inventory.ItemStack bukkit =
                    org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nms);
            net.momirealms.craftengine.core.util.Key key =
                    net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(bukkit);
            return key != null ? key.toString() : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    /**
     * The block a CE item places, read from its {@code block_item} behavior
     * ({@code ItemDefinition.behavior()} -> {@code BlockItem.block()}), or null.
     */
    public static String placedBlockId(ItemStack nms) {
        try {
            if (nms == null || nms.isEmpty()) return null;
            org.bukkit.inventory.ItemStack bukkit =
                    org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nms);
            var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byItemStack(bukkit);
            if (def == null) return null;
            net.momirealms.craftengine.core.item.behavior.ItemBehavior behavior = def.behavior();
            if (behavior instanceof net.momirealms.craftengine.core.item.behavior.BlockItem blockItem) {
                net.momirealms.craftengine.core.util.Key block = blockItem.block();
                return block != null ? block.toString() : null;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }
}
