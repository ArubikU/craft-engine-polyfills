package dev.arubik.craftengine.contraption.render;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.joml.Quaternionf;
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

    /**
     * Seats declared by captured FURNITURE ({@code FurnitureVariant#hitBoxConfigs()} -&gt;
     * {@code SeatConfig}) — rebuilt only when the captured furniture LIST identity changes, i.e. never
     * after assembly (see {@link #lastBuiltFrom}).
     */
    private final List<SeatSlot> furnitureSeatSlots = new ArrayList<>();

    /**
     * Seats declared by captured BLOCKS via CraftEngine's {@code seat_block} behavior (2026-07-16 —
     * see {@link ContraptionBlockSeats} for the full writeup on why the native
     * {@code SeatBlockEntityController#spawnSeat} path cannot be used for a captured block). Keyed by
     * {@link BlockSeatKey} — a seat's own resolved local position + facing — rather than rebuilt from
     * scratch, so a seat that is UNCHANGED across a rebuild keeps its very same {@link SeatSlot} instance
     * and therefore its {@code occupant}: a block-seat rider must not be silently un-seated just because
     * some unrelated block elsewhere on the contraption moved and triggered a rescan.
     */
    private final Map<Object, SeatSlot> blockSeatSlots = new LinkedHashMap<>();

    /**
     * The captured block set {@link #rebuildBlockSeats} last actually scanned. The scan itself is a
     * CraftEngine custom-state + behavior lookup PER CELL, which is far too heavy to repeat every tick for
     * a large structure — and it only ever needs redoing when the block set actually changes shape (a
     * piston pushing a sofa in/out of the contraption, a captured seat block being broken). So the set is
     * compared by value against the previous scan's snapshot and the whole scan short-circuits when it
     * matches, exactly the same "identity/equality gate in front of an expensive rebuild" shape
     * {@link #lastBuiltFrom} already uses for the furniture cells.
     */
    private Set<BlockPos> lastBlockSeatScan;

    /**
     * The COMBINED seat list {@link #seatSlots()} hands out — furniture seats followed by block seats.
     * Recomposed by {@link #recomposeSeatSlots} whenever either half changes, and never on a normal tick.
     * Deliberately kept as one flat list of one {@link SeatSlot} TYPE, so
     * {@code ContraptionSeatListener}'s sit/stand/carry/dismount paths (and
     * {@code ContraptionEntity#carrySeatedRiders}) needed no changes at all to gain block seats — a block
     * seat and a furniture seat are the same kind of thing to every consumer, differing only in where their
     * offset came from.
     */
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
        furnitureSeatSlots.clear();
        lastBuiltFrom = furniture;
        if (furniture == null) {
            recomposeSeatSlots();
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
                        // Unlike every other element cell in this class (item display, text display, armor
                        // stand), a SeatConfig#position() is NOT baked into a rotated client-side model —
                        // it positions a genuinely separate real mount entity in world space (see
                        // ContraptionSeatMount), so it must be rotated by its SOURCE's own yaw (here the
                        // furniture's placement yaw relative to the bearing, cf.yawOffsetDegrees()) before
                        // being combined with cf.localOffset(). That rotation is CraftEngine's own
                        // convention, not ours, and now lives in exactly one place — see
                        // ContraptionSeatMath, which the block-seat path (ContraptionBlockSeats) shares.
                        //
                        // 2026-07-16: this call site used to inline that math itself, with a comment
                        // asserting it had been verified against BukkitSeat#calculateSeatLocation "for
                        // every tested yaw". It had not: re-running that comparison against the real jar
                        // shows the old inline formula matches CraftEngine ONLY for a seat whose config Z
                        // offset is 0 (which every seat configured today happens to be, so nothing
                        // observable changes here) and mirrors the seat across its source for any other Z.
                        // ContraptionSeatMath carries the corrected, re-verified derivation.
                        Vec3 local = cf.localOffset()
                                .add(ContraptionSeatMath.seatOffset(seat.position(), cf.yawOffsetDegrees()));
                        furnitureSeatSlots.add(new SeatSlot(cf, local,
                                ContraptionSeatMath.seatYawDegrees(seat, cf.yawOffsetDegrees()),
                                seat.limitPlayerRotation()));
                    }
                }
            } catch (Throwable ignored) {
                // One malformed furniture definition/variant shouldn't blank the whole swarm.
            }
        }
        recomposeSeatSlots();
    }

    /**
     * (Re)builds the BLOCK-seat half of {@link #seatSlots()} from {@code level}'s captured cells — every
     * seat any captured block declares through CraftEngine's {@code seat_block} behavior (2026-07-16; see
     * {@link ContraptionBlockSeats} for why these have to be re-emitted by us rather than spawned through
     * CraftEngine's own native seat path). Called every tick from {@code ContraptionEntity#rebuildSwarm},
     * right after {@code ContraptionLevel#refreshLocalPositions} has re-synced the cell set — but the
     * actual (per-cell CraftEngine lookup) scan only runs when that cell set genuinely CHANGED, see
     * {@link #lastBlockSeatScan}.
     *
     * <p>An unchanged seat keeps its existing {@link SeatSlot} instance across a rescan (see
     * {@link #blockSeatSlots}), so a rider seated on a sofa is not un-seated by an unrelated block moving.
     * A seat that genuinely stops existing — its block was pushed elsewhere, or broken — drops out of the
     * map and, if it was occupied, simply stops being findable by occupant id; {@code
     * ContraptionSeatListener#dismount} already handles exactly that case (it falls back to the rider's
     * live mount position, see its own javadoc), which is why no explicit eviction is needed here.
     */
    public void rebuildBlockSeats(ContraptionLevel level) {
        Set<BlockPos> current = level == null ? java.util.Set.of() : level.localPositions();
        if (current.equals(lastBlockSeatScan)) {
            return; // block set unchanged — skip the whole per-cell CraftEngine lookup (see javadoc)
        }
        lastBlockSeatScan = new HashSet<>(current); // snapshot: localPositions() is a live view
        Map<Object, SeatSlot> rescanned = new LinkedHashMap<>();
        for (ContraptionBlockSeats.BlockSeat seat : ContraptionBlockSeats.scan(level)) {
            Object key = new BlockSeatKey(seat.local(), seat.yawOffsetDegrees());
            SeatSlot existing = blockSeatSlots.get(key);
            rescanned.put(key, existing != null ? existing
                    : new SeatSlot(null, seat.local(), seat.yawOffsetDegrees(), seat.limitPlayerRotation()));
        }
        blockSeatSlots.clear();
        blockSeatSlots.putAll(rescanned);
        recomposeSeatSlots();
    }

    /**
     * Stable identity of a block seat across rescans — its fully-resolved local position plus its facing.
     * Keyed on the RESULT rather than on {@code (BlockPos, seatIndex)} deliberately: two rescans that
     * produce the same seat in the same place ARE the same seat to a sitting player, regardless of which
     * cell/behavior/index the scan happened to walk it out of.
     */
    private record BlockSeatKey(Vec3 local, float yawOffsetDegrees) {
    }

    /** Rebuilds the flat combined view {@link #seatSlots()} hands out — furniture seats, then block seats. */
    private void recomposeSeatSlots() {
        seatSlots.clear();
        seatSlots.addAll(furnitureSeatSlots);
        seatSlots.addAll(blockSeatSlots.values());
    }

    /**
     * Every sittable seat slot on this contraption — from captured FURNITURE and from captured BLOCKS
     * ({@code seat_block}) alike, as one flat list of one type; see {@link SeatSlot} and
     * {@link #seatSlots} for why the two sources are deliberately indistinguishable to every consumer.
     */
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
     *
     * <p><b>Uniform scale (2026-07-16 fix — captured furniture ignored the contraption's {@code scale}).</b>
     * {@code scale} is the contraption's live {@code ContraptionState#scale()} (roadmap item #9), threaded
     * straight from {@code ContraptionEntity#render} exactly like it already was into
     * {@link ContraptionDisplaySwarm}/{@code ContraptionHitboxSwarm}. Unlike
     * {@link ContraptionBlockEntityElementMirror} (whose positions route through
     * {@code ContraptionLevel#realWorldPositionOf}, which already carried the level's scale), this swarm
     * calls {@code ContraptionMath#renderPosition} DIRECTLY with the bearing pose — so before this fix it
     * genuinely projected every furniture piece to its UNSCALED position AND rendered it at its unscaled
     * size, leaving a scaled contraption's sofas/lamps the wrong size and visibly detached from the blocks
     * they sit on. Both halves are now fixed: {@link Cell#render} scales the position via the same
     * scale-about-pivot {@code renderPosition} overload the block swarm uses, and each Display-backed cell's
     * metadata scales via {@link ContraptionRenderScale}. At {@code scale == 1.0} every path here is
     * byte-for-byte the pre-scale behaviour (see {@link Cell#render}).
     *
     * <p><b>Pitch/roll (2026-07-16 follow-up — the other half of the same gap).</b> The scale fix above
     * left {@code pitch}/{@code roll} pinned at {@code 0} in every {@code renderPosition} call here, which
     * was its own bug for the exact same reason: a TIPPING or LEANING contraption tilted its captured
     * BLOCKS ({@link ContraptionDisplaySwarm} already threads the full pose) while its furniture stayed
     * stubbornly level — a sofa floating flat inside a rolled hull. Both are now threaded straight from
     * {@code ContraptionEntity#render}, the same live {@code ContraptionState#pitchRadians()}/
     * {@code rollRadians()} every other swarm receives, so furniture orbits to the identical
     * {@code renderPosition} mapping its surrounding blocks do. At {@code pitch == 0 && roll == 0} the
     * projection reduces to exactly {@link ContraptionMath#rotateYaw} (see that class's fast-path javadoc),
     * so a never-tilted contraption is byte-for-byte unchanged.
     *
     * <p><b>Model ORIENTATION (2026-07-16 follow-up — "el furniture render no se mueve con el pitch/yaw
     * etc o sea siempre queda recto").</b> The pitch/roll pass above moved every furniture cell to its
     * correctly TILTED place but left each cell's own model standing upright there — a sofa at the right
     * spot inside a rolled hull, still perfectly level. That gap is now closed for the Display-backed cells:
     * {@link Cell#modelRotation} derives the contraption's tilt re-expressed in each element's own entity
     * frame and {@link ContraptionRenderScale} composes it into the element's CraftEngine-authored
     * transform (never overwriting it — furniture metadata is authored by CraftEngine and mirrored here,
     * so an element with its own {@code rotation:} keeps it and merely leans). YAW needed no change: it has
     * always been carried by each cell's entity body yaw ({@code yawDegrees} below), which is the correct
     * lever and composes exactly.
     *
     * <p>Three cell types cannot follow the tilt, each for a concrete structural reason documented at the
     * class in question rather than worked around: {@link ArmorStandCell} (a {@code LivingEntity} has no
     * transform metadata and no body-lean field — yaw only), {@link ItemCell} (CraftEngine authors this
     * element with no orientation at all, in or out of a contraption), and {@link InteractionHitboxCell}
     * (an {@code INTERACTION} box is axis-aligned by definition). All three still follow the full pose for
     * POSITION.
     */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            double rollRadians, double scale, ServerLevel realLevel, ContraptionLevel level) {
        updateAmbientLight(bearingWorldPos, realLevel);
        for (Cell cell : cells) {
            int blockLight = ContraptionLightEmitters.withEmitterFalloff(level, cell.nearestLocalBlockPos(),
                    lastAmbientBlockLight);
            cell.render(viewers, bearingWorldPos, yawRadians, pitchRadians, rollRadians, scale, blockLight,
                    lastAmbientSkyLight);
        }
        renderInteractionHitboxes(viewers, bearingWorldPos, yawRadians, pitchRadians, rollRadians, scale);
    }

    /** Back-compat level (pitch/roll = 0) overload — see the full {@link #render(List, Vec3, double, double, double, double, ServerLevel, ContraptionLevel)}. */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double scale,
            ServerLevel realLevel, ContraptionLevel level) {
        render(viewers, bearingWorldPos, yawRadians, 0.0, 0.0, scale, realLevel, level);
    }

    /** Back-compat scale-1 overload — see the full {@link #render(List, Vec3, double, double, double, double, ServerLevel, ContraptionLevel)}. */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, ServerLevel realLevel,
            ContraptionLevel level) {
        render(viewers, bearingWorldPos, yawRadians, 0.0, 0.0, 1.0, realLevel, level);
    }

    /** Back-compat overload for any caller without a real-world light/captured-block reference — falls back to full-bright/no-falloff. */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, ServerLevel realLevel) {
        render(viewers, bearingWorldPos, yawRadians, 0.0, 0.0, 1.0, realLevel, null);
    }

    /** Back-compat overload for any caller without a real-world light reference — falls back to full-bright. */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians) {
        render(viewers, bearingWorldPos, yawRadians, 0.0, 0.0, 1.0, null, null);
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
    private void renderInteractionHitboxes(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians,
            double pitchRadians, double rollRadians, double scale) {
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
            cell.render(viewers, bearingWorldPos, yawRadians, pitchRadians, rollRadians, scale);
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
        furnitureSeatSlots.clear();
        blockSeatSlots.clear();
        lastBlockSeatScan = null; // defensive: force a real block-seat rescan if this instance is reused
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

        /**
         * This cell's live uniform SCALE (the contraption's {@code ContraptionState#scale()}, roadmap item
         * #9 — see {@link ContraptionFurnitureSwarm#render}'s "Uniform scale" javadoc). {@code 1.0} for any
         * never-scaled body, in which case every {@code metadata} builder here takes
         * {@link ContraptionRenderScale#applyTo}'s immediate-return fast path and emits byte-for-byte the
         * pre-scale packet. A change flags a metadata resend exactly like a brightness change does (see
         * {@link #render}).
         */
        private double scale = 1.0;

        /**
         * This cell's live model ROTATION — the contraption's tilt re-expressed in this element's own frame
         * (see {@link #modelRotation}), or {@code null} on a never-tilted body, which is the value every
         * existing contraption holds forever. Composed into each Display-backed subclass's {@code metadata}
         * via {@link ContraptionRenderScale}, and a change flags a metadata resend exactly like a brightness
         * or scale change already did (see {@link #render}) — a {@code Display}'s transform is metadata, so
         * a rotation that is never resent is a rotation the client never sees.
         */
        private Quaternionf rotation;

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
         * The element's own authored entity PITCH in degrees ({@code config.xRot}, the YAML {@code pitch:}
         * key) — the exact value this cell puts in the {@code xRot} slot of its own spawn/position packets.
         * The pitch twin of {@link #baseYaw}, added 2026-07-16 because {@link #modelRotation} needs the
         * element's full authored entity rotation, not just its yaw half, to express the contraption's
         * world-space tilt in this element's own model frame.
         */
        abstract float basePitch();

        /**
         * The rotation this cell must compose INTO its authored {@code Display} transform so its model
         * follows the contraption's live pose, or {@code null} for "nothing to compose" (2026-07-16 fix —
         * "el furniture render no se mueve con el pitch/yaw etc o sea siempre queda recto"). Fed to
         * {@link ContraptionRenderScale#applyTo(List, double, org.joml.Quaternionf)} by the Display-backed
         * subclasses; see that method for how the composition itself works.
         *
         * <p><b>Why this is a TILT-ONLY residual and not the whole pose.</b> A {@code Display}'s client
         * renderer orients the entity FIRST and then applies the transformation inside that frame — for the
         * default {@code FIXED} billboard (confirmed: CraftEngine's own element factories default
         * {@code billboard} to {@code Billboard.FIXED}) the rendered result is {@code E · T}, where
         * {@code E} is built from the entity's own body yaw/xRot and {@code T} is the authored
         * transformation. That is not a guess about our packets: it is how CraftEngine's OWN real-world
         * furniture already works — {@code ItemDisplayFurnitureElementConfig#getPos} spawns the element at
         * {@code furniturePos.yRot + config.yRot} / {@code furniturePos.xRot + config.xRot} while emitting
         * {@code config.rotation} as the {@code LeftRotation}, and that combination renders correctly at
         * every placement yaw in production.
         *
         * <p>This swarm already sends the contraption's yaw through that same entity-yaw lever
         * ({@link #render} adds {@code toDegrees(yawRadians)} into {@code yawDegrees}), and yaw about
         * vertical commutes cleanly out front: {@code Ry(-cyaw)·Ry(-yaw0) == Ry(-(cyaw + yaw0))}. So YAW
         * was never the missing half — only the tilt was, exactly matching the report's "siempre queda
         * recto" (always stands upright). What is left to express is therefore:
         *
         * <pre>desired = R_c · E0 · T          sent = Ry(-cyaw) · E0 · (Q·T)
         * R_c = Ry(-cyaw) · R_tilt         (the contraption's pose — see ContraptionLevel#realOrientationOf)
         * ⇒ Q = E0⁻¹ · R_tilt · E0</pre>
         *
         * <p>i.e. a CONJUGATION: the contraption's world-space tilt re-expressed in this element's own
         * entity frame, which is precisely what it must be, since the transformation is applied inside that
         * frame. {@code E0} is the element's own tilt-free entity rotation
         * ({@code rotateY(-(cf.yawOffsetDegrees() + baseYaw())).rotateX(basePitch())}) and {@code R_tilt} is
         * {@code rotateX(pitch).rotateZ(roll)} — the identical quaternion, in the identical
         * {@code pitch ∘ roll} order, that {@code ContraptionLevel#realOrientationOf} and
         * {@code ContraptionDisplaySwarm.Cell#metadata} already use, and matching
         * {@code ContraptionMath#rotateYawPitchRoll}'s position math (roll innermost, then pitch, then yaw),
         * so a cell's model orientation and its orbit stay in lock-step.
         *
         * <p><b>Identity pose returns {@code null}, structurally.</b> At {@code pitch == 0 && roll == 0}
         * this returns before building anything, so nothing is composed and every never-tilted contraption
         * emits byte-for-byte its pre-fix metadata. That exact-zero gate is deliberate rather than relying
         * on {@code E0⁻¹ · I · E0} evaluating to a bit-exact identity quaternion in {@code float} math — it
         * very nearly does, but "very nearly" would append a {@code LeftRotation} to elements that never had
         * one, which is not what "unchanged" means.
         */
        Quaternionf modelRotation(double yawRadians, double pitchRadians, double rollRadians) {
            if (pitchRadians == 0.0 && rollRadians == 0.0) {
                return null; // never-tilted body — see javadoc; emits the pre-fix metadata exactly
            }
            Quaternionf tilt = new Quaternionf().rotateX((float) pitchRadians).rotateZ((float) rollRadians);
            Quaternionf e0 = new Quaternionf()
                    .rotateY((float) -Math.toRadians(cf.yawOffsetDegrees() + baseYaw()))
                    .rotateX((float) Math.toRadians(basePitch()));
            return e0.invert(new Quaternionf()).mul(tilt).mul(e0); // E0⁻¹ · R_tilt · E0
        }

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

        /**
         * <b>Scale threading (2026-07-16 fix — roadmap item #9; see
         * {@link ContraptionFurnitureSwarm#render}'s "Uniform scale" javadoc).</b> Two independent things
         * had to be scaled, and both are:
         * <ul>
         * <li><b>Position</b> — {@code local} is the furniture's captured bearing-relative offset PLUS this
         * element's own authored {@code offset()} ({@code config.position}), and the COMBINED vector is what
         * now goes through the scale-aware {@code ContraptionMath#renderPosition(local, bearing, yaw, pitch,
         * roll, scale)} overload. So the element's own local offset is scaled about the bearing pivot too,
         * not merely the furniture's origin — a lamp authored half a block above its base ends up a full
         * block above it on a {@code scale=2} body, exactly like the captured blocks around it (which
         * {@link ContraptionDisplaySwarm} already spaced by {@code scale} the same way). Previously this
         * called the yaw-only {@code renderPosition(local, bearing, yaw)} overload, which pins {@code pitch
         * = roll = 0} and {@code scale = 1} — the whole bug.</li>
         * <li><b>Size</b> — each Display-backed subclass's {@code metadata} multiplies the authored
         * {@code Scale}/{@code Translation} via {@link ContraptionRenderScale}, and a scale change flags a
         * metadata resend here exactly like a brightness change already did (a resize is rare, so this adds
         * no meaningful packet volume).</li>
         * </ul>
         *
         * <p><b>{@code scale == 1.0 && pitch == 0 && roll == 0} is byte-identical to before (zero
         * regression).</b> The yaw-only {@code renderPosition(local, bearing, yaw)} overload this replaced is
         * itself defined as a plain delegation to {@code renderPosition(local, bearing, yaw, 0.0, 0.0, 1.0)}
         * (see {@code ContraptionMath}'s own overload chain), so passing the full pose explicitly reaches the
         * SAME arithmetic at those values — not an equivalent-looking reimplementation. And
         * {@link ContraptionRenderScale#applyTo} returns immediately at {@code 1.0} without touching the
         * metadata list.
         *
         * <p><b>2026-07-16 follow-up: pitch/roll are now threaded too.</b> The scale fix above pinned them
         * at {@code 0}, deferring the body-lean gap; they are now the contraption's real live
         * {@code pitchRadians}/{@code rollRadians}, so captured furniture tilts with the hull it sits in
         * instead of floating level inside a rolled body. See {@link ContraptionFurnitureSwarm#render}'s own
         * "Pitch/roll" javadoc — including why the cell's model ORIENTATION stays yaw-only.
         */
        void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
                double rollRadians, double scale, int blockLight, int skyLight) {
            Vector3f off = offset();
            Vec3 local = cf.localOffset().add(off.x, off.y, off.z);
            Vec3 real = ContraptionMath.renderPosition(local, bearingWorldPos, yawRadians, pitchRadians,
                    rollRadians, scale);
            float yawDegrees = cf.yawOffsetDegrees() + baseYaw() + (float) Math.toDegrees(yawRadians);
            Quaternionf rotation = modelRotation(yawRadians, pitchRadians, rollRadians);
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
                    spawn(p, real, yawDegrees);
                } else {
                    updatePosition(p, real, yawDegrees);
                    if (metaChanged) {
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
         * resend. Null-transitions (a body starting or stopping its tilt) always count. Otherwise compared
         * with the same {@code 1e-4f} epsilon {@link ContraptionPistonShaftSwarm.Segment} already uses for
         * its own {@code LeftRotation} resend gate: a continuously-tilting body's quaternion changes every
         * tick by construction, so an exact-equality gate would be no gate at all, while sub-{@code 1e-4}
         * differences are far below anything a client can render.
         */
        private boolean rotationChanged(Quaternionf next) {
            if (rotation == null || next == null) {
                return rotation != next;
            }
            return !next.equals(rotation, 1e-4f);
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
        float basePitch() {
            return config.xRot;
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
         * lighting" javadoc — 2026-07-02 follow-up fix; this field was never set at all before) + the
         * contraption's live {@code scale} and model {@code rotation} composed into the authored
         * transform (2026-07-16 — see {@link ContraptionRenderScale}). Both are no-ops at an
         * unscaled/never-tilted pose, so this stays byte-for-byte the pre-fix packet there.
         */
        private List<Object> metadata(Player player) {
            List<Object> values = new ArrayList<>(config.metadata.apply(player, null));
            // roadmap item #9 (scale) + the 2026-07-16 tilt fix — both no-ops at scale 1 / null rotation
            ContraptionRenderScale.applyTo(values, scale(), rotation());
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
        float basePitch() {
            return config.xRot;
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
         * lighting" javadoc — 2026-07-02 follow-up fix; this field was never set at all before) + the
         * contraption's live {@code scale} and model {@code rotation} composed into the authored
         * transform (2026-07-16 — see {@link ContraptionRenderScale}). Both are no-ops at an
         * unscaled/never-tilted pose, so this stays byte-for-byte the pre-fix packet there.
         *
         * <p>A {@code TEXT_DISPLAY} honours {@code LeftRotation} exactly like an {@code ITEM_DISPLAY} does
         * — both are the same {@code Display} entity family sharing one transform implementation
         * ({@code com.mojang.math.Transformation}); only {@code BLOCK_DISPLAY} is the documented
         * odd-one-out (see {@link ContraptionRenderScale#applyTo(List, double, Quaternionf)}). Note a text
         * display authored with a non-{@code FIXED} {@code billboard:} is view-aligned by the client and
         * will ignore this — that is the config asking for a always-face-the-camera label, and honouring
         * it is correct, not a gap.
         */
        private List<Object> metadata(Player player) {
            List<Object> values = new ArrayList<>(config.metadata.apply(player));
            // roadmap item #9 (scale) + the 2026-07-16 tilt fix — both no-ops at scale 1 / null rotation
            ContraptionRenderScale.applyTo(values, scale(), rotation());
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
     * Position/metadata only — no held-item equipment packet (see class javadoc).
     *
     * <p><b>NOT SCALE-AWARE — no lever exists (2026-07-16, roadmap item #9).</b> This cell's POSITION
     * scales correctly ({@link Cell#render} is shared by every cell here), but its SIZE cannot follow the
     * contraption's {@code scale}: a vanilla {@code ArmorStand} is a real {@code LivingEntity}, not a
     * {@code Display}, so the {@code Display}-only {@code Scale} transform metadata
     * {@link ContraptionRenderScale} works through simply does not exist on it. The protocol's only armor-
     * stand size lever is {@code ArmorStandData}'s {@code Small} flag — a BINARY half-size toggle, not a
     * continuous factor, and part of the furniture's own authored appearance, so flipping it would mis-size
     * every scale other than {@code 0.5} while silently overriding what the furniture config authored. Left
     * faithful to the authored config: on a scaled contraption an armor-stand-backed furniture element sits
     * in the right (scaled) PLACE at its unscaled size. Same limitation, same reason, as
     * {@link ContraptionBlockEntityElementMirror}'s own {@code ArmorStandCell}.
     *
     * <p><b>YAW-ONLY ORIENTATION — pitch/roll have no lever (2026-07-16 tilt fix).</b> This cell's YAW
     * does follow the contraption: {@link Cell#render} feeds the bearing's live yaw into {@code yawDegrees},
     * which this cell sends as both the body and head yaw of a real {@code ARMOR_STAND}, and a
     * {@code LivingEntity}'s model genuinely rotates with its body yaw. PITCH and ROLL cannot follow, and
     * the reason is structural rather than a scope cut:
     * <ul>
     * <li>The {@code Display}-only {@code transformation} metadata {@link ContraptionRenderScale} composes
     * rotation through does not exist on an armor stand at all — it is a {@code LivingEntity}, not a
     * {@code Display}. Same root cause as the size limitation above.</li>
     * <li>A {@code LivingEntity}'s model is rendered from {@code yBodyRot}/{@code yHeadRot} plus its
     * {@code xRot} — and {@code xRot} only pitches the HEAD. There is no protocol field that leans the
     * entity's body off vertical; the spawn packet's {@code xRot} slot (already sent here as
     * {@code config.xRot}) will not tip the stand over.</li>
     * <li>CraftEngine's {@code ArmorStandData} does expose per-limb poses ({@code HeadPose},
     * {@code BodyPose}, …; verified in the 26.6.2 jar). They are deliberately NOT used: each rotates one
     * BONE about its own joint — the head about the neck, not the entity about its position — so feeding a
     * body lean into {@code HeadPose} would swivel the held item about the wrong pivot and read as a broken
     * model rather than a leaning one. They are also part of the furniture's own authored appearance, which
     * this class mirrors rather than overrides ({@code config.metadata} is sent verbatim below).</li>
     * </ul>
     * Consequence, stated plainly: inside a TILTED contraption an armor-stand-backed furniture element
     * still travels to its correct tilted PLACE ({@link Cell#render}'s position math is shared by every
     * cell here and is fully pose-aware) and still turns with the body's yaw, but stands vertically rather
     * than leaning with the hull. Same limitation, same reason, as
     * {@link ContraptionBlockEntityElementMirror}'s own {@code ArmorStandCell}.
     */
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
        float basePitch() {
            return config.xRot;
        }

        /** No tilt lever on a {@code LivingEntity} — see this class's own "YAW-ONLY ORIENTATION" javadoc. */
        @Override
        Quaternionf modelRotation(double yawRadians, double pitchRadians, double rollRadians) {
            return null;
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

    /**
     * Floating item (item_display) — furniture's variant, one entity (unlike the block-entity one, which
     * rides a real item).
     *
     * <p><b>NOT ROTATABLE — the element has no orientation to follow (2026-07-16 tilt fix).</b> Unique
     * among the cells here, and verified against CraftEngine 26.6.2's own sources rather than assumed:
     * {@code ItemFurnitureElementConfig} authors ONLY a {@code position} — it has no {@code xRot},
     * {@code yRot} or {@code rotation} field at all (every other element config has all three), its
     * {@code getPos} returns a bare {@code Furniture#getRelativePosition} with no rotation term, and its
     * {@code metadata} builder emits {@code ItemEntityData.Item}/{@code NoGravity} — real dropped-ITEM
     * metadata, carrying none of the {@code Display} transform fields {@link ContraptionRenderScale} works
     * through. CraftEngine deliberately renders this element as an un-oriented floating item: it has no
     * orientation in the real world either, at any furniture placement yaw. Matching that faithfully means
     * composing nothing — which is also why this cell's own {@code spawn}/{@code updatePosition} pin the
     * entity yaw/pitch at {@code 0} and ignore the {@code yawDegrees} they are handed. Forcing a rotation
     * onto it here would make a captured floating item behave differently from the identical, uncaptured
     * one sitting next to it. Its POSITION still follows the full pose like every other cell.
     */
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
        float basePitch() {
            return 0f; // matches the literal 0f this cell's own spawn/updatePosition packets send
        }

        /** Deliberately un-oriented, exactly like CraftEngine's own — see this class's javadoc. */
        @Override
        Quaternionf modelRotation(double yawRadians, double pitchRadians, double rollRadians) {
            return null;
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
            ContraptionRenderScale.applyTo(values, scale()); // roadmap item #9 — no-op at scale 1
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
     *
     * <p><b>NOT ROTATABLE AT ALL — not even yaw (2026-07-16 tilt fix).</b> An {@code INTERACTION} entity
     * has no model and no orientation: it IS its {@code Width}/{@code Height} metadata, and the client
     * derives an AXIS-ALIGNED bounding box from those about the entity position. There is no rotation lever
     * to send — the entity's own {@code yRot}/{@code xRot} are ignored for its hit-detection box (which is
     * why the {@code spawn}/{@code updatePosition} packets below already pass a literal {@code 0f, 0f} and
     * always have), and {@code Display} transform metadata does not exist on this entity type. This is the
     * same reason the sibling {@code ContraptionHitboxSwarm} slots are axis-aligned, and it long predates
     * this fix: a rotated contraption's colliders have always been axis-aligned boxes tracking rotated
     * POSITIONS, which is what {@link #render} already does correctly through the full pose. The practical
     * effect on a tilted body is that a non-cubic part's standable/clickable box stays axis-aligned while
     * its visual leans — an approximation, deliberately left as-is rather than papered over: making it
     * exact would need a fundamentally different collider (a swept set of smaller boxes), which is its own
     * piece of work, not part of an orientation fix.
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

        /**
         * This hitbox's live uniform SCALE (roadmap item #9). Unlike the visual cells, an
         * {@code INTERACTION} entity has no {@code Display} transform to scale — its size IS its
         * {@code Width}/{@code Height} metadata, so {@code scale} is multiplied straight into those in
         * {@link #metadata}. Scaling the collider alongside the visual is mandatory, not cosmetic: these are
         * the real standable/clickable surfaces of a captured furniture piece (see this class's own
         * "Hitbox/collider" javadoc), so a scaled sofa whose box stayed 1x would render large while only
         * being standable/clickable over its original small footprint. {@code 1.0} emits byte-for-byte the
         * pre-scale metadata ({@code w * 1.0f == w} exactly — no rounding).
         */
        private double scale = 1.0;

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
            float s = (float) scale;
            InteractionData.Width.addEntityData(width * s, values);
            InteractionData.Height.addEntityData(height * s, values);
            InteractionData.Response.addEntityData(interactive, values);
            return values;
        }

        /**
         * Positions the box via the same scale-aware {@code renderPosition} overload every visual cell here
         * now uses (so collider and visual land on the identical scaled coordinate), and resizes it when the
         * contraption's {@code scale} actually changed — a resize is rare, so the resend costs nothing on a
         * normal tick. At {@code scale == 1.0} the position call is the same delegation chain the yaw-only
         * overload always took, and {@code metaChanged} never fires, so nothing extra is sent.
         *
         * <p>{@code pitchRadians}/{@code rollRadians} are threaded for the same reason the visual cells now
         * thread them (2026-07-16 — see {@link ContraptionFurnitureSwarm#render}'s "Pitch/roll" javadoc),
         * and here it is mandatory rather than cosmetic: these boxes ARE the real standable/clickable
         * surfaces of a captured furniture piece, so leaving them level while the visual tilts would let a
         * player stand on a sofa that visibly isn't there any more.
         */
        void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
                double rollRadians, double scale) {
            Vec3 real = ContraptionMath.renderPosition(localOffset, bearingWorldPos, yawRadians, pitchRadians,
                    rollRadians, scale);
            boolean metaChanged = scale != this.scale;
            this.scale = scale;
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
                    if (metaChanged) {
                        p.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata()),
                                false);
                    }
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
     * One sittable seat slot on a contraption — from a captured FURNITURE piece's real
     * {@code FurnitureVariant#hitBoxConfigs()} -&gt; {@code SeatConfig}, or (2026-07-16) from a captured
     * BLOCK's CraftEngine {@code seat_block} behavior via {@link ContraptionBlockSeats}. Both are the same
     * config type ({@code SeatConfig}) resolved through the same copied CraftEngine placement convention
     * ({@link ContraptionSeatMath}), so they are deliberately ONE class here rather than two: every consumer
     * ({@code ContraptionSeatListener}'s sit/stand/quit/death paths,
     * {@code ContraptionEntity#carrySeatedRiders}) treats a seat as a seat and needed no change at all to
     * gain block seats.
     *
     * <p>Real player interaction ({@code ContraptionSeatListener}) redirects a click into occupying a free
     * slot exactly like a rider captured mid-sit at assembly time: the player is carried at this slot's
     * fixed bearing-relative offset via {@code ContraptionState#addSeatedRider}/
     * {@code ContraptionEntity#carrySeatedRiders}, the SAME mechanism, not a new one. See
     * {@code ContraptionFurnitureCapture}'s javadoc for why a captured seat is never a real
     * followed/mounted entity, and {@link ContraptionBlockSeats}'s for why a captured seat BLOCK can never
     * use CraftEngine's own native {@code spawnSeat} path either.
     *
     * <p>{@code bearingLocalOffset} already sits in the bearing's yaw-0 basis — the seat's raw config-local
     * {@code SeatConfig#position()} rotated by its SOURCE's own yaw (the furniture's placement yaw, or the
     * block's {@code facing}-derived rotation) and combined with that source's local offset, per
     * {@link ContraptionSeatMath}. So it feeds straight into {@code ContraptionMath#renderPosition}/
     * {@code ContraptionState#addSeatedRider} with no further correction.
     */
    public static final class SeatSlot {
        /**
         * The captured furniture piece this seat came from, or {@code null} for a seat declared by a
         * captured BLOCK ({@code seat_block} — see {@link ContraptionBlockSeats}), which has no furniture
         * behind it at all. Nothing outside this class reads it today; it is kept for parity/debugging.
         */
        public final ContraptionFurniture furniture;
        private final Vec3 bearingLocalOffset;
        private final float yawOffsetDegrees;
        /**
         * Carried verbatim from the source {@code SeatConfig#limitPlayerRotation()} — CraftEngine uses it to
         * decide whether a seated rider's view is clamped to the seat's facing (its own
         * {@code BukkitSeat#spawnSeatEntityForPlayer} picks a rotation-clamping seat ENTITY TYPE from it).
         * This project's seats are always the {@code ArmorStand} mount ({@code ContraptionSeatMount}), which
         * doesn't clamp, so nothing consumes this yet — it is threaded through so the flag survives capture
         * and a future rotation-clamp doesn't have to re-plumb the whole seat pipeline to find it.
         */
        private final boolean limitPlayerRotation;
        private UUID occupant; // null = free

        private SeatSlot(ContraptionFurniture furniture, Vec3 bearingLocalOffset, float yawOffsetDegrees,
                boolean limitPlayerRotation) {
            this.furniture = furniture;
            this.bearingLocalOffset = bearingLocalOffset;
            this.yawOffsetDegrees = yawOffsetDegrees;
            this.limitPlayerRotation = limitPlayerRotation;
        }

        public Vec3 bearingLocalOffset() {
            return bearingLocalOffset;
        }

        /** See {@link #limitPlayerRotation} — carried through from the source {@code SeatConfig}, unused so far. */
        public boolean limitPlayerRotation() {
            return limitPlayerRotation;
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

        /**
         * This seat's current real-world (snap-to) position under the bearing's live FULL transform.
         *
         * <p><b>2026-07-16 fix — the reported seat drift.</b> This used to call the yaw-only
         * {@code renderPosition(local, bearing, yaw)} overload, which pins {@code pitch = roll = 0} and
         * {@code scale = 1}. Every OTHER cell of a contraption — blocks, colliders, furniture visuals —
         * projects through the full pose, so on any scaled or tilted contraption the seat alone was computed
         * against a different transform than the structure it belongs to: the sofa rendered (and its
         * collider sat) at the scaled/tilted position while the seat, the mount entity tracking it every
         * tick, and the rider on top of it were all placed at the unscaled, level one — i.e. the seat
         * visibly drifted away from its own furniture, by more the further it sat from the bearing pivot.
         * Threading the real {@code pitch}/{@code roll}/{@code scale} puts a seat back on exactly the
         * {@code renderPosition} mapping its own sofa uses. At {@code scale == 1 && pitch == 0 && roll == 0}
         * this is byte-for-byte the old call (the yaw-only overload is literally defined as a delegation to
         * these arguments — see {@code ContraptionMath}'s overload chain), so an ordinary contraption is
         * unaffected.
         */
        public Vec3 currentRealPosition(Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
                double rollRadians, double scale) {
            return ContraptionMath.renderPosition(bearingLocalOffset, bearingWorldPos, yawRadians, pitchRadians,
                    rollRadians, scale);
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
