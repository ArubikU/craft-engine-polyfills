package dev.arubik.craftengine.conveyor;

import net.minecraft.server.level.ServerLevel;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;

/**
 * Block behavior backing a {@link MergerBlockEntity} ({@code polyfills:conveyor_merger}).
 * Directional (4-direction {@code facing} = output side). The buffer size is the only
 * tunable.
 */
public class MergerBehavior extends dev.arubik.craftengine.util.NmsBlockBehavior implements EntityBlock {

    public static final Key FACTORY_KEY = Key.of("polyfills:conveyor_merger");
    public static final Factory FACTORY = new Factory();

    private final Direction defaultFacing;
    private final int slots;
    private int controllerId;

    public MergerBehavior(BlockDefinition block, Direction defaultFacing, int slots) {
        super(block);
        this.defaultFacing = defaultFacing == null ? Direction.NORTH : defaultFacing;
        this.slots = slots > 0 ? slots : MergerBlockEntity.DEFAULT_SLOTS;
    }

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new MergerBlockEntity(blockEntity, defaultFacing, slots);
    }

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, net.minecraft.world.level.Level level,
            net.minecraft.core.BlockPos nmsPos, net.minecraft.world.level.block.state.BlockState oldState,
            Boolean movedByPiston) {
        try {
            CEWorld world = new BukkitWorld(((ServerLevel) level).getWorld()).storageWorld();
            if (world == null)
                return;
            BlockPos pos = new BlockPos(nmsPos.getX(), nmsPos.getY(), nmsPos.getZ());
            BlockEntity be = world.getBlockEntityAtIfLoaded(pos);
            if (be != null && be.controller instanceof AbstractRouterBlockEntity r)
                r.dropAndDespawn();
        } catch (Throwable ignored) {
        }
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            Direction facing = Direction.NORTH;
            Object f = arguments.get("facing");
            if (f != null) {
                try {
                    facing = Direction.valueOf(f.toString().toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            }
            int slots = dev.arubik.craftengine.util.Utils.getAsInt(
                    arguments.getOrDefault("slots", MergerBlockEntity.DEFAULT_SLOTS), "slots");
            return new MergerBehavior(block, facing, slots);
        }
    }
}
