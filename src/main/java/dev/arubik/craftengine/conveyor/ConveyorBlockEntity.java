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
public class ConveyorBlockEntity extends PersistentWorldlyBlockEntity implements RpmConsumer, ConveyorReceiver {

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
    /** The RpmProvider (motor or router relay) driving this line, for stress forwarding. */
    private dev.arubik.craftengine.rotation.RpmProvider drivingMotor;

    /** The source driving this belt's line (null if unpowered). */
    public dev.arubik.craftengine.rotation.RpmProvider drivingMotor() {
        return drivingMotor;
    }

    /** Ticker counter for the pickup cadence. */
    private int tickCounter = 0;
    /** Lazy-load guard + dirty flag for CustomBlockData persistence. */
    private boolean stateLoaded = false;
    /** Set only when an item is added/removed; the tick flushes state just then. */
    private boolean dirty = false;

    /** Default stress (SU) one belt segment imposes on its driving motor. */
    public static final float DEFAULT_STRESS_IMPACT = 4f;
    /** Default number of item positions a belt segment carries at once. */
    public static final int DEFAULT_SLOTS = 4;

    /** Configurable per-block tunables (default to the static constants). */
    private int baseTravelTicks = BASE_TRAVEL_TICKS;
    private float baseRpm = BASE_RPM;
    private float stressImpact = DEFAULT_STRESS_IMPACT;

    // ---- multi-item transport: one entry per inventory slot ----
    /** Number of item positions on this segment; spacing between items = 1/slots. */
    private final int slots;
    /** Travel progress 0..1 of the item in each slot (only meaningful when occupied). */
    private final float[] progress;
    /** Small fixed yaw jitter (radians) per item so the line doesn't look static. */
    private final float[] jitter;
    /** Horizontal edge each item entered from (null = in-line back entry). Drives the
     *  corner path when an item arrives from a perpendicular belt (T-junction). */
    private final Direction[] entryDir;
    /** Per-slot display entity + spawned flag. */
    private final ConveyorItemDisplay[] displays;
    private final boolean[] spawned;

    public ConveyorBlockEntity(BlockEntity blockEntity) {
        this(blockEntity, null, BASE_TRAVEL_TICKS, BASE_RPM, DEFAULT_STRESS_IMPACT, DEFAULT_SLOTS);
    }

    public ConveyorBlockEntity(BlockEntity blockEntity, Direction defaultFacing) {
        this(blockEntity, defaultFacing, BASE_TRAVEL_TICKS, BASE_RPM, DEFAULT_STRESS_IMPACT, DEFAULT_SLOTS);
    }

    public ConveyorBlockEntity(BlockEntity blockEntity, Direction defaultFacing, int baseTravelTicks, float baseRpm) {
        this(blockEntity, defaultFacing, baseTravelTicks, baseRpm, DEFAULT_STRESS_IMPACT, DEFAULT_SLOTS);
    }

    public ConveyorBlockEntity(BlockEntity blockEntity, Direction defaultFacing, int baseTravelTicks, float baseRpm,
            float stressImpact) {
        this(blockEntity, defaultFacing, baseTravelTicks, baseRpm, stressImpact, DEFAULT_SLOTS);
    }

    public ConveyorBlockEntity(BlockEntity blockEntity, Direction defaultFacing, int baseTravelTicks, float baseRpm,
            float stressImpact, int slots) {
        super(blockEntity, Math.max(1, slots));
        if (defaultFacing != null)
            this.defaultFacing = defaultFacing;
        this.baseTravelTicks = baseTravelTicks > 0 ? baseTravelTicks : BASE_TRAVEL_TICKS;
        this.baseRpm = baseRpm > 0 ? baseRpm : BASE_RPM;
        this.stressImpact = stressImpact >= 0 ? stressImpact : DEFAULT_STRESS_IMPACT;
        this.slots = Math.max(1, slots);
        this.progress = new float[this.slots];
        this.jitter = new float[this.slots];
        this.entryDir = new Direction[this.slots];
        this.displays = new ConveyorItemDisplay[this.slots];
        this.spawned = new boolean[this.slots];
    }

    /** Minimum progress gap between two consecutive items so they don't overlap. */
    private float spacing() {
        return 1f / slots;
    }

    // ---------------- slot helpers ----------------

    private boolean slotEmpty(int i) {
        net.minecraft.world.item.ItemStack s = getItem(i);
        return s == null || s.isEmpty();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < slots; i++)
            if (!slotEmpty(i))
                return false;
        return true;
    }

    /** The content of slot {@code i} as a Bukkit stack, or null when empty. */
    private org.bukkit.inventory.ItemStack bukkitSlot(int i) {
        if (slotEmpty(i))
            return null;
        return CraftItemStack.asBukkitCopy(getItem(i));
    }

    /** First empty slot index, or -1 if full. */
    private int firstEmptySlot() {
        for (int i = 0; i < slots; i++)
            if (slotEmpty(i))
                return i;
        return -1;
    }

    /** True when the entry point (progress 0) is clear enough to accept a new item. */
    private boolean backHasRoom() {
        if (firstEmptySlot() < 0)
            return false;
        float gap = spacing();
        for (int i = 0; i < slots; i++)
            if (!slotEmpty(i) && progress[i] < gap)
                return false;
        return true;
    }

    /** Index of the front-most (highest progress) occupied slot, or -1. */
    private int frontSlot() {
        int best = -1;
        for (int i = 0; i < slots; i++)
            if (!slotEmpty(i) && (best < 0 || progress[i] > progress[best]))
                best = i;
        return best;
    }

    private static float randJitter() {
        // ±9° fixed yaw wobble so items don't look rigidly aligned.
        return (float) ((Math.random() - 0.5) * Math.toRadians(18));
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
        this.dirty = true; // persist the belt link on next tick
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
        if (!backHasRoom())
            return false;
        int i = firstEmptySlot();
        if (i < 0)
            return false;
        setItem(i, CraftItemStack.asNMSCopy(stack));
        progress[i] = 0f;
        jitter[i] = randJitter();
        entryDir[i] = facing().opposite(); // in-line back entry
        dirty = true;
        return true;
    }

    /**
     * Remove and return the front-most item as a Bukkit stack (or null if empty).
     * Used by right-click "take".
     */
    public org.bukkit.inventory.ItemStack takeSlot() {
        int i = frontSlot();
        if (i < 0)
            return null;
        org.bukkit.inventory.ItemStack out = bukkitSlot(i);
        clearSlot(i);
        return out;
    }

    /**
     * Put the player's hand stack onto the belt at the entry point if there is room.
     * Returns what should remain in the player's hand (air on success).
     */
    public org.bukkit.inventory.ItemStack putSlot(org.bukkit.inventory.ItemStack hand) {
        if (hand == null || hand.getType().isAir())
            return hand;
        if (acceptItem(hand))
            return new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR);
        return hand;
    }

    /** Empty slot {@code i}, resetting its position and pushing the empty item to its display. */
    private void clearSlot(int i) {
        setItem(i, net.minecraft.world.item.ItemStack.EMPTY);
        progress[i] = 0f;
        entryDir[i] = null;
        dirty = true;
        if (displays[i] != null)
            displays[i].setNmsItem(net.minecraft.world.item.ItemStack.EMPTY);
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

        // The conveyor BE's setChanged() is a no-op, so we persist state ourselves to
        // CustomBlockData (the proven, chunk-backed path) — only when an item actually
        // enters/leaves (dirty), never on plain movement.
        if (!stateLoaded) {
            loadState(world);
            stateLoaded = true;
        }
        if (dirty) {
            dirty = false;
            saveState(world);
        }

        // Effective rpm: a body reads its upstream's; the head (start) drives the
        // whole line from a vapor motor adjacent to EITHER end (start or tail), on any
        // of the 3 open sides (front/left/right of that end). The belt direction is
        // fixed at placement, so the motor's side never changes it.
        ConveyorBlockEntity upstream = upstreamConveyor(world, pos, facing);
        if (upstream != null) {
            this.effectiveRpm = upstream.effectiveRpm();
            this.drivingMotor = upstream.drivingMotor();
        } else {
            // Head: find the driving motor at either end (front/left/right of an end).
            dev.arubik.craftengine.rotation.RpmProvider startM = motorAround(world, pos, facing);
            ConveyorBlockEntity tail = tailSegment(world, facing);
            dev.arubik.craftengine.rotation.RpmProvider tailM =
                    motorAround(world, tail.pos(), facing.opposite());
            dev.arubik.craftengine.rotation.RpmProvider motor = strongerMotor(startM, tailM);
            this.drivingMotor = motor;

            if (motor == null) {
                this.effectiveRpm = this.inputRpm; // legacy push path / no motor
            } else {
                // Stress economy: per-part SU scales with SPEED. Each segment imposes
                // (stressImpact × rpm/baseRpm) SU. Size the load from the motor's POTENTIAL rpm
                // (stable even while stalled) and ALWAYS report it, so the motor sees the WHOLE
                // tree (this line + every branch beyond routers) and decides the stall itself.
                float potRpm = motor.potentialRpm();
                float loadRatio = baseRpm > 0 ? potRpm / baseRpm : 0f;
                float lineStress = countSegments(world, facing) * stressImpact * loadRatio;
                motor.reportStressLoad(lineStress);
                // Actual movement follows the motor's live rpm: 0 when it has stalled the network.
                float rpm = motor.getRpm();
                this.effectiveRpm = rpm > 0f ? Math.max(this.inputRpm, rpm) : 0f;
            }
        }

        // Redstone control (inverted): no signal = ON, signal = OFF. A powered belt stops.
        if (redstonePowered(world, pos))
            this.effectiveRpm = 0f;

        // Drive the 'activated' block-state (running when it has RPM) so the model/
        // texture swaps between animated (on) and static (off).
        maybeUpdateActivated(world, pos, this.effectiveRpm > 0f);

        // Auto-pickup cadence: only when the entry point is clear.
        if (++tickCounter >= PICKUP_INTERVAL) {
            tickCounter = 0;
            if (backHasRoom())
                tryPickup(world, pos);
        }

        if (isEmpty()) {
            despawnAll(world, pos);
            return;
        }

        float inc = ConveyorMath.progressPerTick(effectiveRpm, baseRpm, baseTravelTicks);
        if (inc > 0f) {
            float gap = spacing();
            int front = frontSlot();
            // Snapshot so all items advance against a consistent set of positions.
            float[] snapshot = progress.clone();
            for (int i = 0; i < slots; i++) {
                if (slotEmpty(i))
                    continue;
                if (i == front) {
                    float desired = snapshot[i] + inc;
                    if (desired > 1f) {
                        // Reached the end this tick: hand off carrying the OVERSHOOT so the
                        // item keeps a constant speed across the boundary (no hitch). If the
                        // next belt is full it stalls at the very end instead.
                        if (tryHandOff(world, pos, facing, i, desired - 1f))
                            continue;
                        progress[i] = 1f;
                    } else {
                        progress[i] = Math.max(progress[i], desired);
                    }
                    continue;
                }
                // Followers cap behind the item ahead (next-higher progress).
                float ahead = 1f + gap;
                for (int j = 0; j < slots; j++) {
                    if (j == i || slotEmpty(j))
                        continue;
                    if (snapshot[j] > snapshot[i] && snapshot[j] < ahead)
                        ahead = snapshot[j];
                }
                float cap = Math.min(1f, ahead - gap);
                progress[i] = Math.max(progress[i], Math.min(cap, snapshot[i] + inc));
            }
        }

        renderAll(world, pos, facing);
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
            int i = firstEmptySlot();
            if (i < 0)
                return;
            org.bukkit.inventory.ItemStack stack = nearest.getItemStack();
            setItem(i, CraftItemStack.asNMSCopy(stack));
            progress[i] = 0f;
            jitter[i] = randJitter();
            entryDir[i] = facing().opposite();
            dirty = true;
            nearest.remove();
        } catch (Throwable ignored) {
        }
    }

    /**
     * The front item reached the end; move it to the next belt carrying
     * {@code startProgress} (the overshoot past 1.0) so its speed stays constant.
     *
     * @return true if the item left this segment (adopted by next OR dropped off the
     *         end); false if it must stall here (next belt full).
     */
    private boolean tryHandOff(CEWorld world, BlockPos pos, Direction facing, int i, float startProgress) {
        org.bukkit.inventory.ItemStack item = bukkitSlot(i);
        if (item == null)
            return true; // nothing to move
        ConveyorBlockEntity next = downstreamConveyor(world, pos, facing);
        if (next == null) {
            // Not linked, but a conveyor at our exit cell (corner/T) still accepts it.
            for (BlockPos cand : exitCandidates(pos, facing)) {
                ConveyorBlockEntity c = conveyorAt(world, cand);
                if (c != null && c != this) {
                    next = c;
                    break;
                }
            }
        }
        if (next != null) {
            // The item enters `next` from the edge facing back toward us. For an in-line
            // belt that's next's back (straight); for a perpendicular belt it's a side
            // edge → next renders a corner (edge → centre → its own exit).
            Direction entry = facing.opposite();
            if (next.adoptItem(item, jitter[i], displays[i], spawned[i], startProgress, entry)) {
                // Same display entity moves on (no pop), carried jitter (stable rotation).
                displays[i] = null;
                spawned[i] = false;
                clearSlot(i);
                next.renderAll(world, next.pos(), next.facing());
                return true;
            }
            return false; // next is full -> stall at the end
        }
        // No belt ahead, but maybe a generic ConveyorReceiver (machine/buffer) sits at
        // our exit cell. Push into it; if it's full, STALL (don't drop) so the whole
        // 1×1 line backs up until it frees.
        ConveyorReceiver recv = receiverAt(world, pos, facing);
        if (recv != null) {
            if (recv.isFull())
                return false; // stall
            // Seamless: hand our live display entity to a display-capable receiver (funnel) so the
            // item doesn't flicker across the boundary (same as belt->belt adoption).
            if (recv instanceof ConveyorDisplayReceiver dr
                    && dr.adoptConveyorItem(item, jitter[i], displays[i], spawned[i], facing)) {
                displays[i] = null; // entity moved on (no pop/despawn)
                spawned[i] = false;
                clearSlot(i);
                return true;
            }
            if (recv.receiveConveyorItem(item, facing, jitter[i])) { // carry rotation into the router
                despawnSlot(world, pos, i);
                clearSlot(i);
                return true;
            }
            return false; // couldn't take right now -> stall
        }
        // Truly nothing ahead: drop it off the end.
        dropAtEnd(world, pos, facing, item);
        despawnSlot(world, pos, i);
        clearSlot(i);
        return true;
    }

    /** A non-conveyor {@link ConveyorReceiver} controller at one of our exit cells. */
    private ConveyorReceiver receiverAt(CEWorld world, BlockPos pos, Direction facing) {
        for (BlockPos cand : exitCandidates(pos, facing)) {
            BlockEntity be = world.getBlockEntityAtIfLoaded(cand);
            if (be != null && be.controller instanceof ConveyorReceiver r
                    && !(be.controller instanceof ConveyorBlockEntity))
                return r;
        }
        return null;
    }

    /**
     * Accept a handed-off item at {@code startProgress}, adopting its carried jitter
     * and live display entity so the visual is continuous across the boundary.
     */
    /** Inverted redstone: true (= OFF) when the block receives any redstone power. */
    private boolean redstonePowered(CEWorld world, BlockPos pos) {
        try {
            org.bukkit.World bw = (org.bukkit.World) world.world().platformWorld();
            return bw != null && bw.getBlockAt(pos.x(), pos.y(), pos.z()).isBlockIndirectlyPowered();
        } catch (Throwable t) {
            return false;
        }
    }

    /** Comparator signal from how full the segment's item slots are (like a container). */
    public int analogSignal() {
        int occupied = 0;
        for (int i = 0; i < slots; i++)
            if (!slotEmpty(i))
                occupied++;
        if (occupied == 0)
            return 0;
        return Math.min(15, (int) Math.floor((occupied / (float) slots) * 14f) + 1);
    }

    // ---- ConveyorReceiver: belts ARE receivers (generic contract) ----

    @Override
    public boolean isFull() {
        return !backHasRoom();
    }

    @Override
    public boolean receiveConveyorItem(org.bukkit.inventory.ItemStack stack, Direction sourceFacing) {
        return receiveConveyorItem(stack, sourceFacing, randJitter());
    }

    @Override
    public boolean receiveConveyorItem(org.bukkit.inventory.ItemStack stack, Direction sourceFacing, float carriedJitter) {
        if (stack == null || stack.getType().isAir() || !backHasRoom())
            return false;
        int i = firstEmptySlot();
        if (i < 0)
            return false;
        setItem(i, CraftItemStack.asNMSCopy(stack));
        progress[i] = 0f;
        jitter[i] = carriedJitter; // preserve the item's rotation across the hop
        entryDir[i] = sourceFacing != null ? sourceFacing.opposite() : facing().opposite();
        dirty = true;
        return true;
    }

    boolean adoptItem(org.bukkit.inventory.ItemStack stack, float carriedJitter,
            ConveyorItemDisplay disp, boolean wasSpawned, float startProgress, Direction entry) {
        if (stack == null || stack.getType().isAir())
            return false;
        // The incoming item enters at `st`; it must stay a full gap BEHIND every item
        // already on this segment (and be the back-most). Otherwise reject → the source
        // belt stalls, instead of two items overlapping at the boundary.
        float st = ConveyorMath.clamp01(startProgress);
        float gap = spacing();
        int i = -1;
        for (int j = 0; j < slots; j++) {
            if (slotEmpty(j)) {
                if (i < 0)
                    i = j;
                continue;
            }
            if (progress[j] < st + gap)
                return false; // would overlap / break ordering
        }
        if (i < 0)
            return false; // no free slot
        setItem(i, CraftItemStack.asNMSCopy(stack));
        progress[i] = st;
        jitter[i] = carriedJitter;
        entryDir[i] = (entry != null) ? entry : facing().opposite();
        displays[i] = disp;       // adopt the existing entity (no pop)
        spawned[i] = wasSpawned;
        dirty = true;
        return true;
    }

    /**
     * Adopt an item + its live display from a non-belt source (a funnel ejecting onto us) and render
     * immediately, so the hand-off is seamless (no despawn/respawn flicker). Returns false if full.
     */
    public boolean adoptFromFunnel(CEWorld world, org.bukkit.inventory.ItemStack stack, float carriedJitter,
            ConveyorItemDisplay disp, boolean wasSpawned, Direction entry) {
        if (adoptItem(stack, carriedJitter, disp, wasSpawned, 0f, entry)) {
            renderAll(world, pos(), facing());
            return true;
        }
        return false;
    }

    /** Drop every carried item off the end and empty the slots. */
    private void dropAllItems(CEWorld world, BlockPos pos, Direction facing) {
        for (int i = 0; i < slots; i++) {
            if (slotEmpty(i))
                continue;
            dropAtEnd(world, pos, facing, bukkitSlot(i));
            clearSlot(i);
        }
        // Wipe persisted state so a future block at this pos doesn't read stale items.
        try {
            dev.arubik.craftengine.util.CustomBlockData data = blockData(world);
            if (data != null)
                data.clear();
        } catch (Throwable ignored) {
        }
    }

    private void dropAtEnd(CEWorld world, BlockPos pos, Direction facing, org.bukkit.inventory.ItemStack item) {
        try {
            org.bukkit.World bukkitWorld = (org.bukkit.World) world.world().platformWorld();
            if (bukkitWorld != null && item != null && !item.getType().isAir()) {
                Vector3f end = endRel(facing);
                // Spawn it PAST the belt's front face (+0.45 along facing) so it lands outside
                // this belt's own auto-pickup radius — otherwise the belt grabs it right back
                // and the item looks frozen at the end.
                double dx = pos.x() + end.x + facing.stepX() * 0.45;
                double dy = pos.y() + end.y;
                double dz = pos.z() + end.z + facing.stepZ() * 0.45;
                org.bukkit.entity.Item dropped = bukkitWorld.dropItem(new org.bukkit.Location(bukkitWorld, dx, dy, dz),
                        item);
                // Launch it off the end keeping the belt's heading (facing + slope) and a
                // standard ejection speed.
                float inc = ConveyorMath.progressPerTick(effectiveRpm, baseRpm, baseTravelTicks);
                if (inc <= 0f)
                    inc = ConveyorMath.progressPerTick(BASE_RPM, BASE_RPM, BASE_TRAVEL_TICKS);
                int sy = slope().stepY();
                dropped.setVelocity(new org.bukkit.util.Vector(
                        facing.stepX() * Math.max(inc, 0.12), sy * inc + 0.05, facing.stepZ() * Math.max(inc, 0.12)));
                dropped.setPickupDelay(20); // ~1s so it clears the belt before any re-pickup
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

    /** Render every occupied slot's item at its own position; hide emptied ones. */
    private void renderAll(CEWorld world, BlockPos pos, Direction facing) {
        List<Player> viewers = world.world().getTrackedBy(new ChunkPos(pos));
        int slopeY = slope().stepY();
        Vector3f startP = startRel(facing);
        Vector3f exit = endRel(facing);
        // Centre Y is the midpoint of start/end so a sloped belt's mid point sits ON the
        // ramp (not BELT_TOP_Y, which made the item sink in the middle on slopes).
        Vector3f center = new Vector3f(0.5f, (startP.y + exit.y) * 0.5f, 0.5f);
        for (int i = 0; i < slots; i++) {
            if (slotEmpty(i)) {
                despawnSlotFor(viewers, i);
                continue;
            }
            if (displays[i] == null)
                displays[i] = new ConveyorItemDisplay();
            ConveyorItemDisplay d = displays[i];
            d.setNmsItem(getItem(i).copy());

            Direction entry = entryDir[i] != null ? entryDir[i] : facing.opposite();
            boolean inLine = entry == facing.opposite();
            float p = progress[i];

            // Path: entry edge -> centre (p<0.5) -> exit edge (p>=0.5). For an in-line
            // item this is just the straight start->end line; for a side entry (T-junction)
            // it bends through the centre like a corner.
            Vector3f rel;
            org.joml.Quaternionf rot;
            if (p < 0.5f) {
                Vector3f entryPt = inLine ? startRel(facing)
                        : new Vector3f(0.5f + entry.stepX() * 0.5f, ConveyorMath.BELT_TOP_Y,
                                0.5f + entry.stepZ() * 0.5f);
                rel = ConveyorMath.interpolate(entryPt, center, p * 2f);
                // Heading inward = opposite of the entry edge.
                rot = inLine ? ConveyorMath.itemRotation(facing.stepX(), facing.stepZ(), slopeY)
                        : ConveyorMath.itemRotation(-entry.stepX(), -entry.stepZ(), 0);
            } else {
                rel = ConveyorMath.interpolate(center, exit, (p - 0.5f) * 2f);
                rot = ConveyorMath.itemRotation(facing.stepX(), facing.stepZ(), slopeY);
            }
            rot.rotateY(jitter[i]);
            d.setRotation(rot);

            double wx = pos.x() + rel.x, wy = pos.y() + rel.y, wz = pos.z() + rel.z;
            // Per-viewer render: spawns for any tracked player who hasn't seen this item
            // yet (every player sees it, not just whoever was online first) and re-pushes
            // metadata when the rotation changed (slope→flat, corner turn).
            boolean meta = d.consumeRotationDirty();
            d.render(viewers, wx, wy, wz, meta);
            spawned[i] = true;
        }
    }

    private void despawnSlotFor(List<Player> viewers, int i) {
        // Force-remove even if the spawned flag is stale (broke while an item was entering/leaving).
        if (displays[i] != null) {
            for (Player p : viewers)
                displays[i].despawn(p);
            displays[i].clearShown();
            spawned[i] = false;
        }
    }

    private void despawnSlot(CEWorld world, BlockPos pos, int i) {
        despawnSlotFor(world.world().getTrackedBy(new ChunkPos(pos)), i);
    }

    /** Despawn all item displays for this segment. */
    private void despawnAll(CEWorld world, BlockPos pos) {
        List<Player> viewers = world.world().getTrackedBy(new ChunkPos(pos));
        for (int i = 0; i < slots; i++)
            despawnSlotFor(viewers, i);
    }

    // ---------------- linked-list neighbour lookup (by reference) ----------------

    /** The conveyor controller at {@code rel}, or null if absent/unloaded. */
    static ConveyorBlockEntity conveyorAt(CEWorld world, BlockPos rel) {
        BlockEntity be = world.getBlockEntityAtIfLoaded(rel);
        if (be != null && be.controller instanceof ConveyorBlockEntity conveyor)
            return conveyor;
        return null;
    }

    /** Walk downstream from this segment to the tail (no further downstream). */
    private ConveyorBlockEntity tailSegment(CEWorld world, Direction facing) {
        ConveyorBlockEntity cur = this;
        for (int i = 0; i < MAX_LENGTH; i++) {
            ConveyorBlockEntity next = cur.downstreamConveyor(world, cur.pos(), facing);
            if (next == null || next == cur)
                break;
            cur = next;
        }
        return cur;
    }

    /** Number of segments in the line starting at this (head) and going downstream. */
    private int countSegments(CEWorld world, Direction facing) {
        int n = 1;
        ConveyorBlockEntity cur = this;
        for (int i = 0; i < MAX_LENGTH; i++) {
            ConveyorBlockEntity next = cur.downstreamConveyor(world, cur.pos(), facing);
            if (next == null || next == cur)
                break;
            n++;
            cur = next;
        }
        return n;
    }

    /**
     * The vapor motor on a horizontal neighbour of {@code pos} with the highest RPM,
     * skipping {@code exclude} (the side where the belt line continues). Lets a motor
     * power the belt from the front, left or right of an end segment.
     */
    private dev.arubik.craftengine.rotation.RpmProvider motorAround(
            CEWorld world, BlockPos pos, Direction exclude) {
        dev.arubik.craftengine.rotation.RpmProvider best = null;
        for (Direction d : Direction.values()) {
            if (d.stepY() != 0 || d == exclude)
                continue;
            BlockEntity be = world.getBlockEntityAtIfLoaded(pos.relative(d));
            if (be != null
                    && be.controller instanceof dev.arubik.craftengine.rotation.RpmProvider motor) {
                // Only accept power from a source whose emitter reaches this belt (front for a
                // motor; any output side for a conveyor router relay).
                if (!motor.rpmReaches(pos))
                    continue;
                if (best == null || preferMotor(motor, best))
                    best = motor;
            }
        }
        return best;
    }

    /**
     * Pick which adjacent power provider drives this belt. A real motor SOURCE always wins over a
     * relay (conveyor router): so a belt sitting next to its OWN motor uses that motor and that whole
     * branch is PRUNED from any upstream motor's tree (it no longer reports SU through the router).
     * Between two of the same kind, the higher RPM wins.
     */
    private static boolean preferMotor(dev.arubik.craftengine.rotation.RpmProvider cand,
            dev.arubik.craftengine.rotation.RpmProvider cur) {
        boolean candSrc = cand.isRpmSource();
        boolean curSrc = cur.isRpmSource();
        if (candSrc != curSrc)
            return candSrc; // a source beats a relay regardless of RPM
        return cand.getRpm() > cur.getRpm();
    }

    /** Pick the motor with the higher RPM (null-safe). */
    private static dev.arubik.craftengine.rotation.RpmProvider strongerMotor(
            dev.arubik.craftengine.rotation.RpmProvider a,
            dev.arubik.craftengine.rotation.RpmProvider b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        return a.getRpm() >= b.getRpm() ? a : b;
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

        clearStaleData(bukkitWorld, target);
        ConveyorBlockEntity created = conveyorAt(world, target);
        if (created != null)
            created.setPrevPos(pos);

        ConveyorPart newPart = (prevPos == null) ? ConveyorPart.START : ConveyorPart.MIDDLE;
        applyPart(world, pos, facing, slope, newPart);
        return true;
    }

    /**
     * Extend the belt backwards from this START (head) segment: place a new
     * conveyor one cell UPSTREAM (opposite facing, mirrored slope Y), make it the
     * new head (START) and demote this segment to MIDDLE.
     *
     * @return true if a segment was added
     */
    public boolean extendStart(CEWorld world, BlockPos pos, Direction facing, ConveyorSlope slope) {
        if (prevPos != null)
            return false; // only the real head can grow backwards
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

        BlockPos target = pos.relative(facing.opposite());
        int dy = slope.stepY();
        if (dy != 0)
            target = new BlockPos(target.x(), target.y() - dy, target.z()); // upstream sits lower on up-ramps

        org.bukkit.block.Block targetBlock = bukkitWorld.getBlockAt(target.x(), target.y(), target.z());
        if (!isReplaceable(targetBlock))
            return false;

        BlockDefinition def = blockEntity().blockState().owner().value();
        Key blockId = def.id();

        ImmutableBlockState newState = stateWith(def.defaultState(), facing, slope, ConveyorPart.START);
        org.bukkit.Location loc = new org.bukkit.Location(bukkitWorld, target.x(), target.y(), target.z());
        boolean placed = net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.place(
                loc, newState, UpdateFlags.UPDATE_ALL, false);
        if (!placed) {
            placed = net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.place(loc, blockId, false);
            if (!placed)
                return false;
        }

        // The new segment becomes the head; this one now links back to it as MIDDLE.
        clearStaleData(bukkitWorld, target);
        setPrevPos(target);
        applyPart(world, pos, facing, slope, ConveyorPart.MIDDLE);
        return true;
    }

    /** Wipe persisted belt data at a freshly-placed cell (avoids stale items/links). */
    private static void clearStaleData(org.bukkit.World bukkitWorld, BlockPos at) {
        try {
            dev.arubik.craftengine.util.CustomBlockData.from(
                    bukkitWorld.getBlockAt(at.x(), at.y(), at.z())).clear();
        } catch (Throwable ignored) {
        }
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
     * Handle removal of this segment (the player is already breaking THIS block, so
     * vanilla loot drops its own block item). Topology decides the rest:
     * <ul>
     *   <li><b>START</b> (no upstream): the downstream neighbour becomes the new
     *       START — the start "moves", belt shortens by one.</li>
     *   <li><b>END</b> (no downstream): the upstream neighbour becomes the new END —
     *       the end "moves", belt shortens by one.</li>
     *   <li><b>MIDDLE</b> (both sides): destroy the WHOLE belt, dropping every other
     *       segment as a block item plus any carried slot items.</li>
     *   <li><b>lone</b> segment: nothing extra.</li>
     * </ul>
     */
    /** Guards against re-entrancy while a whole-line teardown removes sibling blocks. */
    private static boolean teardownInProgress = false;

    public void onBroken(CEWorld world, BlockPos pos, Direction facing) {
        // A whole-belt teardown removes sibling blocks via remove(), which re-fires this
        // callback. Ignore those nested calls so they don't run partial START/END logic.
        if (teardownInProgress)
            return;

        ConveyorBlockEntity up = upstreamConveyor(world, pos, facing);
        ConveyorBlockEntity down = downstreamConveyor(world, pos, facing);

        // Drop ALL carried items + kill the displays (this block is going away).
        dropAllItems(world, pos, facing);
        despawnAll(world, pos);

        boolean isStart = (up == null);
        boolean isEnd = (down == null);

        if (isStart && isEnd) {
            return; // lone segment
        }
        if (isStart) {
            // 2-segment belt (the neighbour is the only other one): break BOTH.
            if (down.downstreamConveyor(world, down.pos(), down.facing()) == null) {
                teardownInProgress = true;
                try {
                    down.destroySegmentWithDrops(world);
                } finally {
                    teardownInProgress = false;
                }
                return;
            }
            // Otherwise the downstream neighbour becomes the new head/START.
            down.setPrevPos(null);
            down.applyPart(world, down.pos(), down.facing(), down.slope(), ConveyorPart.START);
            return;
        }
        if (isEnd) {
            // 2-segment belt: break BOTH.
            if (up.prevPos == null) {
                teardownInProgress = true;
                try {
                    up.destroySegmentWithDrops(world);
                } finally {
                    teardownInProgress = false;
                }
                return;
            }
            // Otherwise the upstream neighbour becomes the new tail/END.
            up.applyPart(world, up.pos(), up.facing(), up.slope(), ConveyorPart.END);
            return;
        }

        // MIDDLE break: tear down the whole belt, dropping everything.
        Set<BlockPos> visited = new HashSet<>();
        visited.add(pos);
        teardownInProgress = true;
        try {
            ConveyorBlockEntity u = up;
            while (u != null && visited.add(u.pos())) {
                ConveyorBlockEntity next = u.upstreamConveyor(world, u.pos(), u.facing());
                u.destroySegmentWithDrops(world);
                u = next;
            }
            ConveyorBlockEntity d = down;
            while (d != null && visited.add(d.pos())) {
                ConveyorBlockEntity next = d.downstreamConveyor(world, d.pos(), d.facing());
                d.destroySegmentWithDrops(world);
                d = next;
            }
        } finally {
            teardownInProgress = false;
        }
    }

    /** Remove this segment's block (dropping its block item + carried slot stack). */
    private void destroySegmentWithDrops(CEWorld world) {
        BlockPos pos = pos();
        Direction facing = facing();
        dropAllItems(world, pos, facing);
        despawnAll(world, pos);
        try {
            org.bukkit.World bukkitWorld = (org.bukkit.World) world.world().platformWorld();
            if (bukkitWorld != null) {
                org.bukkit.block.Block b = bukkitWorld.getBlockAt(pos.x(), pos.y(), pos.z());
                // remove + drop the conveyor block item (loot_table/self); fall back to AIR.
                if (!net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.remove(b, true))
                    b.setType(org.bukkit.Material.AIR, false);
            }
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
            ImmutableBlockState cur = blockEntity().blockState();
            if (cur == null)
                return;
            // Change ONLY the `part` property in place. Re-placing the block (UPDATE_ALL)
            // would recreate the block entity and wipe its prevPos link + slot, leaving
            // neighbours desynced — use a flag-2 NMS setBlock like maybeUpdateActivated.
            ImmutableBlockState ns = withEnum(cur, PROP_PART, part.name());
            if (ns == cur)
                return;
            Object level = world.world().minecraftWorld();
            Object bp = dev.arubik.craftengine.util.MNms.INSTANCE.constructor$BlockPos(pos.x(), pos.y(), pos.z());
            Object nms = ns.customBlockState().minecraftState();
            dev.arubik.craftengine.util.MNms.INSTANCE.method$LevelWriter$setBlock(level, bp, nms, 2);
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

    // ---------------- persistence (CustomBlockData, chunk-backed) ----------------

    private dev.arubik.craftengine.util.CustomBlockData blockData(CEWorld world) {
        org.bukkit.World bw = (org.bukkit.World) world.world().platformWorld();
        if (bw == null)
            return null;
        return dev.arubik.craftengine.util.CustomBlockData.from(
                bw.getBlockAt(pos().x(), pos().y(), pos().z()));
    }

    private dev.arubik.craftengine.util.TypedKey<Float> progKey(int i) {
        return dev.arubik.craftengine.util.TypedKey.of("craftengine", "cv_prog" + i,
                org.bukkit.persistence.PersistentDataType.FLOAT);
    }

    private dev.arubik.craftengine.util.TypedKey<Float> jitKey(int i) {
        return dev.arubik.craftengine.util.TypedKey.of("craftengine", "cv_jit" + i,
                org.bukkit.persistence.PersistentDataType.FLOAT);
    }

    private dev.arubik.craftengine.util.TypedKey<String> entryKey(int i) {
        return dev.arubik.craftengine.util.TypedKey.of("craftengine", "cv_entry" + i,
                org.bukkit.persistence.PersistentDataType.STRING);
    }

    private static final dev.arubik.craftengine.util.TypedKey<String> PREV_KEY =
            dev.arubik.craftengine.util.TypedKey.of("craftengine", "cv_prev",
                    org.bukkit.persistence.PersistentDataType.STRING);

    /** Flush items + per-slot positions + prevPos to CustomBlockData (called on change). */
    private void saveState(CEWorld world) {
        try {
            dev.arubik.craftengine.util.CustomBlockData data = blockData(world);
            if (data == null)
                return;
            data.set(dev.arubik.craftengine.util.TypedKeys.CONTENTS,
                    dev.arubik.craftengine.util.ArrayItemStackWithSlot.from(this.inventory));
            for (int i = 0; i < slots; i++) {
                data.set(progKey(i), progress[i]);
                data.set(jitKey(i), jitter[i]);
                data.set(entryKey(i), entryDir[i] != null ? entryDir[i].name() : "");
            }
            data.set(PREV_KEY, prevPos != null ? (prevPos.x() + "," + prevPos.y() + "," + prevPos.z()) : "");
        } catch (Throwable ignored) {
        }
    }

    /** Restore items + positions + prevPos from CustomBlockData on first tick. */
    private void loadState(CEWorld world) {
        try {
            dev.arubik.craftengine.util.CustomBlockData data = blockData(world);
            if (data == null)
                return;
            // Only restore when there IS persisted content — never clobber a freshly
            // placed segment or one that just received a hand-off before its first tick.
            java.util.Optional<java.util.List<net.minecraft.world.ItemStackWithSlot>> contents =
                    data.getOptional(dev.arubik.craftengine.util.TypedKeys.CONTENTS);
            if (contents.isPresent()) {
                for (int i = 0; i < slots; i++)
                    setItem(i, net.minecraft.world.item.ItemStack.EMPTY);
                for (net.minecraft.world.ItemStackWithSlot it : contents.get())
                    if (it.slot() >= 0 && it.slot() < slots)
                        setItem(it.slot(), it.stack());
                for (int i = 0; i < slots; i++) {
                    progress[i] = data.getOrDefault(progKey(i), 0f);
                    jitter[i] = data.getOrDefault(jitKey(i), 0f);
                    String e = data.getOrDefault(entryKey(i), "");
                    if (e != null && !e.isEmpty()) {
                        try {
                            entryDir[i] = Direction.valueOf(e);
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
            }
            String pp = data.getOrDefault(PREV_KEY, "");
            if (pp != null && !pp.isEmpty()) {
                String[] xyz = pp.split(",");
                if (xyz.length == 3)
                    this.prevPos = new BlockPos(Integer.parseInt(xyz[0]),
                            Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2]));
            }
        } catch (Throwable ignored) {
        }
    }

    // ---------------- persistence (legacy tag; kept harmless) ----------------

    @Override
    public void saveCustomData(CompoundTag tag) {
        // The inventory slots (the items themselves) are persisted by the parent.
        super.saveCustomData(tag);
        for (int i = 0; i < slots; i++) {
            tag.putFloat("progress" + i, progress[i]);
            tag.putFloat("jitter" + i, jitter[i]);
            tag.putString("entry" + i, entryDir[i] != null ? entryDir[i].name() : "");
        }
        if (prevPos != null) {
            tag.putInt("prevX", prevPos.x());
            tag.putInt("prevY", prevPos.y());
            tag.putInt("prevZ", prevPos.z());
        }
    }

    @Override
    public void loadCustomData(CompoundTag tag) {
        super.loadCustomData(tag);
        for (int i = 0; i < slots; i++) {
            this.progress[i] = tag.getFloat("progress" + i, 0f);
            this.jitter[i] = tag.getFloat("jitter" + i, 0f);
            String e = tag.getString("entry" + i, "");
            if (e != null && !e.isEmpty()) {
                try {
                    this.entryDir[i] = Direction.valueOf(e);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        if (tag.containsKey("prevX") && tag.containsKey("prevY") && tag.containsKey("prevZ")) {
            this.prevPos = new BlockPos(tag.getInt("prevX"), tag.getInt("prevY"), tag.getInt("prevZ"));
        }
    }

    @Override
    public void onRemove() {
        super.onRemove();
    }
}
