package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fluid renderer element: a packet-only ITEM_DISPLAY entity showing a fluid level model
 * (cml:fluidlvl_<type>_<level>) inside a captured tank block. Each element represents one
 * layer/cell of the fluid display. Repositions with the contraption's bearing transform.
 */
public final class FluidRendererElement implements ContraptionElement {

    private final Vec3 localOffset;
    private final Key fluidTypeId;
    private final int fluidLevel;
    private final float scaleX, scaleY, scaleZ;

    private final int entityId;
    private final UUID entityUuid;
    private final Object despawnPacket;
    private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
    private boolean metaDirty = true;
    private double lastScale = 1.0;
    private int lastBlockLight = -1;
    private int lastSkyLight = -1;

    public FluidRendererElement(Vec3 localOffset, Key fluidTypeId, int fluidLevel,
                                float scaleX, float scaleY, float scaleZ) {
        this.localOffset = localOffset;
        this.fluidTypeId = fluidTypeId;
        this.fluidLevel = fluidLevel;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.entityId = Entity.nextEntityId();
        this.entityUuid = UUID.randomUUID();
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(
                IntList.of(entityId));
    }

    @Override
    public Key type() {
        return ElementTypes.FLUID;
    }

    @Override
    public Vec3 localOffset() {
        return localOffset;
    }

    @Override
    public boolean isValid() {
        return fluidTypeId != null && fluidLevel >= 0;
    }

    @Override
    public int[] entityIds() {
        return new int[]{entityId};
    }

    @Override
    public void render(RenderContext ctx) {
        Vec3 worldPos = ContraptionMath.renderPosition(localOffset, ctx.bearing(),
                ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());

        boolean scaleChanged = ctx.scale() != lastScale;
        lastScale = ctx.scale();

        int blockLight = 15;
        int skyLight = 15;
        if (ctx.realLevel() != null) {
            try {
                net.minecraft.core.BlockPos worldAt = net.minecraft.core.BlockPos.containing(worldPos.x, worldPos.y, worldPos.z);
                blockLight = ctx.realLevel().getLightEngine()
                        .getLayerListener(net.minecraft.world.level.LightLayer.BLOCK).getLightValue(worldAt);
                skyLight = ctx.realLevel().getLightEngine()
                        .getLayerListener(net.minecraft.world.level.LightLayer.SKY).getLightValue(worldAt);
            } catch (Throwable ignored) {}
        }
        BlockPos localPos = BlockPos.containing(localOffset.x, localOffset.y, localOffset.z);
        if (ctx.lightMap() != null) {
            blockLight = ctx.lightMap().combinedBlockLight(localPos, blockLight);
        }
        if (blockLight != lastBlockLight || skyLight != lastSkyLight) {
            lastBlockLight = blockLight;
            lastSkyLight = skyLight;
            metaDirty = true;
        }

        Quaternionf rotation = buildRotation(ctx);

        for (Player viewer : ctx.viewers()) {
            UUID viewerId = viewer.uuid();
            if (!shownTo.contains(viewerId)) {
                spawn(viewer, worldPos, rotation);
                shownTo.add(viewerId);
            } else if (ctx.moved() || metaDirty || scaleChanged) {
                sendPositionSync(viewer, worldPos);
                if (metaDirty || scaleChanged) {
                    viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                            entityId, buildMetadata(rotation)), false);
                }
            }
        }
        metaDirty = false;
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
        // Fluid displays are ephemeral visuals — the real tank restores its own render on disassemble
    }

    // ---- private ----

    private void spawn(Player viewer, Vec3 pos, Quaternionf rotation) {
        Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, entityUuid,
                pos.x, pos.y, pos.z, 0f, 0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0);
        Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, buildMetadata(rotation));
        viewer.sendPackets(List.of(add, data), false);
    }

    private void sendPositionSync(Player viewer, Vec3 pos) {
        viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                entityId, pos.x, pos.y, pos.z, 0f, 0f, false), false);
    }

    private List<Object> buildMetadata(Quaternionf rotation) {
        List<Object> values = new ArrayList<>();
        Object nmsItem = resolveFluidItem();
        if (nmsItem != null) {
            DisplayData.ItemDisplayData.ItemStack.addEntityData(nmsItem, values);
        }
        float s = (float) lastScale;
        DisplayData.Scale.addEntityData(new Vector3f(scaleX * s, scaleY * s, scaleZ * s), values);
        if (rotation != null) {
            DisplayData.LeftRotation.addEntityData(rotation, values);
        }
        int bl = lastBlockLight >= 0 ? lastBlockLight : 15;
        int sl = lastSkyLight >= 0 ? lastSkyLight : 15;
        DisplayData.BrightnessOverride.addEntityData((bl << 4) | (sl << 20), values);
        DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
        DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
        return values;
    }

    private Quaternionf buildRotation(RenderContext ctx) {
        if (ctx.pitchRadians() == 0.0 && ctx.rollRadians() == 0.0 && ctx.yawRadians() == 0.0) {
            return null;
        }
        return new Quaternionf()
                .rotateY((float) -ctx.yawRadians())
                .rotateX((float) ctx.pitchRadians())
                .rotateZ((float) ctx.rollRadians());
    }

    private Object resolveFluidItem() {
        try {
            var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(
                    Key.of("cml", "fluidlvl_" + fluidTypeId.value() + "_" + fluidLevel));
            if (def != null) {
                ItemStack bukkit = def.buildBukkitItem();
                if (bukkit != null) {
                    return CraftItemStack.asNMSCopy(bukkit);
                }
            }
        } catch (Throwable ignored) {}
        return null;
    }
}
