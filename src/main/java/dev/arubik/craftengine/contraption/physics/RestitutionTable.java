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
 *   0.0   every ordinary block — absorbs the hit (the default)
 *   1.0   slime block — the ONE vanilla bouncy block
 * </pre>
 * {@link FrictionTable} deliberately does NOT model slime's bounce ("a separate thing we do not model") —
 * this table is that separate thing.
 *
 * <h2>Restitution is CELL-LOCAL, not a body average</h2>
 * Unlike mass, friction, and floatability (one mass-weighted-mean number per body), a contraption only bounces
 * where the CONTACTED cell is bouncy: a slime cell in a structure's floor bounces when that cell lands, while a
 * stone cell beside it does not (the solver resolves restitution at each contact point — see
 * {@code PhysicsWorld#buildRestitutionField}). Between two bodies the bouncier contacted cell wins.
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
        // The slime block is the ONLY vanilla block that bounces, at full restitution (2026-07-17 — user:
        // "el único bloque que tendrá vía vanilla será el slime ... todos tienen 0, el slime 1"). Everything
        // else — stone, dirt, wood, metal, even beds/honey — is a dead stop; a bouncier block is opt-in via
        // restitution.yml or a custom polyfills:restitution_block.
        if (m.name().equals("SLIME_BLOCK")) {
            return 1.0;
        }
        return TABLE.defaultValue();
    }
}
