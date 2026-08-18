/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 */
package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.data.Amount;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import net.minecraft.world.level.Level;

public class FluidOutput
implements RecipeOutput {
    private final FluidStack stack;
    private final float chance;
    private final Amount count;

    public FluidOutput(FluidStack stack, float chance) {
        this(stack, chance, Amount.of(stack.getAmount()));
    }

    public FluidOutput(FluidStack stack, float chance, Amount count) {
        this.stack = stack;
        this.chance = chance;
        this.count = count;
    }

    public FluidOutput(FluidStack stack) {
        this(stack, 1.0f);
    }

    @Override
    public void dispense(Level level, AbstractMachineBlockEntity machine) {
        int rolled = this.count.roll();
        if (rolled <= 0) {
            return;
        }
        machine.fillTank(level, new FluidStack(this.stack.getType(), rolled, this.stack.getPressure()));
    }

    @Override
    public boolean isEmpty() {
        return this.stack.isEmpty();
    }

    @Override
    public Object getOutput() {
        return this.stack;
    }
}

