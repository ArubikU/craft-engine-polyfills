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

import dev.arubik.craftengine.contraption.physics.FloatabilityTable;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

public class FloatabilityBlockBehavior
extends BukkitBlockBehavior {
    public static final Key FACTORY_KEY = Key.of((String)"polyfills:floatability_block");
    public static final Factory FACTORY = new Factory();
    public static final double DEFAULT_FLOATABILITY = 1.0;
    private final double floatability;

    public FloatabilityBlockBehavior(BlockDefinition customBlock, double floatability) {
        super(customBlock);
        this.floatability = floatability;
    }

    public double floatability() {
        return this.floatability;
    }

    public static double floatabilityOf(BlockState state) {
        try {
            FloatabilityBlockBehavior behavior;
            ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
            if (ce != null && !ce.isEmpty() && (behavior = (FloatabilityBlockBehavior)(ce.behavior().getFirst(FloatabilityBlockBehavior.class))) != null) {
                return behavior.floatability();
            }
            return FloatabilityTable.vanillaFloatability(state);
        }
        catch (Throwable t) {
            return 1.0;
        }
    }

    public static class Factory
    implements BlockBehaviorFactory<BlockBehavior> {
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            double floatability = Double.parseDouble(arguments.getOrDefault("floatability", 1.0).toString());
            return new FloatabilityBlockBehavior(block, floatability);
        }
    }
}

