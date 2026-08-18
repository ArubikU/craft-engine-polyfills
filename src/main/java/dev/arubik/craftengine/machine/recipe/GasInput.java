/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.machine.recipe.RecipeInput;

public class GasInput
implements RecipeInput {
    private final GasStack required;

    public GasInput(GasStack required) {
        this.required = required;
    }

    public GasStack getGas() {
        return this.required;
    }

    @Override
    public int getAmount() {
        return this.required.getAmount();
    }

    @Override
    public boolean isEmpty() {
        return this.required.isEmpty();
    }

    @Override
    public boolean matches(GasStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (stack.getAmount() < this.required.getAmount()) {
            return false;
        }
        return this.required.isGasEqual(stack);
    }
}

