package dev.arubik.craftengine.chainery;

import net.momirealms.craftengine.core.plugin.config.ConfigSection;

/**
 * The physical + rendering profile of a chain kind (CHAINERY — "una cadena de hierro tiene 0
 * estiración y máxima longitud la cantidad de cadenas"). Carried by the {@code ChaineryItemBehavior}
 * and copied onto every {@link Chain} it creates, so the same item can describe a stiff iron chain or
 * a slack rope purely from config.
 *
 * @param anchorBlock CraftEngine block id placed at the two endpoints — an INVISIBLE marker carrying
 *                  {@code ChaineryBlockBehavior} (the connection point; never seen, only the rope is).
 * @param linkItem  CraftEngine item id of the chain LINK — consumed one-per-block to build/extend the
 *                  chain, dropped when it breaks, and (its vanilla model) what the rope renders as.
 * @param maxBlocks hard cap on span in blocks — also the max items a single chain may consume. A span
 *                  beyond this is refused.
 * @param stretch   elastic give, 0..1: 0 = rigid (iron, holds an exact distance — XPBD compliance 0);
 *                  higher = a springier rope that can be pulled past its rest length before it bites.
 * @param maxTension impulse (blocks/tick · mass) the link can carry before it SNAPS. 0 disables breaking
 *                  (an unbreakable tether). Used by the physics phase.
 * @param pull      stiffness of the corrective pull applied to a tethered body, 0..1 (1 = yank fully to
 *                  the constraint each tick, lower = ease it in). Used by the physics phase.
 */
public record ChainMaterial(String anchorBlock, String linkItem, int maxBlocks, double stretch,
        double maxTension, double pull) {

    public static final ChainMaterial DEFAULT =
            new ChainMaterial("cml:chain_anchor", "cml:iron_chain", 32, 0.0, 60.0, 1.0);

    /** Reads a material from an item behavior's config section, falling back to {@link #DEFAULT} per field. */
    public static ChainMaterial fromConfig(ConfigSection args) {
        if (args == null) {
            return DEFAULT;
        }
        String anchor = String.valueOf(args.getOrDefault("anchor", DEFAULT.anchorBlock()));
        String link = String.valueOf(args.getOrDefault("link", DEFAULT.linkItem()));
        int maxBlocks = ((Number) args.getOrDefault("max-blocks", DEFAULT.maxBlocks())).intValue();
        double stretch = ((Number) args.getOrDefault("stretch", DEFAULT.stretch())).doubleValue();
        double maxTension = ((Number) args.getOrDefault("max-tension", DEFAULT.maxTension())).doubleValue();
        double pull = ((Number) args.getOrDefault("pull", DEFAULT.pull())).doubleValue();
        return new ChainMaterial(anchor, link, Math.max(1, maxBlocks),
                Math.max(0.0, Math.min(1.0, stretch)),
                Math.max(0.0, maxTension),
                Math.max(0.0, Math.min(1.0, pull)));
    }
}
