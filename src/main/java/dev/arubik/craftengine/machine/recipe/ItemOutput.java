/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 */
package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.data.Amount;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemOutput
implements RecipeOutput {
    private final ItemStack stack;
    private final float chance;
    private final Amount count;

    public ItemOutput(ItemStack stack, float chance) {
        this(stack, chance, Amount.of(stack.getCount()));
    }

    public ItemOutput(ItemStack stack, float chance, Amount count) {
        this.stack = stack;
        this.chance = chance;
        this.count = count;
    }

    public ItemOutput(ItemStack stack) {
        this(stack, 1.0f);
    }

    @Override
    public void dispense(Level level, AbstractMachineBlockEntity machine) {
        if (this.chance < 1.0f && ThreadLocalRandom.current().nextFloat() >= this.chance) {
            return;
        }
        int rolled = this.count.roll();
        if (rolled <= 0) {
            return;
        }
        ItemStack out = this.stack.copy();
        out.setCount(rolled);
        machine.addOutput(out);
    }

    public ItemStack getItem() {
        return this.stack;
    }

    @Override
    public boolean isEmpty() {
        return this.stack.isEmpty();
    }

    @Override
    public Object getOutput() {
        return this.stack;
    }

    @Override
    public float getChance() {
        return this.chance;
    }
}

