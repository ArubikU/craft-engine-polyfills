package dev.arubik.craftengine.fluid.graph;

import net.minecraft.core.BlockPos;

/**
 * A node in the fluid network graph (Phase 1 of the hydraulic-solver rewrite — see
 * {@code TOADD/fluid_rewrite_roadmap.md}). Read-only model for now: built and inspected, not yet
 * driving flow.
 *
 * <p>Mirrors create-pipes-n-physics {@code engine.Node}: a node carries a {@code capacitance}
 * (mB per block of head — tanks store, pipes/pumps ~0) and a {@code head} (fluid surface elevation in
 * blocks, so gravity is just the Y term).</p>
 */
public final class FluidNode {

    public enum Kind {
        PIPE, // pass-through transport (small capacitance)
        TANK, // stores fluid (large capacitance)
        PUMP, // adds head (emf) between its two sides
        HANDLER, // machine / external fluid handler
        OPEN_END // pipe terminating into air / a world fluid (source or sink)
    }

    public final BlockPos pos;
    public final Kind kind;
    /** mB of storage per block of head. Tanks: capacity; junctions/pumps: ~0. */
    public final long capacityMb;
    /** Current fluid surface elevation, in blocks (base Y + fill fraction). */
    public double head;

    public FluidNode(BlockPos pos, Kind kind, long capacityMb, double head) {
        this.pos = pos;
        this.kind = kind;
        this.capacityMb = capacityMb;
        this.head = head;
    }

    @Override
    public String toString() {
        return kind + "@" + pos.toShortString() + "(cap=" + capacityMb + ",head=" + String.format("%.2f", head) + ")";
    }
}
