package dev.arubik.craftengine.block.behavior;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.momirealms.craftengine.bukkit.block.BukkitBlockManager;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitExistingBlock;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateOption;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.properties.Property;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.BlockPos;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import net.momirealms.craftengine.core.block.behavior.EntityBlockBehavior;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.world.CEWorld;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;

public class FanBlockBehavior extends BukkitBlockBehavior implements EntityBlockBehavior {
  public static final Factory FACTORY = new Factory();

  private final Property<Direction> facingProperty;

  private final Property<Boolean> poweredProperty;

  private final Property<Integer> powerLevelProperty;

  private static final Set<String> REDSTONE_SOURCE_IDS = Set.of("minecraft:redstone_block", "minecraft:redstone_torch",
      "minecraft:wall_redstone_torch");

  private static final Map<Direction, Vector> DIRECTION_VECTORS = Map.of(Direction.NORTH, new Vector(0, 0, -1),
      Direction.SOUTH, new Vector(0, 0, 1), Direction.WEST, new Vector(-1, 0, 0), Direction.EAST, new Vector(1, 0, 0),
      Direction.UP, new Vector(0, 1, 0), Direction.DOWN, new Vector(0, -1, 0));

  private final int tickDelay;

  private final Particle particle;

  private final int maxPushDistance;

  private final Set<Key> passableBlocks;

  private final boolean redstoneAffectsPush;

  public FanBlockBehavior(CustomBlock customBlock, Property<Direction> facing, Property<Boolean> powered,
      Property<Integer> powerLevel, int tickDelay, Particle particle, int maxPushDistance, Set<Key> passableBlocks,
      boolean redstoneAffectsPush) {
    super(customBlock);
    this.facingProperty = facing;
    this.poweredProperty = powered;
    this.powerLevelProperty = powerLevel;
    this.tickDelay = tickDelay;
    this.particle = particle;
    this.maxPushDistance = maxPushDistance;
    this.passableBlocks = passableBlocks;
    this.redstoneAffectsPush = redstoneAffectsPush;
  }

  @Override
  public <T extends BlockEntity> BlockEntityType<T> blockEntityType(ImmutableBlockState state) {
    @SuppressWarnings("unchecked")
    BlockEntityType<T> type = (BlockEntityType<T>) BukkitBlockEntityTypes.PERSISTENT_BLOCK_ENTITY_TYPE;
    return type;
  }

  @Override
  public BlockEntity createBlockEntity(net.momirealms.craftengine.core.world.BlockPos pos, ImmutableBlockState state) {
    return new PersistentBlockEntity(pos, state);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> createAsyncBlockEntityTicker(CEWorld world,
      ImmutableBlockState state, BlockEntityType<T> type) {
    if (type != blockEntityType(state))
      return null;

    return (lvl, cePos, ceState, be) -> {
      Object level = world.world().serverWorld();
      if (level == null)
        return;

      ImmutableBlockState blockState = ceState;
      if (blockState == null || blockState.isEmpty()
          || !((Boolean) blockState.get(this.poweredProperty)).booleanValue())
        return;

      Direction facing = (Direction) blockState.get(this.facingProperty);
      BukkitWorld bukkitWorld = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(level));
      BlockPos pos = cePos;
      int pushStrength = getPushStrength(blockState);
      for (int i = 1; i <= this.maxPushDistance; i++) {
        BlockPos targetPos = pos.relative(facing, i);
        BukkitExistingBlock blockInWorld = (BukkitExistingBlock) bukkitWorld.getBlock(targetPos.x(), targetPos.y(),
            targetPos.z());
        if (!isPassable(blockInWorld))
          break;
        applyPushAndParticles(bukkitWorld, targetPos, facing, pushStrength);
      }
    };
  }

  public void onPlace(Object thisBlock, Object[] args, Callable<Object> superMethod) {
    Object state = args[0];
    Object world = args[1];
    Object blockPos = args[2];
    updateActivationFromNearbyRedstone(state, world, blockPos);
  }

  public void tick(Object thisBlock, Object[] args, Callable<Object> superMethod) {
    // Entity ticker handles ticking now
  }

  private int getPushStrength(ImmutableBlockState blockState) {
    if (this.redstoneAffectsPush && this.powerLevelProperty != null)
      return Math.max(1, ((Integer) blockState.get(this.powerLevelProperty)).intValue());
    return 1;
  }

  private boolean isPassable(BukkitExistingBlock blockInWorld) {
    String blockId = getBlockId(blockInWorld);
    return (this.passableBlocks.contains(Key.of(blockId)) || !blockInWorld.block().getType().isSolid());
  }

  private void applyPushAndParticles(BukkitWorld world, BlockPos targetPos, Direction facing, int pushStrength) {
    World bukkitWorld = world.platformWorld();
    double cx = targetPos.x() + 0.5D;
    double cy = targetPos.y() + 0.5D;
    double cz = targetPos.z() + 0.5D;
    Vector pushVector = ((Vector) DIRECTION_VECTORS.getOrDefault(facing, new Vector(0, 0, 0))).clone()
        .multiply(pushStrength * 0.1D);
    double jitter = 0.2D;
    double rx = randomOffset(jitter);
    double ry = randomOffset(jitter);
    double rz = randomOffset(jitter);
    bukkitWorld.spawnParticle(this.particle, cx + rx, cy + ry, cz + rz, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    bukkitWorld.getNearbyEntities(new BoundingBox(cx - 0.5D, cy - 0.5D, cz - 0.5D, cx + 0.5D, cy + 0.5D, cz + 0.5D))

        .forEach(entity -> entity.setVelocity(entity.getVelocity().add(pushVector)));
  }

  private double randomOffset(double range) {
    return (ThreadLocalRandom.current().nextDouble() * 2.0D - 1.0D) * range;
  }

  private String getBlockId(BukkitExistingBlock block) {
    return (block.customBlock() != null) ? block.customBlock().id().toString()
        : block.block().getType().getKey().toString();
  }

  private void updateActivationFromNearbyRedstone(Object stateObj, Object level, Object posObj) {
    ImmutableBlockState blockState = BukkitBlockManager.instance()
        .getImmutableBlockState(BlockStateUtils.blockStateToId(stateObj));
    if (blockState == null || blockState.isEmpty())
      return;
    RedstoneInfo info = analyzeRedstone(level, posObj);
    boolean shouldPower = info.hasPower();
    int targetPowerLevel = shouldPower ? info.maxNeighborPower : 0;
    if (needsUpdate(blockState, shouldPower, targetPowerLevel)) {
      ImmutableBlockState newState = blockState.with(this.poweredProperty, Boolean.valueOf(shouldPower));
      if (this.powerLevelProperty != null)
        newState = newState.with(this.powerLevelProperty, Integer.valueOf(targetPowerLevel));
      FastNMS.INSTANCE.method$LevelWriter$setBlock(level, posObj, newState.customBlockState().literalObject(),
          UpdateOption.UPDATE_ALL.flags());
    }
  }

  public void neighborChanged(Object thisBlock, Object[] args, Callable<Object> superMethod) {
    Object state = args[0];
    Object level = args[1];
    Object posObj = args[2];
    ImmutableBlockState blockState = BukkitBlockManager.instance()
        .getImmutableBlockState(BlockStateUtils.blockStateToId(state));
    if (blockState == null || blockState.isEmpty())
      return;
    RedstoneInfo info = analyzeRedstone(level, posObj);
    boolean shouldPower = info.hasPower();
    int targetPowerLevel = shouldPower ? info.maxNeighborPower : 0;
    if (needsUpdate(blockState, shouldPower, targetPowerLevel)) {
      ImmutableBlockState newState = blockState.with(this.poweredProperty, Boolean.valueOf(shouldPower));
      if (this.powerLevelProperty != null)
        newState = newState.with(this.powerLevelProperty, Integer.valueOf(targetPowerLevel));
      FastNMS.INSTANCE.method$LevelWriter$setBlock(level, posObj, newState.customBlockState().literalObject(),
          UpdateOption.UPDATE_ALL.flags());
      blockState = newState;
    }
    if (!info.hasPower()) {
      ImmutableBlockState newState = blockState.with(this.poweredProperty, Boolean.valueOf(false));
      if (this.powerLevelProperty != null)
        newState = newState.with(this.powerLevelProperty, Integer.valueOf(0));
      FastNMS.INSTANCE.method$LevelWriter$setBlock(level, posObj, newState.customBlockState().literalObject(),
          UpdateOption.UPDATE_ALL.flags());
    }
  }

  private boolean needsUpdate(ImmutableBlockState state, boolean shouldPower, int targetPowerLevel) {
    if (((Boolean) state.get(this.poweredProperty)).booleanValue() != shouldPower)
      return true;
    if (this.powerLevelProperty != null
        && !Objects.equals(state.get(this.powerLevelProperty), Integer.valueOf(targetPowerLevel)))
      return true;
    return false;
  }

  private RedstoneInfo analyzeRedstone(Object level, Object posObj) {
    BukkitWorld world = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(level));
    BlockPos pos = LocationUtils.fromBlockPos(posObj);
    int maxNeighborPower = 0;
    boolean foundDirectSource = false;
    for (Direction dir : Direction.values()) {
      BukkitExistingBlock neighbor = (BukkitExistingBlock) world.getBlock(pos.relative(dir).x(), pos.relative(dir).y(),
          pos.relative(dir).z());
      if (neighbor != null) {
        maxNeighborPower = Math.max(maxNeighborPower, neighbor.block().getBlockPower());
        if (REDSTONE_SOURCE_IDS.contains(getBlockId(neighbor)))
          foundDirectSource = true;
      }
    }
    boolean hasNeighborSignal = FastNMS.INSTANCE.method$SignalGetter$hasNeighborSignal(level, posObj);
    return new RedstoneInfo(maxNeighborPower, foundDirectSource, hasNeighborSignal);
  }

  private static record RedstoneInfo(int maxNeighborPower, boolean foundDirectSource, boolean hasNeighborSignal) {
    boolean hasPower() {
      return (this.foundDirectSource || this.hasNeighborSignal || this.maxNeighborPower > 0);
    }
  }

  public boolean isSignalSource(Object thisBlock, Object[] args, Callable<Object> superMethod) {
    return true;
  }

  public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
    public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
      Property<Direction> facing = (Property<Direction>) block.getProperty("facing");
      Property<Boolean> powered = (Property<Boolean>) block.getProperty("powered");
      Property<Integer> powerLevel = (Property<Integer>) block.getProperty("power");
      if (facing == null)
        throw new IllegalArgumentException("Falta propiedad 'facing'");
      if (powered == null)
        throw new IllegalArgumentException("Falta propiedad 'powered'");
      int tickDelay = Integer.parseInt(arguments.getOrDefault("tickDelay", Integer.valueOf(10)).toString());
      Particle particle = Particle.valueOf(arguments.getOrDefault("particle", "CLOUD").toString().toUpperCase());
      int maxPushDistance = Integer.parseInt(arguments.getOrDefault("maxPushDistance", Integer.valueOf(5)).toString());
      @SuppressWarnings("unchecked")
      List<String> passableBlocksList = (List<String>) arguments.getOrDefault("passableBlocks",
          List.of("minecraft:air"));
      Set<Key> passableBlocks = passableBlocksList.stream()
          .map(s -> Key.of(s))
          .collect(Collectors.toCollection(it.unimi.dsi.fastutil.objects.ObjectOpenHashSet::new));
      boolean redstoneAffectsPush = Boolean
          .parseBoolean(arguments.getOrDefault("redstoneAffectsPush", Boolean.valueOf(true)).toString());
      return (BlockBehavior) new FanBlockBehavior(block, facing, powered, powerLevel, tickDelay, particle,
          maxPushDistance, passableBlocks, redstoneAffectsPush);
    }
  }
}
