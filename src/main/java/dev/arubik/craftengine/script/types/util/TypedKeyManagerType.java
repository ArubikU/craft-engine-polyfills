package dev.arubik.craftengine.script.types.util;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypedKeyBridge;
import dev.arubik.craftengine.script.UserFunction;
import dev.arubik.craftengine.util.NbtType;

import java.util.List;

/**
 * "TypedKey" — script singleton letting a {@code .pf} script define its OWN named type for
 * {@code get_typed}/{@code set_typed}/{@code with_typed} (Machine and Item alike), the script-side
 * counterpart of Java calling {@link TypedKeyBridge#registerCustom}. A script only ever hands over
 * script values; the plumbing composes the script's transform with an existing INNER type (a
 * primitive, or "compound" for a whole value tree) that actually gets persisted:
 *
 * <pre>
 *   TypedKey.define("uuid", "string",
 *       def(v) { return v.to_string() },   // serialize: script value -> the inner "string" shape
 *       def(v) { return uuid_from(v) })    // deserialize: the inner shape -> script value
 * </pre>
 *
 * The functions run with an EMPTY base context (see {@link #EMPTY_CTX}) — they're meant to be pure
 * value transforms, not scripts that reach into a specific Machine/Player, since a typed key's
 * codec is shared by every caller that uses that type name afterward.
 */
public final class TypedKeyManagerType {

    public static final Object INSTANCE = new Object();

    /** A pure transform function has no legitimate need for outer globals (Machine/Player/...) —
     *  an empty context keeps that boundary explicit instead of silently working sometimes. */
    private static final ScriptContext EMPTY_CTX = ScriptContext.builder().build();

    private TypedKeyManagerType() {}

    public static void register() {
        PolyTypeRegistry.define("TypedKey")
            // define(name, inner_type, serialize_fn, deserialize_fn) -> bool
            .method("define", (obj, args) -> {
                if (args.size() < 4) return ScriptValue.of(false);
                String name = args.get(0).asStr();
                TypedKeyBridge.Codec inner = TypedKeyBridge.resolve(args.get(1).asStr());
                UserFunction serialize = asFunction(args.get(2));
                UserFunction deserialize = asFunction(args.get(3));
                if (inner == null || serialize == null || deserialize == null) return ScriptValue.of(false);
                TypedKeyBridge.registerCustom(name, new TypedKeyBridge.Codec() {
                    public NbtType storage() { return inner.storage(); }
                    public Object toStorage(ScriptValue value) {
                        ScriptValue shaped = serialize.call(List.of(value), EMPTY_CTX);
                        return inner.toStorage(shaped);
                    }
                    public ScriptValue fromStorage(Object raw) {
                        ScriptValue shaped = inner.fromStorage(raw);
                        return deserialize.call(List.of(shaped), EMPTY_CTX);
                    }
                });
                return ScriptValue.of(true);
            })
            // exists(name) -> bool — whether a type name (built-in or custom) is currently defined
            .method("exists", (obj, args) ->
                ScriptValue.of(!args.isEmpty() && TypedKeyBridge.resolve(args.get(0).asStr()) != null));
    }

    private static UserFunction asFunction(ScriptValue v) {
        if (v instanceof ScriptValue.Obj o && UserFunction.TYPE.equals(o.typeName())
                && o.instance() instanceof UserFunction fn) {
            return fn;
        }
        return null;
    }

    public static ScriptValue wrap() {
        return ScriptValue.ofObj("TypedKey", INSTANCE);
    }
}
