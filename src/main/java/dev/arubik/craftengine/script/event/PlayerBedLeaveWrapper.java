package dev.arubik.craftengine.script.event;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a player gets out of bed (waking up naturally, or leaving early). A script could pair
 * this with {@link PlayerBedEnterWrapper} to track "time actually spent sleeping" for a
 * well-rested buff.
 */
public final class PlayerBedLeaveWrapper extends ScriptEvent {
    private final PlayerBedLeaveEvent raw;

    public PlayerBedLeaveWrapper(PlayerBedLeaveEvent raw) {
        super("PlayerBedLeaveEvent");
        this.raw = raw;
    }

    public PlayerBedLeaveEvent raw() { return raw; }

    public ScriptValue bed() {
        Block block = raw.getBed();
        if (block == null) return ScriptValue.NULL;
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    // Not Cancellable.
}
