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
import dev.arubik.craftengine.fluid.FluidType;
import net.minecraft.world.item.ItemStack;

public class FluidItemConverter {
    public static Pair<FluidStack, ItemStack> collectFromStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return Pair.of(FluidStack.EMPTY, stack);
        }
        for (FluidInteraction interaction : FluidInteraction.of(FluidInteraction.Kind.FILL)) {
            FluidType type;
            int amount;
            if (!interaction.matchesItem(stack) || !interaction.conditionHolds() || (amount = interaction.rollAmount((type = interaction.fluid()).mbPerFullBlock())) <= 0) continue;
            return Pair.of(new FluidStack(type, amount, 0), interaction.firstResultStack());
        }
        return Pair.of(FluidStack.EMPTY, stack);
    }

    public static Pair<ItemStack, FluidStack> collectToStack(ItemStack container, FluidStack fluid, int requestedAmount) {
        if (container == null || container.isEmpty() || fluid == null || fluid.isEmpty() || requestedAmount <= 0) {
            return Pair.of(ItemStack.EMPTY, fluid);
        }
        FluidType type = fluid.getType();
        int available = fluid.getAmount();
        for (FluidInteraction interaction : FluidInteraction.of(FluidInteraction.Kind.DRAIN)) {
            ItemStack filled;
            int amount;
            if (interaction.fluid() != type || !interaction.matchesItem(container) || !interaction.conditionHolds() || (amount = interaction.rollAmount(type.mbPerFullBlock())) <= 0 || amount > available || (filled = interaction.firstResultStack()).isEmpty()) continue;
            return Pair.of(filled, new FluidStack(type, available - amount, fluid.getPressure()));
        }
        return Pair.of(ItemStack.EMPTY, fluid);
    }
}

