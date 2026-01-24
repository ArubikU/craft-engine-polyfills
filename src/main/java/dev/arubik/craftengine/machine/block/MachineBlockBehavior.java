package dev.arubik.craftengine.machine.block;

import java.util.ArrayList;
import java.util.Map;
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
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlockBehavior;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import dev.arubik.craftengine.gas.GasCarrier;
import dev.arubik.craftengine.gas.GasStack;

public class MachineBlockBehavior extends ConnectableBlockBehavior
        implements FluidCarrier, GasCarrier, EntityBlockBehavior {

    public static final Factory FACTORY = new Factory();

    public MachineBlockBehavior(CustomBlock block) {
        super(block, new ArrayList<>(), null, null, new IOConfiguration.Open());
    }

    public MachineBlockBehavior(CustomBlock block, java.util.List<net.minecraft.core.Direction> connectableFaces,
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.HorizontalDirection> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            IOConfiguration ioConfig) {
        super(block, connectableFaces, horizontalDirectionProperty, verticalDirectionProperty, ioConfig);
    }

    // Default to ABSTRACT_MACHINE since AbstractMachineBlockEntity is the base for
    // this behavior
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityType<T> blockEntityType(ImmutableBlockState state) {
        return (BlockEntityType<T>) BukkitBlockEntityTypes.ABSTRACT_MACHINE;
    }

    @Override
    public IOConfiguration getIOConfiguration(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be instanceof AbstractMachineBlockEntity machine) {
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
        if (be instanceof AbstractMachineBlockEntity machine) {
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
        if (be instanceof AbstractMachineBlockEntity machine) {
            return machine.extractFluid(level, max, drained, side, slot);
        }
        return 0;
    }

    @Override
    public FluidStack getStored(Level level, BlockPos pos) {
        return FluidStack.EMPTY;
    }

    @Override
    public dev.arubik.craftengine.util.TransferAccessMode getAccessMode() {
        return dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE;
    }

    // ========== Gas Carrier Implementation ==========

    @Override
    public GasStack getStoredGas(Level level, BlockPos pos) {
        return GasStack.EMPTY;
    }

    @Override
    public int insertGas(Level level, BlockPos pos, GasStack stack, net.minecraft.core.Direction side) {
        return insertGas(level, pos, stack, side, -1);
    }

    @Override
    public int insertGas(Level level, BlockPos pos, GasStack stack, net.minecraft.core.Direction side, int slot) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be instanceof AbstractMachineBlockEntity machine) {
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
        if (be instanceof AbstractMachineBlockEntity machine) {
            return machine.extractGas(level, max, drained, side, slot);
        }
        return 0;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> createSyncBlockEntityTicker(CEWorld level,
            ImmutableBlockState state, BlockEntityType<T> blockEntityType) {
        BlockEntityTicker<T> ticker = new BlockEntityTicker<T>() {
            @Override
            public void tick(CEWorld arg0, net.momirealms.craftengine.core.world.BlockPos arg1,
                    ImmutableBlockState arg2, T arg3) {
                BlockEntity be = BukkitBlockEntityTypes.getIfLoaded((Level) arg0.world.serverWorld(),
                        Utils.fromPos(arg1));
                if (be instanceof AbstractMachineBlockEntity machine) {
                    machine.tick((Level) arg0.world.serverWorld(), Utils.fromPos(arg1), arg2);
                }
            }
        };
        return ticker;
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
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
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.HorizontalDirection> hProp = null;
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction> vProp = null;

            if (hPropName != null) {
                try {
                    hProp = (net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.HorizontalDirection>) block
                            .getProperty(hPropName);
                } catch (Exception ignored) {
                }
            }
            if (vPropName != null) {
                try {
                    vProp = (net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
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
    public @Nullable BlockEntity createBlockEntity(net.momirealms.craftengine.core.world.BlockPos arg0,
            ImmutableBlockState arg1) {
        return null;
    }

    public void affectNeighborsAfterRemoval(Object thisBlock, Object[] args,
            java.util.concurrent.Callable<Object> superMethod)
            throws Exception {
        superMethod.call();
    }
}
