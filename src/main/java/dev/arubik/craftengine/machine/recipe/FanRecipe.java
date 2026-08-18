/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.FanProcess;
import dev.arubik.craftengine.machine.recipe.RecipeInput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class FanRecipe
extends AbstractProcessingRecipe {
    private final FanProcess requiredProcess;
    private final GasType requiredGas;

    public FanRecipe(List<RecipeInput> inputs, List<RecipeOutput> outputs, int processTime, FanProcess requiredProcess, GasType requiredGas) {
        super(inputs, outputs, processTime);
        this.requiredProcess = requiredProcess == null ? FanProcess.NONE : requiredProcess;
        this.requiredGas = requiredGas;
    }

    public FanProcess getRequiredProcess() {
        return this.requiredProcess;
    }

    public GasType getRequiredGas() {
        return this.requiredGas;
    }

    private RecipeInput firstItemInput() {
        for (RecipeInput in : this.inputs) {
            if (in == null || in.isEmpty()) continue;
            return in;
        }
        return null;
    }

    public int inputAmount() {
        RecipeInput in = this.firstItemInput();
        return in == null ? 1 : Math.max(1, in.getAmount());
    }

    public boolean matches(ItemStack stack, FanProcess family, GasType gas) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (this.requiredProcess != FanProcess.NONE && this.requiredProcess != family) {
            return false;
        }
        if (this.requiredGas != null && this.requiredGas != GasType.EMPTY && this.requiredGas != gas) {
            return false;
        }
        RecipeInput in = this.firstItemInput();
        return in != null && in.matches(stack);
    }
}

