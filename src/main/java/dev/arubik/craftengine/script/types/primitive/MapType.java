package dev.arubik.craftengine.script.types.primitive;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Map type for scripts. Instance is a Map&lt;String, ScriptValue&gt;.
 * Created via make_map("key1", val1, "key2", val2, ...) builtin.
 */
public final class MapType {

    private MapType() {}

    @SuppressWarnings("unchecked")
    public static void register() {
        PolyTypeRegistry.define("Map")
            .property("size", obj -> ScriptValue.of(map(obj).size()))
            .property("keys", obj -> {
                List<ScriptValue> keys = map(obj).keySet().stream()
                        .map(ScriptValue::of)
                        .collect(java.util.stream.Collectors.toList());
                return new ScriptValue.Array(keys);
            })
            .property("values", obj -> new ScriptValue.Array(new java.util.ArrayList<>(map(obj).values())))
            .methodTyped1("get", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (Map<String, ScriptValue> m, String key) -> m.getOrDefault(key, ScriptValue.NULL))
            .methodTyped1("has", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Map<String, ScriptValue> m, String key) -> m.containsKey(key))
            // switch(key[, default]) — migrated to methodTypedOpt2. The 2nd arg is a genuinely
            // optional "default if key missing" value, so a 1-arg call must still run the lookup
            // normally, which is exactly methodTypedOptN's shape (methodTyped2's onMissingArgs
            // would instead short-circuit the whole body). Both slots take a Java `null` default,
            // used purely as an "argument was absent" sentinel so the original's
            // `args.isEmpty()` / `args.size() >= 2` branches are reproduced EXACTLY: a decoded
            // STRING is never null (ScriptValue.of(String) maps null to NULL, so asStr() always
            // returns a real string) and a decoded RAW is never null (identity over a non-null
            // args element), so null can only ever mean "not passed".
            .methodTypedOpt2("switch", TypeCodecs.STRING, (String) null,
                TypeCodecs.RAW, (ScriptValue) null, TypeCodecs.RAW,
                (Map<String, ScriptValue> m, String key, ScriptValue fallback) -> {
                    if (key == null) return ScriptValue.NULL;
                    ScriptValue result = m.get(key);
                    if (result != null) return result;
                    return fallback != null ? fallback : ScriptValue.NULL;
                })
            // map.with(key, value) -> a NEW map with that key set/overwritten — same "returns a
            // copy" idiom as Item's with_component/with_name, since there's no in-place mutator on
            // this type. Lets a script build a dynamic-sized Map incrementally (m = m.with(k, v) in
            // a loop) instead of needing every key/value known upfront at a single make_map(...) call.
            // Migrated to methodTypedOpt2: the missing-args fallback is instance-dependent (it
            // returns THIS map re-wrapped), which methodTyped2's fixed onMissingArgs constant can't
            // express — but methodTypedOptN always runs the body, so the instance is in hand. Null
            // defaults are the "absent" sentinel, same reasoning as switch(...) above.
            .methodTypedOpt2("with", TypeCodecs.STRING, (String) null,
                TypeCodecs.RAW, (ScriptValue) null, TypeCodecs.RAW,
                (Map<String, ScriptValue> m, String key, ScriptValue value) -> {
                    if (key == null || value == null) return ScriptValue.ofObj("Map", m);
                    LinkedHashMap<String, ScriptValue> copy = new LinkedHashMap<>(m);
                    copy.put(key, value);
                    return ScriptValue.ofObj("Map", copy);
                })
            // map.without(key) -> a NEW map with that key removed (no-op if absent).
            // Migrated to methodTypedOpt1: same instance-dependent missing-args fallback as
            // with(...) above, expressible now that the body always runs.
            .methodTypedOpt1("without", TypeCodecs.STRING, (String) null, TypeCodecs.RAW,
                (Map<String, ScriptValue> m, String key) -> {
                    if (key == null) return ScriptValue.ofObj("Map", m);
                    LinkedHashMap<String, ScriptValue> copy = new LinkedHashMap<>(m);
                    copy.remove(key);
                    return ScriptValue.ofObj("Map", copy);
                });
    }

    /** Dynamic property resolution for map keys — allows tank.contents_name etc. */
    @SuppressWarnings("unchecked")
    public static ScriptValue getMapProperty(Object obj, String prop) {
        if (!(obj instanceof Map)) return ScriptValue.NULL;
        Map<String, ScriptValue> m = (Map<String, ScriptValue>) obj;
        return m.getOrDefault(prop, ScriptValue.NULL);
    }

    public static ScriptValue wrap(Map<String, ScriptValue> map) {
        if (map == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("Map", map);
    }

    public static ScriptValue makeMap(List<ScriptValue> args) {
        LinkedHashMap<String, ScriptValue> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < args.size(); i += 2) {
            map.put(args.get(i).asStr(), args.get(i + 1));
        }
        return ScriptValue.ofObj("Map", map);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, ScriptValue> map(Object obj) {
        return (Map<String, ScriptValue>) obj;
    }
}
