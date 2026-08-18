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

public final class FrictionTable {
    public static final double DEFAULT_FRICTION = 0.7;
    private static final BlockPropertyTable TABLE = new BlockPropertyTable("friction.yml", 0.7);

    private FrictionTable() {
    }

    public static void load() {
        TABLE.load();
    }

    public static double vanillaFriction(BlockState state) {
        Material m;
        try {
            m = CraftMagicNumbers.getMaterial((Block)state.getBlock());
        }
        catch (Throwable t) {
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
        if (n.contains("ICE")) {
            return 0.05;
        }
        if (n.contains("SLIME")) {
            return 0.4;
        }
        if (n.contains("HONEY")) {
            return 1.5;
        }
        if (n.contains("SOUL_SAND") || n.contains("SOUL_SOIL")) {
            return 1.2;
        }
        return TABLE.defaultValue();
    }
}

