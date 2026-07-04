package dev.arubik.craftengine.block.behavior;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import net.momirealms.craftengine.bukkit.block.BukkitBlockManager;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitExistingBlock;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateFlags;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.BlockPos;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.world.CEWorld;

import dev.arubik.craftengine.gas.GasCarrier;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasTransferHelper;
import dev.arubik.craftengine.gas.GasType;

/**
 * Gas-powered Copper Fan (Create encased-fan style).
 *
 * <p>The fan no longer runs on redstone alone — it BLOWS only while it has gas in its internal
 * buffer. Each tick it pulls gas from the carrier on its BACK face ({@code facing.getOpposite()}),
 * buffers it, and drains a little while blowing. The gas TYPE in the buffer selects a tier:</p>
 * <ul>
 *   <li>{@code STEAM} — base push + base range, light CLOUD airflow, SMELTING (furnace) processing.</li>
 *   <li>{@code HEAVY_STEAM} — stronger push (×{@code heavyPushMul}) + longer range
 *       (+{@code heavyRangeBonus}), denser SMOKE airflow, BLASTING processing, costs a bit more gas.</li>
 * </ul>
 *
 * <p>Redstone is an OFF switch (matches the pump/conveyor): NO signal = ON, signal = PAUSE. The
 * {@code powered} blockstate reflects the ACTUAL blowing state (driven by the controller each tick),
 * so the existing on/off leaves-host appearances keep working.</p>
 *
 * <p>Along the airflow column the fan also PROCESSES dropped items via VANILLA furnace/blasting
 * recipes (no custom recipe files): after {@code processingInterval} ticks of an item sitting in the
 * airflow, ONE item from the stack is converted to the recipe output and the input shrinks by 1.</p>
 *
 * <p>Backward compatible: if NO gas keys are present in the config the fan keeps its OLD redstone-only
 * push behaviour ({@code powered} driven by redstone, no buffer, no processing).</p>
 */
public class FanBlockBehavior extends BukkitBlockBehavior implements EntityBlock {
  public static final Factory FACTORY = new Factory();

  private final Property<Direction> facingProperty;
  private final Property<Boolean> poweredProperty;

  private static final Set<String> REDSTONE_SOURCE_IDS = Set.of("minecraft:redstone_block",
      "minecraft:redstone_torch", "minecraft:wall_redstone_torch");

  private static final Map<Direction, Vector> DIRECTION_VECTORS = Map.of(Direction.NORTH, new Vector(0, 0, -1),
      Direction.SOUTH, new Vector(0, 0, 1), Direction.WEST, new Vector(-1, 0, 0), Direction.EAST, new Vector(1, 0, 0),
      Direction.UP, new Vector(0, 1, 0), Direction.DOWN, new Vector(0, -1, 0));

  // --- common knobs ---
  private final int tickDelay;
  private final Particle particle; // legacy / steam particle
  private final int maxPushDistance;
  private final Set<Key> passableBlocks;

  // --- gas mode knobs ---
  private final boolean gasMode;
  private final GasTank buffer;
  private final int intakePerTick;
  private final int gasPerTick;
  private final double heavyPushMul;
  private final int heavyRangeBonus;
  private final double heavyGasMul;
  private final int processingInterval;
  private final Particle steamParticle;
  private final Particle heavyParticle;

  public FanBlockBehavior(BlockDefinition customBlock, Property<Direction> facing, Property<Boolean> powered,
      int tickDelay, Particle particle, int maxPushDistance, Set<Key> passableBlocks,
      boolean gasMode, int gasBuffer, int intakePerTick, int gasPerTick, double heavyPushMul, int heavyRangeBonus,
      double heavyGasMul, int processingInterval, Particle steamParticle, Particle heavyParticle) {
    super(customBlock);
    this.facingProperty = facing;
    this.poweredProperty = powered;
    this.tickDelay = tickDelay;
    this.particle = particle;
    this.maxPushDistance = maxPushDistance;
    this.passableBlocks = passableBlocks;
    this.gasMode = gasMode;
    this.buffer = new GasTank("fan_buffer", gasBuffer);
    this.intakePerTick = intakePerTick;
    this.gasPerTick = gasPerTick;
    this.heavyPushMul = heavyPushMul;
    this.heavyRangeBonus = heavyRangeBonus;
    this.heavyGasMul = heavyGasMul;
    this.processingInterval = processingInterval;
    this.steamParticle = steamParticle;
    this.heavyParticle = heavyParticle;
  }

  private int controllerId;

  @Override
  public void initControllerId(int id) {
    this.controllerId = id;
  }

  @Override
  public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
    return new FanController(blockEntity, this);
  }

  /** Controller that drives the fan's per-tick intake / push / processing. */
  public static class FanController extends BlockEntityController {
    private final FanBlockBehavior behavior;
    private int delayCounter = 0;
    // transient per-item processing progress (entityId -> ticks in airflow while active)
    private final java.util.HashMap<Integer, Integer> progress = new java.util.HashMap<>();

    public FanController(BlockEntity blockEntity, FanBlockBehavior behavior) {
      super(blockEntity);
      this.behavior = behavior;
    }

    @Override
    public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(CEWorld world,
        ImmutableBlockState state) {
      @SuppressWarnings("unchecked")
      BlockEntityTicker<C> ticker = (BlockEntityTicker<C>) BlockEntityController
          .createTickerHelper((BlockEntityTicker<FanController>) FanController::tick);
      return ticker;
    }

    public static void tick(CEWorld world, BlockPos cePos, ImmutableBlockState ceState, FanController self) {
      FanBlockBehavior behavior = self.behavior;
      Object level = world.world().minecraftWorld();
      if (level == null)
        return;
      net.minecraft.server.level.ServerLevel serverLevel = (net.minecraft.server.level.ServerLevel) level;

      ImmutableBlockState blockState = ceState;
      if (blockState == null || blockState.isEmpty())
        return;

      // Throttle to tickDelay.
      if (self.delayCounter > 0) {
        self.delayCounter--;
        return;
      }
      self.delayCounter = Math.max(0, behavior.tickDelay - 1);

      Direction facing = (Direction) blockState.get(behavior.facingProperty);
      BukkitWorld bukkitWorld = new BukkitWorld(serverLevel.getWorld());
      net.minecraft.core.BlockPos nmsPos = new net.minecraft.core.BlockPos(cePos.x(), cePos.y(), cePos.z());

      // ---------------- LEGACY redstone-only mode ----------------
      if (!behavior.gasMode) {
        boolean powered = ((Boolean) blockState.get(behavior.poweredProperty)).booleanValue();
        if (!powered)
          return;
        int pushStrength = 1;
        for (int i = 1; i <= behavior.maxPushDistance; i++) {
          BlockPos targetPos = cePos.relative(facing, i);
          BukkitExistingBlock biw = (BukkitExistingBlock) bukkitWorld.getBlock(targetPos.x(), targetPos.y(),
              targetPos.z());
          if (!behavior.isPassable(biw))
            break;
          behavior.applyPushAndParticles(bukkitWorld, targetPos, facing, pushStrength, behavior.particle);
        }
        return;
      }

      // ---------------- GAS mode ----------------
      // 1) Redstone OFF switch: signal present => paused.
      boolean redstoneOff = ((net.minecraft.world.level.SignalGetter) serverLevel).hasNeighborSignal(nmsPos);

      // 2) Intake gas from the carrier on the BACK face into the buffer.
      Direction back = facing.opposite();
      net.minecraft.core.Direction nmsBack = toNms(back);
      behavior.intake(serverLevel, nmsPos, nmsBack);

      // 3) Read buffer.
      GasStack stored = behavior.buffer.getGas(serverLevel, nmsPos);
      boolean gasAvailable = stored != null && !stored.isEmpty();
      GasType tier = gasAvailable ? stored.getType() : GasType.EMPTY;

      boolean blowing = gasAvailable && !redstoneOff && tier != GasType.EMPTY;

      // 4) Keep the `powered` visual state in sync with the actual blowing state.
      if (((Boolean) blockState.get(behavior.poweredProperty)).booleanValue() != blowing) {
        ImmutableBlockState newState = blockState.with(behavior.poweredProperty, Boolean.valueOf(blowing));
        ((net.minecraft.world.level.LevelWriter) serverLevel).setBlock(nmsPos,
            (net.minecraft.world.level.block.state.BlockState) newState.customBlockState().minecraftState(),
            UpdateFlags.UPDATE_ALL);
      }

      if (!blowing) {
        self.progress.clear();
        return;
      }

      // 5) Tier table.
      boolean heavy = tier == GasType.HEAVY_STEAM;
      int range = behavior.maxPushDistance + (heavy ? behavior.heavyRangeBonus : 0);
      double pushMul = heavy ? behavior.heavyPushMul : 1.0;
      Particle p = heavy ? behavior.heavyParticle : behavior.steamParticle;
      int drain = (int) Math.max(1, Math.round(behavior.gasPerTick * (heavy ? behavior.heavyGasMul : 1.0)));

      // 6) Push + process along the airflow column.
      java.util.HashMap<Integer, Integer> seen = new java.util.HashMap<>();
      for (int i = 1; i <= range; i++) {
        BlockPos targetPos = cePos.relative(facing, i);
        BukkitExistingBlock biw = (BukkitExistingBlock) bukkitWorld.getBlock(targetPos.x(), targetPos.y(),
            targetPos.z());
        if (!behavior.isPassable(biw))
          break;
        behavior.applyPushAndParticles(bukkitWorld, targetPos, facing, (int) Math.max(1, Math.round(pushMul)), p);
        behavior.processItems(serverLevel, targetPos, heavy, self.progress, seen);
      }
      // Drop progress entries for items that left the airflow.
      self.progress.keySet().retainAll(seen.keySet());
      self.progress.putAll(seen);

      // 7) Consume gas for this blowing tick.
      behavior.buffer.extract(serverLevel, nmsPos, drain, null);
    }
  }

  // ---------------- gas intake ----------------

  /** Pull gas from the carrier behind the fan into the internal buffer. */
  private void intake(net.minecraft.server.level.ServerLevel level, net.minecraft.core.BlockPos pos,
      net.minecraft.core.Direction back) {
    net.minecraft.core.BlockPos source = pos.relative(back);
    GasCarrier carrier = GasTransferHelper.getCarrier(level, source).orElse(null);
    if (carrier == null)
      return;
    GasStack avail = carrier.getStoredGas(level, source);
    if (avail == null || avail.isEmpty())
      return;
    GasStack cur = buffer.getGas(level, pos);
    // Don't mix types: only intake if buffer empty or same type.
    if (cur != null && !cur.isEmpty() && cur.getType() != avail.getType())
      return;
    int want = Math.min(intakePerTick, buffer.getCapacity() - (cur == null || cur.isEmpty() ? 0 : cur.getAmount()));
    if (want <= 0)
      return;
    carrier.extractGas(level, source, want, extracted -> {
      if (extracted == null || extracted.isEmpty())
        return;
      int inserted = buffer.insert(level, pos, new GasStack(extracted.getType(), extracted.getAmount()));
      int unused = extracted.getAmount() - inserted;
      if (unused > 0)
        carrier.insertGas(level, source, new GasStack(extracted.getType(), unused), back.getOpposite());
    }, back.getOpposite());
  }

  // ---------------- item processing (vanilla recipes) ----------------

  private void processItems(net.minecraft.server.level.ServerLevel level, BlockPos targetPos, boolean heavy,
      Map<Integer, Integer> progress, Map<Integer, Integer> seen) {
    double cx = targetPos.x() + 0.5D;
    double cy = targetPos.y() + 0.5D;
    double cz = targetPos.z() + 0.5D;
    net.minecraft.world.phys.AABB aabb = new net.minecraft.world.phys.AABB(cx - 0.5D, cy - 0.5D, cz - 0.5D,
        cx + 0.5D, cy + 0.5D, cz + 0.5D);
    for (net.minecraft.world.entity.item.ItemEntity item : level.getEntitiesOfClass(
        net.minecraft.world.entity.item.ItemEntity.class, aabb, e -> !e.isRemoved())) {
      int id = item.getId();
      net.minecraft.world.item.ItemStack stack = item.getItem();
      if (stack == null || stack.isEmpty())
        continue;
      net.minecraft.world.item.ItemStack output = smeltResult(level, stack, heavy);
      if (output == null || output.isEmpty()) {
        // No matching recipe: just gets blown normally, no progress tracking.
        continue;
      }
      int ticks = progress.getOrDefault(id, 0) + Math.max(1, tickDelay);
      if (ticks >= processingInterval) {
        // Convert ONE item from the stack.
        net.minecraft.world.item.ItemStack out = output.copy();
        out.setCount(output.getCount());
        item.setItem(shrinkOne(stack));
        if (stack.getCount() - 1 <= 0)
          item.discard();
        net.minecraft.world.entity.item.ItemEntity spawned = new net.minecraft.world.entity.item.ItemEntity(
            level, cx, cy, cz, out);
        spawned.setDeltaMovement(0, 0, 0);
        spawned.setDefaultPickUpDelay();
        level.addFreshEntity(spawned);
        seen.put(id, 0);
      } else {
        seen.put(id, ticks);
      }
    }
  }

  private static net.minecraft.world.item.ItemStack shrinkOne(net.minecraft.world.item.ItemStack stack) {
    net.minecraft.world.item.ItemStack copy = stack.copy();
    copy.shrink(1);
    return copy;
  }

  /** Vanilla furnace (STEAM) / blasting (HEAVY_STEAM) result for a single item, or null. */
  private static net.minecraft.world.item.ItemStack smeltResult(net.minecraft.server.level.ServerLevel level,
      net.minecraft.world.item.ItemStack stack, boolean heavy) {
    try {
      net.minecraft.world.item.ItemStack single = stack.copy();
      single.setCount(1);
      net.minecraft.world.item.crafting.SingleRecipeInput input =
          new net.minecraft.world.item.crafting.SingleRecipeInput(single);
      net.minecraft.world.item.crafting.RecipeManager rm = level.getServer().getRecipeManager();
      if (heavy) {
        java.util.Optional<net.minecraft.world.item.crafting.RecipeHolder<
            net.minecraft.world.item.crafting.BlastingRecipe>> r =
            rm.getRecipeFor(net.minecraft.world.item.crafting.RecipeType.BLASTING, input, level);
        if (r.isPresent())
          return r.get().value().assemble(input, level.registryAccess());
      } else {
        java.util.Optional<net.minecraft.world.item.crafting.RecipeHolder<
            net.minecraft.world.item.crafting.SmeltingRecipe>> r =
            rm.getRecipeFor(net.minecraft.world.item.crafting.RecipeType.SMELTING, input, level);
        if (r.isPresent())
          return r.get().value().assemble(input, level.registryAccess());
      }
    } catch (Throwable ignored) {
      // Null-safe: any NMS hiccup just means "no processing".
    }
    return null;
  }

  // ---------------- push / particles (shared NMS path) ----------------

  @Override
  public void onPlace(Object thisBlock, Object[] args) {
    if (gasMode)
      return; // gas fans drive `powered` from the controller, not redstone-on
    Object state = args[0];
    Object world = args[1];
    Object blockPos = args[2];
    updateActivationFromNearbyRedstone(state, world, blockPos);
  }

  @Override
  public void tick(Object thisBlock, Object[] args) {
    // Entity ticker handles ticking now
  }

  private boolean isPassable(BukkitExistingBlock blockInWorld) {
    String blockId = getBlockId(blockInWorld);
    return (this.passableBlocks.contains(Key.of(blockId)) || !blockInWorld.block().getType().isSolid());
  }

  private void applyPushAndParticles(BukkitWorld world, BlockPos targetPos, Direction facing, int pushStrength,
      Particle particle) {
    World bukkitWorld = world.platformWorld();
    double cx = targetPos.x() + 0.5D;
    double cy = targetPos.y() + 0.5D;
    double cz = targetPos.z() + 0.5D;
    Vector pushVector = ((Vector) DIRECTION_VECTORS.getOrDefault(facing, new Vector(0, 0, 0))).clone()
        .multiply(pushStrength * 0.1D);
    double jitter = 0.2D;
    net.minecraft.server.level.ServerLevel serverLevel = ((org.bukkit.craftbukkit.CraftWorld) bukkitWorld).getHandle();
    // NMS addParticle (not Bukkit World#spawnParticle) so a contraption's own ContraptionLevel
    // override can redirect this to the real world at the bearing's live transform — Bukkit's
    // World#spawnParticle bypasses NMS Level entirely and can't be intercepted that way.
    net.minecraft.core.particles.ParticleOptions nmsParticle =
        org.bukkit.craftbukkit.CraftParticle.createParticleParam(particle, null);
    serverLevel.addParticle(nmsParticle, cx + randomOffset(jitter), cy + randomOffset(jitter),
        cz + randomOffset(jitter), 0.0D, 0.0D, 0.0D);
    net.minecraft.world.phys.AABB aabb = new net.minecraft.world.phys.AABB(cx - 0.5D, cy - 0.5D, cz - 0.5D, cx + 0.5D,
        cy + 0.5D, cz + 0.5D);
    for (net.minecraft.world.entity.Entity nms : serverLevel
        .getEntitiesOfClass(net.minecraft.world.entity.Entity.class, aabb, e -> !e.isRemoved())) {
      org.bukkit.entity.Entity entity = nms.getBukkitEntity();
      entity.setVelocity(entity.getVelocity().add(pushVector));
    }

    // getEntitiesOfClass above only ever sees entities physically inside serverLevel — if the fan
    // lives inside a contraption's own mini ContraptionLevel, that never includes real-world
    // entities (e.g. a player standing near the flying contraption), so also push those, at the
    // real-world-equivalent position/direction from the bearing's live transform.
    if (serverLevel instanceof dev.arubik.craftengine.contraption.level.ContraptionLevel contraptionLevel) {
      net.minecraft.core.BlockPos nmsTargetPos = new net.minecraft.core.BlockPos(targetPos.x(), targetPos.y(), targetPos.z());
      net.minecraft.world.phys.Vec3 realCenter = contraptionLevel.realWorldPositionOf(nmsTargetPos);
      net.minecraft.world.phys.Vec3 realPush = contraptionLevel
          .rotateToRealWorld(new net.minecraft.world.phys.Vec3(pushVector.getX(), pushVector.getY(), pushVector.getZ()));
      Vector realPushVector = new Vector(realPush.x, realPush.y, realPush.z);
      net.minecraft.world.phys.AABB realAabb = new net.minecraft.world.phys.AABB(
          realCenter.x - 0.5D, realCenter.y - 0.5D, realCenter.z - 0.5D,
          realCenter.x + 0.5D, realCenter.y + 0.5D, realCenter.z + 0.5D);
      net.minecraft.world.level.Level realNmsLevel = contraptionLevel.realLevel();
      if (realNmsLevel instanceof net.minecraft.server.level.ServerLevel realServerLevel) {
        for (net.minecraft.world.entity.Entity nms : realServerLevel
            .getEntitiesOfClass(net.minecraft.world.entity.Entity.class, realAabb, e -> !e.isRemoved())) {
          org.bukkit.entity.Entity entity = nms.getBukkitEntity();
          entity.setVelocity(entity.getVelocity().add(realPushVector));
        }
      }
    }
  }

  private double randomOffset(double range) {
    return (ThreadLocalRandom.current().nextDouble() * 2.0D - 1.0D) * range;
  }

  private String getBlockId(BukkitExistingBlock block) {
    return (block.customBlock() != null) ? block.customBlock().id().toString()
        : block.block().getType().getKey().toString();
  }

  private static net.minecraft.core.Direction toNms(Direction dir) {
    return switch (dir) {
      case UP -> net.minecraft.core.Direction.UP;
      case DOWN -> net.minecraft.core.Direction.DOWN;
      case NORTH -> net.minecraft.core.Direction.NORTH;
      case SOUTH -> net.minecraft.core.Direction.SOUTH;
      case EAST -> net.minecraft.core.Direction.EAST;
      case WEST -> net.minecraft.core.Direction.WEST;
    };
  }

  // ---------------- legacy redstone (only used when gasMode == false) ----------------

  private void updateActivationFromNearbyRedstone(Object stateObj, Object level, Object posObj) {
    ImmutableBlockState blockState = BukkitBlockManager.instance()
        .getImmutableBlockState(BlockStateUtils.blockStateToId(stateObj));
    if (blockState == null || blockState.isEmpty())
      return;
    boolean shouldPower = analyzeRedstone(level, posObj);
    if (((Boolean) blockState.get(this.poweredProperty)).booleanValue() != shouldPower) {
      ImmutableBlockState newState = blockState.with(this.poweredProperty, Boolean.valueOf(shouldPower));
      ((net.minecraft.world.level.LevelWriter) level).setBlock((net.minecraft.core.BlockPos) posObj,
          (net.minecraft.world.level.block.state.BlockState) newState.customBlockState().minecraftState(),
          UpdateFlags.UPDATE_ALL);
    }
  }

  @Override
  public void neighborChanged(Object thisBlock, Object[] args) {
    if (gasMode)
      return; // gas fans react to redstone inside the controller tick
    Object state = args[0];
    Object level = args[1];
    Object posObj = args[2];
    updateActivationFromNearbyRedstone(state, level, posObj);
  }

  private boolean analyzeRedstone(Object level, Object posObj) {
    BukkitWorld world = new BukkitWorld(((net.minecraft.server.level.ServerLevel) level).getWorld());
    BlockPos pos = LocationUtils.fromBlockPos(posObj);
    for (Direction dir : Direction.values()) {
      BukkitExistingBlock neighbor = (BukkitExistingBlock) world.getBlock(pos.relative(dir).x(), pos.relative(dir).y(),
          pos.relative(dir).z());
      if (neighbor != null) {
        if (neighbor.block().getBlockPower() > 0)
          return true;
        if (REDSTONE_SOURCE_IDS.contains(getBlockId(neighbor)))
          return true;
      }
    }
    return ((net.minecraft.world.level.SignalGetter) level)
        .hasNeighborSignal((net.minecraft.core.BlockPos) posObj);
  }

  @Override
  public boolean isSignalSource(Object thisBlock, Object[] args) {
    return false;
  }

  public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
    @SuppressWarnings("unchecked")
    public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
      Property<Direction> facing = (Property<Direction>) block.getProperty("facing");
      Property<Boolean> powered = (Property<Boolean>) block.getProperty("powered");
      if (facing == null)
        throw new IllegalArgumentException("Falta propiedad 'facing'");
      if (powered == null)
        throw new IllegalArgumentException("Falta propiedad 'powered'");
      int tickDelay = Integer.parseInt(arguments.getOrDefault("tickDelay", Integer.valueOf(2)).toString());
      Particle particle = Particle.valueOf(arguments.getOrDefault("particle", "CLOUD").toString().toUpperCase());
      int maxPushDistance = Integer.parseInt(arguments.getOrDefault("maxPushDistance", Integer.valueOf(5)).toString());
      List<String> passableBlocksList = (List<String>) arguments.getOrDefault("passableBlocks",
          List.of("minecraft:air"));
      Set<Key> passableBlocks = passableBlocksList.stream()
          .map(s -> Key.of(s))
          .collect(Collectors.toCollection(it.unimi.dsi.fastutil.objects.ObjectOpenHashSet::new));

      // Gas mode is ON by default (the headline feature). Set `gas: false` to keep the
      // legacy redstone-only push (so old configs/fans don't break).
      boolean gasMode = Boolean.parseBoolean(arguments.getOrDefault("gas", Boolean.valueOf(true)).toString());
      int gasBuffer = Integer.parseInt(arguments.getOrDefault("gas_buffer", Integer.valueOf(4000)).toString());
      int intakePerTick = Integer.parseInt(arguments.getOrDefault("intake_per_tick", Integer.valueOf(200)).toString());
      int gasPerTick = Integer.parseInt(arguments.getOrDefault("gas_per_tick", Integer.valueOf(50)).toString());
      double heavyPushMul = Double
          .parseDouble(arguments.getOrDefault("heavy_push_multiplier", Double.valueOf(1.6)).toString());
      int heavyRangeBonus = Integer
          .parseInt(arguments.getOrDefault("heavy_range_bonus", Integer.valueOf(2)).toString());
      double heavyGasMul = Double
          .parseDouble(arguments.getOrDefault("heavy_gas_multiplier", Double.valueOf(1.5)).toString());
      int processingInterval = Integer
          .parseInt(arguments.getOrDefault("processing_interval", Integer.valueOf(40)).toString());
      Particle steamParticle = Particle
          .valueOf(arguments.getOrDefault("steam_particle", "CLOUD").toString().toUpperCase());
      Particle heavyParticle = Particle
          .valueOf(arguments.getOrDefault("heavy_particle", "SMOKE").toString().toUpperCase());

      return (BlockBehavior) new FanBlockBehavior(block, facing, powered, tickDelay, particle, maxPushDistance,
          passableBlocks, gasMode, gasBuffer, intakePerTick, gasPerTick, heavyPushMul, heavyRangeBonus, heavyGasMul,
          processingInterval, steamParticle, heavyParticle);
    }
  }
}
