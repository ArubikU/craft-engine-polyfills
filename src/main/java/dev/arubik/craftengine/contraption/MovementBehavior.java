package dev.arubik.craftengine.contraption;

import net.minecraft.world.phys.Vec3;

/**
 * One component's contribution to a contraption's tick (CONTRAPTIONS.md §1 "Kinematics —
 * single master clock"). The master clock ({@link ContraptionEngine}) calls {@link #tick}
 * on EVERY active behavior first (so internal state — RPM, mining-damage accumulators —
 * always advances), THEN polls {@link #isStalled} across all of them: if ANY behavior
 * reports stalled, the whole contraption's geographic movement is zeroed for that tick,
 * even though internal state kept advancing. {@link #velocityThisTick} is the behavior's
 * intended per-tick world-space translation — only actually applied when nothing is
 * stalled.
 */
public interface MovementBehavior {

    void tick(MovementContext ctx);

    boolean isStalled();

    /** World-space translation this behavior wants to contribute this tick. Most behaviors (e.g. a miner) don't move anything and can leave this at the default. */
    default Vec3 velocityThisTick() {
        return Vec3.ZERO;
    }
}
