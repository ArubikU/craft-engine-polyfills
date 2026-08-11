package dev.arubik.craftengine.contraption.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Set;

/**
 * A block that is part of a multiblock structure.
 *
 * <p>
 * Fluid block tanks, machine cores, refineries: any block that spans multiple positions
 * as a single logical unit. The contraption system needs to know which blocks are
 * multiblock members so it can capture the entire structure (not just the clicked block)
 * and prevent partial disassembly.
 *
 * <p>
 * <b>Implementation note</b>: {@code FluidBlockTankBehavior} and {@code DataMultiBlockBehavior}
 * implement this, so existing multiblocks work without changes.
 */
public interface MultiblockMember {

    /**
     * All positions this multiblock structure occupies.
     *
     * <p>
     * For a 3x3x3 tank, this returns 27 positions. For a 1x1x1 machine core, this returns
     * 1 position (itself). The contraption assembler flood-fills these positions as a unit,
     * so breaking one part disassembles the whole structure.
     *
     * @param level the level this multiblock is in
     * @param pos   any position in the structure (usually the controller/anchor)
     * @return all block positions this structure occupies, never null or empty
     */
    Set<BlockPos> getStructurePositions(Level level, BlockPos pos);

    /**
     * The controller/anchor position of this multiblock.
     *
     * <p>
     * For a tank this is the minimum corner (bottom-northwest). For a machine core this
     * might be the center. The contraption system uses this to deduplicate members (so a
     * 3x3x3 tank registers once, not 27 times).
     *
     * @param level the level this multiblock is in
     * @param pos   any position in the structure
     * @return the canonical controller position
     */
    BlockPos getControllerPosition(Level level, BlockPos pos);

    /**
     * Whether this multiblock structure is currently fully formed.
     *
     * <p>
     * A tank with a missing block is incomplete. A machine core with no casing is incomplete.
     * The contraption assembler skips incomplete structures (they wouldn't work anyway).
     *
     * @param level the level this multiblock is in
     * @param pos   any position in the structure
     * @return true if the structure is valid and complete
     */
    default boolean isStructureComplete(Level level, BlockPos pos) {
        return true; // assume valid unless overridden
    }
}
