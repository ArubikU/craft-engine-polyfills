/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.property;

public enum TankFacing {
    NONE("none"),
    N("n"),
    E("e"),
    S("s"),
    W("w"),
    NE("ne"),
    NW("nw"),
    ES("es"),
    SW("sw"),
    NEP("nep"),
    NWP("nwp"),
    ESP("esp"),
    SWP("swp"),
    NESW("nesw"),
    SOLID("solid");

    private final String name;

    private TankFacing(String name) {
        this.name = name;
    }

    public String getSerializedName() {
        return this.name;
    }

    public static TankFacing of(boolean n, boolean e, boolean s, boolean w) {
        StringBuilder sb = new StringBuilder();
        if (n) {
            sb.append('n');
        }
        if (e) {
            sb.append('e');
        }
        if (s) {
            sb.append('s');
        }
        if (w) {
            sb.append('w');
        }
        String k = sb.length() == 0 ? "none" : sb.toString();
        for (TankFacing f : TankFacing.values()) {
            if (!f.name.equals(k)) continue;
            return f;
        }
        return NESW;
    }

    public static TankFacing of(boolean n, boolean e, boolean s, boolean w, int width) {
        TankFacing base = TankFacing.of(n, e, s, w);
        if (width < 3) {
            return base;
        }
        switch (base.ordinal()) {
            case 5: {
                return NEP;
            }
            case 6: {
                return NWP;
            }
            case 7: {
                return ESP;
            }
            case 8: {
                return SWP;
            }
        }
        return base;
    }
}

