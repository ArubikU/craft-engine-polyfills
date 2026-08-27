package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityPickupItemEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires when any entity picks an item stack up off the ground — this dedicated wrapper is only
 * handed out (see {@code GenericEventBridge#buildContext}) when {@code getEntity()} is actually a
 * {@link org.bukkit.entity.Player}, since that's this project's modern replacement for the
 * deprecated {@code PlayerPickupItemEvent}. A script could use {@link #item()} to implement a
 * "can't pick up more than N of this item" soft cap by checking against the player's current
 * inventory and cancelling once it's full.
 */
public final class EntityPickupItemWrapper extends ScriptEvent {
    private final EntityPickupItemEvent raw;

    public EntityPickupItemWrapper(EntityPickupItemEvent raw) {
        super("EntityPickupItemEvent");
        this.raw = raw;
    }

    public EntityPickupItemEvent raw() { return raw; }

    /** The stack being picked up, as it exists on the ground (before merging into the player's
     *  inventory). */
    public ScriptValue item() {
        Item entity = raw.getItem();
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(entity.getItemStack()));
    }

    /** How much of the ground stack is left over after this pickup (0 if the whole stack was
     *  taken). */
    public int remaining() { return raw.getRemaining(); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
