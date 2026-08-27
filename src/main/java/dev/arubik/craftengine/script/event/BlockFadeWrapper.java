package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockFadeEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a block fades/melts/disappears on its own (ice melting, snow melting, a coral dying out
 * of water, fire burning out, ...). A script could use {@link #newStateMaterial()} to play a custom
 * particle effect whenever ice melts into water near a decoration.
 */
public final class BlockFadeWrapper extends ScriptEvent {
    private final BlockFadeEvent raw;

    public BlockFadeWrapper(BlockFadeEvent raw) {
        super("BlockFadeEvent");
        this.raw = raw;
    }

    public BlockFadeEvent raw() { return raw; }

    public ScriptValue block() {
        Block block = raw.getBlock();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    /** The material the block is about to fade into, lowercase (e.g. {@code "water"}). */
    public String newStateMaterial() { return raw.getNewState().getType().name().toLowerCase(Locale.ROOT); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
