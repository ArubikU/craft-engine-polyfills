package dev.arubik.craftengine.script.event;

/**
 * Base class for every event object bound as the {@code event} variable in an {@code on_*} script
 * context (machines, multiblocks, and items alike) — the generic "this is what triggered you, and
 * you can veto/adjust it" object every such hook gets, replacing ad-hoc per-hook conventions
 * (e.g. the old {@code Machine.set_flag("_transfer_cancel", 1)} veto) with one consistent shape:
 * {@code event.cancelled}, {@code event.cancel()}. Concrete subclasses ({@link BreakEvent}, ...)
 * add whatever extra state/actions that specific kind of event needs, and are registered as their
 * own PolyType extending {@code "Event"} (see {@code dev.arubik.craftengine.script.types.event.EventType})
 * so a script can also branch on {@code event.type} for a cheap instanceof-equivalent.
 */
public abstract class ScriptEvent {
    private boolean cancelled;
    private final String type;

    protected ScriptEvent(String type) {
        this.type = type;
    }

    /** The PolyType name this event is registered under (e.g. {@code "BreakEvent"}) — lets a
     *  script branch on {@code event.type} without a real instanceof operator. */
    public String type() { return type; }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
