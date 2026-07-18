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
 * A captured block's FRICTION: how grippy its surface is, as a Coulomb coefficient. Data, not kinematics —
 * the tangential twin of {@link FloatabilityBlockBehavior}. Where {@code floatability:} says which way a
 * fluid pushes a cell, {@code friction:} says how hard it resists SLIDING.
 *
 * <h2>The scale (higher grips)</h2>
 * <pre>
 *   0.7   ordinary grip (the default)
 *   0.05  ice — slides almost freely
 *   1.5   honey — nearly sticks
 * </pre>
 * See {@link dev.arubik.craftengine.contraption.physics.FrictionTable} for the built-in vanilla families
 * and why friction is combined between the two contacting surfaces rather than owned by one body.
 *
 * <p>Config field (under {@code behavior:}):
 * <pre>
 *   friction: 0.05   # Coulomb friction coefficient for this block
 * </pre>
 */
public class FrictionBlockBehavior extends BukkitBlockBehavior {

    public static final Key FACTORY_KEY = Key.of("polyfills:friction_block");

    public static final Factory FACTORY = new Factory();

    /** Grip for a block that declares nothing — ordinary friction. */
    public static final double DEFAULT_FRICTION = 0.7;

    private final double friction;

    public FrictionBlockBehavior(BlockDefinition customBlock, double friction) {
        super(customBlock);
        this.friction = friction;
    }

    /** This block's Coulomb friction coefficient — see the class javadoc's scale. */
    public double friction() {
        return friction;
    }

    /**
     * The friction a single captured {@code state} contributes: a custom {@code polyfills:friction_block}'s
     * configured {@code friction:}, else {@link dev.arubik.craftengine.contraption.physics.FrictionTable}.
     * Fail-open — any unresolvable state falls back to the default rather than throwing.
     */
    public static double frictionOf(BlockState state) {
        try {
            ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
            if (ce != null && !ce.isEmpty()) {
                FrictionBlockBehavior behavior = ce.behavior().getFirst(FrictionBlockBehavior.class);
                if (behavior != null) {
                    return behavior.friction();
                }
            }
            return dev.arubik.craftengine.contraption.physics.FrictionTable.vanillaFriction(state);
        } catch (Throwable t) {
            return DEFAULT_FRICTION;
        }
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            double friction = Double.parseDouble(
                    arguments.getOrDefault("friction", Double.valueOf(DEFAULT_FRICTION)).toString());
            return new FrictionBlockBehavior(block, friction);
        }
    }
}
