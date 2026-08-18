/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.entity.data.InteractionData
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.InteractionData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

public final class ContraptionSecondaryHitbox
implements ContraptionElement {
    private final Vec3 localOffset;
    private final float width;
    private final float height;
    private final boolean interactive;
    private final int entityId;
    private final UUID entityUuid;
    private final Object despawnPacket;
    private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
    private double lastScale = 1.0;

    public ContraptionSecondaryHitbox(Vec3 localOffset, float width, float height, boolean interactive) {
        this.localOffset = localOffset;
        this.width = width;
        this.height = height;
        this.interactive = interactive;
        this.entityId = Entity.nextEntityId();
        this.entityUuid = UUID.randomUUID();
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
    }

    @Override
    public Key type() {
        return ElementTypes.HITBOX;
    }

    @Override
    public Vec3 localOffset() {
        return this.localOffset;
    }

    @Override
    public boolean isValid() {
        return this.width > 0.0f && this.height > 0.0f;
    }

    @Override
    public int[] entityIds() {
        return new int[]{this.entityId};
    }

    @Override
    public void render(RenderContext ctx) {
        Vec3 worldPos = ContraptionMath.renderPosition(this.localOffset, ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        boolean metaChanged = ctx.scale() != this.lastScale;
        this.lastScale = ctx.scale();
        for (Player viewer : ctx.viewers()) {
            UUID viewerId = viewer.uuid();
            if (!this.shownTo.contains(viewerId)) {
                this.spawn(viewer, worldPos);
                this.shownTo.add(viewerId);
                continue;
            }
            if (!ctx.moved()) continue;
            this.sendPositionSync(viewer, worldPos);
            if (!metaChanged) continue;
            viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.buildMetadata()), false);
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
    }

    private void spawn(Player viewer, Vec3 pos) {
        Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.entityUuid, pos.x, pos.y, pos.z, 0.0f, 0.0f, EntityType.INTERACTION, 0, Vec3.ZERO, 0.0);
        Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.buildMetadata());
        viewer.sendPackets(List.of(add, data), false);
    }

    private void sendPositionSync(Player viewer, Vec3 pos) {
        viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, pos.x, pos.y, pos.z, 0.0f, 0.0f, false), false);
    }

    private List<Object> buildMetadata() {
        ArrayList<Object> values = new ArrayList<Object>();
        float s = (float)this.lastScale;
        InteractionData.Width.addEntityData(Float.valueOf(this.width * s), values);
        InteractionData.Height.addEntityData(Float.valueOf(this.height * s), values);
        InteractionData.Response.addEntityData(this.interactive, values);
        return values;
    }
}

