/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.gas.GasStack;
import net.minecraft.world.item.ItemStack;

public interface RecipeInput {
    default public boolean matches(ItemStack stack) {
        return false;
    }

    default public boolean matches(FluidStack stack) {
        return false;
    }

    default public boolean matches(GasStack stack) {
        return false;
    }

    public int getAmount();

    public boolean isEmpty();
}

