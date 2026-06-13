package dev.arubik.craftengine.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
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
        CraftEngine.instance().logger().info("Default onPlace behavior");
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
        CraftEngine.instance().logger().info("Default tick behavior");
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
        CraftEngine.instance().logger().info("Default neighborChanged behavior");
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
        CraftEngine.instance().logger().info("Default randomTick behavior");
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
        CraftEngine.instance().logger().info("Default affectNeighborsAfterRemoval behavior");
    }

    // onLand is gone; vanilla landing is the fallOn callback.
    // arg order: [level, blockPos, state, replaceableState, fallingBlockEntity]
    @Override
    public void fallOn(Object thisBlock, Object[] args) {
        try {
            onLand(
                thisBlock,
                (Level) args[0],
                (BlockPos) args[1],
                (BlockState) args[2],
                (BlockState) args[3],
                (FallingBlockEntity) args[4]
            );
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void onLand(Object thisBlock, Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock) {
        // default no-op
        CraftEngine.instance().logger().info("Default onLand behavior");
    }

}
