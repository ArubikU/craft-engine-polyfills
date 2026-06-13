package dev.arubik.craftengine.multiblock;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dev.arubik.craftengine.multiblock.HorizontalDoubleGeometry.Half;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;

/** Pure direction / master-resolution math for {@link HorizontalDoubleGeometry}. */
class HorizontalDoubleGeometryTest {

    @Test
    void rightDirectionIsFacingClockwise() {
        // NORTH(-Z) clockwise -> EAST(+X); EAST -> SOUTH; SOUTH -> WEST; WEST -> NORTH
        assertEquals(Direction.EAST, HorizontalDoubleGeometry.rightDirection(Direction.NORTH));
        assertEquals(Direction.SOUTH, HorizontalDoubleGeometry.rightDirection(Direction.EAST));
        assertEquals(Direction.WEST, HorizontalDoubleGeometry.rightDirection(Direction.SOUTH));
        assertEquals(Direction.NORTH, HorizontalDoubleGeometry.rightDirection(Direction.WEST));
    }

    @Test
    void leftDirectionIsOppositeOfRight() {
        for (Direction f : new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST }) {
            assertEquals(HorizontalDoubleGeometry.rightDirection(f).opposite(),
                    HorizontalDoubleGeometry.leftDirection(f));
        }
    }

    @Test
    void rightCellIsOneStepClockwiseFromMaster() {
        BlockPos master = new BlockPos(10, 64, 10);
        // facing NORTH -> right = EAST -> +X
        BlockPos right = HorizontalDoubleGeometry.rightCell(master, Direction.NORTH);
        assertEquals(new BlockPos(11, 64, 10), right);
    }

    @Test
    void masterResolvesFromEitherHalf() {
        BlockPos master = new BlockPos(0, 64, 0);
        Direction facing = Direction.SOUTH; // right = WEST -> -X
        BlockPos right = HorizontalDoubleGeometry.rightCell(master, facing);
        assertEquals(new BlockPos(-1, 64, 0), right);

        // From LEFT, master is itself.
        assertEquals(master, HorizontalDoubleGeometry.masterPos(master, facing, Half.LEFT));
        // From RIGHT, master is one step back.
        assertEquals(master, HorizontalDoubleGeometry.masterPos(right, facing, Half.RIGHT));
    }

    @Test
    void partnerIsSymmetric() {
        BlockPos master = new BlockPos(5, 70, 5);
        for (Direction facing : new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST }) {
            BlockPos right = HorizontalDoubleGeometry.rightCell(master, facing);
            // master's partner is the right cell
            assertEquals(right, HorizontalDoubleGeometry.partnerPos(master, facing, Half.LEFT));
            // right's partner is the master
            assertEquals(master, HorizontalDoubleGeometry.partnerPos(right, facing, Half.RIGHT));
        }
    }

    @Test
    void allFourFacingsRoundTrip() {
        BlockPos master = new BlockPos(100, 64, -50);
        for (Direction facing : new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST }) {
            BlockPos right = HorizontalDoubleGeometry.rightCell(master, facing);
            assertEquals(master, HorizontalDoubleGeometry.masterPos(right, facing, Half.RIGHT));
            assertEquals(right, HorizontalDoubleGeometry.masterPos(right, facing, Half.RIGHT)
                    .relative(HorizontalDoubleGeometry.rightDirection(facing)));
        }
    }
}
