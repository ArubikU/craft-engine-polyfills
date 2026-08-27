package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.weather.ThunderChangeEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.WorldType;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires right before a world's thunder state flips (separate from plain rain — a storm can rain
 * without thundering, but thundering always rains). A script could use {@link #toThunderState()} to
 * cue a lightning ambience track just before a thunderstorm actually kicks in.
 */
public final class ThunderChangeWrapper extends ScriptEvent {
    private final ThunderChangeEvent raw;

    public ThunderChangeWrapper(ThunderChangeEvent raw) {
        super("ThunderChangeEvent");
        this.raw = raw;
    }

    public ThunderChangeEvent raw() { return raw; }

    public ScriptValue world() {
        ServerLevel level = ((CraftWorld) raw.getWorld()).getHandle();
        return WorldType.wrap(level);
    }

    /** {@code true} when the world is about to start thundering, {@code false} when it's about to
     *  stop. */
    public boolean toThunderState() { return raw.toThunderState(); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
