/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.bukkit.item.BukkitItemDefinition
 *  org.bukkit.inventory.ItemStack
 */
package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.machine.recipe.RecipeInput;
import net.minecraft.world.item.ItemStack;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;

public class CraftEngineItemInput
implements RecipeInput {
    private final String itemId;
    private final int amount;

    public CraftEngineItemInput(String itemId, int amount) {
        this.itemId = itemId;
        this.amount = amount;
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (stack.getCount() < this.amount) {
            return false;
        }
        BukkitItemDefinition ceItem = CraftEngineItems.byItemStack((org.bukkit.inventory.ItemStack)stack.asBukkitMirror());
        if (ceItem == null) {
            return false;
        }
        return ceItem.id().toString().equals(this.itemId);
    }

    public String getItemId() {
        return this.itemId;
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public boolean isEmpty() {
        return this.amount <= 0;
    }
}

