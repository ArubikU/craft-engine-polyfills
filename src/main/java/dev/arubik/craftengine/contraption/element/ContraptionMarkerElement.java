/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData$TextDisplayData
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

public final class ContraptionMarkerElement
implements ContraptionElement {
    public static final Key TYPE = Key.of((String)"polyfills", (String)"marker");
    private final Vec3 localOffset;
    private final String label;
    private final int color;
    private final int entityId;
    private final UUID entityUuid;
    private final Object despawnPacket;
    private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();

    public ContraptionMarkerElement(Vec3 localOffset, String label, int color) {
        this.localOffset = localOffset;
        this.label = label;
        this.color = color;
        this.entityId = Entity.nextEntityId();
        this.entityUuid = UUID.randomUUID();
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
    }

    @Override
    public Key type() {
        return TYPE;
    }

    @Override
    public Vec3 localOffset() {
        return this.localOffset;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int[] entityIds() {
        return new int[]{this.entityId};
    }

    @Override
    public void render(RenderContext ctx) {
        Vec3 pos = ContraptionMath.renderPosition(this.localOffset, ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        float yawDeg = (float)ctx.yawDegrees();
        for (Player viewer : ctx.viewers()) {
            UUID viewerId = viewer.uuid();
            if (!this.shownTo.contains(viewerId)) {
                Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.entityUuid, pos.x, pos.y, pos.z, 0.0f, yawDeg, EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0.0);
                ArrayList<Object> values = new ArrayList<>();
                DisplayData.TextDisplayData.Text.addEntityData(Component.literal((String)this.label), values);
                DisplayData.TextDisplayData.BackgroundColor.addEntityData(this.color, values);
                DisplayData.BillboardConstraints.addEntityData((byte)3, values);
                DisplayData.PosRotInterpolationDuration.addEntityData(ctx.interpTicks(), values);
                Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, values);
                viewer.sendPackets(List.of(add, data), false);
                this.shownTo.add(viewerId);
                continue;
            }
            if (!ctx.moved()) continue;
            viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, pos.x, pos.y, pos.z, yawDeg, 0.0f, false), false);
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

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", TYPE.toString());
        tag.putDouble("x", this.localOffset.x);
        tag.putDouble("y", this.localOffset.y);
        tag.putDouble("z", this.localOffset.z);
        tag.putString("label", this.label);
        tag.putInt("color", this.color);
        return tag;
    }

    public static ContraptionMarkerElement fromNbt(CompoundTag tag) {
        Vec3 offset = new Vec3(tag.getDouble("x").orElse(0.0).doubleValue(), tag.getDouble("y").orElse(0.0).doubleValue(), tag.getDouble("z").orElse(0.0).doubleValue());
        String label = tag.getString("label").orElse("");
        int color = tag.getInt("color").orElse(0x40000000);
        return new ContraptionMarkerElement(offset, label, color);
    }
}

