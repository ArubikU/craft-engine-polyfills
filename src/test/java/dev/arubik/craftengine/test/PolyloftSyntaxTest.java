package dev.arubik.craftengine.test;

import dev.arubik.craftengine.script.ScriptBootstrap;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.UserFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Minimal-compatibility tests for the Polyloft ({@code polyloft-bvm}) colon/{@code end} block
 * style added alongside our existing brace style — see ScriptProgram's Parser: "end"/"else" are
 * only reserved inside a colon-opened body, so a brace-only script (all our existing .pf files
 * before this) is completely unaffected. These lock in the specific constructs the docs need to
 * be shareable: colon+end blocks, {@code where} guards, and {@code for key, value in map}
 * destructuring.
 */
public class PolyloftSyntaxTest {

    @BeforeAll
    static void init() {
        try { ScriptBootstrap.init(); } catch (Throwable ignored) {}
    }

    private ScriptContext empty() { return ScriptContext.builder().build(); }

    private ScriptContext run(String source) {
        ScriptProgram prog = ScriptProgram.parse("test", source, java.util.logging.Logger.getLogger("test"));
        assertNotNull(prog, "Script failed to compile");
        return prog.evaluate(empty());
    }

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

    // ---- if / else colon+end ----------------------------------------------

    @Test
    void colonIfNoElse() {
        ScriptContext ctx = run("""
                x = 0
                if true:
                    x = 1
                end
                """);
        assertEquals(1.0, ctx.getVar("x").asNum());
    }

    @Test
    void colonIfElse() {
        String src = """
                x = 0
                if false:
                    x = 1
                else:
                    x = 2
                end
                """;
        assertEquals(2.0, run(src).getVar("x").asNum());
    }

    @Test
    void colonIfElseIfElseChain() {
        String src = """
                def classify(n):
                    if n > 0:
                        return 1
                    else if n < 0:
                        return -1
                    else:
                        return 0
                    end
                end
                """;
        assertEquals(1.0, call(src, "classify", ScriptValue.of(5)).asNum());
        assertEquals(-1.0, call(src, "classify", ScriptValue.of(-3)).asNum());
        assertEquals(0.0, call(src, "classify", ScriptValue.of(0)).asNum());
    }

    @Test
    void mixedBraceOuterColonInner() {
        // A brace-style if containing a colon-style nested if — styles may mix freely.
        String src = """
                x = 0
                if true {
                    if true:
                        x = 9
                    end
                }
                """;
        assertEquals(9.0, run(src).getVar("x").asNum());
    }

    // ---- while colon+end ---------------------------------------------------

    @Test
    void colonWhileLoop() {
        String src = """
                i = 0
                sum = 0
                while i < 5:
                    sum = sum + i
                    i = i + 1
                end
                """;
        assertEquals(10.0, run(src).getVar("sum").asNum());
    }

    // ---- def colon+end -------------------------------------------------

    @Test
    void colonFunctionDef() {
        String src = """
                def add(a, b):
                    return a + b
                end
                """;
        assertEquals(7.0, call(src, "add", ScriptValue.of(3), ScriptValue.of(4)).asNum());
    }

    // ---- for colon+end, where-guard, destructuring -------------------------

    @Test
    void colonForOverArray() {
        String src = """
                total = 0
                for v in [1, 2, 3]:
                    total = total + v
                end
                """;
        assertEquals(6.0, run(src).getVar("total").asNum());
    }

    @Test
    void forWithWhereGuardBraceStyle() {
        // where-guards work in brace-style too — it's a for-header feature, not tied to colon/end.
        String src = """
                total = 0
                for i in range(0, 6) where i < 3 {
                    total = total + i
                }
                """;
        // range(0,6) = [0,1,2,3,4,5]; guard keeps 0,1,2 -> sum 3
        assertEquals(3.0, run(src).getVar("total").asNum());
    }

    @Test
    void forWithWhereGuardColonStyle() {
        String src = """
                total = 0
                for i in range(0, 6) where i < 3:
                    total = total + i
                end
                """;
        assertEquals(3.0, run(src).getVar("total").asNum());
    }

    @Test
    void forDestructuringOverMap() {
        String src = """
                keys_seen = ""
                sum_vals = 0
                for k, v in make_map("a", 1, "b", 2, "c", 3):
                    keys_seen = keys_seen + k
                    sum_vals = sum_vals + v
                end
                """;
        ScriptContext ctx = run(src);
        assertEquals(6.0, ctx.getVar("sum_vals").asNum(), "1+2+3");
        String keys = ctx.getVar("keys_seen").asStr();
        assertTrue(keys.contains("a") && keys.contains("b") && keys.contains("c"), "saw all keys: " + keys);
    }

    @Test
    void forSingleVarOverMapWalksKeys() {
        String src = """
                keys_seen = ""
                for k in make_map("x", 1, "y", 2):
                    keys_seen = keys_seen + k
                end
                """;
        String keys = run(src).getVar("keys_seen").asStr();
        assertTrue(keys.contains("x") && keys.contains("y"), "saw both keys: " + keys);
    }

    // ---- inline single-statement colon bodies (not a Polyloft form itself — polyloft-bvm's
    // own e2e tests never use one, always multi-line + "end" — but an unambiguous, useful
    // extra our engine adds on top: a single statement right after ':' on the SAME line needs
    // no "end" at all, e.g. a guard clause `if not_ready: return`). ---------------------------

    @Test
    void inlineIfNoElse() {
        String src = """
                x = 0
                if true: x = 1
                y = 2
                """;
        ScriptContext ctx = run(src);
        assertEquals(1.0, ctx.getVar("x").asNum());
        assertEquals(2.0, ctx.getVar("y").asNum(), "the statement AFTER the inline if must not be swallowed");
    }

    @Test
    void inlineIfFalseSkipsBody() {
        String src = """
                x = 0
                if false: x = 1
                """;
        assertEquals(0.0, run(src).getVar("x").asNum());
    }

    @Test
    void inlineReturnGuardClause() {
        // The exact shape this was built for: an early-return guard with no block at all.
        String src = """
                def classify(n):
                    if n <= 0: return 0
                    return n * 2
                end
                """;
        assertEquals(0.0, call(src, "classify", ScriptValue.of(-1)).asNum());
        assertEquals(0.0, call(src, "classify", ScriptValue.of(0)).asNum());
        assertEquals(10.0, call(src, "classify", ScriptValue.of(5)).asNum());
    }

    @Test
    void inlineIfFollowedByNextLineElse() {
        // The inline body has no closer of its own, but a following "else:" on the NEXT line
        // still chains correctly (only a same-line else after an inline body is unsupported).
        String src = """
                def sign(n):
                    if n < 0: return -1
                    else:
                        return 1
                    end
                end
                """;
        assertEquals(-1.0, call(src, "sign", ScriptValue.of(-5)).asNum());
        assertEquals(1.0, call(src, "sign", ScriptValue.of(5)).asNum());
    }

    @Test
    void inlineForLoop() {
        String src = """
                total = 0
                for v in [1, 2, 3]: total = total + v
                """;
        assertEquals(6.0, run(src).getVar("total").asNum());
    }

    @Test
    void inlineWhileLoop() {
        String src = """
                i = 0
                while i < 3: i = i + 1
                """;
        assertEquals(3.0, run(src).getVar("i").asNum());
    }

    @Test
    void inlineFunctionDef() {
        String src = """
                def double(n): return n * 2
                """;
        assertEquals(8.0, call(src, "double", ScriptValue.of(4)).asNum());
    }

    // ---- return-inside-for regression (the teleporter bug) -----------------

    @Test
    void returnInsideForExitsFunctionImmediately() {
        // Regression: a bare catch(Throwable) around the WHOLE for-loop body used to swallow
        // ReturnSignal, so `return` inside `for` looked like it "ran to completion" instead of
        // actually exiting — do_teleport() kept going and fired a second, contradictory message.
        String src = """
                def find_first(arr):
                    for v in arr:
                        if v > 2:
                            return v
                        end
                    end
                    return -1
                end
                """;
        ScriptValue arr = new ScriptValue.Array(List.of(
            ScriptValue.of(1), ScriptValue.of(2), ScriptValue.of(5), ScriptValue.of(9)));
        assertEquals(5.0, call(src, "find_first", arr).asNum(),
            "must return 5 immediately, not keep looping to 9 or falling through to -1");
    }
}
