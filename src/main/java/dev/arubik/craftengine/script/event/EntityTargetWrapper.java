package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityTargetEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when a mob (re)picks who/what it's targeting (attacking, following, guarding, ...). A
 * script could use this to make a tamed/guardian mob ignore its owner: check {@link #target()}
 * against a stored owner UUID and {@link #setTarget} it to {@code NULL} (dropping the target
 * entirely) when it matches.
 */
public final class EntityTargetWrapper extends ScriptEvent {
    private final EntityTargetEvent raw;

    public EntityTargetWrapper(EntityTargetEvent raw) {
        super("EntityTargetEvent");
        this.raw = raw;
    }

    public EntityTargetEvent raw() { return raw; }

    /** The entity being targeted, or {@code NULL} if this target change is dropping the target
     *  entirely (e.g. the mob lost sight of it). */
    public ScriptValue target() {
        Entity target = raw.getTarget();
        return target == null ? ScriptValue.NULL : EntityType.wrap(((CraftEntity) target).getHandle());
    }

    /** Redirects/clears the target — pass {@code NULL} to make the mob give up its target. */
    public void setTarget(ScriptValue value) {
        if (value == ScriptValue.NULL || value == null) {
            raw.setTarget(null);
            return;
        }
        if (value instanceof ScriptValue.Obj o && o.instance() instanceof net.minecraft.world.entity.Entity nms) {
            org.bukkit.entity.Entity bukkit = nms.getBukkitEntity();
            if (bukkit instanceof LivingEntity living) raw.setTarget(living);
        }
    }

    /** e.g. {@code "closest_player"}, {@code "target_attacked_entity"}, {@code "forgot_target"}. */
    public String reason() { return raw.getReason().name().toLowerCase(Locale.ROOT); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
