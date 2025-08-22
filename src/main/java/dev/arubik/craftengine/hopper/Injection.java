package dev.arubik.craftengine.hopper;

import java.lang.instrument.Instrumentation;
import java.util.Optional;

import dev.arubik.craftengine.block.behavior.StorageBlockBehavior;
import dev.arubik.craftengine.util.SyncedGuiHolder;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.UnsafeCompositeBlockBehavior;
import net.momirealms.craftengine.bukkit.plugin.injector.BlockGenerator;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

public class Injection {

    public static class Interceptor {
        @SuppressWarnings("unused")
        public static Container intercept(Level level, BlockPos pos, BlockState state) throws Exception {
            System.out.println("[Injection] intercepting getBlockContainer...");
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
            } else if (SyncedGuiHolder.get(level, pos).isPresent()) {
                return SyncedGuiHolder.get(level, pos).get().getContainer();
            } else {
                return null;
            }
        }
    }

    public static class WorldlyContainerInterceptor implements WorldlyContainerHolder {
        public static WorldlyContainerInterceptor INSTANCE = new WorldlyContainerInterceptor();
        @Override
        public WorldlyContainer getContainer(BlockState state, LevelAccessor level, BlockPos pos) {
            Optional<ImmutableBlockState> stateI = BlockStateUtils.getOptionalCustomBlockState(state);
            if (stateI.isPresent()) {
                if (stateI.get().behavior() instanceof StorageBlockBehavior blockBehavior) {
                    return blockBehavior.getContainer(level.getMinecraftWorld(), pos);
                }
                if (stateI.get().behavior() instanceof UnsafeCompositeBlockBehavior unsafeBehavior) {
                    if (unsafeBehavior.getAs(StorageBlockBehavior.class).isPresent()) {
                        return unsafeBehavior.getAs(StorageBlockBehavior.class).get()
                                .getContainer(level.getMinecraftWorld(), pos);
                    }
                }
            }
            return null;
        }
    }

    public static void inject() {
        Instrumentation ins = ByteBuddyAgent.install();
        String packageWithName = BlockGenerator.class.getName();
        String generatedClassName = packageWithName.substring(0, packageWithName.lastIndexOf('.')) + ".CraftEngineBlock";
        try {
            new ByteBuddy()
                .redefine(Class.forName(generatedClassName))
                .implement(WorldlyContainerHolder.class)
                .defineMethod("getContainer", WorldlyContainer.class)
                .intercept(MethodDelegation.to(WorldlyContainerInterceptor.INSTANCE))
                .make()
                .load(Class.forName(generatedClassName)
                        .getClassLoader(),
                      ClassReloadingStrategy.fromInstalledAgent());

            System.out.println("[CraftEnginePolyfill] Injection successful with ByteBuddyAgent.install().");

        } catch (Throwable primary) {
            System.err.println("[CraftEnginePolyfill] Primary injection failed, trying fallback...");
            try {
                new ByteBuddy()
                    .redefine(Class.forName(generatedClassName))
                    .implement(WorldlyContainerHolder.class)
                    .defineMethod("getContainer", WorldlyContainer.class)
                    .intercept(MethodDelegation.to(WorldlyContainerInterceptor.INSTANCE))
                    .make()
                    .load(Class.forName(generatedClassName)
                            .getClassLoader(),
                          ClassReloadingStrategy.fromInstalledAgent());

                System.out.println("[CraftEnginePolyfill] Fallback injection successful.");

            } catch (Throwable secondary) {
                System.err.println("[CraftEnginePolyfill] (#2) failed, arming on-load transformer (#3)...");
                try {
                    new AgentBuilder.Default()
                        .ignore(ElementMatchers.none())
                        .type(ElementMatchers.named(generatedClassName))
                        .transform((builder, td, cl, module, pd) -> builder
                            .implement(WorldlyContainerHolder.class)
                            .defineMethod("getContainer", WorldlyContainer.class, Visibility.PUBLIC)
                            .withParameters(BlockState.class, LevelAccessor.class, BlockPos.class)
                            .intercept(MethodDelegation.to(WorldlyContainerInterceptor.INSTANCE))
                        )
                        .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                        .installOn(ins);

                    System.out.println("[CraftEnginePolyfill] (#3) Transformer armed: will apply when CraftEngineBlock is generated.");
                } catch (Throwable tertiary) {
                    System.err.println("[CraftEnginePolyfill] All 3 attempts failed!");
                    primary.printStackTrace();
                    secondary.printStackTrace();
                    tertiary.printStackTrace();
                }
            }
        }
    }
}
