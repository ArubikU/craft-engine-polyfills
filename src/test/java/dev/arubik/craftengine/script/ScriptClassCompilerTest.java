package dev.arubik.craftengine.script;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Compiles a real shipped {@code .pf} file's {@code def}s with {@link ScriptClassCompiler},
 * disassembles the generated class with ASM's own {@code Textifier} (so the actual bytecode is
 * printed and can be read/reviewed, not just trusted to "probably be right"), and asserts on the
 * things that matter: correct package/class/method naming, and that the compiled methods actually
 * produce the SAME results as the interpreter for representative inputs — the differential check
 * this whole compiler exists to be verifiable against before it's ever wired into a live
 * evaluate() call.
 */
class ScriptClassCompilerTest {

    private static final Logger LOG = Logger.getLogger("test");

    // Compiled ONCE and shared across every @Test — ScriptClassCompiler's generated-class loader
    // is a plugin-lifetime singleton (real usage compiles each distinct file exactly once, ever),
    // so a second tryCompile() for the SAME originPath throws a duplicate-class LinkageError,
    // silently swallowed by tryCompile's own catch-all and returned as null. That's a real
    // constraint of the compiler's design, not a bug to work around per-test — sharing one
    // compile across all assertions here matches how it's actually meant to be called.
    private static ScriptProgram prog;
    private static ScriptClassCompiler.Compiled compiled;

    @BeforeAll
    static void compileOnce() throws Exception {
        Path p = Path.of("src/main/resources/scripts/kinetics/generators/windmill.pf");
        String src = Files.readString(p);
        prog = ScriptProgram.parse("windmill", src, LOG);
        compiled = ScriptClassCompiler.tryCompile("kinetics/generators/windmill", prog.statementsForCompiler());
    }

    @Test
    @DisplayName("compiles windmill.pf's defs into a class named after its folder/file, and prints the real bytecode")
    void compilesAndDisassemblesWindmill() {
        assertNotNull(compiled, "expected at least one of windmill.pf's defs to be eligible");

        assertEquals("dev.arubik.craftengine.script.gen.kinetics.generators.Windmill",
                compiled.generatedClass().getName(),
                "package should mirror the file's folder, class name should be PascalCase from the filename");

        // camelCase from each def name — see ScriptClassCompiler#camelCase's own doc.
        assertTrue(compiled.methodsByDefName().containsKey("_sail_count"));
        assertTrue(compiled.methodsByDefName().containsKey("_advance_head_angle"));
        assertTrue(compiled.methodsByDefName().containsKey("_stop"));
        assertTrue(compiled.methodsByDefName().containsKey("_spin_axis"));
        assertTrue(compiled.methodsByDefName().containsKey("status"));
        assertTrue(compiled.methodsByDefName().containsKey("on_break"));

        Method sailCount = compiled.methodsByDefName().get("_sail_count");
        assertEquals("_sailCount", sailCount.getName());
        Method advanceHeadAngle = compiled.methodsByDefName().get("_advance_head_angle");
        assertEquals("_advanceHeadAngle", advanceHeadAngle.getName());
        Method spinAxis = compiled.methodsByDefName().get("_spin_axis");
        assertEquals("_spinAxis", spinAxis.getName());
        Method onBreak = compiled.methodsByDefName().get("on_break");
        assertEquals("onBreak", onBreak.getName());

        // Print the REAL disassembly — this is the "decompile it so we can read it" step. Printed
        // (not just silently passed) so it shows up in the test report / console for review.
        String disassembly = disassemble(compiled.classBytes());
        System.out.println("---- ScriptClassCompiler output for windmill.pf ----");
        System.out.println(disassembly);
        System.out.println("---- end ----");

        assertTrue(disassembly.contains("class dev/arubik/craftengine/script/gen/kinetics/generators/Windmill"));
        assertTrue(disassembly.contains("public static _sailCount(Ldev/arubik/craftengine/script/ScriptContext$Builder;)Ldev/arubik/craftengine/script/ScriptValue;"));
        assertTrue(disassembly.contains("public static onBreak(Ldev/arubik/craftengine/script/ScriptContext$Builder;)Ldev/arubik/craftengine/script/ScriptValue;"));

        // _stop() calls _advance_head_angle(0) — a same-file def-to-def call — and the top-level
        // "run" body calls _stop/_sail_count/_spin_axis/_advance_head_angle. Both should now
        // compile via real local dispatch (INVOKESTATIC to the sibling method with an isolated
        // per-call Builder), not be excluded the way they were before that dispatch existed.
        assertTrue(disassembly.contains("public static run(Ldev/arubik/craftengine/script/ScriptContext$Builder;)V"),
                "the top-level run body calls other defs — it should compile now that local dispatch exists");
        assertNotNull(compiled.mainMethod());
    }

    @Test
    @DisplayName("a compiled def's result matches evaluating the same body through the interpreter")
    void compiledSpinAxisAgreesWithInterpreter() throws Exception {
        assertNotNull(compiled);
        Method spinAxis = compiled.methodsByDefName().get("_spin_axis");

        // _spin_axis() reads Machine.facing_dx/dy/dz — mock "Machine" as a class instance exposing
        // those, same shape the real MachineType would provide.
        record FakeMachine(double dx, double dy, double dz) {}
        PolyTypeRegistry.define("FakeMachineForSpinAxisTest")
                .property("facing_dx", o -> ScriptValue.of(((FakeMachine) o).dx()))
                .property("facing_dy", o -> ScriptValue.of(((FakeMachine) o).dy()))
                .property("facing_dz", o -> ScriptValue.of(((FakeMachine) o).dz()));

        for (double[] facing : new double[][]{{0, 1, 0}, {1, 0, 0}, {0, 0, 1}}) {
            ScriptContext.Builder b = ScriptContext.builder()
                    .typed("Machine", new FakeMachine(facing[0], facing[1], facing[2]));
            ScriptValue compiledResult = (ScriptValue) spinAxis.invoke(null, b);

            // Interpreter reference: call the SAME def via the normal evaluate() path.
            ScriptContext withDefs = prog.evaluate(ScriptContext.builder()
                    .typed("Machine", new FakeMachine(facing[0], facing[1], facing[2])).build());
            ScriptValue fnVal = withDefs.getVar("_spin_axis");
            assertTrue(fnVal instanceof ScriptValue.Obj o && o.typeName().equals(UserFunction.TYPE));
            UserFunction fn = (UserFunction) ((ScriptValue.Obj) fnVal).instance();
            ScriptValue interpretedResult = fn.call(java.util.List.of(), withDefs);

            assertEquals(interpretedResult.asStr(), compiledResult.asStr(),
                    "facing=" + java.util.Arrays.toString(facing));
        }
    }

    @Test
    @DisplayName("a compiled def CALLING another compiled def (_stop -> _advance_head_angle) matches the interpreter — real local dispatch")
    void compiledStopAgreesWithInterpreter() throws Exception {
        assertNotNull(compiled);
        Method stop = compiled.methodsByDefName().get("_stop");
        assertNotNull(stop, "_stop calls _advance_head_angle, a same-file def — should compile via local dispatch");

        class FakeMachine {
            int rpmOutput = -1;
            double suReported = Double.NaN;
            double headAngle = 0;
            int headLastTick = 0;
        }
        PolyTypeRegistry.define("FakeMachineForStopTest")
                .method("set_rpm_output", (o, a) -> { ((FakeMachine) o).rpmOutput = (int) a.get(0).asNum(); return ScriptValue.NULL; })
                .method("report_su", (o, a) -> { ((FakeMachine) o).suReported = a.get(0).asNum(); return ScriptValue.NULL; })
                .method("get_typed", (o, a) -> {
                    FakeMachine m = (FakeMachine) o;
                    String key = a.get(0).asStr();
                    return switch (key) {
                        case "head_angle" -> ScriptValue.of(m.headAngle);
                        case "head_last_tick" -> ScriptValue.of(m.headLastTick);
                        default -> ScriptValue.NULL;
                    };
                })
                .method("set_typed", (o, a) -> {
                    FakeMachine m = (FakeMachine) o;
                    String key = a.get(0).asStr();
                    double v = a.get(2).asNum();
                    if ("head_angle".equals(key)) m.headAngle = v;
                    if ("head_last_tick".equals(key)) m.headLastTick = (int) v;
                    return ScriptValue.NULL;
                });

        // Note: .typed(className, instance) uses the SAME string as both the script-visible
        // variable name ("Machine") AND the PolyType lookup key — there's no way to bind "Machine"
        // to a FAKE type through it. .val(...) with a manually-built ScriptValue.Obj decouples
        // those: the var is named "Machine" (what the script's dot-access looks up) while the
        // Obj's own typeName stays "FakeMachineForStopTest" (what property/method resolution
        // actually uses), so this fake type is the one really exercised, not the real Machine type.
        FakeMachine compiledMachine = new FakeMachine();
        ScriptContext.Builder b = ScriptContext.builder()
                .val("Machine", ScriptValue.ofObj("FakeMachineForStopTest", compiledMachine));
        ScriptValue compiledResult = (ScriptValue) stop.invoke(null, b);

        FakeMachine interpretedMachine = new FakeMachine();
        ScriptContext.Builder ib = ScriptContext.builder()
                .val("Machine", ScriptValue.ofObj("FakeMachineForStopTest", interpretedMachine));
        ScriptContext withDefs = prog.evaluate(ib.build());
        ScriptValue fnVal = withDefs.getVar("_stop");
        assertTrue(fnVal instanceof ScriptValue.Obj o && o.typeName().equals(UserFunction.TYPE));
        UserFunction fn = (UserFunction) ((ScriptValue.Obj) fnVal).instance();
        ScriptValue interpretedResult = fn.call(java.util.List.of(), withDefs);

        assertEquals(interpretedResult.asStr(), compiledResult.asStr());
        assertEquals(0, compiledMachine.rpmOutput);
        assertEquals(0, interpretedMachine.rpmOutput);
        assertEquals(0.0, compiledMachine.suReported);
        assertEquals(0.0, interpretedMachine.suReported);
    }

    @Test
    @DisplayName("a compiled def with a for-loop (_sail_count) matches the interpreter across several block lists")
    void compiledForLoopAgreesWithInterpreter() throws Exception {
        assertNotNull(compiled);
        Method sailCount = compiled.methodsByDefName().get("_sail_count");
        assertNotNull(sailCount, "the for-loop def should now compile — this is exactly the new code path being checked");

        record FakeBlock(String id) {}
        PolyTypeRegistry.define("FakeBlockForForLoopTest").property("id", o -> ScriptValue.of(((FakeBlock) o).id()));
        record FakeContraption(java.util.List<ScriptValue> blocks) {}
        PolyTypeRegistry.define("FakeContraptionForForLoopTest")
                .method("blocks", (o, a) -> new ScriptValue.Array(((FakeContraption) o).blocks()));

        java.util.function.Function<String[], ScriptValue> contraptionOf = ids -> {
            var blocks = java.util.Arrays.stream(ids)
                    .map(id -> ScriptValue.ofObj("FakeBlockForForLoopTest", new FakeBlock(id)))
                    .toList();
            return ScriptValue.ofObj("FakeContraptionForForLoopTest", new FakeContraption(blocks));
        };

        String[][] cases = {
            {},
            {"minecraft:oak_planks"},
            {"cml:windmill_sail", "cml:windmill_sail", "minecraft:oak_planks"},
            {"cml:windmill_sail", "cml:windmill_sail", "cml:windmill_sail", "cml:windmill_sail",
             "cml:windmill_sail", "cml:windmill_sail", "cml:windmill_sail", "cml:windmill_sail"},
        };

        for (String[] ids : cases) {
            ScriptValue contraption = contraptionOf.apply(ids);

            ScriptContext.Builder b = ScriptContext.builder().val("contraption", contraption);
            ScriptValue compiledResult = (ScriptValue) sailCount.invoke(null, b);

            ScriptContext withDefs = prog.evaluate(ScriptContext.builder().build());
            ScriptValue fnVal = withDefs.getVar("_sail_count");
            assertTrue(fnVal instanceof ScriptValue.Obj o && o.typeName().equals(UserFunction.TYPE));
            UserFunction fn = (UserFunction) ((ScriptValue.Obj) fnVal).instance();
            ScriptValue interpretedResult = fn.call(java.util.List.of(contraption), withDefs);

            assertEquals(interpretedResult.asNum(), compiledResult.asNum(), 1e-9,
                    "blocks=" + java.util.Arrays.toString(ids));
        }
    }

    private static String disassemble(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        StringWriter sw = new StringWriter();
        cr.accept(new TraceClassVisitor(new PrintWriter(sw)), 0);
        return sw.toString();
    }
}
