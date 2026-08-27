package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.FurnaceBurnEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires when a furnace (or smoker/blast furnace) consumes a fuel item to start/continue burning. A
 * script could use {@link #setBurnTime} to give a custom fuel item a longer burn time than its
 * vanilla material would normally provide.
 */
public final class FurnaceBurnWrapper extends ScriptEvent {
    private final FurnaceBurnEvent raw;

    public FurnaceBurnWrapper(FurnaceBurnEvent raw) {
        super("FurnaceBurnEvent");
        this.raw = raw;
    }

    public FurnaceBurnEvent raw() { return raw; }

    public ScriptValue fuel() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getFuel()));
    }

    public int burnTime() { return raw.getBurnTime(); }

    public void setBurnTime(int ticks) { raw.setBurnTime(ticks); }

    /** Whether the furnace is currently lit/burning (as opposed to this fuel item merely being
     *  queued up to burn later). */
    public boolean burning() { return raw.isBurning(); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
