package dev.arubik.craftengine.contraption.physics;

import org.bukkit.Material;

import net.minecraft.world.level.block.state.BlockState;

/**
 * How grippy a block's surface is — the built-in vanilla table plus the owner's {@code friction.yml}
 * overrides. The material twin of {@link FloatabilityTable}/{@code WeightBlockBehavior}, but for the
 * TANGENTIAL contact response rather than mass or buoyancy.
 *
 * <h2>The scale, and why higher grips</h2>
 * It is a Coulomb friction coefficient — the cap on how much sideways impulse a contact may apply, as a
 * fraction of the normal impulse (see {@code XpbdSolver#solveVelocities}).
 * <pre>
 *   0.7   ordinary grip (the default for every block)
 *   0.05  ice — a body slides across it almost freely
 *   0.4   slime — slick, though its bounce is a separate thing we do not model
 *   1.2   soul sand / soul soil — drags, hard to slide on
 *   1.5   honey — nearly sticks in place
 * </pre>
 *
 * <h2>Friction is a property of the CONTACT, not the body</h2>
 * Unlike mass and floatability (one number per body), friction lives between two touching surfaces. The
 * solver combines the two sides — the contraption's own {@code FrictionModel} mean and the block it rests
 * on — as a geometric mean, so an icy raft slides on stone AND a stone raft slides on ice, and neither
 * alone decides it.
 */
public final class FrictionTable {

    private FrictionTable() {
    }

    /** Grip for a block nothing classified — ordinary friction, matching the solver's historical constant. */
    public static final double DEFAULT_FRICTION = 0.7;

    private static final BlockPropertyTable TABLE = new BlockPropertyTable("friction.yml", DEFAULT_FRICTION);

    /** Loads {@code friction.yml}. Called once on enable. */
    public static void load() {
        TABLE.load();
    }

    /**
     * The friction of a vanilla {@code state}: the owner's {@code friction.yml} override, else the built-in
     * family table, else the configured default. A custom block's own {@code friction:} behavior is checked
     * BEFORE this is ever reached — see {@code FrictionBlockBehavior#frictionOf}.
     */
    public static double vanillaFriction(BlockState state) {
        Material m;
        try {
            m = org.bukkit.craftbukkit.util.CraftMagicNumbers.getMaterial(state.getBlock());
        } catch (Throwable t) {
            return TABLE.defaultValue();
        }
        if (m == null || m.isAir()) {
            return TABLE.defaultValue();
        }
        Double override = TABLE.override(m);
        if (override != null) {
            return override;
        }
        String n = m.name();
        // Ice — the canonical slippery surface. The whole family, packed and blue included.
        if (n.contains("ICE")) {
            return 0.05;
        }
        // Slime is slick underfoot (its bounce is a separate mechanic we do not model here).
        if (n.contains("SLIME")) {
            return 0.4;
        }
        // Honey nearly glues a body in place.
        if (n.contains("HONEY")) {
            return 1.5;
        }
        // Soul sand / soul soil drag — hard to slide across.
        if (n.contains("SOUL_SAND") || n.contains("SOUL_SOIL")) {
            return 1.2;
        }
        // Everything else — stone, dirt, wood, metal — ordinary grip.
        return TABLE.defaultValue();
    }
}
