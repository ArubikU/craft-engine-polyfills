package dev.arubik.craftengine.script.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.server.BroadcastMessageEvent;

/**
 * Fires whenever the server broadcasts a message to every listed recipient (vanilla examples:
 * death messages, advancement announcements, {@code /say}). Despite older docs suggesting this
 * event was deprecated in favor of a pure-Adventure replacement, it's still present and used on
 * this Paper version (1.21.11) — its {@code message()}/{@code message(Component)} accessors are
 * themselves already Adventure-based, with {@code getMessage()}/{@code setMessage(String)} kept as
 * a legacy-string convenience. A script could use {@link #setMessage} to prefix every broadcast
 * with a server-wide tag.
 */
public final class BroadcastMessageEventWrapper extends ScriptEvent {
    private final BroadcastMessageEvent raw;

    public BroadcastMessageEventWrapper(BroadcastMessageEvent raw) {
        super("BroadcastMessageEvent");
        this.raw = raw;
    }

    public BroadcastMessageEvent raw() { return raw; }

    public String message() { return ScriptEventUtil.componentToString(raw.message()); }

    public void setMessage(String text) {
        raw.message(ScriptEventUtil.parseComponent(text == null ? "" : text));
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
