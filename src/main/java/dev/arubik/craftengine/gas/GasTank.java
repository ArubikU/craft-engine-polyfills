/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 */
package dev.arubik.craftengine.gas;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.gas.GasCarrierImpl;
import dev.arubik.craftengine.gas.GasKeys;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.util.TypedKey;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class GasTank {
    private final String name;
    private final int capacity;
    private final TypedKey<GasStack> key;
    private final GasType filter;

    public GasTank(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.key = TypedKey.of("craftengine", "machine_gas_" + name, GasKeys.GAS_DATA_TYPE);
        this.filter = null;
    }

    public GasTank(String name, int capacity, GasType filter) {
        this.name = name;
        this.capacity = capacity;
        this.filter = filter;
        this.key = TypedKey.of("craftengine", "machine_gas_" + name, GasKeys.GAS_DATA_TYPE);
    }

    public boolean allows(GasType gas) {
        return this.filter == null || this.filter == gas;
    }

    public boolean allows(GasStack gas) {
        return this.allows(gas.getType());
    }

    public TypedKey<GasStack> getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public GasStack getGas(Level level, BlockPos pos) {
        PersistentBlockEntity be = PersistentBlockEntity.getIfLoaded(level, pos);
        return be != null ? be.getOrDefault(this.key, GasStack.EMPTY) : GasStack.EMPTY;
    }

    public int insert(Level level, BlockPos pos, GasStack stack) {
        if (!this.allows(stack)) {
            return 0;
        }
        return GasCarrierImpl.insertGas(level, pos, stack, this.capacity, this.key);
    }

    public int extract(Level level, BlockPos pos, int amount, Consumer<GasStack> drained) {
        return GasCarrierImpl.extractGas(level, pos, amount, drained, this.key);
    }

    public void deplete(Level level, BlockPos pos) {
        GasCarrierImpl.extractGas(level, pos, Integer.MAX_VALUE, null, this.key);
    }
}

