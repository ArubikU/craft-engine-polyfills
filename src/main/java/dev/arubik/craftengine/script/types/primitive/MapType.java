package dev.arubik.craftengine.script.types.primitive;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;

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
            .method("get", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                String key = args.get(0).asStr();
                return map(obj).getOrDefault(key, ScriptValue.NULL);
            })
            .method("has", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                return ScriptValue.of(map(obj).containsKey(args.get(0).asStr()));
            })
            .method("switch", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                String key = args.get(0).asStr();
                ScriptValue result = map(obj).get(key);
                if (result != null) return result;
                return args.size() >= 2 ? args.get(1) : ScriptValue.NULL;
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
