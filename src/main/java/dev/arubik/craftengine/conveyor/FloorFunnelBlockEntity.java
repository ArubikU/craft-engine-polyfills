package dev.arubik.craftengine.conveyor;

import org.bukkit.craftbukkit.inventory.CraftItemStack;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;

/**
 * FLOOR FUNNEL controller ({@code polyfills:floor_funnel}). A receive-only, instant hopper.
 *
 * <p>Holds exactly ONE internal stack. It accepts a whole stack at once and will not take
 * any more items while it still holds one. Each tick it deposits the held stack straight
 * DOWN into a worldly container (respecting the container's DOWN face rules), semi-instantly:
 * if the container only has room for part of the stack it deposits that part and keeps the
 * rest until space frees. With no container below, the held stack is dropped into the world.
 * If a container below is full, the remainder is kept until the block is broken (drops it)
 * or a player right-clicks it with an empty hand (extracts it).</p>
 *
 * <p>Item intake: belts hand items over directly via {@link ConveyorReceiver} (no drop),
 * dropped items resting on top are vacuumed, and hoppers/droppers above push in through the
 * vanilla container bridge ({@link FloorFunnelBehavior} exposes none here — top intake is the
 * vacuum + belt hand-off). Persistence uses chunk-backed CustomBlockData.</p>
 */
public class FloorFunnelBlockEntity extends PersistentBlockEntity implements ConveyorReceiver {

    // Horizontal reach. The funnel is a THIN floor plate, so items rest near the block's own
    // surface (y ~ +0.0..+0.5), not a full block above — keep the vacuum box low and tall enough.
    private static final double PICKUP_RADIUS = 0.65;
    private static final double PICKUP_HEIGHT = 0.9; // vertical half-extent around the block top

    private org.bukkit.inventory.ItemStack held; // the single held stack (or null)

    // CEILING variant: also EXTRACT a stack from the worldly container directly ABOVE (through its
    // DOWN face). The FLOOR variant leaves this false — it only takes belt hand-offs + vacuumed
    // drops and never pulls from a chest above (so a chest on top isn't auto-drained by a floor plate).
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

    // ---------------- ConveyorReceiver (belt hand-off) ----------------

    @Override
    public boolean isFull() {
        // Cannot take a new item while it still holds a (non-empty) stack.
        return held != null && !held.getType().isAir();
    }

    @Override
    public boolean receiveConveyorItem(org.bukkit.inventory.ItemStack stack, Direction sourceFacing) {
        return receiveConveyorItem(stack, sourceFacing, 0f);
    }

    @Override
    public boolean receiveConveyorItem(org.bukkit.inventory.ItemStack stack, Direction sourceFacing, float jitter) {
        ensureLoaded();
        if (stack == null || stack.getType().isAir())
            return false;
        if (isFull())
            return false;
        this.held = stack.clone();
        this.dirty = true;
        return true;
    }

    // ---------------- container below ----------------

    /** The NMS container directly ABOVE us (vanilla + CraftEngine worldly containers). */
    private net.minecraft.world.Container containerAbove() {
        try {
            net.minecraft.world.level.Level lvl = (net.minecraft.world.level.Level) blockEntity().world().world
                    .minecraftWorld();
            BlockPos p = blockEntity().pos();
            net.minecraft.core.BlockPos np = new net.minecraft.core.BlockPos(p.x(), p.y() + 1, p.z());
            return net.minecraft.world.level.block.entity.HopperBlockEntity.getContainerAt(lvl, np);
        } catch (Throwable t) {
            return null;
        }
    }

    /** Slots accessible from the DOWN face (worldly-aware), or all slots for a plain container. */
    private static int[] downFaceSlots(net.minecraft.world.Container c) {
        if (c instanceof net.minecraft.world.WorldlyContainer wc)
            return wc.getSlotsForFace(net.minecraft.core.Direction.DOWN);
        int[] all = new int[c.getContainerSize()];
        for (int i = 0; i < all.length; i++)
            all[i] = i;
        return all;
    }

    /** Extract ONE whole stack from the container above (through its DOWN face) into the held slot. */
    private void pullFromContainerAbove() {
        if (isFull())
            return;
        net.minecraft.world.Container c = containerAbove();
        if (c == null)
            return;
        net.minecraft.core.Direction face = net.minecraft.core.Direction.DOWN;
        for (int slot : downFaceSlots(c)) {
            net.minecraft.world.item.ItemStack s = c.getItem(slot);
            if (s.isEmpty())
                continue;
            if (c instanceof net.minecraft.world.WorldlyContainer wc && !wc.canTakeItemThroughFace(slot, s, face))
                continue;
            int take = s.getCount();
            net.minecraft.world.item.ItemStack taken = c.removeItem(slot, take);
            if (taken.isEmpty())
                continue;
            this.held = CraftItemStack.asBukkitCopy(taken);
            c.setChanged();
            this.dirty = true;
            return;
        }
    }

    /** The NMS container directly below us (vanilla + CraftEngine worldly containers). */
    private net.minecraft.world.Container containerBelow() {
        try {
            net.minecraft.world.level.Level lvl = (net.minecraft.world.level.Level) blockEntity().world().world
                    .minecraftWorld();
            BlockPos p = blockEntity().pos();
            net.minecraft.core.BlockPos np = new net.minecraft.core.BlockPos(p.x(), p.y() - 1, p.z());
            return net.minecraft.world.level.block.entity.HopperBlockEntity.getContainerAt(lvl, np);
        } catch (Throwable t) {
            return null;
        }
    }

    /** Slots accessible from the UP face (worldly-aware), or all slots for a plain container. */
    private static int[] upFaceSlots(net.minecraft.world.Container c) {
        if (c instanceof net.minecraft.world.WorldlyContainer wc)
            return wc.getSlotsForFace(net.minecraft.core.Direction.UP);
        int[] all = new int[c.getContainerSize()];
        for (int i = 0; i < all.length; i++)
            all[i] = i;
        return all;
    }

    /**
     * Push as much of {@code held} as fits into the container below (face = UP). Mutates
     * {@code held}'s amount down by however many were deposited. Returns true if anything moved.
     */
    private boolean depositBelow() {
        net.minecraft.world.Container c = containerBelow();
        if (c == null)
            return false;
        net.minecraft.core.Direction face = net.minecraft.core.Direction.UP;
        boolean moved = false;
        int remaining = held.getAmount();
        for (int slot : upFaceSlots(c)) {
            if (remaining <= 0)
                break;
            net.minecraft.world.item.ItemStack one = CraftItemStack.asNMSCopy(held);
            one.setCount(1);
            if (c instanceof net.minecraft.world.WorldlyContainer wc && !wc.canPlaceItemThroughFace(slot, one, face))
                continue;
            if (!c.canPlaceItem(slot, one))
                continue;
            net.minecraft.world.item.ItemStack cur = c.getItem(slot);
            int max = Math.min(one.getMaxStackSize(), c.getMaxStackSize());
            if (cur.isEmpty()) {
                int put = Math.min(remaining, max);
                net.minecraft.world.item.ItemStack ins = one.copy();
                ins.setCount(put);
                c.setItem(slot, ins);
                remaining -= put;
                moved = true;
            } else if (net.minecraft.world.item.ItemStack.isSameItemSameComponents(cur, one)) {
                int room = max - cur.getCount();
                if (room <= 0)
                    continue;
                int put = Math.min(remaining, room);
                cur.grow(put);
                remaining -= put;
                moved = true;
            }
        }
        if (moved) {
            c.setChanged();
            held.setAmount(remaining);
            if (remaining <= 0)
                held = null;
            this.dirty = true;
        }
        return moved;
    }

    /** Drop the whole held stack into the world directly below the funnel. */
    private void dropBelow() {
        try {
            if (held == null)
                return;
            org.bukkit.World bw = (org.bukkit.World) blockEntity().world().world.platformWorld();
            BlockPos p = blockEntity().pos();
            org.bukkit.entity.Item it = bw.dropItem(
                    new org.bukkit.Location(bw, p.x() + 0.5, p.y() - 0.25, p.z() + 0.5), held);
            it.setVelocity(new org.bukkit.util.Vector(0, -0.05, 0));
            it.setPickupDelay(10);
            held = null;
            this.dirty = true;
        } catch (Throwable ignored) {
        }
    }

    /** Vacuum a dropped item resting on top into the held slot (only while empty). */
    private void vacuumAbove() {
        if (isFull())
            return;
        try {
            org.bukkit.World bw = (org.bukkit.World) blockEntity().world().world.platformWorld();
            BlockPos p = blockEntity().pos();
            org.bukkit.Location center = new org.bukkit.Location(bw, p.x() + 0.5, p.y() + 0.5, p.z() + 0.5);
            for (org.bukkit.entity.Entity e : bw.getNearbyEntities(center, PICKUP_RADIUS, PICKUP_HEIGHT,
                    PICKUP_RADIUS)) {
                if (!(e instanceof org.bukkit.entity.Item it))
                    continue;
                if (it.isDead() || !it.isValid() || it.getPickupDelay() > 0)
                    continue;
                org.bukkit.inventory.ItemStack bukkit = it.getItemStack();
                if (bukkit == null || bukkit.getType().isAir())
                    continue;
                this.held = bukkit.clone();
                it.remove();
                this.dirty = true;
                return;
            }
        } catch (Throwable ignored) {
        }
    }

    // ---------------- ticking ----------------

    @Override
    @SuppressWarnings("unchecked")
    public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(
            CEWorld world, ImmutableBlockState state) {
        return BlockEntityController.createTickerHelper(
                (BlockEntityTicker<FloorFunnelBlockEntity>) FloorFunnelBlockEntity::tick);
    }

    public static void tick(CEWorld world, BlockPos pos, ImmutableBlockState state, FloorFunnelBlockEntity self) {
        self.serverTick(world);
    }

    private void serverTick(CEWorld world) {
        ensureLoaded();

        // Try to take a fresh stack only when empty: vacuum dropped items, and (CEILING only) also
        // extract from a worldly container directly above.
        if (held == null || held.getType().isAir()) {
            held = null;
            vacuumAbove();
            if (pullAbove && (held == null || held.getType().isAir()))
                pullFromContainerAbove();
        }

        if (held != null && !held.getType().isAir()) {
            if (pullAbove) {
                // CEILING: it only EXTRACTS (from above) and EJECTS below — it never inserts into a
                // container. Always drop the held stack below (onto a belt / floor funnel / the world).
                // Inserting into containers is the FLOOR funnel's job.
                dropBelow();
            } else if (!depositBelow()) {
                // FLOOR: nothing moved this tick — either no container OR a full one.
                if (containerBelow() == null)
                    dropBelow(); // no container -> drop the whole stack below
                // full container -> keep the remainder (until break / right-click)
            }
        }

        if (dirty) {
            dirty = false;
            save();
        }
    }

    // ---------------- player / break access ----------------

    /** Take the held stack out (right-click empty hand). */
    public org.bukkit.inventory.ItemStack takeHeld() {
        ensureLoaded();
        if (held == null || held.getType().isAir())
            return null;
        org.bukkit.inventory.ItemStack out = held;
        held = null;
        dirty = true;
        save();
        return out;
    }

    /** Drop the held stack at the funnel on break. */
    public void dropHeld() {
        try {
            ensureLoaded();
            if (held != null && !held.getType().isAir()) {
                org.bukkit.World bw = (org.bukkit.World) blockEntity().world().world.platformWorld();
                BlockPos p = blockEntity().pos();
                bw.dropItem(new org.bukkit.Location(bw, p.x() + 0.5, p.y() + 0.5, p.z() + 0.5), held);
                held = null;
            }
        } catch (Throwable ignored) {
        }
    }

    // ---------------- persistence (chunk-backed CustomBlockData) ----------------

    private void ensureLoaded() {
        if (!loaded) {
            load();
            loaded = true;
        }
    }

    private dev.arubik.craftengine.util.CustomBlockData blockData() {
        try {
            org.bukkit.World bw = (org.bukkit.World) blockEntity().world().world.platformWorld();
            BlockPos p = blockEntity().pos();
            return dev.arubik.craftengine.util.CustomBlockData.from(bw.getBlockAt(p.x(), p.y(), p.z()));
        } catch (Throwable t) {
            return null;
        }
    }

    private static final dev.arubik.craftengine.util.TypedKey<org.bukkit.inventory.ItemStack> KEY_ITEM =
            dev.arubik.craftengine.util.TypedKeys.ITEM;

    private void load() {
        try {
            dev.arubik.craftengine.util.CustomBlockData d = blockData();
            if (d == null)
                return;
            held = d.getOptional(KEY_ITEM).orElse(null);
            if (held != null && held.getType().isAir())
                held = null;
        } catch (Throwable ignored) {
        }
    }

    private void save() {
        try {
            dev.arubik.craftengine.util.CustomBlockData d = blockData();
            if (d == null)
                return;
            if (held == null || held.getType().isAir())
                d.clear();
            else
                d.set(KEY_ITEM, held);
        } catch (Throwable ignored) {
        }
    }
}
