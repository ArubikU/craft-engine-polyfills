package dev.arubik.craftengine.block.behavior;


import java.util.Optional;
import java.util.concurrent.Callable;

import dev.arubik.craftengine.util.BlockEntityBehaviorController;
import dev.arubik.craftengine.util.NmsBlockBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.core.block.CustomBlock;

/**
 * Fake BlockEntity-like behavior that extends NmsBlockBehavior.
 * Each block instance gets a unique UUID and persistent data.
 */
public abstract class BlockEntityBlockBehavior extends NmsBlockBehavior {

    public BlockEntityBlockBehavior(CustomBlock customBlock) {
        super(customBlock);
    }

    // ---- Hooks ----

    @Override
    public void onPlace(Object thisBlock, Level level, BlockPos pos, BlockState state, Callable<Object> superMethod) {
        try { superMethod.call(); } catch (Exception ignored) {}
        BlockEntityBehaviorController.register(level, pos);
    }

    @Override
    public void tick(Object thisBlock, Level level, BlockPos pos, BlockState state, Callable<Object> superMethod) {
        try { superMethod.call(); } catch (Exception ignored) {}
        BlockEntityBehaviorController.tick(level, pos);
    }

    @Override
    public void neighborChanged(Object thisBlock, Level level, BlockPos pos, BlockState state, Callable<Object> superMethod) {
        try { superMethod.call(); } catch (Exception ignored) {}
        BlockEntityBehaviorController.neighborChanged(level, pos);
    }

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Level level, BlockPos pos, BlockState state, Callable<Object> superMethod) {
        try { superMethod.call(); } catch (Exception ignored) {}
        BlockEntityBehaviorController.unregister(level, pos);
    }

    // Newer NMS variant providing movedByPiston flag
    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Level level, BlockPos pos, BlockState state, Boolean movedByPiston, Callable<Object> superMethod) {
        try { superMethod.call(); } catch (Exception ignored) {}
        if (Boolean.TRUE.equals(movedByPiston)) {
            // Do not unregister; state will be handled by piston move logic
            BlockEntityBehaviorController.move(level, pos, pos);
        } else {
            BlockEntityBehaviorController.unregister(level, pos);
        }
    }

    // Intentionally rely on NMS/base implementation for cross-version variants of affectNeighborsAfterRemoval

    // ---- Data access ----

    protected Optional<BlockEntityBehaviorController.BlockData> getData(Level level, BlockPos pos) {
        return BlockEntityBehaviorController.get(level, pos);
    }

    protected void setData(Level level, BlockPos pos, CompoundTag tag) {
        BlockEntityBehaviorController.update(level, pos, tag);
    }

}
