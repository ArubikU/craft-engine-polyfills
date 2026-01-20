package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import net.minecraft.world.level.Level;

public class FluidOutput implements RecipeOutput {
    private final FluidStack stack;
    private final float chance;

    public FluidOutput(FluidStack stack, float chance) {
        this.stack = stack;
        this.chance = chance;
    }

    public FluidOutput(FluidStack stack) {
        this(stack, 1.0f);
    }

    @Override
    public void dispense(Level level, AbstractMachineBlockEntity machine) {
        machine.fillTank(level, stack.copy());
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
