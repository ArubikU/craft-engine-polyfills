package dev.arubik.craftengine.fluid;

import dev.arubik.craftengine.fluid.behavior.FluidCarrier;
import dev.arubik.craftengine.util.TransferAccessMode;
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
    public static int transfer(Level level, BlockPos from, BlockPos to, int maxAmount, int pressureDecay) {
        if (level == null || from == null || to == null || maxAmount <= 0)
            return 0;

        Optional<FluidCarrier> sourceCarrier = getCarrier(level, from);
        Optional<FluidCarrier> targetCarrier = getCarrier(level, to);

        if (!sourceCarrier.isPresent() || !targetCarrier.isPresent())
            return 0;

        final int[] transferred = { 0 };

        Direction direction = getDirection(from, to);
        net.minecraft.core.Direction mcDirection = net.minecraft.core.Direction.valueOf(direction.name());

        sourceCarrier.get().extractFluid(level, from, maxAmount, extracted -> {
            if (extracted.isEmpty())
                return;

            // Apply pressure decay
            FluidStack decayed = new FluidStack(
                    extracted.getType(),
                    extracted.getAmount(),
                    Math.max(0, extracted.getPressure() - pressureDecay));

            int inserted = targetCarrier.get().insertFluid(level, to, decayed, mcDirection.getOpposite());
            transferred[0] = inserted;

            // Return unused fluid to source
            int unused = extracted.getAmount() - inserted;
            if (unused > 0) {
                FluidStack remaining = new FluidStack(
                        extracted.getType(),
                        unused,
                        extracted.getPressure());
                sourceCarrier.get().insertFluid(level, from, remaining, mcDirection); // Re-insert unused to the same
                                                                                      // side
                                                                                      // we extracted from? No, usually
                                                                                      // "internal".
                // But insertFluid needs a side. If we extracted from 'direction', we put back
                // to 'direction'?
                // actually re-insertion usually happens "internally".
                // But the interface demands a direction.
                // If we extracted from North face, and couldn't send it all, we put it back
                // "into" the North face?
                // Or we pretend it never left? Use null? Or use direction.
                // Using 'direction' implies we are pushing it BACK into the block from the
                // outside face?
                // No, we want to return it to storage.
                // FluidCarrierImpl.insertFluid(..., direction) -> adds to tanks accessible from
                // direction.
                // If we extracted from direction, then tanks were accessible from direction.
                // So re-inserting to direction is safe.
            }
        }, mcDirection);

        return transferred[0];
    }

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
    public static boolean push(Level level, BlockPos from, Direction dir, int amount, int pressureDecay) {
        BlockPos target = offset(from, dir);
        int transferred = transfer(level, from, target, amount, pressureDecay);
        return transferred > 0;
    }

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
    public static boolean pull(Level level, BlockPos to, Direction dir, int amount, int pressureDecay) {
        BlockPos source = offset(to, dir);
        int transferred = transfer(level, source, to, amount, pressureDecay);
        return transferred > 0;
    }

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
    public static boolean balance(Level level, BlockPos posA, BlockPos posB, int maxTransfer, int deadZone) {
        Optional<FluidCarrier> carrierA = getCarrier(level, posA);
        Optional<FluidCarrier> carrierB = getCarrier(level, posB);

        if (!carrierA.isPresent() || !carrierB.isPresent())
            return false;

        FluidStack a = carrierA.get().getStored(level, posA);
        FluidStack b = carrierB.get().getStored(level, posB);

        // Type compatibility check
        if (!a.isEmpty() && !b.isEmpty() && a.getType() != b.getType())
            return false;

        if (a.isEmpty() && b.isEmpty())
            return false;

        int amountA = a.isEmpty() ? 0 : a.getAmount();
        int amountB = b.isEmpty() ? 0 : b.getAmount();
        int diff = amountA - amountB;

        // Dead zone check
        if (Math.abs(diff) < deadZone)
            return false;

        if (diff == 0)
            return false;

        // Calculate transfer amount (tend toward equilibrium)
        int move = Math.min(maxTransfer, Math.abs(diff) / 2 + (Math.abs(diff) % 2));

        if (diff > 0) {
            // A → B
            return transfer(level, posA, posB, move, 0) > 0;
        } else {
            // B → A
            return transfer(level, posB, posA, move, 0) > 0;
        }
    }

    /**
     * Get FluidCarrier behavior from a block.
     */
    public static Optional<FluidCarrier> getCarrier(Level level, BlockPos pos) {
        if (level == null || pos == null)
            return Optional.empty();

        ImmutableBlockState state = BlockStateUtils
                .getOptionalCustomBlockState(level.getBlockState(pos))
                .orElse(null);

        if (state == null || state.isEmpty())
            return Optional.empty();

        return state.behavior().getAs(FluidCarrier.class);
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

        String[] positions = history.split(";");
        String newPosStr = newPos.getX() + "," + newPos.getY() + "," + newPos.getZ();

        // Check if newPos already appears in history
        for (String pos : positions) {
            if (pos.equals(newPosStr))
                return true; // Loop detected!
        }

        return false;
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
        String newPosStr = newPos.getX() + "," + newPos.getY() + "," + newPos.getZ();

        if (history == null || history.isEmpty())
            return newPosStr;

        String[] positions = history.split(";");

        // Keep last 2 positions + new one = 3 total
        if (positions.length >= 2) {
            return positions[positions.length - 1] + ";" + newPosStr;
        } else {
            return history + ";" + newPosStr;
        }
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
