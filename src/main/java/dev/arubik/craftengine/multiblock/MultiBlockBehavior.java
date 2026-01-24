package dev.arubik.craftengine.multiblock;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.bukkit.persistence.PersistentDataType;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
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
import net.momirealms.craftengine.core.world.chunk.CEChunk;
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

    private static final TypedKey<Boolean> KEY_DISASSEMBLING = TypedKey.of("craftengine", "multiblock_disassembling",
            PersistentDataType.BOOLEAN);

    public MultiBlockBehavior(CustomBlock customBlock, MultiBlockSchema schema, String partBlockId) {
        this(customBlock, schema, partBlockId, new java.util.ArrayList<>(), null, null, new IOConfiguration.Open());
    }

    public MultiBlockBehavior(CustomBlock customBlock, MultiBlockSchema schema, String partBlockId,
            java.util.List<net.minecraft.core.Direction> connectableFaces,
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.HorizontalDirection> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            IOConfiguration ioConfig) {
        super(customBlock, connectableFaces, horizontalDirectionProperty, verticalDirectionProperty, ioConfig);
        this.schema = schema;
        this.partBlockId = partBlockId;
        this.ioProvider = IOConfigurationProvider.OPEN;

        net.momirealms.craftengine.core.block.properties.Property<MultiBlockRole> roleProperty = (net.momirealms.craftengine.core.block.properties.Property<MultiBlockRole>) customBlock
                .getProperty("multiblock_role");

        if (roleProperty == null) {
            throw new IllegalStateException("CustomBlock must have 'multiblock_role' property defined");
        }
        this.MULTIBLOCK_ROLE = roleProperty;
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

    /**
     * rotate a position based on the facing direction
     * Assumes NORTH is the default schema direction
     */
    protected BlockPos rotate(BlockPos pos, Direction facing) {
        if (facing == null || facing == Direction.NORTH || facing == Direction.UP || facing == Direction.DOWN) {
            return pos;
        }

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        return switch (facing) {
            case SOUTH -> new BlockPos(-x, y, -z);
            case WEST -> new BlockPos(z, y, -x); // North (0,0,-1) -> West (-1,0,0)
            case EAST -> new BlockPos(-z, y, x); // North (0,0,-1) -> East (1,0,0)
            default -> pos;
        };
    }

    /**
     * Unrotate a direction based on the facing direction
     * Maps World Direction -> Schema Direction
     */
    protected Direction unrotate(Direction dir, Direction facing) {
        if (facing == null || facing == Direction.NORTH || facing == Direction.UP || facing == Direction.DOWN) {
            return dir;
        }

        if (dir.getAxis().isVertical())
            return dir;

        return switch (facing) {
            case SOUTH -> dir.getOpposite();
            case WEST -> dir.getClockWise(); // World West -> Schema North
            case EAST -> dir.getCounterClockWise(); // World East -> Schema North
            default -> dir;
        };
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            dev.arubik.craftengine.machine.block.MachineBlockBehavior base = (dev.arubik.craftengine.machine.block.MachineBlockBehavior) dev.arubik.craftengine.machine.block.MachineBlockBehavior.FACTORY
                    .create(block, arguments);
            String partBlockId = (String) arguments.getOrDefault("part_block_id", "craftengine:multiblock_part");
            BlockPos coreOffset = BlockPos.ZERO;
            MultiBlockSchema schema = new MultiBlockSchema(coreOffset);
            return new MultiBlockBehavior(block, schema, partBlockId, base.getConnectableFaces(),
                    base.horizontalDirectionProperty, base.verticalDirectionProperty,
                    base.defaultIOConfig);
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
        BlockEntity result;

        if (role == MultiBlockRole.CORE) {
            // Create machine block entity for CORE in machine multiblocks
            result = (BlockEntity) createMachineBlockEntity(pos, state);
        } else {
            // Create part block entity for all PARTs and non-machine COREs
            result = (BlockEntity) new MultiBlockPartBlockEntity(pos, state);
        }

        // Register disassembly hook
        if (result instanceof PersistentBlockEntity pbe) {
            registerDisassemblyHook(pbe);
        }

        return result;
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

        Level level = (Level) ((BukkitWorld) context.getLevel()).serverWorld();
        BlockPos pos = new BlockPos(context.getClickedPos().x(), context.getClickedPos().y(),
                context.getClickedPos().z());

        // Get block entity
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);

        // Debug logging
        System.out.println("[MultiBlockBehavior] useWithoutItem called at " + pos);
        System.out.println(
                "[MultiBlockBehavior] BlockEntity type: " + (be != null ? be.getClass().getSimpleName() : "null"));
        System.out.println("[MultiBlockBehavior] State role from property: " + state.get(MULTIBLOCK_ROLE));

        // Handle MultiBlockMachineBlockEntity (machine cores that aren't formed yet)
        if (be instanceof MultiBlockMachineBlockEntity machine) {
            System.out.println("[MultiBlockBehavior] Detected MultiBlockMachineBlockEntity");
            MultiBlockRole stateRole = state.get(MULTIBLOCK_ROLE);

            if (stateRole == MultiBlockRole.CORE) {
                // If it's a machine core and the state role is CORE, it's formed
                System.out.println("[MultiBlockBehavior] Machine role is CORE");
                return onInteractFormed(context, be, level, pos);
            } else {
                // Try to form - we need to create a temporary wrapper or adapt tryForm
                System.out.println("[MultiBlockBehavior] Attempting to form multiblock structure...");
                if (tryFormMachine(level, pos, state)) {
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        }

        if (!(be instanceof MultiBlockPartBlockEntity mbe)) {
            System.out.println("[MultiBlockBehavior] BlockEntity is not MultiBlockPartBlockEntity, returning PASS");
            return InteractionResult.PASS;
        }

        // Use BlockState role as primary source (entity role might be stale)
        MultiBlockRole stateRole = state.get(MULTIBLOCK_ROLE);
        MultiBlockRole entityRole = mbe.getRole();
        // Prefer BlockState role, but if it's NONE, fall back to entity role
        MultiBlockRole role = (stateRole != MultiBlockRole.NONE) ? stateRole : entityRole;

        System.out.println("[MultiBlockBehavior] Part role from entity: " + entityRole);
        System.out.println("[MultiBlockBehavior] Using role: " + role);
        System.out.println("[MultiBlockBehavior] Part isFormed: " + mbe.isFormed());

        if (role == MultiBlockRole.CORE) {
            // Core block - already formed
            if (mbe.isFormed()) {
                return onInteractFormed(context, mbe, level, pos);
            } else {
                // Try to form (shouldn't happen often, CORE should mean formed)
                if (tryForm(level, pos, state, mbe)) {
                    return InteractionResult.SUCCESS;
                }
            }
        } else if (role == MultiBlockRole.PART) {
            // Part block - delegate to core
            BlockPos corePos = mbe.getCorePos();
            System.out.println("[MultiBlockBehavior] PART block, corePos: " + corePos);
            if (corePos != null) {
                BlockEntity coreBe = BukkitBlockEntityTypes.getIfLoaded(level, corePos);
                System.out.println("[MultiBlockBehavior] Core entity type: "
                        + (coreBe != null ? coreBe.getClass().getSimpleName() : "null"));
                if (coreBe instanceof MultiBlockMachineBlockEntity coreEntity) {
                    return onInteractFormed(context, coreEntity, level, corePos);
                } else if (coreBe instanceof MultiBlockPartBlockEntity coreEntity) {
                    return onInteractFormed(context, coreEntity, level, corePos);
                }
            } else {
                // Part has no corePos - this shouldn't happen, but try to recover
                System.out.println("[MultiBlockBehavior] PART has no corePos set!");
            }
        } else if (role == MultiBlockRole.NONE) {
            // Block placed but not formed - try to form
            System.out.println("[MultiBlockBehavior] Role is NONE, attempting to form...");
            if (tryForm(level, pos, state, mbe)) {
                System.out.println("[MultiBlockBehavior] Formation successful!");
                return InteractionResult.SUCCESS;
            } else {
                System.out.println("[MultiBlockBehavior] Formation failed - schema not matched");
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
        System.out.println("[MultiBlockBehavior] tryForm called at " + corePos);

        // Determine Rotation
        Direction facing = Direction.NORTH;
        if (verticalDirectionProperty != null && currentState.get(verticalDirectionProperty) != null) {
            try {
                facing = Direction.valueOf(currentState.get(verticalDirectionProperty).toString());
            } catch (Exception ignored) {
            }
        } else if (horizontalDirectionProperty != null && currentState.get(horizontalDirectionProperty) != null) {
            try {
                facing = DirectionalIOHelper.fromHorizontalDirection(currentState.get(horizontalDirectionProperty));
            } catch (Exception ignored) {
            }
        }
        System.out.println("[MultiBlockBehavior] Detected facing: " + facing);

        BlockPos coreOffset = schema.getCoreOffset();
        System.out.println("[MultiBlockBehavior] Core offset in schema: " + coreOffset);
        System.out.println("[MultiBlockBehavior] Schema has " + schema.getParts().size() + " parts");

        // 1. Verify all parts match the schema
        for (Map.Entry<BlockPos, java.util.function.Predicate<BlockState>> entry : schema.getParts().entrySet()) {
            BlockPos partSchemaPos = entry.getKey();
            BlockPos relativePos = partSchemaPos.subtract(coreOffset);
            BlockPos rotatedRelative = rotate(relativePos, facing);
            BlockPos partPos = corePos.offset(rotatedRelative);

            System.out.println("[MultiBlockBehavior] Checking schema pos " + partSchemaPos +
                    " -> relative " + relativePos + " -> rotated " + rotatedRelative + " -> world " + partPos);

            if (partPos.equals(corePos)) {
                System.out.println("[MultiBlockBehavior]   Skipping core position");
                continue;
            }

            BlockState state = level.getBlockState(partPos);
            boolean matches = entry.getValue().test(state);
            System.out.println(
                    "[MultiBlockBehavior]   Block at " + partPos + ": " + state.getBlock() + " matches: " + matches);

            if (!matches) {
                System.out.println("[MultiBlockBehavior] Schema validation FAILED at " + partPos);
                return false; // Schema validation failed
            }
        }

        System.out.println("[MultiBlockBehavior] Schema validation PASSED!");

        // 2. Mark core as formed (by setting role) and update BlockState to CORE role
        coreEntity.remove(KEY_DISASSEMBLING);
        coreEntity.setRole(MultiBlockRole.CORE);
        BlockEntity coreBeForHooks = (BlockEntity) coreEntity;

        // Update the BlockState to have role=CORE (this is important for entity type
        // determination)
        ImmutableBlockState coreState = currentState.with(MULTIBLOCK_ROLE, MultiBlockRole.CORE);
        level.setBlock(corePos, (BlockState) coreState.customBlockState().literalObject(), 3);
        System.out.println("[MultiBlockBehavior] Updated core BlockState to role=CORE");

        // 3. Remove old BlockEntity and create the correct type
        // (MultiBlockMachineBlockEntity)
        BukkitWorld world = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(level));
        net.momirealms.craftengine.core.world.BlockPos cePos = new net.momirealms.craftengine.core.world.BlockPos(
                corePos.getX(), corePos.getY(), corePos.getZ());
        CEChunk chunk = world.storageWorld().getChunkAtIfLoaded(cePos.x() >> 4, cePos.z() >> 4);
        if (chunk != null) {
            // Remove old entity
            chunk.removeBlockEntity(cePos);
            System.out.println("[MultiBlockBehavior] Removed old BlockEntity");

            // Create and add new machine entity
            MultiBlockMachineBlockEntity newMachineEntity = createMachineBlockEntity(cePos, coreState);
            // Ensure flag is clear on new entity
            newMachineEntity.remove(KEY_DISASSEMBLING);

            // Set world before adding to chunk so the entity can load data
            newMachineEntity.setWorld(world.storageWorld());
            newMachineEntity.setChanged();
            chunk.addBlockEntity((BlockEntity) newMachineEntity);
            System.out.println("[MultiBlockBehavior] Created new machine BlockEntity: "
                    + newMachineEntity.getClass().getSimpleName() + " (FORMED)");
            coreEntity = null; // Reference is now invalid
            coreBeForHooks = (BlockEntity) newMachineEntity;
        }

        // 4. Get part block state - use partBlockId if defined, otherwise use the same
        // block as core
        Optional<CustomBlock> partBlockOpt = BukkitBlockManager.instance().blockById(Key.from(partBlockId));
        CustomBlock partBlock;
        if (partBlockOpt.isEmpty()) {
            System.out.println("[MultiBlockBehavior] Part block not found for id: " + partBlockId
                    + ", using customBlock as fallback");
            partBlock = this.block();
        } else {
            partBlock = partBlockOpt.get();
        }
        Object nmsStateObject = partBlock.defaultState().customBlockState().literalObject();

        // 5. Replace all schema blocks with part blocks
        for (Map.Entry<BlockPos, java.util.function.Predicate<BlockState>> entry : schema.getParts().entrySet()) {
            BlockPos partSchemaPos = entry.getKey();
            BlockPos relativePos = partSchemaPos.subtract(coreOffset);
            BlockPos rotatedRelative = rotate(relativePos, facing);
            BlockPos partPos = corePos.offset(rotatedRelative);
            if (partPos.equals(corePos))
                continue;

            BlockState originalState = level.getBlockState(partPos);
            System.out.println("[MultiBlockBehavior] Replacing part at " + partPos + " (original: "
                    + originalState.getBlock() + ")");

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

                    // Configure the part entity
                    partEntity.setRole(MultiBlockRole.PART);
                    partEntity.setCorePos(corePos);
                    partEntity.setOriginalBlock(originalState);
                    partEntity.setChanged();

                    // Configure IO if provider is set
                    if (ioProvider != null) {
                        // Pass unrotated relative pos to provider (Schema Space)
                        IOConfiguration ioConfig = ioProvider.configurePartIO(relativePos);

                        // Wrap in RotatedIOConfiguration to handle world directions
                        if (ioConfig != null) {
                            partEntity.setIOConfiguration(ioConfig);
                        }
                    }
                }
                System.out.println("[MultiBlockBehavior] Successfully replaced part at " + partPos);

            } catch (Exception e) {
                e.printStackTrace();
                // Rollback on error
                disassemble(level, corePos, coreBeForHooks);
                return false;
            }
        }

        // 6. Call onForm hook - we need to cast if possible
        if (coreBeForHooks instanceof MultiBlockPartBlockEntity part) {
            onForm(level, corePos, part);
        }

        // 7. Register disassembly hooks
        registerDisassemblyHooks(level, corePos, coreBeForHooks);

        return true;
    }

    private void registerDisassemblyHooks(Level level, BlockPos corePos, BlockEntity coreEntity) {
        if (coreEntity instanceof PersistentBlockEntity pbe) {
            registerDisassemblyHook(pbe);
        }

        // Also register on all parts
        Direction facing = getFacing(level, corePos);
        BlockPos coreOffset = schema.getCoreOffset();

        for (Map.Entry<BlockPos, java.util.function.Predicate<BlockState>> entry : schema.getParts().entrySet()) {
            BlockPos partSchemaPos = entry.getKey();
            BlockPos relativePos = partSchemaPos.subtract(coreOffset);
            BlockPos rotatedRelative = rotate(relativePos, facing);
            BlockPos partPos = corePos.offset(rotatedRelative);
            if (partPos.equals(corePos))
                continue;

            BlockEntity partBe = BukkitBlockEntityTypes.getIfLoaded(level, partPos);
            if (partBe instanceof PersistentBlockEntity pbe) {
                registerDisassemblyHook(pbe);
            }
        }
    }

    private void registerDisassemblyHook(PersistentBlockEntity pbe) {
        System.out.println("[MultiBlockBehavior] Registering disassembly hook for " + pbe.getClass().getSimpleName()
                + " at " + pbe.pos());
        pbe.setPreCleanup((be) -> {
            System.out.println("[MultiBlockBehavior] PRE-CLEANUP EXECUTION at " + be.pos());
            handleDisassemblyFromHook(be);
            return null;
        });
    }

    private void handleDisassemblyFromHook(PersistentBlockEntity be) {
        BlockPos pos = Utils.fromPos(be.pos());
        Level level = (Level) be.world.world.serverWorld();

        System.out.println("[MultiBlockBehavior] handleDisassemblyFromHook START at " + pos + " (Type: "
                + be.getClass().getSimpleName() + ")");

        BlockPos corePos = null;
        if (be instanceof MultiBlockPartBlockEntity part) {
            corePos = part.getCorePos();
            System.out.println("[MultiBlockBehavior]   Identified as MultiBlockPart, corePos=" + corePos);
        } else if (be instanceof MultiBlockMachineBlockEntity) {
            corePos = pos;
            System.out.println("[MultiBlockBehavior]   Identified as MultiBlockMachine (CORE), corePos=" + corePos);
        }

        if (corePos == null) {
            System.out.println("[MultiBlockBehavior]   ERROR: Could not identify corePos for " + pos);
            return;
        }

        BlockEntity coreBe = BukkitBlockEntityTypes.getIfLoaded(level, corePos);
        if (coreBe == null && corePos.equals(pos)) {
            coreBe = be; // Handle self if it's the core
        }

        if (coreBe instanceof PersistentBlockEntity pbe) {
            boolean isDisassembling = pbe.getOrDefault(KEY_DISASSEMBLING, false);

            // Interaction matching logic: treat as formed if it's a Machine and state is
            // CORE,
            // or if it's a Part and has corePos
            boolean isFormed = false;
            if (coreBe instanceof MultiBlockMachineBlockEntity) {
                ImmutableBlockState ibs = be.world
                        .getBlockStateAtIfLoaded(net.momirealms.craftengine.core.world.BlockPos.of(corePos.asLong()));
                isFormed = ibs.getNullable(MULTIBLOCK_ROLE) == MultiBlockRole.CORE;
            } else if (coreBe instanceof MultiBlockPartBlockEntity part) {
                isFormed = part.isFormed();
            }

            System.out.println("[MultiBlockBehavior]   Core found at " + corePos + ". Formed=" + isFormed
                    + ", Disassembling=" + isDisassembling);

            // Loop prevention: check if already disassembling
            if (isDisassembling) {
                System.out.println("[MultiBlockBehavior]   ALREADY disassembling, skipping redundant trigger.");
                return;
            }

            // Check if it's actually formed
            if (!isFormed) {
                System.out.println("[MultiBlockBehavior]   NOT formed, skipping disassembly.");
                return;
            }

            // Set disassembling flag on core
            pbe.set(KEY_DISASSEMBLING, true);

            System.out.println("[MultiBlockBehavior]   !!! TRIGGERING DISASSEMBLY !!!");

            try {
                // Handle drops if it's a container
                if (coreBe instanceof net.minecraft.world.WorldlyContainer container) {
                    System.out.println("[MultiBlockBehavior]   Dropping inventory for container core.");
                    dropInventory(level, corePos, container);
                }

                // Disassemble
                disassemble(level, corePos, coreBe);
            } finally {
                // Reset flag even if it was replaced (in case it wasn't)
                pbe.set(KEY_DISASSEMBLING, false);
            }
        }
    }

    protected Direction getFacing(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState coreCustomState = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                .getOptionalCustomBlockState(state).orElse(null);

        if (coreCustomState != null) {
            if (verticalDirectionProperty != null && coreCustomState.get(verticalDirectionProperty) != null) {
                try {
                    return Direction.valueOf(coreCustomState.get(verticalDirectionProperty).toString());
                } catch (Exception ignored) {
                }
            } else if (horizontalDirectionProperty != null
                    && coreCustomState.get(horizontalDirectionProperty) != null) {
                try {
                    return DirectionalIOHelper
                            .fromHorizontalDirection(coreCustomState.get(horizontalDirectionProperty));
                } catch (Exception ignored) {
                }
            }
        }
        return Direction.NORTH;
    }

    /**
     * Try to form a multiblock for machine-type cores.
     * This variant doesn't require a MultiBlockPartBlockEntity since machine cores
     * use MultiBlockMachineBlockEntity instead.
     */
    protected boolean tryFormMachine(Level level, BlockPos corePos, ImmutableBlockState currentState) {
        System.out.println("[MultiBlockBehavior] tryFormMachine called at " + corePos);

        // Determine Rotation
        Direction facing = Direction.NORTH;
        if (verticalDirectionProperty != null && currentState.get(verticalDirectionProperty) != null) {
            try {
                facing = Direction.valueOf(currentState.get(verticalDirectionProperty).toString());
            } catch (Exception ignored) {
            }
        } else if (horizontalDirectionProperty != null && currentState.get(horizontalDirectionProperty) != null) {
            try {
                facing = DirectionalIOHelper.fromHorizontalDirection(currentState.get(horizontalDirectionProperty));
            } catch (Exception ignored) {
            }
        }
        System.out.println("[MultiBlockBehavior] Detected facing: " + facing);

        BlockPos coreOffset = schema.getCoreOffset();
        System.out.println("[MultiBlockBehavior] Core offset in schema: " + coreOffset);
        System.out.println("[MultiBlockBehavior] Schema has " + schema.getParts().size() + " parts");

        // 1. Verify all parts match the schema
        for (Map.Entry<BlockPos, java.util.function.Predicate<BlockState>> entry : schema.getParts().entrySet()) {
            BlockPos partSchemaPos = entry.getKey();
            BlockPos relativePos = partSchemaPos.subtract(coreOffset);
            BlockPos rotatedRelative = rotate(relativePos, facing);
            BlockPos partPos = corePos.offset(rotatedRelative);

            System.out.println("[MultiBlockBehavior] Checking schema pos " + partSchemaPos +
                    " -> relative " + relativePos + " -> rotated " + rotatedRelative + " -> world " + partPos);

            if (partPos.equals(corePos)) {
                System.out.println("[MultiBlockBehavior]   Skipping core position");
                continue;
            }

            BlockState state = level.getBlockState(partPos);
            boolean matches = entry.getValue().test(state);
            System.out.println(
                    "[MultiBlockBehavior]   Block at " + partPos + ": " + state.getBlock() + " matches: " + matches);

            if (!matches) {
                System.out.println("[MultiBlockBehavior] Schema validation FAILED at " + partPos);
                return false; // Schema validation failed
            }
        }

        System.out.println("[MultiBlockBehavior] Schema validation PASSED!");

        // 2. Mark core as formed
        ImmutableBlockState coreState = currentState.with(MULTIBLOCK_ROLE, MultiBlockRole.CORE);
        level.setBlock(corePos, (BlockState) coreState.customBlockState().literalObject(), 3);
        System.out.println("[MultiBlockBehavior] Updated core BlockState to role=CORE");

        // 4. Get part block state - use partBlockId if defined, otherwise use the same
        // block as core
        Optional<CustomBlock> partBlockOpt = BukkitBlockManager.instance().blockById(Key.from(partBlockId));
        CustomBlock partBlock;
        if (partBlockOpt.isEmpty()) {
            System.out.println("[MultiBlockBehavior] Part block not found for id: " + partBlockId
                    + ", using customBlock as fallback");
            partBlock = this.block();
        } else {
            partBlock = partBlockOpt.get();
        }
        Object nmsStateObject = partBlock.defaultState().customBlockState().literalObject();
        BukkitWorld world = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(level));

        // 5. Replace all schema blocks with part blocks
        for (Map.Entry<BlockPos, java.util.function.Predicate<BlockState>> entry : schema.getParts().entrySet()) {
            BlockPos partSchemaPos = entry.getKey();
            BlockPos relativePos = partSchemaPos.subtract(coreOffset);
            BlockPos rotatedRelative = rotate(relativePos, facing);
            BlockPos partPos = corePos.offset(rotatedRelative);
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
                        // Pass unrotated relative pos to provider (Schema Space)
                        IOConfiguration ioConfig = ioProvider.configurePartIO(relativePos);

                        // Wrap in RotatedIOConfiguration to handle world directions
                        if (ioConfig != null) {
                            partEntity.setIOConfiguration(ioConfig);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Rollback on error
                System.out.println("[MultiBlockBehavior] FAILED during part replacement: " + e.getMessage());
                return false;
            }
        }

        // 6. Call onFormMachine hook
        System.out.println("[MultiBlockBehavior] Multiblock FORMED successfully!");
        onFormMachine(level, corePos);

        // 7. Register disassembly hooks
        BlockEntity coreBe = BukkitBlockEntityTypes.getIfLoaded(level, corePos);
        if (coreBe instanceof PersistentBlockEntity pbe) {
            // Ensure flag is clear
            pbe.remove(KEY_DISASSEMBLING);
            registerDisassemblyHooks(level, corePos, coreBe);
        }

        return true;
    }

    /**
     * Hook called when machine multiblock is formed
     */
    protected void onFormMachine(Level level, BlockPos pos) {
        // Override in subclasses if needed
    }

    /**
     * Hook called when multiblock is formed
     */
    protected void onForm(Level level, BlockPos pos, MultiBlockPartBlockEntity core) {
        // Override in subclasses if needed
    }

    // ========== Disassembly Logic ==========

    protected void disassemble(Level level, BlockPos corePos, BlockEntity coreEntity) {
        disassemble(level, corePos, coreEntity, null);
    }

    protected void disassemble(Level level, BlockPos corePos, BlockEntity coreEntity, BlockState coreState) {
        System.out.println("[MultiBlockBehavior] disassemble START at " + corePos);
        if (coreEntity != null) {
            System.out.println("[MultiBlockBehavior]   Core entity type: " + coreEntity.getClass().getName());
        } else {
            System.out.println("[MultiBlockBehavior]   Core entity is NULL");
        }

        // Determine Rotation
        if (coreState == null) {
            coreState = level.getBlockState(corePos);
        }

        ImmutableBlockState coreCustomState = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                .getOptionalCustomBlockState(coreState).orElse(null);

        Direction facing = Direction.NORTH;
        if (coreCustomState != null) {
            System.out.println("[MultiBlockBehavior]   Core custom state found, checking role and facing...");
            // Reset core role in BlockState if it's still being managed by the behavior
            // (Only if we're not in the middle of a removal of this specific block)
            if (coreCustomState.getNullable(MULTIBLOCK_ROLE) != null
                    && coreCustomState.get(MULTIBLOCK_ROLE) == MultiBlockRole.CORE) {
                System.out.println("[MultiBlockBehavior]   Resetting core role in BlockState");
                try {
                    ImmutableBlockState newState = coreCustomState.with(MULTIBLOCK_ROLE, MultiBlockRole.NONE);
                    level.setBlock(corePos, (BlockState) newState.customBlockState().literalObject(), 3);
                } catch (Exception ignored) {
                }
            }

            // Get facing for rotation
            if (verticalDirectionProperty != null && coreCustomState.get(verticalDirectionProperty) != null) {
                try {
                    facing = Direction.valueOf(coreCustomState.get(verticalDirectionProperty).toString());
                    System.out.println("[MultiBlockBehavior]   Detected Vertical Facing: " + facing);
                } catch (Exception ignored) {
                }
            } else if (horizontalDirectionProperty != null
                    && coreCustomState.get(horizontalDirectionProperty) != null) {
                try {
                    facing = DirectionalIOHelper
                            .fromHorizontalDirection(coreCustomState.get(horizontalDirectionProperty));
                    System.out.println("[MultiBlockBehavior]   Detected Horizontal Facing: " + facing);
                } catch (Exception ignored) {
                }
            }
        } else {
            System.out.println("[MultiBlockBehavior]   Core custom state is NULL, using NORTH facing.");
        }
        System.out.println("[MultiBlockBehavior]   Using disassembled facing: " + facing);

        BlockPos coreOffset = schema.getCoreOffset();

        // Restore all parts
        System.out
                .println("[MultiBlockBehavior]   Starting part restoration for " + schema.getParts().size() + " parts");
        for (Map.Entry<BlockPos, java.util.function.Predicate<BlockState>> entry : schema.getParts().entrySet()) {
            BlockPos partSchemaPos = entry.getKey();
            BlockPos relativePos = partSchemaPos.subtract(coreOffset);
            BlockPos rotatedRelative = rotate(relativePos, facing);
            BlockPos partPos = corePos.offset(rotatedRelative);

            if (partPos.equals(corePos)) {
                // We'll handle core restoration at the end
                continue;
            }

            BlockEntity partBe = BukkitBlockEntityTypes.getIfLoaded(level, partPos);

            if (partBe instanceof MultiBlockPartBlockEntity partEntity) {
                BlockState originalState = partEntity.getOriginalBlock();
                if (originalState != null) {
                    System.out
                            .println("[MultiBlockBehavior]   Restoring " + partPos + " to " + originalState.getBlock());
                    level.setBlock(partPos, originalState, 3);
                } else {
                    System.out.println("[MultiBlockBehavior]   Restoring " + partPos
                            + " to AIR (null original state in " + partBe.getClass().getSimpleName() + ")");
                    level.setBlock(partPos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
                }
            } else if (partBe != null) {
                System.out.println("[MultiBlockBehavior]   Part at " + partPos
                        + " is NOT a MultiBlockPartBlockEntity but " + partBe.getClass().getName());
            } else {
                System.out.println("[MultiBlockBehavior]   Part at " + partPos + " is NULL (Not loaded?)");
            }
        }

        // Restore Core block itself at the end if it was a part entity
        if (coreEntity instanceof MultiBlockPartBlockEntity partEntity) {
            BlockState originalState = partEntity.getOriginalBlock();
            if (originalState != null) {
                System.out.println(
                        "[MultiBlockBehavior]   Restoring CORE " + corePos + " to " + originalState.getBlock());
                level.setBlock(corePos, originalState, 3);
            }
        }

        onDisassemble(level, corePos, coreEntity);
    }

    protected void dropInventory(Level level, BlockPos pos, net.minecraft.world.WorldlyContainer container) {
        if (container == null || container.isEmpty())
            return;

        System.out.println(
                "[MultiBlockBehavior]   Dropping inventory for container with size " + container.getContainerSize());

        for (int i = 0; i < container.getContainerSize(); i++) {
            net.minecraft.world.item.ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                System.out.println("[MultiBlockBehavior]     Dropping " + stack.getCount() + "x " + stack.getItem()
                        + " from slot " + i);
                net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }

        container.clearContent();
    }

    /**
     * Hook called when multiblock is disassembled
     */
    protected void onDisassemble(Level level, BlockPos pos, BlockEntity core) {
        // Override in subclasses if needed
    }

    // Disposal logic is now managed via PersistentBlockEntity hooks (preCleanup)
    // to ensure block entity data is accessible during disassembly.

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

    public PersistentBlockEntity getBlockEntity(Level world, net.minecraft.core.BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(world, pos);
        if (be instanceof PersistentBlockEntity)
            return (PersistentBlockEntity) be;
        return null;
    }

    @Override
    public Object getContainer(Object thisBlock, Object[] args) {
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];

        BlockEntity eBlockEntity = getBlockEntity(level, pos);
        if (eBlockEntity == null)
            return null;
        if (eBlockEntity instanceof MultiBlockPartBlockEntity core) {
            return core;
        }
        if (eBlockEntity instanceof MultiBlockMachineBlockEntity core) {
            return core;
        }
        return null;
    }
}
