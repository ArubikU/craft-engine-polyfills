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

public class BlockContainer extends AbstractWorldlyContainer {

    private final String title;
    // Fields inventory, viewers, size, pos, level, maxStackSize, stillValid are
    // inherited

    public BlockContainer(ItemStack[] inventory, Set<UUID> viewers, int size, String title, BlockPos pos,
            Level level, SoundMap soundMap) {
        super(size, pos, level);
        this.inventory = inventory;
        this.viewers.addAll(viewers);
        this.title = title;
        this.soundMap = soundMap;

        DataHolders.INSTANCE.addHolder(this);
    }

    private SoundMap soundMap;

    public void setSoundMap(SoundMap soundMap) {
        this.soundMap = soundMap;
    }

    private static final Map<String, BlockContainer> REGISTRY = new ConcurrentHashMap<>();

    private static String makeKey(Level level, BlockPos pos) {
        return level.getWorld().getName() + "@" + "x" + pos.getX() + "y" + pos.getY() + "z" + pos.getZ();
    }

    public static BlockContainer getOrCreate(Level level, BlockPos pos, int size, String title, SoundMap soundMap) {
        return REGISTRY.computeIfAbsent(makeKey(level, pos),
                k -> new BlockContainer(new ItemStack[size], ConcurrentHashMap.newKeySet(), size, title, pos, level,
                        soundMap));
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
    protected void unregister() {
        remove(level, pos);
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
        this.inventory = new ItemStack[size];
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

    // WorldlyContainer methods are mostly inherited now.
    // We override modifying ones to ensure syncToBukkit happens.

    @Override
    public ItemStack removeItem(int slot, int amount) {
        // We reuse logic but add sync. Or we just override completely to be safe.
        // Super logic: result = inventory[slot].split(amount); setChanged();
        // BlockContainer logic: result = ...; syncToBukkit(); setChanged();

        // Let's defer to super for logic, but we need syncToBukkit BEFORE setChanged?
        // setChanged() triggers viewer sync (openInventory).
        // syncToBukkit() updates the Inventory view.
        // If we use super.removeItem, inventory is updated.
        // Then setChanged is called.
        // We can override setChanged to call syncToBukkit first?
        // But removing item logic in BlockContainer explicitly called syncToBukkit.

        // Implementation:
        ItemStack result = super.removeItem(slot, amount);
        syncToBukkit();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = super.removeItemNoUpdate(slot);
        syncToBukkit();
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        syncToBukkit();
        saveToData();
    }

    // getMaxStackSize inherited but we have custom logic for CustomBlockData in
    // BlockContainer
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

    // stillValid inherited

    // getContents inherited/duplicated -> Abstract returns List<ItemStack>,
    // BlockContainer likely same.
    // Abstract impl:
    /*
     * @Override
     * public List<ItemStack> getContents() {
     * // Implicit via Arrays.asList(inventory) or manual
     * }
     * Wait, Abstract didn't impl getContents() fully?
     * Abstract does NOT implement getContents() in the provided code snippet I
     * wrote?
     * I checked the file write. I did NOT write getContents() in
     * AbstractWorldlyContainer.
     * So BlockContainer needs to keep it or I add it to Abstract.
     * InventoryHolder (Bukkit) doesn't strictly require getContents?
     * Wait, implemented method in BlockContainer was:
     * public List<ItemStack> getContents()
     * This is likely from WorldlyContainer or Container (NMS).
     * Container defines `getContents()`.
     * So Abstract should have implemented it if it implements Container.
     * I missed `getContents` in Abstract.
     * It will be abstract then? Or I implement it in BlockContainer.
     */

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
        super.onOpen(player);
        // Play Open Sound
        if (soundMap != null) {
            soundMap.playOpen(player.getLocation());
        }
    }

    @Override
    public void onClose(CraftHumanEntity player) {
        super.onClose(player);
        // Play Close Sound
        if (soundMap != null) {
            soundMap.playClose(player.getLocation());
        }
    }

    // getViewers inherited

    // getOwner inherited

    @Override
    public void setMaxStackSize(int size) {
        if (bukkitInventory != null) {
            bukkitInventory.setMaxStackSize(size);
        }
        super.setMaxStackSize(size); // updates inherited field
        CustomBlockData data = CustomBlockData.from(level, pos);
        data.set(TypedKeys.MAX_STACK_SIZE, size);
    }

    // getLocation inherited

    @Override
    public void clearContent() {
        super.clearContent();
        if (bukkitInventory != null) {
            bukkitInventory.clear();
        }
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

    // getSlotsForFace inherited
    // canPlaceItemThroughFace inherited
    // canTakeItemThroughFace inherited

    // Listener logic kept
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
            if (event.getSource() != null && event.getSource().getHolder() instanceof BlockContainer s) {
                s.syncFromBukkit();
                s.setChanged();
                s.saveToData();
            }
            if (event.getDestination() != null && event.getDestination().getHolder() instanceof BlockContainer s) {
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
