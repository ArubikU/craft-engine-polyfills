package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.ItemSpawnEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires whenever a dropped-item entity is added to the world (a block drop, a player toss, a
 * dispenser ejection, ...) — narrower than {@link EntitySpawnWrapper} (which this event's own class
 * also IS-A, but gets its own dedicated wrapper here since {@link #item()} is what a script actually
 * wants from it). A script could use this to despawn/replace item entities of a banned material the
 * instant they touch the ground, rather than polling for them later.
 */
public final class ItemSpawnWrapper extends ScriptEvent {
    private final ItemSpawnEvent raw;

    public ItemSpawnWrapper(ItemSpawnEvent raw) {
        super("ItemSpawnEvent");
        this.raw = raw;
    }

    public ItemSpawnEvent raw() { return raw; }

    public ScriptValue entity() {
        return EntityType.wrap(((CraftEntity) raw.getEntity()).getHandle());
    }

    public ScriptValue item() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getEntity().getItemStack()));
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
