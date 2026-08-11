package dev.arubik.craftengine.contraption.bearing;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;

import org.bukkit.DyeColor;
import org.bukkit.Material;

import net.minecraft.world.phys.AABB;

/**
 * The pure rules behind the happy-ghast harness contraption (2026-07-16 goal, as redesigned — "just when
 * right click a ghast with a harness equiped and with a hammer, if the ghast is between some glued block
 * ... it should make a entire contraption"): which items count as a harness, which world cells a ghast
 * can pick a glued structure up from, and where the contraption's bearing origin must sit so the structure
 * pivots about the ghast. Kept free of any server/NMS state so all three are provable in a plain JVM test —
 * see {@code GhastHarnessTest}.
 */
public final class GhastHarness {

    private GhastHarness() {
    }

    /**
     * The 16 harness items. Built from {@link DyeColor#values()} rather than hand-listed so a colour can
     * never be silently missed, and resolved through {@link Material#valueOf} because {@code Material}
     * exposes no colour accessor — every dye colour has a {@code <COLOR>_HARNESS} constant (verified
     * against the 1.21.11 paper-api jar), so the lookup is total.
     */
    private static final Set<Material> HARNESSES;

    static {
        Set<Material> set = EnumSet.noneOf(Material.class);
        for (DyeColor color : DyeColor.values()) {
            set.add(Material.valueOf(color.name() + "_HARNESS"));
        }
        HARNESSES = Collections.unmodifiableSet(set);
    }

    /** Whether {@code material} is one of the 16 harness items. */
    public static boolean isHarness(Material material) {
        return material != null && HARNESSES.contains(material);
    }

    /** Every harness material, for tests/tools that need to enumerate them. */
    public static Set<Material> harnesses() {
        return HARNESSES;
    }

    /**
     * Every block cell {@code box} overlaps — the cells a harnessed ghast "occupies", which
     * {@link GhastContraptionType#gluedStructureAround} probes the glue graph at. An adult
     * {@code HappyGhast} is {@code EntityType.Builder.of(HappyGhast::new, ...).sized(4.0F, 4.0F)}
     * (verified against the mapped jar), so its box spans four blocks per axis — but the box is read
     * from the live entity rather than reconstructed here, since a scaled or baby ghast has a smaller
     * one and {@code getBoundingBox()} is the only number that is always right.
     *
     * <p>A face-touching neighbour is NOT a cell of the box: a box ending exactly on an integer plane
     * (a ghast at whole-number coordinates, which is the common case) would otherwise claim the whole
     * slab beyond it. {@code maxX} is therefore an exclusive bound unless the span is degenerate. Pure;
     * returned in ascending order per axis so callers see a deterministic sweep.
     */
    public static java.util.List<net.minecraft.core.BlockPos> cellsOverlapping(AABB box) {
        java.util.List<net.minecraft.core.BlockPos> cells = new java.util.ArrayList<>();
        for (int x = floor(box.minX); x <= lastCell(box.minX, box.maxX); x++) {
            for (int y = floor(box.minY); y <= lastCell(box.minY, box.maxY); y++) {
                for (int z = floor(box.minZ); z <= lastCell(box.minZ, box.maxZ); z++) {
                    cells.add(new net.minecraft.core.BlockPos(x, y, z));
                }
            }
        }
        return cells;
    }

    /**
     * The contraption yaw that makes the structure face the way a ghast at {@code ghastYawDegrees} faces
     * (2026-07-16 — "checa bien el yaw tambien ya que al girar a la izquierda giraba a la derecha el
     * modelo").
     *
     * <h2>There is no negation, and that is the fix</h2>
     * This used to be {@code toRadians(-yaw)}, copied from {@link MinecartBearing}'s follow path on the
     * stated grounds that it is "the raw negated-degrees conversion every other entity-anchored path uses".
     * It is simply the wrong sign, and it inverted every turn. Both angles wind the SAME way — decompiled
     * and re-derived rather than assumed:
     * <ul>
     *   <li><b>Entity yaw.</b> {@code Entity#calculateViewVector} builds the look direction as
     *   {@code (-sin(yaw), cos(yaw))} on {@code (x, z)}. So yaw 0 faces {@code +Z} (south) and yaw 90 faces
     *   {@code -X} (west) — south to west, i.e. clockwise seen from above.</li>
     *   <li><b>Contraption yaw.</b> {@code ContraptionMath#rotateYaw} maps {@code (1,0)} to
     *   {@code (cos, sin)}, sending {@code +X} (east) toward {@code +Z} (south) as the angle grows — east to
     *   south, also clockwise.</li>
     * </ul>
     * Two clockwise angles differ by a constant, never a sign, so the conversion is the plain unit change.
     * {@code GhastFollowBehavior} absorbs the constant into its own yaw offset, which is exactly why the
     * sign was the only thing that could ever have been wrong here — and why a wrong one is invisible at
     * rest and only shows up as "it turns the other way" the moment the ghast moves.
     *
     * <p>The invariant worth remembering (and the one {@code GhastHarnessTest} pins): rotating local SOUTH
     * by this angle reproduces the ghast's own look direction exactly, since
     * {@code rotateYaw((0,0,1), yaw) == (-sin(yaw), cos(yaw))}. South, because that is where an entity at
     * yaw 0 looks and therefore what the structure's un-rotated local frame means.
     *
     * <p>No wrapping is applied: callers drive an absolute target through a shortest-arc step, which is
     * where wrap belongs. Wrapping here would only hide the discontinuity one layer lower.
     */
    public static double contraptionYaw(float ghastYawDegrees) {
        return Math.toRadians(ghastYawDegrees);
    }

    /**
     * The constant that turns a ghast's live heading into its contraption's target yaw:
     * {@code contraptionYaw - contraptionYaw(ghastYawDegrees)}, measured once when the anchor is acquired
     * and persisted with the assembly ever after (2026-07-16 — "a veces pasa que el contraption de un happy
     * ghast pierde su norte y al re abrir el sv se acomoda al norte del happy ghast").
     *
     * <h2>Why it must be recorded, never re-derived</h2>
     * The offset is only meaningful against the heading it was measured at — it is the whole record of how
     * the structure sat relative to the ghast at capture. Re-measuring it later against a heading the ghast
     * has since turned to yields {@code contraptionYaw(later) - contraptionYaw(later)} plus whatever the
     * contraption's yaw happens to be at that instant, which silently redefines the structure's rest pose as
     * "aligned with wherever the ghast is looking now". Concretely: a contraption is always REBUILT at yaw 0
     * (a capture stores yaw-0 local cells and both {@code GhastContraptionType#rehydrate} and its
     * restore-from-item path construct the state at yaw 0), so re-deriving after a restart gives
     * {@code 0 - contraptionYaw(now)} and a target of exactly 0 — the structure snaps to world north, no
     * matter which way it was actually facing when the server went down.
     */
    public static double captureYawOffset(double contraptionYawRadians, float ghastYawDegrees) {
        return contraptionYawRadians - contraptionYaw(ghastYawDegrees);
    }

    /**
     * The absolute contraption yaw a ghast at {@code ghastYawDegrees} demands, given the {@code yawOffset}
     * recorded by {@link #captureYawOffset} at capture — the exact inverse of that method, so the pair round
     * trips: capturing at any heading and immediately following at that same heading reproduces the
     * contraption's own yaw.
     *
     * <p>Unwrapped, deliberately: this is a TARGET, and the caller drives toward it along a shortest arc
     * ({@link #shortestAngleDelta}), which is where the {@code ±PI} discontinuity belongs. Wrapping here
     * would only move it one layer down.
     */
    public static double followTargetYaw(double yawOffset, float ghastYawDegrees) {
        return contraptionYaw(ghastYawDegrees) + yawOffset;
    }

    /**
     * Shortest signed angular difference from {@code from} to {@code to}, both radians, result in
     * {@code (-PI, PI]} — so an absolute target on the far side of the ±PI seam is reached by the short way
     * round rather than by unwinding a whole turn.
     */
    public static double shortestAngleDelta(double from, double to) {
        double delta = (to - from) % (Math.PI * 2);
        if (delta > Math.PI) {
            delta -= Math.PI * 2;
        } else if (delta < -Math.PI) {
            delta += Math.PI * 2;
        }
        return delta;
    }

    /**
     * The render pipeline's rotation pivot in LOCAL cell space, X/Z only. Must stay equal to
     * {@code ContraptionMath}'s own (private) {@code PIVOT_XZ} — every cell is projected as
     * {@code bearingOrigin + PIVOT + scale*R*(cell - PIVOT)}, so this is the one number that decides
     * which material point of the body {@code bearingOrigin + PIVOT} names. Y is deliberately absent:
     * the render pivot is the bearing's XZ centre at its BASE line, not its mid-height.
     */
    private static final double PIVOT_XZ = 0.5;

    /**
     * The bearing origin that makes the render pipeline rotate the whole structure about the GHAST'S
     * CENTRE (2026-07-16 — "ajusta el pivot point al centro del ghast para evitar problemas").
     *
     * <h2>Why a computed origin instead of a different pivot</h2>
     * The render pivot is fixed at local {@link #PIVOT_XZ} — it is baked into every swarm through
     * {@code ContraptionMath#renderPosition} and cannot be moved without touching the whole pipeline.
     * {@code ContraptionTransform} documents the way out: because the projection is affine in the cell,
     * choosing the bearing origin each tick is exactly equivalent to choosing an arbitrary pivot, with
     * no residual dependence on which cell you solve for — so the frames agree for EVERY cell at once.
     * Its closed form {@code bearingOrigin = comWorld - scale*R*(com - P) - P} is what this is, solved
     * for the ghast's centre rather than a centre of mass.
     *
     * <h2>The derivation</h2>
     * The body's material point at local {@code PIVOT} lands at {@code bearingOrigin + PIVOT} whatever
     * the rotation, so THAT is the point to place. Rigidly rotating the assembly about the ghast centre
     * {@code C} means it must sit at {@code C + R*(its capture-time offset from C)}, giving
     * {@code bearingOrigin = C + R*pivotOffset - PIVOT}. At capture the contraption's yaw is 0 and its
     * origin cell is {@code anchorCell = ghastPos + anchorOffset}, which pins
     * {@code pivotOffset = anchorOffset + PIVOT - (0, centreHeight, 0)}.
     *
     * <p><b>Zero regression at yaw 0.</b> Substituting {@code yawRadians == 0} collapses this to exactly
     * {@code ghastPos + anchorOffset} — the constant-offset anchoring this replaces. So {@code anchorOffset}
     * keeps its meaning verbatim (the capture-time ghast-to-origin-cell vector), and every harness already
     * packed with one stays correct without a migration.
     *
     * @param ghastPos     the ghast's live position, which for a {@code HappyGhast} is at its FEET
     * @param centreHeight the ghast's centre above {@code ghastPos} — pass {@code getBoundingBox().getYsize() / 2},
     *                     never a constant: it is {@code 2.0} only for an unscaled adult, and the live box is
     *                     the only number that is always right
     * @param anchorOffset the capture-time {@code anchorCell - ghastPos} vector persisted with the assembly
     * @param yawRadians   the contraption's current yaw
     */
    public static net.minecraft.world.phys.Vec3 bearingOrigin(net.minecraft.world.phys.Vec3 ghastPos,
            double centreHeight, net.minecraft.world.phys.Vec3 anchorOffset, double yawRadians) {
        net.minecraft.world.phys.Vec3 pivotOffset = new net.minecraft.world.phys.Vec3(
                anchorOffset.x + PIVOT_XZ,
                anchorOffset.y - centreHeight,
                anchorOffset.z + PIVOT_XZ);
        net.minecraft.world.phys.Vec3 rotated = ContraptionMath.rotateYaw(pivotOffset, yawRadians);
        return new net.minecraft.world.phys.Vec3(
                ghastPos.x + rotated.x - PIVOT_XZ,
                ghastPos.y + centreHeight + rotated.y,
                ghastPos.z + rotated.z - PIVOT_XZ);
    }

    /** The highest cell index a span {@code [min, max]} genuinely covers — see {@link #cellsOverlapping}. */
    private static int lastCell(double min, double max) {
        int last = floor(max);
        return last > floor(min) && max == last ? last - 1 : last;
    }

    private static int floor(double v) {
        int i = (int) v;
        return v < i ? i - 1 : i;
    }
}
