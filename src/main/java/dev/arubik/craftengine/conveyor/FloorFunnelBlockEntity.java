/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.Container
 *  net.minecraft.world.WorldlyContainer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.HopperBlockEntity
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker
 *  net.momirealms.craftengine.core.util.Direction
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Item
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package dev.arubik.craftengine.conveyor;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.conveyor.ConveyorReceiver;
import dev.arubik.craftengine.util.TypedKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.world.CEWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class FloorFunnelBlockEntity
extends PersistentBlockEntity
implements ConveyorReceiver {
    private static final double PICKUP_RADIUS = 0.65;
    private static final double PICKUP_HEIGHT = 0.9;
    private ItemStack held;
    private final boolean pullAbove;
    private boolean loaded = false;
    private boolean dirty = false;

    public FloorFunnelBlockEntity(BlockEntity blockEntity) {
        this(blockEntity, false);
    }

    public FloorFunnelBlockEntity(BlockEntity blockEntity, boolean pullAbove) {
        super(blockEntity);
        this.pullAbove = pullAbove;
    }

    @Override
    public boolean isFull() {
        return this.held != null && !this.held.getType().isAir();
    }

    @Override
    public boolean receiveConveyorItem(ItemStack stack, net.momirealms.craftengine.core.util.Direction sourceFacing) {
        return this.receiveConveyorItem(stack, sourceFacing, 0.0f);
    }

    @Override
    public boolean receiveConveyorItem(ItemStack stack, net.momirealms.craftengine.core.util.Direction sourceFacing, float jitter) {
        this.ensureLoaded();
        if (stack == null || stack.getType().isAir()) {
            return false;
        }
        if (this.isFull()) {
            return false;
        }
        this.held = stack.clone();
        this.dirty = true;
        return true;
    }

    private Container containerAbove() {
        try {
            Level lvl = (Level)this.blockEntity().world().world.minecraftWorld();
            net.momirealms.craftengine.core.world.BlockPos p = this.blockEntity().pos();
            BlockPos np = new BlockPos(p.x(), p.y() + 1, p.z());
            return HopperBlockEntity.getContainerAt((Level)lvl, (BlockPos)np);
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static int[] downFaceSlots(Container c) {
        if (c instanceof WorldlyContainer) {
            WorldlyContainer wc = (WorldlyContainer)c;
            return wc.getSlotsForFace(Direction.DOWN);
        }
        int[] all = new int[c.getContainerSize()];
        for (int i = 0; i < all.length; ++i) {
            all[i] = i;
        }
        return all;
    }

    private void pullFromContainerAbove() {
        if (this.isFull()) {
            return;
        }
        Container c = this.containerAbove();
        if (c == null) {
            return;
        }
        Direction face = Direction.DOWN;
        for (int slot : FloorFunnelBlockEntity.downFaceSlots(c)) {
            int take;
            net.minecraft.world.item.ItemStack taken;
            WorldlyContainer wc;
            net.minecraft.world.item.ItemStack s = c.getItem(slot);
            if (s.isEmpty() || c instanceof WorldlyContainer && !(wc = (WorldlyContainer)c).canTakeItemThroughFace(slot, s, face) || (taken = c.removeItem(slot, take = s.getCount())).isEmpty()) continue;
            this.held = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)taken);
            c.setChanged();
            this.dirty = true;
            return;
        }
    }

    private Container containerBelow() {
        try {
            Level lvl = (Level)this.blockEntity().world().world.minecraftWorld();
            net.momirealms.craftengine.core.world.BlockPos p = this.blockEntity().pos();
            BlockPos np = new BlockPos(p.x(), p.y() - 1, p.z());
            return HopperBlockEntity.getContainerAt((Level)lvl, (BlockPos)np);
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static int[] upFaceSlots(Container c) {
        if (c instanceof WorldlyContainer) {
            WorldlyContainer wc = (WorldlyContainer)c;
            return wc.getSlotsForFace(Direction.UP);
        }
        int[] all = new int[c.getContainerSize()];
        for (int i = 0; i < all.length; ++i) {
            all[i] = i;
        }
        return all;
    }

    private boolean depositBelow() {
        Container c = this.containerBelow();
        if (c == null) {
            return false;
        }
        Direction face = Direction.UP;
        boolean moved = false;
        int remaining = this.held.getAmount();
        for (int slot : FloorFunnelBlockEntity.upFaceSlots(c)) {
            int room;
            WorldlyContainer wc;
            if (remaining <= 0) break;
            net.minecraft.world.item.ItemStack one = CraftItemStack.asNMSCopy((ItemStack)this.held);
            one.setCount(1);
            if (c instanceof WorldlyContainer && !(wc = (WorldlyContainer)c).canPlaceItemThroughFace(slot, one, face) || !c.canPlaceItem(slot, one)) continue;
            net.minecraft.world.item.ItemStack cur = c.getItem(slot);
            int max = Math.min(one.getMaxStackSize(), c.getMaxStackSize());
            if (cur.isEmpty()) {
                int put = Math.min(remaining, max);
                net.minecraft.world.item.ItemStack ins = one.copy();
                ins.setCount(put);
                c.setItem(slot, ins);
                remaining -= put;
                moved = true;
                continue;
            }
            if (!net.minecraft.world.item.ItemStack.isSameItemSameComponents((net.minecraft.world.item.ItemStack)cur, (net.minecraft.world.item.ItemStack)one) || (room = max - cur.getCount()) <= 0) continue;
            int put = Math.min(remaining, room);
            cur.grow(put);
            remaining -= put;
            moved = true;
        }
        if (moved) {
            c.setChanged();
            this.held.setAmount(remaining);
            if (remaining <= 0) {
                this.held = null;
            }
            this.dirty = true;
        }
        return moved;
    }

    private void dropBelow() {
        try {
            if (this.held == null) {
                return;
            }
            World bw = (World)this.blockEntity().world().world.platformWorld();
            net.momirealms.craftengine.core.world.BlockPos p = this.blockEntity().pos();
            Item it = bw.dropItem(new Location(bw, (double)p.x() + 0.5, (double)p.y() - 0.25, (double)p.z() + 0.5), this.held);
            it.setVelocity(new Vector(0.0, -0.05, 0.0));
            it.setPickupDelay(10);
            this.held = null;
            this.dirty = true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void vacuumAbove() {
        if (this.isFull()) {
            return;
        }
        try {
            World bw = (World)this.blockEntity().world().world.platformWorld();
            net.momirealms.craftengine.core.world.BlockPos p = this.blockEntity().pos();
            Location center = new Location(bw, (double)p.x() + 0.5, (double)p.y() + 0.5, (double)p.z() + 0.5);
            for (Entity e : bw.getNearbyEntities(center, 0.65, 0.9, 0.65)) {
                ItemStack bukkit;
                Item it;
                if (!(e instanceof Item) || (it = (Item)e).isDead() || !it.isValid() || it.getPickupDelay() > 0 || (bukkit = it.getItemStack()) == null || bukkit.getType().isAir()) continue;
                this.held = bukkit.clone();
                it.remove();
                this.dirty = true;
                return;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(CEWorld world, ImmutableBlockState state) {
        return BlockEntityController.createTickerHelper(FloorFunnelBlockEntity::tick);
    }

    public static void tick(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, ImmutableBlockState state, FloorFunnelBlockEntity self) {
        self.serverTick(world);
    }

    private void serverTick(CEWorld world) {
        this.ensureLoaded();
        if (this.held == null || this.held.getType().isAir()) {
            this.held = null;
            this.vacuumAbove();
            if (this.pullAbove && (this.held == null || this.held.getType().isAir())) {
                this.pullFromContainerAbove();
            }
        }
        if (this.held != null && !this.held.getType().isAir()) {
            if (this.pullAbove) {
                this.dropBelow();
            } else if (!this.depositBelow() && this.containerBelow() == null) {
                this.dropBelow();
            }
        }
        if (this.dirty) {
            this.dirty = false;
            this.save();
        }
    }

    public ItemStack takeHeld() {
        this.ensureLoaded();
        if (this.held == null || this.held.getType().isAir()) {
            return null;
        }
        ItemStack out = this.held;
        this.held = null;
        this.dirty = true;
        this.save();
        return out;
    }

    public void dropHeld() {
        try {
            this.ensureLoaded();
            if (this.held != null && !this.held.getType().isAir()) {
                World bw = (World)this.blockEntity().world().world.platformWorld();
                net.momirealms.craftengine.core.world.BlockPos p = this.blockEntity().pos();
                bw.dropItem(new Location(bw, (double)p.x() + 0.5, (double)p.y() + 0.5, (double)p.z() + 0.5), this.held);
                this.held = null;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void ensureLoaded() {
        if (!this.loaded) {
            this.load();
            this.loaded = true;
        }
    }

    private void load() {
        try {
            net.minecraft.world.item.ItemStack nms = this.getOptional(TypedKeys.NMS_ITEM).orElse(null);
            this.held = nms != null && !nms.isEmpty() ? CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)nms) : null;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void save() {
        try {
            if (this.held == null || this.held.getType().isAir()) {
                this.clear();
            } else {
                this.set(TypedKeys.NMS_ITEM, CraftItemStack.asNMSCopy((ItemStack)this.held));
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

