package dev.arubik.craftengine.util;

import java.util.concurrent.Callable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;

public class NmsBlockBehavior extends BukkitBlockBehavior {

    public NmsBlockBehavior(CustomBlock customBlock) {
        super(customBlock);
    }


    
    @Override
    public void onPlace(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        onPlace(
            thisBlock,
            (Level) args[0],
            (BlockPos) args[1],
            (BlockState) args[2],
            superMethod
        );
    }

    public void onPlace(Object thisBlock, Level level, BlockPos pos, BlockState state, Callable<Object> superMethod) {
        
    }
    
    @Override
    public void tick(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        tick(
            thisBlock,
            (Level) args[0],
            (BlockPos) args[1],
            (BlockState) args[2],
            superMethod
        );
    }
    public void tick(Object thisBlock, Level level, BlockPos pos, BlockState state, Callable<Object> superMethod) {

    }

    
    @Override
    public void neighborChanged(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        neighborChanged(
            thisBlock,
            (Level) args[0],
            (BlockPos) args[1],
            (BlockState) args[2],
            superMethod
        );
    
    }

    public void neighborChanged(Object thisBlock, Level level, BlockPos pos, BlockState state, Callable<Object> superMethod) {

    }

        @Override
    public void randomTick(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        randomTick(
            thisBlock,
            (Level) args[0],
            (BlockPos) args[1],
            (BlockState) args[2],
            superMethod
        );
    }

    public void randomTick(Object thisBlock, Level level, BlockPos pos, BlockState state, Callable<Object> superMethod) {

    }

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        if(args.length < 4) {
        affectNeighborsAfterRemoval(
            thisBlock,
            (Level) args[0],
            (BlockPos) args[1],
            (BlockState) args[2],
            superMethod
        );
        }else{
            final boolean movedByPiston = (args[3] instanceof Boolean b) && b;
            affectNeighborsAfterRemoval(
                thisBlock,
                (Level) args[0],
                (BlockPos) args[1],
                (BlockState) args[2],
                movedByPiston,
                superMethod
            );
        }
    }
    public void affectNeighborsAfterRemoval(Object thisBlock, Level level, BlockPos pos, BlockState state, Callable<Object> superMethod) {
    
    }
    public void affectNeighborsAfterRemoval(Object thisBlock, Level level, BlockPos pos, BlockState state, Boolean movedByPiston, Callable<Object> superMethod) {
    }

    // 1.20.2+ LevelAccessor level, BlockPos pos, BlockState state
    @Override
    public Object pickupBlock(Object thisObj, Object[] args, Callable<Object> superMethod) {
        try {
            if (args != null && args.length >= 3) {
                return pickupBlock(
                    thisObj,
                    (LevelAccessor) args[0],
                    (BlockPos) args[1],
                    (BlockState) args[2],
                    superMethod
                );
            }
            return superMethod.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object pickupBlock(Object thisObj, LevelAccessor level, BlockPos pos, BlockState state, Callable<Object> superMethod) {
        try {
            return superMethod.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    }
        
}