package dev.arubik.craftengine.script.event;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.PlayerType;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a player breaks a block in survival (creative-mode instant breaks don't fire this). A
 * script could use this to implement a "no drops in this arena" rule: check {@link #block()}'s
 * position against an arena region and {@link #setDropItems} to {@code false} without stopping the
 * break itself.
 */
public final class BlockBreakWrapper extends ScriptEvent {
    private final BlockBreakEvent raw;

    public BlockBreakWrapper(BlockBreakEvent raw) {
        super("BlockBreakEvent");
        this.raw = raw;
    }

    public BlockBreakEvent raw() { return raw; }

    public ScriptValue block() {
        Block block = raw.getBlock();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    public ScriptValue player() {
        return PlayerType.wrap(((CraftPlayer) raw.getPlayer()).getHandle());
    }

    public boolean dropItems() { return raw.isDropItems(); }
    public void setDropItems(boolean dropItems) { raw.setDropItems(dropItems); }

    public int expToDrop() { return raw.getExpToDrop(); }
    public void setExpToDrop(int exp) { raw.setExpToDrop(exp); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
