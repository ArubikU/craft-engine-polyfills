package dev.arubik.craftengine.fluid;

import dev.arubik.craftengine.util.CustomDataType;
import dev.arubik.craftengine.util.TypedKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class FluidTank {
    private final String name;
    private final int capacity;
    private final TypedKey<FluidStack> key;

    private final FluidType filter;

    public FluidTank(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.key = TypedKey.of("craftengine", "machine_fluid_" + name, FluidKeys.FLUID_DATA_TYPE);
        this.filter = null;
    }

    public FluidTank(String name, int capacity, TypedKey<FluidStack> key) {
        this.name = name;
        this.capacity = capacity;
        this.key = key;
        this.filter = null;
    }

    public FluidTank(String name, int capacity, FluidType filter) {
        this.name = name;
        this.capacity = capacity;
        this.key = TypedKey.of("craftengine", "machine_fluid_" + name, FluidKeys.FLUID_DATA_TYPE);
        this.filter = filter;
    }

    public FluidTank(String name, int capacity, TypedKey<FluidStack> key, FluidType filter) {
        this.name = name;
        this.capacity = capacity;
        this.key = key;
        this.filter = filter;
    }

    // We need to match the PersistentDataType used in FluidKeys.
    // If FluidKeys uses a custom DataType, we must reuse it.

    public TypedKey<FluidStack> getKey() {
        return key;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFiltered() {
        return filter != null;
    }

    public FluidType getFilter() {
        return filter;
    }

    public boolean allows(FluidType fluid) {
        return filter == null || filter == fluid;
    }

    public boolean allows(FluidStack fluid) {
        return allows(fluid.getType());
    }

    public FluidStack getFluid(Level level, BlockPos pos) {
        return dev.arubik.craftengine.util.CustomBlockData.from(level, pos).getOrDefault(key, FluidStack.EMPTY);
    }

    public int insert(Level level, BlockPos pos, FluidStack stack) {
        if (!allows(stack))
            return 0;
        return FluidCarrierImpl.insertFluid(level, pos, stack, capacity, 0, key);
    }

    public int extract(Level level, BlockPos pos, int amount, java.util.function.Consumer<FluidStack> drained) {
        return FluidCarrierImpl.extractFluid(level, pos, amount, drained, key);
    }

    public void deplete(Level level, BlockPos pos) {
        FluidCarrierImpl.extractFluid(level, pos, Integer.MAX_VALUE, null, key);
    }
}
