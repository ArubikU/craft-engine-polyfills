package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockGrowEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a crop/plant grows into its next stage (see {@link BlockFormWrapper} and
 * {@link BlockSpreadWrapper} for the natural-formation and spreading specializations that are Java
 * subclasses of this same Bukkit event). A script could use {@link #newStateMaterial()} to grant XP
 * whenever a farmer's wheat reaches its fully-grown stage.
 */
public class BlockGrowWrapper extends ScriptEvent {
    private final BlockGrowEvent raw;

    public BlockGrowWrapper(BlockGrowEvent raw) {
        this("BlockGrowEvent", raw);
    }

    protected BlockGrowWrapper(String type, BlockGrowEvent raw) {
        super(type);
        this.raw = raw;
    }

    public BlockGrowEvent raw() { return raw; }

    public ScriptValue block() {
        Block block = raw.getBlock();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    /** The material this block is about to grow into, lowercase (e.g. {@code "wheat"}). */
    public String newStateMaterial() { return raw.getNewState().getType().name().toLowerCase(Locale.ROOT); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
