package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockPistonExtendEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a piston is about to extend and push whatever blocks are in front of it. A script
 * could use {@link #length()} to stop overly long contraptions: cancel whenever more blocks than a
 * configured limit would be pushed at once.
 *
 * <p>The API has no {@code getLength()} accessor — {@link #length()} is derived from
 * {@code getBlocks().size()}, the number of blocks this extension is actually about to move.
 */
public final class BlockPistonExtendWrapper extends ScriptEvent {
    private final BlockPistonExtendEvent raw;

    public BlockPistonExtendWrapper(BlockPistonExtendEvent raw) {
        super("BlockPistonExtendEvent");
        this.raw = raw;
    }

    public BlockPistonExtendEvent raw() { return raw; }

    public ScriptValue block() {
        Block block = raw.getBlock();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    /** e.g. {@code "north"}, {@code "up"}. */
    public String direction() { return raw.getDirection().name().toLowerCase(Locale.ROOT); }

    /** Number of blocks this extension is about to push — see the class javadoc. */
    public int length() { return raw.getBlocks().size(); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
