/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 */
package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.machine.recipe.RecipeInput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TagInput
implements RecipeInput {
    private final TagKey<Item> tag;
    private final int amount;

    public TagInput(TagKey<Item> tag, int amount) {
        this.tag = tag;
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
        return stack.is(this.tag);
    }

    public TagKey<Item> getTag() {
        return this.tag;
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

