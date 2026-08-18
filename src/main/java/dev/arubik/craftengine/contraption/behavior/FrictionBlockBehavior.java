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

import dev.arubik.craftengine.contraption.physics.FrictionTable;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

public class FrictionBlockBehavior
extends BukkitBlockBehavior {
    public static final Key FACTORY_KEY = Key.of((String)"polyfills:friction_block");
    public static final Factory FACTORY = new Factory();
    public static final double DEFAULT_FRICTION = 0.7;
    private final double friction;

    public FrictionBlockBehavior(BlockDefinition customBlock, double friction) {
        super(customBlock);
        this.friction = friction;
    }

    public double friction() {
        return this.friction;
    }

    public static double frictionOf(BlockState state) {
        try {
            FrictionBlockBehavior behavior;
            ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
            if (ce != null && !ce.isEmpty() && (behavior = (FrictionBlockBehavior)(ce.behavior().getFirst(FrictionBlockBehavior.class))) != null) {
                return behavior.friction();
            }
            return FrictionTable.vanillaFriction(state);
        }
        catch (Throwable t) {
            return 0.7;
        }
    }

    public static class Factory
    implements BlockBehaviorFactory<BlockBehavior> {
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            double friction = Double.parseDouble(arguments.getOrDefault("friction", 0.7).toString());
            return new FrictionBlockBehavior(block, friction);
        }
    }
}

