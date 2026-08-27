package dev.arubik.craftengine.script.types.primitive;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
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
            // Return is genuinely dynamic (resolvePath can yield NULL, a number, a string, an Array,
            // or a nested NbtData wrapper) — kept as TypeCodecs.RAW so the original ScriptValue is
            // passed through unchanged.
            .methodTyped1("get", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (CompoundTag tag, String path) -> resolvePath(tag, path))
            .methodTyped1("has", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (CompoundTag tag, String path) -> pathExists(tag, path))
            .methodTyped1("get_int", TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (CompoundTag tag, String path) -> resolvePath(tag, path).asNum())
            .methodTyped1("get_string", TypeCodecs.STRING, TypeCodecs.STRING, "",
                (CompoundTag tag, String path) -> resolvePath(tag, path).asStr())
            .methodTyped1("get_double", TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (CompoundTag tag, String path) -> resolvePath(tag, path).asNum())
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
