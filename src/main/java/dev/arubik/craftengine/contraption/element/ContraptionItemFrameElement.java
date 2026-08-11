package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ContraptionItemFrameElement implements ContraptionElement {

    private final UUID sourceEntityId;
    private final Vec3 localPos;
    private final Direction facing;

    private ItemStack item = ItemStack.EMPTY;
    private int rotation = 0;

    private final int entityId = net.minecraft.world.entity.Entity.nextEntityId();
    private final UUID entityUuid = UUID.randomUUID();
    private final Object despawnPacket;
    private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
    private boolean metaDirty = true;

    public ContraptionItemFrameElement(UUID sourceEntityId, Vec3 localPos, Direction facing,
                                       ItemStack item, int rotation) {
        this.sourceEntityId = sourceEntityId;
        this.localPos = localPos;
        this.facing = facing;
        this.item = item.copy();
        this.rotation = rotation;
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
    }

    @Override public Key type() { return ElementTypes.ITEM_FRAME; }
    @Override public Vec3 localOffset() { return localPos; }
    @Override public boolean isValid() { return true; }
    @Override public int[] entityIds() { return new int[]{entityId}; }

    @Override
    public List<AABB> interactionBounds() {
        return List.of(new AABB(localPos.x - 0.4, localPos.y - 0.4, localPos.z - 0.4,
                                localPos.x + 0.4, localPos.y + 0.4, localPos.z + 0.4));
    }

    @Override
    public void tick(RenderContext ctx) {
        if (ctx.level() == null) return;
        for (net.minecraft.world.entity.Entity e : ctx.level().getAllEntities()) {
            if (e instanceof ItemFrame frame && frame.getUUID().equals(sourceEntityId)) {
                ItemStack live = frame.getItem();
                int liveRot = frame.getRotation();
                if (!live.equals(item) || liveRot != rotation) {
                    item = live.copy();
                    rotation = liveRot;
                    metaDirty = true;
                }
                return;
            }
        }
    }

    @Override
    public void render(RenderContext ctx) {
        Vec3 worldPos = ContraptionMath.renderPosition(localPos, ctx.bearing(),
                ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        float worldYaw = (float) ctx.yawDegrees();

        for (Player viewer : ctx.viewers()) {
            UUID vid = viewer.uuid();
            if (shownTo.add(vid)) {
                spawn(viewer, worldPos, worldYaw);
            } else if (ctx.moved()) {
                viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                        entityId, worldPos.x, worldPos.y, worldPos.z, worldYaw, 0f, false), false);
            }
        }
        if (metaDirty) {
            sendMeta(ctx.viewers());
            metaDirty = false;
        }
    }

    @Override
    public void despawn(List<Player> viewers) {
        for (Player p : viewers) {
            if (shownTo.remove(p.uuid())) p.sendPacket(despawnPacket, false);
        }
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
        Vec3 origin = new Vec3(bearingPos.getX(), bearingPos.getY(), bearingPos.getZ());
        Vec3 world = ContraptionMath.renderPosition(localPos, origin, 0, 0, 0, 1);
        ItemFrame frame = new ItemFrame(EntityType.ITEM_FRAME, level);
        frame.setPos(world.x, world.y, world.z);
        frame.setDirection(rotateDirection(facing, quarterTurns));
        frame.setItem(item, false);
        frame.setRotation(rotation);
        level.addFreshEntity(frame);
    }

    @Override
    public boolean onInteract(ServerPlayer player, ContraptionState state, Vec3 hitPos,
                               InteractionHand hand, boolean rightClick) {
        if (!rightClick) {
            if (!item.isEmpty()) {
                if (player.level() instanceof ServerLevel sl) {
                    Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
                    Vec3 world = ContraptionMath.renderPosition(localPos, bearing,
                            state.yawRadians(), state.pitchRadians(), state.rollRadians(), state.scale());
                    sl.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(
                            sl, world.x, world.y, world.z, item.copy()));
                }
                item = ItemStack.EMPTY;
                metaDirty = true;
            }
            return true;
        }
        ItemStack held = player.getItemInHand(hand);
        if (item.isEmpty() && !held.isEmpty()) {
            item = held.copyWithCount(1);
            if (!player.isCreative()) held.shrink(1);
            rotation = 0;
        } else if (!item.isEmpty()) {
            rotation = (rotation + 1) & 7;
        }
        metaDirty = true;
        return true;
    }

    private void spawn(Player viewer, Vec3 pos, float yaw) {
        // HangingEntity facing encoded as Direction ordinal in spawn data field
        viewer.sendPackets(List.of(
                MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                        entityId, entityUuid, pos.x, pos.y, pos.z, 0f, yaw,
                        EntityType.ITEM_FRAME, facing.ordinal(), Vec3.ZERO, 0),
                MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, buildMeta())
        ), false);
    }

    private void sendMeta(List<Player> viewers) {
        Object pkt = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, buildMeta());
        for (Player p : viewers) {
            if (shownTo.contains(p.uuid())) p.sendPacket(pkt, false);
        }
    }

    private List<Object> buildMeta() {
        List<Object> meta = new ArrayList<>();
        meta.add(SynchedEntityData.DataValue.create(ItemFrame.DATA_ITEM, item.copy()));
        meta.add(SynchedEntityData.DataValue.create(ItemFrame.DATA_ROTATION, rotation));
        return meta;
    }

    private static Direction rotateDirection(Direction dir, int quarterTurns) {
        for (int i = 0; i < (quarterTurns & 3); i++) {
            dir = switch (dir) {
                case NORTH -> Direction.WEST;
                case WEST  -> Direction.SOUTH;
                case SOUTH -> Direction.EAST;
                case EAST  -> Direction.NORTH;
                default    -> dir;
            };
        }
        return dir;
    }
}
