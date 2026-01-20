package dev.arubik.craftengine.machine.recipe.condition;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;

public interface RecipeCondition {
    /**
     * Tests if the condition is met for the given machine.
     * 
     * @param machine The machine instance.
     * @return true if the condition is met.
     */
    boolean test(net.minecraft.world.level.Level level, AbstractMachineBlockEntity machine);

    /**
     * @return A human-readable description for UI/JEI.
     */
    String getDescription();
}
