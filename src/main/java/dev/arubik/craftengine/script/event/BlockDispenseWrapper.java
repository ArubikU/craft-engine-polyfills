package dev.arubik.craftengine.script.event;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.util.Vector;
import org.joml.Vector3d;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.primitive.VectorType;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a dispenser is about to eject an item (or a bucket, firework, egg, ...). A script
 * could use {@link #item()} to redirect a dispenser's arrows into a custom projectile by cancelling
 * the vanilla dispense and spawning its own instead.
 */
public final class BlockDispenseWrapper extends ScriptEvent {
    private final BlockDispenseEvent raw;

    public BlockDispenseWrapper(BlockDispenseEvent raw) {
        super("BlockDispenseEvent");
        this.raw = raw;
    }

    public BlockDispenseEvent raw() { return raw; }

    public ScriptValue block() {
        Block block = raw.getBlock();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    public ScriptValue item() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getItem()));
    }

    public ScriptValue velocity() {
        Vector v = raw.getVelocity();
        return VectorType.wrap(v.getX(), v.getY(), v.getZ());
    }

    /** Replaces the ejection velocity — takes a script {@code Vector} (same object
     *  {@link #velocity()} returns). */
    public void setVelocity(ScriptValue value) {
        if (value instanceof ScriptValue.Obj o && o.instance() instanceof Vector3d v) {
            raw.setVelocity(new Vector(v.x, v.y, v.z));
        }
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
