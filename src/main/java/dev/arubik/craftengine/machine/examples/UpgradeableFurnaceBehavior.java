package dev.arubik.craftengine.machine.examples;

import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import dev.arubik.craftengine.machine.upgrade.UpgradeRegistry;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

/**
 * Sample machine behavior proving the {@code machine/upgrade} slot system is
 * reusable. Delegates all logic to {@link UpgradeableFurnaceBlockEntity}.
 *
 * <p>Modeled on {@link TestMachineBehavior}. Exposes a public {@link #FACTORY}
 * and {@link #POLYFILL_UPGRADEABLE_FURNACE} key for the main thread to wire into
 * {@code BlockBehaviors}.</p>
 */
public class UpgradeableFurnaceBehavior extends MachineBlockBehavior {

    public static final Key POLYFILL_UPGRADEABLE_FURNACE = Key.of("polyfills:upgradeable_furnace");

    public static final Factory FACTORY = new Factory();

    static {
        // Make sure the sample upgrades are present even before any block is placed.
        UpgradeableFurnaceBlockEntity.registerDefaultUpgrades(UpgradeRegistry.global());
    }

    public UpgradeableFurnaceBehavior(BlockDefinition block, java.util.List<net.minecraft.core.Direction> connectableFaces,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            dev.arubik.craftengine.multiblock.IOConfiguration ioConfig) {
        super(block, connectableFaces, horizontalDirectionProperty, verticalDirectionProperty, ioConfig);
    }

    public UpgradeableFurnaceBehavior(BlockDefinition block) {
        super(block);
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new UpgradeableFurnaceBlockEntity(blockEntity);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            MachineBlockBehavior base = (MachineBlockBehavior) MachineBlockBehavior.FACTORY.create(block, arguments);
            return new UpgradeableFurnaceBehavior(block, base.getConnectableFaces(), base.horizontalDirectionProperty,
                    base.verticalDirectionProperty, base.getIOConfiguration(null, null));
        }
    }
}
