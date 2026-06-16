package dev.arubik.craftengine.machine.examples;

import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.IOConfigurationProvider;
import dev.arubik.craftengine.multiblock.MultiBlockBehavior;
import dev.arubik.craftengine.multiblock.MultiBlockMachineBlockEntity;
import dev.arubik.craftengine.multiblock.MultiBlockSchema;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

/**
 * [TASK-03] Refinery multiblock ({@code polyfills:refinery}).
 *
 * <p>A vertical 1x2 structure: the {@code cml:refinery} CORE on the bottom, a
 * {@code cml:refinery_mixer} stacked directly on top. Right-click the core to FORM it — the
 * mixer block is swapped into a {@code role=part} appearance of the refinery (still showing the
 * mixer model) and the machine starts ticking. Break either block to disassemble and restore the
 * mixer. The machine only runs while formed (no ad-hoc neighbour checks), is steam-powered, and
 * consumes water per craft.</p>
 */
public class RefineryBehavior extends MultiBlockBehavior {

    public static final Key POLYFILL_REFINERY = Key.of("polyfills:refinery");
    public static final Factory FACTORY = new Factory();

    private static final String MIXER_ID = "cml:refinery_mixer";

    private final java.util.List<dev.arubik.craftengine.machine.menu.bar.MachineBar> bars;

    public RefineryBehavior(BlockDefinition block, MultiBlockSchema schema, String partBlockId,
            java.util.List<Direction> connectableFaces,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> h,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> v,
            IOConfiguration ioConfig,
            java.util.List<dev.arubik.craftengine.machine.menu.bar.MachineBar> bars) {
        super(block, schema, partBlockId, connectableFaces, h, v, ioConfig);
        this.bars = bars;
        withIOProvider(refineryIO());
    }

    /** True when {@code state} is the custom {@code cml:refinery_mixer} block. */
    private static boolean isMixer(BlockState state) {
        var cs = net.momirealms.craftengine.bukkit.util.BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        return cs != null && cs.owner() != null && MIXER_ID.equals(cs.owner().value().id().toString());
    }

    private static IOConfigurationProvider refineryIO() {
        return relativePos -> {
            // Core (0,0,0): the machine's own IOConfiguration (set in the block entity) handles IO.
            if (relativePos.equals(BlockPos.ZERO))
                return new IOConfiguration.Closed();
            // Top mixer part: water (fluid) + steam (gas) piped in through its TOP, routed to the
            // core's tank slot 0.
            IOConfiguration.Simple cfg = new IOConfiguration.Simple();
            cfg.addInput(IOConfiguration.IOType.GAS, Direction.UP);
            cfg.addInput(IOConfiguration.IOType.FLUID, Direction.UP);
            cfg.withInputSlot(IOConfiguration.IOType.GAS, 0, Direction.UP);
            cfg.withInputSlot(IOConfiguration.IOType.FLUID, 0, Direction.UP);
            return cfg;
        };
    }

    @Override
    protected MultiBlockMachineBlockEntity createMachineBlockEntity(BlockEntity blockEntity) {
        return new RefineryBlockEntity(blockEntity, schema, bars);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            dev.arubik.craftengine.machine.block.MachineBlockBehavior base =
                    (dev.arubik.craftengine.machine.block.MachineBlockBehavior) dev.arubik.craftengine.machine.block.MachineBlockBehavior.FACTORY
                            .create(block, arguments);
            String partBlockId = (String) arguments.getOrDefault("part_block_id", "cml:refinery");

            // Vertical 1x2: core at origin, the mixer one block up.
            MultiBlockSchema schema = new MultiBlockSchema(BlockPos.ZERO);
            schema.addPart(0, 1, 0, RefineryBehavior::isMixer);

            java.util.List<dev.arubik.craftengine.machine.menu.bar.MachineBar> bars =
                    dev.arubik.craftengine.machine.menu.bar.MachineBars.parse(arguments.get("bars"));

            return new RefineryBehavior(block, schema, partBlockId, base.getConnectableFaces(),
                    base.horizontalDirectionProperty, base.verticalDirectionProperty,
                    base.defaultIOConfig, bars);
        }
    }
}
