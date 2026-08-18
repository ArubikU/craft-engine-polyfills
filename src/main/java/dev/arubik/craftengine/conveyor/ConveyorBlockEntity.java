/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.SignalGetter
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Direction
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  net.momirealms.craftengine.core.world.ChunkPos
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.Item
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.conveyor;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.block.entity.PersistentWorldlyBlockEntity;
import dev.arubik.craftengine.contraption.assembly.ContraptionCapture;
import dev.arubik.craftengine.contraption.level.ContraptionBoundary;
import dev.arubik.craftengine.conveyor.ConveyorDisplayReceiver;
import dev.arubik.craftengine.conveyor.ConveyorItemDisplay;
import dev.arubik.craftengine.conveyor.ConveyorMath;
import dev.arubik.craftengine.conveyor.ConveyorPart;
import dev.arubik.craftengine.conveyor.ConveyorReceiver;
import dev.arubik.craftengine.conveyor.ConveyorSlope;
import dev.arubik.craftengine.rotation.RpmConsumer;
import dev.arubik.craftengine.rotation.RpmProvider;
import dev.arubik.craftengine.util.MNms;
import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.util.TypedKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.ChunkPos;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ConveyorBlockEntity
extends PersistentWorldlyBlockEntity
implements RpmConsumer,
ConveyorReceiver {
    public static final float BASE_RPM = 64.0f;
    public static final int BASE_TRAVEL_TICKS = 16;
    public static final int MAX_LENGTH = 64;
    public static final int PICKUP_INTERVAL = 5;
    public static final double PICKUP_RADIUS = 0.75;
    public static final String PROP_FACING = "facing";
    public static final String PROP_SLOPE = "slope";
    public static final String PROP_PART = "part";
    private Direction defaultFacing = Direction.NORTH;
    private net.momirealms.craftengine.core.world.BlockPos prevPos;
    private float inputRpm = 0.0f;
    private float effectiveRpm = 0.0f;
    private RpmProvider drivingMotor;
    private int tickCounter = 0;
    public static final int REDSTONE_SCAN_INTERVAL = 10;
    private boolean redstonePoweredCache = false;
    private int redstoneScanCooldown = -1;
    private boolean stateLoaded = false;
    private boolean dirty = false;
    public static final float DEFAULT_STRESS_IMPACT = 4.0f;
    public static final int DEFAULT_SLOTS = 4;
    private int baseTravelTicks = 16;
    private float baseRpm = 64.0f;
    private float stressImpact = 4.0f;
    private final int slots;
    private final float[] progress;
    private final float[] jitter;
    private final Direction[] entryDir;
    private final ConveyorItemDisplay[] displays;
    private final boolean[] spawned;
    private static boolean teardownInProgress = false;
    private static final String PROP_ACTIVATED = "activated";
    private Boolean lastActivated = null;
    private static final TypedKey<String> PREV_KEY = TypedKey.of("craftengine", "cv_prev", NbtType.STRING);

    public RpmProvider drivingMotor() {
        return this.drivingMotor;
    }

    public ConveyorBlockEntity(BlockEntity blockEntity) {
        this(blockEntity, null, 16, 64.0f, 4.0f, 4);
    }

    public ConveyorBlockEntity(BlockEntity blockEntity, Direction defaultFacing) {
        this(blockEntity, defaultFacing, 16, 64.0f, 4.0f, 4);
    }

    public ConveyorBlockEntity(BlockEntity blockEntity, Direction defaultFacing, int baseTravelTicks, float baseRpm) {
        this(blockEntity, defaultFacing, baseTravelTicks, baseRpm, 4.0f, 4);
    }

    public ConveyorBlockEntity(BlockEntity blockEntity, Direction defaultFacing, int baseTravelTicks, float baseRpm, float stressImpact) {
        this(blockEntity, defaultFacing, baseTravelTicks, baseRpm, stressImpact, 4);
    }

    public ConveyorBlockEntity(BlockEntity blockEntity, Direction defaultFacing, int baseTravelTicks, float baseRpm, float stressImpact, int slots) {
        super(blockEntity, Math.max(1, slots));
        if (defaultFacing != null) {
            this.defaultFacing = defaultFacing;
        }
        this.baseTravelTicks = baseTravelTicks > 0 ? baseTravelTicks : 16;
        this.baseRpm = baseRpm > 0.0f ? baseRpm : 64.0f;
        this.stressImpact = stressImpact >= 0.0f ? stressImpact : 4.0f;
        this.slots = Math.max(1, slots);
        this.progress = new float[this.slots];
        this.jitter = new float[this.slots];
        this.entryDir = new Direction[this.slots];
        this.displays = new ConveyorItemDisplay[this.slots];
        this.spawned = new boolean[this.slots];
    }

    private float spacing() {
        return 1.0f / (float)this.slots;
    }

    static List<Player> viewersOf(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos) {
        ContraptionBoundary contraption = ConveyorBlockEntity.contraptionOf(world);
        if (contraption != null) {
            return contraption.realViewers(new BlockPos(pos.x(), pos.y(), pos.z()));
        }
        return world.world().getTrackedBy(new ChunkPos(pos));
    }

    static ContraptionBoundary contraptionOf(CEWorld world) {
        return ContraptionBoundary.of((Level)world.world().minecraftWorld()).orElse(null);
    }

    private boolean slotEmpty(int i) {
        net.minecraft.world.item.ItemStack s = this.getItem(i);
        return s == null || s.isEmpty();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < this.slots; ++i) {
            if (this.slotEmpty(i)) continue;
            return false;
        }
        return true;
    }

    private ItemStack bukkitSlot(int i) {
        if (this.slotEmpty(i)) {
            return null;
        }
        return CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)this.getItem(i));
    }

    private int firstEmptySlot() {
        for (int i = 0; i < this.slots; ++i) {
            if (!this.slotEmpty(i)) continue;
            return i;
        }
        return -1;
    }

    private boolean backHasRoom() {
        if (this.firstEmptySlot() < 0) {
            return false;
        }
        float gap = this.spacing();
        for (int i = 0; i < this.slots; ++i) {
            if (this.slotEmpty(i) || !(this.progress[i] < gap)) continue;
            return false;
        }
        return true;
    }

    private int frontSlot() {
        int best = -1;
        for (int i = 0; i < this.slots; ++i) {
            if (this.slotEmpty(i) || best >= 0 && !(this.progress[i] > this.progress[best])) continue;
            best = i;
        }
        return best;
    }

    private static float randJitter() {
        return (float)((Math.random() - 0.5) * Math.toRadians(18.0));
    }

    @Override
    public int[] getSlotsForFace(net.minecraft.core.Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, net.minecraft.world.item.ItemStack stack, net.minecraft.core.Direction direction) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, net.minecraft.world.item.ItemStack stack, net.minecraft.core.Direction direction) {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    public void setChanged() {
    }

    private static String enumName(ImmutableBlockState state, String name) {
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
        String n = ConveyorBlockEntity.enumName(this.blockEntity().blockState(), PROP_FACING);
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

    public ConveyorSlope slope() {
        return ConveyorSlope.fromName(ConveyorBlockEntity.enumName(this.blockEntity().blockState(), PROP_SLOPE));
    }

    public ConveyorPart part() {
        return ConveyorPart.fromName(ConveyorBlockEntity.enumName(this.blockEntity().blockState(), PROP_PART));
    }

    @Override
    public void setInputRpm(float rpm) {
        this.inputRpm = rpm;
    }

    @Override
    public float getInputRpm() {
        return this.inputRpm;
    }

    public float effectiveRpm() {
        return this.effectiveRpm;
    }

    public net.momirealms.craftengine.core.world.BlockPos prevPos() {
        return this.prevPos;
    }

    public void setPrevPos(net.momirealms.craftengine.core.world.BlockPos prevPos) {
        this.prevPos = prevPos;
        this.dirty = true;
    }

    public boolean acceptItem(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            return false;
        }
        if (!this.backHasRoom()) {
            return false;
        }
        int i = this.firstEmptySlot();
        if (i < 0) {
            return false;
        }
        this.setItem(i, CraftItemStack.asNMSCopy((ItemStack)stack));
        this.progress[i] = 0.0f;
        this.jitter[i] = ConveyorBlockEntity.randJitter();
        this.entryDir[i] = this.facing().opposite();
        this.dirty = true;
        return true;
    }

    public ItemStack takeSlot() {
        int i = this.frontSlot();
        if (i < 0) {
            return null;
        }
        ItemStack out = this.bukkitSlot(i);
        this.clearSlot(i);
        return out;
    }

    public ItemStack putSlot(ItemStack hand) {
        if (hand == null || hand.getType().isAir()) {
            return hand;
        }
        if (this.acceptItem(hand)) {
            return new ItemStack(Material.AIR);
        }
        return hand;
    }

    public ItemStack peekCarried() {
        int i = this.frontSlot();
        return i < 0 ? null : this.bukkitSlot(i);
    }

    public int peekCarriedDisplayId() {
        int i = this.frontSlot();
        if (i < 0 || this.displays[i] == null) {
            return -1;
        }
        return this.displays[i].entityId();
    }

    public void forEachCarried(CarriedVisitor v) {
        for (int i = 0; i < this.slots; ++i) {
            ItemStack cur;
            int did;
            ItemStack rep;
            if (this.slotEmpty(i) || (rep = v.visit(did = this.displays[i] != null ? this.displays[i].entityId() : -1, cur = this.bukkitSlot(i))) == cur) continue;
            if (rep == null || rep.getType().isAir()) {
                this.clearSlot(i);
            } else {
                this.setItem(i, CraftItemStack.asNMSCopy((ItemStack)rep));
                this.dirty = true;
                if (this.displays[i] != null) {
                    this.displays[i].setNmsItem(this.getItem(i).copy());
                }
            }
            this.pushDisplay(i);
        }
    }

    private void pushDisplay(int i) {
        try {
            List<Player> viewers = ConveyorBlockEntity.viewersOf(this.blockEntity().world(), this.pos());
            if (this.slotEmpty(i)) {
                this.despawnSlotFor(viewers, i);
            } else if (this.displays[i] != null) {
                for (Player p : viewers) {
                    this.displays[i].updateMetadata(p);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public boolean replaceCarried(ItemStack replacement) {
        int i = this.frontSlot();
        if (i < 0) {
            return false;
        }
        if (replacement == null || replacement.getType().isAir()) {
            this.clearSlot(i);
            return true;
        }
        this.setItem(i, CraftItemStack.asNMSCopy((ItemStack)replacement));
        this.dirty = true;
        if (this.displays[i] != null) {
            this.displays[i].setNmsItem(this.getItem(i).copy());
        }
        return true;
    }

    private void clearSlot(int i) {
        this.setItem(i, net.minecraft.world.item.ItemStack.EMPTY);
        this.progress[i] = 0.0f;
        this.entryDir[i] = null;
        this.dirty = true;
        if (this.displays[i] != null) {
            this.displays[i].setNmsItem(net.minecraft.world.item.ItemStack.EMPTY);
        }
    }

    public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(CEWorld world, ImmutableBlockState state) {
        return BlockEntityController.createTickerHelper(ConveyorBlockEntity::tick);
    }

    public static void tick(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, ImmutableBlockState state, ConveyorBlockEntity self) {
        self.serverTick(world, pos);
    }

    private void serverTick(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos) {
        ConveyorBlockEntity upstream;
        Direction facing = this.facing();
        if (!this.stateLoaded) {
            this.loadState(world);
            this.stateLoaded = true;
        }
        if (this.dirty) {
            this.dirty = false;
            this.saveState(world);
        }
        if ((upstream = this.upstreamConveyor(world, pos, facing)) != null) {
            this.effectiveRpm = upstream.effectiveRpm();
            this.drivingMotor = upstream.drivingMotor();
        } else {
            RpmProvider motor;
            RpmProvider startM = this.motorAround(world, pos, facing);
            ConveyorBlockEntity tail = this.tailSegment(world, facing);
            RpmProvider tailM = this.motorAround(world, tail.pos(), facing.opposite());
            this.drivingMotor = motor = ConveyorBlockEntity.strongerMotor(startM, tailM);
            if (motor == null) {
                this.effectiveRpm = this.inputRpm;
            } else {
                float potRpm = motor.potentialRpm();
                float loadRatio = this.baseRpm > 0.0f ? potRpm / this.baseRpm : 0.0f;
                float lineStress = (float)this.countSegments(world, facing) * this.stressImpact * loadRatio;
                motor.reportStressLoad(lineStress);
                float rpm = motor.getRpm();
                float f = this.effectiveRpm = rpm > 0.0f ? Math.max(this.inputRpm, rpm) : 0.0f;
            }
        }
        if (this.redstonePoweredThrottled(world, pos)) {
            this.effectiveRpm = 0.0f;
        }
        this.maybeUpdateActivated(world, pos, this.effectiveRpm > 0.0f);
        if (++this.tickCounter >= 5) {
            this.tickCounter = 0;
            if (this.backHasRoom()) {
                this.tryPickup(world, pos);
            }
        }
        if (this.isEmpty()) {
            this.despawnAll(world, pos);
            return;
        }
        float inc = ConveyorMath.progressPerTick(this.effectiveRpm, this.baseRpm, this.baseTravelTicks);
        if (inc > 0.0f) {
            float gap = this.spacing();
            int front = this.frontSlot();
            float[] snapshot = (float[])this.progress.clone();
            for (int i = 0; i < this.slots; ++i) {
                if (this.slotEmpty(i)) continue;
                if (i == front) {
                    float desired = snapshot[i] + inc;
                    if (desired > 1.0f) {
                        if (this.tryHandOff(world, pos, facing, i, desired - 1.0f)) continue;
                        this.progress[i] = 1.0f;
                        continue;
                    }
                    this.progress[i] = Math.max(this.progress[i], desired);
                    continue;
                }
                float ahead = 1.0f + gap;
                for (int j = 0; j < this.slots; ++j) {
                    if (j == i || this.slotEmpty(j) || !(snapshot[j] > snapshot[i]) || !(snapshot[j] < ahead)) continue;
                    ahead = snapshot[j];
                }
                float cap = Math.min(1.0f, ahead - gap);
                this.progress[i] = Math.max(this.progress[i], Math.min(cap, snapshot[i] + inc));
            }
        }
        this.renderAll(world, pos, facing);
    }

    private void tryPickup(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos) {
        try {
            World bukkitWorld = (World)world.world().platformWorld();
            if (bukkitWorld == null) {
                return;
            }
            Location center = new Location(bukkitWorld, (double)pos.x() + 0.5, (double)pos.y() + 0.5, (double)pos.z() + 0.5);
            double cx = (double)pos.x() + 0.5;
            double cy = (double)pos.y() + 0.5;
            double cz = (double)pos.z() + 0.5;
            ServerLevel serverLevel = ((CraftWorld)bukkitWorld).getHandle();
            AABB aabb = new AABB(cx - 0.75, cy - 0.75, cz - 0.75, cx + 0.75, cy + 0.75, cz + 0.75);
            Item nearest = null;
            double best = Double.MAX_VALUE;
            ContraptionBoundary boundary = ContraptionBoundary.of((Level)serverLevel).orElse(null);
            @SuppressWarnings("unchecked")
            List<ItemEntity> items = boundary != null ? boundary.getLocalEntities(ItemEntity.class, aabb, e -> !e.isRemoved()) : serverLevel.getEntitiesOfClass(ItemEntity.class, aabb, e -> !e.isRemoved());
            for (ItemEntity nms : items) {
                double d;
                Item item = (Item)nms.getBukkitEntity();
                if (item.isDead() || !item.isValid() || !((d = item.getLocation().distanceSquared(center)) < best)) continue;
                best = d;
                nearest = item;
            }
            if (nearest == null) {
                return;
            }
            int i = this.firstEmptySlot();
            if (i < 0) {
                return;
            }
            ItemStack stack = nearest.getItemStack();
            this.setItem(i, CraftItemStack.asNMSCopy((ItemStack)stack));
            this.progress[i] = 0.0f;
            this.jitter[i] = ConveyorBlockEntity.randJitter();
            this.entryDir[i] = this.facing().opposite();
            this.dirty = true;
            nearest.remove();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private boolean tryHandOff(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, Direction facing, int i, float startProgress) {
        ItemStack item = this.bukkitSlot(i);
        if (item == null) {
            return true;
        }
        ConveyorBlockEntity next = this.downstreamConveyor(world, pos, facing);
        if (next == null) {
            for (net.momirealms.craftengine.core.world.BlockPos cand : ConveyorBlockEntity.exitCandidates(pos, facing)) {
                ConveyorBlockEntity c = ConveyorBlockEntity.conveyorAt(world, cand);
                if (c == null || c == this) continue;
                next = c;
                break;
            }
        }
        if (next != null) {
            Direction entry = facing.opposite();
            if (next.adoptItem(item, this.jitter[i], this.displays[i], this.spawned[i], startProgress, entry)) {
                this.displays[i] = null;
                this.spawned[i] = false;
                this.clearSlot(i);
                next.renderAll(world, next.pos(), next.facing());
                return true;
            }
            return false;
        }
        ConveyorReceiver recv = this.receiverAt(world, pos, facing);
        if (recv != null) {
            ConveyorDisplayReceiver dr;
            if (recv.isFull()) {
                return false;
            }
            if (recv instanceof ConveyorDisplayReceiver && (dr = (ConveyorDisplayReceiver)recv).adoptConveyorItem(item, this.jitter[i], this.displays[i], this.spawned[i], facing)) {
                this.displays[i] = null;
                this.spawned[i] = false;
                this.clearSlot(i);
                return true;
            }
            if (recv.receiveConveyorItem(item, facing, this.jitter[i])) {
                this.despawnSlot(world, pos, i);
                this.clearSlot(i);
                return true;
            }
            return false;
        }
        this.dropAtEnd(world, pos, facing, item);
        this.despawnSlot(world, pos, i);
        this.clearSlot(i);
        return true;
    }

    private ConveyorReceiver receiverAt(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, Direction facing) {
        for (net.momirealms.craftengine.core.world.BlockPos cand : ConveyorBlockEntity.exitCandidates(pos, facing)) {
            BlockEntityController blockEntityController;
            BlockEntity be = world.getBlockEntityAtIfLoaded(cand);
            if (be == null || !((blockEntityController = be.controller) instanceof ConveyorReceiver)) continue;
            ConveyorReceiver r = (ConveyorReceiver)blockEntityController;
            if (be.controller instanceof ConveyorBlockEntity) continue;
            return r;
        }
        return null;
    }

    private boolean redstonePoweredThrottled(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos) {
        if (this.redstoneScanCooldown < 0) {
            this.redstoneScanCooldown = Math.floorMod(pos.x() * 31 + pos.y() * 17 + pos.z(), 10);
            this.redstonePoweredCache = this.redstonePowered(world, pos);
        } else if (--this.redstoneScanCooldown <= 0) {
            this.redstoneScanCooldown = 10;
            this.redstonePoweredCache = this.redstonePowered(world, pos);
        }
        return this.redstonePoweredCache;
    }

    private boolean redstonePowered(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos) {
        try {
            Object mw = world.world().minecraftWorld();
            if (mw instanceof SignalGetter) {
                SignalGetter sg = (SignalGetter)mw;
                return sg.hasNeighborSignal(new BlockPos(pos.x(), pos.y(), pos.z()));
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return false;
    }

    public int analogSignal() {
        int occupied = 0;
        for (int i = 0; i < this.slots; ++i) {
            if (this.slotEmpty(i)) continue;
            ++occupied;
        }
        if (occupied == 0) {
            return 0;
        }
        return Math.min(15, (int)Math.floor((float)occupied / (float)this.slots * 14.0f) + 1);
    }

    @Override
    public boolean isFull() {
        return !this.backHasRoom();
    }

    @Override
    public boolean receiveConveyorItem(ItemStack stack, Direction sourceFacing) {
        return this.receiveConveyorItem(stack, sourceFacing, ConveyorBlockEntity.randJitter());
    }

    @Override
    public boolean receiveConveyorItem(ItemStack stack, Direction sourceFacing, float carriedJitter) {
        if (stack == null || stack.getType().isAir() || !this.backHasRoom()) {
            return false;
        }
        int i = this.firstEmptySlot();
        if (i < 0) {
            return false;
        }
        this.setItem(i, CraftItemStack.asNMSCopy((ItemStack)stack));
        this.progress[i] = 0.0f;
        this.jitter[i] = carriedJitter;
        this.entryDir[i] = sourceFacing != null ? sourceFacing.opposite() : this.facing().opposite();
        this.dirty = true;
        return true;
    }

    boolean adoptItem(ItemStack stack, float carriedJitter, ConveyorItemDisplay disp, boolean wasSpawned, float startProgress, Direction entry) {
        if (stack == null || stack.getType().isAir()) {
            return false;
        }
        float st = ConveyorMath.clamp01(startProgress);
        float gap = this.spacing();
        int i = -1;
        for (int j = 0; j < this.slots; ++j) {
            if (this.slotEmpty(j)) {
                if (i >= 0) continue;
                i = j;
                continue;
            }
            if (!(this.progress[j] < st + gap)) continue;
            return false;
        }
        if (i < 0) {
            return false;
        }
        this.setItem(i, CraftItemStack.asNMSCopy((ItemStack)stack));
        this.progress[i] = st;
        this.jitter[i] = carriedJitter;
        this.entryDir[i] = entry != null ? entry : this.facing().opposite();
        this.displays[i] = disp;
        this.spawned[i] = wasSpawned;
        this.dirty = true;
        return true;
    }

    public boolean adoptFromFunnel(CEWorld world, ItemStack stack, float carriedJitter, ConveyorItemDisplay disp, boolean wasSpawned, Direction entry) {
        if (this.adoptItem(stack, carriedJitter, disp, wasSpawned, 0.0f, entry)) {
            this.renderAll(world, this.pos(), this.facing());
            return true;
        }
        return false;
    }

    private void dropAllItems(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, Direction facing) {
        for (int i = 0; i < this.slots; ++i) {
            if (this.slotEmpty(i)) continue;
            this.dropAtEnd(world, pos, facing, this.bukkitSlot(i));
            this.clearSlot(i);
        }
        this.clear();
    }

    private void dropAtEnd(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, Direction facing, ItemStack item) {
        try {
            World bukkitWorld = (World)world.world().platformWorld();
            if (bukkitWorld != null && item != null && !item.getType().isAir()) {
                Vector3f end = this.endRel(facing);
                double dx = (double)((float)pos.x() + end.x) + (double)facing.stepX() * 0.45;
                double dy = (float)pos.y() + end.y;
                double dz = (double)((float)pos.z() + end.z) + (double)facing.stepZ() * 0.45;
                Item dropped = bukkitWorld.dropItem(new Location(bukkitWorld, dx, dy, dz), item);
                float inc = ConveyorMath.progressPerTick(this.effectiveRpm, this.baseRpm, this.baseTravelTicks);
                if (inc <= 0.0f) {
                    inc = ConveyorMath.progressPerTick(64.0f, 64.0f, 16);
                }
                int sy = this.slope().stepY();
                dropped.setVelocity(new Vector((double)facing.stepX() * Math.max((double)inc, 0.12), (double)((float)sy * inc) + 0.05, (double)facing.stepZ() * Math.max((double)inc, 0.12)));
                dropped.setPickupDelay(20);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private Vector3f startRel(Direction facing) {
        return ConveyorMath.startPoint(facing.stepX(), facing.stepZ(), this.slope().stepY());
    }

    private Vector3f endRel(Direction facing) {
        return ConveyorMath.endPoint(facing.stepX(), facing.stepZ(), this.slope().stepY());
    }

    private void renderAll(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, Direction facing) {
        List<Player> viewers = ConveyorBlockEntity.viewersOf(world, pos);
        ContraptionBoundary contraption = ConveyorBlockEntity.contraptionOf(world);
        int slopeY = this.slope().stepY();
        Vector3f startP = this.startRel(facing);
        Vector3f exit = this.endRel(facing);
        Vector3f center = new Vector3f(0.5f, (startP.y + exit.y) * 0.5f, 0.5f);
        for (int i = 0; i < this.slots; ++i) {
            Quaternionf rot;
            Vector3f rel;
            if (this.slotEmpty(i)) {
                this.despawnSlotFor(viewers, i);
                continue;
            }
            if (this.displays[i] == null) {
                this.displays[i] = new ConveyorItemDisplay();
            }
            ConveyorItemDisplay d = this.displays[i];
            d.setNmsItem(this.getItem(i).copy());
            Direction entry = this.entryDir[i] != null ? this.entryDir[i] : facing.opposite();
            boolean inLine = entry == facing.opposite();
            float p = this.progress[i];
            if (p < 0.5f) {
                Vector3f entryPt = inLine ? this.startRel(facing) : new Vector3f(0.5f + (float)entry.stepX() * 0.5f, 0.28f, 0.5f + (float)entry.stepZ() * 0.5f);
                rel = ConveyorMath.interpolate(entryPt, center, p * 2.0f);
                rot = inLine ? ConveyorMath.itemRotation(facing.stepX(), facing.stepZ(), slopeY) : ConveyorMath.itemRotation(-entry.stepX(), -entry.stepZ(), 0);
            } else {
                rel = ConveyorMath.interpolate(center, exit, (p - 0.5f) * 2.0f);
                rot = ConveyorMath.itemRotation(facing.stepX(), facing.stepZ(), slopeY);
            }
            rot.rotateY(this.jitter[i]);
            double wx = (float)pos.x() + rel.x;
            double wy = (float)pos.y() + rel.y;
            double wz = (float)pos.z() + rel.z;
            if (contraption != null) {
                Vec3 real = contraption.realWorldPositionOf(new Vec3(wx, wy, wz));
                wx = real.x;
                wy = real.y;
                wz = real.z;
                rot = contraption.realOrientationOf(rot);
            }
            d.setRotation(rot);
            boolean meta = d.consumeRotationDirty();
            d.render(viewers, wx, wy, wz, meta);
            this.spawned[i] = true;
        }
    }

    private void despawnSlotFor(List<Player> viewers, int i) {
        if (this.displays[i] != null) {
            for (Player p : viewers) {
                this.displays[i].despawn(p);
            }
            this.displays[i].clearShown();
            this.spawned[i] = false;
        }
    }

    private void despawnSlot(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, int i) {
        this.despawnSlotFor(ConveyorBlockEntity.viewersOf(world, pos), i);
    }

    private void despawnAll(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos) {
        List<Player> viewers = ConveyorBlockEntity.viewersOf(world, pos);
        for (int i = 0; i < this.slots; ++i) {
            this.despawnSlotFor(viewers, i);
        }
    }

    static ConveyorBlockEntity conveyorAt(CEWorld world, net.momirealms.craftengine.core.world.BlockPos rel) {
        BlockEntityController blockEntityController;
        BlockEntity be = world.getBlockEntityAtIfLoaded(rel);
        if (be != null && (blockEntityController = be.controller) instanceof ConveyorBlockEntity) {
            ConveyorBlockEntity conveyor = (ConveyorBlockEntity)blockEntityController;
            return conveyor;
        }
        return null;
    }

    private ConveyorBlockEntity tailSegment(CEWorld world, Direction facing) {
        ConveyorBlockEntity next;
        ConveyorBlockEntity cur = this;
        for (int i = 0; i < 64 && (next = cur.downstreamConveyor(world, cur.pos(), facing)) != null && next != cur; ++i) {
            cur = next;
        }
        return cur;
    }

    private int countSegments(CEWorld world, Direction facing) {
        ConveyorBlockEntity next;
        int n = 1;
        ConveyorBlockEntity cur = this;
        for (int i = 0; i < 64 && (next = cur.downstreamConveyor(world, cur.pos(), facing)) != null && next != cur; ++i) {
            ++n;
            cur = next;
        }
        return n;
    }

    private RpmProvider motorAround(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, Direction exclude) {
        RpmProvider best = null;
        for (Direction d : Direction.values()) {
            RpmProvider motor;
            BlockEntityController blockEntityController;
            BlockEntity be;
            if (d.stepY() != 0 || d == exclude || (be = world.getBlockEntityAtIfLoaded(pos.relative(d))) == null || !((blockEntityController = be.controller) instanceof RpmProvider) || !(motor = (RpmProvider)blockEntityController).rpmReaches(pos) || best != null && !ConveyorBlockEntity.preferMotor(motor, best)) continue;
            best = motor;
        }
        return best;
    }

    private static boolean preferMotor(RpmProvider cand, RpmProvider cur) {
        boolean curSrc;
        boolean candSrc = cand.isRpmSource();
        if (candSrc != (curSrc = cur.isRpmSource())) {
            return candSrc;
        }
        return cand.getRpm() > cur.getRpm();
    }

    private static RpmProvider strongerMotor(RpmProvider a, RpmProvider b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.getRpm() >= b.getRpm() ? a : b;
    }

    ConveyorBlockEntity downstreamConveyor(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, Direction facing) {
        for (net.momirealms.craftengine.core.world.BlockPos cand : ConveyorBlockEntity.exitCandidates(pos, facing)) {
            ConveyorBlockEntity c = ConveyorBlockEntity.conveyorAt(world, cand);
            if (c == null || !pos.equals(c.prevPos())) continue;
            return c;
        }
        return null;
    }

    ConveyorBlockEntity upstreamConveyor(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, Direction facing) {
        if (this.prevPos == null) {
            return null;
        }
        return ConveyorBlockEntity.conveyorAt(world, this.prevPos);
    }

    static List<net.momirealms.craftengine.core.world.BlockPos> exitCandidates(net.momirealms.craftengine.core.world.BlockPos pos, Direction facing) {
        net.momirealms.craftengine.core.world.BlockPos flat = pos.relative(facing);
        ArrayList<net.momirealms.craftengine.core.world.BlockPos> list = new ArrayList<net.momirealms.craftengine.core.world.BlockPos>(3);
        list.add(flat);
        list.add(new net.momirealms.craftengine.core.world.BlockPos(flat.x(), flat.y() + 1, flat.z()));
        list.add(new net.momirealms.craftengine.core.world.BlockPos(flat.x(), flat.y() - 1, flat.z()));
        return list;
    }

    net.momirealms.craftengine.core.world.BlockPos exitPos(Direction facing) {
        net.momirealms.craftengine.core.world.BlockPos flat = this.pos().relative(facing);
        int dy = this.slope() == ConveyorSlope.UP ? 1 : 0;
        return dy == 0 ? flat : new net.momirealms.craftengine.core.world.BlockPos(flat.x(), flat.y() + dy, flat.z());
    }

    boolean isTail(CEWorld world, Direction facing) {
        return this.downstreamConveyor(world, this.pos(), facing) == null;
    }

    public boolean extend(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, Direction facing, ConveyorSlope slope) {
        Block targetBlock;
        World bukkitWorld;
        if (this.part() != ConveyorPart.END) {
            return false;
        }
        if (this.lengthFromHere(world, facing) >= 64) {
            return false;
        }
        try {
            bukkitWorld = (World)world.world().platformWorld();
        }
        catch (Throwable t) {
            return false;
        }
        if (bukkitWorld == null) {
            return false;
        }
        net.momirealms.craftengine.core.world.BlockPos target = pos.relative(facing);
        int dy = slope.stepY();
        if (dy != 0) {
            target = new net.momirealms.craftengine.core.world.BlockPos(target.x(), target.y() + dy, target.z());
        }
        if (!ConveyorBlockEntity.isReplaceable(targetBlock = bukkitWorld.getBlockAt(target.x(), target.y(), target.z()))) {
            return false;
        }
        BlockDefinition def = (BlockDefinition)this.blockEntity().blockState().owner().value();
        Key blockId = def.id();
        ImmutableBlockState newState = ConveyorBlockEntity.stateWith(def.defaultState(), facing, slope, ConveyorPart.END);
        Location loc = new Location(bukkitWorld, (double)target.x(), (double)target.y(), (double)target.z());
        boolean placed = CraftEngineBlocks.place((Location)loc, (ImmutableBlockState)newState, (int)3, (boolean)false);
        if (!placed && !(placed = CraftEngineBlocks.place((Location)loc, (Key)blockId, (boolean)false))) {
            return false;
        }
        ConveyorBlockEntity.clearStaleData(bukkitWorld, target);
        ConveyorBlockEntity created = ConveyorBlockEntity.conveyorAt(world, target);
        if (created != null) {
            created.setPrevPos(pos);
        }
        ConveyorPart newPart = this.prevPos == null ? ConveyorPart.START : ConveyorPart.MIDDLE;
        this.applyPart(world, pos, facing, slope, newPart);
        return true;
    }

    public boolean extendStart(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, Direction facing, ConveyorSlope slope) {
        Block targetBlock;
        World bukkitWorld;
        if (this.prevPos != null) {
            return false;
        }
        if (this.lengthFromHere(world, facing) >= 64) {
            return false;
        }
        try {
            bukkitWorld = (World)world.world().platformWorld();
        }
        catch (Throwable t) {
            return false;
        }
        if (bukkitWorld == null) {
            return false;
        }
        net.momirealms.craftengine.core.world.BlockPos target = pos.relative(facing.opposite());
        int dy = slope.stepY();
        if (dy != 0) {
            target = new net.momirealms.craftengine.core.world.BlockPos(target.x(), target.y() - dy, target.z());
        }
        if (!ConveyorBlockEntity.isReplaceable(targetBlock = bukkitWorld.getBlockAt(target.x(), target.y(), target.z()))) {
            return false;
        }
        BlockDefinition def = (BlockDefinition)this.blockEntity().blockState().owner().value();
        Key blockId = def.id();
        ImmutableBlockState newState = ConveyorBlockEntity.stateWith(def.defaultState(), facing, slope, ConveyorPart.START);
        Location loc = new Location(bukkitWorld, (double)target.x(), (double)target.y(), (double)target.z());
        boolean placed = CraftEngineBlocks.place((Location)loc, (ImmutableBlockState)newState, (int)3, (boolean)false);
        if (!placed && !(placed = CraftEngineBlocks.place((Location)loc, (Key)blockId, (boolean)false))) {
            return false;
        }
        ConveyorBlockEntity.clearStaleData(bukkitWorld, target);
        this.setPrevPos(target);
        this.applyPart(world, pos, facing, slope, ConveyorPart.MIDDLE);
        return true;
    }

    private static void clearStaleData(World bukkitWorld, net.momirealms.craftengine.core.world.BlockPos at) {
        try {
            ServerLevel level = ((CraftWorld)bukkitWorld).getHandle();
            BlockPos nmsPos = new BlockPos(at.x(), at.y(), at.z());
            PersistentBlockEntity.executeAt((Level)level, nmsPos, PersistentBlockEntity::clear);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private int lengthFromHere(CEWorld world, Direction facing) {
        int n = 1;
        HashSet<net.momirealms.craftengine.core.world.BlockPos> seen = new HashSet<net.momirealms.craftengine.core.world.BlockPos>();
        for (ConveyorBlockEntity walk = this.upstreamConveyor(world, this.pos(), facing); walk != null && seen.add(walk.pos()); walk = walk.upstreamConveyor(world, walk.pos(), walk.facing())) {
            ++n;
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onBroken(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, Direction facing) {
        boolean isEnd;
        if (teardownInProgress) {
            return;
        }
        if (ContraptionCapture.isRemovingForCapture()) {
            return;
        }
        ConveyorBlockEntity up = this.upstreamConveyor(world, pos, facing);
        ConveyorBlockEntity down = this.downstreamConveyor(world, pos, facing);
        this.dropAllItems(world, pos, facing);
        this.despawnAll(world, pos);
        boolean isStart = up == null;
        boolean bl = isEnd = down == null;
        if (isStart && isEnd) {
            return;
        }
        if (isStart) {
            if (down.downstreamConveyor(world, down.pos(), down.facing()) == null) {
                teardownInProgress = true;
                try {
                    down.destroySegmentWithDrops(world);
                }
                finally {
                    teardownInProgress = false;
                }
                return;
            }
            down.setPrevPos(null);
            down.applyPart(world, down.pos(), down.facing(), down.slope(), ConveyorPart.START);
            return;
        }
        if (isEnd) {
            if (up.prevPos == null) {
                teardownInProgress = true;
                try {
                    up.destroySegmentWithDrops(world);
                }
                finally {
                    teardownInProgress = false;
                }
                return;
            }
            up.applyPart(world, up.pos(), up.facing(), up.slope(), ConveyorPart.END);
            return;
        }
        HashSet<net.momirealms.craftengine.core.world.BlockPos> visited = new HashSet<net.momirealms.craftengine.core.world.BlockPos>();
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
        }
        finally {
            teardownInProgress = false;
        }
    }

    private void destroySegmentWithDrops(CEWorld world) {
        net.momirealms.craftengine.core.world.BlockPos pos = this.pos();
        Direction facing = this.facing();
        this.dropAllItems(world, pos, facing);
        this.despawnAll(world, pos);
        try {
            Block b;
            World bukkitWorld = (World)world.world().platformWorld();
            if (bukkitWorld != null && !CraftEngineBlocks.remove((Block)(b = bukkitWorld.getBlockAt(pos.x(), pos.y(), pos.z())), (boolean)true)) {
                b.setType(Material.AIR, false);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void maybeUpdateActivated(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, boolean active) {
        if (this.lastActivated != null && this.lastActivated == active) {
            return;
        }
        ImmutableBlockState cur = this.blockEntity().blockState();
        if (cur == null || cur.getProperty(PROP_ACTIVATED) == null) {
            this.lastActivated = active;
            return;
        }
        String now = ConveyorBlockEntity.enumName(cur, PROP_ACTIVATED);
        if (String.valueOf(active).equalsIgnoreCase(now)) {
            this.lastActivated = active;
            return;
        }
        ImmutableBlockState ns = ConveyorBlockEntity.withEnum(cur, PROP_ACTIVATED, String.valueOf(active));
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

    private static ImmutableBlockState stateWith(ImmutableBlockState base, Direction facing, ConveyorSlope slope, ConveyorPart part) {
        ImmutableBlockState s = base;
        s = ConveyorBlockEntity.withEnum(s, PROP_FACING, facing.name());
        s = ConveyorBlockEntity.withEnum(s, PROP_SLOPE, slope.name());
        s = ConveyorBlockEntity.withEnum(s, PROP_PART, part.name());
        return s;
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

    private void applyPart(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, Direction facing, ConveyorSlope slope, ConveyorPart part) {
        try {
            ImmutableBlockState cur = this.blockEntity().blockState();
            if (cur == null) {
                return;
            }
            ImmutableBlockState ns = ConveyorBlockEntity.withEnum(cur, PROP_PART, part.name());
            if (ns == cur) {
                return;
            }
            Object level = world.world().minecraftWorld();
            Object bp = MNms.INSTANCE.constructor$BlockPos(pos.x(), pos.y(), pos.z());
            Object nms = ns.customBlockState().minecraftState();
            MNms.INSTANCE.method$LevelWriter$setBlock(level, bp, nms, 2);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    static boolean isReplaceable(Block block) {
        if (block == null) {
            return false;
        }
        Material m = block.getType();
        return m.isAir() || m == Material.WATER || m == Material.LAVA || m == Material.SHORT_GRASS || m == Material.TALL_GRASS || m == Material.SNOW;
    }

    private TypedKey<Float> progKey(int i) {
        return TypedKey.of("craftengine", "cv_prog" + i, NbtType.FLOAT);
    }

    private TypedKey<Float> jitKey(int i) {
        return TypedKey.of("craftengine", "cv_jit" + i, NbtType.FLOAT);
    }

    private TypedKey<String> entryKey(int i) {
        return TypedKey.of("craftengine", "cv_entry" + i, NbtType.STRING);
    }

    private void saveState(CEWorld world) {
        for (int i = 0; i < this.slots; ++i) {
            this.set(this.progKey(i), Float.valueOf(this.progress[i]));
            this.set(this.jitKey(i), Float.valueOf(this.jitter[i]));
            this.set(this.entryKey(i), this.entryDir[i] != null ? this.entryDir[i].name() : "");
        }
        this.set(PREV_KEY, this.prevPos != null ? this.prevPos.x() + "," + this.prevPos.y() + "," + this.prevPos.z() : "");
    }

    private void loadState(CEWorld world) {
        String[] xyz;
        for (int i = 0; i < this.slots; ++i) {
            this.progress[i] = this.getOrDefault(this.progKey(i), Float.valueOf(0.0f)).floatValue();
            this.jitter[i] = this.getOrDefault(this.jitKey(i), Float.valueOf(0.0f)).floatValue();
            String e = this.getOrDefault(this.entryKey(i), "");
            if (e == null || e.isEmpty()) continue;
            try {
                this.entryDir[i] = Direction.valueOf((String)e);
                continue;
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        String pp = this.getOrDefault(PREV_KEY, "");
        if (pp != null && !pp.isEmpty() && (xyz = pp.split(",")).length == 3) {
            this.prevPos = new net.momirealms.craftengine.core.world.BlockPos(Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2]));
        }
    }

    @Override
    public void onRemove() {
        super.onRemove();
        try {
            this.despawnAll(this.blockEntity().world(), this.pos());
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public void despawnRender() {
        this.despawnAll(this.blockEntity().world(), this.pos());
    }

    public static interface CarriedVisitor {
        public ItemStack visit(int var1, ItemStack var2);
    }
}

