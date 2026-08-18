/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package dev.arubik.craftengine.machine.block.entity;

import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.bar.BarDefinition;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.CraftEngineItemInput;
import dev.arubik.craftengine.machine.recipe.ItemInput;
import dev.arubik.craftengine.machine.recipe.ItemOutput;
import dev.arubik.craftengine.machine.recipe.RecipeInput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.recipe.TagInput;
import dev.arubik.craftengine.machine.recipe.loader.RecipeManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public final class DataMachineSupport {
    private DataMachineSupport() {
    }

    public static boolean isItemInput(RecipeInput input) {
        return input instanceof ItemInput || input instanceof CraftEngineItemInput || input instanceof TagInput;
    }

    public static int matchingInputSlot(AbstractMachineBlockEntity machine, MachineDefinition definition, AbstractProcessingRecipe recipe) {
        boolean wantsItem = false;
        for (RecipeInput input : recipe.getInputs()) {
            if (!DataMachineSupport.isItemInput(input)) continue;
            wantsItem = true;
        }
        int[] inputs = definition.inputSlots();
        if (!wantsItem) {
            return inputs.length > 0 ? inputs[0] : 0;
        }
        for (int slot : inputs) {
            ItemStack stack = machine.getItem(slot);
            if (stack == null || stack.isEmpty()) continue;
            boolean all = true;
            for (RecipeInput input : recipe.getInputs()) {
                if (!DataMachineSupport.isItemInput(input) || input.matches(stack) && stack.getCount() >= input.getAmount()) continue;
                all = false;
                break;
            }
            if (!all) continue;
            return slot;
        }
        return -1;
    }

    public static AbstractProcessingRecipe matchingRecipe(AbstractMachineBlockEntity machine, MachineDefinition definition, String machineId) {
        for (AbstractProcessingRecipe recipe : RecipeManager.getRecipes(machineId)) {
            if (DataMachineSupport.matchingInputSlot(machine, definition, recipe) < 0) continue;
            return recipe;
        }
        return null;
    }

    public static boolean canFitOutput(AbstractMachineBlockEntity machine, MachineDefinition definition, RecipeOutput output) {
        if (!(output instanceof ItemOutput)) {
            return true;
        }
        ItemOutput itemOutput = (ItemOutput)output;
        ItemStack produced = (ItemStack)itemOutput.getOutput();
        for (int slot : definition.outputSlots()) {
            ItemStack current = machine.getItem(slot);
            if (current == null || current.isEmpty()) {
                return true;
            }
            if (!ItemStack.isSameItem((ItemStack)current, (ItemStack)produced) || current.getCount() + produced.getCount() > current.getMaxStackSize()) continue;
            return true;
        }
        return false;
    }

    public static void consumeInputs(AbstractMachineBlockEntity machine, MachineDefinition definition, AbstractProcessingRecipe recipe) {
        int slot = DataMachineSupport.matchingInputSlot(machine, definition, recipe);
        if (slot < 0) {
            return;
        }
        for (RecipeInput input : recipe.getInputs()) {
            if (!DataMachineSupport.isItemInput(input)) continue;
            machine.removeItem(slot, input.getAmount());
        }
    }

    public static List<MachineBar> resolveBars(MachineDefinition definition, List<MachineBar> fallback) {
        if (definition.bars().isEmpty()) {
            return fallback;
        }
        ArrayList<MachineBar> resolved = new ArrayList<MachineBar>();
        for (MachineDefinition.BarRef ref : definition.bars()) {
            BarDefinition def = BarDefinition.REGISTRY.get(ref.bar());
            if (def == null) continue;
            resolved.add(def.toBar(ref.slots()));
        }
        return resolved.isEmpty() ? fallback : resolved;
    }
}

