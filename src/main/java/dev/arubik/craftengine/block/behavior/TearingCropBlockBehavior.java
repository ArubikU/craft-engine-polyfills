package dev.arubik.craftengine.block.behavior;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.BlockTags;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitExistingBlock;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.property.IntegerProperty;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.util.random.RandomUtils;
import net.momirealms.craftengine.core.util.Tuple;
import net.momirealms.craftengine.core.world.BlockPos;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.PointedDripstone;

public class TearingCropBlockBehavior extends BukkitBlockBehavior {
  public static final Factory FACTORY = new Factory();

  private final boolean water;

  private final IntegerProperty ageProperty;

  private final float growSpeed;

  private final int heightLimit;

  protected final List<Object> tagsCanSurviveOn;

  protected final Set<Object> blockStatesCanSurviveOn;

  protected final Set<String> customBlocksCansSurviveOn;

  protected final boolean blacklistMode;

  public TearingCropBlockBehavior(BlockDefinition block, Property<Integer> ageProperty, float growSpeed, boolean water,
      int heightLimit, boolean blacklist, List<Object> tagsCanSurviveOn, Set<Object> blockStatesCanSurviveOn,
      Set<String> customBlocksCansSurviveOn) {
    super(block);
    this.ageProperty = (IntegerProperty) ageProperty;
    this.growSpeed = growSpeed;
    this.water = water;
    this.heightLimit = heightLimit;
    this.blacklistMode = blacklist;
    this.tagsCanSurviveOn = tagsCanSurviveOn;
    this.blockStatesCanSurviveOn = blockStatesCanSurviveOn;
    this.customBlocksCansSurviveOn = customBlocksCansSurviveOn;
  }

  public final int getAge(ImmutableBlockState state) {
    return ((Integer) state.get((Property) this.ageProperty)).intValue();
  }

  public boolean isMaxAge(ImmutableBlockState state) {
    return (((Integer) state.get((Property) this.ageProperty)).intValue() == this.ageProperty.max);
  }

  public float growSpeed() {
    return this.growSpeed;
  }

  protected boolean mayPlaceOn(Object belowState, Object world, Object belowPos) {
    for (Object tag : this.tagsCanSurviveOn) {
      if (((net.minecraft.world.level.block.state.BlockState) belowState)
          .is((net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block>) tag))
        return !this.blacklistMode;
    }
    Optional<ImmutableBlockState> optionalCustomState = BlockStateUtils.getOptionalCustomBlockState(belowState);
    if (optionalCustomState.isEmpty()) {
      if (!this.blockStatesCanSurviveOn.isEmpty() && this.blockStatesCanSurviveOn.contains(belowState))
        return !this.blacklistMode;
    } else {
      ImmutableBlockState belowCustomState = optionalCustomState.get();
      if (this.customBlocksCansSurviveOn.contains(belowCustomState.owner().value().id().toString()))
        return !this.blacklistMode;
      if (this.customBlocksCansSurviveOn.contains(belowCustomState.toString()))
        return !this.blacklistMode;
    }
    return this.blacklistMode;
  }

  public BlockPos getTearingDripstone(Object level, BlockPos pos) {
    BukkitWorld world = new BukkitWorld(((net.minecraft.server.level.ServerLevel) level).getWorld());
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

  @Override
  public void randomTick(Object thisBlock, Object[] args) {
    if (RandomUtils.generateRandomFloat(0.0F, 1.0F) >= this.growSpeed)
      return;
    Object level = args[1];
    BlockPos pos = LocationUtils.fromBlockPos(args[2]);
    int y = pos.y();
    int x = pos.x();
    int z = pos.z();
    Object belowPos = new net.minecraft.core.BlockPos(x, y - 1, z);
    Object belowState = ((net.minecraft.world.level.BlockGetter) level)
        .getBlockState((net.minecraft.core.BlockPos) belowPos);
    if (!mayPlaceOn(belowState, level, belowPos))
      return;
    BukkitWorld world = new BukkitWorld(((net.minecraft.server.level.ServerLevel) level).getWorld());
    BlockPos targetPos = pos.above();
    BlockPos tearing = getTearingDripstone(level, targetPos);
    if (tearing == null)
      return;
    if (!canTear(world, tearing))
      return;
    BukkitExistingBlock block = (BukkitExistingBlock) world.getBlock(pos.x(), pos.y(), pos.z());
    int age = getAge(block.customBlockState());
    if (age < this.ageProperty.max) {
      BukkitExistingBlock blockInWorld = (BukkitExistingBlock) world.getBlock(pos.x(), pos.y(), pos.z());
      blockInWorld.block().setBlockData(BlockStateUtils.fromBlockData(block.customBlock().defaultState()
          .with((Property) this.ageProperty, Integer.valueOf(age + 1)).customBlockState().minecraftState()));
    }
  }

  public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
    public static Tuple<List<Object>, Set<Object>, Set<String>> readTagsAndState(ConfigSection arguments,
        boolean aboveOrBelow) {
      List<Object> mcTags = new ArrayList();
      for (String tag : dev.arubik.craftengine.util.Utils.getAsStringList(arguments
          .getOrDefault((aboveOrBelow ? "above" : "bottom") + "-block-tags", List.of())))
        mcTags.add(BlockTags.getOrCreate(Key.of(tag)));
      Set<Object> mcBlocks = new HashSet();
      Set<String> customBlocks = new HashSet<>();
      for (String blockStateStr : dev.arubik.craftengine.util.Utils
          .getAsStringList(arguments.getOrDefault((aboveOrBelow ? "above" : "bottom") + "-blocks", List.of()))) {
        int index = blockStateStr.indexOf('[');
        Key blockType = (index != -1) ? Key.from(blockStateStr.substring(0, index)) : Key.from(blockStateStr);
        Material material = (Material) Registry.MATERIAL
            .get(new NamespacedKey(blockType.namespace(), blockType.value()));
        if (material != null) {
          if (index == -1) {
            mcBlocks.addAll(BlockStateUtils.getPossibleBlockStates(blockType));
            continue;
          }
          mcBlocks.add(BlockStateUtils.blockDataToBlockState(Bukkit.createBlockData(blockStateStr)));
          continue;
        }
        customBlocks.add(blockStateStr);
      }
      return new Tuple(mcTags, mcBlocks, customBlocks);
    }

    public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
      Property<Integer> ageProperty = (Property<Integer>) dev.arubik.craftengine.util.Utils
          .requireNonNullOrThrow(block.getProperty("age"), "warning.config.block.behavior.crop.missing_age");
      float growSpeed = dev.arubik.craftengine.util.Utils.getAsFloat(arguments.getOrDefault("grow-speed", Float.valueOf(0.125F)),
          "grow-speed");
      Boolean water = Boolean.valueOf(arguments.getOrDefault("liquid", "water").toString().equalsIgnoreCase("water"));
      int heightLimit = Integer.parseInt(arguments.getOrDefault("heightLimit", Integer.valueOf(12)).toString());
      Tuple<List<Object>, Set<Object>, Set<String>> tuple = readTagsAndState(arguments, false);
      boolean blacklistMode = dev.arubik.craftengine.util.Utils
          .getAsBoolean(arguments.getOrDefault("blacklist", Boolean.valueOf(false)), "blacklist");
      return (BlockBehavior) new TearingCropBlockBehavior(block, ageProperty, growSpeed, water.booleanValue(),
          heightLimit, blacklistMode, (List<Object>) tuple.left(), (Set<Object>) tuple.mid(),
          (Set<String>) tuple.right());
    }
  }
}
