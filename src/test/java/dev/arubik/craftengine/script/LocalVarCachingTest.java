package dev.arubik.craftengine.script;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies {@link ScriptClassCompiler}'s native-local caching for NUM/BOOL script variables (see
 * {@code MethodCtx#cachedVars}/{@code emitAssign}'s own doc): a variable whose assigned value is
 * provably NUM/BOOL gets cached in a raw JVM local, so the NEXT statement's read of that name skips
 * {@code ScriptContext.getClassInstance}/{@code getVar}/boxing entirely — down to a bare
 * {@code DLOAD}/{@code ILOAD}. The SAFETY-CRITICAL part is invalidation: the cache must be dropped
 * the instant execution crosses into or out of any {@code if}/{@code for}/{@code while}, since a
 * single static local can't represent "whichever branch/iteration actually ran". A bug here means
 * silently WRONG values, not a crash — the worst failure mode — so this specifically targets the
 * branch-reassignment case, differentially compared against the interpreter, plus a bytecode-shape
 * check proving the fast path is genuinely taken for the straight-line case.
 */
class LocalVarCachingTest {

    private static final Logger LOG = Logger.getLogger("test");

    private static ScriptClassCompiler.Compiled compile(String defSrc, String suffix) {
        ScriptProgram prog = ScriptProgram.parse("localvar-" + suffix, defSrc, LOG);
        return ScriptClassCompiler.tryCompile("localvar/" + suffix + "-" + System.identityHashCode(new Object()),
                prog.statementsForCompiler());
    }

    private static java.lang.reflect.Method compiledDef(String defSrc, String defName, String suffix) throws Exception {
        ScriptClassCompiler.Compiled c = compile(defSrc, suffix);
        assertNotNull(c, "expected this def to compile");
        java.lang.reflect.Method m = c.methodsByDefName().get(defName);
        assertNotNull(m, defName + " should have compiled");
        return m;
    }

    private static ScriptValue interpretedResult(String defSrc, String defName, ScriptContext ctx,
                                                  ScriptValue... args) throws Exception {
        ScriptProgram prog = ScriptProgram.parse("localvar-interp", defSrc, LOG);
        ScriptContext withDefs = prog.evaluate(ctx);
        ScriptValue fnVal = withDefs.getVar(defName);
        assertTrue(fnVal instanceof ScriptValue.Obj o && o.typeName().equals(UserFunction.TYPE));
        UserFunction fn = (UserFunction) ((ScriptValue.Obj) fnVal).instance();
        return fn.call(java.util.List.of(args), withDefs);
    }

    @Test
    void branchReassignmentIsNotStaleAfterMergePoint() throws Exception {
        // n is assigned DIFFERENTLY in each branch — if caching survived the branch instead of
        // being invalidated, reading n afterward could see a slot from the WRONG branch (or an
        // uninitialized one), not whatever branch actually ran.
        String src = """
                def pick(flag):
                    n = 1
                    if flag:
                        n = 100
                    else:
                        n = 200
                    end
                    return n + 1
                end
                """;
        var m = compiledDef(src, "pick", "branch");

        for (boolean flag : new boolean[]{true, false}) {
            ScriptContext.Builder b = ScriptContext.builder().val("flag", ScriptValue.of(flag));
            ScriptValue compiledResult = (ScriptValue) m.invoke(null, b);
            ScriptValue interp = interpretedResult(src, "pick", ScriptContext.builder().build(),
                    ScriptValue.of(flag));
            assertEquals(interp.asNum(), compiledResult.asNum(), "flag=" + flag);
        }
        // Concrete expected values, not just "matches the interpreter" — pins the actual semantics.
        ScriptContext.Builder bt = ScriptContext.builder().val("flag", ScriptValue.of(true));
        assertEquals(101.0, ((ScriptValue) m.invoke(null, bt)).asNum());
        ScriptContext.Builder bf = ScriptContext.builder().val("flag", ScriptValue.of(false));
        assertEquals(201.0, ((ScriptValue) m.invoke(null, bf)).asNum());
    }

    @Test
    void reassignmentInsideAForLoopIsNotStaleAfterTheLoop() throws Exception {
        String src = """
                def sum_then_double(items):
                    total = 0
                    for x in items:
                        total = total + x
                    end
                    return total * 2
                end
                """;
        var m = compiledDef(src, "sum_then_double", "forloop");

        var items = new ScriptValue.Array(java.util.List.of(ScriptValue.of(1.0), ScriptValue.of(2.0), ScriptValue.of(3.0)));
        ScriptContext.Builder b = ScriptContext.builder().val("items", items);
        ScriptValue compiledResult = (ScriptValue) m.invoke(null, b);
        assertEquals(12.0, compiledResult.asNum()); // (1+2+3)*2

        ScriptValue interp = interpretedResult(src, "sum_then_double", ScriptContext.builder().build(), items);
        assertEquals(interp.asNum(), compiledResult.asNum());
    }

    @Test
    void sequentialAssignsActuallyUseTheCachedFastPath() throws Exception {
        // Straight-line, no branches — every read of `b` after its assignment should hit the
        // NUM cache directly (a bare DLOAD), not ScriptContext.getClassInstance/getVar.
        String src = """
                def chain():
                    a = 5
                    b = a + 1
                    c = b * 2
                    return c
                end
                """;
        ScriptClassCompiler.Compiled compiled = compile(src, "chain");
        assertNotNull(compiled);
        var m = compiled.methodsByDefName().get("chain");
        assertNotNull(m);
        assertEquals(12.0, ((ScriptValue) m.invoke(null, ScriptContext.builder())).asNum()); // (5+1)*2

        ClassReader cr = new ClassReader(compiled.classBytes());
        StringWriter sw = new StringWriter();
        cr.accept(new TraceClassVisitor(new PrintWriter(sw)), 0);
        String disassembly = sw.toString();

        long getVarCalls = disassembly.lines().filter(l -> l.contains("getVar") || l.contains("getClassInstance")).count();
        assertEquals(0, getVarCalls,
                "a straight-line chain of NUM assigns/reads should need ZERO getClassInstance/getVar calls:\n" + disassembly);
        assertTrue(disassembly.contains("DLOAD"), "should actually load cached doubles from JVM locals:\n" + disassembly);
    }
}
