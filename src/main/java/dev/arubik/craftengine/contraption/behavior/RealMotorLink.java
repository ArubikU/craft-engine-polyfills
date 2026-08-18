/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.Level
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.rotation.RpmProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;

public final class RealMotorLink {
    private RealMotorLink() {
    }

    public static BlockPos findAdjacentMotor(Level level, BlockPos bearingPos) {
        BlockPos best = null;
        float bestPotential = 0.0f;
        for (Direction d : Direction.values()) {
            RpmProvider provider;
            BlockEntityController blockEntityController;
            BlockPos neighborPos = bearingPos.relative(d);
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, neighborPos);
            if (be == null || !((blockEntityController = be.controller) instanceof RpmProvider) || !(provider = (RpmProvider)blockEntityController).isRpmSource() || !(provider.potentialRpm() > bestPotential)) continue;
            bestPotential = provider.potentialRpm();
            best = neighborPos.immutable();
        }
        return best;
    }

    public static float currentRpm(Level level, BlockPos motorPos) {
        BlockEntityController blockEntityController;
        if (level == null || motorPos == null) {
            return 0.0f;
        }
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, motorPos);
        if (be != null && (blockEntityController = be.controller) instanceof RpmProvider) {
            RpmProvider provider = (RpmProvider)blockEntityController;
            return provider.getRpm();
        }
        return 0.0f;
    }

    public static void reportStressLoad(Level level, BlockPos motorPos, float su) {
        BlockEntityController blockEntityController;
        if (level == null || motorPos == null) {
            return;
        }
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, motorPos);
        if (be != null && (blockEntityController = be.controller) instanceof RpmProvider) {
            RpmProvider provider = (RpmProvider)blockEntityController;
            provider.reportStressLoad(su);
        }
    }
}

