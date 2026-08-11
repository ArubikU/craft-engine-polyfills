package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.player.PlayerCarry;
import java.util.*;

/** Physics collision logic for ContraptionHitboxElement (carry, push, side-collision). */
public final class ContraptionHitboxCollision {

    private static final double STEP_DISTANCE = 2.0;
    private static final double FALL_MIN_SPEED = 0.35;
    private static final double FLOOR_CLEARANCE = 0.35;
    private static final double STATIC_DELTA_SQ = 0.02 * 0.02;
    private static final double RIDER_HALF_WIDTH = 0.3;
    private static final double RIDER_HEIGHT = 1.8;

    private final Set<UUID> currentRiders = new HashSet<>();
    private final Map<UUID, double[]> stepState = new HashMap<>();

    public Set<UUID> currentRiderIds() {
        return Collections.unmodifiableSet(currentRiders);
    }

    void releaseAll() {
        for (UUID id : currentRiders) {
            PlayerCarry.release(id);
        }
        currentRiders.clear();
    }
}
