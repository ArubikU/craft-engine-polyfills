package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import net.minecraft.world.level.Level;

public class XpOutput implements RecipeOutput {
    private final float amount;

    public XpOutput(float amount) {
        this.amount = amount;
    }

    @Override
    public void dispense(Level level, AbstractMachineBlockEntity machine) {
        machine.addXp(amount);
    }

    @Override
    public boolean isEmpty() {
        return amount <= 0;
    }

    @Override
    public Object getOutput() {
        return amount;
    }
}
