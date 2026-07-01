package dev.arubik.craftengine.util;

import org.bukkit.NamespacedKey;

public class TypedKey<T> {
    private final NamespacedKey key;
    private final NbtType type;
    private final CustomDataType<T, ?> customType;

    // Constructor para tipos nativos
    public TypedKey(NamespacedKey key, NbtType type) {
        this.key = key;
        this.type = type;
        this.customType = null;
    }

    // Constructor para tipos personalizados
    public <P> TypedKey(NamespacedKey key, CustomDataType<T, P> customType) {
        this.key = key;
        this.type = null;
        this.customType = customType;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public NbtType getType() {
        return type;
    }

    public CustomDataType<T, ?> getCustomType() {
        return customType;
    }

    public boolean isCustom() {
        return customType != null;
    }

    public static <T> TypedKey<T> of(String namespace, String key, NbtType type) {
        return new TypedKey<>(new NamespacedKey(namespace, key), type);
    }

    public static <T, P> TypedKey<T> of(String namespace, String key, CustomDataType<T, P> customType) {
        return new TypedKey<>(new NamespacedKey(namespace, key), customType);
    }

    public static <E extends Enum<E>> TypedKey<E> forEnum(String namespace, String key, Class<E> enumClass) {
        return TypedKey.of(namespace, key, new CustomDataType<>(
                NbtType.STRING,
                Enum::name,
                s -> Enum.valueOf(enumClass, s)
        ));
    }


    public static <T> T getOfCompound(TypedKey<T> key, net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        if (key.isCustom()) {
            @SuppressWarnings("unchecked")
            CustomDataType<T, Object> customType = (CustomDataType<T, Object>) key.getCustomType();
            Object primitive = switch (key.getCustomType().getBaseType()) {
                case BOOLEAN -> tag.getBoolean(key.getKey().getKey());
                case BYTE -> tag.getByte(key.getKey().getKey());
                case BYTE_ARRAY -> tag.getByteArray(key.getKey().getKey());
                case DOUBLE -> tag.getDouble(key.getKey().getKey());
                case FLOAT -> tag.getFloat(key.getKey().getKey());
                case INTEGER -> tag.getInt(key.getKey().getKey());
                case LONG -> tag.getLong(key.getKey().getKey());
                case SHORT -> tag.getShort(key.getKey().getKey());
                case STRING -> tag.getString(key.getKey().getKey());
                default -> null;
            };
            return primitive != null ? customType.getDeserializer().apply(primitive) : null;
        } else {
            return switch (key.getType()) {
                case BYTE_ARRAY -> (T) tag.getByteArray(key.getKey().getKey());
                case STRING -> (T) tag.getString(key.getKey().getKey());
                case INTEGER -> (T) Integer.valueOf(tag.getInt(key.getKey().getKey()));
                case DOUBLE -> (T) Double.valueOf(tag.getDouble(key.getKey().getKey()));
                case FLOAT -> (T) Float.valueOf(tag.getFloat(key.getKey().getKey()));
                case LONG -> (T) Long.valueOf(tag.getLong(key.getKey().getKey()));
                case SHORT -> (T) Short.valueOf(tag.getShort(key.getKey().getKey()));
                case BYTE -> (T) Byte.valueOf(tag.getByte(key.getKey().getKey()));
                default -> null;
            };
        }
    }

}
