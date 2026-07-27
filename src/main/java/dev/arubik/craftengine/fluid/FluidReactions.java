package dev.arubik.craftengine.fluid;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.item.ItemStack;

/**
 * Reactions between stored liquid and a held item — lava plus a water bucket
 * yielding obsidian, and anything a pack cares to add.
 *
 * <p>
 * This class previously held exactly two hardcoded recipes and a comment saying
 * "no other reactions are defined". The set now comes from
 * {@link FluidInteraction#REGISTRY} ({@code fluid_interactions/*.json}, kind
 * {@code reaction}).
 */
public class FluidReactions {

    /**
     * Reacts a held item with stored liquid: consumes part of the liquid and
     * produces items. Returns the remaining liquid and everything generated.
     */
    public static Pair<FluidStack, List<ItemStack>> reaction(FluidStack fluid, ItemStack playerItem) {
        List<ItemStack> outputs = new ArrayList<>();
        if (fluid == null || fluid.isEmpty() || playerItem == null || playerItem.isEmpty())
            return Pair.of(fluid, outputs);

        for (FluidInteraction interaction : FluidInteraction.of(FluidInteraction.Kind.REACTION)) {
            if (interaction.fluid() != fluid.getType())
                continue;
            if (!interaction.matchesItem(playerItem) || !interaction.conditionHolds())
                continue;
            int amount = interaction.rollAmount(FluidType.MB_PER_BUCKET);
            // Not enough stored to react: leave everything untouched, matching the old
            // behaviour of returning the fluid unchanged rather than partially consuming it.
            if (amount <= 0 || fluid.getAmount() < amount)
                return Pair.of(fluid, outputs);
            outputs.addAll(interaction.resultStacks());
            return Pair.of(new FluidStack(fluid.getType(), fluid.getAmount() - amount, fluid.getPressure()), outputs);
        }
        return Pair.of(fluid, outputs);
    }
}
