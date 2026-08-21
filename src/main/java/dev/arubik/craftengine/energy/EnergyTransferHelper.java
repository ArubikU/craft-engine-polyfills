package dev.arubik.craftengine.energy;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

/** Discovers {@link EnergyCarrier}s at a position, mirroring {@code GasTransferHelper}. */
public final class EnergyTransferHelper {

    private EnergyTransferHelper() {
    }

    public static Optional<EnergyCarrier> getCarrier(Level level, BlockPos pos) {
        if (level == null || pos == null)
            return Optional.empty();
        if (!level.hasChunkAt(pos))
            return Optional.empty(); // an unguarded read here would force-load the chunk
        ImmutableBlockState state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos))
                .orElse(null);
        if (state == null || state.isEmpty())
            return Optional.empty();
        return Optional.ofNullable(state.behavior().getFirst(EnergyCarrier.class));
    }

    public static BlockPos offset(BlockPos pos, Direction dir) {
        return switch (dir) {
            case UP -> pos.above();
            case DOWN -> pos.below();
            case NORTH -> pos.north();
            case SOUTH -> pos.south();
            case EAST -> pos.east();
            case WEST -> pos.west();
        };
    }

    /** Direct transfer between two known carriers, e.g. from a creative/infinite source. */
    public static int transfer(Level level, BlockPos from, BlockPos to, int maxAmount) {
        if (level == null || from == null || to == null || maxAmount <= 0)
            return 0;
        Optional<EnergyCarrier> source = getCarrier(level, from);
        Optional<EnergyCarrier> target = getCarrier(level, to);
        if (source.isEmpty() || target.isEmpty())
            return 0;
        Direction dir = direction(from, to);
        int extracted = source.get().extractEnergy(level, from, maxAmount, dir);
        if (extracted <= 0)
            return 0;
        int inserted = target.get().insertEnergy(level, to, extracted, dir.getOpposite());
        int unused = extracted - inserted;
        if (unused > 0)
            source.get().insertEnergy(level, from, unused, dir);
        return inserted;
    }

    private static Direction direction(BlockPos from, BlockPos to) {
        if (from.getX() < to.getX())
            return Direction.EAST;
        if (from.getX() > to.getX())
            return Direction.WEST;
        if (from.getY() < to.getY())
            return Direction.UP;
        if (from.getY() > to.getY())
            return Direction.DOWN;
        if (from.getZ() < to.getZ())
            return Direction.SOUTH;
        if (from.getZ() > to.getZ())
            return Direction.NORTH;
        return Direction.UP;
    }
}
