/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.BlockState
 *  net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.behavior.BlockBehavior
 *  net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
 *  net.momirealms.craftengine.core.plugin.config.ConfigSection
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.physics.RestitutionTable;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

public class RestitutionBlockBehavior
extends BukkitBlockBehavior {
    public static final Key FACTORY_KEY = Key.of((String)"polyfills:restitution_block");
    public static final Factory FACTORY = new Factory();
    public static final double DEFAULT_RESTITUTION = 0.0;
    private final double restitution;

    public RestitutionBlockBehavior(BlockDefinition customBlock, double restitution) {
        super(customBlock);
        this.restitution = restitution;
    }

    public double restitution() {
        return this.restitution;
    }

    public static double restitutionOf(BlockState state) {
        try {
            RestitutionBlockBehavior behavior;
            ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
            if (ce != null && !ce.isEmpty() && (behavior = (RestitutionBlockBehavior)(ce.behavior().getFirst(RestitutionBlockBehavior.class))) != null) {
                return behavior.restitution();
            }
            return RestitutionTable.vanillaRestitution(state);
        }
        catch (Throwable t) {
            return 0.0;
        }
    }

    public static class Factory
    implements BlockBehaviorFactory<BlockBehavior> {
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            double restitution = Double.parseDouble(arguments.getOrDefault("restitution", 0.0).toString());
            return new RestitutionBlockBehavior(block, restitution);
        }
    }
}

