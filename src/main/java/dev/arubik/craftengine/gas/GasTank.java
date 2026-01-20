package dev.arubik.craftengine.gas;

import dev.arubik.craftengine.util.TypedKey;
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
        return filter == null || filter == gas;
    }

    public boolean allows(GasStack gas) {
        return allows(gas.getType());
    }

    public TypedKey<GasStack> getKey() {
        return key;
    }

    public int getCapacity() {
        return capacity;
    }

    public GasStack getGas(Level level, BlockPos pos) {
        return dev.arubik.craftengine.util.CustomBlockData.from(level, pos).getOrDefault(key, GasStack.EMPTY);
    }

    public int insert(Level level, BlockPos pos, GasStack stack) {
        if (!allows(stack))
            return 0;
        return GasCarrierImpl.insertGas(level, pos, stack, capacity, key);
    }

    public int extract(Level level, BlockPos pos, int amount, java.util.function.Consumer<GasStack> drained) {
        return GasCarrierImpl.extractGas(level, pos, amount, drained, key);
    }

    public void deplete(Level level, BlockPos pos) {
        GasCarrierImpl.extractGas(level, pos, Integer.MAX_VALUE, null, key);
    }
}
