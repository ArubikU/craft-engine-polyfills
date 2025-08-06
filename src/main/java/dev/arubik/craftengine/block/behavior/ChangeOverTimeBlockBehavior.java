package dev.arubik.craftengine.block.behavior;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.bukkit.GameEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.util.Vector;

import net.momirealms.craftengine.bukkit.block.BukkitBlockManager;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitBlockInWorld;
import net.momirealms.craftengine.core.block.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateOption;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.util.RandomUtils;
import net.momirealms.craftengine.core.util.ResourceConfigUtils;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.World;

public class ChangeOverTimeBlockBehavior extends BukkitBlockBehavior {
    public static final Factory FACTORY = new Factory();
    private final float delay;
    private final Key nextBlock;

    public ChangeOverTimeBlockBehavior(CustomBlock customBlock, float delay, Key nextBlock) {
        super(customBlock);
        this.delay = delay;
        this.nextBlock = nextBlock;
    }

    @Override
    public void randomTick(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        if(!shouldChange(args)) return;
        Optional<CustomBlock> optionalNewCustomBlock = BukkitBlockManager.instance().blockById(nextBlock);
        if (!optionalNewCustomBlock.isPresent()) return;
        Object blockState = args[0];
        World level = (World) args[1];
        BlockPos blockPos = (BlockPos) args[2];
        Optional<ImmutableBlockState> optionalCurrentState = BlockStateUtils .getOptionalCustomBlockState(blockState);
        if (optionalCurrentState.isEmpty()) return;
        ImmutableBlockState newState = optionalNewCustomBlock.get().getBlockState(optionalCurrentState.get().propertiesNbt());
        BukkitBlockInWorld blockInWorld = (BukkitBlockInWorld) level.getBlockAt(LocationUtils.fromBlockPos(blockPos));
        BlockFormEvent event = new BlockFormEvent(blockInWorld.block(),BlockStateUtils.fromBlockData(newState.customBlockState().handle()).createBlockState());
        if (event.callEvent()) {
            FastNMS.INSTANCE.method$LevelWriter$setBlock(level.serverWorld(), blockPos, newState.customBlockState().handle(), UpdateOption.UPDATE_ALL_IMMEDIATE.flags());
            blockInWorld.block().getWorld().sendGameEvent(null, GameEvent.BLOCK_CHANGE, new Vector(blockPos.x(), blockPos.y(), blockPos.z()));
        }
    }

    private boolean shouldChange(Object[] args) {
        return RandomUtils.generateRandomFloat(0F, 1F) < this.delay;
    }

    public static class Factory implements BlockBehaviorFactory {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            float delay = Float.valueOf(arguments.getOrDefault("delay", 0.05688889F).toString());
            Key nextBlock = Key.of(ResourceConfigUtils.requireNonEmptyStringOrThrow(arguments.getOrDefault("next-block", "minecraft:air"), "warning.config.block.behavior.change_over_time_block_missing_next_block"));
            return new ChangeOverTimeBlockBehavior(block, delay, nextBlock);
        }
    }
}
