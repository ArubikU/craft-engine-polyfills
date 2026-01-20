package dev.arubik.craftengine.util;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.minecraft.nbt.CompoundTag;

public class TypedKey<T> {
    private final NamespacedKey key;
    private final PersistentDataType<?, T> type;
    private final CustomDataType<T, ?> customType;

    // Constructor para tipos nativos
    public TypedKey(NamespacedKey key, PersistentDataType<?, T> type) {
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

    public PersistentDataType<?, T> getType() {
        return type;
    }

    public CustomDataType<T, ?> getCustomType() {
        return customType;
    }

    public boolean isCustom() {
        return customType != null;
    }

    public static <T> TypedKey<T> of(String namespace, String key, PersistentDataType<?, T> type) {
        return new TypedKey<>(new NamespacedKey(namespace, key), type);
    }

    public static <T, P> TypedKey<T> of(String namespace, String key, CustomDataType<T, P> customType) {
        return new TypedKey<>(new NamespacedKey(namespace, key), customType);
    }

    public static <E extends Enum<E>> TypedKey<E> forEnum(String namespace, String key, Class<E> enumClass) {
        return TypedKey.of(namespace, key, new CustomDataType<>(
                PersistentDataType.STRING,
                Enum::name,
                s -> Enum.valueOf(enumClass, s)
        ));
    }

    
    public static <T> void set(TypedKey<T> key, T value, PersistentDataContainer pdc) {
        if(value == null) {
            pdc.remove(key.getKey());
            return;
        }
        if (key.isCustom()) {
            @SuppressWarnings("unchecked")
            CustomDataType<T, Object> customType = (CustomDataType<T, Object>) key.getCustomType();

            if(customType.isContainerSerializer()){
                PersistentDataContainer container = pdc.getAdapterContext().newPersistentDataContainer();
                pdc.set(key.getKey(), customType.getBaseType(), customType.getContainerSerializer().apply(value, container));
            }else{
                Object primitive = customType.getSerializer().apply(value);
                pdc.set(key.getKey(), customType.getBaseType(), primitive);
            }
        } else {
            pdc.set(key.getKey(), key.getType(), value);
        }
    }
    public static boolean has(TypedKey<?> key, PersistentDataContainer pdc) {
        return pdc.has(key.getKey(), key.getType());
    }

    public static <T> T get(TypedKey<T> key, PersistentDataContainer pdc) {

        if (key.isCustom()) {
            @SuppressWarnings("unchecked")
            CustomDataType<T, Object> customType = (CustomDataType<T, Object>) key.getCustomType();
            Object primitive = pdc.get(key.getKey(), customType.getBaseType());
            return primitive != null ? customType.getDeserializer().apply(primitive) : null;
        } else {
            return pdc.get(key.getKey(), key.getType());
        }
    }


    public static <T> T getOfCompound(TypedKey<T> key, net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        if (key.isCustom()) {
            @SuppressWarnings("unchecked")
            CustomDataType<T, Object> customType = (CustomDataType<T, Object>) key.getCustomType();
            Object primitive = null;
            if(key.getCustomType().getBaseType()==PersistentDataType.BOOLEAN){
                primitive = tag.getBoolean(key.getKey().getKey());
            }else if(key.getCustomType().getBaseType()==PersistentDataType.BYTE){
                primitive = tag.getByte(key.getKey().getKey());
            }else if(key.getCustomType().getBaseType()==PersistentDataType.BYTE_ARRAY){ 
                primitive = tag.getByteArray(key.getKey().getKey());
            }else if(key.getCustomType().getBaseType()==PersistentDataType.DOUBLE){
                primitive = tag.getDouble(key.getKey().getKey());
            }else if(key.getCustomType().getBaseType()==PersistentDataType.FLOAT){
                primitive = tag.getFloat(key.getKey().getKey());
            }else if(key.getCustomType().getBaseType()==PersistentDataType.INTEGER){
                primitive = tag.getInt(key.getKey().getKey());
            }else if(key.getCustomType().getBaseType()==PersistentDataType.LONG){
                primitive = tag.getLong(key.getKey().getKey());
            }else if(key.getCustomType().getBaseType()==PersistentDataType.SHORT){
                primitive = tag.getShort(key.getKey().getKey());
            }else if(key.getCustomType().getBaseType()==PersistentDataType.STRING){
                primitive = tag.getString(key.getKey().getKey());
            }
            return primitive != null ? customType.getDeserializer().apply(primitive) : null;
        } else {
            if(key.getType() == PersistentDataType.BYTE_ARRAY){
                return (T) tag.getByteArray(key.getKey().getKey());
            }else if(key.getType() == PersistentDataType.STRING){
                return (T) tag.getString(key.getKey().getKey());
            }else if(key.getType() == PersistentDataType.INTEGER){
                return (T) Integer.valueOf(tag.getInt(key.getKey().getKey()));
            }else if(key.getType() == PersistentDataType.DOUBLE){
                return (T) Double.valueOf(tag.getDouble(key.getKey().getKey()));
            }else if(key.getType() == PersistentDataType.FLOAT){
                return (T) Float.valueOf(tag.getFloat(key.getKey().getKey()));
            }else if(key.getType() == PersistentDataType.LONG){
                return (T) Long.valueOf(tag.getLong(key.getKey().getKey()));
            }else if(key.getType() == PersistentDataType.SHORT){
                return (T) Short.valueOf(tag.getShort(key.getKey().getKey()));
            }else if(key.getType() == PersistentDataType.BYTE){
                return (T) Byte.valueOf(tag.getByte(key.getKey().getKey()));
            }
        }
        return null;
    }

}
