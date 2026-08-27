package dev.arubik.craftengine.script.event;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockPlaceEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.PlayerType;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a player places a block (see {@link BlockMultiPlaceWrapper} for the multi-block
 * specialization — a bed or a door placing two blocks at once). A script could use
 * {@link #blockPlacedAgainst()} to implement "you can only place torches against stone" by checking
 * that block's id and {@link ScriptEvent#setCancelled} otherwise.
 */
public class BlockPlaceWrapper extends ScriptEvent {
    private final BlockPlaceEvent raw;

    public BlockPlaceWrapper(BlockPlaceEvent raw) {
        this("BlockPlaceEvent", raw);
    }

    protected BlockPlaceWrapper(String type, BlockPlaceEvent raw) {
        super(type);
        this.raw = raw;
    }

    public BlockPlaceEvent raw() { return raw; }

    public ScriptValue block() {
        Block block = raw.getBlock();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    public ScriptValue player() {
        return PlayerType.wrap(((CraftPlayer) raw.getPlayer()).getHandle());
    }

    public ScriptValue blockPlacedAgainst() {
        Block block = raw.getBlockAgainst();
        if (block == null) return ScriptValue.NULL;
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    public boolean canBuild() { return raw.canBuild(); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
