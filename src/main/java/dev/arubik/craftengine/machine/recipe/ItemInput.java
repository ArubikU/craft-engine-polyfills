/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.machine.recipe.RecipeInput;
import net.minecraft.world.item.ItemStack;

public class ItemInput
implements RecipeInput {
    private final ItemStack required;
    private final boolean exactNbt;

    public ItemInput(ItemStack required, boolean exactNbt) {
        this.required = required;
        this.exactNbt = exactNbt;
    }

    public ItemInput(ItemStack required) {
        this(required, false);
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (stack.getCount() < this.required.getCount()) {
            return false;
        }
        if (!stack.is(this.required.getItem())) {
            return false;
        }
        if (this.exactNbt) {
            return ItemStack.isSameItemSameComponents((ItemStack)this.required, (ItemStack)stack);
        }
        return true;
    }

    public ItemStack getStack() {
        return this.required;
    }

    @Override
    public int getAmount() {
        return this.required.getCount();
    }

    @Override
    public boolean isEmpty() {
        return this.required.isEmpty();
    }
}

