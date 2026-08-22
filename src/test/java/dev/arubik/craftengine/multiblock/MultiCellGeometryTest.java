package dev.arubik.craftengine.multiblock;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.arubik.craftengine.multiblock.MultiCellGeometry.Offset;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;

/**
 * Pure position math for {@link MultiCellGeometry} — the generalized replacement for the deleted
 * {@code HorizontalDoubleGeometry}. The single-offset {@code {right:1}} case is asserted against
 * the EXACT same expected positions {@code HorizontalDoubleGeometryTest} used to check (this is
 * the regression proof that migrating {@code WorkbenchBehavior} onto {@link MultiCellBlockBehavior}
 * changed nothing observable), plus new coverage for an arbitrary N-tall vertical stack (the shape
 * {@code HorizontalDoubleGeometry} could never express).
 */
class MultiCellGeometryTest {

    /** Single secondary cell one step toward facing.clockWise() — the old horizontal-double shape. */
    private static final List<Offset> RIGHT_CELL = List.of(new Offset(1, 0, 0));

    @Test
    void singleOffsetReproducesHorizontalDoubleForAllFacings() {
        BlockPos master = new BlockPos(10, 64, 10);
        // Exactly the old HorizontalDoubleGeometry.rightCell expectations, ported 1:1.
        assertEquals(new BlockPos(11, 64, 10),
                MultiCellGeometry.cellPos(master, Direction.NORTH, RIGHT_CELL, 1));
        assertEquals(new BlockPos(10, 64, 11),
                MultiCellGeometry.cellPos(master, Direction.EAST, RIGHT_CELL, 1));
        assertEquals(new BlockPos(9, 64, 10),
                MultiCellGeometry.cellPos(master, Direction.SOUTH, RIGHT_CELL, 1));
        assertEquals(new BlockPos(10, 64, 9),
                MultiCellGeometry.cellPos(master, Direction.WEST, RIGHT_CELL, 1));
    }

    @Test
    void masterResolvesFromEitherCellForAllFacings() {
        BlockPos master = new BlockPos(100, 64, -50);
        for (Direction facing : new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST }) {
            BlockPos right = MultiCellGeometry.cellPos(master, facing, RIGHT_CELL, 1);
            // From index 0 (master itself), master resolves to itself.
            assertEquals(master, MultiCellGeometry.masterPos(master, facing, RIGHT_CELL, 0));
            // From index 1 (the secondary), master resolves one step back.
            assertEquals(master, MultiCellGeometry.masterPos(right, facing, RIGHT_CELL, 1));
        }
    }

    @Test
    void allCellPositionsIncludesMasterFirst() {
        BlockPos master = new BlockPos(5, 70, 5);
        List<BlockPos> all = MultiCellGeometry.allCellPositions(master, Direction.SOUTH, RIGHT_CELL);
        assertEquals(2, all.size());
        assertEquals(master, all.get(0));
        assertEquals(MultiCellGeometry.cellPos(master, Direction.SOUTH, RIGHT_CELL, 1), all.get(1));
    }

    /** A 4-tall vertical tower (the energy windmill's shape) — no HorizontalDoubleGeometry
     * equivalent existed for this; {@code up} must stay facing-invariant. */
    @Test
    void verticalStackIgnoresFacingForUpOffsets() {
        List<Offset> tower = List.of(new Offset(0, 1, 0), new Offset(0, 2, 0), new Offset(0, 3, 0));
        BlockPos master = new BlockPos(0, 64, 0);
        for (Direction facing : new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST }) {
            assertEquals(new BlockPos(0, 65, 0), MultiCellGeometry.cellPos(master, facing, tower, 1));
            assertEquals(new BlockPos(0, 66, 0), MultiCellGeometry.cellPos(master, facing, tower, 2));
            assertEquals(new BlockPos(0, 67, 0), MultiCellGeometry.cellPos(master, facing, tower, 3));
            // Master resolves correctly from the topmost cell regardless of facing.
            assertEquals(master, MultiCellGeometry.masterPos(new BlockPos(0, 67, 0), facing, tower, 3));
        }
    }
}
