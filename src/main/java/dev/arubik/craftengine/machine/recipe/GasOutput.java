/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 */
package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.data.Amount;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import net.minecraft.world.level.Level;

public class GasOutput
implements RecipeOutput {
    private final GasStack stack;
    private final float chance;
    private final Amount count;

    public GasOutput(GasStack stack, float chance) {
        this(stack, chance, Amount.of(stack.getAmount()));
    }

    public GasOutput(GasStack stack, float chance, Amount count) {
        this.stack = stack;
        this.chance = chance;
        this.count = count;
    }

    public GasOutput(GasStack stack) {
        this(stack, 1.0f);
    }

    @Override
    public void dispense(Level level, AbstractMachineBlockEntity machine) {
        int rolled = this.count.roll();
        if (rolled <= 0) {
            return;
        }
        GasStack out = this.stack.copy();
        out.setAmount(rolled);
        machine.fillGasTank(level, out);
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

