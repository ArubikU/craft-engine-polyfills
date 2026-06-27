package dev.arubik.craftengine.block.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;

/**
 * Storage block entity — same native CraftEngine block-entity NBT storage as
 * {@link CustomCrafterBlockEntity} ({@code saveCustomData}/{@code loadCustomData}, NOT the Bukkit PDC the
 * old {@code BlockContainer} used). Holds an N-slot inventory and exposes itself as a vanilla
 * {@link Container} so hoppers/comparators + a vanilla {@code ChestMenu} work directly. Piston moves are
 * handled by {@link dev.arubik.craftengine.block.behavior.StorageBlockBehavior} via piston events.
 */
public class StorageBlockEntity extends BlockEntityController implements Container {

    private int size = 27;
    private int maxStack = 99;
    private NonNullList<ItemStack> items = NonNullList.withSize(54, ItemStack.EMPTY);

    public StorageBlockEntity(BlockEntity blockEntity) {
        super(blockEntity);
    }

    public void configure(int size, int maxStack) {
        this.size = Math.max(9, Math.min(size, 54));
        this.maxStack = Math.max(1, Math.min(maxStack, 99));
        if (this.items.size() < this.size)
            this.items = NonNullList.withSize(this.size, ItemStack.EMPTY);
    }

    public Level level() {
        try {
            return (Level) blockEntity().world().world().minecraftWorld();
        } catch (Throwable t) {
            return null;
        }
    }

    public net.minecraft.core.BlockPos pos() {
        return net.minecraft.core.BlockPos.of(blockEntity().pos().asLong());
    }

    private static HolderLookup.Provider registries(Level level) {
        if (level != null)
            return level.registryAccess();
        return ((org.bukkit.craftbukkit.CraftServer) org.bukkit.Bukkit.getServer()).getServer().registryAccess();
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public java.util.List<ItemStack> getContents() {
        return this.items;
    }

    // ---------------- Container ----------------

    @Override
    public int getContainerSize() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < this.size; i++)
            if (!this.items.get(i).isEmpty())
                return false;
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int amount) {
        ItemStack r = ContainerHelper.removeItem(this.items, index, amount);
        if (!r.isEmpty())
            setChanged();
        return r;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (stack.getCount() > getMaxStackSize())
            stack.setCount(getMaxStackSize());
        this.items.set(index, stack);
        setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return this.maxStack;
    }

    @Override
    public void setMaxStackSize(int s) {
        this.maxStack = s;
    }

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
        setChanged();
    }

    @Override
    public org.bukkit.Location getLocation() {
        return null; // forced by Paper's Container; we don't use Bukkit.
    }

    @Override
    public org.bukkit.inventory.InventoryHolder getOwner() {
        return null; // forced by Paper's Container signature; not used.
    }

    private final java.util.List<org.bukkit.entity.HumanEntity> viewers = new java.util.ArrayList<>();

    @Override
    public java.util.List<org.bukkit.entity.HumanEntity> getViewers() {
        return this.viewers;
    }

    @Override
    public void onOpen(org.bukkit.craftbukkit.entity.CraftHumanEntity who) {
        this.viewers.add(who);
    }

    @Override
    public void onClose(org.bukkit.craftbukkit.entity.CraftHumanEntity who) {
        this.viewers.remove(who);
    }

    @Override
    public void setChanged() {
        // CraftEngine persists via saveCustomData on chunk save.
    }

    /** Comparator output (vanilla container fullness 0-15). */
    public int getRedstoneSignal() {
        return net.minecraft.world.inventory.AbstractContainerMenu.getRedstoneSignalFromContainer(this);
    }

    // ---------------- CraftEngine native NMS storage ----------------

    @Override
    public void saveCustomData(CompoundTag tag) {
        HolderLookup.Provider provider = registries(level());
        try {
            net.minecraft.resources.RegistryOps<net.minecraft.nbt.Tag> ops =
                    net.minecraft.resources.RegistryOps.create(net.minecraft.nbt.NbtOps.INSTANCE, provider);
            net.minecraft.nbt.CompoundTag root = new net.minecraft.nbt.CompoundTag();
            for (int i = 0; i < this.size; i++) {
                final int idx = i;
                ItemStack s = this.items.get(i);
                if (!s.isEmpty())
                    ItemStack.CODEC.encodeStart(ops, s).result().ifPresent(t -> root.put("i" + idx, t));
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            NbtIo.writeCompressed(root, baos);
            tag.putByteArray("items", baos.toByteArray());
        } catch (Throwable ignored) {
        }
        tag.putInt("size", this.size);
        tag.putInt("max_stack", this.maxStack);
    }

    @Override
    public void loadCustomData(CompoundTag tag) {
        int sz = tag.getInt("size");
        if (sz >= 9 && sz <= 54)
            this.size = sz;
        int ms = tag.getInt("max_stack");
        if (ms >= 1 && ms <= 99)
            this.maxStack = ms;
        this.items = NonNullList.withSize(54, ItemStack.EMPTY);
        HolderLookup.Provider provider = registries(level());
        try {
            byte[] bytes = tag.getByteArray("items");
            if (bytes != null && bytes.length > 0) {
                net.minecraft.nbt.CompoundTag root = NbtIo.readCompressed(new ByteArrayInputStream(bytes),
                        NbtAccounter.unlimitedHeap());
                net.minecraft.resources.RegistryOps<net.minecraft.nbt.Tag> ops =
                        net.minecraft.resources.RegistryOps.create(net.minecraft.nbt.NbtOps.INSTANCE, provider);
                for (int i = 0; i < 54; i++) {
                    net.minecraft.nbt.Tag it = root.get("i" + i);
                    if (it != null) {
                        final int slot = i;
                        ItemStack.CODEC.parse(ops, it).result().ifPresent(s -> this.items.set(slot, s));
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }
}
