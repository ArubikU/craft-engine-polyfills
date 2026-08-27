package dev.arubik.craftengine.script.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;
import org.joml.Vector3d;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.primitive.VectorType;

/**
 * Fires whenever the server is about to push a player's velocity to their client (knockback,
 * explosions, launch pads, ...). A script could use {@link #setVelocity} to cap a custom launch
 * pad's max speed, or amplify it for a "super jump" boots effect.
 */
public final class PlayerVelocityWrapper extends ScriptEvent {
    private final PlayerVelocityEvent raw;

    public PlayerVelocityWrapper(PlayerVelocityEvent raw) {
        super("PlayerVelocityEvent");
        this.raw = raw;
    }

    public PlayerVelocityEvent raw() { return raw; }

    public ScriptValue velocity() {
        Vector v = raw.getVelocity();
        return VectorType.wrap(v.getX(), v.getY(), v.getZ());
    }

    /** Replaces the velocity — takes a script {@code Vector} (same object {@link #velocity()}
     *  returns). */
    public void setVelocity(ScriptValue value) {
        if (value instanceof ScriptValue.Obj o && o.instance() instanceof Vector3d v) {
            raw.setVelocity(new Vector(v.x, v.y, v.z));
        }
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
