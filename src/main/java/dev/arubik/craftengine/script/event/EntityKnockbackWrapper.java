package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.event.Cancellable;
import org.bukkit.util.Vector;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.primitive.VectorType;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import org.joml.Vector3d;

/**
 * Fires whenever an entity is about to be knocked back — the {@code EntityKnockbackByEntityEvent}
 * subtype the task list called out doesn't exist on this server's Paper API version (1.21.11), and
 * this general {@code io.papermc.paper.event.entity.EntityKnockbackEvent} has no "who hit me"
 * accessor either (only {@link #cause()} and the knockback vector itself — no {@code getHitBy()}),
 * so {@link #cause()} (e.g. {@code "entity_attack"}, {@code "explosion"}, {@code "sweep_attack"})
 * is exposed instead of a hitter entity. A script could use {@link #cause()} to implement a "no
 * knockback from explosions" armor perk by zeroing {@link #setKnockback} whenever the cause is
 * {@code "explosion"}.
 */
public final class EntityKnockbackWrapper extends ScriptEvent {
    private final EntityKnockbackEvent raw;

    public EntityKnockbackWrapper(EntityKnockbackEvent raw) {
        super("EntityKnockbackEvent");
        this.raw = raw;
    }

    public EntityKnockbackEvent raw() { return raw; }

    /** e.g. {@code "entity_attack"}, {@code "explosion"}, {@code "sweep_attack"}, {@code "push"}. */
    public String cause() { return raw.getCause().name().toLowerCase(Locale.ROOT); }

    public ScriptValue knockback() {
        Vector v = raw.getKnockback();
        return VectorType.wrap(v.getX(), v.getY(), v.getZ());
    }

    /** Replaces the knockback vector — takes a script {@code Vector} (same object {@link
     *  #knockback()} returns), same idiom as {@code VectorType}'s own add/sub/scale methods. */
    public void setKnockback(ScriptValue value) {
        if (value instanceof ScriptValue.Obj o && o.instance() instanceof Vector3d v) {
            raw.setKnockback(new Vector(v.x, v.y, v.z));
        }
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
