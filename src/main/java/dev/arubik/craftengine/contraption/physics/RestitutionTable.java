package dev.arubik.craftengine.contraption.physics;

import org.bukkit.Material;

import net.minecraft.world.level.block.state.BlockState;

/**
 * How bouncy a block's surface is — the built-in vanilla table plus the owner's {@code restitution.yml}
 * overrides. The normal-direction twin of {@link FrictionTable}: where friction caps the TANGENTIAL
 * response, restitution decides how much of a head-on impact is handed back as a bounce.
 *
 * <h2>The scale, and why higher bounces</h2>
 * It is a coefficient of restitution — the fraction of closing speed returned as separation at a contact
 * (see {@code XpbdSolver#solveVelocities}). {@code 0} is a dead stop, {@code 1} a perfect elastic bounce.
 * <pre>
 *   0.0   ordinary block — absorbs the hit (the default for every block)
 *   0.8   slime block — flings a falling body back up
 *   0.5   bed — a firm bounce
 *   0.0   honey — sticky, keeps its old no-bounce grip
 * </pre>
 * {@link FrictionTable} deliberately does NOT model slime's bounce ("a separate thing we do not model") —
 * this table is that separate thing.
 *
 * <h2>Restitution is a property of the CONTACT, not the body</h2>
 * Like friction, bounce lives between two touching surfaces. The solver combines the two sides — the
 * contraption's own {@code RestitutionModel} mean and the surface it strikes — by taking the BOUNCIER of the
 * two (the standard restitution-combine rule: a rubber ball bounces off concrete because the ball is bouncy,
 * not because both surfaces are), so a slime contraption bounces off any ground and any body bounces off a
 * slime contraption.
 */
public final class RestitutionTable {

    private RestitutionTable() {
    }

    /** Bounce for a block nothing classified — none, matching the solver's historical hardcoded restitution. */
    public static final double DEFAULT_RESTITUTION = 0.0;

    private static final BlockPropertyTable TABLE = new BlockPropertyTable("restitution.yml", DEFAULT_RESTITUTION);

    /** Loads {@code restitution.yml}. Called once on enable. */
    public static void load() {
        TABLE.load();
    }

    /**
     * The restitution of a vanilla {@code state}: the owner's {@code restitution.yml} override, else the
     * built-in family table, else the configured default. A custom block's own {@code restitution:} behavior
     * is checked BEFORE this is ever reached — see {@code RestitutionBlockBehavior#restitutionOf}.
     */
    public static double vanillaRestitution(BlockState state) {
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
        // The slime block — the canonical bounce surface. Guard against SLIME_BALL (an item, not placeable)
        // but SLIME_BLOCK is the one that matters here.
        if (n.equals("SLIME_BLOCK")) {
            return 0.8;
        }
        // Beds bounce a fall in vanilla — a firm, lesser rebound than slime.
        if (n.endsWith("_BED")) {
            return 0.5;
        }
        // Honey is sticky: it keeps its no-bounce absorption (and its high friction, in FrictionTable).
        if (n.contains("HONEY")) {
            return 0.0;
        }
        // Everything else — stone, dirt, wood, metal — does not bounce.
        return TABLE.defaultValue();
    }
}
