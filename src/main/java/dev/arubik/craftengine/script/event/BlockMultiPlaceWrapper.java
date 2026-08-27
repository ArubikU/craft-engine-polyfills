package dev.arubik.craftengine.script.event;

import org.bukkit.event.block.BlockMultiPlaceEvent;

/**
 * Fires instead of {@link BlockPlaceWrapper} when a single placement action sets more than one
 * block (a bed's second half, a door's top half, a tall flower's upper block, ...) — everything
 * {@code BlockPlaceEvent} has (via inheriting {@link BlockPlaceWrapper}, {@link #block()} is one of
 * the blocks placed) plus nothing extra a script commonly needs beyond that shared shape. A script
 * could use {@code event.type == "BlockMultiPlaceEvent"} to tell a bed placement apart from a plain
 * single-block placement without a separate hook.
 */
public final class BlockMultiPlaceWrapper extends BlockPlaceWrapper {
    public BlockMultiPlaceWrapper(BlockMultiPlaceEvent raw) {
        super("BlockMultiPlaceEvent", raw);
    }

    @Override
    public BlockMultiPlaceEvent raw() { return (BlockMultiPlaceEvent) super.raw(); }
}
