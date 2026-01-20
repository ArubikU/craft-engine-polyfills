package dev.arubik.craftengine.machine.recipe;

import java.util.List;

import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.recipe.condition.RecipeCondition;

public class AbstractProcessingRecipe {
    protected final List<RecipeInput> inputs;
    protected final List<RecipeOutput> outputs;
    protected final int processTime;
    protected boolean fuelRequired = true;
    protected final List<RecipeCondition> conditions;

    public AbstractProcessingRecipe(List<RecipeInput> inputs, List<RecipeOutput> outputs, int processTime) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.processTime = processTime;
        this.conditions = new java.util.ArrayList<>();
    }

    public AbstractProcessingRecipe(List<RecipeInput> inputs2, List<RecipeOutput> outputs2, int processTime2,
            boolean fuelRequired2, List<RecipeCondition> conditions2) {
        this.inputs = inputs2;
        this.outputs = outputs2;
        this.processTime = processTime2;
        this.fuelRequired = fuelRequired2;
        this.conditions = conditions2;
    }

    public List<RecipeInput> getInputs() {
        return inputs;
    }

    public List<RecipeOutput> getOutputs() {
        return outputs;
    }

    public int getProcessTime() {
        return processTime;
    }

    public AbstractProcessingRecipe addCondition(RecipeCondition condition) {
        this.conditions.add(condition);
        return this;
    }

    public List<RecipeCondition> getConditions() {
        return conditions;
    }

    public AbstractProcessingRecipe setFuelRequired(boolean fuelRequired) {
        this.fuelRequired = fuelRequired;
        return this;
    }

    public boolean isFuelRequired() {
        return fuelRequired;
    }

    public static class AbstractProcessingRecipeBuilder {
        protected final List<RecipeInput> inputs = new java.util.ArrayList<>();
        protected final List<RecipeOutput> outputs = new java.util.ArrayList<>();
        protected int processTime;
        protected boolean fuelRequired = true;
        protected final List<RecipeCondition> conditions = new java.util.ArrayList<>();

        public AbstractProcessingRecipeBuilder addInput(RecipeInput input) {
            this.inputs.add(input);
            return this;
        }

        public AbstractProcessingRecipeBuilder addInputs(List<RecipeInput> inputs) {
            this.inputs.addAll(inputs);
            return this;
        }

        public AbstractProcessingRecipeBuilder addOutputs(List<RecipeOutput> outputs) {
            this.outputs.addAll(outputs);
            return this;
        }

        public AbstractProcessingRecipeBuilder addOutput(RecipeOutput output) {
            this.outputs.add(output);
            return this;
        }

        // * HELPERS *
        public AbstractProcessingRecipeBuilder addFluidInput(FluidType fluidType, int amount) {
            this.inputs.add(new FluidInput(FluidStack.of(fluidType, amount), false));
            return this;
        }

        public AbstractProcessingRecipeBuilder addFluidOutput(FluidType fluidType, int amount) {
            this.outputs.add(new FluidOutput(FluidStack.of(fluidType, amount), 1));
            return this;
        }

        public AbstractProcessingRecipeBuilder addGasInput(GasType gasType, int amount) {
            this.inputs.add(new GasInput(GasStack.of(gasType, amount)));
            return this;
        }

        public AbstractProcessingRecipeBuilder addGasOutput(GasType gasType, int amount) {
            this.outputs.add(new GasOutput(GasStack.of(gasType, amount), 1));
            return this;
        }

        public AbstractProcessingRecipeBuilder setProcessTime(int processTime) {
            this.processTime = processTime;
            return this;
        }

        public AbstractProcessingRecipeBuilder setFuelRequired(boolean fuelRequired) {
            this.fuelRequired = fuelRequired;
            return this;
        }

        public AbstractProcessingRecipeBuilder addCondition(RecipeCondition condition) {
            this.conditions.add(condition);
            return this;
        }

        public AbstractProcessingRecipe build() {
            return new AbstractProcessingRecipe(inputs, outputs, processTime, fuelRequired, conditions);
        }
    }

    public static AbstractProcessingRecipeBuilder builder() {
        return new AbstractProcessingRecipeBuilder();
    }
}
