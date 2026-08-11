package dev.arubik.craftengine.contraption.render;

import org.joml.Vector3f;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.seat.SeatConfig;

/**
 * The single, verified reproduction of CraftEngine's own seat-placement rotation convention
 * ({@code net.momirealms.craftengine.bukkit.entity.seat.BukkitSeat#calculateSeatLocation}) — shared by
 * BOTH seat sources this project captures into a contraption: CraftEngine FURNITURE seats
 * ({@link ContraptionFurnitureSwarm#rebuild}, whose source yaw is the furniture's own placement yaw
 * {@code ContraptionFurniture#yawOffsetDegrees()}) and CraftEngine BLOCK seats
 * ({@link ContraptionBlockSeats}, whose source yaw is the block's {@code facing}-derived rotation).
 * Extracted — like {@link ContraptionRenderScale} and {@link ContraptionLightEmitters} — so the two can
 * never drift apart on a convention that is entirely CraftEngine's to define, and so the numeric
 * verification below only has to be done, and trusted, in one place.
 *
 * <p><b>Why a seat is NOT positioned like every other captured element.</b> Every other mirrored cell
 * (item display, text display, armor stand, hitbox part) carries an offset that is already baked into a
 * client-side model whose own rotation the swarm applies separately, so its raw config offset can be added
 * to the piece's local origin unrotated. A {@code SeatConfig#position()} is different in kind: it positions
 * a genuinely SEPARATE real mount entity in world space (see {@code ContraptionSeatMount}), exactly as real
 * CraftEngine does — so the seat's raw config-local offset must be rotated by its SOURCE's own yaw before
 * being combined with that source's local offset, or the seat lands in the wrong spot for any sofa/chair not
 * already facing the bearing's yaw-0 orientation.
 *
 * <p><b>The convention, copied literally (not re-derived).</b> {@code BukkitSeat#calculateSeatLocation}
 * computes, for a source {@code Location} whose yaw is {@code sourceYawDegrees}:
 * <pre>
 *   v = QuaternionUtils.toQuaternionf(0, toRadians(180 - loc.getYaw()), 0).conjugate().transform(position)
 *   result = loc.clone().setYaw(seat.yRot() + loc.getYaw()).add(v.x, v.y + 0.6, -v.z)
 * </pre>
 * {@link #seatOffset} is exactly that {@code (v.x, v.y, -v.z)} triple, with {@code v} expressed through this
 * project's own {@link ContraptionMath#rotateYaw} instead of a JOML quaternion — the two are the SAME
 * rotation, not an approximation: {@code v == rotateYaw(position, toRadians(180 - sourceYaw))} was verified
 * exactly (agreement to &lt; 1e-3 on all three components) against CraftEngine's real
 * {@code QuaternionUtils.toQuaternionf(...).conjugate().transform(...)} over 2000 randomized
 * {@code (position, sourceYaw)} pairs, run on the real {@code craft-engine.jar} on this project's classpath.
 * The {@code +0.6} is deliberately NOT included here — it is a mount-entity height correction that
 * {@code ContraptionSeatMount} already owns and applies (paired with its own {@code 0.9875} armor-stand
 * spawn correction, both copied from the same CraftEngine source), and double-applying it here would raise
 * every seat by 0.6 blocks.
 *
 * <p><b>2026-07-16 — this CORRECTS a real (previously "verified") bug in the furniture seat path.</b>
 * {@link ContraptionFurnitureSwarm#rebuild} used to inline
 * {@code rotated = rotateYaw(seat.position(), +toRadians(sourceYaw))} and then apply
 * {@code (-rotated.x, rotated.y, -rotated.z)}, carrying a comment asserting that this equalled
 * CraftEngine's quaternion math "for every tested yaw". It does not: re-running that same comparison
 * against the real jar shows the old formula agrees with CraftEngine ONLY when the seat's config Z offset
 * is exactly {@code 0}, and diverges for every other Z (e.g. at {@code sourceYaw=0},
 * {@code position=(0.3,0.1,0.7)}, CraftEngine lands the seat at local {@code z = +0.7} while the old
 * formula produced {@code z = -0.7} — a seat placed on the wrong side of its own sofa). Every seat config
 * shipped in {@code testserver}'s blocks/furniture today uses {@code 0,0,0}, where the two formulas
 * coincide exactly — which is why the error was never observed and why routing the furniture path through
 * this class changes NOTHING for any currently-configured seat, while making a {@code z != 0} seat land
 * where CraftEngine would put it in the real world instead of mirrored across the source.
 */
public final class ContraptionSeatMath {

    private ContraptionSeatMath() {
    }

    /**
     * The seat's offset from its SOURCE's own local position, in the bearing's yaw-0 basis — i.e. ready to be
     * added to the source's {@code localOffset} and fed straight into {@code ContraptionMath#renderPosition}/
     * {@code ContraptionState#addSeatedRider} (the contraption's OWN live yaw/pitch/roll/scale is applied
     * later, by that projection, not here).
     *
     * <p>{@code sourceYawDegrees} is whatever yaw CraftEngine itself would have read off the seat's source
     * {@code Location}: a furniture piece's placement yaw, or a seat block's {@code facing}-derived rotation
     * (see {@link ContraptionBlockSeats#facingYawDegrees}). See the class javadoc for the exact CraftEngine
     * expression this reproduces and the numeric verification behind it.
     */
    public static Vec3 seatOffset(Vector3f configPosition, float sourceYawDegrees) {
        Vec3 rotated = ContraptionMath.rotateYaw(
                new Vec3(configPosition.x, configPosition.y, configPosition.z),
                Math.toRadians(180.0 - sourceYawDegrees));
        return new Vec3(rotated.x, rotated.y, -rotated.z);
    }

    /**
     * The seat's own world-facing yaw contribution, before the contraption's live rotation is added on top by
     * {@code ContraptionFurnitureSwarm.SeatSlot#currentYawDegrees} — CraftEngine's own
     * {@code seat.yRot() + loc.getYaw()} (see class javadoc). Kept here beside {@link #seatOffset} so the
     * position and the facing of a seat are always read from the same copied source.
     */
    public static float seatYawDegrees(SeatConfig seat, float sourceYawDegrees) {
        return sourceYawDegrees + seat.yRot();
    }
}
