package dev.arubik.craftengine.conveyor.routing;

import dev.arubik.craftengine.conveyor.routing.AbstractRouterBlockEntity;
import dev.arubik.craftengine.conveyor.routing.SplitterBlockEntity;
import dev.arubik.craftengine.util.CeWorlds;
import dev.arubik.craftengine.util.NmsBlockBehavior;
import dev.arubik.craftengine.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import org.bukkit.World;

public class SplitterBehavior
extends NmsBlockBehavior
implements EntityBlock {
    public static final Key FACTORY_KEY = Key.of((String)"polyfills:conveyor_splitter");
    public static final Factory FACTORY = new Factory();
    private final Direction defaultFacing;
    private final int slots;
    private int controllerId;

    public SplitterBehavior(BlockDefinition block, Direction defaultFacing, int slots) {
        super(block);
        this.defaultFacing = defaultFacing == null ? Direction.NORTH : defaultFacing;
        this.slots = slots > 0 ? slots : 6;
    }

    public void initControllerId(int id) {
        this.controllerId = id;
    }

    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new SplitterBlockEntity(blockEntity, this.defaultFacing, this.slots);
    }

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Level level, BlockPos nmsPos, BlockState oldState, Boolean movedByPiston) {
        try {
            BlockEntityController blockEntityController;
            CEWorld world = CeWorlds.of((World)((ServerLevel)level).getWorld()).storageWorld();
            if (world == null) {
                return;
            }
            net.momirealms.craftengine.core.world.BlockPos pos = new net.momirealms.craftengine.core.world.BlockPos(nmsPos.getX(), nmsPos.getY(), nmsPos.getZ());
            BlockEntity be = world.getBlockEntityAtIfLoaded(pos);
            if (be != null && (blockEntityController = be.controller) instanceof AbstractRouterBlockEntity) {
                AbstractRouterBlockEntity r = (AbstractRouterBlockEntity)blockEntityController;
                r.dropAndDespawn();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static class Factory
    implements BlockBehaviorFactory<BlockBehavior> {
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            Direction facing = Direction.NORTH;
            Object f = arguments.get("facing");
            if (f != null) {
                try {
                    facing = Direction.valueOf((String)f.toString().toUpperCase());
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
            }
            int slots = Utils.getAsInt(arguments.getOrDefault("slots", 6), "slots");
            return new SplitterBehavior(block, facing, slots);
        }
    }
}

