/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Direction
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  net.momirealms.craftengine.core.world.ChunkPos
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.conveyor;

import dev.arubik.craftengine.block.entity.PersistentWorldlyBlockEntity;
import dev.arubik.craftengine.conveyor.ConveyorBlockEntity;
import dev.arubik.craftengine.conveyor.ConveyorDisplayReceiver;
import dev.arubik.craftengine.conveyor.ConveyorItemDisplay;
import dev.arubik.craftengine.conveyor.ConveyorMath;
import dev.arubik.craftengine.conveyor.ConveyorReceiver;
import dev.arubik.craftengine.conveyor.ConveyorRouting;
import dev.arubik.craftengine.rotation.RpmProvider;
import dev.arubik.craftengine.util.MNms;
import java.util.List;
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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class AbstractRouterBlockEntity
extends PersistentWorldlyBlockEntity
implements ConveyorDisplayReceiver,
RpmProvider {
    public static final String PROP_FACING = "facing";
    public static final String PROP_ACTIVATED = "activated";
    private Boolean lastActivated = null;
    protected Direction defaultFacing = Direction.NORTH;
    protected final int slots;
    protected float relayedRpm = 0.0f;
    protected float moveRpm = 0.0f;
    protected RpmProvider relayMotor;
    private final Direction[] entryDir;
    private final Direction[] exitDir;
    private final float[] progress;
    private final float[] jitter;
    private final ConveyorItemDisplay[] displays;
    private final boolean[] spawned;

    protected abstract Direction[] inputSides();

    protected abstract Direction[] outputSides();

    private void updateRpm(CEWorld world, BlockPos pos) {
        float best = 0.0f;
        float bestPot = 0.0f;
        boolean beltFeeds = false;
        RpmProvider motor = null;
        for (Direction d : this.inputSides()) {
            BlockEntityController blockEntityController;
            BlockEntity be = world.getBlockEntityAtIfLoaded(pos.relative(d));
            if (be == null || !((blockEntityController = be.controller) instanceof ConveyorBlockEntity)) continue;
            ConveyorBlockEntity belt = (ConveyorBlockEntity)blockEntityController;
            beltFeeds = true;
            best = Math.max(best, belt.effectiveRpm());
            RpmProvider m = belt.drivingMotor();
            if (m == null) continue;
            float p = m.potentialRpm();
            if (motor != null && !(p > bestPot)) continue;
            bestPot = p;
            motor = m;
        }
        if (this.redstonePowered(world, pos)) {
            this.relayedRpm = 0.0f;
            this.relayMotor = null;
            this.moveRpm = 64.0f;
            return;
        }
        if (!beltFeeds && motor == null && best <= 0.0f && this.hasItems()) {
            best = 64.0f;
        }
        this.relayedRpm = best;
        this.relayMotor = motor;
        this.moveRpm = best;
    }

    private boolean redstonePowered(CEWorld world, BlockPos pos) {
        try {
            World bw = (World)world.world.platformWorld();
            return bw != null && bw.getBlockAt(pos.x(), pos.y(), pos.z()).isBlockIndirectlyPowered();
        }
        catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isRpmSource() {
        return false;
    }

    @Override
    public float getRpm() {
        return this.relayedRpm;
    }

    @Override
    public float potentialRpm() {
        return this.relayMotor != null ? this.relayMotor.potentialRpm() : this.relayedRpm;
    }

    @Override
    public float stressCapacity() {
        return this.relayMotor != null ? this.relayMotor.stressCapacity() : Float.MAX_VALUE;
    }

    @Override
    public void reportStressLoad(float su) {
        if (this.relayMotor != null) {
            this.relayMotor.reportStressLoad(su);
        }
    }

    @Override
    public boolean rpmReaches(BlockPos consumerPos) {
        for (Direction d : this.outputSides()) {
            BlockPos o = this.blockEntity().pos().relative(d);
            if (o.x() != consumerPos.x() || o.y() != consumerPos.y() || o.z() != consumerPos.z()) continue;
            return true;
        }
        return false;
    }

    protected AbstractRouterBlockEntity(BlockEntity blockEntity, Direction defaultFacing, int slots) {
        super(blockEntity, Math.max(1, slots));
        if (defaultFacing != null) {
            this.defaultFacing = defaultFacing;
        }
        this.slots = Math.max(1, slots);
        this.entryDir = new Direction[this.slots];
        this.exitDir = new Direction[this.slots];
        this.progress = new float[this.slots];
        this.jitter = new float[this.slots];
        this.displays = new ConveyorItemDisplay[this.slots];
        this.spawned = new boolean[this.slots];
    }

    protected static String enumName(ImmutableBlockState state, String name) {
        if (state == null) {
            return null;
        }
        Property p = state.getProperty(name);
        if (p == null) {
            return null;
        }
        Comparable v = state.get(p);
        if (v == null) {
            return null;
        }
        try {
            return Property.formatValue((Property)p, (Comparable)v);
        }
        catch (Throwable t) {
            return String.valueOf(v);
        }
    }

    public Direction facing() {
        String n = AbstractRouterBlockEntity.enumName(this.blockEntity().blockState(), PROP_FACING);
        if (n != null) {
            try {
                return Direction.valueOf((String)n.toUpperCase());
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return this.defaultFacing;
    }

    protected boolean slotEmpty(int i) {
        net.minecraft.world.item.ItemStack s = this.getItem(i);
        return s == null || s.isEmpty();
    }

    protected boolean hasItems() {
        for (int i = 0; i < this.slots; ++i) {
            if (this.slotEmpty(i)) continue;
            return true;
        }
        return false;
    }

    protected int firstEmptySlot() {
        for (int i = 0; i < this.slots; ++i) {
            if (!this.slotEmpty(i)) continue;
            return i;
        }
        return -1;
    }

    protected ItemStack bukkitSlot(int i) {
        if (this.slotEmpty(i)) {
            return null;
        }
        return CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)this.getItem(i));
    }

    protected void clearSlot(int i) {
        this.setItem(i, net.minecraft.world.item.ItemStack.EMPTY);
    }

    public void setChanged() {
    }

    @Override
    public boolean isFull() {
        return this.firstEmptySlot() < 0;
    }

    @Override
    public boolean receiveConveyorItem(ItemStack stack, Direction sourceFacing) {
        return this.receiveConveyorItem(stack, sourceFacing, 0.0f);
    }

    @Override
    public boolean receiveConveyorItem(ItemStack stack, Direction sourceFacing, float carriedJitter) {
        if (stack == null || stack.getType().isAir()) {
            return false;
        }
        if (!this.acceptsFrom(sourceFacing)) {
            return false;
        }
        int i = this.firstEmptySlot();
        if (i < 0) {
            return false;
        }
        this.setItem(i, CraftItemStack.asNMSCopy((ItemStack)stack));
        this.entryDir[i] = sourceFacing != null ? sourceFacing.opposite() : this.facing().opposite();
        this.exitDir[i] = null;
        this.progress[i] = 0.0f;
        this.jitter[i] = carriedJitter;
        try {
            BlockPos p = this.blockEntity().pos();
            this.renderSlot(this.blockEntity().world().world().getTrackedBy(new ChunkPos(p)), p, i);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return true;
    }

    @Override
    public boolean adoptConveyorItem(ItemStack stack, float jitter, ConveyorItemDisplay display, boolean spawned, Direction sourceFacing) {
        BlockPos p2;
        if (stack == null || stack.getType().isAir()) {
            return false;
        }
        if (!this.acceptsFrom(sourceFacing)) {
            return false;
        }
        int i = this.firstEmptySlot();
        if (i < 0) {
            return false;
        }
        this.setItem(i, CraftItemStack.asNMSCopy((ItemStack)stack));
        this.entryDir[i] = sourceFacing != null ? sourceFacing.opposite() : this.facing().opposite();
        this.exitDir[i] = null;
        this.progress[i] = 0.0f;
        this.jitter[i] = jitter;
        if (this.displays[i] != null && this.displays[i] != display) {
            try {
                p2 = this.blockEntity().pos();
                this.despawnSlot(this.blockEntity().world().world().getTrackedBy(new ChunkPos(p2)), i);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        this.displays[i] = display;
        this.spawned[i] = spawned;
        try {
            p2 = this.blockEntity().pos();
            this.renderSlot(this.blockEntity().world().world().getTrackedBy(new ChunkPos(p2)), p2, i);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return true;
    }

    protected abstract boolean acceptsFrom(Direction var1);

    protected abstract Direction chooseExit(CEWorld var1, BlockPos var2);

    protected void onDispatched(Direction dir) {
    }

    public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(CEWorld world, ImmutableBlockState state) {
        return BlockEntityController.createTickerHelper(AbstractRouterBlockEntity::tick);
    }

    public static void tick(CEWorld world, BlockPos pos, ImmutableBlockState state, AbstractRouterBlockEntity self) {
        self.updateRpm(world, pos);
        self.maybeUpdateActivated(world, pos, self.relayedRpm > 0.0f);
        self.advanceAndRender(world, pos);
    }

    private void maybeUpdateActivated(CEWorld world, BlockPos pos, boolean active) {
        if (this.lastActivated != null && this.lastActivated == active) {
            return;
        }
        ImmutableBlockState cur = this.blockEntity().blockState();
        if (cur == null || cur.getProperty(PROP_ACTIVATED) == null) {
            this.lastActivated = active;
            return;
        }
        String now = AbstractRouterBlockEntity.enumName(cur, PROP_ACTIVATED);
        if (String.valueOf(active).equalsIgnoreCase(now)) {
            this.lastActivated = active;
            return;
        }
        ImmutableBlockState ns = AbstractRouterBlockEntity.withEnum(cur, PROP_ACTIVATED, String.valueOf(active));
        if (ns == cur) {
            return;
        }
        try {
            Object level = world.world().minecraftWorld();
            Object bp = MNms.INSTANCE.constructor$BlockPos(pos.x(), pos.y(), pos.z());
            Object nms = ns.customBlockState().minecraftState();
            MNms.INSTANCE.method$LevelWriter$setBlock(level, bp, nms, 2);
            this.lastActivated = active;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static ImmutableBlockState withEnum(ImmutableBlockState state, String prop, String valueName) {
        Property p = state.getProperty(prop);
        if (p == null) {
            return state;
        }
        try {
            Comparable value = p.valueByName(valueName.toLowerCase());
            if (value == null) {
                value = p.valueByName(valueName);
            }
            if (value == null) {
                return state;
            }
            return ImmutableBlockState.with((ImmutableBlockState)state, (Property)p, value);
        }
        catch (Throwable t) {
            return state;
        }
    }

    private void advanceAndRender(CEWorld world, BlockPos pos) {
        float inc = ConveyorMath.progressPerTick(this.moveRpm, 64.0f, 16);
        boolean powered = this.moveRpm > 0.0f;
        List viewers = world.world().getTrackedBy(new ChunkPos(pos));
        for (int i = 0; i < this.slots; ++i) {
            if (this.slotEmpty(i)) {
                this.despawnSlot(viewers, i);
                this.entryDir[i] = null;
                this.exitDir[i] = null;
                this.progress[i] = 0.0f;
                continue;
            }
            if (this.entryDir[i] == null) {
                this.entryDir[i] = this.facing().opposite();
            }
            if (powered) {
                if (this.exitDir[i] == null) {
                    Direction ex;
                    this.progress[i] = Math.min(0.5f, this.progress[i] + inc);
                    if (this.progress[i] >= 0.5f && (ex = this.chooseExit(world, pos)) != null) {
                        this.exitDir[i] = ex;
                    }
                } else {
                    this.progress[i] = Math.min(1.0f, this.progress[i] + inc);
                    if (this.progress[i] >= 1.0f && this.dispatch(world, pos, this.exitDir[i], i, viewers)) {
                        this.onDispatched(this.exitDir[i]);
                        this.clearSlot(i);
                        this.entryDir[i] = null;
                        this.exitDir[i] = null;
                        this.progress[i] = 0.0f;
                        continue;
                    }
                }
            }
            this.renderSlot(viewers, pos, i);
        }
    }

    private boolean dispatch(CEWorld world, BlockPos pos, Direction dir, int i, List<Player> viewers) {
        ConveyorReceiver r = ConveyorRouting.receiverAt(world, pos, dir);
        if (r == null || r.isFull()) {
            return false;
        }
        ItemStack item = this.bukkitSlot(i);
        if (item == null) {
            return true;
        }
        if (r instanceof ConveyorBlockEntity) {
            ConveyorBlockEntity belt = (ConveyorBlockEntity)r;
            if (belt.adoptFromFunnel(world, item, this.jitter[i], this.displays[i], this.spawned[i], dir.opposite())) {
                this.displays[i] = null;
                this.spawned[i] = false;
                return true;
            }
            return false;
        }
        if (r instanceof ConveyorDisplayReceiver) {
            ConveyorDisplayReceiver dr = (ConveyorDisplayReceiver)r;
            if (dr.adoptConveyorItem(item, this.jitter[i], this.displays[i], this.spawned[i], dir)) {
                this.displays[i] = null;
                this.spawned[i] = false;
                return true;
            }
            return false;
        }
        if (r.receiveConveyorItem(item, dir, this.jitter[i])) {
            this.despawnSlot(viewers, i);
            return true;
        }
        return false;
    }

    private void renderSlot(List<Player> viewers, BlockPos pos, int i) {
        Direction move;
        Vector3f rel;
        if (this.displays[i] == null) {
            this.displays[i] = new ConveyorItemDisplay();
        }
        ConveyorItemDisplay d = this.displays[i];
        d.setNmsItem(this.getItem(i));
        Vector3f center = new Vector3f(0.5f, 0.28f, 0.5f);
        Vector3f entryEdge = new Vector3f(0.5f + (float)this.entryDir[i].stepX() * 0.5f, 0.28f, 0.5f + (float)this.entryDir[i].stepZ() * 0.5f);
        if (this.exitDir[i] == null) {
            rel = ConveyorMath.interpolate(entryEdge, center, Math.min(1.0f, this.progress[i] * 2.0f));
            move = this.entryDir[i].opposite();
        } else {
            Vector3f exitEdge = new Vector3f(0.5f + (float)this.exitDir[i].stepX() * 0.5f, 0.28f, 0.5f + (float)this.exitDir[i].stepZ() * 0.5f);
            rel = ConveyorMath.interpolate(center, exitEdge, Math.max(0.0f, (this.progress[i] - 0.5f) * 2.0f));
            move = this.exitDir[i];
        }
        Quaternionf rot = ConveyorMath.itemRotation(move.stepX(), move.stepZ(), 0);
        rot.rotateY(this.jitter[i]);
        d.setRotation(rot);
        d.render(viewers, (float)pos.x() + rel.x, (float)pos.y() + rel.y, (float)pos.z() + rel.z, !this.spawned[i]);
        d.consumeRotationDirty();
        this.spawned[i] = true;
    }

    private void despawnSlot(List<Player> viewers, int i) {
        if (this.displays[i] != null && this.spawned[i]) {
            for (Player p : viewers) {
                this.displays[i].despawn(p);
            }
            this.displays[i].clearShown();
            this.spawned[i] = false;
        }
    }

    public void dropAndDespawn() {
        try {
            CEWorld world = this.blockEntity().world();
            BlockPos pos = this.blockEntity().pos();
            List viewers = world.world().getTrackedBy(new ChunkPos(pos));
            World bw = (World)world.world.platformWorld();
            for (int i = 0; i < this.slots; ++i) {
                if (!this.slotEmpty(i) && bw != null) {
                    bw.dropItem(new Location(bw, (double)pos.x() + 0.5, (double)pos.y() + 0.5, (double)pos.z() + 0.5), this.bukkitSlot(i));
                }
                this.despawnSlot(viewers, i);
                this.clearSlot(i);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

