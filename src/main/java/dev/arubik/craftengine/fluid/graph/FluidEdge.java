package dev.arubik.craftengine.fluid.graph;

/**
 * An edge between two {@link FluidNode}s in the fluid network graph (Phase 1, read-only).
 *
 * <p>Mirrors create-pipes-n-physics {@code engine.Edge}: a pipe run carries a {@code conductance}
 * (mB/tick per block of head difference), a {@code crestY} (highest point along the run, for
 * siphon/cavitation gating) and an {@code allowedSign} one-way constraint
 * (+1 = a→b only, -1 = b→a only, 0 = bidirectional).</p>
 *
 * <p>Phase 1 builds one edge per connected adjacent pair (no contraction yet). Edge contraction of
 * 2-connection pass-through pipes is a Phase-1b optimization.</p>
 */
public final class FluidEdge {

    /** Index into {@link FluidGraph#nodes}. */
    public final int a;
    public final int b;
    /** mB/tick per block of head difference. */
    public final double conductance;
    /** Highest Y along the run (the pair's max Y for now). */
    public final int crestY;
    /** Pump boost in blocks of head, driving a→b (0 for plain pipe). */
    public final double emf;
    /** +1 = a→b only, -1 = b→a only, 0 = bidirectional. */
    public final int allowedSign;

    public FluidEdge(int a, int b, double conductance, int crestY, double emf, int allowedSign) {
        this.a = a;
        this.b = b;
        this.conductance = conductance;
        this.crestY = crestY;
        this.emf = emf;
        this.allowedSign = allowedSign;
    }

    @Override
    public String toString() {
        return a + "<->" + b + "(G=" + String.format("%.1f", conductance) + ",crestY=" + crestY
                + (emf != 0 ? ",emf=" + emf : "") + (allowedSign != 0 ? ",sign=" + allowedSign : "") + ")";
    }
}
