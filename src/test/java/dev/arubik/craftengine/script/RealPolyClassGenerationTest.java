package dev.arubik.craftengine.script;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Coverage guarantee for {@link PolyClassGenerator}: EVERY registered member of a type gets a
 * generated Java method. A member missing from the generated maps would silently fall back to
 * {@code memberCall} forever — the "the JIT quietly dropped things" failure that is invisible
 * without a test like this.
 *
 * <p>This exercises a large synthetic type rather than the real {@code MachineType}, deliberately.
 * {@link PolyTypeRegistry} is process-global, and registering the real MachineType here would
 * REPLACE the small stand-in "Machine" that several other test classes register in their own
 * {@code @BeforeAll} — their fake instances would then be handed to real handlers that cast to
 * {@code MachineType.MachineRef}, breaking unrelated tests depending on class execution order. The
 * real MachineType's generated class is verified out-of-band instead (see
 * {@code scratch/polyclasses-real}, produced with {@code -PpolyclassDump}): 114 members, none
 * dropped.
 *
 * <p>The synthetic type covers every shape the generator has to handle — required-arg typed methods
 * across arities, optional-arg typed methods, untyped methods, properties, and inheritance.
 */
class RealPolyClassGenerationTest {

    private static final int FILLER_METHODS = 60;
    private static final int FILLER_PROPS = 40;

    @Test
    void everyMemberOfALargeTypeGetsAGeneratedMethod() {
        PolyTypeRegistry.define("CoverageParentType")
                .method("inherited_method", (o, a) -> ScriptValue.of("parent"))
                .property("inherited_prop", o -> ScriptValue.of("parent"));

        PolyType type = PolyTypeRegistry.define("CoverageBigType", "CoverageParentType");

        // Every registration shape the generator distinguishes.
        type.methodTyped0("t0", TypeCodecs.BOOL, (Object o) -> true);
        type.methodTyped1("t1", TypeCodecs.STRING, TypeCodecs.STRING, "", (Object o, String s) -> s);
        type.methodTyped3("t3", TypeCodecs.DOUBLE, TypeCodecs.BOOL, TypeCodecs.RAW, TypeCodecs.DOUBLE, 0.0,
                (Object o, Double d, Boolean b, ScriptValue v) -> d);
        type.methodTyped7("t7", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.STRING,
                TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object o, String a1, String a2, String a3, String a4, String a5, String a6, String a7) -> true);
        type.methodTypedOpt1("o1", TypeCodecs.DOUBLE, 7.0, TypeCodecs.DOUBLE, (Object o, Double d) -> d);
        type.methodTypedOpt5("o5", TypeCodecs.STRING, null, TypeCodecs.STRING, null, TypeCodecs.STRING, null,
                TypeCodecs.STRING, null, TypeCodecs.STRING, null, TypeCodecs.BOOL,
                (Object o, String a1, String a2, String a3, String a4, String a5) -> a1 == null);
        type.methodTypedOpt7("o7", TypeCodecs.STRING, null, TypeCodecs.STRING, null, TypeCodecs.STRING, null,
                TypeCodecs.STRING, null, TypeCodecs.STRING, null, TypeCodecs.STRING, null,
                TypeCodecs.STRING, null, TypeCodecs.BOOL,
                (Object o, String a1, String a2, String a3, String a4, String a5, String a6, String a7) -> a1 == null);
        // A method the typed API genuinely cannot express (variadic) — must still be generated.
        type.method("variadic", (o, a) -> ScriptValue.of(a.size()));

        for (int i = 0; i < FILLER_METHODS; i++) {
            int n = i;
            type.method("filler_method_" + i, (o, a) -> ScriptValue.of(n));
        }
        for (int i = 0; i < FILLER_PROPS; i++) {
            int n = i;
            type.property("filler_prop_" + i, o -> ScriptValue.of(n));
        }

        PolyClassGenerator.buildAll();
        PolyClassGenerator.GeneratedPolyClass generated = PolyClassGenerator.getOrGenerate("CoverageBigType");
        assertNotNull(generated, "a large type must generate a PolyClass");

        int methodCount = type.allMethodNames().size();
        int propCount = type.allPropertyNames().size();
        assertTrue(methodCount > FILLER_METHODS && propCount > FILLER_PROPS, "sanity: the type should be large");

        // EVERY registered member must have a generated Java method — typed (native signature) or
        // untyped (erased shim). Note a TYPED method is present in BOTH maps: it gets the native
        // method AND an erased companion shim, because a call site may pass fewer args than the
        // fixed native arity (see PolyClassGenerator). So compare the UNION of keys, not the sum.
        Set<String> covered = new LinkedHashSet<>(generated.typedMethods().keySet());
        covered.addAll(generated.untypedMethods().keySet());
        for (String m : type.allMethodNames()) {
            assertTrue(covered.contains(m), "method '" + m + "' got no generated method on the PolyClass");
        }
        for (String p : type.allPropertyNames()) {
            assertTrue(generated.properties().containsKey(p),
                    "property '" + p + "' got no generated accessor on the PolyClass");
        }
        assertEquals(methodCount, covered.size(), "every method accounted for exactly once");
        assertEquals(propCount, generated.properties().size());

        // The typed registrations really did get native signatures, not just erased shims.
        for (String typedName : new String[]{"t0", "t1", "t3", "t7", "o1", "o5", "o7"}) {
            assertTrue(generated.typedMethods().containsKey(typedName),
                    "'" + typedName + "' should have a natively-typed generated method");
        }
        assertFalse(generated.typedMethods().containsKey("variadic"),
                "a variadic method cannot be natively typed and must use the erased shim");

        // Inherited members remain reachable through the child, and the class loaded and resolved
        // its handler fields (generate() runs refresh() before returning), so a large type hits no
        // classfile limit.
        assertTrue(covered.contains("inherited_method"));
        assertTrue(generated.properties().containsKey("inherited_prop"));
    }
}
