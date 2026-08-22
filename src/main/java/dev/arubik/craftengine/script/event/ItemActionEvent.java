package dev.arubik.craftengine.script.event;

import net.minecraft.world.entity.Entity;

/**
 * Generic event for every discrete item-behavior hook (on_right_click, on_left_click, on_use,
 * on_consume, on_break_block, on_place_block, on_equip, on_unequip, on_attack_entity,
 * on_interact_entity, on_drop, on_pickup, on_damage_taken, on_slot_change, on_equipped_tick) —
 * {@link #type()} is the hook's own name (e.g. {@code "on_drop"}) so a script can branch on it,
 * same convention as every other {@code ScriptEvent}.
 *
 * <p>Carries whatever extra data that specific hook actually has, generically extracted by
 * {@code DataItemBehavior#runHook} from the hook's raw args (see {@code ItemListener#callBehavior}):
 * {@link #otherEntity()} is the target of an attack/interact, or the item entity for a drop/pickup;
 * {@link #amount()} is the damage amount for on_damage_taken. Both are null when the firing hook
 * doesn't have one — a script should null-check before using either.
 *
 * <p>Most of these hooks previously had NO cancel mechanism at all — a script could only mutate
 * the {@code item} variable, never veto the underlying action. {@code DataItemBehavior#runHook}
 * now applies {@link #isCancelled()} back onto the real Bukkit event backing the hook (when that
 * event implements {@code Cancellable}), so e.g. {@code on_drop}'s script can genuinely stop the
 * drop, {@code on_break_block} can genuinely stop the break, etc. Hooks with no real underlying
 * Bukkit event (e.g. {@code on_equipped_tick}) still get one of these bound for a consistent
 * context, but cancelling it is a no-op there — nothing to cancel.
 */
public final class ItemActionEvent extends ScriptEvent {
    private Entity otherEntity;
    private Double amount;

    public ItemActionEvent(String hookName) {
        super(hookName);
    }

    /** The attack/interact target, or the item entity for a drop/pickup — null if this hook has
     *  no "other entity" (most of them don't). */
    public Entity otherEntity() { return otherEntity; }
    public ItemActionEvent otherEntity(Entity e) { this.otherEntity = e; return this; }

    /** The damage amount for on_damage_taken — null for every other hook. */
    public Double amount() { return amount; }
    public ItemActionEvent amount(double a) { this.amount = a; return this; }
}
