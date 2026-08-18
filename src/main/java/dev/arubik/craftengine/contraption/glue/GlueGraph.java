/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 */
package dev.arubik.craftengine.contraption.glue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;

public final class GlueGraph {
    private final Map<BlockPos, Set<BlockPos>> adjacency = new HashMap<BlockPos, Set<BlockPos>>();

    public void addNode(BlockPos pos) {
        this.adjacency.computeIfAbsent(pos, p -> new HashSet<>());
    }

    public boolean hasNode(BlockPos pos) {
        return this.adjacency.containsKey(pos);
    }

    public void glue(BlockPos a, BlockPos b) {
        if (a.equals(b)) {
            throw new IllegalArgumentException("cannot glue a block to itself: " + String.valueOf(a));
        }
        this.adjacency.computeIfAbsent(a, p -> new HashSet<>()).add(b);
        this.adjacency.computeIfAbsent(b, p -> new HashSet<>()).add(a);
    }

    public void unglue(BlockPos a, BlockPos b) {
        Set<BlockPos> bEdges;
        Set<BlockPos> aEdges = this.adjacency.get(a);
        if (aEdges != null) {
            aEdges.remove(b);
        }
        if ((bEdges = this.adjacency.get(b)) != null) {
            bEdges.remove(a);
        }
    }

    public boolean isGlued(BlockPos a, BlockPos b) {
        Set<BlockPos> edges = this.adjacency.get(a);
        return edges != null && edges.contains(b);
    }

    public Set<BlockPos> neighbors(BlockPos pos) {
        return this.adjacency.getOrDefault(pos, Set.of());
    }

    public Set<BlockPos> nodes() {
        return this.adjacency.keySet();
    }

    public int size() {
        return this.adjacency.size();
    }

    public List<Set<BlockPos>> removeNode(BlockPos pos) {
        Set<BlockPos> neighbors = this.adjacency.remove(pos);
        if (neighbors != null) {
            for (BlockPos n : neighbors) {
                Set<BlockPos> edges = this.adjacency.get(n);
                if (edges == null) continue;
                edges.remove(pos);
            }
        }
        return this.connectedComponents();
    }

    public List<Set<BlockPos>> connectedComponents() {
        ArrayList<Set<BlockPos>> components = new ArrayList<Set<BlockPos>>();
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        for (BlockPos start : this.adjacency.keySet()) {
            if (visited.contains(start)) continue;
            HashSet<BlockPos> component = new HashSet<BlockPos>();
            ArrayDeque<BlockPos> queue = new ArrayDeque<BlockPos>();
            queue.add(start);
            visited.add(start);
            while (!queue.isEmpty()) {
                BlockPos current = (BlockPos)queue.poll();
                component.add(current);
                for (BlockPos neighbor : this.adjacency.getOrDefault(current, Set.of())) {
                    if (!visited.add(neighbor)) continue;
                    queue.add(neighbor);
                }
            }
            components.add(component);
        }
        return components;
    }

    public boolean isFullyConnected() {
        List<Set<BlockPos>> components = this.connectedComponents();
        return components.size() <= 1;
    }

    public void translateAll(Map<BlockPos, BlockPos> move) {
        if (move.isEmpty()) {
            return;
        }
        Map<BlockPos, Set<BlockPos>> relocated = new HashMap<>();
        for (Map.Entry<BlockPos, BlockPos> entry : move.entrySet()) {
            Set<BlockPos> oldEdges = this.adjacency.get(entry.getKey());
            if (oldEdges == null) continue;
            HashSet<BlockPos> newEdges = new HashSet<BlockPos>();
            for (BlockPos oldNeighbor : oldEdges) {
                newEdges.add(move.getOrDefault(oldNeighbor, oldNeighbor));
            }
            relocated.put(entry.getValue(), newEdges);
        }
        for (BlockPos blockPos : move.keySet()) {
            this.adjacency.remove(blockPos);
        }
        for (Map.Entry<BlockPos, Set<BlockPos>> entry : relocated.entrySet()) {
            this.adjacency.computeIfAbsent(entry.getKey(), p -> new HashSet<>()).addAll(entry.getValue());
        }
    }
}

