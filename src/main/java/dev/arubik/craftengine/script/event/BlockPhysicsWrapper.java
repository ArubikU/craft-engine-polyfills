package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockPhysicsEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires whenever a block's physics are checked as a result of a neighboring block change (sand
 * about to fall, a torch about to pop off an unsupported wall, ...) — this fires VERY frequently,
 * so a script hooked to it should stay cheap. A script could use {@link #changedTypeMaterial()} to
 * stop a custom decoration from ever popping off when its support block changes, by cancelling
 * whenever the changed neighbor matches its mount point.
 */
public final class BlockPhysicsWrapper extends ScriptEvent {
    private final BlockPhysicsEvent raw;

    public BlockPhysicsWrapper(BlockPhysicsEvent raw) {
        super("BlockPhysicsEvent");
        this.raw = raw;
    }

    public BlockPhysicsEvent raw() { return raw; }

    public ScriptValue block() {
        Block block = raw.getBlock();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    /** The material that changed and triggered this physics check, lowercase. */
    public String changedTypeMaterial() {
        return raw.getChangedBlockData().getMaterial().name().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
