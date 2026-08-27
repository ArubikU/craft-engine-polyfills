package dev.arubik.craftengine.conveyor.routing;

import dev.arubik.craftengine.conveyor.belt.ConveyorPart;
import dev.arubik.craftengine.conveyor.belt.ConveyorSlope;
import java.util.ArrayList;
import java.util.List;

public final class ConveyorPath {
    private ConveyorPath() {
    }

    public static Result plan(int ax, int ay, int az, int bx, int by, int bz) {
        int dx = bx - ax;
        int dy = by - ay;
        int dz = bz - az;
        if (dx == 0 && dz == 0) {
            return Result.fail("Start and end must be different horizontal positions.", "polyfill.wand.err_same_pos");
        }
        if (dx != 0 && dz != 0) {
            return Result.fail("Path must be a straight line along one axis (N/S or E/W).", "polyfill.wand.err_not_straight");
        }
        int horiz = Math.abs(dx) + Math.abs(dz);
        if (dy != 0 && Math.abs(dy) != horiz) {
            return Result.fail("Slope must be 45 degrees: Y change must equal the horizontal length.", "polyfill.wand.err_bad_slope");
        }
        int stepX = Integer.signum(dx);
        int stepZ = Integer.signum(dz);
        int stepY = Integer.signum(dy);
        ConveyorSlope slope = stepY > 0 ? ConveyorSlope.UP : (stepY < 0 ? ConveyorSlope.DOWN : ConveyorSlope.FLAT);
        ArrayList<Step> steps = new ArrayList<Step>(horiz + 1);
        for (int i = 0; i <= horiz; ++i) {
            int x = ax + stepX * i;
            int y = ay + stepY * i;
            int z = az + stepZ * i;
            ConveyorPart part = i == 0 ? ConveyorPart.START : (i == horiz ? ConveyorPart.END : ConveyorPart.MIDDLE);
            steps.add(new Step(x, y, z, stepX, stepZ, slope, part));
        }
        return Result.ok(steps);
    }

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
            return this.error == null;
        }

        public List<Step> steps() {
            return this.steps;
        }

        public String error() {
            return this.error;
        }

        public String errorKey() {
            return this.errorKey;
        }
    }

    public static final class Step {
        public final int x;
        public final int y;
        public final int z;
        public final int stepX;
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

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Step)) {
                return false;
            }
            Step s = (Step)o;
            return this.x == s.x && this.y == s.y && this.z == s.z && this.stepX == s.stepX && this.stepZ == s.stepZ && this.slope == s.slope && this.part == s.part;
        }

        public int hashCode() {
            int h = this.x;
            h = 31 * h + this.y;
            h = 31 * h + this.z;
            h = 31 * h + this.stepX;
            h = 31 * h + this.stepZ;
            h = 31 * h + this.slope.hashCode();
            h = 31 * h + this.part.hashCode();
            return h;
        }

        public String toString() {
            return "Step[" + this.x + "," + this.y + "," + this.z + " step=(" + this.stepX + "," + this.stepZ + ") " + String.valueOf(this.slope) + " " + String.valueOf(this.part) + "]";
        }
    }
}

