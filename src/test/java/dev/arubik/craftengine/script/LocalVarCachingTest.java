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

    // --- Precise invalidation ------------------------------------------------------------------
    //
    // The cache is no longer wiped wholesale at every branch/loop; it keeps the entries for names
    // the construct provably cannot write (see ScriptClassCompiler#survivingCache). That is where
    // the real unboxing comes from — logic_gate.pf went from 44 context lookups to 10 — and it is
    // also the easiest thing here to get silently wrong: serving a stale raw double is a WRONG
    // ANSWER, not a crash. Each of these compares against the interpreter, which is the only
    // oracle that can't drift with the compiler.

    /**
     * Asserts the compiled def and the GENUINELY interpreted def agree, for every argument.
     *
     * <p>The kill switch is load-bearing, not decoration: {@code ScriptProgram} routes def calls
     * through this very compiler, so without flipping it off the "interpreted" side would be the
     * compiled method too and the comparison would be vacuous — it would pass no matter how wrong
     * the invalidation was. (Found exactly that way: an earlier version of this helper failed to
     * detect a deliberately sabotaged invalidation.)
     */
    private static void assertMatchesInterpreter(String src, String defName, String suffix,
                                                  String paramName, ScriptValue... args) throws Exception {
        var m = compiledDef(src, defName, suffix);
        for (ScriptValue arg : args) {
            ScriptContext.Builder b = ScriptContext.builder().val(paramName, arg);
            ScriptValue compiled = (ScriptValue) m.invoke(null, b);

            boolean wasEnabled = ScriptClassCompiler.CLASS_JIT_ENABLED;
            ScriptValue interp;
            try {
                ScriptClassCompiler.CLASS_JIT_ENABLED = false;
                interp = interpretedResult(src, defName, ScriptContext.builder().build(), arg);
            } finally {
                ScriptClassCompiler.CLASS_JIT_ENABLED = wasEnabled;
            }
            assertEquals(interp.asStr(), compiled.asStr(), "arg=" + arg.asStr());
        }
    }

    @Test
    void aNameAssignedInOnlyOneBranchIsNotServedStaleAfterTheMerge() throws Exception {
        // `n` is cached before the chain, reassigned in ONE arm. After the merge, the pre-chain slot
        // still holds the OLD value — keeping it cached would return 1 even when the branch ran.
        String src = """
                def pick(flag):
                    n = 1
                    other = 7
                    if flag:
                        n = 100
                    end
                    return n + other
                end
                """;
        assertMatchesInterpreter(src, "pick", "one-branch", "flag",
                ScriptValue.of(true), ScriptValue.of(false));
    }

    @Test
    void siblingClausesAssigningTheSameNameDoNotSeeEachOther() throws Exception {
        String src = """
                def pick(mode):
                    r = 0
                    if mode == 0:
                        r = 10
                    else if mode == 1:
                        r = 20
                    else:
                        r = 30
                    end
                    return r
                end
                """;
        assertMatchesInterpreter(src, "pick", "siblings", "mode",
                ScriptValue.of(0.0), ScriptValue.of(1.0), ScriptValue.of(2.0));
    }

    @Test
    void aNameNoBranchAssignsStaysCachedAndStaysCorrect() throws Exception {
        // The whole point of the finer invalidation: `keep` survives the chain because nothing in
        // it writes `keep`. It must still produce the right answer.
        String src = """
                def calc(flag):
                    keep = 5
                    n = 0
                    if flag:
                        n = keep + 1
                    else:
                        n = keep + 2
                    end
                    return n * keep
                end
                """;
        assertMatchesInterpreter(src, "calc", "kept", "flag",
                ScriptValue.of(true), ScriptValue.of(false));
    }

    @Test
    void aNameAssignedInsideALoopIsNotServedStaleAfterOrAcrossIterations() throws Exception {
        String src = """
                def run(items):
                    total = 0
                    seen = 0
                    for x in items:
                        total = total + x
                        seen = seen + 1
                    end
                    return total * 100 + seen
                end
                """;
        var items = new ScriptValue.Array(java.util.List.of(
                ScriptValue.of(1.0), ScriptValue.of(2.0), ScriptValue.of(3.0)));
        assertMatchesInterpreter(src, "run", "loop-assign", "items", items);
    }

    @Test
    void aNameTheLoopBodyNeverWritesStaysCachedAcrossIterations() throws Exception {
        String src = """
                def run(items):
                    factor = 10
                    total = 0
                    for x in items:
                        total = total + x * factor
                    end
                    return total
                end
                """;
        var items = new ScriptValue.Array(java.util.List.of(
                ScriptValue.of(1.0), ScriptValue.of(2.0), ScriptValue.of(3.0)));
        assertMatchesInterpreter(src, "run", "loop-keep", "items", items);
    }

    @Test
    void anAssignmentNestedInsideABranchInsideALoopStillInvalidates() throws Exception {
        // The recursion in collectAssignedNames is what this covers: `acc` is written two levels
        // down. Missing that recursion is the failure mode that silently serves a stale double.
        String src = """
                def run(items):
                    acc = 0
                    for x in items:
                        if x > 1:
                            acc = acc + x
                        end
                    end
                    return acc
                end
                """;
        var items = new ScriptValue.Array(java.util.List.of(
                ScriptValue.of(1.0), ScriptValue.of(2.0), ScriptValue.of(3.0)));
        assertMatchesInterpreter(src, "run", "nested-assign", "items", items);
    }

    @Test
    void aLoopVariableIsNotCachedAcrossIterations() throws Exception {
        // The loop var is rebound every pass; caching it would freeze the first element.
        String src = """
                def run(items):
                    out = 0
                    for x in items:
                        out = out * 10 + x
                    end
                    return out
                end
                """;
        var items = new ScriptValue.Array(java.util.List.of(
                ScriptValue.of(1.0), ScriptValue.of(2.0), ScriptValue.of(3.0)));
        assertMatchesInterpreter(src, "run", "loop-var", "items", items);
    }

    @Test
    void stringVariableCachingAlsoSkipsTheScriptContextRoundTrip() throws Exception {
        // Same fast path as the NUM case, but for a string (ANY-typed) value — the value is
        // already a boxed ScriptValue by the time it's computed, so caching it is a plain
        // ALOAD/ASTORE of that SAME reference (no re-boxing on the reads, only the one box at
        // the literal's own construction, unavoidable since the Builder always needs a
        // ScriptValue either way).
        String src = """
                def greet():
                    a = "hello"
                    b = a
                    return b + "!"
                end
                """;
        ScriptClassCompiler.Compiled compiled = compile(src, "strchain");
        assertNotNull(compiled);
        var m = compiled.methodsByDefName().get("greet");
        assertNotNull(m);
        assertEquals("hello!", ((ScriptValue) m.invoke(null, ScriptContext.builder())).asStr());

        ClassReader cr = new ClassReader(compiled.classBytes());
        StringWriter sw = new StringWriter();
        cr.accept(new TraceClassVisitor(new PrintWriter(sw)), 0);
        String disassembly = sw.toString();

        long getVarCalls = disassembly.lines().filter(l -> l.contains("getVar") || l.contains("getClassInstance")).count();
        assertEquals(0, getVarCalls,
                "a straight-line chain of string assigns/reads should ALSO need zero getClassInstance/getVar calls:\n" + disassembly);
    }
}
