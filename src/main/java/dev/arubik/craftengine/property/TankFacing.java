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
    NE("ne"), NW("nw"), ES("es"), SW("sw"), // windowed half-corner (footprint width == 2)
    NEP("nep"), NWP("nwp"), ESP("esp"), SWP("swp"), // plain corner, no window (footprint width >= 3)
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

    /**
     * Width-aware variant: a CORNER cell (exactly two adjacent exterior sides) renders a windowed
     * half-corner only in a 2×2 footprint; in a 3×3+ footprint the corners are PLAIN (no window) and
     * only the edge-mid cells carry the centered window. Everything else is width-independent.
     */
    public static TankFacing of(boolean n, boolean e, boolean s, boolean w, int width) {
        TankFacing base = of(n, e, s, w);
        if (width < 3)
            return base; // 1×1 / 2×2: corners stay windowed
        switch (base) {
            case NE:
                return NEP;
            case NW:
                return NWP;
            case ES:
                return ESP;
            case SW:
                return SWP;
            default:
                return base;
        }
    }
}
