package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.world.ChunkUnloadEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.WorldType;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a chunk is about to unload (a player walked far enough away, or the server is
 * shutting down). A script could use {@link #chunkX()}/{@link #chunkZ()} to flush any per-chunk
 * state it's been caching (a custom machine index, a claim overlay, ...) before it's gone.
 */
public final class ChunkUnloadWrapper extends ScriptEvent {
    private final ChunkUnloadEvent raw;

    public ChunkUnloadWrapper(ChunkUnloadEvent raw) {
        super("ChunkUnloadEvent");
        this.raw = raw;
    }

    public ChunkUnloadEvent raw() { return raw; }

    public ScriptValue world() {
        ServerLevel level = ((CraftWorld) raw.getWorld()).getHandle();
        return WorldType.wrap(level);
    }

    public int chunkX() { return raw.getChunk().getX(); }
    public int chunkZ() { return raw.getChunk().getZ(); }

    // ChunkUnloadEvent isn't Cancellable in modern Paper (the old cancel-to-keep-loaded contract
    // was removed) — no proxy, uses ScriptEvent's own flag.
}
