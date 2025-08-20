package dev.arubik.craftengine.block.behavior;

import java.util.concurrent.Callable;

import dev.arubik.craftengine.util.NmsBlockBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.core.block.CustomBlock;

public class SpikeBlockBehavior extends NmsBlockBehavior{

    public SpikeBlockBehavior(CustomBlock customBlock) {
        super(customBlock);
    }

    @Override
    public void tick(Object thisBlock, Level level, BlockPos pos, BlockState state, Callable<Object> superMethod) {

    }

}