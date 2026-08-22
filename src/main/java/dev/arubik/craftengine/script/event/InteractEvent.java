package dev.arubik.craftengine.script.event;

/**
 * Fired for a right/left-click interaction — a machine's {@code on_right_click}/{@code on_left_click}
 * (JSON: {@code interactScript}/{@code attackScript}) or an item's equivalent hooks. {@code cancel()}
 * means "suppress whatever the default follow-through would have been" (a machine's menu not
 * opening, an item's vanilla click behavior not happening) — for machines this is currently
 * ALWAYS implied simply by declaring the script (see {@code MachineBlockBehavior#useWithoutItem});
 * exposing it as a real, script-settable flag here is the first step toward making that an actual
 * choice instead of an unconditional side effect of declaring the hook at all.
 */
public final class InteractEvent extends ScriptEvent {
    private final String hand;

    public InteractEvent(String hookName, String hand) {
        super(hookName);
        this.hand = hand;
    }

    /** {@code "main_hand"}/{@code "off_hand"}, or {@code null} when not applicable (e.g. a machine
     *  interact where the game doesn't distinguish hands the same way). */
    public String hand() { return hand; }
}
