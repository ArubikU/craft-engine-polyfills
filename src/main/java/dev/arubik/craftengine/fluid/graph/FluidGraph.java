package dev.arubik.craftengine.fluid.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;

/**
 * A connected fluid network as a graph of {@link FluidNode}s + {@link FluidEdge}s (Phase 1, read-only).
 * Produced by {@link FluidGraphBuilder}; will later be fed to the hydraulic solver (Phase 2).
 */
public final class FluidGraph {

    public final List<FluidNode> nodes = new ArrayList<>();
    public final List<FluidEdge> edges = new ArrayList<>();
    private final Map<Long, Integer> indexByPos = new HashMap<>();

    /** Adds a node if absent; returns its index. */
    public int addNode(FluidNode node) {
        long key = node.pos.asLong();
        Integer existing = indexByPos.get(key);
        if (existing != null)
            return existing;
        int idx = nodes.size();
        nodes.add(node);
        indexByPos.put(key, idx);
        return idx;
    }

    public Integer indexOf(BlockPos pos) {
        return indexByPos.get(pos.asLong());
    }

    public void addEdge(FluidEdge edge) {
        edges.add(edge);
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public int size() {
        return nodes.size();
    }
}
