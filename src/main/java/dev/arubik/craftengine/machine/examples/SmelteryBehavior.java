package dev.arubik.craftengine.machine.examples;

import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

/** EntityBlock behavior for the RPM-powered Smeltery ({@code polyfills:smeltery}). */
public class SmelteryBehavior extends MachineBlockBehavior {

    public static final Key POLYFILL_SMELTERY = Key.of("polyfills:smeltery");
    public static final Factory FACTORY = new Factory();

    public SmelteryBehavior(BlockDefinition block,
            java.util.List<net.minecraft.core.Direction> connectableFaces,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> h,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> v,
            dev.arubik.craftengine.multiblock.IOConfiguration ioConfig) {
        super(block, connectableFaces, h, v, ioConfig);
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new SmelteryBlockEntity(blockEntity);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            MachineBlockBehavior base = (MachineBlockBehavior) MachineBlockBehavior.FACTORY.create(block, arguments);
            return new SmelteryBehavior(block, base.getConnectableFaces(),
                    base.horizontalDirectionProperty, base.verticalDirectionProperty,
                    base.getIOConfiguration(null, null));
        }
    }
}
