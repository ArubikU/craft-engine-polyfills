package dev.arubik.craftengine.conveyor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.joml.Vector3f;

import dev.arubik.craftengine.block.entity.PersistentWorldlyBlockEntity;
import dev.arubik.craftengine.rotation.RpmConsumer;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateFlags;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.ChunkPos;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;

/**
 * Create-style conveyor belt segment. Each segment is now a 1-slot chest-like
 * container (extends {@link PersistentWorldlyBlockEntity} with size 1) so that
 * hoppers may insert/extract and the inventory is persisted automatically.
 *
 * <p>The single slot holds up to one full stack of ONE item type. Movement is
 * driven by RPM (via {@link RpmConsumer}): when the belt is powered and the slot
 * is non-empty the item interpolates start-&gt;end (slope-aware) and on arrival
 * the WHOLE stack moves to the next segment's slot, or is dropped if it can't be
 * accepted. A per-tick ticker also auto-picks-up nearby dropped items.</p>
 *
 * <p>Block properties (read live, never cached): {@code facing} (travel toward
 * the next segment), {@code slope} (FLAT/UP/DOWN) and {@code part}
 * (START/MIDDLE/END). The belt is a linked list via persisted {@link #prevPos}.</p>
 */
public class ConveyorBlockEntity extends PersistentWorldlyBlockEntity implements RpmConsumer {

    /** Reference rpm at which the belt runs at base speed. */
    public static final float BASE_RPM = 64f;
    /** Ticks for one item to cross one segment at BASE_RPM. */
    public static final int BASE_TRAVEL_TICKS = 16;
    /** Maximum number of segments a single belt may grow to. */
    public static final int MAX_LENGTH = 64;

    /** How often (ticks) to scan for dropped items to pick up. */
    public static final int PICKUP_INTERVAL = 5;
    /** Pickup scan radius around the block centre. */
    public static final double PICKUP_RADIUS = 0.75;

    /** Property names the CraftEngine block config must define. */
    public static final String PROP_FACING = "facing";
    public static final String PROP_SLOPE = "slope";
    public static final String PROP_PART = "part";

    /** Fallback facing when the block defines no {@code facing} property. */
    private Direction defaultFacing = Direction.NORTH;

    /** Position of the upstream (toward START) segment; null if this is START. */
    private BlockPos prevPos;

    /** rpm delivered to this consumer this tick (set externally for the head). */
    private float inputRpm = 0f;
    /** rpm actually driving this segment (head: inputRpm; body: upstream effective). */
    private float effectiveRpm = 0f;

    /** Travel progress 0..1 of the slot stack across this segment. */
    private float progress = 0f;
    /** Ticker counter for the pickup cadence. */
    private int tickCounter = 0;

    // render
    private ConveyorItemDisplay display;
    private boolean displaySpawned = false;

    public ConveyorBlockEntity(BlockEntity blockEntity) {
        super(blockEntity, 1);
    }

    public ConveyorBlockEntity(BlockEntity blockEntity, Direction defaultFacing) {
        super(blockEntity, 1);
        if (defaultFacing != null)
            this.defaultFacing = defaultFacing;
    }

    // ---------------- slot helpers ----------------

    /** The NMS stack currently in slot 0 (never null; EMPTY when empty). */
    private net.minecraft.world.item.ItemStack slot() {
        return getItem(0);
    }

    @Override
    public boolean isEmpty() {
        net.minecraft.world.item.ItemStack s = slot();
        return s == null || s.isEmpty();
    }

    /** The slot content as a Bukkit stack, or null when empty. */
    private org.bukkit.inventory.ItemStack bukkitSlot() {
        if (isEmpty())
            return null;
        return CraftItemStack.asBukkitCopy(slot());
    }

    // --- WorldlyContainer faces: allow all faces for slot 0 (hopper in/out) ---
    @Override
    public int[] getSlotsForFace(net.minecraft.core.Direction side) {
        return new int[] { 0 };
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, net.minecraft.world.item.ItemStack stack,
            net.minecraft.core.Direction direction) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, net.minecraft.world.item.ItemStack stack,
            net.minecraft.core.Direction direction) {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {
        // Slot persistence is pulled at chunk-save via the controller; no dirty flag needed.
    }

    // ---------------- live property reads (never cached) ----------------

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static String enumName(ImmutableBlockState state, String name) {
        if (state == null)
            return null;
        Property p = state.getProperty(name);
        if (p == null)
            return null;
        Object v = state.get(p);
        if (v == null)
            return null;
        try {
            return net.momirealms.craftengine.core.block.property.Property.formatValue(p, (Comparable<?>) v);
        } catch (Throwable t) {
            return String.valueOf(v);
        }
    }

    /** The live travel direction read from {@code facing}, or the default. */
    public Direction facing() {
        String n = enumName(blockEntity().blockState(), PROP_FACING);
        if (n != null) {
            try {
                return Direction.valueOf(n.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return defaultFacing;
    }

    /** The live slope read from {@code slope}, or {@link ConveyorSlope#FLAT}. */
    public ConveyorSlope slope() {
        return ConveyorSlope.fromName(enumName(blockEntity().blockState(), PROP_SLOPE));
    }

    /** The live part read from {@code part}, or {@link ConveyorPart#END}. */
    public ConveyorPart part() {
        return ConveyorPart.fromName(enumName(blockEntity().blockState(), PROP_PART));
    }

    // ---------------- RpmConsumer ----------------

    @Override
    public void setInputRpm(float rpm) {
        this.inputRpm = rpm;
    }

    @Override
    public float getInputRpm() {
        return inputRpm;
    }

    /** The rpm this segment is effectively running at (what downstream reads). */
    public float effectiveRpm() {
        return effectiveRpm;
    }

    public BlockPos prevPos() {
        return prevPos;
    }

    public void setPrevPos(BlockPos prevPos) {
        this.prevPos = prevPos;
    }

    /**
     * Try to place an item onto this belt's slot at the start point.
     *
     * <p>Backed by the container slot: accepts when empty, or when stackable with
     * the existing item (same type) up to the max stack size. Merges what fits.</p>
     *
     * @return true if the entire stack was accepted (slot may have merged it)
     */
    public boolean acceptItem(org.bukkit.inventory.ItemStack stack) {
        if (stack == null || stack.getType().isAir())
            return false;
        net.minecraft.world.item.ItemStack incoming = CraftItemStack.asNMSCopy(stack);
        if (isEmpty()) {
            setItem(0, incoming);
            this.progress = 0f;
            refreshDisplayItem();
            return true;
        }
        net.minecraft.world.item.ItemStack cur = slot();
        if (net.minecraft.world.item.ItemStack.isSameItemSameComponents(cur, incoming)) {
            int max = Math.min(getMaxStackSize(), cur.getMaxStackSize());
            int space = max - cur.getCount();
            if (space <= 0)
                return false;
            int move = Math.min(space, incoming.getCount());
            cur.grow(move);
            setItem(0, cur);
            refreshDisplayItem();
            return move >= incoming.getCount();
        }
        return false;
    }

    /**
     * Remove and return the entire slot stack as a Bukkit stack (or null if empty).
     * Used by right-click "take".
     */
    public org.bukkit.inventory.ItemStack takeSlot() {
        if (isEmpty())
            return null;
        org.bukkit.inventory.ItemStack out = bukkitSlot();
        setItem(0, net.minecraft.world.item.ItemStack.EMPTY);
        this.progress = 0f;
        refreshDisplayItem();
        return out;
    }

    /**
     * Put the player's hand stack into the slot. If the slot is empty, takes the
     * whole stack. If same type, merges what fits. Otherwise swaps. Used by
     * right-click "put".
     *
     * @return what should remain in the player's hand (empty/air-able stack)
     */
    public org.bukkit.inventory.ItemStack putSlot(org.bukkit.inventory.ItemStack hand) {
        if (hand == null || hand.getType().isAir())
            return hand;
        net.minecraft.world.item.ItemStack incoming = CraftItemStack.asNMSCopy(hand);
        if (isEmpty()) {
            setItem(0, incoming);
            this.progress = 0f;
            refreshDisplayItem();
            return new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR);
        }
        net.minecraft.world.item.ItemStack cur = slot();
        if (net.minecraft.world.item.ItemStack.isSameItemSameComponents(cur, incoming)) {
            int max = Math.min(getMaxStackSize(), cur.getMaxStackSize());
            int space = max - cur.getCount();
            int move = Math.max(0, Math.min(space, incoming.getCount()));
            if (move > 0) {
                cur.grow(move);
                setItem(0, cur);
                refreshDisplayItem();
            }
            org.bukkit.inventory.ItemStack remain = hand.clone();
            remain.setAmount(hand.getAmount() - move);
            return remain;
        }
        // swap
        org.bukkit.inventory.ItemStack old = bukkitSlot();
        setItem(0, incoming);
        this.progress = 0f;
        refreshDisplayItem();
        return old;
    }

    private void refreshDisplayItem() {
        if (display != null)
            display.setNmsItem(slot().isEmpty() ? net.minecraft.world.item.ItemStack.EMPTY : slot().copy());
    }

    // ---------------- ticking ----------------

    @Override
    @SuppressWarnings("unchecked")
    public <C extends BlockEntityController> net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<C> createBlockEntityTicker(
            CEWorld world, ImmutableBlockState state) {
        return BlockEntityController.createTickerHelper(
                (net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<ConveyorBlockEntity>) ConveyorBlockEntity::tick);
    }

    public static void tick(CEWorld world, BlockPos pos, ImmutableBlockState state, ConveyorBlockEntity self) {
        self.serverTick(world, pos);
    }

    private void serverTick(CEWorld world, BlockPos pos) {
        Direction facing = facing();

        // Effective rpm: head reads its motor input; a body reads its upstream's.
        ConveyorBlockEntity upstream = upstreamConveyor(world, pos, facing);
        if (upstream != null) {
            this.effectiveRpm = upstream.effectiveRpm();
        } else {
            this.effectiveRpm = this.inputRpm;
        }

        // Drive the 'activated' block-state (running when it has RPM) so the model/
        // texture swaps between animated (on) and static (off).
        maybeUpdateActivated(world, pos, this.effectiveRpm > 0f);

        // Auto-pickup cadence: only when the slot is empty.
        if (++tickCounter >= PICKUP_INTERVAL) {
            tickCounter = 0;
            if (isEmpty())
                tryPickup(world, pos);
        }

        if (isEmpty()) {
            ensureDisplayHidden(world, pos);
            this.progress = 0f;
            return;
        }

        float inc = ConveyorMath.progressPerTick(effectiveRpm, BASE_RPM, BASE_TRAVEL_TICKS);
        if (inc > 0f) {
            this.progress = ConveyorMath.clamp01(this.progress + inc);
        }

        renderCarried(world, pos, facing);

        if (this.progress >= 1f) {
            handOff(world, pos, facing);
        }
    }

    /** Pull the nearest dropped item entity into the (empty) slot. */
    private void tryPickup(CEWorld world, BlockPos pos) {
        try {
            org.bukkit.World bukkitWorld = (org.bukkit.World) world.world().platformWorld();
            if (bukkitWorld == null)
                return;
            org.bukkit.Location center = new org.bukkit.Location(bukkitWorld,
                    pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5);
            org.bukkit.entity.Item nearest = null;
            double best = Double.MAX_VALUE;
            for (org.bukkit.entity.Entity e : bukkitWorld.getNearbyEntities(center,
                    PICKUP_RADIUS, PICKUP_RADIUS, PICKUP_RADIUS)) {
                if (!(e instanceof org.bukkit.entity.Item item))
                    continue;
                if (item.isDead() || !item.isValid())
                    continue;
                double d = item.getLocation().distanceSquared(center);
                if (d < best) {
                    best = d;
                    nearest = item;
                }
            }
            if (nearest == null)
                return;
            org.bukkit.inventory.ItemStack stack = nearest.getItemStack();
            setItem(0, CraftItemStack.asNMSCopy(stack));
            this.progress = 0f;
            refreshDisplayItem();
            nearest.remove();
        } catch (Throwable ignored) {
        }
    }

    private void handOff(CEWorld world, BlockPos pos, Direction facing) {
        ConveyorBlockEntity next = downstreamConveyor(world, pos, facing);
        org.bukkit.inventory.ItemStack item = bukkitSlot();
        if (item == null)
            return;
        if (next != null && next.acceptItem(item)) {
            clearCarried(world, pos);
            return;
        }
        dropAtEnd(world, pos, facing, item);
        clearCarried(world, pos);
    }

    private void dropAtEnd(CEWorld world, BlockPos pos, Direction facing, org.bukkit.inventory.ItemStack item) {
        try {
            org.bukkit.World bukkitWorld = (org.bukkit.World) world.world().platformWorld();
            if (bukkitWorld != null && item != null && !item.getType().isAir()) {
                Vector3f end = endRel(facing);
                bukkitWorld.dropItem(new org.bukkit.Location(bukkitWorld,
                        pos.x() + end.x, pos.y() + end.y, pos.z() + end.z), item);
            }
        } catch (Throwable ignored) {
        }
    }

    // ---------------- render geometry (slope-aware) ----------------

    private Vector3f startRel(Direction facing) {
        return ConveyorMath.startPoint(facing.stepX(), facing.stepZ(), slope().stepY());
    }

    private Vector3f endRel(Direction facing) {
        return ConveyorMath.endPoint(facing.stepX(), facing.stepZ(), slope().stepY());
    }

    private void renderCarried(CEWorld world, BlockPos pos, Direction facing) {
        if (display == null) {
            display = new ConveyorItemDisplay();
        }
        display.setNmsItem(slot().copy());
        display.setRotation(ConveyorMath.itemRotation(facing.stepX(), facing.stepZ(), slope().stepY()));
        Vector3f rel = ConveyorMath.interpolate(startRel(facing), endRel(facing), progress);
        double wx = pos.x() + rel.x;
        double wy = pos.y() + rel.y;
        double wz = pos.z() + rel.z;
        List<Player> viewers = world.world().getTrackedBy(new ChunkPos(pos));
        if (!displaySpawned) {
            for (Player p : viewers)
                display.spawn(p, wx, wy, wz);
            displaySpawned = true;
        } else {
            for (Player p : viewers)
                display.updatePosition(p, wx, wy, wz);
        }
    }

    private void ensureDisplayHidden(CEWorld world, BlockPos pos) {
        if (display != null && displaySpawned) {
            for (Player p : world.world().getTrackedBy(new ChunkPos(pos)))
                display.despawn(p);
            displaySpawned = false;
        }
    }

    private void clearCarried(CEWorld world, BlockPos pos) {
        ensureDisplayHidden(world, pos);
        setItem(0, net.minecraft.world.item.ItemStack.EMPTY);
        this.progress = 0f;
    }

    // ---------------- linked-list neighbour lookup (by reference) ----------------

    /** The conveyor controller at {@code rel}, or null if absent/unloaded. */
    static ConveyorBlockEntity conveyorAt(CEWorld world, BlockPos rel) {
        BlockEntity be = world.getBlockEntityAtIfLoaded(rel);
        if (be != null && be.controller instanceof ConveyorBlockEntity conveyor)
            return conveyor;
        return null;
    }

    ConveyorBlockEntity downstreamConveyor(CEWorld world, BlockPos pos, Direction facing) {
        for (BlockPos cand : exitCandidates(pos, facing)) {
            ConveyorBlockEntity c = conveyorAt(world, cand);
            if (c != null && pos.equals(c.prevPos()))
                return c;
        }
        return null;
    }

    ConveyorBlockEntity upstreamConveyor(CEWorld world, BlockPos pos, Direction facing) {
        if (prevPos == null)
            return null;
        return conveyorAt(world, prevPos);
    }

    static List<BlockPos> exitCandidates(BlockPos pos, Direction facing) {
        BlockPos flat = pos.relative(facing);
        List<BlockPos> list = new ArrayList<>(3);
        list.add(flat); // FLAT
        list.add(new BlockPos(flat.x(), flat.y() + 1, flat.z())); // UP
        list.add(new BlockPos(flat.x(), flat.y() - 1, flat.z())); // DOWN
        return list;
    }

    BlockPos exitPos(Direction facing) {
        BlockPos flat = pos().relative(facing);
        int dy = slope().stepY();
        return dy == 0 ? flat : new BlockPos(flat.x(), flat.y() + dy, flat.z());
    }

    boolean isTail(CEWorld world, Direction facing) {
        return downstreamConveyor(world, pos(), facing) == null;
    }

    // ---------------- extend (programmatic) ----------------

    /**
     * Extend the belt from this END segment: place a new conveyor at the exit
     * position (respecting slope Y), link it (prevPos = this pos, part = END),
     * and demote this segment to MIDDLE (or START if it had no prev). Still
     * useful programmatically; no longer invoked by right-click.
     *
     * @return true if a segment was added
     */
    public boolean extend(CEWorld world, BlockPos pos, Direction facing, ConveyorSlope slope) {
        if (part() != ConveyorPart.END)
            return false;
        if (lengthFromHere(world, facing) >= MAX_LENGTH)
            return false;

        org.bukkit.World bukkitWorld;
        try {
            bukkitWorld = (org.bukkit.World) world.world().platformWorld();
        } catch (Throwable t) {
            return false;
        }
        if (bukkitWorld == null)
            return false;

        BlockPos target = pos.relative(facing);
        int dy = slope.stepY();
        if (dy != 0)
            target = new BlockPos(target.x(), target.y() + dy, target.z());

        org.bukkit.block.Block targetBlock = bukkitWorld.getBlockAt(target.x(), target.y(), target.z());
        if (!isReplaceable(targetBlock))
            return false;

        BlockDefinition def = blockEntity().blockState().owner().value();
        Key blockId = def.id();

        ImmutableBlockState newState = stateWith(def.defaultState(), facing, slope, ConveyorPart.END);
        org.bukkit.Location loc = new org.bukkit.Location(bukkitWorld, target.x(), target.y(), target.z());
        boolean placed = net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.place(
                loc, newState, UpdateFlags.UPDATE_ALL, false);
        if (!placed) {
            placed = net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.place(loc, blockId, false);
            if (!placed)
                return false;
        }

        ConveyorBlockEntity created = conveyorAt(world, target);
        if (created != null)
            created.setPrevPos(pos);

        ConveyorPart newPart = (prevPos == null) ? ConveyorPart.START : ConveyorPart.MIDDLE;
        applyPart(world, pos, facing, slope, newPart);
        return true;
    }

    private int lengthFromHere(CEWorld world, Direction facing) {
        int n = 1;
        ConveyorBlockEntity walk = upstreamConveyor(world, pos(), facing);
        Set<BlockPos> seen = new HashSet<>();
        while (walk != null && seen.add(walk.pos())) {
            n++;
            walk = walk.upstreamConveyor(world, walk.pos(), walk.facing());
        }
        return n;
    }

    // ---------------- break ----------------

    /**
     * Handle removal of this segment. Drops this segment's slot stack, then:
     * <ul>
     *   <li>END (tail): promote the upstream segment back to END.</li>
     *   <li>START/MIDDLE: tear down the WHOLE belt, each removed segment dropping
     *       its own slot stack.</li>
     * </ul>
     */
    public void onBroken(CEWorld world, BlockPos pos, Direction facing) {
        ConveyorBlockEntity down = downstreamConveyor(world, pos, facing);
        boolean tail = (down == null);

        // drop our own slot stack
        if (!isEmpty()) {
            dropAtEnd(world, pos, facing, bukkitSlot());
            setItem(0, net.minecraft.world.item.ItemStack.EMPTY);
        }

        if (tail) {
            ConveyorBlockEntity up = upstreamConveyor(world, pos, facing);
            if (up != null)
                up.applyPart(world, up.pos(), up.facing(), up.slope(), ConveyorPart.END);
            return;
        }

        Set<BlockPos> visited = new HashSet<>();
        visited.add(pos);

        ConveyorBlockEntity up = upstreamConveyor(world, pos, facing);
        while (up != null && visited.add(up.pos())) {
            ConveyorBlockEntity next = up.upstreamConveyor(world, up.pos(), up.facing());
            up.removeSelf(world);
            up = next;
        }
        ConveyorBlockEntity d = down;
        while (d != null && visited.add(d.pos())) {
            ConveyorBlockEntity next = d.downstreamConveyor(world, d.pos(), d.facing());
            d.removeSelf(world);
            d = next;
        }
    }

    /** Remove this segment's block, dropping its slot stack. */
    private void removeSelf(CEWorld world) {
        BlockPos pos = pos();
        Direction facing = facing();
        if (!isEmpty()) {
            dropAtEnd(world, pos, facing, bukkitSlot());
            clearCarried(world, pos);
        }
        try {
            org.bukkit.World bukkitWorld = (org.bukkit.World) world.world().platformWorld();
            if (bukkitWorld != null)
                bukkitWorld.getBlockAt(pos.x(), pos.y(), pos.z()).setType(org.bukkit.Material.AIR, false);
        } catch (Throwable ignored) {
        }
    }

    // ---------------- state mutation helpers ----------------

    /** Property name for the running flag the polyfill drives from RPM. */
    private static final String PROP_ACTIVATED = "activated";
    private Boolean lastActivated = null;

    /**
     * Sets the {@code activated} boolean block-state when the running state flips,
     * swapping the model/texture between animated (on) and static (off). Uses a
     * client-only NMS setBlock (flag 2) so the block entity (slot/links) is NOT
     * recreated — same approach the redstone/diode behaviors use to change state.
     */
    private void maybeUpdateActivated(CEWorld world, BlockPos pos, boolean active) {
        if (lastActivated != null && lastActivated == active)
            return;
        ImmutableBlockState cur = blockEntity().blockState();
        if (cur == null || cur.getProperty(PROP_ACTIVATED) == null) {
            lastActivated = active;   // property not defined on this block; nothing to toggle
            return;
        }
        String now = enumName(cur, PROP_ACTIVATED);
        if (String.valueOf(active).equalsIgnoreCase(now)) {
            lastActivated = active;
            return;
        }
        ImmutableBlockState ns = withEnum(cur, PROP_ACTIVATED, String.valueOf(active));
        if (ns == cur)
            return;
        try {
            Object level = world.world().minecraftWorld();
            Object bp = dev.arubik.craftengine.util.MNms.INSTANCE.constructor$BlockPos(pos.x(), pos.y(), pos.z());
            Object nms = ns.customBlockState().minecraftState();
            // flag 2 = notify clients only (no neighbour updates, keeps the block entity)
            dev.arubik.craftengine.util.MNms.INSTANCE.method$LevelWriter$setBlock(level, bp, nms, 2);
            lastActivated = active;
        } catch (Throwable ignored) {
        }
    }

    private static ImmutableBlockState stateWith(ImmutableBlockState base, Direction facing,
            ConveyorSlope slope, ConveyorPart part) {
        ImmutableBlockState s = base;
        s = withEnum(s, PROP_FACING, facing.name());
        s = withEnum(s, PROP_SLOPE, slope.name());
        s = withEnum(s, PROP_PART, part.name());
        return s;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static ImmutableBlockState withEnum(ImmutableBlockState state, String prop, String valueName) {
        Property p = state.getProperty(prop);
        if (p == null)
            return state;
        try {
            Object value = p.valueByName(valueName.toLowerCase());
            if (value == null)
                value = p.valueByName(valueName);
            if (value == null)
                return state;
            return ImmutableBlockState.with(state, p, value);
        } catch (Throwable t) {
            return state;
        }
    }

    private void applyPart(CEWorld world, BlockPos pos, Direction facing, ConveyorSlope slope, ConveyorPart part) {
        try {
            org.bukkit.World bukkitWorld = (org.bukkit.World) world.world().platformWorld();
            if (bukkitWorld == null)
                return;
            BlockDefinition def = blockEntity().blockState().owner().value();
            ImmutableBlockState newState = stateWith(def.defaultState(), facing, slope, part);
            org.bukkit.Location loc = new org.bukkit.Location(bukkitWorld, pos.x(), pos.y(), pos.z());
            net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.place(
                    loc, newState, UpdateFlags.UPDATE_ALL, false);
        } catch (Throwable ignored) {
        }
    }

    static boolean isReplaceable(org.bukkit.block.Block block) {
        if (block == null)
            return false;
        org.bukkit.Material m = block.getType();
        return m.isAir() || m == org.bukkit.Material.WATER || m == org.bukkit.Material.LAVA
                || m == org.bukkit.Material.SHORT_GRASS || m == org.bukkit.Material.TALL_GRASS
                || m == org.bukkit.Material.SNOW;
    }

    // ---------------- persistence ----------------

    @Override
    public void saveCustomData(CompoundTag tag) {
        // Inventory (slot 0) is persisted by the parent.
        super.saveCustomData(tag);
        tag.putFloat("progress", progress);
        if (prevPos != null) {
            tag.putInt("prevX", prevPos.x());
            tag.putInt("prevY", prevPos.y());
            tag.putInt("prevZ", prevPos.z());
        }
    }

    @Override
    public void loadCustomData(CompoundTag tag) {
        super.loadCustomData(tag);
        this.progress = tag.getFloat("progress", 0f);
        if (tag.containsKey("prevX") && tag.containsKey("prevY") && tag.containsKey("prevZ")) {
            this.prevPos = new BlockPos(tag.getInt("prevX"), tag.getInt("prevY"), tag.getInt("prevZ"));
        }
    }

    @Override
    public void onRemove() {
        super.onRemove();
    }
}
