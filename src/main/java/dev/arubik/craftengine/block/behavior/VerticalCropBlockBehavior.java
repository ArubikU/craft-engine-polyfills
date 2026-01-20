package dev.arubik.craftengine.block.behavior;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.plugin.reflection.minecraft.MBlocks;
import net.momirealms.craftengine.bukkit.plugin.reflection.minecraft.MFluids;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.properties.BooleanProperty;
import net.momirealms.craftengine.core.block.properties.IntegerProperty;
import net.momirealms.craftengine.core.block.properties.Property;
import net.momirealms.craftengine.core.util.MiscUtils;
import net.momirealms.craftengine.core.util.random.RandomUtils;
import net.momirealms.craftengine.core.util.ResourceConfigUtils;
import net.momirealms.craftengine.core.world.BlockPos;

public class VerticalCropBlockBehavior extends BukkitBlockBehavior {

  public static final Factory FACTORY = new Factory();

  private static final ObjectArrayList<Object> WATER = ObjectArrayList
      .of(new Object[] { MFluids.WATER, MFluids.FLOWING_WATER });

  private static final ObjectArrayList<Object> LAVA = ObjectArrayList
      .of(new Object[] { MFluids.LAVA, MFluids.FLOWING_LAVA });

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

  public VerticalCropBlockBehavior(CustomBlock customBlock, Property<Integer> ageProperty, int maxHeight,
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
    Object targetnmsPos = LocationUtils.toBlockPos(targetPos);
    Object directionFluid = FastNMS.INSTANCE.method$BlockGetter$getFluidState(level, targetnmsPos);
    boolean canGrowAir = (this.allowAirGrow
        && FastNMS.INSTANCE.method$FluidState$getType(directionFluid) == MFluids.EMPTY
        && FastNMS.INSTANCE.method$BlockGetter$getBlockState(level, targetnmsPos) == MBlocks.AIR$defaultState);
    boolean canGrowWater = (this.allowWaterGrow
        && WATER.contains(FastNMS.INSTANCE.method$FluidState$getType(directionFluid)));
    if (!canGrowAir && !canGrowWater)
      return false;
    if (this.stopOverliquidGrowing && !isWater(level, targetPos) && canGrowAir && this.allowWaterGrow)
      return false;
    if (this.liquidPositions.length == 0)
      return true;
    for (BlockPos offset : this.liquidPositions) {
      Object checkPos = LocationUtils.toBlockPos(basePos.x() + offset.x(), basePos.y() + offset.y(), basePos
          .z() + offset.z());
      Object fs = FastNMS.INSTANCE.method$BlockGetter$getFluidState(level, checkPos);
      Object ft = FastNMS.INSTANCE.method$FluidState$getType(fs);
      if ((this.requireWater && WATER.contains(ft)) || (this.requireLava && LAVA.contains(ft)))
        return true;
    }
    return false;
  }

  private boolean isWater(Object level, BlockPos targetPos) {
    Object fluidState = FastNMS.INSTANCE.method$BlockGetter$getFluidState(level, LocationUtils.toBlockPos(targetPos));
    Optional<ImmutableBlockState> optionalState = BlockStateUtils.getOptionalCustomBlockState(
        FastNMS.INSTANCE.method$BlockGetter$getBlockState(level, LocationUtils.toBlockPos(targetPos)));
    if (optionalState.isPresent()) {
      ImmutableBlockState currentState = optionalState.get();
      if (this.allowWaterGrow)
        return (WATER.contains(FastNMS.INSTANCE.method$FluidState$getType(fluidState))
            || ((Boolean) currentState.get((Property) this.waterloggedProperty)).booleanValue());
    }
    return WATER.contains(FastNMS.INSTANCE.method$FluidState$getType(fluidState));
  }

  public void randomTick(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
    if (RandomUtils.generateRandomFloat(0.0F, 1.0F) >= this.growSpeed)
      return;
    Object level = args[1];
    BukkitWorld bukkitWorld = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(level));
    BlockPos currentPos = LocationUtils.fromBlockPos(args[2]);
    int height = 0;
    BlockPos hPos = currentPos;
    while (true) {
      ImmutableBlockState nextOpt = bukkitWorld.getBlock(hPos.x(), hPos.y(), hPos.z()).customBlockState();
      if (nextOpt == null)
        break;
      if (!nextOpt.isEmpty() && nextOpt.owner().value() == this.customBlock) {
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
      tipState = this.customBlock.defaultState().with((Property) this.waterloggedProperty, Boolean.valueOf(true)).with(
          (Property) this.ageProperty,
          Integer.valueOf(this.invertGrowth ? this.ageProperty.max : this.ageProperty.min));
    } else {
      tipState = this.customBlock.defaultState().with((Property) this.waterloggedProperty, Boolean.valueOf(false)).with(
          (Property) this.ageProperty,
          Integer.valueOf(this.invertGrowth ? this.ageProperty.max : this.ageProperty.min));
    }
    FastNMS.INSTANCE.method$LevelWriter$setBlock(bukkitWorld.serverWorld(), LocationUtils.toBlockPos(targetPos),
        tipState.customBlockState().literalObject(), 2);
  }

  public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
    public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
      Property<Integer> ageProperty = (Property<Integer>) ResourceConfigUtils.requireNonNullOrThrow(block
          .getProperty("age"), "warning.config.block.behavior.vertical_crop.missing_age");
      int maxHeight = ResourceConfigUtils.getAsInt(arguments.getOrDefault("max-height", Integer.valueOf(3)),
          "max-height");
      boolean direction = "up".equals(arguments.getOrDefault("direction", "up").toString().toLowerCase(Locale.ROOT));
      List<String> requiredLiquids = MiscUtils
          .getAsStringList(arguments.getOrDefault("required-liquids", ObjectArrayList.of()));
      boolean reqWater = requiredLiquids.contains("water");
      boolean reqLava = requiredLiquids.contains("lava");
      List<String> posStrings = MiscUtils.getAsStringList(arguments.getOrDefault("liquids-pos", ObjectArrayList.of()));
      BlockPos[] liquidPositions = new BlockPos[posStrings.size()];
      for (int i = 0; i < posStrings.size(); i++) {
        StringTokenizer tokenizer = new StringTokenizer(posStrings.get(i), ",");
        int x = Integer.parseInt(tokenizer.nextToken());
        int y = Integer.parseInt(tokenizer.nextToken());
        int z = Integer.parseInt(tokenizer.nextToken());
        liquidPositions[i] = new BlockPos(x, y, z);
      }
      boolean stopOver = ResourceConfigUtils.getAsBoolean(
          arguments.getOrDefault("stop-Overliquid-growing", Boolean.valueOf(false)), "stop-Overliquid-growing");
      List<String> growTypes = MiscUtils
          .getAsStringList(arguments.getOrDefault("grow-types", ObjectArrayList.of((Object[]) new String[] { "air" })));
      boolean allowAir = growTypes.contains("air");
      boolean allowWater = growTypes.contains("water");
      float growSpeed = ResourceConfigUtils.getAsFloat(arguments.getOrDefault("grow-speed", Integer.valueOf(1)),
          "grow-speed");
      boolean invertGrowth = ResourceConfigUtils
          .getAsBoolean(arguments.getOrDefault("invert-growth", Boolean.valueOf(false)), "invert-growth");
      BooleanProperty waterloggedProperty = null;
      if (allowWater)
        waterloggedProperty = (BooleanProperty) ResourceConfigUtils.requireNonNullOrThrow(block
            .getProperty("waterlogged"), "warning.config.block.behavior.vertical_crop.missing_waterlogged");
      return (BlockBehavior) new VerticalCropBlockBehavior(block, ageProperty, maxHeight, growSpeed, direction,
          liquidPositions, reqWater, reqLava, stopOver, allowAir, allowWater, invertGrowth, waterloggedProperty);
    }
  }
}
