/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.Container
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.HopperBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.HopperBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.ContraptionAccessor;
import dev.arubik.craftengine.contraption.ContraptionWorlds;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public final class ContraptionHopperBridge {
    private static final int TRANSFER_COOLDOWN = 8;
    private static final Map<ContraptionLevel, Map<Long, Integer>> COOLDOWNS = new WeakHashMap<ContraptionLevel, Map<Long, Integer>>();

    private ContraptionHopperBridge() {
    }

    public static void tick(ContraptionEntity entity) {
        Level level;
        if (entity == null) {
            return;
        }
        ContraptionLevel level2 = entity.state().level();
        if (level2 == null || !((level = level2.realLevel()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel realLevel = (ServerLevel)level;
        Map<Long, Integer> cooldowns = COOLDOWNS.computeIfAbsent(level2, k -> new HashMap());
        boolean docked = ContraptionWorlds.isGridAligned(level2);
        for (BlockPos local : level2.localPositions()) {
            BlockEntity be;
            try {
                be = level2.getBlockEntity(local);
            }
            catch (Throwable t) {
                continue;
            }
            if (!(be instanceof HopperBlockEntity)) continue;
            HopperBlockEntity hopper = (HopperBlockEntity)be;
            long key = local.asLong();
            int cd = cooldowns.getOrDefault(key, 0);
            if (cd > 0) {
                cooldowns.put(key, cd - 1);
                continue;
            }
            if (!docked) continue;
            boolean moved = false;
            try {
                moved = ContraptionHopperBridge.tryTransfer(level2, realLevel, local, hopper);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (!moved) continue;
            cooldowns.put(key, 8);
        }
    }

    private static boolean tryTransfer(ContraptionLevel level, ServerLevel realLevel, BlockPos local, HopperBlockEntity hopper) {
        Container src;
        Container dest;
        BlockState state = level.getBlockState(local);
        if (!state.hasProperty((Property)HopperBlock.FACING)) {
            return false;
        }
        Direction localFacing = (Direction)state.getValue((Property)HopperBlock.FACING);
        Optional<BlockPos> outCell = ContraptionWorlds.realBlockNeighbor(level, local, localFacing);
        Optional<Direction> outHeading = ContraptionWorlds.realDirectionOf(level, localFacing);
        if (outCell.isPresent() && outHeading.isPresent() && (dest = ContraptionAccessor.realContainerAt(realLevel, outCell.get())) != null && dest != hopper && ContraptionAccessor.transferItem((Container)hopper, Direction.DOWN, dest, outHeading.get().getOpposite())) {
            return true;
        }
        Optional<BlockPos> inCell = ContraptionWorlds.realBlockNeighbor(level, local, Direction.UP);
        return inCell.isPresent() && (src = ContraptionAccessor.realContainerAt(realLevel, inCell.get())) != null && src != hopper && ContraptionAccessor.transferItem(src, Direction.DOWN, (Container)hopper, Direction.UP);
    }
}

