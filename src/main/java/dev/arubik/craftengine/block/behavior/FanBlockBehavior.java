package dev.arubik.craftengine.block.behavior;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.bukkit.Particle;
import org.bukkit.util.Vector;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.momirealms.craftengine.bukkit.block.BukkitBlockManager;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitBlockInWorld;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateOption;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.properties.Property;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.BlockPos;

public class FanBlockBehavior extends BukkitBlockBehavior {
    public static final Factory FACTORY = new Factory();

    private final Property<Direction> facingProperty;
    private final Property<Boolean> poweredProperty;
    private final Property<Integer> powerLevelProperty;

    private static final Set<String> REDSTONE_SOURCE_IDS = Set.of(
        "minecraft:redstone_block",
        "minecraft:redstone_torch",
        "minecraft:wall_redstone_torch"
    );

    // Mapa estático para evitar switch repetitivo
    private static final Map<Direction, Vector> DIRECTION_VECTORS = Map.of(
        Direction.NORTH, new Vector(0, 0, -1),
        Direction.SOUTH, new Vector(0, 0, 1),
        Direction.WEST,  new Vector(-1, 0, 0),
        Direction.EAST,  new Vector(1, 0, 0),
        Direction.UP,    new Vector(0, 1, 0),
        Direction.DOWN,  new Vector(0, -1, 0)
    );

    private final int tickDelay;
    private final Particle particle;
    private final int maxPushDistance;
    private final Set<Key> passableBlocks;
    private final boolean redstoneAffectsPush;

    public FanBlockBehavior(CustomBlock customBlock,
                            Property<Direction> facing,
                            Property<Boolean> powered,
                            Property<Integer> powerLevel,
                            int tickDelay,
                            Particle particle,
                            int maxPushDistance,
                            Set<Key> passableBlocks,
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
    public void onPlace(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        Object state = args[0];
        Object world = args[1];

        Object blockPos = args[2];
        FastNMS.INSTANCE.method$ScheduledTickAccess$scheduleBlockTick(world, blockPos, thisBlock, tickDelay);

        updateActivationFromNearbyRedstone(state, world, blockPos);
        
    }

    @Override
    public void tick(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        Object state = args[0];
        Object level = args[1];
        Object posObj = args[2];
        FastNMS.INSTANCE.method$ScheduledTickAccess$scheduleBlockTick(level, posObj, thisBlock, tickDelay);

        ImmutableBlockState blockState = BukkitBlockManager.instance()
                .getImmutableBlockState(BlockStateUtils.blockStateToId(state));
        if (blockState == null || blockState.isEmpty() || !blockState.get(this.poweredProperty)) return;

        Direction facing = blockState.get(this.facingProperty);
        BukkitWorld world = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(level));
        BlockPos pos = LocationUtils.fromBlockPos(posObj);

        int pushStrength = getPushStrength(blockState);

        for (int i = 1; i <= maxPushDistance; i++) {
            BlockPos targetPos = pos.relative(facing, i);
            BukkitBlockInWorld blockInWorld = (BukkitBlockInWorld) world.getBlockAt(targetPos);

            if (!isPassable(blockInWorld)) break;

            applyPushAndParticles(world, targetPos, facing, pushStrength);
        }
    }

    private int getPushStrength(ImmutableBlockState blockState) {
        if (redstoneAffectsPush && powerLevelProperty != null) {
            return Math.max(1, blockState.get(this.powerLevelProperty));
        }
        return 1;
    }

    private boolean isPassable(BukkitBlockInWorld blockInWorld) {
        String blockId = getBlockId(blockInWorld);
        return passableBlocks.contains(Key.of(blockId)) || !blockInWorld.block().getType().isSolid();
    }

    private void applyPushAndParticles(BukkitWorld world, BlockPos targetPos, Direction facing, int pushStrength) {
        org.bukkit.World bukkitWorld = world.platformWorld();

        double cx = targetPos.x() + 0.5;
        double cy = targetPos.y() + 0.5;
        double cz = targetPos.z() + 0.5;

        Vector pushVector = DIRECTION_VECTORS.getOrDefault(facing, new Vector(0, 0, 0))
                .clone().multiply(pushStrength * 0.1);

        // Partículas con jitter
        double jitter = 0.2;
        double rx = randomOffset(jitter);
        double ry = randomOffset(jitter);
        double rz = randomOffset(jitter);

        bukkitWorld.spawnParticle(particle, cx + rx, cy + ry, cz + rz, 1, 0, 0, 0, 0);

        bukkitWorld.getNearbyEntities(new org.bukkit.util.BoundingBox(
                cx - 0.5, cy - 0.5, cz - 0.5,
                cx + 0.5, cy + 0.5, cz + 0.5
        )).forEach(entity -> entity.setVelocity(entity.getVelocity().add(pushVector)));
    }

    private double randomOffset(double range) {
        return (ThreadLocalRandom.current().nextDouble() * 2 - 1) * range;
    }

    private String getBlockId(BukkitBlockInWorld block) {
        return block.customBlock() != null
                ? block.customBlock().id().toString()
                : block.block().getType().getKey().toString();
    }

    private void updateActivationFromNearbyRedstone(Object stateObj, Object level, Object posObj) {
        ImmutableBlockState blockState = BukkitBlockManager.instance()
                .getImmutableBlockState(BlockStateUtils.blockStateToId(stateObj));
        if (blockState == null || blockState.isEmpty()) return;

        RedstoneInfo info = analyzeRedstone(level, posObj);
        boolean shouldPower = info.hasPower();
        int targetPowerLevel = shouldPower ? info.maxNeighborPower : 0;

        if (needsUpdate(blockState, shouldPower, targetPowerLevel)) {
            ImmutableBlockState newState = blockState.with(this.poweredProperty, shouldPower);
            if (this.powerLevelProperty != null) newState = newState.with(this.powerLevelProperty, targetPowerLevel);

            FastNMS.INSTANCE.method$LevelWriter$setBlock(level, posObj, newState.customBlockState().handle(), UpdateOption.UPDATE_ALL.flags());
        }
    }

    @Override
    public void neighborChanged(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        Object state = args[0];
        Object level = args[1];
        Object posObj = args[2];

        ImmutableBlockState blockState = BukkitBlockManager.instance()
                .getImmutableBlockState(BlockStateUtils.blockStateToId(state));
        if (blockState == null || blockState.isEmpty()) return;

        RedstoneInfo info = analyzeRedstone(level, posObj);
        boolean shouldPower = info.hasPower();
        int targetPowerLevel = shouldPower ? info.maxNeighborPower : 0;

        if (needsUpdate(blockState, shouldPower, targetPowerLevel)) {
            ImmutableBlockState newState = blockState.with(this.poweredProperty, shouldPower);
            if (this.powerLevelProperty != null) newState = newState.with(this.powerLevelProperty, targetPowerLevel);
            FastNMS.INSTANCE.method$LevelWriter$setBlock(level, posObj, newState.customBlockState().handle(), UpdateOption.UPDATE_ALL.flags());
            blockState = newState;
        }

        if (!info.hasPower()) {
            ImmutableBlockState newState = blockState.with(this.poweredProperty, false);
            if (powerLevelProperty != null) newState = newState.with(this.powerLevelProperty, 0);
            FastNMS.INSTANCE.method$LevelWriter$setBlock(level, posObj, newState.customBlockState().handle(), UpdateOption.UPDATE_ALL.flags());
        }
    }

    private boolean needsUpdate(ImmutableBlockState state, boolean shouldPower, int targetPowerLevel) {
        if (state.get(this.poweredProperty) != shouldPower) return true;
        if (this.powerLevelProperty != null && !Objects.equals(state.get(this.powerLevelProperty), targetPowerLevel))
            return true;
        return false;
    }

    private RedstoneInfo analyzeRedstone(Object level, Object posObj) {
        BukkitWorld world = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(level));
        BlockPos pos = LocationUtils.fromBlockPos(posObj);

        int maxNeighborPower = 0;
        boolean foundDirectSource = false;

        for (Direction dir : Direction.values()) {
            BukkitBlockInWorld neighbor = (BukkitBlockInWorld) world.getBlockAt(pos.relative(dir));
            if (neighbor == null) continue;

            maxNeighborPower = Math.max(maxNeighborPower, neighbor.block().getBlockPower());
            if (REDSTONE_SOURCE_IDS.contains(getBlockId(neighbor))) {
                foundDirectSource = true;
            }
        }

        boolean hasNeighborSignal = FastNMS.INSTANCE.method$SignalGetter$hasNeighborSignal(level, posObj);
        return new RedstoneInfo(maxNeighborPower, foundDirectSource, hasNeighborSignal);
    }

    private record RedstoneInfo(int maxNeighborPower, boolean foundDirectSource, boolean hasNeighborSignal) {
        boolean hasPower() {
            return foundDirectSource || hasNeighborSignal || maxNeighborPower > 0;
        }
    }

    @Override
    public boolean isSignalSource(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        return true;
    }

    public static class Factory implements BlockBehaviorFactory {
        @Override
        @SuppressWarnings({ "unchecked", "all" })
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            Property<Direction> facing = (Property<Direction>) block.getProperty("facing");
            Property<Boolean> powered = (Property<Boolean>) block.getProperty("powered");
            Property<Integer> powerLevel = (Property<Integer>) block.getProperty("power");

            if (facing == null) throw new IllegalArgumentException("Falta propiedad 'facing'");
            if (powered == null) throw new IllegalArgumentException("Falta propiedad 'powered'");

            int tickDelay = Integer.parseInt(arguments.getOrDefault("tickDelay", 10).toString());
            Particle particle = Particle.valueOf(arguments.getOrDefault("particle", "CLOUD").toString().toUpperCase());
            int maxPushDistance = Integer.parseInt(arguments.getOrDefault("maxPushDistance", 5).toString());

            Set<Key> passableBlocks = ((List<String>) arguments.getOrDefault("passableBlocks", List.of("minecraft:air")))
                    .stream().map(Key::of).collect(Collectors.toCollection(ObjectOpenHashSet::new));

            boolean redstoneAffectsPush = Boolean.parseBoolean(arguments.getOrDefault("redstoneAffectsPush", true).toString());

            return new FanBlockBehavior(block, facing, powered, powerLevel, tickDelay, particle,
                    maxPushDistance, passableBlocks, redstoneAffectsPush);
        }
    }
}
