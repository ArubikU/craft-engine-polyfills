package dev.arubik.craftengine.machine.recipe;

import net.minecraft.world.item.ItemStack;

public class ItemInput implements RecipeInput {
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
        if (stack == null || stack.isEmpty())
            return false;
        if (stack.getCount() < required.getCount())
            return false;

        if (!stack.is(required.getItem()))
            return false;

        if (exactNbt) {
            return ItemStack.isSameItemSameComponents(required, stack);
        }
        return true;
    }

    @Override
    public int getAmount() {
        return required.getCount();
    }

    @Override
    public boolean isEmpty() {
        return required.isEmpty();
    }
}
