package dev.arubik.craftengine.contraption.physics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import dev.arubik.craftengine.contraption.behavior.FrictionBlockBehavior;
import dev.arubik.craftengine.contraption.behavior.WeightBlockBehavior;
import dev.arubik.craftengine.contraption.level.ContraptionLevel;

/**
 * A contraption's own surface grip — the mass-weighted mean of its cells' friction, the tangential twin
 * of {@link FloatabilityModel}.
 *
 * <h2>One body, one number — for its own side of a contact</h2>
 * A contraption is a single rigid body, so its cells agree on one friction, the MASS-WEIGHTED mean
 * ({@code Σ(w·f) / Σw}) — a couple of ice cells do not make an iron barge slippery, a body that is mostly
 * ice does. The solver then combines THIS number with the block the body is resting on (see
 * {@code XpbdSolver#solveVelocities}), so the final grip depends on both surfaces, not just the contraption.
 */
public record FrictionModel(double friction) {

    /** A contraption with no cells — ordinary grip. */
    public static final FrictionModel EMPTY = new FrictionModel(FrictionTable.DEFAULT_FRICTION);

    /** Aggregates the mass-weighted mean friction over a captured level's CURRENT cells. */
    public static FrictionModel of(ContraptionLevel level) {
        if (level == null) {
            return EMPTY;
        }
        double mass = 0.0;
        double moment = 0.0;
        for (BlockPos local : level.localPositions()) {
            BlockState state = level.getBlockState(local);
            if (state.isAir()) {
                continue;
            }
            double w = WeightBlockBehavior.weightOf(state);
            mass += w;
            moment += w * FrictionBlockBehavior.frictionOf(state);
        }
        return mass <= 0.0 ? EMPTY : new FrictionModel(moment / mass);
    }
}
