package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.world.SpawnChangeEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.WorldType;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires whenever a world's spawn point moves (a player-set spawn, {@code /setworldspawn}, ...). A
 * script could use {@link #previousLocation()} to log spawn-point history or offer an "undo" back
 * to the old location.
 */
public final class SpawnChangeWrapper extends ScriptEvent {
    private final SpawnChangeEvent raw;

    public SpawnChangeWrapper(SpawnChangeEvent raw) {
        super("SpawnChangeEvent");
        this.raw = raw;
    }

    public SpawnChangeEvent raw() { return raw; }

    public ScriptValue world() {
        ServerLevel level = ((CraftWorld) raw.getWorld()).getHandle();
        return WorldType.wrap(level);
    }

    public ScriptValue previousLocation() { return ScriptEventUtil.wrapLocation(raw.getPreviousLocation()); }

    // SpawnChangeEvent isn't Cancellable — the spawn already moved by the time this fires.
}
