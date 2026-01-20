package dev.arubik.craftengine.block.behavior;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import net.momirealms.craftengine.bukkit.block.BukkitBlockManager;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.plugin.reflection.minecraft.MBlocks;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitExistingBlock;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.EmptyBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.util.random.RandomUtils;
import net.momirealms.craftengine.core.world.BlockPos;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.PointedDripstone;
import org.bukkit.event.block.BlockFormEvent;

public class TearingBlockSpawnBehavior extends BukkitBlockBehavior {
  public static final Factory FACTORY = new Factory();

  private final boolean water;

  private final Key toPlace;

  private final float chance;

  private final int heightLimit;

  private Object defaultBlockState;

  private ImmutableBlockState defaultImmutableBlockState;

  public TearingBlockSpawnBehavior(CustomBlock customBlock, Key toPlace, Boolean water, float chance, int heightLimit) {
    super(customBlock);
    this.water = water.booleanValue();
    this.toPlace = toPlace;
    this.chance = chance;
    this.heightLimit = heightLimit;
  }

  public Object getDefaultBlockState() {
    if (this.defaultBlockState != null)
      return this.defaultBlockState;
    Optional<CustomBlock> optionalCustomBlock = BukkitBlockManager.instance().blockById(this.toPlace);
    if (optionalCustomBlock.isPresent()) {
      CustomBlock customBlock = optionalCustomBlock.get();
      this.defaultBlockState = customBlock.defaultState().customBlockState().literalObject();
      this.defaultImmutableBlockState = customBlock.defaultState();
    } else {
      CraftEngine.instance().logger()
          .warn("Failed to create solid block " + String.valueOf(this.toPlace) + " in TearingBlockSpawnBehavior");
      this.defaultBlockState = MBlocks.STONE$defaultState;
      this.defaultImmutableBlockState = EmptyBlock.STATE;
    }
    return this.defaultBlockState;
  }

  public ImmutableBlockState defaultImmutableBlockState() {
    if (this.defaultImmutableBlockState == null)
      getDefaultBlockState();
    return this.defaultImmutableBlockState;
  }

  public BlockPos getTearingDripstone(Object level, BlockPos pos) {
    BukkitWorld world = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(level));
    int heightLimit = Math.min(this.heightLimit + pos.y(), 320);
    for (int y = pos.y(); y < heightLimit; y++) {
      BlockPos currentPos = new BlockPos(pos.x(), y, pos.z());
      BukkitExistingBlock blockInWorld = (BukkitExistingBlock) world.getBlock(currentPos.x(), currentPos.y(),
          currentPos.z());
      if (blockInWorld != null && blockInWorld.block().getType() != Material.AIR) {
        BlockData blockData = blockInWorld.block().getBlockData();
        if (blockData instanceof PointedDripstone) {
          PointedDripstone pDrip = (PointedDripstone) blockData;
          if (pDrip.getVerticalDirection() == BlockFace.DOWN)
            return currentPos;
        }
      }
    }
    return null;
  }

  public boolean canTear(BukkitWorld world, BlockPos pos) {
    BukkitExistingBlock blockInWorld = (BukkitExistingBlock) world.getBlock(pos.x(), pos.y(), pos.z());
    BlockData blockData = blockInWorld.block().getBlockData();

    if (!(blockData instanceof PointedDripstone pDripstone))
      return false;
    if (pDripstone.getVerticalDirection() != BlockFace.DOWN)
      return false;

    BlockPos highest = findHighestConnectedDripstone(pos, world);

    if (highest == null)
      return false;

    BlockPos fluidPos = highest.above().above();

    // Usar BukkitExistingBlock para verificar el estado del fluido
    BukkitExistingBlock fluidBlock = (BukkitExistingBlock) world.getBlock(fluidPos.x(), fluidPos.y(), fluidPos.z());
    BlockData fluidBlockData = fluidBlock.block().getBlockData();

    // Verificar si es agua directa o waterlogged
    boolean hasWater = fluidBlockData.getMaterial() == Material.WATER ||
        (fluidBlockData instanceof org.bukkit.block.data.Waterlogged waterlogged && waterlogged.isWaterlogged());

    // Verificar si es lava directa
    boolean hasLava = fluidBlockData.getMaterial() == Material.LAVA;

    return (water && hasWater) || (!water && hasLava);
  }

  public BlockPos findHighestConnectedDripstone(BlockPos start, BukkitWorld world) {
    BlockPos pos = start;
    while (true) {
      BlockPos above = pos.above();
      BukkitExistingBlock blockInWorld = (BukkitExistingBlock) world.getBlock(above.x(), above.y(), above.z());
      BlockData blockData = blockInWorld.block().getBlockData();
      if (blockData instanceof PointedDripstone) {
        PointedDripstone pDripstone = (PointedDripstone) blockData;
        if (pDripstone.getVerticalDirection() != BlockFace.DOWN)
          break;
        pos = above;
        continue;
      }
      break;
    }
    return pos;
  }

  public void randomTick(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
    Object level = args[1];
    BlockPos pos = LocationUtils.fromBlockPos(args[2]);
    if (RandomUtils.generateRandomFloat(0.0F, 1.0F) > this.chance)
      return;
    BukkitWorld world = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(level));
    BlockPos targetPos = pos.above();
    BukkitExistingBlock blockInWorld = (BukkitExistingBlock) world.getBlock(targetPos.x(), targetPos.y(),
        targetPos.z());
    if (blockInWorld != null && (blockInWorld.block().getType() != Material.AIR
        || blockInWorld.block().getType() == Material.POINTED_DRIPSTONE))
      return;
    BlockPos tearing = getTearingDripstone(level, targetPos);
    if (tearing == null)
      return;
    if (!canTear(world, tearing))
      return;
    placeBlock(level, targetPos);
  }

  public void placeBlock(Object level, BlockPos pos) {
    try {
      BukkitExistingBlock blockInWorld = (BukkitExistingBlock) (new BukkitWorld(
          FastNMS.INSTANCE.method$Level$getCraftWorld(level))).getBlock(pos.x(), pos.y(), pos.z());
      BlockData blockData = BlockStateUtils.fromBlockData(getDefaultBlockState());
      BlockState state = blockData.createBlockState();
      BlockFormEvent event = new BlockFormEvent(blockInWorld.block(), state);
      if (!event.callEvent())
        return;
      BukkitWorld world = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(level));
      FastNMS.INSTANCE.method$LevelWriter$setBlock(world.serverWorld(), LocationUtils.toBlockPos(pos),
          this.defaultImmutableBlockState.customBlockState().literalObject(), 3);
    } catch (Exception e) {
      CraftEngine.instance().logger().warn("Failed to update state for placement " + String.valueOf(pos), e);
    }
  }

  public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
    public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
      Boolean water = Boolean.valueOf(arguments.getOrDefault("liquid", "water").toString().equalsIgnoreCase("water"));
      Key toPlace = Key.from((String) arguments.getOrDefault("toPlace", "dripstone"));
      float chance = Float.parseFloat(arguments.getOrDefault("chance", Float.valueOf(0.011377778F)).toString());
      int heightLimit = Integer.parseInt(arguments.getOrDefault("heightLimit", Integer.valueOf(12)).toString());
      return (BlockBehavior) new TearingBlockSpawnBehavior(block, toPlace, water, chance, heightLimit);
    }
  }
}
