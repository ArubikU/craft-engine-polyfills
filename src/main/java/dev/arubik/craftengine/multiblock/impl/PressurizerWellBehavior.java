package dev.arubik.craftengine.multiblock.impl;

import dev.arubik.craftengine.multiblock.MultiBlockBehavior;
import dev.arubik.craftengine.multiblock.MultiBlockMachineBlockEntity;
import dev.arubik.craftengine.multiblock.MultiBlockSchema;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

/**
 * Pressurizer Well multiblock ({@code polyfills:pressurizer_well}).
 *
 * <p>A 3×3×6 tower built entirely from {@code cml:pressurizer_well} blocks. The player right-clicks the
 * CENTRAL-BOTTOM block to FORM it: the schema (core at the bottom-centre, the other 53 blocks as parts)
 * is validated AND the block directly below the core must be a nitrogenated-cal block (a valid vein),
 * else forming fails. Once formed the {@link PressurizerWellBlockEntity} consumes steam and raises the
 * anchored vein's pump limit. Break any block to disassemble.</p>
 */
public class PressurizerWellBehavior extends MultiBlockBehavior {

    public static final Key FACTORY_KEY = Key.of("polyfills:pressurizer_well");
    public static final Factory FACTORY = new Factory();

    private static final String SELF_ID = "cml:pressurizer_well";

    public PressurizerWellBehavior(BlockDefinition block, MultiBlockSchema schema, String partBlockId,
            java.util.List<Direction> connectableFaces,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> h,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> v,
            dev.arubik.craftengine.multiblock.IOConfiguration ioConfig) {
        super(block, schema, partBlockId, connectableFaces, h, v, ioConfig);
    }

    @Override
    protected MultiBlockMachineBlockEntity createMachineBlockEntity(BlockEntity blockEntity) {
        return new PressurizerWellBlockEntity(blockEntity, schema);
    }

    /** A valid vein anchor: ANY block that is a gas vein node (has a {@code gas_provider} behavior). */
    private static boolean isCalBlock(Level level, BlockPos pos) {
        return dev.arubik.craftengine.machine.block.entity.GasPumpBlockEntity.providerAt(level, pos) != null;
    }

    /** True when {@code state} is a {@code cml:pressurizer_well} block (the tower's casing/core). */
    private static boolean isWellBlock(BlockState state) {
        var cs = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        return cs != null && cs.owner() != null && SELF_ID.equals(cs.owner().value().id().toString());
    }

    @Override
    protected boolean canFormAt(Level level, BlockPos corePos) {
        // The core is the central-bottom block; require a cal vein directly beneath it.
        return isCalBlock(level, corePos.below());
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            dev.arubik.craftengine.machine.block.MachineBlockBehavior base =
                    (dev.arubik.craftengine.machine.block.MachineBlockBehavior) dev.arubik.craftengine.machine.block.MachineBlockBehavior.FACTORY
                            .create(block, arguments);
            String partBlockId = (String) arguments.getOrDefault("part_block_id", "cml:pressurizer_well");

            // 3×3×6 tower; core = central-bottom (1,0,1). Every other cell must be a pressurizer_well block.
            BlockPos coreOffset = new BlockPos(1, 0, 1);
            MultiBlockSchema schema = new MultiBlockSchema(coreOffset);
            for (int x = 0; x < 3; x++)
                for (int z = 0; z < 3; z++)
                    for (int y = 0; y < 6; y++)
                        schema.addPart(x, y, z, PressurizerWellBehavior::isWellBlock);

            PressurizerWellBehavior beh = new PressurizerWellBehavior(block, schema, partBlockId,
                    base.getConnectableFaces(),
                    base.horizontalDirectionProperty, base.verticalDirectionProperty, base.defaultIOConfig);
            beh.setAssemblyItems(parseAssemblyItems(arguments.get("assembly-items")));
            return beh;
        }

        /** Parse {@code assembly-items: [cml:iron_hammer, ...]} into Keys (empty list = any hammer). */
        private static java.util.List<Key> parseAssemblyItems(Object o) {
            java.util.List<Key> out = new java.util.ArrayList<>();
            if (o instanceof java.util.List<?> list) {
                for (Object e : list) {
                    String s = String.valueOf(e);
                    int i = s.indexOf(':');
                    out.add(i < 0 ? Key.of("cml", s) : Key.of(s.substring(0, i), s.substring(i + 1)));
                }
            }
            return out;
        }
    }
}
