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
    public static final class ListCodec<T> implements PolyType.TypeCodec<List<T>> {
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
