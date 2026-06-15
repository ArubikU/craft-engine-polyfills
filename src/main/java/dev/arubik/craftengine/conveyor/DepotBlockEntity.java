package dev.arubik.craftengine.conveyor;

import org.bukkit.craftbukkit.inventory.CraftItemStack;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.item.BukkitItemManager;
import net.momirealms.craftengine.bukkit.world.inventory.BukkitWorldlyStorageContainer;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.item.Item;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.WorldPosition;

/**
 * DEPOT controller: a 14³ buffer block that doubles as an inventory.
 *
 * <p>Storage is a CraftEngine {@link BukkitWorldlyStorageContainer} — the SAME native
 * container the engine wraps for vanilla hopper insert/extract. The depot behavior
 * implements {@code core.block.behavior.WorldlyContainerHolder} and hands this container
 * to the engine on demand, so hoppers (and the comparator) talk to exactly the store the
 * belt, right-click and on-top item renderer use.</p>
 */
public class DepotBlockEntity extends PersistentBlockEntity implements ConveyorReceiver {

    private final int storageSize;
    private final BukkitWorldlyStorageContainer container;

    /** Up to 9 floating item displays shown on top of the depot (one per stack). */
    private final int maxDisplays;
    private final ConveyorItemDisplay[] displays;
    private final boolean[] spawned;

    private int renderTick = 0;
    private int lastHash = Integer.MIN_VALUE;
    private boolean loaded = false;

    public DepotBlockEntity(BlockEntity blockEntity, int storageSize, String title) {
        super(blockEntity);
        this.storageSize = Math.max(1, storageSize);
        // Owner provides the world position so the container reports its location.
        net.momirealms.craftengine.bukkit.world.WorldlyContainerHolder owner =
                new net.momirealms.craftengine.bukkit.world.WorldlyContainerHolder(p -> {
                }, this::worldPos);
        this.container = new BukkitWorldlyStorageContainer(owner, this.storageSize, true, true);
        this.maxDisplays = Math.min(this.storageSize, 9);
        this.displays = new ConveyorItemDisplay[maxDisplays];
        this.spawned = new boolean[maxDisplays];
    }

    /** The native container the engine bridges to hoppers (returned by the behavior). */
    public BukkitWorldlyStorageContainer container() {
        return container;
    }

    private WorldPosition worldPos() {
        try {
            net.momirealms.craftengine.core.world.CEWorld w = blockEntity().world();
            if (w == null)
                return null;
            return new WorldPosition(w.world, blockEntity().pos());
        } catch (Throwable t) {
            return null;
        }
    }

    private Level nmsLevel() {
        try {
            return (Level) blockEntity().world().world.minecraftWorld();
        } catch (Throwable t) {
            return null;
        }
    }

    // ---------------- item <-> CE Item bridging ----------------

    private org.bukkit.inventory.ItemStack get(int i) {
        Item it = container.getItem(i);
        if (it == null || it.isEmpty())
            return null;
        // Item.minecraftItem() returns the NMS stack regardless of the Item impl.
        return CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack) it.minecraftItem());
    }

    private void set(int i, org.bukkit.inventory.ItemStack s) {
        if (s == null || s.getType().isAir())
            container.setItem(i, BukkitItemManager.instance().emptyItem());
        else
            container.setItem(i, BukkitItemManager.instance().wrap(s));
    }

    private int maxStackFor(org.bukkit.inventory.ItemStack s) {
        return Math.min(container.maxStackSize(), s.getMaxStackSize());
    }

    // ---------------- ConveyorReceiver ----------------

    @Override
    public boolean isFull() {
        for (int i = 0; i < container.containerSize(); i++)
            if (get(i) == null)
                return false;
        return true; // (partial merges still possible; checked per-insert)
    }

    @Override
    public boolean receiveConveyorItem(org.bukkit.inventory.ItemStack stack, Direction sourceFacing) {
        if (stack == null || stack.getType().isAir())
            return false;
        if (!canFit(stack))
            return false;
        insert(stack);
        return true;
    }

    private boolean canFit(org.bukkit.inventory.ItemStack stack) {
        int max = maxStackFor(stack);
        int need = stack.getAmount();
        for (int i = 0; i < container.containerSize() && need > 0; i++) {
            org.bukkit.inventory.ItemStack cur = get(i);
            if (cur == null)
                need -= max;
            else if (cur.isSimilar(stack))
                need -= Math.max(0, max - cur.getAmount());
        }
        return need <= 0;
    }

    /** Insert {@code stack} (assumed to fit; verified by {@link #canFit}). */
    private void insert(org.bukkit.inventory.ItemStack stack) {
        int max = maxStackFor(stack);
        org.bukkit.inventory.ItemStack rem = stack.clone();
        for (int i = 0; i < container.containerSize() && rem.getAmount() > 0; i++) {
            org.bukkit.inventory.ItemStack cur = get(i);
            if (cur == null || !cur.isSimilar(rem))
                continue;
            int move = Math.min(rem.getAmount(), max - cur.getAmount());
            if (move > 0) {
                cur.setAmount(cur.getAmount() + move);
                set(i, cur);
                rem.setAmount(rem.getAmount() - move);
            }
        }
        for (int i = 0; i < container.containerSize() && rem.getAmount() > 0; i++) {
            if (get(i) != null)
                continue;
            int move = Math.min(rem.getAmount(), max);
            org.bukkit.inventory.ItemStack put = rem.clone();
            put.setAmount(move);
            set(i, put);
            rem.setAmount(rem.getAmount() - move);
        }
    }

    // ---------------- right-click put / take (no GUI) ----------------

    /** Take the last non-empty stack out; returns it (or null). */
    public org.bukkit.inventory.ItemStack takeOne() {
        for (int i = container.containerSize() - 1; i >= 0; i--) {
            org.bukkit.inventory.ItemStack cur = get(i);
            if (cur != null) {
                set(i, null);
                return cur;
            }
        }
        return null;
    }

    /** Put as much of {@code hand} as fits; returns the leftover. */
    public org.bukkit.inventory.ItemStack putSome(org.bukkit.inventory.ItemStack hand) {
        if (hand == null || hand.getType().isAir())
            return hand;
        int max = maxStackFor(hand);
        org.bukkit.inventory.ItemStack rem = hand.clone();
        for (int i = 0; i < container.containerSize() && rem.getAmount() > 0; i++) {
            org.bukkit.inventory.ItemStack cur = get(i);
            if (cur == null || !cur.isSimilar(rem))
                continue;
            int move = Math.min(rem.getAmount(), max - cur.getAmount());
            if (move > 0) {
                cur.setAmount(cur.getAmount() + move);
                set(i, cur);
                rem.setAmount(rem.getAmount() - move);
            }
        }
        for (int i = 0; i < container.containerSize() && rem.getAmount() > 0; i++) {
            if (get(i) != null)
                continue;
            int move = Math.min(rem.getAmount(), max);
            org.bukkit.inventory.ItemStack put = rem.clone();
            put.setAmount(move);
            set(i, put);
            rem.setAmount(rem.getAmount() - move);
        }
        return rem.getAmount() > 0 ? rem : new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR);
    }

    // ---------------- comparator ----------------

    /** Vanilla-style comparator signal from fill ratio. */
    public int analogSignal() {
        float fill = 0f;
        int nonEmpty = 0;
        int size = container.containerSize();
        for (int i = 0; i < size; i++) {
            org.bukkit.inventory.ItemStack cur = get(i);
            if (cur == null)
                continue;
            fill += cur.getAmount() / (float) Math.min(container.maxStackSize(), cur.getMaxStackSize());
            nonEmpty++;
        }
        if (nonEmpty == 0)
            return 0;
        return Math.min(15, (int) Math.floor((fill / size) * 14f) + 1);
    }

    // ---------------- drop on break ----------------

    public void dropAll() {
        org.bukkit.World bw;
        try {
            bw = (org.bukkit.World) blockEntity().world().world.platformWorld();
        } catch (Throwable t) {
            bw = null;
        }
        var pos = blockEntity().pos();
        for (int i = 0; i < container.containerSize(); i++) {
            org.bukkit.inventory.ItemStack cur = get(i);
            if (cur != null && bw != null)
                bw.dropItem(new org.bukkit.Location(bw, pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5), cur);
            set(i, null);
        }
        despawnAllDisplays();
        try {
            dev.arubik.craftengine.util.CustomBlockData.from(
                    bw.getBlockAt(pos.x(), pos.y(), pos.z())).clear();
        } catch (Throwable ignored) {
        }
    }

    /** Despawn all on-top item displays for every tracked viewer. */
    public void despawnAllDisplays() {
        try {
            var pos = blockEntity().pos();
            java.util.List<net.momirealms.craftengine.core.entity.player.Player> viewers =
                    blockEntity().world().world().getTrackedBy(
                            new net.momirealms.craftengine.core.world.ChunkPos(pos));
            for (int i = 0; i < maxDisplays; i++)
                despawn(viewers, i);
        } catch (Throwable ignored) {
        }
    }

    // ---------------- persistence (chunk-backed CustomBlockData) ----------------

    private dev.arubik.craftengine.util.CustomBlockData blockData() {
        try {
            org.bukkit.World bw = (org.bukkit.World) blockEntity().world().world.platformWorld();
            var pos = blockEntity().pos();
            return dev.arubik.craftengine.util.CustomBlockData.from(bw.getBlockAt(pos.x(), pos.y(), pos.z()));
        } catch (Throwable t) {
            return null;
        }
    }

    private void load() {
        try {
            dev.arubik.craftengine.util.CustomBlockData data = blockData();
            if (data == null)
                return;
            data.getOptional(dev.arubik.craftengine.util.TypedKeys.CONTENTS).ifPresent(contents -> {
                for (int i = 0; i < container.containerSize(); i++)
                    set(i, null);
                for (net.minecraft.world.ItemStackWithSlot it : contents)
                    if (it.slot() >= 0 && it.slot() < container.containerSize())
                        set(it.slot(), CraftItemStack.asBukkitCopy(it.stack()));
            });
        } catch (Throwable ignored) {
        }
    }

    private int hashContents() {
        int h = 7;
        for (int i = 0; i < container.containerSize(); i++) {
            org.bukkit.inventory.ItemStack cur = get(i);
            h = h * 31 + (cur == null ? 0 : (cur.getType().ordinal() * 131 + cur.getAmount()));
        }
        return h;
    }

    private void saveIfChanged() {
        int h = hashContents();
        if (h == lastHash)
            return;
        lastHash = h;
        try {
            dev.arubik.craftengine.util.CustomBlockData data = blockData();
            if (data == null)
                return;
            net.minecraft.world.item.ItemStack[] nms =
                    new net.minecraft.world.item.ItemStack[container.containerSize()];
            for (int i = 0; i < nms.length; i++) {
                org.bukkit.inventory.ItemStack cur = get(i);
                nms[i] = cur == null ? net.minecraft.world.item.ItemStack.EMPTY : CraftItemStack.asNMSCopy(cur);
            }
            data.set(dev.arubik.craftengine.util.TypedKeys.CONTENTS,
                    dev.arubik.craftengine.util.ArrayItemStackWithSlot.from(nms));
            // Refresh comparators reading this block.
            Level lvl = nmsLevel();
            if (lvl != null) {
                var pos = blockEntity().pos();
                net.minecraft.core.BlockPos bp = new net.minecraft.core.BlockPos(pos.x(), pos.y(), pos.z());
                lvl.updateNeighbourForOutputSignal(bp, lvl.getBlockState(bp).getBlock());
            }
        } catch (Throwable ignored) {
        }
    }

    // ---------------- ticking / rendering ----------------

    @Override
    @SuppressWarnings("unchecked")
    public <C extends net.momirealms.craftengine.core.block.entity.BlockEntityController> net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<C> createBlockEntityTicker(
            net.momirealms.craftengine.core.world.CEWorld world,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        return net.momirealms.craftengine.core.block.entity.BlockEntityController.createTickerHelper(
                (net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<DepotBlockEntity>) DepotBlockEntity::tick);
    }

    public static void tick(net.momirealms.craftengine.core.world.CEWorld world,
            net.momirealms.craftengine.core.world.BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state, DepotBlockEntity self) {
        if (!self.loaded) {
            self.load();
            self.loaded = true;
            self.lastHash = self.hashContents();
        }
        if ((self.renderTick++ % 5) != 0)
            return;
        self.renderItems(world, pos);
        self.saveIfChanged();
    }

    private static final org.joml.Quaternionf FLAT =
            new org.joml.Quaternionf().rotateX((float) Math.toRadians(-90));

    private void renderItems(net.momirealms.craftengine.core.world.CEWorld world,
            net.momirealms.craftengine.core.world.BlockPos pos) {
        java.util.List<net.momirealms.craftengine.core.entity.player.Player> viewers =
                world.world().getTrackedBy(new net.momirealms.craftengine.core.world.ChunkPos(pos));
        int shown = 0;
        for (int slot = 0; slot < container.containerSize() && shown < maxDisplays; slot++) {
            org.bukkit.inventory.ItemStack cur = get(slot);
            if (cur == null)
                continue;
            renderOne(viewers, pos, shown, cur);
            shown++;
        }
        for (int i = shown; i < maxDisplays; i++)
            despawn(viewers, i);
    }

    private void renderOne(java.util.List<net.momirealms.craftengine.core.entity.player.Player> viewers,
            net.momirealms.craftengine.core.world.BlockPos pos, int i, org.bukkit.inventory.ItemStack stack) {
        if (displays[i] == null)
            displays[i] = new ConveyorItemDisplay();
        ConveyorItemDisplay d = displays[i];
        d.setNmsItem(CraftItemStack.asNMSCopy(stack));
        d.setRotation(FLAT);
        int col = i % 3, row = i / 3;
        double wx = pos.x() + 0.5 + (col - 1) * 0.24;
        double wy = pos.y() + 0.94;
        double wz = pos.z() + 0.5 + (row - 1) * 0.24;
        d.render(viewers, wx, wy, wz, true);
        d.consumeRotationDirty();
        spawned[i] = true;
    }

    private void despawn(java.util.List<net.momirealms.craftengine.core.entity.player.Player> viewers, int i) {
        if (displays[i] != null && spawned[i]) {
            for (var p : viewers)
                displays[i].despawn(p);
            displays[i].clearShown();
            spawned[i] = false;
        }
    }
}
