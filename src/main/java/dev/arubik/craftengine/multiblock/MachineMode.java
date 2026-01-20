package dev.arubik.craftengine.multiblock;

import net.minecraft.util.StringRepresentable;

/**
 * Machine mode for multi-schema generic machines
 * Represents which schema/machine type is currently active
 */
public enum MachineMode implements StringRepresentable {
    NONE("none"),
    HIGH_FURNACE("high_furnace"),
    ALLOY_SMELTER("alloy_smelter");

    private final String name;

    MachineMode(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    /**
     * Get property value for BlockState
     */
    public String getPropertyValue() {
        return this.name;
    }

    /**
     * Convert from BlockState property value to enum
     */
    public static MachineMode fromProperty(String value) {
        if (value == null)
            return NONE;

        for (MachineMode mode : values()) {
            if (mode.name.equals(value)) {
                return mode;
            }
        }
        return NONE;
    }
}
