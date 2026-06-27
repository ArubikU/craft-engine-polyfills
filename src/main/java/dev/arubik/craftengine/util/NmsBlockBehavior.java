package dev.arubik.craftengine.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.plugin.CraftEngine;

public abstract class NmsBlockBehavior extends BukkitBlockBehavior {

    public NmsBlockBehavior(BlockDefinition customBlock) {
        super(customBlock);
    }

    @Override
    public void onPlace(Object thisBlock, Object[] args) {
        onPlace(
            thisBlock,
            (Level) args[1],
            (BlockPos) args[2],
            (BlockState) args[0]
        );
    }

    public void onPlace(Object thisBlock, Level level, BlockPos pos, BlockState state) {
    }

    @Override
    public void tick(Object thisBlock, Object[] args) {
        tick(
            thisBlock,
            (Level) args[1],
            (BlockPos) args[2],
            (BlockState) args[0]
        );
    }

    public void tick(Object thisBlock, Level level, BlockPos pos, BlockState state) {
    }

    @Override
    public void neighborChanged(Object thisBlock, Object[] args) {
        neighborChanged(
            thisBlock,
            (Level) args[1],
            (BlockPos) args[2],
            (BlockState) args[0]
        );
    }

    public void neighborChanged(Object thisBlock, Level level, BlockPos pos, BlockState state) {
    }

    @Override
    public void randomTick(Object thisBlock, Object[] args) {
        randomTick(
            thisBlock,
            (Level) args[1],
            (BlockPos) args[2],
            (BlockState) args[0]
        );
    }

    public void randomTick(Object thisBlock, Level level, BlockPos pos, BlockState state) {
    }

    // onRemove no longer exists on BlockBehavior in 26.6.2; block-removal logic is now
    // routed through affectNeighborsAfterRemoval.
    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Object[] args) {
        final boolean movedByPiston = (args.length > 3) && (args[3] instanceof Boolean b) && b;
        affectNeighborsAfterRemoval(
            thisBlock,
            (Level) args[1],
            (BlockPos) args[2],
            (BlockState) args[0],
            movedByPiston
        );
    }

    public void affectNeighborsAfterRemoval(Object thisBlock, Level level, BlockPos pos, BlockState state, Boolean movedByPiston) {
    }

    // Vanilla Block.fallOn(Level, BlockState state, BlockPos pos, Entity entity, float fallDistance).
    // The engine forwards these verbatim (no fallOn$* reorder constants exist), so:
    //   args[0]=level, args[1]=state, args[2]=pos, args[3]=entity, args[4]=fallDistance.
    @Override
    public void fallOn(Object thisBlock, Object[] args) {
        try {
            onLand(
                thisBlock,
                (Level) args[0],
                (BlockState) args[1],
                (BlockPos) args[2],
                (Entity) args[3],
                ((Number) args[4]).floatValue()
            );
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /** Called when {@code entity} lands on this block after falling {@code fallDistance} blocks. Default no-op. */
    public void onLand(Object thisBlock, Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        // default no-op
    }

}
