package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockPistonRetractEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a piston is about to retract (and, if sticky, pull the block in front back with it).
 * A script could use this to protect a "no pistons" zone: check {@link #block()}'s position and
 * {@link ScriptEvent#setCancelled} the retraction.
 */
public final class BlockPistonRetractWrapper extends ScriptEvent {
    private final BlockPistonRetractEvent raw;

    public BlockPistonRetractWrapper(BlockPistonRetractEvent raw) {
        super("BlockPistonRetractEvent");
        this.raw = raw;
    }

    public BlockPistonRetractEvent raw() { return raw; }

    public ScriptValue block() {
        Block block = raw.getBlock();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    /** e.g. {@code "north"}, {@code "up"}. */
    public String direction() { return raw.getDirection().name().toLowerCase(Locale.ROOT); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
