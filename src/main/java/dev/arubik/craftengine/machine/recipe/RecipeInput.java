package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.gas.GasStack;
import net.minecraft.world.item.ItemStack;

public interface RecipeInput {
    /**
     * Checks if the given item stack matches this input.
     */
    default boolean matches(ItemStack stack) {
        return false;
    }

    /**
     * Checks if the given fluid stack matches this input.
     */
    default boolean matches(FluidStack stack) {
        return false;
    }

    default boolean matches(GasStack stack) {
        return false;
    }

    /**
     * Required amount to match.
     */
    int getAmount();

    /**
     * Returns true if this input is empty/valid.
     */
    boolean isEmpty();
}
