package dev.arubik.craftengine.property;

public enum ConnectedFace {
    
    NONE("none"),
    CONNECTED("connected");
    private final String name;

    private ConnectedFace(final String name) {
        this.name = name;
    }

    public String getSerializedName() {
        return this.name;
    }
}
