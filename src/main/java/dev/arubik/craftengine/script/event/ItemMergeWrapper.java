package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.ItemMergeEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when two nearby dropped-item entities are about to combine into a single stack —
 * {@link #entity()} is the one that will be removed, {@link #target()} the one that absorbs it. A
 * script could use this to keep two custom items with different hidden NBT data from silently
 * merging into one stack that loses that distinction, by cancelling whenever they don't actually
 * match.
 */
public final class ItemMergeWrapper extends ScriptEvent {
    private final ItemMergeEvent raw;

    public ItemMergeWrapper(ItemMergeEvent raw) {
        super("ItemMergeEvent");
        this.raw = raw;
    }

    public ItemMergeEvent raw() { return raw; }

    /** The item entity that will be removed once the merge completes. */
    public ScriptValue entity() {
        return EntityType.wrap(((CraftEntity) raw.getEntity()).getHandle());
    }

    /** The item entity that absorbs {@link #entity()}'s stack. */
    public ScriptValue target() {
        return EntityType.wrap(((CraftEntity) raw.getTarget()).getHandle());
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
