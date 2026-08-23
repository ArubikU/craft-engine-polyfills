/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.EmptyBlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData$BlockDisplayData
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Key
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.contraption.ContraptionInteractionListener;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ContraptionBlockElement
implements ContraptionElement {
    private final BlockPos localPos;
    private BlockState blockState;
    private CompoundTag blockEntityNbt;
    protected final int entityId;
    protected final UUID entityUuid;
    protected final Object despawnPacket;
    protected final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
    protected boolean metaDirty = true;
    protected int lastBlockLight = -1;
    protected int lastSkyLight = -1;
    protected double lastPitch;
    protected double lastRoll;
    protected double lastScale = 1.0;
    protected final boolean hasEntityRenderer;
    protected final float modelYawOffsetDegrees;
    // How many ticks the client should interpolate the NEXT position/transform update over —
    // adapts to the real gap between actual moves (see ContraptionEntity#lastMoveGapTicks) instead
    // of a fixed guess, so a script-driven bearing that only mutates state every action_interval
    // ticks still looks like continuous motion instead of snapping then freezing.
    protected int interpTicks = 2;
    private int lastSentInterpTicks = -1;

    public ContraptionBlockElement(BlockPos localPos, BlockState blockState, CompoundTag blockEntityNbt) {
        this(localPos, blockState, blockEntityNbt, false, 0.0f);
    }

    public ContraptionBlockElement(BlockPos localPos, BlockState blockState, CompoundTag blockEntityNbt, boolean hasEntityRenderer, float modelYawOffsetDegrees) {
        this.localPos = localPos;
        this.blockState = blockState;
        this.blockEntityNbt = blockEntityNbt;
        this.hasEntityRenderer = hasEntityRenderer;
        this.modelYawOffsetDegrees = modelYawOffsetDegrees;
        this.entityId = Entity.nextEntityId();
        this.entityUuid = UUID.randomUUID();
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
    }

    @Override
    public Key type() {
        return ElementTypes.BLOCK;
    }

    @Override
    public Vec3 localOffset() {
        return new Vec3((double)this.localPos.getX() + 0.5, (double)this.localPos.getY() + 0.5, (double)this.localPos.getZ() + 0.5);
    }

    public BlockPos localPos() {
        return this.localPos;
    }

    public void markDirty() {
        this.metaDirty = true;
    }

    public BlockState blockState() {
        return this.blockState;
    }

    @Override
    public boolean isValid() {
        return this.blockState != null && !this.blockState.isAir();
    }

    @Override
    public List<AABB> interactionBounds() {
        if (this.blockState == null || this.blockState.isAir()) {
            return List.of();
        }
        try {
            VoxelShape shape = this.blockState.getInteractionShape((BlockGetter)EmptyBlockGetter.INSTANCE, this.localPos);
            if (shape.isEmpty()) {
                shape = this.blockState.getShape((BlockGetter)EmptyBlockGetter.INSTANCE, this.localPos);
            }
            if (shape.isEmpty()) {
                return List.of();
            }
            ArrayList<AABB> boxes = new ArrayList<AABB>();
            for (AABB box : shape.toAabbs()) {
                boxes.add(box.move((double)this.localPos.getX(), (double)this.localPos.getY(), (double)this.localPos.getZ()));
            }
            return boxes;
        }
        catch (Throwable ignored) {
            return List.of(new AABB((double)this.localPos.getX(), (double)this.localPos.getY(), (double)this.localPos.getZ(), (double)this.localPos.getX() + 1.0, (double)this.localPos.getY() + 1.0, (double)this.localPos.getZ() + 1.0));
        }
    }

    @Override
    public boolean onInteract(ServerPlayer player, ContraptionState state, Vec3 hitPos, InteractionHand hand, boolean rightClick) {
        Direction face = this.nearestFace(hitPos);
        ContraptionInteractionListener.Hit hit = new ContraptionInteractionListener.Hit(state, this.localPos, hitPos, face);
        if (rightClick) {
            ContraptionInteractionListener.forward(player, hit);
        } else {
            ContraptionInteractionListener.forwardAttack(player, hit);
        }
        return true;
    }

    private Direction nearestFace(Vec3 localHit) {
        double bx = (double)this.localPos.getX() + 0.5;
        double by = (double)this.localPos.getY() + 0.5;
        double bz = (double)this.localPos.getZ() + 0.5;
        double dx = localHit.x - bx;
        double dy = localHit.y - by;
        double dz = localHit.z - bz;
        double ax = Math.abs(dx);
        double ay = Math.abs(dy);
        double az = Math.abs(dz);
        if (ax >= ay && ax >= az) {
            return dx > 0.0 ? Direction.EAST : Direction.WEST;
        }
        if (ay >= az) {
            return dy > 0.0 ? Direction.UP : Direction.DOWN;
        }
        return dz > 0.0 ? Direction.SOUTH : Direction.NORTH;
    }

    @Override
    public int[] entityIds() {
        return new int[]{this.entityId};
    }

    @Override
    public void tick(RenderContext ctx) {
        if (ctx.level() == null) {
            return;
        }
        BlockState live = ctx.level().getBlockState(this.localPos);
        if (!live.equals(this.blockState)) {
            this.blockState = live;
            this.metaDirty = true;
        }
    }

    @Override
    public void render(RenderContext ctx) {
        this.interpTicks = ctx.interpTicks();
        if (this.interpTicks != this.lastSentInterpTicks) {
            // The interpolation window itself changed (the real gap between moves settled on a
            // new value) — the client only re-reads PosRot/TransformationInterpolationDuration
            // when metadata is actually resent, so force that even if nothing else about this
            // block changed this tick (a pure-yaw spin never touches pitch/roll/scale/light).
            this.lastSentInterpTicks = this.interpTicks;
            this.metaDirty = true;
        }
        Vec3 center = this.localOffset();
        Vec3 pos = ContraptionMath.renderPosition(center, ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        float yawDeg = (float)ctx.yawDegrees() + this.modelYawOffsetDegrees;
        if (ctx.pitchRadians() != this.lastPitch || ctx.rollRadians() != this.lastRoll || ctx.scale() != this.lastScale) {
            this.lastPitch = ctx.pitchRadians();
            this.lastRoll = ctx.rollRadians();
            this.lastScale = ctx.scale();
            this.metaDirty = true;
        }
        int blockLight = 15;
        int skyLight = 15;
        if (ctx.realLevel() != null) {
            try {
                BlockPos worldAt = BlockPos.containing((double)pos.x, (double)pos.y, (double)pos.z);
                blockLight = ctx.realLevel().getLightEngine().getLayerListener(LightLayer.BLOCK).getLightValue(worldAt);
                skyLight = ctx.realLevel().getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(worldAt);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (ctx.lightMap() != null) {
            blockLight = ctx.lightMap().combinedBlockLight(this.localPos, blockLight);
        }
        if (blockLight != this.lastBlockLight || skyLight != this.lastSkyLight) {
            this.lastBlockLight = blockLight;
            this.lastSkyLight = skyLight;
            this.metaDirty = true;
        }
        for (Player viewer : ctx.viewers()) {
            UUID viewerId = viewer.uuid();
            if (!this.shownTo.contains(viewerId)) {
                this.spawn(viewer, pos, yawDeg);
                this.shownTo.add(viewerId);
                continue;
            }
            if (!ctx.moved() && !this.metaDirty) continue;
            this.sendPositionSync(viewer, pos, yawDeg);
        }
        if (this.metaDirty) {
            this.sendMetadata(ctx.viewers());
            this.metaDirty = false;
        }
    }

    @Override
    public void despawn(List<Player> viewers) {
        for (Player viewer : viewers) {
            if (!this.shownTo.remove(viewer.uuid())) continue;
            viewer.sendPacket(this.despawnPacket, false);
        }
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
        this.disassemble(level, bearingPos, quarterTurns, null);
    }

    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns, ContraptionLevel contraptionLevel) {
        BlockEntity be;
        BlockPos worldPos = ContraptionMath.toWorld(ContraptionBlockElement.rotateLocal(this.localPos, quarterTurns), bearingPos);
        BlockState rotated = this.blockState.rotate(ContraptionBlockElement.rotationFromQuarterTurns(quarterTurns));
        level.setBlock(worldPos, rotated, 3);
        if (this.blockEntityNbt != null && (be = BlockEntity.loadStatic((BlockPos)worldPos, (BlockState)rotated, (CompoundTag)this.blockEntityNbt, (HolderLookup.Provider)level.registryAccess())) != null) {
            be.setLevel((Level)level);
            level.setBlockEntity(be);
        }
        if (contraptionLevel != null) {
            PersistentBlockEntity ce;
            byte[] ceBytes;
            PersistentBlockEntity live = PersistentBlockEntity.getIfLoaded((Level)contraptionLevel.serverLevel(), this.localPos);
            if (live != null) {
                try {
                    ceBytes = live.serializeToBytes();
                }
                catch (Throwable t) {
                    ceBytes = contraptionLevel.getCeControllerData(this.localPos);
                }
            } else {
                ceBytes = contraptionLevel.getCeControllerData(this.localPos);
            }
            if (ceBytes != null && (ce = PersistentBlockEntity.getIfLoaded((Level)level, worldPos)) != null) {
                try {
                    ce.loadFromBytes(ceBytes);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        }
    }

    protected void spawn(Player viewer, Vec3 pos, float yawDeg) {
        Object spawnPacket = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.entityUuid, pos.x, pos.y, pos.z, 0.0f, yawDeg, EntityType.BLOCK_DISPLAY, 0, Vec3.ZERO, yawDeg);
        viewer.sendPackets(List.of(spawnPacket), false);
        this.sendMetadata(List.of(viewer));
    }

    protected void sendPositionSync(Player viewer, Vec3 pos, float yawDeg) {
        Object syncPacket = MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, pos.x, pos.y, pos.z, yawDeg, 0.0f, false);
        viewer.sendPacket(syncPacket, false);
    }

    protected void sendMetadata(List<Player> viewers) {
        List<Object> values = this.buildMetadataValues();
        if (values.isEmpty()) {
            return;
        }
        Object metaPacket = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, values);
        for (Player viewer : viewers) {
            if (!this.shownTo.contains(viewer.uuid())) continue;
            viewer.sendPacket(metaPacket, false);
        }
    }

    protected List<Object> buildMetadataValues() {
        boolean scaled;
        ArrayList<Object> values = new ArrayList<Object>();
        DisplayData.BlockDisplayData.BlockState.addEntityData((this.hasEntityRenderer ? Blocks.AIR.defaultBlockState() : this.blockState), values);
        float s = (float)this.lastScale;
        boolean bl = scaled = this.lastScale != 1.0;
        if (this.lastPitch == 0.0 && this.lastRoll == 0.0) {
            DisplayData.Translation.addEntityData(new Vector3f(-0.5f * s, -0.5f * s, -0.5f * s), values);
            if (scaled) {
                DisplayData.Scale.addEntityData(new Vector3f(s, s, s), values);
            }
        } else {
            Quaternionf tiltQ = new Quaternionf().rotateX((float)this.lastPitch).rotateZ((float)this.lastRoll);
            Vector3f t = tiltQ.transform(new Vector3f(-0.5f * s, -0.5f * s, -0.5f * s));
            DisplayData.Translation.addEntityData(t, values);
            DisplayData.LeftRotation.addEntityData(tiltQ, values);
            if (scaled) {
                DisplayData.Scale.addEntityData(new Vector3f(s, s, s), values);
            }
        }
        DisplayData.PosRotInterpolationDuration.addEntityData(this.interpTicks, values);
        DisplayData.TransformationInterpolationDuration.addEntityData(this.interpTicks, values);
        if (this.lastBlockLight >= 0 && this.lastSkyLight >= 0) {
            DisplayData.BrightnessOverride.addEntityData((this.lastBlockLight << 4 | this.lastSkyLight << 20), values);
        }
        return values;
    }

    private static BlockPos rotateLocal(BlockPos local, int quarterTurns) {
        return switch (quarterTurns & 3) {
            case 0 -> local;
            case 1 -> new BlockPos(-local.getZ(), local.getY(), local.getX());
            case 2 -> new BlockPos(-local.getX(), local.getY(), -local.getZ());
            case 3 -> new BlockPos(local.getZ(), local.getY(), -local.getX());
            default -> local;
        };
    }

    private static Rotation rotationFromQuarterTurns(int quarterTurns) {
        return switch (quarterTurns & 3) {
            case 1 -> Rotation.CLOCKWISE_90;
            case 2 -> Rotation.CLOCKWISE_180;
            case 3 -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }
}

