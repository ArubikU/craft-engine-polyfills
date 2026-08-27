package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockIgniteEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a block is about to be set on fire (flint and steel, lava, lightning, a fireball, ...).
 * A script could use {@link #cause()} to implement "no player-lit fires in this region": check for
 * {@code "flint_and_steel"} and {@link ScriptEvent#setCancelled} it near a protected build.
 */
public final class BlockIgniteWrapper extends ScriptEvent {
    private final BlockIgniteEvent raw;

    public BlockIgniteWrapper(BlockIgniteEvent raw) {
        super("BlockIgniteEvent");
        this.raw = raw;
    }

    public BlockIgniteEvent raw() { return raw; }

    public ScriptValue block() {
        Block block = raw.getBlock();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    /** e.g. {@code "flint_and_steel"}, {@code "lava"}, {@code "lightning"}, {@code "spread"}. */
    public String cause() { return raw.getCause().name().toLowerCase(Locale.ROOT); }

    /** The entity that caused the ignition (the player swinging flint and steel, the fireball that
     *  hit, ...), or {@code NULL} if none was involved (lava, spread from another fire, ...). */
    public ScriptValue ignitionSourceEntity() {
        Entity entity = raw.getIgnitingEntity();
        if (entity == null) return ScriptValue.NULL;
        return EntityType.wrap(((CraftEntity) entity).getHandle());
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
