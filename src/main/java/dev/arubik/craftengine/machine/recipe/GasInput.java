package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.gas.GasStack;

public class GasInput implements RecipeInput {
    private final GasStack required;

    public GasInput(GasStack required) {
        this.required = required;
    }

    @Override
    public int getAmount() {
        return required.getAmount();
    }

    @Override
    public boolean isEmpty() {
        return required.isEmpty();
    }

    public boolean matches(GasStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (stack.getAmount() < required.getAmount())
            return false;
        return required.isGasEqual(stack);
    }
}
