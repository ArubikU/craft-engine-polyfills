package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.WorldType;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires right after a player finishes crossing into a different world (a portal, a teleport
 * command, ...). A script could use {@link #fromWorld()} to apply a per-world "sync inventory"
 * scheme — e.g. swap in a separate hotbar loadout stored for whichever world the player just left.
 */
public final class PlayerChangedWorldWrapper extends ScriptEvent {
    private final PlayerChangedWorldEvent raw;

    public PlayerChangedWorldWrapper(PlayerChangedWorldEvent raw) {
        super("PlayerChangedWorldEvent");
        this.raw = raw;
    }

    public PlayerChangedWorldEvent raw() { return raw; }

    public ScriptValue fromWorld() {
        ServerLevel level = ((CraftWorld) raw.getFrom()).getHandle();
        return WorldType.wrap(level);
    }

    // Not Cancellable — the world change already happened by the time this fires.
}
