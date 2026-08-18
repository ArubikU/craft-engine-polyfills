/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.Container
 *  net.minecraft.world.WorldlyContainer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.HopperBlockEntity
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Direction
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.Item
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.conveyor;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.contraption.level.ContraptionBoundary;
import dev.arubik.craftengine.conveyor.ConveyorBlockEntity;
import dev.arubik.craftengine.conveyor.ConveyorDisplayReceiver;
import dev.arubik.craftengine.conveyor.ConveyorItemDisplay;
import dev.arubik.craftengine.conveyor.ConveyorMath;
import dev.arubik.craftengine.conveyor.ConveyorReceiver;
import dev.arubik.craftengine.util.MNms;
import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.util.TypedKey;
import dev.arubik.craftengine.util.TypedKeys;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.world.CEWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FunnelBlockEntity
extends PersistentBlockEntity
implements ConveyorDisplayReceiver {
    public static final String PROP_FACING = "facing";
    public static final String PROP_MODE = "mode";
    public static final String PROP_ACTIVATED = "activated";
    private ItemStack transit;
    private float progress = 0.0f;
    private ConveyorItemDisplay display;
    private boolean spawned = false;
    private float carriedJitter = 0.0f;
    private boolean pendingDespawn = false;
    private boolean loaded = false;
    private boolean dirty = false;
    private Boolean lastActivated = null;
    private static final TypedKey<Float> KEY_PROG = TypedKey.of("craftengine", "funnel_prog", NbtType.FLOAT);

    public FunnelBlockEntity(BlockEntity blockEntity) {
        super(blockEntity);
    }

    private String enumName(String name) {
        ImmutableBlockState st = this.blockEntity().blockState();
        if (st == null) {
            return null;
        }
        Property p = st.getProperty(name);
        if (p == null) {
            return null;
        }
        Comparable v = st.get(p);
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

    private net.momirealms.craftengine.core.util.Direction facing() {
        String n = this.enumName(PROP_FACING);
        if (n != null) {
            try {
                return net.momirealms.craftengine.core.util.Direction.valueOf((String)n.toUpperCase());
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return net.momirealms.craftengine.core.util.Direction.NORTH;
    }

    private boolean outMode() {
        return !"in".equalsIgnoreCase(this.enumName(PROP_MODE));
    }

    private boolean redstonePowered() {
        try {
            World bw = (World)this.blockEntity().world().world.platformWorld();
            net.momirealms.craftengine.core.world.BlockPos p = this.blockEntity().pos();
            return bw != null && bw.getBlockAt(p.x(), p.y(), p.z()).isBlockIndirectlyPowered();
        }
        catch (Throwable t) {
            return false;
        }
    }

    private Container containerAt(net.momirealms.craftengine.core.util.Direction facing) {
        try {
            Level lvl = (Level)this.blockEntity().world().world.minecraftWorld();
            net.momirealms.craftengine.core.world.BlockPos p = this.blockEntity().pos();
            net.momirealms.craftengine.core.util.Direction o = facing.opposite();
            BlockPos np = new BlockPos(p.x() + o.stepX(), p.y() + o.stepY(), p.z() + o.stepZ());
            return HopperBlockEntity.getContainerAt((Level)lvl, (BlockPos)np);
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static Direction nms(net.momirealms.craftengine.core.util.Direction d) {
        return Direction.valueOf((String)d.name());
    }

    private static int[] facesFor(Container c, Direction face) {
        if (c instanceof WorldlyContainer) {
            WorldlyContainer wc = (WorldlyContainer)c;
            return wc.getSlotsForFace(face);
        }
        int[] all = new int[c.getContainerSize()];
        for (int i = 0; i < all.length; ++i) {
            all[i] = i;
        }
        return all;
    }

    private ConveyorBlockEntity belt(CEWorld world, net.momirealms.craftengine.core.util.Direction facing) {
        try {
            BlockEntityController blockEntityController;
            net.momirealms.craftengine.core.world.BlockPos bp = this.blockEntity().pos().relative(facing);
            BlockEntity be = world.getBlockEntityAtIfLoaded(bp);
            if (be != null && (blockEntityController = be.controller) instanceof ConveyorBlockEntity) {
                ConveyorBlockEntity c = (ConveyorBlockEntity)blockEntityController;
                return c;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private ConveyorReceiver beltReceiver(CEWorld world, net.momirealms.craftengine.core.util.Direction facing) {
        try {
            BlockEntityController blockEntityController;
            net.momirealms.craftengine.core.world.BlockPos bp = this.blockEntity().pos().relative(facing);
            BlockEntity be = world.getBlockEntityAtIfLoaded(bp);
            if (be != null && (blockEntityController = be.controller) instanceof ConveyorReceiver) {
                ConveyorReceiver r = (ConveyorReceiver)blockEntityController;
                return r;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private float inc(CEWorld world, net.momirealms.craftengine.core.util.Direction facing) {
        ConveyorBlockEntity bf = this.belt(world, facing);
        ConveyorBlockEntity bo = this.belt(world, facing.opposite());
        float rpm = 0.0f;
        if (bf != null) {
            rpm = Math.max(rpm, bf.effectiveRpm());
        }
        if (bo != null) {
            rpm = Math.max(rpm, bo.effectiveRpm());
        }
        if (rpm == 0.0f) {
            rpm = 64.0f;
        }
        return ConveyorMath.progressPerTick(rpm, 64.0f, 16);
    }

    @Override
    public boolean isFull() {
        return this.outMode() || this.transit != null;
    }

    @Override
    public boolean receiveConveyorItem(ItemStack stack, net.momirealms.craftengine.core.util.Direction sourceFacing) {
        return this.receiveConveyorItem(stack, sourceFacing, 0.0f);
    }

    @Override
    public boolean receiveConveyorItem(ItemStack stack, net.momirealms.craftengine.core.util.Direction sourceFacing, float jitter) {
        if (this.outMode() || this.transit != null || stack == null || stack.getType().isAir()) {
            return false;
        }
        this.transit = stack.clone();
        this.carriedJitter = jitter;
        this.progress = 0.0f;
        this.dirty = true;
        return true;
    }

    @Override
    public boolean adoptConveyorItem(ItemStack stack, float jitter, ConveyorItemDisplay incoming, boolean wasSpawned, net.momirealms.craftengine.core.util.Direction sourceFacing) {
        if (this.outMode() || this.transit != null || stack == null || stack.getType().isAir()) {
            return false;
        }
        if (this.display != null && this.display != incoming) {
            try {
                this.despawn(this.blockEntity().world());
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        this.transit = stack.clone();
        this.carriedJitter = jitter;
        this.progress = 0.0f;
        this.display = incoming;
        this.spawned = wasSpawned;
        this.pendingDespawn = false;
        this.dirty = true;
        return true;
    }

    public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(CEWorld world, ImmutableBlockState state) {
        return BlockEntityController.createTickerHelper(FunnelBlockEntity::tick);
    }

    public static void tick(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, ImmutableBlockState state, FunnelBlockEntity self) {
        self.serverTick(world);
    }

    private void serverTick(CEWorld world) {
        if (!this.loaded) {
            this.load();
            this.loaded = true;
        }
        if (this.pendingDespawn) {
            this.despawn(world);
            this.pendingDespawn = false;
        }
        net.momirealms.craftengine.core.util.Direction facing = this.facing();
        boolean out = this.outMode();
        if (this.redstonePowered()) {
            this.maybeUpdateActivated(world, false);
            this.renderTransit(world, facing, out);
            if (this.dirty) {
                this.dirty = false;
                this.save();
            }
            return;
        }
        if (out) {
            if (this.transit == null) {
                this.pullFromContainer(facing);
            }
            if (this.transit != null) {
                this.progress = Math.min(1.0f, this.progress + this.inc(world, facing));
                if (this.progress >= 1.0f) {
                    ConveyorReceiver belt = this.beltReceiver(world, facing);
                    if (belt instanceof ConveyorBlockEntity) {
                        ConveyorBlockEntity cbe = (ConveyorBlockEntity)belt;
                        if (cbe.adoptFromFunnel(world, this.transit, this.carriedJitter, this.display, this.spawned, facing.opposite())) {
                            this.display = null;
                            this.spawned = false;
                            this.transit = null;
                            this.progress = 0.0f;
                            this.dirty = true;
                            if (this.dirty) {
                                this.dirty = false;
                                this.save();
                            }
                            return;
                        }
                    } else if (belt instanceof ConveyorDisplayReceiver) {
                        ConveyorDisplayReceiver dr = (ConveyorDisplayReceiver)belt;
                        if (!dr.isFull() && dr.adoptConveyorItem(this.transit, this.carriedJitter, this.display, this.spawned, facing)) {
                            this.display = null;
                            this.spawned = false;
                            this.transit = null;
                            this.progress = 0.0f;
                            this.dirty = true;
                            if (this.dirty) {
                                this.dirty = false;
                                this.save();
                            }
                            return;
                        }
                    } else if (belt != null) {
                        if (!belt.isFull() && belt.receiveConveyorItem(this.transit, facing, this.carriedJitter)) {
                            this.transit = null;
                            this.progress = 0.0f;
                            this.dirty = true;
                            this.pendingDespawn = true;
                            if (this.dirty) {
                                this.dirty = false;
                                this.save();
                            }
                            return;
                        }
                    } else {
                        this.dropFront(facing);
                        this.clearTransit(world);
                    }
                }
            }
        } else if (this.transit != null) {
            this.progress = Math.min(1.0f, this.progress + this.inc(world, facing));
            if (this.progress >= 1.0f && this.insertToContainer(facing, this.transit)) {
                this.clearTransit(world);
            }
        }
        boolean running = this.containerAt(facing) != null;
        this.maybeUpdateActivated(world, running);
        this.renderTransit(world, facing, out);
        if (this.dirty) {
            this.dirty = false;
            this.save();
        }
    }

    private void pullFromContainer(net.momirealms.craftengine.core.util.Direction facing) {
        Container c = this.containerAt(facing);
        if (c == null) {
            return;
        }
        Direction face = FunnelBlockEntity.nms(facing);
        for (int slot : FunnelBlockEntity.facesFor(c, face)) {
            net.minecraft.world.item.ItemStack one;
            WorldlyContainer wc;
            net.minecraft.world.item.ItemStack s = c.getItem(slot);
            if (s.isEmpty() || c instanceof WorldlyContainer && !(wc = (WorldlyContainer)c).canTakeItemThroughFace(slot, s, face) || (one = c.removeItem(slot, 1)).isEmpty()) continue;
            c.setChanged();
            this.transit = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)one);
            this.progress = 0.0f;
            this.dirty = true;
            return;
        }
    }

    private boolean insertToContainer(net.momirealms.craftengine.core.util.Direction facing, ItemStack stack) {
        Container c = this.containerAt(facing);
        if (c == null) {
            return false;
        }
        Direction face = FunnelBlockEntity.nms(facing);
        net.minecraft.world.item.ItemStack ins = CraftItemStack.asNMSCopy((ItemStack)stack);
        for (int slot : FunnelBlockEntity.facesFor(c, face)) {
            WorldlyContainer wc;
            if (c instanceof WorldlyContainer && !(wc = (WorldlyContainer)c).canPlaceItemThroughFace(slot, ins, face) || !c.canPlaceItem(slot, ins)) continue;
            net.minecraft.world.item.ItemStack cur = c.getItem(slot);
            if (cur.isEmpty()) {
                c.setItem(slot, ins);
                c.setChanged();
                return true;
            }
            if (!net.minecraft.world.item.ItemStack.isSameItemSameComponents((net.minecraft.world.item.ItemStack)cur, (net.minecraft.world.item.ItemStack)ins)) continue;
            int max = Math.min(cur.getMaxStackSize(), c.getMaxStackSize());
            if (cur.getCount() >= max) continue;
            cur.grow(1);
            c.setChanged();
            return true;
        }
        return false;
    }

    private void dropFront(net.momirealms.craftengine.core.util.Direction facing) {
        try {
            if (this.transit == null) {
                return;
            }
            World bw = (World)this.blockEntity().world().world.platformWorld();
            net.momirealms.craftengine.core.world.BlockPos p = this.blockEntity().pos();
            double x = (double)p.x() + 0.5 + (double)facing.stepX() * 0.6;
            double y = (double)p.y() + 0.3;
            double z = (double)p.z() + 0.5 + (double)facing.stepZ() * 0.6;
            Item it = bw.dropItem(new Location(bw, x, y, z), this.transit);
            float inc = ConveyorMath.progressPerTick(64.0f, 64.0f, 16);
            it.setVelocity(new Vector((double)((float)facing.stepX() * inc), 0.05, (double)((float)facing.stepZ() * inc)));
            it.setPickupDelay(10);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void clearTransit(CEWorld world) {
        this.transit = null;
        this.progress = 0.0f;
        this.dirty = true;
        this.despawn(world);
    }

    private void renderTransit(CEWorld world, net.momirealms.craftengine.core.util.Direction facing, boolean out) {
        if (this.transit == null) {
            this.despawn(world);
            return;
        }
        net.momirealms.craftengine.core.world.BlockPos pos = this.blockEntity().pos();
        List<Player> viewers = ConveyorBlockEntity.viewersOf(world, pos);
        ContraptionBoundary contraption = ConveyorBlockEntity.contraptionOf(world);
        if (this.display == null) {
            this.display = new ConveyorItemDisplay();
        }
        this.display.setNmsItem(CraftItemStack.asNMSCopy((ItemStack)this.transit));
        net.momirealms.craftengine.core.util.Direction toContainer = facing.opposite();
        Vector3f containerEdge = new Vector3f(0.5f + (float)toContainer.stepX() * 0.5f, 0.28f, 0.5f + (float)toContainer.stepZ() * 0.5f);
        Vector3f beltEdge = new Vector3f(0.5f + (float)facing.stepX() * 0.5f, 0.28f, 0.5f + (float)facing.stepZ() * 0.5f);
        Vector3f rel = out ? ConveyorMath.interpolate(containerEdge, beltEdge, this.progress) : ConveyorMath.interpolate(beltEdge, containerEdge, this.progress);
        net.momirealms.craftengine.core.util.Direction move = out ? facing : toContainer;
        Quaternionf rot = ConveyorMath.itemRotation(move.stepX(), move.stepZ(), 0);
        rot.rotateY(this.carriedJitter);
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
        this.display.setRotation(rot);
        this.display.render(viewers, wx, wy, wz, !this.spawned);
        this.display.consumeRotationDirty();
        this.spawned = true;
    }

    private void despawn(CEWorld world) {
        if (this.display != null) {
            for (Player p : ConveyorBlockEntity.viewersOf(world, this.blockEntity().pos())) {
                this.display.despawn(p);
            }
            this.display.clearShown();
            this.spawned = false;
        }
    }

    private void maybeUpdateActivated(CEWorld world, boolean active) {
        if (this.lastActivated != null && this.lastActivated == active) {
            return;
        }
        ImmutableBlockState cur = this.blockEntity().blockState();
        if (cur == null || cur.getProperty(PROP_ACTIVATED) == null) {
            this.lastActivated = active;
            return;
        }
        try {
            ImmutableBlockState ns = FunnelBlockEntity.withActivated(cur, active);
            if (ns != null && ns != cur) {
                net.momirealms.craftengine.core.world.BlockPos pos = this.blockEntity().pos();
                Object level = world.world().minecraftWorld();
                Object bp = MNms.INSTANCE.constructor$BlockPos(pos.x(), pos.y(), pos.z());
                Object nms = ns.customBlockState().minecraftState();
                MNms.INSTANCE.method$LevelWriter$setBlock(level, bp, nms, 2);
            }
            this.lastActivated = active;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static ImmutableBlockState withActivated(ImmutableBlockState state, boolean active) {
        Property p = state.getProperty(PROP_ACTIVATED);
        if (p == null) {
            return state;
        }
        try {
            Comparable value = p.valueByName(String.valueOf(active));
            if (value == null) {
                return state;
            }
            return ImmutableBlockState.with((ImmutableBlockState)state, (Property)p, value);
        }
        catch (Throwable t) {
            return state;
        }
    }

    public void dropTransit() {
        try {
            if (this.transit != null) {
                World bw = (World)this.blockEntity().world().world.platformWorld();
                net.momirealms.craftengine.core.world.BlockPos p = this.blockEntity().pos();
                bw.dropItem(new Location(bw, (double)p.x() + 0.5, (double)p.y() + 0.5, (double)p.z() + 0.5), this.transit);
                this.transit = null;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            this.despawn(this.blockEntity().world());
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void load() {
        try {
            net.minecraft.world.item.ItemStack nms = this.getOptional(TypedKeys.NMS_ITEM).orElse(null);
            this.transit = nms != null && !nms.isEmpty() ? CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)nms) : null;
            this.progress = this.getOrDefault(KEY_PROG, Float.valueOf(0.0f)).floatValue();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void save() {
        try {
            if (this.transit == null) {
                this.clear();
            } else {
                this.set(TypedKeys.NMS_ITEM, CraftItemStack.asNMSCopy((ItemStack)this.transit));
                this.set(KEY_PROG, Float.valueOf(this.progress));
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

