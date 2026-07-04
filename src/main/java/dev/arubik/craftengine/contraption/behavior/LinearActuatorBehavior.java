package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * MVP mover (CONTRAPTIONS.md §5 Phase 3): constant-direction straight-line translation. Never
 * stalls on its own — it exists purely to contribute a per-tick velocity; any other behavior in
 * the same contraption (e.g. {@code MinerBehavior}) can still zero the whole contraption's
 * movement for a tick via the master clock's stall vote.
 *
 * <p><b>Task 4 (CONTRAPTIONS.md 2026-07-01 session) — speed comes from a REAL adjacent motor,
 * not a fixed constant.</b> Mirrors {@link RotationalBearingBehavior}'s same change: a LINEAR
 * bearing no longer moves at {@code ContraptionAssembler.DEFAULT_LINEAR_SPEED} forever — it
 * pulls a live rpm from {@link #motorPos} (a real-world neighbor {@link BlockPos}, found once at
 * assembly via {@link RealMotorLink#findAdjacentMotor}, re-resolved every tick via
 * {@link RealMotorLink#currentRpm}) and converts it to a blocks/sec speed along
 * {@link #directionUnit}. <b>Documented simplification, no real gear/pitch design for linear
 * actuators exists yet</b>: the conversion used is {@code speedBlocksPerSec = rpm / 60.0} (i.e.
 * "one motor revolution per minute" is arbitrarily defined as "one block per minute" — the same
 * order-of-magnitude choice as the old {@code DEFAULT_LINEAR_SPEED = 1.0} constant this
 * replaces, not a physically-modelled rack-and-pinion pitch). {@code fallbackSpeed} (blocks/sec)
 * is used only when {@link #motorPos} is {@code null} (no motor found at assembly).
 */
public final class LinearActuatorBehavior implements MovementBehavior {

    private final Vec3 directionUnit;
    /** Real-world neighbor position of the motor this actuator pulls from, or {@code null} if none was found at assembly. */
    private final BlockPos motorPos;
    /** Used only when {@link #motorPos} is {@code null} — see class javadoc. Blocks/sec. */
    private final double fallbackSpeed;
    /** SU demanded from the real motor per captured block — see {@code BearingBlockBehavior#suPerBlock()}. */
    private final double suPerBlock;

    private Vec3 velocityPerTick = Vec3.ZERO;

    public LinearActuatorBehavior(Vec3 directionUnit, BlockPos motorPos, double fallbackSpeed, double suPerBlock) {
        this.directionUnit = directionUnit;
        this.motorPos = motorPos;
        this.fallbackSpeed = fallbackSpeed;
        this.suPerBlock = suPerBlock;
    }

    /** Convenience: fixed velocity, no real motor link — pre-Task-4 behavior, kept for tests/tools. */
    public static LinearActuatorBehavior blocksPerSecond(double dx, double dy, double dz) {
        LinearActuatorBehavior b = new LinearActuatorBehavior(new Vec3(dx, dy, dz).normalize(), null,
                new Vec3(dx, dy, dz).length(), 0);
        b.velocityPerTick = new Vec3(dx / 20.0, dy / 20.0, dz / 20.0);
        return b;
    }

    @Override
    public void tick(MovementContext ctx) {
        double speedBlocksPerSec;
        if (motorPos != null) {
            int blockCount = ctx.state().level() != null ? ctx.state().level().blockCount() : 1;
            // Task 4/5 follow-up — see RotationalBearingBehavior's tick() for the identical
            // "base overhead + this tick's real consumer demand" combine.
            float totalSu = (float) (suPerBlock * blockCount) + ctx.state().suDemand();
            RealMotorLink.reportStressLoad(ctx.level(), motorPos, totalSu);
            float rpm = RealMotorLink.currentRpm(ctx.level(), motorPos);
            ctx.state().setGlobalRpm(rpm); // Task 4: feed globally, same as the rotational bearing
            speedBlocksPerSec = rpm / 60.0;
        } else {
            speedBlocksPerSec = fallbackSpeed;
        }
        velocityPerTick = directionUnit.scale(speedBlocksPerSec / 20.0);
    }

    @Override
    public boolean isStalled() {
        return false;
    }

    @Override
    public Vec3 velocityThisTick() {
        return velocityPerTick;
    }
}
