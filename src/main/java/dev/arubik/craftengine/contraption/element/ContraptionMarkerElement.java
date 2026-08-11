package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sample PERSISTENT element — survives contraption serialization independently of the level's
 * own block/furniture data. Use case: a player-placed marker (waypoint, redstone label, etc.)
 * that has no backing block in the ContraptionLevel but must reappear after a save/load cycle.
 *
 * <p>This is the template for any future persistent element: override {@link #isPersistent()}
 * to return {@code true} and implement {@link #toNbt()} / the static {@code fromNbt} factory.
 * ElementBuilder skips persistent elements (they are loaded from the saved NBT list instead).
 */
public final class ContraptionMarkerElement implements ContraptionElement {

    public static final Key TYPE = Key.of("polyfills", "marker");

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
        this.entityId = net.minecraft.world.entity.Entity.nextEntityId();
        this.entityUuid = UUID.randomUUID();
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
    }

    @Override
    public Key type() {
        return TYPE;
    }

    @Override
    public Vec3 localOffset() {
        return localOffset;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int[] entityIds() {
        return new int[]{entityId};
    }

    @Override
    public void render(RenderContext ctx) {
        Vec3 pos = ContraptionMath.renderPosition(localOffset, ctx.bearing(),
                ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        float yawDeg = (float) ctx.yawDegrees();

        for (Player viewer : ctx.viewers()) {
            UUID viewerId = viewer.uuid();
            if (!shownTo.contains(viewerId)) {
                Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, entityUuid,
                        pos.x, pos.y, pos.z, 0f, yawDeg, EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0);
                List<Object> values = new ArrayList<>();
                DisplayData.TextDisplayData.Text.addEntityData(
                        net.minecraft.network.chat.Component.literal(label), values);
                DisplayData.TextDisplayData.BackgroundColor.addEntityData(color, values);
                DisplayData.BillboardConstraints.addEntityData((byte) 3, values);
                DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
                Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, values);
                viewer.sendPackets(List.of(add, data), false);
                shownTo.add(viewerId);
            } else if (ctx.moved()) {
                viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                        entityId, pos.x, pos.y, pos.z, yawDeg, 0f, false), false);
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
        // Markers have no real-world representation — nothing to restore on disassemble.
    }

    // ---- persistence ----

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", TYPE.toString());
        tag.putDouble("x", localOffset.x);
        tag.putDouble("y", localOffset.y);
        tag.putDouble("z", localOffset.z);
        tag.putString("label", label);
        tag.putInt("color", color);
        return tag;
    }

    public static ContraptionMarkerElement fromNbt(CompoundTag tag) {
        Vec3 offset = new Vec3(
                tag.getDouble("x").orElse(0.0),
                tag.getDouble("y").orElse(0.0),
                tag.getDouble("z").orElse(0.0));
        String label = tag.getString("label").orElse("");
        int color = tag.getInt("color").orElse(0x40000000);
        return new ContraptionMarkerElement(offset, label, color);
    }
}
