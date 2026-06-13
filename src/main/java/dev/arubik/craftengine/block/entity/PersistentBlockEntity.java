package dev.arubik.craftengine.block.entity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.arubik.craftengine.util.CustomDataType;
import dev.arubik.craftengine.util.TypedKey;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.CompositeBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;

/**
 * Re-based onto {@link BlockEntityController} for craft-engine 26.6.2
 * ({@code BlockEntity} is now final). The engine constructs the {@link BlockEntity}
 * and stores this controller in {@code blockEntity.controller}.
 */
public class PersistentBlockEntity extends BlockEntityController {

    private CompoundTag container;

    public Function<CompoundTag, Void> preRemoveHook = null;

    public Function<PersistentBlockEntity, Void> preCleanup = null;

    public PersistentBlockEntity(BlockEntity blockEntity) {
        super(blockEntity);
        this.container = new CompoundTag();
    }

    // Thin accessors replacing the old inherited BlockEntity members.
    public ImmutableBlockState blockState() {
        return blockEntity().blockState();
    }

    public void setPreRemoveHook(Function<CompoundTag, Void> hook) {
        this.preRemoveHook = hook;
    }

    public void setPreCleanup(Function<PersistentBlockEntity, Void> hook) {
        this.preCleanup = hook;
    }

    @Override
    public void onRemove() {

        if (this.preRemoveHook != null) {
            this.preRemoveHook.apply(this.container);
        }
        if (this.preCleanup != null) {
            this.preCleanup.apply(this);
        }
    }

    // Delegación de métodos

    public <P, C> boolean has(NamespacedKey key, PersistentDataType<P, C> type) {
        return container.containsKey(key.getKey());
    }

    public boolean has(NamespacedKey key) {
        return container.containsKey(key.getKey());
    }

    public <P, C> @Nullable C get(NamespacedKey key, PersistentDataType<P, C> type) {
        if (!container.containsKey(key.getKey())) {
            return null;
        }

        // Handle primitive types directly
        if (type == PersistentDataType.STRING) {
            return (C) container.getString(key.getKey());
        } else if (type == PersistentDataType.INTEGER) {
            return (C) Integer.valueOf(container.getInt(key.getKey()));
        } else if (type == PersistentDataType.DOUBLE) {
            return (C) Double.valueOf(container.getDouble(key.getKey()));
        } else if (type == PersistentDataType.BYTE) {
            return (C) Byte.valueOf(container.getByte(key.getKey()));
        } else if (type == PersistentDataType.BYTE_ARRAY) {
            return (C) container.getByteArray(key.getKey());
        } else if (type == PersistentDataType.LONG) {
            return (C) Long.valueOf(container.getLong(key.getKey()));
        } else if (type == PersistentDataType.FLOAT) {
            return (C) Float.valueOf(container.getFloat(key.getKey()));
        } else if (type == PersistentDataType.SHORT) {
            return (C) Short.valueOf(container.getShort(key.getKey()));
        } else if (type == PersistentDataType.INTEGER_ARRAY) {
            return (C) container.getIntArray(key.getKey());
        } else if (type == PersistentDataType.LONG_ARRAY) {
            return (C) container.getLongArray(key.getKey());
        } else if (type == PersistentDataType.BOOLEAN) {
            byte value = container.getByte(key.getKey());
            return (C) Boolean.valueOf(value != 0);
        }

        return null;
    }

    public <P, C> @Nullable C get(Key key, PersistentDataType<P, C> type) {
        return this.get(NamespacedKey.fromString(key.toString()), type);
    }

    public <P, C> C getOrDefault(NamespacedKey key, PersistentDataType<P, C> type, C defaultValue) {
        if (has(key, type)) {
            return get(key, type);
        }
        return defaultValue;
    }

    public <P, C> C getOrDefault(Key key, PersistentDataType<P, C> type, C defaultValue) {
        return this.getOrDefault(NamespacedKey.fromString(key.toString()), type, defaultValue);
    }

    public Set<NamespacedKey> getKeys() {
        return container.keySet().stream().map(NamespacedKey::fromString).collect(java.util.stream.Collectors.toSet());
    }

    public boolean isEmpty() {
        return container.isEmpty();
    }

    public byte[] serializeToBytes() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        container.write(dataOutputStream);
        dataOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void saveCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        this.container.keySet().forEach(key -> {
            tag.put(key, container.get(key));
        });
    }

    @Override
    public void loadCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        this.container = tag;
    }

    public <P, C> void set(@NotNull NamespacedKey key, @NotNull PersistentDataType<P, C> type, @NotNull C value) {
        // Handle primitive types directly
        if (type == PersistentDataType.STRING) {
            container.putString(key.getKey(), (String) value);
        } else if (type == PersistentDataType.INTEGER) {
            container.putInt(key.getKey(), (Integer) value);
        } else if (type == PersistentDataType.DOUBLE) {
            container.putDouble(key.getKey(), (Double) value);
        } else if (type == PersistentDataType.BYTE) {
            container.putByte(key.getKey(), (Byte) value);
        } else if (type == PersistentDataType.BYTE_ARRAY) {
            container.putByteArray(key.getKey(), (byte[]) value);
        } else if (type == PersistentDataType.LONG) {
            container.putLong(key.getKey(), (Long) value);
        } else if (type == PersistentDataType.FLOAT) {
            container.putFloat(key.getKey(), (Float) value);
        } else if (type == PersistentDataType.SHORT) {
            container.putShort(key.getKey(), (Short) value);
        } else if (type == PersistentDataType.INTEGER_ARRAY) {
            container.putIntArray(key.getKey(), (int[]) value);
        } else if (type == PersistentDataType.LONG_ARRAY) {
            container.putLongArray(key.getKey(), (long[]) value);
        } else if (type == PersistentDataType.BOOLEAN) {
            container.putByte(key.getKey(), (byte) ((Boolean) value ? 1 : 0));
        } else {
            throw new IllegalArgumentException("Unsupported PersistentDataType: " + type);
        }
    }

    public <P, C> void setIfAbsent(@NotNull NamespacedKey key, @NotNull PersistentDataType<P, C> type,
            @NotNull C value) {
        if (!has(key, type)) {
            set(key, type, value);
        }
    }

    public <P, C> void set(@NotNull Key key, @NotNull PersistentDataType<P, C> type, @NotNull C value) {
        this.set(NamespacedKey.fromString(key.toString()), type, value);
    }

    public void remove(@NotNull NamespacedKey key) {
        container.remove(key.getKey());
    }

    public <T> void set(TypedKey<T> key, T value) {
        if (value == null) {
            remove(key);
            return;
        }
        if (key.isCustom()) {
            @SuppressWarnings("unchecked")
            CustomDataType<T, Object> customType = (CustomDataType<T, Object>) key.getCustomType();

            Object primitive = customType.getSerializer().apply(value);
            set(key.getKey(), customType.getBaseType(), primitive);

        } else {
            set(key.getKey(), key.getType(), value);
        }
    }

    public <T> T get(TypedKey<T> key) {

        if (key.isCustom()) {
            @SuppressWarnings("unchecked")
            CustomDataType<T, Object> customType = (CustomDataType<T, Object>) key.getCustomType();
            Object primitive = get(key.getKey(), customType.getBaseType());
            return primitive != null ? customType.getDeserializer().apply(primitive) : null;
        } else {
            return get(key.getKey(), key.getType());
        }
    }

    public <T> Optional<T> getOptional(TypedKey<T> key) {
        return Optional.ofNullable(get(key));
    }

    public <T> boolean has(TypedKey<T> key) {
        return has(key.getKey(), key.getType());
    }

    public <T> void remove(TypedKey<T> key) {
        if (key == null)
            return;
        if (has(key)) {
            remove(key.getKey());
        }
    }

    public <T> T getOrDefault(TypedKey<T> key, T defaultValue) {
        T value = get(key);
        return value != null ? value : defaultValue;
    }

    public BlockBehavior getBlockBehavior() {
        Optional<ImmutableBlockState> customStateOpt = BlockStateUtils.getOptionalCustomBlockState(blockEntity().blockState());
        if (customStateOpt.isPresent()) {
            return customStateOpt.get().behavior();
        }
        return null;
    }

    public <T> T getBlockBehavior(Class<T> clazz) {
        Optional<ImmutableBlockState> customStateOpt = BlockStateUtils.getOptionalCustomBlockState(blockEntity().blockState());
        if (customStateOpt.isPresent()) {
            // check if is instance of or implements etc
            if (customStateOpt.get().behavior().getClass().isInstance(clazz)) {
                return (T) customStateOpt.get().behavior();
            }
            if (customStateOpt.get().behavior() instanceof CompositeBlockBehavior beh) {
                T found = beh.getFirst(clazz);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
