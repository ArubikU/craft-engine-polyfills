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
 * Verifies the compile-time {@link PolyType} dot-call specialization in {@link
 * ScriptBytecodeCompiler}'s {@code dotMethodCall}/{@code dotPropertyGet} — the direct-dispatch
 * fast path added so a formula like {@code Server.get_player(name)} compiles to a real call on the
 * resolved {@link PolyType.MethodHandler} instead of routing through {@link
 * ScriptFormula#memberCall}.
 *
 * <p>The bare JUnit environment never runs {@code ScriptBootstrap.init()}, so none of the REAL
 * types ("Machine", "Player", "Server", ...) are registered here — every test below registers its
 * OWN uniquely-named fake type first, which is exactly what makes this a genuine test of the
 * specialization logic itself (compile-time PolyTypeRegistry lookup, runtime type guard, fallback)
 * rather than an accident of what happens to be registered.
 */
class PolyTypeSpecializationTest {

    @Test
    void happyPathCallsTheResolvedHandlerDirectly() {
        PolyTypeRegistry.define("SpecHappyType")
                .method("greet", (o, a) -> ScriptValue.of("hello " + a.get(0).asStr()));

        ScriptFormula.Node node = ScriptBytecodeCompiler.tryCompile("SpecHappyType.greet(\"world\")");
        assertNotNull(node, "should still compile via the JIT (not bail to the interpreter)");

        ScriptContext ctx = ScriptContext.builder()
                .typed("SpecHappyType", new Object())
                .build();
        assertEquals("hello world", node.eval(ctx).asStr());
    }

    @Test
    void happyPathPropertyAccess() {
        record Holder(double value) {}
        PolyTypeRegistry.define("SpecHappyPropType")
                .property("value", o -> ScriptValue.of(((Holder) o).value()));

        ScriptFormula.Node node = ScriptBytecodeCompiler.tryCompile("SpecHappyPropType.value + 1");
        assertNotNull(node);

        ScriptContext ctx = ScriptContext.builder()
                .typed("SpecHappyPropType", new Holder(41))
                .build();
        assertEquals(42.0, node.eval(ctx).asNum());
    }

    @Test
    void mismatchedRuntimeTypeFallsBackCorrectly() {
        // The type IS registered with this method — specialization WILL be attempted at compile
        // time — but at runtime the bound value under this name is a COMPLETELY DIFFERENT
        // ScriptValue.Obj typeName. The guard must catch this and fall back, not blindly call the
        // wrong handler with the wrong instance shape.
        PolyTypeRegistry.define("SpecMismatchType")
                .method("greet", (o, a) -> ScriptValue.of("SHOULD NOT RUN"));
        PolyTypeRegistry.define("SpecOtherType")
                .method("greet", (o, a) -> ScriptValue.of("other type's greet"));

        ScriptFormula.Node node = ScriptBytecodeCompiler.tryCompile("SpecMismatchType.greet(\"x\")");
        assertNotNull(node);

        ScriptContext ctx = ScriptContext.builder()
                // Bound under the name "SpecMismatchType" but with typeName "SpecOtherType" —
                // exactly the shape that makes the runtime guard's typeName() check the one thing
                // standing between correct behavior and calling the wrong handler.
                .val("SpecMismatchType", ScriptValue.ofObj("SpecOtherType", new Object()))
                .build();
        assertEquals("other type's greet", node.eval(ctx).asStr());
    }

    @Test
    void polyClassInstanceUnderTheSameNameBypassesTheSpecializedHandler() {
        // The real-world precedent this guards against: ScriptValue#callMethod/#getProperty check
        // "instance() instanceof PolyClass" FIRST, unconditionally, before ever consulting
        // PolyTypeRegistry — because a script-visible name (their own example was "World") can be
        // bound to a PolyClass instance that owns its OWN dispatch, even though PolyTypeRegistry
        // ALSO has a same-named type registered with real methods. If the specialization skipped
        // this check, it would call the WRONG (PolyType-registered) handler instead of the
        // PolyClass's own, silently diverging from the correct interpreter behavior.
        PolyTypeRegistry.define("SpecPolyClassType")
                .method("greet", (o, a) -> ScriptValue.of("WRONG HANDLER RAN"));

        class FakePolyClass implements PolyClass {
            @Override public ScriptValue get(String property) { return ScriptValue.NULL; }
            @Override public ScriptValue call(String method, List<ScriptValue> args) {
                return ScriptValue.of("polyclass's own dispatch");
            }
        }

        ScriptFormula.Node node = ScriptBytecodeCompiler.tryCompile("SpecPolyClassType.greet(\"x\")");
        assertNotNull(node);

        ScriptContext ctx = ScriptContext.builder()
                .val("SpecPolyClassType", ScriptValue.ofObj("SpecPolyClassType", new FakePolyClass()))
                .build();
        assertEquals("polyclass's own dispatch", node.eval(ctx).asStr());
    }

    @Test
    void nullReceiverStillReturnsNull() {
        PolyTypeRegistry.define("SpecNullType")
                .method("greet", (o, a) -> ScriptValue.of("SHOULD NOT RUN"));

        ScriptFormula.Node node = ScriptBytecodeCompiler.tryCompile("SpecNullType.greet(\"x\")");
        assertNotNull(node);

        ScriptContext ctx = ScriptContext.builder().build(); // "SpecNullType" bound to nothing
        assertEquals(ScriptValue.NULL, node.eval(ctx));
    }

    @Test
    void happyPathBytecodeActuallyTakesTheDirectDispatchFastPath() throws Exception {
        // The other tests prove the RESULT is correct in every scenario — but the fast path and
        // the fallback are semantically equivalent by design, so a passing result alone doesn't
        // prove the fast path actually ran. This inspects the real generated bytecode (same
        // disassembly technique ScriptClassCompilerTest uses) to confirm the direct PolyType/
        // MethodHandler dispatch is genuinely present and reachable BEFORE any
        // ScriptFormula.memberCall — memberCall itself still appears exactly once, as the
        // deliberate narrow fallback for a runtime type mismatch (see emitPolyTypeGuard's own
        // doc); this asserts the ORDERING, not memberCall's total absence.
        PolyTypeRegistry.define("SpecDisasmType")
                .method("greet", (o, a) -> ScriptValue.of("hi"));

        String src = "def test_spec():\n    return SpecDisasmType.greet(\"world\")\nend\n";
        ScriptProgram prog = ScriptProgram.parse("spec-disasm", src, Logger.getLogger("test"));
        ScriptClassCompiler.Compiled compiled =
                ScriptClassCompiler.tryCompile("spec/disasm-" + System.identityHashCode(new Object()),
                        prog.statementsForCompiler());
        assertNotNull(compiled);
        assertTrue(compiled.methodsByDefName().containsKey("test_spec"));

        ClassReader cr = new ClassReader(compiled.classBytes());
        StringWriter sw = new StringWriter();
        cr.accept(new TraceClassVisitor(new PrintWriter(sw)), 0);
        String disassembly = sw.toString();

        int resolveMethodIdx = disassembly.indexOf("resolveMethod");
        int memberCallIdx = disassembly.indexOf("memberCall");
        assertTrue(disassembly.contains("PolyTypeRegistry"), "should call PolyTypeRegistry.get directly");
        assertTrue(resolveMethodIdx >= 0, "should call PolyType.resolveMethod directly");
        assertTrue(memberCallIdx >= 0, "the narrow fallback should still exist, for a genuine type mismatch");
        assertTrue(resolveMethodIdx < memberCallIdx,
                "the direct-dispatch fast path must be reachable BEFORE the fallback, not the other way around:\n" + disassembly);
    }

    @Test
    void unregisteredMethodOnARegisteredTypeFallsBackCorrectly() {
        PolyTypeRegistry.define("SpecPartialType")
                .method("known_method", (o, a) -> ScriptValue.of("known"));

        // "unknown_method" was never registered on this type — resolveMethod returns null at
        // compile time, so this should take the ordinary (non-specialized) path and still resolve
        // to NULL at runtime, exactly like calling any undefined method does.
        ScriptFormula.Node node = ScriptBytecodeCompiler.tryCompile("SpecPartialType.unknown_method(\"x\")");
        assertNotNull(node);

        ScriptContext ctx = ScriptContext.builder().typed("SpecPartialType", new Object()).build();
        assertEquals(ScriptValue.NULL, node.eval(ctx));
    }
}
