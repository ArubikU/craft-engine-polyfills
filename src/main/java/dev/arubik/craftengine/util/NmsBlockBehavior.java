package dev.arubik.craftengine.util;

import java.util.concurrent.Callable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.plugin.CraftEngine;

public abstract class NmsBlockBehavior extends BukkitBlockBehavior {

    public NmsBlockBehavior(CustomBlock customBlock) {
        super(customBlock);
    }
    
    @Override
    public void onPlace(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        onPlace(
            thisBlock,
            (Level) args[1],
            (BlockPos) args[2],
            (BlockState) args[0],
            superMethod
        );
    }

    public void onPlace(Object thisBlock, Level level, BlockPos pos, BlockState state, Callable<Object> superMethod) {
                CraftEngine.instance().logger().info("Default onPlace behavior");
    }
    
    @Override
    public void tick(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        tick(
            thisBlock,
            (Level) args[1],
            (BlockPos) args[2],
            (BlockState) args[0],
            superMethod
        );
    }
    public void tick(Object thisBlock, Level level, BlockPos pos, BlockState state, Callable<Object> superMethod) {
        CraftEngine.instance().logger().info("Default tick behavior");
    }

    
    @Override
    public void neighborChanged(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        neighborChanged(
            thisBlock,
            (Level) args[1],
            (BlockPos) args[2],
            (BlockState) args[0],
            superMethod
        );
    
    }

    public void neighborChanged(Object thisBlock, Level level, BlockPos pos, BlockState state, Callable<Object> superMethod) {
        CraftEngine.instance().logger().info("Default neighborChanged behavior");
    }

    @Override
    public void randomTick(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        randomTick(
            thisBlock,
            (Level) args[1],
            (BlockPos) args[2],
            (BlockState) args[0],
            superMethod
        );
    }

    public void randomTick(Object thisBlock, Level level, BlockPos pos, BlockState state, Callable<Object> superMethod) {
        CraftEngine.instance().logger().info("Default randomTick behavior");
    }

    @Override
    public void onRemove(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        Object state = args[0];
        Object level = args[1];
        Object pos = args[2];
        Object newState = args[3];
        onRemove(thisBlock, (Level) level, (BlockPos) pos, (BlockState) state, (BlockState) newState, superMethod);
    }

    public void onRemove(Object thisBlock, Level level, BlockPos pos, BlockState state, BlockState newState, Callable<Object> superMethod) throws Exception {
        CraftEngine.instance().logger().info("Default onRemove behavior");
        superMethod.call();
    }

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        if(args.length < 4) {
        affectNeighborsAfterRemoval(
            thisBlock,
            (Level) args[1],
            (BlockPos) args[2],
            (BlockState) args[0],
            false,
            superMethod
        );
        }else{
            final boolean movedByPiston = (args[3] instanceof Boolean b) && b;
            affectNeighborsAfterRemoval(
                thisBlock,
                (Level) args[1],
                (BlockPos) args[2],
                (BlockState) args[0],
                movedByPiston,
                superMethod
            );
        }
    }
    public void affectNeighborsAfterRemoval(Object thisBlock, Level level, BlockPos pos, BlockState state, Boolean movedByPiston, Callable<Object> superMethod) throws Exception {
        CraftEngine.instance().logger().info("Default affectNeighborsAfterRemoval behavior");
        superMethod.call();
    }

    // Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock
    @Override
    public void onLand(Object thisBlock, Object[] args) {
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