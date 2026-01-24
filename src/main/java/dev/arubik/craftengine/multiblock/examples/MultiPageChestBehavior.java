package dev.arubik.craftengine.multiblock.examples;

import java.util.Map;

import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.IOConfigurationProvider;
import dev.arubik.craftengine.multiblock.MultiBlockBehavior;
import dev.arubik.craftengine.multiblock.MultiBlockPartBlockEntity;
import dev.arubik.craftengine.multiblock.MultiBlockSchema;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * True multi-page 3x3x3 storage chest with navigation
 * - 3 pages of 45 slots each (135 slots total)
 * - Navigation buttons to switch between pages
 * - Hopper IO: Input from top/sides, output from bottom
 */
public class MultiPageChestBehavior extends MultiBlockBehavior {

    public static final Key FACTORY_KEY = Key.of("polyfills:multipage_chest");
    public static final Factory FACTORY = new Factory();

    public MultiPageChestBehavior(CustomBlock customBlock, MultiBlockSchema schema, String partBlockId) {
        super(customBlock, schema, partBlockId);
    }

    public MultiPageChestBehavior(CustomBlock customBlock, MultiBlockSchema schema, String partBlockId,
            java.util.List<Direction> connectableFaces,
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.HorizontalDirection> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            IOConfiguration ioConfig) {
        super(customBlock, schema, partBlockId, connectableFaces, horizontalDirectionProperty,
                verticalDirectionProperty, ioConfig);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            String partBlockId = (String) arguments.getOrDefault("part_block_id", "craftengine:multiblock_part");

            // Define 3x3x3 Schema - Core at center (1,1,1)
            BlockPos coreOffset = new BlockPos(1, 1, 1);
            MultiBlockSchema schema = new MultiBlockSchema(coreOffset);

            // All positions must be Iron Blocks
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        schema.addPart(x, y, z, (state) -> state.is(BlockTags.LOGS));
                    }
                }
            }
            schema.addPart(1, 2, 1, (state) -> state.is(BlockTags.TRAPDOORS));

            dev.arubik.craftengine.machine.block.MachineBlockBehavior base = (dev.arubik.craftengine.machine.block.MachineBlockBehavior) dev.arubik.craftengine.machine.block.MachineBlockBehavior.FACTORY
                    .create(block, arguments);

            MultiPageChestBehavior behavior = new MultiPageChestBehavior(block, schema, partBlockId,
                    base.getConnectableFaces(), base.horizontalDirectionProperty, base.verticalDirectionProperty,
                    base.defaultIOConfig);

            // Set IO configuration provider for hopper access
            behavior.withIOProvider(new IOConfigurationProvider() {
                @Override
                public IOConfiguration configurePartIO(BlockPos relativePos) {
                    // relativePos is relative to core (0,0,0 = core position)
                    int y = relativePos.getY();

                    // Core (0,0,0) - no direct IO
                    if (relativePos.equals(BlockPos.ZERO)) {
                        return new IOConfiguration.Closed(); // No IO for core
                    }

                    IOConfiguration.Simple config = new IOConfiguration.Simple();

                    // Top layer (y > 0) - Input (Fills pages sequentially)
                    if (y > 0) {
                        for (Direction dir : Direction.values()) {
                            if (dir == Direction.DOWN)
                                continue;
                            config.addInput(IOConfiguration.IOType.ITEM, dir);
                        }
                    }
                    // Middle layer (y == 0) - Input (Fills pages sequentially)
                    else if (y == 0) {
                        for (Direction dir : Direction.values()) {
                            if (dir == Direction.DOWN || dir == Direction.UP)
                                continue;
                            config.addInput(IOConfiguration.IOType.ITEM, dir);
                        }
                    }
                    // Bottom layer (y < 0) - Output (Extracts from pages sequentially)
                    else {
                        config.addOutput(IOConfiguration.IOType.ITEM, Direction.DOWN);
                    }

                    return config;
                }
            });

            return behavior;
        }
    }

    @Override
    protected dev.arubik.craftengine.multiblock.MultiBlockMachineBlockEntity createMachineBlockEntity(
            net.momirealms.craftengine.core.world.BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        return new MultiPageChestMachineBlockEntity(pos, state, schema);
    }

    @Override
    protected InteractionResult onInteractFormed(UseOnContext context, BlockEntity core, Level level,
            BlockPos corePos) {

        System.out.println(
                "[MultiPageChestBehavior] onInteractFormed called, core type: " + core.getClass().getSimpleName());

        if (core instanceof MultiPageChestMachineBlockEntity chest) {
            net.minecraft.world.entity.player.Player player = (net.minecraft.world.entity.player.Player) context
                    .getPlayer().serverPlayer();
            chest.openMenu(player);
            return InteractionResult.SUCCESS;
        }

        if (core instanceof MultiBlockPartBlockEntity partEntity) {
            System.out.println(
                    "[MultiPageChestBehavior] Core is MultiBlockPartBlockEntity, creating temporary machine entity for menu");

            // Get the immutable block state
            net.momirealms.craftengine.core.world.BlockPos cePos = new net.momirealms.craftengine.core.world.BlockPos(
                    corePos.getX(), corePos.getY(), corePos.getZ());
            net.momirealms.craftengine.core.block.ImmutableBlockState state = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                    .getOptionalCustomBlockState(level.getBlockState(corePos)).orElse(null);

            if (state != null) {
                // Create the machine entity (it will load data from CustomBlockData)
                MultiPageChestMachineBlockEntity chest = new MultiPageChestMachineBlockEntity(cePos, state, schema);

                chest.setWorld(context.getLevel().storageWorld());
                net.minecraft.world.entity.player.Player player = (net.minecraft.world.entity.player.Player) context
                        .getPlayer().serverPlayer();
                chest.openMenu(player);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    protected void onDisassemble(Level level, BlockPos pos, BlockEntity core) {
        // Container persistence is handled by CustomBlockData automatically
        super.onDisassemble(level, pos, core);
    }
}
