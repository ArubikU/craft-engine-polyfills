/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.arubik.craftengine.contraption.physics;

import dev.arubik.craftengine.contraption.behavior.FrictionBlockBehavior;
import dev.arubik.craftengine.contraption.behavior.WeightBlockBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public record FrictionModel(double friction) {
    public static final FrictionModel EMPTY = new FrictionModel(0.7);

    public static FrictionModel of(ContraptionLevel level) {
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
            moment += w * FrictionBlockBehavior.frictionOf(state);
        }
        return mass <= 0.0 ? EMPTY : new FrictionModel(moment / mass);
    }
}

