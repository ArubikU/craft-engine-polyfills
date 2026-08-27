package dev.arubik.craftengine.script.event;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerBucketFillEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a player fills a bucket from a world fluid source block. A script could use
 * {@link #blockClicked} to prevent draining a fluid source inside a protected region, or
 * {@link #itemStack} to see exactly which filled bucket (lava, water, or a modded fluid variant)
 * the player is about to receive.
 */
public final class PlayerBucketFillWrapper extends ScriptEvent {
    private final PlayerBucketFillEvent raw;

    public PlayerBucketFillWrapper(PlayerBucketFillEvent raw) {
        super("PlayerBucketFillEvent");
        this.raw = raw;
    }

    public PlayerBucketFillEvent raw() { return raw; }

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
