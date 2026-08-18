/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.block.entity.renderer.constant.ArmorStandBlockEntityElement
 *  net.momirealms.craftengine.bukkit.block.entity.renderer.constant.ItemBlockEntityElement
 *  net.momirealms.craftengine.bukkit.block.entity.renderer.constant.ItemDisplayBlockEntityElement
 *  net.momirealms.craftengine.bukkit.block.entity.renderer.constant.TextDisplayBlockEntityElement
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData
 *  net.momirealms.craftengine.core.block.entity.render.ConstantBlockEntityRenderer
 *  net.momirealms.craftengine.core.block.entity.render.element.BlockEntityElement
 *  net.momirealms.craftengine.core.block.entity.render.element.ConstantBlockEntityElement
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.plugin.CraftEngine
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  net.momirealms.craftengine.core.world.chunk.CEChunk
 *  org.bukkit.World
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.contraption.render.ContraptionRenderScale;
import dev.arubik.craftengine.util.MNms;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.block.entity.renderer.constant.ArmorStandBlockEntityElement;
import net.momirealms.craftengine.bukkit.block.entity.renderer.constant.ItemBlockEntityElement;
import net.momirealms.craftengine.bukkit.block.entity.renderer.constant.ItemDisplayBlockEntityElement;
import net.momirealms.craftengine.bukkit.block.entity.renderer.constant.TextDisplayBlockEntityElement;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.block.entity.render.ConstantBlockEntityRenderer;
import net.momirealms.craftengine.core.block.entity.render.element.BlockEntityElement;
import net.momirealms.craftengine.core.block.entity.render.element.ConstantBlockEntityElement;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.chunk.CEChunk;
import org.bukkit.World;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public final class ContraptionEntityRendererElement
implements ContraptionElement {
    private final BlockPos localPos;
    private final List<Cell> cells = new ArrayList<Cell>();
    private ConstantBlockEntityRenderer lastRenderer;

    public ContraptionEntityRendererElement(BlockPos localPos) {
        this.localPos = localPos;
    }

    public BlockPos localPos() {
        return this.localPos;
    }

    @Override
    public Key type() {
        return ElementTypes.ENTITY_RENDERER;
    }

    @Override
    public Vec3 localOffset() {
        return new Vec3((double)this.localPos.getX() + 0.5, (double)this.localPos.getY() + 0.5, (double)this.localPos.getZ() + 0.5);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int[] entityIds() {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (Cell cell : this.cells) {
            for (int id : cell.entityIds()) {
                ids.add(id);
            }
        }
        return ids.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public void tick(RenderContext ctx) {
        ConstantBlockEntityRenderer renderer = ContraptionEntityRendererElement.resolveRenderer(ctx.level(), this.localPos);
        if (renderer == this.lastRenderer) {
            return;
        }
        for (Cell cell : this.cells) {
            cell.despawnAll(ctx.viewers());
        }
        this.cells.clear();
        this.lastRenderer = renderer;
        if (renderer == null) {
            return;
        }
        for (ConstantBlockEntityElement element : renderer.elements()) {
            Cell c = ContraptionEntityRendererElement.wrap((BlockEntityElement)element, this.localPos);
            if (c == null) continue;
            this.cells.add(c);
        }
    }

    @Override
    public void render(RenderContext ctx) {
        if (this.cells.isEmpty()) {
            return;
        }
        int blockLight = 15;
        int skyLight = 15;
        if (ctx.realLevel() != null) {
            try {
                Vec3 worldPos = ContraptionMath.renderPosition(this.localOffset(), ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
                BlockPos at = BlockPos.containing((double)worldPos.x, (double)worldPos.y, (double)worldPos.z);
                blockLight = ctx.realLevel().getLightEngine().getLayerListener(LightLayer.BLOCK).getLightValue(at);
                skyLight = ctx.realLevel().getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(at);
            }
            catch (Throwable worldPos) {
                // empty catch block
            }
        }
        if (ctx.lightMap() != null) {
            blockLight = ctx.lightMap().combinedBlockLight(this.localPos, blockLight);
        }
        Quaternionf tiltQ = ctx.pitchRadians() == 0.0 && ctx.rollRadians() == 0.0 ? null : new Quaternionf().rotateX((float)ctx.pitchRadians()).rotateZ((float)ctx.rollRadians());
        for (Cell cell : this.cells) {
            cell.render(ctx.viewers(), ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale(), tiltQ, blockLight, skyLight, ctx.moved());
        }
    }

    @Override
    public void despawn(List<Player> viewers) {
        for (Cell cell : this.cells) {
            cell.despawnAll(viewers);
        }
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
    }

    private static ConstantBlockEntityRenderer resolveRenderer(ContraptionLevel level, BlockPos local) {
        if (level == null) {
            return null;
        }
        try {
            World w = level.getWorld();
            CEWorld ceWorld = CraftEngine.instance().worldManager().getWorld(w.getUID());
            if (ceWorld == null) {
                return null;
            }
            net.momirealms.craftengine.core.world.BlockPos cePos = new net.momirealms.craftengine.core.world.BlockPos(local.getX(), local.getY(), local.getZ());
            CEChunk chunk = ceWorld.getChunkAtIfLoaded(cePos);
            return chunk == null ? null : chunk.getConstantBlockEntityRenderer(cePos);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static Cell wrap(BlockEntityElement element, BlockPos local) {
        if (element instanceof ItemDisplayBlockEntityElement) {
            ItemDisplayBlockEntityElement e = (ItemDisplayBlockEntityElement)element;
            return new ItemDisplayCell(e, local);
        }
        if (element instanceof TextDisplayBlockEntityElement) {
            TextDisplayBlockEntityElement e = (TextDisplayBlockEntityElement)element;
            return new TextDisplayCell(e, local);
        }
        if (element instanceof ArmorStandBlockEntityElement) {
            ArmorStandBlockEntityElement e = (ArmorStandBlockEntityElement)element;
            return new ArmorStandCell(e, local);
        }
        if (element instanceof ItemBlockEntityElement) {
            ItemBlockEntityElement e = (ItemBlockEntityElement)element;
            return new ItemRideCell(e, local);
        }
        return null;
    }

    private static void addInterpolationTuning(List<Object> values) {
        DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
        DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
    }

    private static abstract class Cell {
        final BlockPos local;
        final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
        int lastBlockLight = -1;
        int lastSkyLight = -1;
        double lastScale = 1.0;
        Quaternionf lastRotation;

        Cell(BlockPos local) {
            this.local = local;
        }

        abstract int[] entityIds();

        abstract void spawn(Player var1, Vec3 var2, float var3);

        abstract void updatePosition(Player var1, Vec3 var2, float var3);

        abstract void updateMetadata(Player var1);

        abstract void despawn(Player var1);

        abstract Vector3f offset();

        abstract float baseYaw();

        abstract float basePitch();

        Quaternionf modelRotation(Quaternionf tiltQ) {
            if (tiltQ == null) {
                return null;
            }
            Quaternionf e0 = new Quaternionf().rotateY((float)(-Math.toRadians(this.baseYaw()))).rotateX((float)Math.toRadians(this.basePitch()));
            return e0.invert(new Quaternionf()).mul((Quaternionfc)tiltQ).mul((Quaternionfc)e0);
        }

        private boolean rotationChanged(Quaternionf next) {
            if (this.lastRotation == null || next == null) {
                return this.lastRotation != next;
            }
            return !next.equals((Quaternionfc)this.lastRotation, 1.0E-4f);
        }

        void render(List<Player> viewers, Vec3 bearing, double yawRadians, double pitchRadians, double rollRadians, double scale, Quaternionf tiltQ, int blockLight, int skyLight, boolean moved) {
            Vector3f off = this.offset();
            Vec3 localWithOff = new Vec3((double)((float)this.local.getX() + off.x), (double)((float)this.local.getY() + off.y), (double)((float)this.local.getZ() + off.z));
            Vec3 worldPos = ContraptionMath.renderPosition(localWithOff, bearing, yawRadians, pitchRadians, rollRadians, scale);
            float yawDeg = this.baseYaw() + (float)Math.toDegrees(yawRadians);
            Quaternionf rotation = this.modelRotation(tiltQ);
            boolean metaChanged = blockLight != this.lastBlockLight || skyLight != this.lastSkyLight || scale != this.lastScale || this.rotationChanged(rotation);
            this.lastBlockLight = blockLight;
            this.lastSkyLight = skyLight;
            this.lastScale = scale;
            this.lastRotation = rotation;
            HashSet<UUID> current = new HashSet<UUID>();
            for (Player p : viewers) {
                UUID id = p.uuid();
                if (id == null) continue;
                current.add(id);
                if (this.shownTo.add(id)) {
                    this.spawn(p, worldPos, yawDeg);
                    continue;
                }
                this.updatePosition(p, worldPos, yawDeg);
                if (!metaChanged) continue;
                this.updateMetadata(p);
            }
            this.shownTo.retainAll(current);
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) {
                this.despawn(p);
            }
            this.shownTo.clear();
        }
    }

    private static final class ItemDisplayCell
    extends Cell {
        private final ItemDisplayBlockEntityElement element;
        private final UUID uuid = UUID.randomUUID();

        ItemDisplayCell(ItemDisplayBlockEntityElement element, BlockPos local) {
            super(local);
            this.element = element;
        }

        @Override
        int[] entityIds() {
            return new int[]{this.element.entityId};
        }

        @Override
        Vector3f offset() {
            return this.element.config.position();
        }

        @Override
        float baseYaw() {
            return this.element.config.yRot();
        }

        @Override
        float basePitch() {
            return this.element.config.xRot();
        }

        @Override
        void spawn(Player player, Vec3 pos, float yawDeg) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.element.entityId, this.uuid, pos.x, pos.y, pos.z, this.element.config.xRot(), yawDeg, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId, this.metadata(player));
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 pos, float yawDeg) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.element.entityId, pos.x, pos.y, pos.z, yawDeg, this.element.config.xRot(), false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId, this.metadata(player)), false);
        }

        private List<Object> metadata(Player player) {
            ArrayList<Object> values = new ArrayList<Object>(this.element.config.metadataValues(player, this.element.tintSource));
            ContraptionRenderScale.applyTo(values, this.lastScale, this.lastRotation);
            DisplayData.BrightnessOverride.addEntityData((this.lastBlockLight << 4 | this.lastSkyLight << 20), values);
            ContraptionEntityRendererElement.addInterpolationTuning(values);
            return values;
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(this.element.cachedDespawnPacket, false);
        }
    }

    private static final class TextDisplayCell
    extends Cell {
        private final TextDisplayBlockEntityElement element;
        private final UUID uuid = UUID.randomUUID();

        TextDisplayCell(TextDisplayBlockEntityElement element, BlockPos local) {
            super(local);
            this.element = element;
        }

        @Override
        int[] entityIds() {
            return new int[]{this.element.entityId};
        }

        @Override
        Vector3f offset() {
            return this.element.config.position();
        }

        @Override
        float baseYaw() {
            return this.element.config.yRot();
        }

        @Override
        float basePitch() {
            return this.element.config.xRot();
        }

        @Override
        void spawn(Player player, Vec3 pos, float yawDeg) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.element.entityId, this.uuid, pos.x, pos.y, pos.z, this.element.config.xRot(), yawDeg, EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0.0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId, this.metadata(player));
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 pos, float yawDeg) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.element.entityId, pos.x, pos.y, pos.z, yawDeg, this.element.config.xRot(), false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId, this.metadata(player)), false);
        }

        private List<Object> metadata(Player player) {
            ArrayList<Object> values = new ArrayList<Object>(this.element.config.metadataValues(player));
            ContraptionRenderScale.applyTo(values, this.lastScale, this.lastRotation);
            DisplayData.BrightnessOverride.addEntityData((this.lastBlockLight << 4 | this.lastSkyLight << 20), values);
            ContraptionEntityRendererElement.addInterpolationTuning(values);
            return values;
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(this.element.cachedDespawnPacket, false);
        }
    }

    private static final class ArmorStandCell
    extends Cell {
        private final ArmorStandBlockEntityElement element;
        private final UUID uuid = UUID.randomUUID();

        ArmorStandCell(ArmorStandBlockEntityElement element, BlockPos local) {
            super(local);
            this.element = element;
        }

        @Override
        int[] entityIds() {
            return new int[]{this.element.entityId};
        }

        @Override
        Vector3f offset() {
            return this.element.config.position();
        }

        @Override
        float baseYaw() {
            return this.element.config.yRot();
        }

        @Override
        float basePitch() {
            return this.element.config.xRot();
        }

        @Override
        Quaternionf modelRotation(Quaternionf tiltQ) {
            return null;
        }

        @Override
        void spawn(Player player, Vec3 pos, float yawDeg) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.element.entityId, this.uuid, pos.x, pos.y, pos.z, this.element.config.xRot(), yawDeg, EntityType.ARMOR_STAND, 0, Vec3.ZERO, yawDeg);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId, this.element.config.metadataValues(player));
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 pos, float yawDeg) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.element.entityId, pos.x, pos.y, pos.z, yawDeg, this.element.config.xRot(), false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId, this.element.config.metadataValues(player)), false);
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(this.element.cachedDespawnPacket, false);
        }
    }

    private static final class ItemRideCell
    extends Cell {
        private final ItemBlockEntityElement element;
        private final UUID uuid1 = UUID.randomUUID();
        private final UUID uuid2 = UUID.randomUUID();

        ItemRideCell(ItemBlockEntityElement element, BlockPos local) {
            super(local);
            this.element = element;
        }

        @Override
        int[] entityIds() {
            return new int[]{this.element.entityId1, this.element.entityId2};
        }

        @Override
        Vector3f offset() {
            return this.element.config.position();
        }

        @Override
        float baseYaw() {
            return 0.0f;
        }

        @Override
        float basePitch() {
            return 0.0f;
        }

        @Override
        Quaternionf modelRotation(Quaternionf tiltQ) {
            return null;
        }

        @Override
        void spawn(Player player, Vec3 pos, float yawDeg) {
            Object add1 = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.element.entityId1, this.uuid1, pos.x, pos.y, pos.z, 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
            ArrayList<Object> displayValues = new ArrayList<Object>();
            ContraptionEntityRendererElement.addInterpolationTuning(displayValues);
            Object data1 = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId1, displayValues);
            Object add2 = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.element.entityId2, this.uuid2, pos.x, pos.y, pos.z, 0.0f, 0.0f, EntityType.ITEM, 0, Vec3.ZERO, 0.0);
            Object ride = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId2, this.element.config.metadataValues(player, this.element.tintSource));
            player.sendPackets(List.of(add1, data1, add2, this.element.cachedRidePacket, ride), false);
        }

        @Override
        void updatePosition(Player player, Vec3 pos, float yawDeg) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.element.entityId1, pos.x, pos.y, pos.z, 0.0f, 0.0f, false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId2, this.element.config.metadataValues(player, this.element.tintSource)), false);
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(this.element.cachedDespawnPacket, false);
        }
    }
}

