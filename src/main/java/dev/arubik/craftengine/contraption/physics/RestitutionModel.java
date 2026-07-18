package dev.arubik.craftengine.contraption.physics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import dev.arubik.craftengine.contraption.behavior.RestitutionBlockBehavior;
import dev.arubik.craftengine.contraption.behavior.WeightBlockBehavior;
import dev.arubik.craftengine.contraption.level.ContraptionLevel;

/**
 * A contraption's own bounciness — the mass-weighted mean of its cells' restitution, the normal-direction
 * twin of {@link FrictionModel}.
 *
 * <h2>One body, one number — for its own side of a contact</h2>
 * A contraption is a single rigid body, so its cells agree on one restitution, the MASS-WEIGHTED mean
 * ({@code Σ(w·e) / Σw}) — a couple of slime cells do not make an iron barge trampoline, a body that is mostly
 * slime does. The solver then combines THIS number with the surface the body strikes by taking the bouncier
 * of the two (see {@code XpbdSolver#solveVelocities}), so a slime raft bounces off stone and an iron raft
 * bounces off a slime floor.
 */
public record RestitutionModel(double restitution) {

    /** A contraption with no cells — no bounce. */
    public static final RestitutionModel EMPTY = new RestitutionModel(RestitutionTable.DEFAULT_RESTITUTION);

    /** Aggregates the mass-weighted mean restitution over a captured level's CURRENT cells. */
    public static RestitutionModel of(ContraptionLevel level) {
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
            moment += w * RestitutionBlockBehavior.restitutionOf(state);
        }
        return mass <= 0.0 ? EMPTY : new RestitutionModel(moment / mass);
    }
}
