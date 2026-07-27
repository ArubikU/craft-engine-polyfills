package dev.arubik.craftengine.machine.block.entity;

import java.util.List;

import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.CraftEngineItemInput;
import dev.arubik.craftengine.machine.recipe.ItemInput;
import dev.arubik.craftengine.machine.recipe.ItemOutput;
import dev.arubik.craftengine.machine.recipe.RecipeInput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.recipe.TagInput;
import dev.arubik.craftengine.machine.recipe.loader.RecipeManager;
import net.minecraft.world.item.ItemStack;

/**
 * The recipe half of a data-defined machine, shared by the single-block and the
 * multiblock entity.
 *
 * <p>
 * Both need identical behaviour but cannot share a superclass: a plain machine
 * extends {@link AbstractMachineBlockEntity} while a multiblock one must extend
 * {@code MultiBlockMachineBlockEntity}, and Java has single inheritance. Putting
 * the logic here keeps one copy rather than letting the two drift.
 */
public final class DataMachineSupport {

    private DataMachineSupport() {
    }

    public static boolean isItemInput(RecipeInput input) {
        return input instanceof ItemInput || input instanceof CraftEngineItemInput || input instanceof TagInput;
    }

    /**
     * The input slot holding the item a recipe wants, or -1.
     *
     * <p>
     * A recipe with no item input (a pure fluid or gas conversion) reports the first
     * input slot so the caller still gets a non-negative answer.
     */
    public static int matchingInputSlot(AbstractMachineBlockEntity machine, MachineDefinition definition,
            AbstractProcessingRecipe recipe) {
        boolean wantsItem = false;
        for (RecipeInput input : recipe.getInputs())
            if (isItemInput(input))
                wantsItem = true;
        int[] inputs = definition.inputSlots();
        if (!wantsItem)
            return inputs.length > 0 ? inputs[0] : 0;

        for (int slot : inputs) {
            ItemStack stack = machine.getItem(slot);
            if (stack == null || stack.isEmpty())
                continue;
            boolean all = true;
            for (RecipeInput input : recipe.getInputs()) {
                if (!isItemInput(input))
                    continue;
                if (!input.matches(stack) || stack.getCount() < input.getAmount()) {
                    all = false;
                    break;
                }
            }
            if (all)
                return slot;
        }
        return -1;
    }

    /** The first recipe of this machine's type whose inputs are present. */
    public static AbstractProcessingRecipe matchingRecipe(AbstractMachineBlockEntity machine,
            MachineDefinition definition, String machineId) {
        for (AbstractProcessingRecipe recipe : RecipeManager.getRecipes(machineId))
            if (matchingInputSlot(machine, definition, recipe) >= 0)
                return recipe;
        return null;
    }

    /** Whether an item output has room; fluid/gas/xp check their own tanks when dispensed. */
    public static boolean canFitOutput(AbstractMachineBlockEntity machine, MachineDefinition definition,
            RecipeOutput output) {
        if (!(output instanceof ItemOutput itemOutput))
            return true;
        ItemStack produced = (ItemStack) itemOutput.getOutput();
        for (int slot : definition.outputSlots()) {
            ItemStack current = machine.getItem(slot);
            if (current == null || current.isEmpty())
                return true;
            if (ItemStack.isSameItem(current, produced)
                    && current.getCount() + produced.getCount() <= current.getMaxStackSize())
                return true;
        }
        return false;
    }

    public static void consumeInputs(AbstractMachineBlockEntity machine, MachineDefinition definition,
            AbstractProcessingRecipe recipe) {
        int slot = matchingInputSlot(machine, definition, recipe);
        if (slot < 0)
            return;
        for (RecipeInput input : recipe.getInputs())
            if (isItemInput(input))
                machine.removeItem(slot, input.getAmount());
    }

    /** Gauges the definition declares, resolved against {@code bars/*.json}. */
    public static List<dev.arubik.craftengine.machine.menu.bar.MachineBar> resolveBars(
            MachineDefinition definition,
            List<dev.arubik.craftengine.machine.menu.bar.MachineBar> fallback) {
        if (definition.bars().isEmpty())
            return fallback;
        List<dev.arubik.craftengine.machine.menu.bar.MachineBar> resolved = new java.util.ArrayList<>();
        for (MachineDefinition.BarRef ref : definition.bars()) {
            var def = dev.arubik.craftengine.machine.menu.bar.BarDefinition.REGISTRY.get(ref.bar());
            if (def != null)
                resolved.add(def.toBar(ref.slots()));
        }
        return resolved.isEmpty() ? fallback : resolved;
    }
}
