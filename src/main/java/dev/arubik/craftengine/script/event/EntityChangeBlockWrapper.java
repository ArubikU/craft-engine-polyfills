package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when an entity changes a block in the world (an enderman picking up/placing a block, a
 * sheep eating grass, a silverfish burrowing into stone, a falling anvil crushing something, ...).
 * A script could use {@link #to()} to stop endermen from griefing a protected build: check
 * {@link #block()}'s position against a claim and {@link #setCancelled} the change.
 */
public final class EntityChangeBlockWrapper extends ScriptEvent {
    private final EntityChangeBlockEvent raw;

    public EntityChangeBlockWrapper(EntityChangeBlockEvent raw) {
        super("EntityChangeBlockEvent");
        this.raw = raw;
    }

    public EntityChangeBlockEvent raw() { return raw; }

    public ScriptValue block() {
        Block block = raw.getBlock();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    /** The material the block is about to become, lowercase (e.g. {@code "air"}, {@code "dirt"}). */
    public String to() { return raw.getTo().name().toLowerCase(Locale.ROOT); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
