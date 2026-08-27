package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.types.primitive.MapType;
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
 * Drives the real {@code cogwheel.pf} against a mock Machine and records every {@code relay_to}
 * call, so the gear-ratio contract is pinned without a server.
 *
 * <p>Reported symptom was "small and large cogs do not transmit in any way". Two separate defects
 * fed it — this class covers the script half (which neighbour gets driven, at what ratio); the
 * Java half (relay hop distance) is covered by {@link RelayDistanceTest}.
 */
class CogwheelTransmissionTest {

    private static final Logger LOG = Logger.getLogger("test");
    private static final Path SCRIPT = Path.of("src/main/resources/scripts/kinetics/shafts/cogwheel.pf");

    /** One recorded Machine.relay_to(block, rpm) call. */
    record Relay(String neighborId, double rpm) {}

    static final List<Relay> relays = new ArrayList<>();
    static final Map<String, Integer> flags = new HashMap<>();
    static String axis = "y";
    static double rpmOut = 0;
    /** Neighbours keyed by offset "dx,dy,dz". */
    static final Map<String, ScriptValue> neighbors = new HashMap<>();

    /** Stand-in for a neighbouring block: has an id and an "axis" property. */
    record FakeBlock(String id, String axis) {}

    private static ScriptValue block(String id, String axis) {
        return ScriptValue.ofObj("Block", new FakeBlock(id, axis));
    }

    @BeforeAll
    static void registerTypes() {
        if (PolyTypeRegistry.get("Map") == null) MapType.register();

        PolyTypeRegistry.define("Block")
            .property("id", o -> ScriptValue.of(((FakeBlock) o).id()))
            .property("is_air", o -> ScriptValue.of(((FakeBlock) o).id().isEmpty()))
            .method("property", (o, a) -> {
                if (a.isEmpty()) return ScriptValue.NULL;
                return "axis".equals(a.get(0).asStr())
                        ? ScriptValue.of(((FakeBlock) o).axis())
                        : ScriptValue.NULL;
            });

        PolyTypeRegistry.define("Machine")
            .property("axis", o -> ScriptValue.of(axis))
            .property("rpm_network", o -> ScriptValue.of(1))
            .method("block_at", (o, a) -> {
                String key = (int) a.get(0).asNum() + "," + (int) a.get(1).asNum() + "," + (int) a.get(2).asNum();
                return neighbors.getOrDefault(key, block("", "none"));
            })
            .method("relay_to", (o, a) -> {
                if (a.size() < 2) return ScriptValue.of(false);
                ScriptValue bv = a.get(0);
                String id = bv instanceof ScriptValue.Obj ob && ob.instance() instanceof FakeBlock fb ? fb.id() : "?";
                relays.add(new Relay(id, a.get(1).asNum()));
                return ScriptValue.of(true);
            })
            .method("set_rpm_output", (o, a) -> { rpmOut = a.isEmpty() ? 0 : a.get(0).asNum(); return ScriptValue.of(true); })
            .method("report_su", (o, a) -> ScriptValue.of(true))
            .method("set_state", (o, a) -> ScriptValue.of(true));
    }

    @BeforeEach
    void reset() {
        relays.clear();
        flags.clear();
        neighbors.clear();
        axis = "y";
        rpmOut = 0;
    }

    private ScriptContext run(String fn, double rpm) throws Exception {
        ScriptProgram prog = ScriptProgram.parse("cogwheel", Files.readString(SCRIPT), LOG);
        ScriptContext ctx = prog.evaluate(ScriptContext.builder()
                .typed("Machine", new Object())
                .num("rpm", rpm)
                .build());
        ScriptValue fnVal = ctx.getVar(fn);
        assertInstanceOf(ScriptValue.Obj.class, fnVal, fn + " must be defined in cogwheel.pf");
        UserFunction f = (UserFunction) ((ScriptValue.Obj) fnVal).instance();
        f.call(List.of(), ctx);
        return ctx;
    }

    /** For axis=y the meshing neighbours are the four horizontal ones. */
    private void putHorizontal(String id, String neighborAxis) {
        neighbors.put("1,0,0", block(id, neighborAxis));
    }

    private void put(int dx, int dy, int dz, String id, String neighborAxis) {
        neighbors.put(dx + "," + dy + "," + dz, block(id, neighborAxis));
    }

    private static final String SMALL = "cml:cogwheel_small";
    private static final String LARGE = "cml:cogwheel_large";

    @Test
    @DisplayName("cogwheel.pf parses and exposes its entry points")
    void scriptParses() throws Exception {
        ScriptProgram prog = ScriptProgram.parse("cogwheel", Files.readString(SCRIPT), LOG);
        ScriptContext ctx = prog.evaluate(ScriptContext.builder().typed("Machine", new Object()).num("rpm", 0).build());
        for (String fn : List.of("tick_large", "tick_small", "on_break", "status")) {
            assertInstanceOf(ScriptValue.Obj.class, ctx.getVar(fn), fn + " must be defined");
        }
    }

    @Test
    @DisplayName("small -> small meshes 1:1 and reverses direction")
    void smallToSmallInverts() throws Exception {
        putHorizontal("cml:cogwheel_small", "y");
        run("tick_small", 32);

        assertEquals(1, relays.size(), "exactly one neighbour should be driven, got " + relays);
        assertEquals(-32.0, relays.get(0).rpm(), 1e-6, "meshed gears spin opposite ways at 1:1");
    }

    @Test
    @DisplayName("small -> large meshes DIAGONALLY in-plane, at half speed reversed")
    void smallToLargeHalves() throws Exception {
        // isLargeToSmallCog: on-axis offset 0, both off-axis components +-1.
        put(1, 0, 1, LARGE, "y");
        run("tick_small", 32);

        assertEquals(1, relays.size(), "expected one relay, got " + relays);
        assertEquals(-16.0, relays.get(0).rpm(), 1e-6, "small driving large halves the speed");
    }

    @Test
    @DisplayName("large -> small meshes DIAGONALLY in-plane, at double speed reversed")
    void largeToSmallDoubles() throws Exception {
        put(1, 0, 1, SMALL, "y");
        run("tick_large", 32);

        assertEquals(1, relays.size(), "expected one relay, got " + relays);
        assertEquals(-64.0, relays.get(0).rpm(), 1e-6, "large driving small doubles the speed");
    }

    @Test
    @DisplayName("large -> small does NOT mesh orthogonally")
    void largeToSmallIsNotOrthogonal() throws Exception {
        // The old Java attempt applied the large ratios to orthogonal neighbours, which is not
        // how these mesh; only the in-plane diagonal counts.
        putHorizontal(SMALL, "y");
        run("tick_large", 32);

        assertTrue(relays.isEmpty(),
                "an orthogonally adjacent small cog must not mesh with a large one, got " + relays);
    }

    @Test
    @DisplayName("all four in-plane diagonals mesh for a large cog")
    void largeMeshesAllFourDiagonals() throws Exception {
        for (int[] d : new int[][]{{1, 0, 1}, {1, 0, -1}, {-1, 0, 1}, {-1, 0, -1}}) {
            put(d[0], d[1], d[2], SMALL, "y");
        }
        run("tick_large", 16);

        assertEquals(4, relays.size(), "every in-plane diagonal small cog is driven, got " + relays);
        for (Relay r : relays) assertEquals(-32.0, r.rpm(), 1e-6);
    }

    @Test
    @DisplayName("large <-> large meshes across DIFFERENT axes, diagonally, at 1:1")
    void largeToLargeMeshesCrossAxis() throws Exception {
        // isLargeToLargeGear: axes differ, offset non-zero on both axes, zero on the third.
        // ours = y, theirs = x, offset (1,1,0): both components positive -> same sign -> +1.
        axis = "y";
        put(1, 1, 0, LARGE, "x");
        run("tick_large", 32);

        assertEquals(1, relays.size(), "expected one relay, got " + relays);
        assertEquals(32.0, relays.get(0).rpm(), 1e-6, "matching offset signs keep the direction");
    }

    @Test
    @DisplayName("large <-> large reverses when the two offset components disagree in sign")
    void largeToLargeSignFollowsOffsets() throws Exception {
        axis = "y";
        put(-1, 1, 0, LARGE, "x");   // ours(y)=+1, theirs(x)=-1 -> differing signs -> -1
        run("tick_large", 32);

        assertEquals(1, relays.size(), "expected one relay, got " + relays);
        assertEquals(-32.0, relays.get(0).rpm(), 1e-6, "differing offset signs reverse the direction");
    }

    @Test
    @DisplayName("large <-> large on the SAME axis does not mesh")
    void largeToLargeSameAxisIsInvalid() throws Exception {
        axis = "y";
        put(1, 1, 0, LARGE, "y");
        run("tick_large", 32);

        assertTrue(relays.isEmpty(), "the two large cogs must be on different axes, got " + relays);
    }

    @Test
    @DisplayName("large <-> large orthogonally adjacent does not mesh")
    void largeToLargeOrthogonalIsInvalid() throws Exception {
        axis = "y";
        putHorizontal(LARGE, "x");   // offset (1,0,0): zero along our own axis
        run("tick_large", 32);

        assertTrue(relays.isEmpty(), "the offset must be non-zero along BOTH axes, got " + relays);
    }

    @Test
    @DisplayName("a small cog ignores the cross-axis diagonals reserved for large cogs")
    void smallIgnoresCrossAxisDiagonals() throws Exception {
        axis = "y";
        put(1, 1, 0, LARGE, "x");
        run("tick_small", 32);

        assertTrue(relays.isEmpty(), "only large cogs mesh across axes, got " + relays);
    }

    @Test
    @DisplayName("small -> small does NOT mesh across the in-plane diagonal")
    void smallToSmallIsNotDiagonal() throws Exception {
        put(1, 0, 1, SMALL, "y");
        run("tick_small", 32);

        assertTrue(relays.isEmpty(), "small cogs only mesh orthogonally, got " + relays);
    }

    @Test
    @DisplayName("a stopped cog drives nothing")
    void stoppedCogDrivesNothing() throws Exception {
        putHorizontal(SMALL, "y");
        run("tick_small", 0);

        assertTrue(relays.isEmpty(), "a cog at rest must not relay, got " + relays);
    }

    @Test
    @DisplayName("a neighbour on a different axis is not meshed")
    void mismatchedAxisIsSkipped() throws Exception {
        putHorizontal("cml:cogwheel_small", "x");   // our axis is y
        run("tick_small", 32);

        assertTrue(relays.isEmpty(), "cogs on crossing axes must not mesh, got " + relays);
    }

    @Test
    @DisplayName("a non-cogwheel neighbour is ignored")
    void nonCogNeighborIgnored() throws Exception {
        putHorizontal("minecraft:stone", "y");
        run("tick_small", 32);

        assertTrue(relays.isEmpty(), "only cogwheels mesh, got " + relays);
    }

    @Test
    @DisplayName("all four perpendicular neighbours are meshed for a y-axis cog")
    void allFourPerpendicularNeighborsDriven() throws Exception {
        axis = "y";
        for (String k : List.of("1,0,0", "-1,0,0", "0,0,1", "0,0,-1")) {
            neighbors.put(k, block("cml:cogwheel_small", "y"));
        }
        run("tick_small", 16);

        assertEquals(4, relays.size(), "a y-axis cog meshes with all four horizontal neighbours");
        for (Relay r : relays) assertEquals(-16.0, r.rpm(), 1e-6);
    }

    @Test
    @DisplayName("the cog re-emits its own rpm so downstream pulls see it")
    void cogReEmitsItsRpm() throws Exception {
        run("tick_small", 48);
        assertEquals(48.0, rpmOut, 1e-6, "set_rpm_output must carry the pulled rpm through");
    }

    @Test
    @DisplayName("status reports activation straight from rpm, with no latching flag")
    void statusFollowsRpm() throws Exception {
        assertEquals("true", callStatus(32), "spinning cog is activated");
        assertEquals("false", callStatus(0), "stopped cog is not activated");
        // The old code latched on an NBT flag that could drift from the real block state; make
        // sure status is a pure function of rpm so it always self-heals.
        assertTrue(flags.isEmpty(), "status must not persist any latching flag, got " + flags);
    }

    private String callStatus(double rpm) throws Exception {
        ScriptContext ctx = run("status", rpm);
        ScriptValue fnVal = ctx.getVar("status");
        UserFunction f = (UserFunction) ((ScriptValue.Obj) fnVal).instance();
        return f.call(List.of(), ctx).asStr();
    }
}
