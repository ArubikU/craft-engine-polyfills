package dev.arubik.craftengine.machine.block;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.behavior.FluidCarrier;
import dev.arubik.craftengine.util.TransferAccessMode;
import dev.arubik.craftengine.util.Utils;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import dev.arubik.craftengine.gas.GasCarrier;
import dev.arubik.craftengine.gas.GasStack;
import net.minecraft.core.Direction;
import net.momirealms.craftengine.core.block.property.EnumProperty;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import java.util.List;

public class MachineBlockBehavior extends ConnectableBlockBehavior
        implements FluidCarrier, GasCarrier, EntityBlock,
        net.momirealms.craftengine.core.block.behavior.WorldlyContainerHolder {

    public static final Factory FACTORY = new Factory();

    /**
     * Hopper/funnel bridge: expose the machine block entity (already an NMS
     * {@link net.minecraft.world.WorldlyContainer}) to vanilla {@code getContainerAt}, so
     * funnels/hoppers/comparators see the machine — face-filtering is handled by the BE's
     * {@code getSlotsForFace}/{@code canPlace/TakeItemThroughFace}. args[1]=Level, args[2]=BlockPos.
     */
    @Override
    public Object getContainer(Object thisBlock, Object[] args) {
        try {
            net.minecraft.world.level.Level level = (net.minecraft.world.level.Level) args[1];
            BlockPos pos = (BlockPos) args[2];
            net.momirealms.craftengine.core.block.entity.BlockEntity be =
                    dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level, pos);
            if (dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity.DEBUG_IO)
                System.out.println("[MachineIO] getContainer @" + pos.toShortString() + " be="
                        + (be == null ? "null" : be.controller == null ? "no-controller"
                                : be.controller.getClass().getSimpleName()));
            if (be != null && be.controller instanceof net.minecraft.world.Container c)
                return c;
        } catch (Throwable ignored) {
        }
        return null;
    }

    // Optional MACHINE_MODE property (if block has it)
    public final net.momirealms.craftengine.core.block.property.Property<dev.arubik.craftengine.multiblock.MachineMode> MACHINE_MODE;

    public MachineBlockBehavior(BlockDefinition block) {
        super(block, new ArrayList<>(), null, null, new IOConfiguration.Open());
        this.MACHINE_MODE = null; // Default for this constructor
    }

    public MachineBlockBehavior(BlockDefinition customBlock, List<Direction> connectableFaces,
            EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            IOConfiguration ioConfig) {
        super(customBlock, connectableFaces, horizontalDirectionProperty, verticalDirectionProperty, ioConfig);

        // Try to get MACHINE_MODE property from block if it exists
        net.momirealms.craftengine.core.block.property.Property<dev.arubik.craftengine.multiblock.MachineMode> machineModeProp = null;
        try {
            machineModeProp = (net.momirealms.craftengine.core.block.property.Property<dev.arubik.craftengine.multiblock.MachineMode>) customBlock
                    .getProperty(dev.arubik.craftengine.property.Properties.MACHINE_MODE.value());
        } catch (ClassCastException | NullPointerException ignored) {
            // Property not found or wrong type, keep as null
        }
        this.MACHINE_MODE = machineModeProp;
    }

    // --- EntityBlock (controller composition model) ---
    protected int controllerId;

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    /**
     * Base machine behavior backs an abstract entity, so it creates no controller.
     * Concrete machine behaviors (e.g. TestMachineBehavior) override this.
     */
    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return null;
    }

    // Right-click opens the machine's menu (the base machine had no use hook, so
    // sample machines like the upgradeable furnace did nothing on interact).
    @Override
    public net.momirealms.craftengine.core.entity.player.InteractionResult useWithoutItem(
            net.momirealms.craftengine.core.world.context.UseOnContext context, ImmutableBlockState state) {
        try {
            net.minecraft.server.level.ServerLevel level = ((org.bukkit.craftbukkit.CraftWorld) ((net.momirealms.craftengine.bukkit.world.BukkitWorld) context
                    .getLevel()).platformWorld()).getHandle();
            BlockPos pos = (BlockPos) net.momirealms.craftengine.bukkit.util.LocationUtils
                    .toBlockPos(context.getClickedPos());
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
            if (be != null && be.controller instanceof AbstractMachineBlockEntity machine
                    && context.getPlayer() instanceof net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer cePlayer
                    && cePlayer.platformPlayer() instanceof org.bukkit.entity.Player bukkit) {
                machine.getMenu().open(bukkit);
                return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
            }
        } catch (Throwable ignored) {
        }
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }

    @Override
    public IOConfiguration getIOConfiguration(Level level, BlockPos pos) {
        // Called with a null level at config-parse time (no world yet) to fetch the
        // default config — don't touch the world then.
        if (level == null) {
            return defaultIOConfig;
        }
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && be.controller instanceof AbstractMachineBlockEntity machine) {
            IOConfiguration config = machine.getIOConfiguration();
            if (config != null)
                return config;
        }
        return defaultIOConfig;
    }

    @Override
    public int insertFluid(Level level, BlockPos pos, FluidStack stack, net.minecraft.core.Direction side) {
        return insertFluid(level, pos, stack, side, -1);
    }

    @Override
    public int insertFluid(Level level, BlockPos pos, FluidStack stack, net.minecraft.core.Direction side, int slot) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && be.controller instanceof AbstractMachineBlockEntity machine) {
            return machine.insertFluid(level, stack, side, slot);
        }
        return 0;
    }

    @Override
    public int extractFluid(Level level, BlockPos pos, int max, Consumer<FluidStack> drained,
            net.minecraft.core.Direction side) {
        return extractFluid(level, pos, max, drained, side, -1);
    }

    @Override
    public int extractFluid(Level level, BlockPos pos, int max, Consumer<FluidStack> drained,
            net.minecraft.core.Direction side, int slot) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && be.controller instanceof AbstractMachineBlockEntity machine) {
            return machine.extractFluid(level, max, drained, side, slot);
        }
        return 0;
    }

    @Override
    public FluidStack getStored(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && be.controller instanceof AbstractMachineBlockEntity machine)
            return machine.getStoredFluidForCarrier();
        return FluidStack.EMPTY;
    }

    @Override
    public long getCapacity(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && be.controller instanceof AbstractMachineBlockEntity machine)
            return machine.getFluidCapacityForCarrier();
        return 0L;
    }

    @Override
    public void setStoredRaw(Level level, BlockPos pos, FluidStack stack) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && be.controller instanceof AbstractMachineBlockEntity machine)
            machine.setStoredFluidRaw(level, stack);
    }

    @Override
    public dev.arubik.craftengine.util.TransferAccessMode getAccessMode() {
        return dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE;
    }

    // ========== Gas Carrier Implementation ==========

    @Override
    public GasStack getStoredGas(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && be.controller instanceof AbstractMachineBlockEntity machine)
            return machine.getStoredGasForCarrier();
        return GasStack.EMPTY;
    }

    @Override
    public int insertGas(Level level, BlockPos pos, GasStack stack, net.minecraft.core.Direction side) {
        return insertGas(level, pos, stack, side, -1);
    }

    @Override
    public int insertGas(Level level, BlockPos pos, GasStack stack, net.minecraft.core.Direction side, int slot) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && be.controller instanceof AbstractMachineBlockEntity machine) {
            return machine.insertGas(level, stack, side, slot);
        }
        return 0;
    }

    @Override
    public int extractGas(Level level, BlockPos pos, int max, Consumer<GasStack> drained,
            net.minecraft.core.Direction side) {
        return extractGas(level, pos, max, drained, side, -1);
    }

    @Override
    public int extractGas(Level level, BlockPos pos, int max, Consumer<GasStack> drained,
            net.minecraft.core.Direction side, int slot) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && be.controller instanceof AbstractMachineBlockEntity machine) {
            return machine.extractGas(level, max, drained, side, slot);
        }
        return 0;
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            java.util.List<net.minecraft.core.Direction> faces = new java.util.ArrayList<>();
            Object facesArg = arguments.getOrDefault("faces", "all");
            if (facesArg instanceof String faceStr) {
                switch (faceStr.toLowerCase()) {
                    case "all" -> {
                        for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values())
                            faces.add(d);
                    }
                    case "horizontal" -> {
                        faces.add(net.minecraft.core.Direction.NORTH);
                        faces.add(net.minecraft.core.Direction.SOUTH);
                        faces.add(net.minecraft.core.Direction.EAST);
                        faces.add(net.minecraft.core.Direction.WEST);
                    }
                    case "vertical" -> {
                        faces.add(net.minecraft.core.Direction.UP);
                        faces.add(net.minecraft.core.Direction.DOWN);
                    }
                    default -> {
                        try {
                            faces.add(net.minecraft.core.Direction.valueOf(faceStr.toUpperCase()));
                        } catch (Exception ignored) {
                        }
                    }
                }
            } else if (facesArg instanceof java.util.List<?> faceList) {
                for (Object face : faceList) {
                    try {
                        faces.add(net.minecraft.core.Direction.valueOf(face.toString().toUpperCase()));
                    } catch (Exception ignored) {
                    }
                }
            }
            if (faces.isEmpty()) {
                for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values())
                    faces.add(d);
            }

            String hPropName = (String) arguments.get("horizontal-direction-property");
            String vPropName = (String) arguments.get("vertical-direction-property");
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> hProp = null;
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> vProp = null;

            if (hPropName != null) {
                try {
                    hProp = (net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                            .getProperty(hPropName);
                } catch (Exception ignored) {
                }
            }
            if (vPropName != null) {
                try {
                    vProp = (net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                            .getProperty(vPropName);
                } catch (Exception ignored) {
                }
            }

            IOConfiguration ioConfig = new IOConfiguration.Open();
            String ioType = (String) arguments.get("io");
            if ("closed".equalsIgnoreCase(ioType))
                ioConfig = new IOConfiguration.Closed();

            return new MachineBlockBehavior(block, faces, hProp, vProp, ioConfig);
        }
    }

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Object[] args) {
        super.affectNeighborsAfterRemoval(thisBlock, args);
        // Drop the machine's stored items when the block is broken. args[1]=Level, args[2]=BlockPos.
        try {
            Level level = (Level) args[1];
            BlockPos nmsPos = (BlockPos) args[2];
            net.momirealms.craftengine.core.block.entity.BlockEntity be =
                    dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level, nmsPos);
            if (be != null && be.controller instanceof AbstractMachineBlockEntity m) {
                m.dropAllContents(level, nmsPos);
            }
        } catch (Throwable ignored) {
        }
    }
}
