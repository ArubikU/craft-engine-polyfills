package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import net.minecraft.world.level.Level;

public class GasOutput implements RecipeOutput {
    private final GasStack stack;
    private final float chance;

    public GasOutput(GasStack stack, float chance) {
        this.stack = stack;
        this.chance = chance;
    }

    public GasOutput(GasStack stack) {
        this(stack, 1.0f);
    }

    @Override
    public void dispense(Level level, AbstractMachineBlockEntity machine) {
        machine.fillGasTank(level, stack.copy());
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
