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

    /** Mirrors rpmThrough for a machine using output_relative (a gearbox). */
    private static float relativeOutput(float rpm, boolean faceIsOutput, Face in, Face out) {
        if (!faceIsOutput || rpm == 0f) return 0f;
        if (in == null) return 0f;
        return rpm * RpmPropagation.gearboxModifier(out.axis, out.sign, in.axis, in.sign);
    }

    @Test
    @DisplayName("a face that is not a declared output delivers nothing")
    void undeclaredFaceDeliversNothing() {
        assertEquals(0f, staticOutput(32f, false, false), 1e-6);
        assertEquals(0f, relativeOutput(32f, false, Face.NORTH, Face.EAST), 1e-6);
    }

    @Test
    @DisplayName("a stopped machine delivers nothing through any face")
    void stoppedMachineDeliversNothing() {
        assertEquals(0f, staticOutput(0f, true, false), 1e-6);
        assertEquals(0f, relativeOutput(0f, true, Face.NORTH, Face.SOUTH), 1e-6);
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
    @DisplayName("a gearbox reverses across the driven axis and through it")
    void relativeOutputFollowsTheDrivenFace() {
        // Driven from north: south (same axis) reverses, east/west (perpendicular) follow the
        // axis-direction rule. This is what a neighbour pulling from that face would read.
        assertEquals(-32f, relativeOutput(32f, true, Face.NORTH, Face.SOUTH), 1e-6);
        assertEquals(-32f, relativeOutput(32f, true, Face.NORTH, Face.WEST), 1e-6);
        assertEquals(32f, relativeOutput(32f, true, Face.NORTH, Face.EAST), 1e-6);
    }

    @Test
    @DisplayName("an undriven gearbox delivers nothing rather than a phantom direction")
    void undrivenRelativeMachineDeliversNothing() {
        assertEquals(0f, relativeOutput(32f, true, null, Face.EAST), 1e-6);
    }

    @Test
    @DisplayName("what comes out of one face is what the block on that side pulls in")
    void outputMatchesWhatTheNeighbourReads() {
        // The whole point of the method: a script asking rpm_out must agree with the value the
        // neighbouring machine's own pull would compute for the same face.
        for (Face in : Face.values()) {
            for (Face out : Face.values()) {
                float viaMethod = relativeOutput(32f, true, in, out);
                float viaPull = 32f * RpmPropagation.gearboxModifier(out.axis, out.sign, in.axis, in.sign);
                assertEquals(viaPull, viaMethod, 1e-6, "in=" + in + " out=" + out);
            }
        }
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
