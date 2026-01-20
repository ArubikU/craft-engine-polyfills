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
        super(block, new ArrayList<>(), getHorizontalProp(block), getVerticalProp(block), new IOConfiguration.Open());
    }

    // Default to ABSTRACT_MACHINE since AbstractMachineBlockEntity is the base for
    // this behavior
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityType<T> blockEntityType(ImmutableBlockState state) {
        return (BlockEntityType<T>) BukkitBlockEntityTypes.ABSTRACT_MACHINE;
    }

    @SuppressWarnings("unchecked")
    private static net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.HorizontalDirection> getHorizontalProp(
            CustomBlock block) {
        try {
            var prop = block.getProperty("facing");
            if (prop instanceof net.momirealms.craftengine.core.block.properties.EnumProperty<?> ep
                    && ep.valueClass() == net.momirealms.craftengine.core.util.HorizontalDirection.class) {
                return (net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.HorizontalDirection>) prop;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction> getVerticalProp(
            CustomBlock block) {
        try {
            var prop = block.getProperty("facing");
            if (prop instanceof net.momirealms.craftengine.core.block.properties.EnumProperty<?> ep
                    && ep.valueClass() == net.momirealms.craftengine.core.util.Direction.class) {
                return (net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction>) prop;
            }
        } catch (Exception ignored) {
        }
        return null;
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
            return new MachineBlockBehavior(block);
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(net.momirealms.craftengine.core.world.BlockPos arg0,
            ImmutableBlockState arg1) {
        return null;
    }
}
