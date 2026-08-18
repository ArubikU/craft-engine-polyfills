/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.arubik.craftengine.contraption.physics;

import dev.arubik.craftengine.contraption.behavior.FloatabilityBlockBehavior;
import dev.arubik.craftengine.contraption.behavior.WeightBlockBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public record FloatabilityModel(double floatability) {
    public static final FloatabilityModel EMPTY = new FloatabilityModel(1.0);

    public static FloatabilityModel of(ContraptionLevel level) {
        if (level == null) {
            return EMPTY;
        }
        double mass = 0.0;
        double moment = 0.0;
        for (BlockPos local : level.localPositions()) {
            BlockState state = level.getBlockState(local);
            if (state.isAir()) continue;
            double w = WeightBlockBehavior.weightOf(state);
            mass += w;
            moment += w * FloatabilityBlockBehavior.floatabilityOf(state);
        }
        return mass <= 0.0 ? EMPTY : new FloatabilityModel(moment / mass);
    }
}

