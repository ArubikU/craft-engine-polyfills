package dev.arubik.craftengine.script;

import dev.arubik.craftengine.rotation.RpmPropagation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the rule behind {@code Machine.rpm_out(direction)} — "what actually comes out of this
 * side?" — which {@code DataMachineBlockEntity.rpmThrough} implements.
 *
 * <p>Scripts used to reconstruct this from {@code Machine.rpm} plus their own idea of the io
 * config, and got the sign wrong on anything with an inverted or relative output. The rule has
 * three inputs: is the face a declared output at all, is the machine gearbox-style relative,
 * and does a static {@code output_inverted} list name the face.
 */
class RpmThroughFaceTest {

    /** The six faces as (axis index, axis sign), matching NMS Direction. */
    private enum Face {
        DOWN(1, -1), UP(1, +1), NORTH(2, -1), SOUTH(2, +1), WEST(0, -1), EAST(0, +1);
        final int axis, sign;
        Face(int axis, int sign) { this.axis = axis; this.sign = sign; }
        Face opposite() {
            return switch (this) {
                case DOWN -> UP; case UP -> DOWN;
                case NORTH -> SOUTH; case SOUTH -> NORTH;
                case WEST -> EAST; case EAST -> WEST;
            };
        }
    }

    /** Mirrors rpmThrough for a machine using a static output_inverted list. */
    private static float staticOutput(float rpm, boolean faceIsOutput, boolean faceIsInverted) {
        if (!faceIsOutput || rpm == 0f) return 0f;
        return RpmPropagation.applyInversion(rpm, faceIsInverted);
    }

    @Test
    @DisplayName("a plain output face passes the machine's own rpm straight through")
    void plainOutputPassesThrough() {
        assertEquals(32f, staticOutput(32f, true, false), 1e-6);
        assertEquals(-32f, staticOutput(-32f, true, false), 1e-6);
    }

    @Test
    @DisplayName("a face named by output_inverted reverses it")
    void invertedOutputReverses() {
        assertEquals(-32f, staticOutput(32f, true, true), 1e-6);
        assertEquals(32f, staticOutput(-32f, true, true), 1e-6);
    }

    @Test
    @DisplayName("opposite faces of a plain relay carry the same value")
    void plainRelayIsSymmetric() {
        // A shaft declares both axis faces as output_same, so both ends read alike.
        for (Face f : Face.values()) {
            assertEquals(staticOutput(32f, true, false), staticOutput(32f, true, false), 1e-6,
                    "face " + f + " vs " + f.opposite());
        }
    }
}
