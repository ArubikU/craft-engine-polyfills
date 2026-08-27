package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.weather.WeatherChangeEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.WorldType;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires right before a world's rain/clear state flips. A script could use {@link #toWeatherState()}
 * to broadcast a "storm's coming" warning to everyone in that world just before it starts raining.
 */
public final class WeatherChangeWrapper extends ScriptEvent {
    private final WeatherChangeEvent raw;

    public WeatherChangeWrapper(WeatherChangeEvent raw) {
        super("WeatherChangeEvent");
        this.raw = raw;
    }

    public WeatherChangeEvent raw() { return raw; }

    public ScriptValue world() {
        ServerLevel level = ((CraftWorld) raw.getWorld()).getHandle();
        return WorldType.wrap(level);
    }

    /** {@code true} when the world is about to start raining, {@code false} when it's about to
     *  clear up. */
    public boolean toWeatherState() { return raw.toWeatherState(); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
