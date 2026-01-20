package dev.arubik.craftengine.multiblock;

import net.minecraft.core.BlockPos;

/**
 * Provider interface for configuring I/O on MultiBlock parts based on their
 * position.
 * The MultiBlock core uses this to determine what types of I/O each part should
 * handle.
 */
@FunctionalInterface
public interface IOConfigurationProvider {

    /**
     * Provide IO configuration for a part at the given relative position.
     * 
     * @param relativePos Position relative to the core block (e.g., -1,0,0 for one
     *                    block west)
     * @return IO configuration for this part, or null to use default behavior (open
     *         to all)
     */
    IOConfiguration configurePartIO(BlockPos relativePos);

    /**
     * Default provider that allows all I/O on all parts
     */
    IOConfigurationProvider OPEN = (pos) -> new IOConfiguration.Open();

    /**
     * Provider that closes all I/O on parts (only core can interact)
     */
    IOConfigurationProvider CLOSED = (pos) -> new IOConfiguration.Closed();
}
