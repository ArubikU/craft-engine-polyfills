package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Baked internal-emitter light map for a contraption. Computed ONCE per rebuildSwarm call
 * (when the level's block set changes), not per-element per-tick. Elements read from this
 * cache via {@link #getBlockLight(BlockPos)} — O(1) lookup instead of O(n) full scan.
 *
 * <p>Only tracks INTERNAL emitter contribution (torch falloff inside the contraption).
 * The real-world ambient light (from the external world at the element's projected position)
 * is still sampled per-element in render, but only when the contraption moves — not every tick.
 */
public final class ContraptionLightMap {

    private final Long2IntOpenHashMap map = new Long2IntOpenHashMap();

    public ContraptionLightMap() {
        map.defaultReturnValue(0);
    }

    /**
     * Recomputes the full internal light map from the level's current block set.
     * For each position in the level, stores max(0, best emitter contribution at that position).
     * Call this once per rebuildSwarm, not per tick.
     */
    public void bake(ContraptionLevel level) {
        map.clear();
        if (level == null) return;

        // First pass: collect all emitters
        var emitters = new java.util.ArrayList<EmitterEntry>();
        for (BlockPos pos : level.localPositions()) {
            BlockState state = level.getBlockState(pos);
            int emission = state.getLightEmission();
            if (emission > 0) {
                emitters.add(new EmitterEntry(pos.immutable(), emission));
            }
        }

        if (emitters.isEmpty()) return;

        // Second pass: for each cell, compute max contribution from all emitters
        for (BlockPos target : level.localPositions()) {
            int best = 0;
            for (EmitterEntry e : emitters) {
                int dist = chebyshev(target, e.pos);
                int contribution = e.emission - dist;
                if (contribution > best) {
                    best = contribution;
                }
            }
            if (best > 0) {
                map.put(target.asLong(), Math.min(15, best));
            }
        }
    }

    /**
     * Returns the baked internal emitter light at this position (0-15).
     * Caller should max() this with the real-world ambient to get final block light.
     */
    public int getInternalLight(BlockPos local) {
        return map.get(local.asLong());
    }

    /**
     * Combines baked internal emitter light with external ambient block light.
     * Equivalent to the old withEmitterFalloff() but O(1).
     */
    public int combinedBlockLight(BlockPos local, int ambientBlockLight) {
        return Math.min(15, Math.max(ambientBlockLight, map.get(local.asLong())));
    }

    private static int chebyshev(BlockPos a, BlockPos b) {
        return Math.max(Math.abs(a.getX() - b.getX()),
                Math.max(Math.abs(a.getY() - b.getY()), Math.abs(a.getZ() - b.getZ())));
    }

    private record EmitterEntry(BlockPos pos, int emission) {}
}
