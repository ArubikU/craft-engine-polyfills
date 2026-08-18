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
package dev.arubik.craftengine.gas;

import dev.arubik.craftengine.gas.GasCarrier;
import dev.arubik.craftengine.gas.GasStack;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

public class GasTransferHelper {
    private GasTransferHelper() {
    }

    public static int transfer(Level level, BlockPos from, BlockPos to, int maxAmount, int pressureDecay) {
        Direction direction;
        if (level == null || from == null || to == null || maxAmount <= 0) {
            return 0;
        }
        Optional<GasCarrier> sourceCarrier = GasTransferHelper.getCarrier(level, from);
        Optional<GasCarrier> targetCarrier = GasTransferHelper.getCarrier(level, to);
        if (!sourceCarrier.isPresent() || !targetCarrier.isPresent()) {
            return 0;
        }
        int[] transferred = new int[]{0};
        Direction mcDirection = direction = GasTransferHelper.getDirection(from, to);
        sourceCarrier.get().extractGas(level, from, maxAmount, extracted -> {
            int inserted;
            if (extracted.isEmpty()) {
                return;
            }
            GasStack decayed = new GasStack(extracted.getType(), extracted.getAmount(), Math.max(0, extracted.getPressure() - pressureDecay));
            transferred[0] = inserted = ((GasCarrier)targetCarrier.get()).insertGas(level, to, decayed, mcDirection.getOpposite());
            int unused = extracted.getAmount() - inserted;
            if (unused > 0) {
                GasStack remaining = new GasStack(extracted.getType(), unused, extracted.getPressure());
                ((GasCarrier)sourceCarrier.get()).insertGas(level, from, remaining, mcDirection);
            }
        }, mcDirection);
        return transferred[0];
    }

    public static Optional<GasCarrier> getCarrier(Level level, BlockPos pos) {
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
        return Optional.ofNullable((GasCarrier)state.behavior().getFirst(GasCarrier.class));
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

