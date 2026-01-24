package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

public class HopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper {
    public static final int MOVE_ITEM_SPEED = 8;
    public static final int HOPPER_CONTAINER_SIZE = 5;
    private static final int[][] CACHED_SLOTS = new int[54][];
    private static final int NO_COOLDOWN_TIME = -1;
    private static final Component DEFAULT_NAME = Component.translatable("container.hopper");
    private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
    public int cooldownTime = -1;
    private long tickedGameTime;
    private Direction facing;

    // CraftBukkit start - add fields and methods
    public List<org.bukkit.entity.HumanEntity> transaction = new java.util.ArrayList<>();
    private int maxStack = MAX_STACK;

    @Override
    public List<ItemStack> getContents() {
        return this.items;
    }

    @Override
    public void onOpen(org.bukkit.craftbukkit.entity.CraftHumanEntity player) {
        this.transaction.add(player);
    }

    @Override
    public void onClose(org.bukkit.craftbukkit.entity.CraftHumanEntity player) {
        this.transaction.remove(player);
    }

    @Override
    public java.util.List<org.bukkit.entity.HumanEntity> getViewers() {
        return this.transaction;
    }

    @Override
    public int getMaxStackSize() {
        return this.maxStack;
    }

    public void setMaxStackSize(int size) {
        this.maxStack = size;
    }
    // CraftBukkit end

    public HopperBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityType.HOPPER, pos, blockState);
        this.facing = blockState.getValue(HopperBlock.FACING);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(input)) {
            ContainerHelper.loadAllItems(input, this.items);
        }

        this.cooldownTime = input.getIntOr("TransferCooldown", -1);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!this.trySaveLootTable(output)) {
            ContainerHelper.saveAllItems(output, this.items);
        }

        output.putInt("TransferCooldown", this.cooldownTime);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        this.unpackLootTable(null);
        return ContainerHelper.removeItem(this.getItems(), index, count);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.unpackLootTable(null);
        this.getItems().set(index, stack);
        stack.limitSize(this.getMaxStackSize(stack));
    }

    @Override
    public void setBlockState(BlockState blockState) {
        super.setBlockState(blockState);
        this.facing = blockState.getValue(HopperBlock.FACING);
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    public static void pushItemsTick(Level level, BlockPos pos, BlockState state, HopperBlockEntity blockEntity) {
        blockEntity.cooldownTime--;
        blockEntity.tickedGameTime = level.getGameTime();
        if (!blockEntity.isOnCooldown()) {
            blockEntity.setCooldown(0);
            // Spigot start
            boolean result = tryMoveItems(level, pos, state, blockEntity, () -> {
                return suckInItems(level, blockEntity);
            });
            if (!result && blockEntity.level.spigotConfig.hopperCheck > 1) {
                blockEntity.setCooldown(blockEntity.level.spigotConfig.hopperCheck);
            }
            // Spigot end
        }
    }

    // Paper start - Perf: Optimize Hoppers
    private static final int HOPPER_EMPTY = 0;
    private static final int HOPPER_HAS_ITEMS = 1;
    private static final int HOPPER_IS_FULL = 2;

    private static int getFullState(final HopperBlockEntity hopper) {
        hopper.unpackLootTable(null);

        final List<ItemStack> hopperItems = hopper.items;

        boolean empty = true;
        boolean full = true;

        for (int i = 0, len = hopperItems.size(); i < len; ++i) {
            final ItemStack stack = hopperItems.get(i);
            if (stack.isEmpty()) {
                full = false;
                continue;
            }

            if (!full) {
                // can't be full
                return HOPPER_HAS_ITEMS;
            }

            empty = false;

            if (stack.getCount() != stack.getMaxStackSize()) {
                // can't be full or empty
                return HOPPER_HAS_ITEMS;
            }
        }

        return empty ? HOPPER_EMPTY : (full ? HOPPER_IS_FULL : HOPPER_HAS_ITEMS);
    }
    // Paper end - Perf: Optimize Hoppers

    private static boolean tryMoveItems(Level level, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier validator) {
        if (level.isClientSide()) {
            return false;
        } else {
            if (!blockEntity.isOnCooldown() && state.getValue(HopperBlock.ENABLED)) {
                boolean flag = false;
                final int fullState = getFullState(blockEntity); // Paper - Perf: Optimize Hoppers
                if (fullState != HOPPER_EMPTY) { // Paper - Perf: Optimize Hoppers
                    flag = ejectItems(level, pos, blockEntity);
                }

                if (fullState != HOPPER_IS_FULL || flag) { // Paper - Perf: Optimize Hoppers
                    flag |= validator.getAsBoolean(); // Paper - note: this is not a validator, it's what adds/sucks in items
                }

                if (flag) {
                    blockEntity.setCooldown(level.spigotConfig.hopperTransfer); // Spigot
                    setChanged(level, pos, state);
                    return true;
                }
            }

            return false;
        }
    }

    private boolean inventoryFull() {
        for (ItemStack itemStack : this.items) {
            if (itemStack.isEmpty() || itemStack.getCount() != itemStack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    // Paper start - Perf: Optimize Hoppers
    public static boolean skipHopperEvents;
    private static boolean skipPullModeEventFire;
    private static boolean skipPushModeEventFire;

    private static boolean hopperPush(final Level level, final Container destination, final Direction direction, final HopperBlockEntity hopper) {
        skipPushModeEventFire = skipHopperEvents;
        boolean foundItem = false;
        for (int i = 0; i < hopper.getContainerSize(); ++i) {
            final ItemStack item = hopper.getItem(i);
            if (!item.isEmpty()) {
                foundItem = true;
                ItemStack origItemStack = item;
                ItemStack movedItem = origItemStack;

                final int originalItemCount = origItemStack.getCount();
                final int movedItemCount = Math.min(level.spigotConfig.hopperAmount, originalItemCount);
                origItemStack.setCount(movedItemCount);

                // We only need to fire the event once to give protection plugins a chance to cancel this event
                // Because nothing uses getItem, every event call should end up the same result.
                if (!skipPushModeEventFire) {
                    movedItem = callPushMoveEvent(destination, movedItem, hopper);
                    if (movedItem == null) { // cancelled
                        origItemStack.setCount(originalItemCount);
                        return false;
                    }
                }

                final ItemStack remainingItem = addItem(hopper, destination, movedItem, direction);
                final int remainingItemCount = remainingItem.getCount();
                if (remainingItemCount != movedItemCount) {
                    origItemStack = origItemStack.copy(true);
                    origItemStack.setCount(originalItemCount);
                    if (!origItemStack.isEmpty()) {
                        origItemStack.setCount(originalItemCount - movedItemCount + remainingItemCount);
                    }
                    hopper.setItem(i, origItemStack);
                    destination.setChanged();
                    return true;
                }
                origItemStack.setCount(originalItemCount);
            }
        }
        if (foundItem && level.paperConfig().hopper.cooldownWhenFull) { // Inventory was full - cooldown
            hopper.setCooldown(level.spigotConfig.hopperTransfer);
        }
        return false;
    }

    private static boolean hopperPull(final Level level, final Hopper hopper, final Container container, ItemStack origItemStack, final int i) {
        ItemStack movedItem = origItemStack;
        final int originalItemCount = origItemStack.getCount();
        final int movedItemCount = Math.min(level.spigotConfig.hopperAmount, originalItemCount);
        container.setChanged(); // original logic always marks source inv as changed even if no move happens.
        movedItem.setCount(movedItemCount);

        if (!skipPullModeEventFire) {
            movedItem = callPullMoveEvent(hopper, container, movedItem);
            if (movedItem == null) { // cancelled
                origItemStack.setCount(originalItemCount);
                // Drastically improve performance by returning true.
                // No plugin could have relied on the behavior of false as the other call
                // site for IMIE did not exhibit the same behavior
                return true;
            }
        }

        final ItemStack remainingItem = addItem(container, hopper, movedItem, null);
        final int remainingItemCount = remainingItem.getCount();
        if (remainingItemCount != movedItemCount) {
            origItemStack = origItemStack.copy(true);
            origItemStack.setCount(originalItemCount);
            if (!origItemStack.isEmpty()) {
                origItemStack.setCount(originalItemCount - movedItemCount + remainingItemCount);
            }

            ignoreBlockEntityUpdates = true;
            container.setItem(i, origItemStack);
            ignoreBlockEntityUpdates = false;
            container.setChanged();
            return true;
        }
        origItemStack.setCount(originalItemCount);

        if (level.paperConfig().hopper.cooldownWhenFull) {
            applyCooldown(hopper);
        }

        return false;
    }

    @Nullable
    private static ItemStack callPushMoveEvent(Container destination, ItemStack itemStack, HopperBlockEntity hopper) {
        final org.bukkit.inventory.Inventory destinationInventory = getInventory(destination);
        final io.papermc.paper.event.inventory.PaperInventoryMoveItemEvent event = new io.papermc.paper.event.inventory.PaperInventoryMoveItemEvent(
            hopper.getOwner(false).getInventory(),
            org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(itemStack),
            destinationInventory,
            true
        );
        final boolean result = event.callEvent();
        if (!event.calledGetItem && !event.calledSetItem) {
            skipPushModeEventFire = true;
        }
        if (!result) {
            applyCooldown(hopper);
            return null;
        }

        if (event.calledSetItem) {
            return org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(event.getItem());
        } else {
            return itemStack;
        }
    }

    @Nullable
    private static ItemStack callPullMoveEvent(final Hopper hopper, final Container container, final ItemStack itemstack) {
        final org.bukkit.inventory.Inventory sourceInventory = getInventory(container);
        final org.bukkit.inventory.Inventory destination = getInventory(hopper);

        // Mirror is safe as no plugins ever use this item
        final io.papermc.paper.event.inventory.PaperInventoryMoveItemEvent event = new io.papermc.paper.event.inventory.PaperInventoryMoveItemEvent(sourceInventory, org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(itemstack), destination, false);
        final boolean result = event.callEvent();
        if (!event.calledGetItem && !event.calledSetItem) {
            skipPullModeEventFire = true;
        }
        if (!result) {
            applyCooldown(hopper);
            return null;
        }

        if (event.calledSetItem) {
            return org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(event.getItem());
        } else {
            return itemstack;
        }
    }

    private static org.bukkit.inventory.Inventory getInventory(final Container container) {
        final org.bukkit.inventory.Inventory sourceInventory;
        if (container instanceof net.minecraft.world.CompoundContainer compoundContainer) {
            // Have to special-case large chests as they work oddly
            sourceInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest(compoundContainer);
        } else if (container instanceof BlockEntity blockEntity) {
            sourceInventory = blockEntity.getOwner(false).getInventory();
        } else if (container.getOwner() != null) {
            sourceInventory = container.getOwner().getInventory();
        } else {
            sourceInventory = new org.bukkit.craftbukkit.inventory.CraftInventory(container);
        }
        return sourceInventory;
    }

    private static void applyCooldown(final Hopper hopper) {
        if (hopper instanceof HopperBlockEntity blockEntity && blockEntity.getLevel() != null) {
            blockEntity.setCooldown(blockEntity.getLevel().spigotConfig.hopperTransfer);
        }
    }

    private static boolean allMatch(Container container, Direction direction, java.util.function.BiPredicate<ItemStack, Integer> test) {
        if (container instanceof WorldlyContainer) {
            for (int slot : ((WorldlyContainer) container).getSlotsForFace(direction)) {
                if (!test.test(container.getItem(slot), slot)) {
                    return false;
                }
            }
        } else {
            int size = container.getContainerSize();
            for (int slot = 0; slot < size; slot++) {
                if (!test.test(container.getItem(slot), slot)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean anyMatch(Container container, Direction direction, java.util.function.BiPredicate<ItemStack, Integer> test) {
        if (container instanceof WorldlyContainer) {
            for (int slot : ((WorldlyContainer) container).getSlotsForFace(direction)) {
                if (test.test(container.getItem(slot), slot)) {
                    return true;
                }
            }
        } else {
            int size = container.getContainerSize();
            for (int slot = 0; slot < size; slot++) {
                if (test.test(container.getItem(slot), slot)) {
                    return true;
                }
            }
        }
        return true;
    }
    private static final java.util.function.BiPredicate<ItemStack, Integer> STACK_SIZE_TEST = (itemStack, i) -> itemStack.getCount() >= itemStack.getMaxStackSize();
    private static final java.util.function.BiPredicate<ItemStack, Integer> IS_EMPTY_TEST = (itemStack, i) -> itemStack.isEmpty();
    // Paper end - Perf: Optimize Hoppers

    private static boolean ejectItems(Level level, BlockPos pos, HopperBlockEntity blockEntity) {
        Container attachedContainer = getAttachedContainer(level, pos, blockEntity);
        if (attachedContainer == null) {
            return false;
        } else {
            Direction opposite = blockEntity.facing.getOpposite();
            if (isFullContainer(attachedContainer, opposite)) {
                return false;
            } else {
                // Paper start - Perf: Optimize Hoppers
                return hopperPush(level, attachedContainer, opposite, blockEntity);
                //for (int i = 0; i < blockEntity.getContainerSize(); i++) {
                //    ItemStack item = blockEntity.getItem(i);
                //    if (!item.isEmpty()) {
                //        int count = item.getCount();
                //        // CraftBukkit start - Call event when pushing items into other inventories
                //        ItemStack original = item.copy();
                //        org.bukkit.craftbukkit.inventory.CraftItemStack oitemstack = org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(
                //            blockEntity.removeItem(i, level.spigotConfig.hopperAmount)
                //        ); // Spigot

                //        org.bukkit.inventory.Inventory destinationInventory;
                //        // Have to special case large chests as they work oddly
                //        if (attachedContainer instanceof final net.minecraft.world.CompoundContainer compoundContainer) {
                //            destinationInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest(compoundContainer);
                //        } else if (attachedContainer.getOwner() != null) {
                //            destinationInventory = attachedContainer.getOwner().getInventory();
                //        } else {
                //            destinationInventory = new org.bukkit.craftbukkit.inventory.CraftInventory(attachedContainer);
                //        }

                //        org.bukkit.event.inventory.InventoryMoveItemEvent event = new org.bukkit.event.inventory.InventoryMoveItemEvent(
                //            blockEntity.getOwner().getInventory(),
                //            oitemstack,
                //            destinationInventory,
                //            true
                //        );
                //        if (!event.callEvent()) {
                //            blockEntity.setItem(i, original);
                //            blockEntity.setCooldown(level.spigotConfig.hopperTransfer); // Delay hopper checks // Spigot
                //            return false;
                //        }
                //        int origCount = event.getItem().getAmount(); // Spigot
                //        ItemStack itemStack = HopperBlockEntity.addItem(blockEntity, attachedContainer, org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(event.getItem()), opposite);
                //        // CraftBukkit end

                //        if (itemStack.isEmpty()) {
                //            attachedContainer.setChanged();
                //            return true;
                //        }

                //        item.setCount(count);
                //        // Spigot start
                //        item.shrink(origCount - itemStack.getCount());
                //        if (count <= level.spigotConfig.hopperAmount) {
                //            // Spigot end
                //            blockEntity.setItem(i, item);
                //        }
                //    }
                //}

                //return false;
                // Paper end - Perf: Optimize Hoppers
            }
        }
    }

    private static int[] getSlots(Container container, Direction direction) {
        if (container instanceof WorldlyContainer worldlyContainer) {
            return worldlyContainer.getSlotsForFace(direction);
        } else {
            int containerSize = container.getContainerSize();
            if (containerSize < CACHED_SLOTS.length) {
                int[] ints = CACHED_SLOTS[containerSize];
                if (ints != null) {
                    return ints;
                } else {
                    int[] ints1 = createFlatSlots(containerSize);
                    CACHED_SLOTS[containerSize] = ints1;
                    return ints1;
                }
            } else {
                return createFlatSlots(containerSize);
            }
        }
    }

    private static int[] createFlatSlots(int size) {
        int[] ints = new int[size];
        int i = 0;

        while (i < ints.length) {
            ints[i] = i++;
        }

        return ints;
    }

    private static boolean isFullContainer(Container container, Direction direction) {
        int[] slots = getSlots(container, direction);

        for (int i : slots) {
            ItemStack item = container.getItem(i);
            if (item.getCount() < item.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    public static boolean suckInItems(Level level, Hopper hopper) {
        BlockPos blockPos = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY() + 1.0, hopper.getLevelZ());
        BlockState blockState = level.getBlockState(blockPos);
        Container sourceContainer = getSourceContainer(level, hopper, blockPos, blockState);
        if (sourceContainer != null) {
            Direction direction = Direction.DOWN;
            skipPullModeEventFire = skipHopperEvents; // Paper - Perf: Optimize Hoppers

            for (int i : getSlots(sourceContainer, direction)) {
                if (tryTakeInItemFromSlot(hopper, sourceContainer, i, direction, level)) { // Spigot
                    return true;
                }
            }

            return false;
        } else {
            boolean flag = hopper.isGridAligned() && blockState.isCollisionShapeFullBlock(level, blockPos) && !blockState.is(BlockTags.DOES_NOT_BLOCK_HOPPERS);
            if (!flag) {
                for (ItemEntity itemEntity : getItemsAtAndAbove(level, hopper)) {
                    if (addItem(hopper, itemEntity)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    private static boolean tryTakeInItemFromSlot(Hopper hopper, Container container, int slot, Direction direction, Level level) { // Spigot
        ItemStack item = container.getItem(slot);
        if (!item.isEmpty() && canTakeItemFromContainer(hopper, container, item, slot, direction)) {
            // Paper start - Perf: Optimize Hoppers
            return hopperPull(level, hopper, container, item, slot);
            //int count = item.getCount();
            //// CraftBukkit start - Call event on collection of items from inventories into the hopper
            //ItemStack original = item.copy();
            //org.bukkit.craftbukkit.inventory.CraftItemStack oitemstack = org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(
            //    container.removeItem(slot, level.spigotConfig.hopperAmount) // Spigot
            //);

            //org.bukkit.inventory.Inventory sourceInventory;
            //// Have to special case large chests as they work oddly
            //if (container instanceof final net.minecraft.world.CompoundContainer compoundContainer) {
            //    sourceInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest(compoundContainer);
            //} else if (container.getOwner() != null) {
            //    sourceInventory = container.getOwner().getInventory();
            //} else {
            //    sourceInventory = new org.bukkit.craftbukkit.inventory.CraftInventory(container);
            //}

            //org.bukkit.event.inventory.InventoryMoveItemEvent event = new org.bukkit.event.inventory.InventoryMoveItemEvent(
            //    sourceInventory,
            //    oitemstack,
            //    hopper.getOwner().getInventory(),
            //    false
            //);

            //if (!event.callEvent()) {
            //    container.setItem(slot, original);

            //    if (hopper instanceof final HopperBlockEntity hopperBlockEntity) {
            //        hopperBlockEntity.setCooldown(level.spigotConfig.hopperTransfer); // Spigot
            //    }

            //    return false;
            //}
            //int origCount = event.getItem().getAmount(); // Spigot
            //ItemStack itemStack = HopperBlockEntity.addItem(container, hopper, org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(event.getItem()), null);
            //// CraftBukkit end

            //if (itemStack.isEmpty()) {
            //    container.setChanged();
            //    return true;
            //}

            //item.setCount(count);
            //// Spigot start
            //item.shrink(origCount - itemStack.getCount());
            //if (count <= level.spigotConfig.hopperAmount) {
            //    // Spigot end
            //    container.setItem(slot, item);
            //}
            // Paper end - Perf: Optimize Hoppers
        }

        return false;
    }

    public static boolean addItem(Container container, ItemEntity item) {
        boolean flag = false;
        // CraftBukkit start
        if (org.bukkit.event.inventory.InventoryPickupItemEvent.getHandlerList().getRegisteredListeners().length > 0) { // Paper - optimize hoppers
        org.bukkit.event.inventory.InventoryPickupItemEvent event = new org.bukkit.event.inventory.InventoryPickupItemEvent(
            getInventory(container), (org.bukkit.entity.Item) item.getBukkitEntity() // Paper - Perf: Optimize Hoppers; use getInventory() to avoid snapshot creation
        );
        if (!event.callEvent()) {
            return false;
        }
        // CraftBukkit end
        } // Paper - Perf: Optimize Hoppers
        ItemStack itemStack = item.getItem().copy();
        ItemStack itemStack1 = addItem(null, container, itemStack, null);
        if (itemStack1.isEmpty()) {
            flag = true;
            item.setItem(ItemStack.EMPTY);
            item.discard(org.bukkit.event.entity.EntityRemoveEvent.Cause.PICKUP); // CraftBukkit - add Bukkit remove cause
        } else {
            item.setItem(itemStack1);
        }

        return flag;
    }

    public static ItemStack addItem(@Nullable Container source, Container destination, ItemStack stack, @Nullable Direction direction) {
        if (destination instanceof WorldlyContainer worldlyContainer && direction != null) {
            int[] slotsForFace = worldlyContainer.getSlotsForFace(direction);

            for (int i = 0; i < slotsForFace.length && !stack.isEmpty(); i++) {
                stack = tryMoveInItem(source, destination, stack, slotsForFace[i], direction);
            }
        } else {
            int containerSize = destination.getContainerSize();

            for (int i = 0; i < containerSize && !stack.isEmpty(); i++) {
                stack = tryMoveInItem(source, destination, stack, i, direction);
            }
        }

        return stack;
    }

    private static boolean canPlaceItemInContainer(Container container, ItemStack stack, int slot, @Nullable Direction direction) {
        return container.canPlaceItem(slot, stack)
            && !(container instanceof WorldlyContainer worldlyContainer && !worldlyContainer.canPlaceItemThroughFace(slot, stack, direction));
    }

    private static boolean canTakeItemFromContainer(Container source, Container destination, ItemStack stack, int slot, Direction direction) {
        return destination.canTakeItem(source, slot, stack)
            && !(destination instanceof WorldlyContainer worldlyContainer && !worldlyContainer.canTakeItemThroughFace(slot, stack, direction));
    }

    private static ItemStack tryMoveInItem(@Nullable Container source, Container destination, ItemStack stack, int slot, @Nullable Direction direction) {
        ItemStack item = destination.getItem(slot);
        if (canPlaceItemInContainer(destination, stack, slot, direction)) {
            boolean flag = false;
            boolean isEmpty = destination.isEmpty();
            if (item.isEmpty()) {
                // Spigot start - SPIGOT-6693, SimpleContainer#setItem
                ItemStack leftover = ItemStack.EMPTY; // Paper - Make hoppers respect inventory max stack size
                if (!stack.isEmpty() && stack.getCount() > destination.getMaxStackSize()) {
                    leftover = stack; // Paper - Make hoppers respect inventory max stack size
                    stack = stack.split(destination.getMaxStackSize());
                }
                // Spigot end
                ignoreBlockEntityUpdates = true; // Paper - Perf: Optimize Hoppers
                destination.setItem(slot, stack);
                ignoreBlockEntityUpdates = false; // Paper - Perf: Optimize Hoppers
                stack = leftover; // Paper - Make hoppers respect inventory max stack size
                flag = true;
            } else if (canMergeItems(item, stack)) {
                int i = Math.min(stack.getMaxStackSize(), destination.getMaxStackSize()) - item.getCount(); // Paper - Make hoppers respect inventory max stack size
                int min = Math.min(stack.getCount(), i);
                stack.shrink(min);
                item.grow(min);
                flag = min > 0;
            }

            if (flag) {
                if (isEmpty && destination instanceof HopperBlockEntity hopperBlockEntity && !hopperBlockEntity.isOnCustomCooldown()) {
                    int min = 0;
                    if (source instanceof HopperBlockEntity hopperBlockEntity1 && hopperBlockEntity.tickedGameTime >= hopperBlockEntity1.tickedGameTime) {
                        min = 1;
                    }

                    hopperBlockEntity.setCooldown(hopperBlockEntity.level.spigotConfig.hopperTransfer - min); // Spigot
                }

                destination.setChanged();
            }
        }

        return stack;
    }

    // CraftBukkit start
    private static @Nullable Container runHopperInventorySearchEvent(
        Container container,
        org.bukkit.craftbukkit.block.CraftBlock hopper,
        org.bukkit.craftbukkit.block.CraftBlock searchLocation,
        org.bukkit.event.inventory.HopperInventorySearchEvent.ContainerType containerType
    ) {
        org.bukkit.event.inventory.HopperInventorySearchEvent event = new org.bukkit.event.inventory.HopperInventorySearchEvent(
            (container != null) ? new org.bukkit.craftbukkit.inventory.CraftInventory(container) : null,
            containerType,
            hopper,
            searchLocation
        );
        event.callEvent();
        return (event.getInventory() != null) ? ((org.bukkit.craftbukkit.inventory.CraftInventory) event.getInventory()).getInventory() : null;
    }
    // CraftBukkit end

    private static @Nullable Container getAttachedContainer(Level level, BlockPos pos, HopperBlockEntity blockEntity) {
        // Paper start
        BlockPos searchPosition = pos.relative(blockEntity.facing);
        Container inventory = getContainerAt(level, searchPosition);
        if (org.bukkit.event.inventory.HopperInventorySearchEvent.getHandlerList().getRegisteredListeners().length == 0) return inventory;

        org.bukkit.craftbukkit.block.CraftBlock hopper = org.bukkit.craftbukkit.block.CraftBlock.at(level, pos);
        org.bukkit.craftbukkit.block.CraftBlock searchBlock = org.bukkit.craftbukkit.block.CraftBlock.at(level, searchPosition);
        return HopperBlockEntity.runHopperInventorySearchEvent(
            inventory,
            hopper,
            searchBlock,
            org.bukkit.event.inventory.HopperInventorySearchEvent.ContainerType.DESTINATION
        );
        // Paper end
    }

    private static @Nullable Container getSourceContainer(Level level, Hopper hopper, BlockPos pos, BlockState state) {
        // Paper start
        final Container inventory = HopperBlockEntity.getContainerAt(level, pos, state, hopper.getLevelX(), hopper.getLevelY() + 1.0D, hopper.getLevelZ());
        if (org.bukkit.event.inventory.HopperInventorySearchEvent.getHandlerList().getRegisteredListeners().length == 0) return inventory;

        final BlockPos hopperPos = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY(), hopper.getLevelZ());
        org.bukkit.craftbukkit.block.CraftBlock hopperBlock = org.bukkit.craftbukkit.block.CraftBlock.at(level, hopperPos);
        org.bukkit.craftbukkit.block.CraftBlock containerBlock = org.bukkit.craftbukkit.block.CraftBlock.at(level, hopperPos.above());
        return HopperBlockEntity.runHopperInventorySearchEvent(
            inventory,
            hopperBlock,
            containerBlock,
            org.bukkit.event.inventory.HopperInventorySearchEvent.ContainerType.SOURCE
        );
        // Paper end
    }

    public static List<ItemEntity> getItemsAtAndAbove(Level level, Hopper hopper) {
        AABB aabb = hopper.getSuckAabb().move(hopper.getLevelX() - 0.5, hopper.getLevelY() - 0.5, hopper.getLevelZ() - 0.5);
        return level.getEntitiesOfClass(ItemEntity.class, aabb, EntitySelector.ENTITY_STILL_ALIVE);
    }

    public static @Nullable Container getContainerAt(Level level, BlockPos pos) {
        return getContainerAt(level, pos, level.getBlockState(pos), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, true); // Paper - Optimize hoppers
    }

    private static @Nullable Container getContainerAt(Level level, BlockPos pos, BlockState state, double x, double y, double z) {
        // Paper start - Perf: Optimize Hoppers
        return HopperBlockEntity.getContainerAt(level, pos, state, x, y, z, false);
    }
    private static @Nullable Container getContainerAt(Level level, BlockPos pos, BlockState state, double x, double y, double z, final boolean optimizeEntities) {
        // Paper end - Perf: Optimize Hoppers
        Container blockContainer = getBlockContainer(level, pos, state);
        if (blockContainer == null && (!optimizeEntities || !level.paperConfig().hopper.ignoreOccludingBlocks || !state.getBukkitMaterial().isOccluding())) { // Paper - Perf: Optimize Hoppers
            blockContainer = getEntityContainer(level, x, y, z);
        }

        return blockContainer;
    }

    private static @Nullable Container getBlockContainer(Level level, BlockPos pos, BlockState state) {
        if (!level.spigotConfig.hopperCanLoadChunks && !level.hasChunkAt(pos)) return null; // Spigot
        Block block = state.getBlock();
        if (block instanceof WorldlyContainerHolder) {
            return ((WorldlyContainerHolder)block).getContainer(state, level, pos);
        } else if (state.hasBlockEntity() && level.getBlockEntity(pos) instanceof Container container) {
            if (container instanceof ChestBlockEntity && block instanceof ChestBlock) {
                container = ChestBlock.getContainer((ChestBlock)block, state, level, pos, true);
            }

            return container;
        } else {
            return null;
        }
    }

    private static @Nullable Container getEntityContainer(Level level, double x, double y, double z) {
        List<Entity> entities = level.getEntitiesOfClass(
            (Class) Container.class, new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntitySelector.CONTAINER_ENTITY_SELECTOR // Paper - Perf: Optimize hoppers
        );
        return !entities.isEmpty() ? (Container)entities.get(level.random.nextInt(entities.size())) : null;
    }

    private static boolean canMergeItems(ItemStack stack1, ItemStack stack2) {
        return stack1.getCount() < stack1.getMaxStackSize() && ItemStack.isSameItemSameComponents(stack1, stack2); // Paper - Perf: Optimize Hoppers; used to return true for full itemstacks?!
    }

    @Override
    public double getLevelX() {
        return this.worldPosition.getX() + 0.5;
    }

    @Override
    public double getLevelY() {
        return this.worldPosition.getY() + 0.5;
    }

    @Override
    public double getLevelZ() {
        return this.worldPosition.getZ() + 0.5;
    }

    @Override
    public boolean isGridAligned() {
        return true;
    }

    public void setCooldown(int cooldownTime) {
        this.cooldownTime = cooldownTime;
    }

    private boolean isOnCooldown() {
        return this.cooldownTime > 0;
    }

    private boolean isOnCustomCooldown() {
        return this.cooldownTime > 8;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    public static void entityInside(Level level, BlockPos pos, BlockState state, Entity entity, HopperBlockEntity blockEntity) {
        if (entity instanceof ItemEntity itemEntity
            && !itemEntity.getItem().isEmpty()
            && entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ()).intersects(blockEntity.getSuckAabb())) {
            tryMoveItems(level, pos, state, blockEntity, () -> addItem(blockEntity, itemEntity));
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new HopperMenu(id, player, this);
    }
}
