/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.property;

public enum TankShape {
    PLAIN("plain"),
    WINDOW("window"),
    WINDOW_NE("window_ne"),
    WINDOW_NW("window_nw"),
    WINDOW_SE("window_se"),
    WINDOW_SW("window_sw");

    private final String name;

    private TankShape(String name) {
        this.name = name;
    }

    public String getSerializedName() {
        return this.name;
    }
}

