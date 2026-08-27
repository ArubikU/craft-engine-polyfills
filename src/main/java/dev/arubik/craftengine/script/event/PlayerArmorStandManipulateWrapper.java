package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when a player swaps an item with an armor stand (equipping/unequipping a piece via right
 * click). A script could use {@link #armorStandItem()}/{@link #playerItem()} to protect a
 * display-only armor stand's outfit by cancelling any manipulation that would remove or replace it.
 */
public final class PlayerArmorStandManipulateWrapper extends ScriptEvent {
    private final PlayerArmorStandManipulateEvent raw;

    public PlayerArmorStandManipulateWrapper(PlayerArmorStandManipulateEvent raw) {
        super("PlayerArmorStandManipulateEvent");
        this.raw = raw;
    }

    public PlayerArmorStandManipulateEvent raw() { return raw; }

    public ScriptValue armorStand() {
        return EntityType.wrap(((CraftEntity) raw.getRightClicked()).getHandle());
    }

    /** The item the player was holding (and is trying to place on the stand). */
    public ScriptValue playerItem() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getPlayerItem()));
    }

    /** What the armor stand currently has equipped in the manipulated slot. */
    public ScriptValue armorStandItem() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getArmorStandItem()));
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
