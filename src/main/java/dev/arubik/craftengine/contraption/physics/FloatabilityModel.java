package dev.arubik.craftengine.contraption.physics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import dev.arubik.craftengine.contraption.behavior.FloatabilityBlockBehavior;
import dev.arubik.craftengine.contraption.behavior.WeightBlockBehavior;
import dev.arubik.craftengine.contraption.level.ContraptionLevel;

/**
 * How a contraption behaves in a fluid — the buoyancy twin of {@code MassModel}.
 *
 * <h2>Why this is not part of the mass model</h2>
 * Mass answers "how much is this": it places the center of mass, builds the inertia tensor, and decides
 * how far a blast throws the body. Floatability answers a different question — "which way does a fluid
 * push it" — and the two were once the same number, because buoyancy compared a block's WEIGHT against
 * a fluid density. That coupling meant mass silently decided flotation: a heavy block could not be made
 * to float and a light one could not be made to sink without lying about its mass. A lead-lined wooden
 * barrel is heavy AND floats, and that is now expressible.
 *
 * <p>The final motion still uses both, as physics requires — the lift this model sizes is a FORCE, and
 * {@code a = F/m} takes the mass from {@code MassModel}. What changed is that mass no longer decides
 * the force's SIGN.
 *
 * <h2>One body, one number</h2>
 * A contraption is a single rigid body, so its cells must agree on one floatability. It is the
 * MASS-WEIGHTED mean — {@code Σ(w·f) / Σw} — because that is what makes the crossover physical: a
 * couple of cork blocks cannot float an anvil, enough of them can, and the tipping point lands exactly
 * where the weights put it. Mass appears here as a WEIGHTING, not as the buoyancy itself.
 */
public record FloatabilityModel(double floatability) {

    /** A contraption with no cells — full gravity in water, i.e. it sinks. */
    public static final FloatabilityModel EMPTY = new FloatabilityModel(FloatabilityTable.DEFAULT_FLOATABILITY);

    /**
     * Aggregates the mass-weighted mean floatability over a captured level's CURRENT cells.
     *
     * <p>Weighted by the same {@link WeightBlockBehavior#weightOf} the mass model uses, so the two
     * agree about what each cell IS even though they disagree about what that implies.
     */
    public static FloatabilityModel of(ContraptionLevel level) {
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
            moment += w * FloatabilityBlockBehavior.floatabilityOf(state);
        }
        return mass <= 0.0 ? EMPTY : new FloatabilityModel(moment / mass);
    }
}
