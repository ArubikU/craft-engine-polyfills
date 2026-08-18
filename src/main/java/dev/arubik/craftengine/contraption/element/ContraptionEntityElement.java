/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.ProblemReporter
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntitySpawnReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.storage.TagValueInput
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

public final class ContraptionEntityElement
implements ContraptionElement {
    private final Vec3 localOffset;
    private final CompoundTag entityNbt;
    private final EntityType<?> entityType;
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
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.mirrorEntityId));
    }

    @Override
    public Key type() {
        return ElementTypes.ENTITY;
    }

    @Override
    public Vec3 localOffset() {
        return this.localOffset;
    }

    @Override
    public boolean isValid() {
        return this.entityNbt != null;
    }

    @Override
    public int[] entityIds() {
        return new int[]{this.mirrorEntityId};
    }

    @Override
    public void render(RenderContext ctx) {
        Vec3 worldPos = ContraptionMath.renderPosition(this.localOffset, ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        float yawDeg = (float)ctx.yawDegrees();
        for (Player viewer : ctx.viewers()) {
            UUID viewerId = viewer.uuid();
            if (!this.shownTo.contains(viewerId)) {
                this.spawnMirror(viewer, worldPos, yawDeg);
                this.shownTo.add(viewerId);
                continue;
            }
            if (!ctx.moved()) continue;
            this.sendPositionSync(viewer, worldPos, yawDeg);
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
        Vec3 rotated = ContraptionEntityElement.rotateVec(this.localOffset, quarterTurns);
        Vec3 worldPos = new Vec3((double)bearingPos.getX() + rotated.x, (double)bearingPos.getY() + rotated.y, (double)bearingPos.getZ() + rotated.z);
        Entity restored = this.entityType.create((Level)level, EntitySpawnReason.LOAD);
        if (restored != null) {
            restored.load(TagValueInput.create((ProblemReporter)ProblemReporter.DISCARDING, (HolderLookup.Provider)level.registryAccess(), (CompoundTag)this.entityNbt));
            restored.setPos(worldPos.x, worldPos.y, worldPos.z);
            level.addFreshEntity(restored);
        }
    }

    private void spawnMirror(Player viewer, Vec3 pos, float yawDeg) {
        Object spawnPacket = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.mirrorEntityId, this.mirrorUuid, pos.x, pos.y, pos.z, 0.0f, yawDeg, this.entityType, 0, Vec3.ZERO, yawDeg);
        viewer.sendPacket(spawnPacket, false);
    }

    private void sendPositionSync(Player viewer, Vec3 pos, float yawDeg) {
        Object syncPacket = MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.mirrorEntityId, pos.x, pos.y, pos.z, yawDeg, 0.0f, false);
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

