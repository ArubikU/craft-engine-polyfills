package dev.arubik.craftengine.script;

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
}
