package dev.arubik.craftengine.conveyor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.joml.Vector3f;

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
 * Create-style conveyor belt segment, driven entirely by BLOCK-STATE PROPERTIES
 * and a linked-list segment model.
 *
 * <p><b>Block properties</b> (read live from {@link BlockEntity#blockState()},
 * never cached): {@code facing} (NORTH/EAST/SOUTH/WEST horizontal travel toward
 * the next segment), {@code slope} (FLAT/UP/DOWN, 45-degree) and {@code part}
 * (START/MIDDLE/END, END being the growth/exit point). Missing properties fall
 * back gracefully (facing -&gt; ctor default or NORTH, slope -&gt; FLAT, part -&gt; END).</p>
 *
 * <p><b>Linked list:</b> each segment persists {@link #prevPos} (toward START).
 * A segment is the END when the block at {@code pos.relative(facing)} is not a
 * conveyor. Validation is by reference — the downstream segment is only "ours"
 * when its {@code prevPos} equals our pos. No global per-tick rescan.</p>
 *
 * <p>Holds one in-transit item advanced 0..1 by speed (RPM via {@link RpmConsumer}).
 * On arrival it is handed to the downstream segment via {@link #acceptItem}, else
 * dropped. Start/end render points ramp Y for UP/DOWN slopes.</p>
 */
public class ConveyorBlockEntity extends BlockEntityController implements RpmConsumer {

    /** Reference rpm at which the belt runs at base speed. */
    public static final float BASE_RPM = 64f;
    /** Ticks for one item to cross one segment at BASE_RPM. */
    public static final int BASE_TRAVEL_TICKS = 16;
    /** Maximum number of segments a single belt may grow to. */
    public static final int MAX_LENGTH = 64;

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

    // single-item buffer (Bukkit stack so persistence is trivial); null = empty
    private org.bukkit.inventory.ItemStack carried;
    private float progress = 0f;

    // render
    private ConveyorItemDisplay display;
    private boolean displaySpawned = false;

    public ConveyorBlockEntity(BlockEntity blockEntity) {
        super(blockEntity);
    }

    public ConveyorBlockEntity(BlockEntity blockEntity, Direction defaultFacing) {
        super(blockEntity);
        if (defaultFacing != null)
            this.defaultFacing = defaultFacing;
    }

    private BlockPos pos() {
        return blockEntity().pos();
    }

    // ---------------- live property reads (never cached) ----------------

    /** Read an enum property's selected value NAME from a live state, or null. */
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

    public boolean isEmpty() {
        return carried == null || carried.getType().isAir();
    }

    public BlockPos prevPos() {
        return prevPos;
    }

    public void setPrevPos(BlockPos prevPos) {
        this.prevPos = prevPos;
    }

    /**
     * Try to place an item onto this belt at the start point.
     *
     * @return false if the belt already carries an item (full)
     */
    public boolean acceptItem(org.bukkit.inventory.ItemStack stack) {
        if (!isEmpty() || stack == null || stack.getType().isAir())
            return false;
        this.carried = stack.clone();
        this.progress = 0f;
        if (display != null)
            display.setNmsItem(CraftItemStack.asNMSCopy(this.carried));
        return true;
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

        // 1. Effective rpm. Head = upstream neighbour is not a conveyor (keeps motor
        //    rpm); a body segment instead reads the upstream conveyor's effective rpm.
        ConveyorBlockEntity upstream = upstreamConveyor(world, pos, facing);
        if (upstream != null) {
            this.effectiveRpm = upstream.effectiveRpm();
        } else {
            this.effectiveRpm = this.inputRpm; // head driven by motor
        }

        if (isEmpty()) {
            ensureDisplayHidden(world, pos);
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

    private void handOff(CEWorld world, BlockPos pos, Direction facing) {
        ConveyorBlockEntity next = downstreamConveyor(world, pos, facing);
        org.bukkit.inventory.ItemStack item = this.carried;
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
            if (bukkitWorld != null) {
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
            display.setNmsItem(CraftItemStack.asNMSCopy(this.carried));
        }
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
        this.carried = null;
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

    /**
     * The downstream segment: the conveyor at {@code pos.relative(facing)} whose
     * {@code prevPos} points back at {@code pos}. The exit may be +/-1 Y for a
     * sloped belt, so we also probe above/below. Reference-validated.
     */
    ConveyorBlockEntity downstreamConveyor(CEWorld world, BlockPos pos, Direction facing) {
        for (BlockPos cand : exitCandidates(pos, facing)) {
            ConveyorBlockEntity c = conveyorAt(world, cand);
            if (c != null && pos.equals(c.prevPos()))
                return c;
        }
        return null;
    }

    /** The upstream segment referenced by {@link #prevPos}, if it is a conveyor. */
    ConveyorBlockEntity upstreamConveyor(CEWorld world, BlockPos pos, Direction facing) {
        if (prevPos == null)
            return null;
        return conveyorAt(world, prevPos);
    }

    /** Candidate exit positions for a (possibly sloped) segment, in priority order. */
    static List<BlockPos> exitCandidates(BlockPos pos, Direction facing) {
        BlockPos flat = pos.relative(facing);
        List<BlockPos> list = new ArrayList<>(3);
        list.add(flat); // FLAT
        list.add(new BlockPos(flat.x(), flat.y() + 1, flat.z())); // UP
        list.add(new BlockPos(flat.x(), flat.y() - 1, flat.z())); // DOWN
        return list;
    }

    /** The exit position for THIS segment given its live facing + slope. */
    BlockPos exitPos(Direction facing) {
        BlockPos flat = pos().relative(facing);
        int dy = slope().stepY();
        return dy == 0 ? flat : new BlockPos(flat.x(), flat.y() + dy, flat.z());
    }

    /** True when no downstream conveyor follows this segment (this is the tail). */
    boolean isTail(CEWorld world, Direction facing) {
        return downstreamConveyor(world, pos(), facing) == null;
    }

    // ---------------- extend (right-click on END) ----------------

    /**
     * Extend the belt from this END segment: place a new conveyor at the exit
     * position (respecting slope Y), link it (prevPos = this pos, part = END),
     * and demote this segment to MIDDLE (or START if it had no prev). Only
     * extends when the target is air/replaceable and the belt is under
     * {@link #MAX_LENGTH}.
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

        // New segment: facing + slope + part=END, placed at target.
        ImmutableBlockState newState = stateWith(def.defaultState(), facing, slope, ConveyorPart.END);
        org.bukkit.Location loc = new org.bukkit.Location(bukkitWorld, target.x(), target.y(), target.z());
        boolean placed = net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.place(
                loc, newState, UpdateFlags.UPDATE_ALL, false);
        if (!placed) {
            // fall back to id-based placement
            placed = net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.place(loc, blockId, false);
            if (!placed)
                return false;
        }

        // Link the freshly placed segment back to us.
        ConveyorBlockEntity created = conveyorAt(world, target);
        if (created != null)
            created.setPrevPos(pos);

        // Demote this segment: END -> MIDDLE (or START if no upstream).
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
     * Handle removal of this segment (called from the behavior's
     * affectNeighborsAfterRemoval).
     *
     * <ul>
     *   <li>END (tail): just shorten — promote the upstream segment back to END.</li>
     *   <li>START/MIDDLE: break the WHOLE belt — walk upstream via prevPos and
     *       downstream via reference, removing every segment and dropping its
     *       in-transit item.</li>
     * </ul>
     */
    public void onBroken(CEWorld world, BlockPos pos, Direction facing) {
        ConveyorBlockEntity down = downstreamConveyor(world, pos, facing);
        boolean tail = (down == null);

        // drop our own carried item
        if (!isEmpty())
            dropAtEnd(world, pos, facing, this.carried);

        if (tail) {
            ConveyorBlockEntity up = upstreamConveyor(world, pos, facing);
            if (up != null)
                up.applyPart(world, up.pos(), up.facing(), up.slope(), ConveyorPart.END);
            return;
        }

        // Not the tail: tear down the whole belt.
        Set<BlockPos> visited = new HashSet<>();
        visited.add(pos);

        // upstream chain
        ConveyorBlockEntity up = upstreamConveyor(world, pos, facing);
        while (up != null && visited.add(up.pos())) {
            ConveyorBlockEntity next = up.upstreamConveyor(world, up.pos(), up.facing());
            up.removeSelf(world);
            up = next;
        }
        // downstream chain
        ConveyorBlockEntity d = down;
        while (d != null && visited.add(d.pos())) {
            ConveyorBlockEntity next = d.downstreamConveyor(world, d.pos(), d.facing());
            d.removeSelf(world);
            d = next;
        }
    }

    /** Remove this segment's block from the world, dropping its in-transit item. */
    private void removeSelf(CEWorld world) {
        BlockPos pos = pos();
        Direction facing = facing();
        if (!isEmpty()) {
            dropAtEnd(world, pos, facing, this.carried);
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

    /** Build a state from a base with the given facing/slope/part (skips absent props). */
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

    /** Re-place this block at {@code pos} with {@code part} updated, preserving facing/slope. */
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

    private static boolean isReplaceable(org.bukkit.block.Block block) {
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
        tag.putFloat("progress", progress);
        if (prevPos != null) {
            tag.putInt("prevX", prevPos.x());
            tag.putInt("prevY", prevPos.y());
            tag.putInt("prevZ", prevPos.z());
        }
        if (!isEmpty()) {
            tag.putByteArray("item", carried.serializeAsBytes());
        }
    }

    @Override
    public void loadCustomData(CompoundTag tag) {
        this.progress = tag.getFloat("progress", 0f);
        if (tag.containsKey("prevX") && tag.containsKey("prevY") && tag.containsKey("prevZ")) {
            this.prevPos = new BlockPos(tag.getInt("prevX"), tag.getInt("prevY"), tag.getInt("prevZ"));
        }
        if (tag.containsKey("item")) {
            try {
                this.carried = org.bukkit.inventory.ItemStack.deserializeBytes(tag.getByteArray("item"));
            } catch (Throwable ignored) {
                this.carried = null;
            }
        }
    }

    @Override
    public void onRemove() {
        super.onRemove();
    }
}
