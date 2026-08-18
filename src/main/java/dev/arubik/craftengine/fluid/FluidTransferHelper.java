/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.Level
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 */
package dev.arubik.craftengine.fluid;

import dev.arubik.craftengine.fluid.behavior.FluidCarrier;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

public class FluidTransferHelper {
    private FluidTransferHelper() {
    }

    public static Optional<FluidCarrier> getCarrier(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return Optional.empty();
        }
        if (!level.hasChunkAt(pos)) {
            return Optional.empty();
        }
        ImmutableBlockState state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (state == null || state.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable((FluidCarrier)state.behavior().getFirst(FluidCarrier.class));
    }

    public static BlockPos offset(BlockPos pos, Direction dir) {
        return switch (dir) {
            default -> throw new MatchException(null, null);
            case Direction.UP -> pos.above();
            case Direction.DOWN -> pos.below();
            case Direction.NORTH -> pos.north();
            case Direction.SOUTH -> pos.south();
            case Direction.EAST -> pos.east();
            case Direction.WEST -> pos.west();
        };
    }

    public static boolean wouldCreateLoop(String history, BlockPos newPos) {
        if (history == null || history.isEmpty()) {
            return false;
        }
        String tok = newPos.getX() + "," + newPos.getY() + "," + newPos.getZ();
        return (";" + history + ";").contains(";" + tok + ";");
    }

    public static String updateHistory(String history, BlockPos newPos) {
        String tok = newPos.getX() + "," + newPos.getY() + "," + newPos.getZ();
        if (history == null || history.isEmpty()) {
            return tok;
        }
        int semi = history.lastIndexOf(59);
        String last = semi < 0 ? history : history.substring(semi + 1);
        return last + ";" + tok;
    }

    private static Direction getDirection(BlockPos from, BlockPos to) {
        if (from.getX() < to.getX()) {
            return Direction.EAST;
        }
        if (from.getX() > to.getX()) {
            return Direction.WEST;
        }
        if (from.getY() < to.getY()) {
            return Direction.UP;
        }
        if (from.getY() > to.getY()) {
            return Direction.DOWN;
        }
        if (from.getZ() < to.getZ()) {
            return Direction.SOUTH;
        }
        if (from.getZ() > to.getZ()) {
            return Direction.NORTH;
        }
        return Direction.UP;
    }
}

