package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerDropItemEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when a player presses Q (or drops a full stack) — the dropped item as a live ITEM ENTITY
 * (not just an item stack), since it's already sitting in the world by the time this fires. A
 * script could use {@link #itemDrop()} to tag the entity (e.g. via NBT/PDC on the underlying NMS
 * entity) so a later pickup hook can tell "this was a player-thrown drop" apart from a mob drop.
 */
public final class PlayerDropItemWrapper extends ScriptEvent {
    private final PlayerDropItemEvent raw;

    public PlayerDropItemWrapper(PlayerDropItemEvent raw) {
        super("PlayerDropItemEvent");
        this.raw = raw;
    }

    public PlayerDropItemEvent raw() { return raw; }

    /** The dropped item ENTITY (use {@code .item} on the wrapped Entity's ItemEntity-specific
     *  property, if exposed, to reach the stack itself) — exposed this way rather than as a bare
     *  ItemStack because the entity is what a script would actually want to tag/despawn/nudge. */
    public ScriptValue itemDrop() {
        return EntityType.wrap(((CraftEntity) raw.getItemDrop()).getHandle());
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
