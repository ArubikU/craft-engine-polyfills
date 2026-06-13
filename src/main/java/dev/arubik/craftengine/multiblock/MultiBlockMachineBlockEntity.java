package dev.arubik.craftengine.multiblock;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineController;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.entity.BlockEntity;

/**
 * Base controller for MultiBlock Machine block entities (ce 26.6.2).
 * Used for CORE blocks in multiblock structures that need full machine
 * capability. Re-based onto BlockEntityController via AbstractMachineController.
 */
public abstract class MultiBlockMachineBlockEntity extends AbstractMachineController {

    protected final MultiBlockSchema schema;

    public MultiBlockMachineBlockEntity(int containerSize, BlockEntity blockEntity, MultiBlockSchema schema) {
        super(containerSize, blockEntity);
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
     * Drive machine ticking through the controller model. Mirrors the legacy
     * behavior-driven sync tick: only CORE machine controllers tick.
     */
    @Override
    public <C extends net.momirealms.craftengine.core.block.entity.BlockEntityController> net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<C> createBlockEntityTicker(
            net.momirealms.craftengine.core.world.CEWorld world,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        return net.momirealms.craftengine.core.block.entity.BlockEntityController.createTickerHelper(
                (net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<MultiBlockMachineBlockEntity>) MultiBlockMachineBlockEntity::tick);
    }

    public static void tick(net.momirealms.craftengine.core.world.CEWorld world,
            net.momirealms.craftengine.core.world.BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state,
            MultiBlockMachineBlockEntity self) {
        self.tick((Level) world.world.minecraftWorld(),
                dev.arubik.craftengine.util.Utils.fromPos(pos), state);
    }

    /**
     * Abstract methods from AbstractMachineController that must be implemented
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
