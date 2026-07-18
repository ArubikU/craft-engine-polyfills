package dev.arubik.craftengine.contraption;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import dev.arubik.craftengine.contraption.level.ContraptionBoundary;
import dev.arubik.craftengine.contraption.level.ContraptionLevel;

/**
 * Central resolver for "which contraption owns this level" and "find an entity that may live in
 * either the real world or a contraption level" — roadmap item #5, see
 * {@code .migration/ROADMAP-world-boundary.md} §3 ("Resolve an ENTITY across the two worlds without
 * {@code instanceof}"). Companion to {@link ContraptionBoundary}: the boundary is the per-level
 * capability (and the single authorized {@code instanceof} site); this class adds the
 * <b>{@code ContraptionLevel} &rarr; {@link ContraptionEntity} back-index</b> the boundary alone
 * cannot provide, plus the cross-world entity/query helpers built on top of it.
 *
 * <p><b>The back-index.</b> {@link ContraptionManager} already maps {@code UUID -> ContraptionEntity}
 * (the forward direction: given a contraption id, get its facade). Several subsystems need the
 * <em>reverse</em>: given a {@link ContraptionLevel} a captured block is ticking inside, recover the
 * owning {@link ContraptionEntity} (its render swarms, kinematics {@link ContraptionState}, seats,
 * item mirrors). This class holds that reverse map, populated the instant a facade is registered and
 * cleared the instant it is removed — both driven from {@link ContraptionManager#register}/
 * {@link ContraptionManager#remove} so the two indexes can never drift. Keyed by the
 * {@link ContraptionBoundary} the level exposes (a {@link ContraptionLevel} <em>is</em> one), so the
 * key type never names the concrete class and no extra {@code instanceof} is introduced here.
 *
 * <p><b>Foundation for later roadmap items.</b> Item #1 (cross-world teleport) re-anchors a
 * contraption between worlds and needs exactly this reverse resolution to migrate the facade's
 * real-world satellites; item #6 (hopper I/O bridge) needs {@link #owning(Level)} to reach the facade
 * from a captured hopper's tick. The API here is deliberately the clean seam those build on.
 *
 * <p><b>Threading.</b> Contraption registration and the engine's {@code tickAll} both run on the
 * server main thread, so the back-index is only ever touched there. A {@link WeakHashMap} is used
 * anyway as a safety net: should a {@link ContraptionLevel} ever be disposed without a matching
 * {@link ContraptionManager#remove} (it should not — the two are wired together), its entry is not
 * pinned in memory by this index.
 */
public final class ContraptionWorlds {

    private ContraptionWorlds() {
    }

    /**
     * Reverse {@code ContraptionLevel -> ContraptionEntity} map — see the class javadoc. Keyed by the
     * {@link ContraptionBoundary} capability the level exposes (identity of the level instance, since
     * neither {@link ContraptionLevel} nor {@code ServerLevel} overrides {@code equals}). Populated by
     * {@link #index}/{@link #unindex}, driven from {@link ContraptionManager}.
     */
    private static final Map<ContraptionBoundary, ContraptionEntity> BACK_INDEX = new WeakHashMap<>();

    // ---- back-index maintenance (called only from ContraptionManager.register/remove) ----

    /**
     * Records the {@code level -> entity} reverse mapping for a freshly-registered facade. No-op when
     * the facade has no level ({@link ContraptionState#level()} is {@code null} — the null-tolerant
     * pure-kinematics/registry unit-test path, see {@link ContraptionState}'s javadoc), so tests that
     * never build a real {@link ContraptionLevel} are unaffected. Idempotent — re-registering the same
     * level simply overwrites its entry.
     */
    static void index(ContraptionEntity entity) {
        if (entity == null) {
            return;
        }
        ContraptionLevel level = entity.state().level();
        if (level != null) {
            BACK_INDEX.put(level, entity);
        }
    }

    /**
     * Drops the reverse mapping for a facade being removed. Only removes the entry if it still points
     * at {@code entity} (defensive against a stale double-remove after the level was re-registered to a
     * different facade). No-op for a null/level-less facade.
     */
    static void unindex(ContraptionEntity entity) {
        if (entity == null) {
            return;
        }
        ContraptionLevel level = entity.state().level();
        if (level != null) {
            BACK_INDEX.remove(level, entity);
        }
    }

    // ---- reverse resolution: level -> owning facade ----

    /** The {@link ContraptionEntity} that owns {@code level}, or empty if none is registered for it. */
    public static Optional<ContraptionEntity> owning(ContraptionLevel level) {
        return level == null ? Optional.empty() : Optional.ofNullable(BACK_INDEX.get(level));
    }

    /**
     * The {@link ContraptionEntity} owning {@code level} if it is a contraption level, else empty —
     * resolves the boundary via {@link ContraptionBoundary#of} (the single authorized {@code instanceof})
     * and then the back-index, so a caller holding only a plain {@link Level} reference (real world OR
     * contraption) can recover the facade without ever testing the concrete class itself.
     */
    public static Optional<ContraptionEntity> owning(Level level) {
        return ContraptionBoundary.of(level).flatMap(b -> Optional.ofNullable(BACK_INDEX.get(b)));
    }

    /** Alias for {@link #owning(ContraptionLevel)} — reads naturally as "the entity of this level". */
    public static Optional<ContraptionEntity> entityOf(ContraptionLevel level) {
        return owning(level);
    }

    /** Alias for {@link #owning(Level)} — reads naturally as "the entity of this level". */
    public static Optional<ContraptionEntity> entityOf(Level level) {
        return owning(level);
    }

    // ---- cross-world entity resolution / query ----

    /**
     * Resolves the entity with id {@code id} that may live in EITHER {@code level} itself OR — when
     * {@code level} is a contraption — its {@link ContraptionBoundary#realLevel() real world}. Tries
     * {@code level}'s own lookup first, then falls back to the contraption's real level, returning
     * {@code null} if neither has it. This is the entity-side seam design §3 calls out and that item #1
     * (cross-world teleport) and item #6 (hopper I/O) build on.
     */
    public static Entity resolveEntity(Level level, UUID id) {
        if (level instanceof ServerLevel serverLevel) {
            Entity direct = serverLevel.getEntity(id);
            if (direct != null) {
                return direct;
            }
        }
        Level real = ContraptionBoundary.of(level).map(ContraptionBoundary::realLevel).orElse(null);
        if (real instanceof ServerLevel realServerLevel) {
            return realServerLevel.getEntity(id);
        }
        return null;
    }

    /**
     * <b>The dual-world entity query a captured effect block should call</b> instead of
     * {@code level.getEntitiesOfClass(...)}. Routes through the {@code getEntities(EntityTypeTest, AABB,
     * Predicate)} seam that {@link ContraptionLevel} overrides to add the real-world union — so the
     * caller actually receives real-world entities when it runs inside a contraption, and byte-for-byte
     * vanilla behavior when it runs in a plain {@link ServerLevel}.
     *
     * <p>Moved here from {@code ContraptionLevel} (design §3 "move the existing static here, keep a
     * delegating shim") so the union entry point lives beside the rest of the cross-world resolution.
     * {@link ContraptionLevel#unionEntities} remains as a thin delegating shim for existing callers.
     * See {@link ContraptionLevel#getEntities} for the full Moonrise-bypass rationale for why this
     * indirection (rather than {@code getEntitiesOfClass}) is required to hit the override.
     */
    public static <T extends Entity> List<T> unionEntities(Level level, Class<T> clazz, AABB box,
            Predicate<? super T> predicate) {
        return level.getEntities(EntityTypeTest.forClass(clazz), box, predicate);
    }

    // ---- cross-world BLOCK resolution (roadmap item #6 — hopper/inventory I/O bridge) ----
    //
    // The entity seam above answers "which real-world entities is this captured block flying
    // through". The block seam below answers the harder companion question the hopper I/O bridge
    // needs: "which REAL-world block cell does a captured block's local neighbor map onto right now"
    // — the reusable primitive design §4 ("Block-side accessor") calls for. See
    // ContraptionHopperBridge for the one current consumer.

    /**
     * Half-block position tolerance (in blocks) for {@link #isGridAligned}: the bearing's continuous
     * X/Y/Z must each sit within this of an integer for the contraption to count as "docked" onto the
     * real grid. Small enough that a moving contraption is only ever considered aligned at a genuine
     * integer stop, never mid-slide.
     */
    private static final double GRID_POS_EPS = 0.05;
    /**
     * Yaw tolerance (radians, ~1.1°) for {@link #isGridAligned}: the live yaw must sit within this of
     * one of the four cardinal rotations. A rotating contraption is therefore only aligned at a clean
     * 0/90/180/270° stop — the only orientations at which a rotated local neighbor maps to a
     * well-defined real block cell rather than a fractional, rotated point.
     */
    private static final double GRID_YAW_EPS = 0.02;

    /**
     * Whether {@code level}'s contraption is currently <b>grid-aligned / docked</b> onto the real
     * world — its bearing position within {@link #GRID_POS_EPS} of an integer block on every axis AND
     * its yaw within {@link #GRID_YAW_EPS} of a cardinal rotation (Create's own "the contraption I/Os
     * only while docked" rule). This is the precondition for {@link #realBlockNeighbor} /
     * {@link #realDirectionOf} returning a meaningful answer: OFF-grid (mid-translation) or off-axis
     * (mid-rotation), a captured block's real-world neighbor is a fractional, rotated point with no
     * single real {@code BlockPos}, so those methods return empty and any block bridge simply does
     * nothing that tick. Reads the live transform from the owning {@link ContraptionEntity}'s
     * {@link ContraptionState} via the back-index; returns {@code false} if {@code level} has no
     * registered facade (nothing to resolve a transform from).
     */
    public static boolean isGridAligned(ContraptionLevel level) {
        ContraptionState state = owning(level).map(ContraptionEntity::state).orElse(null);
        if (state == null) {
            return false;
        }
        if (!nearInteger(state.x()) || !nearInteger(state.y()) || !nearInteger(state.z())) {
            return false;
        }
        double yaw = state.yawRadians();
        double snapped = ContraptionMath.snapYawToCardinal(yaw);
        return Math.abs(wrapRadians(yaw - snapped)) <= GRID_YAW_EPS;
    }

    /**
     * The REAL-world {@link BlockPos} that the cell one step in LOCAL direction {@code localFace} from
     * captured cell {@code localPos} maps onto — but ONLY when the contraption is
     * {@linkplain #isGridAligned docked} (see that method). Returns {@link Optional#empty()} off-grid /
     * off-axis (the real neighbor is undefined then) or when the contraption has no live real
     * {@link ServerLevel} to project into.
     *
     * <p>When aligned, the captured cell's own real position is the grid-snapped
     * {@link ContraptionBoundary#realWorldPositionOf(BlockPos) real world position} of {@code localPos}
     * (an exact integer block, since position and yaw are both snapped), and {@code localFace} is
     * rotated by the cardinal-snapped bearing yaw into a real-world unit step (Y is yaw-invariant, so
     * an UP/DOWN face is unchanged) — the neighbor is that step off the cell. This is the block-access
     * seam design §4 specifies; {@link ContraptionHopperBridge} is its first consumer, but any future
     * captured-block ↔ real-world block feature reuses it.
     */
    public static Optional<BlockPos> realBlockNeighbor(ContraptionLevel level, BlockPos localPos,
            Direction localFace) {
        if (level == null || localPos == null || localFace == null || !isGridAligned(level)) {
            return Optional.empty();
        }
        if (!(level.realLevel() instanceof ServerLevel)) {
            return Optional.empty();
        }
        BlockPos realCell = ContraptionMath.gridSnap(level.realWorldPositionOf(localPos));
        int[] step = rotatedStep(level, localFace);
        return Optional.of(realCell.offset(step[0], step[1], step[2]));
    }

    /**
     * The REAL-world {@link Direction} a LOCAL {@code localFace} points along right now — the cardinal
     * bearing yaw applied to the local face — or {@link Optional#empty()} when the contraption isn't
     * {@linkplain #isGridAligned docked}. The companion to {@link #realBlockNeighbor}: the neighbor
     * method gives the destination cell, this gives the face/heading needed for a worldly-container
     * face-aware insert/extract at that cell (see {@link ContraptionHopperBridge}).
     */
    public static Optional<Direction> realDirectionOf(ContraptionLevel level, Direction localFace) {
        if (level == null || localFace == null || !isGridAligned(level)) {
            return Optional.empty();
        }
        int[] step = rotatedStep(level, localFace);
        for (Direction d : Direction.values()) {
            if (d.getStepX() == step[0] && d.getStepY() == step[1] && d.getStepZ() == step[2]) {
                return Optional.of(d);
            }
        }
        return Optional.empty();
    }

    /**
     * Rotates {@code localFace}'s unit step by the contraption's cardinal-snapped bearing yaw and
     * rounds to the nearest integer step {@code {dx,dy,dz}}. Caller must have already confirmed
     * {@link #isGridAligned}, so the snapped yaw is a clean quarter turn and the rounded components are
     * exact (each ∈ {-1,0,1}). Shared by {@link #realBlockNeighbor} and {@link #realDirectionOf} so the
     * two never disagree about where a face lands.
     */
    private static int[] rotatedStep(ContraptionLevel level, Direction localFace) {
        double yaw = ContraptionMath.snapYawToCardinal(level.realYawRadians());
        Vec3 rotated = ContraptionMath.rotateYaw(
                new Vec3(localFace.getStepX(), localFace.getStepY(), localFace.getStepZ()), yaw);
        return new int[] { (int) Math.round(rotated.x), (int) Math.round(rotated.y),
                (int) Math.round(rotated.z) };
    }

    /** Whether {@code v} is within {@link #GRID_POS_EPS} of an integer. */
    private static boolean nearInteger(double v) {
        return Math.abs(v - Math.rint(v)) <= GRID_POS_EPS;
    }

    /** Wraps an angle (radians) into {@code [-PI, PI]} so a near-cardinal yaw diff reads as small regardless of winding. */
    private static double wrapRadians(double radians) {
        double twoPi = Math.PI * 2;
        double wrapped = radians % twoPi;
        if (wrapped < -Math.PI) {
            wrapped += twoPi;
        } else if (wrapped > Math.PI) {
            wrapped -= twoPi;
        }
        return wrapped;
    }
}
