package dev.arubik.craftengine.machine.examples;

import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

/**
 * A Test Machine Behavior to validate IO Logic and UI.
 * Delegates all logic to TestMachineBlockEntity.
 */
public class TestMachineBehavior extends MachineBlockBehavior {
    public static final Key FACTORY_KEY = Key.of("polyfills:test_machine");

    public static final Factory FACTORY = new Factory();

    public TestMachineBehavior(BlockDefinition block, java.util.List<net.minecraft.core.Direction> connectableFaces,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            dev.arubik.craftengine.multiblock.IOConfiguration ioConfig) {
        super(block, connectableFaces, horizontalDirectionProperty, verticalDirectionProperty, ioConfig);
    }

    public TestMachineBehavior(BlockDefinition block) {
        super(block);
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new TestMachineBlockEntity(blockEntity);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            MachineBlockBehavior base = (MachineBlockBehavior) MachineBlockBehavior.FACTORY.create(block, arguments);
            return new TestMachineBehavior(block, base.getConnectableFaces(), base.horizontalDirectionProperty,
                    base.verticalDirectionProperty, base.getIOConfiguration(null, null));
        }
    }
}
