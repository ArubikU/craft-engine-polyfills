package dev.arubik.craftengine.script;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies {@link PolyDispatch}, the {@code invokedynamic} inline cache used for member access whose
 * receiver type ISN'T known at compile time — a plain variable, or any chained hop:
 *
 * <pre>
 *   contraption = Machine.contraption
 *   contraption.set_spin(axis, rpm)     # receiver type unknown statically
 *   Machine.contraption.blocks()        # chained hop, likewise
 * </pre>
 *
 * These used to call {@link ScriptFormula#memberCall} on every single evaluation. Now each call site
 * links itself into a guarded direct call on the type it observes.
 *
 * <p>The interesting tests here are not the happy path (a cache that returns the right value once is
 * easy) but the ones where a cache is DANGEROUS: a receiver whose type changes between calls, a
 * {@link PolyClass} that owns its own dispatch, and a registry mutation that must invalidate an
 * already-warm cache. A stale inline cache produces silently wrong values, not a crash.
 */
class PolyDispatchTest {

    private static ScriptValue evalWith(ScriptFormula.Node node, String varName, ScriptValue receiver) {
        return node.eval(ScriptContext.builder().val(varName, receiver).build());
    }

    @Test
    void aVariableReceiverDispatchesCorrectlyAndKeepsWorkingWhenWarm() {
        PolyTypeRegistry.define("DispatchGreetType")
                .method("greet", (o, a) -> ScriptValue.of("hi " + a.get(0).asStr()));

        ScriptFormula.Node node = ScriptBytecodeCompiler.tryCompile("thing.greet(\"world\")");
        assertNotNull(node);
        ScriptValue receiver = ScriptValue.ofObj("DispatchGreetType", new Object());

        // Several calls: the first links the cache, the rest must take the cached path and still be
        // right. A cache that returned a stale value would show up from the second call on.
        for (int i = 0; i < 5; i++) {
            assertEquals("hi world", evalWith(node, "thing", receiver).asStr(), "call #" + i);
        }
    }

    @Test
    void aReceiverWhoseTypeChangesBetweenCallsStillDispatchesToTheRightHandler() {
        // The guard's whole job. Warm the cache on one type, then hand the SAME call site a
        // different one — it must not run the first type's handler.
        PolyTypeRegistry.define("DispatchPolyAType").method("id", (o, a) -> ScriptValue.of("A"));
        PolyTypeRegistry.define("DispatchPolyBType").method("id", (o, a) -> ScriptValue.of("B"));
        PolyTypeRegistry.define("DispatchPolyCType").method("id", (o, a) -> ScriptValue.of("C"));

        ScriptFormula.Node node = ScriptBytecodeCompiler.tryCompile("thing.id()");
        assertNotNull(node);

        ScriptValue a = ScriptValue.ofObj("DispatchPolyAType", new Object());
        ScriptValue b = ScriptValue.ofObj("DispatchPolyBType", new Object());
        ScriptValue c = ScriptValue.ofObj("DispatchPolyCType", new Object());

        // Interleaved, and repeated past the megamorphic cutoff, so both the guard chain and the
        // pinned-generic state get exercised.
        for (int i = 0; i < 4; i++) {
            assertEquals("A", evalWith(node, "thing", a).asStr(), "round " + i);
            assertEquals("B", evalWith(node, "thing", b).asStr(), "round " + i);
            assertEquals("C", evalWith(node, "thing", c).asStr(), "round " + i);
        }
    }

    @Test
    void aWarmCacheIsInvalidatedWhenTheRegistryChanges() {
        // The staleness hazard, for inline caches. A cached handler must not outlive the
        // registration it came from — PolyTypeRegistry mutation invalidates the SwitchPoint every
        // cached target is guarded by.
        PolyType type = PolyTypeRegistry.define("DispatchRefreshType")
                .method("value", (o, a) -> ScriptValue.of("before"));

        ScriptFormula.Node node = ScriptBytecodeCompiler.tryCompile("thing.value()");
        assertNotNull(node);
        ScriptValue receiver = ScriptValue.ofObj("DispatchRefreshType", new Object());

        // Warm it properly first — a cache that was never linked would pass this trivially.
        for (int i = 0; i < 3; i++) assertEquals("before", evalWith(node, "thing", receiver).asStr());

        type.replaceMethod("value", (o, a) -> ScriptValue.of("after"));
        assertEquals("after", evalWith(node, "thing", receiver).asStr(),
                "a warm inline cache must be invalidated by a registry mutation");

        // And it must re-link cleanly rather than staying pinned to the generic path.
        for (int i = 0; i < 3; i++) assertEquals("after", evalWith(node, "thing", receiver).asStr());
    }

    @Test
    void aPolyClassReceiverNeverTakesTheCachedPolyTypeHandler() {
        // Same load-bearing carve-out the static specialization has: a PolyClass owns its own
        // dispatch and must never be routed to a same-named PolyType's handler.
        PolyTypeRegistry.define("DispatchPolyClassType")
                .method("greet", (o, a) -> ScriptValue.of("WRONG HANDLER RAN"));

        class FakePolyClass implements PolyClass {
            @Override public ScriptValue get(String property) { return ScriptValue.NULL; }
            @Override public ScriptValue call(String method, List<ScriptValue> args) {
                return ScriptValue.of("polyclass's own dispatch");
            }
        }

        ScriptFormula.Node node = ScriptBytecodeCompiler.tryCompile("thing.greet()");
        assertNotNull(node);
        ScriptValue receiver = ScriptValue.ofObj("DispatchPolyClassType", new FakePolyClass());

        for (int i = 0; i < 3; i++) {
            assertEquals("polyclass's own dispatch", evalWith(node, "thing", receiver).asStr(), "call #" + i);
        }
    }

    @Test
    void aPropertyOnAVariableReceiverIsCachedAndInvalidatedToo() {
        PolyType type = PolyTypeRegistry.define("DispatchPropType")
                .property("v", o -> ScriptValue.of(1.0));

        ScriptFormula.Node node = ScriptBytecodeCompiler.tryCompile("thing.v + 0");
        assertNotNull(node);
        ScriptValue receiver = ScriptValue.ofObj("DispatchPropType", new Object());

        for (int i = 0; i < 3; i++) assertEquals(1.0, evalWith(node, "thing", receiver).asNum());
        type.replaceProperty("v", o -> ScriptValue.of(42.0));
        assertEquals(42.0, evalWith(node, "thing", receiver).asNum());
    }

    @Test
    void nonObjReceiversAndUnknownMembersStillBehaveExactlyAsBefore() {
        PolyTypeRegistry.define("DispatchPartialType").method("known", (o, a) -> ScriptValue.of("k"));

        // An unresolvable member on a registered type, and a receiver that isn't an Obj at all —
        // both must fall through to the ordinary path, not be cached into something wrong.
        ScriptFormula.Node unknown = ScriptBytecodeCompiler.tryCompile("thing.unknown_member()");
        assertNotNull(unknown);
        ScriptValue obj = ScriptValue.ofObj("DispatchPartialType", new Object());
        for (int i = 0; i < 3; i++) assertEquals(ScriptValue.NULL, evalWith(unknown, "thing", obj));

        ScriptFormula.Node onString = ScriptBytecodeCompiler.tryCompile("thing.upper()");
        assertNotNull(onString);
        // Whatever a string receiver does here, it must do it CONSISTENTLY across repeated calls —
        // the cache must not change the answer between the first evaluation and later ones.
        ScriptValue str = ScriptValue.of("abc");
        ScriptValue first = evalWith(onString, "thing", str);
        for (int i = 0; i < 3; i++) {
            assertEquals(first, evalWith(onString, "thing", str), "repeat #" + i);
        }
    }

    @Test
    void aChainedHopAlsoDispatchesThroughTheInlineCache() {
        PolyTypeRegistry.define("DispatchInnerType").method("name", (o, a) -> ScriptValue.of("inner"));
        PolyTypeRegistry.define("DispatchOuterType")
                .property("inner", o -> ScriptValue.ofObj("DispatchInnerType", new Object()));

        ScriptFormula.Node node = ScriptBytecodeCompiler.tryCompile("DispatchOuterType.inner.name()");
        assertNotNull(node);
        ScriptContext ctx = ScriptContext.builder().typed("DispatchOuterType", new Object()).build();
        for (int i = 0; i < 3; i++) assertEquals("inner", node.eval(ctx).asStr(), "call #" + i);
    }

    @Test
    void generatedBytecodeUsesInvokedynamicRatherThanADirectMemberCall() throws Exception {
        PolyTypeRegistry.define("DispatchDisasmType").method("greet", (o, a) -> ScriptValue.of("hi"));

        String src = "def test_dyn(thing):\n    return thing.greet()\nend\n";
        ScriptProgram prog = ScriptProgram.parse("dispatch-disasm", src, Logger.getLogger("test"));
        ScriptClassCompiler.Compiled compiled = ScriptClassCompiler.tryCompile(
                "dispatch/disasm-" + System.identityHashCode(new Object()), prog.statementsForCompiler());
        assertNotNull(compiled);

        ClassReader cr = new ClassReader(compiled.classBytes());
        StringWriter sw = new StringWriter();
        cr.accept(new TraceClassVisitor(new PrintWriter(sw)), 0);
        String disassembly = sw.toString();

        assertTrue(disassembly.contains("INVOKEDYNAMIC"),
                "an unknown-type receiver should compile to an invokedynamic call site:\n" + disassembly);
        assertTrue(disassembly.contains("bootstrapCall"),
                "it should link through PolyDispatch's bootstrap:\n" + disassembly);
        assertFalse(disassembly.contains("ScriptFormula.memberCall"),
                "the unconditional per-evaluation memberCall should be gone from generated code:\n" + disassembly);
    }
}
