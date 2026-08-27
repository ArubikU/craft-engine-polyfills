package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.weather.LightningStrikeEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when a lightning bolt strikes (naturally during a storm, from a trident, or spawned by a
 * plugin/command). A script could use {@link #cause()} to only apply a custom "struck block turns
 * to charcoal" effect for {@code "weather"}-caused strikes, leaving trident-summoned bolts alone.
 */
public final class LightningStrikeWrapper extends ScriptEvent {
    private final LightningStrikeEvent raw;

    public LightningStrikeWrapper(LightningStrikeEvent raw) {
        super("LightningStrikeEvent");
        this.raw = raw;
    }

    public LightningStrikeEvent raw() { return raw; }

    public ScriptValue lightning() {
        return EntityType.wrap(((CraftEntity) raw.getLightning()).getHandle());
    }

    /** e.g. {@code "weather"}, {@code "trident"}, {@code "command"}, {@code "custom"}, {@code
     *  "spawner_egg"}, {@code "unknown"}. */
    public String cause() { return raw.getCause().name().toLowerCase(Locale.ROOT); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
