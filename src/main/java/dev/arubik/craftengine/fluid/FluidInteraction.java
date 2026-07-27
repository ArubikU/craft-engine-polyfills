package dev.arubik.craftengine.fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.arubik.craftengine.data.Amount;
import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import dev.arubik.craftengine.data.VanillaData;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * One data-defined way an item and a liquid interact: emptying a bucket into a
 * tank, drawing a bottle out of one, or a held item reacting with what is
 * stored.
 *
 * <p>
 * These three behaviours used to be if-chains in {@code FluidItemConverter} and
 * {@code FluidReactions} — the latter with exactly two hardcoded recipes and a
 * comment admitting nothing else was possible. They are now entries in
 * {@link #REGISTRY}, loaded from {@code fluid_interactions/*.json} and written
 * in vanilla's own predicate vocabulary (see
 * {@link dev.arubik.craftengine.data.VanillaData}).
 *
 * <p>
 * Unlike fluids and pipes this registry <em>is</em> rebuilt on reload: an
 * interaction is a recipe, not an identity, so nothing in the world holds a
 * reference to one.
 */
public final class FluidInteraction {

    /** Every data-defined fluid interaction, in file order. Rebuilt on reload. */
    public static final Registry<FluidInteraction> REGISTRY = Registries.create("fluid_interaction");

    /** Which of the three interaction points an entry participates in. */
    public enum Kind {
        /** Held item is consumed and its liquid poured in — a full bucket emptying. */
        FILL,
        /** Held container is filled from what is stored — a bucket or bottle drawing out. */
        DRAIN,
        /** Held item reacts with the stored liquid, producing items — lava + water bucket. */
        REACTION
    }

    private final Kind kind;
    private final ItemPredicate input;
    private final FluidType fluid;
    /** How many mB this moves, rolled per use. See {@link Amount}. */
    private final Amount amount;
    private final List<ItemStack> results;
    private final Optional<LootItemCondition> condition;
    private final int priority;

    public FluidInteraction(Kind kind, ItemPredicate input, FluidType fluid, Amount amount,
            List<ItemStack> results, Optional<LootItemCondition> condition, int priority) {
        this.kind = kind;
        this.input = input;
        this.fluid = fluid;
        this.amount = amount;
        this.results = List.copyOf(results);
        this.condition = condition;
        this.priority = priority;
    }

    public Kind kind() {
        return kind;
    }

    public FluidType fluid() {
        return fluid;
    }

    /** Higher wins; ties fall back to registration order. */
    public int priority() {
        return priority;
    }

    /** Whether the held stack satisfies this entry's item predicate. */
    public boolean matchesItem(ItemStack stack) {
        return input == null || input.test(stack);
    }

    /**
     * Rolls how many mB this interaction moves.
     *
     * <p>
     * A provider rather than a plain number because several are genuinely random —
     * an experience bottle is worth {@code 3 + rand(5) + rand(5)}, which is a
     * vanilla {@code sum} of {@code constant} and two {@code uniform} rolls.
     */
    public int rollAmount(int fallback) {
        return amount == null ? Math.max(0, fallback) : amount.roll();
    }

    /** Extra gate from vanilla's condition vocabulary, if the entry declared one. */
    public boolean conditionHolds() {
        if (condition.isEmpty())
            return true;
        var context = VanillaData.lootContext(VanillaData.anyLevel());
        if (context == null)
            return true;
        try {
            return condition.get().test(context);
        } catch (Throwable ignored) {
            return true;
        }
    }

    /** The result stacks, copied fresh each time so callers may mutate them. */
    public List<ItemStack> resultStacks() {
        List<ItemStack> stacks = new ArrayList<>(results.size());
        for (ItemStack result : results)
            if (!result.isEmpty())
                stacks.add(result.copy());
        return stacks;
    }

    /** The single replacement item, or {@link ItemStack#EMPTY} if the item is consumed. */
    public ItemStack firstResultStack() {
        return results.isEmpty() ? ItemStack.EMPTY : results.get(0).copy();
    }

    /** Entries of one kind, most specific (highest priority) first. */
    public static List<FluidInteraction> of(Kind kind) {
        List<FluidInteraction> out = new ArrayList<>();
        for (FluidInteraction interaction : REGISTRY.values())
            if (interaction.kind == kind)
                out.add(interaction);
        out.sort((a, b) -> Integer.compare(b.priority, a.priority));
        return out;
    }

    @Override
    public String toString() {
        return kind + "[" + (fluid == null ? "any" : fluid.id()) + "]";
    }
}
