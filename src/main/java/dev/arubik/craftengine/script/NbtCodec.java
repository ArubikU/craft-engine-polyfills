package dev.arubik.craftengine.script;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Recursive ScriptValue &lt;-&gt; real nested NMS {@link CompoundTag} encoder — backs the
 * TypedKeyBridge "compound" type, so a script can persist a whole {@code Map}/array/primitive
 * TREE under one typed key instead of flattening it into a single primitive by hand.
 *
 * <p>Every value is boxed as {@code {t: "<kind>", v: <value>}} rather than relying on reading a
 * stored tag's runtime type back — self-describing beats type-sniffing an untyped {@code Tag} and
 * needs nothing beyond {@link CompoundTag}'s ordinary typed getters/setters (all already used
 * elsewhere in this codebase, e.g. {@code ChainRegistry}'s own save/load).
 */
public final class NbtCodec {

    private NbtCodec() {}

    private static final String TYPE_KEY = "t";
    private static final String VALUE_KEY = "v";

    /** Encodes a whole {@code Map<String,ScriptValue>} (a script "Map" instance) into a CompoundTag. */
    public static CompoundTag encodeMap(Map<String, ScriptValue> map) {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<String, ScriptValue> e : map.entrySet()) {
            if (e.getValue() == ScriptValue.NULL) continue; // absent key == null, same as every other typed getter
            tag.put(e.getKey(), encodeValue(e.getValue()));
        }
        return tag;
    }

    /** Decodes a CompoundTag built by {@link #encodeMap} back into a {@code Map<String,ScriptValue>}. */
    public static Map<String, ScriptValue> decodeMap(CompoundTag tag) {
        Map<String, ScriptValue> out = new LinkedHashMap<>();
        for (String key : tag.keySet()) {
            tag.getCompound(key).ifPresent(box -> out.put(key, decodeValue(box)));
        }
        return out;
    }

    private static CompoundTag encodeValue(ScriptValue value) {
        CompoundTag box = new CompoundTag();
        switch (value) {
            case ScriptValue.Null ignored -> box.putString(TYPE_KEY, "null");
            case ScriptValue.Bool b -> {
                box.putString(TYPE_KEY, "bool");
                box.putBoolean(VALUE_KEY, b.value());
            }
            case ScriptValue.Num n -> {
                box.putString(TYPE_KEY, "num");
                box.putDouble(VALUE_KEY, n.value());
            }
            case ScriptValue.Str s -> {
                box.putString(TYPE_KEY, "str");
                box.putString(VALUE_KEY, s.value());
            }
            case ScriptValue.Array a -> {
                box.putString(TYPE_KEY, "arr");
                ListTag list = new ListTag();
                for (ScriptValue elem : a.elements()) list.add(encodeValue(elem));
                box.put(VALUE_KEY, list);
            }
            case ScriptValue.Obj o when "Map".equals(o.typeName()) -> {
                box.putString(TYPE_KEY, "map");
                @SuppressWarnings("unchecked")
                Map<String, ScriptValue> nested = (Map<String, ScriptValue>) o.instance();
                box.put(VALUE_KEY, encodeMap(nested));
            }
            case ScriptValue.Item i -> {
                // Reuse the exact whole-ItemStack encoding TypedKeyBridge's "item" type already uses,
                // so a compound holding an item slot round-trips the same way a bare "item" key does.
                box.putString(TYPE_KEY, "item");
                box.putString(VALUE_KEY, String.valueOf(TypedKeyBridge.resolve("item").toStorage(i)));
            }
            default -> box.putString(TYPE_KEY, "null"); // an unsupported value type — drop safely
        }
        return box;
    }

    private static ScriptValue decodeValue(CompoundTag box) {
        String type = box.getString(TYPE_KEY).orElse("null");
        return switch (type) {
            case "bool" -> ScriptValue.of(box.getBoolean(VALUE_KEY).orElse(false));
            case "num" -> ScriptValue.of(box.getDouble(VALUE_KEY).orElse(0.0));
            case "str" -> ScriptValue.of(box.getString(VALUE_KEY).orElse(""));
            case "arr" -> {
                List<ScriptValue> out = new ArrayList<>();
                ListTag list = box.getListOrEmpty(VALUE_KEY);
                for (int i = 0; i < list.size(); i++) {
                    out.add(decodeValue(list.getCompoundOrEmpty(i)));
                }
                yield new ScriptValue.Array(out);
            }
            case "map" -> dev.arubik.craftengine.script.types.primitive.MapType.wrap(
                    decodeMap(box.getCompound(VALUE_KEY).orElseGet(CompoundTag::new)));
            case "item" -> TypedKeyBridge.resolve("item").fromStorage(box.getString(VALUE_KEY).orElse(""));
            default -> ScriptValue.NULL;
        };
    }
}
