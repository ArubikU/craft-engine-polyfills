package dev.arubik.craftengine.block.behavior;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import net.momirealms.craftengine.bukkit.block.behavior.AbstractCanSurviveBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.BlockTags;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateOption;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.properties.BooleanProperty;
import net.momirealms.craftengine.core.block.properties.IntegerProperty;
import net.momirealms.craftengine.core.block.properties.Property;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.util.MiscUtils;
import net.momirealms.craftengine.core.util.ResourceConfigUtils;
import net.momirealms.craftengine.core.util.Tuple;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.ExistingBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

public class BushBlockBehavior extends AbstractCanSurviveBlockBehavior {
  public static final Factory FACTORY = new Factory();

  protected final List<Object> tagsCanSurviveOn;

  protected final Set<Object> blockStatesCanSurviveOn;

  protected final Set<String> customBlocksCansSurviveOn;

  protected final boolean blacklistMode;

  protected final boolean stackable;

  protected final boolean ageDirection;

  protected final IntegerProperty ageProperty;

  private BooleanProperty waterloggedProperty = BooleanProperty.create("waterlogged", false);

  public BushBlockBehavior(CustomBlock block, int delay, boolean blacklist, boolean stackable, boolean ageDirection,
      List<Object> tagsCanSurviveOn, Set<Object> blockStatesCanSurviveOn, Set<String> customBlocksCansSurviveOn,
      Property<Integer> ageProperty) {
    super(block, delay);
    this.blacklistMode = blacklist;
    this.stackable = stackable;
    this.ageDirection = ageDirection;
    this.tagsCanSurviveOn = tagsCanSurviveOn;
    this.blockStatesCanSurviveOn = blockStatesCanSurviveOn;
    this.customBlocksCansSurviveOn = customBlocksCansSurviveOn;
    this.ageProperty = (IntegerProperty) ageProperty;
  }

  public void setWaterlogged(BooleanProperty waterlogged) {
    this.waterloggedProperty = waterlogged;
  }

  public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
    public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
      Tuple<List<Object>, Set<Object>, Set<String>> tuple = BushBlockBehavior.readTagsAndState(arguments, false);
      boolean stackable = ResourceConfigUtils.getAsBoolean(arguments.getOrDefault("stackable", Boolean.valueOf(false)),
          "stackable");
      int delay = ResourceConfigUtils.getAsInt(arguments.getOrDefault("delay", Integer.valueOf(0)), "delay");
      boolean blacklistMode = ResourceConfigUtils
          .getAsBoolean(arguments.getOrDefault("blacklist", Boolean.valueOf(false)), "blacklist");
      boolean ageDirection = ResourceConfigUtils.getAsBoolean(
          Boolean.valueOf(arguments.getOrDefault("age-direction", "up").toString().equals("up")), "age-direction");
      Property<Integer> ageProperty = (Property<Integer>) ResourceConfigUtils
          .requireNonNullOrThrow(block.getProperty("age"), "warning.config.block.behavior.crop.missing_age");
      BushBlockBehavior bbb = new BushBlockBehavior(block, delay, blacklistMode, stackable, ageDirection,
          (List<Object>) tuple.left(), (Set<Object>) tuple.mid(), (Set<String>) tuple.right(), ageProperty);
      block.properties().forEach(p -> {
        if (p.name().equals("waterlogged") && p instanceof BooleanProperty) {
          BooleanProperty bp = (BooleanProperty) p;
          bbb.setWaterlogged(bp);
        }
      });
      return (BlockBehavior) bbb;
    }
  }

  public static Tuple<List<Object>, Set<Object>, Set<String>> readTagsAndState(Map<String, Object> arguments,
      boolean aboveOrBelow) {
    List<Object> mcTags = new ArrayList();
    for (String tag : MiscUtils.getAsStringList(arguments
        .getOrDefault((aboveOrBelow ? "above" : "bottom") + "-block-tags", List.of())))
      mcTags.add(BlockTags.getOrCreate(Key.of(tag)));
    Set<Object> mcBlocks = new HashSet();
    Set<String> customBlocks = new HashSet<>();
    for (String blockStateStr : MiscUtils
        .getAsStringList(arguments.getOrDefault((aboveOrBelow ? "above" : "bottom") + "-blocks", List.of()))) {
      int index = blockStateStr.indexOf('[');
      Key blockType = (index != -1) ? Key.from(blockStateStr.substring(0, index)) : Key.from(blockStateStr);
      Material material = (Material) Registry.MATERIAL.get(new NamespacedKey(blockType.namespace(), blockType.value()));
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

  protected boolean canSurvive(Object thisBlock, Object state, Object world, Object blockPos) throws Exception {
    int y = FastNMS.INSTANCE.field$Vec3i$y(blockPos);
    int x = FastNMS.INSTANCE.field$Vec3i$x(blockPos);
    int z = FastNMS.INSTANCE.field$Vec3i$z(blockPos);
    Object belowPos = FastNMS.INSTANCE.constructor$BlockPos(x, y - 1, z);
    Object belowState = FastNMS.INSTANCE.method$BlockGetter$getBlockState(world, belowPos);
    return mayPlaceOn(belowState, world, belowPos);
  }

  protected boolean mayPlaceOn(Object belowState, Object world, Object belowPos) {
    for (Object tag : this.tagsCanSurviveOn) {
      if (FastNMS.INSTANCE.method$BlockStateBase$is(belowState, tag))
        return !this.blacklistMode;
    }
    Optional<ImmutableBlockState> optionalCustomState = BlockStateUtils.getOptionalCustomBlockState(belowState);
    if (optionalCustomState.isEmpty()) {
      if (!this.blockStatesCanSurviveOn.isEmpty() && this.blockStatesCanSurviveOn.contains(belowState))
        return !this.blacklistMode;
    } else {
      ImmutableBlockState belowCustomState = optionalCustomState.get();
      if (belowCustomState.owner().value() == this.customBlock)
        return this.stackable;
      if (this.customBlocksCansSurviveOn.contains(((CustomBlock) belowCustomState.owner().value()).id().toString()))
        return !this.blacklistMode;
      if (this.customBlocksCansSurviveOn.contains(belowCustomState.toString()))
        return !this.blacklistMode;
    }
    return this.blacklistMode;
  }

  public void onPlace(Object thisBlock, Object[] args, Callable<Object> superMethod) {
    super.onPlace(thisBlock, args, superMethod);
    Object world = args[1];
    Object blockPos = args[2];
    if (this.ageProperty == null)
      return;
    Optional<ImmutableBlockState> optionalState = BlockStateUtils.getOptionalCustomBlockState(args[0]);
    if (optionalState.isEmpty())
      return;
    BlockPos hPos = LocationUtils.fromBlockPos(blockPos);
    hPos = hPos.offset(0, 1, 0);
    BukkitWorld bukkitWorld = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(world));
    ImmutableBlockState aboveState = bukkitWorld.getBlock(hPos.x(), hPos.y(), hPos.z()).customBlockState();
    if (aboveState != null && aboveState.owner().value() == this.customBlock)
      return;
    ImmutableBlockState currentState = optionalState.get();
    BlockPos pos = LocationUtils.fromBlockPos(blockPos);
    int tipAge = this.ageDirection ? this.ageProperty.max : this.ageProperty.min;
    ImmutableBlockState tipState = currentState.with((Property) this.ageProperty, Integer.valueOf(tipAge));
    FastNMS.INSTANCE.method$LevelWriter$setBlock(world, LocationUtils.toBlockPos(pos),
        tipState.customBlockState().literalObject(),
        UpdateOption.builder().updateImmediate().updateClients().build().flags());
    if (isTip(world, blockPos))
      updateBlocks(world, blockPos);
  }

  public boolean isTip(Object world, Object pos) {
    BukkitWorld bukkitWorld = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(world));
    BlockPos hPos = LocationUtils.fromBlockPos(pos);
    hPos = hPos.offset(0, 1, 0);
    ExistingBlock above = bukkitWorld.getBlock(hPos.x(), hPos.y(), hPos.z());
    if (above == null)
      return true;
    CustomBlock cb = above.customBlock();
    if (cb == null)
      return true;
    if (cb == this.customBlock)
      return false;
    return true;
  }

  public void onRemove(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
    Object level = args[1];
    Object pos = args[2];
    updateBlocks(level, pos);
  }

  public void affectNeighborsAfterRemoval(Object thisBlock, Object[] args, Callable<Object> superMethod)
      throws Exception {
    Object level = args[1];
    Object pos = args[2];
    updateBlocks(level, pos);
  }

  protected void updateBlocks(Object world, Object topPos) {
    BukkitWorld bukkitWorld = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(world));
    BlockPos pos = LocationUtils.fromBlockPos(topPos).offset(0, -1, 0);
    ImmutableBlockState tipState = null;
    if (bukkitWorld.getBlock(pos.x(), pos.y(), pos.z()).customBlock() != null)
      tipState = bukkitWorld.getBlock(pos.x(), pos.y(), pos.z()).customBlockState();
    int ageValue = this.ageDirection ? (this.ageProperty.max - 1) : (this.ageProperty.min + 1);
    while (true) {
      ExistingBlock biw = bukkitWorld.getBlock(pos.x(), pos.y(), pos.z());
      if (biw == null)
        break;
      CustomBlock cb = biw.customBlock();
      if (cb == null)
        break;
      ImmutableBlockState belowState = biw.customBlockState();
      if (belowState == null)
        break;
      if (belowState.owner().value() != this.customBlock)
        break;
      if (tipState == null)
        tipState = belowState;
      if (belowState.contains((Property) this.ageProperty))
        belowState = belowState.with((Property) this.ageProperty, Integer.valueOf(ageValue));
      if (belowState.contains((Property) this.waterloggedProperty)
          || tipState.contains((Property) this.waterloggedProperty))
        belowState = belowState.with((Property) this.waterloggedProperty,
            Boolean.valueOf((((Boolean) belowState.get((Property) this.waterloggedProperty)).booleanValue()
                || ((Boolean) tipState.get((Property) this.waterloggedProperty)).booleanValue())));
      FastNMS.INSTANCE.method$LevelWriter$setBlock(world, LocationUtils.toBlockPos(pos),
          belowState.customBlockState().literalObject(),
          UpdateOption.builder().updateImmediate().updateClients().build().flags());
      if (this.ageDirection) {
        if (ageValue > this.ageProperty.min)
          ageValue--;
      } else if (ageValue < this.ageProperty.max) {
        ageValue++;
      }
      pos = pos.offset(0, -1, 0);
    }
  }
}
