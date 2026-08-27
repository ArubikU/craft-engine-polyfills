package dev.arubik.craftengine.script.types.primitive;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;

public final class ItemType {

    /** Storage-key prefix for every TypedKeyBridge-backed accessor below — one flat namespace on
     *  the item's own CUSTOM_DATA tag, mirroring Machine's identical "tkey_" prefix over its block
     *  entity's CE tag (see MachineType) so a typed value bridged between a machine and this item
     *  (ItemDefinition#bridgeTyped, Machine.to_item) round-trips under the exact same key either
     *  side reads. */
    private static final String TYPED_PREFIX = "tkey_";

    /** Namespace singleton bound as the bare {@code Item} identifier so a script can call
     *  {@code Item.skull(player)} as a static-style constructor — see {@code ScriptContext.Builder
     *  #typed("Item", ItemType.NAMESPACE)}. Every OTHER {@code Item.*} method above operates on a
     *  real wrapped {@code ItemStack} instance instead; this is the one exception. */
    public static final Object NAMESPACE = new Object();

    private ItemType() {}

    public static void register() {
        PolyTypeRegistry.define("Item")
            // Item.skull(player) — a PLAYER_HEAD item carrying that player's skin, built via
            // Bukkit's own SkullMeta#setOwningPlayer (the modern, non-deprecated way to set a
            // skull's owner/texture) then converted back to the NMS ItemStack this codebase's
            // ScriptValue.Item actually wraps. Only reachable through the NAMESPACE instance —
            // every other Item.* method below expects `obj` to already be a real ItemStack.
            .method("skull", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                try {
                    org.bukkit.entity.Player bukkitPlayer = extractBukkitPlayer(args.get(0));
                    if (bukkitPlayer == null) return ScriptValue.NULL;
                    org.bukkit.inventory.ItemStack bukkitStack =
                            new org.bukkit.inventory.ItemStack(org.bukkit.Material.PLAYER_HEAD);
                    org.bukkit.inventory.meta.SkullMeta meta =
                            (org.bukkit.inventory.meta.SkullMeta) bukkitStack.getItemMeta();
                    meta.setOwningPlayer(bukkitPlayer);
                    bukkitStack.setItemMeta(meta);
                    ItemStack nmsStack = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkitStack);
                    return ItemType.wrap(nmsStack);
                } catch (Throwable t) { return ScriptValue.NULL; }
            })
            // Item.create(id, count?) — the CraftEngine-aware counterpart to the plain-vanilla
            // create_item(...) builtin (which only ever looks up BuiltInRegistries.ITEM, so it
            // can't build e.g. "default:gui_head_size_1" or any "cml:"/"polyfills:" custom item).
            // Tries CraftEngine's own item registry first, falls back to vanilla so this can fully
            // replace create_item(...) in a script that wants ONE constructor for either kind.
            .method("create", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                String id = args.get(0).asStr();
                int count = args.size() >= 2 ? (int) args.get(1).asNum() : 1;
                try {
                    net.momirealms.craftengine.core.util.Key key = net.momirealms.craftengine.core.util.Key.of(id);
                    var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(key);
                    // CraftEngineItems.byId only finds items registered under the ACTIVE resource
                    // pack's own namespace ("cml" for this project's "modern" pack) — items from
                    // CraftEngine's OTHER bundled packs (default_assets' "default:gui_head_size_1"
                    // etc., loaded per the boot log's "Loaded pack: default_assets. Default
                    // namespace: default") come back NULL here even though they're fully loaded and
                    // CraftEngine's own "/craftengine item give" command can spawn them — decompiling
                    // GiveItemCommand showed it falls back to BukkitItemManager.instance()
                    // .getItemDefinitionByPath(path) (path-only, pack-agnostic) for exactly this
                    // case, so do the same instead of silently degrading to a vanilla/AIR item.
                    if (def == null) {
                        var byPath = net.momirealms.craftengine.bukkit.item.BukkitItemManager.instance()
                                .getItemDefinitionByPath(key.value());
                        if (byPath.isPresent() && byPath.get() instanceof net.momirealms.craftengine.bukkit.item.BukkitItemDefinition bukkitDef) {
                            def = bukkitDef;
                        }
                    }
                    if (def != null) {
                        org.bukkit.inventory.ItemStack bukkit = def.buildBukkitItem();
                        bukkit.setAmount(count);
                        return ItemType.wrap(org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkit));
                    }
                } catch (Throwable ignored) {}
                try {
                    var item = (net.minecraft.world.item.Item) BuiltInRegistries.ITEM.getValue(
                            net.minecraft.resources.Identifier.parse(id.contains(":") ? id : "minecraft:" + id));
                    if (item == null) return ScriptValue.NULL;
                    return ItemType.wrap(new ItemStack(item, count));
                } catch (Throwable ignored) { return ScriptValue.NULL; }
            })
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
            // Accepts a plain id ("oak_log"/"minecraft:oak_log"/a CraftEngine custom id) OR a
            // "#"-prefixed tag ("#minecraft:logs", vanilla or a CraftEngine custom item's own
            // declared tags) — see ItemMatch, the shared id/tag matcher used across the script
            // engine (Container.pull_item, the matches()/has_item() builtins, here).
            .methodTyped1("matches", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (ItemStack obj, String pattern) ->
                    dev.arubik.craftengine.script.types.util.ItemMatch.matches(obj, pattern))
            // Full identity comparison (type + every data component — enchantments, custom name,
            // durability, everything), ignoring stack COUNT — the same rule vanilla stacking uses.
            // `matches(id)` above only ever compared the base item type; a filter that wants to
            // require a SPECIFIC enchanted book (not just "any book") needs this instead.
            // arg1 uses RAW — the arg is checked via `instanceof ScriptValue.Item`, not coerced to a
            // native type, so it must stay a ScriptValue for that check to work identically.
            .methodTyped1("same_as", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (ItemStack obj, ScriptValue arg0) -> {
                    if (!(arg0 instanceof ScriptValue.Item other)) return false;
                    return ItemStack.isSameItemSameComponents(obj, other.stack());
                })
            .method("with_count", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.ofItem(stack(obj));
                ItemStack copy = stack(obj).copy();
                copy.setCount((int) args.get(0).asNum());
                return ScriptValue.ofItem(copy);
            })
            .methodTyped1("can_break", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (ItemStack obj, String blockId) -> {
                    try {
                        net.minecraft.world.level.block.Block block = (net.minecraft.world.level.block.Block)
                            BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.parse(
                                blockId.contains(":") ? blockId : "minecraft:" + blockId));
                        if (block == null) return false;
                        net.minecraft.world.level.block.state.BlockState bs = block.defaultBlockState();
                        return obj.getDestroySpeed(bs) > 1.0f;
                    } catch (Throwable ignored) { return false; }
                })

            // ---- Data component API (pure NMS) ----

            // item.component("name") → {Name}Component obj or NULL
            // RAW return — getComponent already returns a ScriptValue (component obj or NULL), no
            // native return type to pin it to.
            .methodTyped1("component", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (ItemStack obj, String name) -> DataComponentTypes.getComponent(obj, name))

            // item.has_component("name") → bool
            .methodTyped1("has_component", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (ItemStack obj, String name) -> DataComponentTypes.hasComponent(obj, name))

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
            })

            // item.with_name("<red>Foo") → item copy with a MiniMessage-parsed custom name.
            // Equivalent to with_component("custom_name", text) but named for the common case
            // (e.g. an on_render/on_shot script picking a name ad hoc) instead of needing to know
            // the raw component id. Falls back to a literal (unparsed) Component on bad MiniMessage
            // syntax rather than dropping the name entirely.
            .method("with_name", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.ofItem(stack(obj));
                ItemStack copy = stack(obj).copy();
                String text = args.get(0).asStr();
                try {
                    copy.set(DataComponents.CUSTOM_NAME, toDisplayComponent(text));
                } catch (Throwable ignored) {}
                return ScriptValue.ofItem(copy);
            })

            // item.with_lore(line, line, ...) or item.with_lore([line, line, ...]) → item copy
            // with that MiniMessage-parsed lore, replacing whatever lore (if any) it already had.
            // Both call shapes work, same flexibility as event.set_drops — no array-literal syntax
            // required for the common single-or-few-lines case.
            .method("with_lore", (obj, args) -> {
                ItemStack copy = stack(obj).copy();
                List<net.minecraft.network.chat.Component> lines = new ArrayList<>();
                for (ScriptValue v : args) {
                    if (v instanceof ScriptValue.Array arr) {
                        for (ScriptValue e : arr.elements()) lines.add(toDisplayComponent(e.asStr()));
                    } else {
                        lines.add(toDisplayComponent(v.asStr()));
                    }
                }
                try { copy.set(DataComponents.LORE, new net.minecraft.world.item.component.ItemLore(lines)); }
                catch (Throwable ignored) {}
                return ScriptValue.ofItem(copy);
            })

            // ---- Generic TypedKey storage (see dev.arubik.craftengine.script.TypedKeyBridge) ----
            // Standardized get/set-by-NbtType over this item's own CUSTOM_DATA, the item-side
            // counterpart of Machine.get_typed/set_typed — one key/type convention instead of a
            // bespoke accessor pair per feature. Items are values here, so with_typed returns a
            // NEW copy (same idiom as with_component) rather than mutating in place.
            // Migrated to the typed-registration API (PolyType.methodTyped2, mirrors
            // MachineType.get_typed) — key/type are always strings; the stored VALUE stays
            // TypeCodecs.RAW since its real coercion is dynamic, decided by whichever
            // TypedKeyBridge.Codec `typeName` names.
            .methodTyped2("get_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (ItemStack obj, String key, String typeName) -> {
                    dev.arubik.craftengine.script.TypedKeyBridge.Codec codec = dev.arubik.craftengine.script.TypedKeyBridge.resolve(typeName);
                    if (codec == null) return ScriptValue.NULL;
                    return readTyped(obj, TYPED_PREFIX + key, codec);
                })
            .methodTyped2("has_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (ItemStack obj, String key, String typeName) -> {
                    dev.arubik.craftengine.script.TypedKeyBridge.Codec codec = dev.arubik.craftengine.script.TypedKeyBridge.resolve(typeName);
                    if (codec == null) return false;
                    return hasTyped(obj, TYPED_PREFIX + key);
                })
            .method("with_typed", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.ofItem(stack(obj));
                dev.arubik.craftengine.script.TypedKeyBridge.Codec codec = dev.arubik.craftengine.script.TypedKeyBridge.resolve(args.get(1).asStr());
                if (codec == null) return ScriptValue.ofItem(stack(obj));
                return ScriptValue.ofItem(writeTyped(stack(obj), TYPED_PREFIX + args.get(0).asStr(), codec, args.get(2)));
            })

            // ---- Item behavior motor API (dev.arubik.craftengine.item.ItemDefinition) ----

            // item.transmutate("polyfills:new_id") → a fresh instance of that CraftEngine custom
            // item, carrying over count and this item's custom NBT (tanks, container, any other
            // custom_data) — the actual identity swap, everything else (enchantments, damage,
            // display name, ...) is intentionally NOT preserved since the target item's own
            // definition supplies its own defaults for those.
            .method("transmutate", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.ofItem(stack(obj));
                try {
                    ItemStack self = stack(obj);
                    net.momirealms.craftengine.core.util.Key targetId =
                            net.momirealms.craftengine.core.util.Key.of(args.get(0).asStr());
                    var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(targetId);
                    if (def == null) return ScriptValue.ofItem(self);
                    org.bukkit.inventory.ItemStack bukkit = def.buildBukkitItem();
                    ItemStack result = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkit);
                    result.setCount(self.getCount());
                    var customData = self.get(DataComponents.CUSTOM_DATA);
                    if (customData != null) result.set(DataComponents.CUSTOM_DATA, customData);
                    return ScriptValue.ofItem(result);
                } catch (Throwable ignored) { return ScriptValue.ofItem(stack(obj)); }
            })

            // item.with_profile(player_or_name) — sets a REAL player-skin profile on THIS item
            // (any material Bukkit exposes as SkullMeta for, including a custom item whose
            // underlying material is player_head, e.g. CraftEngine's bundled
            // "default:gui_head_size_1"/"gui_head_size_4" — see Item.create(...)). Lets a script
            // combine a nicer GUI-scaled head model with an ARBITRARY target player's face, unlike
            // that item's own default {@code client_bound_data: profile: <arg:player.name>}
            // templating (which only ever shows the VIEWER their own face). Accepts either a
            // wrapped Player/Entity value or a plain player-name string (works offline too, via
            // whatever skin Bukkit already has cached for that name).
            .method("with_profile", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.ofItem(stack(obj));
                try {
                    org.bukkit.OfflinePlayer target = resolveProfileTarget(args.get(0));
                    if (target == null) return ScriptValue.ofItem(stack(obj));
                    org.bukkit.inventory.ItemStack bukkitStack =
                            org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(stack(obj));
                    if (!(bukkitStack.getItemMeta() instanceof org.bukkit.inventory.meta.SkullMeta meta)) {
                        return ScriptValue.ofItem(stack(obj));
                    }
                    meta.setOwningPlayer(target);
                    bukkitStack.setItemMeta(meta);
                    return ItemType.wrap(org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkitStack));
                } catch (Throwable ignored) { return ScriptValue.ofItem(stack(obj)); }
            })
            // item.tank("name") → current amount stored in that named tank buffer
            .methodTyped1("tank", TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (ItemStack obj, String name) ->
                    (double) dev.arubik.craftengine.item.ItemStateData.tankAmount(obj, name))


            // item.tank_capacity("name") → capacity declared on this item's ItemDefinition, or 0
            .methodTyped1("tank_capacity", TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (ItemStack obj, String name) -> {
                    var def = dev.arubik.craftengine.item.ItemDefinition.byId(ceKey(obj));
                    if (def == null) return 0.0;
                    var tank = def.tank(name);
                    return tank != null ? (double) tank.capacity() : 0.0;
                })

            // item.set_tank("name", amount) → item copy with that tank buffer set (clamped to
            // capacity when the item's own ItemDefinition declares one, uncapped otherwise)
            .method("set_tank", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.ofItem(stack(obj));
                ItemStack self = stack(obj);
                String name = args.get(0).asStr();
                int amount = (int) args.get(1).asNum();
                var def = dev.arubik.craftengine.item.ItemDefinition.byId(ceKey(self));
                var tank = def != null ? def.tank(name) : null;
                int capacity = tank != null ? tank.capacity() : Integer.MAX_VALUE;
                return ScriptValue.ofItem(dev.arubik.craftengine.item.ItemStateData.setTankAmount(self, name, amount, capacity));
            })

            // item.add_tank("name", delta) → set_tank(name, tank(name) + delta), clamped as above
            .method("add_tank", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.ofItem(stack(obj));
                ItemStack self = stack(obj);
                String name = args.get(0).asStr();
                int delta = (int) args.get(1).asNum();
                var def = dev.arubik.craftengine.item.ItemDefinition.byId(ceKey(self));
                var tank = def != null ? def.tank(name) : null;
                int capacity = tank != null ? tank.capacity() : Integer.MAX_VALUE;
                int current = dev.arubik.craftengine.item.ItemStateData.tankAmount(self, name);
                return ScriptValue.ofItem(dev.arubik.craftengine.item.ItemStateData.setTankAmount(self, name, current + delta, capacity));
            })

            // item.has_definition() → true if this is a polyfills:data_item-backed item with an
            // items/*.json ItemDefinition (as opposed to a plain vanilla item or some other CE
            // item behavior). Lets a script introspect an arbitrary item (e.g. one read out of a
            // slot via Player.get_inventory_slot) before assuming it has tanks/scripts/pages.
            .methodTyped0("has_definition", TypeCodecs.BOOL,
                (ItemStack obj) -> {
                    try {
                        return dev.arubik.craftengine.item.ItemDefinition.byId(ceKey(obj)) != null;
                    } catch (Throwable ignored) { return false; }
                })

            // item.has_script("on_equipped_tick") → true if this item's ItemDefinition declares a
            // script ref for that event.
            .methodTyped1("has_script", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (ItemStack obj, String eventName) -> {
                    try {
                        var def = dev.arubik.craftengine.item.ItemDefinition.byId(ceKey(obj));
                        return def != null && def.script(eventName) != null;
                    } catch (Throwable ignored) { return false; }
                })

            // item.get_script("on_equipped_tick") → the "file.pf:function" ref, or "" if this item
            // has no definition or doesn't declare that event.
            .methodTyped1("get_script", TypeCodecs.STRING, TypeCodecs.STRING, "",
                (ItemStack obj, String eventName) -> {
                    try {
                        var def = dev.arubik.craftengine.item.ItemDefinition.byId(ceKey(obj));
                        if (def == null) return "";
                        String ref = def.script(eventName);
                        return ref != null ? ref : "";
                    } catch (Throwable ignored) { return ""; }
                })

            // item.update() → re-renders this item's display name/lore from its ItemDefinition's
            // "name"/"lore" templates (MiniMessage, "${expr}" inline scripts, or a bare
            // "script.pf:func" call — see TextTemplate), a no-op if it declares neither. Scripts call
            // this after changing state that a template reads (e.g. item.with_typed/set_tank) to make
            // the change visible — there is no automatic re-render, since a plain data component
            // write has no hook of its own to piggyback on.
            // RAW return — this returns a rebuilt Item (ScriptValue.ofItem), no native return type
            // to pin it to; 0 args (args isn't consulted by the original body either).
            .methodTyped0("update", TypeCodecs.RAW,
                (ItemStack self) -> {
                try {
                    var def = dev.arubik.craftengine.item.ItemDefinition.byId(ceKey(self));
                    if (def == null || (def.nameTemplate() == null && def.loreTemplate().isEmpty())) {
                        return ScriptValue.ofItem(self);
                    }
                    ItemStack result = self.copy();
                    dev.arubik.craftengine.script.ScriptContext ctx =
                            dev.arubik.craftengine.script.ScriptContext.builder().item("item", self).build();
                    if (def.nameTemplate() != null) {
                        String raw = dev.arubik.craftengine.script.TextTemplate.evaluateRaw(def.nameTemplate(), ctx);
                        if (raw != null) {
                            net.kyori.adventure.text.Component adv =
                                    net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(raw);
                            result.set(DataComponents.CUSTOM_NAME,
                                    (net.minecraft.network.chat.Component) io.papermc.paper.adventure.PaperAdventure.asVanilla(adv));
                        }
                    }
                    if (!def.loreTemplate().isEmpty()) {
                        java.util.List<String> rawLines = dev.arubik.craftengine.script.TextTemplate.evaluateLoreRaw(def.loreTemplate(), ctx);
                        java.util.List<net.minecraft.network.chat.Component> lines = new ArrayList<>();
                        for (String line : rawLines) {
                            net.kyori.adventure.text.Component adv =
                                    net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(line != null ? line : "");
                            lines.add((net.minecraft.network.chat.Component) io.papermc.paper.adventure.PaperAdventure.asVanilla(adv));
                        }
                        result.set(DataComponents.LORE, new net.minecraft.world.item.component.ItemLore(lines));
                    }
                    return ScriptValue.ofItem(result);
                } catch (Throwable ignored) { return ScriptValue.ofItem(self); }
                });
    }

    /** MiniMessage text → NMS Component with italics defaulted off (the convention every menu
     *  icon/name/lore in this codebase already uses — otherwise a plain-text name/lore line
     *  renders italic, vanilla's default for a custom name). Falls back to a literal Component on
     *  bad MiniMessage syntax rather than dropping the text. */
    private static net.minecraft.network.chat.Component toDisplayComponent(String text) {
        try {
            net.kyori.adventure.text.Component adv = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                    .deserialize(text == null ? "" : text)
                    .decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false);
            return (net.minecraft.network.chat.Component) io.papermc.paper.adventure.PaperAdventure.asVanilla(adv);
        } catch (Throwable ignored) {
            return net.minecraft.network.chat.Component.literal(text == null ? "" : text);
        }
    }

    /** Convert a ScriptValue to a JSON-compatible primitive for two-arg with_component. */
    private static Object toPrimitive(ScriptValue v) {
        if (v instanceof ScriptValue.Num n)  return n.value();
        if (v instanceof ScriptValue.Bool b) return b.value();
        if (v instanceof ScriptValue.Str s)  return s.value();
        return v.asStr();
    }

    /** The ROOT CAUSE of a whole class of "this Item value looks empty/wrong everywhere except
     *  method chaining" bugs (e.g. GeneratedPageContent#customIcon rendering a real head/skull item
     *  as its string-icon fallback, i.e. paper): {@code ScriptFormula#memberCall}/{@code memberGet}
     *  dispatch methods identically for {@link ScriptValue.Item} and a plain {@code ScriptValue.Obj}
     *  tagged "Item" (converting the latter internally), so a caller doing `Item.create(...).foo()`
     *  never notices which one this returns — but every OTHER piece of code that pattern-matches
     *  `instanceof ScriptValue.Item` (this class's own {@code is_item}/{@code to_item}/emptiness
     *  checks in ScriptFormula, and consumers outside this package like GeneratedPageContent) only
     *  recognizes the real record, not the lookalike Obj. Use {@link ScriptValue#ofItem} so a value
     *  built here is indistinguishable from one built anywhere else in this codebase. */
    public static ScriptValue wrap(ItemStack stack) {
        return ScriptValue.ofItem(stack);
    }

    private static ItemStack stack(Object obj) { return (ItemStack) obj; }

    /** Pulls a real Bukkit {@link org.bukkit.entity.Player} out of a wrapped NMS {@code Player}/
     *  {@code Entity} script value — the same "wrapped instance -> live Bukkit entity" idiom used
     *  throughout this codebase via {@code getBukkitEntity()}. */
    private static org.bukkit.entity.Player extractBukkitPlayer(ScriptValue v) {
        if (!(v instanceof ScriptValue.Obj o) || !(o.instance() instanceof net.minecraft.world.entity.Entity nmsEntity)) {
            return null;
        }
        org.bukkit.entity.Entity bukkit = nmsEntity.getBukkitEntity();
        return bukkit instanceof org.bukkit.entity.Player p ? p : null;
    }

    /** {@code with_profile}'s target resolver — a wrapped online Player/Entity value first; else a
     *  UUID string resolves via {@code Bukkit.getOfflinePlayer(UUID)} (safe — just wraps the id,
     *  no network call); else a plain name resolves via {@code getOfflinePlayerIfCached(name)}
     *  ONLY (never the deprecated {@code getOfflinePlayer(String)}, which can silently BLOCK the
     *  main thread with a Mojang API call for a name this server has never seen before — a real
     *  hazard at any real player count). A name that isn't cached simply yields no profile (a
     *  plain default skin) rather than risking that stall — store the UUID at creation time (see
     *  {@code warps.pf#create_warp}'s {@code Player.uuid}) if a target needs to resolve reliably
     *  even before anyone else has ever seen that name on this server. */
    private static org.bukkit.OfflinePlayer resolveProfileTarget(ScriptValue v) {
        org.bukkit.entity.Player online = extractBukkitPlayer(v);
        if (online != null) return online;
        String raw = v.asStr();
        if (raw == null || raw.isBlank()) return null;
        try {
            return org.bukkit.Bukkit.getOfflinePlayer(java.util.UUID.fromString(raw));
        } catch (IllegalArgumentException notAUuid) {
            return org.bukkit.Bukkit.getOfflinePlayerIfCached(raw);
        } catch (Throwable ignored) {
            return null;
        }
    }

    /** Reads one {@link dev.arubik.craftengine.util.NbtType}-primitive value out of an item's raw
     *  NMS custom-data tag (or null if absent) — the item-side counterpart of PersistentBlockEntity's
     *  own typed getters, which operate on CraftEngine's own (different) CompoundTag class instead
     *  of NMS's. Returns the RAW Java primitive; {@link dev.arubik.craftengine.script.TypedKeyBridge.Codec#fromStorage}
     *  turns that into a script value (so a custom codec like "item"/"vector" can decode it too). */
    private static Object readRaw(CompoundTag tag, String key, dev.arubik.craftengine.util.NbtType type) {
        return switch (type) {
            case BYTE -> tag.getByte(key).orElse(null);
            case SHORT -> tag.getShortOr(key, (short) 0);
            case INTEGER -> tag.getIntOr(key, 0);
            case LONG -> tag.getLong(key).orElse(null);
            case FLOAT -> tag.getFloatOr(key, 0f);
            case DOUBLE -> tag.getDoubleOr(key, 0.0);
            case STRING -> tag.getStringOr(key, null);
            case BOOLEAN -> tag.getBooleanOr(key, false);
            case BYTE_ARRAY -> tag.getByteArray(key).orElse(null);
            case INTEGER_ARRAY -> tag.getIntArray(key).orElse(null);
            case LONG_ARRAY -> tag.getLongArray(key).orElse(null);
        };
    }

    /** Writes one raw primitive into an item's raw NMS custom-data tag (mutates {@code tag} in
     *  place — caller re-sets the CUSTOM_DATA component from it, keeping items' copy-on-write
     *  convention). {@code v} is whatever {@link dev.arubik.craftengine.script.TypedKeyBridge.Codec#toStorage} produced. */
    private static void writeRaw(CompoundTag tag, String key, dev.arubik.craftengine.util.NbtType type, Object v) {
        switch (type) {
            case BYTE -> tag.putByte(key, (Byte) v);
            case SHORT -> tag.putShort(key, (Short) v);
            case INTEGER -> tag.putInt(key, (Integer) v);
            case LONG -> tag.putLong(key, (Long) v);
            case FLOAT -> tag.putFloat(key, (Float) v);
            case DOUBLE -> tag.putDouble(key, (Double) v);
            case STRING -> tag.putString(key, (String) v);
            case BOOLEAN -> tag.putBoolean(key, (Boolean) v);
            case BYTE_ARRAY -> tag.putByteArray(key, (byte[]) v);
            case INTEGER_ARRAY -> tag.putIntArray(key, (int[]) v);
            case LONG_ARRAY -> tag.putLongArray(key, (long[]) v);
        }
    }

    /** Public, non-script-value entry point onto the SAME {@code "tkey_"+name} store {@code
     *  get_typed}/{@code with_typed} use — for Java-side bridging (see {@code ItemDefinition
     *  #bridgeTyped}, {@code DataItemBehavior#fillMachineContainer}, {@code MachineType.to_item})
     *  that needs the raw stored primitive without a ScriptValue round trip. Null if absent. */
    public static Object readTypedRaw(ItemStack stack, String name, dev.arubik.craftengine.util.NbtType type) {
        try {
            var cd = stack.get(DataComponents.CUSTOM_DATA);
            if (cd == null) return null;
            // getUnsafe(), not copyTag() — this is a READ-ONLY path (readRaw never mutates the
            // tag it's given), so the defensive deep-copy copyTag() normally does to protect
            // against accidental mutation is pure waste here. Matters because a typed-key read is
            // exactly the kind of thing a script calls constantly (a per-tick flag/counter check),
            // not just at save time.
            return readRaw(cd.getUnsafe(), TYPED_PREFIX + name, type);
        } catch (Throwable ignored) { return null; }
    }

    /** Write counterpart of {@link #readTypedRaw} — returns a NEW copy (items are copy-on-write). */
    public static ItemStack writeTypedRaw(ItemStack stack, String name, dev.arubik.craftengine.util.NbtType type, Object value) {
        try {
            ItemStack copy = stack.copy();
            var cd = copy.getOrDefault(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY);
            CompoundTag tag = cd.copyTag();
            writeRaw(tag, TYPED_PREFIX + name, type, value);
            copy.set(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
            return copy;
        } catch (Throwable ignored) { return stack; }
    }

    /** Shared read path for every TypedKeyBridge-backed accessor (get_typed) — {@code storageKey}
     *  is the FULL prefixed key ("tkey_foo", ...). */
    private static ScriptValue readTyped(ItemStack self, String storageKey, dev.arubik.craftengine.script.TypedKeyBridge.Codec codec) {
        try {
            var cd = self.get(DataComponents.CUSTOM_DATA);
            if (cd == null) return codec.fromStorage(null);
            // getUnsafe(), not copyTag() — read-only path, see readTypedRaw's identical note.
            return codec.fromStorage(readRaw(cd.getUnsafe(), storageKey, codec.storage()));
        } catch (Throwable ignored) { return codec.fromStorage(null); }
    }

    private static boolean hasTyped(ItemStack self, String storageKey) {
        try {
            var cd = self.get(DataComponents.CUSTOM_DATA);
            return cd != null && cd.getUnsafe().contains(storageKey);
        } catch (Throwable ignored) { return false; }
    }

    /** Shared write path — returns a NEW copy (items are copy-on-write, same idiom as with_component). */
    private static ItemStack writeTyped(ItemStack self, String storageKey, dev.arubik.craftengine.script.TypedKeyBridge.Codec codec, ScriptValue value) {
        try {
            ItemStack copy = self.copy();
            var cd = copy.getOrDefault(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY);
            CompoundTag tag = cd.copyTag();
            writeRaw(tag, storageKey, codec.storage(), codec.toStorage(value));
            copy.set(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
            return copy;
        } catch (Throwable ignored) { return self; }
    }

    /** This item's CraftEngine custom-item key, for looking up its {@code ItemDefinition}. */
    private static net.momirealms.craftengine.core.util.Key ceKey(ItemStack nms) {
        String id = customItemId(nms);
        return id != null ? net.momirealms.craftengine.core.util.Key.of(id) : null;
    }

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
