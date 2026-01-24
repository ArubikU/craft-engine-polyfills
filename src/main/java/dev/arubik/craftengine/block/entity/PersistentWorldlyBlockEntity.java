package dev.arubik.craftengine.block.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import dev.arubik.craftengine.util.DataHolders;
import dev.arubik.craftengine.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.UnsafeCompositeBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.CustomBlockStateWrapper;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.world.BlockPos;

/**
 * A BlockEntity that is also a WorldlyContainer (Inventory).
 * Combines PersistentBlockEntity features with AbstractWorldlyContainer logic.
 */
public abstract class PersistentWorldlyBlockEntity extends PersistentBlockEntity
        implements WorldlyContainer, InventoryHolder {

    protected ItemStack[] inventory;
    protected final Set<UUID> viewers = ConcurrentHashMap.newKeySet();
    protected final int size;
    protected int maxStackSize = 64;

    // Not needed: protected boolean stillValid = true; // BlockEntity has 'valid'
    // field? No, it has 'valid'. usage: isValid()
    // The decompiled BlockEntity has 'protected boolean valid;'

    public PersistentWorldlyBlockEntity(BlockPos pos, ImmutableBlockState state, int size) {
        super(pos, state);
        this.size = size;
        this.inventory = new ItemStack[size];
        Arrays.fill(this.inventory, ItemStack.EMPTY);
    }

    // --- WorldlyContainer / Container Implementation ---

    @Override
    public int getContainerSize() {
        return size;
    }

    public Level getNMSLevel() {
        return (Level) world.world.serverWorld();
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
            setChanged();
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
        if (!isValid()) // Check BlockEntity validity
            return false;
        return !player.isRemoved() && player.distanceToSqr(
                pos.x() + 0.5,
                pos.y() + 0.5,
                pos.z() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        Arrays.fill(inventory, ItemStack.EMPTY);
        setChanged();
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
        return null; // Subclasses should implement or provide
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
        return new Location((World) world.world.platformWorld(), pos.x(), pos.y(), pos.z());
    }

    // --- Lifecycle ---

    public void unregister() {
        // Cleaning up
    }

    public BlockBehavior getBlockBehavior() {
        Optional<ImmutableBlockState> customStateOpt = BlockStateUtils.getOptionalCustomBlockState(blockState);
        if (customStateOpt.isPresent()) {
            return customStateOpt.get().behavior();
        }
        return null;
    }

    public <T> T getBlockBehavior(Class<T> clazz) {
        Optional<ImmutableBlockState> customStateOpt = BlockStateUtils.getOptionalCustomBlockState(blockState);
        if (customStateOpt.isPresent()) {
            // check if is instance of or implements etc
            if (customStateOpt.get().behavior().getClass().isInstance(clazz)) {
                return (T) customStateOpt.get().behavior();
            }
            if (customStateOpt.get().behavior() instanceof UnsafeCompositeBlockBehavior beh) {
                if (beh.getAs(clazz).isPresent()) {
                    return beh.getAs(clazz).get();
                }
            }
        }
        return null;
    }
}
