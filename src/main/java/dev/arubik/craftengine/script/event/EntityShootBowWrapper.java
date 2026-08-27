package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityShootBowEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when a living entity (player or skeleton alike) looses an arrow from a bow. A script could
 * use {@link #force()} to implement a "min draw strength" restriction on a custom bow item, or
 * {@link #setConsumeItem} to make a special arrow type never get consumed from the shooter's
 * inventory.
 */
public final class EntityShootBowWrapper extends ScriptEvent {
    private final EntityShootBowEvent raw;

    public EntityShootBowWrapper(EntityShootBowEvent raw) {
        super("EntityShootBowEvent");
        this.raw = raw;
    }

    public EntityShootBowEvent raw() { return raw; }

    /** The arrow (or other projectile) entity being loosed, or {@code NULL} if the API reports
     *  none. */
    public ScriptValue projectile() {
        org.bukkit.entity.Entity projectile = raw.getProjectile();
        return projectile == null ? ScriptValue.NULL : EntityType.wrap(((CraftEntity) projectile).getHandle());
    }

    /** How far the bow was drawn back, 0.0-1.0. */
    public float force() { return raw.getForce(); }

    public boolean consumeItem() { return raw.shouldConsumeItem(); }
    public void setConsumeItem(boolean consume) { raw.setConsumeItem(consume); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
