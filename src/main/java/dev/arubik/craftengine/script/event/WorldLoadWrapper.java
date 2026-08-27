package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.world.WorldLoadEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.WorldType;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires once a world has finished loading (at server startup, or after a plugin loads one on
 * demand). A script could use this to run one-time per-world setup, e.g. seeding a scoreboard or
 * pre-warming a chunk cache for that world.
 */
public final class WorldLoadWrapper extends ScriptEvent {
    private final WorldLoadEvent raw;

    public WorldLoadWrapper(WorldLoadEvent raw) {
        super("WorldLoadEvent");
        this.raw = raw;
    }

    public WorldLoadEvent raw() { return raw; }

    public ScriptValue world() {
        ServerLevel level = ((CraftWorld) raw.getWorld()).getHandle();
        return WorldType.wrap(level);
    }

    // WorldLoadEvent isn't Cancellable — the world already finished loading by the time this fires.
}
