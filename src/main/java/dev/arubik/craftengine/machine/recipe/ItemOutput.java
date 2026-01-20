package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemOutput implements RecipeOutput {
    private final ItemStack stack;
    private final float chance;

    public ItemOutput(ItemStack stack, float chance) {
        this.stack = stack;
        this.chance = chance;
    }

    public ItemOutput(ItemStack stack) {
        this(stack, 1.0f);
    }

    @Override
    public void dispense(Level level, AbstractMachineBlockEntity machine) {
        machine.addOutput(stack.copy());
    }

    public ItemStack getItem() {
        return stack;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public Object getOutput() {
        return stack;
    }
}
