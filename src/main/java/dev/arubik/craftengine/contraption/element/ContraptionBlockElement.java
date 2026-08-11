package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.ContraptionInteractionListener;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ContraptionBlockElement implements ContraptionElement {

    private final BlockPos localPos;
    private BlockState blockState;
    private CompoundTag blockEntityNbt;

    // Packet render state
    private final int entityId;
    private final UUID entityUuid;
    private final Object despawnPacket;
    private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
    private boolean metaDirty = true;
    private int lastBlockLight = -1;
    private int lastSkyLight = -1;
    private double lastPitch, lastRoll, lastScale = 1.0;

    public ContraptionBlockElement(BlockPos localPos, BlockState blockState, CompoundTag blockEntityNbt) {
        this.localPos = localPos;
        this.blockState = blockState;
        this.blockEntityNbt = blockEntityNbt;
        this.entityId = net.minecraft.world.entity.Entity.nextEntityId();
        this.entityUuid = UUID.randomUUID();
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
    }

    @Override
    public Key type() {
        return ElementTypes.BLOCK;
    }

    @Override
    public Vec3 localOffset() {
        return new Vec3(localPos.getX() + 0.5, localPos.getY() + 0.5, localPos.getZ() + 0.5);
    }

    public BlockPos localPos() {
        return localPos;
    }

    public void markDirty() {
        metaDirty = true;
    }

    public BlockState blockState() {
        return blockState;
    }

    @Override
    public boolean isValid() {
        return blockState != null && !blockState.isAir();
    }

    @Override
    public List<AABB> interactionBounds() {
        if (blockState == null || blockState.isAir()) return List.of();
        try {
            VoxelShape shape = blockState.getInteractionShape(
                    net.minecraft.world.level.EmptyBlockGetter.INSTANCE, localPos);
            if (shape.isEmpty()) shape = blockState.getShape(
                    net.minecraft.world.level.EmptyBlockGetter.INSTANCE, localPos);
            if (shape.isEmpty()) return List.of();
            List<AABB> boxes = new ArrayList<>();
            for (AABB box : shape.toAabbs()) {
                boxes.add(box.move(localPos.getX(), localPos.getY(), localPos.getZ()));
            }
            return boxes;
        } catch (Throwable ignored) {
            return List.of(new AABB(localPos.getX(), localPos.getY(), localPos.getZ(),
                    localPos.getX() + 1.0, localPos.getY() + 1.0, localPos.getZ() + 1.0));
        }
    }

    @Override
    public boolean onInteract(ServerPlayer player, ContraptionState state, Vec3 hitPos, InteractionHand hand, boolean rightClick) {
        Direction face = nearestFace(hitPos);
        ContraptionInteractionListener.Hit hit = new ContraptionInteractionListener.Hit(state, localPos, hitPos, face);
        if (rightClick) {
            ContraptionInteractionListener.forward(player, hit);
        } else {
            ContraptionInteractionListener.forwardAttack(player, hit);
        }
        return true;
    }

    private Direction nearestFace(Vec3 localHit) {
        double bx = localPos.getX() + 0.5, by = localPos.getY() + 0.5, bz = localPos.getZ() + 0.5;
        double dx = localHit.x - bx, dy = localHit.y - by, dz = localHit.z - bz;
        double ax = Math.abs(dx), ay = Math.abs(dy), az = Math.abs(dz);
        if (ax >= ay && ax >= az) return dx > 0 ? Direction.EAST : Direction.WEST;
        if (ay >= az) return dy > 0 ? Direction.UP : Direction.DOWN;
        return dz > 0 ? Direction.SOUTH : Direction.NORTH;
    }

    @Override
    public int[] entityIds() {
        return new int[]{entityId};
    }

    @Override
    public void tick(RenderContext ctx) {
        if (ctx.level() == null) return;
        BlockState live = ctx.level().getBlockState(localPos);
        if (!live.equals(blockState)) {
            blockState = live;
            metaDirty = true;
        }
    }

    @Override
    public void render(RenderContext ctx) {
        Vec3 center = localOffset();
        Vec3 pos = ContraptionMath.renderPosition(center, ctx.bearing(),
                ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());

        float yawDeg = (float) ctx.yawDegrees();

        if (ctx.pitchRadians() != lastPitch || ctx.rollRadians() != lastRoll || ctx.scale() != lastScale) {
            lastPitch = ctx.pitchRadians();
            lastRoll = ctx.rollRadians();
            lastScale = ctx.scale();
            metaDirty = true;
        }

        int blockLight = 15;
        int skyLight = 15;
        if (ctx.realLevel() != null) {
            try {
                net.minecraft.core.BlockPos worldAt = net.minecraft.core.BlockPos.containing(pos.x, pos.y, pos.z);
                blockLight = ctx.realLevel().getLightEngine()
                        .getLayerListener(net.minecraft.world.level.LightLayer.BLOCK).getLightValue(worldAt);
                skyLight = ctx.realLevel().getLightEngine()
                        .getLayerListener(net.minecraft.world.level.LightLayer.SKY).getLightValue(worldAt);
            } catch (Throwable ignored) {}
        }
        if (ctx.lightMap() != null) {
            blockLight = ctx.lightMap().combinedBlockLight(localPos, blockLight);
        }
        if (blockLight != lastBlockLight || skyLight != lastSkyLight) {
            lastBlockLight = blockLight;
            lastSkyLight = skyLight;
            metaDirty = true;
        }

        for (Player viewer : ctx.viewers()) {
            UUID viewerId = viewer.uuid();
            if (!shownTo.contains(viewerId)) {
                spawn(viewer, pos, yawDeg);
                shownTo.add(viewerId);
            } else if (ctx.moved() || metaDirty) {
                sendPositionSync(viewer, pos, yawDeg);
            }
        }

        if (metaDirty) {
            sendMetadata(ctx.viewers());
            metaDirty = false;
        }
    }

    @Override
    public void despawn(List<Player> viewers) {
        for (Player viewer : viewers) {
            if (shownTo.remove(viewer.uuid())) {
                viewer.sendPacket(despawnPacket, false);
            }
        }
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
        disassemble(level, bearingPos, quarterTurns, null);
    }

    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns,
                            ContraptionLevel contraptionLevel) {
        BlockPos worldPos = ContraptionMath.toWorld(
                rotateLocal(localPos, quarterTurns), bearingPos);
        BlockState rotated = blockState.rotate(rotationFromQuarterTurns(quarterTurns));
        level.setBlock(worldPos, rotated, 3);

        if (blockEntityNbt != null) {
            net.minecraft.world.level.block.entity.BlockEntity be =
                    net.minecraft.world.level.block.entity.BlockEntity.loadStatic(
                            worldPos, rotated, blockEntityNbt, level.registryAccess());
            if (be != null) {
                be.setLevel(level);
                level.setBlockEntity(be);
            }
        }

        if (contraptionLevel != null) {
            dev.arubik.craftengine.block.entity.PersistentBlockEntity live =
                    dev.arubik.craftengine.block.entity.PersistentBlockEntity.getIfLoaded(
                            contraptionLevel.serverLevel(), localPos);
            byte[] ceBytes;
            if (live != null) {
                try {
                    ceBytes = live.serializeToBytes();
                } catch (Throwable t) {
                    ceBytes = contraptionLevel.getCeControllerData(localPos);
                }
            } else {
                ceBytes = contraptionLevel.getCeControllerData(localPos);
            }
            if (ceBytes != null) {
                dev.arubik.craftengine.block.entity.PersistentBlockEntity ce =
                        dev.arubik.craftengine.block.entity.PersistentBlockEntity.getIfLoaded(level, worldPos);
                if (ce != null) {
                    try {
                        ce.loadFromBytes(ceBytes);
                    } catch (Throwable ignored) {}
                }
            }
        }
    }

    private void spawn(Player viewer, Vec3 pos, float yawDeg) {
        Object spawnPacket = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                entityId, entityUuid,
                pos.x, pos.y, pos.z,
                0f, yawDeg,
                net.minecraft.world.entity.EntityType.BLOCK_DISPLAY, 0, Vec3.ZERO, yawDeg);
        viewer.sendPackets(List.of(spawnPacket), false);
        sendMetadata(List.of(viewer));
    }

    private void sendPositionSync(Player viewer, Vec3 pos, float yawDeg) {
        Object syncPacket = MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                entityId, pos.x, pos.y, pos.z, yawDeg, 0f, false);
        viewer.sendPacket(syncPacket, false);
    }

    private void sendMetadata(List<Player> viewers) {
        List<Object> values = buildMetadataValues();
        if (values.isEmpty()) return;
        Object metaPacket = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, values);
        for (Player viewer : viewers) {
            if (shownTo.contains(viewer.uuid())) {
                viewer.sendPacket(metaPacket, false);
            }
        }
    }

    private List<Object> buildMetadataValues() {
        var values = new java.util.ArrayList<Object>();
        DisplayData.BlockDisplayData.BlockState.addEntityData(blockState, values);
        float s = (float) lastScale;
        boolean scaled = lastScale != 1.0;
        if (lastPitch == 0.0 && lastRoll == 0.0) {
            DisplayData.Translation.addEntityData(
                    new org.joml.Vector3f(-0.5f * s, -0.5f * s, -0.5f * s), values);
            if (scaled) {
                DisplayData.Scale.addEntityData(new org.joml.Vector3f(s, s, s), values);
            }
        } else {
            org.joml.Quaternionf tiltQ = new org.joml.Quaternionf()
                    .rotateX((float) lastPitch).rotateZ((float) lastRoll);
            org.joml.Vector3f t = tiltQ.transform(
                    new org.joml.Vector3f(-0.5f * s, -0.5f * s, -0.5f * s));
            DisplayData.Translation.addEntityData(t, values);
            DisplayData.LeftRotation.addEntityData(tiltQ, values);
            if (scaled) {
                DisplayData.Scale.addEntityData(new org.joml.Vector3f(s, s, s), values);
            }
        }
        DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
        if (lastBlockLight >= 0 && lastSkyLight >= 0) {
            DisplayData.BrightnessOverride.addEntityData((lastBlockLight << 4) | (lastSkyLight << 20), values);
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
