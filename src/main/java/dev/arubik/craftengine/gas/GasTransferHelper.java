package dev.arubik.craftengine.gas;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

import java.util.Optional;

/**
 * Utility class for common gas transfer operations.
 * Reduces code duplication across GasPipeBehavior, GasPumpBehavior, and
 * GasValveBehavior.
 */
public class GasTransferHelper {

    private GasTransferHelper() {
    }

    /**
     * Attempt to transfer gas from source to target position.
     * 
     * @param level         The world level
     * @param from          Source position
     * @param to            Target position
     * @param maxAmount     Maximum amount to transfer (mb)
     * @param pressureDecay Pressure lost per transfer
     * @return Amount actually transferred (mb)
     */
    public static int transfer(Level level, BlockPos from, BlockPos to, int maxAmount, int pressureDecay) {
        if (level == null || from == null || to == null || maxAmount <= 0)
            return 0;

        Optional<GasCarrier> sourceCarrier = getCarrier(level, from);
        Optional<GasCarrier> targetCarrier = getCarrier(level, to);

        if (!sourceCarrier.isPresent() || !targetCarrier.isPresent())
            return 0;

        final int[] transferred = { 0 };

        Direction direction = getDirection(from, to);
        net.minecraft.core.Direction mcDirection = direction;

        sourceCarrier.get().extractGas(level, from, maxAmount, extracted -> {
            if (extracted.isEmpty())
                return;

            // Apply pressure decay
            GasStack decayed = new GasStack(
                    extracted.getType(),
                    extracted.getAmount(),
                    Math.max(0, extracted.getPressure() - pressureDecay));

            int inserted = targetCarrier.get().insertGas(level, to, decayed, mcDirection.getOpposite());
            transferred[0] = inserted;

            // Return unused gas to source
            int unused = extracted.getAmount() - inserted;
            if (unused > 0) {
                GasStack remaining = new GasStack(
                        extracted.getType(),
                        unused,
                        extracted.getPressure());
                sourceCarrier.get().insertGas(level, from, remaining, mcDirection);
            }
        }, mcDirection);

        return transferred[0];
    }

    /**
     * Attempt to push gas in a specific direction.
     * 
     * @param level         The world level
     * @param from          Source position
     * @param dir           Direction to push
     * @param amount        Amount to push (mb)
     * @param pressureDecay Pressure decay per jump
     * @return true if any gas was transferred
     */

    /**
     * Attempt to pull gas from a specific direction.
     * 
     * @param level         The world level
     * @param to            Target position
     * @param dir           Direction to pull from
     * @param amount        Amount to pull (mb)
     * @param pressureDecay Pressure decay per jump
     * @return true if any gas was transferred
     */

    /**
     * Balance gas between two carriers (homogenization).
     * 
     * @param level       The world level
     * @param posA        First position
     * @param posB        Second position
     * @param maxTransfer Maximum to transfer per operation
     * @param deadZone    Minimum difference to trigger transfer (prevents
     *                    micro-transfers)
     * @return true if gas was balanced
     */

    /**
     * Get GasCarrier behavior from a block.
     */
    public static Optional<GasCarrier> getCarrier(Level level, BlockPos pos) {
        if (level == null || pos == null)
            return Optional.empty();
        // An unloaded chunk has no carrier, and asking anyway is not free: Level#getBlockState on an
        // absent chunk goes through ServerChunkCache#getChunkFallback into syncLoad, which LOADS THE
        // CHUNK synchronously on the server thread. Profiling put roughly a quarter of the whole
        // server thread here — the gas graph walks neighbours outward, so every edge that left loaded
        // terrain force-loaded a chunk, every tick, for a network nobody was near.
        if (!level.hasChunkAt(pos))
            return Optional.empty();

        ImmutableBlockState state = BlockStateUtils
                .getOptionalCustomBlockState(level.getBlockState(pos))
                .orElse(null);

        if (state == null || state.isEmpty())
            return Optional.empty();

        return Optional.ofNullable(state.behavior().getFirst(GasCarrier.class));
    }

    /**
     * Offset a position in a direction.
     */
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

    /**
     * Check if a transfer would create a loop.
     * Uses transfer history to detect cycles.
     * 
     * @param history String representation of last 3 positions
     *                "x1,y1,z1;x2,y2,z2;x3,y3,z3"
     * @param newPos  Position being transferred to
     * @return true if adding newPos would create a loop
     */
    public static boolean wouldCreateLoop(String history, BlockPos newPos) {
        if (history == null || history.isEmpty())
            return false;
        String tok = newPos.getX() + "," + newPos.getY() + "," + newPos.getZ();
        // Delimiter-wrapped containment: no split() array alloc, no substring false positives.
        return (";" + history + ";").contains(";" + tok + ";");
    }

    /**
     * Update transfer history with new position.
     * Keeps only last 3 positions.
     * 
     * @param history Current history
     * @param newPos  New position to add
     * @return Updated history string
     */
    public static String updateHistory(String history, BlockPos newPos) {
        String tok = newPos.getX() + "," + newPos.getY() + "," + newPos.getZ();
        if (history == null || history.isEmpty())
            return tok;
        int semi = history.lastIndexOf(';');
        String last = semi < 0 ? history : history.substring(semi + 1);
        return last + ";" + tok;
    }

    private static Direction getDirection(BlockPos from, BlockPos to) {
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
