package dev.arubik.craftengine.multiblock.impl;

public enum MachineType {
    NONE("none"),
    CHEST("chest"),
    SMELTER("smelter");

    private final String name;

    MachineType(String name) {
        this.name = name;
    }

}
