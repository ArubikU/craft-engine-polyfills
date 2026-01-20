package dev.arubik.craftengine.util;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;

import dev.arubik.craftengine.CraftEnginePolyfills;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents a {@link PersistentDataContainer} for a specific {@link Block}.
 * Also provides some static utility methods
 * that can be used on every PersistentDataContainer.
 * <p>
 * By default, and for backward compatibility reasons, data stored inside blocks
 * is independent of the underlying block.
 * That means: if you store some data inside a dirt block, and that block is now
 * pushed by a piston, then the information
 * will still reside in the old block's location. <b>You can of course also make
 * CustomBockData automatically take care of those situations</b>,
 * so that CustomBlockData will always be updated on certain Bukkit Events like
 * BlockBreakEvent, EntityExplodeEvent, etc.
 * For more information about this please see {@link #registerListener(Plugin)}.
 */
public class CustomBlockData implements PersistentDataContainer {

    /**
     * The default package name that must be changed
     */
    private static final char[] DEFAULT_PACKAGE = new char[] { 'c', 'o', 'm', '.', 'j', 'e', 'f', 'f', '_', 'm', 'e',
            'd', 'i', 'a', '.', 'c', 'u', 's', 't', 'o', 'm', 'b', 'l', 'o', 'c', 'k', 'd', 'a', 't', 'a' };

    /**
     * Set of "dirty block positions", that is blocks that have been modified and
     * need to be saved to the chunk
     */
    private static final Set<Map.Entry<UUID, BlockVector>> DIRTY_BLOCKS = new HashSet<>();

    /**
     * Builtin list of native PersistentDataTypes
     */
    public static final PersistentDataType<?, ?>[] PRIMITIVE_DATA_TYPES = new PersistentDataType<?, ?>[] {
            PersistentDataType.BYTE,
            PersistentDataType.SHORT,
            PersistentDataType.INTEGER,
            PersistentDataType.LONG,
            PersistentDataType.FLOAT,
            PersistentDataType.DOUBLE,
            PersistentDataType.STRING,
            PersistentDataType.BYTE_ARRAY,
            PersistentDataType.INTEGER_ARRAY,
            PersistentDataType.LONG_ARRAY,
            PersistentDataType.TAG_CONTAINER_ARRAY,
            PersistentDataType.TAG_CONTAINER };

    /**
     * NamespacedKey for the CustomBlockData "protected" key
     */
    private static final NamespacedKey PERSISTENCE_KEY = Objects.requireNonNull(
            NamespacedKey.fromString("customblockdata:protected"), "Could not create persistence NamespacedKey");

    /**
     * Regex used to identify valid CustomBlockData keys
     */
    private static final Pattern KEY_REGEX = Pattern.compile("^x(\\d+)y(-?\\d+)z(\\d+)$");

    /**
     * The minimum X and Z coordinate of any block inside a chunk.
     */
    private static final int CHUNK_MIN_XZ = 0;

    /**
     * The maximum X and Z coordinate of any block inside a chunk.
     */
    private static final int CHUNK_MAX_XZ = (2 << 3) - 1;

    /**
     * Whether WorldInfo#getMinHeight() method exists. In some very specific
     * versions, it's directly declared in World.
     */
    private static final boolean HAS_MIN_HEIGHT_METHOD;

    private static boolean onFolia;

    static {
        checkRelocation();
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            onFolia = true;
        } catch (ClassNotFoundException e) {
            onFolia = false;
        }
    }

    static {
        boolean tmpHasMinHeightMethod = false;
        try {
            // Usually declared in WorldInfo, which World extends - except for some very
            // specific versions
            World.class.getMethod("getMinHeight");
            tmpHasMinHeightMethod = true;
        } catch (final ReflectiveOperationException ignored) {
        }
        HAS_MIN_HEIGHT_METHOD = tmpHasMinHeightMethod;
    }

    /**
     * The Chunk PDC belonging to this CustomBlockData object
     */
    private final PersistentDataContainer pdc;

    /**
     * The Chunk this CustomBlockData object belongs to
     */
    private final Chunk chunk;

    /**
     * The NamespacedKey used to identify this CustomBlockData object inside the
     * Chunk's PDC
     */
    private final NamespacedKey key;

    /**
     * The Map.Entry containing the UUID of the block and its BlockVector for usage
     * with {@link #DIRTY_BLOCKS}
     */
    private final Map.Entry<UUID, BlockVector> blockEntry;

    /**
     * The Plugin this CustomBlockData object belongs to
     */
    private final Plugin plugin;

    /**
     * Gets the PersistentDataContainer associated with the given block and plugin
     *
     * @param block  Block
     * @param plugin Plugin
     */

    private static HashMap<String, CustomBlockData> CACHE = new HashMap<>();
    private static HashMap<Long, Set<String>> CHUNK_CACHE = new HashMap<>();

    public CustomBlockData(final @NotNull Block block, final @NotNull Plugin plugin) {
        this.chunk = block.getChunk();
        this.key = getKey(plugin, block);
        this.pdc = getPersistentDataContainer();
        this.blockEntry = getBlockEntry(block);
        this.plugin = plugin;

        CACHE.put(getKey(block), this);
        CHUNK_CACHE.compute(this.chunk.getChunkKey(), (k, v) -> {
            if (v == null)
                v = new HashSet<>();
            v.add(getKey(block));
            return v;
        });
    }

    /**
     * Gets the PersistentDataContainer associated with the given block and plugin
     *
     * @param block     Block
     * @param namespace Namespace
     * @deprecated Use {@link #CustomBlockData(Block, Plugin)} instead.
     */
    @Deprecated
    public CustomBlockData(final @NotNull Block block, final @NotNull String namespace) {
        this.chunk = block.getChunk();
        this.key = new NamespacedKey(namespace, getKey(block));
        this.pdc = getPersistentDataContainer();
        this.plugin = JavaPlugin.getProvidingPlugin(CustomBlockData.class);
        this.blockEntry = getBlockEntry(block);
        CACHE.put(getKey(block), this);
        CHUNK_CACHE.compute(this.chunk.getChunkKey(), (k, v) -> {
            if (v == null)
                v = new HashSet<>();
            v.add(getKey(block));
            return v;
        });
    }

    public static void clearChunk(Long chunkKey) {
        Set<String> keys = CHUNK_CACHE.remove(chunkKey);
        if (keys != null) {
            keys.forEach(CACHE::remove);
        }
    }

    public PersistentDataContainer getPdc() {
        return pdc;
    }

    /**
     * Prints a nag message when the CustomBlockData package is not relocated
     */
    private static void checkRelocation() {
        if (CustomBlockData.class.getPackage().getName().equals(new String(DEFAULT_PACKAGE))) {
            try {
                JavaPlugin plugin = JavaPlugin.getProvidingPlugin(CustomBlockData.class);
                plugin.getLogger().warning("Nag author(s) " + String.join(", ", plugin.getDescription().getAuthors())
                        + " of plugin " + plugin.getName() + " for not relocating the CustomBlockData package.");
            } catch (IllegalArgumentException exception) {
                // Could not get plugin
            }
        }
    }

    /**
     * Gets the block entry for this block used for {@link #DIRTY_BLOCKS}
     * 
     * @param block Block
     * @return Block entry
     */
    private static Map.Entry<UUID, BlockVector> getBlockEntry(final @NotNull Block block) {
        final UUID uuid = block.getWorld().getUID();
        final BlockVector blockVector = new BlockVector(block.getX(), block.getY(), block.getZ());
        return new AbstractMap.SimpleEntry<>(uuid, blockVector);
    }

    /**
     * Checks whether this block is flagged as "dirty"
     * 
     * @param block Block
     * @return Whether this block is flagged as "dirty"
     */
    static boolean isDirty(Block block) {
        return DIRTY_BLOCKS.contains(getBlockEntry(block));
    }

    /**
     * Sets this block as "dirty" and removes it from the list after the next tick.
     * <p>
     * If the plugin is disabled, this method will do nothing, to prevent the
     * IllegalPluginAccessException.
     * 
     * @param plugin     Plugin
     * @param blockEntry Block entry
     */
    static void setDirty(Plugin plugin, Map.Entry<UUID, BlockVector> blockEntry) {
        if (!plugin.isEnabled()) // checks if the plugin is disabled to prevent the IllegalPluginAccessException
            return;

        DIRTY_BLOCKS.add(blockEntry);
        if (onFolia) {
            Bukkit.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> {
                DIRTY_BLOCKS.remove(blockEntry);
            }, 1L);
        } else {
            Bukkit.getScheduler().runTask(plugin, () -> DIRTY_BLOCKS.remove(blockEntry));
        }
    }

    /**
     * Gets the NamespacedKey for this block
     * 
     * @param plugin Plugin
     * @param block  Block
     * @return NamespacedKey
     */
    private static NamespacedKey getKey(Plugin plugin, Block block) {
        return new NamespacedKey(plugin, getKey(block));
    }

    /**
     * Gets a String-based {@link NamespacedKey} that consists of the block's
     * relative coordinates within its chunk
     *
     * @param block block
     * @return NamespacedKey consisting of the block's relative coordinates within
     *         its chunk
     */
    @NotNull
    static String getKey(@NotNull final Block block) {
        final int x = block.getX() & 0x000F;
        final int y = block.getY();
        final int z = block.getZ() & 0x000F;
        return "x" + x + "y" + y + "z" + z;
    }

    /**
     * Gets the block represented by the given {@link NamespacedKey} in the given
     * {@link Chunk}
     */
    @Nullable
    static Block getBlockFromKey(final NamespacedKey key, final Chunk chunk) {
        final Matcher matcher = KEY_REGEX.matcher(key.getKey());
        if (!matcher.matches())
            return null;
        final int x = Integer.parseInt(matcher.group(1));
        final int y = Integer.parseInt(matcher.group(2));
        final int z = Integer.parseInt(matcher.group(3));
        if ((x < CHUNK_MIN_XZ || x > CHUNK_MAX_XZ) || (z < CHUNK_MIN_XZ || z > CHUNK_MAX_XZ)
                || (y < getWorldMinHeight(chunk.getWorld()) || y > chunk.getWorld().getMaxHeight() - 1))
            return null;
        return chunk.getBlock(x, y, z);
    }

    /**
     * Returns the given {@link World}'s minimum build height, or 0 if not supported
     * in this Bukkit version
     */
    static int getWorldMinHeight(final World world) {
        if (HAS_MIN_HEIGHT_METHOD) {
            return world.getMinHeight();
        } else {
            return 0;
        }
    }

    /**
     * Get if the given Block has any CustomBockData associated with it
     */
    public static boolean hasCustomBlockData(Block block, Plugin plugin) {
        return block.getChunk().getPersistentDataContainer().has(getKey(plugin, block),
                PersistentDataType.TAG_CONTAINER);
    }

    /**
     * Get if the given Block's CustomBlockData is protected. Protected
     * CustomBlockData will not be changed by any Bukkit Events
     *
     * @return true if the Block's CustomBlockData is protected, false if it doesn't
     *         have any CustomBlockData or it's not protected
     * @see #registerListener(Plugin)
     */
    public static boolean isProtected(Block block, Plugin plugin) {
        return CustomBlockData.from(block, plugin).isProtected();
    }

    /**
     * Starts to listen and manage block-related events such as
     * {@link BlockBreakEvent}. By default, CustomBlockData
     * is "stateless". That means: when you add data to a block, and now a player
     * breaks the block, the data will
     * still reside at the original block location. This is to ensure that you
     * always have full control about what data
     * is saved at which location.
     * <p>
     * If you do not want to handle this yourself, you can instead let
     * CustomBlockData handle those events by calling this
     * method once. It will then listen to the common events itself, and
     * automatically remove/update CustomBlockData.
     * <p>
     * Block changes made using the Bukkit API (e.g.
     * {@link Block#setType(Material)}) or using a plugin like WorldEdit
     * will <b>not</b> be registered by this (but pull requests are welcome, of
     * course)
     * <p>
     * For example, when you call this method in onEnable, CustomBlockData will now
     * get automatically removed from a block
     * when a player breaks this block. It will additionally call custom events like
     * {@link com.jeff_media.customblockdata.events.CustomBlockDataRemoveEvent}.
     * Those events implement {@link org.bukkit.event.Cancellable}. If one of the
     * CustomBlockData events is cancelled,
     * it will not alter any CustomBlockData.
     *
     * @param plugin Your plugin's instance
     */
    public static void registerListener(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new BlockDataListener(plugin), plugin);
    }

    /**
     * Returns a Set of all blocks in this chunk containing Custom Block Data
     * created by the given plugin
     *
     * @param plugin Plugin
     * @param chunk  Chunk
     * @return A Set containing all blocks in this chunk containing Custom Block
     *         Data created by the given plugin
     */
    @NotNull
    public static Set<Block> getBlocksWithCustomData(final Plugin plugin, final Chunk chunk) {
        final NamespacedKey dummy = new NamespacedKey(plugin, "dummy");
        return getBlocksWithCustomData(chunk, dummy);
    }

    /**
     * Returns a {@link Set} of all blocks in this {@link Chunk} containing Custom
     * Block Data matching the given {@link NamespacedKey}'s namespace
     *
     * @param namespace Namespace
     * @param chunk     Chunk
     * @return A {@link Set} containing all blocks in this chunk containing Custom
     *         Block Data created by the given plugin
     */
    @NotNull
    private static Set<Block> getBlocksWithCustomData(final @NotNull Chunk chunk,
            final @NotNull NamespacedKey namespace) {
        final PersistentDataContainer chunkPDC = chunk.getPersistentDataContainer();
        return chunkPDC.getKeys().stream().filter(key -> key.getNamespace().equals(namespace.getNamespace()))
                .map(key -> getBlockFromKey(key, chunk))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a {@link Set} of all blocks in this {@link Chunk} containing Custom
     * Block Data created by the given plugin
     *
     * @param namespace Namespace
     * @param chunk     Chunk
     * @return A {@link Set} containing all blocks in this chunk containing Custom
     *         Block Data created by the given plugin
     */
    @NotNull
    public static Set<Block> getBlocksWithCustomData(final String namespace, final Chunk chunk) {
        @SuppressWarnings("deprecation")
        final NamespacedKey dummy = new NamespacedKey(namespace, "dummy");
        return getBlocksWithCustomData(chunk, dummy);
    }

    /**
     * Gets the proper primitive {@link PersistentDataType} for the given
     * {@link NamespacedKey} in the given {@link PersistentDataContainer}
     *
     * @return The primitive PersistentDataType for the given key, or null if the
     *         key doesn't exist
     */
    public static PersistentDataType<?, ?> getDataType(PersistentDataContainer pdc, NamespacedKey key) {
        for (PersistentDataType<?, ?> dataType : PRIMITIVE_DATA_TYPES) {
            if (pdc.has(key, dataType))
                return dataType;
        }
        return null;
    }

    /**
     * Gets the Block associated with this CustomBlockData, or null if the world is
     * no longer loaded.
     */
    public @Nullable Block getBlock() {
        World world = Bukkit.getWorld(blockEntry.getKey());
        if (world == null)
            return null;
        BlockVector vector = blockEntry.getValue();
        return world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    /**
     * Gets the PersistentDataContainer associated with this block.
     *
     * @return PersistentDataContainer of this block
     */
    @NotNull
    private PersistentDataContainer getPersistentDataContainer() {
        final PersistentDataContainer chunkPDC = chunk.getPersistentDataContainer();
        final PersistentDataContainer blockPDC = chunkPDC.get(key, PersistentDataType.TAG_CONTAINER);
        if (blockPDC != null)
            return blockPDC;
        return chunkPDC.getAdapterContext().newPersistentDataContainer();
    }

    /**
     * Gets whether this CustomBlockData is protected. Protected CustomBlockData
     * will not be changed by any Bukkit Events
     *
     * @see #registerListener(Plugin)
     */
    public boolean isProtected() {
        return has(PERSISTENCE_KEY, DataType.BOOLEAN);
    }

    /**
     * Sets whether this CustomBlockData is protected. Protected CustomBlockData
     * will not be changed by any Bukkit Events
     *
     * @see #registerListener(Plugin)
     */
    public void setProtected(boolean isProtected) {
        if (isProtected) {
            set(PERSISTENCE_KEY, DataType.BOOLEAN, true);
        } else {
            remove(PERSISTENCE_KEY);
        }
    }

    /**
     * Removes all CustomBlockData and disables the protection status
     * ({@link #setProtected(boolean)}
     */
    public void clear() {
        pdc.getKeys().forEach(pdc::remove);
        save();
    }

    /**
     * Saves the block's {@link PersistentDataContainer} inside the chunk's
     * PersistentDataContainer
     */
    private void save() {
        setDirty(plugin, blockEntry);
        if (pdc.isEmpty()) {
            chunk.getPersistentDataContainer().remove(key);
        } else {
            chunk.getPersistentDataContainer().set(key, PersistentDataType.TAG_CONTAINER, pdc);
        }
    }

    /**
     * Copies all data to another block. Data already present in the destination
     * block will keep intact, unless it gets
     * overwritten by identically named keys. Data in the source block won't be
     * changed.
     */
    @SuppressWarnings({ "unchecked", "rawtypes", "ConstantConditions" })
    public void copyTo(Block block, Plugin plugin) {
        CustomBlockData newCbd = CustomBlockData.from(block, plugin);
        getKeys().forEach(key -> {
            PersistentDataType dataType = getDataType(this, key);
            if (dataType == null)
                return;
            newCbd.set(key, dataType, get(key, dataType));
        });
    }

    public void loadFrom(PersistentDataContainer other) {
        for (NamespacedKey key : other.getKeys()) {
            PersistentDataType<?, ?> dataType = getDataType(other, key);
            if (dataType != null) {
                Object value = other.get(key, dataType);
                set(key, (PersistentDataType<Object, Object>) dataType, value);
            }
        }
    }

    @Override
    public <T, Z> void set(final @NotNull NamespacedKey namespacedKey,
            final @NotNull PersistentDataType<T, Z> persistentDataType, final @NotNull Z z) {
        pdc.set(namespacedKey, persistentDataType, z);
        save();
    }

    @Override
    public <T, Z> boolean has(final @NotNull NamespacedKey namespacedKey,
            final @NotNull PersistentDataType<T, Z> persistentDataType) {
        return pdc.has(namespacedKey, persistentDataType);
    }

    @Override
    public boolean has(final @NotNull NamespacedKey namespacedKey) {
        for (PersistentDataType<?, ?> type : PRIMITIVE_DATA_TYPES) {
            if (pdc.has(namespacedKey, type))
                return true;
        }
        return false;
    }

    @Nullable
    @Override
    public <T, Z> Z get(final @NotNull NamespacedKey namespacedKey,
            final @NotNull PersistentDataType<T, Z> persistentDataType) {
        return pdc.get(namespacedKey, persistentDataType);
    }

    @NotNull
    @Override
    public <T, Z> Z getOrDefault(final @NotNull NamespacedKey namespacedKey,
            final @NotNull PersistentDataType<T, Z> persistentDataType, final @NotNull Z z) {
        return pdc.getOrDefault(namespacedKey, persistentDataType, z);
    }

    @NotNull
    @Override
    public Set<NamespacedKey> getKeys() {
        return pdc.getKeys();
    }

    @Override
    public void remove(final @NotNull NamespacedKey namespacedKey) {
        pdc.remove(namespacedKey);
        save();
    }

    @Override
    public boolean isEmpty() {
        return pdc.isEmpty();
    }

    @NotNull
    @Override
    public PersistentDataAdapterContext getAdapterContext() {
        return pdc.getAdapterContext();
    }

    /**
     * @see PersistentDataContainer#serializeToBytes()
     * @deprecated Paper-only
     */
    @NotNull
    @Override
    @PaperOnly
    @Deprecated
    public byte[] serializeToBytes() throws IOException {
        return pdc.serializeToBytes();
    }

    /**
     * @see PersistentDataContainer#readFromBytes(byte[], boolean) ()
     * @deprecated Paper-only
     */
    @Override
    @PaperOnly
    @Deprecated
    public void readFromBytes(byte[] bytes, boolean clear) throws IOException {
        pdc.readFromBytes(bytes, clear);
    }

    /**
     * @see PersistentDataContainer#readFromBytes(byte[]) ()
     * @deprecated Paper-only
     */
    @Override
    @PaperOnly
    @Deprecated
    public void readFromBytes(byte[] bytes) throws IOException {
        pdc.readFromBytes(bytes);
    }

    /**
     * Gets the proper primitive {@link PersistentDataType} for the given
     * {@link NamespacedKey}
     *
     * @return The primitive PersistentDataType for the given key, or null if the
     *         key doesn't exist
     */
    public PersistentDataType<?, ?> getDataType(NamespacedKey key) {
        return getDataType(this, key);
    }

    /**
     * Indicates a method that only works on Paper and forks, but not on Spigot or
     * CraftBukkit
     */
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    private @interface PaperOnly {
    }

    private static final class DataType {
        private static final PersistentDataType<Byte, Boolean> BOOLEAN = new PersistentDataType<Byte, Boolean>() {
            @NotNull
            @Override
            public Class<Byte> getPrimitiveType() {
                return Byte.class;
            }

            @NotNull
            @Override
            public Class<Boolean> getComplexType() {
                return Boolean.class;
            }

            @NotNull
            @Override
            public Byte toPrimitive(@NotNull Boolean complex, @NotNull PersistentDataAdapterContext context) {
                return complex ? (byte) 1 : (byte) 0;
            }

            @NotNull
            @Override
            public Boolean fromPrimitive(@NotNull Byte primitive, @NotNull PersistentDataAdapterContext context) {
                return primitive == (byte) 1;
            }
        };
    }

    @Override
    public void copyTo(PersistentDataContainer other, boolean replace) {
        for (NamespacedKey key : getKeys()) {
            if (replace || !other.has(key)) {
                PersistentDataType<?, ?> dataType = getDataType(key);
                if (dataType != null) {
                    Object value = get(key, dataType);
                    // noinspection unchecked
                    other.set(key, (PersistentDataType<Object, Object>) dataType, value);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "CustomBlockData{" +
                "block=" + this.getBlock() +
                ", plugin=" + plugin +
                ", data=" + this.pdc +
                '}';
    }

    public static CustomBlockData from(Level level, BlockPos pos) {

        String key = getKey(level.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()));
        if (CACHE.containsKey(key)) {
            return CACHE.get(key);
        }
        return new CustomBlockData(level.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()),
                CraftEnginePolyfills.instance());
    }

    public static CustomBlockData from(Block block) {
        if (CACHE.containsKey(getKey(block))) {
            return CACHE.get(getKey(block));
        }
        return new CustomBlockData(block, CraftEnginePolyfills.instance());
    }

    public static CustomBlockData from(Block block, Plugin plugin) {
        if (CACHE.containsKey(getKey(block))) {
            return CACHE.get(getKey(block));
        }
        return new CustomBlockData(block, plugin);
    }

    public <T> void set(TypedKey<T> key, T value) {
        if (value == null) {
            remove(key);
            return;
        }
        if (key.isCustom()) {
            @SuppressWarnings("unchecked")
            CustomDataType<T, Object> customType = (CustomDataType<T, Object>) key.getCustomType();

            if (customType.isContainerSerializer()) {
                PersistentDataContainer container = chunk.getPersistentDataContainer().getAdapterContext()
                        .newPersistentDataContainer();
                pdc.set(key.getKey(), customType.getBaseType(),
                        customType.getContainerSerializer().apply(value, container));
            } else {
                Object primitive = customType.getSerializer().apply(value);
                pdc.set(key.getKey(), customType.getBaseType(), primitive);
            }
        } else {
            pdc.set(key.getKey(), key.getType(), value);
        }

        save();
    }

    public <T> T get(TypedKey<T> key) {

        if (key.isCustom()) {
            @SuppressWarnings("unchecked")
            CustomDataType<T, Object> customType = (CustomDataType<T, Object>) key.getCustomType();
            Object primitive = pdc.get(key.getKey(), customType.getBaseType());
            return primitive != null ? customType.getDeserializer().apply(primitive) : null;
        } else {
            return pdc.get(key.getKey(), key.getType());
        }
    }

    public <T> Optional<T> getOptional(TypedKey<T> key) {
        return Optional.ofNullable(get(key));
    }

    public <T> boolean has(TypedKey<T> key) {

        if (key.isCustom()) {
            return pdc.has(key.getKey(), key.getCustomType().getBaseType());
        } else {
            return pdc.has(key.getKey(), key.getType());
        }
    }

    public <T> void remove(TypedKey<T> key) {
        if (key == null)
            return;
        if (pdc.has(key.getKey())) {
            pdc.remove(key.getKey());
        }
        save();
    }

    public <T> T getOrDefault(TypedKey<T> key, T defaultValue) {
        T value = get(key);
        return value != null ? value : defaultValue;
    }

    public void updateNeighbors() {

    }

    public void edit(java.util.function.Consumer<CustomBlockData> consumer) {
        consumer.accept(this);
    }

    @Override
    public int getSize() {
        return pdc.getSize();
    }
}