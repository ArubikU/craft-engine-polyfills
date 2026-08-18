/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.machine.recipe.RecipeInput;

public class FluidInput
implements RecipeInput {
    private final FluidStack required;
    private final boolean exactNbt;

    public FluidInput(FluidStack required, boolean exactNbt) {
        this.required = required;
        this.exactNbt = exactNbt;
    }

    @Override
    public boolean matches(FluidStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (stack.getAmount() < this.required.getAmount()) {
            return false;
        }
        return this.required.isFluidEqual(stack);
    }

    public FluidStack getFluid() {
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
}

