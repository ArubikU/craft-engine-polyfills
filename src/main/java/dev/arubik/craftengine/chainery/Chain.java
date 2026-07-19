package dev.arubik.craftengine.chainery;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.core.BlockPos;

/**
 * A single placed chain (CHAINERY): two endpoint block-entity cells joined by a physically/aesthetically
 * rendered span, addressed by a stable {@link #id}. Both endpoints are chain blocks in the world; whether
 * an endpoint is static décor or a moving contraption cell is resolved at physics time from its live
 * position, so this record stays a plain span descriptor. Held in {@link ChainRegistry} and persisted with
 * it, so a chain survives restart and rides through contraption capture/restore with its endpoint blocks.
 */
public final class Chain {

    /** Stable identity — written into both endpoint block entities and used as the registry key. */
    public final UUID id;
    /** Bukkit world UID both endpoints live in (a chain never spans two worlds). */
    public final UUID worldId;
    /** The two endpoint chain-block positions. */
    public final BlockPos a;
    public final BlockPos b;
    /**
     * The face each endpoint's anchor is stuck to (the direction from the anchor's centre toward the surface
     * it hangs off), so the chain attaches at that FACE, not the anchor's centre — a half-block offset applied
     * at resolve time (and rotated with the contraption for a captured anchor). Null = centre (legacy/no face).
     */
    public final net.minecraft.core.Direction faceA;
    public final net.minecraft.core.Direction faceB;

    public final ChainMaterial material;
    /**
     * The chain's natural length in links (= items consumed). Starts at the taut span; right-clicking an
     * endpoint with more chain items EXTENDS it (adds slack, so the rope sags instead of pulling taut). The
     * rope pulls only once the endpoints are farther apart than this. Mutable — persisted on save.
     */
    public int blocks;

    /**
     * Live PACKET-ONLY link displays (fake block_display entities, one per rope segment) — NOT persisted,
     * rebuilt on demand. Packet-based (no real Bukkit entities) so the render shares the same lightweight
     * fake-entity path the phys contraption swarm uses, and renders the link's real BLOCK model. See
     * {@code ChainRenderer}.
     */
    public final transient List<ChainBlockDisplay> links = new ArrayList<>();

    /** Live verlet rope for this span's sag/ground physics + render — NOT persisted, rebuilt on demand. */
    public final transient ChainRope rope = new ChainRope();

    public Chain(UUID id, UUID worldId, BlockPos a, BlockPos b, net.minecraft.core.Direction faceA,
            net.minecraft.core.Direction faceB, ChainMaterial material, int blocks) {
        this.id = id;
        this.worldId = worldId;
        this.a = a;
        this.b = b;
        this.faceA = faceA;
        this.faceB = faceB;
        this.material = material;
        this.blocks = blocks;
    }

    /** Straight-line rest length of the chain in blocks (the taut distance between endpoint centres). */
    public double restLength() {
        return Math.sqrt(a.distSqr(b));
    }
}
