package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerTeleportEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires for any teleport a player takes (command, plugin-triggered, ender pearl, chorus fruit, a
 * nether/end portal counts as its own {@link PlayerPortalWrapper} instead — see its javadoc). A
 * script could use {@link #cause()} to charge a different cost for {@code /warp} versus a
 * plugin-issued teleport, or rewrite {@link #setTo} to snap the destination onto a safe Y level.
 */
public final class PlayerTeleportWrapper extends ScriptEvent {
    private final PlayerTeleportEvent raw;

    public PlayerTeleportWrapper(PlayerTeleportEvent raw) {
        super("PlayerTeleportEvent");
        this.raw = raw;
    }

    public PlayerTeleportEvent raw() { return raw; }

    public ScriptValue from() { return ScriptEventUtil.wrapLocation(raw.getFrom()); }

    public ScriptValue to() { return ScriptEventUtil.wrapLocation(raw.getTo()); }

    public void setTo(ScriptValue location) {
        org.bukkit.Location loc = ScriptEventUtil.toBukkitLocation(location, raw.getTo());
        if (loc != null) raw.setTo(loc);
    }

    /** e.g. {@code "command"}, {@code "plugin"}, {@code "ender_pearl"}, {@code "unknown"}. */
    public String cause() { return raw.getCause().name().toLowerCase(Locale.ROOT); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
