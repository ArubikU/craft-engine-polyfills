package dev.arubik.craftengine.contraption.physics;

import org.bukkit.Material;

import net.minecraft.world.level.block.state.BlockState;

/**
 * How much gravity a block keeps while it is IN A FLUID — the built-in vanilla table plus the owner's
 * {@code floatability.yml} overrides.
 *
 * <h2>The scale, and why lower is floatier</h2>
 * <pre>
 *    1.0  keeps full gravity in water — sinks (stone, dirt, metal)
 *    0.0  fully buoyant in water — floats (wood, wool)
 *   -1.0  pushed up as hard as gravity pulls down — rises
 *    2.0  sinks even through lava
 * </pre>
 * The number is compared against the FLUID's own buoyancy ({@link WorldBlockCache.Fluid#buoyancy()} —
 * 1 for water, 2 for lava), and the difference is the net lift:
 * <pre>
 *   lift = (fluidBuoyancy - floatability) x weight x g
 * </pre>
 * That single expression is the whole model, and it produces every case people expect without one
 * special case: wood ({@code 0}) in water gets a full {@code 1x} lift and floats; stone ({@code 1}) in
 * water gets {@code 0} and sinks, but in LAVA gets {@code 2-1 = 1x} and floats — stone rafts on lava
 * are not coded anywhere, they are what the arithmetic says. Netherite ({@code 2}) sinks through both.
 *
 * <h2>Why this is not the mass table</h2>
 * Mass answers "how much is this" — it drives the center of mass, the inertia tensor, and how hard a
 * blast throws the body. Floatability answers "which way does a fluid push it". They were the same
 * number once (buoyancy compared {@code weight} against a fluid density), which meant a heavy block
 * could not be made to float and a light one could not be made to sink without lying about its mass. A
 * lead-lined wooden barrel is heavy AND floats; that is now expressible.
 *
 * <p>Floatability does NOT touch gravity in air. A block with {@code 0} is not weightless — it falls
 * normally and floats when it lands in water, which is what wood does.
 */
public final class FloatabilityTable {

    private FloatabilityTable() {
    }

    /** Floatability for a block nothing classified — full gravity in water, i.e. it sinks. */
    public static final double DEFAULT_FLOATABILITY = 1.0;

    private static final BlockPropertyTable TABLE = new BlockPropertyTable("floatability.yml", DEFAULT_FLOATABILITY);

    /** Loads {@code floatability.yml}. Called once on enable. */
    public static void load() {
        TABLE.load();
    }

    /**
     * The floatability of a vanilla {@code state}: the owner's {@code floatability.yml} override, else
     * the built-in family table, else the configured default.
     *
     * <p>A CraftEngine custom block's own {@code floatability:} behavior is checked BEFORE this is ever
     * reached — see {@code FloatabilityBlockBehavior#floatabilityOf}.
     */
    public static double vanillaFloatability(BlockState state) {
        Material m;
        try {
            m = org.bukkit.craftbukkit.util.CraftMagicNumbers.getMaterial(state.getBlock());
        } catch (Throwable t) {
            return TABLE.defaultValue();
        }
        if (m == null || m.isAir()) {
            return 0.0; // air displaces nothing and weighs nothing — it must never drag a body down
        }
        Double override = TABLE.override(m);
        if (override != null) {
            return override;
        }
        String n = m.name();
        // Wood floats. The whole family, including worked wood — a plank raft is the canonical case.
        if (n.contains("PLANKS") || n.endsWith("_LOG") || n.endsWith("_WOOD") || n.contains("BAMBOO")
                || n.contains("STEM") || n.contains("HYPHAE") || n.contains("BARREL") || n.contains("CHEST")
                && !n.contains("ENDER")) {
            return 0.0;
        }
        // Cloth, plants and trapped air — light, dry things that ride on the surface.
        if (n.endsWith("_WOOL") || n.endsWith("_CARPET") || n.contains("LEAVES") || n.contains("HAY")
                || n.contains("SPONGE") || n.contains("BAMBOO") || n.contains("SCAFFOLDING")) {
            return 0.0;
        }
        // Ice and glass sit almost neutral — they sink, but lazily.
        if (n.contains("ICE") || n.contains("GLASS")) {
            return 0.8;
        }
        // Dense metal and netherite sink through lava too, so they must outweigh its buoyancy of 2.
        if (n.contains("NETHERITE") || n.contains("ANCIENT_DEBRIS")) {
            return 2.5;
        }
        if (n.contains("OBSIDIAN") || n.equals("IRON_BLOCK") || n.equals("GOLD_BLOCK") || n.equals("DIAMOND_BLOCK")
                || n.equals("EMERALD_BLOCK") || n.contains("COPPER_BLOCK") || n.contains("ANVIL")
                || n.contains("LODESTONE")) {
            return 2.2;
        }
        // Everything else — stone, dirt, ore, masonry — sinks in water and rides on lava, because lava's
        // buoyancy of 2 beats their 1. That is the vanilla intuition, and it falls out rather than being
        // special-cased.
        return TABLE.defaultValue();
    }
}
