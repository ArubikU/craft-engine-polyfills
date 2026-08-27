package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerPortalEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires when a player steps through a nether/end portal, before the destination is actually
 * computed/entered. A script could rewrite {@link #setTo} to link a custom portal network (e.g.
 * two player-built frames teleporting to each other) instead of vanilla's automatic nether-ratio
 * linking.
 */
public final class PlayerPortalWrapper extends ScriptEvent {
    private final PlayerPortalEvent raw;

    public PlayerPortalWrapper(PlayerPortalEvent raw) {
        super("PlayerPortalEvent");
        this.raw = raw;
    }

    public PlayerPortalEvent raw() { return raw; }

    public ScriptValue from() { return ScriptEventUtil.wrapLocation(raw.getFrom()); }

    public ScriptValue to() { return ScriptEventUtil.wrapLocation(raw.getTo()); }

    public void setTo(ScriptValue location) {
        org.bukkit.Location loc = ScriptEventUtil.toBukkitLocation(location, raw.getTo());
        if (loc != null) raw.setTo(loc);
    }

    /** e.g. {@code "nether_portal"}, {@code "end_portal"}, {@code "end_gateway"}. */
    public String cause() { return raw.getCause().name().toLowerCase(Locale.ROOT); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
