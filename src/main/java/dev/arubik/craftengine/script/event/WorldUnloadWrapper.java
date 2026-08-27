package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.world.WorldUnloadEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.WorldType;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires right before a world unloads. A script could use this to flush any per-world state it's
 * been tracking (a machine registry, a scoreboard, ...) before the world reference goes stale.
 */
public final class WorldUnloadWrapper extends ScriptEvent {
    private final WorldUnloadEvent raw;

    public WorldUnloadWrapper(WorldUnloadEvent raw) {
        super("WorldUnloadEvent");
        this.raw = raw;
    }

    public WorldUnloadEvent raw() { return raw; }

    public ScriptValue world() {
        ServerLevel level = ((CraftWorld) raw.getWorld()).getHandle();
        return WorldType.wrap(level);
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
