package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.InteractionData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Secondary hitbox element: a packet-only INTERACTION entity providing collision/click detection
 * for furniture and other custom elements inside a contraption. Mirrors the pattern from
 * ContraptionFurnitureSwarm's InteractionHitboxCell — axis-aligned, repositioned every tick.
 */
public final class ContraptionSecondaryHitbox implements ContraptionElement {

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
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(
                IntList.of(entityId));
    }

    @Override
    public Key type() {
        return ElementTypes.HITBOX;
    }

    @Override
    public Vec3 localOffset() {
        return localOffset;
    }

    @Override
    public boolean isValid() {
        return width > 0 && height > 0;
    }

    @Override
    public int[] entityIds() {
        return new int[]{entityId};
    }

    @Override
    public void render(RenderContext ctx) {
        Vec3 worldPos = ContraptionMath.renderPosition(localOffset, ctx.bearing(),
                ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());

        boolean metaChanged = ctx.scale() != lastScale;
        lastScale = ctx.scale();

        for (Player viewer : ctx.viewers()) {
            UUID viewerId = viewer.uuid();
            if (!shownTo.contains(viewerId)) {
                spawn(viewer, worldPos);
                shownTo.add(viewerId);
            } else if (ctx.moved()) {
                sendPositionSync(viewer, worldPos);
                if (metaChanged) {
                    viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                            entityId, buildMetadata()), false);
                }
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
        // Hitboxes are ephemeral — nothing to restore on disassemble
    }

    // ---- private ----

    private void spawn(Player viewer, Vec3 pos) {
        Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, entityUuid,
                pos.x, pos.y, pos.z, 0f, 0f, EntityType.INTERACTION, 0, Vec3.ZERO, 0);
        Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, buildMetadata());
        viewer.sendPackets(List.of(add, data), false);
    }

    private void sendPositionSync(Player viewer, Vec3 pos) {
        viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                entityId, pos.x, pos.y, pos.z, 0f, 0f, false), false);
    }

    private List<Object> buildMetadata() {
        List<Object> values = new ArrayList<>();
        float s = (float) lastScale;
        InteractionData.Width.addEntityData(width * s, values);
        InteractionData.Height.addEntityData(height * s, values);
        InteractionData.Response.addEntityData(interactive, values);
        return values;
    }
}
