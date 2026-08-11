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
 * REAL CraftEngine block behavior for a contraption "weight" / ballast block (roadmap item #9 —
 * PhysContraption, {@code .migration/ROADMAP-claims-and-phys.md} §2 "Mass / weight aggregation
 * model"). It is a near-verbatim clone of {@link MoverBlockBehavior}/{@link MinerBlockBehavior}'s
 * shape (a {@code BukkitBlockBehavior} subclass + a {@link Factory} reading a {@link ConfigSection},
 * registered in {@code block.BlockBehaviors.register()} under {@link #FACTORY_KEY}) — but far
 * simpler: it has no {@code facing}, no block entity/controller, and no {@link RpmConsumer} life.
 *
 * <p><b>Data, not kinematics.</b> Unlike the miner (which auto-attaches a {@link MinerBehavior}) and
 * the mover (which auto-attaches a {@link MoverBehavior}), a weight block does <em>not</em> register
 * itself with {@code MovementBehaviorRegistry} and contributes NO {@link dev.arubik.craftengine
 * .contraption.MovementBehavior} of its own. Its sole job is to carry a {@code weight:} config value
 * that the physics integrator ({@link PhysicsBehavior}) aggregates: a contraption's total mass and
 * center of mass are summed once from every captured weight block via {@link MassModel#of}, which
 * reads each captured cell's weight through {@link #weightOf(BlockState)}. A heavier ballast block
 * therefore shifts the center of mass and raises the total mass without ever becoming a competing
 * movement source — exactly the "weight is a captured-block PROPERTY" model the roadmap describes,
 * mirroring how the miner made <em>mining</em> a captured-block property.
 *
 * <p>Config fields (under {@code behavior:}), single field:
 * <pre>
 *   weight: 10.0   # mass units this block contributes to the contraption's mass model
 * </pre>
 *
 * <p><b>Milestone-1 scope.</b> For the first PhysContraption milestone (gravity + falling + ground
 * collision, NO torque/rotation) the aggregated total mass does not yet change the fall itself — a
 * rigid body in free fall accelerates independently of its mass — so weight blocks are, honestly,
 * not yet mechanically load-bearing this milestone. They are wired end-to-end now (block behavior +
 * aggregation + a computed center of mass) so the torque milestone, which DOES need mass/COM/inertia,
 * has the data model already in place and validated. See {@link MassModel} and {@link PhysicsBehavior}.
 */
public class WeightBlockBehavior extends BukkitBlockBehavior {

    /** Registration key for {@code block.BlockBehaviors#register()}. */
    public static final Key FACTORY_KEY = Key.of("polyfills:weight_block");

    public static final Factory FACTORY = new Factory();

    /** Default mass a weight block contributes when its config omits {@code weight:}. */
    public static final double DEFAULT_WEIGHT = 10.0;

    private final double weight;

    public WeightBlockBehavior(BlockDefinition customBlock, double weight) {
        super(customBlock);
        this.weight = weight;
    }

    /** The mass (in the contraption mass model's units) this weight block contributes. */
    public double weight() {
        return weight;
    }

    /**
     * The mass a single captured {@code state} contributes to a contraption's mass model: a custom
     * {@code polyfills:weight_block}'s configured {@code weight:}, else {@link MassModel#BASELINE_BLOCK_MASS}
     * (so every captured block — plain vanilla or otherwise — carries a nonzero baseline mass, and a
     * contraption with zero weight blocks still has a sensible total). Fail-open in the exact same
     * shape {@link MoverBlockBehavior#buildMovementBehavior}/{@link MinerBlockBehavior#buildMovementBehavior}
     * use: any non-CE / unresolvable state falls back to the baseline rather than throwing.
     */
    public static double weightOf(BlockState state) {
        try {
            ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
            if (ce != null && !ce.isEmpty()) {
                WeightBlockBehavior behavior = ce.behavior().getFirst(WeightBlockBehavior.class);
                if (behavior != null) {
                    return behavior.weight(); // explicit ballast block — its configured weight wins
                }
                // Balanced weights for the functional custom blocks (2026-07-18 — "balancea bien todas las
                // máquinas y redstone para que no pesen mucho ... a las pipes ponles 0.25"). Detected by
                // behavior so every current AND future machine/pipe/redstone block is covered, not a hardcoded
                // id list. Before this they fell to the vanilla stone-family weight (15), which made a fan+tank
                // thruster far too heavy to fly.
                Double functional = functionalWeight(ce);
                if (functional != null) {
                    return functional;
                }
            }
            // Vanilla (or any non-weight custom) block: use the material weight table below (2026-07-04
            // — "agrega una weight table a los bloques vanilla") so a contraption's mass/COM reflects
            // what it's actually built from (a stone hull is heavy, a wool sail is light) instead of a
            // flat baseline for everything.
            return vanillaWeight(state);
        } catch (Throwable t) {
            return MassModel.BASELINE_BLOCK_MASS;
        }
    }

    /**
     * A material-family weight table for vanilla blocks (2026-07-04 request). Maps the block to its
     * Bukkit {@link org.bukkit.Material} and buckets it by family — dense metals/obsidian are heavy,
     * stone/ore blocks medium-heavy, wood/dirt medium, cloth/leaves/plants light — so the aggregate
     * {@link MassModel} (total mass + center of mass) is physically meaningful. Anything unrecognized
     * falls back to {@link MassModel#BASELINE_BLOCK_MASS}. Deliberately coarse (name-family buckets,
     * not a per-block census) — the physics only needs relative mass distribution, not real kilograms.
     */
    /** Owner overrides for the built-in table below — see {@code mass.yml} and {@link dev.arubik.craftengine.contraption.physics.BlockPropertyTable}. */
    private static final dev.arubik.craftengine.contraption.physics.BlockPropertyTable TABLE =
            new dev.arubik.craftengine.contraption.physics.BlockPropertyTable("mass.yml", MassModel.BASELINE_BLOCK_MASS);

    /** Loads {@code mass.yml}. Called once on enable. */
    public static void loadTable() {
        TABLE.load();
    }

    /** Balanced weight for a functional custom block: pipes barely weigh anything, machines/tanks are light, redstone control is light. */
    public static final double PIPE_WEIGHT = 0.25;
    public static final double MACHINE_WEIGHT = 4.0;
    public static final double REDSTONE_WEIGHT = 1.0;

    /**
     * A balanced weight for a functional custom block by its BEHAVIOR, or {@code null} if it is not one (fall
     * through to the material table). Pipes/carriers are near-weightless (they're thin conduits); machines,
     * tanks and pumps are light so a fan-driven contraption can actually move; redstone control blocks are
     * light too. Behavior-based so a new machine/pipe added later needs no change here.
     */
    private static Double functionalWeight(ImmutableBlockState ce) {
        var b = ce.behavior();
        // The PHYS anchor is the weightless pivot the whole structure hangs off — it must not add mass of its
        // own (2026-07-18 — "reduce el peso del phys anchor a 0"). Other bearing kinds keep their block weight.
        var bearing = b.getFirst(dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.class);
        if (bearing != null && net.momirealms.craftengine.core.util.Key.of("polyfills", "phys").equals(bearing.type())) {
            return 0.0;
        }
        if (b.getFirst(dev.arubik.craftengine.gas.behavior.GasPipeBehavior.class) != null
                || b.getFirst(dev.arubik.craftengine.fluid.behavior.PipeBehavior.class) != null) {
            return PIPE_WEIGHT;
        }
        if (b.getFirst(dev.arubik.craftengine.machine.block.MachineBlockBehavior.class) != null
                || b.getFirst(dev.arubik.craftengine.gas.behavior.GasTankBehavior.class) != null
                || b.getFirst(dev.arubik.craftengine.gas.behavior.CreativeGasTankBehavior.class) != null
                || b.getFirst(dev.arubik.craftengine.gas.behavior.GasPumpBehavior.class) != null
                || b.getFirst(dev.arubik.craftengine.fluid.behavior.PumpBehavior.class) != null
                || b.getFirst(dev.arubik.craftengine.fluid.behavior.TankBlockBehavior.class) != null
                || b.getFirst(dev.arubik.craftengine.fluid.behavior.FluidBlockTankBehavior.class) != null
                || b.getFirst(dev.arubik.craftengine.block.behavior.FanBlockBehavior.class) != null) {
            return MACHINE_WEIGHT;
        }
        if (b.getFirst(dev.arubik.craftengine.block.behavior.RedstoneController.class) != null
                || b.getFirst(dev.arubik.craftengine.block.behavior.RedstoneOperator.class) != null) {
            return REDSTONE_WEIGHT;
        }
        return null;
    }

    private static double vanillaWeight(BlockState state) {
        org.bukkit.Material m;
        try {
            m = org.bukkit.craftbukkit.util.CraftMagicNumbers.getMaterial(state.getBlock());
        } catch (Throwable t) {
            return MassModel.BASELINE_BLOCK_MASS;
        }
        if (m == null || m.isAir()) {
            return 0.5; // air / no block — negligible
        }
        Double override = TABLE.override(m);
        if (override != null) {
            return override; // the owner's mass.yml wins over the built-in family bucket below
        }
        String n = m.name();
        // Scale by the block's actual VOLUME — a stone slab must not weigh the same as a full stone block
        // (2026-07-18 — "bájale el peso a las slabs ... no pueden pesar igual que un bloque"). Applied on top of
        // the material family below, so it works for every material's slab/stairs/wall/fence/pane.
        return familyWeight(n) * shapeFactor(n);
    }

    /** How much of a full block the shape actually fills — slabs half, stairs three-quarters, thin connectors less. */
    private static double shapeFactor(String n) {
        if (n.endsWith("_SLAB")) {
            return 0.5;
        }
        if (n.endsWith("_STAIRS")) {
            return 0.75;
        }
        if (n.endsWith("_WALL") || n.endsWith("_FENCE") || n.endsWith("_FENCE_GATE") || n.endsWith("_PANE")
                || n.endsWith("_BARS") || n.endsWith("_TRAPDOOR") || n.endsWith("_DOOR")) {
            return 0.4;
        }
        return 1.0;
    }

    /** The full-block weight for a material by name — the family table; scaled to the real shape by {@link #shapeFactor}. */
    private static double familyWeight(String n) {
        // TNT — a hollow block of powder, kept light (2026-07-18 — "bájale al TNT a 1, pesa mucho"), so a
        // bomb contraption isn't dragged down by its own payload.
        if (n.equals("TNT")) {
            return 1.0;
        }
        // Very light: cloth, plants, decoration.
        if (n.endsWith("_CARPET") || n.contains("SAPLING") || n.contains("FLOWER") || n.contains("LEAVES")
                || n.contains("VINE") || n.contains("GRASS") && !n.equals("GRASS_BLOCK") || n.contains("FERN")
                || n.contains("TORCH") || n.contains("BANNER") || n.contains("BUTTON")) {
            return 1.0;
        }
        if (n.endsWith("_WOOL") || n.contains("SNOW") || n.contains("HAY") || n.contains("SPONGE")
                || n.contains("GLASS") || n.contains("ICE") && !n.equals("PACKED_ICE") && !n.equals("BLUE_ICE")) {
            return 3.0;
        }
        // Wood family.
        if (n.contains("PLANKS") || n.endsWith("_LOG") || n.endsWith("_WOOD") || n.contains("BAMBOO")
                || n.contains("STEM") || n.contains("HYPHAE") || n.contains("STAIRS") && n.contains("OAK")) {
            return 6.0;
        }
        // Loose earth.
        if (n.contains("DIRT") || n.equals("GRASS_BLOCK") || n.contains("SAND") || n.contains("GRAVEL")
                || n.contains("CLAY") || n.contains("SOUL") || n.contains("MUD") || n.contains("MYCELIUM")) {
            return 8.0;
        }
        // Heaviest: dense metals / netherite / obsidian.
        if (n.contains("NETHERITE") || n.contains("ANCIENT_DEBRIS")) {
            return 45.0;
        }
        if (n.contains("OBSIDIAN") || n.equals("IRON_BLOCK") || n.equals("GOLD_BLOCK") || n.equals("DIAMOND_BLOCK")
                || n.equals("EMERALD_BLOCK") || n.contains("COPPER_BLOCK") || n.contains("ANVIL")
                || n.contains("LODESTONE") || n.contains("RESPAWN_ANCHOR")) {
            return 30.0;
        }
        // Redstone / mechanism components — light control gear, NOT ballast (2026-07-18 — "redstone para que
        // no pese mucho"). Checked BEFORE the stone family because "REDSTONE_WIRE"/"REDSTONE_TORCH" contain
        // "STONE" and would otherwise be classed as heavy masonry. REDSTONE_BLOCK is deliberately excluded — a
        // solid block of redstone IS ballast, and falls through to the metal/stone buckets.
        if ((n.contains("REDSTONE") && !n.equals("REDSTONE_BLOCK")) || n.contains("REPEATER")
                || n.contains("COMPARATOR") || n.contains("OBSERVER") || n.contains("PISTON")
                || n.contains("DISPENSER") || n.contains("DROPPER") || n.contains("HOPPER")
                || n.contains("LEVER") || n.contains("TRIPWIRE") || n.equals("TARGET") || n.equals("NOTE_BLOCK")
                || n.contains("RAIL") || n.contains("PRESSURE_PLATE")) {
            return 2.0;
        }
        // Stone / ore / masonry family — the default "heavy building material".
        if (n.contains("STONE") || n.contains("COBBLE") || n.contains("DEEPSLATE") || n.contains("BRICK")
                || n.contains("CONCRETE") || n.contains("BLACKSTONE") || n.contains("BASALT") || n.contains("ORE")
                || n.contains("PRISMARINE") || n.contains("QUARTZ") || n.contains("TERRACOTTA")) {
            return 15.0;
        }
        return TABLE.defaultValue(); // unclassified — mass.yml's `default:`
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            double weight = Double
                    .parseDouble(arguments.getOrDefault("weight", Double.valueOf(DEFAULT_WEIGHT)).toString());
            return new WeightBlockBehavior(block, weight);
        }
    }
}
