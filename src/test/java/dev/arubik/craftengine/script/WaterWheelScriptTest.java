package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.types.primitive.MapType;
import dev.arubik.craftengine.script.types.primitive.VectorType;
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
 * Runs the real {@code water_wheel.pf} against a mock world.
 *
 * <p>Pins Create's {@code determineAndApplyFlowScore} / {@code getGeneratedSpeed} contract, and
 * the three ways the previous implementation diverged from it: it summed a weighted per-face
 * contribution instead of counting direction votes, it awarded full speed to STILL water, and it
 * read {@code Machine.property("axis")} — a method that does not exist on Machine — so every
 * wheel silently scanned the Z plane no matter how it was placed.
 */
class WaterWheelScriptTest {

    private static final Logger LOG = Logger.getLogger("test");
    private static final Path SCRIPT = Path.of("src/main/resources/scripts/water_wheel.pf");

    /** A fluid at some offset: flow vector, or null for "no fluid here". */
    record Fluid(double x, double y, double z) {}

    static final Map<String, Fluid> world = new HashMap<>();
    static final Set<String> probed = new LinkedHashSet<>();
    static String axis = "y";
    static Double rpmOut = null;
    static Double suOut = null;

    /** Stand-in for a Block with the fluid properties water_wheel.pf reads. */
    record FakeBlock(Fluid fluid) {}

    @BeforeAll
    static void registerTypes() {
        if (PolyTypeRegistry.get("Map") == null) MapType.register();
        if (PolyTypeRegistry.get("Vector") == null) VectorType.register();

        PolyTypeRegistry.define("Block")
            .property("has_fluid", o -> ScriptValue.of(((FakeBlock) o).fluid() != null))
            .property("fluid_flow", o -> {
                Fluid f = ((FakeBlock) o).fluid();
                return f == null ? VectorType.wrap(0, 0, 0) : VectorType.wrap(f.x(), f.y(), f.z());
            });

        PolyTypeRegistry.define("Machine")
            .property("axis", o -> ScriptValue.of(axis))
            .method("block_at", (o, a) -> {
                String key = (int) a.get(0).asNum() + "," + (int) a.get(1).asNum() + "," + (int) a.get(2).asNum();
                probed.add(key);
                return ScriptValue.ofObj("Block", new FakeBlock(world.get(key)));
            })
            .method("set_rpm_output", (o, a) -> { rpmOut = a.get(0).asNum(); return ScriptValue.of(true); })
            .method("report_su", (o, a) -> { suOut = a.get(0).asNum(); return ScriptValue.of(true); });
    }

    @BeforeEach
    void reset() {
        world.clear();
        probed.clear();
        axis = "y";
        rpmOut = null;
        suOut = null;
    }

    private void water(int dx, int dy, int dz, double fx, double fy, double fz) {
        world.put(dx + "," + dy + "," + dz, new Fluid(fx, fy, fz));
    }

    private void run(String fn) throws Exception {
        ScriptProgram prog = ScriptProgram.parse("water_wheel", Files.readString(SCRIPT), LOG);
        ScriptContext ctx = prog.evaluate(ScriptContext.builder()
                .typed("Machine", new Object())
                .num("rpm", 0)
                .build());
        ScriptValue fnVal = ctx.getVar(fn);
        assertInstanceOf(ScriptValue.Obj.class, fnVal, fn + " must be defined in water_wheel.pf");
        ((UserFunction) ((ScriptValue.Obj) fnVal).instance()).call(List.of(), ctx);
    }

    private double rpm() {
        assertNotNull(rpmOut, "the wheel must always report an rpm, even when it is zero");
        return rpmOut;
    }

    // ---------------------------------------------------------------- basics

    @Test
    @DisplayName("the script exposes its entry points")
    void entryPoints() throws Exception {
        ScriptProgram prog = ScriptProgram.parse("water_wheel", Files.readString(SCRIPT), LOG);
        ScriptContext ctx = prog.evaluate(ScriptContext.builder()
                .typed("Machine", new Object()).num("rpm", 0).build());
        for (String fn : List.of("tick_small", "tick_large", "status", "on_break")) {
            assertInstanceOf(ScriptValue.Obj.class, ctx.getVar(fn), fn + " must be defined");
        }
    }

    @Test
    @DisplayName("dry wheel produces nothing")
    void dryWheelIsIdle() throws Exception {
        run("tick_small");
        assertEquals(0.0, rpm(), 1e-6);
        assertEquals(0.0, suOut, 1e-6, "an idle wheel must not claim to generate stress");
    }

    // ------------------------------------------------------------- direction

    @Test
    @DisplayName("tangential flow turns the wheel forward at exactly 8 rpm")
    void tangentialFlowDrivesForward() throws Exception {
        // axis=y, offset +X: normal (1,0,0); positiveMotion = rotate(normal,90,Y) = (0,0,-1).
        // A flow along -Z is therefore fully aligned with positive rotation.
        water(1, 0, 0, 0, 0, -1);
        run("tick_small");
        assertEquals(8.0, rpm(), 1e-6);
    }

    @Test
    @DisplayName("reversed flow turns the wheel backward")
    void reversedFlowDrivesBackward() throws Exception {
        water(1, 0, 0, 0, 0, 1);
        run("tick_small");
        assertEquals(-8.0, rpm(), 1e-6);
    }

    @Test
    @DisplayName("STILL water does nothing — this is the big behavioural fix")
    void stillWaterDoesNothing() throws Exception {
        // A source block with no slope has a zero flow vector. The previous implementation
        // awarded a full +32 for any source block, so a wheel dropped in a pond span at speed.
        water(1, 0, 0, 0, 0, 0);
        water(-1, 0, 0, 0, 0, 0);
        water(0, 0, 1, 0, 0, 0);
        water(0, 0, -1, 0, 0, 0);
        run("tick_small");
        assertEquals(0.0, rpm(), 1e-6, "still water must not drive the wheel");
    }

    @Test
    @DisplayName("radial flow does nothing — it pushes at the hub, not around it")
    void radialFlowDoesNothing() throws Exception {
        water(1, 0, 0, 1, 0, 0);
        run("tick_small");
        assertEquals(0.0, rpm(), 1e-6);
    }

    @Test
    @DisplayName("flow along the rotation axis is projected out")
    void axialFlowIsIgnored() throws Exception {
        axis = "y";
        water(1, 0, 0, 0, 1, 0);   // straight up, parallel to the axis
        run("tick_small");
        assertEquals(0.0, rpm(), 1e-6, "the axis component cannot turn the wheel");
    }

    @Test
    @DisplayName("a weakly aligned flow does not count")
    void weakAlignmentIsRejected() throws Exception {
        // dot must exceed 0.5; a 45-degree flow gives ~0.707 (counts), so use a shallower one.
        water(1, 0, 0, 0.97, 0, -0.24);   // mostly radial
        run("tick_small");
        assertEquals(0.0, rpm(), 1e-6);
    }

    // ------------------------------------------------------------- magnitude

    @Test
    @DisplayName("more water does NOT mean more speed")
    void speedDoesNotScaleWithWaterCount() throws Exception {
        // Every face pushing the same way: Create still yields exactly 8, because flowScore is
        // clamped to [-1,1]. The previous version summed 4 x 32 and returned 128.
        water(1, 0, 0, 0, 0, -1);
        water(-1, 0, 0, 0, 0, 1);
        water(0, 0, 1, 1, 0, 0);
        water(0, 0, -1, -1, 0, 0);
        run("tick_small");
        assertEquals(8.0, rpm(), 1e-6, "flowScore only picks a direction, it is not a throttle");
    }

    @Test
    @DisplayName("opposing flows cancel out")
    void opposingFlowsCancel() throws Exception {
        water(1, 0, 0, 0, 0, -1);   // +1
        water(-1, 0, 0, 0, 0, -1);  // -1
        run("tick_small");
        assertEquals(0.0, rpm(), 1e-6);
    }

    @Test
    @DisplayName("a driven wheel reports generated stress capacity")
    void drivenWheelGeneratesStress() throws Exception {
        water(1, 0, 0, 0, 0, -1);
        run("tick_small");
        assertNotNull(suOut);
        assertTrue(suOut < 0, "generated capacity is reported as a negative SU value, got " + suOut);
    }

    // ------------------------------------------------------------------ axis

    @Test
    @DisplayName("the wheel honours its axis instead of always scanning Z")
    void axisIsHonoured() throws Exception {
        // With axis=y the in-plane neighbours are +-X and +-Z; +-Y must never be probed.
        axis = "y";
        run("tick_small");
        assertTrue(probed.contains("1,0,0") && probed.contains("0,0,1"),
                "in-plane neighbours must be scanned, probed=" + probed);
        assertFalse(probed.contains("0,1,0") || probed.contains("0,-1,0"),
                "the axis direction must never be scanned, probed=" + probed);
    }

    @Test
    @DisplayName("an X-axis wheel scans the YZ plane")
    void xAxisScansYZ() throws Exception {
        axis = "x";
        run("tick_small");
        assertEquals(Set.of("0,1,0", "0,-1,0", "0,0,1", "0,0,-1"), probed);
    }

    @Test
    @DisplayName("a Z-axis wheel scans the XY plane")
    void zAxisScansXY() throws Exception {
        axis = "z";
        run("tick_small");
        assertEquals(Set.of("1,0,0", "-1,0,0", "0,1,0", "0,-1,0"), probed);
    }

    @Test
    @DisplayName("each axis turns the same way for an equivalent flow")
    void allAxesAgree() throws Exception {
        // axis=x, offset +Y: normal (0,1,0); rotate(n,90,X) = (x,-z,y) = (0,0,1).
        axis = "x";
        water(0, 1, 0, 0, 0, 1);
        run("tick_small");
        assertEquals(8.0, rpm(), 1e-6, "x-axis wheel");

        reset();
        // axis=z, offset +X: normal (1,0,0); rotate(n,90,Z) = (-y,x,z) = (0,1,0).
        axis = "z";
        water(1, 0, 0, 0, 1, 0);
        run("tick_small");
        assertEquals(8.0, rpm(), 1e-6, "z-axis wheel");
    }

    // ----------------------------------------------------------- large wheel

    @Test
    @DisplayName("the large wheel scans the distance-2 ring AND the adjacent ring")
    void largeWheelOffsets() throws Exception {
        axis = "y";
        run("tick_large");
        assertEquals(16, probed.size(), "4 adjacent + Create's 12 outer, got " + probed);
        assertEquals(Set.of(
                // adjacent: this wheel is a single block, so water poured beside it lands here
                "1,0,0", "-1,0,0", "0,0,1", "0,0,-1",
                // Create's LARGE_OFFSETS, where the water meets a real 3x3 structure
                "2,0,0", "2,0,1", "2,0,-1",
                "-2,0,0", "-2,0,1", "-2,0,-1",
                "0,0,2", "1,0,2", "-1,0,2",
                "0,0,-2", "1,0,-2", "-1,0,-2"), probed);
    }

    @Test
    @DisplayName("the large wheel is slower but stronger than the small one")
    void largeWheelIsSlowerAndStronger() throws Exception {
        water(2, 0, 0, 0, 0, -1);
        run("tick_large");
        assertEquals(4.0, rpm(), 1e-6, "getGeneratedSpeed divides by size, so the big wheel is slower");
        double largeSu = suOut;

        reset();
        water(1, 0, 0, 0, 0, -1);
        run("tick_small");
        assertTrue(Math.abs(largeSu) > Math.abs(suOut),
                "the large wheel must generate more capacity: large=" + largeSu + " small=" + suOut);
    }

    @Test
    @DisplayName("the large wheel is driven by water poured right beside it")
    void largeWheelIsDrivenByAdjacentWater() throws Exception {
        // The reported failure: flooding around a large wheel did nothing, because only
        // Create's distance-2 ring was scanned and a single-block wheel has its water at 1.
        water(1, 0, 0, 0, 0, -1);
        run("tick_large");
        assertEquals(4.0, rpm(), 1e-6);
    }

    @Test
    @DisplayName("the large wheel is still driven from the outer ring")
    void largeWheelIsDrivenByOuterRing() throws Exception {
        water(2, 0, 0, 0, 0, -1);
        run("tick_large");
        assertEquals(4.0, rpm(), 1e-6);
    }

    // ---------------------------------------------------------------- status

    @Test
    @DisplayName("status follows rpm with no latching flag")
    void statusFollowsRpm() throws Exception {
        assertEquals("true", status(12));
        assertEquals("false", status(0));
    }

    private String status(double rpm) throws Exception {
        ScriptProgram prog = ScriptProgram.parse("water_wheel", Files.readString(SCRIPT), LOG);
        ScriptContext ctx = prog.evaluate(ScriptContext.builder()
                .typed("Machine", new Object()).num("rpm", rpm).build());
        UserFunction f = (UserFunction) ((ScriptValue.Obj) ctx.getVar("status")).instance();
        return f.call(List.of(), ctx).asStr();
    }
}
