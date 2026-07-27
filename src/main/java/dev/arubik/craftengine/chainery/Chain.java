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
    /** The two endpoint chain-block positions. Mutable: a captured endpoint that disassembles elsewhere is
     *  re-anchored to wherever its restored block landed (see {@code ChainRegistry.reanchor}). */
    public BlockPos a;
    public BlockPos b;
    /**
     * The attach offset for each endpoint — the vector from the anchor cell's CENTRE to the exact point the
     * chain hooks onto, computed from the connected block's real collision box (so it hangs off a slab's top,
     * a fence's post, a stair's step, not just a flat cell face). Applied at resolve time and rotated with the
     * contraption for a captured anchor. Null = centre (no offset).
     */
    public org.joml.Vector3d offsetA;
    public org.joml.Vector3d offsetB;

    public final ChainMaterial material;
    /**
     * The chain's natural length in links (= items consumed). Starts at the taut span; right-clicking an
     * endpoint with more chain items EXTENDS it (adds slack, so the rope sags instead of pulling taut). The
     * rope pulls only once the endpoints are farther apart than this. Mutable — persisted on save.
     */
    public int blocks;

    /**
     * If an endpoint has been captured into a contraption, that contraption's id + the anchor's LOCAL cell —
     * persisted, so the chain reconnects to the contraption after a restart even if the block-entity link was
     * lost (esp. a chain joining TWO contraptions). Null = the endpoint is static at {@link #a}/{@link #b}.
     */
    public java.util.UUID contraptionA;
    public java.util.UUID contraptionB;
    public BlockPos localA;
    public BlockPos localB;

    /**
     * Persistent per-chain NBT — arbitrary data a plugin can stash on a chain (zipline config, owner, cooldowns,
     * whatever), persisted with chains.dat and reachable from {@link ChainInteractEvent#getChain()}. Use the raw
     * {@link net.minecraft.nbt.CompoundTag} directly, or the typed convenience getters/setters below.
     */
    public final net.minecraft.nbt.CompoundTag data;

    /**
     * Live PACKET-ONLY link displays (fake block_display entities, one per rope segment) — NOT persisted,
     * rebuilt on demand. Packet-based (no real Bukkit entities) so the render shares the same lightweight
     * fake-entity path the phys contraption swarm uses, and renders the link's real BLOCK model. See
     * {@code ChainRenderer}.
     */
    public final transient List<ChainBlockDisplay> links = new ArrayList<>();

    /** Live PACKET-ONLY interaction hitboxes (one per link) making the chain clickable — see ChainInteraction. */
    public final transient List<ChainInteraction> hitboxes = new ArrayList<>();

    /** Live verlet rope for this span's sag/ground physics + render — NOT persisted, rebuilt on demand. */
    public final transient ChainRope rope = new ChainRope();

    /** Consecutive ticks the chain has been over its break tension — a margin so a transient assembly spike
     *  doesn't snap a taut chain; it only breaks after this stays high (see ChainEngine#applyRope). */
    public transient int overTensionTicks = 0;

    /** Consecutive ticks an endpoint has looked orphaned (anchor gone, not captured) — grace so a captured chain
     *  isn't deleted in the window between restart and its contraption reloading (see ChainEngine#tickAll). */
    public transient int orphanTicks = 0;

    /** Previous tick's resolved endpoint world positions — used to derive the endpoints' along-axis velocity so the
     *  tether pull can DAMP relative motion (else it's an undamped spring that oscillates forever). Null until first
     *  coupled tick; see {@code ChainEngine#applyRope}. */
    public transient org.joml.Vector3d lastEndA;
    public transient org.joml.Vector3d lastEndB;

    public Chain(UUID id, UUID worldId, BlockPos a, BlockPos b, org.joml.Vector3d offsetA,
            org.joml.Vector3d offsetB, ChainMaterial material, int blocks,
            net.minecraft.nbt.CompoundTag data) {
        this.id = id;
        this.worldId = worldId;
        this.a = a;
        this.b = b;
        this.offsetA = offsetA;
        this.offsetB = offsetB;
        this.material = material;
        this.blocks = blocks;
        this.data = data != null ? data : new net.minecraft.nbt.CompoundTag();
    }

    // ---- typed convenience over the persistent NBT (or use the raw `data` CompoundTag) ----

    public String getString(String key) {
        return data.getString(key).orElse(null);
    }

    public void setString(String key, String value) {
        if (value == null) {
            data.remove(key);
        } else {
            data.putString(key, value);
        }
    }

    public int getInt(String key, int def) {
        return data.getInt(key).orElse(def);
    }

    public void setInt(String key, int value) {
        data.putInt(key, value);
    }

    public double getDouble(String key, double def) {
        return data.getDouble(key).orElse(def);
    }

    public void setDouble(String key, double value) {
        data.putDouble(key, value);
    }

    public boolean has(String key) {
        return data.contains(key);
    }

    public void removeData(String key) {
        data.remove(key);
    }

    /** Straight-line rest length of the chain in blocks (the taut distance between endpoint centres). */
    public double restLength() {
        return Math.sqrt(a.distSqr(b));
    }
}
