package dev.arubik.craftengine.contraption.render;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.joml.Vector3f;

import dev.arubik.craftengine.contraption.ContraptionFurniture;
import dev.arubik.craftengine.contraption.ContraptionMath;
import dev.arubik.craftengine.contraption.level.ContraptionLevel;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineFurniture;
import net.momirealms.craftengine.bukkit.entity.data.InteractionData;
import net.momirealms.craftengine.bukkit.entity.furniture.element.ArmorStandFurnitureElementConfig;
import net.momirealms.craftengine.bukkit.entity.furniture.element.ItemDisplayFurnitureElementConfig;
import net.momirealms.craftengine.bukkit.entity.furniture.element.ItemFurnitureElementConfig;
import net.momirealms.craftengine.bukkit.entity.furniture.element.TextDisplayFurnitureElementConfig;
import net.momirealms.craftengine.core.entity.furniture.FurnitureDefinition;
import net.momirealms.craftengine.core.entity.furniture.FurnitureVariant;
import net.momirealms.craftengine.core.entity.furniture.element.FurnitureElementConfig;
import net.momirealms.craftengine.core.entity.furniture.hitbox.FurnitureHitBoxConfig;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.entity.seat.SeatConfig;

/**
 * Renders every {@link ContraptionFurniture} cell captured into a contraption. See
 * {@link ContraptionFurniture}'s own javadoc for the full architecture writeup: each cell now
 * carries a genuinely LIVE {@code BukkitFurniture} placed inside the contraption's hidden
 * {@code ContraptionLevel} (the same "real object lives in a stable local space, only the packet
 * mirror gets repositioned" technique {@link ContraptionDisplaySwarm} already uses for captured
 * blocks), so this swarm re-reads that live instance's CURRENT {@code currentVariant()}/
 * {@code hitboxes()} every rebuild instead of freezing whatever the static capture-time config
 * said — a behavior-driven variant swap or hitbox change inside the fake world is picked up the
 * same way a live {@code BlockState} change is for blocks. One packet-only "visual" per
 * {@link FurnitureElementConfig} the CURRENT variant declares (same low-level technique as
 * {@link ContraptionBlockEntityElementMirror} — read that class's javadoc for the shared
 * reasoning: config fields are public, so we rebuild spawn/position packets ourselves instead of
 * needing CraftEngine to render into the real world directly, which it never will since nothing
 * real-world-side is tracking the fake-world instance).
 *
 * <p><b>Hitbox/collider — course-corrected (2026-07-02 session).</b> An EARLIER same-day pass
 * ("aplica logica de peek cuando un furniture se convierte a contraption") routed each furniture
 * cell's real live {@code FurnitureHitBox} parts through the SAME shape-aware
 * {@code ContraptionShulkerColliderSwarm} block cells use for their peek-merge colliders. That
 * turned out to be the wrong call, per explicit user feedback ("los furnitures estan heredando
 * shulker swarm hitbox cuando ellos tienen su propia hitbox"): furniture already has its OWN
 * native hitbox mechanism — decompiling CraftEngine's real
 * {@code InteractionFurnitureHitbox}/{@code AbstractFurnitureHitBox} (see
 * {@code testserver}'s craft-engine-bukkit sources jar) shows every hitbox part is backed by a
 * REAL, server-spawned {@code BukkitCollider} (a genuine NMS {@code Interaction} entity —
 * {@code ColliderType.INTERACTION} is this project's server default) placed into whatever
 * {@code World} the furniture itself lives in — here, the contraption's hidden
 * {@code ContraptionLevel}. That collider is what gives CraftEngine furniture its real
 * standing/pushback physics AND its real click-routing (a genuine
 * {@code PlayerInteractEntityEvent} against a real entity, confirmed against CraftEngine's own
 * {@code InteractListener}, which reads {@code FurnitureHitboxPart#interactive()} to decide
 * seat-mount vs. block-style {@code useItemOn} dispatch). It is NOT the cosmetic shulker CraftEngine
 * shows for a {@code ShulkerFurnitureHitboxConfig} variant (that one really is packet-only/pure
 * visual, per {@link ContraptionShulkerColliderSwarm}'s own javadoc) — the two are separate
 * objects even for a shulker-styled hitbox (see {@code AbstractFurnitureHitBox#createCollider}).
 *
 * <p>Since that real collider lives inside the hidden {@code ContraptionLevel}, it is invisible/
 * un-clickable from the real world for the exact same reason every other real object living in
 * that level is (see {@code ContraptionLevel}'s own javadoc, "Real vs. fake positions" —
 * {@code realViewers} never resolves against this level's own, permanently-empty, tracking set).
 * So the fix here is NOT to re-derive a shulker-shaped approximation of the hitbox from scratch
 * (that gave up furniture's own real per-part shape/{@code interactive} semantics for a coarser,
 * independently-computed one, and — being a {@code SHULKER} rather than an {@code INTERACTION} —
 * can never receive a real client click at all: {@code SHULKER} has no clientside hit-detection
 * the way {@code INTERACTION} does), it's to MIRROR that real collider's own shape/position, the
 * same low-level "rebuild the packet ourselves, in real-world space" technique every other cell
 * in this class already uses for furniture's VISUAL elements: one packet-only {@code INTERACTION}
 * entity per {@code FurnitureHitboxPart} (see {@link InteractionHitboxCell}), reusing that part's
 * real {@code aabb()} (width/height, correctly shaped per the furniture's own configured hitbox —
 * not a hardcoded cube) and {@code interactive()} flag (fed straight into the same
 * {@code InteractionData.Response} metadata field CraftEngine's own hitbox uses), repositioned
 * every tick via {@code ContraptionMath#renderPosition} exactly like every other mirrored element.
 * This gives BOTH standing/pushback collision (the client's own local physics collides against
 * any {@code INTERACTION}, exactly how {@code ContraptionHitboxSwarm}'s own slots already provide
 * collision for blocks) AND real click detection ({@code ContraptionInteractionListener}'s
 * existing {@code onInteractEntity} handler already exists specifically because clicking a
 * packet-only {@code INTERACTION} entity fires a real {@code PlayerInteractEntityEvent} — see
 * that listener's own javadoc). Falls back to a plain scale-1/1×1×1 mirror at the furniture's own
 * origin if the live instance has no hitbox parts (no live furniture at all, or a genuinely
 * hitbox-less decorative variant) — still gives SOME standable/clickable surface rather than none.
 *
 * <p>Only the 4 built-in "constant" furniture element types are mirrored (item display, text
 * display, armor stand, floating item) — same scope cut as the block-entity element mirror.
 * Held-item equipment on the armor-stand mirror is not sent (no equipment-packet constructor
 * wired yet). Tint sources are not resolved (they need to read dye/firework colors off a live
 * furniture in a way this project has no existing bridge for yet — out of scope, unchanged from
 * before this session).
 */
public final class ContraptionFurnitureSwarm {

    private final List<Cell> cells = new ArrayList<>();
    private final List<SeatSlot> seatSlots = new ArrayList<>();

    /**
     * Last real ambient (block, sky) light pair read at the bearing's real-world block position —
     * see {@link #render}'s "Ambient lighting" javadoc. Same read-once-per-render-call / shared-
     * across-every-cell convention {@link ContraptionDisplaySwarm#updateAmbientLight} already uses
     * for captured blocks (2026-07-02 follow-up — "los display de los furniture... no estan
     * recibiendo la iluminacion arregla eso tmb"): furniture's own Display-backed cells
     * ({@link ItemDisplayCell}, {@link TextDisplayCell}, {@link ItemCell}) never set a
     * {@code DisplayData.BrightnessOverride} at all before this fix — not even the old hardcoded
     * full-bright constant the block swarm used to have — so they were left at whatever the
     * client's own default rendering falls back to for a packet-only entity with no real chunk
     * position to read light from, tracking neither day/night nor nearby real light sources. Kept
     * as a small independent field pair here (not a shared helper with
     * {@link ContraptionDisplaySwarm}) since the two classes are otherwise-independent swarms that
     * may be touched by different agents concurrently — duplicating this ~10-line light read is
     * lower-risk than coupling them through a new shared utility for now.
     */
    private int lastAmbientBlockLight = 15;
    private int lastAmbientSkyLight = 15;

    /**
     * Real per-part hitbox mirrors (2026-07-02 session, course correction — see this class's own
     * "Hitbox/collider" javadoc paragraph below for the full writeup on why this REPLACES an
     * earlier same-day attempt that routed furniture hitboxes through
     * {@link ContraptionShulkerColliderSwarm} instead). Keyed by the same {@link PartKey} the
     * old shulker approach used, so an unchanged part reuses its entity id across ticks exactly
     * like every other {@link Cell} in this class.
     */
    private final Map<Object, InteractionHitboxCell> interactionHitboxes = new HashMap<>();

    /**
     * Reference to the last {@code furniture} list this swarm actually built cells from — NOT a
     * content/deep-equality cache, just identity. {@link ContraptionFurniture} is only ever SET
     * once per contraption ({@code ContraptionState#setFurniture}, called only from
     * {@code ContraptionAssembler#assemble}), so the visual/hitbox/seat CELL SET never changes
     * shape after capture — only each cell's render TRANSFORM changes, every tick, via
     * {@link #render} (unaffected by this identity check), and now also each cell's LIVE
     * variant/hitbox STATE, re-read fresh every {@link #render} call from {@code cf.liveFurniture()}
     * (also unaffected — see that method's own javadoc). Skipping the cell-rebuild entirely
     * whenever {@code furniture} is the SAME list instance already built from means this swarm's
     * cells reuse their entity ids/{@code shownTo} tracking indefinitely instead of being torn
     * down and respawned every tick (bug fixed in an earlier session — see git history/
     * CONTRAPTIONS.md for the "furniture leak" writeup this javadoc used to carry in full).
     */
    private List<ContraptionFurniture> lastBuiltFrom;

    /**
     * (Re)builds the swarm's visual + hitbox cells from the captured furniture list. Cell
     * identity (which packet-entity ids exist) is derived from the STATIC capture-time
     * definition/variant-at-capture-time — see {@link #lastBuiltFrom}'s javadoc for why this only
     * runs once per distinct {@code furniture} list identity, not every tick.
     */
    public void rebuild(List<ContraptionFurniture> furniture) {
        if (furniture == lastBuiltFrom) {
            return; // nothing changed since the last successful build — see javadoc above
        }
        despawnAllInternal();
        seatSlots.clear();
        lastBuiltFrom = furniture;
        if (furniture == null) {
            return;
        }
        for (ContraptionFurniture cf : furniture) {
            try {
                FurnitureDefinition def = CraftEngineFurniture.byId(cf.definitionId());
                if (def == null) {
                    continue;
                }
                FurnitureVariant variant = def.getVariant(cf.variantName());
                if (variant == null) {
                    continue;
                }
                for (FurnitureElementConfig<?> config : variant.elementConfigs()) {
                    Cell cell = wrap(config, cf);
                    if (cell != null) {
                        cells.add(cell);
                    }
                }
                // Seat completion: every SeatConfig the variant's hit-boxes declare becomes a
                // sittable slot real player interaction can redirect into (see
                // ContraptionSeatListener) — same bearing-relative-offset carrying mechanism
                // ContraptionFurnitureCapture already uses for a rider captured mid-sit.
                for (FurnitureHitBoxConfig<?> hitBox : variant.hitBoxConfigs()) {
                    for (SeatConfig seat : hitBox.seats()) {
                        // Unlike every other element cell in this class (item display, text
                        // display, armor stand), a SeatConfig#position() is NOT baked into a
                        // rotated client-side model — it positions a genuinely separate real
                        // mount entity in world space (see ContraptionSeatMount), exactly like
                        // real CraftEngine's own BukkitSeat#calculateSeatLocation, which rotates
                        // SeatConfig#position() by the source location's yaw before adding it.
                        // The furniture's own placement yaw relative to the bearing
                        // (cf.yawOffsetDegrees()) is that "source yaw" here, so the seat's raw
                        // config-local offset must be rotated by it BEFORE combining with the
                        // furniture's localOffset() — adding it unrotated (as every other cell
                        // does for its own already-model-relative offset) put the seat in the
                        // wrong spot for any furniture piece not facing the bearing's yaw-0
                        // orientation.
                        //
                        // Verified numerically against BukkitSeat#calculateSeatLocation's real
                        // quaternion math (toQuaternionf(0, radians(180 - yaw), 0).conjugate(),
                        // then .add(offset.x, .., -offset.z)): for every tested yaw, that
                        // computation equals -rotateYaw(seat.position(), yawRadians) — i.e. the
                        // rotated offset negated — NOT +rotateYaw(...). Negating after rotation
                        // (rather than rotating by yaw+180 or flipping rotateYaw's own sign
                        // convention, which is shared/relied on elsewhere in this class for
                        // bearing rotation) keeps this call site self-contained.
                        Vec3 rotatedSeatOffset = ContraptionMath.rotateYaw(
                                new Vec3(seat.position().x, seat.position().y, seat.position().z),
                                Math.toRadians(cf.yawOffsetDegrees()));
                        Vec3 local = cf.localOffset().add(-rotatedSeatOffset.x, rotatedSeatOffset.y, -rotatedSeatOffset.z);
                        seatSlots.add(new SeatSlot(cf, local, cf.yawOffsetDegrees() + seat.yRot()));
                    }
                }
            } catch (Throwable ignored) {
                // One malformed furniture definition/variant shouldn't blank the whole swarm.
            }
        }
    }

    /** Every sittable seat slot from every captured furniture piece — see {@link SeatSlot}. */
    public List<SeatSlot> seatSlots() {
        return seatSlots;
    }

    /**
     * Render every cell for the given viewers at the bearing's current transform.
     *
     * <p><b>Ambient lighting (2026-07-02 follow-up — "los display de los furniture... no estan
     * recibiendo la iluminacion arregla eso tmb pls").</b> Same fix {@link ContraptionDisplaySwarm}
     * already got for captured blocks the same session, applied here for furniture's own
     * Display-backed cells: {@code realLevel} (the contraption's real {@code ServerLevel}, null-
     * tolerant — the pure-kinematics/registry unit-test path and any caller not yet threading it
     * through both fall back to full-bright, matching this class's pre-fix visual) is read ONCE
     * per call at the bearing's real-world block position and shared by every cell in this swarm —
     * see {@link #updateAmbientLight}.
     *
     * <p><b>Internal light emitters (2026-07-02 live-test follow-up — "la antorcha no ilumina a
     * los furnitures... ni al block entity renderer de craftengine").</b> A prior round of this
     * fix deliberately left out cross-cell emitter falloff here, reasoning CraftEngine's furniture
     * element configs expose no light-emission property to piggyback on — true, but the ACTUAL
     * falloff source was never furniture lighting itself, it's a captured BLOCK (a torch, etc.)
     * elsewhere in the SAME contraption. Each cell's block-light now also runs through
     * {@link ContraptionLightEmitters#withEmitterFalloff} against {@code level}'s full captured
     * block set (the same one {@link ContraptionDisplaySwarm} already scans for its own cells),
     * using the nearest local {@code BlockPos} to this cell's own local offset as the falloff
     * target — see that helper's javadoc for the full root-cause writeup. {@code level} null
     * (no captured-block data, e.g. the pure-kinematics/registry unit-test path) skips the scan
     * and falls back to real-ambient-only, same as before this fix.
     */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, ServerLevel realLevel,
            ContraptionLevel level) {
        updateAmbientLight(bearingWorldPos, realLevel);
        for (Cell cell : cells) {
            int blockLight = ContraptionLightEmitters.withEmitterFalloff(level, cell.nearestLocalBlockPos(),
                    lastAmbientBlockLight);
            cell.render(viewers, bearingWorldPos, yawRadians, blockLight, lastAmbientSkyLight);
        }
        renderInteractionHitboxes(viewers, bearingWorldPos, yawRadians);
    }

    /** Back-compat overload for any caller without a real-world light/captured-block reference — falls back to full-bright/no-falloff. */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, ServerLevel realLevel) {
        render(viewers, bearingWorldPos, yawRadians, realLevel, null);
    }

    /** Back-compat overload for any caller without a real-world light reference — falls back to full-bright. */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians) {
        render(viewers, bearingWorldPos, yawRadians, null, null);
    }

    /**
     * Re-reads real block/sky light at the bearing's current real-world block position — same
     * mechanism/source as {@link ContraptionDisplaySwarm#updateAmbientLight}
     * ({@code realLevel.getLightEngine().getLayerListener(LightLayer.BLOCK/SKY).getLightValue(pos)}).
     * {@code realLevel} null (no live Bukkit world, e.g. unit tests) falls back to full-bright
     * (15/15) — same visual as this class's pre-fix behavior, just now scoped to only the
     * null-world case instead of always.
     */
    private void updateAmbientLight(Vec3 bearingWorldPos, ServerLevel realLevel) {
        if (realLevel == null) {
            lastAmbientBlockLight = 15;
            lastAmbientSkyLight = 15;
            return;
        }
        BlockPos pos = BlockPos.containing(bearingWorldPos.x, bearingWorldPos.y, bearingWorldPos.z);
        try {
            lastAmbientBlockLight = realLevel.getLightEngine().getLayerListener(LightLayer.BLOCK).getLightValue(pos);
            lastAmbientSkyLight = realLevel.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(pos);
        } catch (Throwable ignored) {
            // Defensive: never let a light-engine read failure blank the whole swarm's rendering.
            lastAmbientBlockLight = 15;
            lastAmbientSkyLight = 15;
        }
    }

    /**
     * (Re)populates {@link #interactionHitboxes} from every furniture cell's LIVE hitbox parts
     * (see class javadoc, "Hitbox/collider — course-corrected") and renders them at the bearing's
     * current transform. Runs every {@link #render} call (not gated behind {@link #lastBuiltFrom}'s
     * identity check like {@link #rebuild}) so a behavior-driven hitbox/variant change inside the
     * fake world is picked up the same tick it happens — cheap: an already-tracked key just
     * updates its existing {@link InteractionHitboxCell}'s render transform, exactly like every
     * other {@link Cell} in this class already does per-tick.
     */
    private void renderInteractionHitboxes(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians) {
        if (lastBuiltFrom == null) {
            pruneInteractionHitboxes(java.util.Collections.emptySet(), viewers);
            return;
        }
        Set<Object> liveKeys = new HashSet<>();
        for (ContraptionFurniture cf : lastBuiltFrom) {
            try {
                List<net.momirealms.craftengine.core.entity.furniture.hitbox.FurnitureHitBox> hitboxes =
                        cf.hasLiveFurniture() ? cf.liveFurniture().hitboxes() : null;
                if (hitboxes == null || hitboxes.isEmpty()) {
                    // No live hitbox data (no live instance, or a genuinely hitbox-less
                    // decorative variant) — fall back to a plain scale-1/1x1x1 mirror at the
                    // furniture's own origin so there is still SOME standable/clickable surface.
                    Object key = cf;
                    liveKeys.add(key);
                    InteractionHitboxCell cell = interactionHitboxes.computeIfAbsent(key,
                            k -> new InteractionHitboxCell(1f, 1f, true));
                    cell.updateLocalOffset(cf.localOffset());
                    continue;
                }
                int partIndex = 0;
                for (net.momirealms.craftengine.core.entity.furniture.hitbox.FurnitureHitBox hitBox : hitboxes) {
                    List<net.momirealms.craftengine.core.entity.furniture.hitbox.FurnitureHitboxPart> parts = hitBox.parts();
                    if (parts == null) {
                        continue;
                    }
                    for (net.momirealms.craftengine.core.entity.furniture.hitbox.FurnitureHitboxPart part : parts) {
                        net.momirealms.craftengine.core.world.collision.AABB aabb = part.aabb();
                        if (aabb == null) {
                            continue;
                        }
                        Object key = new PartKey(cf, partIndex++);
                        liveKeys.add(key);
                        // aabb is already resolved to ABSOLUTE fake-world-local coordinates (the
                        // live furniture's own real collider, placed inside ContraptionLevel —
                        // see Furniture#getRelativePosition) — convert to bearing-relative local
                        // offset the same way ContraptionFurnitureCapture derives cf.localOffset()
                        // from a real-world absolute position, just against the fake level's own
                        // origin (bearing sits at local (0,0,0) inside that level) instead.
                        double centerX = (aabb.minX + aabb.maxX) / 2.0;
                        double centerZ = (aabb.minZ + aabb.maxZ) / 2.0;
                        float width = (float) Math.max(0.0625, Math.max(aabb.maxX - aabb.minX, aabb.maxZ - aabb.minZ));
                        float height = (float) Math.max(0.0625, aabb.maxY - aabb.minY);
                        boolean interactive = part.interactive();
                        InteractionHitboxCell cell = interactionHitboxes.computeIfAbsent(key,
                                k -> new InteractionHitboxCell(width, height, interactive));
                        cell.updateLocalOffset(new Vec3(centerX, aabb.minY, centerZ));
                    }
                }
            } catch (Throwable ignored) {
                // One malformed furniture instance's hitbox data shouldn't blank every collider.
            }
        }
        pruneInteractionHitboxes(liveKeys, viewers);
        for (InteractionHitboxCell cell : interactionHitboxes.values()) {
            cell.render(viewers, bearingWorldPos, yawRadians);
        }
    }

    /** Despawns/drops any {@link #interactionHitboxes} entry not present in {@code liveKeys}. */
    private void pruneInteractionHitboxes(Set<Object> liveKeys, List<Player> viewers) {
        java.util.Iterator<Map.Entry<Object, InteractionHitboxCell>> it = interactionHitboxes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Object, InteractionHitboxCell> e = it.next();
            if (!liveKeys.contains(e.getKey())) {
                e.getValue().despawnAll(viewers);
                it.remove();
            }
        }
    }

    /** Stable per-(furniture,part-index) key for {@link #interactionHitboxes}' diff-based reuse. */
    private record PartKey(ContraptionFurniture cf, int index) {
    }

    public void despawnAll(List<Player> viewers) {
        for (Cell cell : cells) {
            cell.despawnAll(viewers);
        }
        cells.clear();
        seatSlots.clear();
        for (InteractionHitboxCell cell : interactionHitboxes.values()) {
            cell.despawnAll(viewers);
        }
        interactionHitboxes.clear();
        lastBuiltFrom = null; // defensive: force a real rebuild if this instance were ever reused post-teardown
    }

    private void despawnAllInternal() {
        cells.clear(); // rebuild always starts from a fresh capture list; no stale-viewer despawn needed mid-rebuild
    }

    public int cellCount() {
        return cells.size();
    }

    public int interactionHitboxCount() {
        return interactionHitboxes.size();
    }

    private static Cell wrap(FurnitureElementConfig<?> config, ContraptionFurniture cf) {
        if (config instanceof ItemDisplayFurnitureElementConfig c) {
            return new ItemDisplayCell(c, cf);
        }
        if (config instanceof TextDisplayFurnitureElementConfig c) {
            return new TextDisplayCell(c, cf);
        }
        if (config instanceof ArmorStandFurnitureElementConfig c) {
            return new ArmorStandCell(c, cf);
        }
        if (config instanceof ItemFurnitureElementConfig c) {
            return new ItemCell(c, cf);
        }
        return null; // unrecognised/dynamic element type: not mirrored, best-effort scope
    }

    /**
     * One mirrored furniture element or hitbox, redirected through the bearing's live transform.
     *
     * <p><b>ROOT CAUSE (this session — "el furniture como tal se mueve pero no se muestra que se
     * mueve").</b> {@link #render} recomputes {@code real}/{@code yawDegrees} from the CURRENT
     * bearing transform every single call (confirmed: {@code cf.localOffset()} is a final field
     * on an immutable {@code record}, {@link ContraptionMath#renderPosition} is pure/stateless,
     * and {@code ContraptionEntity#render} calls {@code furnitureSwarm.render(...)} every tick
     * unconditionally — none of that was ever stale). {@code updatePosition} DOES fire every
     * tick and DOES send a fresh {@code ClientboundEntityPositionSyncPacket} to the correct
     * {@code entityId} (the exact same id used at {@code spawn} time — no id mismatch either).
     * So the logical position genuinely was being recalculated and transmitted correctly, exactly
     * matching the user's precise diagnosis — the bug was never in the math or the tick/call
     * wiring at all.
     *
     * <p>The actual bug: every mirrored element here is a Mojang {@code Display} entity
     * ({@code ITEM_DISPLAY}/{@code TEXT_DISPLAY}) — a client-rendered entity type that does NOT
     * reuse the normal entity movement-lerp path for its visual transform. A {@code Display}'s
     * client-side renderer only re-interpolates its rendered position/rotation over
     * {@code PosRotInterpolationDuration} ticks (and its transform over
     * {@code TransformationInterpolationDuration} ticks) — metadata fields read from its
     * {@code ClientboundSetEntityDataPacket}, sent ONCE at {@code spawn} time here and never
     * again. With that duration left at its unset default (0 ticks), the client has no
     * interpolation window to animate through: it silently keeps rendering at essentially the
     * spawn-time transform (visually frozen) even while faithfully acking every position-sync
     * packet server-side — precisely "moves internally, never shown moving". Compare
     * {@code ContraptionDisplaySwarm.Cell#metadata}, the known-working block-display swarm: it
     * explicitly sets both interpolation-duration fields every spawn — this furniture swarm's
     * {@code ItemDisplayCell}/{@code TextDisplayCell}/{@code ItemCell} spawn methods never did.
     * (The furniture-authored {@code config.metadata.apply(...)} values are CraftEngine's own
     * per-element appearance metadata — item/text content, scale, translation — never tuned for
     * "this is about to be repositioned every tick by an external mover", so they don't set this
     * either.) {@code ArmorStandCell} is unaffected — a vanilla {@code ArmorStand} is a normal
     * {@code LivingEntity}, which DOES use the standard movement-lerp path, no metadata-driven
     * interpolation window required.
     *
     * <p>Fix: every Display-backed cell's {@code spawn} now appends the same
     * {@code PosRotInterpolationDuration}/{@code TransformationInterpolationDuration} metadata
     * values {@code ContraptionDisplaySwarm} already uses (see {@link #addInterpolationTuning})
     * on top of the furniture's own authored metadata, giving the client an actual window to
     * animate into each new synced position instead of freezing at spawn-time's transform.
     */
    private abstract static class Cell {
        final ContraptionFurniture cf;
        final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();

        // Live-tracked brightness (2026-07-02 follow-up — see ContraptionFurnitureSwarm's own
        // "Ambient lighting" javadoc on #render/#updateAmbientLight). -1 sentinel so the very
        // first #render call always counts as a change and sends real metadata instead of
        // silently reusing whatever spawn() happened to send. Unused by non-Display cells
        // (ArmorStandCell, InteractionHitboxCell) — see their own no-op resendMetadata overrides.
        private int blockLight = -1;
        private int skyLight = -1;

        Cell(ContraptionFurniture cf) {
            this.cf = cf;
        }

        /**
         * Appends the interpolation-window metadata a client-rendered {@code Display} entity
         * needs to animate into each newly-synced position/rotation instead of snapping/freezing
         * at whatever transform it last received — see this class's own ROOT-CAUSE javadoc above.
         * Same duration ({@code 2} ticks) {@link ContraptionDisplaySwarm.Cell#metadata} already
         * uses for the known-working block swarm — kept identical rather than re-tuned, since
         * both swarms are driven by the exact same per-tick {@code ContraptionEntity#render} loop
         * and there's no reason furniture would need a different window.
         */
        static void addInterpolationTuning(List<Object> values) {
            net.momirealms.craftengine.bukkit.entity.data.DisplayData.PosRotInterpolationDuration
                    .addEntityData(2, values);
            net.momirealms.craftengine.bukkit.entity.data.DisplayData.TransformationInterpolationDuration
                    .addEntityData(2, values);
        }

        abstract Vector3f offset();

        abstract float baseYaw();

        /**
         * This cell's own local (bearing-relative, yaw-0 basis) position, rounded to the nearest
         * captured {@code BlockPos} — the target position fed into
         * {@link ContraptionLightEmitters#withEmitterFalloff} (see {@link #render}'s "Internal
         * light emitters" javadoc). Not exact for an off-center element (a text display sitting at
         * a fractional offset above its furniture's origin, say) but a fine approximation for a
         * purely cosmetic falloff value.
         */
        BlockPos nearestLocalBlockPos() {
            Vector3f off = offset();
            Vec3 local = cf.localOffset().add(off.x, off.y, off.z);
            return ContraptionLightEmitters.nearestBlockPos(local);
        }

        abstract void spawn(Player player, Vec3 realPos, float yawDegrees);

        abstract void updatePosition(Player player, Vec3 realPos, float yawDegrees);

        abstract void despawn(Player player);

        /**
         * Resends this cell's metadata packet with the current {@link #blockLight}/{@link #skyLight}
         * baked in (see {@link ContraptionDisplaySwarm.Cell#sendMetadata} for the block-swarm
         * counterpart this mirrors). No-op by default — only the Display-backed cells
         * ({@link ItemDisplayCell}, {@link TextDisplayCell}, {@link ItemCell}) override this;
         * {@link ArmorStandCell} sends no {@code BrightnessOverride} at all (a vanilla
         * {@code ArmorStand} is a real living entity with its own real lighting, not a client-side
         * {@code Display} that needs one) so it has nothing to resend here.
         */
        void resendMetadata(Player player) {
            // no-op by default
        }

        void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, int blockLight, int skyLight) {
            Vector3f off = offset();
            Vec3 local = cf.localOffset().add(off.x, off.y, off.z);
            Vec3 real = ContraptionMath.renderPosition(local, bearingWorldPos, yawRadians);
            float yawDegrees = cf.yawOffsetDegrees() + baseYaw() + (float) Math.toDegrees(yawRadians);
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
                    spawn(p, real, yawDegrees);
                } else {
                    updatePosition(p, real, yawDegrees);
                    if (lightChanged) {
                        resendMetadata(p);
                    }
                }
            }
            shownTo.retainAll(current);
        }

        /** Current live brightness reading — see subclasses' {@code spawn}/{@link #resendMetadata}. */
        int blockLight() {
            return blockLight;
        }

        int skyLight() {
            return skyLight;
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
        private final ItemDisplayFurnitureElementConfig config;
        private final int entityId = nextEntityId();
        private final UUID uuid = UUID.randomUUID();
        private final Object despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));

        ItemDisplayCell(ItemDisplayFurnitureElementConfig config, ContraptionFurniture cf) {
            super(cf);
            this.config = config;
        }

        @Override
        Vector3f offset() {
            return config.position;
        }

        @Override
        float baseYaw() {
            return config.yRot;
        }

        @Override
        void spawn(Player player, Vec3 realPos, float yawDegrees) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, uuid,
                    realPos.x, realPos.y, realPos.z, config.xRot, yawDegrees, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata(player));
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 realPos, float yawDegrees) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    entityId, realPos.x, realPos.y, realPos.z, yawDegrees, config.xRot, false), false);
        }

        @Override
        void resendMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata(player)), false);
        }

        /**
         * Furniture's own authored appearance metadata + interpolation tuning + this cell's live
         * ambient {@code BrightnessOverride} (see {@code ContraptionFurnitureSwarm}'s own "Ambient
         * lighting" javadoc — 2026-07-02 follow-up fix; this field was never set at all before).
         */
        private List<Object> metadata(Player player) {
            List<Object> values = new ArrayList<>(config.metadata.apply(player, null));
            addInterpolationTuning(values); // see ROOT-CAUSE javadoc on ContraptionFurnitureSwarm.Cell
            net.momirealms.craftengine.bukkit.entity.data.DisplayData.BrightnessOverride
                    .addEntityData((blockLight() << 4) | (skyLight() << 20), values);
            return values;
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(despawnPacket, false);
        }
    }

    private static final class TextDisplayCell extends Cell {
        private final TextDisplayFurnitureElementConfig config;
        private final int entityId = nextEntityId();
        private final UUID uuid = UUID.randomUUID();
        private final Object despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));

        TextDisplayCell(TextDisplayFurnitureElementConfig config, ContraptionFurniture cf) {
            super(cf);
            this.config = config;
        }

        @Override
        Vector3f offset() {
            return config.position;
        }

        @Override
        float baseYaw() {
            return config.yRot;
        }

        @Override
        void spawn(Player player, Vec3 realPos, float yawDegrees) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, uuid,
                    realPos.x, realPos.y, realPos.z, config.xRot, yawDegrees, EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata(player));
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 realPos, float yawDegrees) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    entityId, realPos.x, realPos.y, realPos.z, yawDegrees, config.xRot, false), false);
        }

        @Override
        void resendMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata(player)), false);
        }

        /**
         * Furniture's own authored appearance metadata + interpolation tuning + this cell's live
         * ambient {@code BrightnessOverride} (see {@code ContraptionFurnitureSwarm}'s own "Ambient
         * lighting" javadoc — 2026-07-02 follow-up fix; this field was never set at all before).
         */
        private List<Object> metadata(Player player) {
            List<Object> values = new ArrayList<>(config.metadata.apply(player));
            addInterpolationTuning(values); // see ROOT-CAUSE javadoc on ContraptionFurnitureSwarm.Cell
            net.momirealms.craftengine.bukkit.entity.data.DisplayData.BrightnessOverride
                    .addEntityData((blockLight() << 4) | (skyLight() << 20), values);
            return values;
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(despawnPacket, false);
        }
    }

    /** Position/metadata only — no held-item equipment packet (see class javadoc). */
    private static final class ArmorStandCell extends Cell {
        private final ArmorStandFurnitureElementConfig config;
        private final int entityId = nextEntityId();
        private final UUID uuid = UUID.randomUUID();
        private final Object despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));

        ArmorStandCell(ArmorStandFurnitureElementConfig config, ContraptionFurniture cf) {
            super(cf);
            this.config = config;
        }

        @Override
        Vector3f offset() {
            return config.position;
        }

        @Override
        float baseYaw() {
            return config.yRot;
        }

        @Override
        void spawn(Player player, Vec3 realPos, float yawDegrees) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, uuid,
                    realPos.x, realPos.y, realPos.z, config.xRot, yawDegrees, EntityType.ARMOR_STAND, 0, Vec3.ZERO, yawDegrees);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, config.metadata.apply(player));
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 realPos, float yawDegrees) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    entityId, realPos.x, realPos.y, realPos.z, yawDegrees, config.xRot, false), false);
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(despawnPacket, false);
        }
    }

    /** Floating item (item_display) — furniture's variant, one entity (unlike the block-entity one, which rides a real item). */
    private static final class ItemCell extends Cell {
        private final ItemFurnitureElementConfig config;
        private final int entityId = nextEntityId();
        private final UUID uuid = UUID.randomUUID();
        private final Object despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));

        ItemCell(ItemFurnitureElementConfig config, ContraptionFurniture cf) {
            super(cf);
            this.config = config;
        }

        @Override
        Vector3f offset() {
            return config.position;
        }

        @Override
        float baseYaw() {
            return 0f;
        }

        @Override
        void spawn(Player player, Vec3 realPos, float yawDegrees) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, uuid,
                    realPos.x, realPos.y, realPos.z, 0f, 0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata(player));
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 realPos, float yawDegrees) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    entityId, realPos.x, realPos.y, realPos.z, 0f, 0f, false), false);
        }

        @Override
        void resendMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata(player)), false);
        }

        /**
         * Furniture's own authored appearance metadata + interpolation tuning + this cell's live
         * ambient {@code BrightnessOverride} (see {@code ContraptionFurnitureSwarm}'s own "Ambient
         * lighting" javadoc — 2026-07-02 follow-up fix; this field was never set at all before).
         */
        private List<Object> metadata(Player player) {
            List<Object> values = new ArrayList<>(config.metadata.apply(player, null));
            addInterpolationTuning(values); // see ROOT-CAUSE javadoc on ContraptionFurnitureSwarm.Cell
            net.momirealms.craftengine.bukkit.entity.data.DisplayData.BrightnessOverride
                    .addEntityData((blockLight() << 4) | (skyLight() << 20), values);
            return values;
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(despawnPacket, false);
        }
    }

    /**
     * Packet-only real-world mirror of one furniture {@code FurnitureHitboxPart} — a genuine
     * {@code INTERACTION} entity (see class javadoc, "Hitbox/collider — course-corrected"),
     * bottom-anchored exactly like {@code ContraptionHitboxSwarm}'s own {@code Slot} (box:
     * X/Z ±width/2 around the offset, Y from the offset upward by height). {@code interactive}
     * feeds {@code InteractionData.Response} — same field CraftEngine's own hitbox metadata sets,
     * kept faithful here so a decorative/non-clickable part doesn't visually "respond" either.
     *
     * <p>Reuses this class's own {@code lx/ly/lz} bearing-local-offset + per-tick
     * {@code ContraptionMath#renderPosition} redirect convention (not {@link Cell}'s own
     * offset/baseYaw shape, since a hitbox part has no meaningful "yaw" of its own — it's an
     * axis-aligned box, same as every other packet-only hitbox slot in this package).
     */
    private static final class InteractionHitboxCell {
        private final float width;
        private final float height;
        private final boolean interactive;
        private final int entityId = nextEntityId();
        private final UUID uuid = UUID.randomUUID();
        private final Object despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
        private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
        private Vec3 localOffset = Vec3.ZERO;

        InteractionHitboxCell(float width, float height, boolean interactive) {
            this.width = width;
            this.height = height;
            this.interactive = interactive;
        }

        void updateLocalOffset(Vec3 localOffset) {
            this.localOffset = localOffset;
        }

        private List<Object> metadata() {
            List<Object> values = new ArrayList<>();
            InteractionData.Width.addEntityData(width, values);
            InteractionData.Height.addEntityData(height, values);
            InteractionData.Response.addEntityData(interactive, values);
            return values;
        }

        void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians) {
            Vec3 real = ContraptionMath.renderPosition(localOffset, bearingWorldPos, yawRadians);
            Set<UUID> current = new HashSet<>();
            for (Player p : viewers) {
                UUID id = uuidOf(p);
                if (id == null) {
                    continue;
                }
                current.add(id);
                if (shownTo.add(id)) {
                    spawn(p, real);
                } else {
                    updatePosition(p, real);
                }
            }
            shownTo.retainAll(current);
        }

        private void spawn(Player player, Vec3 realPos) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, uuid,
                    realPos.x, realPos.y, realPos.z, 0f, 0f, EntityType.INTERACTION, 0, Vec3.ZERO, 0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata());
            player.sendPackets(List.of(add, data), false);
        }

        private void updatePosition(Player player, Vec3 realPos) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    entityId, realPos.x, realPos.y, realPos.z, 0f, 0f, false), false);
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) {
                p.sendPacket(despawnPacket, false);
            }
            shownTo.clear();
        }

        private static UUID uuidOf(Player player) {
            Object pp = player.platformPlayer();
            return pp instanceof org.bukkit.entity.Player b ? b.getUniqueId() : null;
        }
    }

    /**
     * One sittable seat slot from a captured furniture piece's real
     * {@code FurnitureVariant#hitBoxConfigs()} -&gt; {@code SeatConfig} (the same config real
     * CraftEngine furniture uses for its own vanilla vehicle-mount seats — see
     * {@code ContraptionFurnitureCapture}'s javadoc for why a captured seat is never a real
     * followed/mounted entity). Real player interaction ({@code ContraptionSeatListener})
     * redirects a click into occupying a free slot exactly like a rider captured mid-sit at
     * assembly time: the player is carried at this slot's fixed bearing-relative offset via
     * {@code ContraptionState#addSeatedRider}/{@code ContraptionEntity#carrySeatedRiders}, the
     * SAME mechanism, not a new one.
     *
     * <p>{@code bearingLocalOffset} follows the exact same approximation every other
     * {@link Cell} in this class already takes (see this class's own javadoc, "same
     * technique"): the seat's raw config-local {@code SeatConfig#position()} is added
     * directly to the furniture's captured {@code localOffset()} WITHOUT first rotating by
     * the furniture's own placement yaw ({@code yawOffsetDegrees()}) — consistent with how
     * every other visual element (item display, text display, armor stand) is positioned in
     * this same swarm, not a new inconsistency. The combined vector already sits in the
     * bearing's yaw-0 basis, ready to feed straight into
     * {@code ContraptionMath#renderPosition}/{@code ContraptionState#addSeatedRider}.
     */
    public static final class SeatSlot {
        public final ContraptionFurniture furniture;
        private final Vec3 bearingLocalOffset;
        private final float yawOffsetDegrees;
        private UUID occupant; // null = free

        private SeatSlot(ContraptionFurniture furniture, Vec3 bearingLocalOffset, float yawOffsetDegrees) {
            this.furniture = furniture;
            this.bearingLocalOffset = bearingLocalOffset;
            this.yawOffsetDegrees = yawOffsetDegrees;
        }

        public Vec3 bearingLocalOffset() {
            return bearingLocalOffset;
        }

        public boolean isFree() {
            return occupant == null;
        }

        public UUID occupant() {
            return occupant;
        }

        public void occupy(UUID playerId) {
            occupant = playerId;
        }

        public void vacate() {
            occupant = null;
        }

        /** This seat's current real-world (snap-to) position under the bearing's live transform. */
        public Vec3 currentRealPosition(Vec3 bearingWorldPos, double yawRadians) {
            return ContraptionMath.renderPosition(bearingLocalOffset, bearingWorldPos, yawRadians);
        }

        /** This seat's current facing (degrees) under the bearing's live rotation. */
        public float currentYawDegrees(double yawRadians) {
            return yawOffsetDegrees + (float) Math.toDegrees(yawRadians);
        }
    }

    // ---- fresh server-unique fake entity id (Entity.ENTITY_COUNTER is private) ----
    private static final AtomicInteger ENTITY_COUNTER;
    static {
        AtomicInteger counter;
        try {
            Field f = net.minecraft.world.entity.Entity.class.getDeclaredField("ENTITY_COUNTER");
            f.setAccessible(true);
            counter = (AtomicInteger) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
        ENTITY_COUNTER = counter;
    }

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }
}
