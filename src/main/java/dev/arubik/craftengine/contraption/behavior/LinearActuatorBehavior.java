/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.contraption.behavior.RealMotorLink;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class LinearActuatorBehavior
implements MovementBehavior {
    private final Vec3 directionUnit;
    private final BlockPos motorPos;
    private final double fallbackSpeed;
    private final double suPerBlock;
    private Vec3 velocityPerTick = Vec3.ZERO;

    public LinearActuatorBehavior(Vec3 directionUnit, BlockPos motorPos, double fallbackSpeed, double suPerBlock) {
        this.directionUnit = directionUnit;
        this.motorPos = motorPos;
        this.fallbackSpeed = fallbackSpeed;
        this.suPerBlock = suPerBlock;
    }

    public static LinearActuatorBehavior blocksPerSecond(double dx, double dy, double dz) {
        LinearActuatorBehavior b = new LinearActuatorBehavior(new Vec3(dx, dy, dz).normalize(), null, new Vec3(dx, dy, dz).length(), 0.0);
        b.velocityPerTick = new Vec3(dx / 20.0, dy / 20.0, dz / 20.0);
        return b;
    }

    @Override
    public void tick(MovementContext ctx) {
        double speedBlocksPerSec;
        if (this.motorPos != null) {
            int blockCount = ctx.state().level() != null ? ctx.state().level().blockCount() : 1;
            float totalSu = (float)(this.suPerBlock * (double)blockCount) + ctx.state().suDemand();
            RealMotorLink.reportStressLoad((Level)ctx.level(), this.motorPos, totalSu);
            float rpm = RealMotorLink.currentRpm((Level)ctx.level(), this.motorPos);
            ctx.state().setGlobalRpm(rpm);
            speedBlocksPerSec = (double)rpm / 60.0;
        } else {
            speedBlocksPerSec = this.fallbackSpeed;
        }
        this.velocityPerTick = this.directionUnit.scale(speedBlocksPerSec / 20.0);
    }

    @Override
    public boolean isStalled() {
        return false;
    }

    @Override
    public Vec3 velocityThisTick() {
        return this.velocityPerTick;
    }
}

