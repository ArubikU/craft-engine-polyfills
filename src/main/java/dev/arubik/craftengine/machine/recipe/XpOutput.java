/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 */
package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.data.Amount;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import net.minecraft.world.level.Level;

public class XpOutput
implements RecipeOutput {
    private final float amount;
    private final Amount count;

    public XpOutput(float amount) {
        this(amount, Amount.of((int)amount));
    }

    public XpOutput(float amount, Amount count) {
        this.amount = amount;
        this.count = count;
    }

    @Override
    public void dispense(Level level, AbstractMachineBlockEntity machine) {
        machine.addXp(this.count.roll());
    }

    @Override
    public boolean isEmpty() {
        return this.amount <= 0.0f;
    }

    @Override
    public Object getOutput() {
        return Float.valueOf(this.amount);
    }
}

