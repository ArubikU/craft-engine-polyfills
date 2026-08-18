/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package dev.arubik.craftengine.contraption.render;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class ContraptionLightEmitters {
    private ContraptionLightEmitters() {
    }

    public static int withEmitterFalloff(ContraptionLevel level, BlockPos targetLocal, int ambientBlockLight) {
        if (level == null) {
            return ambientBlockLight;
        }
        int best = ambientBlockLight;
        try {
            for (BlockPos pos : level.localPositions()) {
                int dist;
                int contribution;
                BlockState state = level.getBlockState(pos);
                int emission = state.getLightEmission();
                if (emission <= 0 || (contribution = emission - (dist = ContraptionLightEmitters.chebyshevDistance(targetLocal, pos))) <= best) continue;
                best = contribution;
            }
        }
        catch (Throwable ignored) {
            return ambientBlockLight;
        }
        return Math.min(15, Math.max(0, best));
    }

    static BlockPos nearestBlockPos(Vec3 local) {
        return BlockPos.containing((double)local.x, (double)local.y, (double)local.z);
    }

    private static int chebyshevDistance(BlockPos a, BlockPos b) {
        return Math.max(Math.abs(a.getX() - b.getX()), Math.max(Math.abs(a.getY() - b.getY()), Math.abs(a.getZ() - b.getZ())));
    }
}

