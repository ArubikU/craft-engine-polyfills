package dev.arubik.craftengine.block.entity;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.arubik.craftengine.util.CustomDataType;
import dev.arubik.craftengine.util.NbtType;
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

    public boolean has(NamespacedKey key, NbtType type) {
        return container.containsKey(key.getKey());
    }

    public boolean has(NamespacedKey key) {
        return container.containsKey(key.getKey());
    }

    @SuppressWarnings("unchecked")
    public <C> @Nullable C get(NamespacedKey key, NbtType type) {
        if (!container.containsKey(key.getKey())) {
            return null;
        }

        // Handle primitive types directly
        return switch (type) {
            case STRING -> (C) container.getString(key.getKey());
            case INTEGER -> (C) Integer.valueOf(container.getInt(key.getKey()));
            case DOUBLE -> (C) Double.valueOf(container.getDouble(key.getKey()));
            case BYTE -> (C) Byte.valueOf(container.getByte(key.getKey()));
            case BYTE_ARRAY -> (C) container.getByteArray(key.getKey());
            case LONG -> (C) Long.valueOf(container.getLong(key.getKey()));
            case FLOAT -> (C) Float.valueOf(container.getFloat(key.getKey()));
            case SHORT -> (C) Short.valueOf(container.getShort(key.getKey()));
            case INTEGER_ARRAY -> (C) container.getIntArray(key.getKey());
            case LONG_ARRAY -> (C) container.getLongArray(key.getKey());
            case BOOLEAN -> (C) Boolean.valueOf(container.getByte(key.getKey()) != 0);
        };
    }

    public <C> @Nullable C get(Key key, NbtType type) {
        return this.get(NamespacedKey.fromString(key.toString()), type);
    }

    public <C> C getOrDefault(NamespacedKey key, NbtType type, C defaultValue) {
        if (has(key, type)) {
            return get(key, type);
        }
        return defaultValue;
    }

    public <C> C getOrDefault(Key key, NbtType type, C defaultValue) {
        return this.getOrDefault(NamespacedKey.fromString(key.toString()), type, defaultValue);
    }

    public Set<NamespacedKey> getKeys() {
        return container.keySet().stream().map(NamespacedKey::fromString).collect(java.util.stream.Collectors.toSet());
    }

    /** Raw key -> string-rendered value dump of this block entity's persisted data, for debug tooling. */
    public java.util.Map<String, String> debugDump() {
        java.util.Map<String, String> out = new java.util.LinkedHashMap<>();
        for (String key : container.keySet()) {
            out.put(key, String.valueOf(container.get(key)));
        }
        return out;
    }

    public boolean isEmpty() {
        return container.isEmpty();
    }

    /**
     * Exports this block entity's whole persisted state as a self-describing NBT blob via
     * {@code NBT.toBytes} (a leading type-id byte + full tag tree) — NOT {@link CompoundTag#write},
     * which only writes the compound's raw body and is not byte-compatible with a generic reader.
     * {@link #loadFromBytes} is the exact inverse.
     *
     * <p>Routes through {@link #saveCustomData} (not a raw dump of {@link #container}) — a subclass
     * like {@link PersistentWorldlyBlockEntity} only flushes its LIVE in-memory state (e.g. the
     * {@code inventory[]} array) into {@code container} INSIDE its own {@code saveCustomData}
     * override, which normally only runs at a real chunk-save. Serializing {@code container}
     * directly (the previous implementation) meant a contraption capture — happening mid-game,
     * never a real chunk-save — read whatever {@code container} last held (stale/empty), silently
     * losing any live-only state a subclass hadn't flushed yet. This mirrors exactly what a real
     * save does: build a fresh tag, let every override in the chain (including this base class's
     * own, which copies {@code container}'s remaining keys) populate it, then serialize that.
     */
    public byte[] serializeToBytes() throws IOException {
        net.momirealms.craftengine.libraries.nbt.CompoundTag tag = new net.momirealms.craftengine.libraries.nbt.CompoundTag();
        saveCustomData(tag);
        return net.momirealms.craftengine.libraries.nbt.NBT.toBytes(tag);
    }

    /**
     * Replaces this block entity's whole persisted container from a blob produced by
     * {@link #serializeToBytes()}. Routes through {@link #loadCustomData} (not just a raw field
     * swap) so subclass overrides that parse the container into live in-memory state — e.g.
     * {@link PersistentWorldlyBlockEntity#loadCustomData} populating its {@code inventory}
     * array from {@code TypedKeys.NMS_ITEMS} — actually run; otherwise a caller reading back
     * live state (rather than re-querying the container key-by-key) would see nothing change.
     */
    public void loadFromBytes(byte[] bytes) throws IOException {
        loadCustomData(net.momirealms.craftengine.libraries.nbt.NBT.fromBytes(bytes));
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

    public <C> void set(@NotNull NamespacedKey key, @NotNull NbtType type, @NotNull C value) {
        // Handle primitive types directly
        switch (type) {
            case STRING -> container.putString(key.getKey(), (String) value);
            case INTEGER -> container.putInt(key.getKey(), (Integer) value);
            case DOUBLE -> container.putDouble(key.getKey(), (Double) value);
            case BYTE -> container.putByte(key.getKey(), (Byte) value);
            case BYTE_ARRAY -> container.putByteArray(key.getKey(), (byte[]) value);
            case LONG -> container.putLong(key.getKey(), (Long) value);
            case FLOAT -> container.putFloat(key.getKey(), (Float) value);
            case SHORT -> container.putShort(key.getKey(), (Short) value);
            case INTEGER_ARRAY -> container.putIntArray(key.getKey(), (int[]) value);
            case LONG_ARRAY -> container.putLongArray(key.getKey(), (long[]) value);
            case BOOLEAN -> container.putByte(key.getKey(), (byte) ((Boolean) value ? 1 : 0));
        }
    }

    public <C> void setIfAbsent(@NotNull NamespacedKey key, @NotNull NbtType type, @NotNull C value) {
        if (!has(key, type)) {
            set(key, type, value);
        }
    }

    public <C> void set(@NotNull Key key, @NotNull NbtType type, @NotNull C value) {
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

    /** Removes all keys from this block entity's own persisted data. */
    public void clear() {
        new java.util.HashSet<>(container.keySet()).forEach(container::remove);
    }

    /**
     * Looks up the {@link PersistentBlockEntity} controller backing the block entity at {@code pos},
     * or null if unloaded / not a {@link PersistentBlockEntity}. Single lookup point replacing the
     * duplicated getBlockEntity() helpers that used to live on individual behaviors.
     */
    @Nullable
    public static PersistentBlockEntity getIfLoaded(net.minecraft.world.level.Level world,
            net.minecraft.core.BlockPos pos) {
        BlockEntity be = dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(world, pos);
        if (be != null && be.controller instanceof PersistentBlockEntity p) {
            return p;
        }
        return null;
    }

    /** Runs {@code consumer} against the loaded block entity at {@code pos}, if any. */
    public static void executeAt(net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos,
            java.util.function.Consumer<PersistentBlockEntity> consumer) {
        PersistentBlockEntity be = getIfLoaded(world, pos);
        if (be != null) {
            consumer.accept(be);
        }
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
            if (clazz.isInstance(customStateOpt.get().behavior())) {
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
