/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.minecraft.world.item.ItemStack
 */
package dev.arubik.craftengine.fluid;

import com.mojang.datafixers.util.Pair;
import dev.arubik.craftengine.fluid.FluidInteraction;
import dev.arubik.craftengine.fluid.FluidStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class FluidReactions {
    public static Pair<FluidStack, List<ItemStack>> reaction(FluidStack fluid, ItemStack playerItem) {
        ArrayList<ItemStack> outputs = new ArrayList<ItemStack>();
        if (fluid == null || fluid.isEmpty() || playerItem == null || playerItem.isEmpty()) {
            return Pair.of(fluid, outputs);
        }
        for (FluidInteraction interaction : FluidInteraction.of(FluidInteraction.Kind.REACTION)) {
            if (interaction.fluid() != fluid.getType() || !interaction.matchesItem(playerItem) || !interaction.conditionHolds()) continue;
            int amount = interaction.rollAmount(1000);
            if (amount <= 0 || fluid.getAmount() < amount) {
                return Pair.of(fluid, outputs);
            }
            outputs.addAll(interaction.resultStacks());
            return Pair.of(new FluidStack(fluid.getType(), fluid.getAmount() - amount, fluid.getPressure()), outputs);
        }
        return Pair.of(fluid, outputs);
    }
}

