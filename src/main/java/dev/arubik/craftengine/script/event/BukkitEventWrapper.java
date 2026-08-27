package dev.arubik.craftengine.script.event;

/**
 * {@link ScriptEvent} wrapper around an arbitrary Bukkit/Paper {@link org.bukkit.event.Event} —
 * the object bound as {@code event} for the generic {@code /events} JSON-configured bridge (see
 * {@code dev.arubik.craftengine.events.GenericEventBridge}), as opposed to the purpose-built
 * {@code ScriptEvent} subclasses (BreakEvent, InteractEvent, ...) that this plugin's OWN machine/item
 * hooks fire with already-typed state.
 *
 * <p>Cancellation can't just track a local flag the way the base class does for those synthetic
 * events: a synthetic event has no "real" underlying thing to cancel, so the flag alone is the
 * whole story, and whatever fired it reads {@code isCancelled()} back off this same object. A
 * wrapped Bukkit event, on the other hand, is the live object the server's own event pipeline will
 * keep acting on after this listener returns — cancelling only the wrapper and leaving the real
 * event's own cancelled state untouched would mean a script's {@code event.cancel()} silently does
 * nothing. So when the raw event is {@link org.bukkit.event.Cancellable}, both directions proxy
 * straight through to it instead of shadowing it with a separate flag.
 */
public final class BukkitEventWrapper extends ScriptEvent {
    private final org.bukkit.event.Event raw;

    public BukkitEventWrapper(org.bukkit.event.Event raw) {
        super("BukkitEvent");
        this.raw = raw;
    }

    public org.bukkit.event.Event raw() { return raw; }

    @Override
    public boolean isCancelled() {
        if (raw instanceof org.bukkit.event.Cancellable c) return c.isCancelled();
        return super.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        if (raw instanceof org.bukkit.event.Cancellable c) {
            c.setCancelled(cancelled);
            return;
        }
        super.setCancelled(cancelled);
    }
}
