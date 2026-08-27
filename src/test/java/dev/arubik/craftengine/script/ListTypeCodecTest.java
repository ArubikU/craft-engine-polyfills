package dev.arubik.craftengine.script;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Covers {@link TypeCodecs#listOf} and its specialization in {@link PolyClassGenerator}: a typed
 * registration may take or return a {@code List<T>} of PolyType instances natively instead of a raw
 * {@link ScriptValue.Array}.
 *
 * <p>The generated methods are invoked BY REFLECTION rather than through a compiled formula, on
 * purpose: that is the only way to prove the natively-typed wrapper method itself ran (and therefore
 * that the decode/encode really happens inside it), instead of the erased shim silently doing the
 * same work one tier down and producing an identical answer.
 *
 * <p>Type names are unique to this class — {@link PolyTypeRegistry} is process-global and shared
 * with every other test class in the suite.
 */
class ListTypeCodecTest {

    private static final String HOST = "ListCodecHostType";
    private static final String ELEMENT = "ListCodecFooType";

    /** A stand-in for whatever real object a PolyType wraps (Player, Entity, ...). */
    record Foo(String id) {}
    /** A DIFFERENT wrapped type, to prove a wrongly-typed element is dropped rather than fatal. */
    record Bar(String id) {}

    @BeforeAll
    static void register() {
        PolyTypeRegistry.define(ELEMENT)
                .property("id", o -> ScriptValue.of(((Foo) o).id()));

        PolyType.TypeCodec<List<Foo>> foos = TypeCodecs.listOf(ELEMENT, Foo.class);

        PolyTypeRegistry.define(HOST)
                // Takes a List<Foo>: joins the ids, so the assertion sees both CONTENT and ORDER.
                .methodTyped1("join_ids", foos, TypeCodecs.STRING, "<missing>",
                        (Object o, List<Foo> list) -> {
                            List<String> ids = new ArrayList<>();
                            for (Foo f : list) ids.add(f.id());
                            return String.join(",", ids);
                        })
                .methodTyped1("count", foos, TypeCodecs.DOUBLE, -1.0,
                        (Object o, List<Foo> list) -> (double) list.size())
                // Returns a List<Foo>, which must come back to the script side as an Array of Objs.
                .methodTyped0("give_foos", foos,
                        (Object o) -> List.of(new Foo("a"), new Foo("b"), new Foo("c")))
                .methodTyped0("give_empty", foos, (Object o) -> List.of())
                .methodTyped0("give_null", foos, (Object o) -> null)
                // Mutated by shapeChangeDegradesToGenericDispatch; nothing else touches it.
                .methodTyped1("shape_shift", foos, TypeCodecs.STRING, "",
                        (Object o, List<Foo> list) -> "typed:" + list.size());
    }

    private static PolyClassGenerator.GeneratedPolyClass generated() {
        PolyClassGenerator.GeneratedPolyClass g = PolyClassGenerator.getOrGenerate(HOST);
        assertNotNull(g, "the host type must generate a PolyClass");
        return g;
    }

    /** Invokes the natively-typed generated method for {@code scriptName} on a fresh wrapper. */
    private static Object callTyped(String scriptName, Class<?>[] paramTypes, Object... args) throws Exception {
        PolyClassGenerator.GeneratedPolyClass g = generated();
        PolyClassGenerator.TypedMemberRef ref = g.typedMethods().get(scriptName);
        assertNotNull(ref, "'" + scriptName + "' must have taken the natively-typed path, not the erased shim");
        Class<?> cls = Class.forName(g.internalName().replace('/', '.'));
        Method m = cls.getDeclaredMethod(ref.javaName(), paramTypes);
        Object wrapper = cls.getConstructor(Object.class).newInstance(new Object());
        return m.invoke(wrapper, args);
    }

    private static ScriptValue array(ScriptValue... elements) {
        return new ScriptValue.Array(List.of(elements));
    }

    private static ScriptValue foo(String id) {
        return ScriptValue.ofObj(ELEMENT, new Foo(id));
    }

    @Test
    void aListArgumentIsSpecializedAndReachesTheHandlerUnwrappedInOrder() throws Exception {
        // The whole point: a List<T> slot must NOT knock the method back to the erased shim, which is
        // what happened before Kind.LIST existed (a list codec is a fresh instance, so the
        // identity-based kindOf saw an unknown codec and refused to specialize).
        assertTrue(generated().typedMethods().containsKey("join_ids"));
        assertTrue(generated().typedMethods().containsKey("give_foos"));

        assertEquals("x,y,z",
                callTyped("join_ids", new Class<?>[]{ScriptValue.class}, array(foo("x"), foo("y"), foo("z"))));
    }

    @Test
    void theListSlotKeepsTheScriptValueSignatureSoTheCallSiteIsUnchanged() {
        PolyClassGenerator.TypedMemberRef ref = generated().typedMethods().get("join_ids");
        assertEquals(PolyClassGenerator.Kind.LIST, ref.argKinds()[0]);
        assertEquals("(Ldev/arubik/craftengine/script/ScriptValue;)Ljava/lang/String;", ref.descriptor(),
                "a LIST slot must look exactly like RAW at the signature — the decode happens inside");

        PolyClassGenerator.TypedMemberRef ret = generated().typedMethods().get("give_foos");
        assertEquals(PolyClassGenerator.Kind.LIST, ret.retKind());
        assertEquals("()Ldev/arubik/craftengine/script/ScriptValue;", ret.descriptor());
    }

    @Test
    void elementsOfTheWrongTypeOrShapeAreSkippedNotFatal() throws Exception {
        ScriptValue mixed = array(
                foo("keep1"),
                ScriptValue.of("a bare string, not an Obj"),
                ScriptValue.ofObj(ELEMENT, new Bar("wrong java type")),
                ScriptValue.NULL,
                ScriptValue.of(42.0),
                foo("keep2"));
        assertEquals("keep1,keep2",
                callTyped("join_ids", new Class<?>[]{ScriptValue.class}, mixed),
                "bad elements must be dropped, and the good ones must still arrive in order");
    }

    @Test
    void aNonArrayArgumentDecodesToAnEmptyList() throws Exception {
        // Degrade, don't explode — the same thing every hand-written untyped handler does with a
        // wrong-shaped argument.
        assertEquals(0.0, count(ScriptValue.of("nope")));
        assertEquals(0.0, count(ScriptValue.NULL));
        assertEquals(0.0, count(ScriptValue.of(7.0)));
        // ...and a well-formed Array still counts, so "empty" isn't just the method being broken.
        assertEquals(2.0, count(array(foo("a"), foo("b"))));
    }

    private static double count(ScriptValue arg) throws Exception {
        return ((Double) callTyped("count", new Class<?>[]{ScriptValue.class}, arg)).doubleValue();
    }

    @Test
    void aListReturnComesBackAsAnArrayTheScriptSideCanRead() throws Exception {
        Object raw = callTyped("give_foos", new Class<?>[0]);
        assertInstanceOf(ScriptValue.Array.class, raw);
        List<ScriptValue> elements = ((ScriptValue.Array) raw).elements();
        assertEquals(3, elements.size());

        List<String> ids = new ArrayList<>();
        for (ScriptValue element : elements) {
            ScriptValue.Obj obj = assertInstanceOf(ScriptValue.Obj.class, element);
            assertEquals(ELEMENT, obj.typeName(), "elements must be boxed under the declared PolyType");
            // Reading it the way a script would, through the registered PolyType.
            ids.add(element.getProperty("id").asStr());
        }
        assertEquals(List.of("a", "b", "c"), ids);

        assertEquals(List.of(), ((ScriptValue.Array) callTyped("give_empty", new Class<?>[0])).elements());
        assertEquals(List.of(), ((ScriptValue.Array) callTyped("give_null", new Class<?>[0])).elements(),
                "a null list must encode to an empty Array, not to NULL");
    }

    @Test
    void theUntypedPathDecodesIdenticallyToTheSpecializedOne() {
        // The erased MethodHandler the typed registration also installs (what the interpreter and
        // every short-arg call site use) must agree with the generated native method.
        ScriptValue r = PolyTypeRegistry.get(HOST).resolveMethod("join_ids")
                .call(new Object(), List.of(array(foo("p"), foo("q"))));
        assertEquals("p,q", r.asStr());

        ScriptValue ret = PolyTypeRegistry.get(HOST).resolveMethod("give_foos").call(new Object(), List.of());
        assertInstanceOf(ScriptValue.Array.class, ret);
        assertEquals(3, ((ScriptValue.Array) ret).elements().size());
    }

    @Test
    void shapeChangeDegradesToGenericDispatch() throws Exception {
        // Sanity: it starts out specialized and working.
        assertEquals("typed:2",
                callTyped("shape_shift", new Class<?>[]{ScriptValue.class}, array(foo("a"), foo("b"))));

        // Re-register it as a plain untyped method. The generated native method's signature is fixed
        // forever, so resolveTypedHandler must refuse (the descriptor no longer has a typed entry at
        // all) and the wrapper must route through genericCall instead of running the stale handler.
        PolyTypeRegistry.get(HOST).replaceMethod("shape_shift",
                (o, a) -> ScriptValue.of("untyped:" + a.size()));

        assertEquals("untyped:1",
                callTyped("shape_shift", new Class<?>[]{ScriptValue.class}, array(foo("a"), foo("b"))),
                "a shape change must fall back to generic dispatch, never keep calling the old handler");

        // And a typed re-registration with a DIFFERENT arg shape must be refused too: 'L' vs 'S'.
        PolyTypeRegistry.get(HOST).methodTyped1("shape_shift", TypeCodecs.STRING, TypeCodecs.STRING, "",
                (Object o, String s) -> "restrung:" + s);
        assertEquals("restrung:a-string",
                callTyped("shape_shift", new Class<?>[]{ScriptValue.class}, ScriptValue.of("a-string")),
                "the generic fallback must run the newly-registered handler, coercing as it goes");
    }
}
