package dev.arubik.craftengine.util;

import net.minecraft.resources.Identifier;

/**
 * Typed NBT key for block entity storage. Uses NMS Identifier — no Bukkit dependency.
 * Stored in CE CompoundTag under "namespace:path" key string.
 */
public class TypedKey<T> {
    private final Identifier id;
    private final NbtType type;
    private final CustomDataType<T, ?> customType;

    public TypedKey(Identifier id, NbtType type) {
        this.id = id;
        this.type = type;
        this.customType = null;
    }

    public <P> TypedKey(Identifier id, CustomDataType<T, P> customType) {
        this.id = id;
        this.type = null;
        this.customType = customType;
    }

    /** The storage key string used in CompoundTag.
     * For backwards compatibility with existing saved data, uses only the path portion
     * (e.g. "flag_rpm" not "polyfills:flag_rpm"). Matches the old NamespacedKey.getKey() behavior.
     */
    public String nbtKey() {
        return id.getPath(); // "flag_rpm" — same as NamespacedKey.getKey() was
    }

    /** Full namespaced string "namespace:path". */
    public String fullKey() {
        return id.toString();
    }

    public Identifier getId() { return id; }
    /** @deprecated Use {@link #getId()} or {@link #nbtKey()} */
    @Deprecated
    public Identifier getKey() { return id; }
    public NbtType getType() { return type; }
    public CustomDataType<T, ?> getCustomType() { return customType; }
    public boolean isCustom() { return customType != null; }

    public static <T> TypedKey<T> of(String namespace, String path, NbtType type) {
        return new TypedKey<>(Identifier.parse(namespace + ":" + path), type);
    }

    public static <T, P> TypedKey<T> of(String namespace, String path, CustomDataType<T, P> customType) {
        return new TypedKey<>(Identifier.parse(namespace + ":" + path), customType);
    }

    public static <E extends Enum<E>> TypedKey<E> forEnum(String namespace, String path, Class<E> enumClass) {
        return TypedKey.of(namespace, path, new CustomDataType<>(
                NbtType.STRING, Enum::name, s -> Enum.valueOf(enumClass, s)));
    }

    public static <T> T getOfCompound(TypedKey<T> key, net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        String k = key.nbtKey();
        if (key.isCustom()) {
            @SuppressWarnings("unchecked")
            CustomDataType<T, Object> ct = (CustomDataType<T, Object>) key.getCustomType();
            Object primitive = switch (ct.getBaseType()) {
                case BOOLEAN    -> tag.getBoolean(k);
                case BYTE       -> tag.getByte(k);
                case BYTE_ARRAY -> tag.getByteArray(k);
                case DOUBLE     -> tag.getDouble(k);
                case FLOAT      -> tag.getFloat(k);
                case INTEGER    -> tag.getInt(k);
                case LONG       -> tag.getLong(k);
                case SHORT      -> tag.getShort(k);
                case STRING     -> tag.getString(k);
                default -> null;
            };
            return primitive != null ? ct.getDeserializer().apply(primitive) : null;
        }
        return switch (key.getType()) {
            case BYTE_ARRAY -> (T) tag.getByteArray(k);
            case STRING     -> (T) tag.getString(k);
            case INTEGER    -> (T) Integer.valueOf(tag.getInt(k));
            case DOUBLE     -> (T) Double.valueOf(tag.getDouble(k));
            case FLOAT      -> (T) Float.valueOf(tag.getFloat(k));
            case LONG       -> (T) Long.valueOf(tag.getLong(k));
            case SHORT      -> (T) Short.valueOf(tag.getShort(k));
            case BYTE       -> (T) Byte.valueOf(tag.getByte(k));
            default -> null;
        };
    }
}
