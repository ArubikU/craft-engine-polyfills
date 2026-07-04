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
        return renderPosition(new Vec3(localOffset.getX(), localOffset.getY(), localOffset.getZ()), bearingWorldPos, yawRadians);
    }

    /** Continuous-offset overload of {@link #renderPosition(BlockPos, Vec3, double)} — for anything captured at a fractional local position (e.g. a followed furniture piece). */
    public static Vec3 renderPosition(Vec3 localOffset, Vec3 bearingWorldPos, double yawRadians) {
        // Translate so the pivot (bearing's XZ center) is the origin, rotate about THAT, then
        // translate back — see #PIVOT_XZ's javadoc for why only X/Z need this and not Y.
        Vec3 centered = new Vec3(localOffset.x - PIVOT_XZ, localOffset.y, localOffset.z - PIVOT_XZ);
        Vec3 rotated = rotateYaw(centered, yawRadians);
        double worldX = bearingWorldPos.x + PIVOT_XZ + rotated.x;
        double worldY = bearingWorldPos.y + rotated.y;
        double worldZ = bearingWorldPos.z + PIVOT_XZ + rotated.z;
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
        Vec3 centered = new Vec3(realPos.x - bearingWorldPos.x - PIVOT_XZ, realPos.y - bearingWorldPos.y,
                realPos.z - bearingWorldPos.z - PIVOT_XZ);
        Vec3 unrotated = rotateYaw(centered, -yawRadians);
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
