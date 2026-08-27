package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerItemBreakEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires the moment a tool/weapon/armor piece's durability hits zero and it breaks. A script could
 * use {@link #brokenItem()} to read whatever custom data the item carried (an enchant tier, a set
 * bonus id, ...) one last time before it's gone, e.g. to refund the player a fraction of its
 * crafting cost.
 *
 * <p>{@code PlayerItemBreakEvent} does NOT implement {@code Cancellable} — the item has already
 * broken by the time this fires, there is nothing left to veto — so {@link ScriptEvent#cancel()}
 * here only flips the inert base-class flag.
 */
public final class PlayerItemBreakWrapper extends ScriptEvent {
    private final PlayerItemBreakEvent raw;

    public PlayerItemBreakWrapper(PlayerItemBreakEvent raw) {
        super("PlayerItemBreakEvent");
        this.raw = raw;
    }

    public PlayerItemBreakEvent raw() { return raw; }

    public ScriptValue brokenItem() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw.getBrokenItem()));
    }
}
