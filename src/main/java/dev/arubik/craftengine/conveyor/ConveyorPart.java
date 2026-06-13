package dev.arubik.craftengine.conveyor;

/**
 * Position of a segment within a belt's linked list.
 *
 * <ul>
 *   <li>{@link #START} – the upstream-most segment (no {@code prevPos}).</li>
 *   <li>{@link #MIDDLE} – an interior segment.</li>
 *   <li>{@link #END} – the growth / exit point of the belt.</li>
 * </ul>
 *
 * <p>Read from the CraftEngine block property {@code "part"} by its value name;
 * {@link #fromName(String)} is lenient and falls back to {@link #END} (a lone
 * single-segment belt is its own END).</p>
 */
public enum ConveyorPart {
    START,
    MIDDLE,
    END;

    /** Lenient parse of a property value name; unknown/null -&gt; {@link #END}. */
    public static ConveyorPart fromName(String name) {
        if (name == null)
            return END;
        switch (name.trim().toUpperCase()) {
            case "START":
                return START;
            case "MIDDLE":
                return MIDDLE;
            default:
                return END;
        }
    }
}
