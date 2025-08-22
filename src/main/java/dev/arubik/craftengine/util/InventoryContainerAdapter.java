package dev.arubik.craftengine.util;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adapter that implements NMS Container over a Bukkit Inventory.
 * This allows external NMS code to interact with a Bukkit inventory.
 */
public class InventoryContainerAdapter implements WorldlyContainer {

    private final Inventory bukkitInventory;
    private final Runnable onChanged;

    // Arrays for slot indices for inventories with 1 to 6 rows (9 slots per row)
    private static final int[] SLOTS_1_ROW = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final int[] SLOTS_2_ROWS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
    private static final int[] SLOTS_3_ROWS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
    private static final int[] SLOTS_4_ROWS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34};
    private static final int[] SLOTS_5_ROWS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
    private static final int[] SLOTS_6_ROWS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};

    public InventoryContainerAdapter(Inventory bukkitInventory, @Nullable Runnable onChanged) {
        this.bukkitInventory = bukkitInventory;
        this.onChanged = onChanged != null ? onChanged : () -> {};
    }

    @Override
    public int getContainerSize() {
        return bukkitInventory.getSize();
    }

    @Override
    public boolean isEmpty() {
        for (org.bukkit.inventory.ItemStack it : bukkitInventory.getContents()) {
            if (it != null && !it.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        org.bukkit.inventory.ItemStack b = bukkitInventory.getItem(slot);
        if (b == null || b.isEmpty()) return ItemStack.EMPTY;
        return CraftItemStack.asNMSCopy(b);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (amount <= 0) return ItemStack.EMPTY;
        org.bukkit.inventory.ItemStack b = bukkitInventory.getItem(slot);
        if (b == null || b.isEmpty()) return ItemStack.EMPTY;

        int take = Math.min(amount, b.getAmount());
        org.bukkit.inventory.ItemStack taken = b.clone();
        taken.setAmount(take);

        int remain = b.getAmount() - take;
        if (remain > 0) {
            b.setAmount(remain);
            bukkitInventory.setItem(slot, b);
        } else {
            bukkitInventory.setItem(slot, null);
        }
        setChanged();
        return CraftItemStack.asNMSCopy(taken);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        org.bukkit.inventory.ItemStack b = bukkitInventory.getItem(slot);
        if (b == null || b.isEmpty()) return ItemStack.EMPTY;
        bukkitInventory.setItem(slot, null);
        return CraftItemStack.asNMSCopy(b);
    }

    @Override
    public void setItem(int slot, @Nonnull ItemStack stack) {
        bukkitInventory.setItem(slot, CraftItemStack.asBukkitCopy(stack));
        setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return bukkitInventory.getMaxStackSize();
    }

    @Override
    public void setChanged() {
        this.onChanged.run();
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        // Not bound to a specific block by default; always valid
        return true;
    }

    @Override
    public void clearContent() {
        bukkitInventory.clear();
        setChanged();
    }

    // --- CraftBukkit additions ---

    @Override
    public List<ItemStack> getContents() {
        org.bukkit.inventory.ItemStack[] arr = bukkitInventory.getContents();
        List<ItemStack> out = new ArrayList<>(arr.length);
        for (org.bukkit.inventory.ItemStack b : arr) {
            out.add(b == null || b.isEmpty() ? ItemStack.EMPTY : CraftItemStack.asNMSCopy(b));
        }
        return Collections.unmodifiableList(out);
    }

    @Override
    public void onOpen(@javax.annotation.Nonnull CraftHumanEntity player) {
        // no-op; Bukkit handles viewers internally
    }

    @Override
    public void onClose(@javax.annotation.Nonnull CraftHumanEntity player) {
        // no-op
    }

    @Override
    public List<HumanEntity> getViewers() {
        return bukkitInventory.getViewers();
    }

    @Override
    public InventoryHolder getOwner() {
        return bukkitInventory.getHolder();
    }

    @Override
    public void setMaxStackSize(int size) {
        bukkitInventory.setMaxStackSize(size);
    }

    @Override
    public Location getLocation() {
        return bukkitInventory.getLocation();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return  switch (bukkitInventory.getSize()) {
            case 9 -> SLOTS_1_ROW;
            case 18 -> SLOTS_2_ROWS;
            case 27 -> SLOTS_3_ROWS;
            case 36 -> SLOTS_4_ROWS;
            case 45 -> SLOTS_5_ROWS;
            case 54 -> SLOTS_6_ROWS;
            default -> new int[0]; // No slots for other sizes
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }
}
