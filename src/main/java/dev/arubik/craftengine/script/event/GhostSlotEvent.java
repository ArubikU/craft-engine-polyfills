package dev.arubik.craftengine.script.event;

/**
 * Fired for a {@link dev.arubik.craftengine.machine.menu.layout.MenuSlotType#GHOST} slot's
 * {@code set} script (a machine or item menu's script-backed identity-marker slot — filters and
 * the like). {@code cancel()} means "reject this click" — the slot's own get-script simply won't
 * see whatever the set-script would otherwise have stored, since the slot never holds a real item
 * to begin with (see {@code GhostSlotSpec}'s javadoc for why that makes this safe to reject with
 * no cleanup).
 */
public final class GhostSlotEvent extends ScriptEvent {
    private final int slot;
    private final String clickedId;
    private final String clickType;

    public GhostSlotEvent(int slot, String clickedId, String clickType) {
        super("ghost_slot");
        this.slot = slot;
        this.clickedId = clickedId;
        this.clickType = clickType;
    }

    public int slot() { return slot; }
    public String clickedId() { return clickedId; }
    public String clickType() { return clickType; }
}
