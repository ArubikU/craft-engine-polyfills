package dev.arubik.craftengine.multiblock;

import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;

/**
 * Pure geometry for a horizontal 2-wide double block. No Bukkit / NMS types, so
 * it is unit-testable in isolation (see {@code HorizontalDoubleGeometryTest}).
 *
 * <h2>Convention</h2>
 * A horizontal double block occupies two horizontally-adjacent cells:
 * <ul>
 *   <li>{@code LEFT}  = the master / origin cell (hosts the block entity, owns the menu).</li>
 *   <li>{@code RIGHT} = the second half, sitting one cell toward {@code facing.clockWise()}.</li>
 * </ul>
 * {@code facing} is the block's horizontal facing (the direction the front of the
 * structure points). We define the "right" direction (from the master's point of
 * view) as {@code facing.clockWise()}. So:
 * <pre>
 *   rightCell  = masterPos.relative(facing.clockWise())
 *   masterPos  = rightPos.relative(facing.counterClockWise())   // = facing.clockWise().opposite()
 * </pre>
 * This is a fixed, documented convention: rotating {@code facing} +90 clockwise
 * yields the offset from master to the RIGHT half.
 */
public final class HorizontalDoubleGeometry {

    private HorizontalDoubleGeometry() {}

    /** The two halves of a horizontal double block. */
    public enum Half {
        LEFT,
        RIGHT;

        public static Half fromName(String name) {
            if (name == null) return LEFT;
            return name.equalsIgnoreCase("right") ? RIGHT : LEFT;
        }

        public Half other() {
            return this == LEFT ? RIGHT : LEFT;
        }
    }

    /** Direction from the master (LEFT) toward the RIGHT half: {@code facing} rotated +90 clockwise. */
    public static Direction rightDirection(Direction facing) {
        return facing.clockWise();
    }

    /** Direction from the RIGHT half back toward the master (LEFT): opposite of {@link #rightDirection}. */
    public static Direction leftDirection(Direction facing) {
        return facing.counterClockWise();
    }

    /** Cell occupied by the RIGHT half, given the master (LEFT) cell and facing. */
    public static BlockPos rightCell(BlockPos masterPos, Direction facing) {
        return masterPos.relative(rightDirection(facing));
    }

    /**
     * Resolve the master (LEFT) cell from any half.
     * If {@code half == LEFT}, {@code thisPos} is already the master.
     * If {@code half == RIGHT}, the master is one cell toward {@link #leftDirection}.
     */
    public static BlockPos masterPos(BlockPos thisPos, Direction facing, Half half) {
        return half == Half.LEFT ? thisPos : thisPos.relative(leftDirection(facing));
    }

    /** Resolve the partner (the OTHER half's) cell from any half. */
    public static BlockPos partnerPos(BlockPos thisPos, Direction facing, Half half) {
        return half == Half.LEFT
                ? thisPos.relative(rightDirection(facing))
                : thisPos.relative(leftDirection(facing));
    }
}
