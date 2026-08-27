/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Container
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.bukkit.util.LocationUtils
 *  net.momirealms.craftengine.bukkit.world.BukkitWorld
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.behavior.BlockBehavior
 *  net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
 *  net.momirealms.craftengine.core.block.behavior.EntityBlock
 *  net.momirealms.craftengine.core.block.behavior.WorldlyContainerHolder
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.property.EnumProperty
 *  net.momirealms.craftengine.core.block.property.IntegerProperty
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.entity.player.InteractionResult
 *  net.momirealms.craftengine.core.plugin.config.ConfigSection
 *  net.momirealms.craftengine.core.util.Direction
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.context.UseOnContext
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.entity.Player
 */
package dev.arubik.craftengine.machine.block;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.behavior.FluidCarrier;
import dev.arubik.craftengine.gas.GasCarrier;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.MachineMode;
import dev.arubik.craftengine.multiblock.MultiBlockPartBlockEntity;
import dev.arubik.craftengine.property.Properties;
import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.util.TransferAccessMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.behavior.WorldlyContainerHolder;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.property.EnumProperty;
import net.momirealms.craftengine.core.block.property.IntegerProperty;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class MachineBlockBehavior
extends ConnectableBlockBehavior
implements FluidCarrier,
GasCarrier,
dev.arubik.craftengine.energy.EnergyCarrier,
EntityBlock,
WorldlyContainerHolder {
    public static final Factory FACTORY = new Factory();
    public final Property<MachineMode> MACHINE_MODE;
    protected int controllerId;

    public Object getContainer(Object thisBlock, Object[] args) {
        try {
            BlockEntityController blockEntityController;
            Level level = (Level)args[1];
            BlockPos pos = (BlockPos)args[2];
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
            if (AbstractMachineBlockEntity.DEBUG_IO) {
                System.out.println("[MachineIO] getContainer @" + pos.toShortString() + " be=" + (be == null ? "null" : (be.controller == null ? "no-controller" : be.controller.getClass().getSimpleName())));
            }
            if (be != null && (blockEntityController = be.controller) instanceof Container) {
                // This is the TRUE single choke point for "what container does this custom block
                // expose" — reached both by our own ItemTransferHelper fallback AND (crucially)
                // CraftEngine's own vanilla-hopper/comparator compatibility hooks, which call this
                // WorldlyContainerHolder capability DIRECTLY and never go through
                // ItemTransferHelper at all. Without checking on_get_container here too, a vanilla
                // hopper sitting on a Portable Storage Interface (or any future on_get_container
                // user) would only ever see the machine's own raw (empty) inventory — exactly what
                // was happening before this check existed.
                if (blockEntityController instanceof AbstractMachineBlockEntity machine) {
                    var override = machine.resolveContainerOverride();
                    if (override.isPresent()) return override.get();
                }
                Container c = (Container)blockEntityController;
                return c;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    public MachineBlockBehavior(BlockDefinition block) {
        super(block, new ArrayList<net.minecraft.core.Direction>(), null, null, new IOConfiguration.Open());
        this.MACHINE_MODE = null;
    }

    public MachineBlockBehavior(BlockDefinition customBlock, List<net.minecraft.core.Direction> connectableFaces, EnumProperty<Direction> horizontalDirectionProperty, EnumProperty<Direction> verticalDirectionProperty, IOConfiguration ioConfig) {
        super(customBlock, connectableFaces, horizontalDirectionProperty, verticalDirectionProperty, ioConfig);
        Property machineModeProp = null;
        try {
            machineModeProp = customBlock.getProperty(Properties.MACHINE_MODE.value());
        }
        catch (ClassCastException | NullPointerException runtimeException) {
            // empty catch block
        }
        this.MACHINE_MODE = machineModeProp;
    }

    public void initControllerId(int id) {
        this.controllerId = id;
    }

    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return null;
    }

    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        try {
            BlockEntityController blockEntityController;
            ServerLevel level = ((CraftWorld)((BukkitWorld)context.getLevel()).platformWorld()).getHandle();
            BlockPos pos = (BlockPos)LocationUtils.toBlockPos((net.momirealms.craftengine.core.world.BlockPos)context.getClickedPos());
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded((Level)level, pos);
            if (be != null && (blockEntityController = be.controller) instanceof AbstractMachineBlockEntity) {
                BukkitServerPlayer cePlayer;
                AbstractMachineBlockEntity machine = (AbstractMachineBlockEntity)blockEntityController;
                net.momirealms.craftengine.core.entity.player.Player cePlayerRaw = context.getPlayer();
                if (cePlayerRaw instanceof BukkitServerPlayer && (cePlayer = (BukkitServerPlayer)cePlayerRaw).platformPlayer() instanceof Player) {
                    DataMachineBlockEntity dm;
                    Player bukkit = (Player) cePlayer.platformPlayer();
                    boolean canOpen = true;
                    if (machine instanceof DataMachineBlockEntity && (dm = (DataMachineBlockEntity)machine).definition() != null) {
                        canOpen = dm.definition().openUi();
                    }
                    if (machine instanceof DataMachineBlockEntity && (dm = (DataMachineBlockEntity)machine).definition() != null && dm.definition().interactScript() != null) {
                        ServerPlayer nmsPlayer = ((CraftPlayer)bukkit).getHandle();
                        dm.runInteractScript(dm.definition().interactScript(), nmsPlayer);
                        return InteractionResult.SUCCESS_AND_CANCEL;
                    }
                    if (canOpen) {
                        machine.getMenu().open(bukkit);
                        return InteractionResult.SUCCESS_AND_CANCEL;
                    }
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return InteractionResult.PASS;
    }

    public int getSignal(Object thisBlock, Object[] args) {
        if (args == null || args.length < 3) {
            return 0;
        }
        int val = MachineBlockBehavior.readPowerLevel((BlockState)args[0], (BlockGetter)args[1], (BlockPos)args[2]);
        if (val > 0 && DataMachineBlockEntity.SCRIPT_DEBUG) {
            System.out.println("[CEP getSignal] val=" + val + " pos=" + String.valueOf(args[2]));
        }
        return val;
    }

    public int getDirectSignal(Object thisBlock, Object[] args) {
        if (args == null || args.length < 3) {
            return 0;
        }
        return MachineBlockBehavior.readPowerLevel((BlockState)args[0], (BlockGetter)args[1], (BlockPos)args[2]);
    }

    public boolean isSignalSource(Object thisBlock, Object[] args) {
        return true;
    }

    public int getAnalogOutputSignal(Object thisBlock, Object[] args) {
        if (args == null || args.length < 3) {
            return 0;
        }
        return MachineBlockBehavior.readPowerLevel((BlockState)args[0], (BlockGetter)args[1], (BlockPos)args[2]);
    }

    public boolean hasAnalogOutputSignal(Object thisBlock, Object[] args) {
        return true;
    }

    private static int readPowerLevel(BlockState state, BlockGetter world, BlockPos pos) {
        try {
            MultiBlockPartBlockEntity part;
            BlockPos corePos;
            BlockEntityController blockEntityController;
            Level level;
            BlockEntity be;
            if (world instanceof Level && (be = BukkitBlockEntityTypes.getIfLoaded(level = (Level)world, pos)) != null && (blockEntityController = be.controller) instanceof MultiBlockPartBlockEntity && (corePos = (part = (MultiBlockPartBlockEntity)blockEntityController).getCorePos()) != null && !corePos.equals(pos)) {
                state = level.getBlockState(corePos);
            }
        }
        catch (Throwable level) {
            // empty catch block
        }
        int ceVal = MachineBlockBehavior.readFromCEState(state);
        if (ceVal > 0) {
            return ceVal;
        }
        return MachineBlockBehavior.readPowerFromBE(world, pos);
    }

    private static int readFromCEState(BlockState state) {
        try {
            ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
            if (cs == null) {
                return 0;
            }
            Property prop = ((BlockDefinition)cs.owner().value()).getProperty("power_level");
            if (prop instanceof IntegerProperty) {
                IntegerProperty ip = (IntegerProperty)prop;
                Integer val = (Integer)cs.get((Property)ip);
                return val != null ? Math.max(0, Math.min(15, val)) : 0;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return 0;
    }

    private static int readPowerFromBE(BlockGetter world, BlockPos pos) {
        if (!(world instanceof Level)) {
            return 0;
        }
        Level level = (Level)world;
        try {
            BlockEntityController blockEntityController;
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
            if (be != null && (blockEntityController = be.controller) instanceof PersistentBlockEntity) {
                PersistentBlockEntity pbe = (PersistentBlockEntity)blockEntityController;
                return dev.arubik.craftengine.machine.MachineRedstone.output(pbe);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return 0;
    }

    @Override
    public IOConfiguration getIOConfiguration(Level level, BlockPos pos) {
        AbstractMachineBlockEntity machine;
        IOConfiguration config;
        BlockEntityController blockEntityController;
        if (level == null) {
            return this.defaultIOConfig;
        }
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be == null) {
            return this.defaultIOConfig;
        }
        blockEntityController = be.controller;
        if (blockEntityController instanceof AbstractMachineBlockEntity && (config = (machine = (AbstractMachineBlockEntity)blockEntityController).getIOConfiguration()) != null) {
            return config;
        }
        // A celled-multiblock PART (e.g. the energy windmill's 3 mast cells) never gets a real
        // AbstractMachineBlockEntity — it's a MultiBlockPartBlockEntity. Its OWN per-cell config
        // (set from the machine's "cell_io" at placement, see CelledDataMachineBehavior#onPlace)
        // is the actual per-face/per-resource-type answer for THIS cell; falling back to the
        // block's blanket defaultIOConfig here is what let a cable visually connect to (and, via
        // carrierConnectsHere, treat as fair game on) ANY face of ANY mast cell instead of only
        // the cell(s) cell_io actually grants. Absent cell_io entirely (no CelledMachineDefinition
        // registered for this machine), the part's ioConfig stays null and this still falls
        // through to defaultIOConfig — unchanged, permissive behavior for machines that never
        // opted into per-cell rules.
        if (blockEntityController instanceof dev.arubik.craftengine.multiblock.MultiBlockPartBlockEntity part) {
            IOConfiguration partConfig = part.getIOConfiguration();
            if (partConfig != null) {
                return partConfig;
            }
        }
        return this.defaultIOConfig;
    }

    @Override
    public int insertFluid(Level level, BlockPos pos, FluidStack stack, net.minecraft.core.Direction side) {
        return this.insertFluid(level, pos, stack, side, -1);
    }

    @Override
    public int insertFluid(Level level, BlockPos pos, FluidStack stack, net.minecraft.core.Direction side, int slot) {
        BlockEntityController blockEntityController;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (blockEntityController = be.controller) instanceof AbstractMachineBlockEntity) {
            AbstractMachineBlockEntity machine = (AbstractMachineBlockEntity)blockEntityController;
            return machine.insertFluid(level, stack, side, slot);
        }
        return 0;
    }

    @Override
    public int extractFluid(Level level, BlockPos pos, int max, Consumer<FluidStack> drained, net.minecraft.core.Direction side) {
        return this.extractFluid(level, pos, max, drained, side, -1);
    }

    @Override
    public int extractFluid(Level level, BlockPos pos, int max, Consumer<FluidStack> drained, net.minecraft.core.Direction side, int slot) {
        BlockEntityController blockEntityController;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (blockEntityController = be.controller) instanceof AbstractMachineBlockEntity) {
            AbstractMachineBlockEntity machine = (AbstractMachineBlockEntity)blockEntityController;
            return machine.extractFluid(level, max, drained, side, slot);
        }
        return 0;
    }

    @Override
    public FluidStack getStored(Level level, BlockPos pos) {
        BlockEntityController blockEntityController;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (blockEntityController = be.controller) instanceof AbstractMachineBlockEntity) {
            AbstractMachineBlockEntity machine = (AbstractMachineBlockEntity)blockEntityController;
            return machine.getStoredFluidForCarrier();
        }
        return FluidStack.EMPTY;
    }

    @Override
    public long getCapacity(Level level, BlockPos pos) {
        BlockEntityController blockEntityController;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (blockEntityController = be.controller) instanceof AbstractMachineBlockEntity) {
            AbstractMachineBlockEntity machine = (AbstractMachineBlockEntity)blockEntityController;
            return machine.getFluidCapacityForCarrier();
        }
        return 0L;
    }

    @Override
    public void setStoredRaw(Level level, BlockPos pos, FluidStack stack) {
        BlockEntityController blockEntityController;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (blockEntityController = be.controller) instanceof AbstractMachineBlockEntity) {
            AbstractMachineBlockEntity machine = (AbstractMachineBlockEntity)blockEntityController;
            machine.setStoredFluidRaw(level, stack);
        }
    }

    @Override
    public TransferAccessMode getAccessMode() {
        return TransferAccessMode.ANYONE_CAN_TAKE;
    }

    @Override
    public GasStack getStoredGas(Level level, BlockPos pos) {
        BlockEntityController blockEntityController;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (blockEntityController = be.controller) instanceof AbstractMachineBlockEntity) {
            AbstractMachineBlockEntity machine = (AbstractMachineBlockEntity)blockEntityController;
            return machine.getStoredGasForCarrier();
        }
        return GasStack.EMPTY;
    }

    @Override
    public int insertGas(Level level, BlockPos pos, GasStack stack, net.minecraft.core.Direction side) {
        return this.insertGas(level, pos, stack, side, -1);
    }

    @Override
    public int insertGas(Level level, BlockPos pos, GasStack stack, net.minecraft.core.Direction side, int slot) {
        BlockEntityController blockEntityController;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (blockEntityController = be.controller) instanceof AbstractMachineBlockEntity) {
            AbstractMachineBlockEntity machine = (AbstractMachineBlockEntity)blockEntityController;
            return machine.insertGas(level, stack, side, slot);
        }
        return 0;
    }

    @Override
    public int extractGas(Level level, BlockPos pos, int max, Consumer<GasStack> drained, net.minecraft.core.Direction side) {
        return this.extractGas(level, pos, max, drained, side, -1);
    }

    @Override
    public int extractGas(Level level, BlockPos pos, int max, Consumer<GasStack> drained, net.minecraft.core.Direction side, int slot) {
        BlockEntityController blockEntityController;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (blockEntityController = be.controller) instanceof AbstractMachineBlockEntity) {
            AbstractMachineBlockEntity machine = (AbstractMachineBlockEntity)blockEntityController;
            return machine.extractGas(level, max, drained, side, slot);
        }
        return 0;
    }

    @Override
    public long getGasCapacity(Level level, BlockPos pos) {
        BlockEntityController blockEntityController;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (blockEntityController = be.controller) instanceof AbstractMachineBlockEntity) {
            AbstractMachineBlockEntity machine = (AbstractMachineBlockEntity)blockEntityController;
            return machine.getGasCapacityForCarrier();
        }
        return GasCarrier.super.getGasCapacity(level, pos);
    }

    /**
     * A machine only lets gas cross a face its {@code io} block declares. Without these the gas
     * network solver treated every machine as an open conduit and equalized adjacent machines'
     * tanks to 50/50 — see {@link AbstractMachineBlockEntity#allowsGas}.
     */
    @Override
    public boolean canGasOutput(Level level, BlockPos pos, net.minecraft.core.Direction side) {
        BlockEntityController c;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (c = be.controller) instanceof AbstractMachineBlockEntity machine) {
            return machine.allowsGas(level, side, false);
        }
        return false;
    }

    @Override
    public boolean canGasInput(Level level, BlockPos pos, net.minecraft.core.Direction side) {
        BlockEntityController c;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (c = be.controller) instanceof AbstractMachineBlockEntity machine) {
            return machine.allowsGas(level, side, true);
        }
        return false;
    }

    @Override
    public void setStoredGasRaw(Level level, BlockPos pos, GasStack stack) {
        BlockEntityController blockEntityController;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (blockEntityController = be.controller) instanceof AbstractMachineBlockEntity) {
            AbstractMachineBlockEntity machine = (AbstractMachineBlockEntity)blockEntityController;
            machine.setStoredGasRaw(level, stack);
        }
    }

    // ---- CraftEnergy delegation — same shape as the Fluid/Gas blocks above ----

    @Override
    public int getStoredEnergy(Level level, BlockPos pos) {
        BlockEntityController c;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (c = be.controller) instanceof AbstractMachineBlockEntity machine) {
            return machine.getStoredEnergyForCarrier();
        }
        return 0;
    }

    @Override
    public int insertEnergy(Level level, BlockPos pos, int amount, net.minecraft.core.Direction side) {
        BlockEntityController c;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (c = be.controller) instanceof AbstractMachineBlockEntity machine) {
            return machine.insertEnergy(level, amount, side);
        }
        return 0;
    }

    @Override
    public int extractEnergy(Level level, BlockPos pos, int max, net.minecraft.core.Direction side) {
        BlockEntityController c;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (c = be.controller) instanceof AbstractMachineBlockEntity machine) {
            return machine.extractEnergy(level, max, side);
        }
        return 0;
    }

    @Override
    public long getEnergyCapacity(Level level, BlockPos pos) {
        BlockEntityController c;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (c = be.controller) instanceof AbstractMachineBlockEntity machine) {
            return machine.getEnergyCapacityForCarrier();
        }
        return dev.arubik.craftengine.energy.EnergyCarrier.super.getEnergyCapacity(level, pos);
    }

    /** A machine only lets energy cross a face its {@code io} block declares — see
     *  {@link #canGasOutput}/{@link #canGasInput} above for why this matters to the network solver. */
    @Override
    public boolean canEnergyOutput(Level level, BlockPos pos, net.minecraft.core.Direction side) {
        BlockEntityController c;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (c = be.controller) instanceof AbstractMachineBlockEntity machine) {
            return machine.allowsEnergy(level, side, false);
        }
        return false;
    }

    @Override
    public boolean canEnergyInput(Level level, BlockPos pos, net.minecraft.core.Direction side) {
        BlockEntityController c;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (c = be.controller) instanceof AbstractMachineBlockEntity machine) {
            return machine.allowsEnergy(level, side, true);
        }
        return false;
    }

    @Override
    public void setStoredEnergyRaw(Level level, BlockPos pos, int amount) {
        BlockEntityController c;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (c = be.controller) instanceof AbstractMachineBlockEntity machine) {
            machine.setStoredEnergyRaw(level, amount);
        }
    }

    public void affectNeighborsAfterRemoval(Object thisBlock, Object[] args) {
        super.affectNeighborsAfterRemoval(thisBlock, args);
        try {
            BlockEntityController blockEntityController;
            Level level = (Level)args[1];
            BlockPos nmsPos = (BlockPos)args[2];
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, nmsPos);
            if (be != null && (blockEntityController = be.controller) instanceof DataMachineBlockEntity) {
                DataMachineBlockEntity dm = (DataMachineBlockEntity)blockEntityController;
                if (level instanceof ServerLevel) {
                    ServerLevel sl = (ServerLevel)level;
                    dm.despawnSpecDisplays(sl);
                }
                dm.dropAllContents(level, nmsPos);
            } else if (be != null && (blockEntityController = be.controller) instanceof AbstractMachineBlockEntity) {
                AbstractMachineBlockEntity m = (AbstractMachineBlockEntity)blockEntityController;
                m.dropAllContents(level, nmsPos);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public void neighborChanged(Object thisBlock, Object[] args) {
        super.neighborChanged(thisBlock, args);
        try {
            BlockEntityController blockEntityController;
            Level level = (Level)args[1];
            BlockPos pos = (BlockPos)args[2];
            if (level.isClientSide()) {
                return;
            }
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
            if (be != null && (blockEntityController = be.controller) instanceof DataMachineBlockEntity) {
                DataMachineBlockEntity dm = (DataMachineBlockEntity)blockEntityController;
                dm.invalidateRpm(level);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static class Factory
    implements BlockBehaviorFactory<BlockBehavior> {
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            ArrayList<net.minecraft.core.Direction> faces;
            block27: {
                Object facesArg;
                block26: {
                    faces = new ArrayList<net.minecraft.core.Direction>();
                    facesArg = arguments.getOrDefault("faces", "all");
                    if (!(facesArg instanceof String)) break block26;
                    String faceStr = (String) facesArg;
                    switch (faceStr.toLowerCase()) {
                        case "all": {
                            for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values()) {
                                faces.add(d);
                            }
                            break block27;
                        }
                        case "horizontal": {
                            faces.add(net.minecraft.core.Direction.NORTH);
                            faces.add(net.minecraft.core.Direction.SOUTH);
                            faces.add(net.minecraft.core.Direction.EAST);
                            faces.add(net.minecraft.core.Direction.WEST);
                            break;
                        }
                        case "vertical": {
                            faces.add(net.minecraft.core.Direction.UP);
                            faces.add(net.minecraft.core.Direction.DOWN);
                            break;
                        }
                        default: {
                            try {
                                faces.add(net.minecraft.core.Direction.valueOf(faceStr.toUpperCase()));
                                break;
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                        }
                    }
                    break block27;
                }
                if (facesArg instanceof List) {
                    List faceList = (List)facesArg;
                    for (Object face : faceList) {
                        try {
                            faces.add(net.minecraft.core.Direction.valueOf((String)face.toString().toUpperCase()));
                        }
                        catch (Exception exception) {}
                    }
                }
            }
            if (faces.isEmpty()) {
                for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values()) {
                    faces.add(d);
                }
            }
            String hPropName = (String)arguments.get("horizontal-direction-property");
            String vPropName = (String)arguments.get("vertical-direction-property");
            EnumProperty hProp = null;
            EnumProperty vProp = null;
            if (hPropName != null) {
                try {
                    hProp = (EnumProperty)block.getProperty(hPropName);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            if (vPropName != null) {
                try {
                    vProp = (EnumProperty)block.getProperty(vPropName);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            IOConfiguration ioConfig = new IOConfiguration.Open();
            String ioType = (String)arguments.get("io");
            if ("closed".equalsIgnoreCase(ioType)) {
                ioConfig = new IOConfiguration.Closed();
            }
            return new MachineBlockBehavior(block, faces, (EnumProperty<Direction>)hProp, (EnumProperty<Direction>)vProp, ioConfig);
        }
    }
}

