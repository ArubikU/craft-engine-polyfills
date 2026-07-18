package dev.arubik.craftengine.contraption.render;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.BaseEntityData;
import net.momirealms.craftengine.bukkit.entity.data.monster.ShulkerData;
import net.momirealms.craftengine.core.entity.player.Player;

import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * Packet-only {@code SHULKER}-based hitbox layer, ADDITIONAL to (not a replacement for)
 * {@link ContraptionHitboxSwarm}'s {@code INTERACTION} slots — added per the 2026-07-02 session
 * request to mirror how real CraftEngine furniture positions its own shulker collider at an
 * arbitrary FRACTIONAL local offset (not grid-snapped to a block's 0,0,0 corner), which reads
 * better for a contraption cell that's mid-move/mid-rotation (see class javadoc motivation:
 * "util cuando un coso esta que se mueve").
 *
 * <p><b>What decompiling CraftEngine's own furniture collider revealed</b> (see
 * {@code net.momirealms.craftengine.bukkit.entity.furniture.hitbox.ShulkerFurnitureHitbox} /
 * {@code ShulkerFurnitureHitboxConfig} in {@code testserver/plugins/craft-engine.jar}): the
 * shulker CraftEngine spawns for a furniture piece is <b>packet-only</b> — its {@code show}/
 * {@code hide} methods only ever call {@code Player#sendPacket} with a bundled
 * {@code ClientboundAddEntityPacket} (one {@code ITEM_DISPLAY} anchor + one {@code SHULKER})
 * plus a {@code ClientboundSetEntityDataPacket}; it is never added to the real server
 * {@code Level}. It has NO real server-side collision physics of its own — the shulker you see
 * is purely cosmetic/visual, exactly like every other swarm in this package.
 *
 * <p>The REAL pushback/standing-collision physics real CraftEngine furniture gets comes from a
 * completely separate object: {@code AbstractFurnitureHitBox#createCollider} builds a
 * {@code BukkitCollider}, which (per {@code ColliderType}) spawns a genuine NMS
 * {@code Interaction} (or {@code Boat}) entity via {@code FastNMS.createCollisionInteraction}/
 * {@code createCollisionBoat} — a REAL entity actually added to the level, separate from the
 * visual shulker packets. Mirroring that exactly (a second, real, server-spawned collision
 * entity) is a materially bigger change than this task's ask; consistent with this project's
 * established packet-only convention (see {@link ContraptionHitboxSwarm}'s own javadoc, which
 * made the identical call for its INTERACTION slots), this class stays packet-only too — the
 * shulker here is the same kind of real-BLOCK-SHAPED visual box CraftEngine shows, and
 * "collision" is still this class's own manual standing-check math (see
 * {@link #isStandingOnFootprint}), just against a shulker-shaped 1×1×1 box (scaled via the
 * {@code Scale} attribute, matching {@code ShulkerFurnitureHitboxConfig}'s own scale handling)
 * instead of Interaction's continuous width/height metadata.
 *
 * <h2>Distance level-of-detail</h2>
 * A shulker's box is a CUBE ({@code scale} on every axis — see {@link #addSlot}'s decompiled
 * derivation), so no choice of parameters makes ONE of them a staircase, a door, or even a slab's
 * true footprint. The only lever is HOW MANY are spent on a cell, and the only thing that makes
 * spending more worthwhile is a viewer close enough to feel the difference. Cells registered via
 * {@link #setCell} therefore carry their real {@code VoxelShape} boxes, and each viewer
 * independently resolves each cell to a cube budget from their distance to it —
 * {@link #BUDGET_FAR} / {@link #BUDGET_MID} / {@link #BUDGET_NEAR} — which {@link ShulkerBoxFit}
 * turns into cubes covering the ACTUAL boxes (a slab's flat region, a stair's two regions), not an
 * octree of the enclosing cell. The same cell is consequently a different set of entities to
 * different players at the same instant; {@link #renderCells} explains what that costs and why the
 * {@link Slot} model already supported it.
 *
 * <p>Positioning: like {@code ShulkerFurnitureHitboxConfig}, a {@link Slot} stores a continuous
 * bearing-local offset ({@code lx,ly,lz}, NOT snapped to a block's integer corner) plus its own
 * {@code scale} — callers push arbitrary fractional offsets via {@link #addSlot}, there is no
 * auto-derivation from the captured level here (unlike {@code ContraptionHitboxSwarm}'s
 * grid-based {@code autoSlots}) since this class exists specifically for the
 * non-grid-snapped case.
 */
public final class ContraptionShulkerColliderSwarm {

    /**
     * Caller-pushed fixed-geometry slots ({@link #addSlot}) — one shulker each, shown to every
     * viewer regardless of distance. Distinct from {@link #cells}: a fixed slot's caller states the
     * exact box it wants, so there is nothing for the LOD fitter to subdivide.
     */
    private final Map<Object, Slot> fixedSlots = new LinkedHashMap<>();

    /**
     * Whether the body is currently turned off the world grid — set each render from the live transform.
     *
     * <p>Only then is it worth spending the near tiers' budget on a plain full block: axis-aligned, one
     * cube IS the block and more cubes buy nothing. See {@link ShulkerBoxFit#fit(java.util.List, int, boolean)}.
     */
    private boolean offAxis;

    /** Real-geometry cells ({@link #setCell}), each resolved to a per-viewer LOD tier every render. */
    private final Map<Object, CellGroup> cells = new LinkedHashMap<>();

    private final Set<UUID> currentRiders = new HashSet<>();

    /**
     * A viewer who must never be sent these colliders — see {@link #setExcludedViewer}.
     */
    private volatile UUID excludedViewer;

    /**
     * Withholds this swarm's colliders from one viewer, despawning any they already have.
     *
     * <h2>Why a viewer must sometimes be excluded</h2>
     * A shulker is SOLID to movement: {@code Shulker#canBeCollidedWith} returns {@code isAlive()}, so a
     * client treats these packet-only colliders as real obstacles. That is the entire point of them —
     * it is what makes a contraption's deck walkable — but it also means they obstruct anything that
     * client is moving.
     *
     * <p>That breaks a ridden Happy Ghast specifically. {@code HappyGhast#getControllingPassenger}
     * returns its rider, so the ghast is driven by the RIDER'S CLIENT, which simulates its movement
     * against every entity it knows about, these colliders included. The ghast is inside its own
     * contraption and the contraption follows it a tick behind, so the ghast spent every tick shoving
     * against its own lagging walls and crawled. Excluding the anchor entity server-side cannot help:
     * the collision is not happening on the server.
     *
     * <p>The rider is seated, so they lose nothing — a passenger does not need the deck to be solid.
     * Every other viewer keeps full colliders, so anyone standing on the contraption still stands on it.
     */
    public void setExcludedViewer(UUID excludedViewer) {
        this.excludedViewer = excludedViewer;
    }

    /**
     * Debug toggle ({@code /cep show shulkers}): when true the collider shulkers drop their invisible
     * flag and render as real shulker models, so the LOD tiers can actually be SEEN — which is the
     * only practical way to check that a stair or a slab is being covered by the right cubes rather
     * than trusting the fitter's unit tests.
     */
    public static volatile boolean SHOW_COLLIDERS = false;

    /**
     * Bumped whenever {@link #SHOW_COLLIDERS} flips. A slot compares this against its own last-seen
     * value and marks itself data-dirty, which is what pushes the new visibility to shulkers that are
     * ALREADY spawned — the flag lives in the entity-data packet, so without a resend a toggle would
     * only affect colliders spawned after it.
     */
    private static final AtomicInteger VISIBILITY_GENERATION = new AtomicInteger();

    /** Flips the debug visibility and forces every live collider to be re-sent. */
    public static boolean toggleShowColliders() {
        boolean now = !SHOW_COLLIDERS;
        SHOW_COLLIDERS = now;
        VISIBILITY_GENERATION.incrementAndGet();
        return now;
    }

    /**
     * Cube budget for a cell nobody is near: one shulker, exactly the pre-LOD behaviour (see
     * {@link ShulkerBoxFit#fit}'s single-cube path). A collider a player cannot reach only has to
     * exist for the moment they arrive; spending entities on its shape is pure waste.
     */
    public static final int BUDGET_FAR = 1;

    /**
     * Cube budget at conversational range. Four cubes is the first count that buys real shape:
     * it tiles a slab's true 1×0.5×1 region exactly (2×1×2 cubes of side 0.5), where the single
     * far cube covers only the middle 0.5×0.5 of its footprint.
     */
    public static final int BUDGET_MID = 4;

    /**
     * Cube budget within arm's reach — the granularity a player can actually feel. Sixteen is
     * enough for the shapes this exists for: a stair's two regions fit in 6 cubes, a bottom slab in
     * 4, and a door's thin panel tiles 4×4×1 at side 0.25. It is also the point of diminishing
     * returns against {@link #MAX_LOD_SHULKERS_PER_VIEWER} — a 64-cube tier would let two cells
     * exhaust the whole per-viewer entity budget.
     */
    public static final int BUDGET_NEAR = 16;

    /**
     * No collider at all. Past {@link #FAR_EXIT} a player cannot touch the cell this tick or next, so
     * a solid hitbox there buys nothing and costs a fake entity per cell per viewer — by far the
     * largest share of the swarm's entity budget, since most cells of most contraptions are far from
     * most players most of the time.
     *
     * <p>This is a real tier, not an absence: a viewer leaving range must be sent an explicit despawn
     * (a fake entity is never untracked by a client on its own), which is exactly what falling to this
     * tier does.
     */
    public static final int BUDGET_NONE = 0;

    /**
     * Distance at or under which a cell upgrades to {@link #BUDGET_NEAR}.
     *
     * <p>Deliberately 1.5 rather than 1.0: a player's own body is ~0.6 wide and their reach starts
     * before contact, so the highest tier has to already be in place BEFORE they arrive. At 1.0 the
     * upgrade landed at the same instant the player reached the cell — the tier flipped exactly while
     * they were touching it, which is precisely when a collider changing shape underfoot is felt.
     */
    public static final double NEAR_ENTER = 1.5;

    /**
     * Distance past which a {@link #BUDGET_NEAR} cell drops back down. The gap to
     * {@link #NEAR_ENTER} is the hysteresis band: a player standing exactly on a threshold jitters
     * by well under half a block per tick, so a band this wide cannot be crossed twice in quick
     * succession — without it every such player would re-spawn and re-despawn 16 fake entities per
     * cell every tick. Widened alongside {@link #NEAR_ENTER} so approaching a contraption cannot
     * strobe the tier.
     */
    public static final double NEAR_EXIT = 2.5;

    /** Distance at or under which a cell upgrades to {@link #BUDGET_MID} — conversational range. */
    public static final double MID_ENTER = 3.0;

    /** Distance past which a {@link #BUDGET_MID} cell drops to {@link #BUDGET_FAR}; see {@link #NEAR_EXIT} for the band's purpose. */
    public static final double MID_EXIT = 4.0;

    /**
     * Distance at or under which a cell is worth a single blunt collider at all. Inside this a player
     * can still walk into the contraption within a tick or two, so something solid has to be there.
     */
    public static final double FAR_ENTER = 4.0;

    /**
     * Distance past which a cell drops to {@link #BUDGET_NONE} and is despawned outright. Beyond this
     * a player cannot reach the cell before the next tier re-evaluation puts a collider back, so
     * there is nothing to collide with and nothing to lose.
     */
    public static final double FAR_EXIT = 5.0;

    /**
     * Ceiling on the shulkers one viewer may be sent for one contraption's LOD-upgraded cells.
     * Cells are considered nearest-first and anything past the ceiling is forced to
     * {@link #BUDGET_FAR}, so the degradation is "the far side of a big contraption gets coarse",
     * never "entity count explodes". 256 still affords {@link #BUDGET_NEAR} to the 16 nearest
     * cells — more than a standing player can be in contact with at once — while staying under the
     * scale at which vanilla's own per-player entity tracking starts to cost real bandwidth.
     * {@link #BUDGET_FAR} cells are not counted against it: one collider per cell is the floor this
     * layer has always had, and dropping below it would open holes.
     */
    public static final int MAX_LOD_SHULKERS_PER_VIEWER = 256;

    /** Throttle for the {@link #MAX_LOD_SHULKERS_PER_VIEWER} warning — the cap is hit every tick once hit at all. */
    private static final long CAP_WARN_INTERVAL_MS = 60_000L;
    private static volatile long lastCapWarnMs = 0L;

    /**
     * Registers a fractional bearing-local offset slot. {@code scale} follows vanilla's own
     * shulker {@code Scale} attribute convention (1.0 = a normal full-block 1×1×1 box);
     * {@code key} identifies this slot across rebuilds so an unchanged slot REUSES its existing
     * entity id / {@code shownTo} tracking instead of despawning+respawning (same diff-based
     * reuse pattern {@code ContraptionHitboxSwarm#rebuild}'s javadoc documents) — pass a stable
     * caller-owned key (e.g. the furniture cell's own id, or a fixed slot index).
     *
     * <p>A fixed slot is exempt from the distance LOD {@link #setCell} cells get: its caller states
     * the exact box it wants rather than handing over geometry to approximate, so there is nothing
     * to subdivide and no budget to spend.
     *
     * <p>Equivalent to {@link #addSlot(Object, double, double, double, float, net.minecraft.core.Direction, int)}
     * with {@code attachFace=DOWN, peek=0} (the previous always-full-cube behavior) — kept for
     * callers that don't need shape-aware sizing.
     */
    public void addSlot(Object key, double lx, double ly, double lz, float scale) {
        addSlot(key, lx, ly, lz, scale, net.minecraft.core.Direction.DOWN, 0);
    }

    /**
     * Full-parameter slot registration (2026-07-02 session — "genera shulker hitbox dependiendo
     * del bounding box ... puedes usar peek para ahorrar shulker box's"). {@code attachFace} is
     * vanilla's {@code ShulkerData.AttachFace} — the box grows AWAY from this face (see this
     * class's own decompile writeup, below, for the exact math); {@code peek} is the raw
     * {@code ShulkerData.RawPeekAmount} byte, 0-100.
     *
     * <p><b>Decompiled peek-to-collision relationship</b> (verified via {@code javap} against
     * this project's mapped {@code net.minecraft.world.entity.monster.Shulker} — NOT guessed):
     * {@code Shulker#makeBoundingBox} computes {@code physicalPeek = getPhysicalPeek(currentPeekAmount)}
     * where {@code currentPeekAmount} tracks {@code getRawPeekAmount()/100f} once its
     * open/close animation settles, then calls
     * {@code getProgressAabb(scale, attachFace.getOpposite(), physicalPeek, entityPos)} ->
     * {@code getProgressDeltaAabb(scale, direction, -1.0f, physicalPeek, pos)}. That method
     * builds the base box {@code [-scale/2, 0, -scale/2]}-{@code [scale/2, scale, scale/2]}
     * (a bottom-anchored {@code scale}×{@code scale}×{@code scale} cube — "bottom" meaning the
     * ATTACH face, since {@code direction = attachFace.getOpposite()}), then
     * {@code expandTowards(direction * max(-1,physicalPeek) * scale)} and
     * {@code contract(direction * -(1+min(-1,physicalPeek)) * scale)}. Since
     * {@code physicalPeek ∈ [0,1]} (never negative once settled), {@code max(-1,physicalPeek)=physicalPeek}
     * and {@code min(-1,physicalPeek)=-1} so the contract term is always {@code 1+(-1)=0} (no-op).
     * Net effect: peek EXPANDS the box outward along {@code attachFace.getOpposite()} (i.e. AWAY
     * from the attach face) by {@code physicalPeek*scale}, where {@code physicalPeek=
     * 0.5 - sin((0.5+peek/100)*PI)*0.5} ({@code getPhysicalPeek}, decompiled) — at
     * {@code peek=0}, {@code physicalPeek=0} (no growth, exactly the classic 1×1×1 box); at
     * {@code peek=100} ({@code peek/100=1.0}), {@code physicalPeek=0.5-sin(1.5*PI)*0.5=0.5-(-0.5)=1.0},
     * i.e. a FULL extra {@code scale} of length added on top of the base cube — a
     * {@code peek=100} shulker attached DOWN (growing UP, opposite of DOWN) with {@code scale=1}
     * spans a full 2 blocks of height, confirming peek can cover an extra whole block per 100
     * units with NO second entity. The near (attach) face never contracts, so the box's base
     * stays anchored exactly at the attach point regardless of peek — this is what lets
     * {@link ContraptionHitboxSwarm#rebuild} stack a 2-tall column under ONE shulker (attachFace
     * DOWN at the bottom cell's local offset, peek=100) instead of two separate scale-1 shulkers.
     */
    public void addSlot(Object key, double lx, double ly, double lz, float scale,
            net.minecraft.core.Direction attachFace, int peek) {
        Slot existing = fixedSlots.get(key);
        if (existing != null) {
            existing.updateOffset(lx, ly, lz, scale, attachFace, peek);
            return;
        }
        fixedSlots.put(key, new Slot(key, lx, ly, lz, scale, attachFace, peek));
    }

    /**
     * Registers/updates one cell's REAL collision geometry, letting this class choose how many
     * shulkers to spend on it per viewer per tick (see {@link #renderCells}). {@code localBoxes}
     * are the cell's {@code VoxelShape} boxes already merged, {@code optimize()}d and translated
     * into the bearing-local frame — the same frame {@link #addSlot}'s {@code lx/ly/lz} live in.
     *
     * <p>Callers are expected to hand back the SAME list instance for an unchanged cell (the cell's
     * blockstate is what its geometry depends on, and blockstates are interned): the identity check
     * below then makes a per-tick re-registration free. A genuinely changed shape drops every
     * already-fitted tier, despawning its shulkers on the next render — the fit is derived from the
     * geometry, so it cannot outlive it.
     */
    public void setCell(Object key, List<AABB> localBoxes, List<Player> viewers) {
        CellGroup group = cells.get(key);
        if (group == null) {
            cells.put(key, new CellGroup(localBoxes));
            return;
        }
        group.setBoxes(localBoxes, viewers);
    }

    /**
     * Drops any slot or cell whose key is not in {@code liveKeys}, despawning it for every viewer
     * first.
     */
    public void prune(Set<Object> liveKeys, List<Player> viewers) {
        java.util.Iterator<Map.Entry<Object, Slot>> fixedIt = fixedSlots.entrySet().iterator();
        while (fixedIt.hasNext()) {
            Map.Entry<Object, Slot> e = fixedIt.next();
            if (!liveKeys.contains(e.getKey())) {
                for (Player p : viewers) {
                    e.getValue().despawn(p);
                }
                fixedIt.remove();
            }
        }
        java.util.Iterator<Map.Entry<Object, CellGroup>> cellIt = cells.entrySet().iterator();
        while (cellIt.hasNext()) {
            Map.Entry<Object, CellGroup> e = cellIt.next();
            if (!liveKeys.contains(e.getKey())) {
                e.getValue().despawnAll(viewers);
                cellIt.remove();
            }
        }
    }

    public void clear(List<Player> viewers) {
        for (Slot s : fixedSlots.values()) {
            for (Player p : viewers) {
                s.despawn(p);
            }
        }
        fixedSlots.clear();
        for (CellGroup g : cells.values()) {
            g.despawnAll(viewers);
        }
        cells.clear();
        currentRiders.clear();
    }

    /**
     * Render every slot at the bearing's current world-space position, rotated around the
     * bearing by {@code yawRadians} (2026-07-02 session — see {@code ContraptionHitboxSwarm
     * #render}'s javadoc for the full "rotate doesn't affect hitboxes" bug this fixes). {@code
     * moved} — see {@code ContraptionEntity#render} — skips resending position-sync packets when
     * the contraption's transform hasn't changed since the last call.
     */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, boolean moved) {
        render(viewers, bearingWorldPos, yawRadians, 1.0, moved);
    }

    /**
     * Scale-aware {@link #render(List, Vec3, double, boolean)} (roadmap item #9 — per-contraption
     * {@code scale}): each slot's local offset is projected through
     * {@link ContraptionMath#renderPosition(Vec3, Vec3, double, double, double)} with {@code scale} (so the
     * colliders spread apart with the visual) AND its shulker {@code Scale} attribute is multiplied by
     * {@code scale} (so each box grows with the model) — see {@link Slot#render}. At {@code scale == 1.0}
     * this reduces byte-for-byte to the pre-scale behaviour (renderPosition's scale-1 fast path; the
     * {@code scale*1.0} attribute identity; no extra scale-resend packet).
     */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double scale, boolean moved) {
        render(viewers, bearingWorldPos, yawRadians, 0.0, scale, moved);
    }

    /**
     * Pitch-aware {@link #render(List, Vec3, double, double, boolean)} (roadmap item #9 phase 5 — TIPPING):
     * projects each slot through the SAME
     * {@link ContraptionMath#renderPosition(Vec3, Vec3, double, double, double)} the block_display cells use,
     * now with the identical {@code pitchRadians} (previously hard-coded {@code 0.0}), so a tipping body's
     * shulker colliders follow the tilted cell positions instead of staying flat. At {@code pitch == 0} this
     * is byte-for-byte the pre-pitch behaviour (renderPosition's pitch-0 fast path).
     */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            double scale, boolean moved) {
        render(viewers, bearingWorldPos, yawRadians, pitchRadians, 0.0, scale, moved);
    }

    /**
     * Pitch+ROLL-aware {@link #render(List, Vec3, double, double, double, boolean)} (roadmap item #9 phase 6 —
     * ROLL): projects each slot through the SAME
     * {@link ContraptionMath#renderPosition(Vec3, Vec3, double, double, double, double)} the block_display cells
     * use, now with the identical {@code pitchRadians} AND {@code rollRadians}, so a body leaning toward its
     * heavy side keeps its shulker colliders on the tilted cell positions. At {@code pitch == 0 && roll == 0}
     * this is byte-for-byte the pre-tilt behaviour (renderPosition's tilt-0 fast path).
     */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            double rollRadians, double scale, boolean moved) {
        for (Slot slot : fixedSlots.values()) {
            Vec3 pos = dev.arubik.craftengine.contraption.ContraptionMath.renderPosition(
                    slot.localCenter(), bearingWorldPos, yawRadians, pitchRadians, rollRadians, scale);
            // A fixed slot has no LOD tier to fall through, so the exclusion is applied as an explicit
            // wanted-set: every viewer but the excluded one. Passing the set (rather than null) is what
            // makes Slot#render despawn it for them.
            slot.render(viewers, wantedExcluding(viewers), pos.x, pos.y, pos.z, scale, moved);
        }
        if (!cells.isEmpty()) {
            renderCells(viewers, bearingWorldPos, yawRadians, pitchRadians, rollRadians, scale, moved);
        }
    }

    /**
     * Resolves every {@link #setCell} cell to a per-VIEWER LOD tier and renders each tier's shulkers
     * to exactly the viewers currently in it.
     *
     * <p><b>Why the tier has to be per-viewer, and what that costs.</b> The same cell must appear as
     * one blunt cube to a player across the field and as sixteen stair-shaped cubes to the player
     * standing on it, simultaneously. Since these are packet-only entities, that is achievable — but
     * only because a {@link Slot} already tracks its own {@code shownTo} set per viewer. The
     * addition here is that a slot is no longer implicitly wanted by everyone in {@code viewers}:
     * {@link Slot#render} now takes the subset that should see it and DESPAWNS it for the rest. That
     * explicit despawn is mandatory, not tidiness — a fake entity is never untracked by the client
     * on its own, so a viewer crossing a tier boundary would otherwise keep the old tier's colliders
     * forever, on top of the new tier's.
     *
     * <p>Each tier's fitted cubes and slots are built lazily on first use and cached on the
     * {@link CellGroup} (keyed by budget), so the {@code VoxelShape} fit runs once per cell per tier
     * per geometry change — never per tick. Per tick this costs one {@code renderPosition} per cell
     * for the tier anchor plus one distance test per (cell, viewer).
     */
    private void renderCells(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            double rollRadians, double scale, boolean moved) {
        offAxis = isOffAxis(yawRadians) || isOffAxis(pitchRadians) || isOffAxis(rollRadians);
        List<CellGroup> live = new ArrayList<>(cells.values());
        // Before ANY cache read: assignTiers below reads cubesFor for the LOD cap, and the tier loop
        // reads slotsFor. Both must see caches already reconciled with this pass's offAxis.
        for (CellGroup g : live) {
            g.syncOffAxis(offAxis, viewers);
        }
        Map<CellGroup, Vec3> anchors = new IdentityHashMap<>();
        for (CellGroup g : live) {
            anchors.put(g, dev.arubik.craftengine.contraption.ContraptionMath.renderPosition(
                    g.anchor, bearingWorldPos, yawRadians, pitchRadians, rollRadians, scale));
        }

        Map<CellGroup, Map<Integer, Set<UUID>>> wanted = new IdentityHashMap<>();
        for (CellGroup g : live) {
            wanted.put(g, new HashMap<>());
        }
        Set<UUID> onlineIds = new HashSet<>();
        for (Player p : viewers) {
            UUID id = uuidOf(p);
            Vec3 viewerPos = positionOf(p);
            Vec3 eyePos = eyePositionOf(p);
            if (id == null || viewerPos == null) {
                continue;
            }
            onlineIds.add(id);
            assignTiers(live, anchors, id, viewerPos, eyePos, scale, wanted);
        }
        for (CellGroup g : live) {
            // A viewer who quit (or whose platform position stopped resolving) must not keep a
            // hysteresis tier pinned forever — their slot tracking is already dropped by
            // Slot#render's own retainAll, so the tier state has to follow it.
            g.viewerTier.keySet().retainAll(onlineIds);
        }

        for (CellGroup g : live) {
            Map<Integer, Set<UUID>> byTier = wanted.get(g);
            Set<Integer> touched = new HashSet<>(byTier.keySet());
            touched.addAll(g.tierSlots.keySet());
            for (int tier : touched) {
                Set<UUID> want = byTier.getOrDefault(tier, Set.of());
                List<Slot> tierSlots = g.tierSlots.get(tier);
                if (tierSlots == null) {
                    if (want.isEmpty()) {
                        continue;
                    }
                    tierSlots = g.slotsFor(tier, offAxis);
                }
                if (want.isEmpty() && !g.anyTracked(tierSlots)) {
                    // Nobody is in this tier and nobody still sees its shulkers — free the slots
                    // (and their entity ids) rather than paying a render pass for them every tick.
                    g.tierSlots.remove(tier);
                    continue;
                }
                for (Slot slot : tierSlots) {
                    Vec3 pos = dev.arubik.craftengine.contraption.ContraptionMath.renderPosition(
                            slot.localCenter(), bearingWorldPos, yawRadians, pitchRadians, rollRadians,
                            scale);
                    slot.render(viewers, want, pos.x, pos.y, pos.z, scale, moved);
                }
                if (want.isEmpty()) {
                    g.tierSlots.remove(tier); // the pass above just despawned the last tracker
                }
            }
        }
    }

    /**
     * Picks {@code viewer}'s tier for every cell and records it into {@code wanted}, applying both
     * the hysteresis band (see {@link #tierFor}) and {@link #MAX_LOD_SHULKERS_PER_VIEWER}. The cap
     * walks the upgraded cells nearest-first so what gets sacrificed is always the furthest thing
     * the viewer could have felt.
     */
    private void assignTiers(List<CellGroup> live, Map<CellGroup, Vec3> anchors, UUID viewer, Vec3 viewerPos,
            Vec3 eyePos, double scale, Map<CellGroup, Map<Integer, Set<UUID>>> wanted) {
        Map<CellGroup, Integer> tierOf = new IdentityHashMap<>();
        Map<CellGroup, Double> distOf = new IdentityHashMap<>();
        List<CellGroup> upgraded = new ArrayList<>();
        boolean excluded = viewer.equals(this.excludedViewer);
        for (CellGroup g : live) {
            Vec3 a = anchors.get(g);
            double dist = viewerDistance(a, viewerPos, eyePos);
            // An excluded viewer is simply always out of range. Routing it through the tier system
            // rather than skipping the loop means the existing BUDGET_NONE path despawns whatever they
            // already had — a client never untracks a fake entity on its own.
            int tier = excluded ? BUDGET_NONE
                    : tierFor(g.viewerTier.getOrDefault(viewer, BUDGET_NONE), dist, scale);
            tierOf.put(g, tier);
            distOf.put(g, dist);
            if (tier != BUDGET_FAR && tier != BUDGET_NONE) {
                upgraded.add(g);
            }
        }
        if (!upgraded.isEmpty()) {
            upgraded.sort(Comparator.comparingDouble(distOf::get));
            int used = 0;
            boolean capped = false;
            for (CellGroup g : upgraded) {
                int cost = g.cubesFor(tierOf.get(g), offAxis).size();
                if (used + cost > MAX_LOD_SHULKERS_PER_VIEWER) {
                    tierOf.put(g, BUDGET_FAR);
                    capped = true;
                } else {
                    used += cost;
                }
            }
            if (capped) {
                warnCapped(live.size());
            }
        }
        for (CellGroup g : live) {
            int tier = tierOf.get(g);
            g.viewerTier.put(viewer, tier);
            wanted.get(g).computeIfAbsent(tier, t -> new HashSet<>()).add(viewer);
        }
    }

    /**
     * The tier a cell at {@code dist} should hold, given the tier it currently holds for that
     * viewer. Every downgrade uses the {@code *_EXIT} threshold and every upgrade the
     * {@code *_ENTER} one, so the tier a viewer sits in is sticky across the band between them —
     * this is the whole hysteresis mechanism (see {@link #NEAR_EXIT}).
     *
     * <p><b>Thresholds are multiplied by the contraption's {@code scale}.</b> They are stated for a
     * scale-1 cell, where one block from the cell's centre means the player is about to touch its
     * face. On a scale-2 contraption each cell is two blocks across, so its face is already a full
     * block away from its own centre — a fixed 1-block threshold would only ever upgrade once the
     * player was standing INSIDE the cell, i.e. exactly when it is too late to matter. Scaling the
     * distances keeps the tier tied to how large the cell actually looks, which is the thing that
     * decides whether a stair reading as a cube is noticeable.
     */
    static int tierFor(int currentTier, double dist, double scale) {
        double s = scale <= 0.0 ? 1.0 : scale;
        double nearEnter = NEAR_ENTER * s;
        double nearExit = NEAR_EXIT * s;
        double midEnter = MID_ENTER * s;
        double midExit = MID_EXIT * s;
        double farEnter = FAR_ENTER * s;
        double farExit = FAR_EXIT * s;
        // Each branch is "the tier I am already in, held until its own EXIT" followed by the ordinary
        // ENTER ladder — that asymmetry IS the hysteresis, and it is why the tier a viewer holds has
        // to be an input rather than a pure function of distance.
        if (currentTier == BUDGET_NEAR) {
            if (dist <= nearExit) {
                return BUDGET_NEAR;
            }
            if (dist <= midExit) {
                return BUDGET_MID;
            }
            return dist <= farExit ? BUDGET_FAR : BUDGET_NONE;
        }
        if (currentTier == BUDGET_MID) {
            if (dist <= nearEnter) {
                return BUDGET_NEAR;
            }
            if (dist <= midExit) {
                return BUDGET_MID;
            }
            return dist <= farExit ? BUDGET_FAR : BUDGET_NONE;
        }
        if (currentTier == BUDGET_FAR) {
            if (dist <= nearEnter) {
                return BUDGET_NEAR;
            }
            if (dist <= midEnter) {
                return BUDGET_MID;
            }
            return dist <= farExit ? BUDGET_FAR : BUDGET_NONE;
        }
        if (dist <= nearEnter) {
            return BUDGET_NEAR;
        }
        if (dist <= midEnter) {
            return BUDGET_MID;
        }
        return dist <= farEnter ? BUDGET_FAR : BUDGET_NONE;
    }

    private static void warnCapped(int cellCount) {
        long now = System.currentTimeMillis();
        if (now - lastCapWarnMs < CAP_WARN_INTERVAL_MS) {
            return;
        }
        lastCapWarnMs = now;
        org.bukkit.Bukkit.getLogger().warning("[Contraption] shulker collider LOD hit the per-viewer cap ("
                + MAX_LOD_SHULKERS_PER_VIEWER + ") on a " + cellCount
                + "-cell contraption; furthest upgraded cells fall back to one collider each.");
    }

    /** The real-world position of {@code player}, or null if their platform player can't be resolved. */
    /**
     * Every viewer except {@link #excludedViewer}, or {@code null} when nobody is excluded — {@code null}
     * is Slot#render's "wanted by everyone" fast path, so an ordinary contraption allocates nothing.
     */
    private Set<UUID> wantedExcluding(List<Player> viewers) {
        UUID excluded = this.excludedViewer;
        if (excluded == null) {
            return null;
        }
        Set<UUID> wanted = new HashSet<>();
        for (Player p : viewers) {
            UUID id = uuidOf(p);
            if (id != null && !id.equals(excluded)) {
                wanted.add(id);
            }
        }
        return wanted;
    }

    /**
     * Whether {@code radians} is far enough from a multiple of 90 degrees for the body to be visibly
     * turned against the world grid.
     *
     * <p>At a multiple of 90 a rotated cube is still an axis-aligned cube — the block lands back on the
     * grid and one collider matches the render exactly. Only between those does an axis-aligned cube stop
     * describing the block, and the tolerance keeps a body resting at a hair off 90 from paying for a
     * subdivision nobody could feel.
     */
    private static boolean isOffAxis(double radians) {
        double degrees = Math.toDegrees(radians) % 90.0;
        if (degrees < 0.0) {
            degrees += 90.0;
        }
        return degrees > OFF_AXIS_TOLERANCE_DEGREES && degrees < 90.0 - OFF_AXIS_TOLERANCE_DEGREES;
    }

    /** How far off a 90-degree multiple still counts as grid-aligned. */
    private static final double OFF_AXIS_TOLERANCE_DEGREES = 1.0;

    private static Vec3 positionOf(Player player) {
        Object pp = player.platformPlayer();
        if (!(pp instanceof org.bukkit.entity.Player bukkitPlayer)) {
            return null;
        }
        org.bukkit.Location loc = bukkitPlayer.getLocation();
        return new Vec3(loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * The viewer's EYE position. Tiering off the feet alone misjudges a player by most of their own
     * height: standing on top of a contraption puts their feet on the deck but their eyes ~1.6 blocks
     * up, and walking up to a wall puts their eyes against a cell their feet are nowhere near. A
     * player is a ~1.8-block-tall column, so the distance that matters is to the NEAREST part of them
     * — see {@link #viewerDistance}.
     */
    private static Vec3 eyePositionOf(Player player) {
        Object pp = player.platformPlayer();
        if (!(pp instanceof org.bukkit.entity.Player bukkitPlayer)) {
            return null;
        }
        org.bukkit.Location loc = bukkitPlayer.getEyeLocation();
        return new Vec3(loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * Distance from a cell to the closest of the viewer's feet and eyes — the two ends of the column
     * a player actually occupies. Whichever end is nearer is the one that can touch the cell first, so
     * that end decides the tier.
     */
    private static double viewerDistance(Vec3 cell, Vec3 feet, Vec3 eyes) {
        double byFeet = feet == null ? Double.MAX_VALUE : cell.distanceTo(feet);
        double byEyes = eyes == null ? Double.MAX_VALUE : cell.distanceTo(eyes);
        return Math.min(byFeet, byEyes);
    }

    private static UUID uuidOf(Player player) {
        Object pp = player.platformPlayer();
        return pp instanceof org.bukkit.entity.Player b ? b.getUniqueId() : null;
    }

    /**
     * Live shulker count across every fixed slot and every cell tier that currently has slots built.
     * Cell tiers are per-viewer, so this is an upper bound on what any ONE viewer sees, not a
     * per-viewer figure.
     */
    public int slotCount() {
        int count = fixedSlots.size();
        for (CellGroup g : cells.values()) {
            for (List<Slot> tier : g.tierSlots.values()) {
                count += tier.size();
            }
        }
        return count;
    }

    /**
     * One {@link #setCell} cell: its real geometry, the cube fit + shulker slots cached per LOD
     * budget, and which tier each viewer currently sits in.
     */
    private static final class CellGroup {
        private List<AABB> boxes;
        /** Geometric centre of {@link #boxes}, bearing-local — the point every viewer's LOD distance is measured to. */
        private Vec3 anchor;
        private final Map<Integer, List<ShulkerBoxFit.Cube>> tierCubes = new HashMap<>();

        /** The {@link #offAxis} the cached {@link #tierCubes} were fitted under. */
        private boolean cachedOffAxis;
        private final Map<Integer, List<Slot>> tierSlots = new HashMap<>();
        private final Map<UUID, Integer> viewerTier = new HashMap<>();

        CellGroup(List<AABB> boxes) {
            setGeometry(boxes);
        }

        void setBoxes(List<AABB> next, List<Player> viewers) {
            if (boxes == next || boxes.equals(next)) {
                return; // unchanged blockstate — the cached fit is still exact
            }
            setGeometry(next);
            tierCubes.clear();
            // Every existing slot describes the OLD shape, so they all have to go (see #dropSlots for
            // why they cannot simply be cleared).
            dropSlots(viewers);
        }

        /**
         * Drops every tier's slots, despawning them for {@code viewers} first.
         *
         * <p><b>Never {@code tierSlots.clear()} directly.</b> A slot is a packet-only entity: the client
         * only ever forgets it when told to, and the ONLY thing that can tell it is the {@link Slot}
         * object itself (it owns the entity ids and the {@code shownTo} set). Clearing the map without
         * despawning strands whatever those slots were showing on every client permanently — no later
         * render pass can reach them, because a pass can only despawn slots it still holds. That is what
         * left ghost shulkers hanging in the air after a body crossed an off-axis boundary (2026-07-16 —
         * "el shulker no desaparece y se queda renderizado").
         */
        private void dropSlots(List<Player> viewers) {
            for (List<Slot> tier : tierSlots.values()) {
                for (Slot s : tier) {
                    for (Player p : viewers) {
                        s.despawn(p);
                    }
                }
            }
            tierSlots.clear();
        }

        /**
         * Applies a change of the contraption's off-axis state, re-fitting from scratch.
         *
         * <p>Called once per render pass per group, from {@link #renderCells}, which is the only place
         * that both knows {@code offAxis} and holds {@code viewers} — the two things this needs together.
         * It used to be done lazily inside {@link #cubesFor}/{@link #slotsFor} instead, which was wrong
         * twice over: those have no {@code viewers}, so the slot drop leaked ghosts; and whichever of the
         * two ran first flipped {@link #cachedOffAxis}, leaving the other unable to tell it was holding a
         * cache built for the opposite fit.
         */
        void syncOffAxis(boolean offAxis, List<Player> viewers) {
            if (offAxis == cachedOffAxis) {
                return;
            }
            cachedOffAxis = offAxis;
            // The fit itself differs off-axis (see ShulkerBoxFit#fit's fillBudget), so a body that starts
            // or stops turning must re-fit rather than keep cubes/slots shaped for the other case.
            tierCubes.clear();
            dropSlots(viewers);
        }

        private void setGeometry(List<AABB> next) {
            this.boxes = next;
            double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
            double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
            for (AABB b : next) {
                minX = Math.min(minX, b.minX);
                minY = Math.min(minY, b.minY);
                minZ = Math.min(minZ, b.minZ);
                maxX = Math.max(maxX, b.maxX);
                maxY = Math.max(maxY, b.maxY);
                maxZ = Math.max(maxZ, b.maxZ);
            }
            this.anchor = next.isEmpty() ? Vec3.ZERO
                    : new Vec3((minX + maxX) / 2.0, (minY + maxY) / 2.0, (minZ + maxZ) / 2.0);
        }

        List<ShulkerBoxFit.Cube> cubesFor(int budget, boolean offAxis) {
            if (budget <= BUDGET_NONE) {
                return List.of(); // out of range — no collider, so no cubes to fit
            }
            // No invalidation here — #syncOffAxis owns it, and has already run this pass.
            return tierCubes.computeIfAbsent(budget, b -> ShulkerBoxFit.fit(boxes, b, offAxis));
        }

        List<Slot> slotsFor(int budget, boolean offAxis) {
            // No invalidation here either — see #cubesFor / #syncOffAxis.
            List<ShulkerBoxFit.Cube> fitted = cubesFor(budget, offAxis);
            return tierSlots.computeIfAbsent(budget, b -> {
                List<ShulkerBoxFit.Cube> cubes = fitted;
                List<Slot> out = new ArrayList<>(cubes.size());
                for (ShulkerBoxFit.Cube c : cubes) {
                    // Every LOD cube is a plain bottom-anchored cube: attachFace DOWN with peek 0 is
                    // the parameter pair that makes a shulker's box exactly its Scale attribute (see
                    // #addSlot's decompiled peek derivation). Peek growth is deliberately unused —
                    // it can only stretch ONE box along ONE axis, which is precisely the shape a
                    // multi-cube fit already expresses better.
                    out.add(new Slot(c, c.centerX(), c.y0(), c.centerZ(), (float) c.size(), Direction.DOWN, 0));
                }
                return out;
            });
        }

        boolean anyTracked(List<Slot> tier) {
            for (Slot s : tier) {
                if (!s.shownTo.isEmpty()) {
                    return true;
                }
            }
            return false;
        }

        void despawnAll(List<Player> viewers) {
            dropSlots(viewers);
            tierCubes.clear();
            viewerTier.clear();
        }
    }

    /**
     * Manual standing-detection against each slot's shulker-shaped box (scaled 1×1×1), the same
     * packet-only "collision" approach {@link ContraptionHitboxSwarm#carryRiders} uses — real
     * per-tick carrying is left to the caller (this class only reports who's standing where; no
     * caller wires it into {@code PlayerCarry} yet — this layer is additive/opt-in per
     * {@link #addSlot}, unlike {@code ContraptionHitboxSwarm}'s always-on auto top-cell slots).
     *
     * <p>Scoped to {@link #fixedSlots}. {@link #setCell} cells are deliberately excluded: their box
     * set is per-viewer (see {@link #renderCells}), so there is no single answer this signature —
     * which has no viewer — could give for them. The viewer-independent shape-accurate standing
     * check against real cell geometry lives in {@code ContraptionHitboxSwarm}'s own
     * {@code standBottomY}/{@code standTopY} window instead.
     */
    public boolean isStandingOnFootprint(Vec3 playerPos, Vec3 bearingWorldPos) {
        for (Slot slot : fixedSlots.values()) {
            double half = slot.scale / 2.0;
            double minX = bearingWorldPos.x + slot.lx - half;
            double maxX = bearingWorldPos.x + slot.lx + half;
            double minZ = bearingWorldPos.z + slot.lz - half;
            double maxZ = bearingWorldPos.z + slot.lz + half;
            double topY = bearingWorldPos.y + slot.ly + slot.scale + slot.peekGrowth();
            if (playerPos.x >= minX - 0.3 && playerPos.x <= maxX + 0.3
                    && playerPos.z >= minZ - 0.3 && playerPos.z <= maxZ + 0.3
                    && playerPos.y >= topY - 0.3 && playerPos.y <= topY + 0.9) {
                return true;
            }
        }
        return false;
    }

    /**
     * One packet-only fake shulker collider: an invisible {@code ITEM_DISPLAY} anchor carrying a
     * {@code SHULKER} as its client-side PASSENGER, both at a continuous bearing-local offset, the
     * shulker scaled via the {@code Scale} attribute (vanilla 1.20.5+; ignored pre-1.20.5, matching
     * {@code ShulkerFurnitureHitboxConfig}'s own version-gated scale handling).
     *
     * <p><b>Why the anchor is mandatory, not decorative</b> (2026-07-16 — "las shulker nms box packet
     * no se escalan y acomodan cuando cambia o se mueve un phys contraption"). This class used to spawn
     * the {@code SHULKER} alone and move it directly, its javadoc asserting that the 2-entity spawn
     * {@code ShulkerFurnitureHitbox} uses was ceremony CraftEngine only needed for variant-swap
     * rendering. That assertion was wrong and was the bug. Verified against this project's own
     * {@code mappedServerJar.jar}, {@code net.minecraft.world.entity.monster.Shulker#setPos}:
     * <pre>
     *   if (this.isPassenger()) super.setPos(x, y, z);
     *   else super.setPos(Mth.floor(x) + 0.5, Mth.floor(y + 0.5), Mth.floor(z) + 0.5);
     * </pre>
     * A shulker that rides nothing SNAPS ITSELF TO THE BLOCK GRID — client-side too, since
     * {@code setPos} is where every inbound position lands: {@code ClientboundEntityPositionSyncPacket}
     * -> {@code Entity#moveOrInterpolateTo} -> (Shulker's {@code getInterpolation()} returns
     * {@code null}, so no interpolation handler intercepts) -> {@code setPos}. A lone shulker can
     * therefore only ever occupy a block centre: it cannot sit at a slab's or a rotated/tipped cell's
     * fractional offset, cannot follow a sub-block move at all, and jumps a whole block at a time when
     * it does move — which is exactly "no se acomodan". The same {@code setPos} additionally zeroes
     * {@code DATA_PEEK_ID} and starts a 6-tick {@code clientSideTeleportInterpolation} lerp on every
     * blockPos change, so a moving contraption's colliders also lost their peek growth and dragged a
     * third of a second behind the visual.
     *
     * <p>Riding fixes it at the root: the {@code isPassenger()} branch stores the exact fractional
     * position, and the client re-derives it from the vehicle every tick ({@code Level#tickPassenger}
     * -> {@code Entity#rideTick} -> {@code Entity#positionRider} -> {@code Entity::setPos}).
     * {@code ITEM_DISPLAY} is the right vehicle: it is {@code sized(0, 0)}, so its {@code PASSENGER}
     * attachment falls back to {@code (0, height, 0) == ZERO} and the shulker lands exactly ON the
     * anchor with no offset to compensate; it owns a real {@code InterpolationHandler} whose default
     * {@code teleport_duration} of 0 snaps instantly rather than adding lag; and with no display
     * metadata ever sent it renders nothing. Only the ANCHOR is moved from here (see
     * {@link #updatePosition}) — the shulker never receives another position packet after its spawn.
     */
    /**
     * How often a tracked slot re-asserts its {@code SetPassengers} mount (2026-07-16 — "aveces pasan
     * cosas raras con la shulker hitbox y el render, el render si ta pegado al suelo pero el shulker no").
     *
     * <p>The mount is the ONLY thing keeping a shulker off the block grid: {@code Shulker#setPos} floors
     * every inbound position unless {@code isPassenger()} (see this class's javadoc for the decompile).
     * It used to be sent exactly once, at spawn, and only the anchor is ever moved afterwards — so ANY
     * client-side event that broke the mount (an eject, a {@code stopRiding} from
     * {@code ClientLevel#tickPassenger}'s {@code getVehicle() != vehicle} check, a dropped packet) was
     * permanent AND unrecoverable: nothing re-asserted it, and the shulker never receives another
     * position packet of its own. The collider then sat grid-snapped, up to half a block off its render,
     * forever. Re-asserting bounds that to at most this many ticks whatever the trigger was.
     *
     * <p>Re-asserting is safe to do blind, which is why no detection is attempted (client mount state is
     * not observable from here anyway): the client's handler ejects and re-mounts within the single
     * packet, and {@code Entity#startRiding} returns false as a harmless no-op when the vehicle is already
     * this entity's. No {@code setPos} runs in between, so an already-correct slot sees no visible change.
     */
    private static final int MOUNT_REASSERT_TICKS = 40;

    private static final class Slot {
        final Object key;
        double lx, ly, lz;
        float scale;
        net.minecraft.core.Direction attachFace;
        int peek;
        private final int entityId = nextEntityId();
        /** The {@code ITEM_DISPLAY} this slot's shulker rides — the only entity of the pair ever moved (see the class javadoc). */
        private final int anchorId = nextEntityId();
        /**
         * Ticks until the next {@code SetPassengers} re-assert (see {@link #render}), staggered by
         * {@link #entityId} so a big contraption's slots never all re-assert on the same tick.
         */
        private int mountTick = Math.floorMod(entityId, MOUNT_REASSERT_TICKS);
        private final UUID uuid = UUID.randomUUID();
        private final UUID anchorUuid = UUID.randomUUID();
        private final Object despawnPacket;
        private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
        /**
         * Set by {@link #updateOffset} when {@code scale} changes since the last spawn/resend;
         * cleared once {@link #render} has pushed the updated {@code Attributes.SCALE} packet to
         * every current viewer. See {@link #render}'s javadoc for why the Scale attribute needs
         * its own explicit resend path, unlike entity-data/position which already go out every
         * tick regardless.
         */
        private volatile boolean scaleDirty = false;

        /**
         * Set by {@link #updateOffset} when {@code attachFace}/{@code peek} change; cleared once
         * {@link #render} has pushed the updated {@link #metadata} to every current viewer. Needed for
         * the same reason as {@link #scaleDirty} and NOT covered by it: a captured cell can change to a
         * shape with an identical {@code scale} but a different orientation/growth (a bottom slab
         * swapped for a top slab is {@code scale=0.5} either way, only {@code attachFace} flips
         * DOWN->UP), which without this left every already-tracking player on the pre-swap box forever.
         */
        private volatile boolean dataDirty = false;

        /** The {@link #VISIBILITY_GENERATION} this slot last pushed; a mismatch means the debug toggle flipped. */
        private volatile int visibilityGeneration = VISIBILITY_GENERATION.get();

        /**
         * Live uniform CONTRAPTION scale (roadmap item #9) multiplying this slot's own shape {@link #scale}
         * when the {@code Scale} attribute is sent — so the effective box size is {@code scale*contraptionScale}
         * (see {@link #effectiveScale}). {@code 1.0} = un-scaled contraption (the pre-scale behaviour exactly:
         * {@code scale*1.0 == scale}). A change flags {@link #scaleDirty} for an attribute resend.
         */
        private double contraptionScale = 1.0;

        Slot(Object key, double lx, double ly, double lz, float scale,
                net.minecraft.core.Direction attachFace, int peek) {
            this.key = key;
            this.lx = lx;
            this.ly = ly;
            this.lz = lz;
            this.scale = scale;
            this.attachFace = attachFace;
            this.peek = peek;
            this.despawnPacket = MNms.INSTANCE
                    .constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId, anchorId));
        }

        /**
         * This slot's base cube CENTRE in bearing-local space — the point that must be rotated, rather
         * than {@code (lx, ly, lz)} itself (2026-07-16 — "creo que es porque al rotar queda mal la base
         * del shulker").
         *
         * <p>A shulker's box is X/Z centred but Y BOTTOM-anchored: {@code AABB(-s/2, 0, -s/2, s/2, s,
         * s/2)} around its position (decompiled {@code Shulker#getProgressDeltaAabb}). Crucially it grows
         * up along WORLD +Y no matter how the contraption is oriented — an AABB has no orientation to
         * inherit. So the slot's stored bottom-centre is only the bottom-centre in LOCAL space; rotating
         * it lands the box's FOOT where the tilted cube's foot would be, which is not where the
         * axis-aligned box's foot actually is. The residual is exactly {@code R*(0,s/2,0) - (0,s/2,0)}.
         *
         * <p>That residual is identically zero for yaw (yaw fixes the Y axis) and for any 90-degree-multiple
         * orientation, which is why this was invisible until bodies could pitch and roll, and why it read as
         * intermittent rather than as a constant offset.
         *
         * <p>The fix is to rotate the CENTRE — which transforms correctly under any R — and let
         * {@link #render} drop the result by half the box height in WORLD space, re-deriving the foot the
         * only way the entity actually measures it. {@link #addSlot}'s bottom-anchored contract is
         * unchanged, since callers and {@link #isStandingOnFootprint} depend on it.
         */
        Vec3 localCenter() {
            return new Vec3(lx, ly + scale / 2.0, lz);
        }

        void updateOffset(double lx, double ly, double lz, float scale,
                net.minecraft.core.Direction attachFace, int peek) {
            this.lx = lx;
            this.ly = ly;
            this.lz = lz;
            if (this.attachFace != attachFace || this.peek != peek) {
                dataDirty = true;
            }
            if (this.scale != scale) {
                // A cell whose captured block CHANGES shape across a rebuild (e.g. a stair swapped
                // for a slab) needs the Scale attribute RESENT to everyone already tracking this
                // slot — see #render's javadoc for why this can't just happen unconditionally every
                // tick (the attributes packet is comparatively heavy/rare, unlike the entity-data/
                // position-sync traffic that already goes out every tick regardless).
                scaleDirty = true;
            }
            this.scale = scale;
            this.attachFace = attachFace;
            this.peek = peek;
        }

        /**
         * The real-world-scale extent this slot's box grows by along {@link #attachFace}'s
         * opposite direction, from peek alone (see this class's {@code addSlot} javadoc for the
         * decompiled derivation) — {@code physicalPeek * scale}, where {@code physicalPeek =
         * 0.5 - sin((0.5 + peek/100) * PI) * 0.5}. Used by
         * {@link ContraptionHitboxSwarm}/{@link #isStandingOnFootprint} callers that need this
         * slot's REAL grown extent, not just its base {@code scale}.
         */
        double peekGrowth() {
            float peekFrac = peek / 100f;
            double physicalPeek = 0.5 - Math.sin((0.5 + peekFrac) * Math.PI) * 0.5;
            return physicalPeek * scale;
        }

        /**
         * The box size actually sent over the {@code Scale} attribute — this slot's shape {@link #scale}
         * times the live {@link #contraptionScale} (roadmap item #9). At {@code contraptionScale == 1.0} this
         * is exactly {@code scale} (float-exact), so the un-scaled attribute packet is unchanged.
         */
        float effectiveScale() {
            return (float) (scale * contraptionScale);
        }

        private List<Object> metadata() {
            List<Object> values = new ArrayList<>();
            // Invisible shared-flag bit — a real vanilla shulker's block-shaped collision SHAPE
            // is a purely client-side rendering fact of the entity type; hiding the model still
            // leaves the manual standing-check math (this class's own) as the actual mechanism,
            // same convention as ContraptionHitboxSwarm's invisible INTERACTION cells. Visible
            // here would work too (useful for debugging) — kept invisible to match production
            // use, matching CraftEngine's own peek/attach-face-driven shulker which IS visible
            // by design (it's the furniture's literal model) but ours is a bare collider, not a
            // rendered furniture piece, so invisible is the right default.
            BaseEntityData.SharedFlags.addEntityData((byte) (SHOW_COLLIDERS ? 0x00 : 0x20), values);
            ShulkerData.AttachFace.addEntityData(attachFace, values);
            ShulkerData.RawPeekAmount.addEntityData((byte) peek, values);
            ShulkerData.Color.addEntityData((byte) 16, values); // 16 = no color / default
            return values;
        }

        void spawn(Player player, double x, double y, double z) {
            Object anchorPacket = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                    anchorId, anchorUuid, x, y, z, 0f, 0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0);
            Object addPacket = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                    entityId, uuid, x, y, z, 0f, 0f, EntityType.SHULKER, 0, Vec3.ZERO, 0);
            // Mount BEFORE the entity data below, not after (the one place this deliberately diverges
            // from ShulkerFurnitureHitbox's own packet order): the client's set-passengers handler runs
            // Shulker#startRiding, which unconditionally forces AttachFace back to DOWN — sending our
            // shape-derived AttachFace first would just get clobbered by the mount.
            Object mountPacket = MNms.INSTANCE
                    .constructor$ClientboundSetPassengersPacket(anchorId, new int[] { entityId });
            Object dataPacket = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata());
            // Scale attribute (2026-07-02 session — see field-level javadoc on #scaleDirty / MNms
            // #constructor$ClientboundScaleAttributePacket): without this the client-visible/
            // -collidable shulker box is ALWAYS a full 1x1x1 regardless of `scale`, since
            // Attributes.SCALE is synced via its own dedicated attributes packet, not via the
            // entity-data packet above (which only carries AttachFace/RawPeekAmount/Color, none of
            // which affect the box's actual size) — this was the root cause of "slabs/stairs render
            // a full shulker instead of their real bounding box."
            Object scalePacket = MNms.INSTANCE.constructor$ClientboundScaleAttributePacket(entityId, effectiveScale());
            player.sendPackets(List.of(anchorPacket, addPacket, mountPacket, dataPacket, scalePacket), false);
        }

        /**
         * Moves the ANCHOR — never the shulker. See the class javadoc: a shulker addressed directly
         * floors its own inbound position to the block grid, so the only way it reaches a fractional
         * (rotated/tipped/slab) position is to be carried there by the vehicle it rides.
         */
        void updatePosition(Player player, double x, double y, double z) {
            player.sendPacket(MNms.INSTANCE
                    .constructor$ClientboundEntityPositionSyncPacket(anchorId, x, y, z, 0f, 0f, false), false);
        }

        void despawn(Player player) {
            player.sendPacket(despawnPacket, false);
        }

        /**
         * @param viewers every player this swarm renders to
         * @param wanted the subset of {@code viewers} (by uuid) that should currently SEE this slot,
         *        or {@code null} for "all of them" (the fixed-slot path, which has no LOD). A viewer
         *        outside {@code wanted} that this slot is still shown to is despawned here — the
         *        only place a tier change is ever undone, since a packet-only entity is never
         *        untracked by the client on its own.
         */
        void render(List<Player> viewers, Set<UUID> wanted, double x, double y, double z, double contraptionScale,
                boolean moved) {
            if (contraptionScale != this.contraptionScale) {
                // Contraption resized (roadmap item #9 — e.g. the phys wand): the effective Scale attribute
                // changed, so resend it to everyone already tracking this slot (same path as a shape-scale change).
                this.contraptionScale = contraptionScale;
                scaleDirty = true;
            }
            // (x,y,z) is the base cube's world CENTRE (see #localCenter). The entity's position is its
            // box's FOOT, so drop half the box height along world -Y — deliberately world, not local:
            // the box is an AABB and grows up world +Y whatever the body's orientation. MUST follow the
            // contraptionScale update above, since effectiveScale() folds it in.
            y -= effectiveScale() / 2.0;
            Set<UUID> current = new HashSet<>();
            // Scale-change resend (see #scaleDirty's javadoc / #updateOffset): captured once per
            // render call so every viewer already tracking this slot gets the fresh attribute, not
            // just newly-spawned-this-call ones — cleared at the end of the loop, not per-viewer,
            // so a viewer added THIS SAME call (spawn() below, which already sends the current
            // scale) doesn't ALSO get a redundant second attributes packet.
            boolean resendScale = scaleDirty;
            // The debug visibility toggle rides the same resend as a shape change: the invisible flag
            // is part of the entity-data packet, so an already-spawned shulker only learns about it
            // when that packet is re-sent.
            int generation = VISIBILITY_GENERATION.get();
            if (generation != visibilityGeneration) {
                visibilityGeneration = generation;
                dataDirty = true;
            }
            // Same capture-once-per-call discipline as resendScale above, for the same reason (see
            // #dataDirty).
            boolean resendData = dataDirty;
            // Periodic mount re-assert (see MOUNT_REASSERT_TICKS). Counted per render call rather than
            // off a world clock so a slot only pays for it while it is actually being rendered.
            boolean reassertMount = false;
            if (++mountTick >= MOUNT_REASSERT_TICKS) {
                mountTick = 0;
                reassertMount = true;
            }
            for (Player p : viewers) {
                UUID id = uuidOf(p);
                if (id == null) {
                    continue;
                }
                if (wanted != null && !wanted.contains(id)) {
                    if (shownTo.remove(id)) {
                        despawn(p);
                    }
                    continue;
                }
                current.add(id);
                if (shownTo.add(id)) {
                    spawn(p, x, y, z);
                } else {
                    if (moved) {
                        updatePosition(p, x, y, z);
                    }
                    if (reassertMount) {
                        // Mount BEFORE the data, exactly as #spawn does and for the same reason: the
                        // client's set-passengers handler runs Shulker#startRiding, which unconditionally
                        // forces AttachFace back to DOWN. Re-asserting the mount alone would therefore
                        // silently flatten every non-DOWN slot's shape (a top slab is UP), so the
                        // shape-derived metadata must follow it in the same ordered batch.
                        p.sendPackets(List.of(
                                MNms.INSTANCE.constructor$ClientboundSetPassengersPacket(anchorId,
                                        new int[] { entityId }),
                                MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata())),
                                false);
                    } else if (resendData) {
                        p.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata()), false);
                    }
                    if (resendScale) {
                        p.sendPacket(MNms.INSTANCE.constructor$ClientboundScaleAttributePacket(entityId, effectiveScale()), false);
                    }
                }
            }
            if (resendScale) {
                scaleDirty = false;
            }
            if (resendData) {
                dataDirty = false;
            }
            shownTo.retainAll(current);
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
