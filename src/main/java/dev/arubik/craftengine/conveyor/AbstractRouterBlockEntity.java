package dev.arubik.craftengine.conveyor;

import org.bukkit.craftbukkit.inventory.CraftItemStack;

import dev.arubik.craftengine.block.entity.PersistentWorldlyBlockEntity;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;

/**
 * Base for the conveyor-network "router" blocks (merger / splitter). Each holds a
 * tiny internal item buffer ({@code slots} stacks). Belts deliver into the buffer
 * via {@link ConveyorReceiver#receiveConveyorItem}; every tick {@link #route} tries
 * to push buffered stacks back onto downstream belts/receivers. Directionality (which
 * sides are inputs vs. outputs) is enforced per subclass.
 *
 * <p>Buffered items are invisible (no display entity) — these blocks are pass-through
 * routers, not transport surfaces.</p>
 */
public abstract class AbstractRouterBlockEntity extends PersistentWorldlyBlockEntity
        implements ConveyorDisplayReceiver, dev.arubik.craftengine.rotation.RpmProvider {

    public static final String PROP_FACING = "facing";
    /** Boolean block-state property toggled on/off with relayed RPM (for animated vs idle models). */
    public static final String PROP_ACTIVATED = "activated";
    private Boolean lastActivated = null;

    protected Direction defaultFacing = Direction.NORTH;
    protected final int slots;

    /** RPM relayed from the feeding belt line, and the real motor driving it (for stress forwarding). */
    protected float relayedRpm = 0f; // RPM exposed to neighbours (0 when redstone-isolated)
    protected float moveRpm = 0f;    // speed for THIS block's own item movement (base while redstoned)
    protected dev.arubik.craftengine.rotation.RpmProvider relayMotor;

    /** Belt sides that FEED this router (inputs). */
    protected abstract Direction[] inputSides();

    /** Belt sides this router DRIVES (outputs) — they inherit the relayed RPM. */
    protected abstract Direction[] outputSides();

    /** Read the strongest feeding belt's RPM + its motor so the router relays power to its outputs. */
    private void updateRpm(CEWorld world, BlockPos pos) {
        float best = 0f;
        float bestPot = 0f;
        boolean beltFeeds = false; // a conveyor line feeds this router (vs a pure funnel/IO push)
        dev.arubik.craftengine.rotation.RpmProvider motor = null;
        for (Direction d : inputSides()) {
            BlockEntity be = world.getBlockEntityAtIfLoaded(pos.relative(d));
            if (be != null && be.controller instanceof ConveyorBlockEntity belt) {
                beltFeeds = true;
                best = Math.max(best, belt.effectiveRpm()); // live speed (0 while the motor stalls)
                // Keep the driving motor even when the belt is stalled (effectiveRpm 0): pick by
                // POTENTIAL rpm so output belts can still report load and keep the motor latched.
                dev.arubik.craftengine.rotation.RpmProvider m = belt.drivingMotor();
                if (m != null) {
                    float p = m.potentialRpm();
                    if (motor == null || p > bestPot) {
                        bestPot = p;
                        motor = m;
                    }
                }
            }
        }
        // Redstone: a powered router STOPS TRANSMITTING RPM/SU (getRpm -> 0, no SU report) but keeps
        // routing its own buffered items to the outputs at BASE speed (deliberately counter-intuitive).
        if (redstonePowered(world, pos)) {
            this.relayedRpm = 0f;   // don't drive output belts / don't expose RPM to neighbours
            this.relayMotor = null; // don't report SU upstream
            this.moveRpm = ConveyorBlockEntity.BASE_RPM; // still move items across the block
            return;
        }
        if (!beltFeeds && motor == null && best <= 0f && hasItems()) {
            // A pure funnel/IO push (chest funnel, machine output) with NO feeding belt: carry items
            // at BASE belt speed without distributing rotational power. Guarded by !beltFeeds so a
            // motor-driven line that just LOST its motor (broken) goes to 0 instead of free-running:
            // otherwise leftover items would keep the router (and its output belts) moving forever.
            best = ConveyorBlockEntity.BASE_RPM;
        }
        this.relayedRpm = best;
        this.relayMotor = motor;
        this.moveRpm = best;
    }

    private boolean redstonePowered(CEWorld world, BlockPos pos) {
        try {
            org.bukkit.World bw = (org.bukkit.World) world.world.platformWorld();
            return bw != null && bw.getBlockAt(pos.x(), pos.y(), pos.z()).isBlockIndirectlyPowered();
        } catch (Throwable t) {
            return false;
        }
    }

    // ---- RpmProvider: relay the feeding line's power to the output belts ----
    @Override
    public boolean isRpmSource() {
        return false; // a router only relays power; it is not a motor
    }

    @Override
    public float getRpm() {
        return relayedRpm;
    }
    public float potentialRpm() {
        // Output belts size their stress load from this; forward the real motor's potential so the
        // load stays stable even while the network is stalled (keeps the motor latched off).
        return relayMotor != null ? relayMotor.potentialRpm() : relayedRpm;
    }

    @Override
    public float stressCapacity() {
        return relayMotor != null ? relayMotor.stressCapacity() : Float.MAX_VALUE;
    }

    @Override
    public void reportStressLoad(float su) {
        if (relayMotor != null)
            relayMotor.reportStressLoad(su); // forward output-line stress to the real motor
    }

    @Override
    public boolean rpmReaches(BlockPos consumerPos) {
        for (Direction d : outputSides()) {
            BlockPos o = blockEntity().pos().relative(d);
            if (o.x() == consumerPos.x() && o.y() == consumerPos.y() && o.z() == consumerPos.z())
                return true;
        }
        return false;
    }

    // Per-slot transit state (item rendered moving entry-edge -> centre -> exit-edge).
    private final Direction[] entryDir;
    private final Direction[] exitDir;
    private final float[] progress;
    private final float[] jitter; // carried item yaw, kept consistent across the hop
    private final ConveyorItemDisplay[] displays;
    private final boolean[] spawned;

    protected AbstractRouterBlockEntity(BlockEntity blockEntity, Direction defaultFacing, int slots) {
        super(blockEntity, Math.max(1, slots));
        if (defaultFacing != null)
            this.defaultFacing = defaultFacing;
        this.slots = Math.max(1, slots);
        this.entryDir = new Direction[this.slots];
        this.exitDir = new Direction[this.slots];
        this.progress = new float[this.slots];
        this.jitter = new float[this.slots];
        this.displays = new ConveyorItemDisplay[this.slots];
        this.spawned = new boolean[this.slots];
    }

    // ---------------- live facing read ----------------

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected static String enumName(ImmutableBlockState state, String name) {
        if (state == null)
            return null;
        Property p = state.getProperty(name);
        if (p == null)
            return null;
        Object v = state.get(p);
        if (v == null)
            return null;
        try {
            return Property.formatValue(p, (Comparable<?>) v);
        } catch (Throwable t) {
            return String.valueOf(v);
        }
    }

    /** Output direction (the {@code facing} property), or the default. */
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

    // ---------------- buffer helpers ----------------

    protected boolean slotEmpty(int i) {
        net.minecraft.world.item.ItemStack s = getItem(i);
        return s == null || s.isEmpty();
    }

    /** True when any buffer slot holds an item (something is being carried across the router). */
    protected boolean hasItems() {
        for (int i = 0; i < slots; i++)
            if (!slotEmpty(i))
                return true;
        return false;
    }

    protected int firstEmptySlot() {
        for (int i = 0; i < slots; i++)
            if (slotEmpty(i))
                return i;
        return -1;
    }

    protected org.bukkit.inventory.ItemStack bukkitSlot(int i) {
        if (slotEmpty(i))
            return null;
        return CraftItemStack.asBukkitCopy(getItem(i));
    }

    protected void clearSlot(int i) {
        setItem(i, net.minecraft.world.item.ItemStack.EMPTY);
    }

    // Container#setChanged is abstract here; the buffer is persisted via the parent's
    // saveCustomData (KEY_INVENTORY) on chunk save, so this is a no-op.
    @Override
    public void setChanged() {
    }

    // ---------------- ConveyorReceiver ----------------

    @Override
    public boolean isFull() {
        return firstEmptySlot() < 0;
    }

    @Override
    public boolean receiveConveyorItem(org.bukkit.inventory.ItemStack stack, Direction sourceFacing) {
        return receiveConveyorItem(stack, sourceFacing, 0f);
    }

    @Override
    public boolean receiveConveyorItem(org.bukkit.inventory.ItemStack stack, Direction sourceFacing, float carriedJitter) {
        if (stack == null || stack.getType().isAir())
            return false;
        if (!acceptsFrom(sourceFacing))
            return false;
        int i = firstEmptySlot();
        if (i < 0)
            return false;
        setItem(i, CraftItemStack.asNMSCopy(stack));
        // Item entered from the side opposite the belt's travel direction.
        entryDir[i] = (sourceFacing != null) ? sourceFacing.opposite() : facing().opposite();
        exitDir[i] = null;
        progress[i] = 0f;
        jitter[i] = carriedJitter; // keep the item's rotation consistent
        // Spawn the carried display NOW (not next tick) so there is no 1-tick gap on hand-off from a
        // funnel/belt -> no flicker while the previous owner's display despawns.
        try {
            BlockPos p = blockEntity().pos();
            renderSlot(blockEntity().world().world().getTrackedBy(
                    new net.momirealms.craftengine.core.world.ChunkPos(p)), p, i);
        } catch (Throwable ignored) {
        }
        return true;
    }

    @Override
    public boolean adoptConveyorItem(org.bukkit.inventory.ItemStack stack, float jitter,
            ConveyorItemDisplay display, boolean spawned, Direction sourceFacing) {
        if (stack == null || stack.getType().isAir())
            return false;
        if (!acceptsFrom(sourceFacing))
            return false;
        int i = firstEmptySlot();
        if (i < 0)
            return false;
        setItem(i, CraftItemStack.asNMSCopy(stack));
        entryDir[i] = (sourceFacing != null) ? sourceFacing.opposite() : facing().opposite();
        exitDir[i] = null;
        progress[i] = 0f;
        this.jitter[i] = jitter;
        // Adopt the sender's live display (no respawn) -> seamless entry, no flicker.
        if (displays[i] != null && displays[i] != display) {
            try {
                BlockPos p = blockEntity().pos();
                despawnSlot(blockEntity().world().world().getTrackedBy(
                        new net.momirealms.craftengine.core.world.ChunkPos(p)), i);
            } catch (Throwable ignored) {
            }
        }
        displays[i] = display;
        this.spawned[i] = spawned;
        try {
            BlockPos p = blockEntity().pos();
            renderSlot(blockEntity().world().world().getTrackedBy(
                    new net.momirealms.craftengine.core.world.ChunkPos(p)), p, i);
        } catch (Throwable ignored) {
        }
        return true;
    }

    /**
     * Whether an item arriving from a belt whose travel direction is
     * {@code sourceFacing} is allowed in (enforces input-side directionality).
     */
    protected abstract boolean acceptsFrom(Direction sourceFacing);

    /** Choose an output side that currently has a non-full receiver (or null to hold). */
    protected abstract Direction chooseExit(CEWorld world, BlockPos pos);

    /** Notify the subclass that a stack left via {@code dir} (splitter balances on this). */
    protected void onDispatched(Direction dir) {
    }

    // ---------------- ticking ----------------

    @Override
    @SuppressWarnings("unchecked")
    public <C extends BlockEntityController> net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<C> createBlockEntityTicker(
            CEWorld world, ImmutableBlockState state) {
        return BlockEntityController.createTickerHelper(
                (net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<AbstractRouterBlockEntity>) AbstractRouterBlockEntity::tick);
    }

    public static void tick(CEWorld world, BlockPos pos, ImmutableBlockState state, AbstractRouterBlockEntity self) {
        self.updateRpm(world, pos); // relay power every tick (so output belts see it even when idle)
        self.maybeUpdateActivated(world, pos, self.relayedRpm > 0f); // on/off model swap by RPM
        self.advanceAndRender(world, pos);
    }

    /** Flip the {@code activated} block-state (animated vs idle model) when relayed RPM changes. */
    private void maybeUpdateActivated(CEWorld world, BlockPos pos, boolean active) {
        if (lastActivated != null && lastActivated == active)
            return;
        ImmutableBlockState cur = blockEntity().blockState();
        if (cur == null || cur.getProperty(PROP_ACTIVATED) == null) {
            lastActivated = active; // property not defined on this block; nothing to toggle
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
            dev.arubik.craftengine.util.MNms.INSTANCE.method$LevelWriter$setBlock(level, bp, nms, 2);
            lastActivated = active;
        } catch (Throwable ignored) {
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static ImmutableBlockState withEnum(ImmutableBlockState state, String prop, String valueName) {
        net.momirealms.craftengine.core.block.property.Property p = state.getProperty(prop);
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

    /** Advance each transit (at the input belt's speed) and render the carried item across the block. */
    private void advanceAndRender(CEWorld world, BlockPos pos) {
        float inc = ConveyorMath.progressPerTick(moveRpm, ConveyorBlockEntity.BASE_RPM,
                ConveyorBlockEntity.BASE_TRAVEL_TICKS);
        boolean powered = moveRpm > 0f;
        java.util.List<net.momirealms.craftengine.core.entity.player.Player> viewers =
                world.world().getTrackedBy(new net.momirealms.craftengine.core.world.ChunkPos(pos));
        for (int i = 0; i < slots; i++) {
            if (slotEmpty(i)) {
                despawnSlot(viewers, i);
                entryDir[i] = null;
                exitDir[i] = null;
                progress[i] = 0f;
                continue;
            }
            if (entryDir[i] == null)
                entryDir[i] = facing().opposite(); // reloaded from disk: assume the input side
            if (powered) {
                if (exitDir[i] == null) {
                    progress[i] = Math.min(0.5f, progress[i] + inc); // entry edge -> centre
                    if (progress[i] >= 0.5f) {
                        Direction ex = chooseExit(world, pos);
                        if (ex != null)
                            exitDir[i] = ex; // else hold at the centre until an output frees
                    }
                } else {
                    progress[i] = Math.min(1f, progress[i] + inc); // centre -> exit edge
                    if (progress[i] >= 1f && dispatch(world, pos, exitDir[i], i, viewers)) {
                        onDispatched(exitDir[i]);
                        clearSlot(i); // display already transferred or despawned by dispatch
                        entryDir[i] = null;
                        exitDir[i] = null;
                        progress[i] = 0f;
                        continue; // (else hold at 1: downstream full -> backpressure)
                    }
                }
            }
            renderSlot(viewers, pos, i);
        }
    }

    /**
     * Hand slot {@code i} out toward {@code dir}, TRANSFERRING the live display entity to the
     * receiver so the item never flickers leaving the router (belt/funnel/machine/router all adopt).
     * Returns false (stall) if the receiver is missing/full.
     */
    private boolean dispatch(CEWorld world, BlockPos pos, Direction dir, int i,
            java.util.List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
        ConveyorReceiver r = ConveyorRouting.receiverAt(world, pos, dir);
        if (r == null || r.isFull())
            return false;
        org.bukkit.inventory.ItemStack item = bukkitSlot(i);
        if (item == null)
            return true;
        if (r instanceof ConveyorBlockEntity belt) {
            if (belt.adoptFromFunnel(world, item, jitter[i], displays[i], spawned[i], dir.opposite())) {
                displays[i] = null; // entity moved on (no despawn)
                spawned[i] = false;
                return true;
            }
            return false;
        }
        if (r instanceof ConveyorDisplayReceiver dr) {
            if (dr.adoptConveyorItem(item, jitter[i], displays[i], spawned[i], dir)) {
                displays[i] = null;
                spawned[i] = false;
                return true;
            }
            return false;
        }
        if (r.receiveConveyorItem(item, dir, jitter[i])) {
            despawnSlot(viewers, i); // generic receiver -> no adoption, pop our display
            return true;
        }
        return false;
    }

    private void renderSlot(java.util.List<net.momirealms.craftengine.core.entity.player.Player> viewers,
            BlockPos pos, int i) {
        if (displays[i] == null)
            displays[i] = new ConveyorItemDisplay();
        ConveyorItemDisplay d = displays[i];
        d.setNmsItem(getItem(i));
        org.joml.Vector3f center = new org.joml.Vector3f(0.5f, ConveyorMath.BELT_TOP_Y, 0.5f);
        org.joml.Vector3f entryEdge = new org.joml.Vector3f(0.5f + entryDir[i].stepX() * 0.5f,
                ConveyorMath.BELT_TOP_Y, 0.5f + entryDir[i].stepZ() * 0.5f);
        org.joml.Vector3f rel;
        Direction move;
        if (exitDir[i] == null) {
            rel = ConveyorMath.interpolate(entryEdge, center, Math.min(1f, progress[i] * 2f));
            move = entryDir[i].opposite();
        } else {
            org.joml.Vector3f exitEdge = new org.joml.Vector3f(0.5f + exitDir[i].stepX() * 0.5f,
                    ConveyorMath.BELT_TOP_Y, 0.5f + exitDir[i].stepZ() * 0.5f);
            rel = ConveyorMath.interpolate(center, exitEdge, Math.max(0f, (progress[i] - 0.5f) * 2f));
            move = exitDir[i];
        }
        org.joml.Quaternionf rot = ConveyorMath.itemRotation(move.stepX(), move.stepZ(), 0);
        rot.rotateY(jitter[i]); // carry the same yaw jitter as the belts -> consistent rotation
        d.setRotation(rot);
        d.render(viewers, pos.x() + rel.x, pos.y() + rel.y, pos.z() + rel.z, !spawned[i]);
        d.consumeRotationDirty();
        spawned[i] = true;
    }

    private void despawnSlot(java.util.List<net.momirealms.craftengine.core.entity.player.Player> viewers, int i) {
        if (displays[i] != null && spawned[i]) {
            for (net.momirealms.craftengine.core.entity.player.Player p : viewers)
                displays[i].despawn(p);
            displays[i].clearShown();
            spawned[i] = false;
        }
    }

    /** Drop carried items + kill displays (call on break). */
    public void dropAndDespawn() {
        try {
            CEWorld world = blockEntity().world();
            BlockPos pos = blockEntity().pos();
            java.util.List<net.momirealms.craftengine.core.entity.player.Player> viewers =
                    world.world().getTrackedBy(new net.momirealms.craftengine.core.world.ChunkPos(pos));
            org.bukkit.World bw = (org.bukkit.World) world.world.platformWorld();
            for (int i = 0; i < slots; i++) {
                if (!slotEmpty(i) && bw != null)
                    bw.dropItem(new org.bukkit.Location(bw, pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5),
                            bukkitSlot(i));
                despawnSlot(viewers, i);
                clearSlot(i);
            }
        } catch (Throwable ignored) {
        }
    }
}
