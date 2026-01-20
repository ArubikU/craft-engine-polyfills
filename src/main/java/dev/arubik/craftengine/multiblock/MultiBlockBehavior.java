package dev.arubik.craftengine.multiblock;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.bukkit.persistence.PersistentDataType;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.util.CustomBlockData;
import dev.arubik.craftengine.util.TypedKey;
import dev.arubik.craftengine.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.BukkitBlockManager;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlockBehavior;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.entity.player.InteractionHand;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.util.HorizontalDirection;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.behavior.FluidCarrier;
import dev.arubik.craftengine.gas.GasCarrier;
import dev.arubik.craftengine.gas.GasStack;

/**
 * Unified MultiBlock Behavior that handles both CORE and PART blocks.
 * Supports optional machine capability and IO configuration.
 * Replaces MultiBlockCoreBehavior, MultiBlockPartBehavior,
 * MultiBlockMachineBehavior, and MultiBlockMachinePartBehavior.
 */
public class MultiBlockBehavior extends dev.arubik.craftengine.machine.block.MachineBlockBehavior {

    public static final Key FACTORY_KEY = Key.of("polyfills:multiblock");
    public static final Factory FACTORY = new Factory();

    protected MultiBlockSchema schema;
    protected String partBlockId;
    protected IOConfigurationProvider ioProvider; // Optional: for IO configuration

    // BlockState properties
    protected final net.momirealms.craftengine.core.block.properties.Property<MultiBlockRole> MULTIBLOCK_ROLE;
    protected final net.momirealms.craftengine.core.block.properties.Property<net.momirealms.craftengine.core.util.HorizontalDirection> HORIZONTAL_FACING; // Optional
    protected final net.momirealms.craftengine.core.block.properties.Property<net.momirealms.craftengine.core.util.Direction> FULL_FACING; // Optional

    private static final TypedKey<Boolean> KEY_FORMED = TypedKey.of("craftengine", "multiblock_formed",
            PersistentDataType.BOOLEAN);

    public MultiBlockBehavior(CustomBlock customBlock, MultiBlockSchema schema, String partBlockId) {
        super(customBlock);
        this.schema = schema;
        this.partBlockId = partBlockId;
        this.ioProvider = IOConfigurationProvider.OPEN;

        net.momirealms.craftengine.core.block.properties.Property<MultiBlockRole> roleProperty = (net.momirealms.craftengine.core.block.properties.Property<MultiBlockRole>) customBlock
                .getProperty("multiblock_role");

        if (roleProperty == null) {
            throw new IllegalStateException("CustomBlock must have 'multiblock_role' property defined");
        }
        this.MULTIBLOCK_ROLE = roleProperty;
        this.FULL_FACING = this.verticalDirectionProperty;
        this.HORIZONTAL_FACING = this.horizontalDirectionProperty;
    }

    @Override
    public dev.arubik.craftengine.multiblock.IOConfiguration getIOConfiguration(net.minecraft.world.level.Level level,
            net.minecraft.core.BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be instanceof MultiBlockPartBlockEntity part) {
            dev.arubik.craftengine.multiblock.IOConfiguration config = part.getIOConfiguration();
            if (config != null)
                return config;
        }
        return defaultIOConfig;
    }

    @Override
    public boolean canConnectTo(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
            net.minecraft.core.Direction direction) {
        dev.arubik.craftengine.multiblock.IOConfiguration config = getIOConfiguration(level, pos);
        if (config == null)
            return true;

        return config.canConnect(direction);
    }

    /**
     * Set IO configuration provider
     */
    public MultiBlockBehavior withIOProvider(IOConfigurationProvider provider) {
        this.ioProvider = provider != null ? provider : IOConfigurationProvider.OPEN;
        return this;
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            // This is a base factory - typically extended by specific multiblock types
            String partBlockId = (String) arguments.getOrDefault("part_block_id", "craftengine:multiblock_part");
            BlockPos coreOffset = BlockPos.ZERO; // Default, should be overridden
            MultiBlockSchema schema = new MultiBlockSchema(coreOffset);
            return new MultiBlockBehavior(block, schema, partBlockId);
        }
    }

    protected MultiBlockMachineBlockEntity createMachineBlockEntity(net.momirealms.craftengine.core.world.BlockPos pos,
            ImmutableBlockState state) {
        throw new UnsupportedOperationException("Machine multiblocks must override createMachineBlockEntity()");
    }

    // ========== EntityBlockBehavior Implementation ==========

    @Override
    public BlockEntity createBlockEntity(net.momirealms.craftengine.core.world.BlockPos pos,
            ImmutableBlockState state) {
        // Check role to determine entity type
        MultiBlockRole role = state.get(MULTIBLOCK_ROLE);

        if (role == MultiBlockRole.CORE) {
            // Create machine block entity for CORE in machine multiblocks
            return (BlockEntity) createMachineBlockEntity(pos, state);
        } else {
            // Create part block entity for all PARTs and non-machine COREs
            return (BlockEntity) new MultiBlockPartBlockEntity(pos, state);
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> blockEntityType(ImmutableBlockState state) {
        // Check role to determine entity type
        MultiBlockRole role = state.get(MULTIBLOCK_ROLE);

        if (role == MultiBlockRole.CORE) {
            // Machine block entity type for CORE in machine multiblocks
            return (BlockEntityType<T>) BukkitBlockEntityTypes.PERSISTENT_BLOCK_ENTITY_TYPE;
        } else {
            // Part block entity type for all PARTs and non-machine COREs
            return (BlockEntityType<T>) BukkitBlockEntityTypes.PERSISTENT_BLOCK_ENTITY_TYPE;
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> createSyncBlockEntityTicker(
            CEWorld world, ImmutableBlockState state, BlockEntityType<T> type) {
        // Only tick if this is a machine multiblock
        if (state.get(MULTIBLOCK_ROLE) != MultiBlockRole.CORE) {
            return null;
        }

        return (lvl, pos, st, be) -> {
            if (be instanceof MultiBlockMachineBlockEntity machine) {
                // Tick the machine directly
                machine.tick((Level) lvl.world.serverWorld(), Utils.fromPos(pos), st);
            }
        };
    }

    // ========== Interaction Handling ==========

    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        if (context.getHand() != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        Level level = ((Level) ((BukkitWorld) context.getLevel()).platformWorld());
        BlockPos pos = new BlockPos(context.getClickedPos().x(), context.getClickedPos().y(),
                context.getClickedPos().z());

        // Get block entity
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (!(be instanceof MultiBlockPartBlockEntity mbe)) {
            return InteractionResult.PASS;
        }

        MultiBlockRole role = mbe.getRole();

        if (role == MultiBlockRole.CORE) {
            // Core block
            if (mbe.isFormed()) {
                return onInteractFormed(context, mbe, level, pos);
            } else {
                // Try to form
                if (tryForm(level, pos, state, mbe)) {
                    return InteractionResult.SUCCESS;
                }
            }
        } else if (role == MultiBlockRole.PART) {
            // Part block - delegate to core
            BlockPos corePos = mbe.getCorePos();
            if (corePos != null) {
                BlockEntity coreBe = BukkitBlockEntityTypes.getIfLoaded(level, corePos);
                if (coreBe instanceof MultiBlockPartBlockEntity coreEntity) {
                    return onInteractFormed(context, coreEntity, level, corePos);
                }
            }
        }

        return InteractionResult.PASS;
    }

    /**
     * Called when a formed multiblock is interacted with
     */
    protected InteractionResult onInteractFormed(UseOnContext context, BlockEntity core, Level level, BlockPos pos) {
        // If there's a machine capability, open the menu
        if (core instanceof MultiBlockMachineBlockEntity machine) {
            net.minecraft.world.entity.player.Player nmsPlayer = (net.minecraft.world.entity.player.Player) context
                    .getPlayer().serverPlayer();
            machine.openMenu(nmsPlayer);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    // ========== Formation Logic ==========

    protected boolean tryForm(Level level, BlockPos corePos, ImmutableBlockState currentState,
            MultiBlockPartBlockEntity coreEntity) {
        BlockPos coreOffset = schema.getCoreOffset();
        BlockPos origin = corePos.offset(-coreOffset.getX(), -coreOffset.getY(), -coreOffset.getZ());

        // 1. Verify all parts match the schema
        for (Map.Entry<BlockPos, java.util.function.Predicate<BlockState>> entry : schema.getParts().entrySet()) {
            BlockPos partPos = origin.offset(entry.getKey());
            if (partPos.equals(corePos))
                continue;

            BlockState state = level.getBlockState(partPos);
            if (!entry.getValue().test(state)) {
                return false; // Schema validation failed
            }
        }

        // 2. Mark core as formed
        coreEntity.setRole(MultiBlockRole.CORE);
        CustomBlockData.from(level, corePos).set(KEY_FORMED, true);

        // 4. Get part block state
        Optional<CustomBlock> partBlockOpt = BukkitBlockManager.instance().blockById(Key.from(partBlockId));
        if (partBlockOpt.isEmpty()) {
            return false;
        }
        Object nmsStateObject = partBlockOpt.get().defaultState().customBlockState().literalObject();
        BukkitWorld world = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(level));

        // 5. Replace all schema blocks with part blocks
        for (Map.Entry<BlockPos, java.util.function.Predicate<BlockState>> entry : schema.getParts().entrySet()) {
            BlockPos partPos = origin.offset(entry.getKey());
            if (partPos.equals(corePos))
                continue;

            BlockState originalState = level.getBlockState(partPos);

            try {
                // Set to part block
                FastNMS.INSTANCE.method$LevelWriter$setBlock(world.serverWorld(),
                        LocationUtils.toBlockPos(new net.momirealms.craftengine.core.world.BlockPos(
                                partPos.getX(), partPos.getY(), partPos.getZ())),
                        nmsStateObject, 3);

                // Configure the part
                BlockEntity partBe = BukkitBlockEntityTypes.getIfLoaded(level, partPos);
                if (partBe instanceof MultiBlockPartBlockEntity partEntity) {

                    // Set part role in BlockState
                    BlockState partState = level.getBlockState(partPos);
                    ImmutableBlockState partCustomState = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                            .getOptionalCustomBlockState(partState).orElse(null);
                    if (partCustomState != null) {
                        partCustomState = partCustomState.with(MULTIBLOCK_ROLE, MultiBlockRole.PART);
                        level.setBlock(partPos, (BlockState) partCustomState.customBlockState().literalObject(), 3);
                    }

                    partEntity.setCorePos(corePos);
                    partEntity.setOriginalBlock(originalState);

                    // Configure IO if provider is set
                    if (ioProvider != null) {
                        BlockPos relativePos = partPos.subtract(corePos);
                        IOConfiguration ioConfig = ioProvider.configurePartIO(relativePos);

                        // If using RelativeIO, configure facing (try both types)
                        if (ioConfig instanceof IOConfiguration.RelativeIO relativeIO) {
                            // Get core's BlockState
                            BlockState coreState = level.getBlockState(corePos);
                            ImmutableBlockState coreCustomState = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                                    .getOptionalCustomBlockState(coreState).orElse(null);

                            if (coreCustomState != null) {
                                Direction facingDir = null;

                                // Try full direction first (6-way)
                                if (FULL_FACING != null) {
                                    try {
                                        Object facing = coreCustomState.get(FULL_FACING);
                                        if (facing != null) {
                                            facingDir = Direction.valueOf(facing.toString());
                                        }
                                    } catch (Exception e) {
                                        // Property not available
                                    }
                                }

                                // If not found, try horizontal direction (4-way)
                                if (facingDir == null && HORIZONTAL_FACING != null) {
                                    try {
                                        net.momirealms.craftengine.core.util.HorizontalDirection horizontalFacing = coreCustomState
                                                .get(HORIZONTAL_FACING);
                                        if (horizontalFacing != null) {
                                            facingDir = DirectionalIOHelper.fromHorizontalDirection(horizontalFacing);
                                        }
                                    } catch (Exception e) {
                                        // Property not available
                                    }
                                }

                                // Apply facing if found
                                if (facingDir != null) {
                                    relativeIO.withFacing(facingDir);
                                }
                            }
                        }

                        if (ioConfig != null) {
                            partEntity.setIOConfiguration(ioConfig);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Rollback on error
                disassemble(level, corePos, coreEntity);
                return false;
            }
        }

        // 6. Call onForm hook
        onForm(level, corePos, coreEntity);
        return true;
    }

    /**
     * Hook called when multiblock is formed
     */
    protected void onForm(Level level, BlockPos pos, MultiBlockPartBlockEntity core) {
        // Override in subclasses if needed
    }

    // ========== Disassembly Logic ==========

    protected void disassemble(Level level, BlockPos corePos, MultiBlockPartBlockEntity coreEntity) {
        CustomBlockData.from(level, corePos).set(KEY_FORMED, false);

        // Reset core role in BlockState
        BlockState coreState = level.getBlockState(corePos);
        ImmutableBlockState coreCustomState = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                .getOptionalCustomBlockState(coreState).orElse(null);
        if (coreCustomState != null) {
            coreCustomState = coreCustomState.with(MULTIBLOCK_ROLE, MultiBlockRole.NONE);
            level.setBlock(corePos, (BlockState) coreCustomState.customBlockState().literalObject(), 3);
        }

        BlockPos coreOffset = schema.getCoreOffset();
        BlockPos origin = corePos.offset(-coreOffset.getX(), -coreOffset.getY(), -coreOffset.getZ());

        // Restore all parts
        for (Map.Entry<BlockPos, java.util.function.Predicate<BlockState>> entry : schema.getParts().entrySet()) {
            BlockPos partPos = origin.offset(entry.getKey());
            if (partPos.equals(corePos))
                continue;

            BlockEntity partBe = BukkitBlockEntityTypes.getIfLoaded(level, partPos);
            if (partBe instanceof MultiBlockPartBlockEntity partEntity) {
                BlockState originalState = partEntity.getOriginalBlock();
                if (originalState != null) {
                    level.setBlock(partPos, originalState, 3);
                } else {
                    level.setBlock(partPos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        onDisassemble(level, corePos, coreEntity);
    }

    /**
     * Hook called when multiblock is disassembled
     */
    protected void onDisassemble(Level level, BlockPos pos, MultiBlockPartBlockEntity core) {
        // Override in subclasses if needed
    }

    // ========== Block Removal Handling ==========

    @Override
    public void onRemove(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        BlockState state = (BlockState) args[0];
        BlockState newState = (BlockState) args[3];

        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
            if (be instanceof MultiBlockPartBlockEntity mbe) {
                if (mbe.getRole() == MultiBlockRole.CORE && mbe.isFormed()) {
                    disassemble(level, pos, mbe);
                } else if (mbe.getRole() == MultiBlockRole.PART) {
                    BlockPos corePos = mbe.getCorePos();
                    if (corePos != null) {
                        BlockEntity coreBe = BukkitBlockEntityTypes.getIfLoaded(level, corePos);
                        if (coreBe instanceof MultiBlockPartBlockEntity coreEntity) {
                            disassemble(level, corePos, coreEntity);
                        }
                    }
                }
            }
        }

        super.onRemove(thisBlock, args, superMethod);
    }

    // ========== Redstone Support ==========

    /**
     * Override to provide redstone signal output
     * Delegates to the part block entity which can check IO configuration
     */
    @Override
    public int getSignal(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        // args[0] = BlockState, args[1] = BlockGetter, args[2] = BlockPos, args[3] =
        // Direction
        try {
            BlockState state = (BlockState) args[0];
            net.minecraft.world.level.Level level = (net.minecraft.world.level.Level) args[1];
            BlockPos pos = (BlockPos) args[2];
            net.minecraft.core.Direction direction = (net.minecraft.core.Direction) args[3];

            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
            if (be instanceof MultiBlockPartBlockEntity part) {
                if (part.canOutputRedstone(direction)) {
                    return part.getRedstoneOutput();
                }
            }

        } catch (Exception e) {
            // Fail silently
        }
        return 0;
    }

    /**
     * Override to provide direct redstone signal
     * Delegates to the part block entity
     */
    @Override
    public int getDirectSignal(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        // For most multiblocks, direct signal = indirect signal
        return getSignal(thisBlock, args, superMethod);
    }

    /**
     * Declare this as a potential signal source if any part can output redstone
     */
    @Override
    public boolean isSignalSource(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        try {
            net.minecraft.world.level.Level level = (net.minecraft.world.level.Level) args[1];
            BlockPos pos = (BlockPos) args[2];
            net.minecraft.core.Direction direction = (net.minecraft.core.Direction) args[3];

            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
            if (be instanceof MultiBlockPartBlockEntity part) {
                return part.canOutputRedstone(direction);
            }
            if (be instanceof MultiBlockMachineBlockEntity machine) {
                return machine.canOutputRedstone(direction);
            }

        } catch (Exception e) {
            // Fail silently
        }
        return false;
    }

    // ========== Fluid Carrier Implementation (Delegation) ==========

    @Override
    public FluidStack getStored(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);

        if (be instanceof MultiBlockMachineBlockEntity) {
            return super.getStored(level, pos);
        }

        if (be instanceof MultiBlockPartBlockEntity part) {
            BlockEntity core = BukkitBlockEntityTypes.getIfLoaded(level, part.getCorePos());
            if (core instanceof MultiBlockMachineBlockEntity co) {
                int slot = part.getIOConfiguration().getTargetSlot(IOConfiguration.IOType.FLUID);
                return co.getFluidInSlot(slot);
            }
        }

        return FluidStack.EMPTY;
    }

    @Override
    public int insertFluid(Level level, BlockPos pos, FluidStack stack, Direction side) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);

        // Direct machine at this position - use parent logic
        if (be instanceof MultiBlockMachineBlockEntity) {
            return super.insertFluid(level, pos, stack, side);
        }

        // Part - check IO and redirect to core
        if (be instanceof MultiBlockPartBlockEntity part) {
            if (!part.acceptsInput(IOConfiguration.IOType.FLUID, side))
                return 0;

            int slot = part.getIOConfiguration().getTargetSlot(IOConfiguration.IOType.FLUID, side);
            BlockEntity core = BukkitBlockEntityTypes.getIfLoaded(level, part.getCorePos());
            if (core instanceof MultiBlockMachineBlockEntity co) {
                return co.insertFluidInSlot(slot, stack, level);
            }
        }

        return 0;
    }

    @Override
    public int extractFluid(Level level, BlockPos pos, int max, Consumer<FluidStack> drained, Direction side) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);

        // Direct machine at this position - use parent logic
        if (be instanceof MultiBlockMachineBlockEntity) {
            return super.extractFluid(level, pos, max, drained, side);
        }

        // Part - check IO and redirect to core
        if (be instanceof MultiBlockPartBlockEntity part) {
            if (!part.providesOutput(IOConfiguration.IOType.FLUID, side))
                return 0;

            int slot = part.getIOConfiguration().getTargetSlot(IOConfiguration.IOType.FLUID, side);
            BlockEntity core = BukkitBlockEntityTypes.getIfLoaded(level, part.getCorePos());
            if (core instanceof MultiBlockMachineBlockEntity co) {
                return co.extractFluidInSlot(slot, max, level, drained);
            }
        }

        return 0;
    }

    @Override
    public dev.arubik.craftengine.util.TransferAccessMode getAccessMode() {
        return dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE;
    }

    // ========== Gas Carrier Implementation (Delegation) ==========

    @Override
    public GasStack getStoredGas(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);

        if (be instanceof MultiBlockMachineBlockEntity) {
            return super.getStoredGas(level, pos);
        }

        if (be instanceof MultiBlockPartBlockEntity part) {
            BlockEntity core = BukkitBlockEntityTypes.getIfLoaded(level, part.getCorePos());
            if (core instanceof MultiBlockMachineBlockEntity co) {
                return co.getGasInSlot(part.getIOConfiguration().getTargetSlot(IOConfiguration.IOType.GAS));
            }
        }

        return GasStack.EMPTY;
    }

    @Override
    public int insertGas(Level level, BlockPos pos, GasStack stack, Direction side) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);

        // Direct machine at this position - use parent logic
        if (be instanceof MultiBlockMachineBlockEntity) {
            return super.insertGas(level, pos, stack, side);
        }

        // Part - check IO and redirect to core
        if (be instanceof MultiBlockPartBlockEntity part) {
            if (!part.acceptsInput(IOConfiguration.IOType.GAS, side))
                return 0;

            int slot = part.getIOConfiguration().getTargetSlot(IOConfiguration.IOType.GAS, side);
            BlockEntity core = BukkitBlockEntityTypes.getIfLoaded(level, part.getCorePos());
            if (core instanceof MultiBlockMachineBlockEntity co) {
                return co.insertGasInSlot(slot, stack, level);
            }
        }

        return 0;
    }

    @Override
    public int extractGas(Level level, BlockPos pos, int max, Consumer<GasStack> drained, Direction side) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);

        // Direct machine at this position - use parent logic
        if (be instanceof MultiBlockMachineBlockEntity) {
            return super.extractGas(level, pos, max, drained, side);
        }

        // Part - check IO and redirect to core
        if (be instanceof MultiBlockPartBlockEntity part) {
            if (!part.providesOutput(IOConfiguration.IOType.GAS, side))
                return 0;

            int slot = part.getIOConfiguration().getTargetSlot(IOConfiguration.IOType.GAS, side);
            BlockEntity core = BukkitBlockEntityTypes.getIfLoaded(level, part.getCorePos());
            if (core instanceof MultiBlockMachineBlockEntity co) {
                return co.extractGasInSlot(slot, max, level, drained);
            }
        }

        return 0;
    }

}
