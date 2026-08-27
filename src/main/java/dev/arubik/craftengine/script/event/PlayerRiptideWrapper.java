package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.util.Vector;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.primitive.VectorType;

/**
 * Fires when a player launches themselves with a Riptide-enchanted trident. A script could use
 * {@link #velocity} to detect a Riptide launch and apply a custom knockback/damage effect to
 * nearby entities, mimicking an "impact" enchant.
 */
public final class PlayerRiptideWrapper extends ScriptEvent {
    private final PlayerRiptideEvent raw;

    public PlayerRiptideWrapper(PlayerRiptideEvent raw) {
        super("PlayerRiptideEvent");
        this.raw = raw;
    }

    public PlayerRiptideEvent raw() { return raw; }

    public ScriptValue item() { return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getItem())); }

    public ScriptValue velocity() {
        Vector v = raw.getVelocity();
        return VectorType.wrap(v.getX(), v.getY(), v.getZ());
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
