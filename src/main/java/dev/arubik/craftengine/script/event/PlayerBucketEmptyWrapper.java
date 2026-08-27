package dev.arubik.craftengine.script.event;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a player empties a bucket onto a block in the world. A script could use
 * {@link #blockClicked} to prevent placing lava/water inside a protected region, or
 * {@link #itemStack} (the resulting empty/leftover item) to detect a custom bucket variant being
 * used.
 */
public final class PlayerBucketEmptyWrapper extends ScriptEvent {
    private final PlayerBucketEmptyEvent raw;

    public PlayerBucketEmptyWrapper(PlayerBucketEmptyEvent raw) {
        super("PlayerBucketEmptyEvent");
        this.raw = raw;
    }

    public PlayerBucketEmptyEvent raw() { return raw; }

    public ScriptValue blockClicked() {
        Block block = raw.getBlockClicked();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    public ScriptValue itemStack() { return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getItemStack())); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
