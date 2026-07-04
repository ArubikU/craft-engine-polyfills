package dev.arubik.craftengine.contraption.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.joml.Vector3f;

import dev.arubik.craftengine.contraption.level.ContraptionLevel;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.block.entity.renderer.constant.ArmorStandBlockEntityElement;
import net.momirealms.craftengine.bukkit.block.entity.renderer.constant.ItemBlockEntityElement;
import net.momirealms.craftengine.bukkit.block.entity.renderer.constant.ItemDisplayBlockEntityElement;
import net.momirealms.craftengine.bukkit.block.entity.renderer.constant.TextDisplayBlockEntityElement;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.render.BlockEntityRenderer;
import net.momirealms.craftengine.core.block.entity.render.element.BlockEntityElement;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.world.CEWorld;

/**
 * Mirrors CraftEngine's OWN built-in block-entity {@code entity-renderer} elements
 * (config-driven, e.g. {@code entity-renderer: item_display}) for a block sitting inside a
 * {@link ContraptionLevel}. CraftEngine's built-in dispatch for these
 * ({@code BlockEntity#updateConstantRenderers()}, {@code CEChunk#spawnBlockEntities}/
 * {@code despawnBlockEntities}) is entirely driven by real per-player chunk-tracking —
 * confirmed by reading CraftEngine 26.6.2's own sources — which is always empty for a
 * {@link ContraptionLevel} (see that class's "Known limitation" javadoc), so it never fires
 * for a block living in one. Rather than accept that as unfixable, this class drives the SAME
 * element objects ourselves: {@code BlockEntityElement}'s {@code show/hide/update(Player)} are
 * public, and the 4 built-in "constant" element types (item display, text display, armor
 * stand, floating item) each expose their config (position offset, rotation, and a
 * {@code metadataValues(Player, ...)} builder we reuse verbatim) as public fields — enough to
 * reconstruct a spawn packet at the bearing's REAL translated position instead of the cached
 * one CraftEngine baked at the block's local (fake) {@link ContraptionLevel} coordinates.
 *
 * <p>Scope/caveat: these are CraftEngine's own {@code bukkit.block.entity.renderer.constant}
 * implementation classes, not a documented "public API" package — a working, honest choice
 * given there is no supported extension point (see {@link ContraptionLevel}'s javadoc), but
 * one that can break across a CraftEngine version bump more easily than this project's other
 * NMS usage. Only the 4 "constant" element types are handled; CraftEngine's "dynamic" element
 * types (drawers, item frames, dynamic display items) are NOT mirrored — best-effort, matches
 * this project's stated scope for entity mirroring elsewhere. The armor-stand mirror does NOT
 * send the held-item equipment packet (no equipment-packet constructor wired in {@link MNms}
 * yet) — everything else (position, rotation, base metadata) mirrors correctly.
 */
public final class ContraptionBlockEntityElementMirror {

    private final Map<Long, List<Cell>> byBlock = new HashMap<>();

    /**
     * Last real ambient (block, sky) light pair read at the bearing's real-world block position —
     * same read-once-per-render-call / shared-across-every-cell convention
     * {@link ContraptionDisplaySwarm#updateAmbientLight} and {@link ContraptionFurnitureSwarm}'s
     * own copy of it already use (2026-07-02 follow-up — "las cell de los bloques que usan entity
     * render item render deben recibir iluminacion tambien"): this mirror's Display-backed cells
     * ({@link ItemDisplayCell}, {@link TextDisplayCell}) never set a
     * {@code DisplayData.BrightnessOverride} at all before this fix — same missing-field bug the
     * furniture swarm had, now fixed here too for CraftEngine's own {@code entity-renderer}-driven
     * block visuals (shulker-box-style item-texture models, banner patterns, skulls, etc.).
     * {@link ItemRideCell}'s {@code entityId2} is deliberately left untouched — it's spawned as a
     * real {@code EntityType.ITEM}, not a {@code Display}, and {@code ITEM} entities have no
     * {@code BrightnessOverride} metadata field to set in the first place (that field only exists
     * on the {@code Display} entity family) — its lighting is whatever a real dropped-item entity
     * gets from the client by default, unrelated to this bug. Kept as its own independent field pair
     * rather than a shared helper with the other two swarms, for the same reason: these are three
     * otherwise-independent classes that may be touched by different
     * agents concurrently, and duplicating this ~10-line light read is lower-risk than coupling them.
     */
    private int lastAmbientBlockLight = 15;
    private int lastAmbientSkyLight = 15;

    /**
     * (Re)builds from every captured block's CraftEngine renderer state (if loaded). Two
     * SEPARATE CraftEngine renderer mechanisms exist and both need mirroring:
     * <ul>
     * <li>{@link BlockEntity#renderer()} — elements gathered from a real
     * {@code BlockEntityController} (e.g. a machine's controller-driven display). Built ONCE
     * at {@code BlockEntity} construction (see CraftEngine's {@code BlockEntity} constructor)
     * and never rebuilt on a later blockstate/appearance change.</li>
     * <li>{@code CEChunk#getConstantBlockEntityRenderer(pos)} — the {@code entity-renderer:}
     * YAML config path (e.g. {@code entity-renderer: { item: cml:fbt_single_n } }), used by
     * blocks like {@code cml:fluid_block_tank}/{@code copper_tank} that share ONE vanilla
     * blockstate across many CraftEngine appearances (distinguished by custom-block
     * properties, not the vanilla state). This is populated per-position directly by
     * {@code WorldStorageInjector#compareAndUpdateBlockState} on every {@code setBlock} —
     * completely independent of any {@code BlockEntityController} — and is what actually
     * updates when the appearance changes (bottom/top/facing here). Missing this path was why
     * a shared-blockstate multi-appearance block (fluid_block_tank, copper_tank, ...) never
     * rendered its wall/shell visual inside a contraption: only the (irrelevant, and for these
     * blocks usually null) {@link BlockEntity#renderer()} was ever mirrored.</li>
     * </ul>
     */
    public void rebuild(ContraptionLevel level, List<Player> viewers) {
        if (level == null) {
            for (List<Cell> cells : byBlock.values()) {
                for (Cell cell : cells) {
                    cell.despawnAll(viewers);
                }
            }
            byBlock.clear();
            return;
        }
        Set<net.minecraft.core.BlockPos> wanted = level.localPositions();
        // Only despawn+drop positions that stopped holding a captured block at all — an
        // already-tracked position is left completely untouched (same rationale as
        // ContraptionDisplaySwarm/ContraptionHitboxSwarm's rebuild: reuse existing entities,
        // never despawn/recreate on every tick just because rebuild ran again).
        java.util.Iterator<Map.Entry<Long, List<Cell>>> it = byBlock.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, List<Cell>> e = it.next();
            if (!wanted.contains(net.minecraft.core.BlockPos.of(e.getKey()))) {
                for (Cell cell : e.getValue()) {
                    cell.despawnAll(viewers);
                }
                it.remove();
            }
        }
        CEWorld ceWorld = ceWorldOf(level);
        if (ceWorld == null) {
            return;
        }
        for (net.minecraft.core.BlockPos local : wanted) {
            if (byBlock.containsKey(local.asLong())) {
                continue; // already tracked — reuse existing Cells as-is, don't re-wrap/re-spawn
            }
            net.momirealms.craftengine.core.world.BlockPos cePos =
                    new net.momirealms.craftengine.core.world.BlockPos(local.getX(), local.getY(), local.getZ());
            List<Cell> cells = new ArrayList<>();

            // Path 1: entity-renderer YAML config (constant renderer), per-position in CEChunk —
            // the shared-blockstate multi-appearance pattern (fluid_block_tank, copper_tank, ...).
            try {
                net.momirealms.craftengine.core.world.chunk.CEChunk chunk =
                        ceWorld.getChunkAtIfLoaded(cePos);
                if (chunk != null) {
                    net.momirealms.craftengine.core.block.entity.render.ConstantBlockEntityRenderer constant =
                            chunk.getConstantBlockEntityRenderer(cePos);
                    if (constant != null) {
                        for (BlockEntityElement element : constant.elements()) {
                            Cell cell = wrap(element, local);
                            if (cell != null) {
                                cells.add(cell);
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                // best-effort — fall through to path 2 regardless
            }

            // Path 2: real BlockEntityController-driven renderer (machines, ...).
            try {
                BlockEntity be = ceWorld.getBlockEntityAtIfLoaded(cePos, false);
                if (be != null) {
                    BlockEntityRenderer renderer = be.renderer();
                    if (renderer != null) {
                        for (BlockEntityElement element : renderer.elements()) {
                            Cell cell = wrap(element, local);
                            if (cell != null) {
                                cells.add(cell);
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                // best-effort
            }

            // Path 3: BetterModel-driven visual owned directly by the block entity (e.g. the
            // crusher's "on" animation model) — NOT part of CraftEngine's BlockEntityElement
            // system at all, so paths 1/2 above never see it. See BetterModelCell javadoc.
            try {
                BlockEntity be = ceWorld.getBlockEntityAtIfLoaded(cePos, false);
                if (be != null && be.controller instanceof dev.arubik.craftengine.machine.render.BetterModelDriven driven) {
                    cells.add(new BetterModelCell(driven.betterModelRenderer(), local));
                }
            } catch (Throwable t) {
                // best-effort — BetterModel absent, or renderer not shown yet
            }

            if (!cells.isEmpty()) {
                byBlock.put(local.asLong(), cells);
            }
        }
    }

    /**
     * Render every mirrored element for {@code viewers} at the bearing's current transform.
     *
     * <p><b>Ambient lighting (2026-07-02 follow-up — "las cell de los bloques que usan entity
     * render item render deben recibir iluminacion tambien").</b> {@code realLevel} (the
     * contraption's real {@code ServerLevel}, null-tolerant — falls back to full-bright, matching
     * this class's pre-fix visual) is read ONCE per call at the bearing's real-world block position
     * and shared by every cell here — same mechanism as {@link ContraptionDisplaySwarm}/
     * {@link ContraptionFurnitureSwarm}.
     *
     * <p><b>Internal light emitters (2026-07-02 live-test follow-up — "la antorcha no ilumina...
     * al block entity renderer de craftengine").</b> Each cell's final block-light is now ALSO
     * boosted by {@link ContraptionLightEmitters#withEmitterFalloff}, scanning the {@code level}'s
     * full captured block set (e.g. a captured torch elsewhere in the contraption) for the same
     * falloff {@link ContraptionDisplaySwarm} already applies among its own cells — see that
     * helper's javadoc for the full root-cause writeup on why this was missing here.
     */
    public void render(List<Player> viewers, ContraptionLevel level, ServerLevel realLevel) {
        if (level == null) {
            return;
        }
        updateAmbientLight(level, realLevel);
        for (List<Cell> cells : byBlock.values()) {
            for (Cell cell : cells) {
                int blockLight = ContraptionLightEmitters.withEmitterFalloff(level, cell.local, lastAmbientBlockLight);
                cell.render(viewers, level, blockLight, lastAmbientSkyLight);
            }
        }
    }

    /** Back-compat overload for any caller without a real-world light reference — falls back to full-bright. */
    public void render(List<Player> viewers, ContraptionLevel level) {
        render(viewers, level, null);
    }

    /**
     * Re-reads real block/sky light at the bearing's current real-world block position — same
     * mechanism/source as {@link ContraptionDisplaySwarm#updateAmbientLight}. {@code realLevel}
     * null (no live Bukkit world, e.g. unit tests) falls back to full-bright (15/15).
     */
    private void updateAmbientLight(ContraptionLevel level, ServerLevel realLevel) {
        if (realLevel == null) {
            lastAmbientBlockLight = 15;
            lastAmbientSkyLight = 15;
            return;
        }
        try {
            Vec3 bearingWorldPos = level.realWorldPositionOf(Vec3.ZERO);
            BlockPos pos = BlockPos.containing(bearingWorldPos.x, bearingWorldPos.y, bearingWorldPos.z);
            lastAmbientBlockLight = realLevel.getLightEngine().getLayerListener(LightLayer.BLOCK).getLightValue(pos);
            lastAmbientSkyLight = realLevel.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(pos);
        } catch (Throwable ignored) {
            // Defensive: never let a light-engine read failure blank the whole mirror's rendering.
            lastAmbientBlockLight = 15;
            lastAmbientSkyLight = 15;
        }
    }

    public void despawnAll(List<Player> viewers) {
        for (List<Cell> cells : byBlock.values()) {
            for (Cell cell : cells) {
                cell.despawnAll(viewers);
            }
        }
        byBlock.clear();
    }

    public int cellCount() {
        int n = 0;
        for (List<Cell> cells : byBlock.values()) {
            n += cells.size();
        }
        return n;
    }

    private static CEWorld ceWorldOf(ContraptionLevel level) {
        try {
            org.bukkit.World bukkitWorld = level.getWorld();
            return CraftEngine.instance().worldManager().getWorld(bukkitWorld.getUID());
        } catch (Throwable t) {
            return null;
        }
    }

    private static Cell wrap(BlockEntityElement element, net.minecraft.core.BlockPos local) {
        if (element instanceof ItemDisplayBlockEntityElement e) {
            return new ItemDisplayCell(e, local);
        }
        if (element instanceof TextDisplayBlockEntityElement e) {
            return new TextDisplayCell(e, local);
        }
        if (element instanceof ArmorStandBlockEntityElement e) {
            return new ArmorStandCell(e, local);
        }
        if (element instanceof ItemBlockEntityElement e) {
            return new ItemRideCell(e, local);
        }
        return null; // dynamic/unknown element types: not mirrored, best-effort scope
    }

    /**
     * Interpolation-window metadata every {@code Display}-backed cell here must append to its own
     * spawn/metadata payload (2026-07-02 follow-up — "el tank sigue jitereando" persisted even
     * after {@code FluidDisplay}/{@code ContraptionDisplaySwarm}/{@code ContraptionFurnitureSwarm}
     * all got this same fix). ROOT CAUSE: the tank's actual BLOCK MODEL — every one of its 60
     * {@code cml:fluid_block_tank} appearances is rendered via {@code entity-renderer: { item:
     * cml:fbt_* } }, i.e. an {@code ItemDisplayBlockEntityElement} mirrored here as an
     * {@link ItemDisplayCell} — is a client-rendered {@code Display} entity whose renderer only
     * re-interpolates position/rotation over {@code PosRotInterpolationDuration} ticks (metadata
     * read once from its {@code ClientboundSetEntityDataPacket}). This class's {@code spawn}
     * methods never appended that field at all: {@link ItemDisplayCell#metadata}/
     * {@link TextDisplayCell#metadata} only built the element's own authored appearance values +
     * {@code BrightnessOverride}, so with the duration left at its unset default (0 ticks) the
     * client had no window to animate through and instead re-snapped instantly to every fresh
     * {@code ClientboundEntityPositionSyncPacket} — sent unconditionally every tick by
     * {@link Cell#render} below (unlike {@code ContraptionDisplaySwarm}, which only resends when
     * the bearing actually {@code moved}) — which reads as constant jitter on a moving contraption
     * instead of a smooth glide. {@code ArmorStandCell} doesn't need this (a vanilla
     * {@code ArmorStand} uses the normal entity movement-lerp path, not metadata-driven
     * interpolation); {@code ItemRideCell}'s {@code entityId1} (the {@code ITEM_DISPLAY} half) DOES
     * need it for the same reason as {@link ItemDisplayCell}.
     */
    private static void addInterpolationTuning(List<Object> values) {
        net.momirealms.craftengine.bukkit.entity.data.DisplayData.PosRotInterpolationDuration
                .addEntityData(2, values);
        net.momirealms.craftengine.bukkit.entity.data.DisplayData.TransformationInterpolationDuration
                .addEntityData(2, values);
    }

    /** One mirrored CraftEngine element, redirected to the bearing's real-world transform. */
    private abstract static class Cell {
        final net.minecraft.core.BlockPos local;
        final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();

        // Live-tracked brightness (2026-07-02 follow-up — see this class's own "Ambient lighting"
        // javadoc on #render/#updateAmbientLight). -1 sentinel so the very first #render call
        // always counts as a change and sends real metadata instead of reusing spawn()'s values.
        // Unused by non-Display cells (ArmorStandCell, BetterModelCell) — see their own no-op
        // updateMetadata bodies.
        private int blockLight = -1;
        private int skyLight = -1;

        Cell(net.minecraft.core.BlockPos local) {
            this.local = local;
        }

        abstract void spawn(Player player, Vec3 realPos, float yawDegrees);

        abstract void updatePosition(Player player, Vec3 realPos, float yawDegrees);

        abstract void updateMetadata(Player player);

        abstract void despawn(Player player);

        abstract Vector3f offset();

        abstract float baseYaw();

        int blockLight() {
            return blockLight;
        }

        int skyLight() {
            return skyLight;
        }

        void render(List<Player> viewers, ContraptionLevel level, int blockLight, int skyLight) {
            Vector3f off = offset();
            Vec3 localPos = new Vec3(local.getX() + off.x, local.getY() + off.y, local.getZ() + off.z);
            Vec3 realPos = level.realWorldPositionOf(localPos);
            float yawDegrees = baseYaw() + (float) Math.toDegrees(level.realYawRadians());
            boolean lightChanged = blockLight != this.blockLight || skyLight != this.skyLight;
            this.blockLight = blockLight;
            this.skyLight = skyLight;
            Set<UUID> current = new HashSet<>();
            for (Player p : viewers) {
                UUID id = uuidOf(p);
                if (id == null) {
                    continue;
                }
                current.add(id);
                if (shownTo.add(id)) {
                    spawn(p, realPos, yawDegrees);
                } else {
                    updatePosition(p, realPos, yawDegrees);
                    if (lightChanged) {
                        updateMetadata(p);
                    }
                }
            }
            shownTo.retainAll(current);
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) {
                despawn(p);
            }
            shownTo.clear();
        }

        static UUID uuidOf(Player player) {
            Object pp = player.platformPlayer();
            return pp instanceof org.bukkit.entity.Player b ? b.getUniqueId() : null;
        }
    }

    private static final class ItemDisplayCell extends Cell {
        private final ItemDisplayBlockEntityElement element;
        private final UUID uuid = UUID.randomUUID();

        ItemDisplayCell(ItemDisplayBlockEntityElement element, net.minecraft.core.BlockPos local) {
            super(local);
            this.element = element;
        }

        @Override
        Vector3f offset() {
            return element.config.position();
        }

        @Override
        float baseYaw() {
            return element.config.yRot();
        }

        @Override
        void spawn(Player player, Vec3 realPos, float yawDegrees) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(element.entityId, uuid,
                    realPos.x, realPos.y, realPos.z, element.config.xRot(), yawDegrees,
                    EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(element.entityId, metadata(player));
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 realPos, float yawDegrees) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    element.entityId, realPos.x, realPos.y, realPos.z, yawDegrees, element.config.xRot(), false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(element.entityId, metadata(player)), false);
        }

        /**
         * CraftEngine's own authored appearance metadata + this cell's live ambient
         * {@code BrightnessOverride} (see class javadoc, "Ambient lighting" — 2026-07-02 follow-up
         * fix; this field was never set at all before).
         */
        private List<Object> metadata(Player player) {
            List<Object> values = new ArrayList<>(element.config.metadataValues(player, element.tintSource));
            net.momirealms.craftengine.bukkit.entity.data.DisplayData.BrightnessOverride
                    .addEntityData((blockLight() << 4) | (skyLight() << 20), values);
            addInterpolationTuning(values);
            return values;
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(element.cachedDespawnPacket, false);
        }
    }

    private static final class TextDisplayCell extends Cell {
        private final TextDisplayBlockEntityElement element;
        private final UUID uuid = UUID.randomUUID();

        TextDisplayCell(TextDisplayBlockEntityElement element, net.minecraft.core.BlockPos local) {
            super(local);
            this.element = element;
        }

        @Override
        Vector3f offset() {
            return element.config.position();
        }

        @Override
        float baseYaw() {
            return element.config.yRot();
        }

        @Override
        void spawn(Player player, Vec3 realPos, float yawDegrees) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(element.entityId, uuid,
                    realPos.x, realPos.y, realPos.z, element.config.xRot(), yawDegrees,
                    EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(element.entityId, metadata(player));
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 realPos, float yawDegrees) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    element.entityId, realPos.x, realPos.y, realPos.z, yawDegrees, element.config.xRot(), false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(element.entityId, metadata(player)), false);
        }

        /**
         * CraftEngine's own authored appearance metadata + this cell's live ambient
         * {@code BrightnessOverride} (see class javadoc, "Ambient lighting" — 2026-07-02 follow-up
         * fix; this field was never set at all before).
         */
        private List<Object> metadata(Player player) {
            List<Object> values = new ArrayList<>(element.config.metadataValues(player));
            net.momirealms.craftengine.bukkit.entity.data.DisplayData.BrightnessOverride
                    .addEntityData((blockLight() << 4) | (skyLight() << 20), values);
            addInterpolationTuning(values);
            return values;
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(element.cachedDespawnPacket, false);
        }
    }

    /** Position/metadata only — no held-item equipment packet (see class javadoc). */
    private static final class ArmorStandCell extends Cell {
        private final ArmorStandBlockEntityElement element;
        private final UUID uuid = UUID.randomUUID();

        ArmorStandCell(ArmorStandBlockEntityElement element, net.minecraft.core.BlockPos local) {
            super(local);
            this.element = element;
        }

        @Override
        Vector3f offset() {
            return element.config.position();
        }

        @Override
        float baseYaw() {
            return element.config.yRot();
        }

        @Override
        void spawn(Player player, Vec3 realPos, float yawDegrees) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(element.entityId, uuid,
                    realPos.x, realPos.y, realPos.z, element.config.xRot(), yawDegrees,
                    EntityType.ARMOR_STAND, 0, Vec3.ZERO, yawDegrees);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(element.entityId,
                    element.config.metadataValues(player));
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 realPos, float yawDegrees) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    element.entityId, realPos.x, realPos.y, realPos.z, yawDegrees, element.config.xRot(), false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(element.entityId,
                    element.config.metadataValues(player)), false);
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(element.cachedDespawnPacket, false);
        }
    }

    /** Floating item (item_display riding a real item entity) — two entity ids, {@code entityId2} carries the data. */
    private static final class ItemRideCell extends Cell {
        private final ItemBlockEntityElement element;
        private final UUID uuid1 = UUID.randomUUID();
        private final UUID uuid2 = UUID.randomUUID();

        ItemRideCell(ItemBlockEntityElement element, net.minecraft.core.BlockPos local) {
            super(local);
            this.element = element;
        }

        @Override
        Vector3f offset() {
            return element.config.position();
        }

        @Override
        float baseYaw() {
            return 0f;
        }

        @Override
        void spawn(Player player, Vec3 realPos, float yawDegrees) {
            Object add1 = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(element.entityId1, uuid1,
                    realPos.x, realPos.y, realPos.z, 0f, 0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0);
            // entityId1 is a Display (ITEM_DISPLAY) and gets its position re-synced every tick by
            // updatePosition below — same jitter root cause as ItemDisplayCell (see
            // addInterpolationTuning's javadoc): without this metadata packet it has no
            // interpolation window at all and re-snaps every tick instead of gliding.
            List<Object> display1Values = new ArrayList<>();
            addInterpolationTuning(display1Values);
            Object data1 = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(element.entityId1, display1Values);
            Object add2 = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(element.entityId2, uuid2,
                    realPos.x, realPos.y, realPos.z, 0f, 0f, EntityType.ITEM, 0, Vec3.ZERO, 0);
            Object ride = dev.arubik.craftengine.util.MNms.INSTANCE
                    .constructor$ClientboundSetEntityDataPacket(element.entityId2,
                            element.config.metadataValues(player, element.tintSource));
            player.sendPackets(List.of(add1, data1, add2, element.cachedRidePacket, ride), false);
        }

        @Override
        void updatePosition(Player player, Vec3 realPos, float yawDegrees) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    element.entityId1, realPos.x, realPos.y, realPos.z, 0f, 0f, false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(element.entityId2,
                    element.config.metadataValues(player, element.tintSource)), false);
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(element.cachedDespawnPacket, false);
        }
    }

    /**
     * Mirrors a BetterModel-driven block visual (e.g. the crusher's "on" animation model — see
     * {@code CrusherBlockEntity}/{@code BetterModelMachineRenderer}) into the bearing's real
     * transform every tick. This is a fundamentally different mechanism from every other
     * {@link Cell}: BetterModel owns its OWN player-tracking (a {@code DummyTracker}'s
     * {@code spawn(Player)}/{@code isSpawned(Player)}), so this class drives a SEPARATE tracker
     * instance rather than hand-building spawn/despawn packets like the other cells.
     *
     * <p>The source {@code BetterModelMachineRenderer} owned by the block entity keeps ticking
     * completely independently in whatever world the block entity actually lives in — for a
     * captured block that is the hidden {@link ContraptionLevel}, where it spawns/moves its OWN
     * tracker too, but harmlessly: nothing there is ever shown to a REAL player (the
     * {@code ContraptionLevel} has no real viewers — see that class's javadoc), so it never
     * visually "stays stuck" anywhere. This mirror's tracker is the only one real players ever
     * see, and it is closed the moment the position stops being captured (see
     * {@link #despawnAll}) or BetterModel is not currently showing anything for this renderer.
     */
    private static final class BetterModelCell extends Cell {
        private final dev.arubik.craftengine.machine.render.BetterModelMachineRenderer source;
        private kr.toxicity.model.api.tracker.DummyTracker mirrorTracker;
        private String mirrorAnim;

        BetterModelCell(dev.arubik.craftengine.machine.render.BetterModelMachineRenderer source,
                net.minecraft.core.BlockPos local) {
            super(local);
            this.source = source;
        }

        @Override
        Vector3f offset() {
            return new Vector3f(0.5f, 0f, 0.5f); // block-center; BetterModel models are placed like the source renderer (block feet-center)
        }

        @Override
        float baseYaw() {
            return 0f; // source renderer already bakes facing into its own yaw; nothing extra to add here
        }

        /** Not used — {@link #render} is overridden below to drive the DummyTracker directly. */
        @Override
        void spawn(Player player, Vec3 realPos, float yawDegrees) {
        }

        @Override
        void updatePosition(Player player, Vec3 realPos, float yawDegrees) {
        }

        @Override
        void updateMetadata(Player player) {
        }

        @Override
        void despawn(Player player) {
            Object pp = player.platformPlayer();
            if (pp instanceof org.bukkit.entity.Player b && mirrorTracker != null) {
                try {
                    mirrorTracker.remove(kr.toxicity.model.api.bukkit.platform.BukkitAdapter.adapt(b));
                } catch (Throwable ignored) {
                }
            }
        }

        @Override
        void render(List<Player> viewers, ContraptionLevel level, int blockLight, int skyLight) {
            if (!dev.arubik.craftengine.machine.render.BetterModelMachineRenderer.available()) {
                closeTracker();
                return;
            }
            if (!source.isShown()) {
                // Source renderer isn't currently displaying anything (e.g. crusher fully idle) —
                // keep the mirror closed too so it disappears in lockstep with the real behaviour.
                closeTracker();
                return;
            }

            org.bukkit.World bukkitWorld = level.getWorld();
            Vec3 localCenter = new Vec3(local.getX() + 0.5, local.getY(), local.getZ() + 0.5);
            Vec3 realPos = level.realWorldPositionOf(localCenter);
            float yawDegrees = (float) Math.toDegrees(level.realYawRadians());
            org.bukkit.Location loc = new org.bukkit.Location(bukkitWorld, realPos.x, realPos.y, realPos.z, yawDegrees, 0f);

            try {
                if (mirrorTracker == null || mirrorTracker.isClosed()) {
                    mirrorAnim = null;
                    mirrorTracker = kr.toxicity.model.api.BetterModel.model(source.modelId())
                            .map(r -> r.create(kr.toxicity.model.api.bukkit.platform.BukkitAdapter.adapt(loc)))
                            .orElse(null);
                } else {
                    mirrorTracker.location(kr.toxicity.model.api.bukkit.platform.BukkitAdapter.adapt(loc));
                }
            } catch (Throwable ignored) {
                mirrorTracker = null;
                return;
            }
            if (mirrorTracker == null) {
                return;
            }

            // Mirror the source renderer's currently-looping animation onto our own tracker.
            String wantAnim = source.currentAnimation();
            if (wantAnim != null && !wantAnim.equals(mirrorAnim)) {
                try {
                    mirrorTracker.animate(wantAnim,
                            new kr.toxicity.model.api.animation.AnimationModifier(0, 0,
                                    kr.toxicity.model.api.animation.AnimationIterator.Type.LOOP, () -> 1f));
                    mirrorAnim = wantAnim;
                } catch (Throwable ignored) {
                }
            } else if (wantAnim == null && mirrorAnim != null) {
                try {
                    mirrorTracker.stopAnimation(mirrorAnim);
                } catch (Throwable ignored) {
                }
                mirrorAnim = null;
            }

            Set<UUID> current = new HashSet<>();
            for (Player p : viewers) {
                UUID id = uuidOf(p);
                if (id == null) {
                    continue;
                }
                current.add(id);
                Object pp = p.platformPlayer();
                if (!(pp instanceof org.bukkit.entity.Player b)) {
                    continue;
                }
                try {
                    var bp = kr.toxicity.model.api.bukkit.platform.BukkitAdapter.adapt(b);
                    if (!mirrorTracker.isSpawned(bp)) {
                        mirrorTracker.spawn(bp);
                    }
                } catch (Throwable ignored) {
                }
                shownTo.add(id);
            }
            shownTo.retainAll(current);
        }

        private void closeTracker() {
            if (mirrorTracker == null) {
                return;
            }
            try {
                mirrorTracker.close();
            } catch (Throwable ignored) {
            }
            mirrorTracker = null;
            mirrorAnim = null;
        }

        @Override
        void despawnAll(List<Player> viewers) {
            closeTracker();
            shownTo.clear();
        }
    }
}
