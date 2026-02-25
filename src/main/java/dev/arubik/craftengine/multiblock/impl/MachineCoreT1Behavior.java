package dev.arubik.craftengine.multiblock.impl;

import java.util.Map;

import org.bukkit.persistence.PersistentDataType;

import dev.arubik.craftengine.multiblock.MultiBlockBehavior;
import dev.arubik.craftengine.multiblock.MultiBlockMachineBlockEntity;
import dev.arubik.craftengine.multiblock.MultiBlockSchema;
import dev.arubik.craftengine.multiblock.examples.MultiPageChestMachineBlockEntity;
import dev.arubik.craftengine.util.TypedKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.properties.EnumProperty;
import net.momirealms.craftengine.core.block.properties.Property;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import net.momirealms.craftengine.core.entity.player.InteractionResult;

public class MachineCoreT1Behavior extends MultiBlockBehavior {

    public static final Factory FACTORY = new Factory();

    // Schemas
    private final MultiBlockSchema chestSchema;
    private final MultiBlockSchema smelterSchema;

    // Properties
    protected final EnumProperty<MachineType> MACHINE_TYPE;

    public MachineCoreT1Behavior(CustomBlock customBlock, String partBlockId) {
        super(customBlock, new MultiBlockSchema(BlockPos.ZERO), partBlockId); // Default schema is placeholder

        // Define Chest Schema (3x3x3 Box of Copper Blocks)
        this.chestSchema = new MultiBlockSchema(new BlockPos(1, 1, 1)); // Core in center
        fillBox(chestSchema, -1, -1, -1, 1, 1, 1,
                (state) -> state.is(net.minecraft.world.level.block.Blocks.COPPER_BLOCK));

        // Define Smelter Schema (3x3x3 Hollow Box of Iron Blocks)
        this.smelterSchema = new MultiBlockSchema(new BlockPos(1, 1, 1)); // Core in center
        fillHollowBox(smelterSchema, -1, -1, -1, 1, 1, 1,
                (state) -> state.is(net.minecraft.world.level.block.Blocks.IRON_BLOCK));

        // Load Property
        Property<?> prop = customBlock.getProperty("machine_type");
        if (prop instanceof EnumProperty<?> enumProp && enumProp.valueClass() == MachineType.class) {
            this.MACHINE_TYPE = (EnumProperty<MachineType>) enumProp;
        } else {
            throw new IllegalStateException(
                    "CustomBlock for MachineCoreT1Behavior must have EnumProperty<MachineType> 'machine_type'");
        }
    }

    private void fillBox(MultiBlockSchema schema, int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
            java.util.function.Predicate<net.minecraft.world.level.block.state.BlockState> predicate) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    schema.addPart(x, y, z, predicate);
                }
            }
        }
    }

    private void fillHollowBox(MultiBlockSchema schema, int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
            java.util.function.Predicate<net.minecraft.world.level.block.state.BlockState> predicate) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ) {
                        schema.addPart(x, y, z, predicate);
                    }
                }
            }
        }
    }

    @Override
    protected boolean tryFormMachine(net.minecraft.world.level.Level level, BlockPos pos, ImmutableBlockState state) {
        MultiBlockSchema original = this.schema;

        // Try Smelter
        this.schema = smelterSchema;
        if (super.tryFormMachine(level, pos, state)) {
            if (state != null) {
                ImmutableBlockState newState = state.with(MACHINE_TYPE, MachineType.SMELTER);
                level.setBlock(pos, (BlockState) newState.customBlockState().literalObject(), 3);
            }
            this.schema = original;
            return true;
        }

        // Try Chest
        this.schema = chestSchema;
        if (super.tryFormMachine(level, pos, state)) {
            if (state != null) {
                ImmutableBlockState newState = state.with(MACHINE_TYPE, MachineType.CHEST);
                level.setBlock(pos, (BlockState) newState.customBlockState().literalObject(), 3);
            }
            this.schema = original;
            return true;
        }

        this.schema = original;
        return false;
    }

    @Override
    public dev.arubik.craftengine.multiblock.IOConfigurationProvider getIOProvider() {
        // Return provider based on current schema
        if (this.schema == smelterSchema) {
            return new dev.arubik.craftengine.multiblock.IOConfigurationProvider() {
                @Override
                public dev.arubik.craftengine.multiblock.IOConfiguration configurePartIO(BlockPos relativePos) {
                    // For smelter, all parts have same IO config
                    dev.arubik.craftengine.multiblock.IOConfiguration.Simple config = new dev.arubik.craftengine.multiblock.IOConfiguration.Simple();

                    // All directions are available for IO
                    for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
                        // Items
                        config.addInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, dir);
                        config.addOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, dir);

                        // Fluids (Water input)
                        config.addInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID, dir);

                        // Gas (Steam output)
                        config.addOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS, dir);
                    }

                    return config;
                }
            };
        } else if (this.schema == chestSchema) {
            return new dev.arubik.craftengine.multiblock.IOConfigurationProvider() {
                @Override
                public dev.arubik.craftengine.multiblock.IOConfiguration configurePartIO(BlockPos relativePos) {
                    // relativePos is relative to core (0,0,0 = core position)
                    int y = relativePos.getY();

                    // Core (0,0,0) - no direct IO
                    if (relativePos.equals(BlockPos.ZERO)) {
                        return new dev.arubik.craftengine.multiblock.IOConfiguration.Closed();
                    }

                    dev.arubik.craftengine.multiblock.IOConfiguration.Simple config = new dev.arubik.craftengine.multiblock.IOConfiguration.Simple();

                    // Top layer (y > 0) - Input (Fills pages sequentially)
                    if (y > 0) {
                        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
                            if (dir == net.minecraft.core.Direction.DOWN)
                                continue;
                            config.addInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, dir);
                        }
                    }
                    // Middle layer (y == 0) - Input (Fills pages sequentially)
                    else if (y == 0) {
                        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
                            if (dir == net.minecraft.core.Direction.DOWN || dir == net.minecraft.core.Direction.UP)
                                continue;
                            config.addInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, dir);
                        }
                    }
                    // Bottom layer (y < 0) - Output (Extracts from pages sequentially)
                    else {
                        config.addOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM,
                                net.minecraft.core.Direction.DOWN);
                    }

                    return config;
                }
            };
        }

        return super.getIOProvider();
    }

    @Override
    protected MultiBlockMachineBlockEntity createMachineBlockEntity(net.momirealms.craftengine.core.world.BlockPos pos,
            ImmutableBlockState state) {

        // When created via `tryFormMachine` flow above, the state passed IN might not
        // have the TYPE yet
        // because we update it AFTER.
        // So we need to rely on the active schema being set in `this.schema` when this
        // is called.

        if (this.schema == smelterSchema) {
            return new IndustrialSmelterBlockEntity(pos, state, smelterSchema);
        } else if (this.schema == chestSchema) {
            return new MultiPageChestMachineBlockEntity(pos, state, chestSchema);
        }

        // Fallback for loading from disk (when schema validation isn't running)
        // Check state
        MachineType type = state.get(MACHINE_TYPE);
        if (type == MachineType.SMELTER) {
            return new IndustrialSmelterBlockEntity(pos, state, smelterSchema);
        } else if (type == MachineType.CHEST) {
            return new MultiPageChestMachineBlockEntity(pos, state, chestSchema);
        }

        // Default fallthrough (shouldn't happen for formed machinery)
        return new MultiPageChestMachineBlockEntity(pos, state, chestSchema);
    }

    @Override
    protected InteractionResult onInteractFormed(UseOnContext context, BlockEntity core, Level level, BlockPos pos) {
        if (core instanceof MultiBlockMachineBlockEntity machine) {
            net.minecraft.world.entity.player.Player nmsPlayer = (net.minecraft.world.entity.player.Player) context
                    .getPlayer().serverPlayer();
            machine.openMenu(nmsPlayer);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            String partBlockId = (String) arguments.getOrDefault("part_block_id", "craftengine:multiblock_part");
            return new MachineCoreT1Behavior(block, partBlockId);
        }
    }
}
