package dev.arubik.craftengine.script.event;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockDamageEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.PlayerType;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires the instant a player starts hitting a block, before {@link BlockBreakWrapper} — this is
 * where creative-mode's instant break decision is made. A script could use {@link #setInstabreak}
 * to grant a temporary "insta-mine" perk: force it {@code true} while a timed buff is active,
 * regardless of the player's actual game mode or tool.
 */
public final class BlockDamageWrapper extends ScriptEvent {
    private final BlockDamageEvent raw;

    public BlockDamageWrapper(BlockDamageEvent raw) {
        super("BlockDamageEvent");
        this.raw = raw;
    }

    public BlockDamageEvent raw() { return raw; }

    public ScriptValue block() {
        Block block = raw.getBlock();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    public ScriptValue player() {
        return PlayerType.wrap(((CraftPlayer) raw.getPlayer()).getHandle());
    }

    public boolean instabreak() { return raw.getInstaBreak(); }
    public void setInstabreak(boolean instabreak) { raw.setInstaBreak(instabreak); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
