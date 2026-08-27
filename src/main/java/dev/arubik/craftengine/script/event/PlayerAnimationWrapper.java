package dev.arubik.craftengine.script.event;

import java.util.Locale;

import org.bukkit.event.player.PlayerAnimationEvent;

/**
 * Fires when a player performs an animation (currently only the arm-swing/attack animation). A
 * script could use {@link #animationType} to detect a bare-handed "punch" swing and trigger a
 * custom martial-arts ability. Not {@code Cancellable} in this Paper version — see
 * {@code PlayerAnimationEvent} javadoc; the base class's own tracked flag is used for
 * {@code event.cancel()} if a script calls it, but it has no effect on the real animation.
 */
public final class PlayerAnimationWrapper extends ScriptEvent {
    private final PlayerAnimationEvent raw;

    public PlayerAnimationWrapper(PlayerAnimationEvent raw) {
        super("PlayerAnimationEvent");
        this.raw = raw;
    }

    public PlayerAnimationEvent raw() { return raw; }

    public String animationType() { return raw.getAnimationType().name().toLowerCase(Locale.ROOT); }
}
