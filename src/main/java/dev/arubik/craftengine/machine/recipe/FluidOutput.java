package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import net.minecraft.world.level.Level;

public class FluidOutput implements RecipeOutput {
    private final FluidStack stack;
    private final float chance;
    /** How many to produce, rolled per craft. See {@link dev.arubik.craftengine.data.Amount}. */
    private final dev.arubik.craftengine.data.Amount count;

    public FluidOutput(FluidStack stack, float chance) {
        this(stack, chance, dev.arubik.craftengine.data.Amount.of(stack.getAmount()));
    }

    public FluidOutput(FluidStack stack, float chance, dev.arubik.craftengine.data.Amount count) {
        this.stack = stack;
        this.chance = chance;
        this.count = count;
    }

    public FluidOutput(FluidStack stack) {
        this(stack, 1.0f);
    }

    @Override
    public void dispense(Level level, AbstractMachineBlockEntity machine) {
        int rolled = count.roll();
        if (rolled <= 0)
            return;
        machine.fillTank(level, new FluidStack(stack.getType(), rolled, stack.getPressure()));
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
