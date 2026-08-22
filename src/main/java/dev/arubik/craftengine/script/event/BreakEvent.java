package dev.arubik.craftengine.script.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;

/**
 * Fired for {@code on_break} (machines, and anything else that breaks/is removed). Pre-populated
 * with the DEFAULT drops (whatever the removed thing would normally spill) so a script that
 * declares {@code on_break} for an unrelated reason (a sound effect, a stat counter, ...) doesn't
 * have to also re-specify drops just to keep the default behavior — only a script that actually
 * calls {@link #setDrops} changes what ends up on the ground, and {@link ScriptEvent#setCancelled}
 * suppresses dropping anything at all.
 */
public final class BreakEvent extends ScriptEvent {
    private List<ItemStack> drops;

    public BreakEvent(List<ItemStack> defaultDrops) {
        super("break");
        this.drops = new ArrayList<>(defaultDrops);
    }

    public List<ItemStack> drops() { return drops; }
    public void setDrops(List<ItemStack> drops) { this.drops = drops; }
}
