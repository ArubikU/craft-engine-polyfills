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
    public final ChainMaterial material;
    /**
     * The chain's natural length in links (= items consumed). Starts at the taut span; right-clicking an
     * endpoint with more chain items EXTENDS it (adds slack, so the rope sags instead of pulling taut). The
     * rope pulls only once the endpoints are farther apart than this. Mutable — persisted on save.
     */
    public int blocks;

    /**
     * Live render handles (spawned display-entity UUIDs) — NOT persisted; rebuilt on load. Kept here so
     * {@code ChainRenderer} can reposition or despawn this chain's links without re-deriving them.
     */
    public final transient List<UUID> renderEntities = new ArrayList<>();

    public Chain(UUID id, UUID worldId, BlockPos a, BlockPos b, ChainMaterial material, int blocks) {
        this.id = id;
        this.worldId = worldId;
        this.a = a;
        this.b = b;
        this.material = material;
        this.blocks = blocks;
    }

    /** Straight-line rest length of the chain in blocks (the taut distance between endpoint centres). */
    public double restLength() {
        return Math.sqrt(a.distSqr(b));
    }
}
