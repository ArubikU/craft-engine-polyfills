package dev.arubik.craftengine.conveyor;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure, Bukkit-free planning for two-point conveyor placement.
 *
 * <p>Given two integer block positions A (start) and B (end) the planner
 * validates that the run is a single straight horizontal axis (north/south or
 * east/west) and that any vertical change is a consistent 45-degree slope (each
 * step changes Y by exactly the same +/-1, or stays flat). It then produces the
 * ordered list of segments to place, each carrying its travel facing step, slope
 * and {@link ConveyorPart}.</p>
 *
 * <p>No Bukkit/NMS types are referenced so this is unit-testable in isolation.</p>
 */
public final class ConveyorPath {

    private ConveyorPath() {
    }

    /** One planned segment: absolute position, facing step (A->B), slope, part. */
    public static final class Step {
        public final int x;
        public final int y;
        public final int z;
        /** Travel direction unit step on X (one of -1,0,+1). */
        public final int stepX;
        /** Travel direction unit step on Z (one of -1,0,+1). */
        public final int stepZ;
        public final ConveyorSlope slope;
        public final ConveyorPart part;

        public Step(int x, int y, int z, int stepX, int stepZ, ConveyorSlope slope, ConveyorPart part) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.stepX = stepX;
            this.stepZ = stepZ;
            this.slope = slope;
            this.part = part;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Step s))
                return false;
            return x == s.x && y == s.y && z == s.z && stepX == s.stepX && stepZ == s.stepZ
                    && slope == s.slope && part == s.part;
        }

        @Override
        public int hashCode() {
            int h = x;
            h = 31 * h + y;
            h = 31 * h + z;
            h = 31 * h + stepX;
            h = 31 * h + stepZ;
            h = 31 * h + slope.hashCode();
            h = 31 * h + part.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return "Step[" + x + "," + y + "," + z + " step=(" + stepX + "," + stepZ + ") " + slope + " " + part + "]";
        }
    }

    /**
     * Result of planning: either an ordered step list or a failure. A failure carries
     * an English {@code error()} (tests/logs) and a client-translatable
     * {@code errorKey()} under {@code polyfill.wand.*} (the wand renders it as an
     * Adventure {@code Component.translatable} so the player sees their own language).
     */
    public static final class Result {
        private final List<Step> steps;
        private final String error;
        private final String errorKey;

        private Result(List<Step> steps, String error, String errorKey) {
            this.steps = steps;
            this.error = error;
            this.errorKey = errorKey;
        }

        public static Result ok(List<Step> steps) {
            return new Result(steps, null, null);
        }

        public static Result fail(String error, String errorKey) {
            return new Result(null, error, errorKey);
        }

        public boolean isValid() {
            return error == null;
        }

        public List<Step> steps() {
            return steps;
        }

        public String error() {
            return error;
        }

        /** The {@code polyfill.wand.*} lang key for this failure (null when valid). */
        public String errorKey() {
            return errorKey;
        }
    }

    /**
     * Plan a belt line from A to B.
     *
     * <p>Rules:
     * <ul>
     *   <li>A and B must differ.</li>
     *   <li>The horizontal run must be along exactly ONE axis: either dx==0 (Z
     *       run) or dz==0 (X run), not both non-zero and not both zero.</li>
     *   <li>The vertical change must be flat (dy==0) or a consistent 45-degree
     *       diagonal: |dy| == horizontal length, so every step moves Y by the
     *       same +/-1.</li>
     * </ul>
     *
     * @return a valid {@link Result} with one {@link Step} per block, or a fail
     *         result with a human-readable reason.
     */
    public static Result plan(int ax, int ay, int az, int bx, int by, int bz) {
        int dx = bx - ax;
        int dy = by - ay;
        int dz = bz - az;

        if (dx == 0 && dz == 0) {
            return Result.fail("Start and end must be different horizontal positions.",
                    "polyfill.wand.err_same_pos");
        }
        if (dx != 0 && dz != 0) {
            return Result.fail("Path must be a straight line along one axis (N/S or E/W).",
                    "polyfill.wand.err_not_straight");
        }

        int horiz = Math.abs(dx) + Math.abs(dz); // length along the single axis
        if (dy != 0 && Math.abs(dy) != horiz) {
            return Result.fail("Slope must be 45 degrees: Y change must equal the horizontal length.",
                    "polyfill.wand.err_bad_slope");
        }

        int stepX = Integer.signum(dx);
        int stepZ = Integer.signum(dz);
        int stepY = Integer.signum(dy);

        ConveyorSlope slope;
        if (stepY > 0)
            slope = ConveyorSlope.UP;
        else if (stepY < 0)
            slope = ConveyorSlope.DOWN;
        else
            slope = ConveyorSlope.FLAT;

        List<Step> steps = new ArrayList<>(horiz + 1);
        for (int i = 0; i <= horiz; i++) {
            int x = ax + stepX * i;
            int y = ay + stepY * i;
            int z = az + stepZ * i;
            ConveyorPart part;
            if (i == 0)
                part = ConveyorPart.START;
            else if (i == horiz)
                part = ConveyorPart.END;
            else
                part = ConveyorPart.MIDDLE;
            steps.add(new Step(x, y, z, stepX, stepZ, slope, part));
        }
        return Result.ok(steps);
    }
}
