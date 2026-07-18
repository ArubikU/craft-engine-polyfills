package dev.arubik.craftengine.block.behavior;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import dev.arubik.craftengine.util.MBlocks;
import net.minecraft.world.level.material.Fluids;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.property.BooleanProperty;
import net.momirealms.craftengine.core.block.property.IntegerProperty;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.util.random.RandomUtils;
import net.momirealms.craftengine.core.world.BlockPos;

public class VerticalCropBlockBehavior extends BukkitBlockBehavior {

  public static final Factory FACTORY = new Factory();

  private static final ObjectArrayList<Object> WATER = ObjectArrayList
      .of(new Object[] { Fluids.WATER, Fluids.FLOWING_WATER });

  private static final ObjectArrayList<Object> LAVA = ObjectArrayList
      .of(new Object[] { Fluids.LAVA, Fluids.FLOWING_LAVA });

  private final int maxHeight;

  private final IntegerProperty ageProperty;

  private final BooleanProperty waterloggedProperty;

  private final BlockPos[] liquidPositions;

  private final boolean requireWater;

  private final boolean requireLava;

  private final boolean stopOverliquidGrowing;

  private final boolean allowAirGrow;

  private final boolean allowWaterGrow;

  private final boolean direction;

  private final float growSpeed;

  private final boolean invertGrowth;

  public VerticalCropBlockBehavior(BlockDefinition customBlock, Property<Integer> ageProperty, int maxHeight,
      float growSpeed, boolean direction, BlockPos[] liquidPositions, boolean requireWater, boolean requireLava,
      boolean stopOverliquidGrowing, boolean allowAirGrow, boolean allowWaterGrow, boolean invertGrowth,
      BooleanProperty waterloggedProperty) {
    super(customBlock);
    this.maxHeight = maxHeight;
    this.ageProperty = (IntegerProperty) ageProperty;
    this.growSpeed = growSpeed;
    this.direction = direction;
    this.liquidPositions = liquidPositions;
    this.requireWater = requireWater;
    this.requireLava = requireLava;
    this.stopOverliquidGrowing = stopOverliquidGrowing;
    this.allowAirGrow = allowAirGrow;
    this.allowWaterGrow = allowWaterGrow;
    this.invertGrowth = invertGrowth;
    this.waterloggedProperty = waterloggedProperty;
  }

  private boolean canGrow(Object level, BlockPos targetPos, BlockPos basePos) {
    net.minecraft.core.BlockPos targetnmsPos = (net.minecraft.core.BlockPos) LocationUtils.toBlockPos(targetPos);
    net.minecraft.world.level.material.FluidState directionFluid = ((net.minecraft.world.level.BlockGetter) level)
        .getFluidState(targetnmsPos);
    boolean canGrowAir = (this.allowAirGrow
        && directionFluid.getType() == Fluids.EMPTY
        && ((net.minecraft.world.level.BlockGetter) level).getBlockState(targetnmsPos) == MBlocks.AIR$defaultState);
    boolean canGrowWater = (this.allowWaterGrow
        && WATER.contains(directionFluid.getType()));
    if (!canGrowAir && !canGrowWater)
      return false;
    if (this.stopOverliquidGrowing && !isWater(level, targetPos) && canGrowAir && this.allowWaterGrow)
      return false;
    if (this.liquidPositions.length == 0)
      return true;
    for (BlockPos offset : this.liquidPositions) {
      net.minecraft.core.BlockPos checkPos = (net.minecraft.core.BlockPos) LocationUtils
          .toBlockPos(basePos.x() + offset.x(), basePos.y() + offset.y(), basePos.z() + offset.z());
      net.minecraft.world.level.material.FluidState fs = ((net.minecraft.world.level.BlockGetter) level)
          .getFluidState(checkPos);
      Object ft = fs.getType();
      if ((this.requireWater && WATER.contains(ft)) || (this.requireLava && LAVA.contains(ft)))
        return true;
    }
    return false;
  }

  private boolean isWater(Object level, BlockPos targetPos) {
    net.minecraft.world.level.material.FluidState fluidState = ((net.minecraft.world.level.BlockGetter) level)
        .getFluidState((net.minecraft.core.BlockPos) LocationUtils.toBlockPos(targetPos));
    Optional<ImmutableBlockState> optionalState = BlockStateUtils.getOptionalCustomBlockState(
        ((net.minecraft.world.level.BlockGetter) level)
            .getBlockState((net.minecraft.core.BlockPos) LocationUtils.toBlockPos(targetPos)));
    if (optionalState.isPresent()) {
      ImmutableBlockState currentState = optionalState.get();
      if (this.allowWaterGrow)
        return (WATER.contains(fluidState.getType())
            || ((Boolean) currentState.get((Property) this.waterloggedProperty)).booleanValue());
    }
    return WATER.contains(fluidState.getType());
  }

  @Override
  public void randomTick(Object thisBlock, Object[] args) {
    if (RandomUtils.generateRandomFloat(0.0F, 1.0F) >= this.growSpeed)
      return;
    Object level = args[1];
    BukkitWorld bukkitWorld = dev.arubik.craftengine.util.CeWorlds.of(((net.minecraft.server.level.ServerLevel) level).getWorld());
    BlockPos currentPos = LocationUtils.fromBlockPos(args[2]);
    int height = 0;
    BlockPos hPos = currentPos;
    while (true) {
      ImmutableBlockState nextOpt = bukkitWorld.getBlock(hPos.x(), hPos.y(), hPos.z()).customBlockState();
      if (nextOpt == null)
        break;
      if (!nextOpt.isEmpty() && nextOpt.owner().value() == this.blockDefinition) {
        hPos = this.direction ? hPos.offset(0, -1, 0) : hPos.offset(0, 1, 0);
        height++;
        continue;
      }
      break;
    }
    BlockPos targetPos = this.direction ? currentPos.offset(0, 1, 0) : currentPos.offset(0, -1, 0);
    if (!canGrow(level, targetPos, hPos))
      return;
    if (height >= this.maxHeight)
      return;
    ImmutableBlockState tipState = null;
    if (isWater(level, targetPos)) {
      tipState = this.blockDefinition.defaultState().with((Property) this.waterloggedProperty, Boolean.valueOf(true)).with(
          (Property) this.ageProperty,
          Integer.valueOf(this.invertGrowth ? this.ageProperty.max : this.ageProperty.min));
    } else {
      tipState = this.blockDefinition.defaultState().with((Property) this.waterloggedProperty, Boolean.valueOf(false)).with(
          (Property) this.ageProperty,
          Integer.valueOf(this.invertGrowth ? this.ageProperty.max : this.ageProperty.min));
    }
    ((net.minecraft.world.level.LevelWriter) bukkitWorld.minecraftWorld()).setBlock(
        (net.minecraft.core.BlockPos) LocationUtils.toBlockPos(targetPos),
        (net.minecraft.world.level.block.state.BlockState) tipState.customBlockState().minecraftState(), 2);
  }

  public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
    public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
      Property<Integer> ageProperty = (Property<Integer>) dev.arubik.craftengine.util.Utils.requireNonNullOrThrow(block
          .getProperty("age"), "warning.config.block.behavior.vertical_crop.missing_age");
      int maxHeight = dev.arubik.craftengine.util.Utils.getAsInt(arguments.getOrDefault("max-height", Integer.valueOf(3)),
          "max-height");
      boolean direction = "up".equals(arguments.getOrDefault("direction", "up").toString().toLowerCase(Locale.ROOT));
      List<String> requiredLiquids = dev.arubik.craftengine.util.Utils
          .getAsStringList(arguments.getOrDefault("required-liquids", ObjectArrayList.of()));
      boolean reqWater = requiredLiquids.contains("water");
      boolean reqLava = requiredLiquids.contains("lava");
      List<String> posStrings = dev.arubik.craftengine.util.Utils.getAsStringList(arguments.getOrDefault("liquids-pos", ObjectArrayList.of()));
      BlockPos[] liquidPositions = new BlockPos[posStrings.size()];
      for (int i = 0; i < posStrings.size(); i++) {
        StringTokenizer tokenizer = new StringTokenizer(posStrings.get(i), ",");
        int x = Integer.parseInt(tokenizer.nextToken());
        int y = Integer.parseInt(tokenizer.nextToken());
        int z = Integer.parseInt(tokenizer.nextToken());
        liquidPositions[i] = new BlockPos(x, y, z);
      }
      boolean stopOver = dev.arubik.craftengine.util.Utils.getAsBoolean(
          arguments.getOrDefault("stop-Overliquid-growing", Boolean.valueOf(false)), "stop-Overliquid-growing");
      List<String> growTypes = dev.arubik.craftengine.util.Utils
          .getAsStringList(arguments.getOrDefault("grow-types", ObjectArrayList.of((Object[]) new String[] { "air" })));
      boolean allowAir = growTypes.contains("air");
      boolean allowWater = growTypes.contains("water");
      float growSpeed = dev.arubik.craftengine.util.Utils.getAsFloat(arguments.getOrDefault("grow-speed", Integer.valueOf(1)),
          "grow-speed");
      boolean invertGrowth = dev.arubik.craftengine.util.Utils
          .getAsBoolean(arguments.getOrDefault("invert-growth", Boolean.valueOf(false)), "invert-growth");
      BooleanProperty waterloggedProperty = null;
      if (allowWater)
        waterloggedProperty = (BooleanProperty) dev.arubik.craftengine.util.Utils.requireNonNullOrThrow(block
            .getProperty("waterlogged"), "warning.config.block.behavior.vertical_crop.missing_waterlogged");
      return (BlockBehavior) new VerticalCropBlockBehavior(block, ageProperty, maxHeight, growSpeed, direction,
          liquidPositions, reqWater, reqLava, stopOver, allowAir, allowWater, invertGrowth, waterloggedProperty);
    }
  }
}
