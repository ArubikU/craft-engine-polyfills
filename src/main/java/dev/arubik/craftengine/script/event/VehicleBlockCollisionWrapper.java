package dev.arubik.craftengine.script.event;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a vehicle's bounding box collides with a block (a boat bumping a dock post, a
 * minecart nudging a rail-side block, ...). A script could use {@link #block()} to play a bump
 * sound/particle at the collision site. Note: unlike most events in this batch, the real Bukkit
 * event is NOT {@link org.bukkit.event.Cancellable} (its abstract superclass {@code
 * VehicleCollisionEvent} doesn't implement it and this subclass doesn't add it either) — {@link
 * #isCancelled()}/{@link #setCancelled(boolean)} therefore just track the base class's own local
 * flag rather than proxying to anything real, same fallback the base {@link ScriptEvent} class
 * already provides.
 */
public final class VehicleBlockCollisionWrapper extends ScriptEvent {
    private final VehicleBlockCollisionEvent raw;

    public VehicleBlockCollisionWrapper(VehicleBlockCollisionEvent raw) {
        super("VehicleBlockCollisionEvent");
        this.raw = raw;
    }

    public VehicleBlockCollisionEvent raw() { return raw; }

    public ScriptValue vehicle() {
        return EntityType.wrap(((CraftEntity) raw.getVehicle()).getHandle());
    }

    public ScriptValue block() {
        Block block = raw.getBlock();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    // VehicleBlockCollisionEvent isn't Cancellable — no proxy, uses ScriptEvent's own flag.
}
