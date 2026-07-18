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

import dev.arubik.craftengine.contraption.ContraptionPushSettings;
import dev.arubik.craftengine.contraption.PlayerCarry;
import dev.arubik.craftengine.contraption.level.ContraptionLevel;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.InteractionData;
import net.momirealms.craftengine.core.entity.player.Player;

/**
 * Packet-only invisible {@code INTERACTION} hitbox swarm for a moving contraption
 * (CONTRAPTIONS.md §5 Phase 4). Same entity type real CraftEngine furniture uses for ITS
 * OWN colliders ({@code BukkitCollider}/{@code ColliderType.INTERACTION}) — a real vanilla
 * hitbox-only entity with arbitrary, continuous {@code width}/{@code height} metadata,
 * bottom-anchored (box spans X/Z ±width/2 around the entity, Y from 0 to height above it) —
 * NOT grid-snapped to a fixed 1×1×1 like the old {@code SHULKER}-based version this replaces.
 * (Furniture's OWN collider is a REAL spawned NMS entity with genuine server physics; ours
 * stays packet-only, consistent with every other swarm in this package — so "collision" here
 * is still this class's own manual standing-check math, just no longer forced onto block
 * boundaries.)
 *
 * <p>Default behaviour is unchanged: {@link #rebuild} still derives one slot per EXPOSED TOP
 * surface cell (a captured block with no captured block directly above it, Phase-0 spike #1's
 * granularity finding) at a full 1×1×1 footprint. What's NEW is the underlying {@link Slot}
 * model itself — a continuous local offset + explicit width/height, not an integer
 * {@link BlockPos} with an implied fixed size — so a future caller (e.g. a captured
 * furniture piece wanting its own real footprint instead of the shared block-grid one) can
 * push an arbitrary offset/size hitbox via {@link #addCustomSlot} into the SAME swarm.
 *
 * <p>Also owns the player-carry step: every tick, whichever online players are standing on
 * one of these cells' footprint get the contraption's last-applied delta fed through
 * {@link PlayerCarry}. Axis-aligned only for now (yaw ignored) — matches Phase 3's
 * linear-only-kinematics MVP scope; rotation support is deferred with continuous rotation
 * itself (CONTRAPTIONS.md §7).
 */
public final class ContraptionHitboxSwarm {

    private final Map<BlockPos, Slot> autoSlots = new HashMap<>();
    private final List<Slot> customSlots = new ArrayList<>();
    private final Set<UUID> currentRiders = new HashSet<>();

    /**
     * Per-player footstep-timing state (Bug-2 fix, 2026-07-02 session — "walking on a contraption
     * block plays no step sound"). A player walking across a packet-only SHULKER floor hears no
     * vanilla footstep sounds, because vanilla's {@code Entity#playStepSound} is driven by the REAL
     * block under the player, which is air in the real world for a contraption deck. This tracks,
     * per standing player, the last real-world XZ position a step sound was emitted from, so a step
     * can be re-emitted every {@link #STEP_DISTANCE} blocks of horizontal travel — approximating
     * vanilla's own {@code moveDist}-based cadence ({@code nextStep = floor(moveDist)+1}, confirmed
     * via javap) without needing access to the entity's private {@code moveDist}/{@code nextStep}
     * fields. Cleaned up in {@link #carryRiders} the moment a player stops standing on the footprint
     * (and on {@link #despawnAll}) so it never leaks across quit/teardown.
     */
    private final Map<UUID, double[]> stepState = new HashMap<>();

    /**
     * Horizontal distance (blocks) a rider must walk across the contraption deck before the next
     * footstep sound fires. ~2 blocks matches vanilla's effective walking cadence (its
     * {@code moveDist} accumulates ~0.6 of each tick's horizontal travel, and a step fires each
     * time it crosses an integer, i.e. roughly every 1.6-2 blocks actually walked).
     */
    private static final double STEP_DISTANCE = 2.0;

    /** Minimum downward speed (blocks/tick) at the landing tick for a fall sound to fire (below this reads as a normal step-down, not a fall). */
    private static final double FALL_MIN_SPEED = 0.35;

    /**
     * Additional, more-precise CraftEngine-style SHULKER collider layer (2026-07-02 session —
     * see {@link ContraptionShulkerColliderSwarm}'s own javadoc for the full CraftEngine
     * decompile writeup). Purely additive to the {@code INTERACTION}-based slots above: a
     * caller with a fractional/moving local offset (e.g. a captured furniture piece that isn't
     * grid-snapped) pushes it here via {@link #addShulkerSlot} instead of/in addition to a
     * regular top-cell slot. Lifecycle (render/despawn) is driven from the same methods this
     * class already exposes so {@code ContraptionEntity} doesn't need a second field.
     */
    private final ContraptionShulkerColliderSwarm shulkerColliders = new ContraptionShulkerColliderSwarm();

    /**
     * Withholds the SOLID shulker colliders from one viewer — see
     * {@link ContraptionShulkerColliderSwarm#setExcludedViewer}. Only the shulkers need this: the
     * INTERACTION cells this class spawns do not override {@code canBeCollidedWith}, so a client never
     * treats them as obstacles.
     */
    public void setColliderExcludedViewer(java.util.UUID viewer) {
        shulkerColliders.setExcludedViewer(viewer);
    }

    /**
     * (Re)builds the auto-derived slots from a captured level; custom slots (see
     * {@link #addCustomSlot}) survive a rebuild. Keyed by offset (like
     * {@code ContraptionDisplaySwarm.cells}) so an unchanged top-cell REUSES its existing
     * {@link Slot} (same entity id, same {@code shownTo} tracking) instead of despawning and
     * respawning it — this used to unconditionally {@code clear()} and rebuild every slot from
     * scratch on every single tick (since {@code ContraptionEngine} calls this every tick), which
     * both leaked a full set of orphaned client-side entities every tick (old ones dropped from
     * the list without ever being sent a despawn packet) AND caused the disassemble-time
     * {@link #despawnAll} to only ever know about the LAST tick's slots. Only a top-cell that
     * genuinely stops/starts existing (e.g. a piston head appearing/retracting) should ever
     * cause a despawn/spawn here.
     */
    public void rebuild(ContraptionLevel level, List<Player> viewers, Vec3 bearingWorldPos) {
        // Drop cache entries for cells that no longer exist BEFORE anything reads the cache, so a
        // cell that leaves and returns within one tick can't resolve against its own stale shape.
        cellCache.keySet().retainAll(level.localPositions());
        // Every captured cell gets its own INTERACTION + shulker hitbox now (2026-07-02 session —
        // "veo que no pones shulker y interaction a todos los bloques ... debes ponerle shulker a
        // todo" — a multi-cell structure like a tall fluid_block_tank used to only get ONE hitbox
        // total, at its single exposed-top cell, so side faces of anything below the top (or every
        // cell of a >1-tall column) had no INTERACTION/shulker at all — no click-interact, no
        // standing/collision surface. topCellsOf's exposed-top-only result is STILL used, but only
        // for the carry/standing-footprint check below (see isStandingOnFootprint) — click/collision
        // hitboxes now cover the full footprint, full stop.
        Set<BlockPos> allOffsets = level.localPositions();
        java.util.Iterator<Map.Entry<BlockPos, Slot>> it = autoSlots.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Slot> e = it.next();
            if (!allOffsets.contains(e.getKey())) {
                for (Player p : viewers) {
                    e.getValue().despawn(p);
                }
                it.remove();
            }
        }
        for (BlockPos offset : allOffsets) {
            // INTERACTION's box is centred ±width/2 around the entity's own X/Z (bottom-anchored
            // on Y) — offset.getX()/getZ() are the block's CORNER, so +0.5 centres the 1-wide box
            // exactly on the block's real footprint (without this the box sits half a block off
            // on both axes, visibly floating diagonally beside the block it's meant to cover).
            //
            // standTopY/standBottomY (2026-07-02 session, carry-fix follow-up — "los shulker de
            // slabs y stairs y figuras no normales no agarran el sistema de carry"): the
            // INTERACTION entity itself is DELIBERATELY still spawned at the flat full-cell
            // width=1/height=1 box below (see this loop's own long-standing comment above and
            // rebuild()'s class-level javadoc: "click/collision hitboxes now cover the full
            // footprint, full stop" — clicking/interacting anywhere in the cell must keep working
            // regardless of the real block's shape). But isStandingOnFootprint (the CARRY
            // candidate check — see topSlotsOnly()/carryRiders()) was keying off that SAME flat
            // height=1 assumption to compute a slot's real top surface, so a player standing on a
            // slab's REAL collision surface (e.g. a bottom slab's top face at y+0.5, not the
            // flat-assumed y+1.0) fell either outside or only accidentally inside the standing
            // window depending on slab orientation — see isStandingOnFootprint's javadoc for the
            // exact numbers. computeCell already derives the real per-cell shape's
            // minY/height from the same BlockState#getCollisionShape this class's shulker
            // population loop (below) uses for the passive visual collider — reuse that exact
            // derivation here too, rather than duplicating the VoxelShape query, and store the
            // result on the Slot alongside (not instead of) its flat width/height so both the
            // full-cell INTERACTION spawn AND the shape-accurate carry check can each read the
            // field they actually need.
            ShulkerParams standParams = cellFor(level, offset).params;
            double standBottomY = standParams != null ? standParams.yOffset : 0.0;
            double standTopY = standParams != null ? standParams.yOffset + standParams.scale : 1.0;
            // hasCollision mirrors computeCell's own null-means-no-collision result (torches,
            // tripwire, most plants, etc. — see that method's shape.isEmpty() javadoc) — see the
            // Slot#hasCollision field javadoc for why overlapsAnySolid/resolvePushOut need this.
            boolean hasCollision = standParams != null;
            Slot slot = autoSlots.computeIfAbsent(offset,
                    o -> new Slot(o.getX() + 0.5, o.getY(), o.getZ() + 0.5, 1f, 1f, standBottomY, standTopY));
            // Refresh the shape-derived stand window on every rebuild even for a REUSED slot
            // (computeIfAbsent above only runs the factory for a brand-new key) — the underlying
            // block at this offset can change type in place (e.g. a stair replaced by a full
            // block) without the BlockPos key itself ever disappearing from allOffsets, so a
            // stale stand-window would otherwise survive indefinitely across such an in-place swap.
            slot.standBottomY = standBottomY;
            slot.standTopY = standTopY;
            slot.hasCollision = hasCollision;
        }

        // Auto-populate a shulker collider per captured cell too (2026-07-02 session — "al hacer
        // un spawn holo el contraption no tiene shulker hitbox" — ContraptionShulkerColliderSwarm
        // was wired as opt-in infrastructure but nothing ever actually called addSlot on it, so
        // every contraption had zero shulker colliders regardless). Keyed by the same BlockPos
        // offset as the INTERACTION slot above so ContraptionShulkerColliderSwarm's own
        // key-based diffing reuses/prunes correctly across rebuilds.
        //
        // Shape-aware sizing: each cell's REAL NMS collision shape (BlockState#getCollisionShape —
        // full precision, available here because ContraptionLevel genuinely IS a ServerLevel,
        // unlike GlueWandListener#outlineBlock which only has Bukkit's Block#getBoundingBox to work
        // with) is handed to the swarm whole, merged and flattened to AABBs by #computeCell. A
        // shulker's box is a CUBE, so ONE of them can never be a stair — which is why the swarm
        // spends a distance-dependent NUMBER of them per cell instead of trying to pick better
        // parameters for a single one (see ContraptionShulkerColliderSwarm#renderCells and
        // ShulkerBoxFit). This layer's job ends at supplying the geometry; how many entities it
        // becomes, for whom, is the swarm's.
        //
        // <p><b>2026-07-02 session, LATER same day — the peek-merge "save entities" optimization
        // that used to live here has been REMOVED</b> ("no todos tienen una shulker hitbox solida
        // por alguna razon solo la y0 del coso ... debes ponerle shulker a todo" + concrete 4-tall
        // tower repro: layer1 ✓, layer2 ✗, layer3 ✗, layer4 ✓). Root cause (decompile-verified
        // against vanilla's actual Shulker.makeBoundingBox/getProgressAabb): a peek-grown shulker
        // box is still just ONE cube-ish box spanning the whole merged run — e.g. a 2-tall merge
        // anchored at the bottom cell produces a single solid volume from the bottom cell's floor
        // to the TOP cell's ceiling, with NO horizontal surface anywhere in between. A player
        // "standing on top of" the bottom cell of a merged run is actually standing at the
        // MIDDLE-HEIGHT of that one box, which has nothing to land ON there (you can only rest on
        // the very top of the whole merged box, or get shoved out sideways if you clip into its
        // middle) — so every cell except the true top and true bottom of any merged run had no
        // real standable surface, exactly matching the reported layer2/layer3-broken pattern. A
        // shulker's peek can only ever grow a SINGLE box along ONE axis; it fundamentally cannot
        // reproduce N independent per-cell floors, so there is no peek-parameter fix for this —
        // merging must not happen at all if every cell needs its own real standable top surface.
        // Every captured cell now gets its OWN dedicated scale=1(or shape-derived)/peek=0 shulker,
        // unconditionally — more entities, but the only way every single cell genuinely has a
        // correct, independent solid floor a player can stand on, matching the explicit
        // requirement ("todos deben tener hitbox solida", no exceptions).
        //
        // <p><b>"Optimal shulkering" (2026-07-02 session — "si no hay entidades ni players cerca
        // al contraption no agregue shulkers")</b>: with the per-cell-only population above, a
        // large contraption now spawns/tracks one shulker per cell — real per-tick cost
        // (VoxelShape queries here, plus actual client-tracked entities) for structures nobody is
        // anywhere near. {@code viewers} (see this class's rebuild javadoc / {@code
        // ContraptionEngine.render}'s call site) is every ONLINE PLAYER IN THE WORLD, not a
        // proximity-filtered set — CraftEngine's own player-visibility resolution has no distance
        // cutoff here, so an empty {@code viewers} list alone would essentially never trigger on a
        // populated server. Skip the whole shulker-population block (and prune every existing
        // slot, despawning them) whenever no real player is within {@link #SHULKER_ACTIVATION_RADIUS}
        // (16 blocks, explicit user-specified radius — "16 blocks nor 64") of the contraption's
        // current bearing. The INTERACTION-based autoSlots layer above is deliberately NOT gated
        // the same way: it's the thing carryRiders/isStandingOnFootprint/overlapsAnySolid actually
        // key their manual collision math off of, needed unconditionally regardless of proximity,
        // and it's cheap (no VoxelShape query, no entity, just a plain Java object).
        if (!anyPlayerNearby(viewers, bearingWorldPos, SHULKER_ACTIVATION_RADIUS)) {
            shulkerColliders.prune(java.util.Collections.emptySet(), viewers);
            return;
        }
        Set<Object> shulkerKeys = new HashSet<>();
        for (BlockPos offset : allOffsets) {
            CachedCell cell = cellFor(level, offset);
            if (cell.boxes == null) {
                // No real collision at all (2026-07-02 session — "hay bloques que no deben tener
                // solid hitbox como antorchas"): deliberately DON'T add this offset to shulkerKeys
                // either, so prune() below removes any stale slot left over from a PREVIOUS
                // rebuild where this same cell held a solid block (e.g. a torch placed where a
                // full block used to be) — see #computeCell's javadoc for which shapes hit
                // this path.
                continue;
            }
            shulkerKeys.add(offset);
            // Hand over the cell's REAL merged geometry rather than a pre-chosen single box: the
            // swarm decides how many shulkers to spend on it per viewer, from that viewer's
            // distance (see ContraptionShulkerColliderSwarm#renderCells). Passing the CACHED list
            // instance matters — the swarm's identity check is what makes an unchanged cell free.
            shulkerColliders.setCell(offset, cell.boxes, viewers);
        }
        shulkerColliders.prune(shulkerKeys, viewers);
    }

    /**
     * Per-cell derived collision geometry, cached against the {@code BlockState} it came from — see
     * {@link #cellFor}.
     */
    private static final class CachedCell {
        final net.minecraft.world.level.block.state.BlockState state;
        /** The single-box approximation driving the carry/stand window; null when the cell has no collision at all. */
        final ShulkerParams params;
        /**
         * The cell's merged collision boxes translated into the bearing-local frame, for the shulker
         * swarm's LOD fit; null exactly when {@link #params} is (they share one shape query).
         */
        final List<net.minecraft.world.phys.AABB> boxes;

        CachedCell(net.minecraft.world.level.block.state.BlockState state, ShulkerParams params,
                List<net.minecraft.world.phys.AABB> boxes) {
            this.state = state;
            this.params = params;
            this.boxes = boxes;
        }
    }

    /**
     * Per-cell shape cache. {@link #rebuild} runs every tick for every cell, and a
     * {@code VoxelShape} query plus {@code optimize()/toAabbs()} is far too expensive to repeat at
     * that rate — but a cell's geometry is a pure function of its {@code BlockState}, which only
     * changes when a block genuinely changes. Entries are dropped for vanished cells at the top of
     * {@link #rebuild} and wholesale in {@link #despawnAll}.
     */
    private final Map<BlockPos, CachedCell> cellCache = new HashMap<>();

    /**
     * This cell's cached geometry, recomputed only when its {@code BlockState} changed. The identity
     * comparison is exact, not an optimization gamble: vanilla interns every {@code BlockState} into
     * a single instance per property combination, so two states are the same object iff they are the
     * same state.
     */
    private CachedCell cellFor(ContraptionLevel level, BlockPos offset) {
        net.minecraft.world.level.block.state.BlockState state = level.getBlockState(offset);
        CachedCell cached = cellCache.get(offset);
        if (cached != null && cached.state == state) {
            return cached;
        }
        CachedCell fresh = computeCell(level, offset, state);
        cellCache.put(offset, fresh);
        return fresh;
    }

    /** Radius (blocks) within which a real player must be for {@link #rebuild} to bother populating shulker colliders — see that method's "Optimal shulkering" javadoc. */
    private static final double SHULKER_ACTIVATION_RADIUS = 16.0;

    /**
     * Whether any of {@code viewers} (see {@link #rebuild}'s javadoc — this is every online
     * player in the world, not pre-filtered by distance) has a real Bukkit position within
     * {@code radius} blocks of {@code bearingWorldPos}. Best-effort: a viewer whose platform
     * player type/position can't be resolved is simply skipped rather than treated as "nearby"
     * (fails safe toward NOT spawning shulkers rather than always spawning them).
     */
    private static boolean anyPlayerNearby(List<Player> viewers, Vec3 bearingWorldPos, double radius) {
        double radiusSq = radius * radius;
        for (Player p : viewers) {
            Object pp = p.platformPlayer();
            if (!(pp instanceof org.bukkit.entity.Player bukkitPlayer)) {
                continue;
            }
            org.bukkit.Location loc = bukkitPlayer.getLocation();
            double dx = loc.getX() - bearingWorldPos.x;
            double dy = loc.getY() - bearingWorldPos.y;
            double dz = loc.getZ() - bearingWorldPos.z;
            if (dx * dx + dy * dy + dz * dz <= radiusSq) {
                return true;
            }
        }
        return false;
    }

    /**
     * Derived shulker parameters for one captured cell — see {@link #computeCell}.
     */
    private static final class ShulkerParams {
        final float scale;
        final net.minecraft.core.Direction attachFace;
        final int peek;
        /**
         * Extra local Y offset (added on top of the cell's raw {@code offset.getY()} corner)
         * so the shulker's bottom-anchored box actually lands at the real shape's
         * {@code bounds.minY} instead of always the cell's floor — e.g. an upper slab
         * ({@code minY=0.5,maxY=1.0}) needs {@code yOffset=0.5} so its {@code scale=0.5} box
         * sits in the TOP half of the cell, not the bottom half. Zero for the full-cube/fallback
         * case (shape spans the whole cell, or unknown, so no shift is meaningful).
         */
        final double yOffset;

        ShulkerParams(float scale, net.minecraft.core.Direction attachFace, int peek) {
            this(scale, attachFace, peek, 0.0);
        }

        ShulkerParams(float scale, net.minecraft.core.Direction attachFace, int peek, double yOffset) {
            this.scale = scale;
            this.attachFace = attachFace;
            this.peek = peek;
            this.yOffset = yOffset;
        }
    }

    /** {@code scale=1, attachFace=DOWN, peek=0, yOffset=0} — the previous always-full-cube fallback. */
    private static final ShulkerParams FULL_CUBE = new ShulkerParams(1f, net.minecraft.core.Direction.DOWN, 0);

    /**
     * Resolves one captured cell's {@code BlockState} into both of the things the rest of this class
     * needs from its REAL collision shape, from a SINGLE {@code BlockState#getCollisionShape} query
     * (the genuine NMS {@code VoxelShape}):
     * <ul>
     *   <li>{@link CachedCell#boxes} — the shape merged, {@code optimize()}d and flattened to AABBs
     *   in the bearing-local frame. This is the input to the shulker swarm's distance-LOD fit, which
     *   spends N cubes on it (see {@link ShulkerBoxFit}); it is the ONLY thing that lets a stair or
     *   a door collide as itself, since one shulker is one cube and can never be either.</li>
     *   <li>{@link CachedCell#params} — the single-box approximation the CARRY/standing window is
     *   built from ({@code standBottomY}/{@code standTopY} in {@link #rebuild}). This deliberately
     *   stays single-box and stays the shape's overall {@code bounds()}: it answers "what height is
     *   this cell's top surface", which has one answer per cell regardless of how many colliders the
     *   cell is currently drawn with, and which must not vary by viewer.</li>
     * </ul>
     *
     * <p>{@code params}' Y-extent becomes {@code scale} (a slab's {@code [0,0.5]} reports
     * {@code 0.5}; a full block {@code 1}) with {@code attachFace=DOWN} and {@code yOffset} shifted
     * so the box's bottom sits at the shape's real {@code minY} (an upper slab's {@code 0.5}, not
     * always 0).
     *
     * <p>{@code peek} is always {@code 0}. A peek-grown box is one continuous solid volume with no
     * standable surface at any intermediate cell boundary, which is why the vertical-run peek-merge
     * that once lived in {@link #rebuild} caused "middle layers have no real hitbox" — see that
     * method's javadoc.
     *
     * <p>A {@code null} {@code params}/{@code boxes} means the cell is genuinely passable (torch,
     * plant, open door) and gets no collider at all.
     */
    private static CachedCell computeCell(ContraptionLevel level, BlockPos offset,
            net.minecraft.world.level.block.state.BlockState state) {
        if (state.isAir()) {
            // Defensive only — rebuild()'s caller loop iterates level.localPositions(), which
            // ContraptionLevel#resync already excludes air from (see that class's own javadoc),
            // so this branch is normally unreachable from the real call site. Kept as a genuine
            // "no collider" result rather than FULL_CUBE on principle: air should never be solid,
            // and returning FULL_CUBE here used to be actively wrong if this method is ever called
            // from anywhere else in the future.
            return noCollision(state);
        }
        try {
            // Open door/trapdoor special-case (2026-07-02 session follow-up — "shulker swarm:
            // closed doors/trapdoors = solid hitbox, open doors = no solid hitbox traspasable like
            // torches"): an OPEN door/trapdoor's REAL collision shape is NOT shape.isEmpty() — a
            // door swings to lie flat against a wall, so it still occupies a thin (~0.1875 block)
            // sliver of space, and a trapdoor swings up to hug the wall the same way. Critically,
            // this thin sliver's bounding box is GEOMETRICALLY INDISTINGUISHABLE from the CLOSED
            // shape's own bounding box via bounds() alone: both states are "thin along one
            // horizontal axis (~0.1875), full along the other, full Y height" — opening just
            // rotates/relocates WHICH face the slab sits flush against, it doesn't change the
            // box's own dimensions. A height-extent-only check (below) can't tell them apart
            // (both measure height≈1.0), and neither can an X/Z-footprint-only check (both have
            // the same ~0.1875 x 1.0 footprint) — so this must be resolved from the block's own
            // `open` BlockState property directly, not from shape geometry. Explicitly checking
            // for DoorBlock/TrapDoorBlock's OPEN property before ever consulting the shape means
            // a genuinely open door/trapdoor is treated exactly like shape.isEmpty() below (no
            // shulker slot at all, fully passable, matching a torch) while every other shape
            // (slabs, stairs, a CLOSED door/trapdoor) is completely unaffected by this check.
            net.minecraft.world.level.block.Block block = state.getBlock();
            if (block instanceof net.minecraft.world.level.block.DoorBlock
                    && state.hasProperty(net.minecraft.world.level.block.DoorBlock.OPEN)
                    && state.getValue(net.minecraft.world.level.block.DoorBlock.OPEN)) {
                return noCollision(state);
            }
            if (block instanceof net.minecraft.world.level.block.TrapDoorBlock
                    && state.hasProperty(net.minecraft.world.level.block.TrapDoorBlock.OPEN)
                    && state.getValue(net.minecraft.world.level.block.TrapDoorBlock.OPEN)) {
                return noCollision(state);
            }
            net.minecraft.world.phys.shapes.VoxelShape shape = state.getCollisionShape(level.serverLevel(), offset);
            if (shape.isEmpty()) {
                // No real collision at all — e.g. torches, tripwire, most plants/flowers, rails,
                // signs, buttons, etc. (2026-07-02 session — "hay bloques que no deben tener solid
                // hitbox como antorchas"). This used to return FULL_CUBE with a comment claiming
                // "cube fallback is harmless" — backwards: a block a real player can walk straight
                // through was getting a full solid 1x1x1 shulker collider, i.e. exactly the reported
                // bug (a torch blocking movement/giving pushback like a full block). Returning null
                // tells the caller (rebuild()) to skip adding a shulker slot for this cell entirely.
                return noCollision(state);
            }
            net.minecraft.world.phys.AABB bounds = shape.bounds();
            double height = bounds.maxY - bounds.minY;
            if (height <= 0.0 || height > 1.0) {
                return fullCube(state, offset); // degenerate/oversized shape — don't trust it, fall back to the safe default
            }
            // optimize() runs vanilla's greedy box merging before toAabbs() flattens the shape, so a
            // stair arrives as its true 2 regions rather than the raw voxel soup — the LOD fit's
            // budget is spent per region, so the region count is what its quality rides on. Boxes are
            // translated into the bearing-local frame here (a VoxelShape is authored cell-relative),
            // matching the frame every swarm's local offsets already use.
            List<net.minecraft.world.phys.AABB> boxes = new ArrayList<>();
            for (net.minecraft.world.phys.AABB box : shape.optimize().toAabbs()) {
                boxes.add(box.move(offset.getX(), offset.getY(), offset.getZ()));
            }
            if (boxes.isEmpty()) {
                return noCollision(state);
            }
            return new CachedCell(state,
                    new ShulkerParams((float) height, net.minecraft.core.Direction.DOWN, 0, bounds.minY),
                    List.copyOf(boxes));
        } catch (Throwable t) {
            return fullCube(state, offset); // best-effort — a shape-query failure shouldn't block hitbox population
        }
    }

    /** A cell that is genuinely passable — no stand window, no collider (see {@link #computeCell}). */
    private static CachedCell noCollision(net.minecraft.world.level.block.state.BlockState state) {
        return new CachedCell(state, null, null);
    }

    /** The safe fallback cell: a solid full-cell cube, matching {@link #FULL_CUBE}'s stand window. */
    private static CachedCell fullCube(net.minecraft.world.level.block.state.BlockState state, BlockPos offset) {
        return new CachedCell(state, FULL_CUBE, List.of(new net.minecraft.world.phys.AABB(
                offset.getX(), offset.getY(), offset.getZ(),
                offset.getX() + 1.0, offset.getY() + 1.0, offset.getZ() + 1.0)));
    }

    /**
     * Registers an extra hitbox slot at a CONTINUOUS bearing-local offset with its own
     * width/height, independent of the block grid — the "furniture-style dynamic hitbox"
     * capability this class's javadoc describes. Survives {@link #rebuild} (auto-derived
     * slots are cleared/regenerated; custom ones are additive and caller-owned — call
     * {@link #clearCustomSlots} to remove them explicitly). {@code width} is the box's X/Z
     * span (centered on the offset); {@code height} is its Y span (from the offset upward) —
     * same convention vanilla's own {@code Interaction} entity uses.
     */
    public void addCustomSlot(double localX, double localY, double localZ, float width, float height) {
        customSlots.add(new Slot(localX, localY, localZ, width, height));
    }

    public void clearCustomSlots() {
        customSlots.clear();
    }

    /**
     * Pushes/updates one CraftEngine-style SHULKER collider slot at a continuous bearing-local
     * offset (see {@link ContraptionShulkerColliderSwarm} javadoc) — {@code key} must be stable
     * across ticks/rebuilds for the same logical slot (e.g. a captured furniture cell's own id)
     * so it gets reused rather than despawned+respawned. {@code scale} follows vanilla shulker
     * {@code Scale} attribute convention (1.0 = full-block box).
     */
    public void addShulkerSlot(Object key, double localX, double localY, double localZ, float scale) {
        shulkerColliders.addSlot(key, localX, localY, localZ, scale);
    }

    /** Drops any shulker-collider slot not present in {@code liveKeys} (see {@link #addShulkerSlot}). */
    public void pruneShulkerSlots(Set<Object> liveKeys, List<Player> viewers) {
        shulkerColliders.prune(liveKeys, viewers);
    }

    public int shulkerColliderCount() {
        return shulkerColliders.slotCount();
    }

    private List<Slot> allSlots() {
        if (customSlots.isEmpty()) {
            return new ArrayList<>(autoSlots.values());
        }
        List<Slot> all = new ArrayList<>(autoSlots.size() + customSlots.size());
        all.addAll(autoSlots.values());
        all.addAll(customSlots);
        return all;
    }

    /**
     * Carry/standing-footprint candidate slots — exposed-top cells only (see
     * {@link #carryRiders}'s javadoc for why this stays narrower than {@link #allSlots}, which
     * every cell now contributes to for click/collision purposes). Custom slots are included:
     * they're an explicit caller-pushed footprint (e.g. a captured furniture's own hitbox) and
     * were already part of {@code allSlots()}'s carry contribution before this change.
     */
    private List<Slot> topSlotsOnly() {
        Set<BlockPos> topOffsets = topCellsOf(autoSlots.keySet());
        List<Slot> top = new ArrayList<>(topOffsets.size() + customSlots.size());
        for (BlockPos offset : topOffsets) {
            Slot slot = autoSlots.get(offset);
            if (slot != null) {
                top.add(slot);
            }
        }
        top.addAll(customSlots);
        return top;
    }

    /**
     * Pure exposed-top-cell detection (Phase-0 spike #1 granularity finding), extracted so
     * it's unit-testable without needing a real {@link ContraptionLevel} (which is a
     * genuine {@code net.minecraft.world.level.Level} subclass, not constructible in a
     * pure-JVM test).
     */
    static Set<BlockPos> topCellsOf(Set<BlockPos> occupied) {
        Set<BlockPos> top = new HashSet<>();
        for (BlockPos offset : occupied) {
            if (!occupied.contains(offset.above())) {
                top.add(offset);
            }
        }
        return top;
    }

    /**
     * Render every hitbox slot for the given viewers at the bearing's current world-space
     * position, rotated around the bearing by {@code yawRadians} (2026-07-02 session — "el
     * /cepolyfill contraption rotate ... no veo que afecte a sus interaction shulker" — this
     * used to just add the raw local offset with no rotation at all, so a rotated contraption's
     * hitboxes stayed stuck at their UN-rotated positions while the visual blocks/models
     * correctly rotated via {@code ContraptionDisplaySwarm}/{@code ContraptionMath.renderPosition}).
     * {@code moved} (packet-volume optimization — see {@code ContraptionEntity#render}): false
     * for a stalled/idle contraption whose bearing transform didn't change since the last call —
     * skips resending position-sync packets (a pure rotation still counts as "moved").
     */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, boolean moved) {
        render(viewers, bearingWorldPos, yawRadians, 0.0, 1.0, moved);
    }

    /**
     * Scale-aware {@link #render(List, Vec3, double, boolean)} (roadmap item #9 — per-contraption
     * {@code scale}): the whole collider swarm is sized/spaced to match the scaled visual so a resized
     * contraption is collidable at its rendered size. Two things scale together, both keyed off the same
     * {@code scale}:
     * <ul>
     *   <li><b>Inter-cell SPACING</b> — each slot's local offset is projected through
     *   {@link ContraptionMath#renderPosition(Vec3, Vec3, double, double, double)} with {@code scale}, so
     *   the colliders spread apart (or together) about the bearing pivot exactly like the block-display
     *   cells do.</li>
     *   <li><b>Box SIZE</b> — the {@code INTERACTION} slot's width/height and the shulker collider's
     *   {@code Scale} attribute are both multiplied by {@code scale} (see {@link Slot#render} /
     *   {@link ContraptionShulkerColliderSwarm#render}), so each individual box grows/shrinks with the
     *   model.</li>
     * </ul>
     *
     * <p><b>Documented approximation.</b> This scales the collider GEOMETRY, but the manual
     * standing/side-collision math ({@link #carryRiders} et al., which read each slot's un-scaled local
     * {@code width}/{@code standTopY}) is NOT scale-corrected in this pass — a scaled contraption's visual
     * colliders match its size, but the fine-grained carry/pushback resolution still reasons in the
     * un-scaled local footprint (acceptable best-effort, consistent with this whole layer being packet-only
     * discrete "collision"). At {@code scale == 1.0} everything below reduces byte-for-byte to the pre-scale
     * behaviour (renderPosition's scale-1 fast path; {@code width*1.0}/{@code Scale*1.0} identities; no
     * scale-resend packets ever emitted).
     */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double scale, boolean moved) {
        render(viewers, bearingWorldPos, yawRadians, 0.0, scale, moved);
    }

    /**
     * Pitch-aware {@link #render(List, Vec3, double, double, boolean)} (roadmap item #9 phase 5 — TIPPING).
     * Each slot's local offset is projected through the SAME
     * {@link ContraptionMath#renderPosition(Vec3, Vec3, double, double, double)} the block_display cells use —
     * now with the identical {@code pitchRadians} (previously hard-coded {@code 0.0} here), so a tipping
     * contraption's interaction/shulker colliders orbit to the SAME tilted cell positions the visual blocks
     * do instead of staying flat while the display tips. The boxes themselves stay axis-aligned (an
     * {@code INTERACTION}/{@code SHULKER} box cannot tilt) — this aligns their CENTRES to the canonical
     * {@code renderPosition} mapping, the best a non-oriented box can do. At {@code pitch == 0} this is
     * byte-for-byte the pre-pitch behaviour ({@code renderPosition}'s pitch-0 fast path), so every
     * never-tipped bearing/minecart contraption is unchanged.
     */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            double scale, boolean moved) {
        render(viewers, bearingWorldPos, yawRadians, pitchRadians, 0.0, scale, moved);
    }

    /**
     * Pitch+ROLL-aware {@link #render(List, Vec3, double, double, double, boolean)} (roadmap item #9 phase 6 —
     * ROLL). Each slot's local offset is projected through the SAME
     * {@link ContraptionMath#renderPosition(Vec3, Vec3, double, double, double, double)} the block_display cells
     * use — now with the identical {@code pitchRadians} AND {@code rollRadians} — so a body leaning in any
     * horizontal direction toward its heavy side keeps its interaction/shulker collider CENTRES aligned with the
     * visual blocks instead of the colliders staying flat. The boxes themselves stay axis-aligned (an
     * {@code INTERACTION}/{@code SHULKER} box cannot tilt), aligning their centres to the canonical
     * {@code renderPosition} mapping — the best a non-oriented box can do. At {@code pitch == 0 && roll == 0}
     * this is byte-for-byte the pre-tilt behaviour ({@code renderPosition}'s tilt-0 fast path), so every
     * never-leaned bearing/minecart contraption is unchanged.
     */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            double rollRadians, double scale, boolean moved) {
        for (Slot slot : allSlots()) {
            Vec3 pos = dev.arubik.craftengine.contraption.ContraptionMath.renderPosition(
                    new Vec3(slot.lx, slot.ly, slot.lz), bearingWorldPos, yawRadians, pitchRadians, rollRadians, scale);
            slot.render(viewers, pos.x, pos.y, pos.z, scale, moved);
        }
        shulkerColliders.render(viewers, bearingWorldPos, yawRadians, pitchRadians, rollRadians, scale, moved);
    }

    public void despawnAll(List<Player> viewers) {
        for (Slot slot : autoSlots.values()) {
            for (Player p : viewers) {
                slot.despawn(p);
            }
        }
        for (Slot slot : customSlots) {
            for (Player p : viewers) {
                slot.despawn(p);
            }
        }
        autoSlots.clear();
        customSlots.clear();
        currentRiders.clear();
        stepState.clear();
        cellCache.clear();
        shulkerColliders.clear(viewers);
    }

    /**
     * Carries whichever of {@code candidates} are currently standing on this swarm's
     * footprint, using {@code deltaX/Y/Z} (the contraption's last-applied per-tick delta —
     * zero while stalled, which naturally stops carrying without any extra bookkeeping).
     *
     * <p>Deliberately narrower than {@link #rebuild}'s click/collision hitbox population
     * (2026-07-02 session, same change that made every cell — not just exposed-top ones — get an
     * INTERACTION/shulker slot): the standing/carry check below still uses ONLY the exposed-top
     * slots ({@link #topCellsOf}), not {@link #allSlots}. Reason: {@link #isStandingOnFootprint}'s
     * height window is {@code topY - 0.3} to {@code topY + 0.9} — plenty lenient for "which exact
     * cell is a player's feet resting on" when there's only one candidate cell per column, but two
     * vertically-stacked cells are exactly 1 block apart, well inside that window's ~1.2-block
     * span. If every cell (not just the true top) contributed a carry-footprint slot, a player
     * standing on TOP of a 2-tall structure would ALSO match the cell one level below — harmless
     * here since both belong to the same swarm/contraption (same delta either way), but it stops
     * being harmless the moment two adjacent standalone contraptions of different heights get
     * close enough for their columns to overlap in the height window, at which point restricting
     * to true top-cells is what keeps "standing on the top of THIS contraption" from also being
     * true one level down. Narrowing the height window itself was the other option considered,
     * but that risks breaking legitimate edge-of-block standing tolerance that already works
     * today; scoping the candidate slot set is the smaller, more surgical fix.
     */
    /**
     * Read-only snapshot of whichever real players are currently tracked as "standing on this
     * swarm's footprint" (2026-07-02 session — teardown fall-through fix, see {@code
     * ContraptionEntity#currentRiderIds}'s javadoc for the caller). Purely additive accessor —
     * doesn't touch {@link #currentRiders}'s own update logic in {@link #carryRiders}.
     */
    public Set<UUID> currentRiderIds() {
        return java.util.Collections.unmodifiableSet(currentRiders);
    }

    public void carryRiders(List<ServerPlayer> candidates, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw) {
        carryRiders(candidates, bearingWorldPos, deltaX, deltaY, deltaZ, null, yaw, 0.0);
    }

    /**
     * The world-space displacement a rider at {@code riderPos} must be carried by THIS tick, given
     * the contraption's translational delta ({@code dx,dy,dz}) AND its rotational delta
     * ({@code yawDelta}) — 2026-07-03 "rotation carry falla en el rotation bearing". A pure
     * ROTATIONAL bearing has zero translational delta, but every off-axis cell (and the rider on it)
     * sweeps a real arc: this maps the rider's current footprint point into the local frame
     * ({@code realToLocal} at the NEW transform), re-projects it under the PREVIOUS tick's transform
     * ({@code bearing-Δtranslation}, {@code yaw-Δyaw}), and returns new−old — the exact per-tick
     * movement of the ground under the rider, uniformly covering translation, rotation, or both.
     * Fast-paths pure translation (yawDelta==0) to the unchanged {@code (dx,dy,dz)}.
     */
    private static Vec3 riderCarryDelta(Vec3 riderPos, Vec3 bearingNew, double dx, double dy, double dz,
            double yawNew, double yawDelta) {
        if (yawDelta == 0.0) {
            return new Vec3(dx, dy, dz);
        }
        Vec3 local = dev.arubik.craftengine.contraption.ContraptionMath.realToLocal(riderPos, bearingNew, yawNew);
        Vec3 bearingOld = new Vec3(bearingNew.x - dx, bearingNew.y - dy, bearingNew.z - dz);
        Vec3 oldPoint = dev.arubik.craftengine.contraption.ContraptionMath.renderPosition(local, bearingOld, yawNew - yawDelta);
        return new Vec3(riderPos.x - oldPoint.x, riderPos.y - oldPoint.y, riderPos.z - oldPoint.z);
    }

    /**
     * {@code level} overload (Bug-2 fix, 2026-07-02 session — footstep/fall sounds): the level is
     * needed only to read the captured {@code BlockState} at the cell a standing rider is over, so
     * its {@code SoundType#getStepSound}/{@code getFallSound} can be played manually (see
     * {@link #maybePlayStepSound}). {@code null} is tolerated (footstep sounds simply skipped) for
     * any caller/test path without a live level; everything else is unchanged.
     */
    public void carryRiders(List<ServerPlayer> candidates, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ,
            ContraptionLevel level, double yaw, double yawDelta) {
        List<Slot> slots = topSlotsOnly();
        if (slots.isEmpty()) {
            releaseAll();
            stepState.clear();
            return;
        }
        Set<UUID> ridingNow = new HashSet<>();
        for (ServerPlayer sp : candidates) {
            if (isStandingOnFootprint(sp, bearingWorldPos, slots, deltaY, yaw)) {
                UUID id = sp.getUUID();
                boolean wasRiding = currentRiders.contains(id);
                ridingNow.add(id);
                // Footstep / landing sounds (Bug-2). Emitted BEFORE the carry nudge, keyed off the
                // rider's current real position — see #maybePlayStepSound.
                if (level != null) {
                    maybePlayStepSound(sp, bearingWorldPos, level, wasRiding, yaw);
                }
                // Side-collision clamp (2026-07-02 session — "chequea bien las hitbox solidas en
                // las 4 direcciones, a veces por el movimiento deja moverse dentro de las bounding
                // box de otros (shulker) por el mismo carry"). See #clampDeltaForSideCollision's
                // javadoc for the full reasoning — this is the ONLY place that ever validated the
                // carry delta before; previously it was applied completely blind to whether the
                // resulting position would embed the rider inside a neighboring captured cell's
                // own solid volume.
                Vec3 riderPos = sp.position();
                // Rotational-aware carry delta (2026-07-03) — arc + translation, so a rider on a
                // spinning ROTATIONAL contraption is carried around, not just left in place.
                Vec3 carryVec = riderCarryDelta(riderPos, bearingWorldPos, deltaX, deltaY, deltaZ, yaw, yawDelta);
                Vec3 clamped = clampDeltaForSideCollision(riderPos, bearingWorldPos, carryVec.x, carryVec.y, carryVec.z, yaw);
                PlayerCarry.carry(sp, clamped.x, clamped.y, clamped.z);
            }
        }
        for (UUID left : currentRiders) {
            if (!ridingNow.contains(left)) {
                PlayerCarry.release(left);
                stepState.remove(left); // left the footprint — drop stale step-timing state
            }
        }
        currentRiders.clear();
        currentRiders.addAll(ridingNow);
    }

    /**
     * Manually emits footstep and landing sounds for a rider standing on the contraption deck
     * (Bug-2 fix, 2026-07-02 session). Vanilla's own {@code Entity#playStepSound}/fall logic reads
     * the REAL block under the player ({@code level.getBlockState(below)}), which is air in the
     * real world where a contraption visually is (its blocks live in the hidden
     * {@link ContraptionLevel}), so no footstep or fall sound ever plays. This reads the captured
     * {@code BlockState} at whichever top cell the rider is over, and plays its
     * {@code SoundType}'s step/fall sound through {@link ContraptionLevel}'s own
     * {@code playSeededSound} override (which redirects to the real world at the bearing transform,
     * so the sound reaches the real-world player at the right place).
     *
     * <ul>
     *   <li><b>Fall sound</b>: on the tick a player transitions from not-standing to standing
     *   ({@code wasRiding == false}) with enough downward speed ({@link #FALL_MIN_SPEED}), play the
     *   block's {@code getFallSound} once (volume 0.5, pitch 1.0 — vanilla's own fall-sound levels),
     *   and seed the step accumulator so a footstep doesn't also fire the same tick.</li>
     *   <li><b>Step sound</b>: otherwise, accumulate horizontal (XZ) distance travelled since the
     *   last step and, once it crosses {@link #STEP_DISTANCE}, play {@code getStepSound} at
     *   vanilla's {@code getVolume()*0.15} volume / {@code getPitch()} pitch (confirmed via javap
     *   against this project's mapped server jar, {@code Entity#playStepSound} bytecode).</li>
     * </ul>
     */
    private void maybePlayStepSound(ServerPlayer sp, Vec3 bearingWorldPos, ContraptionLevel level, boolean wasRiding, double yaw) {
        BlockPos cell = standingCellOf(sp.position(), bearingWorldPos, yaw);
        if (cell == null) {
            return;
        }
        net.minecraft.world.level.block.state.BlockState state = level.getBlockState(cell);
        if (state.isAir()) {
            return;
        }
        net.minecraft.world.level.block.SoundType soundType = state.getSoundType();
        Vec3 pos = sp.position();
        UUID id = sp.getUUID();

        if (!wasRiding) {
            // Just landed on / stepped onto the footprint this tick. A real downward speed means a
            // fall (play the fall sound); otherwise just start tracking steps without a sound.
            double downSpeed = -sp.getDeltaMovement().y;
            if (downSpeed >= FALL_MIN_SPEED) {
                net.minecraft.sounds.SoundEvent fall = soundType.getFallSound();
                if (fall != null) {
                    playAt(level, cell, fall, 0.5F, 1.0F);
                }
            }
            stepState.put(id, new double[] {pos.x, pos.z});
            return;
        }

        double[] last = stepState.get(id);
        if (last == null) {
            stepState.put(id, new double[] {pos.x, pos.z});
            return;
        }
        double dx = pos.x - last[0];
        double dz = pos.z - last[1];
        if (dx * dx + dz * dz >= STEP_DISTANCE * STEP_DISTANCE) {
            net.minecraft.sounds.SoundEvent step = soundType.getStepSound();
            if (step != null) {
                playAt(level, cell, step, soundType.getVolume() * 0.15F, soundType.getPitch());
            }
            last[0] = pos.x;
            last[1] = pos.z;
        }
    }

    /** Plays {@code sound} at the given local cell centre through {@link ContraptionLevel}'s real-world-redirecting override. */
    private static void playAt(ContraptionLevel level, BlockPos cell, net.minecraft.sounds.SoundEvent sound, float volume, float pitch) {
        try {
            level.playSeededSound(null, cell.getX() + 0.5, cell.getY() + 1.0, cell.getZ() + 0.5,
                    net.minecraft.core.Holder.direct(sound), net.minecraft.sounds.SoundSource.BLOCKS, volume, pitch, 0L);
        } catch (Throwable ignored) {
            // best-effort — a missing/odd sound should never break the carry loop
        }
    }

    /**
     * The captured top-cell {@link BlockPos} offset a rider at real-world {@code pos} is standing
     * on, or {@code null} if none — the same footprint math {@link #isStandingOnFootprint} uses,
     * but returns WHICH cell (needed to read that cell's {@code BlockState}/{@code SoundType}).
     * When multiple top cells qualify (edge tolerance overlap) the one whose XZ centre is nearest
     * the rider wins, so a walking player's footstep reflects the block they're most over.
     */
    private BlockPos standingCellOf(Vec3 pos, Vec3 bearingWorldPos, double yaw) {
        // Yaw-aware, matching isStandingOnFootprint (2026-07-03): un-rotate the rider into the local
        // frame the slots live in before the footprint test, so footstep sounds resolve the correct
        // cell on a rail-following (yaw-rotated) minecart contraption, not just at yaw≈0.
        Vec3 local = dev.arubik.craftengine.contraption.ContraptionMath.realToLocal(pos, bearingWorldPos, yaw);
        Set<BlockPos> topOffsets = topCellsOf(autoSlots.keySet());
        BlockPos best = null;
        double bestDistSq = Double.MAX_VALUE;
        for (BlockPos offset : topOffsets) {
            Slot slot = autoSlots.get(offset);
            if (slot == null || !slot.hasCollision) {
                continue;
            }
            double half = slot.width / 2.0;
            double cx = slot.lx;
            double cz = slot.lz;
            double topY = slot.ly + slot.standTopY;
            if (local.x >= cx - half - 0.3 && local.x <= cx + half + 0.3
                    && local.z >= cz - half - 0.3 && local.z <= cz + half + 0.3
                    && local.y >= topY - 0.5 && local.y <= topY + 1.0) {
                double ddx = local.x - cx, ddz = local.z - cz;
                double distSq = ddx * ddx + ddz * ddz;
                if (distSq < bestDistSq) {
                    bestDistSq = distSq;
                    best = offset;
                }
            }
        }
        return best;
    }

    /**
     * How far above a rider's real foot Y the side-collision box starts (see {@link #overlapsAnySolid}).
     * Just past {@link #isStandingOnFootprint}'s 0.3 below-top standing tolerance, so "resting on a
     * cell's top face" never registers as "embedded in that cell" and zeroes the horizontal carry.
     */
    private static final double FLOOR_CLEARANCE = 0.35;

    /** Half-width of a standard vanilla player hitbox (0.6 wide) — see {@link #clampDeltaForSideCollision}. */
    /** Below this per-tick movement (squared) a contraption counts as static — no player shove. See
     *  {@link #pushBackNearbyBystanders}. 0.02 blocks/tick squared: well under any real drive, above jitter. */
    private static final double STATIC_DELTA_SQ = 0.02 * 0.02;

    private static final double RIDER_HALF_WIDTH = 0.3;
    /** Standard vanilla standing player hitbox height — see {@link #clampDeltaForSideCollision}. */
    private static final double RIDER_HEIGHT = 1.8;

    /**
     * Side-collision validation for the carry delta (2026-07-02 session — "chequea bien las
     * hitbox solidas en las 4 direcciones, a veces por el movimiento deja moverse dentro de las
     * bounding box de otros (shulker) por el mismo carry").
     *
     * <p><b>What existed before this fix: nothing.</b> {@link #isStandingOnFootprint} is a
     * "is this rider's feet resting on top of a slot's TOP surface" check (a Y-window test, see
     * its own javadoc) — it has never had any notion of "would moving this rider sideways by the
     * platform's delta push them through a solid FACE of some other cell." {@code carryRiders}/
     * {@code carryEntities}/{@code carryNearbyEntities} all just did
     * {@code pos + delta}, unconditionally, every tick — completely blind to every OTHER captured
     * cell's own volume. Per this project's own established convention (see this class's and
     * {@code ContraptionShulkerColliderSwarm}'s class javadocs), there is no real server-side
     * collision here at all — every hitbox in this system is packet-only, so "solid" only ever
     * means "this class's own manual AABB math says so." That manual math previously only existed
     * for the vertical/standing case; there was NO horizontal counterpart whatsoever, so a rider
     * riding a contraption that moves sideways toward an adjacent captured cell (or toward another
     * contraption's cell) could freely end up with their own hitbox overlapping that cell's box —
     * "clipping into the neighbor" exactly as reported.
     *
     * <p><b>The fix</b>: before handing {@code deltaX/Y/Z} to {@code PlayerCarry}/a direct
     * reposition, build the rider's CURRENT real-world AABB (standard player hitbox dimensions —
     * these routines don't have access to the entity's real bounding box for a generic
     * {@code Entity} candidate, and a fixed vanilla-standard box is a reasonable, simple
     * approximation for this manual check) and test it against every OTHER captured cell's own
     * real-world AABB (every {@code allSlots()} entry, translated via the exact same
     * {@link ContraptionMath#renderPosition} math the render swarms already use — reusing it here
     * rather than inventing new coordinate math per this task's explicit instruction). "Every
     * OTHER" deliberately excludes whichever slot(s) the rider is currently standing ON TOP of
     * (found via {@code slots}, the top-only candidate list already computed by the caller) —
     * standing on your own platform's top surface, and being carried along with it, is the
     * intended/desired behavior this whole carry system exists for; only a DIFFERENT cell's solid
     * volume should ever block the rider.
     *
     * <p>Resolution is a simple per-axis separating clamp, not a full swept-AABB physics resolver
     * (this task's own instructions call that acceptable): test the full 3-axis delta first: if
     * applying it as-is doesn't overlap anything, use it unchanged (the common case, zero extra
     * cost beyond the checks). If it WOULD overlap, try zeroing X only, then Z only, then both
     * X+Z (Y is never clamped here — vertical carry is governed entirely by
     * {@code isStandingOnFootprint}'s own window, and clamping Y would fight normal
     * standing-on-top vertical carry, e.g. a rising platform, which is never itself a "side"
     * collision) — the first candidate that doesn't overlap wins, so a diagonal push that's only
     * blocked along one axis still lets the rider slide along the other, matching ordinary
     * axis-aligned collision sliding behavior instead of freezing the rider outright.
     */
    private Vec3 clampDeltaForSideCollision(Vec3 riderPos, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw) {
        return clampDeltaForSideCollisionGeneric(riderPos, bearingWorldPos, deltaX, deltaY, deltaZ, allSlots(), RIDER_HALF_WIDTH, RIDER_HEIGHT, yaw);
    }

    /**
     * Entity-agnostic version of {@link #clampDeltaForSideCollision} — same axis-separating-clamp
     * algorithm, but parameterized on the carried thing's own half-width/height instead of the
     * fixed vanilla player dimensions, so {@link #carryEntities} can pass a mob/item/boat's real
     * {@code getBbWidth()/getBbHeight()} instead of assuming every rider is player-shaped.
     * {@code solids} is passed in (rather than recomputed) so {@link #carryEntities} can compute
     * {@link #allSlots} once outside its per-entity loop instead of once per candidate.
     */
    private static Vec3 clampDeltaForSideCollisionGeneric(Vec3 pos, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ,
            List<Slot> solids, double halfWidth, double height, double yaw) {
        if (deltaX == 0.0 && deltaZ == 0.0) {
            return new Vec3(deltaX, deltaY, deltaZ); // vertical-only motion never causes a SIDE collision
        }
        if (solids.isEmpty()) {
            return new Vec3(deltaX, deltaY, deltaZ);
        }
        // Candidates in priority order: full delta, then X-only-blocked (keep Z), then
        // Z-only-blocked (keep X), then fully blocked (X/Z both zeroed, still carried vertically).
        double[][] candidates = {
                {deltaX, deltaZ},
                {0.0, deltaZ},
                {deltaX, 0.0},
                {0.0, 0.0},
        };
        for (double[] candidate : candidates) {
            double cx = candidate[0], cz = candidate[1];
            Vec3 targetPos = new Vec3(pos.x + cx, pos.y + deltaY, pos.z + cz);
            if (!overlapsAnySolid(targetPos, bearingWorldPos, solids, halfWidth, height, yaw)) {
                return new Vec3(cx, deltaY, cz);
            }
        }
        return new Vec3(0.0, deltaY, 0.0); // every candidate overlapped — carry vertically only, don't push sideways at all
    }

    /**
     * Whether a carried thing's hitbox (half-width/height as given, centered/foot-anchored at
     * {@code targetPos} the same way a real Minecraft entity's bounding box is) would overlap ANY
     * captured cell's real-world AABB (every {@link #allSlots} entry — full footprint, not just
     * top cells, matching {@link #rebuild}'s "every cell gets a hitbox" javadoc). See
     * {@link #clampDeltaForSideCollisionGeneric} for how this is used (the standing-on-top
     * cell(s) are NOT excluded here on purpose — see below).
     *
     * <p>Why the standing cell doesn't need its own exclusion despite "every OTHER cell": a
     * rider's feet rest at/above a slot's TOP face (see {@link #isStandingOnFootprint}'s
     * {@code topY} math) — their hitbox (from foot level upward) never actually overlaps that
     * same slot's own volume, so no explicit exclusion list is needed... <b>provided this
     * method's own solid-box Y-extent uses that SAME real top face, not the flat full-cell
     * height.</b>
     *
     * <p><b>2026-07-02 session, carry-fix follow-up #2 — "cofres stairs slabs cactus no
     * sostienen al jugador como si lo hacen bloques completos"</b>: this method used to build
     * each slot's solid Y-extent as {@code [ly, ly + slot.height)} — {@code slot.height} is
     * ALWAYS the flat full-cell {@code 1f} (see {@code rebuild}'s {@code Slot} construction,
     * deliberately kept at 1/1 for the INTERACTION click/collision spawn regardless of the real
     * block's shape). For a block whose REAL standable top is below the cell ceiling — a chest
     * ({@code standTopY≈0.875}), cactus ({@code≈0.9375}), or a bottom slab ({@code 0.5}) — a
     * rider genuinely standing on that real surface has feet at
     * {@code ly + standTopY < ly + 1.0 = the OLD maxY}, so their foot-level Y
     * ({@code rMinY = standTopY}) fell BELOW the old {@code maxY}, i.e. STILL inside
     * {@code [minY, maxY)} — the rider's own standing position registered as "embedded in this
     * cell's own solid volume." {@link #clampDeltaForSideCollisionGeneric} never clamps Y, only
     * X/Z, so this Y-only false positive could never be resolved by any of its 4 candidates —
     * EVERY candidate (full delta, X-clamped, Z-clamped, both-clamped) still "overlapped" this
     * same self-cell, falling through to its last resort ({@code new Vec3(0, deltaY, 0)}), i.e.
     * the horizontal carry delta got silently zeroed EVERY TICK for exactly these shapes — matching
     * the reported symptom precisely: passive standing/collision was fine (the shulker collider
     * layer already used the real shape correctly), but the moment the contraption actually moved,
     * the rider was left behind/fell off relative to it, since only Y ever got carried (never X/Z).
     * A true full cube was never affected: {@code standTopY == height == 1.0} there, so
     * {@code rMinY(1.0) < maxY(1.0)} is false (strict {@code <}) — no false overlap. Now uses the
     * REAL shape-derived {@code [standBottomY, standTopY)} window (falls back to {@code [0,height]}
     * for legacy/custom slots, identical to the old behavior for those) so a rider standing exactly
     * on a shape's real top is never again mistaken for "inside" that same cell.
     *
     * <p>Slots with {@link Slot#hasCollision} {@code false} (torches, tripwire, most plants/flowers,
     * etc. — see {@code computeCell}'s {@code shape.isEmpty()} javadoc) are skipped entirely —
     * see that field's own javadoc for the companion "torch shoves the player like a full block"
     * bug this closes.
     */
    private static boolean overlapsAnySolid(Vec3 targetPos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth, double height, double yaw) {
        return overlapsAnySolid(targetPos, bearingWorldPos, solids, halfWidth, height, yaw, 0.0, 0.0, 1.0);
    }

    /**
     * Pitch/roll/scale-aware overlap test (2026-07-17 — "que solo haga [push] si se esta dentro del AABB
     * bounding box del bloque considerando su yaw y pitch"). A yaw-only un-rotation lines the box test up
     * with the cells of a contraption that only spins, but a phys contraption that PITCHES or ROLLS has its
     * cells tilted out of the world-axis-aligned frame, so the yaw-only test both misses entities genuinely
     * inside a tilted block and falsely reports ones that only look adjacent from above — exactly the push
     * misbehaviour. Un-rotating by the full orientation puts the entity in the same tilted frame the cells
     * live in, so "inside the block's box" means inside the ACTUAL oriented box.
     */
    private static boolean overlapsAnySolid(Vec3 targetPos, Vec3 bearingWorldPos, List<Slot> solids,
            double halfWidth, double height, double yaw, double pitch, double roll, double scale) {
        Vec3 local = dev.arubik.craftengine.contraption.ContraptionMath.realToLocal(
                targetPos, bearingWorldPos, yaw, pitch, roll, scale <= 0 ? 1.0 : scale);
        double rMinX = local.x - halfWidth, rMaxX = local.x + halfWidth;
        double rMinZ = local.z - halfWidth, rMaxZ = local.z + halfWidth;
        // Raise the rider's collision floor above the standing tolerance band (2026-07-03 — "el tren
        // sigue sin agarrarme"). isStandingOnFootprint accepts a rider whose feet sit up to 0.3
        // BELOW a cell's top face as "standing on" it, so a carried rider's real feet dip slightly
        // into the very cell they stand on. Using their raw foot Y here made overlapsAnySolid report
        // that self-cell as a collision on EVERY candidate delta, so clampDeltaForSideCollision
        // fell through to zeroing the whole horizontal carry every tick — the rider never moved with
        // the platform. Starting the collision box just above that tolerance band lets "resting on
        // top" stop counting as "embedded in", while a genuinely taller neighbor wall (≥ ~1.3 above
        // the floor) still overlaps and blocks. FLOOR_CLEARANCE (0.35) is just past the 0.3 window.
        double rMinY = local.y + FLOOR_CLEARANCE, rMaxY = local.y + height;
        for (Slot slot : solids) {
            if (!slot.hasCollision) {
                continue; // no real collision at all — e.g. a captured torch — never a "solid" to embed in
            }
            double half = slot.width / 2.0;
            double minX = slot.lx - half;
            double maxX = slot.lx + half;
            double minZ = slot.lz - half;
            double maxZ = slot.lz + half;
            double minY = slot.ly + slot.standBottomY;
            double maxY = slot.ly + slot.standTopY;
            boolean overlaps = rMaxX > minX && rMinX < maxX
                    && rMaxZ > minZ && rMinZ < maxZ
                    && rMaxY > minY && rMinY < maxY;
            if (overlaps) {
                return true;
            }
        }
        return false;
    }

    private void releaseAll() {
        for (UUID id : currentRiders) {
            PlayerCarry.release(id);
        }
        currentRiders.clear();
    }

    private static boolean isStandingOnFootprint(ServerPlayer sp, Vec3 bearingWorldPos, List<Slot> slots, double yaw) {
        return isStandingOnFootprint(sp.position(), bearingWorldPos, slots, 0.0, yaw);
    }

    private static boolean isStandingOnFootprint(ServerPlayer sp, Vec3 bearingWorldPos, List<Slot> slots, double deltaY, double yaw) {
        return isStandingOnFootprint(sp.position(), bearingWorldPos, slots, deltaY, yaw);
    }

    private static boolean isStandingOnFootprint(Vec3 pos, Vec3 bearingWorldPos, List<Slot> slots, double yaw) {
        return isStandingOnFootprint(pos, bearingWorldPos, slots, 0.0, yaw);
    }

    /**
     * {@code deltaY} — this tick's platform vertical delta (world-space blocks; 0 for a purely
     * horizontal/idle contraption) — see the widened-window comment below (2026-07-02 session,
     * vertical-lift carry-desync fix: "cuando se mueve hacia arriba los shulkers y el carry del
     * contraption fallan un poco. el jugador se unde").
     *
     * <p><b>Root cause investigated</b>: the render/carry call order in {@code ContraptionEngine}
     * is NOT the problem — {@code stepKinematics} advances {@code state.x/y/z} FIRST, then
     * {@code ContraptionEntity#render} sends the shulker/hitbox position-sync packets at that SAME
     * already-advanced bearing, then {@code carryRiders} reads {@code state.lastDeltaY()} and checks
     * this method against that SAME bearing — shulker visual position and carry math always agree
     * on "where is the platform this tick," so there is no cross-tick position mismatch here.
     *
     * <p>The actual gap is {@code PlayerCarry.carry}'s own early-return gates (that file is
     * read-only reference for this session — see its class javadoc): while the rider has the jump
     * key held OR any directional (WASD) key held, {@code PlayerCarry.carry} returns BEFORE ever
     * touching Y — meaning on a continuously-rising lift, any tick the player jumps or so much as
     * taps a movement key, that tick's vertical carry impulse is silently dropped entirely while
     * the shulker/hitbox floor has already moved up. On a purely horizontal contraption this was
     * harmless (dy was 0 anyway); on a vertical lift it is not — real gravity keeps pulling the
     * player down every one of those skipped ticks while the platform underneath them keeps
     * rising, which is exactly "el jugador se unde." That gap can only be closed inside
     * {@code PlayerCarry} itself (skipping the Y-carry tick during jump/WASD input is deliberate
     * client-authority-respecting design there, not an oversight) — NOT something this class's
     * carry call site can fix by itself.
     *
     * <p>What THIS method can and does fix: on a fast-moving lift, the player's real Y (subject to
     * the gap above, plus ordinary per-tick fall/jump physics) can drift outside the old fixed
     * {@code [topY-0.3, topY+0.9]} (1.2-block) window within a single tick, especially right after
     * one of the skipped-carry ticks above. When that happens the rider was being unregistered from
     * {@code currentRiders} (an explicit {@code PlayerCarry.release} call) and re-registered the
     * next tick it re-entered the window — a flapping on/off classification that reads exactly like
     * intermittent "sinking then catching" on a rising platform: released tick loses ANY carry
     * impulse at all (even the ticks that otherwise would have applied dy), which is strictly worse
     * than the base gap above. Widening the window's lower/upper bound by {@code |deltaY|} (the
     * platform's own this-tick vertical travel) keeps a rider who's merely trailing the platform by
     * about one tick's worth of vertical motion still classified as "riding," so the very next
     * non-jumping/non-WASD tick resumes the additive Y carry instead of the player having to fully
     * re-enter a now out-of-date fixed window first.
     */
    private static boolean isStandingOnFootprint(Vec3 pos, Vec3 bearingWorldPos, List<Slot> slots, double deltaY, double yaw) {
        // Yaw-aware footprint test (2026-07-03 session — "el minecart s emueve pero no me mueve a
        // mi"). A minecart contraption is yaw-rotated to follow the rail direction, so its blocks
        // RENDER at rotated world positions (via ContraptionMath.renderPosition) while the slot
        // offsets (slot.lx/lz) stay in the un-rotated local frame. The old check compared the
        // rider's raw WORLD position against bearingWorldPos + slot.lx (axis-aligned) — correct only
        // at yaw≈0, wrong for any rail-following train, so the rider standing on the visibly-rotated
        // deck never matched any slot and was never carried (nor got footstep sounds). Fix: undo the
        // contraption's rotation on the rider's position first (realToLocal is renderPosition's exact
        // inverse — pivot/yaw aware), then compare in the same local frame the slots live in. Yaw is
        // a rotation about the vertical axis, so Y is unaffected: local.y == pos.y - bearingWorldPos.y
        // and the slot's local top is simply slot.ly + slot.standTopY.
        Vec3 local = dev.arubik.craftengine.contraption.ContraptionMath.realToLocal(pos, bearingWorldPos, yaw);
        double verticalSlack = Math.min(Math.abs(deltaY), 1.0); // cap: never trust more than 1 block/tick of slack
        for (Slot slot : slots) {
            if (!slot.hasCollision) {
                continue; // no real collision at all — e.g. a captured torch — nothing to stand ON
            }
            double minX = slot.lx - slot.width / 2.0;
            double maxX = slot.lx + slot.width / 2.0;
            double minZ = slot.lz - slot.width / 2.0;
            double maxZ = slot.lz + slot.width / 2.0;
            // Real shape-derived top surface (2026-07-02 session, carry-fix follow-up — "los
            // shulker de slabs y stairs y figuras no normales no agarran el sistema de carry").
            // This used to be `slot.ly + slot.height`, i.e. always the cell's FULL flat height
            // (height is always 1f for every auto-derived slot — see rebuild()'s Slot construction
            // — regardless of the real captured block's shape). For a BOTTOM slab (real collision
            // surface at local Y 0..0.5, so its true top face is cell-local y+0.5) that flat
            // assumption put topY a full 0.5 blocks too HIGH (cell-local y+1.0 instead of y+0.5).
            // With this method's existing -0.3/+0.9 tolerance window, a player's real feet resting
            // on the slab's actual surface (world Y = bearingY + slot.ly + 0.5) fell BELOW
            // (topY - 0.3) = bearingY + slot.ly + 0.7 — i.e. 0.2 blocks outside the window's lower
            // bound — so bottom slabs never registered as "standing on the footprint" and were
            // silently skipped by carryRiders/carryEntities every tick (never carried, and never
            // released either since they were never added to currentRiders in the first place —
            // matches the reported "left behind"/"falls through relative to the structure"
            // symptom exactly). A TOP slab's real surface (local Y 0.5..1.0, true top at y+1.0)
            // happened to coincide with the flat assumption by luck of top-slab geometry, which is
            // why the bug appeared shape/orientation-dependent rather than uniformly broken.
            // slot.standTopY now holds the REAL shape-derived local top (see computeCell /
            // this slot's construction in rebuild()), so this now correctly resolves to
            // bearingY + slot.ly + 0.5 for a bottom slab, matching its genuine collision surface.
            double topY = slot.ly + slot.standTopY; // local top face (Y unaffected by yaw)
            double localFeetY = local.y; // == pos.y - bearingWorldPos.y
            if (local.x >= minX - 0.3 && local.x <= maxX + 0.3
                    && local.z >= minZ - 0.3 && local.z <= maxZ + 0.3
                    && localFeetY >= topY - 0.3 - verticalSlack && localFeetY <= topY + 0.9 + verticalSlack) {
                return true;
            }
        }
        return false;
    }

    /**
     * Non-player counterpart to {@link #carryRiders} (2026-07-02 session — "otras entidades que
     * no sean jugador no se mantienen sobre la contraption y la atraviesan": mobs, dropped items,
     * boats, etc. standing on a moving contraption's footprint were never carried at all, so they
     * got left behind — or fell straight through, since these hitbox slots are packet-only and
     * give a non-player entity zero real collision either).
     *
     * <p>Unlike {@link #carryRiders}, a non-player {@link Entity}'s position is fully
     * server-authoritative — there's no client prediction to fight, so this skips
     * {@code PlayerCarry}'s whole velocity-nudge/ack-gating dance entirely and just directly
     * repositions the entity via {@link Entity#setPos} (same convention vanilla itself uses to
     * carry an entity standing on something that pushes it — e.g. a piston's
     * {@code MovingPistonBlock} shoving entities along, or a boat/minecart's own
     * {@code Entity#move} integrating its velocity into position every tick): update both
     * coordinates and the tracked bounding box, then notify {@link Entity#setOldPosAndRot} so
     * nearby clients' interpolation doesn't visibly snap. Position resync to observers happens
     * automatically afterward the same way it already does for any other server-side entity move
     * (next tracked-entity update packet), no extra packet plumbing needed here.
     *
     * <p>Reuses the exact same exposed-top-cell footprint slots and {@link #isStandingOnFootprint}
     * math {@link #carryRiders} uses — "standing on top of this contraption" means the same thing
     * regardless of whether the rider is a player or not.
     */
    public void carryEntities(List<net.minecraft.world.entity.Entity> candidates, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw) {
        if (deltaX == 0.0 && deltaY == 0.0 && deltaZ == 0.0) {
            return;
        }
        List<Slot> slots = topSlotsOnly();
        if (slots.isEmpty()) {
            return;
        }
        List<Slot> solids = allSlots();
        for (net.minecraft.world.entity.Entity entity : candidates) {
            if (entity == null || entity.isRemoved()) {
                continue;
            }
            if (isStandingOnFootprint(entity.position(), bearingWorldPos, slots, yaw)) {
                // Side-collision clamp (see ContraptionHitboxSwarm#clampDeltaForSideCollision's
                // javadoc — same "otherwise blindly repositions by the platform's delta with zero
                // check against a NEIGHBORING cell's own solid volume" bug carryRiders had, just
                // for non-player entities: their own real bounding box half-extents/height are
                // used instead of a fixed player-sized box since a generic Entity here could be
                // anything from an item to a boat).
                double halfWidth = entity.getBbWidth() / 2.0;
                double height = entity.getBbHeight();
                Vec3 pos = entity.position();
                Vec3 clamped = clampDeltaForSideCollisionGeneric(pos, bearingWorldPos, deltaX, deltaY, deltaZ, solids, halfWidth, height, yaw);
                entity.setPos(entity.getX() + clamped.x, entity.getY() + clamped.y, entity.getZ() + clamped.z);
                entity.setOldPosAndRot();
            }
        }
    }

    /**
     * Convenience wrapper for {@link #carryEntities(List, Vec3, double, double, double)} that
     * also does the real-world entity gather + candidate filtering itself (2026-07-02 session).
     * Kept HERE rather than in {@code ContraptionEntity}/{@code ContraptionEngine} specifically
     * so the exclusion check below can call {@link ContraptionItemPickupSwarm#isMirror}, which is
     * package-private (deliberately not widened — that class is a concurrent agent's read-only
     * file right now) and only reachable from other classes in this same {@code render} package.
     *
     * <p>Scoped to a small AABB around the contraption's current real-world bounding box (footprint
     * top-cell slots + a 1-block margin, not the whole world) — cheap, proportional to contraption
     * size. Excludes real {@link ServerPlayer}s (those ride the separate {@link #carryRiders} path,
     * which needs {@code PlayerCarry}'s client-prediction-aware nudging instead of a raw
     * teleport) and any {@link ItemEntity} that is one of {@link ContraptionItemPickupSwarm}'s own
     * real-world pickup mirrors (those are already independently kept in sync with the
     * contraption's live transform every tick by that swarm — see its class javadoc; carrying them
     * AGAIN here would double-apply the delta and make them drift away twice as fast).
     *
     * <p>{@code anchorEntityId} (nullable) is the entity the contraption is anchored to and driven BY —
     * a harnessed ghast flying inside its own structure. Carrying it would add the delta it itself
     * produced back onto its own position, doubling its speed every tick. See
     * {@code ContraptionState#anchorEntityId}.
     */
    public void carryNearbyEntities(net.minecraft.world.level.Level realLevel, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw, java.util.UUID anchorEntityId) {
        if (deltaX == 0.0 && deltaY == 0.0 && deltaZ == 0.0) {
            return;
        }
        List<Slot> slots = topSlotsOnly();
        if (slots.isEmpty() || !(realLevel instanceof net.minecraft.server.level.ServerLevel)) {
            return;
        }
        double minX = bearingWorldPos.x, maxX = bearingWorldPos.x;
        double minY = bearingWorldPos.y, maxY = bearingWorldPos.y;
        double minZ = bearingWorldPos.z, maxZ = bearingWorldPos.z;
        for (Slot slot : slots) {
            minX = Math.min(minX, bearingWorldPos.x + slot.lx - slot.width / 2.0);
            maxX = Math.max(maxX, bearingWorldPos.x + slot.lx + slot.width / 2.0);
            minY = Math.min(minY, bearingWorldPos.y + slot.ly);
            maxY = Math.max(maxY, bearingWorldPos.y + slot.ly + slot.height);
            minZ = Math.min(minZ, bearingWorldPos.z + slot.lz - slot.width / 2.0);
            maxZ = Math.max(maxZ, bearingWorldPos.z + slot.lz + slot.width / 2.0);
        }
        // This gather box is built from the UN-rotated local footprint; a yaw-rotated contraption's
        // real footprint can bulge past it (rotating a box of half-extents (a,b) grows its AABB by up
        // to a+b per axis). Inflate by that worst-case span so a rotated minecart train's riders are
        // still gathered (the membership test below, isStandingOnFootprint, is exact — this only
        // needs to not under-cover) (2026-07-03).
        double rotationMargin = ((maxX - minX) + (maxZ - minZ)) / 2.0;
        net.minecraft.world.phys.AABB bounds = new net.minecraft.world.phys.AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(1.0 + rotationMargin);
        List<net.minecraft.world.entity.Entity> nearby;
        try {
            nearby = realLevel.getEntities((net.minecraft.world.entity.Entity) null, bounds, e -> true);
        } catch (Throwable t) {
            return; // best-effort — same defensive shape ContraptionFurnitureCapture#captureNear uses
        }
        List<net.minecraft.world.entity.Entity> candidates = new ArrayList<>();
        for (net.minecraft.world.entity.Entity entity : nearby) {
            if (entity instanceof ServerPlayer) {
                continue; // real players carried separately via carryRiders (client-prediction-aware)
            }
            if (entity instanceof net.minecraft.world.entity.item.ItemEntity
                    && ContraptionItemPickupSwarm.isMirror(entity.getUUID())) {
                continue; // already position-synced by ContraptionItemPickupSwarm — avoid double-carry
            }
            if (entity.getUUID().equals(anchorEntityId)) {
                continue; // the anchor drives this contraption; carrying it would double its own delta
            }
            candidates.add(entity);
        }
        carryEntities(candidates, bearingWorldPos, deltaX, deltaY, deltaZ, yaw);
    }

    /**
     * Solid pushback for a BYSTANDER player — one who is NOT being carried (not standing on top
     * of this contraption's footprint per {@link #isStandingOnFootprint}), but whose current
     * position would end up embedded inside one of this contraption's captured cells after this
     * tick's movement (e.g. the contraption is moving TOWARD a player who is independently
     * walking INTO it) (2026-07-02 session — "si te mueves en dirección contraria al movimiento
     * contra el muro, lo atraviesas — el muro debería empujarte").
     *
     * <p><b>Root cause this addresses:</b> {@link #carryRiders}'s side-collision clamp (see
     * {@link #clampDeltaForSideCollision}) only ever runs for candidates ALREADY confirmed
     * standing on top of the footprint (i.e. already being carried) — it validates the CARRY
     * delta applied to a rider, never a bystander's own independently-WASD-driven position. A
     * player merely standing in the path of an approaching contraption (not on top of it) was
     * never a candidate for ANY check at all: {@link #carryRiders} skips them (not on the
     * footprint), and this system has no other collision mechanism for a player's own movement —
     * the real vanilla-style physics that's supposed to stop this
     * ({@code ContraptionShulkerColliderSwarm}'s genuine SHULKER entity, which the client's own
     * local physics engine collides against for real) only works if the shulker's last-sent
     * position is not lagging behind the server's authoritative position by the time the player's
     * own client-predicted movement reaches it — a real, inherent network-timing race this
     * server-side check cannot fully close on its own, but CAN backstop: once the player's
     * reported position (from their last {@code ServerboundMovePlayerPacket}, i.e.
     * {@code ServerPlayer#position()}) is already geometrically inside where a cell is ABOUT TO
     * be this tick, push them back out immediately rather than leaving it entirely to client-side
     * collision that may have already been beaten by a fast enough approach.
     *
     * <p>Deliberately NOT part of {@link #carryRiders}'s candidate loop (which is scoped to
     * "already standing on top") — this iterates EVERY nearby real player regardless of carry
     * status, and reuses {@link PlayerCarry#carry} (the same velocity-based nudge mechanism, per
     * this project's explicit "vuelve al empujon" preference) rather than inventing a second
     * transport, for consistency between "being carried along" and "being shoved out of the way."
     * Resolution is the same per-axis separating logic {@link #clampDeltaForSideCollisionGeneric}
     * already uses: compute how far OUT of the (post-movement) solid volume the player needs to
     * move along the shortest axis, and push exactly that far — a genuine solid-pushback (closer
     * to vanilla piston-push behavior) rather than a "ride along" carry.
     */
    public void pushBackNearbyBystanders(net.minecraft.world.level.Level realLevel, Vec3 bearingWorldPos,
            double deltaX, double deltaY, double deltaZ, double yaw) {
        pushBackNearbyBystanders(realLevel, bearingWorldPos, deltaX, deltaY, deltaZ, yaw, ContraptionPushSettings.DEFAULT);
    }

    /**
     * {@link ContraptionPushSettings}-aware overload (2026-07-15 session — "contraption collide
     * detection and settings to push up"): the bystander shove is scaled by
     * {@link ContraptionPushSettings#pushStrength} and, when
     * {@link ContraptionPushSettings#pushUpEnabled} is set, a shove against a lip no taller than
     * {@link ContraptionPushSettings#maxStepUpHeight} is converted into an upward step-up lift
     * instead (see {@link #applyPushSettings}). {@link ContraptionPushSettings#DEFAULT} reproduces
     * the pre-settings behavior exactly ({@code pushStrength=1}, {@code pushUpEnabled=false}).
     */
    public void pushBackNearbyBystanders(net.minecraft.world.level.Level realLevel, Vec3 bearingWorldPos,
            double deltaX, double deltaY, double deltaZ, double yaw, ContraptionPushSettings settings) {
        pushBackNearbyBystanders(realLevel, bearingWorldPos, deltaX, deltaY, deltaZ, yaw, 0.0, 0.0, 1.0, settings);
    }

    /** Pitch/roll/scale-aware {@link #pushBackNearbyBystanders} — orientation-aware push detection (2026-07-17). */
    public void pushBackNearbyBystanders(net.minecraft.world.level.Level realLevel, Vec3 bearingWorldPos,
            double deltaX, double deltaY, double deltaZ, double yaw, double pitch, double roll, double scale,
            ContraptionPushSettings settings) {
        // A STATIC contraption never shoves a PLAYER (2026-07-17 — "si un contraption esta estatico no
        // aplicar pushup a player, solo a entidades"): a player's own packet-only SHULKER colliders already
        // stop them walking into it, so a server-side push here is redundant and only ever fires
        // spuriously — that is the "me sacan volando cuando no deberian". Non-players have no shulker, so
        // pushBackNearbyEntities still pushes them when static. Threshold, not exact zero, because the async
        // solver reports tiny resting jitter that is not real movement.
        if (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ < STATIC_DELTA_SQ) {
            return;
        }
        if (settings == null) {
            settings = ContraptionPushSettings.DEFAULT;
        }
        List<Slot> solids = allSlots();
        if (solids.isEmpty() || !(realLevel instanceof net.minecraft.server.level.ServerLevel)) {
            return;
        }
        double minX = bearingWorldPos.x, maxX = bearingWorldPos.x;
        double minY = bearingWorldPos.y, maxY = bearingWorldPos.y;
        double minZ = bearingWorldPos.z, maxZ = bearingWorldPos.z;
        for (Slot slot : solids) {
            minX = Math.min(minX, bearingWorldPos.x + slot.lx - slot.width / 2.0);
            maxX = Math.max(maxX, bearingWorldPos.x + slot.lx + slot.width / 2.0);
            minY = Math.min(minY, bearingWorldPos.y + slot.ly);
            maxY = Math.max(maxY, bearingWorldPos.y + slot.ly + slot.height);
            minZ = Math.min(minZ, bearingWorldPos.z + slot.lz - slot.width / 2.0);
            maxZ = Math.max(maxZ, bearingWorldPos.z + slot.lz + slot.width / 2.0);
        }
        // Widen for yaw rotation just like carryNearbyEntities (2026-07-03) — the local footprint box
        // under-covers a rotated contraption's real footprint.
        double rotationMargin = ((maxX - minX) + (maxZ - minZ)) / 2.0;
        net.minecraft.world.phys.AABB bounds = new net.minecraft.world.phys.AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(2.0 + rotationMargin);
        List<ServerPlayer> nearby;
        try {
            nearby = realLevel.getEntitiesOfClass(ServerPlayer.class, bounds, p -> true);
        } catch (Throwable t) {
            return; // best-effort — same defensive shape ContraptionHitboxSwarm#carryNearbyEntities uses
        }
        List<Slot> topSlots = topSlotsOnly();
        for (ServerPlayer sp : nearby) {
            if (currentRiders.contains(sp.getUUID())) {
                continue; // already carried via carryRiders — that path owns this rider's delta
            }
            if (!topSlots.isEmpty() && isStandingOnFootprint(sp, bearingWorldPos, topSlots, yaw)) {
                continue; // standing on top but not yet in currentRiders (same-tick race) — carryRiders owns it
            }
            Vec3 playerPos = sp.position();
            Vec3 push = computeSolidPush(playerPos, bearingWorldPos, solids, RIDER_HALF_WIDTH, RIDER_HEIGHT, yaw,
                    pitch, roll, scale, deltaX, deltaY, deltaZ);
            // Apply configurable strength + opt-in step-up (a bystander player is never silently
            // "carried up" — allowCarryUp=false — only shoved, or lifted-over-a-lip when pushUp on).
            push = applyPushSettings(push, playerPos, bearingWorldPos, solids, RIDER_HALF_WIDTH, RIDER_HEIGHT, yaw,
                    settings, false);
            // WALLS ONLY, never the floor (2026-07-17 — "solo contra paredes no contra el suelo que pise").
            // A bystander is shoved sideways out of a wall's path; they must never be lifted, which is what
            // read as being launched into the air. Vertical support is carryRiders' job (standing on top),
            // not this out-of-the-way shove.
            if (push.x == 0.0 && push.z == 0.0) {
                continue;
            }
            PlayerCarry.carry(sp, push.x, 0.0, push.z);
        }
    }

    /**
     * Non-player counterpart to {@link #pushBackNearbyBystanders} (2026-07-03 — "entidades que no
     * son players no pueden interactuar bien con las contraption, las atraviesan"). The contraption's
     * blocks live in a hidden {@code ContraptionLevel} and its colliders are packet-only (client-side
     * for players only), so a real mob/animal/item runs its server-side physics against the EMPTY
     * real world and walks straight through the structure. This gives them server-authoritative
     * solid collision: every nearby non-player entity that is embedded in — or being run over by —
     * a solid cell is directly repositioned out (its own real bounding box drives the push extent,
     * not a fixed player size), and any velocity still driving it INTO the wall is zeroed so it
     * doesn't immediately re-penetrate next tick.
     *
     * <p>Excludes real players (owned by {@link #pushBackNearbyBystanders}) and
     * {@link ContraptionItemPickupSwarm}'s mirror items (already position-synced by that swarm);
     * entities standing on TOP are left to {@link #carryNearbyEntities} (a rider, not a bystander).
     */
    public void pushBackNearbyEntities(net.minecraft.world.level.Level realLevel, Vec3 bearingWorldPos,
            double deltaX, double deltaY, double deltaZ, double yaw) {
        pushBackNearbyEntities(realLevel, bearingWorldPos, deltaX, deltaY, deltaZ, yaw, ContraptionPushSettings.DEFAULT,
                null);
    }

    /**
     * {@link ContraptionPushSettings}-aware overload (2026-07-15 session — see
     * {@link #pushBackNearbyBystanders(net.minecraft.world.level.Level, Vec3, double, double, double, double, ContraptionPushSettings)}).
     * In addition to {@link ContraptionPushSettings#pushStrength} scaling and the
     * {@link ContraptionPushSettings#pushUpEnabled} step-up path, a non-player entity honors
     * {@link ContraptionPushSettings#carryEntities}: when set and a step-up lip within
     * {@link ContraptionPushSettings#maxStepUpHeight} exists, the entity is lifted UP onto the step
     * (horizontal shove suppressed) rather than shoved back. {@link ContraptionPushSettings#DEFAULT}
     * reproduces the pre-settings behavior exactly.
     *
     * <p>{@code anchorEntityId} (nullable) is exempt: the entity the contraption is anchored to and
     * driven by flies INSIDE its own structure by design (a harnessed ghast — "self block of this
     * contraption should not affect the same ghast"), so it must never be shoved out of its own walls.
     * See {@code ContraptionState#anchorEntityId}. Only that one entity — every other ghast, including
     * another harnessed one, still collides normally.
     */
    public void pushBackNearbyEntities(net.minecraft.world.level.Level realLevel, Vec3 bearingWorldPos,
            double deltaX, double deltaY, double deltaZ, double yaw, ContraptionPushSettings settings,
            java.util.UUID anchorEntityId) {
        pushBackNearbyEntities(realLevel, bearingWorldPos, deltaX, deltaY, deltaZ, yaw, 0.0, 0.0, 1.0, settings,
                anchorEntityId);
    }

    /** Pitch/roll/scale-aware {@link #pushBackNearbyEntities} — orientation-aware push detection (2026-07-17). */
    public void pushBackNearbyEntities(net.minecraft.world.level.Level realLevel, Vec3 bearingWorldPos,
            double deltaX, double deltaY, double deltaZ, double yaw, double pitch, double roll, double scale,
            ContraptionPushSettings settings, java.util.UUID anchorEntityId) {
        if (settings == null) {
            settings = ContraptionPushSettings.DEFAULT;
        }
        List<Slot> solids = allSlots();
        if (solids.isEmpty() || !(realLevel instanceof net.minecraft.server.level.ServerLevel)) {
            return;
        }
        double minX = bearingWorldPos.x, maxX = bearingWorldPos.x;
        double minY = bearingWorldPos.y, maxY = bearingWorldPos.y;
        double minZ = bearingWorldPos.z, maxZ = bearingWorldPos.z;
        for (Slot slot : solids) {
            minX = Math.min(minX, bearingWorldPos.x + slot.lx - slot.width / 2.0);
            maxX = Math.max(maxX, bearingWorldPos.x + slot.lx + slot.width / 2.0);
            minY = Math.min(minY, bearingWorldPos.y + slot.ly);
            maxY = Math.max(maxY, bearingWorldPos.y + slot.ly + slot.height);
            minZ = Math.min(minZ, bearingWorldPos.z + slot.lz - slot.width / 2.0);
            maxZ = Math.max(maxZ, bearingWorldPos.z + slot.lz + slot.width / 2.0);
        }
        double rotationMargin = ((maxX - minX) + (maxZ - minZ)) / 2.0;
        net.minecraft.world.phys.AABB bounds = new net.minecraft.world.phys.AABB(minX, minY, minZ, maxX, maxY, maxZ)
                .inflate(2.0 + rotationMargin);
        List<net.minecraft.world.entity.Entity> nearby;
        try {
            nearby = realLevel.getEntities((net.minecraft.world.entity.Entity) null, bounds, e -> true);
        } catch (Throwable t) {
            return;
        }
        List<Slot> topSlots = topSlotsOnly();
        for (net.minecraft.world.entity.Entity entity : nearby) {
            if (entity == null || entity.isRemoved() || entity instanceof ServerPlayer) {
                continue; // players handled by pushBackNearbyBystanders
            }
            if (entity instanceof net.minecraft.world.entity.item.ItemEntity
                    && ContraptionItemPickupSwarm.isMirror(entity.getUUID())) {
                continue; // owned by the pickup swarm
            }
            if (entity.getUUID().equals(anchorEntityId)) {
                continue; // flies inside its own structure by design — see the javadoc
            }
            if (!topSlots.isEmpty() && isStandingOnFootprint(entity.position(), bearingWorldPos, topSlots, yaw)) {
                continue; // standing on top — carryNearbyEntities owns it
            }
            double halfWidth = entity.getBbWidth() / 2.0;
            double height = entity.getBbHeight();
            Vec3 push = computeSolidPush(entity.position(), bearingWorldPos, solids, halfWidth, height, yaw,
                    pitch, roll, scale, deltaX, deltaY, deltaZ);
            // Configurable strength + opt-in step-up; allowCarryUp=true so carryEntities can lift a
            // non-rider onto the step instead of shoving it back (see applyPushSettings).
            push = applyPushSettings(push, entity.position(), bearingWorldPos, solids, halfWidth, height, yaw,
                    settings, true);
            if (push.equals(Vec3.ZERO)) {
                continue;
            }
            // Server-authoritative reposition (unlike a player, a non-player entity's position is
            // fully server-owned — direct setPos is exact, no client-prediction dance needed).
            entity.setPos(entity.getX() + push.x, entity.getY() + push.y, entity.getZ() + push.z);
            entity.setOldPosAndRot();
            // Kill any velocity still driving it into the wall so it doesn't re-penetrate next tick.
            Vec3 vel = entity.getDeltaMovement();
            double nvx = push.x != 0.0 && Math.signum(vel.x) == -Math.signum(push.x) ? 0.0 : vel.x;
            double nvz = push.z != 0.0 && Math.signum(vel.z) == -Math.signum(push.z) ? 0.0 : vel.z;
            if (nvx != vel.x || nvz != vel.z) {
                entity.setDeltaMovement(nvx, vel.y, nvz);
            }
        }
    }

    /**
     * The push an entity/player at {@code pos} needs to NOT be run over by (or embedded in) the
     * contraption this tick — the shared core of both {@link #pushBackNearbyBystanders} (players)
     * and {@link #pushBackNearbyEntities} (mobs/items/etc.), returning {@link Vec3#ZERO} when the
     * thing is clear.
     *
     * <p>Two regimes, in order:
     * <ul>
     *   <li><b>Embedded now / about to be</b> (the original working behavior, 2026-07-03): if
     *   {@code pos} — or {@code pos + platformΔ} — already overlaps a solid, {@link #resolvePushOut}
     *   ejects it sideways; if that returns zero (touching but not yet penetrating) it's shoved
     *   along the platform's own horizontal travel {@code (dx,dz)} so the wall doesn't reach it.</li>
     *   <li><b>Swept (high speed)</b> ("cuando el contraption se mueve muy rapido no le da tiempo a
     *   empujar ... lo traspasa"): at high platform speed a wall can jump ENTIRELY past a thin
     *   entity between ticks, so neither endpoint above overlaps and it's missed. {@link
     *   #resolveSweptPushOut} tests the entity against each solid box EXPANDED backward by this
     *   tick's platform delta (the volume the wall swept through) and, if the wall passed through,
     *   shoves the entity to just AHEAD of the wall's leading face so it rides in front instead of
     *   being left behind/clipped.</li>
     * </ul>
     */
    private static Vec3 computeSolidPush(Vec3 pos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth,
            double height, double yaw, double dx, double dy, double dz) {
        return computeSolidPush(pos, bearingWorldPos, solids, halfWidth, height, yaw, 0.0, 0.0, 1.0, dx, dy, dz);
    }

    /**
     * Pitch/roll/scale-aware push (2026-07-17). Only the DETECTION (is the entity inside a solid) is made
     * orientation-aware — the resulting shove stays horizontal-in-yaw, which is what a pushed-out entity
     * wants regardless of the contraption's tilt. See {@link #overlapsAnySolid}.
     */
    private static Vec3 computeSolidPush(Vec3 pos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth,
            double height, double yaw, double pitch, double roll, double scale, double dx, double dy, double dz) {
        Vec3 target = new Vec3(pos.x + dx, pos.y + dy, pos.z + dz);
        boolean now = overlapsAnySolid(pos, bearingWorldPos, solids, halfWidth, height, yaw, pitch, roll, scale);
        boolean next = overlapsAnySolid(target, bearingWorldPos, solids, halfWidth, height, yaw, pitch, roll, scale);
        // Heading gate (2026-07-17 — "y/o si esta dirigiendose ... evitar miss behaviors"): if the entity
        // is not inside a solid NOW and won't be after the contraption's move, only a genuine sweep toward
        // it should push it. resolveSweptPushOut already restricts to the swept volume, so an entity that is
        // merely adjacent and not being driven into is left alone rather than nudged every tick.
        if (now || next) {
            Vec3 push = resolvePushOut(pos, bearingWorldPos, solids, halfWidth, height, yaw);
            if (!push.equals(Vec3.ZERO)) {
                return push;
            }
            return new Vec3(dx, 0.0, dz); // touching a closing wall — shove along its travel
        }
        return resolveSweptPushOut(pos, bearingWorldPos, solids, halfWidth, height, yaw, dx, dy, dz);
    }

    /**
     * Applies {@link ContraptionPushSettings} to a raw solid-pushback vector (2026-07-15 session —
     * "contraption collide detection and settings to push up"). Purely a post-processing layer on
     * top of {@link #computeSolidPush}'s result — the underlying collision MATH is untouched, so
     * {@link ContraptionPushSettings#DEFAULT} ({@code pushStrength=1}, {@code pushUpEnabled=false},
     * {@code carryEntities=false}) returns the input push unchanged and behavior is identical to
     * before this setting existed.
     *
     * <ul>
     *   <li><b>Strength</b>: the horizontal shove is scaled by
     *   {@link ContraptionPushSettings#pushStrength} (default {@code 1.0} — a no-op).</li>
     *   <li><b>Opt-in step-up</b> (guarded by {@link ContraptionPushSettings#pushUpEnabled}, off by
     *   default so this whole branch is dormant): when a horizontal shove would push the thing
     *   back and the solid directly under/around it presents a lip no taller than
     *   {@link ContraptionPushSettings#maxStepUpHeight} above its feet (measured via
     *   {@link #lipHeightAboveFeet}), the thing is lifted UP by that lip height (times
     *   {@link ContraptionPushSettings#pushUpStrength}) so it steps up over the lip instead of
     *   being shoved back.</li>
     *   <li><b>Carry up</b> ({@code allowCarryUp} + {@link ContraptionPushSettings#carryEntities},
     *   non-player entities only): when a step-up lift is applied, the horizontal shove is
     *   suppressed so the entity is carried up onto the step rather than also pushed sideways. A
     *   bystander player passes {@code allowCarryUp=false} and is never silently lifted this way.</li>
     * </ul>
     */
    private static Vec3 applyPushSettings(Vec3 push, Vec3 pos, Vec3 bearingWorldPos, List<Slot> solids,
            double halfWidth, double height, double yaw, ContraptionPushSettings settings, boolean allowCarryUp) {
        if (push.equals(Vec3.ZERO)) {
            return push; // nothing to push — leave it alone (and skip the lip query)
        }
        double px = push.x * settings.pushStrength;
        double pz = push.z * settings.pushStrength;
        double py = push.y;
        boolean carryUp = allowCarryUp && settings.carryEntities;
        if ((settings.pushUpEnabled || carryUp) && (px != 0.0 || pz != 0.0)) {
            double lip = lipHeightAboveFeet(pos, bearingWorldPos, solids, halfWidth, yaw);
            if (lip > 0.0 && lip <= settings.maxStepUpHeight) {
                py += lip * settings.pushUpStrength;
                if (carryUp) {
                    // Carried UP onto the step, not shoved: drop the horizontal component.
                    px = 0.0;
                    pz = 0.0;
                }
            }
        }
        return new Vec3(px, py, pz);
    }

    /**
     * Height (blocks) of the tallest solid top face above the thing's feet among every
     * {@code hasCollision} slot whose XZ footprint the thing overlaps — i.e. the "lip" a step-up
     * would have to clear (see {@link #applyPushSettings}). Works in the local slot frame (un-rotate
     * {@code pos} by {@code yaw}, matching {@link #overlapsAnySolid}) and reuses each slot's real
     * shape-derived top ({@code slot.standTopY}). Returns {@code 0.0} when no overlapping solid rises
     * above the feet. Read-only geometry query — does not affect the existing collision math.
     */
    private static double lipHeightAboveFeet(Vec3 pos, Vec3 bearingWorldPos, List<Slot> solids,
            double halfWidth, double yaw) {
        Vec3 local = dev.arubik.craftengine.contraption.ContraptionMath.realToLocal(pos, bearingWorldPos, yaw);
        double rMinX = local.x - halfWidth, rMaxX = local.x + halfWidth;
        double rMinZ = local.z - halfWidth, rMaxZ = local.z + halfWidth;
        double feetY = local.y;
        double bestTop = feetY;
        for (Slot slot : solids) {
            if (!slot.hasCollision) {
                continue;
            }
            double half = slot.width / 2.0;
            double minX = slot.lx - half, maxX = slot.lx + half;
            double minZ = slot.lz - half, maxZ = slot.lz + half;
            if (rMaxX > minX && rMinX < maxX && rMaxZ > minZ && rMinZ < maxZ) {
                double top = slot.ly + slot.standTopY;
                if (top > bestTop) {
                    bestTop = top;
                }
            }
        }
        return bestTop - feetY;
    }

    /**
     * High-speed swept pushback (2026-07-03): detects a solid wall that passed ENTIRELY through
     * {@code pos} this tick (neither the current nor the post-move point overlaps, so
     * {@link #computeSolidPush}'s point tests miss it) and returns a push that lands the entity just
     * ahead of that wall's leading face along the platform's own travel. Works in the local slot
     * frame (un-rotate {@code pos} and the platform delta by {@code yaw}), then rotates the push
     * back to world. Horizontal-only (a purely vertical platform move never "runs over" sideways).
     */
    private static Vec3 resolveSweptPushOut(Vec3 pos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth,
            double height, double yaw, double dx, double dy, double dz) {
        if (dx == 0.0 && dz == 0.0) {
            return Vec3.ZERO;
        }
        Vec3 local = dev.arubik.craftengine.contraption.ContraptionMath.realToLocal(pos, bearingWorldPos, yaw);
        // Platform delta expressed in the local slot frame (inverse of renderPosition's +yaw rotation).
        Vec3 ld = dev.arubik.craftengine.contraption.ContraptionMath.rotateYaw(new Vec3(dx, dy, dz), -yaw);
        double rMinX = local.x - halfWidth, rMaxX = local.x + halfWidth;
        double rMinZ = local.z - halfWidth, rMaxZ = local.z + halfWidth;
        double rMinY = local.y + FLOOR_CLEARANCE, rMaxY = local.y + height;
        double bestPushX = 0.0, bestPushZ = 0.0;
        boolean any = false;
        for (Slot slot : solids) {
            if (!slot.hasCollision) {
                continue;
            }
            double half = slot.width / 2.0;
            double minX = slot.lx - half, maxX = slot.lx + half;
            double minZ = slot.lz - half, maxZ = slot.lz + half;
            double minY = slot.ly + slot.standBottomY, maxY = slot.ly + slot.standTopY;
            // Swept box: extend each solid backward along -ld to cover where the wall came FROM.
            double sMinX = minX - Math.max(0.0, ld.x), sMaxX = maxX - Math.min(0.0, ld.x);
            double sMinZ = minZ - Math.max(0.0, ld.z), sMaxZ = maxZ - Math.min(0.0, ld.z);
            double sMinY = minY - Math.max(0.0, ld.y), sMaxY = maxY - Math.min(0.0, ld.y);
            boolean swept = rMaxX > sMinX && rMinX < sMaxX && rMaxZ > sMinZ && rMinZ < sMaxZ
                    && rMaxY > sMinY && rMinY < sMaxY;
            if (!swept) {
                continue;
            }
            any = true;
            // Shove to just ahead of the wall's LEADING face along the dominant motion axis.
            if (Math.abs(ld.x) >= Math.abs(ld.z) && ld.x != 0.0) {
                double lead = ld.x > 0.0 ? (maxX + halfWidth) - local.x : (minX - halfWidth) - local.x;
                if (Math.abs(lead) > Math.abs(bestPushX)) {
                    bestPushX = lead;
                }
            } else if (ld.z != 0.0) {
                double lead = ld.z > 0.0 ? (maxZ + halfWidth) - local.z : (minZ - halfWidth) - local.z;
                if (Math.abs(lead) > Math.abs(bestPushZ)) {
                    bestPushZ = lead;
                }
            }
        }
        if (!any) {
            return Vec3.ZERO;
        }
        return dev.arubik.craftengine.contraption.ContraptionMath.rotateYaw(new Vec3(bestPushX, 0.0, bestPushZ), yaw);
    }

    /**
     * Shortest-axis separating push to get {@code playerPos}'s hitbox (player-sized, standing at
     * its CURRENT — not yet moved — position) fully clear of every overlapping solid slot.
     * Computes, per overlapping solid, the minimal X/Z penetration depth and returns the smallest
     * magnitude correction of the two axes (never touches Y — vertical resolution is
     * {@link #carryRiders}'s job when the player is actually standing on top; a bystander merely
     * caught in a wall's path is only ever pushed sideways, matching vanilla piston pushback's own
     * horizontal-only shove for a wall closing in).
     *
     * <p>2026-07-02 session, torch-pushback follow-up ("la antorcha esta siendo tomada en cuenta
     * en el AABB como full block empujando al jugador fuera de si") — same fix as
     * {@link #overlapsAnySolid}: uses the real shape-derived {@code [standBottomY, standTopY)}
     * window instead of the flat {@code [0, height)} one, and skips any {@link Slot#hasCollision}
     * {@code false} cell (torches, tripwire, etc.) entirely — those never contribute a penetration
     * depth, so a captured torch can no longer shove a bystander at all.
     */
    private static Vec3 resolvePushOut(Vec3 playerPos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth, double height, double yaw) {
        // Yaw-aware (2026-07-03): resolve the separating push in the local slot frame (un-rotate the
        // player first), then rotate the resulting push vector back into world space before it's
        // handed to PlayerCarry — matches overlapsAnySolid's own local-frame test above.
        Vec3 local = dev.arubik.craftengine.contraption.ContraptionMath.realToLocal(playerPos, bearingWorldPos, yaw);
        double rMinX = local.x - halfWidth, rMaxX = local.x + halfWidth;
        double rMinZ = local.z - halfWidth, rMaxZ = local.z + halfWidth;
        double rMinY = local.y, rMaxY = local.y + height;
        double bestPushX = 0.0, bestPushZ = 0.0;
        boolean any = false;
        for (Slot slot : solids) {
            if (!slot.hasCollision) {
                continue; // no real collision at all — e.g. a captured torch — never pushes anyone out
            }
            double half = slot.width / 2.0;
            double minX = slot.lx - half;
            double maxX = slot.lx + half;
            double minZ = slot.lz - half;
            double maxZ = slot.lz + half;
            double minY = slot.ly + slot.standBottomY;
            double maxY = slot.ly + slot.standTopY;
            boolean overlaps = rMaxX > minX && rMinX < maxX
                    && rMaxZ > minZ && rMinZ < maxZ
                    && rMaxY > minY && rMinY < maxY;
            if (!overlaps) {
                continue;
            }
            any = true;
            double penX = Math.min(rMaxX - minX, maxX - rMinX);
            double penZ = Math.min(rMaxZ - minZ, maxZ - rMinZ);
            double signX = (local.x >= (minX + maxX) / 2.0) ? 1.0 : -1.0;
            double signZ = (local.z >= (minZ + maxZ) / 2.0) ? 1.0 : -1.0;
            // Prefer whichever axis has the smaller penetration for THIS slot (least-displacement
            // resolution), then keep the largest push seen across all overlapping slots so a
            // player straddling multiple cells is fully cleared of all of them.
            if (penX <= penZ) {
                if (Math.abs(penX * signX) > Math.abs(bestPushX)) {
                    bestPushX = penX * signX;
                }
            } else {
                if (Math.abs(penZ * signZ) > Math.abs(bestPushZ)) {
                    bestPushZ = penZ * signZ;
                }
            }
        }
        if (!any) {
            return Vec3.ZERO;
        }
        // push computed in local frame → rotate back to world (yaw about Y; Y push is always 0).
        return dev.arubik.craftengine.contraption.ContraptionMath.rotateYaw(new Vec3(bestPushX, 0.0, bestPushZ), yaw);
    }

    public int cellCount() {
        return autoSlots.size() + customSlots.size();
    }

    /**
     * One invisible fake {@code INTERACTION} entity — a continuous bearing-local offset
     * ({@code lx,ly,lz}) plus its own {@code width}/{@code height}, bottom-anchored exactly
     * like vanilla's own {@code Interaction} collider (box: X/Z ±width/2 around the offset,
     * Y from the offset upward by height). No invisible-flag/attach-face hackery needed —
     * unlike the SHULKER this replaces, Interaction has no client-rendered model at all.
     */
    private static final class Slot {
        final double lx, ly, lz;
        final float width, height;
        /**
         * Real shape-derived local Y bounds of this cell's actual collision surface (2026-07-02
         * session, carry-fix follow-up — see the long comment at this slot's construction site in
         * {@link #rebuild} for the full root-cause writeup). Defaults to {@code [0, height]} — the
         * flat full-cell assumption — for any {@link Slot} built via the legacy 5-arg constructor
         * (custom/shulker-adjacent slots that don't go through {@code computeCell}), so
         * behavior for those is completely unchanged. Mutable (not {@code final}) so {@link #rebuild}
         * can refresh it on a REUSED slot when the underlying block changes shape in place without
         * the slot itself being despawned/recreated.
         */
        double standBottomY, standTopY;
        /**
         * Whether this cell's REAL captured block has any collision at all (2026-07-02 session,
         * torch-pushback follow-up — "la antorcha esta siendo tomada en cuenta en el AABB como
         * full block empujando al jugador fuera de si"). {@code true} by default for any
         * {@link Slot} built via the legacy 5-arg constructor (custom/shulker-adjacent slots that
         * don't go through {@code computeCell} — unchanged "always solid" behavior for
         * those), and set from {@code computeCell(level, offset) != null} for auto-derived
         * cells in {@link #rebuild} — mirrors EXACTLY the same real-shape check the passive
         * shulker-collider layer already uses to skip a cell entirely (torches, tripwire, most
         * plants, etc. — see {@code computeCell}'s {@code shape.isEmpty()} javadoc). Before
         * this field existed, {@link #overlapsAnySolid}/{@link #resolvePushOut} (the side-collision
         * clamp and bystander-pushback machinery) iterated {@link #allSlots} unconditionally
         * treating EVERY cell as a solid flat {@code width}x{@code height} box regardless of the
         * real block's actual collision shape — so a captured torch, which should have ZERO real
         * collision, still shoved players away like a full block. Mutable (not {@code final}) for
         * the same in-place-block-swap reason {@link #standBottomY}/{@link #standTopY} are.
         */
        boolean hasCollision = true;
        private final int entityId = nextEntityId();
        private final UUID uuid = UUID.randomUUID();
        private final Object despawnPacket;
        private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();

        /**
         * Live uniform contraption SCALE (roadmap item #9) this slot's box is currently sized by — its
         * {@code INTERACTION} width/height are sent as {@code width*renderScale}/{@code height*renderScale}
         * (see {@link #metadata}). {@code 1.0} means "un-scaled, exactly as before this field existed"
         * ({@code width*1.0}/{@code height*1.0} are the float-exact identities, so the scale-1 spawn packet is
         * byte-for-byte unchanged). A change flags {@link #scaleDirty} for a metadata resend to existing viewers.
         */
        private double renderScale = 1.0;
        /** Set when {@link #renderScale} changes; cleared once {@link #render} resends the sized metadata to every current viewer. */
        private volatile boolean scaleDirty = false;

        Slot(double lx, double ly, double lz, float width, float height) {
            this(lx, ly, lz, width, height, 0.0, height);
        }

        Slot(double lx, double ly, double lz, float width, float height, double standBottomY, double standTopY) {
            this.lx = lx;
            this.ly = ly;
            this.lz = lz;
            this.width = width;
            this.height = height;
            this.standBottomY = standBottomY;
            this.standTopY = standTopY;
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
        }

        private List<Object> metadata() {
            List<Object> values = new ArrayList<>();
            // Box sized by the live contraption scale (roadmap item #9); at renderScale == 1.0 these are the
            // float-exact width/height, so the scale-1 packet is byte-for-byte the pre-scale one.
            InteractionData.Width.addEntityData((float) (width * renderScale), values);
            InteractionData.Height.addEntityData((float) (height * renderScale), values);
            InteractionData.Response.addEntityData(false, values); // no "hit" feedback needed — carry-only
            return values;
        }

        void spawn(Player player, double x, double y, double z) {
            Object addPacket = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                    entityId, uuid, x, y, z, 0f, 0f, EntityType.INTERACTION, 0, Vec3.ZERO, 0);
            Object dataPacket = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata());
            player.sendPackets(List.of(addPacket, dataPacket), false);
        }

        void updatePosition(Player player, double x, double y, double z) {
            player.sendPacket(MNms.INSTANCE
                    .constructor$ClientboundEntityPositionSyncPacket(entityId, x, y, z, 0f, 0f, false), false);
        }

        void despawn(Player player) {
            player.sendPacket(despawnPacket, false);
        }

        void render(List<Player> viewers, double x, double y, double z, double scale, boolean moved) {
            if (scale != renderScale) {
                // Contraption resized (e.g. the phys wand): resize this box and resend its sized metadata to
                // everyone already tracking it (newly-spawned viewers below already get the fresh size).
                renderScale = scale;
                scaleDirty = true;
            }
            boolean resendScale = scaleDirty;
            Set<UUID> current = new HashSet<>();
            for (Player p : viewers) {
                UUID id = uuidOf(p);
                if (id == null) {
                    continue;
                }
                current.add(id);
                if (shownTo.add(id)) {
                    spawn(p, x, y, z);
                } else {
                    if (moved) {
                        updatePosition(p, x, y, z);
                    }
                    if (resendScale) {
                        p.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata()), false);
                    }
                }
            }
            if (resendScale) {
                scaleDirty = false;
            }
            shownTo.retainAll(current);
        }

        private static UUID uuidOf(Player player) {
            Object pp = player.platformPlayer();
            return pp instanceof org.bukkit.entity.Player b ? b.getUniqueId() : null;
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
