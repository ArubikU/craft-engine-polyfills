package dev.arubik.craftengine.script;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tests for gas_motor.pf script logic — no NMS, no server.
 *
 * Verifies:
 * 1. Script parses without errors (defs at top, body below)
 * 2. Function defs are accessible even when body hits early return
 * 3. Button functions correctly modify flag values
 * 4. Status function logic
 * 5. Gas consumption calculation
 */
public class GasMotorScriptTest {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger("test");

    /**
     * A minimal mock "Machine" type that tracks method calls and stores flags
     * in-memory. Registered once in PolyTypeRegistry for all tests.
     */
    static class MockMachine {
        final Map<String, Integer> flags = new ConcurrentHashMap<>();
        float lastRpmOutput = 0f;
        float lastSuReport = 0f;
        final List<String> gasConsumed = new ArrayList<>();
        List<ScriptValue> gasTanks = List.of();
        double overclock = 0.0;
        double efficiency = 0.0;
        boolean isOverstressed = false;

        void reset() {
            flags.clear();
            lastRpmOutput = 0f;
            lastSuReport = 0f;
            gasConsumed.clear();
            gasTanks = List.of();
            overclock = 0.0;
            efficiency = 0.0;
            isOverstressed = false;
        }
    }

    static MockMachine mockMachine = new MockMachine();

    @BeforeAll
    static void registerMockMachineType() {
        if (PolyTypeRegistry.get("Map") == null) {
            dev.arubik.craftengine.script.types.primitive.MapType.register();
        }
        PolyType type = PolyTypeRegistry.define("Machine");
        // gas_motor.pf now calls Machine.get_typed/set_typed(key, "int", ...) instead of the old
        // get_flag/set_flag pair — same backing map, the "type" arg is ignored since this mock only
        // ever exercises "int" values.
        type.method("get_typed", (obj, args) -> {
            if (args.isEmpty()) return ScriptValue.of(0);
            String name = args.get(0).asStr();
            return ScriptValue.of(mockMachine.flags.getOrDefault(name, 0));
        });
        type.method("set_typed", (obj, args) -> {
            if (args.size() < 3) return ScriptValue.of(false);
            mockMachine.flags.put(args.get(0).asStr(), (int) args.get(2).asNum());
            return ScriptValue.of(true);
        });
        type.method("set_rpm_output", (obj, args) -> {
            if (!args.isEmpty()) mockMachine.lastRpmOutput = (float) args.get(0).asNum();
            return ScriptValue.of(true);
        });
        type.method("report_su", (obj, args) -> {
            if (!args.isEmpty()) mockMachine.lastSuReport = (float) args.get(0).asNum();
            return ScriptValue.of(true);
        });
        type.method("consume_gas", (obj, args) -> {
            if (args.size() >= 2) {
                mockMachine.gasConsumed.add(args.get(0).asStr() + ":" + (int) args.get(1).asNum());
            }
            return ScriptValue.of(true);
        });
        type.property("gas_tanks", obj -> new ScriptValue.Array(mockMachine.gasTanks));
        type.property("overclock", obj -> ScriptValue.of(mockMachine.overclock));
        type.property("efficiency", obj -> ScriptValue.of(mockMachine.efficiency));
        type.property("is_overstressed", obj -> ScriptValue.of(mockMachine.isOverstressed));
    }

    private ScriptContext machineCtx() {
        return ScriptContext.builder()
                .typed("Machine", mockMachine)
                .build();
    }

    private ScriptValue makeTankMap(boolean isEmpty, int level, String contentsKey) {
        LinkedHashMap<String, ScriptValue> map = new LinkedHashMap<>();
        map.put("name", ScriptValue.of("vapor"));
        map.put("level", ScriptValue.of(level));
        map.put("capacity", ScriptValue.of(10000));
        map.put("is_empty", ScriptValue.of(isEmpty));
        map.put("contents_key", ScriptValue.of(contentsKey));
        map.put("contents_name", ScriptValue.of(isEmpty ? "" : "Steam"));
        return dev.arubik.craftengine.script.types.primitive.MapType.wrap(map);
    }

    // -----------------------------------------------------------------------
    // Test 1: gas_motor.pf parses without errors
    // -----------------------------------------------------------------------

    @Test
    void gasMotorScriptParses() throws Exception {
        Path scriptPath = Path.of("src/main/resources/scripts/gas_motor.pf");
        assertTrue(Files.exists(scriptPath), "gas_motor.pf must exist");
        String src = Files.readString(scriptPath);
        ScriptProgram prog = ScriptProgram.parse("gas_motor", src, LOG);
        assertNotNull(prog, "gas_motor.pf must parse without null result");
    }

    // -----------------------------------------------------------------------
    // Test 2: Function defs accessible even when body would return early
    // -----------------------------------------------------------------------

    @Test
    void functionDefsRegisteredBeforeBodyReturn() throws Exception {
        Path scriptPath = Path.of("src/main/resources/scripts/gas_motor.pf");
        String src = Files.readString(scriptPath);
        ScriptProgram prog = ScriptProgram.parse("gas_motor", src, LOG);

        mockMachine.reset();
        // No gas tanks → body will hit "return" early at line 74
        mockMachine.gasTanks = List.of();

        ScriptContext result = prog.evaluate(machineCtx());

        // Critical assertion: function defs must still be registered
        assertFunctionDefined(result, "status");
        assertFunctionDefined(result, "increase_rpm");
        assertFunctionDefined(result, "decrease_rpm");
        assertFunctionDefined(result, "increase_su");
        assertFunctionDefined(result, "decrease_su");
        assertFunctionDefined(result, "on_break");
    }

    @Test
    void functionDefsRegisteredWhenGasPresent() throws Exception {
        Path scriptPath = Path.of("src/main/resources/scripts/gas_motor.pf");
        String src = Files.readString(scriptPath);
        ScriptProgram prog = ScriptProgram.parse("gas_motor", src, LOG);

        mockMachine.reset();
        mockMachine.gasTanks = List.of(makeTankMap(false, 5000, "polyfills:steam"));

        ScriptContext result = prog.evaluate(machineCtx());

        assertFunctionDefined(result, "status");
        assertFunctionDefined(result, "increase_rpm");
        assertFunctionDefined(result, "decrease_rpm");
        assertFunctionDefined(result, "increase_su");
        assertFunctionDefined(result, "decrease_su");
        assertFunctionDefined(result, "on_break");
    }

    // -----------------------------------------------------------------------
    // Test 3: Status function returns correct values
    // -----------------------------------------------------------------------

    @Test
    void statusReturnsFalseWhenNoGas() throws Exception {
        Path scriptPath = Path.of("src/main/resources/scripts/gas_motor.pf");
        String src = Files.readString(scriptPath);
        ScriptProgram prog = ScriptProgram.parse("gas_motor", src, LOG);

        mockMachine.reset();
        mockMachine.gasTanks = List.of(); // empty tanks array

        ScriptContext withDefs = prog.evaluate(machineCtx());
        ScriptValue result = callFunction(withDefs, "status");
        assertEquals("false", result.asStr(), "status() should return 'false' when no gas tanks");
    }

    @Test
    void statusReturnsFalseWhenTankEmpty() throws Exception {
        Path scriptPath = Path.of("src/main/resources/scripts/gas_motor.pf");
        String src = Files.readString(scriptPath);
        ScriptProgram prog = ScriptProgram.parse("gas_motor", src, LOG);

        mockMachine.reset();
        mockMachine.gasTanks = List.of(makeTankMap(true, 0, ""));

        ScriptContext withDefs = prog.evaluate(machineCtx());
        ScriptValue result = callFunction(withDefs, "status");
        assertEquals("false", result.asStr(), "status() should return 'false' when tank is empty");
    }

    @Test
    void statusReturnsTrueWhenGasAndRpm() throws Exception {
        Path scriptPath = Path.of("src/main/resources/scripts/gas_motor.pf");
        String src = Files.readString(scriptPath);
        ScriptProgram prog = ScriptProgram.parse("gas_motor", src, LOG);

        mockMachine.reset();
        mockMachine.gasTanks = List.of(makeTankMap(false, 5000, "polyfills:steam"));
        // After evaluation, target_rpm will be set to default (32)

        ScriptContext withDefs = prog.evaluate(machineCtx());
        ScriptValue result = callFunction(withDefs, "status");
        assertEquals("true", result.asStr(), "status() should return 'true' when gas present and rpm > 0");
    }

    // -----------------------------------------------------------------------
    // Test 4: Button functions modify flags correctly
    // -----------------------------------------------------------------------

    @Test
    void increaseRpmFromDefault() throws Exception {
        Path scriptPath = Path.of("src/main/resources/scripts/gas_motor.pf");
        String src = Files.readString(scriptPath);
        ScriptProgram prog = ScriptProgram.parse("gas_motor", src, LOG);

        mockMachine.reset();
        mockMachine.gasTanks = List.of(makeTankMap(false, 5000, "polyfills:steam"));

        ScriptContext withDefs = prog.evaluate(machineCtx());
        // target_rpm should now be 32 (default from steam spec)
        assertEquals(32, mockMachine.flags.getOrDefault("target_rpm", 0));

        // Call increase_rpm(8)
        callFunctionWithArgs(withDefs, "increase_rpm", ScriptValue.of("8"));
        assertEquals(40, mockMachine.flags.get("target_rpm"), "target_rpm should be 32+8=40");
    }

    @Test
    void decreaseRpmToZero() throws Exception {
        Path scriptPath = Path.of("src/main/resources/scripts/gas_motor.pf");
        String src = Files.readString(scriptPath);
        ScriptProgram prog = ScriptProgram.parse("gas_motor", src, LOG);

        mockMachine.reset();
        mockMachine.flags.put("target_rpm", 8);
        mockMachine.flags.put("target_su", 64);
        mockMachine.gasTanks = List.of(makeTankMap(false, 5000, "polyfills:steam"));

        ScriptContext withDefs = prog.evaluate(machineCtx());
        callFunctionWithArgs(withDefs, "decrease_rpm", ScriptValue.of("16"));
        assertEquals(0, mockMachine.flags.get("target_rpm"), "target_rpm should be clamped to 0");
        assertEquals(0, mockMachine.flags.get("target_su"), "target_su should be 0 when rpm is 0");
    }

    @Test
    void increaseSuCapsAt1024() throws Exception {
        Path scriptPath = Path.of("src/main/resources/scripts/gas_motor.pf");
        String src = Files.readString(scriptPath);
        ScriptProgram prog = ScriptProgram.parse("gas_motor", src, LOG);

        mockMachine.reset();
        mockMachine.flags.put("target_su", 1020);
        mockMachine.gasTanks = List.of(makeTankMap(false, 5000, "polyfills:steam"));
        // Need target_rpm set so body doesn't override target_su
        mockMachine.flags.put("target_rpm", 32);

        ScriptContext withDefs = prog.evaluate(machineCtx());
        callFunctionWithArgs(withDefs, "increase_su", ScriptValue.of("16"));
        assertEquals(1024, mockMachine.flags.get("target_su"), "target_su should be capped at 1024");
    }

    @Test
    void decreaseSuFloorsAtZero() throws Exception {
        Path scriptPath = Path.of("src/main/resources/scripts/gas_motor.pf");
        String src = Files.readString(scriptPath);
        ScriptProgram prog = ScriptProgram.parse("gas_motor", src, LOG);

        mockMachine.reset();
        mockMachine.flags.put("target_su", 10);
        mockMachine.flags.put("target_rpm", 32);
        mockMachine.gasTanks = List.of(makeTankMap(false, 5000, "polyfills:steam"));

        ScriptContext withDefs = prog.evaluate(machineCtx());
        callFunctionWithArgs(withDefs, "decrease_su", ScriptValue.of("16"));
        assertEquals(0, mockMachine.flags.get("target_su"), "target_su should floor at 0");
    }

    // -----------------------------------------------------------------------
    // Test 5: Gas consumption and RPM/SU output
    // -----------------------------------------------------------------------

    @Test
    void gasConsumedWhenMotorRunning() throws Exception {
        Path scriptPath = Path.of("src/main/resources/scripts/gas_motor.pf");
        String src = Files.readString(scriptPath);
        ScriptProgram prog = ScriptProgram.parse("gas_motor", src, LOG);

        mockMachine.reset();
        mockMachine.gasTanks = List.of(makeTankMap(false, 5000, "polyfills:steam"));

        prog.evaluate(machineCtx());

        // Motor should have called consume_gas("vapor", 10) — base 10/tick for steam at 1x ratio
        assertFalse(mockMachine.gasConsumed.isEmpty(), "Gas should be consumed");
        assertEquals("vapor:10", mockMachine.gasConsumed.get(0), "Should consume 10 vapor per tick");
    }

    @Test
    void rpmOutputSetWhenGasPresent() throws Exception {
        Path scriptPath = Path.of("src/main/resources/scripts/gas_motor.pf");
        String src = Files.readString(scriptPath);
        ScriptProgram prog = ScriptProgram.parse("gas_motor", src, LOG);

        mockMachine.reset();
        mockMachine.gasTanks = List.of(makeTankMap(false, 5000, "polyfills:steam"));

        prog.evaluate(machineCtx());

        // Default target_rpm = 32 (from steam spec), oc_factor = 1.0
        assertEquals(32.0f, mockMachine.lastRpmOutput, 0.01f, "RPM output should be 32");
        assertEquals(-64.0f, mockMachine.lastSuReport, 0.01f, "SU report should be -64 (generating)");
    }

    @Test
    void noOutputWhenNoGas() throws Exception {
        Path scriptPath = Path.of("src/main/resources/scripts/gas_motor.pf");
        String src = Files.readString(scriptPath);
        ScriptProgram prog = ScriptProgram.parse("gas_motor", src, LOG);

        mockMachine.reset();
        mockMachine.gasTanks = List.of(); // no tanks

        prog.evaluate(machineCtx());

        assertEquals(0.0f, mockMachine.lastRpmOutput, "RPM should be 0 when no gas");
        assertEquals(0.0f, mockMachine.lastSuReport, "SU should be 0 when no gas");
        assertTrue(mockMachine.gasConsumed.isEmpty(), "No gas consumed when none available");
    }

    @Test
    void overclockAmplifiesOutput() throws Exception {
        Path scriptPath = Path.of("src/main/resources/scripts/gas_motor.pf");
        String src = Files.readString(scriptPath);
        ScriptProgram prog = ScriptProgram.parse("gas_motor", src, LOG);

        mockMachine.reset();
        mockMachine.overclock = 0.5; // +50%
        mockMachine.gasTanks = List.of(makeTankMap(false, 5000, "polyfills:steam"));

        prog.evaluate(machineCtx());

        // target_rpm=32 * oc_factor=1.5 = 48
        assertEquals(48.0f, mockMachine.lastRpmOutput, 0.01f, "RPM should be amplified by overclock");
        // target_su=64 * 1.5 = 96
        assertEquals(-96.0f, mockMachine.lastSuReport, 0.01f, "SU should be amplified by overclock");
    }

    // -----------------------------------------------------------------------
    // Test 6: Regression — the original bug (defs AFTER return)
    // -----------------------------------------------------------------------

    @Test
    void regressionDefsAfterReturnWouldFail() {
        // Simulates the OLD broken structure: body with return BEFORE defs
        String brokenScript = """
            x = 10
            if x > 5 { return }
            def my_func() {
                return "hello"
            }
            """;
        ScriptProgram prog = ScriptProgram.parse("broken", brokenScript, LOG);
        ScriptContext result = prog.evaluate(ScriptContext.builder().build());

        // In the broken structure, my_func would NOT be defined because return stops execution
        ScriptValue fnVal = result.getVar("my_func");
        assertTrue(fnVal instanceof ScriptValue.Null || fnVal == ScriptValue.NULL,
                "In broken structure (defs after return), function should NOT be registered");
    }

    @Test
    void fixedDefsBeforeReturnAlwaysWork() {
        // The FIXED structure: defs BEFORE return
        String fixedScript = """
            def my_func() {
                return "hello"
            }
            x = 10
            if x > 5 { return }
            y = 20
            """;
        ScriptProgram prog = ScriptProgram.parse("fixed", fixedScript, LOG);
        ScriptContext result = prog.evaluate(ScriptContext.builder().build());

        // Function should be defined regardless of the return
        ScriptValue fnVal = result.getVar("my_func");
        assertInstanceOf(ScriptValue.Obj.class, fnVal, "Function must be registered before return");
        assertEquals(UserFunction.TYPE, ((ScriptValue.Obj) fnVal).typeName());

        // Call it to verify
        UserFunction fn = (UserFunction) ((ScriptValue.Obj) fnVal).instance();
        ScriptValue ret = fn.call(List.of(), result);
        assertEquals("hello", ret.asStr());
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private void assertFunctionDefined(ScriptContext ctx, String name) {
        ScriptValue val = ctx.getVar(name);
        assertInstanceOf(ScriptValue.Obj.class, val,
                "'" + name + "' should be defined as a function but was: " + val);
        assertEquals(UserFunction.TYPE, ((ScriptValue.Obj) val).typeName(),
                "'" + name + "' should be a UserFunction");
    }

    private ScriptValue callFunction(ScriptContext ctx, String name) {
        ScriptValue fnVal = ctx.getVar(name);
        assertInstanceOf(ScriptValue.Obj.class, fnVal);
        UserFunction fn = (UserFunction) ((ScriptValue.Obj) fnVal).instance();
        return fn.call(List.of(), ctx);
    }

    private ScriptValue callFunctionWithArgs(ScriptContext ctx, String name, ScriptValue... args) {
        ScriptValue fnVal = ctx.getVar(name);
        assertInstanceOf(ScriptValue.Obj.class, fnVal);
        UserFunction fn = (UserFunction) ((ScriptValue.Obj) fnVal).instance();
        return fn.call(List.of(args), ctx);
    }
}
