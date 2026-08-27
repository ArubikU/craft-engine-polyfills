package dev.arubik.craftengine.item;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Per-itemstack persistent state for {@link ItemDefinition}-driven items: named tank buffers
 * (fluid/gas/energy) and each menu page's free {@code STORAGE} slot contents. Both live under a
 * "PolyfillItem" compound inside {@code minecraft:custom_data} (the same component
 * {@code ItemType.nbt} already exposes to scripts).
 *
 * <p>Page storage is NOT the native {@code minecraft:container} component (a plain menu of
 * buttons/bars/storage can't be represented by that single-purpose component), so slots are
 * serialized the same way {@code CustomCrafterBlockEntity#saveCustomData}/{@code loadCustomData}
 * persists a block entity's inventory: {@code ItemStack.CODEC} through {@code RegistryOps} over
 * {@code NbtOps}, gzip-compressed into one byte blob per page.
 */
public final class ItemStateData {
    private static final String ROOT = "PolyfillItem";
    private static final String TANKS = "Tanks";
    private static final String PAGES = "Pages";

    private ItemStateData() {}

    // ------------------------------------------------------------------ tanks

    public static int tankAmount(ItemStack stack, String name) {
        CompoundTag root = readRoot(stack);
        if (root == null || !root.contains(TANKS)) return 0;
        CompoundTag tanks = root.getCompoundOrEmpty(TANKS);
        return tanks.getIntOr(name, 0);
    }

    public static ItemStack setTankAmount(ItemStack stack, String name, int amount, int capacity) {
        int clamped = Math.max(0, Math.min(amount, capacity));
        ItemStack copy = stack.copy();
        CompoundTag root = readRoot(copy);
        if (root == null) root = new CompoundTag();
        CompoundTag tanks = root.getCompoundOrEmpty(TANKS).copy();
        tanks.putInt(name, clamped);
        root.put(TANKS, tanks);
        writeRoot(copy, root);
        return copy;
    }

    // --------------------------------------------------------------- page storage

    private static HolderLookup.Provider registries() {
        return ((org.bukkit.craftbukkit.CraftServer) org.bukkit.Bukkit.getServer()).getServer().registryAccess();
    }

    /** The free STORAGE-slot contents saved for page {@code pageIndex}, padded/truncated to {@code size}. */
    public static ItemStack[] pageStorage(ItemStack stack, int pageIndex, int size) {
        ItemStack[] out = new ItemStack[size];
        for (int i = 0; i < size; i++) out[i] = ItemStack.EMPTY;
        CompoundTag root = readRoot(stack);
        if (root == null || !root.contains(PAGES)) return out;
        Tag rawBytes = root.getCompoundOrEmpty(PAGES).get("p" + pageIndex);
        byte[] bytes = rawBytes instanceof net.minecraft.nbt.ByteArrayTag bat ? bat.getAsByteArray() : null;
        if (bytes == null || bytes.length == 0) return out;
        try {
            RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries());
            CompoundTag blob = NbtIo.readCompressed(new ByteArrayInputStream(bytes), NbtAccounter.unlimitedHeap());
            for (int i = 0; i < size; i++) {
                Tag it = blob.get("i" + i);
                if (it == null) continue;
                final int slot = i;
                ItemStack.CODEC.parse(ops, it).result().ifPresent(s -> out[slot] = s);
            }
        } catch (Throwable ignored) {
        }
        return out;
    }

    /** Persists {@code items} as page {@code pageIndex}'s STORAGE-slot contents. */
    public static ItemStack setPageStorage(ItemStack stack, int pageIndex, ItemStack[] items) {
        ItemStack copy = stack.copy();
        try {
            RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries());
            CompoundTag blob = new CompoundTag();
            for (int i = 0; i < items.length; i++) {
                ItemStack s = items[i];
                if (s != null && !s.isEmpty())
                    blob.put("i" + i, ItemStack.CODEC.encodeStart(ops, s).getOrThrow());
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            NbtIo.writeCompressed(blob, baos);

            CompoundTag root = readRoot(copy);
            if (root == null) root = new CompoundTag();
            CompoundTag pages = root.getCompoundOrEmpty(PAGES).copy();
            pages.putByteArray("p" + pageIndex, baos.toByteArray());
            root.put(PAGES, pages);
            writeRoot(copy, root);
        } catch (Throwable ignored) {
        }
        return copy;
    }

    /**
     * Cumulative {@code storageSlots().length} across every page BEFORE {@code pageIndex} — the
     * underlying machine container is ONE physical inventory shared by every page (unlike an
     * item's per-page NBT storage, which has no such collision risk), so without this offset two
     * pages that both declare e.g. {@code "storage": [0..17]} would alias the SAME 18 container
     * slots instead of getting independent storage. Mirrors
     * {@code DataMachineBlockEntity#storageBaseOffset} — kept in sync with it because both the
     * item's own pages and the machine's own pages are required to declare matching per-page
     * storage-slot COUNTS (not necessarily the same grid positions) for the round trip to be
     * lossless at all; see {@link ItemDefinition#pages()}.
     */
    public static int pageStorageOffset(java.util.List<dev.arubik.craftengine.machine.MachineDefinition.PageDef> pages,
            int pageIndex) {
        int offset = 0;
        for (int i = 0; i < pageIndex && i < pages.size(); i++) offset += pages.get(i).storageSlots().length;
        return offset;
    }

    /**
     * Builds a fresh instance of {@code definition}'s own CraftEngine item, reading each of its
     * pages' STORAGE slots from the SAME absolute offset a machine built from the identical page
     * schema would use ({@link #pageStorageOffset}), so page 2's items land back on page 2 instead
     * of being compacted onto page 1 whenever page 1 wasn't completely full. {@code containerReader}
     * reads one absolute slot of the SOURCE machine's container (e.g. {@code worldly::getItem}).
     */
    public static ItemStack buildFromContainer(ItemDefinition definition,
            java.util.function.IntFunction<ItemStack> containerReader, int containerSize) {
        net.momirealms.craftengine.bukkit.item.BukkitItemDefinition ceDef =
                net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(definition.id());
        org.bukkit.inventory.ItemStack bukkit = ceDef != null ? ceDef.buildBukkitItem()
                : new org.bukkit.inventory.ItemStack(org.bukkit.Material.PAPER);
        ItemStack nms = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkit);

        for (int p = 0; p < definition.pages().size(); p++) {
            int[] storageSlots = definition.pages().get(p).storageSlots();
            if (storageSlots.length == 0) continue;
            int base = pageStorageOffset(definition.pages(), p);
            ItemStack[] slice = new ItemStack[storageSlots.length];
            for (int i = 0; i < storageSlots.length; i++) {
                int containerSlot = base + i;
                slice[i] = containerSlot < containerSize ? containerReader.apply(containerSlot) : ItemStack.EMPTY;
                if (slice[i] == null) slice[i] = ItemStack.EMPTY;
            }
            nms = setPageStorage(nms, p, slice);
        }
        return nms;
    }

    /**
     * Writes every page's STORAGE-slot contents into a target machine's container at the SAME
     * absolute offsets {@link #buildFromContainer} reads from — the round-trip inverse, used to
     * spill a backpack's contents into a freshly placed container block without page 2's items
     * landing in page 1's container slots. {@code containerWriter} writes one absolute slot of the
     * TARGET machine's container (e.g. {@code (slot, item) -> machine.setItem(slot, item)}).
     */
    public static void writeToContainer(ItemStack stack, ItemDefinition definition,
            java.util.function.BiConsumer<Integer, ItemStack> containerWriter, int containerSize) {
        for (int p = 0; p < definition.pages().size(); p++) {
            int[] storageSlots = definition.pages().get(p).storageSlots();
            if (storageSlots.length == 0) continue;
            int base = pageStorageOffset(definition.pages(), p);
            ItemStack[] slice = pageStorage(stack, p, storageSlots.length);
            for (int i = 0; i < slice.length; i++) {
                ItemStack it = slice[i];
                if (it == null || it.isEmpty()) continue;
                int containerSlot = base + i;
                if (containerSlot < containerSize) containerWriter.accept(containerSlot, it);
            }
        }
    }

    // ------------------------------------------------------------------- shared

    private static CompoundTag readRoot(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null || data.isEmpty()) return null;
        CompoundTag tag = data.copyTag();
        return tag.contains(ROOT) ? tag.getCompoundOrEmpty(ROOT) : null;
    }

    private static void writeRoot(ItemStack stack, CompoundTag root) {
        CustomData existing = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = existing != null ? existing.copyTag() : new CompoundTag();
        tag.put(ROOT, root);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
