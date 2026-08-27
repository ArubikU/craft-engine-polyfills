package dev.arubik.craftengine.script.event;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBurnEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires just before fire consumes a block. A script could use {@link #ignitionSource()} to
 * implement "fire started by lava never spreads to wool" by checking the source block's id and
 * {@link ScriptEvent#setCancelled} the burn.
 */
public final class BlockBurnWrapper extends ScriptEvent {
    private final BlockBurnEvent raw;

    public BlockBurnWrapper(BlockBurnEvent raw) {
        super("BlockBurnEvent");
        this.raw = raw;
    }

    public BlockBurnEvent raw() { return raw; }

    public ScriptValue block() {
        Block block = raw.getBlock();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    /** The block whose fire caused this one to burn, or {@code NULL} if there wasn't one. */
    public ScriptValue ignitionSource() {
        Block block = raw.getIgnitingBlock();
        if (block == null) return ScriptValue.NULL;
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
