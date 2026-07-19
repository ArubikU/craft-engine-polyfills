package dev.arubik.craftengine.chainery;

import net.momirealms.craftengine.core.plugin.config.ConfigSection;

/**
 * The physical + rendering profile of a chain kind (CHAINERY — "una cadena de hierro tiene 0
 * estiración y máxima longitud la cantidad de cadenas"). Carried by the {@code ChaineryItemBehavior}
 * and copied onto every {@link Chain} it creates, so the same item can describe a stiff iron chain or
 * a slack rope purely from config.
 *
 * @param blockId   CraftEngine block id whose MODEL renders the links and that gets placed at the two
 *                  endpoints (the block that carries {@code ChaineryBlockBehavior}).
 * @param maxBlocks hard cap on span in blocks — also the max items a single chain may consume. A span
 *                  beyond this is refused.
 * @param stretch   elastic give, 0..1: 0 = rigid (iron, holds an exact distance — XPBD compliance 0);
 *                  higher = a springier rope that can be pulled past its rest length before it bites.
 * @param maxTension impulse (blocks/tick · mass) the link can carry before it SNAPS. 0 disables breaking
 *                  (an unbreakable tether). Used by the physics phase.
 * @param pull      stiffness of the corrective pull applied to a tethered body, 0..1 (1 = yank fully to
 *                  the constraint each tick, lower = ease it in). Used by the physics phase.
 */
public record ChainMaterial(String blockId, int maxBlocks, double stretch, double maxTension, double pull) {

    public static final ChainMaterial DEFAULT = new ChainMaterial("cml:iron_chain", 32, 0.0, 60.0, 1.0);

    /** Reads a material from an item behavior's config section, falling back to {@link #DEFAULT} per field. */
    public static ChainMaterial fromConfig(ConfigSection args) {
        if (args == null) {
            return DEFAULT;
        }
        String block = String.valueOf(args.getOrDefault("block", DEFAULT.blockId()));
        int maxBlocks = ((Number) args.getOrDefault("max-blocks", DEFAULT.maxBlocks())).intValue();
        double stretch = ((Number) args.getOrDefault("stretch", DEFAULT.stretch())).doubleValue();
        double maxTension = ((Number) args.getOrDefault("max-tension", DEFAULT.maxTension())).doubleValue();
        double pull = ((Number) args.getOrDefault("pull", DEFAULT.pull())).doubleValue();
        return new ChainMaterial(block, Math.max(1, maxBlocks),
                Math.max(0.0, Math.min(1.0, stretch)),
                Math.max(0.0, maxTension),
                Math.max(0.0, Math.min(1.0, pull)));
    }
}
