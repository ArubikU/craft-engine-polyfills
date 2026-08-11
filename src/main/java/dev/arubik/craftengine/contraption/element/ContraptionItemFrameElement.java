package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import org.joml.Quaternionf;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ContraptionItemFrameElement implements ContraptionElement {

    private final UUID sourceEntityId;
    private final Vec3 localPos;
    private final Direction facing;

    private ItemStack item = ItemStack.EMPTY;
    private int rotation = 0;

    private final int frameEntityId = net.minecraft.world.entity.Entity.nextEntityId();
    private final UUID frameUuid = UUID.randomUUID();
    private final Object frameRemovePacket;
    private final Set<UUID> frameShownTo = ConcurrentHashMap.newKeySet();

    private final int itemEntityId = net.minecraft.world.entity.Entity.nextEntityId();
    private final UUID itemUuid = UUID.randomUUID();
    private final Object itemRemovePacket;
    private final Set<UUID> itemShownTo = ConcurrentHashMap.newKeySet();

    private boolean metaDirty = true;

    public ContraptionItemFrameElement(UUID sourceEntityId, Vec3 localPos, Direction facing,
                                       ItemStack item, int rotation) {
        this.sourceEntityId = sourceEntityId;
        this.localPos = localPos;
        this.facing = facing;
        this.item = item.copy();
        this.rotation = rotation;
        this.frameRemovePacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(frameEntityId));
        this.itemRemovePacket  = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(itemEntityId));
    }

    @Override public Key type() { return ElementTypes.ITEM_FRAME; }
    @Override public Vec3 localOffset() { return localPos; }
    @Override public boolean isValid() { return true; }
    @Override public int[] entityIds() { return new int[]{frameEntityId, itemEntityId}; }

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
            if (frameShownTo.add(vid)) spawnFrame(viewer, worldPos, worldYaw);
            else if (ctx.moved()) syncPos(viewer, frameEntityId, worldPos, worldYaw);
            if (itemShownTo.add(vid)) spawnItem(viewer, worldPos, worldYaw);
            else if (ctx.moved()) syncPos(viewer, itemEntityId, worldPos, worldYaw);
        }
        if (metaDirty) {
            sendItemMeta(ctx.viewers());
            metaDirty = false;
        }
    }

    @Override
    public void despawn(List<Player> viewers) {
        for (Player p : viewers) {
            if (frameShownTo.remove(p.uuid())) p.sendPacket(frameRemovePacket, false);
            if (itemShownTo.remove(p.uuid()))  p.sendPacket(itemRemovePacket, false);
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
            // left-click: eject item
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
        // right-click: place if empty, rotate if occupied
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

    private void spawnFrame(Player viewer, Vec3 pos, float yaw) {
        List<Object> meta = new ArrayList<>();
        net.minecraft.world.level.block.state.BlockState frameBlock =
                (net.minecraft.world.level.block.state.BlockState)
                net.minecraft.core.registries.BuiltInRegistries.BLOCK
                        .getValue(net.minecraft.resources.Identifier.withDefaultNamespace("oak_planks"))
                        .defaultBlockState();
        DisplayData.BlockDisplayData.BlockState.addEntityData(frameBlock, meta);
        DisplayData.LeftRotation.addEntityData(faceRotation(facing), meta);
        // thin panel: 1x1 face, 1/16 thick
        DisplayData.Scale.addEntityData(new org.joml.Vector3f(1.0f, 1.0f, 0.0625f), meta);
        DisplayData.Translation.addEntityData(new org.joml.Vector3f(-0.5f, -0.5f, -0.03125f), meta);
        DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), meta);
        viewer.sendPackets(List.of(
                MNms.INSTANCE.constructor$ClientboundAddEntityPacket(frameEntityId, frameUuid,
                        pos.x, pos.y, pos.z, 0f, yaw, EntityType.BLOCK_DISPLAY, 0, Vec3.ZERO, 0),
                MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(frameEntityId, meta)
        ), false);
    }

    private void spawnItem(Player viewer, Vec3 pos, float yaw) {
        viewer.sendPackets(List.of(
                MNms.INSTANCE.constructor$ClientboundAddEntityPacket(itemEntityId, itemUuid,
                        pos.x, pos.y, pos.z, 0f, yaw, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0),
                MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(itemEntityId, buildItemMeta())
        ), false);
    }

    private void syncPos(Player viewer, int eid, Vec3 pos, float yaw) {
        viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                eid, pos.x, pos.y, pos.z, yaw, 0f, false), false);
    }

    private void sendItemMeta(List<Player> viewers) {
        List<Object> meta = buildItemMeta();
        Object pkt = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(itemEntityId, meta);
        for (Player p : viewers) {
            if (itemShownTo.contains(p.uuid())) p.sendPacket(pkt, false);
        }
    }

    private List<Object> buildItemMeta() {
        List<Object> meta = new ArrayList<>();
        if (!item.isEmpty()) {
            Object nms = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(
                    org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(item));
            DisplayData.ItemDisplayData.ItemStack.addEntityData(nms, meta);
        }
        Quaternionf itemRot = faceRotation(facing).rotateZ((float) Math.toRadians(rotation * 45f));
        DisplayData.LeftRotation.addEntityData(itemRot, meta);
        DisplayData.Scale.addEntityData(new org.joml.Vector3f(0.5f, 0.5f, 0.5f), meta);
        DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), meta);
        return meta;
    }

    private static Quaternionf faceRotation(Direction dir) {
        return switch (dir) {
            case NORTH -> new Quaternionf().rotateY((float) Math.toRadians(180));
            case SOUTH -> new Quaternionf();
            case WEST  -> new Quaternionf().rotateY((float) Math.toRadians(90));
            case EAST  -> new Quaternionf().rotateY((float) Math.toRadians(270));
            case UP    -> new Quaternionf().rotateX((float) Math.toRadians(-90));
            case DOWN  -> new Quaternionf().rotateX((float) Math.toRadians(90));
        };
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
