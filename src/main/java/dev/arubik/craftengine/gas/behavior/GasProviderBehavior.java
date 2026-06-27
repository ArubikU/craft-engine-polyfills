package dev.arubik.craftengine.gas.behavior;

import dev.arubik.craftengine.gas.GasType;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;

/**
 * Marks a block as a GAS VEIN NODE: a passive source that a gas pump (and a Pressurizer Well) can read.
 * Fully data-driven — the gas type and the extraction-point value live in config, so any block (not just
 * nitrogenated cal) can join the same extraction system, and new gases need no code change.
 *
 * <pre>
 * behavior:
 *   type: polyfills:gas_provider
 *   gas: nitrogen          # GasType name (default nitrogen)
 *   extraction_points: 1.0 # points this block contributes to its vein (default 1.0)
 * </pre>
 *
 * The pump flood-fills the orthogonally-connected vein of providers SHARING THE SAME gas, sums their
 * points, and draws {@code min(cap, points)} worth of that gas per operation.
 */
public class GasProviderBehavior extends BukkitBlockBehavior {

    public static final Factory FACTORY = new Factory();

    private final GasType gas;
    private final double extractionPoints;

    public GasProviderBehavior(BlockDefinition block, GasType gas, double extractionPoints) {
        super(block);
        this.gas = gas == null ? GasType.EMPTY : gas;
        this.extractionPoints = Math.max(0.0, extractionPoints);
    }

    public GasType gasType() {
        return gas;
    }

    public double extractionPoints() {
        return extractionPoints;
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            GasType gas = GasType.fromName(String.valueOf(arguments.getOrDefault("gas", "nitrogen")));
            double pts = Double.parseDouble(String.valueOf(arguments.getOrDefault("extraction_points", 1.0)));
            return new GasProviderBehavior(block, gas, pts);
        }
    }
}
