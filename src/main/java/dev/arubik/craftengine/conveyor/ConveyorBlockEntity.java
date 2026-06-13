package dev.arubik.craftengine.conveyor;

import java.util.List;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.joml.Vector3f;

import dev.arubik.craftengine.rotation.RpmConsumer;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.ChunkPos;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;

/**
 * Create-style conveyor belt segment.
 *
 * <p>Holds a single in-transit item that advances from a block-relative start
 * point to an end point. Speed (ticks-per-item) derives from the input RPM
 * (rpm 0 =&gt; stalled). On reaching the end the item is handed to the next
 * conveyor in the belt's facing direction via {@link #acceptItem}; if there is
 * none or it is full, the item is dropped into the world at the end point.</p>
 *
 * <p>Implements {@link RpmConsumer}: the belt HEAD is fed RPM directly by an
 * adjacent motor (see SHARED ROTATION CONTRACT). Non-head segments derive their
 * RPM each tick by reading the upstream conveyor's effective rpm.</p>
 */
public class ConveyorBlockEntity extends BlockEntityController implements RpmConsumer {

    /** Reference rpm at which the belt runs at base speed. */
    public static final float BASE_RPM = 64f;
    /** Ticks for one item to cross one segment at BASE_RPM. */
    public static final int BASE_TRAVEL_TICKS = 16;

    /** belt travel direction (towards the next segment) */
    private Direction facing = Direction.NORTH;

    /** rpm delivered to this consumer this tick (set externally for the head). */
    private float inputRpm = 0f;
    /** rpm actually driving this segment (head: inputRpm; body: upstream effective). */
    private float effectiveRpm = 0f;

    // single-item buffer (Bukkit stack so persistence is trivial); null = empty
    private org.bukkit.inventory.ItemStack carried;
    private float progress = 0f;
    private final Vector3f start = new Vector3f(0.5f, 0.95f, 0.5f);
    private final Vector3f end = new Vector3f(0.5f, 0.95f, 0.5f);

    // render
    private ConveyorItemDisplay display;
    private boolean displaySpawned = false;

    public ConveyorBlockEntity(BlockEntity blockEntity) {
        super(blockEntity);
    }

    public ConveyorBlockEntity(BlockEntity blockEntity, Direction facing) {
        super(blockEntity);
        if (facing != null)
            setFacing(facing);
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
        // start = entry (opposite face, belt top), end = exit (facing face, belt top)
        float ox = facing.stepX() * 0.5f;
        float oz = facing.stepZ() * 0.5f;
        this.start.set(0.5f - ox, 0.95f, 0.5f - oz);
        this.end.set(0.5f + ox, 0.95f, 0.5f + oz);
    }

    public Direction facing() {
        return facing;
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
        // 1. Determine effective rpm. Head = upstream neighbour is not a conveyor;
        //    such a segment keeps whatever a motor pushed via setInputRpm. A body
        //    segment instead reads the upstream conveyor's effective rpm.
        ConveyorBlockEntity upstream = neighbourConveyor(world, pos, facing.opposite());
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

        // 2. Render the carried item interpolating start -> end.
        renderCarried(world, pos);

        // 3. On arrival, hand off or drop.
        if (this.progress >= 1f) {
            handOff(world, pos);
        }
    }

    private void handOff(CEWorld world, BlockPos pos) {
        ConveyorBlockEntity next = neighbourConveyor(world, pos, facing);
        org.bukkit.inventory.ItemStack item = this.carried;
        if (next != null && next.acceptItem(item)) {
            clearCarried(world, pos);
            return;
        }
        // no next belt (or it is full) -> drop into world at the end point
        dropAtEnd(world, pos, item);
        clearCarried(world, pos);
    }

    private void dropAtEnd(CEWorld world, BlockPos pos, org.bukkit.inventory.ItemStack item) {
        try {
            org.bukkit.World bukkitWorld = (org.bukkit.World) world.world().platformWorld();
            if (bukkitWorld != null) {
                bukkitWorld.dropItem(new org.bukkit.Location(bukkitWorld,
                        pos.x() + end.x, pos.y() + end.y, pos.z() + end.z), item);
            }
        } catch (Throwable ignored) {
            // platformWorld not a Bukkit world (shouldn't happen at runtime)
        }
    }

    // ---------------- rendering ----------------

    private void renderCarried(CEWorld world, BlockPos pos) {
        if (display == null) {
            display = new ConveyorItemDisplay();
            display.setNmsItem(CraftItemStack.asNMSCopy(this.carried));
        }
        Vector3f rel = ConveyorMath.interpolate(start, end, progress);
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

    // ---------------- neighbour lookup ----------------

    /**
     * The conveyor controller at {@code pos.relative(dir)}, or null if that block
     * is not a loaded conveyor.
     */
    private static ConveyorBlockEntity neighbourConveyor(CEWorld world, BlockPos pos, Direction dir) {
        BlockEntity be = world.getBlockEntityAtIfLoaded(pos.relative(dir));
        if (be != null && be.controller instanceof ConveyorBlockEntity conveyor)
            return conveyor;
        return null;
    }

    // ---------------- persistence ----------------

    @Override
    public void saveCustomData(CompoundTag tag) {
        tag.putString("facing", facing.name());
        tag.putFloat("progress", progress);
        if (!isEmpty()) {
            tag.putByteArray("item", carried.serializeAsBytes());
        }
    }

    @Override
    public void loadCustomData(CompoundTag tag) {
        if (tag.containsKey("facing")) {
            try {
                setFacing(Direction.valueOf(tag.getString("facing")));
            } catch (IllegalArgumentException ignored) {
            }
        }
        this.progress = tag.getFloat("progress", 0f);
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
        // best-effort: nothing world-bound to clean up here beyond the fake display,
        // which despawns naturally when viewers stop tracking the chunk.
        super.onRemove();
    }
}
