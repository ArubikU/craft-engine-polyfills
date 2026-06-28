package dev.arubik.craftengine.property;

/**
 * Window-frame shape for a {@code fluid_block_tank} member, mirroring Create's fluid_tank {@code shape}
 * blockstate. Plain = no window (interior/solid). Window = a full single-column window. The four corner
 * variants tile together so a w×w footprint forms ONE continuous window per face.
 */
public enum TankShape {

    PLAIN("plain"),
    WINDOW("window"),
    WINDOW_NE("window_ne"),
    WINDOW_NW("window_nw"),
    WINDOW_SE("window_se"),
    WINDOW_SW("window_sw");

    private final String name;

    TankShape(final String name) {
        this.name = name;
    }

    public String getSerializedName() {
        return this.name;
    }
}
