package dev.arubik.craftengine.contraption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

/**
 * The two pure rules behind the happy-ghast harness contraption: which items are harnesses, and which
 * world cells a ghast occupies (the cells {@link GhastHarnessBearing#gluedStructureAround} probes the
 * glue graph at).
 *
 * <p>Pure: {@link GhastHarness} touches no server state, so both are provable without a live server.
 */
class GhastHarnessTest {

    /** An adult ghast standing at {@code (x, y, z)}: {@code sized(4, 4)} with the position at the feet. */
    private static AABB ghastBoxAt(double x, double y, double z) {
        return new AABB(x - 2, y, z - 2, x + 2, y + 4, z + 2);
    }

    @Test
    @DisplayName("all 16 dye colours have a harness, and nothing else is one")
    void everyColourIsAHarness() {
        assertEquals(16, GhastHarness.harnesses().size(), "there are exactly 16 harness colours");
        for (DyeColor color : DyeColor.values()) {
            assertTrue(GhastHarness.isHarness(Material.valueOf(color.name() + "_HARNESS")),
                    color + " must have a harness");
        }
        assertFalse(GhastHarness.isHarness(Material.SHEARS));
        assertFalse(GhastHarness.isHarness(Material.WHITE_WOOL));
        assertFalse(GhastHarness.isHarness(Material.SADDLE));
        assertFalse(GhastHarness.isHarness(null));
    }

    @Test
    @DisplayName("a ghast on whole coordinates occupies exactly the 4x4x4 its own box spans")
    void ghastOnGridOccupiesItsOwnBox() {
        List<BlockPos> cells = GhastHarness.cellsOverlapping(ghastBoxAt(10, 64, 10));
        assertEquals(64, cells.size(), "4x4x4 — a face-touching neighbour is not occupied");
        assertEquals(64, new HashSet<>(cells).size(), "no cell is reported twice");
        for (BlockPos cell : cells) {
            assertTrue(cell.getX() >= 8 && cell.getX() <= 11, "x within the box: " + cell);
            assertTrue(cell.getY() >= 64 && cell.getY() <= 67, "y within the box: " + cell);
            assertTrue(cell.getZ() >= 8 && cell.getZ() <= 11, "z within the box: " + cell);
        }
    }

    @Test
    @DisplayName("a ghast straddling the grid occupies every cell it partially overlaps")
    void ghastOffGridOccupiesEveryPartialCell() {
        Set<BlockPos> cells = new HashSet<>(GhastHarness.cellsOverlapping(ghastBoxAt(10.5, 64.5, 10.5)));
        assertEquals(125, cells.size(), "5x5x5 — every axis now clips a fifth cell at both ends");
        assertTrue(cells.contains(new BlockPos(8, 64, 8)), "the cell the box's min corner is inside");
        assertTrue(cells.contains(new BlockPos(12, 68, 12)), "the cell the box's max corner is inside");
    }

    @Test
    @DisplayName("a box ending exactly on a block plane does not claim the slab beyond it")
    void faceTouchingNeighbourIsNotOccupied() {
        List<BlockPos> cells = GhastHarness.cellsOverlapping(new AABB(0, 0, 0, 1, 1, 1));
        assertEquals(List.of(new BlockPos(0, 0, 0)), cells);
    }

    @Test
    @DisplayName("negative coordinates floor toward -infinity rather than toward zero")
    void negativeCoordinatesFloorDown() {
        Set<BlockPos> cells = new HashSet<>(GhastHarness.cellsOverlapping(new AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5)));
        assertEquals(8, cells.size());
        assertTrue(cells.contains(new BlockPos(-1, -1, -1)), "the cell below the origin, not cell 0");
        assertTrue(cells.contains(new BlockPos(0, 0, 0)));
    }
}
