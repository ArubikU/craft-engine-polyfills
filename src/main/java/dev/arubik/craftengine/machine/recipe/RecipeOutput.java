package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import net.minecraft.world.level.Level;

public interface RecipeOutput {
    /**
     * Dispenses this output into the machine (addToInventory, fillTank, drop, or
     * grant XP).
     * 
     * @param machine The machine processing the recipe.
     */
    void dispense(Level level, AbstractMachineBlockEntity machine);

    Object getOutput();

    /**
     * Gets the chance (0.0 to 1.0) of this output being generated.
     */
    default float getChance() {
        return 1.0f;
    }

    boolean isEmpty();
}
