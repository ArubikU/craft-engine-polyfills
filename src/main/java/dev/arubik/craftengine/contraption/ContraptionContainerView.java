package dev.arubik.craftengine.contraption;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.pipe.item.ItemTransferHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * A LIVE combined {@link Container} over every {@code STORAGE}/{@code OUTPUT}-role slot of every
 * block-entity riding an assembled contraption — never a snapshot/copy. Every {@link #getItem}/
 * {@link #setItem} call is forwarded straight through to the real underlying container found at
 * that local position via {@link ItemTransferHelper#getContainer}, so anything read out of this
 * view actually leaves the contraption's own inventory and anything written into it actually lands
 * there — exactly the semantics the Portable Storage Interface needs (mirror, not copy).
 *
 * INPUT and FUEL slots are deliberately excluded (a contraption shouldn't have its recipe inputs
 * or furnace fuel silently drained/overwritten by an external puller) — only free {@code STORAGE}
 * slots and finished {@code OUTPUT} slots are exposed. A plain vanilla container riding the
 * contraption (chest, barrel, shulker box...) has no such role split, so all of its slots count.
 *
 * Rebuilt fresh (via {@link #build}) each time it's needed — cheap (one pass over the contraption's
 * local positions) and always reflects the contraption's current block layout, including blocks
 * added/removed since the last build.
 */
public final class ContraptionContainerView implements WorldlyContainer {

    private record Slot(Container container, int index) {}

    private final List<Slot> slots;

    private ContraptionContainerView(List<Slot> slots) {
        this.slots = slots;
    }

    public static ContraptionContainerView build(ContraptionLevel level) {
        List<Slot> slots = new ArrayList<>();
        if (level != null) {
            Level nms = level.serverLevel();
            for (BlockPos local : level.localPositions()) {
                ItemTransferHelper.getContainer(nms, local).ifPresent(container -> {
                    for (int idx : storageAndOutputIndices(container)) {
                        slots.add(new Slot(container, idx));
                    }
                });
            }
        }
        return new ContraptionContainerView(slots);
    }

    private static int[] storageAndOutputIndices(Container container) {
        if (container instanceof AbstractMachineBlockEntity machine) {
            LinkedHashSet<Integer> combined = new LinkedHashSet<>();
            for (int s : machine.getStorageSlots()) combined.add(s);
            for (int s : machine.getOutputSlots()) combined.add(s);
            return combined.stream().mapToInt(Integer::intValue).toArray();
        }
        // A plain vanilla/foreign container has no input/output/fuel concept — every slot counts.
        int size = container.getContainerSize();
        int[] all = new int[size];
        for (int i = 0; i < size; i++) all[i] = i;
        return all;
    }

    /** Whether any exposed slot has room for at least one more of {@code stack} (a full stack or
     *  a partial merge) — used by scripts to decide whether it's worth trying to push at all. */
    public boolean hasRoomFor(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        for (Slot slot : this.slots) {
            ItemStack current = slot.container().getItem(slot.index());
            if (current.isEmpty()) return true;
            if (ItemStack.isSameItemSameComponents(current, stack)
                    && current.getCount() < current.getMaxStackSize()
                    && current.getCount() < slot.container().getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    /** Greedily pushes as much of {@code stack} as fits (merging into partials first, then empty
     *  slots), mutating {@code stack} in place and returning it (possibly emptied). */
    public ItemStack push(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return stack;
        for (Slot slot : this.slots) {
            if (stack.isEmpty()) break;
            ItemStack current = slot.container().getItem(slot.index());
            if (!current.isEmpty() && ItemStack.isSameItemSameComponents(current, stack)) {
                int room = Math.min(current.getMaxStackSize(), slot.container().getMaxStackSize()) - current.getCount();
                if (room > 0) {
                    int moved = Math.min(room, stack.getCount());
                    current.grow(moved);
                    slot.container().setItem(slot.index(), current);
                    stack.shrink(moved);
                    slot.container().setChanged();
                }
            }
        }
        for (Slot slot : this.slots) {
            if (stack.isEmpty()) break;
            ItemStack current = slot.container().getItem(slot.index());
            if (current.isEmpty()) {
                int cap = Math.min(stack.getMaxStackSize(), slot.container().getMaxStackSize());
                int moved = Math.min(cap, stack.getCount());
                ItemStack placed = stack.copyWithCount(moved);
                slot.container().setItem(slot.index(), placed);
                stack.shrink(moved);
                slot.container().setChanged();
            }
        }
        return stack;
    }

    @Override
    public int getContainerSize() {
        return this.slots.size();
    }

    @Override
    public boolean isEmpty() {
        for (Slot slot : this.slots) {
            if (!slot.container().getItem(slot.index()).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int i) {
        if (i < 0 || i >= this.slots.size()) return ItemStack.EMPTY;
        Slot slot = this.slots.get(i);
        return slot.container().getItem(slot.index());
    }

    @Override
    public ItemStack removeItem(int i, int count) {
        if (i < 0 || i >= this.slots.size()) return ItemStack.EMPTY;
        Slot slot = this.slots.get(i);
        ItemStack result = slot.container().removeItem(slot.index(), count);
        slot.container().setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        if (i < 0 || i >= this.slots.size()) return ItemStack.EMPTY;
        Slot slot = this.slots.get(i);
        return slot.container().removeItemNoUpdate(slot.index());
    }

    @Override
    public void setItem(int i, ItemStack stack) {
        if (i < 0 || i >= this.slots.size()) return;
        Slot slot = this.slots.get(i);
        slot.container().setItem(slot.index(), stack);
        slot.container().setChanged();
    }

    @Override
    public void setChanged() {
        for (Slot slot : this.slots) slot.container().setChanged();
    }

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (Slot slot : this.slots) slot.container().setItem(slot.index(), ItemStack.EMPTY);
    }

    /** No single fixed position — delegates to the first backing block's location, or the world
     *  origin if this view is empty. Only exists to satisfy {@link Container}; nothing in this
     *  view's actual usage (script push/pull, PSI mirroring) reads it. */
    @Override
    public org.bukkit.Location getLocation() {
        if (this.slots.isEmpty()) return null;
        try { return this.slots.get(0).container().getLocation(); }
        catch (Throwable ignored) { return null; }
    }

    /** Applies to every backing container — this view has no max-stack-size of its own. */
    @Override
    public void setMaxStackSize(int size) {
        for (Slot slot : this.slots) slot.container().setMaxStackSize(size);
    }

    @Override
    public int getMaxStackSize() {
        int max = 64;
        for (Slot slot : this.slots) max = Math.min(max, slot.container().getMaxStackSize());
        return this.slots.isEmpty() ? 64 : max;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        int max = 64;
        for (Slot slot : this.slots) max = Math.min(max, slot.container().getMaxStackSize(stack));
        return this.slots.isEmpty() ? 64 : max;
    }

    // The remaining Container methods are all about a real player-facing GUI session (open/close
    // tracking, viewer lists, per-slot placement rules). This view is a synthetic aggregate never
    // opened as its own menu (scripts read/write it directly; the Portable Storage Interface opens
    // its OWN menu backed by this same view) — so these are harmless no-ops/pass-throughs rather
    // than delegating to any one backing block in particular.
    @Override
    public void startOpen(net.minecraft.world.entity.ContainerUser user) {}

    @Override
    public void stopOpen(net.minecraft.world.entity.ContainerUser user) {}

    @Override
    public List<net.minecraft.world.entity.ContainerUser> getEntitiesWithContainerOpen() {
        return List.of();
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack stack) {
        if (i < 0 || i >= this.slots.size()) return false;
        Slot slot = this.slots.get(i);
        return slot.container().canPlaceItem(slot.index(), stack);
    }

    @Override
    public boolean canTakeItem(Container target, int i, ItemStack stack) {
        if (i < 0 || i >= this.slots.size()) return true;
        Slot slot = this.slots.get(i);
        return slot.container().canTakeItem(target, slot.index(), stack);
    }

    // WorldlyContainer (face-aware) methods — vanilla hoppers cast straight to this interface for
    // ANY block they find a Container at, so implementing plain Container alone crashes them with
    // a ClassCastException instead of just failing to extract anything. This view has no single
    // "side" of its own (it aggregates many blocks' slots into one flat index space), so every face
    // exposes every slot; per-slot placement/take rules still delegate to whichever real
    // block/index that slot actually maps to (its own WorldlyContainer rules if it has any, else
    // permissive).
    @Override
    public int[] getSlotsForFace(Direction side) {
        int[] all = new int[this.slots.size()];
        for (int i = 0; i < all.length; i++) all[i] = i;
        return all;
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack stack, Direction direction) {
        if (i < 0 || i >= this.slots.size()) return false;
        Slot slot = this.slots.get(i);
        if (slot.container() instanceof WorldlyContainer wc) return wc.canPlaceItemThroughFace(slot.index(), stack, direction);
        return slot.container().canPlaceItem(slot.index(), stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack stack, Direction direction) {
        if (i < 0 || i >= this.slots.size()) return false;
        Slot slot = this.slots.get(i);
        if (slot.container() instanceof WorldlyContainer wc) return wc.canTakeItemThroughFace(slot.index(), stack, direction);
        return true;
    }

    @Override
    public List<ItemStack> getContents() {
        List<ItemStack> result = new ArrayList<>(this.slots.size());
        for (Slot slot : this.slots) result.add(slot.container().getItem(slot.index()));
        return result;
    }

    @Override
    public void onOpen(org.bukkit.craftbukkit.entity.CraftHumanEntity who) {}

    @Override
    public void onClose(org.bukkit.craftbukkit.entity.CraftHumanEntity who) {}

    @Override
    public List<org.bukkit.entity.HumanEntity> getViewers() {
        return List.of();
    }

    @Override
    public org.bukkit.inventory.InventoryHolder getOwner() {
        return null;
    }
}
