package dev.arubik.craftengine.script;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runs the real {@code windmill.pf} against a mock contraption.
 *
 * <p>Pins Create's {@code WindmillBearingBlockEntity.getGeneratedSpeed}:
 * <pre>
 *   int sails = sailBlocks / windmillSailsPerRPM;   // 8
 *   return Mth.clamp(sails, 1, 16) * direction;
 * </pre>
 * and the defect that stopped it working at all: the script counted sails through
 * {@code contraption.contraption_world.blocks()}, but ContraptionWorld has no {@code blocks()} —
 * it lives on Contraption — so the call returned null, the count stayed 0 and the bearing never
 * produced any rpm.
 */
class WindmillScriptTest {

    private static final Logger LOG = Logger.getLogger("test");
    private static final Path SCRIPT = Path.of("src/main/resources/scripts/windmill.pf");

    record FakeBlock(String id) {}

    static final Map<String, Integer> flags = new HashMap<>();
    static final List<ScriptValue> contraptionBlocks = new ArrayList<>();
    static boolean contraptionExists = true;
    static Double rpmOut = null;
    static Double suOut = null;
    static Double contraptionRpm = null;
    static String spinAxis = null;
    static int facingDx = 0, facingDy = 1, facingDz = 0;
    /** Whether the script ever reached ContraptionWorld — it must not; blocks() is on Contraption. */
    static boolean touchedContraptionWorld = false;

    @BeforeAll
    static void registerTypes() {
        PolyTypeRegistry.define("Block")
            .property("id", o -> ScriptValue.of(((FakeBlock) o).id()))
            .property("is_air", o -> ScriptValue.of(((FakeBlock) o).id().isEmpty()));

        PolyTypeRegistry.define("ContraptionWorld")
            .method("blocks", (o, a) -> {
                touchedContraptionWorld = true;
                return ScriptValue.NULL;   // exactly what the real type does: no such method
            });

        PolyTypeRegistry.define("Contraption")
            .property("contraption_world", o -> ScriptValue.ofObj("ContraptionWorld", new Object()))
            .property("uuid", o -> ScriptValue.of("test-uuid"))
            .method("blocks", (o, a) -> new ScriptValue.Array(new ArrayList<>(contraptionBlocks)))
            .method("set_rpm", (o, a) -> { fail("set_rpm only records a number; the bearing must drive rotation directly"); return ScriptValue.of(false); })
            .method("set_spin", (o, a) -> {
                spinAxis = a.get(0).asStr();
                contraptionRpm = a.get(1).asNum();
                return ScriptValue.of(true);
            })
            .method("kill", (o, a) -> ScriptValue.of(true));

        PolyTypeRegistry.define("Machine")
            .property("contraption", o -> contraptionExists
                    ? ScriptValue.ofObj("Contraption", new Object()) : ScriptValue.NULL)
            .property("facing_dx", o -> ScriptValue.of(facingDx))
            .property("facing_dy", o -> ScriptValue.of(facingDy))
            .property("facing_dz", o -> ScriptValue.of(facingDz))
            // windmill.pf now calls Machine.get_typed/set_typed(key, type, ...) instead of the old
            // get_flag/set_flag (+get_str_flag/set_str_flag) pairs. "int" routes to the same backing
            // map get_flag/set_flag use; "string" mirrors the existing no-op str-flag stub above
            // (this test never asserts on the string values, only the numeric ones).
            .method("get_typed", (o, a) -> "string".equals(a.get(1).asStr())
                    ? ScriptValue.of("") : ScriptValue.of(flags.getOrDefault(a.get(0).asStr(), 0)))
            .method("set_typed", (o, a) -> {
                if (!"string".equals(a.get(1).asStr())) flags.put(a.get(0).asStr(), (int) a.get(2).asNum());
                return ScriptValue.of(true);
            })
            .method("set_state", (o, a) -> ScriptValue.of(true))
            .method("set_rpm_output", (o, a) -> { rpmOut = a.get(0).asNum(); return ScriptValue.of(true); })
            .method("report_su", (o, a) -> { suOut = a.get(0).asNum(); return ScriptValue.of(true); });
    }

    @BeforeEach
    void reset() {
        flags.clear();
        contraptionBlocks.clear();
        contraptionExists = true;
        rpmOut = null;
        suOut = null;
        contraptionRpm = null;
        touchedContraptionWorld = false;
        spinAxis = null;
        facingDx = 0; facingDy = 1; facingDz = 0;
        flags.put("assembled", 1);
    }

    private void sails(int n) {
        for (int i = 0; i < n; i++) contraptionBlocks.add(ScriptValue.ofObj("Block", new FakeBlock("cml:windmill_sail")));
    }

    private void other(int n) {
        for (int i = 0; i < n; i++) contraptionBlocks.add(ScriptValue.ofObj("Block", new FakeBlock("minecraft:oak_planks")));
    }

    private void run() throws Exception {
        ScriptProgram prog = ScriptProgram.parse("windmill", Files.readString(SCRIPT), LOG);
        prog.evaluate(ScriptContext.builder().typed("Machine", new Object()).num("rpm", 0).build());
    }

    private double rpm() {
        assertNotNull(rpmOut, "the bearing must always report an rpm");
        return rpmOut;
    }

    @Test
    @DisplayName("sails are counted through Contraption.blocks(), not ContraptionWorld")
    void countsSailsOnTheRightType() throws Exception {
        sails(16);
        run();
        assertFalse(touchedContraptionWorld,
                "ContraptionWorld has no blocks(); going through it is what made the count always 0");
        assertTrue(rpm() != 0, "a 16-sail windmill must turn, got " + rpm());
    }

    @Test
    @DisplayName("eight sails give exactly 1 rpm, not 64")
    void eightSailsGiveOneRpm() throws Exception {
        // Create: sails / 8, clamped to [1,16]. The old script did sails * 8.
        sails(8);
        run();
        assertEquals(1.0, rpm(), 1e-6);
    }

    @Test
    @DisplayName("speed scales with sail count, one rpm per eight sails")
    void speedScalesWithSails() throws Exception {
        sails(32);
        run();
        assertEquals(4.0, rpm(), 1e-6);
    }

    @Test
    @DisplayName("speed is capped at 16 rpm")
    void speedIsCapped() throws Exception {
        sails(1000);
        run();
        assertEquals(16.0, rpm(), 1e-6, "Mth.clamp(sails, 1, 16)");
    }

    @Test
    @DisplayName("too few sails and the windmill does not turn")
    void tooFewSailsDoesNotTurn() throws Exception {
        sails(7);
        run();
        assertEquals(0.0, rpm(), 1e-6);
        assertEquals(0.0, suOut, 1e-6, "and it must not claim to generate stress either");
        assertEquals(0.0, contraptionRpm, 1e-6, "the contraption itself must be stopped too");
    }

    @Test
    @DisplayName("only sail blocks count toward the speed")
    void onlySailsCount() throws Exception {
        sails(8);
        other(500);
        run();
        assertEquals(1.0, rpm(), 1e-6, "planks are structure, not sail area");
    }

    @Test
    @DisplayName("the rotation direction flag reverses the output")
    void directionFlagReverses() throws Exception {
        sails(32);
        flags.put("windmill_dir", -1);
        run();
        assertEquals(-4.0, rpm(), 1e-6);
    }

    @Test
    @DisplayName("an unset direction defaults to clockwise rather than stopping")
    void unsetDirectionDefaultsClockwise() throws Exception {
        sails(32);
        flags.remove("windmill_dir");
        run();
        assertEquals(4.0, rpm(), 1e-6, "flag 0 must not mean zero rpm");
    }

    @Test
    @DisplayName("a turning windmill drives the contraption at the same speed")
    void contraptionTurnsWithIt() throws Exception {
        sails(24);
        run();
        assertNotNull(contraptionRpm);
        assertEquals(rpm(), contraptionRpm, 1e-6);
    }

    @Test
    @DisplayName("a turning windmill generates stress capacity")
    void generatesCapacity() throws Exception {
        sails(32);
        run();
        assertNotNull(suOut);
        assertTrue(suOut < 0, "generated capacity is reported negative, got " + suOut);
    }

    @Test
    @DisplayName("an unassembled bearing produces nothing")
    void unassembledIsIdle() throws Exception {
        flags.put("assembled", 0);
        sails(64);
        run();
        assertEquals(0.0, rpm(), 1e-6);
        assertEquals(0.0, suOut, 1e-6);
    }

    @Test
    @DisplayName("a lost contraption resets the bearing instead of spinning on")
    void lostContraptionResets() throws Exception {
        contraptionExists = false;
        run();
        assertEquals(0.0, rpm(), 1e-6);
        assertEquals(0, flags.get("assembled"), "the bearing must forget it was assembled");
    }

    @Test
    @DisplayName("the bearing turns about the axis it faces, not always flat")
    void spinsAboutItsFacingAxis() throws Exception {
        sails(32);
        facingDx = 0; facingDy = 1; facingDz = 0;   // mounted on the ground, facing up
        run();
        assertEquals("y", spinAxis, "a bearing facing up turns flat");

        reset();
        sails(32);
        facingDx = 1; facingDy = 0; facingDz = 0;   // on a wall, facing east
        run();
        assertEquals("x", spinAxis, "a wall-mounted windmill has to turn like a wheel");

        reset();
        sails(32);
        facingDx = 0; facingDy = 0; facingDz = -1;  // facing north
        run();
        assertEquals("z", spinAxis);
    }

    @Test
    @DisplayName("status follows rpm")
    void statusFollowsRpm() throws Exception {
        assertEquals("true", status(4));
        assertEquals("false", status(0));
    }

    private String status(double rpm) throws Exception {
        ScriptProgram prog = ScriptProgram.parse("windmill", Files.readString(SCRIPT), LOG);
        ScriptContext ctx = prog.evaluate(
                ScriptContext.builder().typed("Machine", new Object()).num("rpm", rpm).build());
        UserFunction f = (UserFunction) ((ScriptValue.Obj) ctx.getVar("status")).instance();
        return f.call(List.of(), ctx).asStr();
    }
}
