package dev.arubik.craftengine.contraption;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * Pure bearing-relative coordinate math (CONTRAPTIONS.md §1 "Coordinates"): converts
 * between absolute world positions and bearing-local offsets, and applies the
 * translate-to-bearing + rotate-in-place kinematic step used every tick by the master
 * clock. No NMS runtime state — safe to unit test directly.
 */
public final class ContraptionMath {

    private ContraptionMath() {
    }

    /** World position minus the bearing's world position, as an integer block offset. */
    public static BlockPos toLocal(BlockPos worldPos, BlockPos bearingWorldPos) {
        return worldPos.subtract(bearingWorldPos);
    }

    /** Bearing world position plus a local offset, back to an integer block position. */
    public static BlockPos toWorld(BlockPos localOffset, BlockPos bearingWorldPos) {
        return bearingWorldPos.offset(localOffset);
    }

    /**
     * Pivot offset (in local-offset space) from the bearing block's own corner-anchored
     * {@code (0,0,0)} origin to its geometric CENTER. Every real Minecraft {@code BlockPos} is
     * corner-anchored (its integer coordinate is the block's minimum corner, not its middle) —
     * so a local offset of {@code (0,0,0)} means "the bearing's own corner," not "the bearing's
     * own middle." Yaw is a rotation about the Y axis only, so only X/Z need centering; Y is
     * left alone (a block's vertical extent isn't relevant to a Y-axis spin, and shifting it
     * would incorrectly move every cell's floor up by half a block).
     */
    private static final double PIVOT_XZ = 0.5;

    /**
     * Renders one cell's world-space render position this tick: rotate the local offset
     * around the bearing block's CENTER (not its corner — see {@link #PIVOT_XZ}'s javadoc) by
     * {@code yawRadians} about the Y axis, then translate by the bearing's current continuous
     * world-space position. Kept as doubles throughout (render-only; never rounded until
     * grid-snap on disassemble).
     *
     * <p><b>2026-07-02 fix — "porque el rotational bearing gira sobre una esquina y no sobre el
     * medio del bloque."</b> This used to rotate {@code localOffset} directly around local
     * {@code (0,0,0)} — correct-looking for any OTHER cell (whose offset is already an integer
     * number of blocks away from the bearing), but wrong for the bearing's OWN cell
     * ({@code localOffset=(0,0,0)}): rotating the zero vector around itself is a no-op, so
     * naively it looks harmless — the actual bug is that {@code (0,0,0)} in this coordinate
     * convention is the bearing block's MINIMUM CORNER, not its middle, so the entire structure
     * was orbiting that corner instead of spinning in place around the bearing's own center.
     * Every OTHER cell's offset is corner-to-corner too, so the whole assembly swept a visibly
     * wider arc than it should have (reads as "flying apart"/"scattered" on a rotating multi-cell
     * structure, exactly the reported symptom) even though every swarm was consistently using the
     * same bearing position/yaw every tick (verified: {@link
     * dev.arubik.craftengine.contraption.ContraptionEntity#render} computes {@code bearing}/
     * {@code yaw} once and passes the literal same values into every swarm's own
     * {@code renderPosition} call this same tick — there is no separate desync bug here, just the
     * wrong pivot amplified across the whole structure). Fixed by translating each local offset
     * to be relative to the bearing's CENTER first, rotating that, then translating back —
     * standard "rotate around a point that isn't the origin" technique.
     */
    public static Vec3 renderPosition(BlockPos localOffset, Vec3 bearingWorldPos, double yawRadians) {
        return renderPosition(new Vec3(localOffset.getX(), localOffset.getY(), localOffset.getZ()), bearingWorldPos,
                yawRadians, 0.0);
    }

    /** Yaw+pitch overload of {@link #renderPosition(BlockPos, Vec3, double)} — see {@link #renderPosition(Vec3, Vec3, double, double)}. */
    public static Vec3 renderPosition(BlockPos localOffset, Vec3 bearingWorldPos, double yawRadians, double pitchRadians) {
        return renderPosition(new Vec3(localOffset.getX(), localOffset.getY(), localOffset.getZ()), bearingWorldPos,
                yawRadians, pitchRadians);
    }

    /**
     * Yaw+pitch+ROLL+SCALE {@link BlockPos} overload (roadmap item #9 phase 6 — ROLL, the horizontal twin of
     * PITCH). Delegates to {@link #renderPosition(Vec3, Vec3, double, double, double, double)} — see there for
     * the {@code yaw ∘ pitch ∘ roll} composition. At {@code pitch == 0 && roll == 0 && scale == 1.0} this is
     * byte-for-byte the yaw-only overload.
     */
    public static Vec3 renderPosition(BlockPos localOffset, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            double rollRadians, double scale) {
        return renderPosition(new Vec3(localOffset.getX(), localOffset.getY(), localOffset.getZ()), bearingWorldPos,
                yawRadians, pitchRadians, rollRadians, scale);
    }

    /**
     * Yaw+pitch+SCALE {@link BlockPos} overload (roadmap item #9 — per-contraption {@code scale}
     * transform, {@code [0.1, 10.0]}, default {@code 1.0}). Delegates to
     * {@link #renderPosition(Vec3, Vec3, double, double, double)} — see there for the scale-about-pivot
     * math and the scale-1 fast path. At {@code scale == 1.0} this is byte-for-byte the yaw+pitch overload.
     */
    public static Vec3 renderPosition(BlockPos localOffset, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            double scale) {
        return renderPosition(new Vec3(localOffset.getX(), localOffset.getY(), localOffset.getZ()), bearingWorldPos,
                yawRadians, pitchRadians, scale);
    }

    /** Continuous-offset, yaw-only overload of {@link #renderPosition(BlockPos, Vec3, double)} (pitch = 0). */
    public static Vec3 renderPosition(Vec3 localOffset, Vec3 bearingWorldPos, double yawRadians) {
        return renderPosition(localOffset, bearingWorldPos, yawRadians, 0.0);
    }

    /**
     * Continuous-offset overload of {@link #renderPosition(BlockPos, Vec3, double)} extended with PITCH
     * (roadmap item #9 phase 5 — TIPPING): rotates the local offset around the bearing pivot by BOTH yaw
     * (about the vertical Y axis) AND pitch (about the horizontal tipping axis), then translates by the
     * bearing's world position. The rotation is the rigid-body composition {@code yaw ∘ pitch} — pitch is
     * applied FIRST (an intrinsic tilt in the body frame), then yaw spins the tilted body about vertical
     * (see {@link #rotateYawPitch}), so the render orientation and this position use the identical rotation.
     *
     * <p><b>Pitch-0 fast path (zero regression).</b> When {@code pitchRadians == 0} this is byte-for-byte the
     * original yaw-only math — {@link #rotateYawPitch} skips the pitch rotation entirely and calls exactly
     * {@link #rotateYaw} as before — so every existing (never-tipped) contraption's render is unchanged. The
     * pitch pivot on the vertical/depth axes matches the yaw pivot: X/Z are centred by {@link #PIVOT_XZ}
     * exactly as yaw already does, and Y is left un-centred (a tip about the bearing's own base line), which
     * only matters at nonzero pitch anyway.
     */
    public static Vec3 renderPosition(Vec3 localOffset, Vec3 bearingWorldPos, double yawRadians, double pitchRadians) {
        return renderPosition(localOffset, bearingWorldPos, yawRadians, pitchRadians, 1.0);
    }

    /**
     * Scale-aware extension of {@link #renderPosition(Vec3, Vec3, double, double)} (roadmap item #9 —
     * per-contraption {@code scale} transform, clamped {@code [0.1, 10.0]} by
     * {@code ContraptionState#setScale}, default {@code 1.0}). After translating the local offset to the
     * bearing PIVOT (X/Z center — see {@link #PIVOT_XZ}) and applying the {@code yaw ∘ pitch} rigid
     * rotation, the resulting offset-FROM-PIVOT is multiplied by {@code scale} before the pivot's world
     * position is added back — so the whole structure grows (scale &gt; 1) or shrinks (scale &lt; 1)
     * uniformly ABOUT its bearing pivot, leaving the pivot itself fixed. Every downstream consumer that
     * positions a cell (render swarm, hitbox swarm, real-world projection) routes through here, so the
     * visual, the colliders, and the persisted real-world projection all scale in lock-step.
     *
     * <p><b>Scale-1 fast path (zero regression).</b> At {@code scale == 1.0} the {@code scale * rotated}
     * products reduce to {@code rotated} exactly (multiplication by the {@code double} literal {@code 1.0}
     * is the identity, no rounding), so this is byte-for-byte the pre-scale {@code yaw ∘ pitch} projection
     * — every existing contraption keeps {@code scale = 1.0} and its cell positions are unchanged.
     */
    public static Vec3 renderPosition(Vec3 localOffset, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            double scale) {
        return renderPosition(localOffset, bearingWorldPos, yawRadians, pitchRadians, 0.0, scale);
    }

    /**
     * ROLL-aware extension of {@link #renderPosition(Vec3, Vec3, double, double, double)} (roadmap item #9
     * phase 6 — ROLL, the horizontal twin of PITCH). A body can now lean in ANY horizontal direction: PITCH
     * tips it about the horizontal X axis (a lean along Z), ROLL tips it about the horizontal Z axis (a lean
     * along X). The rigid rotation is the composition {@code yaw ∘ pitch ∘ roll} — roll is applied FIRST
     * (an intrinsic tilt in the body frame), then pitch, then yaw spins the tilted body about vertical (see
     * {@link #rotateYawPitchRoll}), and the render orientation quaternion composes in the identical order so a
     * cell's rendered position and its model orientation stay in lock-step. Both tilt axes pass through the
     * bearing's XZ-centred base line (X/Z pivot-centred by {@link #PIVOT_XZ}, Y un-centred) exactly like the
     * pitch-only path already does.
     *
     * <p><b>Pitch-0 &amp; roll-0 fast path (zero regression).</b> When {@code pitchRadians == 0 &&
     * rollRadians == 0} {@link #rotateYawPitchRoll} skips BOTH tilt rotations and reduces to exactly
     * {@link #rotateYaw}, so at {@code scale == 1.0} this is byte-for-byte the original yaw-only math — every
     * existing (never-tilted, never-scaled) contraption's projection is unchanged.
     */
    public static Vec3 renderPosition(Vec3 localOffset, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            double rollRadians, double scale) {
        // Translate so the pivot (bearing's XZ center) is the origin, rotate about THAT, then
        // translate back — see #PIVOT_XZ's javadoc for why only X/Z need this and not Y.
        Vec3 centered = new Vec3(localOffset.x - PIVOT_XZ, localOffset.y, localOffset.z - PIVOT_XZ);
        Vec3 rotated = rotateYawPitchRoll(centered, yawRadians, pitchRadians, rollRadians);
        // scale multiplies the rotated offset-FROM-PIVOT so the body grows/shrinks about its bearing
        // pivot; at scale == 1.0 this is exactly `rotated` (identity), preserving the pre-scale math.
        double worldX = bearingWorldPos.x + PIVOT_XZ + scale * rotated.x;
        double worldY = bearingWorldPos.y + scale * rotated.y;
        double worldZ = bearingWorldPos.z + PIVOT_XZ + scale * rotated.z;
        return new Vec3(worldX, worldY, worldZ);
    }

    /**
     * Inverse of {@link #renderPosition(Vec3, Vec3, double)}: given a REAL-world continuous
     * position, returns the bearing-LOCAL position it currently corresponds to (Task 2,
     * CONTRAPTIONS.md 2026-07-01 session "right-click routing" — the raycast runs entirely in
     * real-world space against each contraption's live cell AABBs, then this converts the hit
     * point back to the {@code ContraptionLevel}'s own local coordinate space, e.g. for a
     * {@code BlockHitResult} usable directly against the real block living there).
     *
     * <p>Mirrors {@link #renderPosition(Vec3, Vec3, double)}'s pivot-centering exactly, in
     * reverse: undo the {@code +PIVOT_XZ} world-space translation, rotate by {@code -yawRadians}
     * around the (now-origin) pivot, then undo the local-space centering translation — otherwise
     * this inverse would land 0.5 blocks off on X/Z from the fix above (right-click routing would
     * hit the wrong local block on any rotated contraption).
     */
    public static Vec3 realToLocal(Vec3 realPos, Vec3 bearingWorldPos, double yawRadians) {
        return realToLocal(realPos, bearingWorldPos, yawRadians, 0.0);
    }

    /**
     * Yaw+pitch inverse of {@link #renderPosition(Vec3, Vec3, double, double)} (roadmap item #9 phase 5 —
     * TIPPING). Undoes the exact {@code yaw ∘ pitch} rigid rotation the forward projection applied: since the
     * forward order is pitch-then-yaw, the inverse is yaw-then-pitch run backwards — un-yaw first
     * ({@code rotateYaw(-yaw)}), then un-pitch ({@code rotatePitch(-pitch)}). At {@code pitchRadians == 0} the
     * pitch step is skipped and this is byte-for-byte the original yaw-only inverse.
     */
    public static Vec3 realToLocal(Vec3 realPos, Vec3 bearingWorldPos, double yawRadians, double pitchRadians) {
        return realToLocal(realPos, bearingWorldPos, yawRadians, pitchRadians, 1.0);
    }

    /**
     * Scale-aware inverse of {@link #renderPosition(Vec3, Vec3, double, double, double)} (roadmap item #9 —
     * per-contraption {@code scale}). The forward projection multiplies the rotated offset-from-pivot by
     * {@code scale} LAST, so this inverse divides the world-space offset-from-pivot by {@code scale} FIRST
     * (before un-rotating), landing the right-click/raycast routing on the correct local cell of a scaled
     * contraption. At {@code scale == 1.0} the division is by {@code 1.0} (identity) and this is
     * byte-for-byte the pre-scale yaw+pitch inverse. A {@code scale} of {@code 0} defensively falls back to
     * no division (unreachable in practice — {@code ContraptionState#setScale} clamps to {@code [0.1, 10]}).
     */
    public static Vec3 realToLocal(Vec3 realPos, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            double scale) {
        return realToLocal(realPos, bearingWorldPos, yawRadians, pitchRadians, 0.0, scale);
    }

    /**
     * Yaw+pitch+ROLL inverse of {@link #renderPosition(Vec3, Vec3, double, double, double, double)} (roadmap
     * item #9 phase 6 — ROLL). Undoes the exact {@code yaw ∘ pitch ∘ roll} rigid rotation the forward
     * projection applied: since the forward order is roll-then-pitch-then-yaw, the inverse runs backwards —
     * un-yaw first ({@code rotateYaw(-yaw)}), then un-pitch ({@code rotatePitch(-pitch)}), then un-roll
     * ({@code rotateRoll(-roll)}). The {@code scale} is divided out FIRST exactly as the pitch overload does.
     * At {@code pitchRadians == 0 && rollRadians == 0} both tilt steps are skipped and this is byte-for-byte
     * the original yaw-only inverse.
     */
    public static Vec3 realToLocal(Vec3 realPos, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            double rollRadians, double scale) {
        double invScale = scale != 0.0 ? 1.0 / scale : 1.0;
        Vec3 centered = new Vec3((realPos.x - bearingWorldPos.x - PIVOT_XZ) * invScale,
                (realPos.y - bearingWorldPos.y) * invScale,
                (realPos.z - bearingWorldPos.z - PIVOT_XZ) * invScale);
        Vec3 unrotated = rotateYaw(centered, -yawRadians);
        if (pitchRadians != 0.0) {
            unrotated = rotatePitch(unrotated, -pitchRadians);
        }
        if (rollRadians != 0.0) {
            unrotated = rotateRoll(unrotated, -rollRadians);
        }
        return new Vec3(unrotated.x + PIVOT_XZ, unrotated.y, unrotated.z + PIVOT_XZ);
    }

    /**
     * Rotates a direction/offset vector (NOT a position — no bearing translation) around the Y
     * axis by {@code yawRadians}. Used both by {@link #renderPosition} (position: rotate then
     * translate) and by anything that needs to carry a local-space direction — e.g. a fan's push
     * velocity — into the contraption's current real-world orientation without also translating it.
     */
    public static Vec3 rotateYaw(Vec3 localVector, double yawRadians) {
        double cos = Math.cos(yawRadians);
        double sin = Math.sin(yawRadians);
        double rx = localVector.x * cos - localVector.z * sin;
        double rz = localVector.x * sin + localVector.z * cos;
        return new Vec3(rx, localVector.y, rz);
    }

    /**
     * Rotates a direction/offset vector about the horizontal X axis by {@code pitchRadians} (roadmap item #9
     * phase 5 — TIPPING). The pitch twin of {@link #rotateYaw}: it tips the vector in the Y-Z plane,
     * {@code ry = y·cos − z·sin, rz = y·sin + z·cos}, leaving X unchanged. This sign convention is the
     * standard right-handed rotation about +X, which — unlike the yaw case — maps DIRECTLY onto JOML's
     * {@code Quaternionf#rotateX(pitch)} with no sign flip (JOML rotateX uses the identical
     * {@code y·cos − z·sin} form), so {@code ContraptionLevel#realOrientationOf} composes the render
     * quaternion as {@code rotateY(-yaw).rotateX(+pitch)} to match this position rotation exactly.
     */
    public static Vec3 rotatePitch(Vec3 localVector, double pitchRadians) {
        double cos = Math.cos(pitchRadians);
        double sin = Math.sin(pitchRadians);
        double ry = localVector.y * cos - localVector.z * sin;
        double rz = localVector.y * sin + localVector.z * cos;
        return new Vec3(localVector.x, ry, rz);
    }

    /**
     * Rotates a direction/offset vector about the horizontal Z axis by {@code rollRadians} (roadmap item #9
     * phase 6 — ROLL, the horizontal twin of {@link #rotatePitch}). Where pitch tips the vector in the Y-Z
     * plane (about X), roll tips it in the X-Y plane (about Z): {@code rx = x·cos − y·sin, ry = x·sin + y·cos},
     * leaving Z unchanged. This is the standard right-handed rotation about +Z, which — like the pitch case,
     * and unlike yaw — maps DIRECTLY onto JOML's {@code Quaternionf#rotateZ(roll)} with no sign flip (JOML
     * rotateZ uses the identical {@code x·cos − y·sin} form), so {@code ContraptionLevel#realOrientationOf}
     * composes the render quaternion as {@code rotateY(-yaw).rotateX(+pitch).rotateZ(+roll)} to match this
     * position rotation exactly. A COM overhanging toward +X therefore tips the body's +X side DOWN with a
     * NEGATIVE roll (a point at +X moves to +Y — up — under +roll), which is why
     * {@code PhysicsBehavior#applyTippingTorque} seeds {@code -sign(dx)} torque to lean toward the heavy +X side.
     */
    public static Vec3 rotateRoll(Vec3 localVector, double rollRadians) {
        double cos = Math.cos(rollRadians);
        double sin = Math.sin(rollRadians);
        double rx = localVector.x * cos - localVector.y * sin;
        double ry = localVector.x * sin + localVector.y * cos;
        return new Vec3(rx, ry, localVector.z);
    }

    /**
     * The combined rigid rotation {@code yaw ∘ pitch} used by {@link #renderPosition}: pitch is applied FIRST
     * (intrinsic body tilt via {@link #rotatePitch}), THEN yaw ({@link #rotateYaw}) spins the tilted body about
     * vertical — the same order the render orientation quaternion composes, so a cell's rendered position and
     * its model orientation stay in lock-step. <b>Pitch-0 fast path:</b> when {@code pitchRadians == 0} the
     * pitch rotation is skipped entirely and this is exactly {@link #rotateYaw}, preserving the pre-pitch math
     * byte-for-byte for every contraption that never tips. Now a thin wrapper over
     * {@link #rotateYawPitchRoll} with {@code roll = 0}.
     */
    public static Vec3 rotateYawPitch(Vec3 localVector, double yawRadians, double pitchRadians) {
        return rotateYawPitchRoll(localVector, yawRadians, pitchRadians, 0.0);
    }

    /**
     * The combined rigid rotation {@code yaw ∘ pitch ∘ roll} used by
     * {@link #renderPosition(Vec3, Vec3, double, double, double, double)} (roadmap item #9 phase 6 — ROLL).
     * ROLL is applied FIRST (intrinsic tilt about Z via {@link #rotateRoll}), THEN PITCH (intrinsic tilt about
     * X via {@link #rotatePitch}), THEN YAW ({@link #rotateYaw}) spins the tilted body about vertical — the
     * exact order the render orientation quaternion composes ({@code rotateY(-yaw).rotateX(pitch).rotateZ(roll)}
     * applies roll innermost, then pitch, then yaw to a vector), so a cell's rendered position and its model
     * orientation stay in lock-step. <b>Pitch-0 &amp; roll-0 fast path:</b> when {@code pitchRadians == 0 &&
     * rollRadians == 0} BOTH tilt rotations are skipped and this is exactly {@link #rotateYaw}, preserving the
     * pre-tilt math byte-for-byte for every contraption that never leans.
     */
    public static Vec3 rotateYawPitchRoll(Vec3 localVector, double yawRadians, double pitchRadians,
            double rollRadians) {
        Vec3 rolled = rollRadians == 0.0 ? localVector : rotateRoll(localVector, rollRadians);
        Vec3 pitched = pitchRadians == 0.0 ? rolled : rotatePitch(rolled, pitchRadians);
        return rotateYaw(pitched, yawRadians);
    }

    /**
     * Grid-snap: rounds a continuous render position to the nearest integer block
     * position, for the disassemble-only "round every local-space block position to the
     * nearest integer world coordinate" step (CONTRAPTIONS.md §1 "Assembly/disassembly").
     */
    public static BlockPos gridSnap(Vec3 continuousWorldPos) {
        return new BlockPos(
                (int) Math.round(continuousWorldPos.x),
                (int) Math.round(continuousWorldPos.y),
                (int) Math.round(continuousWorldPos.z));
    }

    /**
     * Axis-snap: rounds a continuous yaw to the nearest of the 4 cardinal-facing rotations
     * (0/90/180/270 degrees, in radians) — real-world blocks can only be placed at one of
     * these 4 rotations, never diagonal (disassembly-time requirement, CONTRAPTIONS.md
     * "al desarmar intentara acomodar en uno de los 4 ejes nunca en diagonal"). Result is
     * always one of {@code {0, PI/2, PI, 3*PI/2}}, normalized to {@code [0, 2*PI)}.
     */
    public static double snapYawToCardinal(double yawRadians) {
        double twoPi = Math.PI * 2;
        double normalized = yawRadians % twoPi;
        if (normalized < 0) {
            normalized += twoPi;
        }
        double quarter = Math.PI / 2;
        int steps = (int) Math.round(normalized / quarter) % 4;
        return steps * quarter;
    }

    /**
     * Number of 90-degree clockwise steps (0-3) separating {@code fromYawRadians} from
     * {@code toYawRadians}, both assumed already cardinal-snapped (see
     * {@link #snapYawToCardinal}) — used to turn a yaw delta into a count of vanilla
     * {@code Rotation.CLOCKWISE_90} applications for disassembly-time block rotation.
     */
    public static int quarterTurnsBetween(double fromYawRadians, double toYawRadians) {
        double quarter = Math.PI / 2;
        double from = snapYawToCardinal(fromYawRadians);
        double to = snapYawToCardinal(toYawRadians);
        int steps = (int) Math.round((to - from) / quarter);
        return ((steps % 4) + 4) % 4;
    }
}
