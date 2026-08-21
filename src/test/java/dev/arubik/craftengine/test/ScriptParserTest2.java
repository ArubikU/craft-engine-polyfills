package dev.arubik.craftengine.test;

import dev.arubik.craftengine.script.ScriptBuiltins;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.UserFunction;
import dev.arubik.craftengine.script.types.primitive.MapType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class ScriptParserTest2 {

    @BeforeAll
    static void init() {
        // Register necessary builtins
        try { dev.arubik.craftengine.script.ScriptBootstrap.init(); } catch (Throwable ignored) {}
    }

    private ScriptContext empty() { return ScriptContext.builder().build(); }

    private ScriptValue call(String source, String funcName, ScriptValue... args) {
        ScriptProgram prog = ScriptProgram.parse("test", source, java.util.logging.Logger.getLogger("test"));
        assertNotNull(prog);
        ScriptContext withDefs = prog.evaluate(empty());
        ScriptValue fnVal = withDefs.getVar(funcName);
        assertTrue(fnVal instanceof ScriptValue.Obj o && o.typeName().equals(UserFunction.TYPE),
            "expected UserFunction for " + funcName + " got: " + fnVal);
        UserFunction fn = (UserFunction) ((ScriptValue.Obj) fnVal).instance();
        return fn.call(List.of(args), withDefs);
    }

    // --- Multiple return in def with if guard ---

    @Test
    void multipleReturnWithGuard() {
        // Simulates _get_tank pattern — if guard + final return
        String src = """
                def get_val(arr) {
                    v = arr.get(0)
                    if v == null { return null }
                    return v
                }
                """;
        // Pass array with 1 element = 42
        ScriptValue arr = new ScriptValue.Array(List.of(ScriptValue.of(42.0)));
        ScriptValue result = call(src, "get_val", arr);
        assertEquals(42.0, result.asNum(), "should return 42 from array[0]");
    }

    @Test
    void multipleReturnWithEmptyArrayReturnsNull() {
        String src = """
                def get_val(arr) {
                    v = arr.get(0)
                    if v == null { return null }
                    return v
                }
                """;
        ScriptValue emptyArr = new ScriptValue.Array(List.of());
        ScriptValue result = call(src, "get_val", emptyArr);
        assertInstanceOf(ScriptValue.Null.class, result, "empty array get(0) should give null");
    }

    @Test
    void mapGetReturnsValue() {
        // Verify that MapType.wrap(map).callMethod("get", ["key"]) works
        Map<String, ScriptValue> map = new LinkedHashMap<>();
        map.put("contents_name", ScriptValue.of("steam"));
        map.put("level", ScriptValue.of(500.0));
        ScriptValue mapVal = MapType.wrap(map);

        String src = """
                def extract(m) {
                    name = m.get("contents_name")
                    if name == "" { return "empty" }
                    if name == null { return "null_name" }
                    return name
                }
                """;
        ScriptValue result = call(src, "extract", mapVal);
        assertEquals("steam", result.asStr());
    }

    @Test
    void mapGetInArrayThenExtract() {
        // Simulate Machine.gas_tanks pattern: array of maps, get(0), then get("field")
        Map<String, ScriptValue> tankMap = new LinkedHashMap<>();
        tankMap.put("name", ScriptValue.of("vapor"));
        tankMap.put("level", ScriptValue.of(0.0));
        tankMap.put("capacity", ScriptValue.of(10000.0));
        tankMap.put("is_empty", ScriptValue.of(true));
        tankMap.put("contents_name", ScriptValue.of(""));

        ScriptValue tankArr = new ScriptValue.Array(List.of(MapType.wrap(tankMap)));

        String src = """
                def get_tank_name(tanks) {
                    if tanks.length <= 0 { return null }
                    tank = tanks.get(0)
                    if tank == null { return null }
                    cn = tank.get("contents_name")
                    if cn == "" { return "<gray>Gas Tank (empty)" }
                    return cn
                }
                """;
        ScriptValue result = call(src, "get_tank_name", tankArr);
        assertEquals("<gray>Gas Tank (empty)", result.asStr(),
            "empty tank should show 'Gas Tank (empty)'");
    }

    @Test
    void nullEqualityAfterFix() {
        // Verify null == null is true, obj == null is false
        String src = """
                def test_null(v) {
                    if v == null { return 1 }
                    return 0
                }
                """;
        // NULL == null → 1
        assertEquals(1.0, call(src, "test_null", ScriptValue.NULL).asNum(), "NULL==null should be true");
        // Num == null → 0
        assertEquals(0.0, call(src, "test_null", ScriptValue.of(5.0)).asNum(), "Num==null should be false");
        // Array == null → 0 (was broken before fix!)
        ScriptValue arr = new ScriptValue.Array(List.of(ScriptValue.of(1.0)));
        assertEquals(0.0, call(src, "test_null", arr).asNum(), "Array==null should be false (regression test)");
        // Empty array == null → 0
        ScriptValue emptyArr = new ScriptValue.Array(List.of());
        assertEquals(0.0, call(src, "test_null", emptyArr).asNum(), "Empty Array==null should be false");
    }

    @Test
    void functionCallReturningMapFromArray() {
        // Full simulation of _get_tank() pattern
        Map<String, ScriptValue> tankMap = new LinkedHashMap<>();
        tankMap.put("level", ScriptValue.of(0.0));
        tankMap.put("capacity", ScriptValue.of(10000.0));
        tankMap.put("contents_name", ScriptValue.of(""));
        ScriptValue tankArr = new ScriptValue.Array(List.of(MapType.wrap(tankMap)));

        String src = """
                def _get_tank(tanks) {
                    if tanks.length <= 0 { return null }
                    return tanks.get(0)
                }
                def get_name(tanks) {
                    tank = _get_tank(tanks)
                    if tank == null { return "No gas tank" }
                    cn = tank.get("contents_name")
                    if cn == "" { return "Gas Tank (empty)" }
                    return cn
                }
                """;
        ScriptValue result = call(src, "get_name", tankArr);
        assertEquals("Gas Tank (empty)", result.asStr(),
            "tank with empty contents_name should return 'Gas Tank (empty)'");
    }
}
