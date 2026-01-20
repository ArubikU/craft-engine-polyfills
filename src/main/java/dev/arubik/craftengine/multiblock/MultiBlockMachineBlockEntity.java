package dev.arubik.craftengine.multiblock;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

/**
 * Base class for MultiBlock Machine block entities.
 * This is used for CORE blocks in multiblock structures that need full machine
 * capability.
 */
public abstract class MultiBlockMachineBlockEntity extends AbstractMachineBlockEntity {

    protected final MultiBlockSchema schema;

    public MultiBlockMachineBlockEntity(int containerSize, net.momirealms.craftengine.core.world.BlockPos pos,
            ImmutableBlockState state,
            MultiBlockSchema schema) {
        super(containerSize, pos, state);
        this.schema = schema;
    }

    /**
     * Gets the multiblock schema for this machine.
     */
    public MultiBlockSchema getSchema() {
        return schema;
    }

    /**
     * Get dynamic redstone output based on which part and which side.
     * Override in subclasses to provide custom logic.
     * 
     * @param relativePos Position relative to core (BlockPos.ZERO for core itself)
     * @param side        Direction from which redstone is being read
     * @return Signal strength 0-15
     */
    public int getRedstoneOutput(BlockPos relativePos, Direction side) {
        // Default: signal based on processing state
        return isProcessing() ? 15 : 0;
    }

    /**
     * Abstract methods from AbstractMachineBlockEntity that must be implemented
     */
    @Override
    public abstract MachineLayout getLayout();

    @Override
    protected abstract AbstractProcessingRecipe getMatchingRecipe(Level level);

    @Override
    protected abstract boolean canFitOutput(Level level, RecipeOutput output);

    @Override
    protected abstract void consumeInputs(Level level, AbstractProcessingRecipe recipe);

    @Override
    protected abstract String getMachineId();
}
