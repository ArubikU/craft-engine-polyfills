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
 * A captured block's FLOATABILITY: how gravity treats it, as a multiplier.
 *
 * <p>The rotational twin of {@link WeightBlockBehavior} in spirit — data, not kinematics. It attaches
 * no {@link dev.arubik.craftengine.contraption.MovementBehavior}, registers with nothing, and exists
 * only to carry a number the physics aggregates. Where {@code weight:} says how MUCH a cell is,
 * {@code floatability:} says which way gravity pulls it and how hard.
 *
 * <h2>The scale</h2>
 * <pre>
 *    1.0  normal gravity — falls (the default for every block)
 *    0.0  weightless — neither falls nor rises; a body of these hangs where it is put
 *   -1.0  inverted gravity — rises as fast as an ordinary block falls
 *    0.5  half gravity — falls lazily
 *    2.0  double gravity — falls hard
 * </pre>
 *
 * <h2>How a body combines them</h2>
 * A contraption is one rigid body with one acceleration, so the cells have to agree on something: the
 * body's gravity scale is the MASS-WEIGHTED mean of its cells' floatability (see
 * {@link MassModel#gravityScale()}). Mass-weighted rather than a plain average because that is what
 * makes the number physical — a single anti-gravity block cannot lift an anvil, but enough of them
 * can, and the crossover lands exactly where the weights say it should. A structure that is half
 * ordinary blocks and half {@code -1.0} blocks nets out near zero and hovers, which is the intuitive
 * result and falls straight out of the arithmetic rather than being special-cased.
 *
 * <h2>Floatability is not buoyancy</h2>
 * They are separate mechanisms and both apply. Buoyancy is decided by {@code weight:} against the
 * fluid's density ({@code WorldBlockCache#WATER_DENSITY}) and only acts on submerged cells — that is
 * what makes wood float and iron sink IN WATER. Floatability changes gravity itself, everywhere,
 * water or not. A {@code 0.0} block hangs in the air; a light wooden one does not.
 *
 * <p>Config fields (under {@code behavior:}), single field:
 * <pre>
 *   floatability: -1.0   # gravity multiplier for this block
 * </pre>
 */
public class FloatabilityBlockBehavior extends BukkitBlockBehavior {

    public static final Key FACTORY_KEY = Key.of("polyfills:floatability_block");

    public static final Factory FACTORY = new Factory();

    /** Gravity multiplier for a block that declares nothing — ordinary gravity, so nothing changes. */
    public static final double DEFAULT_FLOATABILITY = 1.0;

    private final double floatability;

    public FloatabilityBlockBehavior(BlockDefinition customBlock, double floatability) {
        super(customBlock);
        this.floatability = floatability;
    }

    /** This block's gravity multiplier — see the class javadoc's scale. */
    public double floatability() {
        return floatability;
    }

    /**
     * The gravity multiplier a single captured {@code state} contributes: a custom
     * {@code polyfills:floatability_block}'s configured {@code floatability:}, else
     * {@link #DEFAULT_FLOATABILITY}.
     *
     * <p>Fail-open in the same shape {@link WeightBlockBehavior#weightOf} uses: any unresolvable state
     * falls back to the default rather than throwing.
     */
    public static double floatabilityOf(BlockState state) {
        try {
            ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
            if (ce != null && !ce.isEmpty()) {
                FloatabilityBlockBehavior behavior = ce.behavior().getFirst(FloatabilityBlockBehavior.class);
                if (behavior != null) {
                    return behavior.floatability();
                }
            }
            // Vanilla (or any non-floatability custom) block: floatability.yml, else the built-in
            // family table. See FloatabilityTable.
            return dev.arubik.craftengine.contraption.physics.FloatabilityTable.vanillaFloatability(state);
        } catch (Throwable t) {
            return DEFAULT_FLOATABILITY;
        }
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            double floatability = Double.parseDouble(
                    arguments.getOrDefault("floatability", Double.valueOf(DEFAULT_FLOATABILITY)).toString());
            return new FloatabilityBlockBehavior(block, floatability);
        }
    }
}
