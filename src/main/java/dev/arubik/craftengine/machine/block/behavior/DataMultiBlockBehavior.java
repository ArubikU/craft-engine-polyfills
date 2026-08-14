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

    /**
     * Every mode this core can assemble into, tried in order.
     *
     * <p>
     * A core with one mode is the common case; a core with several becomes a
     * different machine depending on the shell built around it, so the shape that
     * matched decides which machine the block entity is.
     */
    private final List<MultiBlockDefinition.Mode> modes;
    /** The mode whose schema last matched, so the entity built next uses its machine. */
    private MultiBlockDefinition.Mode activeMode;

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
            java.util.Map<Key, List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>> upgradeDefs,
            List<MultiBlockDefinition.Mode> modes) {
        super(block, schema, partBlockId, connectableFaces, horizontal, vertical, ioConfig);
        this.modes = modes == null ? List.of() : List.copyOf(modes);
        this.activeMode = this.modes.isEmpty() ? null : this.modes.get(0);
        this.definition = definition;
        this.menuConfig = menuConfig;
        this.bars = bars;
        this.upgradeDefs = upgradeDefs;
    }

    public MachineDefinition definition() {
        return definition;
    }

    /**
     * Tries each mode in order and keeps the one that matched.
     *
     * <p>
     * {@code createMachineBlockEntity} runs right after, so recording the winner here
     * is what lets one core block build a chest or a smelter from the same behavior.
     */
    @Override
    protected boolean tryFormMachine(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        if (modes.size() <= 1)
            return super.tryFormMachine(level, pos, state);

        MultiBlockSchema original = this.schema;
        for (MultiBlockDefinition.Mode mode : modes) {
            this.schema = mode.schema();
            this.activeMode = mode;
            if (mode.io() != null)
                withIOProvider(mode.io());
            if (super.tryFormMachine(level, pos, state))
                return true;
        }
        this.schema = original;
        this.activeMode = modes.get(0);
        return false;
    }

    /** The machine of the mode that matched, or the first mode's. */
    private MachineDefinition activeDefinition() {
        return activeMode != null && activeMode.machine() != null ? activeMode.machine() : definition;
    }

    @Override
    protected MultiBlockMachineBlockEntity createMachineBlockEntity(BlockEntity blockEntity) {
        DataMultiBlockMachineBlockEntity machine =
                new DataMultiBlockMachineBlockEntity(blockEntity, schema, activeDefinition());
        machine.setMenuConfig(menuConfig);
        machine.setBars(bars);
        // Prefer machine-JSON definitions; fall back to YAML block-config upgradeDefs
        var def = activeDefinition();
        if (def != null && !def.upgradeDefs().isEmpty()) {
            machine.setUpgradeDefs(def.upgradeDefs());
        } else {
            machine.setUpgradeDefs(upgradeDefs);
        }
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
                    DataMachineBehavior.parseUpgrades(arguments.get("upgrades")), multiblock.modes());
            if (mode.io() != null)
                behavior.withIOProvider(mode.io());
            return behavior;
        }
    }
}
