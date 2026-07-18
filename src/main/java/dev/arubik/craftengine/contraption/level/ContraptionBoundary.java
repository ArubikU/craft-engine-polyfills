package dev.arubik.craftengine.contraption.level;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.joml.Quaternionf;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;

/**
 * The contraption/real-world boundary as a <em>capability</em>, deliberately decoupled from the
 * concrete {@link ContraptionLevel} class. This is roadmap item #5 ("clean contraption/real-world
 * boundary accessor that removes scattered {@code instanceof ContraptionLevel} usage") — see
 * {@code .migration/ROADMAP-world-boundary.md} §3 ("Resolve an ENTITY across the two worlds without
 * {@code instanceof}"), whose design this implements.
 *
 * <p><b>Why an interface at all.</b> A captured block or block-entity ticks inside a hidden
 * {@link ContraptionLevel} (a genuine {@code ServerLevel} subclass — see that class's javadoc) but,
 * to render/emit/push into the real world, has to reach the bearing's live transform and the
 * original {@code realLevel}. Before this, every such caller did its own
 * {@code level instanceof ContraptionLevel cl} test and then poked at the concrete class. That check
 * appeared in ContraptionLevel's own bridge helpers, in the conveyor/funnel item renderers, and in
 * the fluid-tank renderer — a growing scatter of hard references to one concrete server-level
 * subclass. This interface is the fix: it exposes exactly the real-world-bridging surface those
 * callers need, {@link ContraptionLevel} {@code implements} it (every method already existed there),
 * and the {@code instanceof} collapses to a <b>single authorized site</b>: {@link #of(Level)}.
 * No downstream code ever names the concrete class again — it asks {@code ContraptionBoundary.of(level)}
 * and talks to the capability.
 *
 * <p><b>The one instanceof lives here and only here.</b> {@link #of(Level)} is the sole place in the
 * codebase permitted to test {@code level instanceof ContraptionLevel}; everything else — the fan
 * push/output bridge, the belt/funnel/fluid render redirects, the {@link ContraptionWorlds} entity
 * resolver — goes through it. Adding a second contraption-level implementation later (or moving the
 * check) is then a one-line change here rather than a codebase sweep.
 *
 * @see ContraptionWorlds for the companion {@code ContraptionLevel -> ContraptionEntity} back-index
 *      and cross-world entity resolution (design §3), which builds on this boundary.
 */
public interface ContraptionBoundary {

    /**
     * The original world this contraption was captured from / currently projects into — the real
     * {@code Level} every real-world bridge (viewers, particles, sound, entity union) resolves
     * against. Never the hidden contraption level itself.
     */
    Level realLevel();

    /**
     * Where a continuous LOCAL-space position inside the contraption would be right now if the
     * structure were still really standing in {@link #realLevel()}, under the bearing's live
     * transform (rotate about the bearing centre + translate). The single conversion every
     * local-offset-to-real-world caller should funnel through.
     */
    Vec3 realWorldPositionOf(Vec3 local);

    /**
     * Whole-block overload of {@link #realWorldPositionOf(Vec3)} — resolves the block's MIN corner
     * (matching {@code ContraptionMath.renderPosition}'s convention) under the bearing's live transform.
     */
    Vec3 realWorldPositionOf(BlockPos local);

    /**
     * Rotates (does NOT translate) a LOCAL-space direction/velocity vector into the bearing's current
     * real-world orientation — for landing a local-space effect (a fan push, an item heading) on a
     * real-world target that lives at the bearing's yaw-rotated transform.
     */
    Vec3 rotateToRealWorld(Vec3 localDirection);

    /**
     * Composes a LOCAL-space orientation (e.g. a conveyor item's own facing/slope rotation) with the
     * bearing's current real-world yaw, so a rotating contraption visibly rotates everything riding it.
     * {@code local} may be {@code null} for "bearing yaw only".
     */
    Quaternionf realOrientationOf(Quaternionf local);

    /** The bearing's current real-world yaw, in radians. */
    double realYawRadians();

    /**
     * The bearing's live uniform SCALE factor (roadmap item #9 — per-contraption {@code scale}, clamped
     * {@code [0.1, 10]} by {@code ContraptionState#setScale}). {@code 1.0} for a never-scaled body.
     *
     * <p><b>Why a packet-only renderer needs this even though {@link #realWorldPositionOf} already scales.</b>
     * That projection scales a cell's offset-from-pivot, i.e. WHERE a visual lands — it says nothing about
     * HOW BIG the visual itself is drawn. A renderer that owns a {@code Display}'s own {@code Scale}
     * transform (as {@code FluidTankRender} does for the tank's liquid slabs) must multiply that transform
     * by this factor too, or its visual keeps its 1x size at a scaled position: gaps inside the tank at
     * {@code scale > 1}, liquid protruding through the tank walls at {@code scale < 1}. Read live, every
     * tick, never cached — the Creative Phys Wand resizes a contraption while it is being rendered.
     */
    double realScaleFactor();

    /**
     * Real players who should see something rendered at LOCAL position {@code local} — the
     * contraption level's own chunk-tracking is always empty (no real player is ever inside the hidden
     * dimension), so this resolves against {@link #realLevel()}'s tracking at
     * {@link #realWorldPositionOf(Vec3)}. The redirect every packet-only renderer living inside a
     * contraption should use instead of tracking against the contraption level directly.
     */
    List<Player> realViewers(BlockPos local);

    /** {@link #realViewers(BlockPos)} for an already-translated continuous real-world position. */
    List<Player> realViewers(Vec3 realPos);

    /**
     * Whether {@code entity} is a REAL-world entity (it lives in {@link #realLevel()}, not physically
     * inside the hidden contraption dimension). The apply-time discriminator its bridge helpers use to
     * decide whether a local-space effect must be transformed first (real target) or applied raw
     * (fake, co-captured target). Null-safe: a {@code null} entity is not a real-world entity.
     */
    boolean isRealWorldEntity(Entity entity);

    /**
     * FAKE-LEVEL-ONLY entity query — only entities physically inside this contraption's hidden
     * dimension, never unioned with the real world (contrast the dual-world {@code getEntities}
     * override on {@link ContraptionLevel}). The escape hatch a captured actor uses when it must see
     * ONLY co-captured contents (e.g. a belt vacuuming up items dropped INSIDE the contraption, which
     * must never reach into the real world). See {@link ContraptionLevel#getLocalEntities}.
     */
    <T extends Entity> List<T> getLocalEntities(Class<T> clazz, AABB box, Predicate<? super T> predicate);

    /**
     * The <b>single authorized {@code instanceof ContraptionLevel} site in the codebase.</b> Resolves
     * the contraption boundary for {@code level}, or {@link Optional#empty()} if {@code level} is a
     * plain real world (or {@code null}). Every caller that used to write its own
     * {@code level instanceof ContraptionLevel} check now calls this instead and talks to the
     * capability — keeping the concrete-class dependency confined to this one method.
     */
    static Optional<ContraptionBoundary> of(Level level) {
        return level instanceof ContraptionBoundary boundary ? Optional.of(boundary) : Optional.empty();
    }
}
