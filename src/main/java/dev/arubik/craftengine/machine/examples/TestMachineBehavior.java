package dev.arubik.craftengine.machine.examples;

import java.util.Map;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlockBehavior;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.util.Key;

/**
 * A Test Machine Behavior to validate IO Logic and UI.
 * Delegates all logic to TestMachineBlockEntity.
 */
public class TestMachineBehavior extends MachineBlockBehavior {
    public static final Key FACTORY_KEY = Key.of("polyfills:test_machine");

    public static final Factory FACTORY = new Factory();

    public TestMachineBehavior(CustomBlock block) {
        super(block);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityType<T> blockEntityType(ImmutableBlockState state) {
        return (BlockEntityType<T>) BukkitBlockEntityTypes.TEST_MACHINE;
    }

    @Override
    public net.momirealms.craftengine.core.block.entity.BlockEntity createBlockEntity(
            net.momirealms.craftengine.core.world.BlockPos pos, ImmutableBlockState state) {
        return new TestMachineBlockEntity(pos, state);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            return new TestMachineBehavior(block);
        }
    }
}
