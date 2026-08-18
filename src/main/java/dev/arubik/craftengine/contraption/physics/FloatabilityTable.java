/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  org.bukkit.Material
 *  org.bukkit.craftbukkit.util.CraftMagicNumbers
 */
package dev.arubik.craftengine.contraption.physics;

import dev.arubik.craftengine.contraption.physics.BlockPropertyTable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Material;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

public final class FloatabilityTable {
    public static final double DEFAULT_FLOATABILITY = 1.0;
    private static final BlockPropertyTable TABLE = new BlockPropertyTable("floatability.yml", 1.0);

    private FloatabilityTable() {
    }

    public static void load() {
        TABLE.load();
    }

    public static double vanillaFloatability(BlockState state) {
        Material m;
        try {
            m = CraftMagicNumbers.getMaterial((Block)state.getBlock());
        }
        catch (Throwable t) {
            return TABLE.defaultValue();
        }
        if (m == null || m.isAir()) {
            return 0.0;
        }
        Double override = TABLE.override(m);
        if (override != null) {
            return override;
        }
        String n = m.name();
        if (n.contains("PLANKS") || n.endsWith("_LOG") || n.endsWith("_WOOD") || n.contains("BAMBOO") || n.contains("STEM") || n.contains("HYPHAE") || n.contains("BARREL") || n.contains("CHEST") && !n.contains("ENDER")) {
            return 0.0;
        }
        if (n.endsWith("_WOOL") || n.endsWith("_CARPET") || n.contains("LEAVES") || n.contains("HAY") || n.contains("SPONGE") || n.contains("BAMBOO") || n.contains("SCAFFOLDING")) {
            return 0.0;
        }
        if (n.contains("ICE") || n.contains("GLASS")) {
            return 0.8;
        }
        if (n.contains("NETHERITE") || n.contains("ANCIENT_DEBRIS")) {
            return 2.5;
        }
        if (n.contains("OBSIDIAN") || n.equals("IRON_BLOCK") || n.equals("GOLD_BLOCK") || n.equals("DIAMOND_BLOCK") || n.equals("EMERALD_BLOCK") || n.contains("COPPER_BLOCK") || n.contains("ANVIL") || n.contains("LODESTONE")) {
            return 2.2;
        }
        return TABLE.defaultValue();
    }
}

