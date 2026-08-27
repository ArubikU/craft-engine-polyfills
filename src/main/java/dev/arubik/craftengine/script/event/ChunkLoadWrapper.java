package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.world.ChunkLoadEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.WorldType;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires whenever a chunk is loaded (freshly generated for the first time, or read back off disk).
 * A script could use {@link #isNew()} to run one-time setup only for brand-new chunks (e.g.
 * scattering a custom structure) while leaving previously-generated chunks untouched.
 */
public final class ChunkLoadWrapper extends ScriptEvent {
    private final ChunkLoadEvent raw;

    public ChunkLoadWrapper(ChunkLoadEvent raw) {
        super("ChunkLoadEvent");
        this.raw = raw;
    }

    public ChunkLoadEvent raw() { return raw; }

    public ScriptValue world() {
        ServerLevel level = ((CraftWorld) raw.getWorld()).getHandle();
        return WorldType.wrap(level);
    }

    public int chunkX() { return raw.getChunk().getX(); }
    public int chunkZ() { return raw.getChunk().getZ(); }

    /** {@code true} if this chunk was just generated for the first time, {@code false} if it was
     *  loaded back from existing world data. */
    public boolean isNew() { return raw.isNewChunk(); }

    // ChunkLoadEvent isn't Cancellable — the chunk already finished loading by the time this fires.
}
