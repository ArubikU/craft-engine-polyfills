package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.world.WorldSaveEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.WorldType;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires right after a world finishes saving to disk. A script could use this to piggyback its own
 * data save (a custom machine/economy datastore keyed per-world) onto the same cadence the server
 * already uses.
 */
public final class WorldSaveWrapper extends ScriptEvent {
    private final WorldSaveEvent raw;

    public WorldSaveWrapper(WorldSaveEvent raw) {
        super("WorldSaveEvent");
        this.raw = raw;
    }

    public WorldSaveEvent raw() { return raw; }

    public ScriptValue world() {
        ServerLevel level = ((CraftWorld) raw.getWorld()).getHandle();
        return WorldType.wrap(level);
    }

    // WorldSaveEvent isn't Cancellable — the save already happened by the time this fires.
}
