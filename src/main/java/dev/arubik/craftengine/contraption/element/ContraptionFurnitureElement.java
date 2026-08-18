/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.api.CraftEngineFurniture
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData
 *  net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture
 *  net.momirealms.craftengine.bukkit.entity.furniture.element.ArmorStandFurnitureElementConfig
 *  net.momirealms.craftengine.bukkit.entity.furniture.element.ItemDisplayFurnitureElementConfig
 *  net.momirealms.craftengine.bukkit.entity.furniture.element.ItemFurnitureElementConfig
 *  net.momirealms.craftengine.bukkit.entity.furniture.element.TextDisplayFurnitureElementConfig
 *  net.momirealms.craftengine.core.entity.furniture.FurnitureDefinition
 *  net.momirealms.craftengine.core.entity.furniture.FurnitureVariant
 *  net.momirealms.craftengine.core.entity.furniture.element.FurnitureElementConfig
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LightLayer;
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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;

public final class ContraptionFurnitureElement
implements ContraptionElement {
    private final Vec3 localOffset;
    private final float yawOffsetDegrees;
    private final Key definitionId;
    private final String variantName;
    private BukkitFurniture liveFurniture;
    private final List<MirrorCell> mirrors = new ArrayList<MirrorCell>();
    private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
    private boolean cellsBuilt = false;
    private int lastBlockLight = -1;
    private int lastSkyLight = -1;

    public ContraptionFurnitureElement(Vec3 localOffset, float yawOffsetDegrees, Key definitionId, String variantName, BukkitFurniture liveFurniture) {
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
        return this.localOffset;
    }

    @Override
    public boolean isValid() {
        return this.liveFurniture != null && this.liveFurniture.isValid();
    }

    @Override
    public int[] entityIds() {
        int[] ids = new int[this.mirrors.size()];
        for (int i = 0; i < this.mirrors.size(); ++i) {
            ids[i] = this.mirrors.get((int)i).entityId;
        }
        return ids;
    }

    public Key definitionId() {
        return this.definitionId;
    }

    public String variantName() {
        return this.variantName;
    }

    public float yawOffsetDegrees() {
        return this.yawOffsetDegrees;
    }

    public BukkitFurniture liveFurniture() {
        return this.liveFurniture;
    }

    @Override
    public void tick(RenderContext ctx) {
        if (!this.cellsBuilt) {
            this.buildCells();
            this.cellsBuilt = true;
        }
    }

    @Override
    public void render(RenderContext ctx) {
        boolean lightChanged;
        if (this.mirrors.isEmpty()) {
            return;
        }
        int blockLight = 15;
        int skyLight = 15;
        if (ctx.realLevel() != null) {
            try {
                Vec3 samplePos = ContraptionMath.renderPosition(this.localOffset, ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
                BlockPos worldAt = BlockPos.containing((double)samplePos.x, (double)samplePos.y, (double)samplePos.z);
                blockLight = ctx.realLevel().getLightEngine().getLayerListener(LightLayer.BLOCK).getLightValue(worldAt);
                skyLight = ctx.realLevel().getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(worldAt);
            }
            catch (Throwable samplePos) {
                // empty catch block
            }
        }
        BlockPos localPos = BlockPos.containing((double)this.localOffset.x, (double)this.localOffset.y, (double)this.localOffset.z);
        if (ctx.lightMap() != null) {
            blockLight = ctx.lightMap().combinedBlockLight(localPos, blockLight);
        }
        boolean bl = lightChanged = blockLight != this.lastBlockLight || skyLight != this.lastSkyLight;
        if (lightChanged) {
            this.lastBlockLight = blockLight;
            this.lastSkyLight = skyLight;
        }
        for (MirrorCell mirror : this.mirrors) {
            Vec3 local = this.localOffset.add((double)mirror.offset.x, (double)mirror.offset.y, (double)mirror.offset.z);
            Vec3 worldPos = ContraptionMath.renderPosition(local, ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
            float worldYaw = this.yawOffsetDegrees + mirror.baseYaw + (float)ctx.yawDegrees();
            float worldPitch = mirror.basePitch;
            for (Player viewer : ctx.viewers()) {
                UUID viewerId = viewer.uuid();
                if (!this.shownTo.contains(viewerId)) {
                    mirror.spawn(viewer, worldPos, worldYaw, worldPitch);
                    this.sendBrightness(viewer, mirror.entityId, this.lastBlockLight, this.lastSkyLight);
                    continue;
                }
                if (!ctx.moved()) continue;
                mirror.updatePosition(viewer, worldPos, worldYaw, worldPitch);
            }
        }
        if (lightChanged) {
            for (MirrorCell mirror : this.mirrors) {
                for (Player viewer : ctx.viewers()) {
                    if (!this.shownTo.contains(viewer.uuid())) continue;
                    this.sendBrightness(viewer, mirror.entityId, this.lastBlockLight, this.lastSkyLight);
                }
            }
        }
        for (Player viewer : ctx.viewers()) {
            this.shownTo.add(viewer.uuid());
        }
    }

    private void sendBrightness(Player viewer, int eid, int bl, int sl) {
        ArrayList values = new ArrayList();
        DisplayData.BrightnessOverride.addEntityData((bl << 4 | sl << 20), values);
        viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(eid, values), false);
    }

    @Override
    public void despawn(List<Player> viewers) {
        for (MirrorCell mirror : this.mirrors) {
            for (Player viewer : viewers) {
                if (!this.shownTo.contains(viewer.uuid())) continue;
                viewer.sendPacket(mirror.despawnPacket, false);
            }
        }
        this.shownTo.clear();
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
        if (this.liveFurniture != null && this.liveFurniture.isValid()) {
            try {
                CraftEngineFurniture.remove((Entity)this.liveFurniture.getBukkitEntity(), (boolean)false, (boolean)false);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        Vec3 rotated = ContraptionFurnitureElement.rotateVec(this.localOffset, quarterTurns);
        double worldX = (double)bearingPos.getX() + rotated.x;
        double worldY = (double)bearingPos.getY() + rotated.y;
        double worldZ = (double)bearingPos.getZ() + rotated.z;
        float yaw = this.yawOffsetDegrees + (float)quarterTurns * 90.0f;
        Location loc = new Location((World)level.getWorld(), worldX, worldY, worldZ, yaw, 0.0f);
        CraftEngineFurniture.place((Location)loc, (Key)this.definitionId, (String)this.variantName, (boolean)true);
    }

    private void buildCells() {
        this.mirrors.clear();
        FurnitureDefinition def = CraftEngineFurniture.byId((Key)this.definitionId);
        if (def == null) {
            return;
        }
        FurnitureVariant variant = def.getVariant(this.variantName);
        if (variant == null) {
            return;
        }
        for (FurnitureElementConfig config : variant.elementConfigs()) {
            MirrorCell cell = this.wrapConfig(config);
            if (cell == null) continue;
            this.mirrors.add(cell);
        }
    }

    private MirrorCell wrapConfig(FurnitureElementConfig<?> config) {
        if (config instanceof ItemDisplayFurnitureElementConfig) {
            ItemDisplayFurnitureElementConfig c = (ItemDisplayFurnitureElementConfig)config;
            return new ItemDisplayMirror(c);
        }
        if (config instanceof TextDisplayFurnitureElementConfig) {
            TextDisplayFurnitureElementConfig c = (TextDisplayFurnitureElementConfig)config;
            return new TextDisplayMirror(c);
        }
        if (config instanceof ArmorStandFurnitureElementConfig) {
            ArmorStandFurnitureElementConfig c = (ArmorStandFurnitureElementConfig)config;
            return new ArmorStandMirror(c);
        }
        if (config instanceof ItemFurnitureElementConfig) {
            ItemFurnitureElementConfig c = (ItemFurnitureElementConfig)config;
            return new ItemMirror(c);
        }
        return null;
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

    private static abstract class MirrorCell {
        final int entityId = net.minecraft.world.entity.Entity.nextEntityId();
        final UUID uuid = UUID.randomUUID();
        final Object despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
        final Vector3f offset;
        final float baseYaw;
        final float basePitch;

        MirrorCell(Vector3f offset, float baseYaw, float basePitch) {
            this.offset = offset;
            this.baseYaw = baseYaw;
            this.basePitch = basePitch;
        }

        abstract void spawn(Player var1, Vec3 var2, float var3, float var4);

        abstract void updatePosition(Player var1, Vec3 var2, float var3, float var4);
    }

    private static final class ItemDisplayMirror
    extends MirrorCell {
        private final ItemDisplayFurnitureElementConfig config;

        ItemDisplayMirror(ItemDisplayFurnitureElementConfig config) {
            super(config.position, config.yRot, config.xRot);
            this.config = config;
        }

        @Override
        void spawn(Player viewer, Vec3 pos, float yawDeg, float pitchDeg) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.uuid, pos.x, pos.y, pos.z, pitchDeg, yawDeg, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
            ArrayList values = new ArrayList((Collection)this.config.metadata.apply(viewer, null));
            DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
            DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, values);
            viewer.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player viewer, Vec3 pos, float yawDeg, float pitchDeg) {
            viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, pos.x, pos.y, pos.z, yawDeg, pitchDeg, false), false);
        }
    }

    private static final class TextDisplayMirror
    extends MirrorCell {
        private final TextDisplayFurnitureElementConfig config;

        TextDisplayMirror(TextDisplayFurnitureElementConfig config) {
            super(config.position, config.yRot, config.xRot);
            this.config = config;
        }

        @Override
        void spawn(Player viewer, Vec3 pos, float yawDeg, float pitchDeg) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.uuid, pos.x, pos.y, pos.z, pitchDeg, yawDeg, EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0.0);
            ArrayList values = new ArrayList((Collection)this.config.metadata.apply(viewer));
            DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
            DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, values);
            viewer.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player viewer, Vec3 pos, float yawDeg, float pitchDeg) {
            viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, pos.x, pos.y, pos.z, yawDeg, pitchDeg, false), false);
        }
    }

    private static final class ArmorStandMirror
    extends MirrorCell {
        private final ArmorStandFurnitureElementConfig config;

        ArmorStandMirror(ArmorStandFurnitureElementConfig config) {
            super(config.position, config.yRot, config.xRot);
            this.config = config;
        }

        @Override
        void spawn(Player viewer, Vec3 pos, float yawDeg, float pitchDeg) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.uuid, pos.x, pos.y, pos.z, pitchDeg, yawDeg, EntityType.ARMOR_STAND, 0, Vec3.ZERO, yawDeg);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.config.metadata.apply(viewer));
            viewer.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player viewer, Vec3 pos, float yawDeg, float pitchDeg) {
            viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, pos.x, pos.y, pos.z, yawDeg, pitchDeg, false), false);
        }
    }

    private static final class ItemMirror
    extends MirrorCell {
        private final ItemFurnitureElementConfig config;

        ItemMirror(ItemFurnitureElementConfig config) {
            super(config.position, 0.0f, 0.0f);
            this.config = config;
        }

        @Override
        void spawn(Player viewer, Vec3 pos, float yawDeg, float pitchDeg) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.uuid, pos.x, pos.y, pos.z, 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
            ArrayList values = new ArrayList((Collection)this.config.metadata.apply(viewer, null));
            DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
            DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, values);
            viewer.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player viewer, Vec3 pos, float yawDeg, float pitchDeg) {
            viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, pos.x, pos.y, pos.z, 0.0f, 0.0f, false), false);
        }
    }
}

