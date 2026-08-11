package dev.arubik.craftengine.contraption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import dev.arubik.craftengine.contraption.glue.GlueGraph;
import net.minecraft.core.BlockPos;

/** Pure adjacency/fracture-graph tests for {@link GlueGraph} — no NMS runtime needed. */
class GlueGraphTest {

    private static BlockPos p(int x, int y, int z) {
        return new BlockPos(x, y, z);
    }

    @Test
    void singleNodeIsFullyConnected() {
        GlueGraph graph = new GlueGraph();
        graph.addNode(p(0, 0, 0));
        assertTrue(graph.isFullyConnected());
        assertEquals(1, graph.connectedComponents().size());
    }

    @Test
    void glueMakesTwoNodesFullyConnected() {
        GlueGraph graph = new GlueGraph();
        graph.glue(p(0, 0, 0), p(1, 0, 0));
        assertTrue(graph.isGlued(p(0, 0, 0), p(1, 0, 0)));
        assertTrue(graph.isGlued(p(1, 0, 0), p(0, 0, 0)));
        assertTrue(graph.isFullyConnected());
        assertEquals(2, graph.size());
    }

    @Test
    void unrelatedGluedPairsAreSeparateComponents() {
        GlueGraph graph = new GlueGraph();
        graph.glue(p(0, 0, 0), p(1, 0, 0));
        graph.glue(p(10, 0, 0), p(11, 0, 0));
        assertFalse(graph.isFullyConnected());
        assertEquals(2, graph.connectedComponents().size());
    }

    @Test
    void removingMiddleNodeFracturesLineIntoTwoComponents() {
        // a - b - c ; removing b should split into {a} and {c}
        GlueGraph graph = new GlueGraph();
        BlockPos a = p(0, 0, 0), b = p(1, 0, 0), c = p(2, 0, 0);
        graph.glue(a, b);
        graph.glue(b, c);

        List<Set<BlockPos>> remaining = graph.removeNode(b);

        assertEquals(2, remaining.size());
        assertTrue(remaining.stream().anyMatch(s -> s.size() == 1 && s.contains(a)));
        assertTrue(remaining.stream().anyMatch(s -> s.size() == 1 && s.contains(c)));
        assertFalse(graph.hasNode(b));
        assertEquals(0, graph.neighbors(a).size());
        assertEquals(0, graph.neighbors(c).size());
    }

    @Test
    void removingLeafNodeDoesNotFractureTheRest() {
        GlueGraph graph = new GlueGraph();
        BlockPos a = p(0, 0, 0), b = p(1, 0, 0), c = p(2, 0, 0);
        graph.glue(a, b);
        graph.glue(b, c);

        List<Set<BlockPos>> remaining = graph.removeNode(a);

        assertEquals(1, remaining.size());
        assertEquals(Set.of(b, c), remaining.get(0));
    }

    @Test
    void ungluePreservesBothNodesButSplitsComponent() {
        GlueGraph graph = new GlueGraph();
        BlockPos a = p(0, 0, 0), b = p(1, 0, 0);
        graph.glue(a, b);
        graph.unglue(a, b);

        assertTrue(graph.hasNode(a));
        assertTrue(graph.hasNode(b));
        assertFalse(graph.isGlued(a, b));
        assertEquals(2, graph.connectedComponents().size());
    }

    @Test
    void cannotGlueBlockToItself() {
        GlueGraph graph = new GlueGraph();
        assertThrows(IllegalArgumentException.class, () -> graph.glue(p(0, 0, 0), p(0, 0, 0)));
    }

    @Test
    void removingUnknownNodeIsNoOp() {
        GlueGraph graph = new GlueGraph();
        graph.glue(p(0, 0, 0), p(1, 0, 0));
        List<Set<BlockPos>> remaining = graph.removeNode(p(99, 99, 99));
        assertEquals(1, remaining.size());
        assertEquals(2, remaining.get(0).size());
    }
}
