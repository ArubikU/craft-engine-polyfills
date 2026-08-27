package dev.arubik.craftengine.script.event;

import org.bukkit.event.Cancellable;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * Fires when a player sends a chat message — Paper's modern, async-safe {@code AsyncChatEvent}
 * (NOT the deprecated {@code AsyncPlayerChatEvent}; this project's Paper 1.21.11 API has the
 * modern class, so that's the one wrapped here). A script could use this to add a custom chat
 * format/prefix per permission group, or filter a banned word list by rewriting {@link #setMessage}.
 *
 * <p>Runs off the main thread like the raw event does — a script executed from here must not touch
 * non-thread-safe game state directly (schedule back onto the main thread first if it needs to).
 */
public final class AsyncChatWrapper extends ScriptEvent {
    private final AsyncChatEvent raw;

    public AsyncChatWrapper(AsyncChatEvent raw) {
        super("AsyncChatEvent");
        this.raw = raw;
    }

    public AsyncChatEvent raw() { return raw; }

    /** The chat message as plain text (Adventure components stripped of all formatting). */
    public String message() {
        return PlainTextComponentSerializer.plainText().serialize(raw.message());
    }

    /** Replaces the message with a plain-text {@link Component} built from {@code text} — no
     *  MiniMessage/legacy parsing here since a chat message is user-authored plain text, not
     *  server-authored formatted text. */
    public void setMessage(String text) {
        raw.message(Component.text(text == null ? "" : text));
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
