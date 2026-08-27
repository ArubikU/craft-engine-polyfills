package dev.arubik.craftengine.script.event;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.block.BlockSpreadEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a block spreads FROM another block of the same or a related type (fire spreading to
 * adjacent flammable blocks, grass spreading onto dirt, a mushroom spreading a new cap, ...) — a
 * Java subclass of {@code BlockFormEvent} adding {@link #source()}. A script could use
 * {@link #source()} to trace a fire spread back to the block that started it for a griefing-log
 * feature.
 */
public final class BlockSpreadWrapper extends BlockFormWrapper {
    public BlockSpreadWrapper(BlockSpreadEvent raw) {
        super("BlockSpreadEvent", raw);
    }

    @Override
    public BlockSpreadEvent raw() { return (BlockSpreadEvent) super.raw(); }

    public ScriptValue source() {
        Block block = raw().getSource();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }
}
