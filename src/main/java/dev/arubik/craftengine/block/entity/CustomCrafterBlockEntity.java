package dev.arubik.craftengine.block.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;

/**
 * Custom Crafter block entity — a faithful port of vanilla {@code net.minecraft.world.level.block.entity
 * .CrafterBlockEntity}, re-based onto CraftEngine's native block-entity storage ({@link
 * BlockEntityController#saveCustomData}/{@link BlockEntityController#loadCustomData} — CE's per-block NMS
 * NBT, NOT the Bukkit PDC the pipes use). Holds the 3x3 grid, the 9 slot enable/disable states, the
 * triggered flag and the crafting-ticks counter, exactly like vanilla.
 *
 * <p>It implements the vanilla {@link CraftingContainer} so {@code asCraftInput()}, {@code CrafterMenu}
 * and {@code HopperBlockEntity} item movement all work against it unchanged. The ONE behavioural change
 * lives in {@link dev.arubik.craftengine.block.behavior.CustomCrafterBehavior} (craft step): a damageable
 * ingredient with a {@code hurt_and_break} craft-remainder is damaged IN ITS SLOT instead of being
 * ejected out the front.</p>
 */
public class CustomCrafterBlockEntity extends BlockEntityController implements CraftingContainer {

    public static final int CONTAINER_WIDTH = 3;
    public static final int CONTAINER_HEIGHT = 3;
    public static final int CONTAINER_SIZE = 9;
    public static final int SLOT_DISABLED = 1;
    public static final int SLOT_ENABLED = 0;
    public static final int DATA_TRIGGERED = 9;
    public static final int NUM_DATA = 10;

    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    public int craftingTicksRemaining = 0;

    // Mirrors vanilla's ContainerData: 9 slot states (0 enabled / 1 disabled) + index 9 = triggered.
    private final int[] slotStates = new int[CONTAINER_SIZE];
    private int triggered = 0;
    private int maxStack = 99;

    public CustomCrafterBlockEntity(BlockEntity blockEntity) {
        super(blockEntity);
    }

    // ---------------- ticking (vanilla serverTick: clear CRAFTING flag after the animation) ----------------

    @Override
    public <C extends BlockEntityController> net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<C> createBlockEntityTicker(
            net.momirealms.craftengine.core.world.CEWorld world,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        return BlockEntityController.createTickerHelper(
                (net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<CustomCrafterBlockEntity>) CustomCrafterBlockEntity::tick);
    }

    public static void tick(net.momirealms.craftengine.core.world.CEWorld world,
            net.momirealms.craftengine.core.world.BlockPos cePos,
            net.momirealms.craftengine.core.block.ImmutableBlockState ceState, CustomCrafterBlockEntity self) {
        if (self.craftingTicksRemaining > 0) {
            self.craftingTicksRemaining--;
            if (self.craftingTicksRemaining == 0) {
                dev.arubik.craftengine.block.behavior.CustomCrafterBehavior.clearCrafting(self.level(), self.pos());
            }
        }
    }

    // ---------------- world helpers ----------------

    public net.minecraft.core.BlockPos pos() {
        return net.minecraft.core.BlockPos.of(blockEntity().pos().asLong());
    }

    public Level level() {
        try {
            return (Level) blockEntity().world().world().minecraftWorld();
        } catch (Throwable t) {
            return null;
        }
    }

    private static HolderLookup.Provider registries(Level level) {
        if (level != null)
            return level.registryAccess();
        return ((org.bukkit.craftbukkit.CraftServer) org.bukkit.Bukkit.getServer()).getServer().registryAccess();
    }

    // ---------------- vanilla CrafterBlockEntity port ----------------

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public int getWidth() {
        return CONTAINER_WIDTH;
    }

    @Override
    public int getHeight() {
        return CONTAINER_HEIGHT;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : this.items)
            if (!s.isEmpty())
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
            this.setChanged();
        return r;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (isSlotDisabled(index))
            setSlotState(index, true);
        if (stack.getCount() > this.getMaxStackSize())
            stack.setCount(this.getMaxStackSize());
        this.items.set(index, stack);
        this.setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return this.maxStack;
    }

    @Override
    public void setMaxStackSize(int size) {
        this.maxStack = size;
    }

    @Override
    public void setChanged() {
        // CraftEngine persists this controller via saveCustomData on chunk save — no extra dirty flag needed.
    }

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player player) {
        return true;
    }

    // --- Paper CraftBukkit Container bridge (forced by the interface; minimal, no real Bukkit use) ---
    private final java.util.List<org.bukkit.entity.HumanEntity> transaction = new java.util.ArrayList<>();

    @Override
    public org.bukkit.inventory.InventoryHolder getOwner() {
        return null;
    }

    @Override
    public java.util.List<ItemStack> getContents() {
        return this.items;
    }

    @Override
    public void onOpen(org.bukkit.craftbukkit.entity.CraftHumanEntity who) {
        this.transaction.add(who);
    }

    @Override
    public void onClose(org.bukkit.craftbukkit.entity.CraftHumanEntity who) {
        this.transaction.remove(who);
    }

    @Override
    public java.util.List<org.bukkit.entity.HumanEntity> getViewers() {
        return this.transaction;
    }

    @Override
    public org.bukkit.Location getLocation() {
        Level l = level();
        return l == null ? null : new org.bukkit.Location(l.getWorld(), pos().getX(), pos().getY(), pos().getZ());
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }

    @Override
    public void fillStackedContents(StackedItemContents contents) {
        for (ItemStack s : this.items)
            contents.accountSimpleStack(s);
    }

    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (this.slotStates[slot] == SLOT_DISABLED)
            return false;
        ItemStack cur = this.items.get(slot);
        int count = cur.getCount();
        return count < cur.getMaxStackSize() && (cur.isEmpty() || !smallerStackExist(count, cur, slot));
    }

    private boolean smallerStackExist(int currentSize, ItemStack stack, int slot) {
        for (int i = slot + 1; i < CONTAINER_SIZE; i++) {
            if (!isSlotDisabled(i)) {
                ItemStack item = getItem(i);
                if (item.isEmpty() || (item.getCount() < currentSize && ItemStack.isSameItemSameComponents(item, stack)))
                    return true;
            }
        }
        return false;
    }

    public void setSlotState(int slot, boolean enabled) {
        if (slotCanBeDisabled(slot)) {
            this.slotStates[slot] = enabled ? SLOT_ENABLED : SLOT_DISABLED;
            this.setChanged();
        }
    }

    public boolean isSlotDisabled(int slot) {
        return slot >= 0 && slot < CONTAINER_SIZE && this.slotStates[slot] == SLOT_DISABLED;
    }

    private boolean slotCanBeDisabled(int slot) {
        return slot > -1 && slot < CONTAINER_SIZE && this.items.get(slot).isEmpty();
    }

    public void setTriggered(boolean t) {
        this.triggered = t ? 1 : 0;
    }

    public boolean isTriggered() {
        return this.triggered == 1;
    }

    public void setCraftingTicksRemaining(int v) {
        this.craftingTicksRemaining = v;
    }

    /** Comparator/redstone output: number of slots that are filled OR disabled (vanilla). */
    public int getRedstoneSignal() {
        int n = 0;
        for (int i = 0; i < CONTAINER_SIZE; i++) {
            if (!getItem(i).isEmpty() || isSlotDisabled(i))
                n++;
        }
        return n;
    }

    /** Exposes the slot-state/triggered array as a vanilla ContainerData for {@code CrafterMenu}. */
    public net.minecraft.world.inventory.ContainerData containerData() {
        return new net.minecraft.world.inventory.ContainerData() {
            @Override
            public int get(int index) {
                return index == DATA_TRIGGERED ? triggered : slotStates[index];
            }

            @Override
            public void set(int index, int value) {
                if (index == DATA_TRIGGERED)
                    triggered = value;
                else
                    slotStates[index] = value;
            }

            @Override
            public int getCount() {
                return NUM_DATA;
            }
        };
    }

    // ---------------- CraftEngine native NMS storage ----------------

    @Override
    public void saveCustomData(CompoundTag tag) {
        HolderLookup.Provider provider = registries(level());
        try {
            // 1.21.11 ContainerHelper uses ValueOutput, so serialize each stack via ItemStack.save into a
            // raw NMS CompoundTag (keyed "i<slot>"), compress to bytes, stash in CE's native NBT.
            net.minecraft.resources.RegistryOps<net.minecraft.nbt.Tag> ops =
                    net.minecraft.resources.RegistryOps.create(net.minecraft.nbt.NbtOps.INSTANCE, provider);
            net.minecraft.nbt.CompoundTag root = new net.minecraft.nbt.CompoundTag();
            for (int i = 0; i < CONTAINER_SIZE; i++) {
                ItemStack s = this.items.get(i);
                if (!s.isEmpty())
                    root.put("i" + i, ItemStack.CODEC.encodeStart(ops, s).getOrThrow());
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            NbtIo.writeCompressed(root, baos);
            tag.putByteArray("items", baos.toByteArray());
        } catch (Throwable ignored) {
        }
        tag.putInt("crafting_ticks_remaining", this.craftingTicksRemaining);
        tag.putInt("triggered", this.triggered);
        int[] ss = new int[CONTAINER_SIZE];
        System.arraycopy(this.slotStates, 0, ss, 0, CONTAINER_SIZE);
        tag.putIntArray("slot_states", ss);
    }

    @Override
    public void loadCustomData(CompoundTag tag) {
        this.items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
        HolderLookup.Provider provider = registries(level());
        try {
            byte[] bytes = tag.getByteArray("items");
            if (bytes != null && bytes.length > 0) {
                net.minecraft.nbt.CompoundTag root = NbtIo.readCompressed(new ByteArrayInputStream(bytes),
                        NbtAccounter.unlimitedHeap());
                net.minecraft.resources.RegistryOps<net.minecraft.nbt.Tag> ops =
                        net.minecraft.resources.RegistryOps.create(net.minecraft.nbt.NbtOps.INSTANCE, provider);
                for (int i = 0; i < CONTAINER_SIZE; i++) {
                    net.minecraft.nbt.Tag it = root.get("i" + i);
                    if (it != null) {
                        final int slot = i;
                        ItemStack.CODEC.parse(ops, it).result().ifPresent(s -> this.items.set(slot, s));
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        this.craftingTicksRemaining = tag.getInt("crafting_ticks_remaining");
        this.triggered = tag.getInt("triggered");
        int[] ss = tag.getIntArray("slot_states");
        if (ss != null && ss.length == CONTAINER_SIZE)
            System.arraycopy(ss, 0, this.slotStates, 0, CONTAINER_SIZE);
    }
}
