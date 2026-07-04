package dev.arubik.craftengine.contraption;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.core.BlockPos;

/**
 * Adjacency graph over glued block positions (CONTRAPTIONS.md §1 "Structure detection").
 * Pure data structure, no NMS/Bukkit runtime dependency beyond {@link BlockPos} as a
 * plain value key — testable without a server.
 *
 * <p>Doubles as the fracture graph: when a glued block is destroyed at runtime, the
 * remaining structure may split into independent sub-contraptions. {@link #removeNode}
 * returns exactly that split as a list of connected components.
 */
public final class GlueGraph {

    private final Map<BlockPos, Set<BlockPos>> adjacency = new HashMap<>();

    public void addNode(BlockPos pos) {
        adjacency.computeIfAbsent(pos, p -> new HashSet<>());
    }

    public boolean hasNode(BlockPos pos) {
        return adjacency.containsKey(pos);
    }

    /** Glues two positions together (undirected edge). Adds both as nodes if absent. */
    public void glue(BlockPos a, BlockPos b) {
        if (a.equals(b)) {
            throw new IllegalArgumentException("cannot glue a block to itself: " + a);
        }
        adjacency.computeIfAbsent(a, p -> new HashSet<>()).add(b);
        adjacency.computeIfAbsent(b, p -> new HashSet<>()).add(a);
    }

    /** Removes the glue edge between two positions, if present. Leaves both nodes in the graph. */
    public void unglue(BlockPos a, BlockPos b) {
        Set<BlockPos> aEdges = adjacency.get(a);
        if (aEdges != null) {
            aEdges.remove(b);
        }
        Set<BlockPos> bEdges = adjacency.get(b);
        if (bEdges != null) {
            bEdges.remove(a);
        }
    }

    public boolean isGlued(BlockPos a, BlockPos b) {
        Set<BlockPos> edges = adjacency.get(a);
        return edges != null && edges.contains(b);
    }

    public Set<BlockPos> neighbors(BlockPos pos) {
        return adjacency.getOrDefault(pos, Set.of());
    }

    public Set<BlockPos> nodes() {
        return adjacency.keySet();
    }

    public int size() {
        return adjacency.size();
    }

    /**
     * Removes a node and every edge touching it (e.g. the block was broken in the real
     * world). Returns the connected components of what remains, letting the caller split
     * a fractured contraption into independent sub-contraptions. Never includes the
     * removed node itself; returns an empty list if nothing remains.
     */
    public List<Set<BlockPos>> removeNode(BlockPos pos) {
        Set<BlockPos> neighbors = adjacency.remove(pos);
        if (neighbors != null) {
            for (BlockPos n : neighbors) {
                Set<BlockPos> edges = adjacency.get(n);
                if (edges != null) {
                    edges.remove(pos);
                }
            }
        }
        return connectedComponents();
    }

    /** All connected components of the current graph (BFS over glue edges). */
    public List<Set<BlockPos>> connectedComponents() {
        List<Set<BlockPos>> components = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        for (BlockPos start : adjacency.keySet()) {
            if (visited.contains(start)) {
                continue;
            }
            Set<BlockPos> component = new HashSet<>();
            Deque<BlockPos> queue = new ArrayDeque<>();
            queue.add(start);
            visited.add(start);
            while (!queue.isEmpty()) {
                BlockPos current = queue.poll();
                component.add(current);
                for (BlockPos neighbor : adjacency.getOrDefault(current, Set.of())) {
                    if (visited.add(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
            components.add(component);
        }
        return components;
    }

    /** True if every node in the graph is reachable from every other (single contraption). */
    public boolean isFullyConnected() {
        List<Set<BlockPos>> components = connectedComponents();
        return components.size() <= 1;
    }

    /**
     * Rekeys every node in {@code move} (old absolute {@link BlockPos} -&gt; new absolute
     * {@link BlockPos}) to its new position, preserving edge topology — used when a
     * contraption disassembles somewhere OTHER than where it was captured (grid-snap landed on
     * a different position than the original glue-scan, e.g. it moved before being torn down).
     * Without this, {@link #adjacency} stays keyed at the ORIGINAL capture-time absolute
     * positions forever, so a later {@link GlueRegistry#structureAt} lookup against the block
     * that's now actually sitting at the NEW position finds nothing (unglued), while the stale
     * entries at the old (now-empty-air) positions linger — "el glue al desarmar no se pone
     * donde termino la posicion del contraption si no en la original... termina glue x todo el
     * mundo". Nodes not present in {@code move} (not part of this structure) are untouched;
     * edges between two translated nodes correctly point at each other's NEW position; an edge
     * from a translated node to a node NOT in {@code move} (shouldn't normally happen for a
     * fully-captured structure, but handled defensively) is left pointing at that node's
     * unchanged position.
     */
    public void translateAll(Map<BlockPos, BlockPos> move) {
        if (move.isEmpty()) {
            return;
        }
        Map<BlockPos, Set<BlockPos>> relocated = new HashMap<>();
        for (Map.Entry<BlockPos, BlockPos> e : move.entrySet()) {
            Set<BlockPos> oldEdges = adjacency.get(e.getKey());
            if (oldEdges == null) {
                continue; // not a glued node — nothing to translate
            }
            Set<BlockPos> newEdges = new HashSet<>();
            for (BlockPos oldNeighbor : oldEdges) {
                newEdges.add(move.getOrDefault(oldNeighbor, oldNeighbor));
            }
            relocated.put(e.getValue(), newEdges);
        }
        for (BlockPos oldPos : move.keySet()) {
            adjacency.remove(oldPos);
        }
        for (Map.Entry<BlockPos, Set<BlockPos>> e : relocated.entrySet()) {
            adjacency.computeIfAbsent(e.getKey(), p -> new HashSet<>()).addAll(e.getValue());
        }
    }
}
