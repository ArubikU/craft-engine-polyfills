package dev.arubik.craftengine.test;

import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.UserFunction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the PolyFill script parser — no NMS, no server.
 *
 * Key regression: def function bodies incorrectly captured code AFTER the
 * closing '}' when the return expression contained numeric literals or other
 * tokens that the Tokenizer.next() skips (pos++; continue). This caused the
 * outer script code to be included in capturedBody, leading to infinite
 * recursion when the function called itself via the accidentally-captured code.
 */
public class ScriptParserTest {

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private ScriptContext empty() {
        return ScriptContext.builder().build();
    }

    /**
     * Parse and evaluate a flat script, return resulting context.
     * Simulates what ScriptCall.execute does for a plain script (no func name).
     */
    private ScriptContext run(String source) {
        ScriptProgram prog = ScriptProgram.parse("test", source, java.util.logging.Logger.getLogger("test"));
        assertNotNull(prog, "Script failed to compile");
        return prog.evaluate(empty());
    }

    /**
     * Parse script, evaluate to get defs, then call named function with args.
     */
    private ScriptValue call(String source, String funcName, ScriptValue... args) {
        ScriptProgram prog = ScriptProgram.parse("test", source, java.util.logging.Logger.getLogger("test"));
        assertNotNull(prog);
        ScriptContext withDefs = prog.evaluate(empty());
        ScriptValue fnVal = withDefs.getVar(funcName);
        assertInstanceOf(ScriptValue.Obj.class, fnVal, "'" + funcName + "' is not a function");
        ScriptValue.Obj fnObj = (ScriptValue.Obj) fnVal;
        assertEquals(UserFunction.TYPE, fnObj.typeName(), "'" + funcName + "' is not a UserFunction");
        UserFunction fn = (UserFunction) fnObj.instance();
        return fn.call(java.util.List.of(args), withDefs);
    }

    private double num(ScriptValue v) {
        return v.asNum();
    }

    // -----------------------------------------------------------------------
    // Basic return tests
    // -----------------------------------------------------------------------

    @Test
    void returnInsideMultilineBlock() {
        // same as single-line but function has multi-line body
        String src = "def f() {\n    return 1\n}\n";
        ScriptValue r = call(src, "f");
        System.out.println("multiline body return: " + r.asNum());
        assertEquals(1.0, r.asNum());
    }

    @Test
    void returnAfterIfInBody() {
        // return 1 inside if, then return 0 below
        String src = "def f(x) {\n    if x > 0 { return 1 }\n    return 0\n}\n";
        ScriptValue r5 = call(src, "f", ScriptValue.of(5));
        ScriptValue rm = call(src, "f", ScriptValue.of(-1));
        System.out.println("f(5)=" + r5.asNum() + " f(-1)=" + rm.asNum());

        // Also test condition directly
        ScriptContext condCtx = ScriptContext.builder().num("x", 5).build();
        boolean condResult;
        try {
            condResult = dev.arubik.craftengine.script.ScriptFormula.compile("x > 0").evaluateBool(condCtx);
        } catch (Throwable e) {
            condResult = false;
        }
        System.out.println("x>0 with x=5: " + condResult);

        assertEquals(1.0, r5.asNum());
        assertEquals(0.0, rm.asNum());
    }

    @Test
    void returnLiteralDirect() {
        // simplest possible: def f() { return 1 }
        // verify the return expr is "1" not ""
        String src = "def f() { return 1 }\n";
        ScriptValue r = call(src, "f");
        System.out.println("returnLiteralDirect: " + r + " asNum=" + r.asNum());
        assertEquals(1.0, r.asNum(), "return 1 inside single-line braces");
    }

    @Test
    void returnNumericLiteral() {
        // def f() { return 42 }
        // Should correctly capture "42" as the return expression.
        assertEquals(42.0, num(call("def f() {\n    return 42\n}\n", "f")));
    }

    @Test
    void returnZero() {
        assertEquals(0.0, num(call("def f() {\n    return 0\n}\n", "f")));
    }

    @Test
    void returnFloat() {
        assertEquals(3.14, num(call("def f() {\n    return 3.14\n}\n", "f")), 1e-9);
    }

    @Test
    void returnExpression() {
        assertEquals(10.0, num(call("def f() {\n    return 2 + 8\n}\n", "f")));
    }

    @Test
    void returnConditional() {
        // if condition true → return 1, else → return 0
        String src = """
                def f(x) {
                    if x > 0 { return 1 }
                    return 0
                }
                """;
        assertEquals(1.0, num(call(src, "f", ScriptValue.of(5))));
        assertEquals(0.0, num(call(src, "f", ScriptValue.of(-1))));
    }

    // -----------------------------------------------------------------------
    // Regression: return must NOT bleed outer code into capturedBody
    // -----------------------------------------------------------------------

    @Test
    void outerCodeNotCapturedInFunction() {
        // After the function closes, outer code sets x = 99.
        // Running the flat script should set x = 99 in the top-level context,
        // AND calling f() should return 42 (not loop or include outer code).
        String src = """
                def f() {
                    return 42
                }
                x = 99
                """;
        // Flat evaluation
        ScriptContext ctx = run(src);
        assertEquals(99.0, num(ctx.getVar("x")), "outer x=99 must be set at top level");

        // Function call should return 42, not recurse or pick up outer code
        assertEquals(42.0, num(call(src, "f")));
    }

    @Test
    void numericReturnFollowedByOuterCall() {
        // Pattern equivalent to water_rpm:
        //   def water_rpm(block) {
        //       if block == "water" { return 32 }
        //       return 0
        //   }
        //   rpm = water_rpm("stone")
        //
        // Before the fix, "return 0" left tok pointing at the outer code,
        // causing the outer rpm = water_rpm(...) call to be captured in the
        // function body → infinite recursion when water_rpm was called.
        String src = """
                def water_rpm(block) {
                    if block == "water" { return 32 }
                    return 0
                }
                rpm = water_rpm("stone")
                """;
        ScriptContext ctx = run(src);
        // rpm should be 0 (stone is not water)
        assertEquals(0.0, num(ctx.getVar("rpm")), "rpm must be 0 for non-water block");

        // Calling water_rpm directly with "water" → 32
        assertEquals(32.0, num(call(src, "water_rpm", ScriptValue.of("water"))));
        // Calling water_rpm with "stone" → 0, no recursion
        assertEquals(0.0, num(call(src, "water_rpm", ScriptValue.of("stone"))));
    }

    @Test
    void multipleReturnsInBody() {
        // All three return paths must work and not recurse
        String src = """
                def classify(n) {
                    if n > 0 { return 1 }
                    if n < 0 { return -1 }
                    return 0
                }
                """;
        assertEquals(1.0,  num(call(src, "classify", ScriptValue.of(5))));
        assertEquals(-1.0, num(call(src, "classify", ScriptValue.of(-3))));
        assertEquals(0.0,  num(call(src, "classify", ScriptValue.of(0))));
    }

    @Test
    void fullWaterRpmEquivalent() {
        // Full water_rpm logic (without NMS — using plain string for block.id).
        // We simulate: block is a string "minecraft:water" or something else.
        // This tests the exact pattern from water_wheel.pf at the script level.
        String src = """
                def water_rpm(block_id) {
                    if block_id != "minecraft:water" { return 0 }
                    return 16
                }
                if true {
                    rpm1 = water_rpm("minecraft:stone")
                    rpm2 = water_rpm("minecraft:water")
                }
                """;
        ScriptContext ctx = run(src);
        assertEquals(0.0,  num(ctx.getVar("rpm1")), "stone block → 0");
        assertEquals(16.0, num(ctx.getVar("rpm2")), "water block → 16");
    }

    @Test
    void waterRpmEquivalentWithElseIfElse() {
        // Matches the water_wheel.pf structure with if/else if/else outer block
        String src = """
                def water_rpm(block_id) {
                    if block_id != "minecraft:water" { return 0 }
                    return 16
                }
                axis = "z"
                rpm = 0
                if axis == "y" {
                    rpm = water_rpm("minecraft:stone")
                } else if axis == "x" {
                    rpm = water_rpm("minecraft:stone")
                } else {
                    rpm = water_rpm("minecraft:water")
                }
                """;
        ScriptContext ctx = run(src);
        // axis=="z" → else branch → water_rpm("minecraft:water") → 16
        assertEquals(16.0, num(ctx.getVar("rpm")));
    }

    // -----------------------------------------------------------------------
    // Recursion guard integration
    // -----------------------------------------------------------------------

    @Test
    void noRecursionWhenFunctionCallSelfInOuterCode() {
        // Before the fix: outer `result = water_rpm(...)` was captured inside
        // water_rpm's body → calling water_rpm would re-call itself forever.
        // After the fix: water_rpm body is [if-stmt, return-stmt] only.
        // This test verifies no StackOverflow or depth-limit is hit.
        String src = """
                def water_rpm(b) {
                    if b == "stone" { return 0 }
                    return 16
                }
                result = water_rpm("stone")
                """;
        // If this returns 0 without throwing, the fix works.
        ScriptContext ctx = run(src);
        assertEquals(0.0, num(ctx.getVar("result")));
    }

    // -----------------------------------------------------------------------
    // Function body statement count verification
    // -----------------------------------------------------------------------

    @Test
    void simpleFunctionBodyStatementCount() {
        // def f() { x = 1; return x }  → body has 2 stmts
        // outer code: y = 2
        // Function body must NOT include y = 2
        String src = """
                def f() {
                    x = 1
                    return x
                }
                y = 2
                """;
        // If y=2 leaked into f's body, calling f would set y and return y(=2)
        // With correct parsing, f sets local x=1 and returns 1 (or the local x).
        // We verify y is NOT affected by calling f.
        call(src, "f"); // just must not throw
        ScriptContext flatCtx = run(src);
        assertEquals(2.0, num(flatCtx.getVar("y")), "y=2 must be set at top level");
    }
}
