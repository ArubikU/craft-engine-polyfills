/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.entity.seat.SeatConfig
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.contraption.render;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.seat.SeatConfig;
import org.joml.Vector3f;

public final class ContraptionSeatMath {
    private ContraptionSeatMath() {
    }

    public static Vec3 seatOffset(Vector3f configPosition, float sourceYawDegrees) {
        Vec3 rotated = ContraptionMath.rotateYaw(new Vec3((double)configPosition.x, (double)configPosition.y, (double)configPosition.z), Math.toRadians(180.0 - (double)sourceYawDegrees));
        return new Vec3(rotated.x, rotated.y, -rotated.z);
    }

    public static float seatYawDegrees(SeatConfig seat, float sourceYawDegrees) {
        return sourceYawDegrees + seat.yRot();
    }
}

