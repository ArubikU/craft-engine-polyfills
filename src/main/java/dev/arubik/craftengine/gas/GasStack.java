/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.gas;

import dev.arubik.craftengine.gas.GasType;

public class GasStack {
    private final GasType type;
    private int amount;
    private int pressure;
    public static final GasStack EMPTY = new GasStack(null, 0, 0);

    public boolean isFull(int maxAmount) {
        return !this.type.isEmpty() && this.amount >= maxAmount;
    }

    public GasStack(GasType type, int amount, int pressure) {
        this.type = type;
        this.amount = amount;
        this.pressure = pressure;
    }

    public GasStack(GasType type, int amount) {
        this(type, amount, 0);
    }

    public GasType getType() {
        return this.type;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void grow(int amount) {
        this.amount += amount;
    }

    public void shrink(int amount) {
        this.amount = Math.max(0, this.amount - amount);
    }

    public boolean isEmpty() {
        return this.type == null || this.amount <= 0;
    }

    public GasStack copy() {
        return new GasStack(this.type, this.amount);
    }

    public boolean isGasEqual(GasStack other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        return this.type == other.type;
    }

    public int getPressure() {
        return this.pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public static GasStack of(GasType steam, int i) {
        return new GasStack(steam, i);
    }
}

