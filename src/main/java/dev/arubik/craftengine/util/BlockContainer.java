package dev.arubik.craftengine.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import dev.arubik.craftengine.CraftEnginePolyfills;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BlockContainer implements WorldlyContainer, InventoryHolder, DataHolder {
    private ItemStack[] inventory;
    private Set<UUID> viewers;

    private final int size;
    private final String title;
    private final BlockPos pos;
    private final Level level;
    private int maxStackSize = 64;
    private boolean stillValid = true;

    public BlockContainer(ItemStack[] inventory, Set<UUID> viewers, int size, String title, BlockPos pos,
            Level level) {
        this.inventory = inventory;
        this.viewers = viewers;
        this.size = size;
        this.title = title;
        this.pos = pos;
        this.level = level;

        DataHolders.INSTANCE.addHolder(this);
    }

    private static final Map<String, BlockContainer> REGISTRY = new ConcurrentHashMap<>();

    private static String makeKey(Level level, BlockPos pos) {
        return level.getWorld().getName() + "@" + "x" + pos.getX() + "y" + pos.getY() + "z" + pos.getZ();
    }

    public static BlockContainer getOrCreate(Level level, BlockPos pos, int size, String title) {
        return REGISTRY.computeIfAbsent(makeKey(level, pos),
                k -> new BlockContainer(new ItemStack[size], ConcurrentHashMap.newKeySet(), size, title, pos, level));
    }

    public static Optional<BlockContainer> get(Block block) {
        Level level = ((org.bukkit.craftbukkit.CraftWorld) block.getWorld()).getHandle();
        BlockPos pos = new BlockPos(block.getX(), block.getY(), block.getZ());
        return get(level, pos);
    }

    public static Optional<BlockContainer> get(Level level, BlockPos pos) {
        return Optional.ofNullable(REGISTRY.get(makeKey(level, pos)));
    }

    public static void remove(Level level, BlockPos pos) {
        REGISTRY.remove(makeKey(level, pos));
    }

    @Override
    public void onUnload() {
        saveToData();
        remove(level, pos);
        stillValid = false;
    }

    @Override
    public void destroy() {
        remove(level, pos);
        stillValid = false;
    }

    public void destroy(boolean dropContents) {
        closeViewers();
        if (dropContents) {
            Bukkit.getConsoleSender().sendMessage("Contents: " + Arrays.deepToString(inventory));
            for (ItemStack item : inventory) {
                if (item != null && !item.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item);
                }
            }
        }
        inventory = new ItemStack[size];
        bukkitInventory = null;
        CustomBlockData.from(level, pos).clear();
        DataHolders.INSTANCE.destroyHolder(this);
    }

    private Inventory bukkitInventory;

    public void open(org.bukkit.entity.Player player) {
        if (bukkitInventory == null) {
            bukkitInventory = Bukkit.createInventory(this, size, MiniMessage.miniMessage().deserialize(title));
            syncToBukkit();
        }
        if (!viewers.contains(player.getUniqueId())) {
            viewers.add(player.getUniqueId());
        }
        player.openInventory(bukkitInventory);
    }

    @Override
    public int getContainerSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (stack != null && !stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        ItemStack toreturn = slot >= 0 && slot < inventory.length ? inventory[slot] : ItemStack.EMPTY;
        if (toreturn == null)
            return ItemStack.EMPTY;
        return toreturn;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack toreturn = slot >= 0 && slot < inventory.length && !inventory[slot].isEmpty() && amount > 0
                ? inventory[slot].split(amount)
                : ItemStack.EMPTY;
        syncToBukkit();
        setChanged();
        return toreturn;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack toreturn = slot >= 0 && slot < inventory.length ? inventory[slot] = ItemStack.EMPTY : ItemStack.EMPTY;
        syncToBukkit();
        setChanged();
        return toreturn;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory[slot] = stack;
        syncToBukkit();
        setChanged();
        saveToData();
    }

    @Override
    public int getMaxStackSize() {
        final int[] m = { maxStackSize };
        CustomBlockData.from(level, pos).getOptional(TypedKeys.MAX_STACK_SIZE).ifPresent(val -> m[0] = val);
        return m[0];
    }

    @Override
    public void setChanged() {
        // Sync with viewers
        for (UUID viewerId : viewers) {
            org.bukkit.entity.Player player = Bukkit.getPlayer(viewerId);
            if (player != null) {
                player.openInventory(bukkitInventory);
            }
        }
    }

    public void setChanged(UUID... excludingViewer) {
        // Sync with viewers
        for (UUID viewerId : viewers) {
            if (Arrays.asList(excludingViewer).contains(viewerId))
                continue;
            org.bukkit.entity.Player player = Bukkit.getPlayer(viewerId);
            if (player != null) {
                player.openInventory(bukkitInventory);
            }
        }
    }

    public void closeViewers() {
        if (bukkitInventory != null) {
            for (UUID viewerId : viewers) {
                org.bukkit.entity.Player player = Bukkit.getPlayer(viewerId);
                if (player != null) {
                    player.closeInventory();
                }

            }
        }
        viewers.clear();
        syncFromBukkit();
        setChanged();
        saveToData();
    }

    public void closeViewers(UUID... excludingViewer) {
        if (bukkitInventory != null) {
            for (UUID viewerId : viewers) {
                if (Arrays.asList(excludingViewer).contains(viewerId))
                    continue;
                org.bukkit.entity.Player player = Bukkit.getPlayer(viewerId);
                if (player != null) {
                    player.closeInventory();
                    viewers.remove(viewerId);
                }
            }
        }
    }

    public void closeViewer(UUID viewer) {
        if (bukkitInventory != null) {
            if (viewers.contains(viewer)) {
                org.bukkit.entity.Player player = Bukkit.getPlayer(viewer);
                if (player != null) {
                    player.closeInventory();
                    viewers.remove(viewer);
                }
            }
        }
    }

    public void removeViewer(UUID viewer) {
        viewers.remove(viewer);
    }

    @Override
    public void saveToData() {
        CustomBlockData data = CustomBlockData.from(level, pos);
        data.set(TypedKeys.MAX_STACK_SIZE, getMaxStackSize());
        data.set(TypedKeys.CONTENTS, ArrayItemStackWithSlot.from(inventory));
        updateNeighbors();
    }

    @Override
    public void loadFromData() {
        CustomBlockData data = CustomBlockData.from(level, pos);
        data.getOptional(TypedKeys.MAX_STACK_SIZE).ifPresent(mss -> maxStackSize = mss);
        Arrays.fill(inventory, ItemStack.EMPTY);
        data.getOptional(TypedKeys.CONTENTS).ifPresent(contents -> {
            for (ItemStackWithSlot item : contents) {
                inventory[item.slot()] = item.stack();
            }
        });
        if (bukkitInventory == null) {
            bukkitInventory = Bukkit.createInventory(this, size, MiniMessage.miniMessage().deserialize(title));
            syncToBukkit();
        }
        updateNeighbors();
    }

    @Override
    public boolean stillValid(Player player) {
        if (!stillValid)
            return false;
        if (bukkitInventory == null)
            return false;
        CustomBlockData data = CustomBlockData.from(level, pos);
        if (data.has(TypedKeys.CONTENTS))
            return false;
        return !player.isRemoved() && player.distanceToSqr(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public List<ItemStack> getContents() {
        List<ItemStack> contents = new ArrayList<>();
        for (ItemStack item : inventory) {
            if (item != null)
                contents.add(item);
            else
                contents.add(ItemStack.EMPTY);
        }
        return contents;
    }

    @Override
    public void onOpen(CraftHumanEntity player) {
        if (viewers.contains(player.getUniqueId()))
            return;
        viewers.add(player.getUniqueId());
    }

    @Override
    public void stopOpen(Player player) {
        closeViewer(player.getUUID());
    }

    @Override
    public void onClose(CraftHumanEntity player) {
        if (!viewers.contains(player.getUniqueId()))
            return;
        viewers.remove(player.getUniqueId());
    }

    @Override
    public List<HumanEntity> getViewers() {
        return viewers.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    @Nullable
    public InventoryHolder getOwner() {
        return this;
    }

    @Override
    public void setMaxStackSize(int size) {
        if (bukkitInventory != null) {
            bukkitInventory.setMaxStackSize(size);
        }
        CustomBlockData data = CustomBlockData.from(level, pos);
        data.set(TypedKeys.MAX_STACK_SIZE, size);
    }

    @Override
    @Nullable
    public Location getLocation() {
        return new Location(level.getWorld(), pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void clearContent() {
        inventory = new ItemStack[inventory.length];
        if (bukkitInventory != null) {
            bukkitInventory.clear();
        }
        setChanged();
    }

    public void logDifferences() {
        // will log difference from bukkit inv to inventory stacks
        if (bukkitInventory == null)
            return;
        for (int i = 0; i < inventory.length; i++) {
            ItemStack nms = inventory[i];
            org.bukkit.inventory.ItemStack bukkit = bukkitInventory.getItem(i);
            if (nms == null)
                nms = ItemStack.EMPTY;
            if (bukkit == null)
                bukkit = org.bukkit.inventory.ItemStack.of(Material.AIR);
            if (!ItemStack.matches(nms, CraftItemStack.asNMSCopy(bukkit))) {
                CraftEnginePolyfills.log("Difference at slot " + i + ": NMS=" + nms + " Bukkit=" + bukkit);
            }
        }
    }

    public void syncFromBukkit() {
        // logDifferences();
        if (bukkitInventory != null) {
            for (int i = 0; i < inventory.length; i++) {
                if (i >= bukkitInventory.getSize())
                    break;
                if (bukkitInventory.getItem(i) != null)
                    inventory[i] = ((CraftItemStack) bukkitInventory.getItem(i)).handle;
                else
                    inventory[i] = ItemStack.EMPTY;
            }
        }
    }

    public void syncToBukkit() {
        // logDifferences();
        if (bukkitInventory != null) {
            for (int i = 0; i < inventory.length; i++) {
                if (i >= bukkitInventory.getSize())
                    break;
                if (inventory[i] != null)
                    bukkitInventory.setItem(i, CraftItemStack.asBukkitCopy(inventory[i]));
                else
                    bukkitInventory.setItem(i, org.bukkit.inventory.ItemStack.of(Material.AIR));
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (bukkitInventory == null) {
            bukkitInventory = Bukkit.createInventory(this, size, MiniMessage.miniMessage().deserialize(title));
            bukkitInventory.setMaxStackSize(maxStackSize);
            syncToBukkit();
        }
        return bukkitInventory;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        int[] slots = new int[size * 9];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = i;
        }
        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    private static volatile boolean LISTENER_REGISTERED = false;

    public static void ensureListenerRegistered(Plugin plugin) {
        if (LISTENER_REGISTERED)
            return;
        Bukkit.getPluginManager().registerEvents(new ListenerImpl(), plugin);
        LISTENER_REGISTERED = true;
    }

    private static class ListenerImpl implements Listener {
        @EventHandler
        public void onClose(InventoryCloseEvent event) {
            if (event.getInventory().getHolder() instanceof BlockContainer s) {
                s.removeViewer(event.getPlayer().getUniqueId());
                s.syncFromBukkit();
                s.setChanged();
                s.saveToData();
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onClick(InventoryClickEvent event) {

            if (event.getWhoClicked().getOpenInventory().getTopInventory().getHolder() instanceof BlockContainer s) {

                s.syncFromBukkit();
                s.setChanged(event.getWhoClicked().getUniqueId());
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onDrag(InventoryDragEvent event) {
            if (event.getInventory().getHolder() instanceof BlockContainer s) {

                s.syncFromBukkit();
                s.setChanged(event.getWhoClicked().getUniqueId());
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onMove(InventoryMoveItemEvent event) {
            if (event.getSource().getHolder() instanceof BlockContainer s) {
                s.syncFromBukkit();
                s.setChanged();
                s.saveToData();
            }
            if (event.getDestination().getHolder() instanceof BlockContainer s) {
                s.syncFromBukkit();
                s.setChanged();
                s.saveToData();
            }
        }

    }

    public int getAnalogOutput() {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(this);
    }

    public void updateNeighbors() {
        net.minecraft.world.level.block.Block block = level.getBlockIfLoaded(pos);

        if (block != null) {
            level.updateNeighbourForOutputSignal(pos, block);
        }
    }

}
