package dev.arubik.craftengine.machine.recipe;

public class MachineFuelRecipe {
    private final RecipeInput input;
    private final int burnTime;

    private final RecipeOutput replacement;

    public MachineFuelRecipe(RecipeInput input, int burnTime, RecipeOutput replacement) {
        this.input = input;
        this.burnTime = burnTime;
        this.replacement = replacement;
    }

    public MachineFuelRecipe(RecipeInput input, int burnTime) {
        this(input, burnTime, null);
    }

    public RecipeInput getInput() {
        return input;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public RecipeOutput getReplacement() {
        return replacement;
    }
}
