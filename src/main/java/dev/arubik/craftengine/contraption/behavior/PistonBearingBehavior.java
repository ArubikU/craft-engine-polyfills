/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.contraption.behavior.RealMotorLink;
import dev.arubik.craftengine.rotation.RpmConsumer;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class PistonBearingBehavior
implements MovementBehavior {
    private final Vec3 dir;
    private final BlockPos bodyPos;
    private final BlockPos motorPos;
    private int maxDistance;
    private double baseSpeedBlocksPerSec;
    private final double suPerBlock;
    private Mode mode;
    private long roundRobinDelayTicks;
    private Phase phase = Phase.EXTENDING;
    private double extended = 0.0;
    private boolean started = false;
    private boolean reachedEndThisCycle = false;
    private Vec3 velocityPerTick = Vec3.ZERO;

    public PistonBearingBehavior(Vec3 directionUnit, BlockPos bodyPos, BlockPos motorPos, int maxDistance, double baseSpeedBlocksPerSec, double suPerBlock, Mode mode, long roundRobinDelayTicks) {
        this.dir = directionUnit.normalize();
        this.bodyPos = bodyPos == null ? null : bodyPos.immutable();
        this.motorPos = motorPos == null ? null : motorPos.immutable();
        this.maxDistance = Math.max(1, maxDistance);
        this.baseSpeedBlocksPerSec = baseSpeedBlocksPerSec;
        this.suPerBlock = suPerBlock;
        this.mode = mode == null ? Mode.LINEAR : mode;
        this.roundRobinDelayTicks = Math.max(0L, roundRobinDelayTicks);
    }

    public static PistonBearingBehavior startRetracting(Vec3 directionUnit, BlockPos bodyPos, BlockPos motorPos, int maxDistance, double baseSpeedBlocksPerSec, double suPerBlock, Mode mode, long roundRobinDelayTicks) {
        PistonBearingBehavior b = new PistonBearingBehavior(directionUnit, bodyPos, motorPos, maxDistance, baseSpeedBlocksPerSec, suPerBlock, mode, roundRobinDelayTicks);
        b.phase = Phase.RETRACTING;
        b.extended = b.maxDistance;
        b.reachedEndThisCycle = false;
        return b;
    }

    private double distanceSuMultiplier() {
        return 1.0 + 0.1 * (double)Math.max(0, this.maxDistance - 1);
    }

    @Override
    public void tick(MovementContext ctx) {
        double perTick = this.resolvePerTickSpeed(ctx);
        if (!this.started) {
            this.started = true;
            this.playSound(ctx, this.phase == Phase.RETRACTING ? SoundEvents.PISTON_CONTRACT : SoundEvents.PISTON_EXTEND, 0.85f);
        }
        switch (this.phase.ordinal()) {
            case 0: {
                double step = Math.min(perTick, (double)this.maxDistance - this.extended);
                this.extended += step;
                this.velocityPerTick = this.dir.scale(step);
                if (!(this.extended >= (double)this.maxDistance - 1.0E-6)) break;
                this.extended = this.maxDistance;
                this.phase = Phase.AT_END;
                this.reachedEndThisCycle = true;
                this.playSound(ctx, SoundEvents.PISTON_EXTEND, 0.6f);
                break;
            }
            case 2: {
                double step = Math.min(perTick, this.extended);
                this.extended -= step;
                this.velocityPerTick = this.dir.scale(-step);
                if (!(this.extended <= 1.0E-6)) break;
                this.extended = 0.0;
                this.phase = Phase.RETRACTED;
                this.playSound(ctx, SoundEvents.PISTON_CONTRACT, 0.6f);
                break;
            }
            case 1: 
            case 3: {
                this.velocityPerTick = Vec3.ZERO;
            }
        }
    }

    private void playSound(MovementContext ctx, SoundEvent sound, float pitch) {
        if (this.bodyPos == null || ctx.level() == null) {
            return;
        }
        try {
            ctx.level().playSeededSound(null, (double)this.bodyPos.getX() + 0.5, (double)this.bodyPos.getY() + 0.5, (double)this.bodyPos.getZ() + 0.5, Holder.direct(sound), SoundSource.BLOCKS, 0.5f, pitch, 0L);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private double resolvePerTickSpeed(MovementContext ctx) {
        double speedBlocksPerSec;
        float rpmNow;
        if (this.motorPos != null) {
            int blockCount = ctx.state().level() != null ? ctx.state().level().blockCount() : 1;
            float totalSu = (float)(this.suPerBlock * (double)blockCount * this.distanceSuMultiplier()) + ctx.state().suDemand();
            RealMotorLink.reportStressLoad((Level)ctx.level(), this.motorPos, totalSu);
            rpmNow = RealMotorLink.currentRpm((Level)ctx.level(), this.motorPos);
            speedBlocksPerSec = (double)rpmNow / 60.0;
        } else {
            rpmNow = 0.0f;
            speedBlocksPerSec = this.baseSpeedBlocksPerSec;
        }
        ctx.state().setGlobalRpm(rpmNow);
        for (MovementBehavior other : ctx.state().behaviors()) {
            if (other == this || !(other instanceof RpmConsumer)) continue;
            RpmConsumer consumer = (RpmConsumer)(other);
            consumer.setInputRpm(rpmNow);
        }
        return Math.max(0.0, speedBlocksPerSec) / 20.0;
    }

    public boolean wantsDisassembleAtEnd() {
        return this.phase == Phase.AT_END && this.reachedEndThisCycle;
    }

    public double extendedBlocks() {
        return this.extended;
    }

    public Vec3 direction() {
        return this.dir;
    }

    public int maxDistance() {
        return this.maxDistance;
    }

    public double baseSpeedBlocksPerSec() {
        return this.baseSpeedBlocksPerSec;
    }

    public double suPerBlock() {
        return this.suPerBlock;
    }

    public Mode mode() {
        return this.mode;
    }

    public long roundRobinDelayTicks() {
        return this.roundRobinDelayTicks;
    }

    public void setMode(Mode m) {
        this.mode = m == null ? Mode.LINEAR : m;
    }

    public void setMaxDistance(int d) {
        this.maxDistance = Math.max(1, d);
    }

    public void setBaseSpeedBlocksPerSec(double s) {
        this.baseSpeedBlocksPerSec = s;
    }

    public void setRoundRobinDelayTicks(long t) {
        this.roundRobinDelayTicks = Math.max(0L, t);
    }

    public boolean isFullyRetracted() {
        return this.phase == Phase.RETRACTED;
    }

    @Override
    public boolean isStalled() {
        return false;
    }

    @Override
    public Vec3 velocityThisTick() {
        return this.velocityPerTick;
    }

    private static enum Phase {
        EXTENDING,
        AT_END,
        RETRACTING,
        RETRACTED;

    }

    public static enum Mode {
        LINEAR,
        ROUND_ROBIN;


        public static Mode fromString(String s) {
            if (s == null) {
                return LINEAR;
            }
            switch (s.toLowerCase(Locale.ROOT)) {
                case "round_robin": 
                case "roundrobin": 
                case "robin_euler": 
                case "robineuler": {
                    return ROUND_ROBIN;
                }
            }
            return LINEAR;
        }
    }
}

