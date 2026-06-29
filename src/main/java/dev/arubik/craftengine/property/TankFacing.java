package dev.arubik.craftengine.property;

/**
 * Which HORIZONTAL sides of a {@code fluid_block_tank} member are EXTERIOR (no same-group neighbour). The
 * model renders walls/windows only on these sides; interior sides are culled so a multiblock is hollow.
 * Mask keys are built in n,e,s,w order. Only masks that occur in a square w×w prism exist (center=none,
 * edge-mids=1 side, corners=2 adjacent sides, isolated 1×1=nesw), plus SOLID for the hammer "window off"
 * state (opaque tank, no glass).
 */
public enum TankFacing {

    NONE("none"),
    N("n"), E("e"), S("s"), W("w"),
    NE("ne"), NW("nw"), ES("es"), SW("sw"),
    NESW("nesw"),
    SOLID("solid"); // hammer-disabled window: opaque walls

    private final String name;

    TankFacing(final String name) {
        this.name = name;
    }

    public String getSerializedName() {
        return this.name;
    }

    /** Build from which sides are exterior (n/e/s/w), key in n,e,s,w order. Non-square masks -> NESW. */
    public static TankFacing of(boolean n, boolean e, boolean s, boolean w) {
        StringBuilder sb = new StringBuilder();
        if (n)
            sb.append('n');
        if (e)
            sb.append('e');
        if (s)
            sb.append('s');
        if (w)
            sb.append('w');
        String k = sb.length() == 0 ? "none" : sb.toString();
        for (TankFacing f : values())
            if (f.name.equals(k))
                return f;
        return NESW; // any non-square combination (shouldn't occur) falls back to the full window
    }
}
