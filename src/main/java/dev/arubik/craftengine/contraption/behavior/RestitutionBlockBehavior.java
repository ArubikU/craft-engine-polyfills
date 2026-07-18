package dev.arubik.craftengine.contraption.behavior;

import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

import net.minecraft.world.level.block.state.BlockState;

/**
 * A captured block's RESTITUTION: how much of an impact it gives back as a bounce, as a coefficient of
 * restitution. Data, not kinematics — the normal-direction twin of {@link FrictionBlockBehavior}. Where
 * {@code friction:} says how hard a cell resists SLIDING, {@code restitution:} says how hard it BOUNCES
 * when something slams into it head-on.
 *
 * <h2>The scale (higher bounces)</h2>
 * <pre>
 *   0.0   dead stop — an ordinary block (the default)
 *   1.0   slime block — flings a falling body back up (the one vanilla bouncy block)
 * </pre>
 * See {@link dev.arubik.craftengine.contraption.physics.RestitutionTable} for the built-in vanilla families
 * and why restitution is combined between the two contacting surfaces (bounciest wins) rather than owned by
 * one body.
 *
 * <p>Config field (under {@code behavior:}):
 * <pre>
 *   restitution: 0.8   # coefficient of restitution for this block
 * </pre>
 */
public class RestitutionBlockBehavior extends BukkitBlockBehavior {

    public static final Key FACTORY_KEY = Key.of("polyfills:restitution_block");

    public static final Factory FACTORY = new Factory();

    /** Bounce for a block that declares nothing — none, matching the solver's old hardcoded restitution. */
    public static final double DEFAULT_RESTITUTION = 0.0;

    private final double restitution;

    public RestitutionBlockBehavior(BlockDefinition customBlock, double restitution) {
        super(customBlock);
        this.restitution = restitution;
    }

    /** This block's coefficient of restitution — see the class javadoc's scale. */
    public double restitution() {
        return restitution;
    }

    /**
     * The restitution a single captured {@code state} contributes: a custom {@code polyfills:restitution_block}'s
     * configured {@code restitution:}, else {@link dev.arubik.craftengine.contraption.physics.RestitutionTable}.
     * Fail-open — any unresolvable state falls back to the default rather than throwing.
     */
    public static double restitutionOf(BlockState state) {
        try {
            ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
            if (ce != null && !ce.isEmpty()) {
                RestitutionBlockBehavior behavior = ce.behavior().getFirst(RestitutionBlockBehavior.class);
                if (behavior != null) {
                    return behavior.restitution();
                }
            }
            return dev.arubik.craftengine.contraption.physics.RestitutionTable.vanillaRestitution(state);
        } catch (Throwable t) {
            return DEFAULT_RESTITUTION;
        }
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            double restitution = Double.parseDouble(
                    arguments.getOrDefault("restitution", Double.valueOf(DEFAULT_RESTITUTION)).toString());
            return new RestitutionBlockBehavior(block, restitution);
        }
    }
}
