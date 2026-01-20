package dev.arubik.craftengine.multiblock;

/**
 * Relative direction for machine I/O configuration
 * Independent of actual world direction - transforms based on machine facing
 */
public enum RelativeDirection {
    /** Front of the machine (facing direction) */
    FRONT,
    /** Back of the machine (opposite of facing) */
    BACK,
    /** Left side of the machine (counterclockwise from facing) */
    LEFT,
    /** Right side of the machine (clockwise from facing) */
    RIGHT,
    /** Top of the machine */
    UP,
    /** Bottom of the machine */
    DOWN;

    /**
     * Check if this is a horizontal relative direction
     */
    public boolean isHorizontal() {
        return this == FRONT || this == BACK || this == LEFT || this == RIGHT;
    }

    /**
     * Check if this is a vertical relative direction
     */
    public boolean isVertical() {
        return this == UP || this == DOWN;
    }
}
