package dev.arubik.craftengine.fluid;

public class FluidStack {
    private FluidType type;
    private int amount; // en milibuckets
    private int pressure; // fuerza disponible

    public FluidStack(FluidType type, int amount, int pressure) {
        this.type = type;
        this.amount = amount;
        this.pressure = pressure;
    }

    public boolean isEmpty() {
        return type.isEmpty() || amount <= 0;
    }

    public int getAmount() { return amount; }
    public FluidType getType() { return type; }
    public int getPressure() { return pressure; }

    public void addAmount(int mb) { this.amount += mb; }
    public void removeAmount(int mb) { this.amount = Math.max(0, this.amount - mb); }

    public FluidStack increasePressure(int boost) {
        return new FluidStack(type, amount, pressure + boost);
    }

    public FluidStack decreasePressure() {
        return new FluidStack(type, amount, Math.max(0, pressure - 1));
    }

    public FluidStack copy() {
        return new FluidStack(type, amount, pressure);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + amount;
        result = 31 * result + pressure;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FluidStack)) return false;

        FluidStack other = (FluidStack) obj;
        return this.type == other.type && this.amount == other.amount && this.pressure == other.pressure;
    }

    public boolean equalsIgnorePressure(FluidStack other) {
        if (other == null) return false;
        return this.type == other.type && this.amount == other.amount;
    }

    public final static FluidStack EMPTY = new FluidStack(FluidType.EMPTY, 0, 0);
    public final static FluidStack WATER_1000MB = new FluidStack(FluidType.WATER, 1000, 0);
    public final static FluidStack LAVA_1000MB = new FluidStack(FluidType.LAVA, 1000, 0);
}
