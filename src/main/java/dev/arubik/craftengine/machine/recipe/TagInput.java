package dev.arubik.craftengine.machine.recipe;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TagInput implements RecipeInput {
    private final TagKey<Item> tag;
    private final int amount;

    public TagInput(TagKey<Item> tag, int amount) {
        this.tag = tag;
        this.amount = amount;
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (stack.getCount() < amount)
            return false;

        return stack.is(tag);
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public boolean isEmpty() {
        return amount <= 0;
    }
}
