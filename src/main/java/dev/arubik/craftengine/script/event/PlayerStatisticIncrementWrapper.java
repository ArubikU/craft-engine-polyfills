package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

/**
 * Fires whenever a player's statistic (blocks mined, distance walked, mob kills, ...) is about to
 * increment. A script could use {@link #statistic} + {@link #newValue} to implement a "reach 1000
 * blocks mined" milestone reward without polling every player's statistics on a timer.
 */
public final class PlayerStatisticIncrementWrapper extends ScriptEvent {
    private final PlayerStatisticIncrementEvent raw;

    public PlayerStatisticIncrementWrapper(PlayerStatisticIncrementEvent raw) {
        super("PlayerStatisticIncrementEvent");
        this.raw = raw;
    }

    public PlayerStatisticIncrementEvent raw() { return raw; }

    /** e.g. {@code "mine_block"}, {@code "walk_one_cm"}, {@code "mob_kills"}. */
    public String statistic() { return raw.getStatistic().name().toLowerCase(Locale.ROOT); }

    public int previousValue() { return raw.getPreviousValue(); }

    public int newValue() { return raw.getNewValue(); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
