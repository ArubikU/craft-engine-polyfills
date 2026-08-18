/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.machine.recipe.RecipeInput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;

public class MachineFuelRecipe {
    private final RecipeInput input;
    private final int burnTime;
    private final RecipeOutput replacement;
    private final int overclockedTime;

    public MachineFuelRecipe(RecipeInput input, int burnTime, RecipeOutput replacement, int overclockedTime) {
        this.input = input;
        this.burnTime = burnTime;
        this.replacement = replacement;
        this.overclockedTime = overclockedTime;
    }

    public MachineFuelRecipe(RecipeInput input, int burnTime, RecipeOutput replacement) {
        this(input, burnTime, replacement, 0);
    }

    public MachineFuelRecipe(RecipeInput input, int burnTime) {
        this(input, burnTime, null, 0);
    }

    public RecipeInput getInput() {
        return this.input;
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public RecipeOutput getReplacement() {
        return this.replacement;
    }

    public int getOverclockedTime() {
        return this.overclockedTime;
    }
}

