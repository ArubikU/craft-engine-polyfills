package dev.arubik.craftengine.util;

import dev.arubik.craftengine.CraftEnginePolyfills;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Simple Bukkit InventoryHolder that syncs its content with
 * BlockEntityBehaviorController's per-block CompoundTag using a byte[] payload.
 */
@SuppressWarnings({"deprecation"})
public class SyncedGuiHolder implements InventoryHolder {

    private final Level level;
    private final BlockPos pos;
    @SuppressWarnings("unused")
    private final int size;
    @SuppressWarnings("unused")
    private final String title;
    private final Inventory inventory;
    private final WorldlyContainer containerAdapter;

    private static volatile boolean LISTENER_REGISTERED = false;
    private static final java.util.Map<String, SyncedGuiHolder> REGISTRY = new java.util.concurrent.ConcurrentHashMap<>();

    private SyncedGuiHolder(Level level, BlockPos pos, int size, String title) {
        this.level = level;
        this.pos = pos.immutable();
        this.size = size;
        this.title = title;
        // Use string-based title for broad Bukkit compatibility
        this.inventory = Bukkit.createInventory(this, size, MiniMessage.miniMessage().deserialize(title));
    loadFromData();
    this.containerAdapter = new InventoryContainerAdapter(this.inventory, this::saveToData);
        ensureListenerRegistered(CraftEnginePolyfills.instance());
    }

    public static SyncedGuiHolder getOrCreate(Level level, BlockPos pos, int size, String title) {
        String key = makeKey(level, pos);
        return REGISTRY.computeIfAbsent(key, k -> new SyncedGuiHolder(level, pos, size, title));
    }

    public static Optional<SyncedGuiHolder> get(Level level, BlockPos pos) {
        String key = makeKey(level, pos);
        return Optional.ofNullable(REGISTRY.get(key));
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void saveToData() {
        BlockEntityBehaviorController.get(level, pos).ifPresent(data -> {
            CompoundTag tag = data.customData;
            // Persist title to detect external changes
            try {
                var putString = tag.getClass().getMethod("putString", String.class, String.class);
                putString.invoke(tag, "inv-title", title);
            } catch (Throwable ignored) {}
            byte[] bytes = serializeInventory(inventory);
            if (bytes != null) {
                putByteArray(tag, "items-bin", bytes);
            }
            if (level instanceof ServerLevel server) {
                BlockEntitySavedData.get(server).setDirty();
            }
        });
    }

    private void loadFromData() {
        BlockEntityBehaviorController.get(level, pos).ifPresent(data -> {
            CompoundTag tag = data.customData;
            // Title sync (if stored)
            String t = getString(tag, "inv-title");
            if (t != null && !t.isEmpty() && !t.equals(title)) {
                // We can't change live title; close viewers, they must re-open with the new title
                closeViewers();
            }
            byte[] bytes = getByteArray(tag, "items-bin");
            if (bytes != null) {
                ItemStack[] contents = deserializeInventory(bytes);
                if (contents != null) {
                    // Ensure array length fits the inventory size
                    if (contents.length != inventory.getSize()) {
                        contents = Arrays.copyOf(contents, inventory.getSize());
                    }
                    inventory.setContents(contents);
                }
            }
        });
    }

    private static void ensureListenerRegistered(Plugin plugin) {
        if (LISTENER_REGISTERED) return;
        synchronized (SyncedGuiHolder.class) {
            if (LISTENER_REGISTERED) return;
            Bukkit.getPluginManager().registerEvents(new CloseListener(), plugin);
            LISTENER_REGISTERED = true;
        }
    }

    private static class CloseListener implements Listener {
        @EventHandler
        public void onClose(InventoryCloseEvent event) {
            Inventory inv = event.getInventory();
            InventoryHolder holder = inv.getHolder();
            if (holder instanceof SyncedGuiHolder sgh) {
                // Only save if closed by a player to limit write frequency
                HumanEntity human = event.getPlayer();
                if (human instanceof Player) {
                    sgh.saveToData();
                }
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onClick(InventoryClickEvent event) {
            Inventory inv = event.getInventory();
            InventoryHolder holder = inv.getHolder();
            if (holder instanceof SyncedGuiHolder sgh) {
                // Delay-save one tick to capture final contents
                Bukkit.getScheduler().runTask(CraftEnginePolyfills.instance(), sgh::saveToData);
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onDrag(InventoryDragEvent event) {
            InventoryHolder holder = event.getInventory().getHolder();
            if (holder instanceof SyncedGuiHolder sgh) {
                Bukkit.getScheduler().runTask(CraftEnginePolyfills.instance(), sgh::saveToData);
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onMove(InventoryMoveItemEvent event) {
            InventoryHolder src = event.getSource().getHolder();
            InventoryHolder dst = event.getDestination().getHolder();
            if (src instanceof SyncedGuiHolder sgh) {
                Bukkit.getScheduler().runTask(CraftEnginePolyfills.instance(), sgh::saveToData);
            }
            if (dst instanceof SyncedGuiHolder sgh) {
                Bukkit.getScheduler().runTask(CraftEnginePolyfills.instance(), sgh::saveToData);
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onBlockBreak(BlockBreakEvent event) {
            String key = makeKey(event.getBlock().getWorld().getUID(), event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ());
            SyncedGuiHolder holder = REGISTRY.remove(key);
            if (holder != null) {
                holder.closeViewers();
                holder.clearAndSave();
            }
        }
    }

    // --- Serialization helpers ---

    private static byte[] serializeInventory(Inventory inv) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BukkitObjectOutputStream oos = new BukkitObjectOutputStream(baos)) {
            ItemStack[] contents = inv.getContents();
            oos.writeInt(contents.length);
            for (ItemStack item : contents) {
                oos.writeObject(item);
            }
            oos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    private static ItemStack[] deserializeInventory(byte[] bytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             BukkitObjectInputStream ois = new BukkitObjectInputStream(bais)) {
            int len = ois.readInt();
            ItemStack[] items = new ItemStack[len];
            for (int i = 0; i < len; i++) {
                Object obj = ois.readObject();
                items[i] = (obj instanceof ItemStack) ? (ItemStack) obj : null;
            }
            return items;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    // --- NBT helpers with broad compatibility ---

    private static void putByteArray(CompoundTag tag, String key, byte[] data) {
        try {
            var m = tag.getClass().getMethod("putByteArray", String.class, byte[].class);
            m.invoke(tag, key, data);
            return;
        } catch (Throwable ignored) {}
        // Fallback: store length and ints (inefficient but compatible)
        try {
            var putInt = tag.getClass().getMethod("putInt", String.class, int.class);
            putInt.invoke(tag, key + "_len", data.length);
            for (int i = 0; i < data.length; i++) {
                putInt.invoke(tag, key + "_i_" + i, (int) data[i]);
            }
        } catch (Throwable ignored) {}
    }

    private static byte[] getByteArray(CompoundTag tag, String key) {
        try {
            var m = tag.getClass().getMethod("getByteArray", String.class);
            Object val = m.invoke(tag, key);
            if (val instanceof byte[] arr) return arr;
            if (val instanceof java.util.Optional<?> opt) {
                Object o = opt.orElse(null);
                if (o instanceof byte[] arr) return arr;
            }
        } catch (Throwable ignored) {}
        // Fallback for the integer encoding
        try {
            var contains = tag.getClass().getMethod("contains", String.class);
            var getInt = tag.getClass().getMethod("getInt", String.class);
            Object hasLen = contains.invoke(tag, key + "_len");
            boolean present = false;
            if (hasLen instanceof Boolean b) present = b;
            else if (hasLen instanceof java.util.Optional<?> opt) present = opt.isPresent() && Boolean.TRUE.equals(opt.get());
            if (!present) return null;
            Object lenObj = getInt.invoke(tag, key + "_len");
            int len = (lenObj instanceof Integer i) ? i : 0;
            byte[] arr = new byte[len];
            for (int i = 0; i < len; i++) {
                Object vi = getInt.invoke(tag, key + "_i_" + i);
                arr[i] = (byte) (((vi instanceof Integer ii) ? ii : 0) & 0xFF);
            }
            return arr;
        } catch (Throwable ignored) {}
        return null;
    }

    private static String getString(CompoundTag tag, String key) {
        try {
            var m = tag.getClass().getMethod("getString", String.class);
            Object val = m.invoke(tag, key);
            if (val instanceof String s) return s;
            if (val instanceof java.util.Optional<?> opt) {
                Object o = opt.orElse(null);
                return (o instanceof String) ? (String) o : null;
            }
        } catch (Throwable ignored) {}
        return null;
    }

    public void closeViewers() {
        for (HumanEntity viewer : inventory.getViewers().toArray(new HumanEntity[0])) {
            if (viewer instanceof Player p) p.closeInventory();
        }
    }

    private void clearAndSave() {
        inventory.clear();
        saveToData();
    }

    public void dropInventoryContents(Vec3 dropPosition) {
        // Drop all items in the inventory at the specified position
        for (ItemStack item : inventory.getContents()) {
            if (!item.isEmpty()) {
                new ItemEntity(level, dropPosition.x, dropPosition.y, dropPosition.z, ((CraftItemStack) item).handle);
            }
        }
    }

    public WorldlyContainer getContainer() {
        return containerAdapter;
    }

    // --- Registry helpers ---
    private static String makeKey(Level level, BlockPos pos) {
        java.util.UUID wuid = getWorldUid(level);
        return makeKey(wuid, pos.getX(), pos.getY(), pos.getZ());
    }

    private static String makeKey(java.util.UUID worldUid, int x, int y, int z) {
        return worldUid + ":" + x + ":" + y + ":" + z;
    }

    private static java.util.UUID getWorldUid(Level level) {
        try {
            Object craftWorld = net.momirealms.craftengine.bukkit.nms.FastNMS.INSTANCE.method$Level$getCraftWorld(level);
            java.lang.reflect.Method m = craftWorld.getClass().getMethod("getUID");
            return (java.util.UUID) m.invoke(craftWorld);
        } catch (Throwable t) {
            return new java.util.UUID(0L, 0L);
        }
    }

    // External API
    public static void closeAll(Level level, BlockPos pos) {
        SyncedGuiHolder holder = REGISTRY.remove(makeKey(level, pos));
        if (holder != null) {
            holder.closeViewers();
        }
    }


}
