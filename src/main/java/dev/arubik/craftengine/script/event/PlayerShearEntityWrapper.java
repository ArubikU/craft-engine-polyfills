package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerShearEntityEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when a player shears an entity (sheep, mooshroom, snow golem, ...) with shears. A script
 * could use {@link #entity} to make a custom mob variant drop a rare item on shear instead of the
 * vanilla wool/mushroom.
 */
public final class PlayerShearEntityWrapper extends ScriptEvent {
    private final PlayerShearEntityEvent raw;

    public PlayerShearEntityWrapper(PlayerShearEntityEvent raw) {
        super("PlayerShearEntityEvent");
        this.raw = raw;
    }

    public PlayerShearEntityEvent raw() { return raw; }

    public ScriptValue entity() {
        return EntityType.wrap(((CraftEntity) raw.getEntity()).getHandle());
    }

    public ScriptValue item() { return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getItem())); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
