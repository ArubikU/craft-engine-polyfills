/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.phys.Vec3
 */
package dev.arubik.craftengine.contraption.assembly;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public final class ContraptionMath {
    private static final double PIVOT_XZ = 0.5;

    private ContraptionMath() {
    }

    public static BlockPos toLocal(BlockPos worldPos, BlockPos bearingWorldPos) {
        return worldPos.subtract((Vec3i)bearingWorldPos);
    }

    public static BlockPos toWorld(BlockPos localOffset, BlockPos bearingWorldPos) {
        return bearingWorldPos.offset((Vec3i)localOffset);
    }

    public static Vec3 renderPosition(BlockPos localOffset, Vec3 bearingWorldPos, double yawRadians) {
        return ContraptionMath.renderPosition(new Vec3((double)localOffset.getX(), (double)localOffset.getY(), (double)localOffset.getZ()), bearingWorldPos, yawRadians, 0.0);
    }

    public static Vec3 renderPosition(BlockPos localOffset, Vec3 bearingWorldPos, double yawRadians, double pitchRadians) {
        return ContraptionMath.renderPosition(new Vec3((double)localOffset.getX(), (double)localOffset.getY(), (double)localOffset.getZ()), bearingWorldPos, yawRadians, pitchRadians);
    }

    public static Vec3 renderPosition(BlockPos localOffset, Vec3 bearingWorldPos, double yawRadians, double pitchRadians, double rollRadians, double scale) {
        return ContraptionMath.renderPosition(new Vec3((double)localOffset.getX(), (double)localOffset.getY(), (double)localOffset.getZ()), bearingWorldPos, yawRadians, pitchRadians, rollRadians, scale);
    }

    public static Vec3 renderPosition(BlockPos localOffset, Vec3 bearingWorldPos, double yawRadians, double pitchRadians, double scale) {
        return ContraptionMath.renderPosition(new Vec3((double)localOffset.getX(), (double)localOffset.getY(), (double)localOffset.getZ()), bearingWorldPos, yawRadians, pitchRadians, scale);
    }

    public static Vec3 renderPosition(Vec3 localOffset, Vec3 bearingWorldPos, double yawRadians) {
        return ContraptionMath.renderPosition(localOffset, bearingWorldPos, yawRadians, 0.0);
    }

    public static Vec3 renderPosition(Vec3 localOffset, Vec3 bearingWorldPos, double yawRadians, double pitchRadians) {
        return ContraptionMath.renderPosition(localOffset, bearingWorldPos, yawRadians, pitchRadians, 1.0);
    }

    public static Vec3 renderPosition(Vec3 localOffset, Vec3 bearingWorldPos, double yawRadians, double pitchRadians, double scale) {
        return ContraptionMath.renderPosition(localOffset, bearingWorldPos, yawRadians, pitchRadians, 0.0, scale);
    }

    public static Vec3 renderPosition(Vec3 localOffset, Vec3 bearingWorldPos, double yawRadians, double pitchRadians, double rollRadians, double scale) {
        // x/z are centered on the pivot block's middle (-0.5, then +0.5 back after rotating) so a
        // pure yaw spin (about Y) turns the structure around the pivot block's CENTER. y was never
        // given the same treatment, so pitch/roll (which mix Y into X or Z) rotated about the
        // pivot's BOTTOM FACE/edge instead of its center — visible as the whole contraption
        // swinging around one corner for any bearing not spinning about the vertical axis.
        Vec3 centered = new Vec3(localOffset.x - 0.5, localOffset.y - 0.5, localOffset.z - 0.5);
        Vec3 rotated = ContraptionMath.rotateYawPitchRoll(centered, yawRadians, pitchRadians, rollRadians);
        double worldX = bearingWorldPos.x + 0.5 + scale * rotated.x;
        double worldY = bearingWorldPos.y + 0.5 + scale * rotated.y;
        double worldZ = bearingWorldPos.z + 0.5 + scale * rotated.z;
        return new Vec3(worldX, worldY, worldZ);
    }

    public static Vec3 realToLocal(Vec3 realPos, Vec3 bearingWorldPos, double yawRadians) {
        return ContraptionMath.realToLocal(realPos, bearingWorldPos, yawRadians, 0.0);
    }

    public static Vec3 realToLocal(Vec3 realPos, Vec3 bearingWorldPos, double yawRadians, double pitchRadians) {
        return ContraptionMath.realToLocal(realPos, bearingWorldPos, yawRadians, pitchRadians, 1.0);
    }

    public static Vec3 realToLocal(Vec3 realPos, Vec3 bearingWorldPos, double yawRadians, double pitchRadians, double scale) {
        return ContraptionMath.realToLocal(realPos, bearingWorldPos, yawRadians, pitchRadians, 0.0, scale);
    }

    public static Vec3 realToLocal(Vec3 realPos, Vec3 bearingWorldPos, double yawRadians, double pitchRadians, double rollRadians, double scale) {
        double invScale = scale != 0.0 ? 1.0 / scale : 1.0;
        // Inverse of renderPosition's centering above — y must be un-centered the same way x/z are,
        // or round-tripping (hit detection, entity placement inside the contraption, etc.) would
        // disagree with the now-corrected forward transform.
        Vec3 centered = new Vec3((realPos.x - bearingWorldPos.x - 0.5) * invScale, (realPos.y - bearingWorldPos.y - 0.5) * invScale, (realPos.z - bearingWorldPos.z - 0.5) * invScale);
        Vec3 unrotated = ContraptionMath.rotateYaw(centered, -yawRadians);
        if (pitchRadians != 0.0) {
            unrotated = ContraptionMath.rotatePitch(unrotated, -pitchRadians);
        }
        if (rollRadians != 0.0) {
            unrotated = ContraptionMath.rotateRoll(unrotated, -rollRadians);
        }
        return new Vec3(unrotated.x + 0.5, unrotated.y + 0.5, unrotated.z + 0.5);
    }

    public static Vec3 rotateYaw(Vec3 localVector, double yawRadians) {
        double cos = Math.cos(yawRadians);
        double sin = Math.sin(yawRadians);
        double rx = localVector.x * cos - localVector.z * sin;
        double rz = localVector.x * sin + localVector.z * cos;
        return new Vec3(rx, localVector.y, rz);
    }

    public static Vec3 rotatePitch(Vec3 localVector, double pitchRadians) {
        double cos = Math.cos(pitchRadians);
        double sin = Math.sin(pitchRadians);
        double ry = localVector.y * cos - localVector.z * sin;
        double rz = localVector.y * sin + localVector.z * cos;
        return new Vec3(localVector.x, ry, rz);
    }

    public static Vec3 rotateRoll(Vec3 localVector, double rollRadians) {
        double cos = Math.cos(rollRadians);
        double sin = Math.sin(rollRadians);
        double rx = localVector.x * cos - localVector.y * sin;
        double ry = localVector.x * sin + localVector.y * cos;
        return new Vec3(rx, ry, localVector.z);
    }

    public static Vec3 rotateYawPitch(Vec3 localVector, double yawRadians, double pitchRadians) {
        return ContraptionMath.rotateYawPitchRoll(localVector, yawRadians, pitchRadians, 0.0);
    }

    public static Vec3 rotateYawPitchRoll(Vec3 localVector, double yawRadians, double pitchRadians, double rollRadians) {
        Vec3 rolled = rollRadians == 0.0 ? localVector : ContraptionMath.rotateRoll(localVector, rollRadians);
        Vec3 pitched = pitchRadians == 0.0 ? rolled : ContraptionMath.rotatePitch(rolled, pitchRadians);
        return ContraptionMath.rotateYaw(pitched, yawRadians);
    }

    public static BlockPos gridSnap(Vec3 continuousWorldPos) {
        return new BlockPos((int)Math.round(continuousWorldPos.x), (int)Math.round(continuousWorldPos.y), (int)Math.round(continuousWorldPos.z));
    }

    public static double snapYawToCardinal(double yawRadians) {
        double twoPi = Math.PI * 2;
        double normalized = yawRadians % twoPi;
        if (normalized < 0.0) {
            normalized += twoPi;
        }
        double quarter = 1.5707963267948966;
        int steps = (int)Math.round(normalized / quarter) % 4;
        return (double)steps * quarter;
    }

    public static int quarterTurnsBetween(double fromYawRadians, double toYawRadians) {
        double quarter = 1.5707963267948966;
        double from = ContraptionMath.snapYawToCardinal(fromYawRadians);
        double to = ContraptionMath.snapYawToCardinal(toYawRadians);
        int steps = (int)Math.round((to - from) / quarter);
        return (steps % 4 + 4) % 4;
    }
}

