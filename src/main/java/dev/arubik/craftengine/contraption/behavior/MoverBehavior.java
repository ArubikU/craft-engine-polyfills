/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.behavior.MinecartFollowBehavior;
import dev.arubik.craftengine.rotation.RpmConsumer;
import net.minecraft.world.phys.Vec3;

public final class MoverBehavior
implements MovementBehavior,
RpmConsumer {
    private final Vec3 localDir;
    private final double speedBlocksPerSec;
    private final double gearRatio;
    private final double minRpm;
    private final double suCost;
    private final boolean requiresPower;
    private float inputRpm;
    private Vec3 velocityPerTick = Vec3.ZERO;
    private boolean enabled;

    public MoverBehavior(Vec3 localDirectionUnit, double speedBlocksPerSec, double gearRatio, double minRpm, double suCost, boolean requiresPower, boolean enabled) {
        this.localDir = localDirectionUnit.normalize();
        this.speedBlocksPerSec = speedBlocksPerSec;
        this.gearRatio = gearRatio;
        this.minRpm = minRpm;
        this.suCost = suCost;
        this.requiresPower = requiresPower;
        this.enabled = enabled;
    }

    public MoverBehavior(Vec3 localDirectionUnit, double speedBlocksPerSec) {
        this(localDirectionUnit, speedBlocksPerSec, 1.0, 0.0, 0.0, false, true);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void tick(MovementContext ctx) {
        boolean freeMoving = false;
        for (MovementBehavior b : ctx.state().behaviors()) {
            if (!(b instanceof MinecartFollowBehavior)) continue;
            freeMoving = true;
            break;
        }
        if (!freeMoving) {
            this.velocityPerTick = Vec3.ZERO;
            return;
        }
        if (!this.enabled) {
            this.velocityPerTick = Vec3.ZERO;
            return;
        }
        float rpmToUse = this.inputRpm > 0.0f ? this.inputRpm : ctx.state().globalRpm();
        double effectiveRpm = (double)rpmToUse * this.gearRatio;
        if (this.requiresPower && (effectiveRpm <= 0.0 || effectiveRpm < this.minRpm)) {
            this.velocityPerTick = Vec3.ZERO;
            return;
        }
        ctx.state().addSuDemand((float)this.suCost);
        double perTick = Math.max(0.0, this.speedBlocksPerSec) / 20.0;
        Vec3 worldDir = ContraptionMath.rotateYaw(this.localDir, ctx.state().yawRadians());
        this.velocityPerTick = worldDir.scale(perTick);
    }

    @Override
    public boolean isStalled() {
        return false;
    }

    @Override
    public Vec3 velocityThisTick() {
        return this.velocityPerTick;
    }

    public Vec3 localDirection() {
        return this.localDir;
    }

    public double speedBlocksPerSec() {
        return this.speedBlocksPerSec;
    }

    @Override
    public void setInputRpm(float rpm) {
        this.inputRpm = rpm;
    }

    @Override
    public float getInputRpm() {
        return this.inputRpm;
    }
}

