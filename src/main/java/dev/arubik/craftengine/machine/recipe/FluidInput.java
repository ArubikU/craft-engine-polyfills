package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.fluid.FluidStack;

public class FluidInput implements RecipeInput {
    private final FluidStack required;
    private final boolean exactNbt;

    public FluidInput(FluidStack required, boolean exactNbt) {
        this.required = required;
        this.exactNbt = exactNbt;
    } // No default constructor with only FluidStack to avoid ambiguity if expanded
      // later, kept simple.

    @Override
    public boolean matches(FluidStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (stack.getAmount() < required.getAmount())
            return false;

        return required.isFluidEqual(stack); // Assumes isFluidEqual checks Type + NBT (usually)
        // If isFluidEqual only checks type, we might need specific logic.
        // Assuming standard FluidStack behavior: matches Type and Tag/NBT.
    }

    @Override
    public int getAmount() {
        return required.getAmount();
    }

    @Override
    public boolean isEmpty() {
        return required.isEmpty();
    }
}
