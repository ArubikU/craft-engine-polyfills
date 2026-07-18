package dev.arubik.craftengine.block.behavior;

import dev.arubik.craftengine.util.MBlocks;
import java.util.concurrent.Callable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitExistingBlock;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.BlockStateWrapper;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.LazyReference;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.World;

public class BubbleBlockBehavior extends BukkitBlockBehavior {
  public static final Factory FACTORY = new Factory();

  public boolean drag;

  public boolean direction;

  public LazyReference<BlockStateWrapper> blockState;

  public int limit;

  public BubbleBlockBehavior(BlockDefinition customBlock, boolean drag, boolean direction, int limit) {
    super(customBlock);
    this.drag = drag;
    this.direction = direction;
    this.limit = limit;
    if (drag) {
      this.blockState = LazyReference.lazyReference(
          () -> CraftEngine.instance().blockManager().createBlockState("minecraft:bubble_column[drag=true]"));
    } else {
      this.blockState = LazyReference.lazyReference(
          () -> CraftEngine.instance().blockManager().createBlockState("minecraft:bubble_column[drag=false]"));
    }
  }

  public void onPlace(Object thisBlock, Object[] args, Callable<Object> superMethod) {
    Object world = args[1];
    Object blockPos = args[2];
    dev.arubik.craftengine.util.MNms.INSTANCE.method$ScheduledTickAccess$scheduleBlockTick(world, blockPos, thisBlock, 6);
  }

  public void tick(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
    Object level = args[1];
    BlockPos blockPos = LocationUtils.fromBlockPos(args[2]);
    BukkitWorld bukkitWorld = dev.arubik.craftengine.util.CeWorlds.of(dev.arubik.craftengine.util.MNms.INSTANCE.method$Level$getCraftWorld(level));
    dev.arubik.craftengine.util.MNms.INSTANCE.method$ScheduledTickAccess$scheduleBlockTick(level, args[2], thisBlock, 6);
    if (this.direction) {
      BlockPos current = blockPos.above();
      for (int i = 0; i < this.limit;) {
        current = current.above();
        Object fs = dev.arubik.craftengine.util.MNms.INSTANCE.method$BlockGetter$getFluidState(level, LocationUtils.toBlockPos(current));
        Object ft = dev.arubik.craftengine.util.MNms.INSTANCE.method$FluidState$getType(fs);
        if (ft == Fluids.WATER) {
          BukkitExistingBlock blockInWorld = (BukkitExistingBlock) bukkitWorld.getBlock(current.x(), current.y(),
              current.z());
          blockInWorld.block()
              .setBlockData(BlockStateUtils.fromBlockData(((BlockStateWrapper) this.blockState.get()).minecraftState()));
          i++;
        }
      }
    } else {
      BlockPos current = blockPos.offset(0, -1, 0);
      for (int i = 0; i < this.limit;) {
        current = current.offset(0, -1, 0);
        Object fs = dev.arubik.craftengine.util.MNms.INSTANCE.method$BlockGetter$getFluidState(level, LocationUtils.toBlockPos(current));
        Object ft = dev.arubik.craftengine.util.MNms.INSTANCE.method$FluidState$getType(fs);
        if (ft == Fluids.WATER) {
          BukkitExistingBlock blockInWorld = (BukkitExistingBlock) bukkitWorld.getBlock(current.x(), current.y(),
              current.z());
          blockInWorld.block()
              .setBlockData(BlockStateUtils.fromBlockData(((BlockStateWrapper) this.blockState.get()).minecraftState()));
          i++;
        }
      }
    }
  }

  public void affectNeighborsAfterRemoval(Object thisBlock, Object[] args, Callable<Object> superMethod) {
    updateNeighbours(args[1], args[2], thisBlock);
  }

  public void onRemove(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
    Object state = args[0];
    Object level = args[1];
    Object pos = args[2];
    Object newState = args[3];
    if (!dev.arubik.craftengine.util.MNms.INSTANCE.method$BlockStateBase$is(state, dev.arubik.craftengine.util.MNms.INSTANCE.method$BlockState$getBlock(newState))) {
      updateNeighbours(level, pos, thisBlock);
      superMethod.call();
    }
  }

  private void updateNeighbours(Object arg1, Object arg2, Object thisBlock) {
    World level = (World) arg1;
    BlockPos blockPos = (BlockPos) arg2;
    BukkitWorld bukkitWorld = dev.arubik.craftengine.util.CeWorlds.of(dev.arubik.craftengine.util.MNms.INSTANCE.method$Level$getCraftWorld(level.minecraftWorld()));
    if (this.direction) {
      BlockPos current = blockPos.above();
      for (int i = 0; i < this.limit; i++) {
        current = current.above();
        if (dev.arubik.craftengine.util.MNms.INSTANCE.method$BlockGetter$getBlockState(level,
            LocationUtils.toBlockPos(current)) != MBlocks.BUBBLE_COLUMN)
          break;
        BukkitExistingBlock blockInWorld = (BukkitExistingBlock) bukkitWorld.getBlock(current.x(), current.y(),
            current.z());
        blockInWorld.block().setBlockData(BlockStateUtils.fromBlockData(Blocks.WATER.defaultBlockState()));
      }
    } else {
      BlockPos current = blockPos.offset(0, -1, 0);
      for (int i = 0; i < this.limit; i++) {
        current = current.offset(0, -1, 0);
        if (dev.arubik.craftengine.util.MNms.INSTANCE.method$BlockGetter$getBlockState(level,
            LocationUtils.toBlockPos(current)) != MBlocks.BUBBLE_COLUMN)
          break;
        BukkitExistingBlock blockInWorld = (BukkitExistingBlock) bukkitWorld.getBlock(current.x(), current.y(),
            current.z());
        blockInWorld.block().setBlockData(BlockStateUtils.fromBlockData(Blocks.WATER.defaultBlockState()));
      }
    }
  }

  private static class Factory implements BlockBehaviorFactory<BlockBehavior> {
    public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
      boolean direction = arguments.getOrDefault("direction", "up").toString().equalsIgnoreCase("up");
      boolean drag = arguments.getOrDefault("drag", "up").toString().equalsIgnoreCase("up");
      int limit = Integer.valueOf(arguments.getOrDefault("limit", Integer.valueOf(100)).toString()).intValue();
      return (BlockBehavior) new BubbleBlockBehavior(block, drag, direction, limit);
    }
  }
}
