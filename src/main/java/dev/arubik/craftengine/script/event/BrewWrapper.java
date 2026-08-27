package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.BrewEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires when a brewing stand finishes brewing its three potion slots off one ingredient. A script
 * could use {@link #ingredient()} to implement a completely custom ingredient/effect pairing the
 * vanilla brewing recipe table has no concept of, by cancelling the vanilla brew and applying the
 * effect manually.
 *
 * <p>{@code getContents()} returns a {@code BrewerInventory}, which this codebase has no dedicated
 * script wrapper type for — rather than invent one just for this event, {@link #contentsSize()}
 * exposes its slot count and {@link #ingredient()} pulls out the one slot ({@code
 * BrewerInventory#getIngredient()}) a script is actually likely to want, in place of the full
 * inventory object. {@code BrewEvent} also has no {@code getSource()} accessor (the task description
 * assumed one) — {@link #ingredient()} is the closest real equivalent.
 */
public final class BrewWrapper extends ScriptEvent {
    private final BrewEvent raw;

    public BrewWrapper(BrewEvent raw) {
        super("BrewEvent");
        this.raw = raw;
    }

    public BrewEvent raw() { return raw; }

    /** Slot count of the brewing stand's inventory (see class javadoc for why the full inventory
     *  object isn't exposed). */
    public int contentsSize() { return raw.getContents().getSize(); }

    /** The ingredient slot's item — what's being brewed INTO the three potions. */
    public ScriptValue ingredient() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getContents().getIngredient()));
    }

    /** How many of the (up to three) potion slots actually got a result. */
    public int resultsCount() { return raw.getResults().size(); }

    public int fuelLevel() { return raw.getFuelLevel(); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
