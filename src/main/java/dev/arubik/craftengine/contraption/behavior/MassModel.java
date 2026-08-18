/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix3d
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.behavior.WeightBlockBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3d;

public record MassModel(double totalMass, Vec3 centerOfMass, int cellCount, double ixx, double iyy, double izz, double ixy, double ixz, double iyz) {
    public static final double BASELINE_BLOCK_MASS = 1.0;
    public static final MassModel EMPTY = new MassModel(0.0, Vec3.ZERO, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

    public static MassModel of(ContraptionLevel level) {
        if (level == null) {
            return EMPTY;
        }
        double mass = 0.0;
        double cx = 0.0;
        double cy = 0.0;
        double cz = 0.0;
        int cells = 0;
        for (BlockPos local : level.localPositions()) {
            BlockState state = level.getBlockState(local);
            if (state.isAir()) continue;
            double w = WeightBlockBehavior.weightOf(state);
            mass += w;
            cx += w * ((double)local.getX() + 0.5);
            cy += w * ((double)local.getY() + 0.5);
            cz += w * ((double)local.getZ() + 0.5);
            ++cells;
        }
        if (mass <= 0.0 || cells == 0) {
            return EMPTY;
        }
        double comX = cx / mass;
        double comY = cy / mass;
        double comZ = cz / mass;
        double ixx = 0.0;
        double iyy = 0.0;
        double izz = 0.0;
        double ixy = 0.0;
        double ixz = 0.0;
        double iyz = 0.0;
        for (BlockPos local : level.localPositions()) {
            BlockState state = level.getBlockState(local);
            if (state.isAir()) continue;
            double w = WeightBlockBehavior.weightOf(state);
            double dx = (double)local.getX() + 0.5 - comX;
            double dy = (double)local.getY() + 0.5 - comY;
            double dz = (double)local.getZ() + 0.5 - comZ;
            double self = w / 6.0;
            ixx += self + w * (dy * dy + dz * dz);
            iyy += self + w * (dx * dx + dz * dz);
            izz += self + w * (dx * dx + dy * dy);
            ixy -= w * dx * dy;
            ixz -= w * dx * dz;
            iyz -= w * dy * dz;
        }
        return new MassModel(mass, new Vec3(comX, comY, comZ), cells, ixx, iyy, izz, ixy, ixz, iyz);
    }

    public Matrix3d inertiaTensor() {
        return new Matrix3d(this.ixx, this.ixy, this.ixz, this.ixy, this.iyy, this.iyz, this.ixz, this.iyz, this.izz);
    }

    public Matrix3d inverseInertiaTensor() {
        if (this.cellCount == 0 || this.totalMass <= 0.0) {
            return new Matrix3d().zero();
        }
        Matrix3d inertia = this.inertiaTensor();
        double det = inertia.determinant();
        if (Math.abs(det) < 1.0E-9) {
            return new Matrix3d().zero();
        }
        return inertia.invert(new Matrix3d());
    }

    public double inverseMass() {
        return this.totalMass > 0.0 ? 1.0 / this.totalMass : 0.0;
    }

    public double momentOfInertiaYaw() {
        return this.iyy;
    }

    public double momentOfInertiaPitch() {
        return this.ixx;
    }

    public double momentOfInertiaRoll() {
        return this.izz;
    }
}

