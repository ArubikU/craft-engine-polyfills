package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import net.minecraft.world.phys.Vec3;

/**
 * Behavior for a machine-driven contraption (polyfills:machine_contraption).
 * Reads globalRpm each tick for continuous rotation. Also supports:
 *   - move_to: linear tween toward a world target position at fixed speed
 *   - rotate_to: angular tween toward a yaw target at fixed speed
 * All driven by the machine script via ContraptionValue methods.
 */
public class MachineContraptionBehavior implements MovementBehavior {

    private final Axis axis;

    // Optional linear movement tween
    public Vec3 moveTarget = null;
    private double moveSpeed = 0.0;

    // Optional angular tween (yaw only for now)
    public Double yawTarget = null;
    private double yawSpeed = 0.0;

    public enum Axis { YAW, PITCH, ROLL }

    public MachineContraptionBehavior(Vec3 facingVec) {
        this.axis = axisFor(facingVec);
    }

    public static Axis axisFor(Vec3 facing) {
        double ax = Math.abs(facing.x), ay = Math.abs(facing.y), az = Math.abs(facing.z);
        if (ay >= ax && ay >= az) return Axis.YAW;
        if (ax >= az) return Axis.PITCH;
        return Axis.ROLL;
    }

    public void setMoveTarget(Vec3 target, double speed) {
        this.moveTarget = target;
        this.moveSpeed = Math.max(0.001, speed);
    }

    public void setYawTarget(double targetRad, double speedRad) {
        this.yawTarget = targetRad;
        this.yawSpeed = Math.max(0.0001, Math.abs(speedRad));
    }

    public void clearTargets() {
        moveTarget = null;
        yawTarget = null;
    }

    /** Returns the rotation axis as a string for scripts. */
    public String axisName() {
        return switch (axis) {
            case YAW   -> "y";
            case PITCH -> "x";
            case ROLL  -> "z";
        };
    }

    @Override
    public void tick(MovementContext ctx) {
        // Continuous rotation from globalRpm
        float rpm = ctx.state().globalRpm();
        if (rpm != 0f) {
            double radiansPerTick = rpm * 2.0 * Math.PI / 60.0 / 20.0;
            switch (axis) {
                case YAW   -> ctx.state().setYawRadians(ctx.state().yawRadians() + radiansPerTick);
                case PITCH -> ctx.state().setPitchRadians(ctx.state().pitchRadians() + radiansPerTick);
                case ROLL  -> ctx.state().setRollRadians(ctx.state().rollRadians() + radiansPerTick);
            }
        }

        // Linear movement tween
        if (moveTarget != null) {
            double cx = ctx.state().x(), cy = ctx.state().y(), cz = ctx.state().z();
            double dx = moveTarget.x - cx, dy = moveTarget.y - cy, dz = moveTarget.z - cz;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist <= moveSpeed || dist < 0.01) {
                ctx.state().setPosition(moveTarget.x, moveTarget.y, moveTarget.z);
                moveTarget = null;
            } else {
                double step = moveSpeed / dist;
                ctx.state().setPosition(cx + dx * step, cy + dy * step, cz + dz * step);
            }
        }

        // Angular tween (yaw)
        if (yawTarget != null) {
            double cur = ctx.state().yawRadians();
            double diff = yawTarget - cur;
            while (diff > Math.PI) diff -= 2 * Math.PI;
            while (diff < -Math.PI) diff += 2 * Math.PI;
            if (Math.abs(diff) <= yawSpeed || Math.abs(diff) < 0.001) {
                ctx.state().setYawRadians(yawTarget);
                yawTarget = null;
            } else {
                ctx.state().setYawRadians(cur + Math.signum(diff) * yawSpeed);
            }
        }
    }

    @Override
    public boolean isStalled() {
        return false;
    }
}
