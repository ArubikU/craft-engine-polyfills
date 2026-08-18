/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.conveyor;

public enum ConveyorSlope {
    FLAT(0),
    UP(1),
    DOWN(-1);

    private final int stepY;

    private ConveyorSlope(int stepY) {
        this.stepY = stepY;
    }

    public int stepY() {
        return this.stepY;
    }

    public static ConveyorSlope fromName(String name) {
        if (name == null) {
            return FLAT;
        }
        switch (name.trim().toUpperCase()) {
            case "UP": {
                return UP;
            }
            case "DOWN": {
                return DOWN;
            }
        }
        return FLAT;
    }
}

