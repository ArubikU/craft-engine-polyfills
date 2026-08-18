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
import dev.arubik.craftengine.rotation.RpmConsumer;
import dev.arubik.craftengine.rotation.RpmProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class RotationalBearingBehavior
implements MovementBehavior,
RpmProvider {
    private final BlockPos motorPos;
    private final double fallbackRpm;
    private final double suPerBlock;
    private double currentRpm;

    public RotationalBearingBehavior(BlockPos motorPos, double fallbackRpm, double suPerBlock) {
        this.motorPos = motorPos;
        this.fallbackRpm = fallbackRpm;
        this.suPerBlock = suPerBlock;
    }

    public RotationalBearingBehavior(double fallbackRpm) {
        this(null, fallbackRpm, 0.0);
    }

    @Override
    public void tick(MovementContext ctx) {
        if (this.motorPos != null) {
            int blockCount = ctx.state().level() != null ? ctx.state().level().blockCount() : 1;
            float totalSu = (float)(this.suPerBlock * (double)blockCount) + ctx.state().suDemand();
            RealMotorLink.reportStressLoad((Level)ctx.level(), this.motorPos, totalSu);
            this.currentRpm = RealMotorLink.currentRpm((Level)ctx.level(), this.motorPos);
        } else {
            this.currentRpm = this.fallbackRpm;
        }
        double radiansPerTick = this.currentRpm * 2.0 * Math.PI / 60.0 / 20.0;
        ctx.state().setYawRadians(ctx.state().yawRadians() + radiansPerTick);
        float rpmNow = this.getRpm();
        ctx.state().setGlobalRpm(rpmNow);
        for (MovementBehavior other : ctx.state().behaviors()) {
            if (other == this || !(other instanceof RpmConsumer)) continue;
            RpmConsumer consumer = (RpmConsumer)(other);
            consumer.setInputRpm(rpmNow);
        }
    }

    @Override
    public boolean isStalled() {
        return false;
    }

    @Override
    public Vec3 velocityThisTick() {
        return Vec3.ZERO;
    }

    @Override
    public float getRpm() {
        return (float)this.currentRpm;
    }
}

