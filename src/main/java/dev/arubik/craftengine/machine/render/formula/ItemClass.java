package dev.arubik.craftengine.machine.render.formula;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * {@link PolyClass} wrapping a single Bukkit {@link ItemStack}, exposed when an item
 * value is accessed via a suffix expression such as {@code slot(9).amount} or
 * {@code Workbench.input(0).type}.
 *
 * <h3>Properties</h3>
 * <ul>
 *   <li>{@code amount}        — stack count</li>
 *   <li>{@code type} / {@code id} — namespaced material key ({@code "minecraft:stone"})</li>
 *   <li>{@code material}      — lower-cased material enum name</li>
 *   <li>{@code name}          — display name (plain text) or formatted material name</li>
 *   <li>{@code lore}          — all lore lines joined with {@code \n} (use {@code [n]} to index)</li>
 *   <li>{@code durability}    — remaining durability</li>
 *   <li>{@code max_durability} — maximum durability</li>
 *   <li>{@code is_empty}      — true if stack is null or AIR</li>
 *   <li>{@code is_stackable}  — true if max stack &gt; 1</li>
 *   <li>{@code max_stack}     — max stack size</li>
 *   <li>{@code custom_model_data} — custom model data int, or 0</li>
 * </ul>
 *
 * <h3>Methods</h3>
 * <ul>
 *   <li>{@code lore_line(n)}  — nth lore line as string</li>
 *   <li>{@code has_enchant("id")} — true if the enchantment is present</li>
 *   <li>{@code enchant_level("id")} — level of the enchantment, or 0</li>
 *   <li>{@code matches("id")} — true if item type matches the given material key</li>
 * </ul>
 */
public final class ItemClass implements PolyClass {

    private final ItemStack stack;

    public ItemClass(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public PolyValue get(String property) {
        if (stack == null || stack.getType().isAir()) {
            return switch (property) {
                case "is_empty"   -> PolyValue.of(true);
                case "amount"     -> PolyValue.of(0);
                case "type", "id" -> PolyValue.of("minecraft:air");
                case "ce_id"      -> PolyValue.NULL;
                case "is_ce_item" -> PolyValue.of(false);
                case "material"   -> PolyValue.of("air");
                case "name"       -> PolyValue.of("");
                case "lore"       -> PolyValue.of("");
                default           -> PolyValue.NULL;
            };
        }
        return switch (property) {
            case "amount"        -> PolyValue.of(stack.getAmount());
            case "type", "id"    -> {
                // Prefer the CE custom-item key when available
                try {
                    net.momirealms.craftengine.core.util.Key ceKey =
                            net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(stack);
                    if (ceKey != null) yield PolyValue.of(ceKey.toString());
                } catch (Throwable ignored) {}
                yield PolyValue.of(stack.getType().getKey().toString());
            }
            case "ce_id" -> {
                try {
                    net.momirealms.craftengine.core.util.Key ceKey =
                            net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(stack);
                    if (ceKey != null) yield PolyValue.of(ceKey.toString());
                } catch (Throwable ignored) {}
                yield PolyValue.NULL;
            }
            case "is_ce_item" -> {
                try {
                    yield PolyValue.of(
                            net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(stack) != null);
                } catch (Throwable ignored) {}
                yield PolyValue.of(false);
            }
            case "material"      -> PolyValue.of(stack.getType().name().toLowerCase(java.util.Locale.ROOT));
            case "name"          -> {
                ItemMeta meta = stack.getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    try {
                        yield PolyValue.of(
                                net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
                                        .plainText().serialize(meta.displayName()));
                    } catch (Throwable ignored) {}
                }
                yield PolyValue.of(stack.getType().name().toLowerCase(java.util.Locale.ROOT).replace('_', ' '));
            }
            case "lore"          -> {
                ItemMeta meta = stack.getItemMeta();
                if (meta == null || !meta.hasLore()) { yield PolyValue.of(""); }
                List<net.kyori.adventure.text.Component> lore = meta.lore();
                if (lore == null || lore.isEmpty()) { yield PolyValue.of(""); }
                StringBuilder sb = new StringBuilder();
                try {
                    var plain = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText();
                    for (net.kyori.adventure.text.Component line : lore) {
                        if (sb.length() > 0) sb.append('\n');
                        sb.append(plain.serialize(line));
                    }
                } catch (Throwable ignored) {}
                yield PolyValue.of(sb.toString());
            }
            case "durability"    -> PolyValue.of(stack.getType().getMaxDurability() - getDamage());
            case "max_durability" -> PolyValue.of(stack.getType().getMaxDurability());
            case "is_empty"      -> PolyValue.of(false);
            case "is_stackable"  -> PolyValue.of(stack.getType().getMaxStackSize() > 1);
            case "max_stack"     -> PolyValue.of(stack.getType().getMaxStackSize());
            case "custom_model_data" -> {
                ItemMeta meta = stack.getItemMeta();
                yield (meta != null && meta.hasCustomModelData())
                        ? PolyValue.of(meta.getCustomModelData())
                        : PolyValue.of(0);
            }
            default -> PolyValue.NULL;
        };
    }

    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        if (stack == null || stack.getType().isAir()) {
            return switch (method) {
                case "matches"     -> PolyValue.of(false);
                case "has_enchant" -> PolyValue.of(false);
                case "enchant_level" -> PolyValue.of(0);
                case "is_tagged"   -> PolyValue.of(false);
                default -> PolyValue.NULL;
            };
        }
        return switch (method) {
            case "lore_line" -> {
                int n = args.isEmpty() ? 0 : (int) args.get(0).asNum();
                ItemMeta meta = stack.getItemMeta();
                if (meta == null || !meta.hasLore()) { yield PolyValue.NULL; }
                List<net.kyori.adventure.text.Component> lore = meta.lore();
                if (lore == null || n < 0 || n >= lore.size()) { yield PolyValue.NULL; }
                try {
                    yield PolyValue.of(
                            net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
                                    .plainText().serialize(lore.get(n)));
                } catch (Throwable ignored) { yield PolyValue.NULL; }
            }
            case "has_enchant" -> {
                if (args.isEmpty()) yield PolyValue.of(false);
                org.bukkit.enchantments.Enchantment ench = resolveEnchantment(args.get(0).asStr());
                yield PolyValue.of(ench != null && stack.containsEnchantment(ench));
            }
            case "enchant_level" -> {
                if (args.isEmpty()) yield PolyValue.of(0);
                org.bukkit.enchantments.Enchantment ench = resolveEnchantment(args.get(0).asStr());
                yield PolyValue.of(ench != null ? stack.getEnchantmentLevel(ench) : 0);
            }
            case "matches" -> {
                if (args.isEmpty()) yield PolyValue.of(false);
                String id = args.get(0).asStr();
                org.bukkit.Material mat = org.bukkit.Material.matchMaterial(id);
                yield PolyValue.of(mat != null ? stack.getType() == mat
                        : stack.getType().getKey().toString().equals(id));
            }
            case "is_tagged" -> {
                if (args.isEmpty()) yield PolyValue.of(false);
                String tagId = args.get(0).asStr();
                try {
                    org.bukkit.NamespacedKey key = tagId.contains(":")
                            ? org.bukkit.NamespacedKey.fromString(tagId)
                            : org.bukkit.NamespacedKey.minecraft(tagId);
                    if (key != null) {
                        org.bukkit.Tag<org.bukkit.Material> tag = org.bukkit.Bukkit.getTag(
                                org.bukkit.Tag.REGISTRY_ITEMS, key, org.bukkit.Material.class);
                        if (tag != null) { yield PolyValue.of(tag.isTagged(stack.getType())); }
                    }
                } catch (Throwable ignored) {}
                // TODO: CE item tag check — BukkitItemDefinition.tags() not available yet
                // When the API exposes tags(), re-enable this block.
                yield PolyValue.of(false);
            }
            default -> get(method); // fall back to property
        };
    }

    // ---- helpers ----

    private int getDamage() {
        try {
            if (stack.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable d) {
                return d.getDamage();
            }
        } catch (Throwable ignored) {}
        return 0;
    }

    private static org.bukkit.enchantments.Enchantment resolveEnchantment(String name) {
        try {
            String key = name.contains(":") ? name : "minecraft:" + name;
            return org.bukkit.enchantments.Enchantment.getByKey(
                    org.bukkit.NamespacedKey.fromString(key));
        } catch (Throwable ignored) {
            return null;
        }
    }
}
