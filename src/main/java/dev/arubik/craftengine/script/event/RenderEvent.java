package dev.arubik.craftengine.script.event;

import net.minecraft.world.entity.Entity;

/**
 * {@code on_render} — fired for every rendered copy of an item packet-side (inventory slots,
 * cursor, equipment; see {@code ItemListener.ItemPacketHandler}), potentially several times a
 * tick per viewer. {@link #holder()} is whichever entity the item is being rendered for/on (a
 * player's inventory, another entity's equipment — never null in practice), {@link #slot()} is
 * the raw packet slot index, or {@code -1} when the packet has no single-slot concept (cursor,
 * equipment). Cancelling is a no-op — there is no single "the render" to veto, only the one
 * ItemStack this hook already lets a script rewrite by mutating {@code item} — but it's still
 * bound for the same consistent {@code event.type}/base shape every other hook gets.
 */
public final class RenderEvent extends ScriptEvent {
    private final Entity holder;
    private final int slot;

    public RenderEvent(Entity holder, int slot) {
        super("on_render");
        this.holder = holder;
        this.slot = slot;
    }

    public Entity holder() { return holder; }
    public int slot() { return slot; }
}
