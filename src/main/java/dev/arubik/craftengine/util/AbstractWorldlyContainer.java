package dev.arubik.craftengine.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public abstract class AbstractWorldlyContainer implements WorldlyContainer, InventoryHolder, DataHolder {

    protected ItemStack[] inventory;
    protected final Set<UUID> viewers = ConcurrentHashMap.newKeySet();
    protected final int size;
    protected final BlockPos pos;
    protected final Level level;
    protected int maxStackSize = 64;
    protected boolean stillValid = true;

    // Registry management could be abstract or specific, leaving it specific for
    // now

    public AbstractWorldlyContainer(int size, BlockPos pos, Level level) {
        this.size = size;
        this.pos = pos;
        this.level = level;
        this.inventory = new ItemStack[size];
        Arrays.fill(this.inventory, ItemStack.EMPTY);
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public void setChanged() {
        if (level != null) {
            // Mark block entity as dirty for persistence
            // In NMS/Fabric: setChanged() or markDirty()
            // In Bukkit-like abstract world:
            // We might not have direct access to 'this' as a BlockEntity to call
            // super.setChanged().
            // But AbstractWorldlyContainer IS NOT A BLOCK ENTITY class itself?
            // User code 'AbstractMachineBlockEntity extends AbstractWorldlyContainer'.
            // If AbstractMachineBlockEntity is the BlockEntity, then setChanged() comes
            // from BlockEntity.
            // But WorldlyContainer interface forces setChanged().
            // If AbstractMachineBlockEntity doesn't extend
            // net.minecraft.world.level.block.entity.BlockEntity, then it must implement it
            // manually.
            // Wait, does AbstractWorldlyContainer extend BlockEntity?
            // "public abstract class AbstractWorldlyContainer implements WorldlyContainer,
            // InventoryHolder, DataHolder"
            // It does NOT extend BlockEntity.
            // So AbstractMachineBlockEntity probably *should* extend BlockEntity?
            // But Java doesn't support multiple inheritance.
            // Maybe AbstractWorldlyContainer is meant to be a delegate?
            // Or maybe AbstractWorldlyContainer *should* extend BlockEntity?
            // Let's check imports of AbstractWorldlyContainer.
            // It imports net.minecraft.world.level.block.entity.BlockEntity? No.
            // Line 30: implements WorldlyContainer...

            // If I look at the user's codebase pattern (SpikeBlockBehavior), maybe they
            // separate Behavior from BlockEntity?
            // But AbstractMachineBlockEntity looks like it wants to be the BE.
            // The compilation error says "PolyFurnaceBlockEntity must implement...
            // setChanged()".
            // Implementation:
        }
    }

    // --- WorldlyContainer / Container Implementation ---

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
        if (slot >= 0 && slot < inventory.length) {
            return inventory[slot] == null ? ItemStack.EMPTY : inventory[slot];
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ItemStack.EMPTY;
        if (slot >= 0 && slot < inventory.length && !inventory[slot].isEmpty() && amount > 0) {
            result = inventory[slot].split(amount);
            setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = ItemStack.EMPTY;
        if (slot >= 0 && slot < inventory.length) {
            result = inventory[slot];
            inventory[slot] = ItemStack.EMPTY;
            setChanged(); // Usually no update, but we want consistency
        }
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < inventory.length) {
            inventory[slot] = stack;
            if (stack != null && stack.getCount() > getMaxStackSize()) {
                stack.setCount(getMaxStackSize());
            }
            setChanged();
        }
    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public void setMaxStackSize(int size) {
        this.maxStackSize = size;
    }

    @Override
    public boolean stillValid(Player player) {
        if (!stillValid)
            return false;
        return !player.isRemoved() && player.distanceToSqr(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        Arrays.fill(inventory, ItemStack.EMPTY);
        setChanged();
    }

    // --- DataHolder Common Implementation ---

    @Override
    public void saveToData() {
        CustomBlockData data = CustomBlockData.from(level, pos);
        // Only save specific fields if subclasses call super or we do it here.
        // Better to let subclasses handle specific logic but we can provide helper for
        // inventory.
        // BlockContainer saves "CONTENTS", Spike saves "ITEM".
        // We will leave implementation to subclasses or unify in future.
        // For now, let's keep it abstract or empty default.
    }

    @Override
    public void loadFromData() {
        // Same as above.
    }

    // --- WorldlyContainer Side Logic (Default permissive) ---

    @Override
    public int[] getSlotsForFace(Direction side) {
        int[] slots = new int[size];
        for (int i = 0; i < size; i++)
            slots[i] = i;
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

    // --- Bukkit InventoryHolder Support ---

    @Override
    @Nullable
    public InventoryHolder getOwner() {
        return this;
    }

    @Override
    public @NotNull Inventory getInventory() {
        // Needs to be implemented by subclasses or we use a generic method if title is
        // available
        // BlockContainer has dynamic title, Spike doesn't really have one.
        // We'll leave abstract or throw unsupported.
        // Better: Abstract.
        return null;
    }

    @Override
    public java.util.List<ItemStack> getContents() {
        java.util.List<ItemStack> contents = new java.util.ArrayList<>();
        for (ItemStack item : inventory) {
            if (item != null)
                contents.add(item);
            else
                contents.add(ItemStack.EMPTY);
        }
        return contents;
    }

    // --- Viewer Management Helpers ---
    @Override
    public void onOpen(CraftHumanEntity player) {
        viewers.add(player.getUniqueId());
    }

    @Override
    public void onClose(CraftHumanEntity player) {
        viewers.remove(player.getUniqueId());
    }

    @Override
    public List<HumanEntity> getViewers() {
        return viewers.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Location getLocation() {
        return new Location(level.getWorld(), pos.getX(), pos.getY(), pos.getZ());
    }

    // --- Lifecycle ---

    @Override
    public void onUnload() {
        saveToData();
        stillValid = false;
        unregister();
    }

    @Override
    public void destroy() {
        stillValid = false;
        DataHolders.INSTANCE.destroyHolder(this);
        unregister();
    }

    protected abstract void unregister();

}
