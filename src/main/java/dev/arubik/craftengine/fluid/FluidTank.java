/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 */
package dev.arubik.craftengine.fluid;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.fluid.FluidCarrierImpl;
import dev.arubik.craftengine.fluid.FluidKeys;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.util.TypedKey;
import java.util.function.Consumer;
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

    public TypedKey<FluidStack> getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public boolean isFiltered() {
        return this.filter != null;
    }

    public FluidType getFilter() {
        return this.filter;
    }

    public boolean allows(FluidType fluid) {
        return this.filter == null || this.filter == fluid;
    }

    public boolean allows(FluidStack fluid) {
        return this.allows(fluid.getType());
    }

    public FluidStack getFluid(Level level, BlockPos pos) {
        PersistentBlockEntity be = PersistentBlockEntity.getIfLoaded(level, pos);
        return be != null ? be.getOrDefault(this.key, FluidStack.EMPTY) : FluidStack.EMPTY;
    }

    public int insert(Level level, BlockPos pos, FluidStack stack) {
        if (!this.allows(stack)) {
            return 0;
        }
        return FluidCarrierImpl.insertFluid(level, pos, stack, this.capacity, 0, this.key);
    }

    public int extract(Level level, BlockPos pos, int amount, Consumer<FluidStack> drained) {
        return FluidCarrierImpl.extractFluid(level, pos, amount, drained, this.key);
    }

    public void deplete(Level level, BlockPos pos) {
        FluidCarrierImpl.extractFluid(level, pos, Integer.MAX_VALUE, null, this.key);
    }
}

