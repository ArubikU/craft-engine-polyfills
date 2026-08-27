package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.primitive.ItemType;

/**
 * Fires when a gliding player fires a firework to boost themselves while wearing elytra. A script
 * could use {@link #setShouldConsume} to let a player keep their firework (e.g. an "infinite boost"
 * elytra perk) while still applying the normal boost impulse. Not {@code Cancellable} — the Bukkit
 * API for this event has no veto, only the consume-or-not flag.
 */
public final class PlayerElytraBoostWrapper extends ScriptEvent {
    private final PlayerElytraBoostEvent raw;

    public PlayerElytraBoostWrapper(PlayerElytraBoostEvent raw) {
        super("PlayerElytraBoostEvent");
        this.raw = raw;
    }

    public PlayerElytraBoostEvent raw() { return raw; }

    public ScriptValue item() { return ItemType.wrap(CraftItemStack.asNMSCopy(raw.getItemStack())); }

    public boolean shouldConsume() { return raw.shouldConsume(); }

    public void setShouldConsume(boolean shouldConsume) { raw.setShouldConsume(shouldConsume); }
}
