package dev.arubik.craftengine.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

/**
 * A plain key -&gt; value store keyed by player UUID — the generic primitive behind {@code
 * Player.get_typed}/{@code set_typed} (see {@code PlayerType}), mirroring {@link ServerFlags}
 * exactly except for the extra UUID dimension folded into every key.
 *
 * <p>{@code Player} deliberately does NOT inherit {@code Entity}'s PersistentDataContainer-backed
 * get_typed: a wrapped {@code Player} script value only exists while that player is online, and
 * its underlying NMS {@code ServerPlayer}/PDC is a THROWAWAY object across a respawn/relog —
 * there is no single persistent Bukkit-native handle a script can rely on the way a block entity
 * or a plain non-player Entity has one for its whole lifetime. Keying this store by UUID
 * (persisted to its own file, exactly like {@code ServerFlags}) gives a player's data a home that
 * survives every one of those object-identity resets.
 */
public final class PlayerFlags {

    // Same one-Object-valued-map shape as ServerFlags.TYPED, just keyed "uuid|name" instead of
    // a bare name — see that class's own javadoc for why a single Object map beats one map per
    // NbtType shape here too.
    private static final Map<String, Object> TYPED = new HashMap<>();

    private PlayerFlags() {}

    private static String key(UUID id, String name) {
        return id + "|" + name;
    }

    public static Object getTyped(UUID id, String name) {
        return TYPED.get(key(id, name));
    }

    public static void setTyped(UUID id, String name, Object value) {
        String k = key(id, name);
        if (value == null) TYPED.remove(k);
        else TYPED.put(k, value);
    }

    public static boolean hasTyped(UUID id, String name) {
        return TYPED.containsKey(key(id, name));
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

    /** One sub-tag per NbtType shape, each a flat "uuid|name" -&gt; value CompoundTag — see
     *  {@link ServerFlags#encodeTyped()}, which this mirrors exactly. */
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
