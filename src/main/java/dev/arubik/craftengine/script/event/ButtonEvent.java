package dev.arubik.craftengine.script.event;

/**
 * Fired for a machine or item menu button's {@code "file.pf:function"} script action. {@code
 * cancel()} is currently a no-op (a button click has no "default follow-through" to suppress
 * beyond running the script itself) but is still exposed for a consistent {@code event} shape
 * across every menu interaction — see {@link GhostSlotEvent}, which DOES use it meaningfully.
 */
public final class ButtonEvent extends ScriptEvent {
    private final int slot;
    private final String clickType;

    public ButtonEvent(int slot, String clickType) {
        super("button");
        this.slot = slot;
        this.clickType = clickType;
    }

    public int slot() { return slot; }
    public String clickType() { return clickType; }
}
