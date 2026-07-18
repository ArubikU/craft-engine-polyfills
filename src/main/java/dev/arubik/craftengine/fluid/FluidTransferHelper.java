package dev.arubik.craftengine.fluid;

import dev.arubik.craftengine.fluid.behavior.FluidCarrier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

import java.util.Optional;

/**
 * Utility class for common fluid transfer operations.
 * Reduces code duplication across PipeBehavior, PumpBehavior, and
 * ValveBehavior.
 */
public class FluidTransferHelper {

    private FluidTransferHelper() {
    }

    /**
     * Attempt to transfer fluid from source to target position.
     * 
     * @param level         The world level
     * @param from          Source position
     * @param to            Target position
     * @param maxAmount     Maximum amount to transfer (mb)
     * @param pressureDecay Pressure lost per transfer
     * @return Amount actually transferred (mb)
     */

    /**
     * Attempt to push fluid in a specific direction.
     * 
     * @param level         The world level
     * @param from          Source position
     * @param dir           Direction to push
     * @param amount        Amount to push (mb)
     * @param pressureDecay Pressure decay per jump
     * @return true if any fluid was transferred
     */

    /**
     * Attempt to pull fluid from a specific direction.
     * 
     * @param level         The world level
     * @param to            Target position
     * @param dir           Direction to pull from
     * @param amount        Amount to pull (mb)
     * @param pressureDecay Pressure decay per jump
     * @return true if any fluid was transferred
     */

    /**
     * Balance fluid between two carriers (homogenization).
     * 
     * @param level       The world level
     * @param posA        First position
     * @param posB        Second position
     * @param maxTransfer Maximum to transfer per operation
     * @param deadZone    Minimum difference to trigger transfer (prevents
     *                    micro-transfers)
     * @return true if fluid was balanced
     */

    /**
     * Get FluidCarrier behavior from a block.
     */
    public static Optional<FluidCarrier> getCarrier(Level level, BlockPos pos) {
        if (level == null || pos == null)
            return Optional.empty();
        // An unloaded chunk has no carrier, and asking anyway is not free: Level#getBlockState on an
        // absent chunk goes through ServerChunkCache#getChunkFallback into syncLoad, which LOADS THE
        // CHUNK synchronously on the server thread. The graph walks neighbours outward, so every edge
        // leaving loaded terrain would force-load a chunk, every tick.
        if (!level.hasChunkAt(pos))
            return Optional.empty();

        ImmutableBlockState state = BlockStateUtils
                .getOptionalCustomBlockState(level.getBlockState(pos))
                .orElse(null);

        if (state == null || state.isEmpty())
            return Optional.empty();

        return Optional.ofNullable(state.behavior().getFirst(FluidCarrier.class));
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
        // Delimiter-wrapped containment: no split() array alloc, and the wrapping ';' avoids
        // substring false positives (e.g. "1,2,3" matching inside "11,2,3").
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
        // Keep only the LAST position + the new one (same result as the old split logic), via
        // lastIndexOf — no split() array alloc.
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
