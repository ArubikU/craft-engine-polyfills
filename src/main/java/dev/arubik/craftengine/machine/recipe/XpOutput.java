package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import net.minecraft.world.level.Level;

public class XpOutput implements RecipeOutput {
    private final float amount;
    /** Rolled per craft when the recipe gave a provider instead of a flat number. */
    private final dev.arubik.craftengine.data.Amount count;

    public XpOutput(float amount) {
        this(amount, dev.arubik.craftengine.data.Amount.of((int) amount));
    }

    public XpOutput(float amount, dev.arubik.craftengine.data.Amount count) {
        this.amount = amount;
        this.count = count;
    }

    @Override
    public void dispense(Level level, AbstractMachineBlockEntity machine) {
        machine.addXp(count.roll());
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
