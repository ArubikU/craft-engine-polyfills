/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.fluid;

import dev.arubik.craftengine.fluid.FluidType;

public class FluidStack {
    private FluidType type;
    private int amount;
    private int pressure;
    public static final FluidStack EMPTY = new FluidStack(FluidType.EMPTY, 0, 0);
    public static final FluidStack WATER_1000MB = new FluidStack(FluidType.WATER, 1000, 0);
    public static final FluidStack LAVA_1000MB = new FluidStack(FluidType.LAVA, 1000, 0);

    public FluidStack(FluidType type, int amount, int pressure) {
        this.type = type;
        this.amount = amount;
        this.pressure = pressure;
    }

    public FluidStack(FluidType type, int amount) {
        this.type = type;
        this.amount = amount;
        this.pressure = 0;
    }

    public static FluidStack of(FluidType type, int amount) {
        return new FluidStack(type, amount);
    }

    public boolean isEmpty() {
        return this.type.isEmpty() || this.amount <= 0;
    }

    public boolean isFull(int maxAmount) {
        return !this.type.isEmpty() && this.amount >= maxAmount;
    }

    public boolean isFluidEqual(FluidStack other) {
        if (other == null) {
            return false;
        }
        return this.type == other.type && this.amount == other.amount;
    }

    public int getAmount() {
        return this.amount;
    }

    public FluidType getType() {
        return this.type;
    }

    public int getPressure() {
        return this.pressure;
    }

    public void addAmount(int mb) {
        this.amount += mb;
    }

    public void removeAmount(int mb) {
        this.amount = Math.max(0, this.amount - mb);
    }

    public FluidStack increasePressure(int boost) {
        return new FluidStack(this.type, this.amount, this.pressure + boost);
    }

    public FluidStack decreasePressure() {
        return new FluidStack(this.type, this.amount, Math.max(0, this.pressure - 1));
    }

    public FluidStack copy() {
        return new FluidStack(this.type, this.amount, this.pressure);
    }

    public int hashCode() {
        int result = this.type != null ? this.type.hashCode() : 0;
        result = 31 * result + this.amount;
        result = 31 * result + this.pressure;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FluidStack)) {
            return false;
        }
        FluidStack other = (FluidStack)obj;
        return this.type == other.type && this.amount == other.amount && this.pressure == other.pressure;
    }

    public boolean equalsIgnorePressure(FluidStack other) {
        if (other == null) {
            return false;
        }
        return this.type == other.type && this.amount == other.amount;
    }
}

