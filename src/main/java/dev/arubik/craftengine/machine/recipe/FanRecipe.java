package dev.arubik.craftengine.machine.recipe;

import java.util.List;

import net.minecraft.world.item.ItemStack;

/**
 * A custom {@code fan} recipe: a single item input transformed by an airflow column over {@code time}
 * ticks into one or more (chance-weighted) outputs. Matching adds two fan-specific gates on top of the
 * normal inputs/conditions:
 * <ul>
 *   <li>{@link #requiredProcess} — the PROCESS FAMILY the airflow must be running (SMELTING / BLASTING /
 *       WASHING), decided by the process block in the column. {@link FanProcess#NONE} = any family.</li>
 *   <li>{@link #requiredGas} — the gas TYPE the fan buffer must hold ({@code "steam"}, {@code "heavy_steam"}
 *       or {@code "any"}). Null/EMPTY/"any" = any gas.</li>
 * </ul>
 *
 * <p>Stored under machine id {@code "fan"} in {@link dev.arubik.craftengine.machine.recipe.loader.RecipeManager}.
 * The fan block entity iterates these directly (it does not use the slot-based {@code processTick}).</p>
 */
public class FanRecipe extends AbstractProcessingRecipe {

    private final FanProcess requiredProcess;
    private final dev.arubik.craftengine.gas.GasType requiredGas; // null = any

    public FanRecipe(List<RecipeInput> inputs, List<RecipeOutput> outputs, int processTime,
            FanProcess requiredProcess, dev.arubik.craftengine.gas.GasType requiredGas) {
        super(inputs, outputs, processTime);
        this.requiredProcess = requiredProcess == null ? FanProcess.NONE : requiredProcess;
        this.requiredGas = requiredGas;
    }

    public FanProcess getRequiredProcess() {
        return requiredProcess;
    }

    public dev.arubik.craftengine.gas.GasType getRequiredGas() {
        return requiredGas;
    }

    /** The first item-like input (item / custom_item / tag), used to match the airflow item. */
    private RecipeInput firstItemInput() {
        for (RecipeInput in : inputs) {
            if (in != null && !in.isEmpty())
                return in;
        }
        return null;
    }

    /** How many of the input item one craft consumes. */
    public int inputAmount() {
        RecipeInput in = firstItemInput();
        return in == null ? 1 : Math.max(1, in.getAmount());
    }

    /**
     * True when this recipe applies to {@code stack} under the given live airflow family + gas type.
     */
    public boolean matches(ItemStack stack, FanProcess family, dev.arubik.craftengine.gas.GasType gas) {
        if (stack == null || stack.isEmpty())
            return false;
        if (requiredProcess != FanProcess.NONE && requiredProcess != family)
            return false;
        if (requiredGas != null && requiredGas != dev.arubik.craftengine.gas.GasType.EMPTY && requiredGas != gas)
            return false;
        RecipeInput in = firstItemInput();
        return in != null && in.matches(stack);
    }
}
