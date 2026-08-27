package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires whenever the damage from {@link EntityDamageWrapper} was caused by another entity (a melee
 * hit, a projectile, an explosion set off by someone, ...) — everything {@code EntityDamageEvent}
 * has plus {@link #damager()}. A script could use this to implement a PvP damage multiplier: check
 * whether {@link #damager()} resolves to a player (via {@code event.damager.type == "Player"}) and
 * scale {@link #damage()} up or down accordingly.
 *
 * <p>{@link #damager()} goes through {@link EntityType#wrap} (not {@code PlayerType.wrap}) since
 * {@code EntityType.wrap} already special-cases a {@code ServerPlayer} handle into the same
 * {@code "Player"} PolyType a script would get from {@code PlayerType.wrap} directly — reusing it
 * here means one call handles "damager is a player", "damager is a projectile", and "damager is a
 * mob" alike without three separate branches.
 */
public final class EntityDamageByEntityWrapper extends EntityDamageWrapper {
    private final EntityDamageByEntityEvent raw;

    public EntityDamageByEntityWrapper(EntityDamageByEntityEvent raw) {
        super("EntityDamageByEntityEvent", raw);
        this.raw = raw;
    }

    @Override
    public EntityDamageByEntityEvent raw() { return raw; }

    public ScriptValue damager() {
        return EntityType.wrap(((CraftEntity) raw.getDamager()).getHandle());
    }
}
