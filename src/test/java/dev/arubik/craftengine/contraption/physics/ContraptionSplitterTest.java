package dev.arubik.craftengine.contraption.physics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.minecraft.core.BlockPos;

/**
 * The fracture rule: which cells still hold together after one is broken out.
 *
 * <p>Pure — {@link ContraptionSplitter#islands} takes a plain cell set, so the rule that decides
 * whether a PhysContraption falls apart is provable without a server, a level, or a solver.
 */
class ContraptionSplitterTest {

    private static Set<BlockPos> cells(BlockPos... positions) {
        return new HashSet<>(List.of(positions));
    }

    private static BlockPos p(int x, int y, int z) {
        return new BlockPos(x, y, z);
    }

    @Test
    @DisplayName("a connected body is one island")
    void connectedBodyIsOneIsland() {
        Set<BlockPos> body = cells(p(0, 0, 0), p(1, 0, 0), p(2, 0, 0), p(2, 1, 0));

        List<Set<BlockPos>> islands = ContraptionSplitter.islands(body);

        assertEquals(1, islands.size());
        assertEquals(body, islands.get(0));
    }

    @Test
    @DisplayName("breaking the middle of a one-wide bridge splits it in two")
    void brokenBridgeSplitsInTwo() {
        // The original bridge was x = 0..4; the block at x = 2 has just been broken out.
        Set<BlockPos> body = cells(p(0, 0, 0), p(1, 0, 0), p(3, 0, 0), p(4, 0, 0));

        List<Set<BlockPos>> islands = ContraptionSplitter.islands(body);

        assertEquals(2, islands.size());
        assertTrue(islands.contains(cells(p(0, 0, 0), p(1, 0, 0))));
        assertTrue(islands.contains(cells(p(3, 0, 0), p(4, 0, 0))));
    }

    @Test
    @DisplayName("cells touching only at a corner are not connected")
    void diagonalTouchIsNotConnected() {
        // Adjacent under a 26-neighbour rule, separate under the 6-neighbour face rule this uses:
        // a shared corner holds nothing together.
        List<Set<BlockPos>> islands = ContraptionSplitter.islands(cells(p(0, 0, 0), p(1, 1, 1)));

        assertEquals(2, islands.size());
    }

    @Test
    @DisplayName("cells touching only along an edge are not connected")
    void edgeTouchIsNotConnected() {
        List<Set<BlockPos>> islands = ContraptionSplitter.islands(cells(p(0, 0, 0), p(1, 1, 0)));

        assertEquals(2, islands.size());
    }

    @Test
    @DisplayName("a single cell is one island of itself")
    void singleCellIsOneIsland() {
        List<Set<BlockPos>> islands = ContraptionSplitter.islands(cells(p(7, 3, -2)));

        assertEquals(1, islands.size());
        assertEquals(cells(p(7, 3, -2)), islands.get(0));
    }

    @Test
    @DisplayName("an empty body has no islands")
    void emptyBodyHasNoIslands() {
        assertTrue(ContraptionSplitter.islands(Set.of()).isEmpty());
        assertTrue(ContraptionSplitter.islands(null).isEmpty());
    }

    @Test
    @DisplayName("every cell lands in exactly one island")
    void islandsPartitionTheBody() {
        Set<BlockPos> body = cells(
                p(0, 0, 0), p(0, 1, 0), p(0, 2, 0),
                p(5, 0, 0), p(5, 0, 1),
                p(9, 9, 9));

        List<Set<BlockPos>> islands = ContraptionSplitter.islands(body);

        assertEquals(3, islands.size());
        Set<BlockPos> union = new HashSet<>();
        int total = 0;
        for (Set<BlockPos> island : islands) {
            union.addAll(island);
            total += island.size();
        }
        assertEquals(body, union);
        assertEquals(body.size(), total); // no cell claimed twice
    }
}
