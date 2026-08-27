package dev.arubik.craftengine.script.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockExplodeEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a block itself explodes (TNT, a bed/respawn anchor exploding in the wrong dimension,
 * ...) — the block-sourced counterpart to {@link EntityExplodeWrapper}. A script could protect a
 * claimed region the same way: read {@link #blockList()} to detect the blast entering a protected
 * area and {@link ScriptEvent#setCancelled} the whole explosion.
 */
public final class BlockExplodeWrapper extends ScriptEvent {
    private final BlockExplodeEvent raw;

    public BlockExplodeWrapper(BlockExplodeEvent raw) {
        super("BlockExplodeEvent");
        this.raw = raw;
    }

    public BlockExplodeEvent raw() { return raw; }

    public ScriptValue block() {
        Block block = raw.getBlock();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    public List<ScriptValue> blockList() {
        List<ScriptValue> out = new ArrayList<>();
        for (Block block : raw.blockList()) {
            ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
            out.add(BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ())));
        }
        return out;
    }

    public float yield() { return raw.getYield(); }
    public void setYield(float yield) { raw.setYield(yield); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
