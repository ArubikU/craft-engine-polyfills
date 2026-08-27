package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires whenever an entity heals (natural regen, eating, a potion, a totem, ...). A script could
 * use this to implement a "double healing" enchant on armor: check {@link #reason()} and
 * {@link #setAmount} to twice {@link #amount()} for the cases it should apply to.
 */
public final class EntityRegainHealthWrapper extends ScriptEvent {
    private final EntityRegainHealthEvent raw;

    public EntityRegainHealthWrapper(EntityRegainHealthEvent raw) {
        super("EntityRegainHealthEvent");
        this.raw = raw;
    }

    public EntityRegainHealthEvent raw() { return raw; }

    public double amount() { return raw.getAmount(); }
    public void setAmount(double amount) { raw.setAmount(amount); }

    /** e.g. {@code "regen"}, {@code "eating"}, {@code "satiated"}, {@code "magic"}. */
    public String reason() { return raw.getRegainReason().name().toLowerCase(Locale.ROOT); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
