/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.recipe.FluidInput;
import dev.arubik.craftengine.machine.recipe.FluidOutput;
import dev.arubik.craftengine.machine.recipe.GasInput;
import dev.arubik.craftengine.machine.recipe.GasOutput;
import dev.arubik.craftengine.machine.recipe.RecipeInput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.recipe.condition.RecipeCondition;
import java.util.ArrayList;
import java.util.List;

public class AbstractProcessingRecipe {
    protected final List<RecipeInput> inputs;
    protected final List<RecipeOutput> outputs;
    protected final int processTime;
    protected boolean fuelRequired = true;
    protected boolean requireOverclocked = false;
    protected final List<RecipeCondition> conditions;
    protected int minRpm = 0;
    protected int suCost = 0;

    public int getMinRpm() {
        return this.minRpm;
    }

    public int getSuCost() {
        return this.suCost;
    }

    public AbstractProcessingRecipe setMechanical(int minRpm, int suCost) {
        this.minRpm = Math.max(0, minRpm);
        this.suCost = Math.max(0, suCost);
        return this;
    }

    public AbstractProcessingRecipe(List<RecipeInput> inputs, List<RecipeOutput> outputs, int processTime) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.processTime = processTime;
        this.requireOverclocked = false;
        this.conditions = new ArrayList<RecipeCondition>();
    }

    public AbstractProcessingRecipe(List<RecipeInput> inputs2, List<RecipeOutput> outputs2, int processTime2, boolean fuelRequired2, boolean requireOverclocked2, List<RecipeCondition> conditions2) {
        this.inputs = inputs2;
        this.outputs = outputs2;
        this.processTime = processTime2;
        this.fuelRequired = fuelRequired2;
        this.requireOverclocked = requireOverclocked2;
        this.conditions = conditions2;
    }

    public List<RecipeInput> getInputs() {
        return this.inputs;
    }

    public List<RecipeOutput> getOutputs() {
        return this.outputs;
    }

    public int getProcessTime() {
        return this.processTime;
    }

    public AbstractProcessingRecipe addCondition(RecipeCondition condition) {
        this.conditions.add(condition);
        return this;
    }

    public List<RecipeCondition> getConditions() {
        return this.conditions;
    }

    public AbstractProcessingRecipe setFuelRequired(boolean fuelRequired) {
        this.fuelRequired = fuelRequired;
        return this;
    }

    public boolean isFuelRequired() {
        return this.fuelRequired;
    }

    public AbstractProcessingRecipe setRequireOverclocked(boolean requireOverclocked) {
        this.requireOverclocked = requireOverclocked;
        return this;
    }

    public boolean isRequireOverclocked() {
        return this.requireOverclocked;
    }

    public static AbstractProcessingRecipeBuilder builder() {
        return new AbstractProcessingRecipeBuilder();
    }

    public static class AbstractProcessingRecipeBuilder {
        protected final List<RecipeInput> inputs = new ArrayList<RecipeInput>();
        protected final List<RecipeOutput> outputs = new ArrayList<RecipeOutput>();
        protected int processTime;
        protected boolean fuelRequired = true;
        protected boolean requireOverclocked = false;
        protected final List<RecipeCondition> conditions = new ArrayList<RecipeCondition>();

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

        public AbstractProcessingRecipeBuilder addFluidInput(FluidType fluidType, int amount) {
            this.inputs.add(new FluidInput(FluidStack.of(fluidType, amount), false));
            return this;
        }

        public AbstractProcessingRecipeBuilder addFluidOutput(FluidType fluidType, int amount) {
            this.outputs.add(new FluidOutput(FluidStack.of(fluidType, amount), 1.0f));
            return this;
        }

        public AbstractProcessingRecipeBuilder addGasInput(GasType gasType, int amount) {
            this.inputs.add(new GasInput(GasStack.of(gasType, amount)));
            return this;
        }

        public AbstractProcessingRecipeBuilder addGasOutput(GasType gasType, int amount) {
            this.outputs.add(new GasOutput(GasStack.of(gasType, amount), 1.0f));
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

        public AbstractProcessingRecipeBuilder setRequireOverclocked(boolean requireOverclocked) {
            this.requireOverclocked = requireOverclocked;
            return this;
        }

        public AbstractProcessingRecipeBuilder addCondition(RecipeCondition condition) {
            this.conditions.add(condition);
            return this;
        }

        public AbstractProcessingRecipe build() {
            return new AbstractProcessingRecipe(this.inputs, this.outputs, this.processTime, this.fuelRequired, this.requireOverclocked, this.conditions);
        }
    }
}

