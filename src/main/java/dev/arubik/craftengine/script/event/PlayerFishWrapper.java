package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerFishEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires at every stage of a player fishing (cast, bite, catch, reel-in, fail, ...) — see
 * {@link #state} to tell which. A script could use {@link #state} + {@link #setExp} to grant bonus
 * XP the moment a fish is actually caught, or veto the whole interaction while a "no fishing" region
 * flag is active.
 */
public final class PlayerFishWrapper extends ScriptEvent {
    private final PlayerFishEvent raw;

    public PlayerFishWrapper(PlayerFishEvent raw) {
        super("PlayerFishEvent");
        this.raw = raw;
    }

    public PlayerFishEvent raw() { return raw; }

    /** e.g. {@code "fishing"}, {@code "caught_fish"}, {@code "caught_entity"}, {@code "in_ground"},
     *  {@code "failed_attempt"}, {@code "reel_in"}. */
    public String state() { return raw.getState().name().toLowerCase(Locale.ROOT); }

    /** The entity that was caught (a fish item entity, another entity hooked, ...), or {@code NULL}
     *  when nothing was caught (e.g. mid-cast or a failed attempt). */
    public ScriptValue caught() {
        var caught = raw.getCaught();
        return caught == null ? ScriptValue.NULL : EntityType.wrap(((CraftEntity) caught).getHandle());
    }

    public ScriptValue hook() {
        return EntityType.wrap(((CraftEntity) raw.getHook()).getHandle());
    }

    public int exp() { return raw.getExpToDrop(); }

    public void setExp(int exp) { raw.setExpToDrop(exp); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
