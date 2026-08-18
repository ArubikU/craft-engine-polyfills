/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 */
package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import net.minecraft.world.level.Level;

public interface RecipeOutput {
    public void dispense(Level var1, AbstractMachineBlockEntity var2);

    public Object getOutput();

    default public float getChance() {
        return 1.0f;
    }

    public boolean isEmpty();
}

