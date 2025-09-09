package dev.arubik.craftengine.property;

public enum AttachFace {
    
    FLOOR("floor"),
    WALL("wall"),
    CEILING("ceiling");

    private final String name;

    private AttachFace(final String name) {
        this.name = name;
    }

    public String getSerializedName() {
        return this.name;
    }
}
