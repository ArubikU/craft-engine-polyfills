package dev.arubik.craftengine.contraption.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

/**
 * A block entity controller that ticks inside a moving contraption.
 *
 * <p>
 * This is the actual interface contraption levels check for. It bridges the gap between
 * the simple {@link TickableBehavior} API (2-param tick) and the real CraftEngine block
 * entity ticker pattern (3-param tick with ImmutableBlockState).
 *
 * <p>
 * <b>For external plugins</b>: Implement {@link TickableBehavior} instead (simpler).
 * This interface is for internal use where the full state is available.
 *
 * <p>
 * <b>For CraftEngine polyfills</b>: {@link dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity}
 * already implements this (its tick method has the right signature).
 */
public interface ContraptionTickable {

    /**
     * Ticks this block entity's logic inside a contraption.
     *
     * <p>
     * Called every server tick (20 Hz) while the block is inside a moving contraption.
     * The {@code pos} is the block's position in the contraption's private level, and
     * {@code state} is its cached blockstate (faster than re-reading from the level).
     *
     * @param level the contraption's private level
     * @param pos   the block's position in that level
     * @param state the block's immutable state
     */
    void tick(Level level, BlockPos pos, ImmutableBlockState state);

    /**
     * Called when this block entity is unregistered from a contraption.
     *
     * <p>
     * Machines may need to clean up resources (menus, conveyors, external registries)
     * when removed from a contraption. Default does nothing.
     */
    default void unregister() {
    }
}
