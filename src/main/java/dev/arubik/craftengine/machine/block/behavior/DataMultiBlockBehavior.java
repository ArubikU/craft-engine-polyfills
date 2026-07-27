package dev.arubik.craftengine.machine.block.behavior;

import java.util.List;

import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import dev.arubik.craftengine.machine.block.entity.DataMultiBlockMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import dev.arubik.craftengine.multiblock.MultiBlockBehavior;
import dev.arubik.craftengine.multiblock.MultiBlockDefinition;
import dev.arubik.craftengine.multiblock.MultiBlockMachineBlockEntity;
import dev.arubik.craftengine.multiblock.MultiBlockSchema;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

/**
 * A machine that is also a multiblock, described entirely by data.
 *
 * <p>
 * {@link DataMachineBehavior} covers a machine that is one block. A machine that
 * must be assembled — the refinery with its mixer on top — needs the multiblock
 * half too: the shape to match, the part block to convert, and the per-cell I/O.
 * Rather than widen the single-block behavior and risk the machines already
 * running on it, this is a separate behavior that reads the same
 * {@link MachineDefinition} plus a {@link MultiBlockDefinition}.
 *
 * <pre>{@code
 * behavior:
 *   type: polyfills:data_multiblock_machine
 *   multiblock: polyfills:refinery      # multiblocks/refinery.json
 * }</pre>
 *
 * <p>
 * The machine lives inside the multiblock's mode, not in a file beside it: a
 * shape on its own does nothing, and one core can assemble into different
 * machines depending on the shell built around it.
 */
public class DataMultiBlockBehavior extends MultiBlockBehavior {

    public static final Key FACTORY_KEY = Key.of("polyfills:data_multiblock_machine");
    public static final Factory FACTORY = new Factory();

    private final MachineDefinition definition;
    private final MachineMenuConfig menuConfig;
    private final List<MachineBar> bars;
    private final java.util.Map<Key, List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>> upgradeDefs;

    public DataMultiBlockBehavior(BlockDefinition block, MultiBlockSchema schema, String partBlockId,
            java.util.List<net.minecraft.core.Direction> connectableFaces,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontal,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> vertical,
            dev.arubik.craftengine.multiblock.IOConfiguration ioConfig,
            MachineDefinition definition, MachineMenuConfig menuConfig, List<MachineBar> bars,
            java.util.Map<Key, List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>> upgradeDefs) {
        super(block, schema, partBlockId, connectableFaces, horizontal, vertical, ioConfig);
        this.definition = definition;
        this.menuConfig = menuConfig;
        this.bars = bars;
        this.upgradeDefs = upgradeDefs;
    }

    public MachineDefinition definition() {
        return definition;
    }

    @Override
    protected MultiBlockMachineBlockEntity createMachineBlockEntity(BlockEntity blockEntity) {
        DataMultiBlockMachineBlockEntity machine =
                new DataMultiBlockMachineBlockEntity(blockEntity, schema, definition);
        machine.setMenuConfig(menuConfig);
        machine.setBars(bars);
        machine.setUpgradeDefs(upgradeDefs);
        return machine;
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            MachineBlockBehavior base = (MachineBlockBehavior) MachineBlockBehavior.FACTORY.create(block, arguments);

            Object multiblockId = arguments.get("multiblock");
            MultiBlockDefinition multiblock = MultiBlockDefinition.byName(String.valueOf(multiblockId));
            if (multiblock == null || multiblock.primary() == null)
                throw new IllegalArgumentException("Block " + block.id() + " names multiblock '"
                        + multiblockId + "' which matches no entry in multiblocks/*.json. Known: "
                        + MultiBlockDefinition.REGISTRY.keys());

            var mode = multiblock.primary();
            if (mode.machine() == null)
                throw new IllegalArgumentException("Multiblock '" + multiblockId + "' mode '" + mode.name()
                        + "' declares no 'machine', so there is nothing for this block to run");

            DataMultiBlockBehavior behavior = new DataMultiBlockBehavior(block, mode.schema(),
                    mode.partBlockId(), base.getConnectableFaces(), base.horizontalDirectionProperty,
                    base.verticalDirectionProperty, base.defaultIOConfig, mode.machine(),
                    MachineMenuConfig.parse(arguments::get), MachineBars.parse(arguments.get("bars")),
                    DataMachineBehavior.parseUpgrades(arguments.get("upgrades")));
            if (mode.io() != null)
                behavior.withIOProvider(mode.io());
            return behavior;
        }
    }
}
