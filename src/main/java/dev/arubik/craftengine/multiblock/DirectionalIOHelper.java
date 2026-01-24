package dev.arubik.craftengine.multiblock;

import net.minecraft.core.Direction;
import net.momirealms.craftengine.core.util.HorizontalDirection;
import dev.arubik.craftengine.util.DirectionType;

/**
 * Helper for transforming relative machine directions to world directions
 * Based on machine facing orientation
 */
public class DirectionalIOHelper {

    /**
     * Transform a relative direction to world direction based on machine facing
     * 
     * @param relative The relative direction (FRONT, BACK, LEFT, RIGHT, UP, DOWN)
     * @param facing   The machine's horizontal facing direction
     * @return The actual world direction
     */
    public static Direction getHorizontalWorldDirection(RelativeDirection relative, HorizontalDirection facing) {
        if (relative == null || facing == null) {
            return Direction.NORTH;
        }

        // Horizontal mapping: NORTH is identity
        // FRONT points to facing
        // UP/DOWN are invariant
        Direction baseDir = fromHorizontalDirection(facing);
        return switch (relative) {
            case FRONT -> baseDir;
            case BACK -> baseDir.getOpposite();
            case LEFT -> baseDir.getCounterClockWise();
            case RIGHT -> baseDir.getClockWise();
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
        };
    }

    /**
     * Transform a relative direction to world direction based on full Direction
     * facing
     * Supports 6-way direction (including UP/DOWN as facing)
     * For 6-way machines: UP is the facing!
     * 
     * @param relative The relative direction
     * @param facing   The machine's full direction (can be UP/DOWN)
     * @return The actual world direction
     */
    public static Direction getVerticalWorldDirection(RelativeDirection relative, Direction facing) {
        if (relative == null || facing == null) {
            return Direction.NORTH;
        }
        if (relative == RelativeDirection.UP)
            return facing;
        if (relative == RelativeDirection.DOWN)
            return facing.getOpposite();

        if (facing == Direction.UP || facing == Direction.DOWN) {
            boolean invert = facing == Direction.DOWN;
            return switch (relative) {
                case FRONT -> invert ? Direction.SOUTH : Direction.NORTH;
                case BACK -> invert ? Direction.NORTH : Direction.SOUTH;
                case LEFT -> invert ? Direction.EAST : Direction.WEST;
                case RIGHT -> invert ? Direction.WEST : Direction.EAST;
                default -> facing; // should not happen
            };
        }

        return switch (relative) {
            case FRONT -> Direction.UP;
            case BACK -> Direction.DOWN;
            case LEFT -> facing.getCounterClockWise();
            case RIGHT -> facing.getClockWise();
            default -> facing;
        };
    }

    /**
     * Convert HorizontalDirection to Minecraft Direction
     */
    public static Direction fromHorizontalDirection(HorizontalDirection horizontal) {
        return switch (horizontal) {
            case NORTH -> Direction.NORTH;
            case SOUTH -> Direction.SOUTH;
            case EAST -> Direction.EAST;
            case WEST -> Direction.WEST;
        };
    }

    /**
     * Convert Minecraft Direction to HorizontalDirection
     * Returns NORTH for non-horizontal directions
     */
    public static HorizontalDirection toHorizontalDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> HorizontalDirection.NORTH;
            case SOUTH -> HorizontalDirection.SOUTH;
            case EAST -> HorizontalDirection.EAST;
            case WEST -> HorizontalDirection.WEST;
            default -> HorizontalDirection.NORTH; // UP/DOWN default to NORTH
        };
    }

    /**
     * Map RelativeDirection to standard Local Direction (Minecraft)
     */
    public static Direction toDirection(RelativeDirection relative) {
        return switch (relative) {
            case FRONT -> Direction.NORTH;
            case BACK -> Direction.SOUTH;
            case LEFT -> Direction.WEST;
            case RIGHT -> Direction.EAST;
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
        };
    }

    /**
     * Map Local Direction (Minecraft) to RelativeDirection
     */
    public static RelativeDirection fromDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> RelativeDirection.FRONT;
            case SOUTH -> RelativeDirection.BACK;
            case WEST -> RelativeDirection.LEFT;
            case EAST -> RelativeDirection.RIGHT;
            case UP -> RelativeDirection.UP;
            case DOWN -> RelativeDirection.DOWN;
        };
    }

    /**
     * Transform a world direction back to local direction based on machine facing
     */
    public static Direction getVerticalLocalDirection(Direction world, Direction facing) {
        for (RelativeDirection rel : RelativeDirection.values()) {
            if (getVerticalWorldDirection(rel, facing) == world) {
                return toDirection(rel);
            }
        }
        return world;
    }

    /**
     * Transform a world direction back to local direction based on machine
     * horizontal facing
     */
    public static Direction getHorizontalLocalDirection(Direction world, HorizontalDirection facing) {
        for (RelativeDirection rel : RelativeDirection.values()) {
            if (getHorizontalWorldDirection(rel, facing) == world) {
                return toDirection(rel);
            }
        }
        return world;
    }
}
