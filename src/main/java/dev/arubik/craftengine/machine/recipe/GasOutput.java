package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import net.minecraft.world.level.Level;

public class GasOutput implements RecipeOutput {
    private final GasStack stack;
    private final float chance;
    /** How many to produce, rolled per craft. See {@link dev.arubik.craftengine.data.Amount}. */
    private final dev.arubik.craftengine.data.Amount count;

    public GasOutput(GasStack stack, float chance) {
        this(stack, chance, dev.arubik.craftengine.data.Amount.of(stack.getAmount()));
    }

    public GasOutput(GasStack stack, float chance, dev.arubik.craftengine.data.Amount count) {
        this.stack = stack;
        this.chance = chance;
        this.count = count;
    }

    public GasOutput(GasStack stack) {
        this(stack, 1.0f);
    }

    @Override
    public void dispense(Level level, AbstractMachineBlockEntity machine) {
        int rolled = count.roll();
        if (rolled <= 0)
            return;
        var out = stack.copy();
        out.setAmount(rolled);
        machine.fillGasTank(level, out);
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
