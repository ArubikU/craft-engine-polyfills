package dev.arubik.craftengine.util;

/**
 * Distinguishes between 4-way (Horizontal) and 6-way (Full) mapping logic.
 */
public enum DirectionType {
    /**
     * 4-way directional mapping (NORTH, SOUTH, EAST, WEST).
     * Identity facing is NORTH. UP/DOWN are invariant.
     */
    HORIZONTAL,

    /**
     * 6-way directional mapping (including UP, DOWN).
     * Identity facing is UP. FRONT=UP, UP=NORTH.
     */
    FULL
}
