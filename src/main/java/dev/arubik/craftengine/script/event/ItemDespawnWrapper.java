package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.ItemDespawnEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires right before a dropped-item entity's lifetime expires and it vanishes from the world. A
 * script could use {@link #item()} to keep a valuable custom item from despawning by cancelling
 * this event and resetting its pickup delay/lifetime on the underlying entity.
 */
public final class ItemDespawnWrapper extends ScriptEvent {
    private final ItemDespawnEvent raw;

    public ItemDespawnWrapper(ItemDespawnEvent raw) {
        super("ItemDespawnEvent");
        this.raw = raw;
    }

    public ItemDespawnEvent raw() { return raw; }

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
