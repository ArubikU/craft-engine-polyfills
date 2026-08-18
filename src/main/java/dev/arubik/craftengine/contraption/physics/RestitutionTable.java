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

public final class RestitutionTable {
    public static final double DEFAULT_RESTITUTION = 0.0;
    private static final BlockPropertyTable TABLE = new BlockPropertyTable("restitution.yml", 0.0);

    private RestitutionTable() {
    }

    public static void load() {
        TABLE.load();
    }

    public static double vanillaRestitution(BlockState state) {
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
        if (m.name().equals("SLIME_BLOCK")) {
            return 1.0;
        }
        return TABLE.defaultValue();
    }
}

