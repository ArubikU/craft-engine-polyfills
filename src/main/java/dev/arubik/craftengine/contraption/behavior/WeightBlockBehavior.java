/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.behavior.BlockBehavior
 *  net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
 *  net.momirealms.craftengine.core.plugin.config.ConfigSection
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Material
 *  org.bukkit.craftbukkit.util.CraftMagicNumbers
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.block.behavior.FanBlockBehavior;
import dev.arubik.craftengine.block.behavior.RedstoneController;
import dev.arubik.craftengine.block.behavior.RedstoneOperator;
import dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior;
import dev.arubik.craftengine.contraption.physics.BlockPropertyTable;
import dev.arubik.craftengine.fluid.behavior.FluidBlockTankBehavior;
import dev.arubik.craftengine.fluid.behavior.PipeBehavior;
import dev.arubik.craftengine.fluid.behavior.PumpBehavior;
import dev.arubik.craftengine.fluid.behavior.TankBlockBehavior;
import dev.arubik.craftengine.gas.behavior.CreativeGasTankBehavior;
import dev.arubik.craftengine.gas.behavior.GasPipeBehavior;
import dev.arubik.craftengine.gas.behavior.GasPumpBehavior;
import dev.arubik.craftengine.gas.behavior.GasTankBehavior;
import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Material;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

public class WeightBlockBehavior
extends BukkitBlockBehavior {
    public static final Key FACTORY_KEY = Key.of((String)"polyfills:weight_block");
    public static final Factory FACTORY = new Factory();
    public static final double DEFAULT_WEIGHT = 10.0;
    private final double weight;
    private static final BlockPropertyTable TABLE = new BlockPropertyTable("mass.yml", 1.0);
    public static final double PIPE_WEIGHT = 0.25;
    public static final double MACHINE_WEIGHT = 4.0;
    public static final double REDSTONE_WEIGHT = 1.0;

    public WeightBlockBehavior(BlockDefinition customBlock, double weight) {
        super(customBlock);
        this.weight = weight;
    }

    public double weight() {
        return this.weight;
    }

    public static double weightOf(BlockState state) {
        try {
            ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
            if (ce != null && !ce.isEmpty()) {
                WeightBlockBehavior behavior = (WeightBlockBehavior)(ce.behavior().getFirst(WeightBlockBehavior.class));
                if (behavior != null) {
                    return behavior.weight();
                }
                Double functional = WeightBlockBehavior.functionalWeight(ce);
                if (functional != null) {
                    return functional;
                }
            }
            return WeightBlockBehavior.vanillaWeight(state);
        }
        catch (Throwable t) {
            return 1.0;
        }
    }

    public static void loadTable() {
        TABLE.load();
    }

    private static Double functionalWeight(ImmutableBlockState ce) {
        BlockBehavior b = ce.behavior();
        BearingBlockBehavior bearing = (BearingBlockBehavior)(b.getFirst(BearingBlockBehavior.class));
        if (bearing != null && Key.of((String)"polyfills", (String)"phys").equals(bearing.type())) {
            return 0.0;
        }
        if (b.getFirst(GasPipeBehavior.class) != null || b.getFirst(PipeBehavior.class) != null) {
            return 0.25;
        }
        if (b.getFirst(MachineBlockBehavior.class) != null || b.getFirst(GasTankBehavior.class) != null || b.getFirst(CreativeGasTankBehavior.class) != null || b.getFirst(GasPumpBehavior.class) != null || b.getFirst(PumpBehavior.class) != null || b.getFirst(TankBlockBehavior.class) != null || b.getFirst(FluidBlockTankBehavior.class) != null || b.getFirst(FanBlockBehavior.class) != null) {
            return 4.0;
        }
        if (b.getFirst(RedstoneController.class) != null || b.getFirst(RedstoneOperator.class) != null) {
            return 1.0;
        }
        return null;
    }

    private static double vanillaWeight(BlockState state) {
        Material m;
        try {
            m = CraftMagicNumbers.getMaterial((Block)state.getBlock());
        }
        catch (Throwable t) {
            return 1.0;
        }
        if (m == null || m.isAir()) {
            return 0.5;
        }
        Double override = TABLE.override(m);
        if (override != null) {
            return override;
        }
        String n = m.name();
        return WeightBlockBehavior.familyWeight(n) * WeightBlockBehavior.shapeFactor(n);
    }

    private static double shapeFactor(String n) {
        if (n.endsWith("_SLAB")) {
            return 0.5;
        }
        if (n.endsWith("_STAIRS")) {
            return 0.75;
        }
        if (n.endsWith("_WALL") || n.endsWith("_FENCE") || n.endsWith("_FENCE_GATE") || n.endsWith("_PANE") || n.endsWith("_BARS") || n.endsWith("_TRAPDOOR") || n.endsWith("_DOOR")) {
            return 0.4;
        }
        return 1.0;
    }

    private static double familyWeight(String n) {
        if (n.equals("TNT")) {
            return 1.0;
        }
        if (n.endsWith("_CARPET") || n.contains("SAPLING") || n.contains("FLOWER") || n.contains("LEAVES") || n.contains("VINE") || n.contains("GRASS") && !n.equals("GRASS_BLOCK") || n.contains("FERN") || n.contains("TORCH") || n.contains("BANNER") || n.contains("BUTTON")) {
            return 1.0;
        }
        if (n.endsWith("_WOOL") || n.contains("SNOW") || n.contains("HAY") || n.contains("SPONGE") || n.contains("GLASS") || n.contains("ICE") && !n.equals("PACKED_ICE") && !n.equals("BLUE_ICE")) {
            return 3.0;
        }
        if (n.contains("PLANKS") || n.endsWith("_LOG") || n.endsWith("_WOOD") || n.contains("BAMBOO") || n.contains("STEM") || n.contains("HYPHAE") || n.contains("STAIRS") && n.contains("OAK")) {
            return 6.0;
        }
        if (n.contains("DIRT") || n.equals("GRASS_BLOCK") || n.contains("SAND") || n.contains("GRAVEL") || n.contains("CLAY") || n.contains("SOUL") || n.contains("MUD") || n.contains("MYCELIUM")) {
            return 8.0;
        }
        if (n.contains("NETHERITE") || n.contains("ANCIENT_DEBRIS")) {
            return 45.0;
        }
        if (n.contains("OBSIDIAN") || n.equals("IRON_BLOCK") || n.equals("GOLD_BLOCK") || n.equals("DIAMOND_BLOCK") || n.equals("EMERALD_BLOCK") || n.contains("COPPER_BLOCK") || n.contains("ANVIL") || n.contains("LODESTONE") || n.contains("RESPAWN_ANCHOR")) {
            return 30.0;
        }
        if (n.contains("REDSTONE") && !n.equals("REDSTONE_BLOCK") || n.contains("REPEATER") || n.contains("COMPARATOR") || n.contains("OBSERVER") || n.contains("PISTON") || n.contains("DISPENSER") || n.contains("DROPPER") || n.contains("HOPPER") || n.contains("LEVER") || n.contains("TRIPWIRE") || n.equals("TARGET") || n.equals("NOTE_BLOCK") || n.contains("RAIL") || n.contains("PRESSURE_PLATE")) {
            return 2.0;
        }
        if (n.contains("STONE") || n.contains("COBBLE") || n.contains("DEEPSLATE") || n.contains("BRICK") || n.contains("CONCRETE") || n.contains("BLACKSTONE") || n.contains("BASALT") || n.contains("ORE") || n.contains("PRISMARINE") || n.contains("QUARTZ") || n.contains("TERRACOTTA")) {
            return 15.0;
        }
        return TABLE.defaultValue();
    }

    public static class Factory
    implements BlockBehaviorFactory<BlockBehavior> {
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            double weight = Double.parseDouble(arguments.getOrDefault("weight", 10.0).toString());
            return new WeightBlockBehavior(block, weight);
        }
    }
}

