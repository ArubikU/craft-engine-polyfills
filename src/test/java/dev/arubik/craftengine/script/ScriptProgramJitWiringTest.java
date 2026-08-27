package dev.arubik.craftengine.script;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validates {@link ScriptProgram}'s live wiring of {@link ScriptClassCompiler} — NOT by invoking a
 * compiled {@code Method} directly (that's what {@code ScriptClassCompilerTest} already covers),
 * but by going through the exact PUBLIC entry points a real caller uses: {@link
 * ScriptProgram#evaluate}, then {@link UserFunction#call} on whatever comes back from {@code
 * getVar(defName)}. This is the path every real {@code .pf:func} reference, dot-call, and machine
 * hook actually runs through — the thing worth proving isn't "does the compiled method work in
 * isolation" (already proven) but "does {@code ScriptProgram} actually route a real call to it".
 *
 * <p>Each test compares the SAME call sequence with {@link ScriptClassCompiler#CLASS_JIT_ENABLED}
 * on vs. off — using a FRESH {@link ScriptProgram} instance per side (a compiled class, once bound,
 * can't be un-compiled — the kill switch only gates whether {@code compiledMethodFor} ever hands
 * that compiled {@code Method} out, not whether compilation happened), so this proves the wiring
 * itself doesn't change observable behavior, not just that the compiled bytecode happens to agree
 * with the interpreter (that's {@code ScriptClassCompilerTest}/{@code JitDifferentialTest}'s job).
 */
class ScriptProgramJitWiringTest {

    private static final Logger LOG = Logger.getLogger("test");

    @AfterEach
    void restoreDefault() {
        ScriptClassCompiler.CLASS_JIT_ENABLED = true;
    }

    private static ScriptProgram freshWindmill() throws Exception {
        String src = Files.readString(Path.of("src/main/resources/scripts/kinetics/generators/windmill.pf"));
        // Distinct "name" per instance so each side compiles its OWN class (GenLoader can't
        // redefine the same binary name twice) — the kill switch alone doesn't stop that.
        return ScriptProgram.parse("windmill-wiring-test-" + System.identityHashCode(new Object()), src, LOG);
    }

    private static ScriptValue callDef(ScriptProgram prog, String defName, List<ScriptValue> args,
                                        ScriptContext.Builder base) {
        ScriptContext withDefs = prog.evaluate(base.build());
        ScriptValue fnVal = withDefs.getVar(defName);
        assertTrue(fnVal instanceof ScriptValue.Obj o && o.typeName().equals(UserFunction.TYPE),
                defName + " should be bound as a UserFunction");
        UserFunction fn = (UserFunction) ((ScriptValue.Obj) fnVal).instance();
        return fn.call(args, withDefs);
    }

    @Test
    void spinAxisRoutesThroughCompiledDispatchWhenEnabled() throws Exception {
        record FakeMachine(double dx, double dy, double dz) {}
        PolyTypeRegistry.define("FakeMachineForWiringTest")
                .property("facing_dx", o -> ScriptValue.of(((FakeMachine) o).dx()))
                .property("facing_dy", o -> ScriptValue.of(((FakeMachine) o).dy()))
                .property("facing_dz", o -> ScriptValue.of(((FakeMachine) o).dz()));

        for (double[] facing : new double[][]{{0, 1, 0}, {1, 0, 0}, {0, 0, 1}}) {
            ScriptValue machine = ScriptValue.ofObj("FakeMachineForWiringTest", new FakeMachine(facing[0], facing[1], facing[2]));

            ScriptClassCompiler.CLASS_JIT_ENABLED = false;
            ScriptValue interpreted = callDef(freshWindmill(), "_spin_axis", List.of(),
                    ScriptContext.builder().val("Machine", machine));

            ScriptClassCompiler.CLASS_JIT_ENABLED = true;
            ScriptValue jitted = callDef(freshWindmill(), "_spin_axis", List.of(),
                    ScriptContext.builder().val("Machine", machine));

            assertEquals(interpreted.asStr(), jitted.asStr(), "facing=" + java.util.Arrays.toString(facing));
        }
    }

    @Test
    void stopRoutesThroughCompiledLocalDispatchWhenEnabled() throws Exception {
        class FakeMachine {
            int rpmOutput = -1;
            double suReported = Double.NaN;
            double headAngle = 0;
            int headLastTick = 0;
        }
        PolyTypeRegistry.define("FakeMachineForWiringStopTest")
                .method("set_rpm_output", (o, a) -> { ((FakeMachine) o).rpmOutput = (int) a.get(0).asNum(); return ScriptValue.NULL; })
                .method("report_su", (o, a) -> { ((FakeMachine) o).suReported = a.get(0).asNum(); return ScriptValue.NULL; })
                .method("get_typed", (o, a) -> {
                    FakeMachine m = (FakeMachine) o;
                    return switch (a.get(0).asStr()) {
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

        FakeMachine interpretedMachine = new FakeMachine();
        ScriptClassCompiler.CLASS_JIT_ENABLED = false;
        callDef(freshWindmill(), "_stop", List.of(),
                ScriptContext.builder().val("Machine", ScriptValue.ofObj("FakeMachineForWiringStopTest", interpretedMachine)));

        FakeMachine jittedMachine = new FakeMachine();
        ScriptClassCompiler.CLASS_JIT_ENABLED = true;
        callDef(freshWindmill(), "_stop", List.of(),
                ScriptContext.builder().val("Machine", ScriptValue.ofObj("FakeMachineForWiringStopTest", jittedMachine)));

        assertEquals(interpretedMachine.rpmOutput, jittedMachine.rpmOutput);
        assertEquals(interpretedMachine.suReported, jittedMachine.suReported);
    }

    @Test
    void killSwitchInstantlyRevertsAnAlreadyCompiledFile() throws Exception {
        record FakeMachine(double dx, double dy, double dz) {}
        ScriptValue machine = ScriptValue.ofObj("FakeMachineForWiringTest", new FakeMachine(0, 1, 0));
        ScriptProgram prog = freshWindmill();

        // First call with the JIT on — compiles and binds the compiled dispatch path.
        ScriptClassCompiler.CLASS_JIT_ENABLED = true;
        ScriptValue first = callDef(prog, "_spin_axis", List.of(), ScriptContext.builder().val("Machine", machine));

        // Flip off WITHOUT reloading the script — same ScriptProgram instance, same already-
        // compiled class. compiledMethodFor must re-check the flag on every call for this to work.
        ScriptClassCompiler.CLASS_JIT_ENABLED = false;
        ScriptValue second = callDef(prog, "_spin_axis", List.of(), ScriptContext.builder().val("Machine", machine));

        assertEquals(first.asStr(), second.asStr(), "flipping the kill switch mid-lifetime must not change the result");
    }
}
