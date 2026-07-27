package dev.arubik.craftengine.fluid;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.item.ItemStack;

/**
 * Converts between held items and stored liquid — emptying a bucket into a tank
 * and filling one back out of it.
 *
 * <p>
 * Both directions used to be if-chains over {@code stack.getItem() == Items.X},
 * with the water-bottle case reaching into potion components and the experience
 * bottle's {@code 3 + rand(5) + rand(5)} yield inlined. All of it now lives in
 * {@link FluidInteraction#REGISTRY}, loaded from {@code fluid_interactions/*.json}.
 */
public class FluidItemConverter {

    /**
     * Drains a held item into liquid: returns what it yields and what the item
     * becomes (an empty bucket, a glass bottle, or nothing).
     */
    public static Pair<FluidStack, ItemStack> collectFromStack(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return Pair.of(FluidStack.EMPTY, stack);

        for (FluidInteraction interaction : FluidInteraction.of(FluidInteraction.Kind.FILL)) {
            if (!interaction.matchesItem(stack) || !interaction.conditionHolds())
                continue;
            FluidType type = interaction.fluid();
            int amount = interaction.rollAmount(type.mbPerFullBlock());
            if (amount <= 0)
                continue;
            return Pair.of(new FluidStack(type, amount, 0), interaction.firstResultStack());
        }
        return Pair.of(FluidStack.EMPTY, stack);
    }

    /**
     * Fills a held container from stored liquid: returns the filled item and the
     * liquid left behind.
     *
     * <p>
     * {@code requestedAmount} is the caller's upper bound on how much it is willing
     * to give up; an entry whose roll exceeds what is stored is skipped rather than
     * partially applied, because a half-filled bucket is not a thing.
     */
    public static Pair<ItemStack, FluidStack> collectToStack(ItemStack container, FluidStack fluid,
            int requestedAmount) {
        if (container == null || container.isEmpty() || fluid == null || fluid.isEmpty() || requestedAmount <= 0)
            return Pair.of(ItemStack.EMPTY, fluid);

        FluidType type = fluid.getType();
        int available = fluid.getAmount();

        for (FluidInteraction interaction : FluidInteraction.of(FluidInteraction.Kind.DRAIN)) {
            if (interaction.fluid() != type)
                continue;
            if (!interaction.matchesItem(container) || !interaction.conditionHolds())
                continue;
            int amount = interaction.rollAmount(type.mbPerFullBlock());
            if (amount <= 0 || amount > available)
                continue;
            ItemStack filled = interaction.firstResultStack();
            if (filled.isEmpty())
                continue;
            return Pair.of(filled, new FluidStack(type, available - amount, fluid.getPressure()));
        }
        return Pair.of(ItemStack.EMPTY, fluid);
    }
}
