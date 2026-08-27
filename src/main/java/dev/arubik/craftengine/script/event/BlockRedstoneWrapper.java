package dev.arubik.craftengine.script.event;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.block.BlockRedstoneEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires whenever a redstone-conductive block's current changes. A script could use this to drive a
 * custom machine's power state entirely off vanilla redstone: read {@link #newCurrent()} and flip
 * the machine active/inactive without needing its own tick loop.
 *
 * <p>Not {@link org.bukkit.event.Cancellable} — Bukkit only lets a listener change what current the
 * block ends up reporting via {@link #setNewCurrent}, not veto the change outright, so this wrapper
 * relies on the base class's local (no-op, since nothing reads it back) cancelled flag like any
 * other non-cancellable event. {@link #oldCurrent()} has no setter for the same reason: the API only
 * exposes {@code setNewCurrent(int)}.
 */
public final class BlockRedstoneWrapper extends ScriptEvent {
    private final BlockRedstoneEvent raw;

    public BlockRedstoneWrapper(BlockRedstoneEvent raw) {
        super("BlockRedstoneEvent");
        this.raw = raw;
    }

    public BlockRedstoneEvent raw() { return raw; }

    public ScriptValue block() {
        Block block = raw.getBlock();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    public int oldCurrent() { return raw.getOldCurrent(); }

    public int newCurrent() { return raw.getNewCurrent(); }
    public void setNewCurrent(int current) { raw.setNewCurrent(current); }

    // Not Cancellable.
}
