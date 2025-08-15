package dev.arubik.craftengine.block.behavior;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
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
        Object world = args[1];
        Object blockPos = args[2];
        FastNMS.INSTANCE.method$ScheduledTickAccess$scheduleBlockTick(world, blockPos, thisBlock, tickDelay);
    }

    @Override
    public void tick(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        Object state = args[0];
        Object level = args[1];
        Object posObj = args[2];

        ImmutableBlockState blockState = BukkitBlockManager.instance()
                .getImmutableBlockState(BlockStateUtils.blockStateToId(state));
        if (blockState == null || blockState.isEmpty())
            return;

        if (!blockState.get(this.poweredProperty))
            return;

        Direction facing = blockState.get(this.facingProperty);
        int powerLevel = blockState.get(this.powerLevelProperty);

        BukkitWorld world = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(level));
        BlockPos pos = LocationUtils.fromBlockPos(posObj);

        int pushStrength = 1;
        if (redstoneAffectsPush) {
            pushStrength = Math.max(1, powerLevel);
        }

        for (int i = 1; i <= maxPushDistance; i++) {
            BlockPos targetPos = pos.relative(facing, i);
            BukkitBlockInWorld blockInWorld = (BukkitBlockInWorld) world.getBlockAt(targetPos);

            if (!(passableBlocks.contains(blockInWorld.customBlock().id())
                    || passableBlocks.contains(Key.from(blockInWorld.block().getType().getKey().toString())))
                    && blockInWorld.block().getType().isSolid())
                break;
            org.bukkit.World bukkitWorld = world.platformWorld();

            double cx = targetPos.x() + 0.5;
            double cy = targetPos.y() + 0.5;
            double cz = targetPos.z() + 0.5;

            Vector pushVector;
            switch (facing) {
                case NORTH -> pushVector = new Vector(0, 0, -1);
                case SOUTH -> pushVector = new Vector(0, 0, 1);
                case WEST -> pushVector = new Vector(-1, 0, 0);
                case EAST -> pushVector = new Vector(1, 0, 0);
                case UP -> pushVector = new Vector(0, 1, 0);
                case DOWN -> pushVector = new Vector(0, -1, 0);
                default -> pushVector = new Vector(0, 0, 0);
            }

            pushVector.multiply(pushStrength * 0.1);

            bukkitWorld.spawnParticle(particle, cx, cy, cz, 1, 0, 0, 0, 0);

            bukkitWorld.getNearbyEntities(
                    new org.bukkit.util.BoundingBox(cx - 0.5, cy - 0.5, cz - 0.5,
                            cx + 0.5, cy + 0.5, cz + 0.5))
                    .forEach(entity -> entity.setVelocity(entity.getVelocity().add(pushVector)));
        }
    }

    @Override
    public boolean isSignalSource(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        return true;
    }

    @Override
    public void neighborChanged(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        Object state = args[0];
        Object level = args[1];
        Object pos = args[2];
        boolean hasNeighborSignal = FastNMS.INSTANCE.method$SignalGetter$hasNeighborSignal(level, pos);
        ImmutableBlockState blockState = BukkitBlockManager.instance()
                .getImmutableBlockState(BlockStateUtils.blockStateToId(state));
        if (blockState == null || blockState.isEmpty())
            return;
        boolean triggeredValue = blockState.get(this.poweredProperty);
        if (hasNeighborSignal && !triggeredValue) {
            if (powerLevelProperty != null) {
                BukkitWorld world = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(level));
                BukkitBlockInWorld block = (BukkitBlockInWorld) world.getBlockAt(LocationUtils.fromBlockPos(pos));
                if (block != null) {
                    int powerLevel = block.block().getBlockPower();
                    FastNMS.INSTANCE.method$LevelWriter$setBlock(level, pos,
                            blockState.with(this.poweredProperty, true).with(this.powerLevelProperty, powerLevel)
                                    .customBlockState().handle(),
                            2);
                }
            } else {
                FastNMS.INSTANCE.method$LevelWriter$setBlock(level, pos,
                        blockState.with(this.poweredProperty, true).customBlockState().handle(), 2);
            }
        } else if (!hasNeighborSignal && triggeredValue) {
            if (powerLevelProperty != null) {
                FastNMS.INSTANCE.method$LevelWriter$setBlock(level, pos, blockState.with(this.poweredProperty, false)
                        .with(this.powerLevelProperty, 0).customBlockState().handle(), 2);
            } else {
                FastNMS.INSTANCE.method$LevelWriter$setBlock(level, pos,
                        blockState.with(this.poweredProperty, false).customBlockState().handle(), 2);
            }
        }
    }

    public static class Factory implements BlockBehaviorFactory {
        @Override
        @SuppressWarnings({ "unchecked", "all" })
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            Property<Direction> facing = (Property<Direction>) block.getProperty("facing");
            if (facing == null)
                throw new IllegalArgumentException("Falta propiedad 'facing'");

            Property<Boolean> powered = (Property<Boolean>) block.getProperty("powered");
            if (powered == null)
                throw new IllegalArgumentException("Falta propiedad 'powered'");

            Property<Integer> powerLevel = (Property<Integer>) block.getProperty("power");
            if (powerLevel == null)
                throw new IllegalArgumentException("Falta propiedad 'power'");

            int tickDelay = Integer.parseInt(arguments.getOrDefault("tickDelay", 10).toString());
            Particle particle = Particle.valueOf(arguments.getOrDefault("particle", "CLOUD").toString().toUpperCase());
            int maxPushDistance = Integer.parseInt(arguments.getOrDefault("maxPushDistance", 5).toString());

            Set<Key> passableBlocks = ((List<String>) arguments.getOrDefault("passableBlocks",
                    List.of("minecraft:air")))
                    .stream().map(Key::of).collect(Collectors.toCollection(ObjectOpenHashSet::new));

            boolean redstoneAffectsPush = Boolean
                    .parseBoolean(arguments.getOrDefault("redstoneAffectsPush", true).toString());

            return new FanBlockBehavior(block, facing, powered, powerLevel, tickDelay, particle, maxPushDistance,
                    passableBlocks, redstoneAffectsPush);
        }
    }
}
