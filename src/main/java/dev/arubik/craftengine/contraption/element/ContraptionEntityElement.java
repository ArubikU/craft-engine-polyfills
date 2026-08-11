package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Entity element: a captured mob/item entity stored in the contraption's hidden level.
 * Mirrors it as a packet entity in the real world, repositioned every tick.
 */
public final class ContraptionEntityElement implements ContraptionElement {

    private final Vec3 localOffset;
    private final CompoundTag entityNbt;
    private final EntityType<?> entityType;

    // Packet mirror
    private final int mirrorEntityId;
    private final UUID mirrorUuid;
    private final Object despawnPacket;
    private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();


    public ContraptionEntityElement(Vec3 localOffset, CompoundTag entityNbt, EntityType<?> entityType) {
        this.localOffset = localOffset;
        this.entityNbt = entityNbt;
        this.entityType = entityType;
        this.mirrorEntityId = Entity.nextEntityId();
        this.mirrorUuid = UUID.randomUUID();
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(
                IntList.of(mirrorEntityId));
    }

    @Override
    public Key type() {
        return ElementTypes.ENTITY;
    }

    @Override
    public Vec3 localOffset() {
        return localOffset;
    }

    @Override
    public boolean isValid() {
        return entityNbt != null;
    }

    @Override
    public int[] entityIds() {
        return new int[]{mirrorEntityId};
    }

    @Override
    public void render(RenderContext ctx) {
        Vec3 worldPos = ContraptionMath.renderPosition(localOffset, ctx.bearing(),
                ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        float yawDeg = (float) ctx.yawDegrees();

        for (Player viewer : ctx.viewers()) {
            UUID viewerId = viewer.uuid();
            if (!shownTo.contains(viewerId)) {
                spawnMirror(viewer, worldPos, yawDeg);
                shownTo.add(viewerId);
            } else if (ctx.moved()) {
                sendPositionSync(viewer, worldPos, yawDeg);
            }
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
        Vec3 rotated = rotateVec(localOffset, quarterTurns);
        Vec3 worldPos = new Vec3(
                bearingPos.getX() + rotated.x,
                bearingPos.getY() + rotated.y,
                bearingPos.getZ() + rotated.z);

        Entity restored = entityType.create(level, net.minecraft.world.entity.EntitySpawnReason.LOAD);
        if (restored != null) {
            restored.load(net.minecraft.world.level.storage.TagValueInput.create(
                    net.minecraft.util.ProblemReporter.DISCARDING, level.registryAccess(), entityNbt));
            restored.setPos(worldPos.x, worldPos.y, worldPos.z);
            level.addFreshEntity(restored);
        }
    }

    // ---- private ----

    private void spawnMirror(Player viewer, Vec3 pos, float yawDeg) {
        Object spawnPacket = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                mirrorEntityId, mirrorUuid,
                pos.x, pos.y, pos.z,
                0f, yawDeg,
                entityType, 0, Vec3.ZERO, yawDeg);
        viewer.sendPacket(spawnPacket, false);
    }

    private void sendPositionSync(Player viewer, Vec3 pos, float yawDeg) {
        Object syncPacket = MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                mirrorEntityId, pos.x, pos.y, pos.z, yawDeg, 0f, false);
        viewer.sendPacket(syncPacket, false);
    }

    private static Vec3 rotateVec(Vec3 v, int quarterTurns) {
        return switch (quarterTurns & 3) {
            case 0 -> v;
            case 1 -> new Vec3(-v.z, v.y, v.x);
            case 2 -> new Vec3(-v.x, v.y, -v.z);
            case 3 -> new Vec3(v.z, v.y, -v.x);
            default -> v;
        };
    }
}
