package dev.arubik.craftengine.script.types.primitive;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.nbt.*;

import java.util.ArrayList;
import java.util.List;

/**
 * NbtData type. Instance = NMS CompoundTag.
 * Supports dot-path access: nbt.get("level.data.SpawnX")
 */
public final class NbtDataType {

    private NbtDataType() {}

    public static void register() {
        PolyTypeRegistry.define("NbtData")
            .method("get", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                return resolvePath(tag(obj), args.get(0).asStr());
            })
            .method("has", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                return ScriptValue.of(pathExists(tag(obj), args.get(0).asStr()));
            })
            .method("get_int", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(0);
                ScriptValue v = resolvePath(tag(obj), args.get(0).asStr());
                return ScriptValue.of(v.asNum());
            })
            .method("get_string", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of("");
                ScriptValue v = resolvePath(tag(obj), args.get(0).asStr());
                return ScriptValue.of(v.asStr());
            })
            .method("get_double", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(0.0);
                ScriptValue v = resolvePath(tag(obj), args.get(0).asStr());
                return ScriptValue.of(v.asNum());
            })
            ;
    }

    public static ScriptValue wrap(CompoundTag tag) {
        return tag == null ? ScriptValue.NULL : ScriptValue.ofObj("NbtData", tag);
    }

    public static ScriptValue tagToValue(Tag tag) {
        if (tag == null) return ScriptValue.NULL;
        if (tag instanceof NumericTag nt) return ScriptValue.of(nt.doubleValue());
        if (tag instanceof StringTag st) return ScriptValue.of(st.value());
        if (tag instanceof CompoundTag ct) return wrap(ct);
        if (tag instanceof ListTag lt) {
            List<ScriptValue> elems = new ArrayList<>(lt.size());
            for (int i = 0; i < lt.size(); i++) elems.add(tagToValue(lt.get(i)));
            return new ScriptValue.Array(elems);
        }
        return ScriptValue.of(tag.toString());
    }

    private static ScriptValue resolvePath(CompoundTag root, String path) {
        Tag current = root;
        for (String part : path.split("\\.")) {
            if (!(current instanceof CompoundTag ct)) return ScriptValue.NULL;
            current = ct.get(part);
            if (current == null) return ScriptValue.NULL;
        }
        return tagToValue(current);
    }

    private static boolean pathExists(CompoundTag root, String path) {
        Tag current = root;
        for (String part : path.split("\\.")) {
            if (!(current instanceof CompoundTag ct)) return false;
            current = ct.get(part);
            if (current == null) return false;
        }
        return true;
    }

    private static CompoundTag tag(Object obj) { return (CompoundTag) obj; }
}
