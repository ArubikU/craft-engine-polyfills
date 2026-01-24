package dev.arubik.craftengine.fluid.behavior;

import java.util.Map;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import dev.arubik.craftengine.machine.block.entity.MachinePumpBlockEntity;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.util.Key;

/**
 * MachinePumpBehavior: A machine-based pump behavior.
 */
public class MachinePumpBehavior extends MachineBlockBehavior {
    public static final Key FACTORY_KEY = Key.of("polyfills:machine_pump");
    public static final Factory FACTORY = new Factory();

    public MachinePumpBehavior(CustomBlock block, java.util.List<net.minecraft.core.Direction> connectableFaces,
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.HorizontalDirection> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            dev.arubik.craftengine.multiblock.IOConfiguration ioConfig) {
        super(block, connectableFaces, horizontalDirectionProperty, verticalDirectionProperty, ioConfig);
    }

    public MachinePumpBehavior(CustomBlock block) {
        super(block);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityType<T> blockEntityType(ImmutableBlockState state) {
        return (BlockEntityType<T>) BukkitBlockEntityTypes.MACHINE_PUMP;
    }

    @Override
    public BlockEntity createBlockEntity(net.momirealms.craftengine.core.world.BlockPos pos,
            ImmutableBlockState state) {
        return new MachinePumpBlockEntity(pos, state);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            MachineBlockBehavior base = (MachineBlockBehavior) MachineBlockBehavior.FACTORY.create(block, arguments);
            return new MachinePumpBehavior(block, base.getConnectableFaces(), base.horizontalDirectionProperty,
                    base.verticalDirectionProperty, base.getIOConfiguration(null, null));
        }
    }
}
