package dev.arubik.craftengine.block.entity;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;

import dev.arubik.craftengine.util.AbstractWorldlyContainer;
import dev.arubik.craftengine.util.CustomBlockData;
import dev.arubik.craftengine.util.CustomDataType;
import dev.arubik.craftengine.util.DataHolder;
import dev.arubik.craftengine.util.DataHolders;
import dev.arubik.craftengine.util.TypedKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;

public class SpikeBlockEntity extends AbstractWorldlyContainer {

    // item field replaced by inventory[0]
    private UUID ownerUUID; // Renamed to avoid conflict with Container.getOwner()
    // pos, level, viewers, stillValid inherited

    private static final Map<String, SpikeBlockEntity> REGISTRY = new ConcurrentHashMap<>();

    private static String makeKey(Level level, BlockPos pos) {
        return level.getWorld().getName() + "@" + "x" + pos.getX() + "y" + pos.getY() + "z" + pos.getZ();
    }

    public static SpikeBlockEntity getOrCreate(Level level, BlockPos pos) {
        return REGISTRY.computeIfAbsent(makeKey(level, pos),
                k -> new SpikeBlockEntity(pos, level));
    }

    public static Optional<SpikeBlockEntity> get(Level level, BlockPos pos) {
        return Optional.ofNullable(REGISTRY.get(makeKey(level, pos)));
    }

    public static void remove(Level level, BlockPos pos) {
        REGISTRY.remove(makeKey(level, pos));
    }

    @Override
    protected void unregister() {
        remove(level, pos);
    }

    public SpikeBlockEntity(BlockPos pos, Level level) {
        super(1, pos, level);
        this.maxStackSize = 1;
        DataHolders.INSTANCE.addHolder(this);
    }

    public net.minecraft.world.item.ItemStack getItem() {
        return getItem(0);
    }

    public void setItem(net.minecraft.world.item.ItemStack item) {
        setItem(0, item);
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID owner) {
        this.ownerUUID = owner;
        setChanged();
    }

    private long lastAttackTime = 0;
    private static final int[] SLOTS = new int[] { 0 }; // Single slot

    public long getLastAttackTime() {
        return lastAttackTime;
    }

    public void setLastAttackTime(long lastAttackTime) {
        this.lastAttackTime = lastAttackTime;
        setChanged();
    }

    // WorldlyContainer overrides for specific logic

    @Override
    public boolean canPlaceItemThroughFace(int index, net.minecraft.world.item.ItemStack stack, Direction direction) {
        return index == 0 && inventory[0].isEmpty(); // Only allow placing if empty
    }

    @Override
    public boolean canTakeItemThroughFace(int index, net.minecraft.world.item.ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    public net.minecraft.world.item.ItemStack removeItem(int slot, int amount) {
        // Capture result to check if we emptied it
        net.minecraft.world.item.ItemStack result = super.removeItem(slot, amount);
        if (slot == 0 && inventory[0].isEmpty()) {
            ownerUUID = null;
        }
        return result;
    }

    @Override
    public net.minecraft.world.item.ItemStack removeItemNoUpdate(int slot) {
        net.minecraft.world.item.ItemStack result = super.removeItemNoUpdate(slot);
        if (slot == 0) {
            ownerUUID = null;
        }
        return result;
    }

    @Override
    public void saveToData() {
        CustomBlockData data = CustomBlockData.from(level, pos);
        net.minecraft.world.item.ItemStack item = inventory[0];
        if (item != null && !item.isEmpty()) {
            data.set(TypedKeys.ITEM, CraftItemStack.asBukkitCopy(item));
        } else {
            data.remove(TypedKeys.ITEM);
        }

        if (ownerUUID != null) {
            data.set(TypedKeys.OWNER, ownerUUID);
        }

        data.set(TypedKeys.LAST_ATTACK_TIME, lastAttackTime);
    }

    @Override
    public void loadFromData() {
        CustomBlockData data = CustomBlockData.from(level, pos);
        data.getOptional(TypedKeys.ITEM).ifPresent(bukkitItem -> {
            if (bukkitItem != null) {
                this.inventory[0] = CraftItemStack.asNMSCopy(bukkitItem);
            } else {
                this.inventory[0] = net.minecraft.world.item.ItemStack.EMPTY;
            }
        });
        data.getOptional(TypedKeys.OWNER).ifPresent(o -> this.ownerUUID = o);
        data.getOptional(TypedKeys.LAST_ATTACK_TIME).ifPresent(t -> this.lastAttackTime = t);
    }

    @Override
    public void setChanged() {
        saveToData();
    }

    // setChanged inherited triggers saveToData
}
