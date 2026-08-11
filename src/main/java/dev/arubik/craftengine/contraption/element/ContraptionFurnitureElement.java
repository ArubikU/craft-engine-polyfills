package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineFurniture;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture;
import net.momirealms.craftengine.bukkit.entity.furniture.element.ArmorStandFurnitureElementConfig;
import net.momirealms.craftengine.bukkit.entity.furniture.element.ItemDisplayFurnitureElementConfig;
import net.momirealms.craftengine.bukkit.entity.furniture.element.ItemFurnitureElementConfig;
import net.momirealms.craftengine.bukkit.entity.furniture.element.TextDisplayFurnitureElementConfig;
import net.momirealms.craftengine.core.entity.furniture.FurnitureDefinition;
import net.momirealms.craftengine.core.entity.furniture.FurnitureVariant;
import net.momirealms.craftengine.core.entity.furniture.element.FurnitureElementConfig;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ContraptionFurnitureElement implements ContraptionElement {

    private final Vec3 localOffset;
    private final float yawOffsetDegrees;
    private final Key definitionId;
    private final String variantName;
    private BukkitFurniture liveFurniture;

    private final List<MirrorCell> mirrors = new ArrayList<>();
    private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
    private boolean cellsBuilt = false;
    private int lastBlockLight = -1;
    private int lastSkyLight = -1;

    public ContraptionFurnitureElement(Vec3 localOffset, float yawOffsetDegrees,
                                       Key definitionId, String variantName,
                                       BukkitFurniture liveFurniture) {
        this.localOffset = localOffset;
        this.yawOffsetDegrees = yawOffsetDegrees;
        this.definitionId = definitionId;
        this.variantName = variantName;
        this.liveFurniture = liveFurniture;
    }

    @Override
    public Key type() {
        return ElementTypes.FURNITURE;
    }

    @Override
    public Vec3 localOffset() {
        return localOffset;
    }

    @Override
    public boolean isValid() {
        return liveFurniture != null && liveFurniture.isValid();
    }

    @Override
    public int[] entityIds() {
        int[] ids = new int[mirrors.size()];
        for (int i = 0; i < mirrors.size(); i++) {
            ids[i] = mirrors.get(i).entityId;
        }
        return ids;
    }

    public Key definitionId() {
        return definitionId;
    }

    public String variantName() {
        return variantName;
    }

    public float yawOffsetDegrees() {
        return yawOffsetDegrees;
    }

    public BukkitFurniture liveFurniture() {
        return liveFurniture;
    }

    @Override
    public void tick(RenderContext ctx) {
        if (!cellsBuilt) {
            buildCells();
            cellsBuilt = true;
        }
    }

    @Override
    public void render(RenderContext ctx) {
        if (mirrors.isEmpty()) return;

        // Compute lighting
        int blockLight = 15;
        int skyLight = 15;
        if (ctx.realLevel() != null) {
            try {
                Vec3 samplePos = ContraptionMath.renderPosition(localOffset, ctx.bearing(),
                        ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
                net.minecraft.core.BlockPos worldAt = net.minecraft.core.BlockPos.containing(
                        samplePos.x, samplePos.y, samplePos.z);
                blockLight = ctx.realLevel().getLightEngine()
                        .getLayerListener(net.minecraft.world.level.LightLayer.BLOCK).getLightValue(worldAt);
                skyLight = ctx.realLevel().getLightEngine()
                        .getLayerListener(net.minecraft.world.level.LightLayer.SKY).getLightValue(worldAt);
            } catch (Throwable ignored) {}
        }
        net.minecraft.core.BlockPos localPos = net.minecraft.core.BlockPos.containing(
                localOffset.x, localOffset.y, localOffset.z);
        if (ctx.lightMap() != null) {
            blockLight = ctx.lightMap().combinedBlockLight(localPos, blockLight);
        }
        boolean lightChanged = blockLight != lastBlockLight || skyLight != lastSkyLight;
        if (lightChanged) {
            lastBlockLight = blockLight;
            lastSkyLight = skyLight;
        }

        for (MirrorCell mirror : mirrors) {
            Vec3 local = localOffset.add(mirror.offset.x, mirror.offset.y, mirror.offset.z);
            Vec3 worldPos = ContraptionMath.renderPosition(local, ctx.bearing(),
                    ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
            float worldYaw = yawOffsetDegrees + mirror.baseYaw + (float) ctx.yawDegrees();
            float worldPitch = mirror.basePitch;
            for (Player viewer : ctx.viewers()) {
                UUID viewerId = viewer.uuid();
                if (!shownTo.contains(viewerId)) {
                    mirror.spawn(viewer, worldPos, worldYaw, worldPitch);
                    sendBrightness(viewer, mirror.entityId, lastBlockLight, lastSkyLight);
                } else if (ctx.moved()) {
                    mirror.updatePosition(viewer, worldPos, worldYaw, worldPitch);
                }
            }
        }

        if (lightChanged) {
            for (MirrorCell mirror : mirrors) {
                for (Player viewer : ctx.viewers()) {
                    if (shownTo.contains(viewer.uuid())) {
                        sendBrightness(viewer, mirror.entityId, lastBlockLight, lastSkyLight);
                    }
                }
            }
        }

        for (Player viewer : ctx.viewers()) {
            shownTo.add(viewer.uuid());
        }
    }

    private void sendBrightness(Player viewer, int eid, int bl, int sl) {
        List<Object> values = new ArrayList<>();
        DisplayData.BrightnessOverride.addEntityData((bl << 4) | (sl << 20), values);
        viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(eid, values), false);
    }

    @Override
    public void despawn(List<Player> viewers) {
        for (MirrorCell mirror : mirrors) {
            for (Player viewer : viewers) {
                if (shownTo.contains(viewer.uuid())) {
                    viewer.sendPacket(mirror.despawnPacket, false);
                }
            }
        }
        shownTo.clear();
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
        if (liveFurniture != null && liveFurniture.isValid()) {
            try {
                CraftEngineFurniture.remove(liveFurniture.getBukkitEntity(), false, false);
            } catch (Throwable ignored) {}
        }

        Vec3 rotated = rotateVec(localOffset, quarterTurns);
        double worldX = bearingPos.getX() + rotated.x;
        double worldY = bearingPos.getY() + rotated.y;
        double worldZ = bearingPos.getZ() + rotated.z;
        float yaw = yawOffsetDegrees + quarterTurns * 90f;

        org.bukkit.Location loc = new org.bukkit.Location(
                level.getWorld(), worldX, worldY, worldZ, yaw, 0f);
        CraftEngineFurniture.place(loc, definitionId, variantName, true);
    }

    // ---- cell building ----

    private void buildCells() {
        mirrors.clear();
        FurnitureDefinition def = CraftEngineFurniture.byId(definitionId);
        if (def == null) return;
        FurnitureVariant variant = def.getVariant(variantName);
        if (variant == null) return;

        for (FurnitureElementConfig<?> config : variant.elementConfigs()) {
            MirrorCell cell = wrapConfig(config);
            if (cell != null) {
                mirrors.add(cell);
            }
        }
    }

    private MirrorCell wrapConfig(FurnitureElementConfig<?> config) {
        if (config instanceof ItemDisplayFurnitureElementConfig c) {
            return new ItemDisplayMirror(c);
        }
        if (config instanceof TextDisplayFurnitureElementConfig c) {
            return new TextDisplayMirror(c);
        }
        if (config instanceof ArmorStandFurnitureElementConfig c) {
            return new ArmorStandMirror(c);
        }
        if (config instanceof ItemFurnitureElementConfig c) {
            return new ItemMirror(c);
        }
        return null;
    }

    // ---- mirror cells ----

    private static abstract class MirrorCell {
        final int entityId;
        final UUID uuid;
        final Object despawnPacket;
        final Vector3f offset;
        final float baseYaw;
        final float basePitch;

        MirrorCell(Vector3f offset, float baseYaw, float basePitch) {
            this.entityId = net.minecraft.world.entity.Entity.nextEntityId();
            this.uuid = UUID.randomUUID();
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(
                    IntList.of(entityId));
            this.offset = offset;
            this.baseYaw = baseYaw;
            this.basePitch = basePitch;
        }

        abstract void spawn(Player viewer, Vec3 pos, float yawDeg, float pitchDeg);
        abstract void updatePosition(Player viewer, Vec3 pos, float yawDeg, float pitchDeg);
    }

    private static final class ItemDisplayMirror extends MirrorCell {
        private final ItemDisplayFurnitureElementConfig config;

        ItemDisplayMirror(ItemDisplayFurnitureElementConfig config) {
            super(config.position, config.yRot, config.xRot);
            this.config = config;
        }

        @Override
        void spawn(Player viewer, Vec3 pos, float yawDeg, float pitchDeg) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, uuid,
                    pos.x, pos.y, pos.z, pitchDeg, yawDeg, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0);
            List<Object> values = new ArrayList<>(config.metadata.apply(viewer, null));
            DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
            DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, values);
            viewer.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player viewer, Vec3 pos, float yawDeg, float pitchDeg) {
            viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    entityId, pos.x, pos.y, pos.z, yawDeg, pitchDeg, false), false);
        }
    }

    private static final class TextDisplayMirror extends MirrorCell {
        private final TextDisplayFurnitureElementConfig config;

        TextDisplayMirror(TextDisplayFurnitureElementConfig config) {
            super(config.position, config.yRot, config.xRot);
            this.config = config;
        }

        @Override
        void spawn(Player viewer, Vec3 pos, float yawDeg, float pitchDeg) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, uuid,
                    pos.x, pos.y, pos.z, pitchDeg, yawDeg, EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0);
            List<Object> values = new ArrayList<>(config.metadata.apply(viewer));
            DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
            DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, values);
            viewer.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player viewer, Vec3 pos, float yawDeg, float pitchDeg) {
            viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    entityId, pos.x, pos.y, pos.z, yawDeg, pitchDeg, false), false);
        }
    }

    private static final class ArmorStandMirror extends MirrorCell {
        private final ArmorStandFurnitureElementConfig config;

        ArmorStandMirror(ArmorStandFurnitureElementConfig config) {
            super(config.position, config.yRot, config.xRot);
            this.config = config;
        }

        @Override
        void spawn(Player viewer, Vec3 pos, float yawDeg, float pitchDeg) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, uuid,
                    pos.x, pos.y, pos.z, pitchDeg, yawDeg, EntityType.ARMOR_STAND, 0, Vec3.ZERO, yawDeg);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, config.metadata.apply(viewer));
            viewer.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player viewer, Vec3 pos, float yawDeg, float pitchDeg) {
            viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    entityId, pos.x, pos.y, pos.z, yawDeg, pitchDeg, false), false);
        }
    }

    private static final class ItemMirror extends MirrorCell {
        private final ItemFurnitureElementConfig config;

        ItemMirror(ItemFurnitureElementConfig config) {
            super(config.position, 0f, 0f);
            this.config = config;
        }

        @Override
        void spawn(Player viewer, Vec3 pos, float yawDeg, float pitchDeg) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, uuid,
                    pos.x, pos.y, pos.z, 0f, 0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0);
            List<Object> values = new ArrayList<>(config.metadata.apply(viewer, null));
            DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
            DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, values);
            viewer.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player viewer, Vec3 pos, float yawDeg, float pitchDeg) {
            viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    entityId, pos.x, pos.y, pos.z, 0f, 0f, false), false);
        }
    }

    // ---- utility ----

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
