package dev.arubik.craftengine.script.event;

import org.bukkit.event.block.BlockFormEvent;

/**
 * Fires when a block forms naturally rather than growing from a crop stage (ice/snow forming in a
 * cold biome, concrete solidifying from powder plus water, a mushroom spreading, ...) — a Java
 * subclass of {@code BlockGrowEvent}, see {@link BlockSpreadWrapper} for the further "spread from a
 * source block" specialization. A script could use {@link #newStateMaterial()} to stop ice from
 * forming over a decorative pond by checking for {@code "ice"} and cancelling.
 */
public class BlockFormWrapper extends BlockGrowWrapper {
    public BlockFormWrapper(BlockFormEvent raw) {
        this("BlockFormEvent", raw);
    }

    protected BlockFormWrapper(String type, BlockFormEvent raw) {
        super(type, raw);
    }

    @Override
    public BlockFormEvent raw() { return (BlockFormEvent) super.raw(); }
}
