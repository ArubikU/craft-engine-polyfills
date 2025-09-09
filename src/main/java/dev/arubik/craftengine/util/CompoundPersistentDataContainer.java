package dev.arubik.craftengine.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class CompoundPersistentDataContainer implements PersistentDataContainer {

    private CompoundTag tag;

    public CompoundPersistentDataContainer(CompoundTag tag) {
        this.tag = tag;
    }

    public CompoundTag getTag() {
        return tag;
    }

    private String keyToString(NamespacedKey key) {
        return key.getNamespace() + ":" + key.getKey();
    }

    @Override
    public <P, C> boolean has(NamespacedKey key, PersistentDataType<P, C> type) {
        String name = keyToString(key);
        return tag.contains(name);
    }

    @Override
    public boolean has(NamespacedKey key) {
        String name = keyToString(key);
        return tag.contains(name);
    }

    @Override
    public <P, C> @Nullable C get(NamespacedKey key, PersistentDataType<P, C> type) {
        String name = keyToString(key);
        if (!tag.contains(name)) return null;
        P primitive = type.getPrimitiveType().cast(tag.get(name));
        return type.fromPrimitive(primitive, getAdapterContext());
    }

    @Override
    public <P, C> C getOrDefault(NamespacedKey key, PersistentDataType<P, C> type, C defaultValue) {
        C value = get(key, type);
        return value != null ? value : defaultValue;
    }

    @Override
    public Set<NamespacedKey> getKeys() {
        Set<NamespacedKey> keys = new HashSet<>();
        for (String name : tag.keySet()) {
            if (name.contains(":")) {
                String[] parts = name.split(":", 2);
                keys.add(new NamespacedKey(parts[0], parts[1]));
            } else {
                keys.add(new NamespacedKey("minecraft", name));
            }
        }
        return keys;
    }

    @Override
    public boolean isEmpty() {
        return tag.isEmpty();
    }

    @Override
    public void copyTo(PersistentDataContainer other, boolean replace) {
        for (NamespacedKey key : getKeys()) {
            for (PersistentDataType<?, ?> type : CustomBlockData.PRIMITIVE_DATA_TYPES) {
                Object value = get(key, type);
                if (value != null) {
                    if (replace || !other.has(key, type)) {
                        setInternal(other, key, type, value);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <P, C> void setInternal(PersistentDataContainer container, NamespacedKey key, PersistentDataType<P, C> type, Object value) {
        container.set(key, (PersistentDataType<P, C>) type, (C) value);
    }

    @Override
    public PersistentDataAdapterContext getAdapterContext() {
        return new PersistentDataAdapterContext() {
            @Override
            public @NotNull PersistentDataContainer newPersistentDataContainer() {
                return new CompoundPersistentDataContainer(new CompoundTag());
            }
        };
    }

    @Override
    public byte[] serializeToBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        NbtIo.writeCompressed(tag, out);
        return out.toByteArray();
    }

    @Override
    public <P, C> void set(@NotNull NamespacedKey key, @NotNull PersistentDataType<P, C> type, @NotNull C value) {
        String name = keyToString(key);
        P primitive = type.toPrimitive(value, getAdapterContext());
        if (primitive instanceof Tag) {
            tag.put(name, (Tag) primitive);
        } else {
            throw new IllegalArgumentException("Unsupported primitive type: " + primitive.getClass());
        }
    }

    @Override
    public void remove(@NotNull NamespacedKey key) {
        tag.remove(keyToString(key));
    }

    public void consume(Function<CompoundTag, CompoundTag> function) {
        this.tag = function.apply(tag);
    }

    @Override
    public void readFromBytes(byte @NotNull [] bytes, boolean clear) throws IOException {
        CompoundTag loaded = NbtIo.readCompressed(new ByteArrayInputStream(bytes), net.minecraft.nbt.NbtAccounter.unlimitedHeap());
        if (clear) tag.keySet().forEach(tag::remove);
        tag.merge(loaded);
    }
}
