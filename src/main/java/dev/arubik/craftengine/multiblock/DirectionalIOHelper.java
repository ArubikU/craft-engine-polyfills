package dev.arubik.craftengine.multiblock;

import net.minecraft.core.Direction;
import net.momirealms.craftengine.core.util.HorizontalDirection;

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
    public static Direction getWorldDirection(RelativeDirection relative, HorizontalDirection facing) {
        if (relative == null || facing == null) {
            return Direction.NORTH; // Default fallback
        }

        // Vertical directions don't rotate
        if (relative == RelativeDirection.UP) {
            return Direction.UP;
        }
        if (relative == RelativeDirection.DOWN) {
            return Direction.DOWN;
        }

        // Get base direction from horizontal facing
        Direction baseDir = fromHorizontalDirection(facing);

        // Transform based on relative direction
        return switch (relative) {
            case FRONT -> baseDir;
            case BACK -> baseDir.getOpposite();
            case LEFT -> baseDir.getCounterClockWise();
            case RIGHT -> baseDir.getClockWise();
            default -> baseDir; // Should not reach here
        };
    }

    /**
     * Transform a relative direction to world direction based on full Direction
     * facing
     * Supports 6-way direction (including UP/DOWN as facing)
     * 
     * @param relative The relative direction
     * @param facing   The machine's full direction (can be UP/DOWN)
     * @return The actual world direction
     */
    public static Direction getWorldDirection(RelativeDirection relative, Direction facing) {
        if (relative == null || facing == null) {
            return Direction.NORTH;
        }

        // If facing is UP or DOWN, use special handling
        if (facing == Direction.UP || facing == Direction.DOWN) {
            // For vertical facing, FRONT = facing direction, BACK = opposite
            return switch (relative) {
                case FRONT -> facing;
                case BACK -> facing.getOpposite();
                case UP -> Direction.UP;
                case DOWN -> Direction.DOWN;
                // LEFT/RIGHT default to NORTH/SOUTH when facing is vertical
                case LEFT -> Direction.NORTH;
                case RIGHT -> Direction.SOUTH;
            };
        }

        // For horizontal facing, use HorizontalDirection logic
        HorizontalDirection horizontal = toHorizontalDirection(facing);
        return getWorldDirection(relative, horizontal);
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
}
