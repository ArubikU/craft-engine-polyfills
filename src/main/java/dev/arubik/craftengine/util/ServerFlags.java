package dev.arubik.craftengine.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

/**
 * A plain global key -&gt; value store, scoped to the whole server rather than any one block,
 * player, or world — the generic primitive behind {@code Server.get_typed}/{@code set_typed}
 * (see {@code ServerType}).
 *
 * <p>Exists for state that genuinely belongs to neither a block (Machine.get_typed), an entity
 * (Entity.get_typed, PersistentDataContainer-backed), nor a world (World.get_typed, also
 * PDC-backed) — a cross-dimension registry (frequencies, global counters, feature toggles) is the
 * textbook case. Deliberately just a flat map: any structure beyond "one key, one typed value" (a
 * list of linked positions, say) is the calling SCRIPT's job to encode/decode — see {@code
 * ScriptFormula}'s {@code split}/{@code join} — not something this class should grow bespoke
 * shapes for. That is what keeps this reusable instead of turning into another one-off registry.
 *
 * <p>Persistence mirrors {@code GlueRegistry}/{@code ChainRegistry} exactly (one compressed NBT
 * file), wired into {@code CraftEnginePolyfills#onEnable}/{@code #onDisable}.
 */
public final class ServerFlags {

    // Generic bucket for Server.get_typed/set_typed (see ServerType) — holds any boxed Java
    // primitive a TypedKeyBridge codec produces (Byte/Short/Integer/Long/Float/Double/String/
    // Boolean/byte[]/int[]/long[]), keyed by a flat namespace.
    private static final Map<String, Object> TYPED = new HashMap<>();

    private ServerFlags() {
    }

    public static Object getTyped(String key) {
        return TYPED.get(key);
    }

    public static void setTyped(String key, Object value) {
        if (value == null) TYPED.remove(key);
        else TYPED.put(key, value);
    }

    public static boolean hasTyped(String key) {
        return TYPED.containsKey(key);
    }

    public static void saveAll(Path file) throws IOException {
        CompoundTag root = new CompoundTag();
        root.put("typed", encodeTyped());
        Files.createDirectories(file.getParent(), new FileAttribute[0]);
        NbtIo.writeCompressed(root, file);
    }

    public static void loadAll(Path file) throws IOException {
        if (!Files.exists(file, LinkOption.NOFOLLOW_LINKS))
            return;
        CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
        decodeTyped(root.getCompoundOrEmpty("typed"));
    }

    /** One sub-tag per NbtType shape, each a flat key->value CompoundTag — mirrors ints/strings
     *  above but generalized to every primitive {@link NbtType} a TypedKeyBridge codec can emit,
     *  since a single CompoundTag can't hold mixed-typed values for the same key across entries. */
    private static CompoundTag encodeTyped() {
        CompoundTag bytes = new CompoundTag(), shorts = new CompoundTag(), ints = new CompoundTag(),
                longs = new CompoundTag(), floats = new CompoundTag(), doubles = new CompoundTag(),
                strings = new CompoundTag(), bools = new CompoundTag(), byteArrays = new CompoundTag(),
                intArrays = new CompoundTag(), longArrays = new CompoundTag();
        for (Map.Entry<String, Object> e : TYPED.entrySet()) {
            String k = e.getKey();
            switch (e.getValue()) {
                case Byte v -> bytes.putByte(k, v);
                case Short v -> shorts.putShort(k, v);
                case Integer v -> ints.putInt(k, v);
                case Long v -> longs.putLong(k, v);
                case Float v -> floats.putFloat(k, v);
                case Double v -> doubles.putDouble(k, v);
                case String v -> strings.putString(k, v);
                case Boolean v -> bools.putBoolean(k, v);
                case byte[] v -> byteArrays.putByteArray(k, v);
                case int[] v -> intArrays.putIntArray(k, v);
                case long[] v -> longArrays.putLongArray(k, v);
                default -> { /* unknown shape — skip rather than corrupt the file */ }
            }
        }
        CompoundTag out = new CompoundTag();
        out.put("byte", bytes); out.put("short", shorts); out.put("int", ints); out.put("long", longs);
        out.put("float", floats); out.put("double", doubles); out.put("string", strings);
        out.put("bool", bools); out.put("byte_array", byteArrays); out.put("int_array", intArrays);
        out.put("long_array", longArrays);
        return out;
    }

    private static void decodeTyped(CompoundTag root) {
        CompoundTag bytes = root.getCompoundOrEmpty("byte");
        for (String k : bytes.keySet()) bytes.getByte(k).ifPresent(v -> TYPED.put(k, v));
        CompoundTag shorts = root.getCompoundOrEmpty("short");
        for (String k : shorts.keySet()) shorts.getShort(k).ifPresent(v -> TYPED.put(k, v));
        CompoundTag ints = root.getCompoundOrEmpty("int");
        for (String k : ints.keySet()) ints.getInt(k).ifPresent(v -> TYPED.put(k, v));
        CompoundTag longs = root.getCompoundOrEmpty("long");
        for (String k : longs.keySet()) longs.getLong(k).ifPresent(v -> TYPED.put(k, v));
        CompoundTag floats = root.getCompoundOrEmpty("float");
        for (String k : floats.keySet()) floats.getFloat(k).ifPresent(v -> TYPED.put(k, v));
        CompoundTag doubles = root.getCompoundOrEmpty("double");
        for (String k : doubles.keySet()) doubles.getDouble(k).ifPresent(v -> TYPED.put(k, v));
        CompoundTag strings = root.getCompoundOrEmpty("string");
        for (String k : strings.keySet()) strings.getString(k).ifPresent(v -> TYPED.put(k, v));
        CompoundTag bools = root.getCompoundOrEmpty("bool");
        for (String k : bools.keySet()) bools.getBoolean(k).ifPresent(v -> TYPED.put(k, v));
        CompoundTag byteArrays = root.getCompoundOrEmpty("byte_array");
        for (String k : byteArrays.keySet()) byteArrays.getByteArray(k).ifPresent(v -> TYPED.put(k, v));
        CompoundTag intArrays = root.getCompoundOrEmpty("int_array");
        for (String k : intArrays.keySet()) intArrays.getIntArray(k).ifPresent(v -> TYPED.put(k, v));
        CompoundTag longArrays = root.getCompoundOrEmpty("long_array");
        for (String k : longArrays.keySet()) longArrays.getLongArray(k).ifPresent(v -> TYPED.put(k, v));
    }
}
