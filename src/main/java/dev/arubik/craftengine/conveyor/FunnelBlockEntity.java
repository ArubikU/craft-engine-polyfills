package dev.arubik.craftengine.conveyor;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.joml.Vector3f;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.contraption.level.ContraptionLevel;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.ChunkPos;

/**
 * FUNNEL controller ({@code polyfills:funnel}). A one-item bridge between a conveyor belt
 * (on its {@code facing} side) and a vanilla worldly container (chest/etc. on the opposite
 * side). Two modes via the {@code mode} property:
 * <ul>
 *   <li><b>out</b>: pulls items from the container and pushes them onto the belt.</li>
 *   <li><b>in</b>: receives items from the belt (as a {@link ConveyorReceiver}) and inserts
 *       them into the container.</li>
 * </ul>
 * The carried item is rendered travelling HALF a block (edge ↔ centre) at the speed of the
 * connected belt (or a standard speed if no belt is attached). {@code activated} reflects
 * whether an item is moving.
 */
public class FunnelBlockEntity extends PersistentBlockEntity implements ConveyorDisplayReceiver {

    public static final String PROP_FACING = "facing";
    public static final String PROP_MODE = "mode";   // string: in / out
    public static final String PROP_ACTIVATED = "activated";

    private org.bukkit.inventory.ItemStack transit; // the single in-transit item (or null)
    private float progress = 0f;
    private ConveyorItemDisplay display;
    private boolean spawned = false;
    private float carriedJitter = 0f; // item yaw jitter carried in from the belt, relayed onward
    private boolean pendingDespawn = false; // keep display 1 tick after hand-off to avoid a flicker gap

    private boolean loaded = false;
    private boolean dirty = false;
    private Boolean lastActivated = null;

    public FunnelBlockEntity(BlockEntity blockEntity) {
        super(blockEntity);
    }

    // ---------------- state reads ----------------

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String enumName(String name) {
        ImmutableBlockState st = blockEntity().blockState();
        if (st == null)
            return null;
        Property p = st.getProperty(name);
        if (p == null)
            return null;
        Object v = st.get(p);
        if (v == null)
            return null;
        try {
            return Property.formatValue(p, (Comparable<?>) v);
        } catch (Throwable t) {
            return String.valueOf(v);
        }
    }

    private Direction facing() {
        String n = enumName(PROP_FACING);
        if (n != null) {
            try {
                return Direction.valueOf(n.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return Direction.NORTH;
    }

    private boolean outMode() {
        return !"in".equalsIgnoreCase(enumName(PROP_MODE)); // default = out
    }

    // ---------------- neighbours ----------------

    private boolean redstonePowered() {
        try {
            org.bukkit.World bw = (org.bukkit.World) blockEntity().world().world.platformWorld();
            BlockPos p = blockEntity().pos();
            return bw != null && bw.getBlockAt(p.x(), p.y(), p.z()).isBlockIndirectlyPowered();
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * The NMS {@link net.minecraft.world.Container} behind the funnel (opposite the belt).
     * Uses the vanilla hopper resolver, so it picks up BOTH vanilla containers (chests…)
     * AND CraftEngine custom ones that expose a WorldlyContainerHolder (e.g. the depot) —
     * a Bukkit {@code BlockState instanceof Container} check would miss the depot.
     */
    private net.minecraft.world.Container containerAt(Direction facing) {
        try {
            net.minecraft.world.level.Level lvl = (net.minecraft.world.level.Level) blockEntity().world().world
                    .minecraftWorld();
            BlockPos p = blockEntity().pos();
            Direction o = facing.opposite();
            net.minecraft.core.BlockPos np = new net.minecraft.core.BlockPos(
                    p.x() + o.stepX(), p.y() + o.stepY(), p.z() + o.stepZ());
            return net.minecraft.world.level.block.entity.HopperBlockEntity.getContainerAt(lvl, np);
        } catch (Throwable t) {
            return null;
        }
    }

    private static net.minecraft.core.Direction nms(Direction d) {
        return net.minecraft.core.Direction.valueOf(d.name());
    }

    /** Slots accessible from {@code face} (worldly-aware), or every slot for a plain container. */
    private static int[] facesFor(net.minecraft.world.Container c, net.minecraft.core.Direction face) {
        if (c instanceof net.minecraft.world.WorldlyContainer wc)
            return wc.getSlotsForFace(face);
        int[] all = new int[c.getContainerSize()];
        for (int i = 0; i < all.length; i++)
            all[i] = i;
        return all;
    }

    private ConveyorBlockEntity belt(CEWorld world, Direction facing) {
        try {
            BlockPos bp = blockEntity().pos().relative(facing);
            net.momirealms.craftengine.core.block.entity.BlockEntity be = world.getBlockEntityAtIfLoaded(bp);
            if (be != null && be.controller instanceof ConveyorBlockEntity c)
                return c;
        } catch (Throwable ignored) {
        }
        return null;
    }

    private ConveyorReceiver beltReceiver(CEWorld world, Direction facing) {
        try {
            BlockPos bp = blockEntity().pos().relative(facing);
            net.momirealms.craftengine.core.block.entity.BlockEntity be = world.getBlockEntityAtIfLoaded(bp);
            if (be != null && be.controller instanceof ConveyorReceiver r)
                return r;
        } catch (Throwable ignored) {
        }
        return null;
    }

    /** Per-tick progress: matches the connected belt's speed, else a standard pace. */
    private float inc(CEWorld world, Direction facing) {
        // Inherit the connected belt's speed so the funnel keeps pace and doesn't bottleneck a fast
        // belt. The belt may sit on the facing side (out / item-out) OR the opposite side (the belt
        // feeding an IN funnel), so take whichever neighbour belt is fastest.
        ConveyorBlockEntity bf = belt(world, facing);
        ConveyorBlockEntity bo = belt(world, facing.opposite());
        float rpm = 0f;
        if (bf != null) rpm = Math.max(rpm, bf.effectiveRpm());
        if (bo != null) rpm = Math.max(rpm, bo.effectiveRpm());
        if (rpm <= 0f)
            rpm = ConveyorBlockEntity.BASE_RPM; // no belt / idle belt -> still nudge items at base
        // The funnel spans a FULL block (container edge -> belt edge), so it advances at the
        // same per-segment rate as a conveyor.
        return ConveyorMath.progressPerTick(rpm, ConveyorBlockEntity.BASE_RPM,
                ConveyorBlockEntity.BASE_TRAVEL_TICKS);
    }

    // ---------------- ConveyorReceiver (IN mode) ----------------

    @Override
    public boolean isFull() {
        // Only accepts from a belt when in IN mode and not already carrying.
        return outMode() || transit != null;
    }

    @Override
    public boolean receiveConveyorItem(org.bukkit.inventory.ItemStack stack, Direction sourceFacing) {
        return receiveConveyorItem(stack, sourceFacing, 0f);
    }

    @Override
    public boolean receiveConveyorItem(org.bukkit.inventory.ItemStack stack, Direction sourceFacing, float jitter) {
        if (outMode() || transit != null || stack == null || stack.getType().isAir())
            return false;
        this.transit = stack.clone();
        this.carriedJitter = jitter; // keep the item's yaw consistent across belt -> funnel -> belt/router
        this.progress = 0f;
        this.dirty = true;
        return true;
    }

    @Override
    public boolean adoptConveyorItem(org.bukkit.inventory.ItemStack stack, float jitter,
            ConveyorItemDisplay incoming, boolean wasSpawned, Direction sourceFacing) {
        if (outMode() || transit != null || stack == null || stack.getType().isAir())
            return false;
        // Adopt the belt's live display entity (no respawn) -> seamless belt -> funnel hand-off.
        if (this.display != null && this.display != incoming) {
            try {
                despawn(blockEntity().world());
            } catch (Throwable ignored) {
            }
        }
        this.transit = stack.clone();
        this.carriedJitter = jitter;
        this.progress = 0f;
        this.display = incoming;
        this.spawned = wasSpawned;
        this.pendingDespawn = false;
        this.dirty = true;
        return true;
    }

    // ---------------- ticking ----------------

    @Override
    @SuppressWarnings("unchecked")
    public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(
            CEWorld world, ImmutableBlockState state) {
        return BlockEntityController.createTickerHelper(
                (BlockEntityTicker<FunnelBlockEntity>) FunnelBlockEntity::tick);
    }

    public static void tick(CEWorld world, BlockPos pos, ImmutableBlockState state, FunnelBlockEntity self) {
        self.serverTick(world);
    }

    private void serverTick(CEWorld world) {
        if (!loaded) {
            load();
            loaded = true;
        }
        // Handed our item to a belt/router last tick but kept the display one extra tick so the
        // receiver's freshly-spawned display overlaps ours (no 1-tick gap = no flicker). Despawn now.
        if (pendingDespawn) {
            despawn(world);
            pendingDespawn = false;
        }
        Direction facing = facing();
        boolean out = outMode();

        // Redstone control (inverted): no signal = ON, signal = OFF. Powered = paused.
        if (redstonePowered()) {
            maybeUpdateActivated(world, false);
            renderTransit(world, facing, out);
            if (dirty) {
                dirty = false;
                save();
            }
            return;
        }

        if (out) {
            // OUT: pull from the container, carry to centre, hand to the belt.
            if (transit == null) {
                pullFromContainer(facing);
            }
            if (transit != null) {
                progress = Math.min(1f, progress + inc(world, facing));
                if (progress >= 1f) {
                    ConveyorReceiver belt = beltReceiver(world, facing);
                    if (belt instanceof ConveyorBlockEntity cbe) {
                        // Belt ahead: TRANSFER our live display entity onto it (no respawn) ->
                        // seamless funnel -> belt hand-off, identical to belt -> belt.
                        if (cbe.adoptFromFunnel(world, transit, carriedJitter, display, spawned,
                                facing.opposite())) {
                            this.display = null; // entity moved on; don't despawn it
                            this.spawned = false;
                            this.transit = null;
                            this.progress = 0f;
                            this.dirty = true;
                            if (dirty) {
                                dirty = false;
                                save();
                            }
                            return;
                        }
                        // belt full -> stall (keep rendering our transit)
                    } else if (belt instanceof ConveyorDisplayReceiver dr) {
                        // Machine funnel-IO (or any display-capable receiver): TRANSFER our display so
                        // the item flows seamlessly into the machine's funnel-face transit.
                        if (!dr.isFull() && dr.adoptConveyorItem(transit, carriedJitter, display, spawned,
                                facing)) {
                            this.display = null;
                            this.spawned = false;
                            this.transit = null;
                            this.progress = 0f;
                            this.dirty = true;
                            if (dirty) {
                                dirty = false;
                                save();
                            }
                            return;
                        }
                    } else if (belt != null) {
                        // Non-belt receiver (router): no display adoption -> keep OUR display one
                        // extra tick (overlap) then despawn next tick to avoid a flicker gap.
                        if (!belt.isFull() && belt.receiveConveyorItem(transit, facing, carriedJitter)) {
                            this.transit = null;
                            this.progress = 0f;
                            this.dirty = true;
                            this.pendingDespawn = true;
                            if (dirty) {
                                dirty = false;
                                save();
                            }
                            return; // skip renderTransit (which would despawn our display immediately)
                        }
                    } else {
                        // Belt is OPTIONAL: with none attached, just eject the item out the front.
                        dropFront(facing);
                        clearTransit(world);
                    }
                }
            }
        } else {
            // IN: item arrives from the belt (receiveConveyorItem), carry to the container.
            if (transit != null) {
                progress = Math.min(1f, progress + inc(world, facing));
                if (progress >= 1f) {
                    if (insertToContainer(facing, transit)) {
                        clearTransit(world);
                    }
                    // else: stall until the container frees.
                }
            }
        }

        // "On" (in/out model) continuously while it has a worldly container behind it.
        // The belt is OPTIONAL — in OUT mode with no belt it just ejects items out the front.
        boolean running = containerAt(facing) != null;
        maybeUpdateActivated(world, running);
        renderTransit(world, facing, out);
        if (dirty) {
            dirty = false;
            save();
        }
    }

    private void pullFromContainer(Direction facing) {
        net.minecraft.world.Container c = containerAt(facing);
        if (c == null)
            return;
        net.minecraft.core.Direction face = nms(facing); // container face toward the funnel
        for (int slot : facesFor(c, face)) {
            net.minecraft.world.item.ItemStack s = c.getItem(slot);
            if (s.isEmpty())
                continue;
            if (c instanceof net.minecraft.world.WorldlyContainer wc && !wc.canTakeItemThroughFace(slot, s, face))
                continue;
            net.minecraft.world.item.ItemStack one = c.removeItem(slot, 1);
            if (one.isEmpty())
                continue;
            c.setChanged();
            this.transit = CraftItemStack.asBukkitCopy(one);
            this.progress = 0f;
            this.dirty = true;
            return;
        }
    }

    private boolean insertToContainer(Direction facing, org.bukkit.inventory.ItemStack stack) {
        net.minecraft.world.Container c = containerAt(facing);
        if (c == null)
            return false;
        net.minecraft.core.Direction face = nms(facing);
        net.minecraft.world.item.ItemStack ins = CraftItemStack.asNMSCopy(stack); // single item in transit
        for (int slot : facesFor(c, face)) {
            if (c instanceof net.minecraft.world.WorldlyContainer wc && !wc.canPlaceItemThroughFace(slot, ins, face))
                continue;
            if (!c.canPlaceItem(slot, ins))
                continue;
            net.minecraft.world.item.ItemStack cur = c.getItem(slot);
            if (cur.isEmpty()) {
                c.setItem(slot, ins);
                c.setChanged();
                return true;
            }
            if (net.minecraft.world.item.ItemStack.isSameItemSameComponents(cur, ins)) {
                int max = Math.min(cur.getMaxStackSize(), c.getMaxStackSize());
                if (cur.getCount() < max) {
                    cur.grow(1);
                    c.setChanged();
                    return true;
                }
            }
        }
        return false;
    }

    /** Eject the carried item out the front (belt optional) — thrown like a belt at standard speed. */
    private void dropFront(Direction facing) {
        try {
            if (transit == null)
                return;
            org.bukkit.World bw = (org.bukkit.World) blockEntity().world().world.platformWorld();
            BlockPos p = blockEntity().pos();
            double x = p.x() + 0.5 + facing.stepX() * 0.6;
            double y = p.y() + 0.3;
            double z = p.z() + 0.5 + facing.stepZ() * 0.6;
            org.bukkit.entity.Item it = bw.dropItem(new org.bukkit.Location(bw, x, y, z), transit);
            float inc = ConveyorMath.progressPerTick(ConveyorBlockEntity.BASE_RPM,
                    ConveyorBlockEntity.BASE_RPM, ConveyorBlockEntity.BASE_TRAVEL_TICKS); // standard belt speed
            it.setVelocity(new org.bukkit.util.Vector(facing.stepX() * inc, 0.05, facing.stepZ() * inc));
            it.setPickupDelay(10);
        } catch (Throwable ignored) {
        }
    }

    private void clearTransit(CEWorld world) {
        this.transit = null;
        this.progress = 0f;
        this.dirty = true;
        despawn(world);
    }

    // ---------------- render ----------------

    private void renderTransit(CEWorld world, Direction facing, boolean out) {
        if (transit == null) {
            despawn(world);
            return;
        }
        BlockPos pos = blockEntity().pos();
        java.util.List<Player> viewers = ConveyorBlockEntity.viewersOf(world, pos);
        ContraptionLevel contraption = ConveyorBlockEntity.contraptionOf(world);
        if (display == null)
            display = new ConveyorItemDisplay();
        display.setNmsItem(CraftItemStack.asNMSCopy(transit));

        Direction toContainer = facing.opposite();
        // Full traversal across the whole block: container edge <-> belt edge (through the centre).
        Vector3f containerEdge = new Vector3f(0.5f + toContainer.stepX() * 0.5f, ConveyorMath.BELT_TOP_Y,
                0.5f + toContainer.stepZ() * 0.5f);
        Vector3f beltEdge = new Vector3f(0.5f + facing.stepX() * 0.5f, ConveyorMath.BELT_TOP_Y,
                0.5f + facing.stepZ() * 0.5f);
        // OUT: container edge -> belt edge (then the belt continues). IN: belt edge -> container edge.
        Vector3f rel = out ? ConveyorMath.interpolate(containerEdge, beltEdge, progress)
                : ConveyorMath.interpolate(beltEdge, containerEdge, progress);
        // Face the travel direction (OUT moves toward the belt; IN toward the container).
        Direction move = out ? facing : toContainer;
        org.joml.Quaternionf rot = ConveyorMath.itemRotation(move.stepX(), move.stepZ(), 0);
        rot.rotateY(carriedJitter); // same yaw jitter as the belts -> rotation stays consistent

        double wx = pos.x() + rel.x, wy = pos.y() + rel.y, wz = pos.z() + rel.z;
        if (contraption != null) {
            net.minecraft.world.phys.Vec3 real = contraption
                    .realWorldPositionOf(new net.minecraft.world.phys.Vec3(wx, wy, wz));
            wx = real.x;
            wy = real.y;
            wz = real.z;
            rot = contraption.realOrientationOf(rot);
        }
        display.setRotation(rot);
        display.render(viewers, wx, wy, wz, !spawned);
        display.consumeRotationDirty();
        spawned = true;
    }

    private void despawn(CEWorld world) {
        // Force-remove even if the spawned flag is stale (item broke mid-transit).
        if (display != null) {
            for (Player p : ConveyorBlockEntity.viewersOf(world, blockEntity().pos()))
                display.despawn(p);
            display.clearShown();
            spawned = false;
        }
    }

    private void maybeUpdateActivated(CEWorld world, boolean active) {
        if (lastActivated != null && lastActivated == active)
            return;
        ImmutableBlockState cur = blockEntity().blockState();
        if (cur == null || cur.getProperty(PROP_ACTIVATED) == null) {
            lastActivated = active;
            return;
        }
        try {
            ImmutableBlockState ns = withActivated(cur, active);
            if (ns != null && ns != cur) {
                BlockPos pos = blockEntity().pos();
                Object level = world.world().minecraftWorld();
                Object bp = dev.arubik.craftengine.util.MNms.INSTANCE.constructor$BlockPos(pos.x(), pos.y(), pos.z());
                Object nms = ns.customBlockState().minecraftState();
                dev.arubik.craftengine.util.MNms.INSTANCE.method$LevelWriter$setBlock(level, bp, nms, 2);
            }
            lastActivated = active;
        } catch (Throwable ignored) {
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static ImmutableBlockState withActivated(ImmutableBlockState state, boolean active) {
        Property p = state.getProperty(PROP_ACTIVATED);
        if (p == null)
            return state;
        try {
            Object value = p.valueByName(String.valueOf(active));
            if (value == null)
                return state;
            return ImmutableBlockState.with(state, p, value);
        } catch (Throwable t) {
            return state;
        }
    }

    public void dropTransit() {
        try {
            if (transit != null) {
                org.bukkit.World bw = (org.bukkit.World) blockEntity().world().world.platformWorld();
                BlockPos p = blockEntity().pos();
                bw.dropItem(new org.bukkit.Location(bw, p.x() + 0.5, p.y() + 0.5, p.z() + 0.5), transit);
                transit = null;
            }
        } catch (Throwable ignored) {
        }
        // Always kill the rendered display entity, even mid-transit (broke while entering/leaving).
        try {
            despawn(blockEntity().world());
        } catch (Throwable ignored) {
        }
    }

    // ---------------- persistence (this block entity's own CE tag) ----------------

    private static final dev.arubik.craftengine.util.TypedKey<Float> KEY_PROG =
            dev.arubik.craftengine.util.TypedKey.of("craftengine", "funnel_prog",
                    dev.arubik.craftengine.util.NbtType.FLOAT);

    private void load() {
        try {
            net.minecraft.world.item.ItemStack nms = getOptional(dev.arubik.craftengine.util.TypedKeys.NMS_ITEM)
                    .orElse(null);
            transit = (nms != null && !nms.isEmpty()) ? CraftItemStack.asBukkitCopy(nms) : null;
            progress = getOrDefault(KEY_PROG, 0f);
        } catch (Throwable ignored) {
        }
    }

    private void save() {
        try {
            if (transit == null)
                clear();
            else {
                set(dev.arubik.craftengine.util.TypedKeys.NMS_ITEM, CraftItemStack.asNMSCopy(transit));
                set(KEY_PROG, progress);
            }
        } catch (Throwable ignored) {
        }
    }
}
