package dev.arubik.craftengine.script;

import java.util.ArrayList;
import java.util.List;

/**
 * Ready-made {@link PolyType.TypeCodec} instances for the native types a typed PolyType
 * registration (methodTyped0..3, see PolyType) most commonly needs. Mirrors the same coercions
 * every hand-written untyped handler already performs manually via ScriptValue.asStr()/asNum()/
 * asBool() and ScriptValue.of(...) — these codecs exist only to make that coercion reusable and
 * queryable, not to change how it behaves.
 */
public final class TypeCodecs {

    private TypeCodecs() {}

    public static final PolyType.TypeCodec<String> STRING = new PolyType.TypeCodec<>() {
        @Override public Class<String> type() { return String.class; }
        @Override public String decode(ScriptValue value) { return value.asStr(); }
        @Override public ScriptValue encode(String value) { return ScriptValue.of(value); }
    };

    public static final PolyType.TypeCodec<Double> DOUBLE = new PolyType.TypeCodec<>() {
        @Override public Class<Double> type() { return Double.class; }
        @Override public Double decode(ScriptValue value) { return value.asNum(); }
        @Override public ScriptValue encode(Double value) { return ScriptValue.of(value); }
    };

    public static final PolyType.TypeCodec<Boolean> BOOL = new PolyType.TypeCodec<>() {
        @Override public Class<Boolean> type() { return Boolean.class; }
        @Override public Boolean decode(ScriptValue value) { return value.asBool(); }
        @Override public ScriptValue encode(Boolean value) { return ScriptValue.of(value); }
    };

    /** Identity passthrough — for an argument/return whose real coercion is dynamic (decided at
     *  call time from another argument's value, e.g. TypedKeyBridge's codec-by-name lookup) and so
     *  can't be pinned to one native Java type at registration time. Keeps such a slot expressible
     *  in the typed API instead of forcing it back to the fully-untyped one. */
    public static final PolyType.TypeCodec<ScriptValue> RAW = new PolyType.TypeCodec<>() {
        @Override public Class<ScriptValue> type() { return ScriptValue.class; }
        @Override public ScriptValue decode(ScriptValue value) { return value; }
        @Override public ScriptValue encode(ScriptValue value) { return value; }
    };

    /**
     * A codec for {@code List<T>} where every element is a PolyType instance — i.e. a script-side
     * {@code ScriptValue.Array} of {@code ScriptValue.Obj}, unwrapped to the real Java objects the
     * handler actually wants ({@code List<Player>}, {@code List<Entity>}, ...).
     *
     * <p>A dedicated CLASS rather than an anonymous codec on purpose: {@link PolyClassGenerator}
     * classifies codecs so it can pick a native JVM type for a generated method's slot, and every
     * other codec here is a singleton it can match by identity. A list codec is a fresh instance per
     * {@link #listOf} call, so identity is useless — the generator detects this type with {@code
     * instanceof} instead. Without that it would see an unrecognized codec, refuse to specialize the
     * whole method, and silently drop it to the erased shim.
     */
    /**
     * A codec whose decode is real work rather than a coercion, so it cannot be inlined at the call
     * site and is instead invoked INSIDE the generated wrapper. Both {@link ListCodec} and
     * {@link PolyCodec} are this shape: at the generated method's signature their slot stays a
     * {@code ScriptValue} (exactly like {@link #RAW}), and the wrapper decodes it before handing the
     * real Java value to the typed handler. That is what lets them exist with no change at all to
     * {@link ScriptBytecodeCompiler} — the call site already knows how to pass a {@code ScriptValue}.
     */
    public sealed interface WrappedCodec permits ListCodec, PolyCodec {}

    /**
     * A single PolyType instance, unwrapped to its real Java type: a handler can declare
     * {@code (MachineRef m, BlockRef target) -> ...} and receive the {@code BlockRef} directly
     * instead of picking it out of a {@code ScriptValue.Obj} by hand.
     *
     * <p>{@code decode} yields null for anything that isn't an {@code Obj} holding an
     * {@code instanceType} — degrade, don't throw, matching how every untyped handler here treats a
     * wrong-shaped argument. A null return from the handler encodes back to {@code NULL}.
     */
    public static final class PolyCodec<T> implements PolyType.TypeCodec<T>, WrappedCodec {
        private final String polyTypeName;
        private final Class<T> instanceType;

        PolyCodec(String polyTypeName, Class<T> instanceType) {
            this.polyTypeName = polyTypeName;
            this.instanceType = instanceType;
        }

        /** The PolyType name {@link #encode} re-boxes under. */
        public String polyTypeName() { return polyTypeName; }
        /** The Java type a value must be for {@link #decode} to keep it. */
        public Class<T> instanceType() { return instanceType; }

        @Override public Class<T> type() { return instanceType; }

        @Override public T decode(ScriptValue value) {
            if (!(value instanceof ScriptValue.Obj o)) return null;
            Object inst = o.instance();
            return instanceType.isInstance(inst) ? instanceType.cast(inst) : null;
        }

        @Override public ScriptValue encode(T value) {
            return value == null ? ScriptValue.NULL : ScriptValue.ofObj(polyTypeName, value);
        }
    }

    /**
     * A codec for one instance of {@code polyTypeName}, decoded to {@code instanceType}.
     *
     * <p>This is what most {@link #RAW} slots actually are: a slot that always holds one object of a
     * known PolyType, currently typed as "some ScriptValue" only because there was no way to say
     * otherwise. Declaring it lets the handler take the real type.
     *
     * <p>Only use it where the slot is genuinely always that one type. A slot that legitimately
     * accepts several shapes (an Array here, a number there) must stay {@link #RAW} — this codec
     * would decode those to null.
     */
    public static <T> PolyType.TypeCodec<T> polyType(String polyTypeName, Class<T> instanceType) {
        return new PolyCodec<>(polyTypeName, instanceType);
    }

    public static final class ListCodec<T> implements PolyType.TypeCodec<List<T>>, WrappedCodec {
        private final String polyTypeName;
        private final Class<T> elementType;

        ListCodec(String polyTypeName, Class<T> elementType) {
            this.polyTypeName = polyTypeName;
            this.elementType = elementType;
        }

        /** The PolyType name each element is re-boxed under by {@link #encode}. */
        public String polyTypeName() { return polyTypeName; }
        /** The Java type an element must be for {@link #decode} to keep it. */
        public Class<T> elementType() { return elementType; }

        @SuppressWarnings("unchecked")
        @Override public Class<List<T>> type() { return (Class<List<T>>) (Class<?>) List.class; }

        /** Degrades rather than throws, exactly like every hand-written untyped handler does with a
         *  wrong-shaped argument: a non-Array is an empty list, and an element that isn't an Obj
         *  wrapping an {@code elementType} is skipped instead of failing the whole call. Scripts are
         *  user data; one bad element must not take out the method. */
        @Override public List<T> decode(ScriptValue value) {
            if (!(value instanceof ScriptValue.Array array)) return List.of();
            List<T> out = new ArrayList<>(array.elements().size());
            for (ScriptValue element : array.elements()) {
                if (!(element instanceof ScriptValue.Obj obj)) continue;
                Object instance = obj.instance();
                if (elementType.isInstance(instance)) out.add(elementType.cast(instance));
            }
            return out;
        }

        @Override public ScriptValue encode(List<T> value) {
            if (value == null || value.isEmpty()) return new ScriptValue.Array(List.of());
            List<ScriptValue> out = new ArrayList<>(value.size());
            for (T element : value) out.add(ScriptValue.ofObj(polyTypeName, element));
            return new ScriptValue.Array(out);
        }
    }

    /**
     * A codec for a {@code List<T>} of PolyType instances registered under {@code polyTypeName}.
     * Every call returns a NEW instance (it carries the element type), which is why
     * {@link ListCodec} is a named class — see its doc.
     */
    public static <T> PolyType.TypeCodec<List<T>> listOf(String polyTypeName, Class<T> elementType) {
        return new ListCodec<>(polyTypeName, elementType);
    }
}
