package dev.arubik.craftengine.hopper;

import dev.arubik.craftengine.util.SyncedGuiHolder;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

public class Injection {

    public static void inject() {
        new ByteBuddy()
                .redefine(HopperBlockEntity.class)
                .method(
                        ElementMatchers.isStatic()
                                .and(ElementMatchers.takesArguments(Level.class, BlockPos.class, BlockState.class))
                                .and(ElementMatchers.returns(Container.class)))
                .intercept(MethodDelegation.to(new Object() {
                    @SuppressWarnings("unused")
                    public Container intercept(Level level, BlockPos pos, BlockState state) throws Exception {
                        if (!level.spigotConfig.hopperCanLoadChunks && !level.hasChunkAt(pos))
                            return null; // Spigot
                        Block block = state.getBlock();
                        if (block instanceof WorldlyContainerHolder) {
                            return ((WorldlyContainerHolder) block).getContainer(state, level, pos);
                        } else if (state.hasBlockEntity() && level.getBlockEntity(pos) instanceof Container container) {
                            if (container instanceof ChestBlockEntity && block instanceof ChestBlock) {
                                container = ChestBlock.getContainer((ChestBlock) block, state, level, pos, true);
                            }
                            return container;
                        } else if(SyncedGuiHolder.get(level, pos).isPresent()) {
                            return SyncedGuiHolder.get(level, pos).get().getContainer();
                        } else {
                            return null;
                        }
                    }
                }))
                .make()
                .load(HopperBlockEntity.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
