package dev.arubik.craftengine.contraption.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.joml.Quaternionf;
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
     * The block state each tracked position was wrapped from — see {@link #rebuild}.
     *
     * <p>A mirrored element is built ONCE from whatever the block looked like at that instant. Without
     * this, a position that already had elements was skipped forever, so a block whose appearance changed
     * under it (a {@code fluid_block_tank} whose level or topology moved it to a different appearance)
     * kept rendering the elements it was first wrapped with. The blocks updated correctly inside the
     * contraption level and only the projection was stale, which is what "se actualizan en el mundo
     * contraption pero su update no se muestra en el render" describes.
     *
     * <p>Block states are interned, so this comparison is a reference check in practice and costs nothing
     * per tick. It mirrors what {@code ContraptionDisplaySwarm} already does for its own cells via
     * {@code updateIfChanged}.
     */
    private final Map<Long, net.minecraft.world.level.block.state.BlockState> wrappedFrom = new HashMap<>();

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
            wrappedFrom.clear();
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
                wrappedFrom.remove(e.getKey());
                it.remove();
            }
        }
        CEWorld ceWorld = ceWorldOf(level);
        if (ceWorld == null) {
            return;
        }
        for (net.minecraft.core.BlockPos local : wanted) {
            net.minecraft.world.level.block.state.BlockState nowState = level.getBlockState(local);
            List<Cell> tracked = byBlock.get(local.asLong());
            if (tracked != null) {
                if (nowState.equals(wrappedFrom.get(local.asLong()))) {
                    continue; // unchanged — reuse existing Cells as-is, don't re-wrap/re-spawn
                }
                // The block changed under a position that already had elements. Those elements describe
                // the block it USED to be, so they are dropped and re-wrapped rather than left stale.
                for (Cell cell : tracked) {
                    cell.despawnAll(viewers);
                }
                byBlock.remove(local.asLong());
                wrappedFrom.remove(local.asLong());
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
                wrappedFrom.put(local.asLong(), nowState);
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
     *
     * <p><b>Uniform scale (2026-07-16 fix — a scaled contraption's entity-renderer visuals stayed at
     * size 1).</b> {@code scale} is the contraption's live {@code ContraptionState#scale()} (roadmap
     * item #9), threaded straight from {@code ContraptionEntity#render} exactly like it already is
     * into {@link ContraptionDisplaySwarm}/{@code ContraptionHitboxSwarm}. It affects ONLY each
     * mirrored element's SIZE here, not its position — see {@link Cell#render}'s own javadoc for the
     * (verified) reason positions were already correct at any scale.
     */
    public void render(List<Player> viewers, ContraptionLevel level, ServerLevel realLevel, double scale) {
        if (level == null) {
            return;
        }
        updateAmbientLight(level, realLevel);
        for (List<Cell> cells : byBlock.values()) {
            for (Cell cell : cells) {
                int blockLight = ContraptionLightEmitters.withEmitterFalloff(level, cell.local, lastAmbientBlockLight);
                cell.render(viewers, level, blockLight, lastAmbientSkyLight, scale);
            }
        }
    }

    /** Back-compat scale-1 overload — see the full {@link #render(List, ContraptionLevel, ServerLevel, double)}. */
    public void render(List<Player> viewers, ContraptionLevel level, ServerLevel realLevel) {
        render(viewers, level, realLevel, 1.0);
    }

    /** Back-compat overload for any caller without a real-world light reference — falls back to full-bright. */
    public void render(List<Player> viewers, ContraptionLevel level) {
        render(viewers, level, null, 1.0);
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
        wrappedFrom.clear();
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

        /**
         * This cell's live uniform SCALE (the contraption's {@code ContraptionState#scale()}, roadmap
         * item #9 — see {@link ContraptionBlockEntityElementMirror#render}'s "Uniform scale" javadoc).
         * {@code 1.0} for any never-scaled body, in which case every {@code metadata} builder here takes
         * {@link ContraptionRenderScale#applyTo}'s immediate-return fast path and emits byte-for-byte the
         * pre-scale packet. A change flags a metadata resend exactly like a brightness change does.
         */
        private double scale = 1.0;

        /**
         * This cell's live model ROTATION — the contraption's tilt re-expressed in this element's own frame
         * (see {@link #modelRotation}), or {@code null} on a never-tilted body, which is the value every
         * existing contraption holds forever. Composed into each Display-backed subclass's {@code metadata}
         * via {@link ContraptionRenderScale}, and a change flags a metadata resend exactly like a brightness
         * or scale change already does — a {@code Display}'s transform is metadata, so a rotation that is
         * never resent is a rotation the client never sees.
         */
        private Quaternionf rotation;

        Cell(net.minecraft.core.BlockPos local) {
            this.local = local;
        }

        abstract void spawn(Player player, Vec3 realPos, float yawDegrees);

        abstract void updatePosition(Player player, Vec3 realPos, float yawDegrees);

        abstract void updateMetadata(Player player);

        abstract void despawn(Player player);

        abstract Vector3f offset();

        abstract float baseYaw();

        /**
         * The element's own authored entity PITCH in degrees ({@code element.config.xRot()}) — the exact
         * value this cell puts in the {@code xRot} slot of its own spawn/position packets. The pitch twin of
         * {@link #baseYaw}, added 2026-07-16 because {@link #modelRotation} needs the element's full
         * authored entity rotation, not just its yaw half. See
         * {@code ContraptionFurnitureSwarm.Cell#basePitch}, which is the same idea for captured furniture.
         */
        abstract float basePitch();

        /**
         * The rotation this cell must compose INTO its CraftEngine-authored {@code Display} transform so its
         * model follows the contraption's live pose, or {@code null} for "nothing to compose" (2026-07-16 —
         * the same "siempre queda recto" gap {@link ContraptionFurnitureSwarm} was reported for; this class
         * had it identically, and the two are fixed the same way).
         *
         * <p>The derivation, the {@code E0⁻¹ · R_tilt · E0} conjugation, and the reason YAW needs no term
         * here are all written out once on {@code ContraptionFurnitureSwarm.Cell#modelRotation} — read that.
         * The only difference is where the pose comes from: this class never receives the bearing pose as
         * arguments, it reads it off the {@code level} (whose {@code realYaw}/{@code realPitch}/
         * {@code realRoll} are pushed by every {@code ContraptionState} setter through
         * {@code ContraptionLevel#setTransform}, so they cannot go stale — the same source
         * {@link Cell#render}'s position/yaw already trust), and the element's tilt-free entity yaw is just
         * {@link #baseYaw} (a captured block has no per-instance placement yaw the way a furniture piece's
         * {@code cf.yawOffsetDegrees()} does).
         */
        Quaternionf modelRotation(ContraptionLevel level) {
            double pitchRadians = level.realPitchRadians();
            double rollRadians = level.realRollRadians();
            if (pitchRadians == 0.0 && rollRadians == 0.0) {
                return null; // never-tilted body — emits the pre-fix metadata exactly
            }
            Quaternionf tilt = new Quaternionf().rotateX((float) pitchRadians).rotateZ((float) rollRadians);
            Quaternionf e0 = new Quaternionf()
                    .rotateY((float) -Math.toRadians(baseYaw()))
                    .rotateX((float) Math.toRadians(basePitch()));
            return e0.invert(new Quaternionf()).mul(tilt).mul(e0); // E0⁻¹ · R_tilt · E0
        }

        int blockLight() {
            return blockLight;
        }

        int skyLight() {
            return skyLight;
        }

        /** This cell's live uniform scale — baked into each Display-backed subclass's {@code metadata}. */
        double scale() {
            return scale;
        }

        /** This cell's live model rotation — composed into each Display-backed subclass's {@code metadata}. */
        Quaternionf rotation() {
            return rotation;
        }

        /**
         * Whether {@code next} differs enough from the last-sent {@link #rotation} to be worth a metadata
         * resend. Same null-transition + {@code 1e-4f} epsilon gate as
         * {@code ContraptionFurnitureSwarm.Cell#rotationChanged} — see there for why an exact-equality gate
         * would be no gate at all on a continuously-tilting body.
         */
        private boolean rotationChanged(Quaternionf next) {
            if (rotation == null || next == null) {
                return rotation != next;
            }
            return !next.equals(rotation, 1e-4f);
        }

        /**
         * <b>POSITION IS ALREADY SCALE-CORRECT — verified, deliberately not re-derived here (2026-07-16).</b>
         * Unlike {@link ContraptionFurnitureSwarm}, which calls the yaw-only
         * {@code ContraptionMath#renderPosition(local, bearing, yaw)} directly and therefore genuinely had
         * to have {@code scale} threaded into its position math, this class routes every position through
         * {@code level.realWorldPositionOf(localPos)} — which already forwards the level's FULL live
         * transform ({@code realYaw}, {@code realPitch}, {@code realRoll} and {@code realScale}) into
         * {@code ContraptionMath#renderPosition}'s scale-about-pivot projection. And {@code realScale} is
         * always in lock-step with {@code ContraptionState#scale()}: every state setter that can move or
         * resize the body ({@code setPosition}/{@code setYawRadians}/{@code setPitchRadians}/
         * {@code setScale}/…) pushes the complete pose through {@code ContraptionLevel#setTransform(x, y,
         * z, yaw, pitch, roll, scale)}, so no path can leave the level's scale stale.
         *
         * <p>Crucially this also already covers the ELEMENT'S OWN local offset, not merely the cell origin:
         * {@code localPos} is {@code local + offset()} (the CraftEngine-authored {@code config.position()})
         * BEFORE the projection, so the whole combined offset-from-pivot is what gets rotated and scaled —
         * exactly the required "the element's own local offset is scaled about the bearing pivot too". What
         * was genuinely missing, and is what {@code scale} is threaded in for, is the element's SIZE.
         */
        void render(List<Player> viewers, ContraptionLevel level, int blockLight, int skyLight, double scale) {
            Vector3f off = offset();
            Vec3 localPos = new Vec3(local.getX() + off.x, local.getY() + off.y, local.getZ() + off.z);
            Vec3 realPos = level.realWorldPositionOf(localPos);
            float yawDegrees = baseYaw() + (float) Math.toDegrees(level.realYawRadians());
            Quaternionf rotation = modelRotation(level);
            boolean metaChanged = blockLight != this.blockLight || skyLight != this.skyLight || scale != this.scale
                    || rotationChanged(rotation);
            this.blockLight = blockLight;
            this.skyLight = skyLight;
            this.scale = scale;
            this.rotation = rotation;
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
                    if (metaChanged) {
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
        float basePitch() {
            return element.config.xRot();
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
         * fix; this field was never set at all before) + the contraption's uniform {@code scale}
         * multiplied into the authored {@code Scale}/{@code Translation} (2026-07-16 fix — see
         * {@link ContraptionRenderScale}) + the contraption's live model {@code rotation} composed into that
         * same authored transform (2026-07-16 tilt fix — see {@link Cell#modelRotation}). At
         * {@code scale == 1.0} with a never-tilted body both steps are an immediate no-op, so this is
         * byte-for-byte the pre-scale packet.
         */
        private List<Object> metadata(Player player) {
            List<Object> values = new ArrayList<>(element.config.metadataValues(player, element.tintSource));
            ContraptionRenderScale.applyTo(values, scale(), rotation());
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
        float basePitch() {
            return element.config.xRot();
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
         * fix; this field was never set at all before) + the contraption's uniform {@code scale}
         * multiplied into the authored {@code Scale}/{@code Translation} (2026-07-16 fix — see
         * {@link ContraptionRenderScale}) + the contraption's live model {@code rotation} composed into that
         * same authored transform (2026-07-16 tilt fix — see {@link Cell#modelRotation}). At
         * {@code scale == 1.0} with a never-tilted body both steps are an immediate no-op, so this is
         * byte-for-byte the pre-scale packet.
         */
        private List<Object> metadata(Player player) {
            List<Object> values = new ArrayList<>(element.config.metadataValues(player));
            ContraptionRenderScale.applyTo(values, scale(), rotation());
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

    /**
     * Position/metadata only — no held-item equipment packet (see class javadoc).
     *
     * <p><b>NOT SCALE-AWARE — no lever exists (2026-07-16, roadmap item #9).</b> This cell's POSITION
     * scales correctly (it goes through {@code level.realWorldPositionOf} like every other cell here —
     * see {@link Cell#render}'s javadoc), but its SIZE cannot follow the contraption's {@code scale}: a
     * vanilla {@code ArmorStand} is a real {@code LivingEntity}, not a {@code Display}, and the
     * {@code Display}-only {@code Scale} transform metadata simply does not exist on it — the protocol
     * exposes exactly one size lever for an armor stand, {@code ArmorStandData}'s {@code Small} flag,
     * which is a BINARY half-size toggle (not a continuous factor) AND is part of the element's own
     * authored appearance, so flipping it would both mis-size every scale other than {@code 0.5} and
     * silently override what the block's config actually authored. Left faithful to the authored config
     * instead: on a scaled contraption an armor-stand-backed block visual sits in the right (scaled)
     * PLACE at its unscaled size. Same limitation, same reason, as {@link ContraptionFurnitureSwarm}'s
     * own {@code ArmorStandCell}.
     *
     * <p><b>YAW-ONLY ORIENTATION — pitch/roll have no lever (2026-07-16 tilt fix).</b> Yaw follows the
     * contraption already ({@link Cell#render} folds {@code level.realYawRadians()} into the body yaw this
     * cell sends, and a {@code LivingEntity}'s model does rotate with its body yaw). Pitch/roll cannot: an
     * armor stand has no {@code Display} transform metadata to compose a rotation into, and no protocol
     * field that leans its body off vertical (its {@code xRot} pitches only the head). The full reasoning,
     * including why CraftEngine's {@code ArmorStandData} per-limb poses are NOT a usable substitute, is
     * written out once on {@link ContraptionFurnitureSwarm}'s own {@code ArmorStandCell} — same limitation,
     * same reason. Position still follows the full pose.
     */
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
        float basePitch() {
            return element.config.xRot();
        }

        /** No tilt lever on a {@code LivingEntity} — see this class's own "YAW-ONLY ORIENTATION" javadoc. */
        @Override
        Quaternionf modelRotation(ContraptionLevel level) {
            return null;
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

    /**
     * Floating item (item_display riding a real item entity) — two entity ids, {@code entityId2} carries
     * the data.
     *
     * <p><b>NOT SCALE-AWARE — no lever exists (2026-07-16, roadmap item #9).</b> Position scales (see
     * {@link Cell#render}); size cannot. The VISIBLE half of this cell is {@code entityId2}, a real
     * {@code EntityType.ITEM} — a dropped-item entity rendered by the client's own item renderer, which
     * has no {@code Scale} transform metadata at all (that field belongs to the {@code Display} entity
     * family only; the same reason this cell is deliberately excluded from the {@code BrightnessOverride}
     * fix — see this class's own "Ambient lighting" javadoc). {@code entityId1} IS an
     * {@code ITEM_DISPLAY} and could technically be scaled, but doing so would change nothing visually:
     * it carries no item of its own, existing purely as the mount the real item rides so the item's
     * position can be driven (see {@code element.cachedRidePacket}). Scaling an invisible mount would
     * only risk desyncing the rider's offset. Left alone deliberately.
     *
     * <p><b>NOT ROTATABLE EITHER — same lever, same absence (2026-07-16 tilt fix).</b> Rotation fails for
     * exactly the reason size does, one step earlier: the VISIBLE half is {@code entityId2}, a real
     * {@code EntityType.ITEM} drawn by the client's own dropped-item renderer, which honours no
     * server-sent orientation at all (it bobs and spins on its own) and has no {@code Display} transform to
     * compose into. Rotating {@code entityId1}, the invisible {@code ITEM_DISPLAY} mount, would change
     * nothing visible while risking a desynced rider offset. CraftEngine's own real-world rendering of this
     * element does not orient it either — {@code ItemBlockEntityElement}'s config carries no rotation, the
     * same way {@code ItemFurnitureElementConfig} carries none (see {@link ContraptionFurnitureSwarm}'s
     * {@code ItemCell}). Position follows the full pose; orientation is a non-concept for this element.
     */
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
        float basePitch() {
            return 0f; // matches the literal 0f this cell's own spawn/updatePosition packets send
        }

        /** A real ITEM entity honours no orientation — see this class's own "NOT ROTATABLE EITHER" javadoc. */
        @Override
        Quaternionf modelRotation(ContraptionLevel level) {
            return null;
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

        @Override
        float basePitch() {
            return 0f; // this cell sends no entity packets at all — see #render, which BetterModel drives
        }

        /**
         * <b>Tilt not applied — no packet to compose it into (2026-07-16 tilt fix).</b> Not the same kind of
         * limitation as the other cells here: this one isn't missing a protocol lever, it's outside the
         * metadata pipeline entirely. This cell builds no spawn/metadata packets (BetterModel owns its own
         * tracker and emits its own bone entities — see this class's javadoc), so
         * {@link ContraptionRenderScale} has no authored value list to compose a rotation into. Leaning the
         * model would mean driving BetterModel's own rotation API, a third-party soft dependency
         * ({@code compileOnly}, {@code available()}-gated, pinned to 2.2.0) whose rotation semantics could
         * not be verified against a live server for this change — the identical reasoning that left
         * {@code scale} unapplied here. The model's YAW does already follow the contraption ({@link #render}
         * builds its {@code Location} with {@code level.realYawRadians()}), and its position follows the
         * full pose; only the tilt is absent.
         */
        @Override
        Quaternionf modelRotation(ContraptionLevel level) {
            return null;
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

        /**
         * <b>{@code scale} accepted but NOT applied to the model's size (2026-07-16, roadmap item #9).</b>
         * The POSITION below is scale-correct for free ({@code level.realWorldPositionOf} — see
         * {@link Cell#render}'s javadoc), so a BetterModel-driven visual on a scaled contraption still
         * travels to the right place. Its SIZE does not follow, and deliberately isn't forced to: this cell
         * doesn't build packets at all (see this class's own javadoc — BetterModel owns its tracker and
         * emits its own bone entities), so {@link ContraptionRenderScale} has no metadata list to touch,
         * and resizing would mean driving BetterModel's own model-scale API — a third-party, soft-dependency
         * API (compileOnly, {@code available()}-gated, pinned to 2.2.0 for Java-21 compatibility) whose
         * scale semantics could not be verified against a live server for this change. Guessing at it risks
         * breaking the crusher's working animation for a cosmetic gain on a rare combination
         * (BetterModel-driven machine + non-default contraption scale); the honest, non-regressing choice is
         * to leave the model at its authored size and document it.
         */
        @Override
        void render(List<Player> viewers, ContraptionLevel level, int blockLight, int skyLight, double scale) {
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
