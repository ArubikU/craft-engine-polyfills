package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.render.ContraptionRenderScale;
import dev.arubik.craftengine.util.MNms;
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
import net.momirealms.craftengine.core.block.entity.render.element.BlockEntityElement;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Renders CraftEngine entity-renderer blocks (iron pipes, tanks, etc.) inside a contraption.
 * Re-queries the CE constant renderer each tick so state-driven visuals (tank fill level, lit
 * furnace, etc.) update live. Replaces direct ContraptionBlockEntityElementMirror usage.
 */
public final class ContraptionEntityRendererElement implements ContraptionElement {

    private final BlockPos localPos;
    private final List<Cell> cells = new ArrayList<>();
    // Reference-equality: CE creates a new renderer instance when blockstate changes.
    private net.momirealms.craftengine.core.block.entity.render.ConstantBlockEntityRenderer lastRenderer;

    public ContraptionEntityRendererElement(BlockPos localPos) {
        this.localPos = localPos;
    }

    @Override public Key type() { return ElementTypes.ENTITY_RENDERER; }
    @Override public Vec3 localOffset() {
        return new Vec3(localPos.getX() + 0.5, localPos.getY() + 0.5, localPos.getZ() + 0.5);
    }
    @Override public boolean isValid() { return true; }

    @Override
    public int[] entityIds() {
        List<Integer> ids = new ArrayList<>();
        for (Cell cell : cells) { for (int id : cell.entityIds()) ids.add(id); }
        return ids.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public void tick(RenderContext ctx) {
        var renderer = resolveRenderer(ctx.level(), localPos);
        if (renderer == lastRenderer) return;
        for (Cell cell : cells) cell.despawnAll(ctx.viewers());
        cells.clear();
        lastRenderer = renderer;
        if (renderer == null) return;
        for (BlockEntityElement element : renderer.elements()) {
            Cell c = wrap(element, localPos);
            if (c != null) cells.add(c);
        }
    }

    @Override
    public void render(RenderContext ctx) {
        if (cells.isEmpty()) return;
        int blockLight = 15, skyLight = 15;
        if (ctx.realLevel() != null) {
            try {
                Vec3 worldPos = ContraptionMath.renderPosition(localOffset(), ctx.bearing(),
                        ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
                BlockPos at = BlockPos.containing(worldPos.x, worldPos.y, worldPos.z);
                blockLight = ctx.realLevel().getLightEngine()
                        .getLayerListener(LightLayer.BLOCK).getLightValue(at);
                skyLight = ctx.realLevel().getLightEngine()
                        .getLayerListener(LightLayer.SKY).getLightValue(at);
            } catch (Throwable ignored) {}
        }
        if (ctx.lightMap() != null) {
            blockLight = ctx.lightMap().combinedBlockLight(localPos, blockLight);
        }
        // Precompute raw tilt rotation once (null = upright = fast path in ContraptionRenderScale).
        Quaternionf tiltQ = (ctx.pitchRadians() == 0.0 && ctx.rollRadians() == 0.0) ? null
                : new Quaternionf().rotateX((float) ctx.pitchRadians()).rotateZ((float) ctx.rollRadians());
        for (Cell cell : cells) {
            cell.render(ctx.viewers(), ctx.bearing(), ctx.yawRadians(), ctx.scale(),
                    tiltQ, blockLight, skyLight, ctx.moved());
        }
    }

    @Override
    public void despawn(List<Player> viewers) {
        for (Cell cell : cells) cell.despawnAll(viewers);
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {}

    private static net.momirealms.craftengine.core.block.entity.render.ConstantBlockEntityRenderer
            resolveRenderer(ContraptionLevel level, BlockPos local) {
        if (level == null) return null;
        try {
            org.bukkit.World w = level.getWorld();
            CEWorld ceWorld = CraftEngine.instance().worldManager().getWorld(w.getUID());
            if (ceWorld == null) return null;
            net.momirealms.craftengine.core.world.BlockPos cePos =
                    new net.momirealms.craftengine.core.world.BlockPos(local.getX(), local.getY(), local.getZ());
            net.momirealms.craftengine.core.world.chunk.CEChunk chunk = ceWorld.getChunkAtIfLoaded(cePos);
            return chunk == null ? null : chunk.getConstantBlockEntityRenderer(cePos);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Cell wrap(BlockEntityElement element, BlockPos local) {
        if (element instanceof ItemDisplayBlockEntityElement e) return new ItemDisplayCell(e, local);
        if (element instanceof TextDisplayBlockEntityElement e) return new TextDisplayCell(e, local);
        if (element instanceof ArmorStandBlockEntityElement e) return new ArmorStandCell(e, local);
        if (element instanceof ItemBlockEntityElement e) return new ItemRideCell(e, local);
        return null;
    }

    private static void addInterpolationTuning(List<Object> values) {
        DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
        DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
    }

    // ---- abstract cell base ----

    private abstract static class Cell {
        final BlockPos local;
        final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
        // Fields set in render() before spawn/updateMetadata so metadata() reads current values.
        int lastBlockLight = -1;
        int lastSkyLight = -1;
        double lastScale = 1.0;
        Quaternionf lastRotation;

        Cell(BlockPos local) { this.local = local; }

        abstract int[] entityIds();
        abstract void spawn(Player player, Vec3 pos, float yawDeg);
        abstract void updatePosition(Player player, Vec3 pos, float yawDeg);
        abstract void updateMetadata(Player player);
        abstract void despawn(Player player);
        abstract Vector3f offset();
        abstract float baseYaw();
        abstract float basePitch();

        // E0⁻¹ · R_tilt · E0 — rotate tilt into element's authored frame.
        Quaternionf modelRotation(Quaternionf tiltQ) {
            if (tiltQ == null) return null;
            Quaternionf e0 = new Quaternionf()
                    .rotateY((float) -Math.toRadians(baseYaw()))
                    .rotateX((float) Math.toRadians(basePitch()));
            return e0.invert(new Quaternionf()).mul(tiltQ).mul(e0);
        }

        private boolean rotationChanged(Quaternionf next) {
            if (lastRotation == null || next == null) return lastRotation != next;
            return !next.equals(lastRotation, 1e-4f);
        }

        void render(List<Player> viewers, Vec3 bearing, double yawRadians, double scale,
                    Quaternionf tiltQ, int blockLight, int skyLight, boolean moved) {
            Vector3f off = offset();
            Vec3 localWithOff = new Vec3(local.getX() + off.x, local.getY() + off.y, local.getZ() + off.z);
            Vec3 worldPos = ContraptionMath.renderPosition(localWithOff, bearing, yawRadians, 0.0, 0.0, scale);
            float yawDeg = baseYaw() + (float) Math.toDegrees(yawRadians);
            Quaternionf rotation = modelRotation(tiltQ);
            boolean metaChanged = blockLight != lastBlockLight || skyLight != lastSkyLight
                    || scale != lastScale || rotationChanged(rotation);
            // Update fields BEFORE spawn/updateMetadata so their metadata() calls read current values.
            lastBlockLight = blockLight;
            lastSkyLight = skyLight;
            lastScale = scale;
            lastRotation = rotation;
            Set<UUID> current = new HashSet<>();
            for (Player p : viewers) {
                UUID id = p.uuid();
                if (id == null) continue;
                current.add(id);
                if (shownTo.add(id)) {
                    spawn(p, worldPos, yawDeg);
                } else {
                    updatePosition(p, worldPos, yawDeg);
                    if (metaChanged) updateMetadata(p);
                }
            }
            shownTo.retainAll(current);
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) despawn(p);
            shownTo.clear();
        }
    }

    // ---- ItemDisplay ----

    private static final class ItemDisplayCell extends Cell {
        private final ItemDisplayBlockEntityElement element;
        private final UUID uuid = UUID.randomUUID();

        ItemDisplayCell(ItemDisplayBlockEntityElement element, BlockPos local) {
            super(local);
            this.element = element;
        }

        @Override int[] entityIds() { return new int[]{element.entityId}; }
        @Override Vector3f offset() { return element.config.position(); }
        @Override float baseYaw() { return element.config.yRot(); }
        @Override float basePitch() { return element.config.xRot(); }

        @Override
        void spawn(Player player, Vec3 pos, float yawDeg) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(element.entityId, uuid,
                    pos.x, pos.y, pos.z, element.config.xRot(), yawDeg, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(element.entityId, metadata(player));
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 pos, float yawDeg) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    element.entityId, pos.x, pos.y, pos.z, yawDeg, element.config.xRot(), false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                    element.entityId, metadata(player)), false);
        }

        private List<Object> metadata(Player player) {
            List<Object> values = new ArrayList<>(element.config.metadataValues(player, element.tintSource));
            ContraptionRenderScale.applyTo(values, lastScale, lastRotation);
            DisplayData.BrightnessOverride.addEntityData((lastBlockLight << 4) | (lastSkyLight << 20), values);
            addInterpolationTuning(values);
            return values;
        }

        @Override void despawn(Player player) { player.sendPacket(element.cachedDespawnPacket, false); }
    }

    // ---- TextDisplay ----

    private static final class TextDisplayCell extends Cell {
        private final TextDisplayBlockEntityElement element;
        private final UUID uuid = UUID.randomUUID();

        TextDisplayCell(TextDisplayBlockEntityElement element, BlockPos local) {
            super(local);
            this.element = element;
        }

        @Override int[] entityIds() { return new int[]{element.entityId}; }
        @Override Vector3f offset() { return element.config.position(); }
        @Override float baseYaw() { return element.config.yRot(); }
        @Override float basePitch() { return element.config.xRot(); }

        @Override
        void spawn(Player player, Vec3 pos, float yawDeg) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(element.entityId, uuid,
                    pos.x, pos.y, pos.z, element.config.xRot(), yawDeg, EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(element.entityId, metadata(player));
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 pos, float yawDeg) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    element.entityId, pos.x, pos.y, pos.z, yawDeg, element.config.xRot(), false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                    element.entityId, metadata(player)), false);
        }

        private List<Object> metadata(Player player) {
            List<Object> values = new ArrayList<>(element.config.metadataValues(player));
            ContraptionRenderScale.applyTo(values, lastScale, lastRotation);
            DisplayData.BrightnessOverride.addEntityData((lastBlockLight << 4) | (lastSkyLight << 20), values);
            addInterpolationTuning(values);
            return values;
        }

        @Override void despawn(Player player) { player.sendPacket(element.cachedDespawnPacket, false); }
    }

    // ---- ArmorStand (position-only, no Display transform metadata) ----

    private static final class ArmorStandCell extends Cell {
        private final ArmorStandBlockEntityElement element;
        private final UUID uuid = UUID.randomUUID();

        ArmorStandCell(ArmorStandBlockEntityElement element, BlockPos local) {
            super(local);
            this.element = element;
        }

        @Override int[] entityIds() { return new int[]{element.entityId}; }
        @Override Vector3f offset() { return element.config.position(); }
        @Override float baseYaw() { return element.config.yRot(); }
        @Override float basePitch() { return element.config.xRot(); }
        // ArmorStand has no Display transform metadata — no tilt lever.
        @Override Quaternionf modelRotation(Quaternionf tiltQ) { return null; }

        @Override
        void spawn(Player player, Vec3 pos, float yawDeg) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(element.entityId, uuid,
                    pos.x, pos.y, pos.z, element.config.xRot(), yawDeg, EntityType.ARMOR_STAND, 0, Vec3.ZERO, yawDeg);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                    element.entityId, element.config.metadataValues(player));
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 pos, float yawDeg) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    element.entityId, pos.x, pos.y, pos.z, yawDeg, element.config.xRot(), false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                    element.entityId, element.config.metadataValues(player)), false);
        }

        @Override void despawn(Player player) { player.sendPacket(element.cachedDespawnPacket, false); }
    }

    // ---- ItemRide (ITEM_DISPLAY mount + real ITEM entity) ----

    private static final class ItemRideCell extends Cell {
        private final ItemBlockEntityElement element;
        private final UUID uuid1 = UUID.randomUUID();
        private final UUID uuid2 = UUID.randomUUID();

        ItemRideCell(ItemBlockEntityElement element, BlockPos local) {
            super(local);
            this.element = element;
        }

        @Override int[] entityIds() { return new int[]{element.entityId1, element.entityId2}; }
        @Override Vector3f offset() { return element.config.position(); }
        @Override float baseYaw() { return 0f; }
        @Override float basePitch() { return 0f; }
        // Real ITEM entity honours no Display transform — no tilt lever.
        @Override Quaternionf modelRotation(Quaternionf tiltQ) { return null; }

        @Override
        void spawn(Player player, Vec3 pos, float yawDeg) {
            Object add1 = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(element.entityId1, uuid1,
                    pos.x, pos.y, pos.z, 0f, 0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0);
            List<Object> displayValues = new ArrayList<>();
            addInterpolationTuning(displayValues);
            Object data1 = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(element.entityId1, displayValues);
            Object add2 = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(element.entityId2, uuid2,
                    pos.x, pos.y, pos.z, 0f, 0f, EntityType.ITEM, 0, Vec3.ZERO, 0);
            Object ride = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(element.entityId2,
                    element.config.metadataValues(player, element.tintSource));
            player.sendPackets(List.of(add1, data1, add2, element.cachedRidePacket, ride), false);
        }

        @Override
        void updatePosition(Player player, Vec3 pos, float yawDeg) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    element.entityId1, pos.x, pos.y, pos.z, 0f, 0f, false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(element.entityId2,
                    element.config.metadataValues(player, element.tintSource)), false);
        }

        @Override void despawn(Player player) { player.sendPacket(element.cachedDespawnPacket, false); }
    }
}
