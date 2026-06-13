package dev.arubik.craftengine.conveyor;

/**
 * Vertical profile of a single conveyor segment. 45-degree only.
 *
 * <ul>
 *   <li>{@link #FLAT} – exit at the same Y as the entry.</li>
 *   <li>{@link #UP}   – exit is one block higher (Y ramps +1 across the segment).</li>
 *   <li>{@link #DOWN} – exit is one block lower (Y ramps -1 across the segment).</li>
 * </ul>
 *
 * <p>Read from the CraftEngine block property {@code "slope"} by its value name;
 * {@link #fromName(String)} is lenient and falls back to {@link #FLAT}.</p>
 */
public enum ConveyorSlope {
    FLAT(0),
    UP(1),
    DOWN(-1);

    private final int stepY;

    ConveyorSlope(int stepY) {
        this.stepY = stepY;
    }

    /** The Y delta of the exit block relative to this segment (-1, 0 or +1). */
    public int stepY() {
        return stepY;
    }

    /** Lenient parse of a property value name; unknown/null -&gt; {@link #FLAT}. */
    public static ConveyorSlope fromName(String name) {
        if (name == null)
            return FLAT;
        switch (name.trim().toUpperCase()) {
            case "UP":
                return UP;
            case "DOWN":
                return DOWN;
            default:
                return FLAT;
        }
    }
}
