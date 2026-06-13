package dev.arubik.craftengine.crafting;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.util.Key;

/**
 * Bukkit-side bridge between {@code org.bukkit.inventory.ItemStack} and the pure
 * {@link CraftCell} value type the matcher works with.
 *
 * <p>Kept separate from the matching core so that core stays unit-testable on
 * the plain JVM (no Bukkit on the classpath). Only the menu/behavior layer
 * touches this class.
 */
public final class CraftItemAdapter {

    private CraftItemAdapter() {
    }

    /**
     * Resolves a Bukkit stack to a {@link CraftCell}. Custom craft-engine items
     * use their custom id; vanilla items use {@code minecraft:<material>}.
     */
    public static CraftCell toCell(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) {
            return CraftCell.EMPTY;
        }
        Key custom = CraftEngineItems.getCustomItemId(stack);
        if (custom != null) {
            return CraftCell.of(custom, stack.getAmount());
        }
        NamespacedKey nk = stack.getType().getKey();
        return CraftCell.of(Key.of(nk.getNamespace(), nk.getKey()), stack.getAmount());
    }

    /**
     * Builds a Bukkit stack for an output {@link CraftCell}. Custom items are
     * built via the craft-engine item registry; otherwise a vanilla material is
     * resolved. Returns null if the id cannot be resolved to any item.
     */
    public static ItemStack toBukkit(CraftCell cell) {
        if (cell.isEmpty()) {
            return null;
        }
        Key id = cell.id();
        // Try craft-engine custom item first.
        BukkitItemDefinition def = CraftEngineItems.byId(id);
        if (def != null) {
            ItemStack stack = def.buildBukkitItem();
            if (stack != null) {
                stack.setAmount(cell.count());
                return stack;
            }
        }
        // Fall back to a vanilla material.
        if ("minecraft".equals(id.namespace())) {
            Material mat = Material.matchMaterial(id.value());
            if (mat != null) {
                return new ItemStack(mat, cell.count());
            }
        }
        return null;
    }
}
