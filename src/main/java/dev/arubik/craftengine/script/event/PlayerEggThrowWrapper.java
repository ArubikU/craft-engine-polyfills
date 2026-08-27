package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.player.PlayerEggThrowEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;

/**
 * Fires when a thrown egg lands, right before the server decides whether it hatches a chick. A
 * script could use {@link #setHatching} to guarantee (or entirely prevent) chicken spawns from
 * thrown eggs on a farm-automation-friendly server. Not {@code Cancellable} — the Bukkit API for
 * this event has no veto, only the hatch-or-not flag; the base class's own tracked flag is used for
 * {@code event.cancel()} if a script calls it, but it has no effect on the real egg.
 */
public final class PlayerEggThrowWrapper extends ScriptEvent {
    private final PlayerEggThrowEvent raw;

    public PlayerEggThrowWrapper(PlayerEggThrowEvent raw) {
        super("PlayerEggThrowEvent");
        this.raw = raw;
    }

    public PlayerEggThrowEvent raw() { return raw; }

    public ScriptValue egg() {
        return EntityType.wrap(((CraftEntity) raw.getEgg()).getHandle());
    }

    public boolean hatching() { return raw.isHatching(); }

    public void setHatching(boolean hatching) { raw.setHatching(hatching); }
}
