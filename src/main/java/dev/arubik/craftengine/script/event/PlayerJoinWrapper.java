package dev.arubik.craftengine.script.event;

import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Fires once when a player finishes logging in — the join broadcast every server shows in chat.
 * A script hooked to this can, for instance, replace the default "Foo joined the game" broadcast
 * with a custom announcement (or silence it entirely by setting an empty message) for players in a
 * VIP group.
 */
public final class PlayerJoinWrapper extends ScriptEvent {
    private final PlayerJoinEvent raw;

    public PlayerJoinWrapper(PlayerJoinEvent raw) {
        super("PlayerJoinEvent");
        this.raw = raw;
    }

    public PlayerJoinEvent raw() { return raw; }

    /** The broadcast join message, or {@code null} if it's been suppressed. */
    public String joinMessage() { return ScriptEventUtil.componentToString(raw.joinMessage()); }

    /** Empty string suppresses the broadcast entirely; anything else replaces it (MiniMessage if
     *  tagged, legacy ampersand codes otherwise — see {@link ScriptEventUtil}). */
    public void setJoinMessage(String text) {
        raw.joinMessage(text == null || text.isEmpty() ? null : ScriptEventUtil.parseComponent(text));
    }

    // PlayerJoinEvent isn't Cancellable — no cancellation to proxy; base ScriptEvent's own flag
    // (never read by anything) is all there is here.
}
