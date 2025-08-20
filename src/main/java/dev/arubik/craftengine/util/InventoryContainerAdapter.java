package dev.arubik.craftengine.util;

import net.minecraft.world.Container;
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
public class InventoryContainerAdapter implements Container {

    private final Inventory bukkitInventory;
    private final Runnable onChanged;

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
}
