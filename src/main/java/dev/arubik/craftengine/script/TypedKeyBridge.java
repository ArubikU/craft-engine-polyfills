package dev.arubik.craftengine.script;

import dev.arubik.craftengine.util.NbtType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shared script<->{@link NbtType} bridge — standardizes how EVERY typed-key primitive (Machine's
 * block entity, Item's custom data, Entity/Player's PersistentDataContainer, Server/World/SQL/
 * Redis's own stores, ...) exchanges values with scripts, instead of each feature inventing its
 * own ad hoc encoding. A script picks a type by name ("int", "double", "bool", "byte_array",
 * "item", "uuid", "location", ...) so one key/type pair round-trips identically everywhere.
 *
 * <p>Beyond the primitive {@link NbtType}s, this also supports CUSTOM named types — a {@link Codec}
 * bundles "how to turn a script value into the ONE primitive that actually gets stored" with its
 * inverse, the same shape {@link dev.arubik.craftengine.util.TypedKey}'s own {@code CustomDataType}
 * uses on the Java side (e.g. {@code TypedKey.forEnum}), just script-reachable. Register one from
 * anywhere at plugin startup via {@link #registerCustom} to add a whole new "type name" every
 * get_typed/set_typed/with_typed call across every typed-storage owner picks up for free — no new
 * methods needed. See {@link #registerBuiltinCustomTypes} for the full built-in roster.
 */
public final class TypedKeyBridge {

    private TypedKeyBridge() {}

    /** How one named type reads/writes the ONE underlying primitive {@link #storage()} persists. */
    public interface Codec {
        /** The single NbtType actually written to the block entity / item NBT. */
        NbtType storage();
        /** Script value -> the exact boxed Java primitive {@code storage()} expects (may throw; callers catch). */
        Object toStorage(ScriptValue value);
        /** The stored primitive (or null if absent) -> a script value. Must handle null. */
        ScriptValue fromStorage(Object raw);
    }

    private static final Map<String, Codec> CUSTOM = new ConcurrentHashMap<>();

    /** Registers (or replaces) a custom named type. {@code name} is matched case-insensitively and
     *  takes priority over a built-in primitive of the same name, so a feature can even reshape
     *  what "string" or "int" means if it truly needs to — though a fresh name is the normal case. */
    public static void registerCustom(String name, Codec codec) {
        CUSTOM.put(name.toLowerCase(Locale.ROOT), codec);
    }

    public static boolean hasCustom(String name) {
        return CUSTOM.containsKey(name.toLowerCase(Locale.ROOT));
    }

    /** Resolves a script-facing type name to its {@link Codec} — custom registrations first, then
     *  the built-in primitives. Null if the name matches neither. */
    public static Codec resolve(String name) {
        String key = name.toLowerCase(Locale.ROOT);
        Codec custom = CUSTOM.get(key);
        if (custom != null) return custom;
        NbtType prim = parsePrimitive(key);
        return prim != null ? primitiveCodec(prim) : null;
    }

    /** @deprecated use {@link #resolve} — kept for any external caller still matching a bare NbtType. */
    @Deprecated
    public static NbtType parseType(String name) {
        Codec c = resolve(name);
        return c != null ? c.storage() : null;
    }

    private static NbtType parsePrimitive(String name) {
        return switch (name) {
            case "byte" -> NbtType.BYTE;
            case "short" -> NbtType.SHORT;
            case "int", "integer" -> NbtType.INTEGER;
            case "long" -> NbtType.LONG;
            case "float" -> NbtType.FLOAT;
            case "double", "num", "number" -> NbtType.DOUBLE;
            case "string", "str" -> NbtType.STRING;
            case "bool", "boolean" -> NbtType.BOOLEAN;
            case "byte_array", "bytes" -> NbtType.BYTE_ARRAY;
            case "int_array", "integer_array" -> NbtType.INTEGER_ARRAY;
            case "long_array" -> NbtType.LONG_ARRAY;
            default -> null;
        };
    }

    private static Codec primitiveCodec(NbtType type) {
        return new Codec() {
            public NbtType storage() { return type; }
            public Object toStorage(ScriptValue value) { return toJava(type, value); }
            public ScriptValue fromStorage(Object raw) { return toScript(type, raw); }
        };
    }

    // ---- primitive conversions (also used directly by primitiveCodec above) ----

    /** Converts a script value into the exact boxed Java type a primitive {@link NbtType} expects. */
    public static Object toJava(NbtType type, ScriptValue value) {
        return switch (type) {
            case BYTE -> (byte) value.asNum();
            case SHORT -> (short) value.asNum();
            case INTEGER -> (int) value.asNum();
            case LONG -> (long) value.asNum();
            case FLOAT -> (float) value.asNum();
            case DOUBLE -> value.asNum();
            case STRING -> value.asStr();
            case BOOLEAN -> value.asBool();
            case BYTE_ARRAY -> java.util.Base64.getDecoder().decode(value.asStr());
            case INTEGER_ARRAY -> {
                List<ScriptValue> elems = value instanceof ScriptValue.Array a ? a.elements() : List.of();
                int[] out = new int[elems.size()];
                for (int i = 0; i < out.length; i++) out[i] = (int) elems.get(i).asNum();
                yield out;
            }
            case LONG_ARRAY -> {
                List<ScriptValue> elems = value instanceof ScriptValue.Array a ? a.elements() : List.of();
                long[] out = new long[elems.size()];
                for (int i = 0; i < out.length; i++) out[i] = (long) elems.get(i).asNum();
                yield out;
            }
        };
    }

    /** Converts a raw stored Java primitive back into a script value. */
    public static ScriptValue toScript(NbtType type, Object raw) {
        if (raw == null) return defaultValue(type);
        return switch (type) {
            case BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE -> ScriptValue.of(((Number) raw).doubleValue());
            case STRING -> ScriptValue.of((String) raw);
            case BOOLEAN -> ScriptValue.of((Boolean) raw);
            case BYTE_ARRAY -> ScriptValue.of(java.util.Base64.getEncoder().encodeToString((byte[]) raw));
            case INTEGER_ARRAY -> {
                int[] arr = (int[]) raw;
                List<ScriptValue> out = new ArrayList<>(arr.length);
                for (int v : arr) out.add(ScriptValue.of(v));
                yield new ScriptValue.Array(out);
            }
            case LONG_ARRAY -> {
                long[] arr = (long[]) raw;
                List<ScriptValue> out = new ArrayList<>(arr.length);
                for (long v : arr) out.add(ScriptValue.of(v));
                yield new ScriptValue.Array(out);
            }
        };
    }

    /** The "absent" default for a type — 0 for numeric shapes, "" for string/byte_array, an empty
     *  Array for the *_array shapes, false for bool. */
    public static ScriptValue defaultValue(NbtType type) {
        return switch (type) {
            case STRING -> ScriptValue.of("");
            case BOOLEAN -> ScriptValue.of(false);
            case BYTE_ARRAY -> ScriptValue.of("");
            case INTEGER_ARRAY, LONG_ARRAY -> new ScriptValue.Array(List.of());
            default -> ScriptValue.of(0);
        };
    }

    // ---- built-in custom types ----

    /** Registers the built-ins ("item", "vector") — call once from ScriptBootstrap, after Item and
     *  Vector are themselves registered as PolyTypes. Safe to call more than once (idempotent). */
    public static void registerBuiltinCustomTypes() {
        registerCustom("item", ITEM_CODEC);
        registerCustom("vector", VECTOR_CODEC);
        registerCustom("compound", COMPOUND_CODEC);
        registerCustom("uuid", UUID_CODEC);
        registerCustom("location", LOCATION_CODEC);
        registerCustom("block_state", BLOCK_STATE_CODEC);
        registerCustom("block_pos", BLOCK_POS_CODEC);
        registerCustom("item_array", ITEM_ARRAY_CODEC);
        registerCustom("uuid_array", UUID_ARRAY_CODEC);
    }

    /** Adapts ANY {@code CustomDataType<T, P>} (this codebase's OTHER, non-script
     *  serialize/deserialize convention — see {@code CustomDataType.java}'s own UUID_TYPE/
     *  LOCATION_TYPE/BLOCK_STATE_TYPE/BLOCK_POS_TYPE/ITEM_CODEC_TYPE/ITEM_ARRAY_CODEC_TYPE)
     *  directly into a {@link Codec}, so a "better method" that already exists there is CONSUMED,
     *  not re-derived — genuinely a BRIDGE between CustomDataType's Java-side convention and this
     *  script-side one, not a parallel reimplementation. {@code P} (the primitive {@code
     *  CustomDataType} actually persists — a {@code String} for UUID/Location/BlockState/BlockPos,
     *  a {@code byte[]} for the CODEC-serialized item types) flows straight through as {@link
     *  #storage()}/the raw value, whatever it is — nothing here assumes String. Only {@code
     *  fromScript}/{@code toScript} — the script<->{@code T} half — is written per type. */
    private static <T, P> Codec fromCustomDataType(dev.arubik.craftengine.util.CustomDataType<T, P> type,
            java.util.function.Function<ScriptValue, T> fromScript,
            java.util.function.Function<T, ScriptValue> toScript) {
        return new Codec() {
            public NbtType storage() { return type.getBaseType(); }

            public Object toStorage(ScriptValue value) {
                try { return type.getSerializer().apply(fromScript.apply(value)); }
                catch (Throwable t) { return storage() == NbtType.BYTE_ARRAY ? new byte[0] : ""; }
            }

            @SuppressWarnings("unchecked")
            public ScriptValue fromStorage(Object raw) {
                if (raw == null) return ScriptValue.NULL;
                try {
                    T decoded = type.getDeserializer().apply((P) raw);
                    return decoded == null ? ScriptValue.NULL : toScript.apply(decoded);
                } catch (Throwable t) {
                    return ScriptValue.NULL;
                }
            }
        };
    }

    /** A UUID in its canonical 36-char string form. A UUID has no wrapped script value type of its
     *  own (see UuidType's javadoc — it's always just a plain string, exactly like {@code
     *  Player.uuid}/{@code Entity.uuid}); a script wanting the MORE COMPACT byte/bit-pair encodings
     *  for a high-volume table reaches for {@code Uuid.to_bytes}/{@code most_bits}/{@code
     *  least_bits} instead (see UuidType) and stores those under "string"/"byte_array". */
    private static final Codec UUID_CODEC = fromCustomDataType(dev.arubik.craftengine.util.CustomDataType.UUID_TYPE,
            value -> { try { return java.util.UUID.fromString(value.asStr()); } catch (Throwable t) { return new java.util.UUID(0, 0); } },
            uuid -> ScriptValue.of(uuid.toString()));

    /** An ARRAY of UUIDs, packed as 16 raw bytes each (most-sig then least-sig long, big-endian —
     *  same layout {@code Uuid.to_bytes} uses for one) concatenated into a single {@code byte[]} —
     *  a compact fixed-stride binary shape instead of a comma-joined string, matching this bridge's
     *  general "prefer real binary over text where one exists" direction (see {@link #ITEM_CODEC}/
     *  {@link #COMPOUND_CODEC}). Malformed entries (not a valid UUID string) are skipped rather than
     *  corrupting the whole array's byte alignment. */
    private static final Codec UUID_ARRAY_CODEC = new Codec() {
        public NbtType storage() { return NbtType.BYTE_ARRAY; }

        public Object toStorage(ScriptValue value) {
            if (!(value instanceof ScriptValue.Array arr)) return new byte[0];
            java.util.List<java.util.UUID> uuids = new java.util.ArrayList<>();
            for (ScriptValue e : arr.elements()) {
                try { uuids.add(java.util.UUID.fromString(e.asStr())); } catch (Throwable ignored) {}
            }
            java.nio.ByteBuffer buf = java.nio.ByteBuffer.allocate(uuids.size() * 16);
            for (java.util.UUID u : uuids) {
                buf.putLong(u.getMostSignificantBits());
                buf.putLong(u.getLeastSignificantBits());
            }
            return buf.array();
        }

        public ScriptValue fromStorage(Object raw) {
            byte[] bytes = (byte[]) raw;
            if (bytes == null || bytes.length == 0) return new ScriptValue.Array(java.util.List.of());
            java.nio.ByteBuffer buf = java.nio.ByteBuffer.wrap(bytes);
            java.util.List<ScriptValue> out = new java.util.ArrayList<>(bytes.length / 16);
            while (buf.remaining() >= 16) out.add(ScriptValue.of(new java.util.UUID(buf.getLong(), buf.getLong()).toString()));
            return new ScriptValue.Array(out);
        }
    };

    /** A {@code Location} (world + x,y,z — see LocationType). Yaw/pitch aren't part of {@code
     *  LocationType.LocationRef} at all (it's position-only), so they aren't stored either — a
     *  script needing orientation stores it as separate values alongside this one. */
    private static final Codec LOCATION_CODEC = fromCustomDataType(dev.arubik.craftengine.util.CustomDataType.LOCATION_TYPE,
            value -> {
                if (!(value instanceof ScriptValue.Obj o) || !"Location".equals(o.typeName())
                        || !(o.instance() instanceof dev.arubik.craftengine.script.types.world.LocationType.LocationRef ref)
                        || ref.level() == null) return null;
                return new org.bukkit.Location(ref.level().getWorld(), ref.x(), ref.y(), ref.z());
            },
            bukkitLoc -> {
                if (bukkitLoc.getWorld() == null || !(bukkitLoc.getWorld() instanceof org.bukkit.craftbukkit.CraftWorld craftWorld))
                    return ScriptValue.NULL;
                return dev.arubik.craftengine.script.types.world.LocationType.wrap(craftWorld.getHandle(),
                        bukkitLoc.getX(), bukkitLoc.getY(), bukkitLoc.getZ());
            });

    /** A single block's {@code BlockState}, packed as its block-data string ({@code
     *  "minecraft:oak_stairs[facing=north,...]"}). */
    private static final Codec BLOCK_STATE_CODEC = fromCustomDataType(dev.arubik.craftengine.util.CustomDataType.BLOCK_STATE_TYPE,
            value -> value instanceof ScriptValue.Obj o && o.instance() instanceof net.minecraft.world.level.block.state.BlockState state
                    ? state : null,
            state -> ScriptValue.ofObj("BlockState", state));

    /** A block-space integer position, packed as {@code "x,y,z"}. Distinct from "location" (which
     *  also carries a world and double precision); this is for a script value that's already just a
     *  position within whatever world context it's being used in (e.g. a stored offset, not an
     *  absolute destination). */
    private static final Codec BLOCK_POS_CODEC = fromCustomDataType(dev.arubik.craftengine.util.CustomDataType.BLOCK_POS_TYPE,
            value -> value instanceof ScriptValue.Obj o && o.instance() instanceof net.minecraft.core.BlockPos pos
                    ? pos : new net.minecraft.core.BlockPos((int) value.asNum(), 0, 0),
            pos -> ScriptValue.ofObj("BlockPos", pos));

    /** A full ItemStack (identity + every data component), serialized via its own vanilla CODEC
     *  ({@code CustomDataType.ITEM_CODEC_TYPE} — survives item-component/NBT changes across
     *  versions far better than Bukkit's {@code serializeAsBytes}, which the OLD version of this
     *  codec used) and stored as a compressed {@code byte[]} instead of base64 text — smaller, and
     *  the natural fit for a BLOB/byte_array column when this ends up in SQL. */
    private static final Codec ITEM_CODEC = fromCustomDataType(dev.arubik.craftengine.util.CustomDataType.ITEM_CODEC_TYPE,
            value -> value instanceof ScriptValue.Item itemVal && itemVal.stack() != null
                    ? itemVal.stack() : net.minecraft.world.item.ItemStack.EMPTY,
            nms -> nms == null || nms.isEmpty() ? ScriptValue.NULL : ScriptValue.ofItem(nms));

    /** An ARRAY of ItemStacks (e.g. a whole inventory snapshot) — {@code
     *  CustomDataType.ITEM_ARRAY_CODEC_TYPE}, same CODEC-per-slot serialization as "item" above,
     *  packed together as one {@code byte[]}. Empty slots round-trip as {@code ScriptValue.NULL}
     *  entries in the array (matching {@code ITEM_ARRAY_CODEC_TYPE}'s own EMPTY-fill convention). */
    private static final Codec ITEM_ARRAY_CODEC = fromCustomDataType(dev.arubik.craftengine.util.CustomDataType.ITEM_ARRAY_CODEC_TYPE,
            value -> {
                if (!(value instanceof ScriptValue.Array arr)) return new net.minecraft.world.item.ItemStack[0];
                net.minecraft.world.item.ItemStack[] out = new net.minecraft.world.item.ItemStack[arr.elements().size()];
                for (int i = 0; i < out.length; i++) {
                    ScriptValue e = arr.elements().get(i);
                    out[i] = e instanceof ScriptValue.Item item && item.stack() != null
                            ? item.stack() : net.minecraft.world.item.ItemStack.EMPTY;
                }
                return out;
            },
            stacks -> {
                java.util.List<ScriptValue> out = new java.util.ArrayList<>(stacks.length);
                for (net.minecraft.world.item.ItemStack s : stacks)
                    out.add(s == null || s.isEmpty() ? ScriptValue.NULL : ScriptValue.ofItem(s));
                return new ScriptValue.Array(out);
            });

    /** A {@code Vector} (x,y,z) packed as a comma-joined string. */
    private static final Codec VECTOR_CODEC = new Codec() {
        public NbtType storage() { return NbtType.STRING; }

        public Object toStorage(ScriptValue value) {
            if (!(value instanceof ScriptValue.Obj o) || !(o.instance() instanceof org.joml.Vector3d v)) return "";
            return v.x + "," + v.y + "," + v.z;
        }

        public ScriptValue fromStorage(Object raw) {
            String s = (String) raw;
            if (s == null || s.isEmpty()) return ScriptValue.NULL;
            try {
                String[] parts = s.split(",", -1);
                if (parts.length < 3) return ScriptValue.NULL;
                return dev.arubik.craftengine.script.types.primitive.VectorType.wrap(
                        Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
            } catch (Throwable t) {
                return ScriptValue.NULL;
            }
        }
    };

    /** A whole {@code Map} value tree (nested maps/arrays/primitives/items), persisted as real NBT
     *  via {@link NbtCodec} — see NbtCodec's own javadoc for why (a self-describing box per value,
     *  not type-sniffed) — and packed as COMPRESSED BINARY ({@code byte[]}, gzip'd NBT) rather than
     *  SNBT text: smaller on disk/in a database, and the natural {@code byte_array}/BLOB shape
     *  everywhere else in this bridge already uses for a non-trivial payload (see {@link
     *  #ITEM_CODEC}). A script never sees the bytes directly either way — this only changes what's
     *  actually stored under the hood. */
    private static final Codec COMPOUND_CODEC = new Codec() {
        public NbtType storage() { return NbtType.BYTE_ARRAY; }

        public Object toStorage(ScriptValue value) {
            if (!(value instanceof ScriptValue.Obj o) || !"Map".equals(o.typeName())) return new byte[0];
            try {
                @SuppressWarnings("unchecked")
                Map<String, ScriptValue> map = (Map<String, ScriptValue>) o.instance();
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                net.minecraft.nbt.NbtIo.writeCompressed(NbtCodec.encodeMap(map), baos);
                return baos.toByteArray();
            } catch (Throwable t) {
                return new byte[0];
            }
        }

        public ScriptValue fromStorage(Object raw) {
            byte[] bytes = (byte[]) raw;
            if (bytes == null || bytes.length == 0) return dev.arubik.craftengine.script.types.primitive.MapType.wrap(new java.util.LinkedHashMap<>());
            try {
                net.minecraft.nbt.CompoundTag tag = net.minecraft.nbt.NbtIo.readCompressed(
                        new java.io.ByteArrayInputStream(bytes), net.minecraft.nbt.NbtAccounter.unlimitedHeap());
                return dev.arubik.craftengine.script.types.primitive.MapType.wrap(NbtCodec.decodeMap(tag));
            } catch (Throwable t) {
                return dev.arubik.craftengine.script.types.primitive.MapType.wrap(new java.util.LinkedHashMap<>());
            }
        }
    };
}
