package dev.arubik.craftengine.script;

import dev.arubik.craftengine.util.NbtType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shared script<->{@link NbtType} bridge — standardizes how EVERY typed-key primitive (Machine's
 * block entity, Item's custom data, ...) exchanges values with scripts, instead of each feature
 * inventing its own ad hoc encoding (the way get_item_flag base64-encodes a whole ItemStack, or
 * get_flag/get_str_flag hardcode int/string). A script picks a type by name ("int", "double",
 * "bool", "byte_array", "item", ...) so one key/type pair round-trips identically everywhere.
 *
 * <p>Beyond the primitive {@link NbtType}s, this also supports CUSTOM named types — a {@link Codec}
 * bundles "how to turn a script value into the ONE primitive that actually gets stored" with its
 * inverse, the same shape {@link dev.arubik.craftengine.util.TypedKey}'s own {@code CustomDataType}
 * uses on the Java side (e.g. {@code TypedKey.forEnum}), just script-reachable. Register one from
 * anywhere at plugin startup via {@link #registerCustom} to add a whole new "type name" every
 * get_typed/set_typed/with_typed call across Machine AND Item picks up for free — no new methods
 * needed. "item" and "vector" ship built-in as the two everyone eventually wants.
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

    /** The "absent" default for a type — same convention as get_flag (0) / get_str_flag (""). */
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
    }

    /** A full ItemStack (identity + every data component), base64-encoded — the exact scheme
     *  get_item_flag/set_item_flag already used, now reachable as any other typed value instead of
     *  its own bespoke method pair. */
    private static final Codec ITEM_CODEC = new Codec() {
        public NbtType storage() { return NbtType.STRING; }

        public Object toStorage(ScriptValue value) {
            if (!(value instanceof ScriptValue.Item itemVal) || itemVal.stack() == null || itemVal.stack().isEmpty()) {
                return "";
            }
            try {
                org.bukkit.inventory.ItemStack bukkit =
                        org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(itemVal.stack());
                return java.util.Base64.getEncoder().encodeToString(bukkit.serializeAsBytes());
            } catch (Throwable t) {
                return "";
            }
        }

        public ScriptValue fromStorage(Object raw) {
            String encoded = (String) raw;
            if (encoded == null || encoded.isEmpty()) return ScriptValue.NULL;
            try {
                byte[] bytes = java.util.Base64.getDecoder().decode(encoded);
                org.bukkit.inventory.ItemStack bukkit = org.bukkit.inventory.ItemStack.deserializeBytes(bytes);
                if (bukkit == null || bukkit.getType().isAir()) return ScriptValue.NULL;
                net.minecraft.world.item.ItemStack nms =
                        org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkit);
                return ScriptValue.ofItem(nms);
            } catch (Throwable t) {
                return ScriptValue.NULL;
            }
        }
    };

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
     *  via {@link NbtCodec} and packed to SNBT text so it still fits one STRING primitive under the
     *  hood — see NbtCodec's own javadoc for why (a self-describing box per value, not type-sniffed). */
    private static final Codec COMPOUND_CODEC = new Codec() {
        public NbtType storage() { return NbtType.STRING; }

        public Object toStorage(ScriptValue value) {
            if (!(value instanceof ScriptValue.Obj o) || !"Map".equals(o.typeName())) return "";
            try {
                @SuppressWarnings("unchecked")
                Map<String, ScriptValue> map = (Map<String, ScriptValue>) o.instance();
                return NbtCodec.encodeMap(map).toString();
            } catch (Throwable t) {
                return "";
            }
        }

        public ScriptValue fromStorage(Object raw) {
            String snbt = (String) raw;
            if (snbt == null || snbt.isEmpty()) return dev.arubik.craftengine.script.types.primitive.MapType.wrap(new java.util.LinkedHashMap<>());
            try {
                net.minecraft.nbt.CompoundTag tag = net.minecraft.nbt.TagParser.parseCompoundFully(snbt);
                return dev.arubik.craftengine.script.types.primitive.MapType.wrap(NbtCodec.decodeMap(tag));
            } catch (Throwable t) {
                return dev.arubik.craftengine.script.types.primitive.MapType.wrap(new java.util.LinkedHashMap<>());
            }
        }
    };
}
